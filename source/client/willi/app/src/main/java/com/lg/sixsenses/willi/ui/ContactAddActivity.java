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


public class ContactAddActivity extends AppCompatActivity {
    public static final String TAG = RegisterActivity.class.getName().toString();
    private RestManager restManager;
    private Button AddClose;
    private Button AddContact;

    private EditText editAddEmail;
    private EditText editAddName;
    private EditText editAddNum;

    private TextView textViewResult;

    public void AddClose(View view)
    {
        finish();
    }

    public void AddContact(View view)
    {
        if(editAddEmail.getText().toString().length() == 0)
        {
            //Toast.makeText(getApplicationContext(),"Please enter Email!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Email!");
            return;
        }
        if(editAddName.getText().toString().length() == 0)
        {
            //Toast.makeText(getApplicationContext(),"Please enter Name!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter Name!");
            return;
        }
        if(editAddNum.getText().toString().length() == 0)
        {
            // Toast.makeText(getApplicationContext(),"Please enter Password!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter PhoneNumber!");
            return;
        }

  //      RegisterInfo registerInfo = new RegisterInfo();
  //      registerInfo.setEmail(editAddEmail.getText().toString());
  //      registerInfo.setName(editAddName.getText().toString());
  //      registerInfo.setPassword(editAddNum.getText().toString());

  //      restManager.sendRegisterUser(registerInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);

        AddContact     = (Button)findViewById(R.id.AddContact);
        editAddEmail   = (EditText)findViewById(R.id.editAddEmail);
        editAddName    = (EditText)findViewById(R.id.editAddName);
        editAddNum     = (EditText)findViewById(R.id.editAddNum);

        textViewResult = (TextView)findViewById(R.id.textViewResult);
        textViewResult.setText(null);

        restManager = new RestManager();
    }
}
