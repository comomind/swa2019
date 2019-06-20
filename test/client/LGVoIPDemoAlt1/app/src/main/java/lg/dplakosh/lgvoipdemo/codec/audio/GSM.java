package lg.dplakosh.lgvoipdemo.codec.audio;

import lg.dplakosh.lgvoipdemo.codec.audio.AudioCodec;

// GSM 0610 (Full Rate)
public class GSM extends AudioCodec {
  private static final int SAMPLE_RATE = 8000;

  @Override
  public int getSampleRate() {
    return SAMPLE_RATE;
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
