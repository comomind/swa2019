package com.lg.sixsenses.willi.DataRepository;

import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;

public class DataManager extends Observable {

    public static final String TAG = DataManager.class.getName().toString();
    private static DataManager instance = new DataManager();
    public static DataManager getInstance() {
        return instance;
    }

    public enum CallStatus {IDLE, CALLING, RINGING, CONNECTED}

    private DataManager() {
    }

    private UserInfo myInfo;
    private ArrayList<UserInfo> contactList;
    private String token;
    private String callerPhoneNum;
    private CallStatus callStatus;

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

    public void NotifyUpdate(UpdatedData data) {
        Log.d(TAG,"NotifyUpdate");
        setChanged();
        notifyObservers(data);
    }
}