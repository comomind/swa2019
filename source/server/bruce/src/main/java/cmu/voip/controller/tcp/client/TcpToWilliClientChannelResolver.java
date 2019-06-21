package cmu.voip.controller.tcp.client;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.messaging.MessageChannel;

public class TcpToWilliClientChannelResolver {
	public static final int MAX_CACHE_SIZE = 100;

	private static final Logger logger = LogManager.getLogger(TcpToWilliClientChannelResolver.class);

	private final LinkedHashMap<String, MessageChannel> channels = new LinkedHashMap<String, MessageChannel>() {

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Entry<String, MessageChannel> eldest) {
			// This returning true means the least recently used
			// channel and its application context will be closed and removed
			boolean remove = size() > MAX_CACHE_SIZE;
			if (remove) {
				MessageChannel channel = eldest.getValue();
				ConfigurableApplicationContext ctx = contexts.get(channel);
				if (ctx != null) { // shouldn't be null ideally
					ctx.close();
					contexts.remove(channel);
				}
			}
			return remove;
		}

	};

	private final Map<MessageChannel, ConfigurableApplicationContext> contexts = new HashMap<MessageChannel, ConfigurableApplicationContext>();

	/**
	 * Resolve a customer to a channel, where each customer gets a private
	 * application context and the channel is the inbound channel to that
	 * application context.
	 *
	 * @param customer
	 * @return a channel
	 */
	public MessageChannel resolve(String host,String port) {
		MessageChannel channel = this.channels.get(host+","+port);
		if (channel == null) {
			channel = createNewCustomerChannel(host,port);
		}

		logger.debug("----------------------> " + host+","+port);
		return channel;
	}

	private synchronized MessageChannel createNewCustomerChannel(String host,String port) {
		MessageChannel channel = this.channels.get(host+","+port);
		if (channel == null) {
			
			logger.debug("----------------------> make new channel");
			
			ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext(
					new String[] { "/spring/context-integration-client.xml" }, false);
			boolean result = this.setEnvironmentForCustomer(ctx, host,port);

			if (result) {
				ctx.refresh();
				logger.debug("------------> Ctx Refresh done");
				channel = ctx.getBean("requestOut", MessageChannel.class);
				this.channels.put(host+","+port, channel);
				// Will works as the same reference is presented always
				this.contexts.put(channel, ctx);
			}
		}else {
			logger.debug("----------------------> channel is not null");
		}
		return channel;
	}

	/**
	 * Use Spring 3.1. environment support to set properties for the
	 * customer-specific application context.
	 *
	 * @param ctx
	 * @param customer
	 */
	private boolean setEnvironmentForCustomer(ConfigurableApplicationContext ctx, String host,String port) {

		boolean result = true;

		try {
			StandardEnvironment env = new StandardEnvironment();
			Properties props = new Properties();
			// populate properties for customer
			props.setProperty("host", host);
			props.setProperty("port", port);
			PropertiesPropertySource pps = new PropertiesPropertySource("dynamicTcpClient", props);
			env.getPropertySources().addLast(pps);
			ctx.setEnvironment(env);
		} catch (Exception ex) {
			result = false;
			ex.printStackTrace();
		}

		return result;
	}
}
