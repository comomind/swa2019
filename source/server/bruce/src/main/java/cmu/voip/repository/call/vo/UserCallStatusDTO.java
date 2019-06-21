package cmu.voip.repository.call.vo;

import java.util.Date;

public class UserCallStatusDTO {
	public String email;
	public String ip;
	public int status;
	public Date lasttime;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getLasttime() {
		return lasttime;
	}
	public void setLasttime(Date lasttime) {
		this.lasttime = lasttime;
	}
	@Override
	public String toString() {
		return "UserCallStatusVO [email=" + email + ", ip=" + ip + ", status=" + status + ", lasttime=" + lasttime
				+ "]";
	}
}
