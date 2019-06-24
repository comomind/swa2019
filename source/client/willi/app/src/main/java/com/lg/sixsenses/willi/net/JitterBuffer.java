package com.lg.sixsenses.willi.net;

import android.util.Log;

import com.lg.sixsenses.willi.util.BufferConcurrentLinkedQueue;

import java.io.Serializable;

public class JitterBuffer implements Serializable {
  private static final String TAG = JitterBuffer.class.getSimpleName();

  private int period;
  private int jitter;
  private int jitterSamples;
  private BufferConcurrentLinkedQueue<RtpPacket> queue = new BufferConcurrentLinkedQueue<RtpPacket>();
  private volatile boolean ready = false;

  private long duration;
  private volatile long timestamp;
  //private Format format;
  private int sampleRate;
  private RtpClock clock;
  private long delta;

  Object lock = new Object();

  public JitterBuffer(int jitter, int period) {
    this.period = period;
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
    if(clock != null)
      this.jitterSamples = (int)clock.getTimestamp(jitter);
  }

  public int getJitter() {
    return jitter;
  }

  public void setPeriod(int period) {
    this.period = period;
  }

  public void write(RtpPacket rtpPacket) {
    long t = clock.getTime(rtpPacket.getTimestamp());

    synchronized(lock) {

      //when first packet arrive and timestamp already known
      //we have to determine difference between rtp stream timestamps and local time
      if (delta == 0 && timestamp > 0) {
        delta = t - timestamp;
        timestamp += delta;
      }

      //if buffer's ready flag equals true then it means that reading
      //starting and we should compare timestamp of arrived packet with time of
      //last reading.
      Log.i(TAG, "RX packet: rx ts = " + t + ", local ts = " + timestamp + ", diff = " + (t - timestamp));
//      if (ready && t > timestamp + jitterSamples) {
      if (ready && t > timestamp + jitter) {
        //silently discard outstanding packet
        Log.w(TAG, "Packet " + rtpPacket + " is discarded by jitter buffer");
        return;
      }




    }

    //if RTP packet is not outstanding or reading not started yet (ready == false)
    //queue packet.
    queue.offer(rtpPacket);

    //allow read when buffer is full;
    duration += period;
    if (!ready && duration > (period + jitter)) {
      ready = true;
    }
  }

  public void reset() {
    queue.clear();
    duration = 0;
    clock.reset();
    delta = 0;
  }

  public RtpPacket read() {
    //discard buffer is buffer is not full yet
    if (!ready) {
      return null;
    }

    synchronized(lock) {
      long now = System.currentTimeMillis();
      //remember timestamp
      this.timestamp = clock.getTimestamp(now) + delta;
    }

    //if packet queue is empty (but was full) we have to returns
    //silence
    if (queue.isEmpty()) {
      return null;
    }

    //fill media buffer
    return queue.poll();
  }
}