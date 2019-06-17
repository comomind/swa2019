package lg.dplakosh.lgvoipdemo.net;

public class AudioClock extends RtpClock {
  private double samplesPerMillisecond;

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
  public long getTimestamp(long time) {
    return (long) (time * samplesPerMillisecond);
  }
}
