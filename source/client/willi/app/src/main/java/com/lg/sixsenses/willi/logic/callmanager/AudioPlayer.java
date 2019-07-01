package com.lg.sixsenses.willi.logic.callmanager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Process;
import android.util.Log;

import com.lg.sixsenses.willi.codec.audio.AudioCodec;
import com.lg.sixsenses.willi.net.JitterBuffer;
import com.lg.sixsenses.willi.net.RtpPacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AudioPlayer {
  private static final String TAG = AudioPlayer.class.getSimpleName();

  private boolean isRunning = false;
  private boolean isPlayThreadRun = false;
  private Thread playThread = null;
  private Context context;
  private AudioCodec audioCodec = null;
  private int sessionId = 0;

  private HashMap<String, JitterBuffer> jitterBufferMap;

  public AudioPlayer(AudioCodec audioCodec, int sessionId) {
    this.audioCodec = audioCodec;
    this.sessionId = sessionId;
    this.jitterBufferMap = new HashMap<String, JitterBuffer>();
  }

  public AudioPlayer(Context context, int sessionId, AudioCodec audioCodec) {
    this.context = context;
    this.sessionId = sessionId;
    this.jitterBufferMap = new HashMap<String, JitterBuffer>();
    this.audioCodec = audioCodec;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public synchronized void addJitterBuffer(String peerEmail, JitterBuffer jitterBuffer) {
    jitterBufferMap.put(peerEmail, jitterBuffer);
    Log.d(TAG, "addJitterBuffer, size: " + jitterBufferMap.size());
  }

  public synchronized void removeJitterBuffer(String peerEmail) {
    jitterBufferMap.remove(peerEmail);
    Log.d(TAG, "removeJitterBuffer, size: " + jitterBufferMap.size());
  }

  public synchronized void clearJitterBuffer() {
    jitterBufferMap.clear();
    Log.d(TAG, "clearJitterBuffer, size: " + jitterBufferMap.size());
  }

  public synchronized boolean startPlay() {
    if (isRunning) {
      return true;
    }

    audioCodec.open();

    startPlayThread();
    isRunning = true;

    return false;
  }

  private void startPlayThread() {
    isPlayThreadRun = true;
    playThread = new Thread(new Runnable() {
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
        Log.d(TAG, "play thread started, tid: " + Thread.currentThread().getId());
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
            .setSessionId(sessionId)
                .build();

        outputTrack.play();

        byte[] rawBuffer = new byte[audioCodec.getRawBufferSize()];
        byte[] tempBuffer = new byte[audioCodec.getEncodedBufferSize()];

        short[] mixedTemp = new short[audioCodec.getRawBufferSize()/ 2];
        short[] mixedOutput = new short[audioCodec.getRawBufferSize() / 2];

        Log.d(TAG, "isPlayThreadRun = " + isPlayThreadRun);

        int count = 0;

        ArrayList<RtpPacket> packetList = new ArrayList<>();
        RtpPacket packet = null;

        while(isPlayThreadRun) {
//          Log.d(TAG, "jitterBufferMap size: " + jitterBufferMap.size());

          for (JitterBuffer jitterBuffer : jitterBufferMap.values()) {
            packet = jitterBuffer.read();
            if (packet != null) {
              packetList.add(packet);
            }
          }

          if (packetList.size() > 0) {
//            Log.d(TAG, "packetList size: " + packetList.size());
            short[][] audioShortBuffer = new short[packetList.size()][];

            for (int i=0 ; i< packetList.size() ; i++) {
              RtpPacket p = packetList.get(i);
              p.getPayload(tempBuffer);
              byte[] encodedBuffer = Arrays.copyOf(tempBuffer, p.getPayloadLength());
              audioCodec.decode(encodedBuffer, rawBuffer);

              byte[] audioOutputBuffer = rawBuffer;
              ByteBuffer.wrap(audioOutputBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(mixedTemp);

              audioShortBuffer[i] = Arrays.copyOf(mixedTemp, mixedTemp.length);
            }

            for (int i=0 ; i<audioShortBuffer[0].length ; i++) {
              float mixed = 0;
              mixed = audioShortBuffer[0][i] / 32768.0f;
              for (int j=1 ; j<packetList.size() ; j++) {
                mixed += audioShortBuffer[j][i] / 32768.0f;
              }
              // reduce the volume a bit
              mixed *= 0.8;

              // hard clipping
              if (mixed > 1.0f) {
                mixed = 1.0f;
              }
              if (mixed < -1.0f) {
                mixed = -1.0f;
              }

              short outPutSample = (short)(mixed * 32768.0f);
              mixedOutput[i] = outPutSample;
            }
            outputTrack.write(mixedOutput, 0, mixedOutput.length);
          }
          packetList.clear();
        }

        outputTrack.stop();
        outputTrack.flush();
        outputTrack.release();

      }
    });
    playThread.start();
  }

  public synchronized boolean stopPlay() {
    if (!isRunning) {
      return true;
    }

    stopPlayThread();

    isRunning = false;

    return false;
  }

  private void stopPlayThread() {
    if (playThread != null && playThread.isAlive()) {
      isPlayThreadRun = false;
      Log.i(TAG, "playThread thread join started");
      isPlayThreadRun = false;

      try {
        playThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    playThread = null;

    audioCodec.close();
  }
}
