package com.lg.sixsenses.willi.logic.CallManager;

import android.util.Log;

public class ConnectedState implements CallState {

    public static final String TAG = ConnectedState.class.getName().toString();
    CallStateMachine callStateMachine;

    public ConnectedState(CallStateMachine callStateMachine) {
        this.callStateMachine = callStateMachine;
    }

    public void sendCallRequest() {

    }
    public void recvCallRequest() {

    }
    public void sendCallReject() {
        Log.d(TAG,"sendCallReject : Connected -> Idle");
        callStateMachine.setState(callStateMachine.getIdleState());
    }
    public void recvCallReject() {
        Log.d(TAG,"recvCallReject : Connected -> Idle");
        callStateMachine.setState(callStateMachine.getIdleState());
    }
    public void sendCallAccept() {

    }
    public void recvCallAccept() {

    }
}
