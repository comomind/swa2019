package cmu.voip.com.tcp.vo;

public class TcpCallResponseVO{
	TcpCallHeader header;
	Object body;
	
	public TcpCallHeader getHeader() {
		return header;
	}
	public void setHeader(TcpCallHeader header) {
		this.header = header;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "TcpCallResponse [header=" + header + ", body=" + body + "]";
	}
}
