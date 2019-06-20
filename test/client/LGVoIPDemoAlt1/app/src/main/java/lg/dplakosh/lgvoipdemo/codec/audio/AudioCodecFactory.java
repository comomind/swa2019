package lg.dplakosh.lgvoipdemo.codec.audio;

import android.util.Log;

import lg.dplakosh.lgvoipdemo.codec.CodecConst;

public class AudioCodecFactory {
  public static final String TAG = AudioCodecFactory.class.getSimpleName();

  public static AudioCodec getCodec(CodecConst.CodecType codecType) {
    if (codecType == CodecConst.CodecType.GSM) {
      Log.d(TAG, "return GSM instance");
      return new GSM();
    }

    return null;
  }
}
