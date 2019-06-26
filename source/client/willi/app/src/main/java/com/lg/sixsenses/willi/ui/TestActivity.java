package com.lg.sixsenses.willi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.codec.audio.AudioCodecConst;
import com.lg.sixsenses.willi.codec.audio.AudioCodecFactory;
import com.lg.sixsenses.willi.logic.callmanager.AudioIo;
import com.lg.sixsenses.willi.util.Util;

import org.w3c.dom.Text;

import java.net.InetAddress;

public class TestActivity extends AppCompatActivity {
    public static final String TAG = TestActivity.class.getName().toString();
    private EditText editTextIP;
    private TextView textView;

    private AudioIo audioIo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        editTextIP = (EditText)findViewById(R.id.editTextIP);
        editTextIP.setText(Util.getIPAddress());
        editTextIP.setSelection(editTextIP.length());

        textView = (TextView)findViewById(R.id.textViewIP);
        textView.setText(Util.getIPAddress());
        audioIo = new AudioIo(getApplicationContext());
        audioIo.setAudioCodec(AudioCodecFactory.getCodec(AudioCodecConst.CodecType.OPUS));
    }

    public void buttonSendClick(View view) {
        try {
            InetAddress remoteIp = InetAddress.getByName(editTextIP.getText().toString());
            audioIo.startSend(remoteIp, 60001);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void buttonReceiveClick(View view) {
        audioIo.startReceive(60001);

    }

    public void buttonStopClick(View view) {
        Log.d(TAG, "stopAll");
        audioIo.stopAll();
    }

}
