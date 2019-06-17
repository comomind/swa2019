package lg.dplakosh.lgvoipdemo;

import java.util.Observable;

public class PhoneState extends Observable {
    public enum CallState {LISTENING, CALLING, INCOMMING, INCALL}
    public enum VideoState {START_VIDEO, RECEIVING_VIDEO, STOP_VIDEO,VIDEO_STOPPED}
    private CallState CallStatel = CallState.LISTENING;
    private VideoState  RecVideoState = VideoState.VIDEO_STOPPED;
    private String RemoteIP;
    private String LocalIP;
    private String InComingIP;
    private String CmdIP;
    private Boolean RingerEnabled;
    private Boolean BoostEnabled;
    private Boolean MicEnabled;


    private static PhoneState instance = new PhoneState();

    public static PhoneState getInstance() {
        return instance;
    }

    private PhoneState() {
    }

    void SetPhoneState(CallState callstate) {
        CallStatel = callstate;
    }

    CallState GetPhoneState() {
        return CallStatel;
    }

    void SetRecvVideoState(VideoState videostate) { RecVideoState = videostate; }

    VideoState GetRecvVideoState() { return RecVideoState; }

    void SetInComingIP(String value) {
        InComingIP = value;
    }

    String GetInComingIP() {
        return InComingIP;
    }

    void SetCmdIP(String value) {
        CmdIP = value;
    }

    String GetCmdIP() {
        return CmdIP;
    }

    void SetRemoteIP(String value) {
        RemoteIP = value;
    }

    String GetRemoteIP() {
        return RemoteIP;
    }

    void SetLocallP(String value) {
        LocalIP = value;
    }

    String GetLocalIP() {
        return LocalIP;
    }

    void SetRinger(Boolean value) {
        RingerEnabled = value;
    }

    Boolean GetRinger() {
        return RingerEnabled;
    }

    void SetMic(Boolean value) {
        MicEnabled = value;
    }

    Boolean GetMic() {
        return MicEnabled;
    }

    void SetBoost(Boolean value) {
        BoostEnabled = value;
    }

    Boolean GetBoost() {
        return BoostEnabled;
    }

    void NotifyUpdate() {
        setChanged();
        notifyObservers();
    }
}