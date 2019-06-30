package com.lg.sixsenses.willi.repository;

import com.lg.sixsenses.willi.logic.callmanager.AudioIo;
import com.lg.sixsenses.willi.logic.callmanager.VideoIo;

public class CcAvInfo {
    private AudioIo audioIo;
    private VideoIo videoIo;
    private int recvAudioPort;
    private int recvVideoPort;
    private int sendAudioPort;
    private int sendVideoPort;
    private String remoteIp;

    public AudioIo getAudioIo() {
        return audioIo;
    }

    public void setAudioIo(AudioIo audioIo) {
        this.audioIo = audioIo;
    }

    public VideoIo getVideoIo() {
        return videoIo;
    }

    public void setVideoIo(VideoIo videoIo) {
        this.videoIo = videoIo;
    }

    public int getRecvAudioPort() {
        return recvAudioPort;
    }

    public void setRecvAudioPort(int recvAudioPort) {
        this.recvAudioPort = recvAudioPort;
    }

    public int getRecvVideoPort() {
        return recvVideoPort;
    }

    public void setRecvVideoPort(int recvVideoPort) {
        this.recvVideoPort = recvVideoPort;
    }

    public int getSendAudioPort() {
        return sendAudioPort;
    }

    public void setSendAudioPort(int sendAudioPort) {
        this.sendAudioPort = sendAudioPort;
    }

    public int getSendVideoPort() {
        return sendVideoPort;
    }

    public void setSendVideoPort(int sendVideoPort) {
        this.sendVideoPort = sendVideoPort;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    @Override
    public String toString() {
        return "CcAvInfo{" +
                "audioIo=" + audioIo +
                ", videoIo=" + videoIo +
                ", recvAudioPort=" + recvAudioPort +
                ", recvVideoPort=" + recvVideoPort +
                ", sendAudioPort=" + sendAudioPort +
                ", sendVideoPort=" + sendVideoPort +
                ", remoteIp='" + remoteIp + '\'' +
                '}';
    }
}
