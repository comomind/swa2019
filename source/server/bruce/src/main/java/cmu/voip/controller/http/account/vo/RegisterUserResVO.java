package cmu.voip.controller.http.account.vo;

public class RegisterUserResVO {
	public String name;
	public String email;
	public String phoneNum;
	public int loginStatus;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	
	public int getLoginStatus() {
		return loginStatus;
	}
	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
	}
	@Override
	public String toString() {
		return "RegisterUserResVO [name=" + name + ", email=" + email + ", phoneNum=" + phoneNum + ", loginStatus="
				+ loginStatus + "]";
	}
	
}
