package cmu.voip.controller.http;

public class RestfulRequestVO<T> {
	String token;
	T body;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public T getBody() {
		return body;
	}
	public void setBody(T body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "RestfulRequestVO [ token=" + token + ", body=[" + body.toString() + "] ]";
	}
	
	
}
