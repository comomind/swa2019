package com.lg.sixsenses.willi.logic.callmanager;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.lg.sixsenses.willi.codec.audio.AudioCodec;
import com.lg.sixsenses.willi.codec.audio.AudioCodecConst;
import com.lg.sixsenses.willi.net.RtpConst;
import com.lg.sixsenses.willi.net.RtpPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AudioRecorder {
  private static final String TAG = AudioRecord.class.getSimpleName();


  private HashMap<String, ConcurrentLinkedQueue<byte[]>> recorderQueueMap = null;
  private AudioCodec audioCodec = null;

  private HashMap<String, AvInfo> sendList = null;

  private Thread recordThread = null;
  private boolean isRunning = false;
  private boolean isRecordThreadRun = false;

//  private AudioRecord recorder = null;

  public AudioRecorder(AudioCodec audioCodec) {
    this.audioCodec = audioCodec;
    recorderQueueMap = new HashMap<String, ConcurrentLinkedQueue<byte[]>>();
    sendList = new HashMap<>();
  }

  public synchronized boolean addSendList(String email, InetAddress remoteIp, int remotePort) {
    AvInfo info = new AvInfo(email, remoteIp, remotePort);
    sendList.put(email, info);

    return true;
  }

  public synchronized boolean removeSendList(String email) {
    sendList.remove(email);

    return true;
  }

  public synchronized boolean clearSendList() {
    sendList.clear();
    return true;
  }

  public int getAudioSessionId() {
    // TODO: fix this
//    return recorder.getAudioSessionId();
    return 0;
  }

  public boolean addRecorderQueue(String peerEmail, ConcurrentLinkedQueue<byte[]> recorderQueue) {
    recorderQueueMap.put(peerEmail, recorderQueue);
    return true;
  }

  public boolean removeRecorderQueue(String peerEmail) {
    recorderQueueMap.remove(peerEmail);
    return true;
  }

  public boolean clearRecorderQueue() {
    recorderQueueMap.clear();
    return true;
  }

  public synchronized boolean startRecord() {
    if (isRunning) {
      return true;
    }

    startRecordThread();
    isRunning = true;

    return false;
  }

  public synchronized boolean stopRecord() {
    if (!isRunning) {
      Log.d(TAG, "Record thread already stopped");
      return true;
    }

    stopRecordThread();
    isRunning = false;

    return false;
  }


  public void startRecordThread() {
    isRecordThreadRun = true;
    recordThread = new Thread(new Runnable() {
      @Override
      public void run() {
        byte[] rawBuffer = new byte[audioCodec.getRawBufferSize()];
        byte[] tempBuffer = new byte[audioCodec.getEncodedBufferSize()];

        int bytesRead = 0;

        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, audioCodec.getSampleRate(),
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
            AudioRecord.getMinBufferSize(audioCodec.getSampleRate(), AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));

        recorder.startRecording();

        int rtpSeqNum = 0;
        int timeStampOffset = 0;

        try {
          DatagramSocket socket = new DatagramSocket();

          while (isRecordThreadRun) {
            bytesRead = recorder.read(rawBuffer, 0, audioCodec.getRawBufferSize());

            if (sendList.size() == 0) {
              continue;
            }

            // send packet
            int len = audioCodec.encode(rawBuffer, tempBuffer);
            int timeStampIncrement = audioCodec.getSampleRate() / (AudioCodecConst.MILLISECONDS_IN_A_SECOND / audioCodec.getSampleInterval());
            RtpPacket rtpSendPacket = new RtpPacket(RtpConst.PayloadType.GSM.getValue(), rtpSeqNum, timeStampOffset + (rtpSeqNum * timeStampIncrement), tempBuffer, len);
            int packetLen = rtpSendPacket.getLength();
            byte[] packetBits = new byte[packetLen];

            rtpSendPacket.getPacket(packetBits);

            for (AvInfo info : sendList.values()) {
              DatagramPacket packet = new DatagramPacket(packetBits, packetLen, info.remoteIp, info.remotePort);
              socket.send(packet);
            }
            rtpSeqNum++;

//          // push audio to queue
//          for (ConcurrentLinkedQueue<byte[]> recorderQueue :recorderQueueMap.values()) {
//            byte[] temp = Arrays.copyOf(rawBuffer, rawBuffer.length);
//            recorderQueue.offer(temp);
//          }
          }

          // stop audio thread
          socket.disconnect();
          socket.close();

        } catch (SocketException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }

        recorder.stop();
        recorder.release();
      }
    });
    recordThread.start();
  }

  public void stopRecordThread() {
    if (recordThread != null & recordThread.isAlive()) {
      Log.d(TAG, "recordThread join started");
      isRecordThreadRun = false;
      try {
        recordThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      Log.d(TAG, "recordThread join success");
    }
    recordThread = null;


  }

  class AvInfo {
    String email;
    InetAddress remoteIp;
    int remotePort;

    public AvInfo(String email, InetAddress remoteIp, int remotePort) {
      this.email = email;
      this.remoteIp = remoteIp;
      this.remotePort = remotePort;
    }
  }

}
