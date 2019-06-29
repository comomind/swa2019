package com.lg.sixsenses.willi.logic.callmanager;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.lg.sixsenses.willi.codec.audio.AbstractAudioCodecFactory;
import com.lg.sixsenses.willi.codec.audio.AudioCodec;
import com.lg.sixsenses.willi.codec.audio.AudioCodecConst;
import com.lg.sixsenses.willi.codec.audio.AudioCodecFactory;
import com.lg.sixsenses.willi.logic.servercommmanager.TcpRecvCallManager;
import com.lg.sixsenses.willi.logic.servercommmanager.TcpSendCallManager;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UdpInfo;
import com.lg.sixsenses.willi.ui.CallStateActivity;
import com.lg.sixsenses.willi.repository.UdpPort;

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
    private CallStateActivity.CallStateActivityHandler handler;

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    private ImageView imageView;

    public CallStateActivity.CallStateActivityHandler getHandler() {
        return handler;
    }

    public void setHandler(CallStateActivity.CallStateActivityHandler handler) {
        this.handler = handler;
    }

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

        AbstractAudioCodecFactory codecFactory = new AudioCodecFactory();
        audioIo.setAudioCodec(codecFactory.getCodec(AudioCodecConst.CodecType.OPUS));
    }


    public void startCc(String phoneNum, ArrayList<UdpPort> portList)
    {
        tcpSendCallManager.startCc(phoneNum, portList);
    }

    public void rejectCc(String phone)
    {
        tcpSendCallManager.rejectCc(phone);
    }

}
