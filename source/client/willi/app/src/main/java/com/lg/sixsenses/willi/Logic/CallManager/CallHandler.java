package com.lg.sixsenses.willi.Logic.CallManager;

import android.content.Context;

import com.lg.sixsenses.willi.DataRepository.DataManager;
import com.lg.sixsenses.willi.Logic.ServerCommManager.TcpRecvCallManager;
import com.lg.sixsenses.willi.Logic.ServerCommManager.TcpSendCallManager;

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
    public void callRequest(String phoneNumber)
    {
        tcpSendCallManager.startPhoneCall("1001");
    }

    public void callAccept()
    {
        tcpRecvCallManager.receiveCall();
    }

    public void callRejectforIncomingCall()
    {
        tcpRecvCallManager.rejectCall();
    }
    public void callRejectforConnectedCall(String phoneNum)
    {
        tcpSendCallManager.rejectPhoneCall(phoneNum);
    }

    public void startCallHandler()
    {
        tcpSendCallManager = new TcpSendCallManager();
        tcpRecvCallManager = new TcpRecvCallManager(context);
        tcpRecvCallManager.start();
    }

}
