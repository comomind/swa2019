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
    public enum Resolution {LOW, MID, HIGH }  //640x480 , 320x240, 176 x 144

    private String SERVER_IP;


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
    private boolean isLogin;
    private ArrayList<CcInfo> ccList;
    private Resolution resolution;
    private int camWidth = 176;
    private int camHeight = 144;
    private int comRate = 15;

    public void clearCallInfo()
    {
        callerPhoneNum = null;
        calleePhoneNum = null;
        callStatus = CallStatus.IDLE;
        callId = 0;
        if(peerUdpInfoList != null) peerUdpInfoList.clear();
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

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public ArrayList<CcInfo> getCcList() {
        return ccList;
    }

    public void setCcList(ArrayList<CcInfo> ccList) {
        this.ccList = ccList;
    }

    public String getSERVER_IP() {
        return SERVER_IP;
    }

    public void setSERVER_IP(String SERVER_IP) {
        this.SERVER_IP = SERVER_IP;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
    }

    public int getCamWidth() {
        return camWidth;
    }

    public void setCamWidth(int camWidth) {
        this.camWidth = camWidth;
    }

    public int getCamHeight() {
        return camHeight;
    }

    public void setCamHeight(int camHeight) {
        this.camHeight = camHeight;
    }

    public int getComRate() {
        return comRate;
    }

    public void setComRate(int comRate) {
        this.comRate = comRate;
    }

    public void NotifyUpdate(UpdatedData data) {
        Log.d(TAG,"NotifyUpdate");
        setChanged();
        notifyObservers(data);
    }
}