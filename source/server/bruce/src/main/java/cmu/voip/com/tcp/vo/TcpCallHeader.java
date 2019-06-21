package cmu.voip.com.tcp.vo;

public class TcpCallHeader {
	String type;
	String token;
	String ipaddr;
	String trantype;
	int reqtype;
	int svctype;
	String svcid;
	int result;
	String message;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getIpaddr() {
		return ipaddr;
	}
	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}
	public String getTrantype() {
		return trantype;
	}
	public void setTrantype(String trantype) {
		this.trantype = trantype;
	}
	public int getReqtype() {
		return reqtype;
	}
	public void setReqtype(int reqtype) {
		this.reqtype = reqtype;
	}
	public int getSvctype() {
		return svctype;
	}
	public void setSvctype(int svctype) {
		this.svctype = svctype;
	}
	public String getSvcid() {
		return svcid;
	}
	public void setSvcid(String svcid) {
		this.svcid = svcid;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "TcpCallHeader [type=" + type + ", token=" + token + ", ipaddr=" + ipaddr + ", trantype=" + trantype
				+ ", reqtype=" + reqtype + ", svctype=" + svctype + ", svcid=" + svcid + ", result=" + result
				+ ", message=" + message + "]";
	}
	
	
}
