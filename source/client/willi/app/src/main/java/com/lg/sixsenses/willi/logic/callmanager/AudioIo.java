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

public class AudioIo {
  private static final String TAG = AudioIo.class.getSimpleName();

  // TODO: some const values (should remove this after integration)
  private final int JITTER_BUFFER_JITTER = 30;
  private final int JITTER_BUFFER_PERIOD = 200;

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
  private JitterBuffer jitterBuffer;

//  private int rtpSequenceNumber = 0;
//  private int timestampOffset = 0;

  public AudioIo(Context context) {
    this.context = context;
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

    // terminate send thread
    stopSendThread();

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

    isStartReceive = false;
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
    isStartSend = false;
  }

  private void startReceiveThread() {
    isReceiveThreadRun = true;
    receiveThread = new Thread(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "receive thread started, tid: " + Thread.currentThread().getId());

        try {
          AudioClock clock = new AudioClock();

          jitterBuffer = new JitterBuffer(JITTER_BUFFER_JITTER, JITTER_BUFFER_PERIOD);
          jitterBuffer.setSampleRate(audioCodec.getSampleRate());
          jitterBuffer.setClock(clock);

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
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int previousAudioManagerMode = 0;
        if (audioManager != null) {
          previousAudioManagerMode = audioManager.getMode();
          audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION); //Enable AEC
        }

        // create instance of AudioRecord
        Log.d(TAG, "send thread started, tid: " + Thread.currentThread().getId());
        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, audioCodec.getSampleRate(),
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
            AudioRecord.getMinBufferSize(audioCodec.getSampleRate(), AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));

        AudioTrack outputTrack = new AudioTrack.Builder()
            .setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                //	.setFlags(AudioAttributes.FLAG_LOW_LATENCY) //This is Nougat+ only (API 25) comment if you have lower
                .build())
            .setAudioFormat(new AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(audioCodec.getSampleRate())
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
            .setBufferSizeInBytes(audioCodec.getRawBufferSize())
            .setTransferMode(AudioTrack.MODE_STREAM)
            //.setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY) //Not until Api 26
            .setSessionId(recorder.getAudioSessionId())
            .build();

        int bytesRead = 0;
        byte[] rawBuffer = new byte[audioCodec.getRawBufferSize()];
        byte[] tempBuffer = new byte[audioCodec.getEncodedBufferSize()];

        try {
          Log.i(TAG, "Packet destination: " + remoteIp.toString() + " " + remotePort);
          DatagramSocket socket = new DatagramSocket();
          recorder.startRecording();
          outputTrack.play();

          int rtpSeqNum = 0;
          int timeStampOffset = 0;

          while (isSendThreadRun) {
            RtpPacket rtpReceivedPacket = jitterBuffer.read();
            if (rtpReceivedPacket != null) {
              rtpReceivedPacket.getPayload(tempBuffer);
              byte[] encodedBuffer = Arrays.copyOf(tempBuffer, rtpReceivedPacket.getPayloadLength());
              audioCodec.decode(encodedBuffer, rawBuffer);

              byte[] audioOutputBuffer = rawBuffer;
              if (!isBoostAudio) {
                // normal case
                outputTrack.write(audioOutputBuffer, 0, audioCodec.getRawBufferSize());
              } else {
                // boost case
                short[] boostAudioOutputBuffer = new short[audioOutputBuffer.length / 2];
                // to turn bytes to short as either big endian or little endian
                ByteBuffer.wrap(audioOutputBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(boostAudioOutputBuffer);
                for (int i=0 ; i<boostAudioOutputBuffer.length ; i++) {  // 16 bit sample size
                  int value = boostAudioOutputBuffer[i]*10;   //increase level by gain=20dB: Math.pow(10., dB/20.);  dB to gain factor
                  if (value > 32767) {
                    value = 32767;
                  } else if (value <-32767) {
                    value = -32767;
                  }
                  boostAudioOutputBuffer[i] = (short)value;
                }
                // to turn shorts back to bytes
                outputTrack.write(boostAudioOutputBuffer, 0, boostAudioOutputBuffer.length);
              }
            }

            // capture audio from microphone and send
            bytesRead = recorder.read(rawBuffer, 0, audioCodec.getRawBufferSize());
            if (bytesRead == audioCodec.getRawBufferSize()) {
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
          recorder.stop();
          recorder.release();
          outputTrack.stop();
          outputTrack.flush();
          socket.disconnect();
          socket.close();

          if (audioManager != null) {
            audioManager.setMode(previousAudioManagerMode);
          }
          Log.i(TAG, "audio thread stopped");
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
