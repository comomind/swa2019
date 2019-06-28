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

import java.util.Observable;
import java.util.Observer;

public class ContactModifyActivity extends AppCompatActivity {

    private RestManager restManager;
    private Button voicecall;
    private Button videocall;
    private Button updatebutton;
    private Button deletecontact;
    private Button close;

    private EditText editTextEmail;
    private EditText editTextName;
    private TextView textViewResult;

    public void close(View view)
    {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_modify);

//        DataManager.getInstance().addObserver(this);
/*
        voicecall              = (Button)findViewById(R.id.voicecall);
        editTextEmail               = (EditText)findViewById(R.id.editTextEmail);
        editTextName                = (EditText)findViewById(R.id.editTextName);



        restManager = new RestManager();
*/
    }


}
