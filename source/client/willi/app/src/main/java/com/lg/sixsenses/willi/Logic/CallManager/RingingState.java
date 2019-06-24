package com.lg.sixsenses.willi.logic.CallManager;

import android.util.Log;

public class RingingState implements CallState {

    public static final String TAG = RingingState.class.getName().toString();
    CallStateMachine callStateMachine;

    public RingingState(CallStateMachine callStateMachine) {
        this.callStateMachine = callStateMachine;
    }

    public void sendCallRequest() {

    }
    public void recvCallRequest() {

    }
    public void sendCallReject() {
        Log.d(TAG,"sendCallReject : Ringing -> Idle");
        callStateMachine.setState(callStateMachine.getIdleState());
    }
    public void recvCallReject() {
        Log.d(TAG,"recvCallReject : Ringing -> Idle");
        callStateMachine.setState(callStateMachine.getIdleState());
    }
    public void sendCallAccept() {
        Log.d(TAG,"sendCallAccept : Ringing -> Connected");
        callStateMachine.setState(callStateMachine.getConnectedState());
    }
    public void recvCallAccept() {

    }
}
