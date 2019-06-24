package com.lg.sixsenses.willi.codec.audio;

// GsmCodec 0610 (Full Rate)
public class GsmCodec extends AudioCodec {
  private static final int SAMPLE_RATE = 8000;
  private static final int NUMBER_OF_CHANNELS = 1;
  private static final int SAMPLE_INTERVAL = 20;   // Milliseconds
  private static final int BYTES_PER_SAMPLE = 2;    // Bytes Per Sample
  private static final int RAW_BUFFER_SIZE = 80;
  private static final int GSM_BUFFER_SIZE = 33;
  private static final int FRAME_SIZE = 160;

  public GsmCodec() {
    super(SAMPLE_RATE, FRAME_SIZE, NUMBER_OF_CHANNELS);
    setSampleInterval(SAMPLE_INTERVAL);
    setBytesPerSample(BYTES_PER_SAMPLE);
    setRawBufferSize(FRAME_SIZE * NUMBER_OF_CHANNELS * 2);
    setEncodedBufferSize(GSM_BUFFER_SIZE);
  }

  @Override
  public int open() {
    return gsmOpenNative();
  }

  @Override
  public int decode(byte input[], byte output[]) {
    return gsmDecodeNative(input, output);
  }

  @Override
  public int encode(byte input[], byte output[]) {
    return gsmEncodeNative(input, output);
  }

  @Override
  public void close() {
    gsmCloseNative();
  }

  public static native int gsmOpenNative();
  public static native int gsmDecodeNative(byte input[], byte output[]);
  public static native int gsmEncodeNative(byte input[], byte output[]);
  public static native void gsmCloseNative();
}
