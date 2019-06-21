package cmu.voip.framework.tcpserver;

import org.springframework.messaging.handler.annotation.Header;

public interface ToTCP {
	public void send(String data, @Header("host") String host, @Header("port") int port);
}
