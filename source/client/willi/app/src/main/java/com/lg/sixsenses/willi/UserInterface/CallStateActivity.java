package com.lg.sixsenses.willi.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
            if(DataManager.getInstance().getMyInfo().getPhoneNum() == DataManager.getInstance().getCalleePhoneNum())
                phoneNum = DataManager.getInstance().getCallerPhoneNum();
            else if(DataManager.getInstance().getMyInfo().getPhoneNum() == DataManager.getInstance().getCallerPhoneNum())
                phoneNum = DataManager.getInstance().getCalleePhoneNum();

            class MyRunnable implements Runnable {
                DataManager.CallStatus st;
                String num;
                MyRunnable(DataManager.CallStatus st, String num)
                {
                    this.st = st;
                    this.num = num;
                }

                public void run() {

                }
            }
            runOnUiThread(new MyRunnable(status,phoneNum) {
                @Override
                public void run() {

                    ChangeUI();
                    if(DataManager.getInstance().getCallStatus() == DataManager.CallStatus.IDLE)
                    {
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
            imageViewState.setImageResource(R.drawable.ringing);
            textViewCallstate.setText("Call from "+DataManager.getInstance().getCallerPhoneNum());

            buttonReject.setEnabled(true);
            buttonAccept.setEnabled(true);
        }
    }



}
