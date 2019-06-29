package com.lg.sixsenses.willi.logic.servercommmanager;

import com.lg.sixsenses.willi.repository.UdpPort;

import java.util.ArrayList;

public class TcpCcSignalBody {
    private String cmd;
    private String type;    //3 : CC
    private String ccNumber;
    private ArrayList<UdpPort> list;
    private String rejecter;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<UdpPort> getList() {
        return list;
    }

    public void setList(ArrayList<UdpPort> list) {
        this.list = list;
    }

    public String getRejecter() {
        return rejecter;
    }

    public void setRejecter(String rejecter) {
        this.rejecter = rejecter;
    }

    public String getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    @Override
    public String toString() {
        return "TcpCcSignalBody{" +
                "cmd='" + cmd + '\'' +
                ", type='" + type + '\'' +
                ", ccNumber='" + ccNumber + '\'' +
                ", list=" + list +
                ", rejecter='" + rejecter + '\'' +
                '}';
    }
}
