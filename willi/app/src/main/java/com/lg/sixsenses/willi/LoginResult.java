package com.lg.sixsenses.willi;

import java.util.ArrayList;

public class LoginResult {
    private ArrayList<UserInfo> list;

    public ArrayList<UserInfo> getList() {
        return list;
    }

    public void setList(ArrayList<UserInfo> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "list=" + list +
                '}';
    }
}
