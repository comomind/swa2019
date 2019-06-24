package lg.dplakosh.lgvoipdemo.codec.audio;

public class OpusCodec extends AudioCodec {
  private static final int SAMPLE_RATE = 8000;
  private static final int SAMPLE_INTERVAL = 20;   // Milliseconds
  private static final int BYTES_PER_SAMPLE = 2;    // Bytes Per Sample
  private static final int NUMBER_OF_CHANNELS = 1;
  private static final int FRAME_SIZE = 160;

  public OpusCodec() {
    super(SAMPLE_RATE, FRAME_SIZE, NUMBER_OF_CHANNELS);
    setSampleInterval(SAMPLE_INTERVAL);
    setBytesPerSample(BYTES_PER_SAMPLE);
    setRawBufferSize(FRAME_SIZE * NUMBER_OF_CHANNELS * 2);
    setEncodedBufferSize(1024);
  }

  @Override
  public int open() {
    return opusOpenNative(SAMPLE_RATE, NUMBER_OF_CHANNELS);
  }

  @Override
  public int decode(byte[] input, byte[] output) {
    return opusDecodeNative(input, FRAME_SIZE, output);
  }

  @Override
  public int encode(byte[] input, byte[] output) {
    return opusEncodeNative(input, FRAME_SIZE, output);
  }

  @Override
  public void close() {
    opusCloseNative();
  }

  public static native int opusOpenNative(int sampleRate, int numberOfChannels);
  public static native int opusDecodeNative(byte input[], int frameSize, byte output[]);
  public static native int opusEncodeNative(byte input[], int frameSize, byte output[]);
  public static native void opusCloseNative();
}
