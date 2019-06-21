package cmu.voip.controller.tcp.client;

import org.springframework.messaging.handler.annotation.Header;

public interface TcpToWilliSend {
	public String send(String data, @Header("host") String host, @Header("port") int port);
}
