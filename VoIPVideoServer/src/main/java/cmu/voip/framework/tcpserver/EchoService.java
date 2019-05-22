package cmu.voip.framework.tcpserver;

public class EchoService {

	public String test(String input) {
		
		System.out.println("Echo : " + input);
		if ("FAIL".equals(input)) {
			throw new RuntimeException("Failure Demonstration");
		}
		return "echo:" + input;
	}

}

