package lg.dplakosh.lgvoipdemo.codec.audio;

public abstract class AudioCodec {
  private int frameSize = 0;
  private int sampleRate = 0;
  private int numberOfChannels = 0;
  private int bytesPerSample = 0;
  private int sampleInterval = 0;
  private int rawBufferSize = 0;
  private int encodedBufferSize = 0;

  public AudioCodec() {};
  public AudioCodec(int sampleRate, int frameSize, int numberOfChannels) {
    this.sampleRate = sampleRate;
    this.frameSize = frameSize;
    this.numberOfChannels = numberOfChannels;
  }

  public int getRawBufferSize() {
    return rawBufferSize;
  }

  public void setRawBufferSize(int rawBufferSize) {
    this.rawBufferSize = rawBufferSize;
  }

  public int getFrameSize() {
    return frameSize;
  };
  public int getSampleRate() {
    return sampleRate;
  }
  public int getNumChannels() {
    return numberOfChannels;
  }
  public int getNumberOfChannels() {
    return numberOfChannels;
  }

  public int getBytesPerSample() {
    return bytesPerSample;
  }

  public int getSampleInterval() {
    return sampleInterval;
  }

  public int getEncodedBufferSize() {
    return encodedBufferSize;
  }

  public void setFrameSize(int frameSize) {
    this.frameSize = frameSize;
  }

  public void setSampleRate(int sampleRate) {
    this.sampleRate = sampleRate;
  }

  public void setNumberOfChannels(int numberOfChannels) {
    this.numberOfChannels = numberOfChannels;
  }

  public void setBytesPerSample(int bytesPerSample) {
    this.bytesPerSample = bytesPerSample;
  }

  public void setSampleInterval(int sampleInterval) {
    this.sampleInterval = sampleInterval;
  }

  public void setEncodedBufferSize(int encodedBufferSize) {
    this.encodedBufferSize = encodedBufferSize;
  }

  abstract public int open();
  abstract public int decode(byte input[], byte output[]);
  abstract public int encode(byte input[], byte output[]);
  abstract public void close();
}
