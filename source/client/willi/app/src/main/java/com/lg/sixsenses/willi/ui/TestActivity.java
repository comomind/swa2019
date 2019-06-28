package com.lg.sixsenses.willi.ui;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.codec.audio.AbstractAudioCodecFactory;
import com.lg.sixsenses.willi.codec.audio.AudioCodecConst;
import com.lg.sixsenses.willi.codec.audio.AudioCodecFactory;
import com.lg.sixsenses.willi.logic.callmanager.AudioIo;
import com.lg.sixsenses.willi.util.Util;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Observable;
import java.util.Observer;

public class TestActivity extends AppCompatActivity implements Observer {
    public static final String TAG = TestActivity.class.getName().toString();
    private EditText editTextIP;
    private TextView textView;

    private AudioIo audioIo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        editTextIP = (EditText)findViewById(R.id.editTextIP);
        editTextIP.setText(Util.getIPAddress());
        editTextIP.setSelection(editTextIP.length());

        textView = (TextView)findViewById(R.id.textViewIP);
        textView.setText(Util.getIPAddress());
        audioIo = new AudioIo(getApplicationContext());

        AbstractAudioCodecFactory codecFactory = new AudioCodecFactory();
        audioIo.setAudioCodec(codecFactory.getCodec(AudioCodecConst.CodecType.OPUS));
    }

    public void buttonSendClick(View view) {
        try {
            InetAddress remoteIp = InetAddress.getByName(editTextIP.getText().toString());
            audioIo.startSend(remoteIp, 60001);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void buttonReceiveClick(View view) {
        audioIo.startReceive(60001);

    }

    public void buttonStopClick(View view) {
        Log.d(TAG, "stopAll");
        audioIo.stopAll();
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    public void buttonMixPlayClick(View view) {

        Thread test = new Thread(new Runnable() {
            @Override
            public void run() {
                final int BUFFER_SIZE = 320;
                final int SAMPLE_RATE = 8000;
                final int MAX_COUNT = 1000;

                // open 2 files
                Context context = getApplicationContext();

                InputStream file1 = context.getResources().openRawResource(R.raw.t18k16bit);
                InputStream file2 = context.getResources().openRawResource(R.raw.t28k16bit);

                byte[] file1Buffer = new byte[BUFFER_SIZE];
                byte[] file2Buffer = new byte[BUFFER_SIZE];
                byte[] mixedBuffer = new byte[BUFFER_SIZE];

                int file1ReadSize = 0;
                int file2ReadSize = 0;
                int count = 0;

                AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));

                AudioTrack outputTrack = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        //	.setFlags(AudioAttributes.FLAG_LOW_LATENCY) //This is Nougat+ only (API 25) comment if you have lower
                        .build())
                    .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                    .setBufferSizeInBytes(BUFFER_SIZE)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    //.setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY) //Not until Api 26
                    .setSessionId(recorder.getAudioSessionId())
                    .build();

                recorder.startRecording();
                outputTrack.play();

                while (count < MAX_COUNT) {
                    try {
                        file1ReadSize = file1.read(file1Buffer, 0, BUFFER_SIZE);
                        file2ReadSize = file2.read(file2Buffer, 0, BUFFER_SIZE);

                        if (file1ReadSize != BUFFER_SIZE && file1ReadSize != file2ReadSize) {
                            Log.d(TAG, "fileReadSize wrong error!");
                            break;
                        }

                        // mix
                        short[] buffer1Short = new short[BUFFER_SIZE / 2];
                        short[] buffer2Short = new short[BUFFER_SIZE / 2];
                        short[] mixedShort = new short[BUFFER_SIZE / 2];

                        ByteBuffer.wrap(file1Buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(buffer1Short);
                        ByteBuffer.wrap(file2Buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(buffer2Short);

                        for (int i = 0; i < buffer1Short.length; i++) {
                            float sample1 = buffer1Short[i] / 32768.0f;
                            float sample2 = buffer2Short[i] / 32768.0f;

                            float mixed = sample1 + sample2;

                            // reduce the volume a bit
                            mixed *= 0.8;

                            // hard clipping
                            if (mixed > 1.0f) {
                                mixed = 1.0f;
                            }
                            if (mixed < -1.0f) {
                                mixed = -1.0f;
                            }

                            short outputSample = (short)(mixed * 32768.0f);
                            mixedShort[i] = outputSample;
                        }


                        outputTrack.write(mixedShort, 0, mixedShort.length);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    count++;
                }


                try {
                    recorder.stop();
                    recorder.release();
                    outputTrack.stop();
                    outputTrack.flush();
                    outputTrack.release();


                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        test.start();


    }

}
