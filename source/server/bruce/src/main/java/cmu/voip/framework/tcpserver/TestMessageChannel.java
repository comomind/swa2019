package cmu.voip.framework.tcpserver;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

public class TestMessageChannel implements MessageChannel {

	@Override
	public boolean send(Message<?> message) {
		System.out.println("------> Test Message Type : " +(message.getPayload().getClass().getName()));
		return false;
	}

	@Override
	public boolean send(Message<?> message, long timeout) {
		// TODO Auto-generated method stub
		return false;
	}

}
