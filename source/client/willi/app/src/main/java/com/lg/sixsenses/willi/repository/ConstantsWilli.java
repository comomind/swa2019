package com.lg.sixsenses.willi.repository;

public class ConstantsWilli {

    public static final String SERVER_IP = "192.168.225.31";
    //public static final String SERVER_IP = "128.237.193.216";
    //public static final String SERVER_IP = "10.0.1.67";
    //public static final String SERVER_IP = "10.0.1.2";
    //public static final String SERVER_IP = "128.237.163.200";

    public static final int SERVER_TCP_PORT = 8081;
    public static final int CLIENT_BASE_UDP_AUDIO_PORT = 60000;
    public static final int CLIENT_BASE_UDP_VIDEO_PORT = 50000;
    public static final int CLIENT_TCP_CALL_SIGNAL_PORT = 40000;


    public static final String CALL_REQUEST_BODY_TYPE_AUDIO = "1";
    public static final String CALL_REQUEST_BODY_TYPE_VIDEO = "2";
    public static final String CALL_REQUEST_BODY_TYPE_CC = "3";

    public static final String PREFERENCE_FILENAME              = "settings";
    public static final String PREFERENCE_KEY_AUDIOOUTPUT       = "audioOutput";
    public static final String PREFERENCE_KEY_SOUND             = "sound";
    public static final String PREFERENCE_KEY_MY_PHONE_NUMBER   = "myPhoneNumber";
    public static final String PREFERENCE_KEY_MY_NAME           = "myName";
    public static final String PREFERENCE_KEY_MY_EMAIL          = "myEmail";
    public static final String PREFERENCE_KEY_TOKEN             = "token";
}
