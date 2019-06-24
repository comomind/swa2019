package com.lg.sixsenses.willi.UserInterface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lg.sixsenses.willi.DataRepository.DataManager;
import com.lg.sixsenses.willi.DataRepository.UpdatedData;
import com.lg.sixsenses.willi.Logic.CallManager.CallHandler;
import com.lg.sixsenses.willi.Logic.ServerCommManager.TcpRecvCallManager;
import com.lg.sixsenses.willi.Logic.ServerCommManager.TcpSendCallManager;
import com.lg.sixsenses.willi.R;
import java.util.Observable;
import java.util.Observer;

public class CallStateActivity extends AppCompatActivity implements Observer {

    public static final String TAG = CallStateActivity.class.getName().toString();
    private GifImageView gifImageView;
    private TextView textViewCallstate;
    private Button buttonAccept;
    private Button buttonReject;

    private String phoneNum=null;
    private TcpSendCallManager sender = null;
    private TcpRecvCallManager receiver = null;
    private ImageView imageViewState;
    private PowerManager.WakeLock proximityWakeLock;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callstate);

        DataManager.getInstance().addObserver(this);

        textViewCallstate = (TextView)findViewById(R.id.textViewCallState);
        buttonAccept = (Button)findViewById(R.id.buttonAccept);
        buttonReject = (Button)findViewById(R.id.buttonReject);
//        gifImageView = (GifImageView) findViewById(R.id.GifImageView);
//        gifImageView.setGifImageResource(R.drawable.calling);
        imageViewState = (ImageView)findViewById(R.id.imageViewState);

        // for test
        imageViewState.setImageResource(R.drawable.calling);
        buttonReject.setEnabled(true);
        buttonAccept.setEnabled(false);
        textViewCallstate.setText("Calling with frank");

        ChangeUI();

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
            CallHandler.getInstance().callRejectforConnectedCall(phoneNum);
        else if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.CONNECTED)
            CallHandler.getInstance().callRejectforConnectedCall(phoneNum);
        else if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.RINGING)
            CallHandler.getInstance().callRejectforIncomingCall();
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
                    ChangeUI();
                    if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.IDLE)
                    {
                        disableProximityWakeLock();
                        finish();
                    }

                }
            });



        }

    }

    public void ChangeUI()
    {
        if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.CALLING)
        {
//            gifImageView = (GifImageView) findViewById(R.id.GifImageView);
//            gifImageView.setGifImageResource(R.drawable.calling);
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
        }
        else if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.RINGING)
        {
            DoPmAndBringActivityToForeground();
            imageViewState.setImageResource(R.drawable.ringing);
            textViewCallstate.setText("Call from "+DataManager.getInstance().getCallerPhoneNum());

            buttonReject.setEnabled(true);
            buttonAccept.setEnabled(true);
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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



}
