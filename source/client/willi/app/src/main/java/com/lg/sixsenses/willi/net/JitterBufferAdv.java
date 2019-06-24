package com.lg.sixsenses.willi.net;

import android.util.Log;

import com.lg.sixsenses.willi.util.BufferConcurrentLinkedQueue;

import java.io.Serializable;


public class JitterBufferAdv implements Serializable {
  private static final String TAG = JitterBufferAdv.class.getSimpleName();

  private BufferConcurrentLinkedQueue<RtpPacket> queue = new BufferConcurrentLinkedQueue<RtpPacket>();

  private int waitingTime;        // waiting time (ms) after first packet received
  private int jitter;             // jitter (ms)

  private RtpClock clock;

  private volatile boolean isReady = false;
  private volatile boolean isFirstReceived = true;

  // for receiver
  private long prevReceiverTime = 0;
  private long prevReceiverTimestamp = 0;
  private long prevReceiverTimeUnit = 0;
  private long firstPacketReceiverTime = 0;

  // for sender
  private long prevSenderTimestamp = 0;

  // for jitter
  private double prevJitterTimestamp = 0;

  private double latency = 0;
  private int sampleRate;

  Object lock = new Object();

  public JitterBufferAdv(int jitter, int waitingTime) {
    this.waitingTime = waitingTime;
    setJitter(jitter);
  }

  public void setClock(RtpClock clock) {
    this.clock = clock;
    if (sampleRate > 0) {
      clock.setSampleRate(sampleRate);
      setJitter(jitter);
    }
  }

  public void setSampleRate(int sampleRate) {
    this.sampleRate = sampleRate;
    if (clock != null) {
      clock.setSampleRate(sampleRate);
    }
  }

  private void setJitter(int jitter) {
    this.jitter = jitter;
  }

  public int getJitter() {
    return jitter;
  }

  public void setWaitingTime(int waitingTime) {
    this.waitingTime = waitingTime;
  }

  public void reset() {
    queue.clear();
    clock.reset();
  }

  public void write(RtpPacket rtpPacket) {
    long senderTimestamp = 0;
    long receiverTime = 0;
    long receiverTimeUnit = 0;
    long receiverTimestamp = 0;
    long delayTimestamp = 0;
    double jitterTimestamp = 0;

    senderTimestamp = rtpPacket.getTimestamp();
    receiverTime = System.currentTimeMillis();

    if (!isFirstReceived) {
      receiverTimeUnit = (receiverTime - prevReceiverTime) + prevReceiverTimeUnit;
      receiverTimestamp = clock.getTimestamp(receiverTimeUnit);

      delayTimestamp = (receiverTimestamp - prevReceiverTimestamp) - (senderTimestamp - prevSenderTimestamp);
      if (delayTimestamp < 0) {
        delayTimestamp = -delayTimestamp;
      }

      jitterTimestamp = prevJitterTimestamp + (delayTimestamp - prevJitterTimestamp)/(double)16;

      // discard packet if exceed delay boundary (latency + jitter)
//      if (receiverTimeUnit > latency + jitter) {
//        Log.d(TAG, "write: drop packet, " + receiverTimeUnit + " " + latency + " " + jitter);
//        return;
//      }
    }

    // enqueue
    queue.offer(rtpPacket);

    // post action
    if (isFirstReceived) {
      isFirstReceived = false;
      firstPacketReceiverTime = receiverTime;
    }

    prevSenderTimestamp = senderTimestamp;
    prevJitterTimestamp = jitterTimestamp;

    prevReceiverTime = receiverTime;
    prevReceiverTimestamp = receiverTimestamp;
    prevReceiverTimeUnit = receiverTimeUnit;

    latency = clock.getTime(receiverTimestamp - senderTimestamp);

    Log.d(TAG, "seq: " + rtpPacket.getSequenceNumber() +
        " Si (timestamp): " + senderTimestamp +
        " Si (ms): " + clock.getTime(senderTimestamp) +
        " Rec(i)(ms): " + receiverTimeUnit +
        " RecTS(i)(timestamp): " + receiverTimestamp +
        " j(timestamp): " + jitterTimestamp +
        " j(ms): " + String.format("%.2f", clock.getTime(jitterTimestamp)) +
        " latency(ms): " + clock.getTime(receiverTimestamp - senderTimestamp)
    );

    Log.d(TAG, "write");
  }

  public RtpPacket read() {
    long currentTime = System.currentTimeMillis();
    if (currentTime > firstPacketReceiverTime + waitingTime) {
      isReady = true;
    }

    if (!isReady) {
      Log.d(TAG, "read failed: !isReady");
      return null;
    }

    if (queue.isEmpty()) {
      Log.d(TAG, "read failed: isEmpty");
      return null;
    }

    Log.d(TAG, "read");

    // dequeue
    return queue.poll();
  }

//  public RtpPacket read(long timestamp) {
//
//    if (timestamp > firstPacketReceiverTime + waitingTime) {
//      isReady = true;
//    }
//
//    //discard buffer is buffer is not full yet
//    if (!isReady) {
//      Log.d(TAG, "read failed: !isReady");
//      return null;
//    }
//
//    synchronized(lock) {
//      //remember timestamp
//      this.timestamp = timestamp + delta;
//    }
//
//    //if packet queue is empty (but was full) we have to returns
//    //silence
//    if (queue.isEmpty()) {
//      Log.d(TAG, "read failed: empty!");
//      return null;
//    }
//
//    Log.d(TAG, "read done");
//    //fill media buffer
//    return queue.poll();
//  }
}
