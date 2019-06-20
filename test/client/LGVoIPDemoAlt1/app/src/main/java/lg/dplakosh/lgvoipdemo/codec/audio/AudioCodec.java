package lg.dplakosh.lgvoipdemo.codec.audio;

public abstract class AudioCodec {


  abstract public int getSampleRate();

  abstract public int open();
  abstract public int decode(byte input[], byte output[]);
  abstract public int encode(byte input[], byte output[]);
  abstract public void close();
}
