package cmu.voip.framework.tcpserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.util.TestingUtilities;
import org.springframework.util.SocketUtils;

public class ClientMain {
	private static final String AVAILABLE_SERVER_SOCKET = "availableServerSocket";

	/**
	 * Prevent instantiation.
	 */
	private ClientMain() {}
	
	public static void main(final String... args) {

		final Scanner scanner = new Scanner(System.in);

		System.out.println("\n========================================================="
				+ "\n                                                         "
				+ "\n    Welcome to the Spring Integration                    "
				+ "\n          TCP-Client-Server Sample!                      "
				+ "\n                                                         "
				+ "\n    For more information please visit:                   "
				+ "\n    https://www.springsource.org/spring-integration/                    "
				+ "\n                                                         "
				+ "\n=========================================================");

		final GenericXmlApplicationContext context = Main.setupContext();
		final SimpleGateway gateway = context.getBean(SimpleGateway.class);
		final AbstractServerConnectionFactory crLfServer = context.getBean(AbstractServerConnectionFactory.class);

		System.out.print("Waiting for server to accept connections...");
		TestingUtilities.waitListening(crLfServer, 10000L);
		System.out.println("running.\n\n");

		System.out.println("Please enter some text and press <enter>: ");
		System.out.println("\tNote:");
		System.out.println("\t- Entering FAIL will create an exception");
		System.out.println("\t- Entering q will quit the application");
		System.out.print("\n");
		System.out.println("\t--> Please also check out the other samples, " +
				"that are provided as JUnit tests.");
		System.out.println("\t--> You can also connect to the server on port '" + crLfServer.getPort() + "' using Telnet.\n\n");

		while (true) {

			final String input = scanner.nextLine();

			if ("q".equals(input.trim())) {
				break;
			}
			else {
				final String result = gateway.send(input);
				System.out.println(result);
			}
		}

		System.out.println("Exiting application...bye.");
		System.exit(0);

	}

	public static GenericXmlApplicationContext setupContext() {
		final GenericXmlApplicationContext context = new GenericXmlApplicationContext();

		if (System.getProperty(AVAILABLE_SERVER_SOCKET) == null) {
			System.out.print("Detect open server socket...");
			int availableServerSocket = SocketUtils.findAvailableTcpPort(5678);

			final Map<String, Object> sockets = new HashMap<String, Object>();
			sockets.put(AVAILABLE_SERVER_SOCKET, availableServerSocket);

			final MapPropertySource propertySource = new MapPropertySource("sockets", sockets);

			context.getEnvironment().getPropertySources().addLast(propertySource);
		}

		System.out.println("using port " + context.getEnvironment().getProperty(AVAILABLE_SERVER_SOCKET));

		context.load("classpath:/spring/test.client.xml");
		context.registerShutdownHook();
		context.refresh();

		return context;
	}
}
