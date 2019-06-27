package com.lg.sixsenses.willi.logic.callmanager;

import android.content.Context;
import android.util.Log;

import com.lg.sixsenses.willi.codec.audio.AudioCodec;
import com.lg.sixsenses.willi.codec.audio.AudioCodecConst;
import com.lg.sixsenses.willi.codec.audio.AudioCodecFactory;
import com.lg.sixsenses.willi.logic.servercommmanager.TcpRecvCallManager;
import com.lg.sixsenses.willi.logic.servercommmanager.TcpSendCallManager;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UdpInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class CallHandler {
    public static final String TAG = CallHandler.class.getName().toString();
    private static CallHandler instance = new CallHandler();
    public static CallHandler getInstance() {
        return instance;
    }

    static {
        System.loadLibrary("native-lib");
    }

    private TcpRecvCallManager tcpRecvCallManager;
    private TcpSendCallManager tcpSendCallManager;
    private Context context;

    private AudioIo audioIo;

    public void setContext(Context context)
    {
        this.context = context;
    }

    // accept incoming call by callee
    public void callAccept() {
        tcpRecvCallManager.receiveCall();

        audioIo.startReceive(DataManager.getInstance().getMyUdpInfo().getAudioPort());
        ArrayList<UdpInfo> peerInfos = DataManager.getInstance().getPeerUdpInfoList();
        // TODO: improve this
        UdpInfo peerInfo = peerInfos.get(0);
        InetAddress remoteIp;

        try {
            remoteIp = InetAddress.getByName(peerInfo.getIpaddr());
            audioIo.startSend(remoteIp, peerInfo.getAudioPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    // call request by caller
    public void callRequest(String phoneNumber) {
        tcpSendCallManager.startPhoneCall(phoneNumber);
        audioIo.startReceive(DataManager.getInstance().getMyUdpInfo().getAudioPort());
    }

    // receive call accept message from server in caller
    public void onReceiveCallAcceptMessage()
    {
        Log.d(TAG,"onReceiveCallAcceptMessage");

        ArrayList<UdpInfo> peerInfos = DataManager.getInstance().getPeerUdpInfoList();
        // TODO: improve this
        UdpInfo peerInfo = peerInfos.get(0);
        InetAddress remoteIp;

        try {
            remoteIp = InetAddress.getByName(peerInfo.getIpaddr());
            audioIo.startSend(remoteIp, peerInfo.getAudioPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void onReceiveCallRejectMessage()
    {
        Log.d(TAG,"onReceiveCallRejectMessage");
        audioIo.stopAll();
    }

    public void callRejectForIncomingCall() {
        // incoming call request reject (before accepted)
        tcpRecvCallManager.rejectCall();
        audioIo.stopAll();
    }
    public void callRejectForConnectedCall(String phoneNum)
    {
        // call reject in call (rejected by me)
        tcpSendCallManager.rejectPhoneCall(phoneNum);
        audioIo.stopAll();
    }

    public void startCallHandler()
    {
        tcpSendCallManager = new TcpSendCallManager();
        tcpRecvCallManager = new TcpRecvCallManager(context);
        tcpRecvCallManager.start();

        audioIo = new AudioIo(context);
        // TODO: (REMOVE THIS) set hard coded codec
        AudioCodec codec = AudioCodecFactory.getCodec(AudioCodecConst.CodecType.OPUS);
        audioIo.setAudioCodec(codec);

    }

}
