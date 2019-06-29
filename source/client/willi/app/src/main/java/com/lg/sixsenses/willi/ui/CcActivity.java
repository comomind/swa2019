package com.lg.sixsenses.willi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.codec.audio.AbstractAudioCodecFactory;
import com.lg.sixsenses.willi.codec.audio.AudioCodecConst;
import com.lg.sixsenses.willi.codec.audio.AudioCodecFactory;
import com.lg.sixsenses.willi.logic.callmanager.AudioIo;
import com.lg.sixsenses.willi.logic.callmanager.CallHandler;
import com.lg.sixsenses.willi.logic.callmanager.VideoIo;
import com.lg.sixsenses.willi.repository.CcInfo;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UdpInfo;
import com.lg.sixsenses.willi.repository.UdpPort;

import java.util.ArrayList;

public class CcActivity extends AppCompatActivity {
    public static final String TAG = CcActivity.class.getName().toString();

    ArrayList<AudioIo> audioIoList;
    ArrayList<VideoIo> videoIoList;
    ArrayList<Integer> audioPortList;
    ArrayList<Integer> videoPortList;

    String ccNumber;
    CcInfo thisCc;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cc);

        audioIoList = new ArrayList<AudioIo>();
        videoIoList = new ArrayList<VideoIo>();
        audioPortList = new ArrayList<Integer>();
        videoPortList = new ArrayList<Integer>();

        Intent intent = getIntent();
        ccNumber = intent.getStringExtra("ccNumber");

        ArrayList<CcInfo> ccInfoList = DataManager.getInstance().getCcList();
        for(CcInfo ccInfo:ccInfoList)
        {
            if(ccInfo.getCcNumber() == ccNumber)
            {
                thisCc = ccInfo;
                Log.d(TAG,"CC Info : "+thisCc.toString());
                break;
            }
            //for test
            thisCc = ccInfo;
            ccNumber = thisCc.getCcNumber();
        }

    }

    public void buttonCcStartClick(View view)
    {
        ArrayList<UdpPort> portList = new ArrayList<UdpPort>();
        ArrayList<String> peopleList = thisCc.getaList();

        Log.d(TAG, "people list = "+peopleList.toString());

        for(int i = 0; i<peopleList.size(); i++)
        {
            String email = peopleList.get(i);
            AudioIo audioIo = new AudioIo(this);
            VideoIo videoIo = new VideoIo(this);

            AbstractAudioCodecFactory codecFactory = new AudioCodecFactory();
            audioIo.setAudioCodec(codecFactory.getCodec(AudioCodecConst.CodecType.OPUS));

            int audioPort = startAudioReceive(audioIo, DataManager.getInstance().getMyUdpInfo().getAudioPort()+i);
            int videoPort = startVideoReceive(videoIo, DataManager.getInstance().getMyUdpInfo().getVideoPort()+i);

            UdpPort udpPort = new UdpPort();
            udpPort.setEmail(email);
            udpPort.setAudioPort(audioPort);
            udpPort.setVideoPort(videoPort);
            portList.add(udpPort);

            audioIoList.add(audioIo);
            videoIoList.add(videoIo);
            audioPortList.add(audioPort);
            videoPortList.add(videoPort);
        }
        Log.d(TAG, "PortList : "+portList.toString());
        CallHandler.getInstance().startCc(ccNumber, portList);
    }

    public void buttonCcDisconnectClick(View view)
    {
        ArrayList<UdpPort> portList = new ArrayList<UdpPort>();
        ArrayList<String> peopleList = thisCc.getaList();

        Log.d(TAG, "people list = "+peopleList.toString());

        for(int i = 0; i<peopleList.size(); i++)
        {
            AudioIo audioIo = audioIoList.get(0);
            audioIo.stopAll();

            VideoIo videoIo = videoIoList.get(0);
            videoIo.stopAll();
        }
        Log.d(TAG,"Disconnect CC : "+ccNumber);
        CallHandler.getInstance().rejectCc(ccNumber);
    }



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


}
