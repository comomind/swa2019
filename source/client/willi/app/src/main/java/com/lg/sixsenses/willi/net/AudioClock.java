package com.lg.sixsenses.willi.net;

public class AudioClock extends RtpClock {
  private double samplesPerMillisecond;

  public AudioClock(int sampleRate) {
    super(sampleRate);
  }

  @Override
  public void setSampleRate(int SampleRate) {
    super.setSampleRate(SampleRate);
    this.samplesPerMillisecond = sampleRate / 1000;
  }

  @Override
  public long getTime(long timestamp) {
    return (long) (timestamp / samplesPerMillisecond);
  }

  @Override
  public double getTime(double timestamp) {
    return (timestamp / samplesPerMillisecond);
  }

  @Override
  public long getTimestamp(long time) {
    return (long) (time * samplesPerMillisecond);
  }

}
