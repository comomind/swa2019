package com.lg.sixsenses.willi.codec.audio;

public abstract class AbstractAudioCodecFactory {
  public abstract AudioCodec getCodec(AudioCodecConst.CodecType codecType);
}
