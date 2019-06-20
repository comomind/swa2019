package com.lg.sixsenses.willi.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lg.sixsenses.willi.DataRepository.ConstantsWilli;
import com.lg.sixsenses.willi.DataRepository.LoginInfo;
import com.lg.sixsenses.willi.Logic.CallManager.CallStateMachine;
import com.lg.sixsenses.willi.Logic.ServerCommManager.RestManager;
import com.lg.sixsenses.willi.DataRepository.DataManager;
import com.lg.sixsenses.willi.Logic.ServerCommManager.TcpRecvCallManager;
import com.lg.sixsenses.willi.Logic.ServerCommManager.TcpSendCallManager;
import com.lg.sixsenses.willi.R;
//import com.lg.sixsenses.willi.DataRepository.RegisterInfo;
import com.lg.sixsenses.willi.DataRepository.UpdatedData;
import com.lg.sixsenses.willi.DataRepository.UserInfo;
import com.lg.sixsenses.willi.Util;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class LoginActivity extends AppCompatActivity implements Observer {

    public static final String TAG = LoginActivity.class.getName().toString();
    private RestManager restManager;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewResult;
    private TcpSendCallManager sender = null;
    private TcpRecvCallManager receiver = null;

    public void buttonLoginClick(View view)
    {
        if(editTextEmail.getText().toString().length() == 0)
        {
            //Toast.makeText(getApplicationContext(),"Please enter Email!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Email!");
            return;
        }

        if(editTextPassword.getText().toString().length() == 0)
        {
            // Toast.makeText(getApplicationContext(),"Please enter Password!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Password!");
            return;
        }

        //RegisterInfo registerInfo = new RegisterInfo();
        //registerInfo.setEmail(editTextEmail.getText().toString());
        //registerInfo.setPassword(editTextPassword.getText().toString());

        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setEmail(editTextEmail.getText().toString());
        loginInfo.setPassword(editTextPassword.getText().toString());
        loginInfo.setip(Util.getIPAddress());
        loginInfo.setPort(ConstantsWilli.CLIENT_TCP_CALL_SIGNAL_PORT);

        restManager = new RestManager();
       // restManager.sendLogin(registerInfo);
        restManager.sendLogin(loginInfo);
    }

    public void buttonRegisterClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sender = new TcpSendCallManager();
        DataManager.getInstance().addObserver(this);

        //buttonLogin         = (Button)findViewById(R.id.buttonRegister);
        editTextEmail       = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword    = (EditText)findViewById(R.id.editTextPassword);
        textViewResult      = (TextView)findViewById(R.id.textViewResult);
        textViewResult.setText(null);

        receiver = new TcpRecvCallManager();
        receiver.start();
    }

    @Override
    public void update(Observable o, Object arg) {

        Log.d(TAG,"update in LogInActivity");
        UpdatedData data = (UpdatedData)arg;
        Log.d(TAG,"updated data : "+ data.toString());
        if(data.getType().equals("LoginResult"))
        {
            String p = null;
            ArrayList<UserInfo> contactList = (ArrayList<UserInfo>)(data.getData());
            for(UserInfo userInfo : contactList)
            {
                p += userInfo.toString();
            }
            Log.d(TAG, "Contact List : "+p);

            // TODO result를 표시하는 방법이 필요하다!!!!! 성공인지 실패 인지....
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getApplicationContext(),"My Phone Number is "+ DataManager.getInstance().getMyInfo().getPhoneNum(),Toast.LENGTH_SHORT).show();
                    textViewResult.setText("Login Success!");
                    editTextEmail.setText(null);
                    editTextPassword.setText(null);

                }
            });
        }
        else if(data.getType().equals("CallState"))
        {
            DataManager.CallStatus status = (DataManager.CallStatus)(data.getData());
            Log.d(TAG, "CallState : "+ status.toString());

            class MyRunnable implements Runnable {
                DataManager.CallStatus status;
                MyRunnable(DataManager.CallStatus status) { this.status = status; }

                public void run() {

                }
            }
            runOnUiThread(new MyRunnable(status) {
                @Override
                public void run() {
                    textViewResult.setText("Call State : "+status.toString());
                }
            });
        }
    }

    public void buttonSendClick(View view)
    {
        sender.startPhoneCall("1001");
    }

    public void buttonReceiveClick(View view)
    {
        receiver.receiveCall();
    }
    public void buttonReject1Click(View view)
    {
        Log.d(TAG,"Button Reject1 Click");
//        TcpSendCallManager sender1 = new TcpSendCallManager();
//        sender1.rejectPhoneCall("1001");
        sender.rejectPhoneCall("1001");
    }

    public void buttonReject2Click(View view)
    {
        receiver.rejectCall();
    }

    public void buttonParkClick(View view)
    {
        editTextEmail.setText("park@lge.com");
        editTextPassword.setText("1234");
    }
    public void buttonLeeClick(View view) {
        editTextEmail.setText("lee@lge.com");
        editTextPassword.setText("1234");
    }

}
