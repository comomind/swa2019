package lg.dplakosh.lgvoipdemo.net;

public abstract class RtpClock {

  // Packet size in ms
  int sampleRate;

  protected long now;
  private boolean isSynchronized;

  public RtpClock() {
  }

  public int getSampleRate() {
    return sampleRate;
  }

  public void setSampleRate(int SampleRate) {
    this.sampleRate = SampleRate;
  }

  public void synchronize(long initial) {
    now = initial;
    this.isSynchronized = true;
  }

  public boolean isSynchronized() {
    return this.isSynchronized();
  }

  protected long now() {
    return now;
  }

  public void reset() {
    now = 0;
    this.isSynchronized = false;
    this.sampleRate = -1;
  }

  public abstract long getTime(long timestamp);
  public abstract double getTime(double timestamp);
  public abstract long getTimestamp(long time);
}
