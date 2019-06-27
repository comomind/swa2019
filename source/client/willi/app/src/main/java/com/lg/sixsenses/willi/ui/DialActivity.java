package com.lg.sixsenses.willi.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.lg.sixsenses.willi.repository.ConstantsWilli;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);

        Log.d(TAG,"DialActivity Created !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
        transaction.replace(R.id.frame_layout, dialpadFragment).commitAllowingStateLoss();

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

            SharedPreferences sp = getSharedPreferences(ConstantsWilli.PREFERENCE_FILENAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ConstantsWilli.PREFERENCE_KEY_AUDIOOUTPUT, audioOutput.toString());
            editor.commit();
        }
        else if(data.getType().equals("Sound"))
        {
            DataManager.Sound sound = (DataManager.Sound)(data.getData());

            SharedPreferences sp = getSharedPreferences(ConstantsWilli.PREFERENCE_FILENAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ConstantsWilli.PREFERENCE_KEY_SOUND, sound.toString());
            editor.commit();
        }

    }
}
