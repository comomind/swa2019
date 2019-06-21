package cmu.voip.controller.tcp.client;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.dsl.context.IntegrationFlowRegistration;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;

@Configuration
public class TcpToWilliClientApplication {
	private static final Logger logger = LogManager.getLogger(TcpToWilliClientApplication.class);
	
	@Bean
	public IntegrationFlow tcpToWilliSend() {
		return f -> f.route(new ToWillisRouter());
	}
	
	public static class ToWillisRouter extends AbstractMessageRouter {

		private final static int MAX_CACHED = 10; // When this is exceeded, we remove the LRU.

		@SuppressWarnings("serial")
		private final LinkedHashMap<String, MessageChannel> subFlows = new LinkedHashMap<String, MessageChannel>(
				MAX_CACHED, .75f, true) {

			@Override
			protected boolean removeEldestEntry(Entry<String, MessageChannel> eldest) {
				if (size() > MAX_CACHED) {
					removeSubFlow(eldest);
					return true;
				} else {
					return false;
				}
			}

		};

		@Autowired
		private IntegrationFlowContext flowContext;
		
		@Autowired
		private TcpOutboundGateway outGateway;
		
		@Override
		protected synchronized Collection<MessageChannel> determineTargetChannels(Message<?> message) {
			MessageChannel channel = this.subFlows
					.get(message.getHeaders().get("host", String.class) + message.getHeaders().get("port")+".flow");
			logger.debug("subFlows --> " + message.getHeaders().get("host", String.class) + message.getHeaders().get("port")+".flow");
			if (channel == null) {
				channel = createNewSubflow(message);
			}
			return Collections.singletonList(channel);
		}

		private MessageChannel createNewSubflow(Message<?> message) {
			String host = (String) message.getHeaders().get("host");
			Integer port = (Integer) message.getHeaders().get("port");
			Assert.state(host != null && port != null, "host and/or port header missing");
			String hostPort = host + port;

			TcpNetClientConnectionFactory cf = new TcpNetClientConnectionFactory(host, port);
			/*TcpReceivingChannelAdapter messageHandler = new TcpReceivingChannelAdapter();
	        messageHandler.setConnectionFactory(cf);
	        messageHandler.setOutputChannelName("clientBytes2StringChannel");*/
	        
			TcpSendingMessageHandler handler = new TcpSendingMessageHandler();
			handler.setConnectionFactory(cf);
			IntegrationFlow flow = f -> f.handle(handler);
			IntegrationFlowRegistration flowRegistration =
					this.flowContext.registration(flow)
							.addBean(cf)
							.id(hostPort + ".flow")
							.register();
			logger.debug("input channel --> " + hostPort + ".flow");
			MessageChannel inputChannel = flowRegistration.getInputChannel();
			this.subFlows.put(hostPort, inputChannel);
			
			return inputChannel;
		}

		private void removeSubFlow(Entry<String, MessageChannel> eldest) {
			String hostPort = eldest.getKey();
			this.flowContext.remove(hostPort + ".flow");
		}

	}
}
