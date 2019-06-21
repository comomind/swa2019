package cmu.voip.com.tcp;

public class CallStatConst {
	public static final String CALLREQC2S = "CallRequestC2S";
	public static final String CALLREQS2C = "CallRequestS2C";
	public static final String CALLACCS2C = "CallAcceptS2C";
	public static final String CALLACCC2S = "CallAcceptC2S";
	public static final String CALLREJS2C = "CallRejectS2C";
	public static final String CALLREJC2S = "CallRejectC2S";
	public static final String CALLFAILS2C = "CallFailS2C";
	
	//CALL STATUS STATUS
	public static final int CALL_STAT_CALL_EN=0;    //NORMAL
	public static final int CALL_STAT_CALL_NON=1;   //BUSY
	
	//CALL HISTORY STATUS
	public static final int CALL_HISTORY_STAT_CALLOPEN=1;      //CALLING
	public static final int CALL_HISTORY_STAT_CALLCLOSED=2;    //END
	
	//Login Status
	public static final int CALL_LOGOFF=0;
	public static final int CALL_LOGIN=1;
	public static final int CALL_NET_FAIL=2;
}
