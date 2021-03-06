package lg.dplakosh.lgvoipdemo.codec.audio;

import android.util.Log;

public class AudioCodecFactory {
  public static final String TAG = AudioCodecFactory.class.getSimpleName();

  public static AudioCodec getCodec(AudioCodecConst.CodecType codecType) {
    if (codecType == AudioCodecConst.CodecType.GSM) {
      Log.d(TAG, "return GsmCodec instance");
      return new GsmCodec();
    } else if (codecType == AudioCodecConst.CodecType.OPUS) {
      Log.d(TAG, "return OpusCodec instance");
      return new OpusCodec();
    }

    return null;
  }
}
