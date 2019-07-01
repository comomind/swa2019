package com.lg.sixsenses.willi.logic.callmanager;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.lg.sixsenses.willi.codec.audio.AudioCodec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AudioRecorder {
  private static final String TAG = AudioRecord.class.getSimpleName();


  private HashMap<String, ConcurrentLinkedQueue<byte[]>> recorderQueueMap = null;
  private AudioCodec audioCodec = null;

  private Thread recordThread = null;
  private boolean isRunning = false;
  private boolean isRecordThreadRun = false;

//  private AudioRecord recorder = null;

  public AudioRecorder(AudioCodec audioCodec) {
    this.audioCodec = audioCodec;
    recorderQueueMap = new HashMap<String, ConcurrentLinkedQueue<byte[]>>();
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
        int bytesRead = 0;

        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, audioCodec.getSampleRate(),
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
            AudioRecord.getMinBufferSize(audioCodec.getSampleRate(), AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));

        recorder.startRecording();

        while (isRecordThreadRun) {
          bytesRead = recorder.read(rawBuffer, 0, audioCodec.getRawBufferSize());

          // push audio to queue
          for (ConcurrentLinkedQueue<byte[]> recorderQueue :recorderQueueMap.values()) {
            byte[] temp = Arrays.copyOf(rawBuffer, rawBuffer.length);
            recorderQueue.offer(temp);
          }
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
}
