package cmu.voip.repository.account.vo;

import java.util.Date;

public class UserAccountDTO {
	public String email;
	public String name;
	public String password;
	public int level;
	public Date created;
	public String securityquestion;
	public String securityanswer;
	public String phonenum;
	public int status;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getSecurityquestion() {
		return securityquestion;
	}
	public void setSecurityquestion(String securityquestion) {
		this.securityquestion = securityquestion;
	}
	public String getSecurityanswer() {
		return securityanswer;
	}
	public void setSecurityanswer(String securityanswer) {
		this.securityanswer = securityanswer;
	}
	
	public String getPhonenum() {
		return phonenum;
	}
	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "UserAccountDTO [email=" + email + ", name=" + name + ", password=" + password + ", level=" + level
				+ ", created=" + created + ", securityquestion=" + securityquestion + ", securityanswer="
				+ securityanswer + ", phonenum=" + phonenum + ", status=" + status + "]";
	}
	
}
