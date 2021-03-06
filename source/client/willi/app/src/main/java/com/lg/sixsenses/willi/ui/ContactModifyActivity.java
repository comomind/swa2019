package com.lg.sixsenses.willi.ui;

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
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UserInfo;

import java.util.Observable;
import java.util.Observer;

public class ContactModifyActivity extends AppCompatActivity {

    public static final String TAG = RegisterActivity.class.getName().toString();
    private RestManager restManager;

    private EditText editUpdateName;
    private String email;
    private TextView textViewResult;
    private TextView textViewContactUpdateEmail;

    public void buttonContactUpdateCloseClick(View view)
    {
        Intent intent = new Intent(this, DialActivity.class);
        startActivity(intent);

        finish();
    }

    public void buttonContactUpdateClick(View view)
    {
        if(editUpdateName.getText().toString().length() == 0)
        {
            //Toast.makeText(getApplicationContext(),"Please enter Name!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Name!");
            return;
        }


        UserInfo userInfo = new UserInfo();
        userInfo.setName(editUpdateName.getText().toString());
        userInfo.setEmail(email);

        restManager.sendFriendCommand(userInfo,RestManager.CMD_FRIEND_EDIT);

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
        setContentView(R.layout.activity_contact_modify);

        Intent intent = getIntent();
        email = intent.getStringExtra("Email");

        textViewContactUpdateEmail = (TextView)findViewById(R.id.textViewContactUpdateEmail);
        textViewContactUpdateEmail.setText(email);


        editUpdateName   = (EditText)findViewById(R.id.editUpdateName);

        textViewResult = (TextView)findViewById(R.id.textViewResult);
        textViewResult.setText(null);

        restManager = new RestManager();

    }
}

