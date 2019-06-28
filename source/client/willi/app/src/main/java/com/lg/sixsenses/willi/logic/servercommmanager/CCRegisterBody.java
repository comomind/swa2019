package com.lg.sixsenses.willi.logic.servercommmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class CCRegisterBody {
    Date startDate;
    int duration;
    ArrayList<String> aList;

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

    public ArrayList<String> getaList() {
        return aList;
    }

    public void setaList(ArrayList<String> aList) {
        this.aList = aList;
    }

    @Override
    public String toString() {
        return "CCRegisterBody{" +
                "startDate=" + startDate +
                ", duration=" + duration +
                ", aList=" + aList +
                '}';
    }
}
