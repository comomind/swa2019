package cmu.voip.repository.account.vo;

import java.util.Date;

public class UserCallInfoDTO {
	String phonenum;
	String ip;
	int port;
	int status;
	Date lastcall;

	
	public String getPhonenum() {
		return phonenum;
	}
	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public Date getLastcall() {
		return lastcall;
	}
	public void setLastcall(Date lastcall) {
		this.lastcall = lastcall;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "UserCallInfoDTO [phonenum=" + phonenum + ", ip=" + ip + ", port=" + port + ", status=" + status
				+ ", lastcall=" + lastcall + "]";
	}
	
	
}
