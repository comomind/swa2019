package com.lg.sixsenses.willi.ui;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UpdatedData;
import com.lg.sixsenses.willi.repository.UserInfo;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class DialActivity extends AppCompatActivity implements Observer {

    public static final String TAG = DialActivity.class.getName().toString();

    private AudioManager audioManager;

    // FrameLayout에 각 메뉴의 Fragment를 바꿔 줌
    private FragmentManager fragmentManager = getSupportFragmentManager();
    // 3개의 메뉴에 들어갈 Fragment들
    private SettingFragment settingFragment = new SettingFragment();
    private ContactsFragment contactsFragment = new ContactsFragment();
    private DialpadFragment dialpadFragment = new DialpadFragment();
    private messageFragment messageFragment = new messageFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);

        DataManager.getInstance().addObserver(this);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isSpeakerphoneOn())
        {
            DataManager.getInstance().setAudioOutput(DataManager.AudioOutput.SPEAKER);
        }
        else if (audioManager.isBluetoothScoOn())
        {
            DataManager.getInstance().setAudioOutput(DataManager.AudioOutput.BLUETOOTH);
        }
        else
        {
            DataManager.getInstance().setAudioOutput(DataManager.AudioOutput.EARPIECE);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // 첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, contactsFragment).commitAllowingStateLoss();

        // bottomNavigationView의 아이템이 선택될 때 호출될 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigation_menu1: {
                        transaction.replace(R.id.frame_layout, settingFragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.navigation_menu2: {
                        transaction.replace(R.id.frame_layout, contactsFragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.navigation_menu3: {
                        transaction.replace(R.id.frame_layout, dialpadFragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.navigation_menu4: {
                        transaction.replace(R.id.frame_layout, messageFragment).commitAllowingStateLoss();
                        break;
                    }
                    default:
                        transaction.replace(R.id.frame_layout, dialpadFragment).commitAllowingStateLoss();
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        UpdatedData data = (UpdatedData)arg;
        Log.d(TAG,"updated data : "+ data.toString());
        if(data.getType().equals("AudioOutput"))
        {
            DataManager.AudioOutput audioOutput = (DataManager.AudioOutput)(data.getData());
            if (audioOutput == DataManager.AudioOutput.SPEAKER) {
                audioManager.setBluetoothScoOn(false);
                audioManager.stopBluetoothSco();
                audioManager.setSpeakerphoneOn(true);
            } else if (audioOutput == DataManager.AudioOutput.EARPIECE) {
                audioManager.setBluetoothScoOn(false);
                audioManager.stopBluetoothSco();
                audioManager.setSpeakerphoneOn(false);
            } else if (audioOutput == DataManager.AudioOutput.BLUETOOTH) {
                audioManager.setSpeakerphoneOn(false);
                audioManager.setBluetoothScoOn(true);
                audioManager.startBluetoothSco();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
