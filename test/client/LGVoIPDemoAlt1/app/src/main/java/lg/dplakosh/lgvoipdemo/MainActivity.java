package lg.dplakosh.lgvoipdemo;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.annotation.NonNull;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.provider.Settings;
import android.net.Uri;

import com.instacart.library.truetime.TrueTime;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer, PopupMenu.OnMenuItemClickListener {
    enum EOutputTarget {EARPIECE, SPEAKER, BLUETOOTH}

    static final String LOG_TAG = "VoipDemo";

    static public EOutputTarget AudioOutputTarget;
    public  static boolean BoostAudio=false;
    static public int SimVoice = 0;
    private static final int VOIP_VIDEO_UDP_PORT = 5125;
    private static final int VIDEO_BUFFER_SIZE = 65507;
    private ImageView ImageViewVideo;
    private Thread UdpReceiveVideoThread = null;
    private boolean UdpVoipReceiveVideoThreadRun = false;
    private DatagramSocket RecvVideoUdpSocket;
    private WakeLock proximityWakeLock;
    private Button CallButton;
    private Button AnswerButton;
    private Button RefuseButton;
    private Button EndButton;
    private Button AudioOutputButton;
    private Button SimVoiceButton;
    private ToggleButton ToggleBoostButton;
    private ToggleButton ToggleMicrophoneButton;
    private ToggleButton ToggleRingerButton;
    private EditText RemoteIpText;
    private TextView TextViewLocalIP;
    private TextView TextViewPhoneState;
    private static final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    @SuppressWarnings("deprecation")
    private void DoPmAndBringActivityToForeground() {
        //Turn on screen if off
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if ((pm != null) && (!pm.isInteractive())) {

            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "LG:MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "LG:MyCpuLock");

            wl_cpu.acquire(10000);
        } else if (pm == null) Log.e(LOG_TAG, "Failed to aquire PowerManager pm");
        //Unlock if locked
        unlockScreen();
        //Bring Screen to forground
        Intent intent = new Intent(this, getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getApplicationContext().startActivity(intent);
    }

    @SuppressWarnings("deprecation")
    private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0 && permissions.length == grantResults.length)
                    permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.e(LOG_TAG, "Request for Permission To Record Audio Granted");
                break;
        }
        if (!permissionToRecordAccepted) {
            Log.e(LOG_TAG, "Request for Permission To Record Audio Not Granted");
            finish();
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UpdateGUI();
            }
        });
    }

    private void UpdateGUI() {
        TextViewLocalIP.setText(getString(R.string.LocalIp, PhoneState.getInstance().GetLocalIP()));
        RemoteIpText.setText(PhoneState.getInstance().GetCmdIP());
        int position = RemoteIpText.length();
        RemoteIpText.setSelection(position);
        if (AudioOutputTarget == EOutputTarget.EARPIECE)
            AudioOutputButton.setText(getString(R.string.Earpiece));
        else if (AudioOutputTarget == EOutputTarget.SPEAKER)
            AudioOutputButton.setText(getString(R.string.Speaker));
        else if (AudioOutputTarget == EOutputTarget.BLUETOOTH)
            AudioOutputButton.setText(getString(R.string.BlueTooth));
        ToggleMicrophoneButton.setChecked(PhoneState.getInstance().GetMic());
        ToggleBoostButton.setChecked(PhoneState.getInstance().GetBoost());
        BoostAudio=ToggleBoostButton.isChecked();
        ToggleRingerButton.setChecked(PhoneState.getInstance().GetRinger());
        switch (SimVoice) {
            case 0:
                SimVoiceButton.setText(getString(R.string.None));
                break;
            case 1:
                SimVoiceButton.setText(getString(R.string.Voice1));
                break;
            case 2:
                SimVoiceButton.setText(getString(R.string.Voice2));
                break;
            case 3:
                SimVoiceButton.setText(getString(R.string.Voice3));
                break;
            case 4:
                SimVoiceButton.setText(getString(R.string.Voice4));
                break;
            default:
                break;
        }

        if (PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.LISTENING) {
            AnswerButton.setVisibility(View.INVISIBLE);
            RefuseButton.setVisibility(View.INVISIBLE);
            EndButton.setVisibility(View.INVISIBLE);
            RemoteIpText.setEnabled(true);
            CallButton.setEnabled(true);
            SimVoiceButton.setEnabled(true);
            disableProximityWakeLock();
            TextViewPhoneState.setText(getString(R.string.PhoneState, "Listening", ""));
            disableProximityWakeLock();
        } else if (PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.INCOMMING) {
            DoPmAndBringActivityToForeground();
            CallButton.setEnabled(false);
            SimVoiceButton.setEnabled(false);
            AnswerButton.setVisibility(View.VISIBLE);
            RefuseButton.setVisibility(View.VISIBLE);
            RemoteIpText.setEnabled(false);
            TextViewPhoneState.setText(getString(R.string.PhoneState, "Incoming Call: ", PhoneState.getInstance().GetRemoteIP()));
        } else if (PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.INCALL) {
            TextViewPhoneState.setText(getString(R.string.PhoneState, "In Call: ", PhoneState.getInstance().GetInComingIP()));
            AnswerButton.setVisibility(View.INVISIBLE);
            RefuseButton.setVisibility(View.INVISIBLE);
            EndButton.setVisibility(View.VISIBLE);
            if (enableProximityWakeLock())
                Log.e(LOG_TAG, "enableProximityWakeLock Failed already enabled!");

        } else if (PhoneState.getInstance().GetPhoneState() == PhoneState.CallState.CALLING) {
            TextViewPhoneState.setText(getString(R.string.PhoneState, "Calling: ", PhoneState.getInstance().GetRemoteIP()));
            EndButton.setVisibility(View.VISIBLE);
            RemoteIpText.setEnabled(false);
            CallButton.setEnabled(false);
            SimVoiceButton.setEnabled(false);
        }
        if (PhoneState.getInstance().GetRecvVideoState() == PhoneState.VideoState.START_VIDEO) {
            StartReceiveVideoThread();
        }
        else if (PhoneState.getInstance().GetRecvVideoState() == PhoneState.VideoState.STOP_VIDEO) {
            StopReceiveVideoThread();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(LOG_TAG, "Application Create!");

        CallButton = findViewById(R.id.buttonCall);
        AnswerButton = findViewById(R.id.buttonAnswer);
        RefuseButton = findViewById(R.id.buttonRefuse);
        EndButton = findViewById(R.id.buttonEnd);
        AudioOutputButton = findViewById(R.id.buttonAudioOutput);
        SimVoiceButton = findViewById(R.id.buttonSimVoice);
        ToggleMicrophoneButton = findViewById(R.id.buttonToggleMicrophone);
        ToggleBoostButton = findViewById(R.id.buttonToggleBoost);
        ToggleRingerButton = findViewById(R.id.buttonToggleRinger);
        RemoteIpText = findViewById(R.id.editTextRemoteIp);
        TextViewPhoneState = findViewById(R.id.textViewPhoneState);
        TextViewLocalIP = findViewById(R.id.textViewLocalIp);
        ImageViewVideo= findViewById(R.id.imageViewVideo);
        PhoneState.getInstance().addObserver(this);

        WhiteListBatteryOptimtizations(false);

        Random random = new Random();
        Log.d("rand_test", "rand1: " +random.nextInt());
        Log.d("rand_test", "rand2: " +random.nextInt());
        Log.d("rand_test", "rand3: " +random.nextInt());


        // Service will start if not already running
        startService(new Intent(this, UDPListenerService.class));

        //If authorisation not granted for camera
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            //ask for authorisation
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);


        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            permissionToRecordAccepted = true;
            Log.e(LOG_TAG, "Permission To Record Audio Granted");
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }

        Log.i(LOG_TAG, "VoipDemo started");

// Call Button
        CallButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.i(LOG_TAG, "Call Button");
                Matcher matcher = IP_ADDRESS.matcher(RemoteIpText.getText().toString());
                if (matcher.matches()) {
                    broadcastIntent(RemoteIpText.getText().toString(), "/CALL_BUTTON/");
                } else {
                    final AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                    alert.setTitle("Invailid IP");
                    alert.setMessage("The IP Addresss you entered is invalid!");
                    alert.setButton(-1, "Dismiss", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alert.dismiss();
                        }
                    });
                    alert.show();
                }

            }
        });
        //End Call Button
        EndButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                broadcastIntent("", "/END_CALL_BUTTON/");
            }
        });
        //Answer Call Button
        AnswerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                broadcastIntent("", "/ANSWER_CALL_BUTTON/");
            }
        });

        // Refuse Call Button
        RefuseButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Send refuse notification and end the call
                broadcastIntent("", "/REFUSE_CALL_BUTTON/");

            }
        });
        // Audio Output Button
        AudioOutputButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.inflate(R.menu.popup_menu_audio_output);
                popup.show();
            }
        });
        // SimVoice Button
        SimVoiceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.inflate(R.menu.popup_menu_sim_voice);
                popup.show();
            }
        });
        // Toggle Microphone Button
        ToggleMicrophoneButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ToggleMicrophoneButton.isChecked()) {
                    broadcastIntent("true", "/TOGGLE_MIC_BUTTON/");
                } else {
                    broadcastIntent("false", "/TOGGLE_MIC_BUTTON/");
                }
            }
        });
        // Toggle Microphone Button
        ToggleBoostButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ToggleBoostButton.isChecked()) {
                    broadcastIntent("true", "/TOGGLE_BOOST_BUTTON/");
                } else {
                    broadcastIntent("false", "/TOGGLE_BOOST_BUTTON/");
                }
            }
        });

        // Toggle Ringer Button
        ToggleRingerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ToggleRingerButton.isChecked()) {
                    broadcastIntent("true", "/TOGGLE_RINGER_BUTTON/");
                } else {
                    broadcastIntent("false", "/TOGGLE_RINGER_BUTTON/");
                }
            }
        });

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.earpiece_item:
                AudioOutputTarget = EOutputTarget.EARPIECE;
                broadcastIntent("", "/Audio_Output_Menu_Button/");
                return true;
            case R.id.speaker_item:
                AudioOutputTarget = EOutputTarget.SPEAKER;
                broadcastIntent("", "/Audio_Output_Menu_Button/");
                return true;
            case R.id.bluetooth_item:
                AudioOutputTarget = EOutputTarget.BLUETOOTH;
                broadcastIntent("", "/Audio_Output_Menu_Button/");
                return true;
            case R.id.voice0_item:
                SimVoice = 0;
                broadcastIntent("", "/Sim_Voice_Menu_Button/");
                return true;
            case R.id.voice1_item:
                SimVoice = 1;
                broadcastIntent("", "/Sim_Voice_Menu_Button/");
                return true;
            case R.id.voice2_item:
                SimVoice = 2;
                broadcastIntent("", "/Sim_Voice_Menu_Button/");
                return true;
            case R.id.voice3_item:
                SimVoice = 3;
                broadcastIntent("", "/Sim_Voice_Menu_Button/");
                return true;
            case R.id.voice4_item:
                SimVoice = 4;
                broadcastIntent("", "/Sim_Voice_Menu_Button/");
                return true;

            default:
                return false;
        }
    }

    private void broadcastIntent(String senderIP, String message) {
        Intent intent = new Intent(UDPListenerService.GUI_VOIP_CTRL);
        intent.putExtra("sender", senderIP);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(LOG_TAG, "Application Paused!");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(LOG_TAG, "Application Stopped!");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.e(LOG_TAG, "Application Restarted!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(LOG_TAG, "Application Resume!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcastIntent("", "/END_CALL_BUTTON/");
        StopReceiveVideoThread();
        disableProximityWakeLock();
        Log.e(LOG_TAG, "Application Destroy!");
    }

    @SuppressLint("WakelockTimeout")
    private boolean enableProximityWakeLock() {
        if (proximityWakeLock != null) {
            return true;
        }
        PowerManager powerManager = (PowerManager)
                getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            proximityWakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "LG:ProxLock");
            proximityWakeLock.acquire();
        }
        return false;
    }

    private void disableProximityWakeLock() {
        if (proximityWakeLock != null) {
            proximityWakeLock.release();
            proximityWakeLock = null;
        }
    }

    @SuppressLint("BatteryLife")
    @SuppressWarnings("SameParameterValue")
    private void WhiteListBatteryOptimtizations(boolean EnableReview) {
        String packageName = getApplicationContext().getPackageName();
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (pm != null && pm.isIgnoringBatteryOptimizations(packageName) && EnableReview) {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT < 24)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            getApplicationContext().startActivity(intent);
        } else {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT < 24)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            getApplicationContext().startActivity(intent);
        }

    }

    private void StartReceiveVideoThread() {
        // Create thread for receiving audio data
        PhoneState.getInstance().SetRecvVideoState(PhoneState.VideoState.RECEIVING_VIDEO);
        if ( UdpVoipReceiveVideoThreadRun) return;
        UdpVoipReceiveVideoThreadRun = true;
        UdpReceiveVideoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Create an instance of AudioTrack, used for playing back audio
                Log.i(LOG_TAG, "Receive Data Thread Started. Thread id: " + Thread.currentThread().getId());
                try {
                    // Setup socket to receive the audio data
                    RecvVideoUdpSocket = new DatagramSocket(null);
                    RecvVideoUdpSocket.setReuseAddress(true);
                    RecvVideoUdpSocket.bind(new InetSocketAddress(VOIP_VIDEO_UDP_PORT));

                    while (UdpVoipReceiveVideoThreadRun) {
                        byte[] jpegbuf = new byte[VIDEO_BUFFER_SIZE];
                        DatagramPacket packet = new DatagramPacket(jpegbuf, VIDEO_BUFFER_SIZE);
                        RecvVideoUdpSocket.receive(packet);
                        if (packet.getLength() >0) {
                            final Bitmap bitmap = BitmapFactory.decodeByteArray(packet.getData(), 0, packet.getLength());
                            final Matrix mtx = new Matrix();
                            mtx.postRotate(-90);
                            final Bitmap rotator = Bitmap.createBitmap(bitmap, 0, 0,
                                    bitmap.getWidth(), bitmap.getHeight(), mtx,
                                    true);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageViewVideo.setImageBitmap(rotator);
                                }
                            });

                            //Log.i(LOG_TAG, "Video Packet received: " + packet.getLength());
                        } else
                            Log.i(LOG_TAG, "Invalid Packet LengthReceived: " + packet.getLength());

                    }
                    // close socket
                    RecvVideoUdpSocket.disconnect();
                    RecvVideoUdpSocket.close();
                } catch (SocketException e) {
                    UdpVoipReceiveVideoThreadRun = false;
                    Log.e(LOG_TAG, "SocketException: " + e.toString());
                } catch (IOException e) {
                    UdpVoipReceiveVideoThreadRun = false;
                    Log.e(LOG_TAG, "IOException: " + e.toString());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Clear Video Frame
                        ImageViewVideo.setImageBitmap(null);
                        Log.i(LOG_TAG, "Clear video Frame");
                    }
                });
            }

        });
        UdpReceiveVideoThread.start();

    }

    private void StopReceiveVideoThread() {
        PhoneState.getInstance().SetRecvVideoState(PhoneState.VideoState.VIDEO_STOPPED);
        if (!UdpVoipReceiveVideoThreadRun) return;
        if (UdpReceiveVideoThread != null && UdpReceiveVideoThread.isAlive()) {
            UdpVoipReceiveVideoThreadRun = false;
            RecvVideoUdpSocket.close();
            Log.i(LOG_TAG, "UdpReceiveDataThread Thread Join started");
            UdpVoipReceiveVideoThreadRun = false;
            try {
                UdpReceiveVideoThread.join();
            } catch (InterruptedException e) {
                Log.i(LOG_TAG, "UdpReceiveDataThread Join interruped");
            }
            Log.i(LOG_TAG, " UdpReceiveDataThread Join successs");
        }

        UdpReceiveVideoThread = null;
        RecvVideoUdpSocket = null;
    }
}


