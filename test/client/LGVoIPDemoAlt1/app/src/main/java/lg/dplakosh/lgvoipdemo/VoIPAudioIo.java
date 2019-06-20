package lg.dplakosh.lgvoipdemo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.net.InetSocketAddress;
import java.io.InputStream;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;
import android.os.Process;

import lg.dplakosh.lgvoipdemo.codec.audio.AudioCodec;
import lg.dplakosh.lgvoipdemo.net.AudioClock;
import lg.dplakosh.lgvoipdemo.net.JitterBuffer;
import lg.dplakosh.lgvoipdemo.net.JitterBuffer1;
import lg.dplakosh.lgvoipdemo.net.RtpConst;
import lg.dplakosh.lgvoipdemo.net.RtpPacket;

public class VoIPAudioIo {

    private static final String TAG = "VoIPAudioIo";
    private static final int MILLISECONDS_IN_A_SECOND = 1000;
    private static final int SAMPLE_RATE = 8000; // Hertz
    private static final int SAMPLE_INTERVAL = 20;   // Milliseconds
    private static final int BYTES_PER_SAMPLE = 2;    // Bytes Per Sample
    private static final int TIMESTAMP_INCREMENT_VALUE = SAMPLE_RATE / (MILLISECONDS_IN_A_SECOND / SAMPLE_INTERVAL);
    private static final int RAW_BUFFER_SIZE = SAMPLE_RATE / (MILLISECONDS_IN_A_SECOND / SAMPLE_INTERVAL) * BYTES_PER_SAMPLE;
    private static final int GSM_BUFFER_SIZE = 33;
    private static final int VOIP_DATA_UDP_PORT = 5124;
    private int mSimVoice;
    private Context mContext;
    private Thread AudioIoThread = null;
    private Thread UdpReceiveDataThread = null;
    private DatagramSocket RecvUdpSocket;
    private InetAddress RemoteIp;                   // Address to call
    private boolean IsRunning = false;
    private boolean AudioIoThreadThreadRun = false;
    private boolean UdpVoipReceiveDataThreadRun = false;
//    private ConcurrentLinkedQueue<byte[]> incommingPacketQueue;

    private AudioCodec audioCodec;
    private JitterBuffer jitterBuffer;
    private int RtpSeqNum = 0;

    private int mTimestampOffset = 0;

    // for receiver
    private long prevReceiverTime = 0;
    private long prevReceiverTimestamp = 0;
    private long prevReceiverTimeUnit = 0;

    // for sender
    private long prevSenderTimestamp = 0;

    // D = transit relative time
//    private long prevDelayTimestamp = 0;

    // for jitter
    private double prevJitterTimestamp = 0;


    VoIPAudioIo(Context context) {
        mContext = context;

    }

    public AudioCodec getAudioCodec() {
        return audioCodec;
    }

    public void setAudioCodec(AudioCodec audioCodec) {
        this.audioCodec = audioCodec;
    }

    synchronized boolean StartAudio(InetAddress IP, int SimVoice) {
        if (IsRunning) return (true);
        if (audioCodec.open() == 0)
            Log.i(TAG, "JniGsmOpen() Success");
//        incommingPacketQueue = new ConcurrentLinkedQueue<>();
        mSimVoice = SimVoice;
        this.RemoteIp = IP;
        StartAudioIoThread();
        StartReceiveDataThread();
        IsRunning = true;
        return (false);
    }

    synchronized boolean EndAudio() {
        if (!IsRunning) return (true);
        Log.i(TAG, "Ending Viop Audio");
        if (UdpReceiveDataThread != null && UdpReceiveDataThread.isAlive()) {
            UdpVoipReceiveDataThreadRun = false;
            RecvUdpSocket.close();
            Log.i(TAG, "UdpReceiveDataThread Thread Join started");
            UdpVoipReceiveDataThreadRun = false;
            try {
                UdpReceiveDataThread.join();
            } catch (InterruptedException e) {
                Log.i(TAG, "UdpReceiveDataThread Join interruped");
            }
            Log.i(TAG, " UdpReceiveDataThread Join successs");
        }
        if (AudioIoThread != null && AudioIoThread.isAlive()) {
            AudioIoThreadThreadRun = false;
            Log.i(TAG, "Audio Thread Join started");

            try {
                AudioIoThread.join();
            } catch (InterruptedException e) {
                Log.i(TAG, "Audio Thread Join interruped");
            }
            Log.i(TAG, "Audio Thread Join successs");
        }

        AudioIoThread = null;
        UdpReceiveDataThread = null;
//        incommingPacketQueue = null;
        RecvUdpSocket = null;
        audioCodec.close();
        IsRunning = false;
        return (false);
    }

    private InputStream OpenSimVoice(int SimVoice) {
        InputStream VoiceFile = null;
        switch (SimVoice) {
            case 0:
                break;
            case 1:
                VoiceFile = mContext.getResources().openRawResource(R.raw.t18k16bit);
                break;
            case 2:
                VoiceFile = mContext.getResources().openRawResource(R.raw.t28k16bit);
                break;
            case 3:
                VoiceFile = mContext.getResources().openRawResource(R.raw.t38k16bit);
                break;
            case 4:
                VoiceFile = mContext.getResources().openRawResource(R.raw.t48k16bit);
                break;
            default:
                break;
        }
        return VoiceFile;
    }

    private void StartAudioIoThread() {
        // Creates the thread for capturing and transmitting audio
        AudioIoThreadThreadRun = true;
        AudioIoThread = new Thread(new Runnable() {

            @Override
            public void run() {
                InputStream InputPlayFile;
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                int PreviousAudioManagerMode = 0;
                if (audioManager != null) {
                    PreviousAudioManagerMode = audioManager.getMode();
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION); //Enable AEC
                }

                // Create an instance of the AudioRecord class
                Log.i(TAG, "Audio Thread started. Thread id: " + Thread.currentThread().getId());
                InputPlayFile = OpenSimVoice(mSimVoice);
                AudioRecord Recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));

                AudioTrack OutputTrack = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        //	.setFlags(AudioAttributes.FLAG_LOW_LATENCY) //This is Nougat+ only (API 25) comment if you have lower
                        .build())
                    .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                    .setBufferSizeInBytes(RAW_BUFFER_SIZE)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    //.setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY) //Not until Api 26
                    .setSessionId(Recorder.getAudioSessionId())
                    .build();

                int BytesRead;
                byte[] rawbuf = new byte[RAW_BUFFER_SIZE];
                byte[] gsmbuf = new byte[GSM_BUFFER_SIZE];
                try {
                    // Create a socket and start recording
                    Log.i(TAG, "Packet destination: " + RemoteIp.toString());
                    DatagramSocket socket = new DatagramSocket();
                    Recorder.startRecording();
                    OutputTrack.play();

                    Random random = new Random();
//                    mTimestampOffset = random.nextInt();
                    mTimestampOffset = 0;
                    RtpSeqNum = 0;

                    while (AudioIoThreadThreadRun) {
                        RtpPacket packetFromJitterbuffer = jitterBuffer.read();
                        if (packetFromJitterbuffer != null) {
                          packetFromJitterbuffer.getPayload(gsmbuf);
                          // decode
                          audioCodec.decode(gsmbuf, rawbuf);

                          byte[] AudioOutputBufferBytes = rawbuf;
                            if (!MainActivity.BoostAudio) {
                                OutputTrack.write(AudioOutputBufferBytes, 0, RAW_BUFFER_SIZE);
                            }
                            else {
                                short[] AudioOutputBufferShorts = new short[AudioOutputBufferBytes.length / 2];
                                // to turn bytes to shorts as either big endian or little endian.
                                ByteBuffer.wrap(AudioOutputBufferBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(AudioOutputBufferShorts);
                                for (int i = 0; i < AudioOutputBufferShorts.length; i++) { // 16bit sample size
                                    int value=AudioOutputBufferShorts[i]*10; //increase level by gain=20dB: Math.pow(10., dB/20.);  dB to gain factor
                                    if(value > 32767) {
                                        value = 32767;
                                    } else if(value < -32767) {
                                        value = -32767;
                                    }
                                    AudioOutputBufferShorts[i]=(short)value;
                                }
                                // to turn shorts back to bytes.
                                //byte[] AudioOutputBufferBytes2 = new byte[AudioOutputBufferShorts.length * 2];
                                //ByteBuffer.wrap(AudioOutputBufferBytes2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(AudioOutputBufferShorts);
                                //OutputTrack.write(AudioOutputBufferBytes2, 0, RAW_BUFFER_SIZE);
                                OutputTrack.write( AudioOutputBufferShorts, 0,  AudioOutputBufferShorts.length);
                            }
                        }
                        // Capture audio from microphone and send
                        BytesRead = Recorder.read(rawbuf, 0, RAW_BUFFER_SIZE);
                        if (InputPlayFile != null) {
                            BytesRead = InputPlayFile.read(rawbuf, 0, RAW_BUFFER_SIZE);
                            if (BytesRead != RAW_BUFFER_SIZE) {
                                InputPlayFile.close();
                                InputPlayFile = OpenSimVoice(mSimVoice);
                                BytesRead = InputPlayFile.read(rawbuf, 0, RAW_BUFFER_SIZE);
                            }
                        }
                        if (BytesRead == RAW_BUFFER_SIZE) {
                            audioCodec.encode(rawbuf, gsmbuf);

                            // rtp packet
//                            RtpPacket rtpPacket = new RtpPacket(RtpConst.PayloadType.GSM.getValue(), RtpSeqNum++, TrueTime.now().getTime(), gsmbuf, GSM_BUFFER_SIZE );
                            RtpPacket rtpPacket = new RtpPacket(RtpConst.PayloadType.GSM.getValue(), RtpSeqNum, mTimestampOffset + (RtpSeqNum * TIMESTAMP_INCREMENT_VALUE), gsmbuf, GSM_BUFFER_SIZE );
                            int packetLen = rtpPacket.getLength();
                            byte[] packetBits = new byte[packetLen];
                            rtpPacket.getPacket(packetBits);

                            DatagramPacket packet = new DatagramPacket(packetBits, packetLen, RemoteIp, VOIP_DATA_UDP_PORT);
                            socket.send(packet);
                            RtpSeqNum++;
                        }
                    }

                    // Stop Audio Thread);
                    Recorder.stop();
                    Recorder.release();
                    OutputTrack.stop();
                    OutputTrack.flush();
                    OutputTrack.release();
                    socket.disconnect();
                    socket.close();
                    if (InputPlayFile != null) InputPlayFile.close();
                    if (audioManager != null) audioManager.setMode(PreviousAudioManagerMode);
                    Log.i(TAG, "Audio Thread Stopped");
                } catch (SocketException e) {
                    AudioIoThreadThreadRun = false;
                    Log.e(TAG, "SocketException: " + e.toString());
                } catch (UnknownHostException e) {
                    AudioIoThreadThreadRun = false;
                    Log.e(TAG, "UnknownHostException: " + e.toString());
                } catch (IOException e) {
                    AudioIoThreadThreadRun = false;
                    Log.e(TAG, "IOException: " + e.toString());
                }
            }
        });
        AudioIoThread.start();
    }

    private void StartReceiveDataThread() {
        // Create thread for receiving audio data
        UdpVoipReceiveDataThreadRun = true;
        UdpReceiveDataThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // Create an instance of AudioTrack, used for playing back audio
                Log.i(TAG, "Receive Data Thread Started. Thread id: " + Thread.currentThread().getId());
                try {
                    // Setup socket to receive the audio data
                    RecvUdpSocket = new DatagramSocket(null);
                    RecvUdpSocket.setReuseAddress(true);
                    RecvUdpSocket.bind(new InetSocketAddress(VOIP_DATA_UDP_PORT));

                    boolean isFirstReceived = true;
                    AudioClock audioClock = new AudioClock();
                    // TODO: Need to find out proper(jitter & period) through experiment
                    jitterBuffer = new JitterBuffer(30, 200);
                    jitterBuffer.setSampleRate(SAMPLE_RATE);
                    jitterBuffer.setClock(audioClock);

                    while (UdpVoipReceiveDataThreadRun) {

                        byte[] rtpBuffer = new byte[RtpPacket.HEADER_SIZE + GSM_BUFFER_SIZE];
                        DatagramPacket packet = new DatagramPacket(rtpBuffer, RtpPacket.HEADER_SIZE + GSM_BUFFER_SIZE);
                        RecvUdpSocket.receive(packet);

                        RtpPacket rtpPacket = new RtpPacket(packet.getData(), packet.getLength());

//                        // calculate jitter
//                        long senderTimestamp = 0;
//                        long receiverTime = 0;
//
//                        long receiverTimeUnit = 0;
//                        long receiverTimestamp = 0;
//
//                        long delayTimestamp = 0;
//                        double delayTime = 0;
//
//                        double jitterTimestamp = 0;
//                        double jitterTime = 0;
//
//                        senderTimestamp = rtpPacket.getTimestamp();
//                        receiverTime = System.currentTimeMillis();
//
//                        if (!isFirstReceived) {
//                            receiverTimeUnit = (receiverTime - prevReceiverTime) + prevReceiverTimeUnit;
//                            receiverTimestamp = receiverTimeUnit * 8000 / 1000;
//
//                            delayTimestamp = (receiverTimestamp - prevReceiverTimestamp) - (senderTimestamp - prevSenderTimestamp);
//                            if (delayTimestamp < 0) {
//                                delayTimestamp = -delayTimestamp;
//                            }
//
//                            jitterTimestamp = prevJitterTimestamp + (delayTimestamp - prevJitterTimestamp)/(double)16;
//                            jitterTime = jitterTimestamp / (double)(SAMPLE_RATE / 1000);
//
//                            delayTime = delayTimestamp / (double)(SAMPLE_RATE / 1000);
//                        }
//
//                        Log.d(TAG, "seq: " + rtpPacket.getSequenceNumber() +
//                            " Si (timestamp): " + senderTimestamp +
//                            " Si (ms): " + (senderTimestamp/(double)(SAMPLE_RATE/1000)) +
//                            " Rec(i)(ms): " + receiverTimeUnit +
//                            " RecTS(i)(timestamp): " + receiverTimestamp +
//                            " D(timestamp): " + delayTimestamp +
//                            " D(ms): " + String.format("%.2f", delayTime) +
//                            " j(timestamp): " + jitterTimestamp +
//                            " j(ms): " + String.format("%.2f", jitterTime) +
//                            " latency(ms): " + (receiverTimestamp - senderTimestamp)/8
//                        );
//
//                        // post action
//                        isFirstReceived = false;
////                        prevDelayTimestamp = delayTimestamp;
//                        prevSenderTimestamp = senderTimestamp;
//                        prevJitterTimestamp = jitterTimestamp;
//
//                        prevReceiverTime = receiverTime;
//                        prevReceiverTimestamp = receiverTimestamp;
//                        prevReceiverTimeUnit = receiverTimeUnit;

                        // push jitter buffer
                        jitterBuffer.write(rtpPacket);
                    }

                    // close socket
                    RecvUdpSocket.disconnect();
                    RecvUdpSocket.close();
                } catch (SocketException e) {
                    UdpVoipReceiveDataThreadRun = false;
                    Log.e(TAG, "SocketException: " + e.toString());
                } catch (IOException e) {
                    UdpVoipReceiveDataThreadRun = false;
                    Log.e(TAG, "IOException: " + e.toString());
                }
            }
        });
        UdpReceiveDataThread.start();
    }

}


