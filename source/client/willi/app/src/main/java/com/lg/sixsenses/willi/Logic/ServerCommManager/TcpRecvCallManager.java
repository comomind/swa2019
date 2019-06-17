package com.lg.sixsenses.willi.Logic.ServerCommManager;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lg.sixsenses.willi.DataRepository.ConstantsWilli;
import com.lg.sixsenses.willi.DataRepository.DataManager;
import com.lg.sixsenses.willi.DataRepository.RegisterInfo;
import com.lg.sixsenses.willi.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpRecvCallManager {
    public static final String TAG = TcpRecvCallManager.class.getName().toString();

    public InputStream streamIn = null;
    public OutputStream streamOut = null;


    private class SocketServerThread extends Thread {
        @Override
        public void run() {
            try {
                // 서버소켓 생성

                Log.d(TAG,"ServerSocket Thread ... run()");
                ServerSocket serverSocket = new ServerSocket(ConstantsWilli.CLIENT_TCP_CALL_SIGNAL_PORT);

                // 소켓서버가 종료될때까지 무한루프
                while(true){
                    // 소켓 접속 요청이 올때까지 대기합니다.
                    Socket socket = serverSocket.accept();

                    Log.d(TAG,"ServerSocket Accepted ... ");
                    try{
                        // 응답을 위해 스트림을 얻어옵니다.
                        streamIn = socket.getInputStream();
                        streamOut = socket.getOutputStream();

                        int data = 0;

                        StringBuffer buf = new StringBuffer();

                        char stop = '@';

                        int count = 0;

                        while(count < 2){
                            data =streamIn.read();
                            char ch = (char)data;
                            buf.append(ch);

                            if(ch == stop)
                                count++;
                        }

                        Log.d(TAG, "Client ServerSocket : "+buf.toString());

                        ObjectMapper mapper2 = new ObjectMapper();
                        TypeReference ref = new TypeReference<TcpCallSignalReceive<TcpCallSignalBody>>() {};

                        TcpCallSignalReceive receive = mapper2.readValue(buf.toString(), ref);
                        TcpCallSignalHeader recvHeader = receive.getHeader();
                        TcpCallSignalBody recvBody = (TcpCallSignalBody)(receive.getBody());

                        Log.d(TAG, "TCP Callee Recv Header : " + recvHeader.toString());
                        Log.d(TAG, "TCP Callee Recv Body : " + recvBody.toString());

                        Log.d(TAG, "Ring~ Ring~ Ring~ Call From  : " + recvBody.getCallerPhoneNum());
                        DataManager.getInstance().setCallerPhoneNum(recvBody.getCallerPhoneNum());
//                        buf.append("-Echo\r\n");
//                        // 그리고 현재 날짜를 출력해줍니다.
//                        streamOut.write(buf.toString().getBytes());
//                        streamOut.flush();
//                        Log.w("-------[S ECHO]", buf.toString());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
//                    finally{
//                        // 반드시 소켓은 닫습니다.
//                        try {
//                            streamIn.close();
//                            streamOut.close();
//                        }catch(Exception ex) {
//
//                        }
//                        socket.close();
//                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start()
    {
        SocketServerThread serverThread = new SocketServerThread();
        serverThread.start();
    }

    public void receiveCall()
    {
        class MyRunnable implements Runnable {
            OutputStream streamOut;
            MyRunnable(OutputStream stream) { this.streamOut = stream; }

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
                    body.setCmd("CallResponseC2S");
                    body.setType(ConstantsWilli.CALL_REQUEST_BODY_TYPE_AUDIO);
                    body.setCalleePhoneNum(DataManager.getInstance().getMyInfo().getPhoneNum());
                    body.setCallerPhoneNum(DataManager.getInstance().getCallerPhoneNum());
                    int pNum = Integer.parseInt(DataManager.getInstance().getMyInfo().getPhoneNum());
                    body.setUdpAudioPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_AUDIO_PORT);
                    body.setUdpVideoPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_VIDEO_PORT);
                    body.setIpaddr(Util.getIPAddress());

                    callSignal.setHeader(header);
                    callSignal.setBody(body);

                    ObjectMapper mapper = new ObjectMapper();
                    String input = mapper.writeValueAsString(callSignal);


                    Log.d(TAG, "TCP Response Send : " + input);

                    input = input + "\r\n";

                    streamOut.write(input.getBytes());
                    streamOut.flush();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        AsyncTask.execute(new MyRunnable(streamOut));

    }

}
