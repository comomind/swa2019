package com.lg.sixsenses.willi.logic.callmanager;

import android.content.Context;
import android.util.Log;

import com.lg.sixsenses.willi.logic.servercommmanager.TcpRecvCallManager;
import com.lg.sixsenses.willi.logic.servercommmanager.TcpSendCallManager;

public class CallHandler {
    public static final String TAG = CallHandler.class.getName().toString();
    private static CallHandler instance = new CallHandler();
    public static CallHandler getInstance() {
        return instance;
    }

    private TcpRecvCallManager tcpRecvCallManager;
    private TcpSendCallManager tcpSendCallManager;

    private Context context;

    public void setContext(Context context)
    {
        this.context = context;
    }
    public void callRequest(String phoneNumber) {
        tcpSendCallManager.startPhoneCall("1001");
        // TODO: initiate UDP listen socket


    }

    public void onReceiveCallRejectMessage()
    {
        Log.d(TAG,"onReceiveCallRejectMessage");
    }

    public void onReceiveCallAcceptMessage()
    {
        Log.d(TAG,"onReceiveCallAcceptMessage");
    }

    public void callAccept() {
        tcpRecvCallManager.receiveCall();
        // PRECONDITION: UDP socket
        // TODO: initiate UDP socket
        // TODO: create PLC
        // TODO: create Player


    }

    public void callRejectForIncomingCall() {
        // incoming call request reject (before accepted)
        tcpRecvCallManager.rejectCall();
        // TODO: close UDP stuffs

    }
    public void callRejectForConnectedCall(String phoneNum)
    {
        // call reject in call (rejected by me)
        tcpSendCallManager.rejectPhoneCall(phoneNum);
        // TODO: close UDP stuffs

    }

    public void startCallHandler()
    {
        tcpSendCallManager = new TcpSendCallManager();
        tcpRecvCallManager = new TcpRecvCallManager(context);
        tcpRecvCallManager.start();
    }

}