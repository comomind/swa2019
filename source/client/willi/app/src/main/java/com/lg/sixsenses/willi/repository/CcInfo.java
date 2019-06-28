package com.lg.sixsenses.willi.repository;

import java.util.ArrayList;
import java.util.Arrays;

public class CcInfo
{
    String startDate;
    int duration;
    ArrayList<String> aList;
    String ccNumber;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
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
