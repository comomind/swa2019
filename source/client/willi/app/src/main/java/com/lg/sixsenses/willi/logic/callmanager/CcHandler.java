package com.lg.sixsenses.willi.logic.callmanager;

import android.content.Context;
import android.util.Log;

import com.lg.sixsenses.willi.codec.audio.AbstractAudioCodecFactory;
import com.lg.sixsenses.willi.codec.audio.AudioCodec;
import com.lg.sixsenses.willi.codec.audio.AudioCodecConst;
import com.lg.sixsenses.willi.codec.audio.AudioCodecFactory;
import com.lg.sixsenses.willi.logic.servercommmanager.TcpRecvCallManager;
import com.lg.sixsenses.willi.logic.servercommmanager.TcpSendCallManager;
import com.lg.sixsenses.willi.net.JitterBuffer;
import com.lg.sixsenses.willi.repository.CcAvInfo;
import com.lg.sixsenses.willi.repository.CcInfo;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UdpInfo;
import com.lg.sixsenses.willi.repository.UdpPort;
import com.lg.sixsenses.willi.ui.CallStateActivity;
import com.lg.sixsenses.willi.ui.CcActivity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


public class CcHandler {
    public static final String TAG = CcHandler.class.getSimpleName();
    private static CcHandler instance = new CcHandler();
    private CcHandler() {
    }
    public static CcHandler getInstance() {
        return instance;
    }

    private final int JITTER_BUFFER_JITTER = 30;
    private final int JITTER_BUFFER_PERIOD = 200;

    private TcpSendCallManager tcpSendCallManager;
    private Context context;

    private HashMap<String, CcAvInfo> ccAvInfoHashMap;
    private AudioPlayer audioPlayer = null;
    private AudioRecorder audioRecorder = null;
    private AudioCodec audioCodec = null;

    private CcInfo thisCc = new CcInfo();
    private ArrayList<UdpPort> portList;


    private int videoSendCount = 0;

    private CcActivity.CcActivityHandler handler;
    private ArrayList<Integer> viewIdList;

    public ArrayList<Integer> getViewId() {
        return viewIdList;
    }

    private ArrayList<UdpPort> sendPortList;
    private int myViewId;

    public int getMyViewId() {
        return myViewId;
    }

    public void setMyViewId(int myViewId) {
        this.myViewId = myViewId;
    }

    public void setViewId(ArrayList<Integer> viewIdList) {
        this.viewIdList = viewIdList;
//        int i = 0;
//        //Map<String, CcAvInfo> map = new HashMap<String, CcAvInfo>();
//        for (Map.Entry<String, CcAvInfo> entry : ccAvInfoHashMap.entrySet())
//        {
//            entry.getValue().getVideoIo().setViewId(viewIdList.get(i));
//            i++;
//        }
    }

    public CcActivity.CcActivityHandler getHandler() {
        return handler;
    }

    public void setHandler(CcActivity.CcActivityHandler handler) {
        this.handler = handler;

//        for (Map.Entry<String, CcAvInfo> entry : ccAvInfoHashMap.entrySet())
//        {
//            entry.getValue().getVideoIo().setHandler(handler);
//        }
    }

    public void setContext(Context context)
    {
        this.context = context;
        audioPlayer.setContext(context);
    }



    // receive CcRequest message from another person
    public void onReceiveCcRequestMsg(ArrayList<UdpPort> list)
    {
        //ArrayList<UdpPort> list = DataManager.getInstance().getCcNewPersonList();
        Log.d(TAG,"onReceiveCcRequestMsg : "+list.toString());
        for(UdpPort udpPort : list)
        {
            CcAvInfo ccAvInfo = ccAvInfoHashMap.get(udpPort.getEmail());
            InetAddress remoteIp;
            try {
                remoteIp = InetAddress.getByName(udpPort.getIp());
                ccAvInfo.setSendVideoPort(udpPort.getVideoPort());
                ccAvInfo.setSendAudioPort(udpPort.getAudioPort());
                if(videoSendCount == 0) {
                    ccAvInfo.getVideoIo().setRealSender(true);
                    ccAvInfo.getVideoIo().setMyViewId(myViewId);
                }
                else {
                    ccAvInfo.getVideoIo().setRealSender(false);
                }
                videoSendCount ++;
                sendPortList.add(udpPort);
                setSendPortListForVideoIo(sendPortList);

                audioRecorder.startRecord();

                //

//                ccAvInfo.getAudioIo().startSend(remoteIp, udpPort.getAudioPort());
                audioRecorder.addSendList(udpPort.getEmail(), remoteIp, udpPort.getAudioPort());

                ccAvInfo.getVideoIo().startSend(remoteIp, udpPort.getVideoPort());
                Log.d(TAG,"Start AV  to  "+udpPort.getEmail()+" / A:"+ccAvInfo.getSendAudioPort()+" / V:"+ ccAvInfo.getSendVideoPort()+" IP:"+udpPort.getIp());
            }
            catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void onReceiveCcRejectMsg(String email)
    {
        Log.d(TAG,"onReceiveCcRejectMsg : "+email);

        audioRecorder.removeSendList(email);

        AudioIo audioIo = ccAvInfoHashMap.get(email).getAudioIo();
        audioIo.stopAll();
        audioRecorder.removeRecorderQueue(email);
        audioPlayer.removeJitterBuffer(email);

        VideoIo videoIo = ccAvInfoHashMap.get(email).getVideoIo();
        videoIo.stopAll();
    }

//    public void callRejectForIncomingCall() {
//        // incoming call request reject (before accepted)
////        tcpRecvCallManager.rejectCall();
////        stopAudioVideo();
//    }
//    public void callRejectForConnectedCall(String phoneNum)
//    {
//        // call reject in call (rejected by me)
////        tcpSendCallManager.rejectPhoneCall(phoneNum);
////        stopAudioVideo();
//    }

    public void startCcHandler()
    {
        ccAvInfoHashMap = new HashMap<String, CcAvInfo>();
        viewIdList = new ArrayList<Integer>();
        sendPortList = new ArrayList<UdpPort>();

        AbstractAudioCodecFactory codecFactory = new AudioCodecFactory();
        audioCodec = codecFactory.getCodec(AudioCodecConst.CodecType.OPUS);

        audioRecorder = new AudioRecorder(audioCodec);
        audioPlayer = new AudioPlayer(audioCodec, audioRecorder.getAudioSessionId());
    }


    public void startCc(String ccNumber)
    {
        tcpSendCallManager = new TcpSendCallManager();
        ArrayList<CcInfo> ccInfoList = DataManager.getInstance().getCcList();
        Log.d(TAG,"Start CC : "+ccNumber+ " ccList :"+ccInfoList.toString());

        for(CcInfo ccInfo:ccInfoList)
        {
            Log.d(TAG,"CC Info : "+ccInfo.toString()+ "ccNumber :"+ccNumber);
            if(ccInfo.getCcNumber().equals(ccNumber))
            {
                thisCc = ccInfo;
                Log.d(TAG,"CC Info : "+thisCc.toString());
                break;
            }
        }

        portList = new ArrayList<UdpPort>();
        ArrayList<String> peopleList = thisCc.getaList();

        Log.d(TAG, "people list = "+peopleList.toString());

        for(int i = 0; i<peopleList.size(); i++)
        {
            String email = peopleList.get(i);

            // for audio
            JitterBuffer jitterBuffer = new JitterBuffer(JITTER_BUFFER_JITTER, JITTER_BUFFER_PERIOD, audioCodec.getSampleRate());
            ConcurrentLinkedQueue<byte[]> recorderQueue = new ConcurrentLinkedQueue<byte[]>();

            audioPlayer.addJitterBuffer(email, jitterBuffer);
            audioRecorder.addRecorderQueue(email, recorderQueue);
            AudioIo audioIo = new AudioIo(context, audioCodec, jitterBuffer, recorderQueue);

            // for video
            VideoIo videoIo = new VideoIo(context);
            videoIo.setHandler(handler);
            videoIo.setViewId(viewIdList.get(i));

            audioPlayer.startPlay();
            int audioPort = startAudioReceive(audioIo, DataManager.getInstance().getMyUdpInfo().getAudioPort()+i);
            int videoPort = startVideoReceive(videoIo, DataManager.getInstance().getMyUdpInfo().getVideoPort()+i);


            CcAvInfo ccAvInfo = new CcAvInfo();
            ccAvInfo.setRecvAudioPort(audioPort);
            ccAvInfo.setRecvVideoPort(videoPort);
            ccAvInfo.setAudioIo(audioIo);
            ccAvInfo.setVideoIo(videoIo);
            ccAvInfoHashMap.put(email,ccAvInfo);

            UdpPort udpPort = new UdpPort();
            udpPort.setEmail(email);
            udpPort.setAudioPort(audioPort);
            udpPort.setVideoPort(videoPort);
            portList.add(udpPort);

            Log.d(TAG, "ccAvInfoHashMap "+ccAvInfo.toString());
        }

        tcpSendCallManager.startCc(ccNumber, portList);
    }

    public void rejectCc(String ccNumber)
    {
        Log.d(TAG, "rejectCC() called");
        ArrayList<String> peopleList = thisCc.getaList();
        if (peopleList != null) {
            Log.d(TAG, "people list = " + peopleList.toString());
        }

        audioRecorder.clearSendList();

        for (CcAvInfo avInfo : ccAvInfoHashMap.values()) {
            AudioIo audioIo = avInfo.getAudioIo();
            VideoIo videoIo = avInfo.getVideoIo();

            audioIo.stopAll();
            videoIo.stopAll();
        }

        audioRecorder.stopRecord();
        audioPlayer.stopPlay();

        audioRecorder.clearRecorderQueue();
        audioPlayer.clearJitterBuffer();

        Log.d(TAG,"Disconnect CC : "+ccNumber);

        tcpSendCallManager.rejectCc(ccNumber);
    }
//
//    private void stopAudioVideo() {
//        audioIo.stopAll();
//        videoIo.stopAll();
//    }


    private int startAudioReceive(AudioIo aIo, int port)
    {
        aIo.startReceive(port);
        return aIo.getMyPort();
    }
    private int startVideoReceive(VideoIo vIo, int port)
    {
        vIo.startReceive(port);
        return vIo.getMyPort();
    }


    private void setSendPortListForVideoIo(ArrayList<UdpPort> portList)
    {
        for (Map.Entry<String, CcAvInfo> entry : ccAvInfoHashMap.entrySet())
        {
            entry.getValue().getVideoIo().setUdpPortList(portList);
        }
    }
}
