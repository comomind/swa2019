package com.lg.sixsenses.willi.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class CcInfo
{
    //String startDate;
    Date startDate;
    private String email;
    int duration;
    ArrayList<String> aList;
    String ccNumber;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCcNumber() {
        return ccNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    public ArrayList<String> getaList() {
        return aList;
    }

    public void setaList(ArrayList<String> aList) {
        this.aList = aList;
    }

    @Override
    public String toString() {
        return "CcInfo{" +
                "startDate='" + startDate + '\'' +
                ", duration=" + duration +
                ", aList=" + aList +
                ", ccNumber='" + ccNumber + '\'' +
                '}';
    }
}
