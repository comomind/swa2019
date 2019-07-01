package com.lg.sixsenses.willi.logic.callmanager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Process;
import android.util.Log;

import com.lg.sixsenses.willi.codec.audio.AudioCodec;
import com.lg.sixsenses.willi.codec.audio.AudioCodecConst;
import com.lg.sixsenses.willi.net.AudioClock;
import com.lg.sixsenses.willi.net.JitterBuffer;
import com.lg.sixsenses.willi.net.RtpConst;
import com.lg.sixsenses.willi.net.RtpPacket;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AudioIo {
  private static final String TAG = AudioIo.class.getSimpleName();

  private Context context;

  private Thread sendThread = null;
  private Thread receiveThread = null;

  private DatagramSocket receiveSocket;
  private InetAddress remoteIp;
  private int remotePort = 0;

  public int getMyPort() {
    return myPort;
  }

  public void setMyPort(int myPort) {
    this.myPort = myPort;
  }

  private int myPort = 0;

  private boolean isBoostAudio = false;

  private boolean isStartReceive = false;
  private boolean isStartSend = false;

  private boolean isReceiveThreadRun = false;
  private boolean isSendThreadRun = false;

  private AudioCodec audioCodec;
  private JitterBuffer jitterBuffer = null;
  private ConcurrentLinkedQueue<byte[]> recorderQueue = null;

//  private int rtpSequenceNumber = 0;
//  private int timestampOffset = 0;

  public AudioIo(Context context) {
    this.context = context;
  }

  public AudioIo(Context context, AudioCodec audioCodec, JitterBuffer jitterBuffer, ConcurrentLinkedQueue<byte[]> recorderQueue) {
    this.context = context;
    this.audioCodec = audioCodec;
    this.jitterBuffer = jitterBuffer;
    this.recorderQueue = recorderQueue;
  }

  public boolean getIsBoostAudio() {
    return isBoostAudio;
  }

  public void setIsBoostAudio(boolean boostAudio) {
    isBoostAudio = boostAudio;
  }

  public void setAudioCodec(AudioCodec audioCodec) {
    this.audioCodec = audioCodec;
  }

  public synchronized boolean startReceive(int port) {
    if (isStartReceive) {
      return true;
    }

    if (audioCodec.open() == 0) {
      Log.i(TAG, "codec open success");
    }

    receiveSocket = bindSocket(port);

    // start receive thread
    startReceiveThread();
    isStartReceive = true;
    return false;
  }

  public synchronized boolean startSend(InetAddress remoteIp, int remotePort) {
    if (isStartSend) {
      return true;
    }

    this.remoteIp = remoteIp;
    this.remotePort = remotePort;

    startSendThread();
    isStartSend = true;
    return false;
  }


  public synchronized boolean stopAll() {
    if (!isStartReceive && !isStartSend) {
      Log.d(TAG, "AudioIo already stopped");
      return true;
    }

    // terminate receive thread
    stopReceiveThread();
    isStartReceive = false;

    // terminate send thread
    stopSendThread();
    isStartSend = false;

    audioCodec.close();

    return false;
  }

  public void stopReceiveThread() {
    if (receiveThread != null && receiveThread.isAlive()) {
      isReceiveThreadRun = false;
      receiveSocket.close();
      Log.d(TAG, "receiveThread join started");
      isReceiveThreadRun = false;
      try {
        receiveThread.join();
      } catch(InterruptedException e) {
        e.printStackTrace();
        Log.d(TAG, "receiveThread join interrupted");
      }
      Log.d(TAG, "receiveThread join success");
    }

    receiveThread = null;
    receiveSocket = null;
  }

  public void stopSendThread() {
    if (sendThread != null && sendThread.isAlive()) {
      Log.d(TAG, "sendThread join started");
      isSendThreadRun = false;
      try {
        sendThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
        Log.d(TAG, "sendThread join interrupted");
      }
      Log.d(TAG, "sendThread join success");
    }

    sendThread = null;
  }

  private void startReceiveThread() {
    isReceiveThreadRun = true;
    receiveThread = new Thread(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "receive thread started, tid: " + Thread.currentThread().getId());

        try {
          while (isReceiveThreadRun) {
            byte[] rtpBuffer = new byte[RtpPacket.HEADER_SIZE + audioCodec.getEncodedBufferSize()];
            DatagramPacket packet = new DatagramPacket(rtpBuffer, RtpPacket.HEADER_SIZE + audioCodec.getEncodedBufferSize());
            receiveSocket.receive(packet);

            RtpPacket rtpPacket = new RtpPacket(packet.getData(), packet.getLength());
            jitterBuffer.write(rtpPacket);
          }

          receiveSocket.disconnect();
          receiveSocket.close();
        } catch (SocketException e) {
          isReceiveThreadRun = false;
          Log.e(TAG, "SocketException: " + e.toString());
        } catch (IOException e) {
          isReceiveThreadRun = false;
          Log.e(TAG, "IOException: " + e.toString());
        }
      }
    });
    receiveThread.start();
  }

  private void startSendThread() {
    isSendThreadRun = true;
    sendThread = new Thread(new Runnable() {
      @Override
      public void run() {
        // create instance of AudioRecord
        Log.d(TAG, "send thread started, tid: " + Thread.currentThread().getId());

        int bytesRead = 0;
        byte[] rawBuffer = new byte[audioCodec.getRawBufferSize()];
        byte[] tempBuffer = new byte[audioCodec.getEncodedBufferSize()];

        try {
          Log.i(TAG, "Packet destination: " + remoteIp.toString() + " " + remotePort);

          DatagramSocket socket = new DatagramSocket();

          int rtpSeqNum = 0;
          int timeStampOffset = 0;

          while (isSendThreadRun) {
            rawBuffer = recorderQueue.poll();
            if (rawBuffer != null && rawBuffer.length == audioCodec.getRawBufferSize()) {
//              Log.d(TAG, "send rendered packet");
              int len = audioCodec.encode(rawBuffer, tempBuffer);

              int timeStampIncrement = audioCodec.getSampleRate() / (AudioCodecConst.MILLISECONDS_IN_A_SECOND / audioCodec.getSampleInterval());
              RtpPacket rtpSendPacket = new RtpPacket(RtpConst.PayloadType.GSM.getValue(), rtpSeqNum, timeStampOffset + (rtpSeqNum * timeStampIncrement), tempBuffer, len);
              int packetLen = rtpSendPacket.getLength();
              byte[] packetBits = new byte[packetLen];
              rtpSendPacket.getPacket(packetBits);

              DatagramPacket packet = new DatagramPacket(packetBits, packetLen, remoteIp, remotePort);
              socket.send(packet);
              rtpSeqNum++;
            }
          }

          // stop audio thread
          socket.disconnect();
          socket.close();

        } catch (SocketException e) {
          isReceiveThreadRun = false;
          Log.e(TAG, "SocketException: " + e.toString());
        } catch (UnknownHostException e) {
          isReceiveThreadRun = false;
          Log.e(TAG, "UnknownHostException: " + e.toString());
        } catch (IOException e) {
          isReceiveThreadRun = false;
          Log.e(TAG, "IOException: " + e.toString());
        }
      }
    });
    sendThread.start();
  }

  private DatagramSocket bindSocket(int port) {
    boolean isBind = false;
    DatagramSocket socket = null;
    Log.d(TAG, "bindSocket try, port: " + port);
    while (!isBind) {
      try {
        socket = new DatagramSocket(null);
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(port));
        isBind = true;
      } catch (SocketException e) {
        e.printStackTrace();
        port++;
      }
    }
    myPort = port;
    Log.d(TAG, "bindSocket success, port: " + myPort);
    return socket;
  }

}
