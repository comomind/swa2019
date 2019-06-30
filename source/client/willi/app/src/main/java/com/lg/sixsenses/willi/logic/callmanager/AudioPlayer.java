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

public class AudioPlayer {
  private static final String TAG = AudioPlayer.class.getSimpleName();

  private boolean isRunning = false;
  private boolean isPlayThreadRun = false;
  private Thread playThread = null;
  private Context context;
  private AudioCodec audioCodec = null;

  private ArrayList<JitterBuffer> jitterBufferArray;

  public AudioPlayer(Context context, AudioCodec audioCodec) {
    this.context = context;
    this.jitterBufferArray = new ArrayList<>();
    this.audioCodec = audioCodec;
  }

  public synchronized void addJitterBuffer(JitterBuffer jitterBuffer) {
    jitterBufferArray.add(jitterBuffer);
    Log.d(TAG, "addJitterBuffer, size: " + jitterBufferArray.size());
  }

  public synchronized void removeJitterBuffer(JitterBuffer jitterBuffer) {
    jitterBufferArray.remove(jitterBuffer);
    Log.d(TAG, "removeJitterBuffer, size: " + jitterBufferArray.size());
  }

  public synchronized void clearJitterBuffer() {
    jitterBufferArray.clear();
    Log.d(TAG, "clearJitterBuffer, size: " + jitterBufferArray.size());
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
//            .setSessionId(recorder.getAudioSessionId())
                .build();

        outputTrack.play();

        RtpPacket packet = null;
        byte[] rawBuffer = new byte[audioCodec.getRawBufferSize()];
        byte[] tempBuffer = new byte[audioCodec.getEncodedBufferSize()];

        short[] mixedTemp = new short[rawBuffer.length / 2];
        short[] mixedOuput = new short[rawBuffer.length / 2];

        while(isPlayThreadRun) {
          // get packet from jitter buffer array
          for (JitterBuffer buffer : jitterBufferArray) {
            packet = buffer.read();
            if (packet != null) {
              packet.getPayload(tempBuffer);
              byte[] encodedBuffer = Arrays.copyOf(tempBuffer, packet.getPayloadLength());
              audioCodec.decode(encodedBuffer, rawBuffer);

              byte[] audioOutputBuffer = rawBuffer;
              ByteBuffer.wrap(audioOutputBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(mixedTemp);

              for (int i=0 ; i<mixedTemp.length ; i++) {
                float sample1 = mixedTemp[i] / 32768.0f;
                float sample2 = mixedOuput[i] / 32768.0f;

                float mixed = sample1 + sample2;

                // reduce the volume a bit
                mixed *= 0.8;

                // hard clipping
                if (mixed > 1.0f) {
                  mixed = 1.0f;
                }
                if (mixed < -1.0f) {
                  mixed = -1.0f;
                }

              }



            }
          }

        }

        outputTrack.stop();
        outputTrack.flush();

      }
    });
    playThread.start();
  }

  public synchronized boolean stopPlay() {
    if (!isRunning) {
      return true;
    }

    audioCodec.close();

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
    jitterBufferArray.clear();
  }

}
