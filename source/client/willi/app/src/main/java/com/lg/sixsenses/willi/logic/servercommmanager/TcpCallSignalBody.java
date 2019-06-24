package com.lg.sixsenses.willi.logic.servercommmanager;

public class TcpCallSignalBody {
    private String cmd;
    private String type;
    private String callerPhoneNum;
    private String calleePhoneNum;
    private int udpAudioPort;
    private int udpVideoPort;
    private String ipaddr;
    private long callId;

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

    public String getCallerPhoneNum() {
        return callerPhoneNum;
    }

    public void setCallerPhoneNum(String callerPhoneNum) {
        this.callerPhoneNum = callerPhoneNum;
    }

    public String getCalleePhoneNum() {
        return calleePhoneNum;
    }

    public void setCalleePhoneNum(String calleePhoneNum) {
        this.calleePhoneNum = calleePhoneNum;
    }

    public int getUdpAudioPort() {
        return udpAudioPort;
    }

    public void setUdpAudioPort(int udpAudioPort) {
        this.udpAudioPort = udpAudioPort;
    }

    public int getUdpVideoPort() {
        return udpVideoPort;
    }

    public void setUdpVideoPort(int udpVideoPort) {
        this.udpVideoPort = udpVideoPort;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }

    @Override
    public String toString() {
        return "TcpCallSignalBody{" +
                "cmd='" + cmd + '\'' +
                ", type='" + type + '\'' +
                ", callerPhoneNum='" + callerPhoneNum + '\'' +
                ", calleePhoneNum='" + calleePhoneNum + '\'' +
                ", udpAudioPort=" + udpAudioPort +
                ", udpVideoPort=" + udpVideoPort +
                ", ipaddr='" + ipaddr + '\'' +
                ", callId='" + callId + '\'' +
                '}';
    }
}
