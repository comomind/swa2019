package com.lg.sixsenses.willi.codec.audio;

public class AudioCodecConst {
  public static final int MILLISECONDS_IN_A_SECOND = 1000;

  public enum CodecType {
    PCMU(0),
    GSM(3),
    LPC(7),
    PCMA(8),
    G722(9),
    G729(18),
    OPUS(114);

    private int value;
    CodecType(int i) {
      this.value = i;
    }
    public int getValue() {
      return this.value;
    }

    public static CodecType fromInt(int i) {
      for (CodecType codecType : CodecType.values()) {
        if (codecType.getValue() == i) {
          return codecType;
        }
      }
      return null;
    }
  }
}
