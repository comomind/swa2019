package com.lg.sixsenses.willi.logic.CallManager;

public interface CallState {
    public void sendCallRequest();
    public void recvCallRequest();
    public void sendCallReject();
    public void recvCallReject();
    public void sendCallAccept();
    public void recvCallAccept();

}



