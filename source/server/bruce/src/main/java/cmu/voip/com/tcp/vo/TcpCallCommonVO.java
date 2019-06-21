package cmu.voip.com.tcp.vo;

public class TcpCallCommonVO<T> {
	TcpCallHeader header;
	T body;
	
	public TcpCallHeader getHeader() {
		return header;
	}
	public void setHeader(TcpCallHeader header) {
		this.header = header;
	}
	public T getBody() {
		return body;
	}
	public void setBody(T body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "TcpCallResponse [header=" + header + ", body=" + body + "]";
	}
}
