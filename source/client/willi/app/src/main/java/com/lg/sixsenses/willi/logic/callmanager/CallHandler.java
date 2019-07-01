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
    public static final String TAG = CallHandler.class.getSimpleName();
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
    private VideoIo videoIo;
    private CallStateActivity.CallStateActivityHandler handler;
    private int viewId;

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
        videoIo.setViewId(viewId);
    }

    public CallStateActivity.CallStateActivityHandler getHandler() {
        return handler;
    }

    public void setHandler(CallStateActivity.CallStateActivityHandler handler) {
        this.handler = handler;
        videoIo.setHandler(handler);
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    // accept incoming call by callee
    public void callAccept() {
        tcpRecvCallManager.receiveCall();

        audioIo.startReceive(DataManager.getInstance().getMyUdpInfo().getAudioPort());
        videoIo.startReceive(DataManager.getInstance().getMyUdpInfo().getVideoPort());

        ArrayList<UdpInfo> peerInfos = DataManager.getInstance().getPeerUdpInfoList();
        // TODO: improve this
        UdpInfo peerInfo = peerInfos.get(0);
        InetAddress remoteIp;

        try {
            remoteIp = InetAddress.getByName(peerInfo.getIpaddr());
            audioIo.startSend(remoteIp, peerInfo.getAudioPort());

            ArrayList<UdpPort> list = new ArrayList<UdpPort>();
            UdpPort port = new UdpPort();
            port.setVideoPort(peerInfo.getVideoPort());
            port.setAudioPort(peerInfo.getAudioPort());
            port.setIp(peerInfo.getIpaddr());
            list.add(port);

            videoIo.setUdpPortList(list);
            videoIo.startSend(remoteIp, peerInfo.getVideoPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    // call request by caller
    public void callRequest(String phoneNumber) {
        tcpSendCallManager.startPhoneCall(phoneNumber);

        audioIo.startReceive(DataManager.getInstance().getMyUdpInfo().getAudioPort());
        videoIo.startReceive(DataManager.getInstance().getMyUdpInfo().getVideoPort());
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

            ArrayList<UdpPort> list = new ArrayList<UdpPort>();
            UdpPort port = new UdpPort();
            port.setVideoPort(peerInfo.getVideoPort());
            port.setAudioPort(peerInfo.getAudioPort());
            port.setIp(peerInfo.getIpaddr());
            list.add(port);

            videoIo.setUdpPortList(list);
            videoIo.startSend(remoteIp, peerInfo.getVideoPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void onReceiveCallRejectMessage()
    {
        Log.d(TAG,"onReceiveCallRejectMessage");
        stopAudioVideo();
    }

    public void callRejectForIncomingCall() {
        // incoming call request reject (before accepted)
        tcpRecvCallManager.rejectCall();
        stopAudioVideo();
    }
    public void callRejectForConnectedCall(String phoneNum)
    {
        // call reject in call (rejected by me)
        tcpSendCallManager.rejectPhoneCall(phoneNum);
        stopAudioVideo();
    }

    public void startCallHandler()
    {
        tcpSendCallManager = new TcpSendCallManager();
        tcpRecvCallManager = new TcpRecvCallManager(context);
        tcpRecvCallManager.start();

        // create audio
        audioIo = new AudioIo(context);
        AbstractAudioCodecFactory codecFactory = new AudioCodecFactory();
        audioIo.setAudioCodec(codecFactory.getCodec(AudioCodecConst.CodecType.OPUS));

        // create video
        videoIo = new VideoIo(context);
        videoIo.setRealSender(true);
    }


    public void startCc(String phoneNum, ArrayList<UdpPort> portList)
    {
        tcpSendCallManager.startCc(phoneNum, portList);
    }

    public void rejectCc(String phone)
    {
        tcpSendCallManager.rejectCc(phone);
    }

    private void stopAudioVideo() {
        audioIo.stopAll();
        videoIo.stopAll();
    }
}
