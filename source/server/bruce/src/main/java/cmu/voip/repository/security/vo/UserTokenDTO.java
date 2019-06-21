package cmu.voip.repository.security.vo;

import java.util.Date;

public class UserTokenDTO {
	String email;
	String token;
	int status;
	Date created;
	Date logoff;
	long duration;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getLogoff() {
		return logoff;
	}
	public void setLogoff(Date logoff) {
		this.logoff = logoff;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	@Override
	public String toString() {
		return "UserTokenVO [email=" + email + ", token=" + token + ", status=" + status + ", created=" + created
				+ ", logoff=" + logoff + ", duration=" + duration + "]";
	}
}
