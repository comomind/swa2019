package com.lg.sixsenses.willi.Logic.ServerCommManager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lg.sixsenses.willi.DataRepository.ConstantsWilli;
import com.lg.sixsenses.willi.DataRepository.DataManager;
import com.lg.sixsenses.willi.Logic.CallManager.CallStateMachine;
import com.lg.sixsenses.willi.UserInterface.CallStateActivity;
import com.lg.sixsenses.willi.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class TcpRecvCallManager {
    public static final String TAG = TcpRecvCallManager.class.getName().toString();
    private Context context;
    private Socket firstRecvSocket;

    public TcpRecvCallManager(Context context)
    {
        this.context =context;
    }

    private class SocketServerThread extends Thread {
        @Override
        public void run() {
            try {
                // 서버소켓 생성

                Log.d(TAG, "ServerSocket Thread ... run()");
                ServerSocket serverSocket = new ServerSocket(ConstantsWilli.CLIENT_TCP_CALL_SIGNAL_PORT);

                // 소켓서버가 종료될때까지 무한루프
                while (true) {
                    // 소켓 접속 요청이 올때까지 대기합니다.
                    try {
                        Socket socket = serverSocket.accept();

                        Log.d(TAG, "ServerSocket New Connection Accepted ... ");

                        // 응답을 위해 스트림을 얻어옵니다.
                        InputStream streamIn = socket.getInputStream();

                        int data = 0;
                        StringBuffer buf = new StringBuffer();
                        char stop = '@';
                        int count = 0;

                        while (count < 2) {
                            data = streamIn.read();
                            char ch = (char) data;
                            buf.append(ch);

                            if (ch == stop)
                                count++;
                        }

                        Log.d(TAG, "Client ServerSocket : " + buf.toString());

                        ObjectMapper mapper2 = new ObjectMapper();
                        TypeReference ref = new TypeReference<TcpCallSignalReceive<TcpCallSignalBody>>() {
                        };

                        TcpCallSignalReceive receive = mapper2.readValue(buf.toString(), ref);
                        TcpCallSignalHeader recvHeader = receive.getHeader();
                        TcpCallSignalBody recvBody = (TcpCallSignalBody) (receive.getBody());

//                        Log.d(TAG, "TCP ServerSocket Recv Header : " + recvHeader.toString());
//                        Log.d(TAG, "TCP ServerSocket Recv Body : " + recvBody.toString());

                        if(recvHeader.getType().equals("H")) {
                            TcpCallSignalRequest callSignal = new TcpCallSignalRequest();

                            TcpCallSignalHeader header = new TcpCallSignalHeader();
                            header.setType("H");
                            header.setToken(DataManager.getInstance().getToken());
                            header.setIpaddr(Util.getIPAddress());
                            header.setTrantype("SYNC");
                            header.setReqtype(1);
                            header.setSvctype(1);
                            callSignal.setHeader(header);

                            ObjectMapper mapper = new ObjectMapper();
                            String input = mapper.writeValueAsString(callSignal);

                            Log.d(TAG, "TCP ServerSocket Response for HealthCheck : " + input);

                            input = input + "@@";

                            OutputStream streamOut1 = socket.getOutputStream();
                            streamOut1.write(input.getBytes());
                            streamOut1.flush();


                            streamOut1.close();
                            socket.close();
                            Log.d(TAG, "Socket Closed 1");


                        } else if (recvBody.getCmd().equals("CallRequestS2C"))
                        {
                            CallStateMachine.getInstance().recvCallRequest();
                            DataManager.getInstance().setCallerPhoneNum(recvBody.getCallerPhoneNum());
                            DataManager.getInstance().setCalleePhoneNum(DataManager.getInstance().getMyInfo().getPhoneNum());
                            DataManager.getInstance().setCallId(recvBody.getCallId());
                            //Log.d(TAG, "SetCallID : " + recvBody.getCallId());

                            Log.d(TAG, "Client Sersocket SAVED!!!!!!!!!!!!!!!!!!!!!!1");
                            firstRecvSocket = socket;
                            Intent intent = new Intent(context.getApplicationContext(), CallStateActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        } else if (recvBody.getCmd().equals("CallRejectS2C")) {
                            CallStateMachine.getInstance().recvCallReject();
                            // CallReject 명령을 통화중/Ringing 에 받는 경우..
                            // 서버 에게 확인의 의미로 CallRejectC2S를 보낸다
                            TcpCallSignalRequest callSignal = new TcpCallSignalRequest();

                            TcpCallSignalHeader header = new TcpCallSignalHeader();
                            header.setType("R");
                            header.setToken(DataManager.getInstance().getToken());
                            header.setIpaddr(Util.getIPAddress());
                            header.setTrantype("SYNC");
                            header.setReqtype(1);
                            header.setSvctype(1);


                            TcpCallSignalBody body = new TcpCallSignalBody();
                            body.setCmd("CallRejectC2S");
                            body.setType(ConstantsWilli.CALL_REQUEST_BODY_TYPE_AUDIO);
                            body.setCalleePhoneNum(DataManager.getInstance().getCalleePhoneNum());
                            body.setCallerPhoneNum(DataManager.getInstance().getCallerPhoneNum());
                            body.setCallId(DataManager.getInstance().getCallId());
                            //                    int pNum = Integer.parseInt(DataManager.getInstance().getMyInfo().getPhoneNum());
                            //                    body.setUdpAudioPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_AUDIO_PORT);
                            //                    body.setUdpVideoPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_VIDEO_PORT);
                            //                    body.setIpaddr(Util.getIPAddress());
                            callSignal.setHeader(header);
                            callSignal.setBody(body);

                            ObjectMapper mapper = new ObjectMapper();
                            String input = mapper.writeValueAsString(callSignal);


                            Log.d(TAG, "TCP ServerSocket Response for CallReject : " + input);

                            //input = input + "\r\n";
                            input = input + "@@";


                            OutputStream streamOut1 = socket.getOutputStream();
                            streamOut1.write(input.getBytes());
                            streamOut1.flush();


                            streamOut1.close();
                            socket.close();
                            Log.d(TAG, "Socket Closed 2");

                            if (firstRecvSocket != null) {
                                OutputStream streamOut2 = firstRecvSocket.getOutputStream();
                                streamOut2.write(input.getBytes());
                                streamOut2.flush();
                                streamOut2.close();
                                firstRecvSocket.close();
                                firstRecvSocket = null;
                                Log.d(TAG, "Socket Closed 3");
                            }

                            CallStateMachine.getInstance().sendCallReject();
                        }
                        else if (recvBody.getCmd().equals("CallFailS2C")) {
                            CallStateMachine.getInstance().recvCallReject();
                            // CallFail 명령을 받는 경우..
                            // 1. Caller가 전화를 하였는데, Callee가 전화를 오랴 안받아 CallFail을 받는 경우
                            // 2. Callee가 미쳐 전화를 못받았는데, 서버 타임아웃으로 CallFail을 받는 경우
                            TcpCallSignalRequest callSignal = new TcpCallSignalRequest();

                            TcpCallSignalHeader header = new TcpCallSignalHeader();
                            header.setType("R");
                            header.setToken(DataManager.getInstance().getToken());
                            header.setIpaddr(Util.getIPAddress());
                            header.setTrantype("SYNC");
                            header.setReqtype(1);
                            header.setSvctype(1);


                            TcpCallSignalBody body = new TcpCallSignalBody();
                            body.setCmd("CallFailC2S");
                            body.setType(ConstantsWilli.CALL_REQUEST_BODY_TYPE_AUDIO);
                            body.setCalleePhoneNum(DataManager.getInstance().getCalleePhoneNum());
                            body.setCallerPhoneNum(DataManager.getInstance().getCallerPhoneNum());
                            body.setCallId(DataManager.getInstance().getCallId());
                            //                    int pNum = Integer.parseInt(DataManager.getInstance().getMyInfo().getPhoneNum());
                            //                    body.setUdpAudioPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_AUDIO_PORT);
                            //                    body.setUdpVideoPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_VIDEO_PORT);
                            //                    body.setIpaddr(Util.getIPAddress());
                            callSignal.setHeader(header);
                            callSignal.setBody(body);

                            ObjectMapper mapper = new ObjectMapper();
                            String input = mapper.writeValueAsString(callSignal);


                            Log.d(TAG, "TCP ServerSocket Response for CallFail : " + input);

                            //input = input + "\r\n";
                            input = input + "@@";

                            OutputStream streamOut1 = socket.getOutputStream();
                            streamOut1.write(input.getBytes());
                            streamOut1.flush();


                            streamOut1.close();
                            socket.close();
                            Log.d(TAG, "Socket Closed 4");

                            if (firstRecvSocket != null) {
                                OutputStream streamOut2 = firstRecvSocket.getOutputStream();
                                streamOut2.write(input.getBytes());
                                streamOut2.flush();
                                streamOut2.close();
                                firstRecvSocket.close();
                                firstRecvSocket = null;
                                Log.d(TAG, "Socket Closed 5");
                            }

                            CallStateMachine.getInstance().sendCallReject();
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void start()
    {
        SocketServerThread serverThread = new SocketServerThread();
        serverThread.start();
    }

    // 전화 받기
    public void receiveCall()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TcpCallSignalRequest callSignal = new TcpCallSignalRequest();

                    TcpCallSignalHeader header = new TcpCallSignalHeader();
                    header.setType("R");
                    header.setToken(DataManager.getInstance().getToken());
                    header.setIpaddr(Util.getIPAddress());
                    header.setTrantype("SYNC");
                    header.setSvcid("tcpCallService");
                    header.setReqtype(1);
                    header.setSvctype(1);


                    TcpCallSignalBody body = new TcpCallSignalBody();
                    body.setCmd("CallAcceptC2S");
                    body.setType(ConstantsWilli.CALL_REQUEST_BODY_TYPE_AUDIO);
                    body.setCalleePhoneNum(DataManager.getInstance().getCalleePhoneNum());
                    body.setCallerPhoneNum(DataManager.getInstance().getCallerPhoneNum());
                    int pNum = Integer.parseInt(DataManager.getInstance().getMyInfo().getPhoneNum());
                    body.setUdpAudioPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_AUDIO_PORT);
                    body.setUdpVideoPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_VIDEO_PORT);
                    body.setIpaddr(Util.getIPAddress());
                    body.setCallId(DataManager.getInstance().getCallId());

                    callSignal.setHeader(header);
                    callSignal.setBody(body);

                    ObjectMapper mapper = new ObjectMapper();
                    String input = mapper.writeValueAsString(callSignal);


                    Log.d(TAG, "TCP ClientSocket Response Send : " + input);

                    //input = input + "\r\n";
                    input = input + "@@";
                    OutputStream out = firstRecvSocket.getOutputStream();
                    out.write(input.getBytes());
                    out.flush();

//                    out.close();
//                    firstRecvSocket.getInputStream().close();
//                    firstRecvSocket.getOutputStream().close();

                    firstRecvSocket.close();
                    firstRecvSocket = null;
                    Log.d(TAG, "Socket Closed 6");

                    CallStateMachine.getInstance().sendCallAccept();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Callee 가 전화를 받지 않고 거부하는 경우
    public void rejectCall()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TcpCallSignalRequest callSignal = new TcpCallSignalRequest();

                    TcpCallSignalHeader header = new TcpCallSignalHeader();
                    header.setType("R");
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
//                    int pNum = Integer.parseInt(DataManager.getInstance().getMyInfo().getPhoneNum());
//                    body.setUdpAudioPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_AUDIO_PORT);
//                    body.setUdpVideoPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_VIDEO_PORT);
//                    body.setIpaddr(Util.getIPAddress());
                    body.setCallId(DataManager.getInstance().getCallId());

                    callSignal.setHeader(header);
                    callSignal.setBody(body);

                    ObjectMapper mapper = new ObjectMapper();
                    String input = mapper.writeValueAsString(callSignal);


                    Log.d(TAG, "TCP Response Send : " + input);

                    //input = input + "\r\n";
                    input = input + "@@";
                    OutputStream out = firstRecvSocket.getOutputStream();
                    out.write(input.getBytes());
                    out.flush();

                    out.close();
                    firstRecvSocket.close();
                    firstRecvSocket = null;
                    Log.d(TAG, "Socket Closed 7");

                    CallStateMachine.getInstance().sendCallReject();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
