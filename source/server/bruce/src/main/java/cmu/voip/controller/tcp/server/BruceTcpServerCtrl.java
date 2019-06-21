package cmu.voip.controller.tcp.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;

import cmu.voip.com.tcp.vo.TcpCallHeader;
import cmu.voip.com.tcp.vo.TcpCallResponseVO;
import cmu.voip.repository.security.UserTokenRepository;
import cmu.voip.repository.security.vo.UserTokenDTO;
import cmu.voip.service.TcpService;

public class BruceTcpServerCtrl implements TcpServcieIf {
	
	private static final Logger logger = LogManager.getLogger(BruceTcpServerCtrl.class);
			
	@Autowired
	ApplicationContext applicationContext;
	
	@Autowired
	UserTokenRepository userTokenRepository;
	
	@Override
	public String service(String input) {
		// TODO Auto-generated method stub
		
		logger.debug("--------------------------------> BruceTcpServerCtrl " + new String(input));
		
		String result = "";
		
		TcpCallResponseVO response = new TcpCallResponseVO();
		
		TcpCallHeader resHeader = new TcpCallHeader();
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			HashMap<String, Object> reqMap = mapper.readValue(input, HashMap.class);
			
			Object header = (Object)reqMap.get("header");
			String serviceId = (String)((Map)header).get("svcid");
			String token = (String)((Map)header).get("token");
			
			// Check Caller Validation
			UserTokenDTO collerTokendto = userTokenRepository.selectTokenByToken(token);

			if (collerTokendto == null)
				throw new Exception("Token is not available");
			
			TcpService tcpoService = (TcpService) applicationContext.getBean(serviceId);

			Object res = tcpoService.service(input);
		
			resHeader.setType("R");
			resHeader.setTrantype("S");
			resHeader.setResult(0);
			
			response.setHeader(resHeader);
			response.setBody(res);
			
			result = mapper.writeValueAsString(response);
			
			logger.debug("-----> Result Call : " + result);
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			
			resHeader.setType("R");
			resHeader.setTrantype("S");
			resHeader.setResult(1);
			resHeader.setMessage(ex.getMessage());
			response.setHeader(resHeader);
			
			try {
				result = mapper.writeValueAsString(response);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			logger.debug("-----> Result Error Call : " + result);
			return result;
		}
		
	}
	
}
