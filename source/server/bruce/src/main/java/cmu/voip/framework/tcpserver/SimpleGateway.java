package cmu.voip.framework.tcpserver;

import org.springframework.messaging.handler.annotation.Header;

public interface SimpleGateway {

	public String send(String data, @Header("content") String content);

}
