package com.lg.sixsenses.willi.repository;

public class IpInfo
{
    String ip;
    int port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "IpInfo{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
