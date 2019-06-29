package com.lg.sixsenses.willi.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UpdatedData;
import com.lg.sixsenses.willi.logic.callmanager.CallHandler;
import com.lg.sixsenses.willi.logic.servercommmanager.TcpRecvCallManager;
import com.lg.sixsenses.willi.logic.servercommmanager.TcpSendCallManager;
import com.lg.sixsenses.willi.R;

import java.util.Observable;
import java.util.Observer;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class CallStateActivity extends AppCompatActivity implements Observer {

    public static final String TAG = CallStateActivity.class.getName().toString();
    private TextView textViewCallstate;
    private Button buttonAccept;
    private Button buttonReject;

    private CallStateActivityHandler handler;

    private String phoneNum=null;
    private TcpSendCallManager sender = null;
    private TcpRecvCallManager receiver = null;
    private ImageView imageViewState;
    private PowerManager.WakeLock proximityWakeLock;
    private AudioManager audioManager;
    private MediaPlayer ring;
    private Vibrator vibrator;
    private final long[] vibratorPattern = {0, 300, 1000};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callstate);

        DataManager.getInstance().addObserver(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        textViewCallstate = (TextView)findViewById(R.id.textViewCallState);
        buttonAccept = (Button)findViewById(R.id.buttonAccept);
        buttonReject = (Button)findViewById(R.id.buttonReject);
        imageViewState = (ImageView)findViewById(R.id.imageViewState);

        // for test
        imageViewState.setImageResource(R.drawable.calling);
        buttonReject.setEnabled(true);
        buttonAccept.setEnabled(false);
        textViewCallstate.setText("Calling with frank");

        changeUI();

        handler = new CallStateActivityHandler();
        CallHandler.getInstance().setHandler(handler);
        CallHandler.getInstance().setImageView(imageViewState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataManager.getInstance().deleteObserver(this);
    }

    public void buttonAcceptClick(View view)
    {
        CallHandler.getInstance().callAccept();
    }
    public void buttonRejectClick(View view)
    {
        if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.CALLING)
            CallHandler.getInstance().callRejectForConnectedCall(phoneNum);
        else if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.CONNECTED)
            CallHandler.getInstance().callRejectForConnectedCall(phoneNum);
        else if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.RINGING)
            CallHandler.getInstance().callRejectForIncomingCall();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void update(Observable o, Object arg) {
        UpdatedData data = (UpdatedData)arg;
        Log.d(TAG,"CallStateActivity - updated data : "+ data.toString());
        if(data.getType().equals("CallState"))
        {
            DataManager.CallStatus status = (DataManager.CallStatus)(data.getData());
            Log.d(TAG, "CallState : "+ status.toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeUI();
                    if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.IDLE)
                    {
                        // IDLE시에 DialActivity를 띄운다. 부팅하자마자 전화 받았을때를 위한 조치
                        Intent intent = new Intent(getApplicationContext(),DialActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        disableProximityWakeLock();
                        EndRinger();
                        finish();
                    }

                }
            });
        }
    }

    public void changeUI()
    {
        if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.CALLING)
        {
            imageViewState.setImageResource(R.drawable.calling);
            textViewCallstate.setText("Calling to "+DataManager.getInstance().getCalleePhoneNum());
            buttonReject.setEnabled(true);
            buttonAccept.setEnabled(false);
        }
        else if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.CONNECTED)
        {
            if (enableProximityWakeLock())
                Log.e(TAG, "enableProximityWakeLock Failed already enabled!");

            imageViewState.setImageResource(R.drawable.connected);
            String phone=null;
            if(DataManager.getInstance().getMyInfo().getPhoneNum() == DataManager.getInstance().getCalleePhoneNum())
                phone = DataManager.getInstance().getCallerPhoneNum();
            else if(DataManager.getInstance().getMyInfo().getPhoneNum() == DataManager.getInstance().getCallerPhoneNum())
                phone = DataManager.getInstance().getCalleePhoneNum();

            textViewCallstate.setText("Connected with "+phone);

            buttonReject.setEnabled(true);
            buttonAccept.setEnabled(false);

            EndRinger();

//            // For Test
//            ArrayList<UdpInfo> list = DataManager.getInstance().getPeerUdpInfoList();
//            int i = 0;
//            String re;
//            for(i=0;i<list.size();i++)
//            {
//                re += list.get(i).toString();
//            }
//            Log.d(TAG,"###################### Peer Info ######### : "+re);
        }
        else if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.RINGING)
        {
            DoPmAndBringActivityToForeground();
            imageViewState.setImageResource(R.drawable.ringing);
            textViewCallstate.setText("Call from "+DataManager.getInstance().getCallerPhoneNum());

            buttonReject.setEnabled(true);
            buttonAccept.setEnabled(true);
            StartRinger();

        }
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
        } else if (pm == null) Log.e(TAG, "Failed to aquire PowerManager pm");
        //Unlock if locked
        unlockScreen();
        //Bring Screen to forground
        Intent intent = new Intent(this, getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP);
        getApplicationContext().startActivity(intent);
        Log.e(TAG, "Bring Screen to forground");
    }

    @SuppressWarnings("deprecation")
    private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
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

    int PreviousAudioManagerMode = 0;
    private void StartRinger() {
        if (DataManager.getInstance().getSound() == DataManager.Sound.BELL) {
            if (ring == null) {
                PreviousAudioManagerMode = audioManager.getMode();
                audioManager.setMode(AudioManager.MODE_RINGTONE);
                ring = MediaPlayer.create(getApplicationContext(), R.raw.ring);
                ring.setLooping(true);
                ring.start();
            }
        }
        if (DataManager.getInstance().getSound() == DataManager.Sound.VIBRATE)
            vibrator.vibrate(vibratorPattern, 0);
    }

    private void EndRinger() {
        if (ring != null) {
            ring.stop();
            ring.release();
            ring = null;
            audioManager.setMode(PreviousAudioManagerMode);
        }
        if (DataManager.getInstance().getSound() == DataManager.Sound.VIBRATE)
            vibrator.cancel();
    }

    public class CallStateActivityHandler extends Handler {
        // for Message.what
        public static final int CMD_VIEW_UPDATE = 1;

        // for Message kdy
        public static final String KEY_IMAGEVIEW = "image_view";
        public static final String KEY_IMAGE = "image";


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case CMD_VIEW_UPDATE: {
                    Bundle bundle = msg.getData();
                    ImageView view = (ImageView) bundle.get(KEY_IMAGEVIEW);
                    final Bitmap image = (Bitmap) bundle.get(KEY_IMAGE);
                    view.setImageBitmap(image);
                }
                break;

                default: {
                    Log.d(TAG, "CallStateActivityHandler received suspicious msg!");
                }
                break;
            }



        }
    }


}
