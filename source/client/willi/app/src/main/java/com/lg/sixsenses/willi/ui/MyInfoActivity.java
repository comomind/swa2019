package com.lg.sixsenses.willi.ui;

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
import com.lg.sixsenses.willi.repository.RegisterInfo;
import com.lg.sixsenses.willi.repository.UserInfo;

public class MyInfoActivity extends AppCompatActivity {
    private RestManager restManager;
    private Button MyInfoSave;

    private TextView TextEmail;
    private TextView viewnumber;
    private EditText editTextName;

    private Spinner spinnerSecurityQuestion;
    private EditText editTextSecurityAnswer;
    private TextView textViewResult;

    public void close(View view)
    {
        finish();
    }

    public void MyInfoSave(View view)
    {



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

        textViewResult = (TextView)findViewById(R.id.textViewResult);
        textViewResult.setText(null);

        restManager = new RestManager();
    }
}
