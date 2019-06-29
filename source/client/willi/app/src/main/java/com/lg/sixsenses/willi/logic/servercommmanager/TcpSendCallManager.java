package com.lg.sixsenses.willi.logic.servercommmanager;

import android.util.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lg.sixsenses.willi.logic.callmanager.CallHandler;
import com.lg.sixsenses.willi.repository.ConstantsWilli;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.logic.callmanager.CallStateMachine;
import com.lg.sixsenses.willi.repository.UdpInfo;
import com.lg.sixsenses.willi.repository.UdpPort;
import com.lg.sixsenses.willi.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

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
                    body.setUdpAudioPort(DataManager.getInstance().getMyUdpInfo().getAudioPort());
                    body.setUdpVideoPort(DataManager.getInstance().getMyUdpInfo().getVideoPort());

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
                        else count = 0;
                    }

                    String result = buf.toString();

                    if (result != null && result.length() > 2)
                        response = result.substring(0, result.length() - 2);
                    else
                        response = result;

                    Log.d(TAG, "TCP ClientSocket Recved : " + response);

                    ObjectMapper mapper2 = new ObjectMapper();
                    TypeReference ref = new TypeReference<TcpCallSignalReceive<TcpCallSignalBody>>() {
                    };

                    TcpCallSignalReceive receive = mapper2.readValue(response, ref);
                    TcpCallSignalHeader recvHeader = receive.getHeader();
                    TcpCallSignalBody recvBody = (TcpCallSignalBody) (receive.getBody());

                    //Log.d(TAG, "TCP ClientSocket Recv Header : " + recvHeader.toString());
                    //Log.d(TAG, "TCP ClientSocket Recv Body : " + recvBody.toString());


                    if (recvBody.getCmd().equals("CallAcceptS2C")) {
                        CallStateMachine.getInstance().recvCallAccept();
                        UdpInfo callerUdpInfo = new UdpInfo();
                        callerUdpInfo.setIpaddr(recvBody.getIpaddr());
                        callerUdpInfo.setAudioPort(recvBody.getUdpAudioPort());
                        callerUdpInfo.setVideoPort(recvBody.getUdpVideoPort());
                        ArrayList<UdpInfo> list = new ArrayList<UdpInfo>();
                        list.add(callerUdpInfo);
                        DataManager.getInstance().setPeerUdpInfoList(list);
                        DataManager.getInstance().setCallId(recvBody.getCallId());
                        CallHandler.getInstance().onReceiveCallAcceptMessage();

                    } else if (recvBody.getCmd().equals("CallRejectS2C") ||
                            recvBody.getCmd().equals("CallFailS2C")) {
                        CallStateMachine.getInstance().recvCallReject();
                        CallHandler.getInstance().onReceiveCallRejectMessage();
                    }
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

//                    if (recvBody.getCmd().equals("CallAcceptS2C"))
//                        // TODO: parse body & get UDP port
//
//                        CallStateMachine.getInstance().recvCallAccept();
                    if (recvBody.getCmd().equals("CallRejectS2C"))
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


    public void startCc(String phoneNum, ArrayList<UdpPort> portList) {
        class StartCcThread extends Thread {
            String phoneNum;
            ArrayList<UdpPort> portList;

            StartCcThread(String num, ArrayList<UdpPort> list) {
                phoneNum = num;
                portList = list;
            }

            public void run() {
                try {
                    TcpCcSignalMessage ccMessage = new TcpCcSignalMessage();

                    TcpCallSignalHeader header = new TcpCallSignalHeader();
                    header.setType("C");
                    header.setToken(DataManager.getInstance().getToken());
                    header.setIpaddr(Util.getIPAddress());
                    header.setTrantype("SYNC");
                    header.setSvcid("tcpConferenceService");
                    header.setReqtype(1);
                    header.setSvctype(3);


                    TcpCcSignalBody body = new TcpCcSignalBody();
                    body.setCmd("CcRequestC2S");
                    body.setType(ConstantsWilli.CALL_REQUEST_BODY_TYPE_CC);
                    body.setCcNumber(phoneNum);
                    body.setList(portList);

                    ccMessage.setHeader(header);
                    ccMessage.setBody(body);

                    //Log.d(TAG, "TCP ServerIP : " + ConstantsWilli.SERVER_IP + " Port : " + ConstantsWilli.SERVER_TCP_PORT);
                    Socket socket = new Socket(ConstantsWilli.SERVER_IP, ConstantsWilli.SERVER_TCP_PORT);

                    ObjectMapper mapper = new ObjectMapper();
                    String input = mapper.writeValueAsString(ccMessage);


                    Log.d(TAG, "TCP ClientSocket CC Send ({"+DataManager.getInstance().getMyInfo().getEmail()+") : " + input);

                    //input = input + "\r\n";
                    input = input + "@@";

                    OutputStream os = socket.getOutputStream();
                    os.write(input.getBytes());
                    os.flush();
                    // TO DO : CC StateMachine
                    //CallStateMachine.getInstance().sendCallRequest();

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
                        else count = 0;
                    }

                    String result = buf.toString();

                    if (result != null && result.length() > 2)
                        response = result.substring(0, result.length() - 2);
                    else
                        response = result;

                    Log.d(TAG, "TCP ClientSocket CC Recved : " + response);

                    ObjectMapper mapper2 = new ObjectMapper();
                    TypeReference ref = new TypeReference<TcpCcSignalMessage>() {
                    };

                    TcpCcSignalMessage receive = mapper2.readValue(response, ref);
                    TcpCallSignalHeader recvHeader = receive.getHeader();
                    TcpCcSignalBody recvBody = (TcpCcSignalBody) (receive.getBody());

                    if (recvBody.getCmd().equals("CcAcceptS2C")) {
                        // TO DO : CC StateMachine   recvCCAccept???
                        // CallStateMachine.getInstance().recvCallAccept();
                        ArrayList<UdpPort> udpPortList = (ArrayList<UdpPort>) recvBody.getList();

                        ArrayList<UdpInfo> list = new ArrayList<UdpInfo>();
                        for (UdpPort port : udpPortList) {
                            UdpInfo info = new UdpInfo();
                            info.setIpaddr(port.getIp());
                            info.setAudioPort(port.getAudioPort());
                            info.setVideoPort(port.getVideoPort());
                            list.add(info);
                        }
                        DataManager.getInstance().setPeerUdpInfoList(list);
                        Log.d(TAG,"PeerUdpInfo : "+ list.toString());
                        // TO DO : CC StateMachine & onReceive
                        // CallHandler.getInstance().onReceiveCallAcceptMessage();

                    } else if (recvBody.getCmd().equals("CcRejectS2C")) {
                        // TO DO : CC StateMachine & onReceive
                        //CallStateMachine.getInstance().recvCallReject();
                        //CallHandler.getInstance().onReceiveCallRejectMessage();
                    }
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        //AsyncTask.execute(new MyRunnable(phoneNum));
        StartCcThread th = new StartCcThread(phoneNum, portList);
        th.start();
    }



    public void rejectCc(String phoneNum) {
        class RejectCcThread extends Thread {
            String phoneNum;

            RejectCcThread(String num) {
                phoneNum = num;
            }

            @Override
            public void run() {
                try {
                    TcpCcSignalMessage callSignal = new TcpCcSignalMessage();

                    TcpCallSignalHeader header = new TcpCallSignalHeader();
                    header.setType("E");
                    header.setToken(DataManager.getInstance().getToken());
                    header.setIpaddr(Util.getIPAddress());
                    header.setTrantype("SYNC");
                    header.setSvcid("tcpConferenceReject");
                    header.setReqtype(1);
                    header.setSvctype(3);

                    TcpCcSignalBody body = new TcpCcSignalBody();
                    body.setCmd("CallRejectC2S");
                    body.setType(ConstantsWilli.CALL_REQUEST_BODY_TYPE_CC);
                    body.setRejecter(DataManager.getInstance().getMyInfo().getEmail());


                    callSignal.setHeader(header);
                    callSignal.setBody(body);

                    Socket socket = new Socket(ConstantsWilli.SERVER_IP, ConstantsWilli.SERVER_TCP_PORT);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    ObjectMapper mapper = new ObjectMapper();
                    String input = mapper.writeValueAsString(callSignal);


                    Log.d(TAG, "TCP ClientSocket CC Send : " + input);

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

                    Log.d(TAG, "TCP ClientSocket CC Recv Header : " + recvHeader.toString());
                    Log.d(TAG, "TCP ClientSocket CC Recv Body : " + recvBody.toString());

                    Log.d(TAG, "Call Response : " + recvBody.getCmd());

//
//
//                    if (recvBody.getCmd().equals("CallRejectS2C"))
//                        CallStateMachine.getInstance().recvCallReject();

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        RejectCcThread th = new RejectCcThread(phoneNum);
        th.start();

    }

}