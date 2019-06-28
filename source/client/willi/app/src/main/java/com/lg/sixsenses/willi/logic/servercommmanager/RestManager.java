package com.lg.sixsenses.willi.logic.servercommmanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lg.sixsenses.willi.repository.ConstantsWilli;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.RegisterInfo;
import com.lg.sixsenses.willi.repository.LoginInfo;
import com.lg.sixsenses.willi.repository.UdpInfo;
import com.lg.sixsenses.willi.repository.UpdatedData;
import com.lg.sixsenses.willi.repository.UserInfo;
import com.lg.sixsenses.willi.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class RestManager {

    public static final String TAG = RestManager.class.getName().toString();

    public static final String SERVER_IP = ConstantsWilli.SERVER_IP;
    public static final String PORT = "8080";
    public static final String CMD_REGISTER = "user/register.json";
    public static final String CMD_LOGIN = "user/login.json";
    public static final String CMD_FRIEND_ADD = "user/friendAdd.json";
    public static final String CMD_FRIEND_DEL = "user/friendDelete.json";
    public static final String CMD_FRIEND_EDIT = "user/friendUpdate.json";
    public static final String CMD_UPDATE = "user/update.json";
    public static final String CMD_UPDATEPW = "user/passwordRestore.json";
    public static final String CMD_CC_REGISTER = "cc/register.json";
    public static final String CMD_CC_GET_MSG = "cc/getcc.json";

    public HttpURLConnection setupRestfulConnection(String cmd)
    {
        try {
            String restAPI = "http://" + SERVER_IP + ":" + PORT + "/" + cmd;
            URL url = new URL(restAPI);
            Log.d(TAG, "restAPI : " + restAPI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            return conn;
        }  catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }

    public void sendRestfulRequest(HttpURLConnection conn, Object req)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();

            RestfulRequest restfulRequest = new RestfulRequest();
            restfulRequest.setBody(req);
            restfulRequest.setToken(DataManager.getInstance().getToken());
            String input = mapper.writeValueAsString(restfulRequest);

            Log.d(TAG,"Send : "+ input);
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
        } catch (IOException e) {

            e.printStackTrace();
       }
    }

    public void parsingRestfulResponse(String recv, String type)
    {
        if(type.equals("RegisterResult"))
        {
            try {
                ObjectMapper mapper = new ObjectMapper();
                TypeReference ref = new TypeReference<RestfulResponse<UserInfo>>() {
                };
                RestfulResponse restfulResponse = mapper.readValue(recv, ref);
                Log.d(TAG, restfulResponse.toString());

                UserInfo myInfo = (UserInfo) (restfulResponse.getBody());
                Log.d(TAG, "MyInfo : " + myInfo.toString());
                DataManager.getInstance().setMyInfo((UserInfo) myInfo);
                UpdatedData data = new UpdatedData();
                data.setType("RegisterResult");
                data.setData(myInfo);
                Log.d(TAG, "Notify my info ");
                DataManager.getInstance().NotifyUpdate(data);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        else if(type.equals("LoginResult"))
        {
            try {
                ObjectMapper mapper = new ObjectMapper();
                TypeReference ref = new TypeReference<RestfulResponse<LoginResult>>() {};
                RestfulResponse restfulResponse = mapper.readValue(recv, ref);
                Log.d(TAG, restfulResponse.toString());
                String token = restfulResponse.getToken();
                DataManager.getInstance().setToken(token);
                LoginResult result = (LoginResult) (restfulResponse.getBody());

                UserInfo myInfo = result.getMyInfo();
                ArrayList<UserInfo> list = result.getList();

                DataManager.getInstance().setMyInfo(myInfo);
                DataManager.getInstance().setContactList(list);

                UdpInfo udpInfo = new UdpInfo();
                udpInfo.setIpaddr(Util.getIPAddress());
                int pNum = Integer.parseInt(DataManager.getInstance().getMyInfo().getPhoneNum());
                udpInfo.setAudioPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_AUDIO_PORT);
                udpInfo.setVideoPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_VIDEO_PORT);
                DataManager.getInstance().setMyUdpInfo(udpInfo);

                UpdatedData data = new UpdatedData();
                data.setType("LoginResult");
                data.setData(list);
                Log.d(TAG, "Notify contact list : " + list.toString());
                DataManager.getInstance().NotifyUpdate(data);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        else if(type.equals("FriendResult"))
        {
            try {
                ObjectMapper mapper = new ObjectMapper();
                TypeReference ref = new TypeReference<RestfulResponse<LoginResult>>() {};
                RestfulResponse restfulResponse = mapper.readValue(recv, ref);
                Log.d(TAG, restfulResponse.toString());
                String token = restfulResponse.getToken();
                DataManager.getInstance().setToken(token);
                LoginResult result = (LoginResult) (restfulResponse.getBody());

                UserInfo myInfo = result.getMyInfo();
                ArrayList<UserInfo> list = result.getList();

                DataManager.getInstance().setMyInfo(myInfo);
                DataManager.getInstance().setContactList(list);

                UdpInfo udpInfo = new UdpInfo();
                udpInfo.setIpaddr(Util.getIPAddress());
                int pNum = Integer.parseInt(DataManager.getInstance().getMyInfo().getPhoneNum());
                udpInfo.setAudioPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_AUDIO_PORT);
                udpInfo.setVideoPort(pNum + ConstantsWilli.CLIENT_BASE_UDP_VIDEO_PORT);
                DataManager.getInstance().setMyUdpInfo(udpInfo);

                UpdatedData data = new UpdatedData();
                data.setType("LoginResult");
                data.setData(list);
                Log.d(TAG, "Notify contact list : " + list.toString());
                DataManager.getInstance().NotifyUpdate(data);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        else if(type.equals("CcRegisterResult"))
        {
            try {
                ObjectMapper mapper = new ObjectMapper();
                TypeReference ref = new TypeReference<RestfulResponse<String>>() {};
                RestfulResponse restfulResponse = mapper.readValue(recv, ref);
                Log.d(TAG, restfulResponse.toString());

                String token = restfulResponse.getToken();
                DataManager.getInstance().setToken(token);
                String ccNum = (String)(restfulResponse.getBody());
                Log.d(TAG, "Conference Call Number : " + ccNum);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public void recvRestfulResponse(HttpURLConnection conn)
    {
        try{

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            Log.d(TAG,"Output from Server .... \n");

            StringBuffer buf = new StringBuffer();

            while ((output = br.readLine()) != null) {
                buf.append(output);
            }

            Log.d(TAG,"Recved : "+buf.toString());

            String type = null;
            try {
                JSONObject jsonObject = new JSONObject(buf.toString());
                type = jsonObject.getString("type");

                Log.d(TAG,"Type : "+type);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            parsingRestfulResponse(buf.toString(), type);

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void sendRegisterUser(RegisterInfo registerInfo)
    {
        class MyRunnable implements Runnable {
            RegisterInfo registerInfo;
            MyRunnable(RegisterInfo info) { registerInfo = info; }

            public void run() {
                HttpURLConnection conn = setupRestfulConnection(CMD_REGISTER);
                sendRestfulRequest(conn,registerInfo);
                recvRestfulResponse(conn);
                conn.disconnect();
            }
        }
        AsyncTask.execute(new MyRunnable(registerInfo));
    }

    public void sendLogin(LoginInfo loginInfo)
    {
        class MyRunnable implements Runnable {
            LoginInfo loginInfo;
            //RegisterInfo registerInfo;
            MyRunnable(LoginInfo info) { loginInfo = info; }

            public void run() {
                HttpURLConnection conn = setupRestfulConnection(CMD_LOGIN);
                sendRestfulRequest(conn,loginInfo);
                recvRestfulResponse(conn);
                conn.disconnect();
            }
        }
        AsyncTask.execute(new MyRunnable(loginInfo));
    }

    public void sendFriendCommand(UserInfo userInfo,String cmd)
    {
        class MyRunnable implements Runnable {
            UserInfo userInfo;
            String command;
            //RegisterInfo registerInfo;
            MyRunnable(UserInfo info, String com) { userInfo = info; command = com;}

            public void run() {
                HttpURLConnection conn = setupRestfulConnection(command);
                sendRestfulRequest(conn,userInfo);
                recvRestfulResponse(conn);
                conn.disconnect();
            }
        }
        AsyncTask.execute(new MyRunnable(userInfo, cmd));
    }

    public void sendUpdateUser(RegisterInfo registerInfo) {
        class MyRunnable implements Runnable {
            RegisterInfo registerInfo;

            MyRunnable(RegisterInfo info) {
                registerInfo = info;
            }

            public void run() {
                HttpURLConnection conn = setupRestfulConnection(CMD_UPDATE);
                sendRestfulRequest(conn, registerInfo);
                AsyncTask.execute(new MyRunnable(registerInfo));
            }
        }
    }
    public void sendCCRegister(CCRegisterBody register)
    {
        class MyRunnable implements Runnable {
            CCRegisterBody registerBody;
            //RegisterInfo registerInfo;
            MyRunnable(CCRegisterBody registerBody) { this.registerBody = registerBody; }

            public void run() {
                HttpURLConnection conn = setupRestfulConnection(CMD_CC_REGISTER);
                sendRestfulRequest(conn,registerBody);
                recvRestfulResponse(conn);
                conn.disconnect();
            }
        }
        AsyncTask.execute(new MyRunnable(register));
    }

    public void sendUpdatePasswordUser(RegisterInfo registerInfo)
    {
        class MyRunnable implements Runnable {
            RegisterInfo registerInfo;

            MyRunnable(RegisterInfo info) {
                registerInfo = info;
            }

            public void run() {
                HttpURLConnection conn = setupRestfulConnection(CMD_UPDATEPW);
                sendRestfulRequest(conn, registerInfo);
            }
        }
        AsyncTask.execute(new MyRunnable(registerInfo));
    }

    public void sendGetCCMsg()
    {
        class MyRunnable implements Runnable {
            public void run() {
                HttpURLConnection conn = setupRestfulConnection(CMD_CC_GET_MSG);
                sendRestfulRequest(conn,null);
                recvRestfulResponse(conn);
                conn.disconnect();
            }
        }
        AsyncTask.execute(new MyRunnable());
    }

}
