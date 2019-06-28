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
import com.lg.sixsenses.willi.repository.RegisterInfo;

public class ForgotPWActivity extends AppCompatActivity {
    private RestManager restManager;
    private Button close;
    private Button ForgotPassword;
    private EditText editTextEmail;
    private EditText editTextName;
    private EditText editTextPassword;
    private Spinner spinnerSecurityQuestion;
    private EditText editTextSecurityAnswer;
    private TextView textViewResult;

    public void close(View view)
    {
        finish();
    }

    public void ForgotPassword(View view)
    {
        if(editTextEmail.getText().toString().length() == 0)
        {
            //Toast.makeText(getApplicationContext(),"Please enter Email!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Email!");
            return;
        }
        if(editTextSecurityAnswer.getText().toString().length() == 0)
        {
            //Toast.makeText(getApplicationContext(),"Please enter Security Answer!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Security Answer!");
            return;
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pw);

        ForgotPassword              = (Button)findViewById(R.id.ForgotPassword);
        editTextEmail               = (EditText)findViewById(R.id.editAddEmail);
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
