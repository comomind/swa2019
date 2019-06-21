package cmu.voip.framework.tcpserver;

public class EchoService {
	
	public String test(String input) {
		System.out.println("-------------> Server Echo 8888 : " + input);
		return "echo   : " + input ;
	}

}

