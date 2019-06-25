package com.lg.sixsenses.willi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.util.Util;

import org.w3c.dom.Text;

public class TestActivity extends AppCompatActivity {
    public static final String TAG = TestActivity.class.getName().toString();
    private EditText editTextIP;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        editTextIP = (EditText)findViewById(R.id.editTextIP);
        editTextIP.setText(Util.getIPAddress());
        editTextIP.setSelection(editTextIP.length());

        textView = (TextView)findViewById(R.id.textViewIP);
        textView.setText(Util.getIPAddress());
    }

    public void buttonSendClick(View view)
    {

    }

    public void buttonReceiveClick(View view)
    {

    }

}
