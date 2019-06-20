package com.lg.sixsenses.willi.Logic.ServerCommManager;

import android.util.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lg.sixsenses.willi.DataRepository.ConstantsWilli;
import com.lg.sixsenses.willi.DataRepository.DataManager;
import com.lg.sixsenses.willi.Logic.CallManager.CallStateMachine;
import com.lg.sixsenses.willi.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class TcpSendCallManager {

    public static final String TAG = TcpSendCallManager.class.getName().toString();


    public void startPhoneCall(String phoneNum) {
        class StartPhoneCallThread extends Thread {
            String phoneNum;

            StartPhoneCallThread(String num) {
                phoneNum = num;
            }

            public void run() {
                try {
                    TcpCallSignalRequest callSignal = new TcpCallSignalRequest();

                    TcpCallSignalHeader header = new TcpCallSignalHeader();
                    header.setType("E");
                    header.setToken(DataManager.getInstance().getToken());
                    header.setIpaddr(Util.getIPAddress());
                    header.setTrantype("SYNC");
                    header.setSvcid("tcpCallService");
                    header.setReqtype(1);
                    header.setSvctype(1);

                    TcpCallSignalBody body = new TcpCallSignalBody();
                    body.setCmd("CallRequestC2S");
                    body.setType(ConstantsWilli.CALL_REQUEST_BODY_TYPE_AUDIO);
                    body.setCalleePhoneNum(phoneNum);
                    body.setCallerPhoneNum(DataManager.getInstance().getMyInfo().getPhoneNum());
                    body.setIpaddr(Util.getIPAddress());

                    int pNum = Integer.parseInt(DataManager.getInstance().getMyInfo().getPhoneNum());
                    body.setUdpAudioPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_AUDIO_PORT);
                    body.setUdpVideoPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_VIDEO_PORT);

                    callSignal.setHeader(header);
                    callSignal.setBody(body);

                    Log.d(TAG, "TCP ServerIP : " + ConstantsWilli.SERVER_IP + " Port : " + ConstantsWilli.SERVER_TCP_PORT);
                    Socket socket = new Socket(ConstantsWilli.SERVER_IP, ConstantsWilli.SERVER_TCP_PORT);

                    ObjectMapper mapper = new ObjectMapper();
                    String input = mapper.writeValueAsString(callSignal);


                    Log.d(TAG, "TCP ClientSocket Send : " + input);

                    //input = input + "\r\n";
                    input = input + "@@";

                    OutputStream os = socket.getOutputStream();
                    os.write(input.getBytes());
                    os.flush();
                    DataManager.getInstance().setCallerPhoneNum(DataManager.getInstance().getMyInfo().getPhoneNum());
                    DataManager.getInstance().setCalleePhoneNum(phoneNum);
                    CallStateMachine.getInstance().sendCallRequest();

                    String output;

                    StringBuffer buf = new StringBuffer();
                    InputStream streamIn = socket.getInputStream();
                    String response = "";

                    int data = 0;
                    int count = 0;
                    char mark = '@';

                    while (count < 2) {
                        data = streamIn.read();
                        char ch = (char) data;
                        buf.append(ch);
                        if (ch == mark)
                            count++;
                    }

                    String result = buf.toString();

                    if (result != null && result.length() > 2)
                        response = result.substring(0, result.length() - 2);
                    else
                        response = result;

                    //Log.d(TAG, "TCP ClientSocket Recved : " + response);

                    ObjectMapper mapper2 = new ObjectMapper();
                    TypeReference ref = new TypeReference<TcpCallSignalReceive<TcpCallSignalBody>>() {
                    };

                    TcpCallSignalReceive receive = mapper2.readValue(response, ref);
                    TcpCallSignalHeader recvHeader = receive.getHeader();
                    TcpCallSignalBody recvBody = (TcpCallSignalBody) (receive.getBody());

                    Log.d(TAG, "TCP ClientSocket Recv Header : " + recvHeader.toString());
                    Log.d(TAG, "TCP ClientSocket Recv Body : " + recvBody.toString());


                    if (recvBody.getCmd().equals("CallAcceptS2C")) {
                        CallStateMachine.getInstance().recvCallAccept();
                        DataManager.getInstance().setCallId(recvBody.getCallId());
                    } else if (recvBody.getCmd().equals("CallRejectS2C"))
                        CallStateMachine.getInstance().recvCallReject();

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //AsyncTask.execute(new MyRunnable(phoneNum));
        StartPhoneCallThread th = new StartPhoneCallThread(phoneNum);
        th.start();
    }

    // 전화 통화하고 있는 중에 전화를 끊는 상황
    public void rejectPhoneCall(String phoneNum) {
        class RejectPhoneCallThread extends Thread {
            String phoneNum;

            RejectPhoneCallThread(String num) {
                phoneNum = num;
            }

            @Override
            public void run() {
                try {
                    Log.d(TAG, "Start Run");
                    TcpCallSignalRequest callSignal = new TcpCallSignalRequest();

                    TcpCallSignalHeader header = new TcpCallSignalHeader();
                    header.setType("E");
                    header.setToken(DataManager.getInstance().getToken());
                    header.setIpaddr(Util.getIPAddress());
                    header.setTrantype("SYNC");
                    header.setSvcid("tcpCallReject");
                    header.setReqtype(1);
                    header.setSvctype(1);

                    TcpCallSignalBody body = new TcpCallSignalBody();
                    body.setCmd("CallRejectC2S");
                    body.setType(ConstantsWilli.CALL_REQUEST_BODY_TYPE_AUDIO);
                    body.setCalleePhoneNum(DataManager.getInstance().getCalleePhoneNum());
                    body.setCallerPhoneNum(DataManager.getInstance().getCallerPhoneNum());
                    body.setIpaddr(Util.getIPAddress());
                    body.setCallId(DataManager.getInstance().getCallId());

                    //                    int pNum = Integer.parseInt(DataManager.getInstance().getMyInfo().getPhoneNum());
                    //                    body.setUdpAudioPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_AUDIO_PORT);
                    //                    body.setUdpVideoPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_VIDEO_PORT);

                    callSignal.setHeader(header);
                    callSignal.setBody(body);

                    Log.d(TAG, "TCP ServerIP : " + ConstantsWilli.SERVER_IP + " Port : " + ConstantsWilli.SERVER_TCP_PORT);
                    Socket socket = new Socket(ConstantsWilli.SERVER_IP, ConstantsWilli.SERVER_TCP_PORT);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    ObjectMapper mapper = new ObjectMapper();
                    String input = mapper.writeValueAsString(callSignal);


                    Log.d(TAG, "TCP ClientSocket Send : " + input);

                    //input = input + "\r\n";
                    input = input + "@@";

                    OutputStream os = socket.getOutputStream();
                    os.write(input.getBytes());
                    os.flush();

                    CallStateMachine.getInstance().sendCallReject();

                    String output;
                    //Log.d(TAG, "TCP Respose from Server .... \n");

                    StringBuffer buf = new StringBuffer();
                    InputStream streamIn = socket.getInputStream();
                    String response = "";

                    int data = 0;
                    int count = 0;
                    char mark = '@';

                    while (count < 2) {
                        data = streamIn.read();
                        char ch = (char) data;
                        buf.append(ch);
                        if (ch == mark)
                            count++;
                    }

                    String result = buf.toString();

                    if (result != null && result.length() > 2)
                        response = result.substring(0, result.length() - 2);
                    else
                        response = result;

                    ObjectMapper mapper2 = new ObjectMapper();
                    TypeReference ref = new TypeReference<TcpCallSignalReceive<TcpCallSignalBody>>() {
                    };

                    TcpCallSignalReceive receive = mapper2.readValue(response, ref);
                    TcpCallSignalHeader recvHeader = receive.getHeader();
                    TcpCallSignalBody recvBody = (TcpCallSignalBody) (receive.getBody());

                    Log.d(TAG, "TCP ClientSocket Recv Header : " + recvHeader.toString());
                    Log.d(TAG, "TCP ClientSocket Recv Body : " + recvBody.toString());

                    Log.d(TAG, "Call Response" + recvBody.getCmd());

                    if (recvBody.getCmd().equals("CallAcceptS2C"))
                        CallStateMachine.getInstance().recvCallAccept();
                    else if (recvBody.getCmd().equals("CallRejectS2C"))
                        CallStateMachine.getInstance().recvCallReject();

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        RejectPhoneCallThread th = new RejectPhoneCallThread(phoneNum);
        th.start();
    }


}
