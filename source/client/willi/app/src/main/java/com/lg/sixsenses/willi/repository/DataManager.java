package com.lg.sixsenses.willi.repository;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

public class DataManager extends Observable {

    public static final String TAG = DataManager.class.getName().toString();
    private static DataManager instance = new DataManager();
    public static DataManager getInstance() {
        return instance;
    }

    public enum CallStatus {IDLE, CALLING, RINGING, CONNECTED}
    public enum AudioOutput {BLUETOOTH, SPEAKER, EARPIECE }
    public enum Sound {BELL, VIBRATE, MUTE }

    private DataManager() {
    }

    private UserInfo myInfo;
    private ArrayList<UserInfo> contactList;
    private String token;
    private String callerPhoneNum;
    private String calleePhoneNum;
    private CallStatus callStatus;
    private long callId;
    private ArrayList<UdpInfo> peerUdpInfoList;
    private UdpInfo myUdpInfo;
    private AudioOutput audioOutput;
    private Sound sound;

    public void clearCallInfo()
    {
        callerPhoneNum = null;
        calleePhoneNum = null;
        callStatus = CallStatus.IDLE;
        callId = 0;
        peerUdpInfoList.clear();
    }

    public UserInfo getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(UserInfo myInfo) {
        this.myInfo = myInfo;
    }

    public ArrayList<UserInfo> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<UserInfo> contactList) {
        this.contactList = contactList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCallerPhoneNum() {
        return callerPhoneNum;
    }

    public void setCallerPhoneNum(String callerPhoneNum) {
        this.callerPhoneNum = callerPhoneNum;
    }

    public CallStatus getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(CallStatus callStatus) {
        this.callStatus = callStatus;
    }

    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }

    public String getCalleePhoneNum() {
        return calleePhoneNum;
    }

    public void setCalleePhoneNum(String calleePhoneNum) {
        this.calleePhoneNum = calleePhoneNum;
    }

    public ArrayList<UdpInfo> getPeerUdpInfoList() {
        return peerUdpInfoList;
    }

    public UdpInfo getMyUdpInfo() {
        return myUdpInfo;
    }

    public void setMyUdpInfo(UdpInfo myUdpInfo) {
        this.myUdpInfo = myUdpInfo;
    }

    public void setPeerUdpInfoList(ArrayList<UdpInfo> peerUdpInfoList) {
        this.peerUdpInfoList = peerUdpInfoList;
    }

    public AudioOutput getAudioOutput() {
        return audioOutput;
    }

    public void setAudioOutput(AudioOutput audioOutput) {
        this.audioOutput = audioOutput;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public void NotifyUpdate(UpdatedData data) {
        Log.d(TAG,"NotifyUpdate");
        setChanged();
        notifyObservers(data);
    }
}