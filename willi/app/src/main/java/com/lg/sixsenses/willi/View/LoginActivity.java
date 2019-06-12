package com.lg.sixsenses.willi.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lg.sixsenses.willi.ConnectionManager.RestManager;
import com.lg.sixsenses.willi.Model.DataManager;
import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.RegisterInfo;
import com.lg.sixsenses.willi.UpdatedData;
import com.lg.sixsenses.willi.UserInfo;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class LoginActivity extends AppCompatActivity implements Observer {

    public static final String TAG = LoginActivity.class.getName().toString();
    private RestManager restManager;
    private Button buttonLogin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewResult;

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

        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setEmail(editTextEmail.getText().toString());
        registerInfo.setPassword(editTextPassword.getText().toString());

        restManager = new RestManager();
        restManager.sendLogin(registerInfo);
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

        DataManager.getInstance().addObserver(this);

        buttonLogin         = (Button)findViewById(R.id.buttonRegister);
        editTextEmail       = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword    = (EditText)findViewById(R.id.editTextPassword);
        textViewResult      = (TextView)findViewById(R.id.textViewResult);
        textViewResult.setText(null);

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
    }
}
