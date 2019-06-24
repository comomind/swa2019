package lg.dplakosh.lgvoipdemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;

import lg.dplakosh.lgvoipdemo.codec.audio.AudioCodecConst;
import lg.dplakosh.lgvoipdemo.codec.audio.AudioCodec;
import lg.dplakosh.lgvoipdemo.codec.audio.AudioCodecFactory;

public class UDPListenerService extends Service {
    public static final int CONTROL_DATA_PORT = 5123;
    public final static String GUI_VOIP_CTRL = "GuiVoIpControl";
    private static final String LOG_TAG = "UDPListenerService";
    private static final int BUFFER_SIZE = 128;
    private boolean UdpListenerThreadRun = false;
    private VoIPAudioIo Audio;
    private VoIPVideoIo Video;
    private AudioManager audioManager;
    private DatagramSocket socket;
    public ReceiveMessages _receiver = null;
    private MediaPlayer ring;
    private Vibrator vibrator;
    private final long[] vibratorPattern = {0, 200, 800};


    public class ReceiveMessages extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(UDPListenerService.GUI_VOIP_CTRL)) {
                Log.i(LOG_TAG, "onReceive");
                String message = intent.getStringExtra("message");
                String sender = intent.getStringExtra("sender");
                ProcessReceivedGUIMessage(sender, message);
            }
        }
    }

    private void ProcessReceivedGUIMessage(final String Sender, String MessageIn) {

        switch (MessageIn) {

            case "/CALL_BUTTON/":
                PhoneState.getInstance().SetRemoteIP(Sender);
                PhoneState.getInstance().SetCmdIP(Sender);
                PhoneState.getInstance().SetPhoneState(PhoneState.CallState.CALLING);
                UdpSend(Sender, UDPListenerService.CONTROL_DATA_PORT, "/CALLIP/");
                PhoneState.getInstance().NotifyUpdate();
                break;
            case "/END_CALL_BUTTON/":
                EndCall();
                PhoneState.getInstance().NotifyUpdate();
                break;

            case "/ANSWER_CALL_BUTTON/":
                try {
                    EndRinger();
                    UdpSend(PhoneState.getInstance().GetRemoteIP(), UDPListenerService.CONTROL_DATA_PORT, "/ANSWER/");
                    InetAddress address = InetAddress.getByName(PhoneState.getInstance().GetRemoteIP());
                    PhoneState.getInstance().SetInComingIP(PhoneState.getInstance().GetRemoteIP());
                    PhoneState.getInstance().SetPhoneState(PhoneState.CallState.INCALL);

                    // TODO: need to get audio codec type before start audio
                    AudioCodec audioCodec = AudioCodecFactory.getCodec(AudioCodecConst.CodecType.OPUS);
                    Audio.setAudioCodec(audioCodec);
                    if (Audio.StartAudio(address,MainActivity.SimVoice))
                        Log.e(LOG_TAG, "Audio Already started (Answer Button)");
                    if (Video.StartVideo(address))
                        Log.e(LOG_TAG, "Video Already started (Answer Button)");
                    PhoneState.getInstance().SetRecvVideoState(PhoneState.VideoState.START_VIDEO);
                    Log.i(LOG_TAG, "Answered " + address.toString());
                } catch (UnknownHostException e) {

                    Log.e(LOG_TAG, "UnknownHostException Answer Button: " + e);
                } catch (Exception e) {

                    Log.e(LOG_TAG, "Exception Answer Button: " + e);
                }
                PhoneState.getInstance().NotifyUpdate();
                break;
            case "/REFUSE_CALL_BUTTON/":
                UdpSend(PhoneState.getInstance().GetRemoteIP(), UDPListenerService.CONTROL_DATA_PORT, "/REFUSE/");
                EndCall();
                PhoneState.getInstance().NotifyUpdate();
                break;
            case "/Audio_Output_Menu_Button/":
                if (MainActivity.AudioOutputTarget == MainActivity.EOutputTarget.SPEAKER) {
                    audioManager.setBluetoothScoOn(false);
                    audioManager.stopBluetoothSco();
                    audioManager.setSpeakerphoneOn(true);
                } else if (MainActivity.AudioOutputTarget == MainActivity.EOutputTarget.EARPIECE) {
                    audioManager.setBluetoothScoOn(false);
                    audioManager.stopBluetoothSco();
                    audioManager.setSpeakerphoneOn(false);
                } else if (MainActivity.AudioOutputTarget == MainActivity.EOutputTarget.BLUETOOTH) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setBluetoothScoOn(true);
                    audioManager.startBluetoothSco();
                }
                PhoneState.getInstance().NotifyUpdate();
                break;
            case "/Sim_Voice_Menu_Button/":
                PhoneState.getInstance().NotifyUpdate();
                break;
            case "/TOGGLE_MIC_BUTTON/":
                if (Sender.equals("true")) {
                    PhoneState.getInstance().SetMic(true);
                    audioManager.setMicrophoneMute(false);
                } else if (Sender.equals("false")) {
                    PhoneState.getInstance().SetMic(false);
                    audioManager.setMicrophoneMute(true);
                }
                PhoneState.getInstance().NotifyUpdate();
                break;
            case "/TOGGLE_BOOST_BUTTON/":
                if (Sender.equals("true")) {
                    PhoneState.getInstance().SetBoost(true);
                } else if (Sender.equals("false")) {
                    PhoneState.getInstance().SetBoost(false);
                }
                PhoneState.getInstance().NotifyUpdate();
                break;
            case "/TOGGLE_RINGER_BUTTON/":
                if (Sender.equals("true")) {
                    PhoneState.getInstance().SetRinger(true);
                } else if (Sender.equals("false")) {
                    PhoneState.getInstance().SetRinger(false);
                }
                PhoneState.getInstance().NotifyUpdate();
                break;
            default:
                // Invalid notification received
                Log.w(LOG_TAG, Sender + " sent invalid message: " + MessageIn);
                break;
        }
    }


    private void startListenerForUDP() {
        UdpListenerThreadRun = true;
        Thread UDPListenThread = new Thread(new Runnable() {
            public void run() {
                try {
                    // Setup the socket to receive incoming messages
                    byte[] buffer = new byte[BUFFER_SIZE];
                    socket = new DatagramSocket(null);
                    socket.setReuseAddress(true);
                    socket.bind(new InetSocketAddress(CONTROL_DATA_PORT));
                    DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
                    Log.i(LOG_TAG, "Incoming call listener started");
                    while (UdpListenerThreadRun) {
                        // Listen for incoming call requests
                        Log.i(LOG_TAG, "Listening for incoming calls");
                        socket.receive(packet);
                        String senderIP = packet.getAddress().getHostAddress();
                        String message = new String(buffer, 0, packet.getLength());
                        Log.i(LOG_TAG, "Got UDP message from " + senderIP + ", message: " + message);
                        ProcessReceivedUdpMessage(senderIP, message);
                        if (message.equals("/CALLIP/")) {
                            Log.e(LOG_TAG, "Main activity may not be running so kick it");
                            Intent i = new Intent();
                            i.setClassName("lg.dplakosh.lgvoipdemo", "lg.dplakosh.lgvoipdemo.MainActivity");
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    }
                    Log.e(LOG_TAG, "Call Listener ending");
                    socket.disconnect();
                    socket.close();

                } catch (Exception e) {
                    UdpListenerThreadRun = false;
                    Log.e(LOG_TAG, "no longer listening for UDP messages due to error " + e.getMessage());
                }
            }
        });
        UDPListenThread.start();
    }

    private void ProcessReceivedUdpMessage(final String Sender, String MessageIn) {

        switch (MessageIn) {

            case "/CALLIP/":
                // Receives Call Requests
                if (PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.LISTENING ||
                        (PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.CALLING &&
                                PhoneState.getInstance().GetRemoteIP().equals(PhoneState.getInstance().GetLocalIP()))) {
                    PhoneState.getInstance().SetRemoteIP(Sender);
                    PhoneState.getInstance().SetPhoneState(PhoneState.CallState.INCOMMING);
                    StartRinger();
                    PhoneState.getInstance().NotifyUpdate();
                }
                break;
            case "/ANSWER/":
                // Accept notification received. Start call
                if (PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.CALLING) {
                    try {
                        InetAddress address = InetAddress.getByName(Sender);

                        // TODO: need to get audio codec type before start audio
                        AudioCodec audioCodec = AudioCodecFactory.getCodec(AudioCodecConst.CodecType.OPUS);
                        Audio.setAudioCodec(audioCodec);
                        if (Audio.StartAudio(address,MainActivity.SimVoice))
                            Log.e(LOG_TAG, "Audio Already started (Answer)");
                        if (Video.StartVideo(address))
                            Log.e(LOG_TAG, "Video Already started (Answer)");
                        PhoneState.getInstance().SetRecvVideoState(PhoneState.VideoState.START_VIDEO);
                        PhoneState.getInstance().SetPhoneState(PhoneState.CallState.INCALL);
                        PhoneState.getInstance().SetInComingIP(Sender);
                        PhoneState.getInstance().NotifyUpdate();
                    } catch (Exception e) {

                        Log.e(LOG_TAG, "Exception Answer Messagwe: " + e);
                    }
                }
                break;

            case "/REFUSE/":
            case "/ENDCALL/":
                if ((PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.CALLING) ||
                        (PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.INCALL) ||
                        (PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.INCOMMING)) {
                    EndCall();
                    PhoneState.getInstance().NotifyUpdate();
                }
                break;

            default:
                // Invalid notification received
                Log.w(LOG_TAG, Sender + " sent invalid message: " + MessageIn);
                break;
        }
    }

    private synchronized void EndCall() {
        if (PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.LISTENING) return;
        if (PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.INCALL) {
            if (Audio.EndAudio())
                Log.e(LOG_TAG, "Audio Already Ended (End Call)");
            if (Video.EndVideo())
                Log.e(LOG_TAG, "Video Already Ended (End Call)");
            PhoneState.getInstance().SetRecvVideoState(PhoneState.VideoState.STOP_VIDEO);
        }
        PhoneState.getInstance().SetPhoneState(PhoneState.CallState.LISTENING);
        UdpSend(PhoneState.getInstance().GetRemoteIP(), UDPListenerService.CONTROL_DATA_PORT, "/ENDCALL/");
        EndRinger();
    }

    private void stopListen() {
        UdpListenerThreadRun = false;
        socket.close();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _receiver = new ReceiveMessages();
        registerReceiver(_receiver, new IntentFilter(UDPListenerService.GUI_VOIP_CTRL));
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Audio = new VoIPAudioIo(getApplicationContext());
        Video = new VoIPVideoIo();
        // Get instance of Vibrator from current Context
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        int LocalIpAddressBin = 0;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            LocalIpAddressBin = wifiInfo.getIpAddress();
            PhoneState.getInstance().SetLocallP(String.format(Locale.US, "%d.%d.%d.%d", (LocalIpAddressBin & 0xff), (LocalIpAddressBin >> 8 & 0xff), (LocalIpAddressBin >> 16 & 0xff), (LocalIpAddressBin >> 24 & 0xff)));
        }
        PhoneState.getInstance().SetCmdIP(String.format(Locale.US, "%d.%d.%d.", (LocalIpAddressBin & 0xff), (LocalIpAddressBin >> 8 & 0xff), (LocalIpAddressBin >> 16 & 0xff)));

        if (audioManager.isSpeakerphoneOn()) {
            MainActivity.AudioOutputTarget =MainActivity.EOutputTarget.SPEAKER;
        }
        else if (audioManager.isBluetoothScoOn()) {
            MainActivity.AudioOutputTarget =MainActivity.EOutputTarget.BLUETOOTH;
        }
        else {
            MainActivity.AudioOutputTarget =MainActivity.EOutputTarget.EARPIECE;
        }

        if (audioManager.isMicrophoneMute()) {
            PhoneState.getInstance().SetMic(false);
        } else {
            PhoneState.getInstance().SetMic(true);
        }
        PhoneState.getInstance().SetRinger(false);

        PhoneState.getInstance().SetBoost(false);

        PhoneState.getInstance().SetPhoneState(PhoneState.CallState.LISTENING);
        startListenerForUDP();
        PhoneState.getInstance().NotifyUpdate();
        Log.i(LOG_TAG, "Service started");
    }

    @Override
    public void onDestroy() {
        stopListen();
        if (_receiver != null) unregisterReceiver(_receiver);
        _receiver = null;
        EndRinger();
        if (Audio != null)
            Audio.EndAudio();
        if (Video != null)
            Video.EndVideo();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PhoneState.getInstance().NotifyUpdate();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    int PreviousAudioManagerMode = 0;
    @SuppressWarnings("deprecation")
    private void StartRinger() {
        if (PhoneState.getInstance().GetRinger()) {
            if (ring == null) {
                PreviousAudioManagerMode = audioManager.getMode();
                audioManager.setMode(AudioManager.MODE_RINGTONE);
                ring = MediaPlayer.create(getApplicationContext(), R.raw.ring);
                ring.setLooping(true);
                ring.start();
            }
        }
        vibrator.vibrate(vibratorPattern, 0);
    }

    private void EndRinger() {
        if (ring != null) {
            ring.stop();
            ring.release();
            ring = null;
            audioManager.setMode(PreviousAudioManagerMode);
        }
        vibrator.cancel();
    }
    @SuppressWarnings("SameParameterValue")
    private void UdpSend(final String RemoteIp, final int port, final String Message) {
        Thread replyThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    InetAddress address = InetAddress.getByName(RemoteIp);
                    byte[] buffer = Message.getBytes();
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
                    socket.send(packet);
                    Log.i(LOG_TAG, "UdpSend( " + Message + " ) to " + RemoteIp);
                    socket.disconnect();
                    socket.close();
                } catch (SocketException e) {

                    Log.e(LOG_TAG, "Failure. SocketException in UdpSend: " + e);
                } catch (IOException e) {

                    Log.e(LOG_TAG, "Failure. IOException in UdpSend: " + e);
                }
            }
        });
        replyThread.start();
    }
}