package com.lg.sixsenses.willi.repository;

public class UdpInfo {
    private String ipaddr;
    private int audioPort;
    private int videoPort;

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public int getAudioPort() {
        return audioPort;
    }

    public void setAudioPort(int audioPort) {
        this.audioPort = audioPort;
    }

    public int getVideoPort() {
        return videoPort;
    }

    public void setVideoPort(int videoPort) {
        this.videoPort = videoPort;
    }

    @Override
    public String toString() {
        return "UdpInfo{" +
                "ipaddr='" + ipaddr + '\'' +
                ", audioPort=" + audioPort +
                ", videoPort=" + videoPort +
                '}';
    }
}
