package cmu.voip.service.call.vo;

public class TcpCallSignalVO {
	String cmd;                 //CallRequestC2S, CallRequestS2C, CallAcceptC2S, CallAcceptS2C, CallRejectS2C, CallRejectC2S
	int type;                   // 1 : audio, 2 : video
	String callerPhoneNum;
	String calleePhoneNum;
	int udpAudioPort;
	int udpVideoPort;
	String ipaddr;
	long callId;
	
	public String getCallerPhoneNum() {
		return callerPhoneNum;
	}
	public void setCallerPhoneNum(String callerPhoneNum) {
		this.callerPhoneNum = callerPhoneNum;
	}
	public String getCalleePhoneNum() {
		return calleePhoneNum;
	}
	public void setCalleePhoneNum(String calleePhoneNum) {
		this.calleePhoneNum = calleePhoneNum;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getUdpAudioPort() {
		return udpAudioPort;
	}
	public void setUdpAudioPort(int udpAudioPort) {
		this.udpAudioPort = udpAudioPort;
	}
	public int getUdpVideoPort() {
		return udpVideoPort;
	}
	public void setUdpVideoPort(int udpVideoPort) {
		this.udpVideoPort = udpVideoPort;
	}
	
	public String getIpaddr() {
		return ipaddr;
	}
	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}
	
	public long getCallId() {
		return callId;
	}
	public void setCallId(long callId) {
		this.callId = callId;
	}
	@Override
	public String toString() {
		return "TcpCallSignalVO [cmd=" + cmd + ", type=" + type + ", callerPhoneNum=" + callerPhoneNum
				+ ", calleePhoneNum=" + calleePhoneNum + ", udpAudioPort=" + udpAudioPort + ", udpVideoPort="
				+ udpVideoPort + ", ipaddr=" + ipaddr + ", callId=" + callId + "]";
	}
	
	
	
}
