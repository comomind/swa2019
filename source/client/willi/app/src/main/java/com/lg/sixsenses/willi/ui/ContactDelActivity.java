package com.lg.sixsenses.willi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.logic.servercommmanager.RestManager;
import com.lg.sixsenses.willi.repository.UserInfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.logic.servercommmanager.RestManager;
import com.lg.sixsenses.willi.repository.RegisterInfo;
import com.lg.sixsenses.willi.repository.UserInfo;


public class ContactDelActivity extends AppCompatActivity {
    public static final String TAG = RegisterActivity.class.getName().toString();
    private RestManager restManager;

    String email;
    String name;

    private TextView textViewContactDelEmail;
    private TextView textViewContactDelName;

    private TextView textViewResult;

    public void buttonContactDelCancelClick(View view)
    {
        Intent intent = new Intent(this, DialActivity.class);
        startActivity(intent);

        finish();
    }

    public void buttonContactDelConfirmClick(View view)
    {



        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(email);
        Log.d(TAG, "Delete contact : " + userInfo.getEmail());
        restManager.sendFriendCommand(userInfo, RestManager.CMD_FRIEND_DEL);


        Intent intent = new Intent(this, DialActivity.class);
        startActivity(intent);

        finish();
//        if(editAddNum.getText().toString().length() == 0)
//        {
//            // Toast.makeText(getApplicationContext(),"Please enter Password!",Toast.LENGTH_SHORT).show();
//            textViewResult.setText("Please enter PhoneNumber!");
//            return;
//        }

        //      RegisterInfo registerInfo = new RegisterInfo();
        //      registerInfo.setEmail(editAddEmail.getText().toString());
        //      registerInfo.setName(editAddName.getText().toString());
        //      registerInfo.setPassword(editAddNum.getText().toString());

        //      restManager.sendRegisterUser(registerInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_delete);

        Intent in = getIntent();
        email = in.getStringExtra("Email");
        name = in.getStringExtra("Name");

        textViewContactDelEmail = (TextView) findViewById(R.id.textViewContactDelEmail);
        textViewContactDelName = (TextView) findViewById(R.id.textViewContactDelName);
        textViewContactDelEmail.setText(email);
        textViewContactDelName.setText(name);

        textViewResult = (TextView)findViewById(R.id.textViewResult);
        textViewResult.setText(null);

        restManager = new RestManager();
    }
}
