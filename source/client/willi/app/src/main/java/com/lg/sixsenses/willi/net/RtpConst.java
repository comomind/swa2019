package com.lg.sixsenses.willi.net;

import java.util.HashMap;
import java.util.Map;

public class RtpConst {
  public enum PayloadType {
    PCMU(0),
    GSM(3),
    LPC(7),
    PCMA(8),
    G722(9),
    G729(18);

    private int value;
    private static Map map = new HashMap<>();

    private PayloadType(int value) {
      this.value = value;
    }

    static {
      for (PayloadType pageType : PayloadType.values()) {
        map.put(pageType.value, pageType);
      }
    }

    public static PayloadType valueOf(int pageType) {
      return (PayloadType) map.get(pageType);
    }

    public int getValue() {
      return value;
    }
  }

}
