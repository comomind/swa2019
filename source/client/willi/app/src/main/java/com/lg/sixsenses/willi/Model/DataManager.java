package com.lg.sixsenses.willi.Model;

import android.util.Log;

import com.lg.sixsenses.willi.UpdatedData;
import com.lg.sixsenses.willi.UserInfo;

import java.util.ArrayList;
import java.util.Observable;

public class DataManager extends Observable {

    public static final String TAG = DataManager.class.getName().toString();
    private static DataManager instance = new DataManager();
    public static DataManager getInstance() {
        return instance;
    }

    private DataManager() {
    }

    private UserInfo myInfo;
    private ArrayList<UserInfo> contactList;

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

    public void NotifyUpdate(UpdatedData data) {
        Log.d(TAG,"NotifyUpdate");
        setChanged();
        notifyObservers(data);
    }
}