package com.lg.sixsenses.willi.logic;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

import com.lg.sixsenses.willi.logic.callmanager.CallHandler;
import com.lg.sixsenses.willi.repository.ConstantsWilli;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UdpInfo;
import com.lg.sixsenses.willi.repository.UserInfo;
import com.lg.sixsenses.willi.util.Util;

public class CallReceiveService extends Service {
    public static final String TAG = CallReceiveService.class.getName().toString();
    private AudioManager audioManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG,"Start CallReceiveService~~~");

        // get AudioOutput Setting

        SharedPreferences sp = getApplicationContext().getSharedPreferences(ConstantsWilli.PREFERENCE_FILENAME, Activity.MODE_PRIVATE);
        String audioOutput = sp.getString(ConstantsWilli.PREFERENCE_KEY_AUDIOOUTPUT, "");
        Log.d(TAG, "Read AudioOutput Option from Preference : "+audioOutput);
        if(audioOutput.equals("BLUETOOTH")) DataManager.getInstance().setAudioOutput(DataManager.AudioOutput.BLUETOOTH);
        else if(audioOutput.equals("SPEAKER")) DataManager.getInstance().setAudioOutput(DataManager.AudioOutput.SPEAKER);
        else if(audioOutput.equals("EARPIECE")) DataManager.getInstance().setAudioOutput(DataManager.AudioOutput.EARPIECE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        DataManager.AudioOutput ao = DataManager.getInstance().getAudioOutput();
        if (ao == DataManager.AudioOutput.SPEAKER) {
            audioManager.setBluetoothScoOn(false);
            audioManager.stopBluetoothSco();
            audioManager.setSpeakerphoneOn(true);
        } else if (ao == DataManager.AudioOutput.EARPIECE) {
            audioManager.setBluetoothScoOn(false);
            audioManager.stopBluetoothSco();
            audioManager.setSpeakerphoneOn(false);
        } else if (ao == DataManager.AudioOutput.BLUETOOTH) {
            audioManager.setSpeakerphoneOn(false);
            audioManager.setBluetoothScoOn(true);
            audioManager.startBluetoothSco();
        }

        // get Sound Setting
        String soundOption = sp.getString(ConstantsWilli.PREFERENCE_KEY_SOUND, "");
        if(soundOption.equals("BELL")) DataManager.getInstance().setSound(DataManager.Sound.BELL);
        else if(soundOption.equals("VIBRATE")) DataManager.getInstance().setSound(DataManager.Sound.VIBRATE);
        else if(soundOption.equals("MUTE")) DataManager.getInstance().setSound(DataManager.Sound.MUTE);

        // get MyInfo (PhoneNumber)
        String myPhoneNum = sp.getString(ConstantsWilli.PREFERENCE_KEY_MY_PHONE_NUMBER, "");
        String myName = sp.getString(ConstantsWilli.PREFERENCE_KEY_MY_NAME, "");
        String myEmail = sp.getString(ConstantsWilli.PREFERENCE_KEY_MY_EMAIL, "");
        UserInfo myInfo = new UserInfo();
        myInfo.setName(myName);
        myInfo.setEmail(myEmail);
        myInfo.setPhoneNum(myPhoneNum);
        DataManager.getInstance().setMyInfo(myInfo);

        UdpInfo udpInfo = new UdpInfo();
        udpInfo.setIpaddr(Util.getIPAddress());
        if(DataManager.getInstance().getMyInfo().getPhoneNum() != null && !DataManager.getInstance().getMyInfo().getPhoneNum().equals(""))
        {
            int pNum = Integer.parseInt(DataManager.getInstance().getMyInfo().getPhoneNum());
            udpInfo.setAudioPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_AUDIO_PORT);
            udpInfo.setVideoPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_VIDEO_PORT);
            DataManager.getInstance().setMyUdpInfo(udpInfo);
        }

        // get Token
        String token = sp.getString(ConstantsWilli.PREFERENCE_KEY_TOKEN, "");
        DataManager.getInstance().setToken(token);

        CallHandler.getInstance().setContext(getApplicationContext());
        CallHandler.getInstance().startCallHandler();
    }
}
