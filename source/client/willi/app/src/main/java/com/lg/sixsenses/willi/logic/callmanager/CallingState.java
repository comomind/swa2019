package com.lg.sixsenses.willi.logic.callmanager;
import android.util.Log;

public class CallingState implements CallState {

    public static final String TAG = CallingState.class.getName().toString();
    CallStateMachine callStateMachine;

    public CallingState(CallStateMachine callStateMachine) {
        this.callStateMachine = callStateMachine;
    }

    public void sendCallRequest() {

    }
    public void recvCallRequest() {

    }
    public void sendCallReject() {
        Log.d(TAG,"sendCallReject : Calling -> Idle");
        callStateMachine.setState(callStateMachine.getIdleState());
    }
    public void recvCallReject() {
        Log.d(TAG,"recvCallReject : Calling -> Idle");
        callStateMachine.setState(callStateMachine.getIdleState());

    }
    public void sendCallAccept() {

    }
    public void recvCallAccept() {
        Log.d(TAG,"recvCallAccept : Calling -> Connected");
        callStateMachine.setState(callStateMachine.getConnectedState());
    }
}
