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

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.logic.servercommmanager.RestManager;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.RegisterInfo;
import com.lg.sixsenses.willi.repository.UpdatedData;
import com.lg.sixsenses.willi.repository.UserInfo;

import java.util.Observable;
import java.util.Observer;

public class MyInfoActivity extends AppCompatActivity implements Observer {
    public static final String TAG = MyInfoActivity.class.getName().toString();
    private RestManager restManager;
    private Button MyInfoSave;

    private TextView TextEmail;
    private TextView viewnumber;
    private EditText editTextName;

    private Spinner spinnerSecurityQuestion;
    private EditText editTextSecurityAnswer;
    private TextView textViewResult;
    private TextView result2;

    public void close(View view)
    {
        finish();
    }

    public void MyInfoSave(View view)
    {
        //UserInfo myInfo = DataManager.getInstance().getMyInfo();

        if(editTextSecurityAnswer.getText().toString().length() == 0)
        {
            //Toast.makeText(getApplicationContext(),"Please enter Security Answer!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Security Answer!");
            return;
        }

        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setEmail(TextEmail.getText().toString());
        registerInfo.setName(editTextName.getText().toString());
        registerInfo.setSecurityQuestion(spinnerSecurityQuestion.getSelectedItem().toString());
        registerInfo.setSecurityAnswer(editTextSecurityAnswer.getText().toString());

        restManager.sendUpdateUser(registerInfo);

        textViewResult = (TextView)findViewById(R.id.textViewResult);
        textViewResult.setText("Success Update");
        Log.d(TAG,"update in MyInfo"+editTextName.getText().toString());
        result2 = (TextView)findViewById(R.id.textViewResult);
        result2.setText("New name : "+editTextName.getText().toString());


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        UserInfo myInfo = DataManager.getInstance().getMyInfo();

        MyInfoSave = (Button)findViewById(R.id.MyInfoSave);
        TextEmail = (TextView)findViewById(R.id.TextEmail);
        TextEmail.setText(myInfo.getEmail());
        editTextName = (EditText)findViewById(R.id.editnewname);
        editTextName.setText(myInfo.getName());
        viewnumber = (TextView)findViewById(R.id.viewnumber);
        viewnumber.setText(myInfo.getPhoneNum());

        editTextSecurityAnswer      = (EditText)findViewById(R.id.editTextSecurityAnswer);

        spinnerSecurityQuestion = (Spinner)findViewById(R.id.spinnerSecurityQuestion);
        String[] items = new String[]{"What is your favorite color?", "What is your mother's maiden name?", "Where you were born?"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerSecurityQuestion.setAdapter(adapter);



        restManager = new RestManager();
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d(TAG,"update in MyInfo");
        UpdatedData data = (UpdatedData)arg;
        if(data.getType().equals("UpdateResult"))
        {
            UserInfo myInfo = (UserInfo)data.getData();
            RegisterInfo registerInfo = (RegisterInfo)data.getData();
            Log.d(TAG, "Phone num : "+myInfo.getPhoneNum());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getApplicationContext(),"My Phone Number is "+ DataManager.getInstance().getMyInfo().getPhoneNum(),Toast.LENGTH_SHORT).show();
                    textViewResult.setText("Success Update");


                }
            });


        }
    }
}
