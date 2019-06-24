package com.lg.sixsenses.willi.logic.ServerCommManager;

import com.lg.sixsenses.willi.repository.UserInfo;

import java.util.ArrayList;

public class LoginResult {
    private UserInfo myInfo;
    private ArrayList<UserInfo> list;

    public ArrayList<UserInfo> getList() {
        return list;
    }

    public void setList(ArrayList<UserInfo> list) {
        this.list = list;
    }

    public UserInfo getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(UserInfo myInfo) {
        this.myInfo = myInfo;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "myInfo=" + myInfo +
                ", list=" + list +
                '}';
    }
}
