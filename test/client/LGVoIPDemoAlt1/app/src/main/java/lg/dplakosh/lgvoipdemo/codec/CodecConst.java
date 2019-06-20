package lg.dplakosh.lgvoipdemo.codec;

public class CodecConst {

  public enum CodecType {
    PCMU(0),
    GSM(3),
    LPC(7),
    PCMA(8),
    G722(9),
    G729(18);

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
