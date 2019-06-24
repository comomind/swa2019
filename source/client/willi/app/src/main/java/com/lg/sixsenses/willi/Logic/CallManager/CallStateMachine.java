package com.lg.sixsenses.willi.logic.CallManager;

import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UpdatedData;

public class CallStateMachine {

    CallState idleState;
    CallState ringingState;
    CallState callingState;
    CallState connectedState;

    CallState state;

    private static CallStateMachine instance = new CallStateMachine();
    public static CallStateMachine getInstance() {
        return instance;
    }

    public CallStateMachine() {

        idleState       = new IdleState(this);
        ringingState    = new RingingState(this);
        callingState    = new CallingState(this);
        connectedState  = new ConnectedState(this);

        state = idleState;
    }

    public void sendCallRequest() {
        state.sendCallRequest();
    }
    public void recvCallRequest() { state.recvCallRequest(); }
    public void sendCallReject() {
        state.sendCallReject();
    }
    public void recvCallReject() {
        state.recvCallReject();
    }
    public void sendCallAccept() {
        state.sendCallAccept();
    }
    public void recvCallAccept() {
        state.recvCallAccept();
    }

    public void setState(CallState state)
    {
        this.state = state;
        DataManager.getInstance().setCallStatus(getCallStatus(state));
        UpdatedData data = new UpdatedData();
        data.setType("CallState");
        data.setData(getCallStatus(state));
        if(getCallStatus(state)== DataManager.CallStatus.IDLE) DataManager.getInstance().clearCallInfo();
        DataManager.getInstance().NotifyUpdate(data);
    }
    private DataManager.CallStatus getCallStatus(CallState state)
    {
        if(state == idleState) return DataManager.CallStatus.IDLE;
        else if(state == callingState) return DataManager.CallStatus.CALLING;
        else if(state == ringingState) return DataManager.CallStatus.RINGING;
        else if(state == connectedState) return DataManager.CallStatus.CONNECTED;
        else return DataManager.CallStatus.IDLE;
    }

    public CallState getIdleState() {
        return idleState;
    }
    public CallState getCallingState() {
        return callingState;
    }
    public CallState getRingingState() {
        return ringingState;
    }
    public CallState getConnectedState() {
        return connectedState;
    }

}
