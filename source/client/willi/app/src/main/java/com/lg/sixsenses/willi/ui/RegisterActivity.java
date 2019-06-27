package com.lg.sixsenses.willi.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lg.sixsenses.willi.logic.servercommmanager.RestManager;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.repository.RegisterInfo;
import com.lg.sixsenses.willi.repository.UpdatedData;
import com.lg.sixsenses.willi.repository.UserInfo;

import java.util.Observable;
import java.util.Observer;

public class RegisterActivity extends AppCompatActivity implements Observer{
    public static final String TAG = RegisterActivity.class.getName().toString();
    private RestManager restManager;
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextName;
    private EditText editTextPassword;
    private Spinner spinnerSecurityQuestion;
    private EditText editTextSecurityAnswer;
    private TextView textViewResult;

    public void buttonCloseClick(View view)
    {
        finish();
    }

    public void buttonRegisterClick(View view)
    {
        if(editTextEmail.getText().toString().length() == 0)
        {
            //Toast.makeText(getApplicationContext(),"Please enter Email!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Email!");
            return;
        }
        if(editTextName.getText().toString().length() == 0)
        {
            //Toast.makeText(getApplicationContext(),"Please enter Name!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Name!");
            return;
        }
        if(editTextPassword.getText().toString().length() == 0)
        {
            // Toast.makeText(getApplicationContext(),"Please enter Password!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Password!");
            return;
        }
        if(editTextSecurityAnswer.getText().toString().length() == 0)
        {
            //Toast.makeText(getApplicationContext(),"Please enter Security Answer!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Security Answer!");
            return;
        }

        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setEmail(editTextEmail.getText().toString());
        registerInfo.setName(editTextName.getText().toString());
        registerInfo.setPassword(editTextPassword.getText().toString());
        registerInfo.setSecurityQuestion(spinnerSecurityQuestion.getSelectedItem().toString());
        registerInfo.setSecurityAnswer(editTextSecurityAnswer.getText().toString());

//        registerInfo.setEmail("abc@abc.com");
//        registerInfo.setName("Park");
//        registerInfo.setPassword("lge123");
//        registerInfo.setSecurityQuestion("What is your favorite color?");
//        registerInfo.setSecurityAnswer("Blue");
        restManager.sendRegisterUser(registerInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        DataManager.getInstance().addObserver(this);

        buttonRegister              = (Button)findViewById(R.id.AddContact);
        editTextEmail               = (EditText)findViewById(R.id.editAddEmail);
        editTextName                = (EditText)findViewById(R.id.editAddName);
        editTextPassword            = (EditText)findViewById(R.id.editAddNum);
        //editTextSecurityQuestion    = (EditText)findViewById(R.id.editTextSecurityQuestion);
        editTextSecurityAnswer      = (EditText)findViewById(R.id.editTextSecurityAnswer);

        spinnerSecurityQuestion = (Spinner)findViewById(R.id.spinnerSecurityQuestion);
        String[] items = new String[]{"What is your favorite color?", "What is your mother's maiden name?", "Where you were born?"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerSecurityQuestion.setAdapter(adapter);

        textViewResult = (TextView)findViewById(R.id.textViewResult);
        textViewResult.setText(null);

        restManager = new RestManager();
//        buttonRegister.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RegisterInfo registerInfo = new RegisterInfo();
//                UserInfo userInfo = new UserInfo();
//
//                userInfo.setEmail(editTextEmail.getText().toString());
//                userInfo.setName(editTextName.getText().toString());
//                userInfo.setPassword(editTextPassword.getText().toString());
//                registerInfo.setSecurityQuestion(editTextSecurityQuestion.getText().toString());
//                registerInfo.setSecurityAnswer(editTextSecurityAnswer.getText().toString());
//                registerInfo.setUserInfo(userInfo);
//
//            }
//        }) ;
    }

    @Override
    public void update(Observable o, Object arg) {

        Log.d(TAG,"update in RegisterActivity");
        UpdatedData data = (UpdatedData)arg;
        Log.d(TAG,"updated data : "+ data.toString());
        if(data.getType().equals("RegisterResult"))
        {
            UserInfo myInfo = (UserInfo)data.getData();
            Log.d(TAG, "Phone num : "+myInfo.getPhoneNum());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getApplicationContext(),"My Phone Number is "+ DataManager.getInstance().getMyInfo().getPhoneNum(),Toast.LENGTH_SHORT).show();
                    textViewResult.setText("Resisterd! PhoneNumber : "+DataManager.getInstance().getMyInfo().getPhoneNum());
                    editTextEmail.setText(null);
                    editTextName.setText(null);
                    editTextPassword.setText(null);
                    editTextSecurityAnswer.setText(null);

                }
            });


        }
    }
}
