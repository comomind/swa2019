package com.lg.sixsenses.willi.repository;

public class UdpPort {
    private String email;
    private int audioPort;
    private int videoPort;
    private String ip;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "UdpPort{" +
                "email='" + email + '\'' +
                ", audioPort=" + audioPort +
                ", videoPort=" + videoPort +
                ", ip='" + ip + '\'' +
                '}';
    }
}
