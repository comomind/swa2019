package cmu.voip.framework.tcpserver;

import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cmu.voip.controller.tcp.client.TcpToWilliSend;

@Controller
public class DynamicTcpClientTestController {
	private static final Logger logger = LogManager.getLogger(DynamicTcpClientTestController.class);

	@Autowired
	TcpToWilliSend tcpToWilliSend;
	
	@RequestMapping(method = RequestMethod.GET, value = "/clientCall")
	public String tcpsend7(@Param(value = "name") String name) {
		
		String result = tcpToWilliSend.send(name, "192.168.225.47",8989);
		
		logger.debug("--------------------------> Willi Response is ["+result+"]");
		
		return "login";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/tcpsend8")
	public String tcpsend8() {
		
		String result = tcpToWilliSend.send("tcpsend 8888", "192.168.225.31",8082);
		
		logger.debug("Tcp Gate 8888 Way Sent And result is ["+result+"]");
		
		return "login";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/tcpsend9")
	public String tcpsend9() {
		
		String result = tcpToWilliSend.send("hello", "192.168.225.50",40001);
		
		logger.debug("Tcp Gate 9999  Way Sent And result is ["+result+"]");
		
		return "login";
	}
}
