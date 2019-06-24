package com.lg.sixsenses.willi.logic.servercommmanager;

public class TcpCallSignalHeader {
    private String type;
    private String token;
    private String ipaddr;
    private String trantype;
    private int reqtype;
    private int svctype;
    private String svcid;
    private String result;
    private String message;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getTrantype() {
        return trantype;
    }

    public void setTrantype(String trantype) {
        this.trantype = trantype;
    }

    public int getReqtype() {
        return reqtype;
    }

    public void setReqtype(int reqtype) {
        this.reqtype = reqtype;
    }

    public int getSvctype() {
        return svctype;
    }

    public void setSvctype(int svctype) {
        this.svctype = svctype;
    }

    public String getSvcid() {
        return svcid;
    }

    public void setSvcid(String svcid) {
        this.svcid = svcid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TcpCallSignalHeader{" +
                "type='" + type + '\'' +
                ", token='" + token + '\'' +
                ", ipaddr='" + ipaddr + '\'' +
                ", trantype='" + trantype + '\'' +
                ", reqtype='" + reqtype + '\'' +
                ", svctype='" + svctype + '\'' +
                ", svcid='" + svcid + '\'' +
                ", result='" + result + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
