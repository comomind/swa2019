package cmu.voip.controller.http;

public class RestfulResponseVO {
	int result;
	String message;
	String type;
	String token;
	
	Object body;
	
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
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
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
	@Override
	public String toString() {
		return "RestfulResponseVO [result=" + result + ", message=" + message + ", type=" + type + ", token=" + token
				+ ", body=" + body + "]";
	}
}
