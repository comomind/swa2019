package com.lg.sixsenses.willi.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.codec.audio.AbstractAudioCodecFactory;
import com.lg.sixsenses.willi.codec.audio.AudioCodec;
import com.lg.sixsenses.willi.codec.audio.AudioCodecConst;
import com.lg.sixsenses.willi.codec.audio.AudioCodecFactory;
import com.lg.sixsenses.willi.logic.callmanager.AudioIo;
import com.lg.sixsenses.willi.logic.callmanager.AudioPlayer;
import com.lg.sixsenses.willi.logic.callmanager.AudioRecorder;
import com.lg.sixsenses.willi.logic.callmanager.VideoIo;
import com.lg.sixsenses.willi.net.JitterBuffer;
import com.lg.sixsenses.willi.util.Util;

import java.io.InputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestActivity extends AppCompatActivity {
    public static final String TAG = TestActivity.class.getName().toString();

    private final int JITTER_BUFFER_JITTER = 30;
    private final int JITTER_BUFFER_PERIOD = 200;

    private final int RECEIVE_PORT1 = 60001;
    private final int RECEIVE_PORT2 = 60002;

    private EditText remoteIpText1;
    private EditText remoteIpText2;
    private EditText remotePortText1;
    private EditText remotePortText2;

    private TextView textView;
    private ImageView imageView;

    private InetAddress remoteIpAddress1;
    private InetAddress remoteIpAddress2;

    private int port1;
    private int port2;

    private AudioRecorder audioRecorder;
    private AudioPlayer audioPlayer;
    private AudioIo audioIo1;
    private AudioIo audioIo2;
    private VideoIo videoIo1;

    private TestActivityHandler handler;
    private AudioCodec audioCodec;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        remoteIpText1 = (EditText)findViewById(R.id.editTextIP1);
        remoteIpText1.setText(Util.getIPAddress());
        remoteIpText1.setSelection(remoteIpText1.length());

        remoteIpText2 = (EditText)findViewById(R.id.editTextIP2);
        remoteIpText2.setText(Util.getIPAddress());
        remoteIpText2.setSelection(remoteIpText2.length());

        remotePortText1 = (EditText)findViewById(R.id.editTextPort1);
        remotePortText1.setText("60001");

        remotePortText2 = (EditText)findViewById(R.id.editTextPort2);
        remotePortText2.setText("60002");

        textView = (TextView)findViewById(R.id.textViewIP);
        textView.setText(Util.getIPAddress());

        imageView = (ImageView)findViewById(R.id.imageViewVideo);

        /*
            create resource
                audio codec for audioio, player, recorder
                (per thread) concurrent queue for recorder, audioio
                (per thread) jitterbuffer for player, audioio

                handler for videoio
                (per thread) view id for videoio
         */
        AbstractAudioCodecFactory codecFactory = new AudioCodecFactory();
        audioCodec = codecFactory.getCodec(AudioCodecConst.CodecType.OPUS);
        handler = new TestActivityHandler();

        ConcurrentLinkedQueue<byte[]> recorderQueue = new ConcurrentLinkedQueue<byte[]>();

        JitterBuffer jitterBuffer = new JitterBuffer(JITTER_BUFFER_JITTER, JITTER_BUFFER_PERIOD, audioCodec.getSampleRate());

        audioRecorder = new AudioRecorder(audioCodec);
        audioRecorder.addRecorderQueue("test", recorderQueue);

        audioPlayer = new AudioPlayer(getApplicationContext(), audioRecorder.getAudioSessionId(), audioCodec);
        audioPlayer.addJitterBuffer("test", jitterBuffer);

//        audioIo1 = new AudioIo(getApplicationContext(), audioCodec, jitterBuffer, recorderQueue);
//        audioIo2 = new AudioIo(getApplicationContext(), audioCodec, jitterBuffer, recorderQueue);

        videoIo1 = new VideoIo(getApplicationContext(), handler, imageView.getId());
    }

    public void buttonSendClick(View view) {
        try {
            audioRecorder.startRecord();

            InetAddress remoteIp1 = InetAddress.getByName(remoteIpText1.getText().toString());

            int port1 = Integer.valueOf(remotePortText1.getText().toString());
            audioIo1.startSend(remoteIp1, port1);

            InetAddress remoteIp2 = InetAddress.getByName(remoteIpText2.getText().toString());
            int port2 = Integer.valueOf(remotePortText2.getText().toString());
            audioIo2.startSend(remoteIp2, port2);

//            videoIo1.startSend(remoteIp, 61001);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void buttonReceiveClick(View view) {
        audioPlayer.startPlay();

        audioIo1.startReceive(RECEIVE_PORT1);
        audioIo2.startReceive(RECEIVE_PORT2);

//        videoIo1.startReceive(61001);
    }

    public void buttonStopClick(View view) {
        Log.d(TAG, "stopAll");
        audioIo1.stopAll();
        audioIo2.stopAll();

        videoIo1.stopAll();
        audioRecorder.stopRecord();
        audioPlayer.stopPlay();
    }

    public class TestActivityHandler extends Handler {
        // for Message.what
        public static final int CMD_VIEW_UPDATE = 1;
        public static final int CMD_VIEW_CLEAR = 2;

        // for Message kdy
        public static final String KEY_IMAGE_VIEW_ID = "image_view_id";
        public static final String KEY_IMAGE_BYTES = "image_bytes";


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.d(TAG, "handleMessage: " + msg.what);

            switch (msg.what) {
                case CMD_VIEW_UPDATE: {
                    Bundle bundle = msg.getData();

                    int viewId = bundle.getInt(KEY_IMAGE_VIEW_ID);
                    Log.d(TAG, "viewId: " + imageView.getId() + " viewId(msg)" + viewId);

                    byte[] imageBytes = bundle.getByteArray(KEY_IMAGE_BYTES);

                    final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    final Matrix matrix = new Matrix();
                    matrix.postRotate(-90);
                    final Bitmap rotator = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix,
                        true);

                    // TODO: get image view by id
                    imageView.setImageBitmap(rotator);
                }
                break;

                case CMD_VIEW_CLEAR: {
                    imageView.setImageBitmap(null);
                }
                break;

                default: {
                    Log.d(TAG, "CallStateActivityHandler received suspicious msg!");
                }
                break;
            }
        }
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
