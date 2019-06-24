package com.lg.sixsenses.willi.logic.CallManager;

import android.util.Log;

public class IdleState implements CallState {

    public static final String TAG = IdleState.class.getName().toString();
    CallStateMachine callStateMachine;

    public IdleState(CallStateMachine callStateMachine) {
        this.callStateMachine = callStateMachine;
    }

    public void sendCallRequest() {
        Log.d(TAG,"sendCallRequest : Idle -> Calling");
        callStateMachine.setState(callStateMachine.getCallingState());
    }
    public void recvCallRequest() {
        Log.d(TAG,"recvCallRequest : Idle -> Ringing");
        callStateMachine.setState(callStateMachine.getRingingState());
    }
    public void sendCallReject() {

    }
    public void recvCallReject() {

    }
    public void sendCallAccept() {

    }
    public void recvCallAccept() {

    }
}
