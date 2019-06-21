package cmu.voip.framework.interceptor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import cmu.voip.repository.security.vo.UserTokenDTO;
import cmu.voip.service.security.TokenService;

public class LoginCheckInterceptor extends UrlPatternInterceptor {
	
	private static final Logger logger = LogManager.getLogger(UrlPatternInterceptor.class);
	
	@Resource
	TokenService tokenService;
	
	@Override
	public boolean checkHandle(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse,
			Object pramObject) throws Exception {
		// TODO Auto-generated method stub
		
		ServletInputStream inputStream = paramHttpServletRequest.getInputStream();
		
		BufferedReader br = new BufferedReader(new InputStreamReader((inputStream)));
		
		StringBuffer buf = new StringBuffer();
		
		String output;
		
		while ((output = br.readLine()) != null) {
			buf.append(output);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		logger.debug("Input Message ----> " + buf.toString());
		
		HashMap<String,Object> resultMap = mapper.readValue(buf.toString(),HashMap.class);
		
		String token = (String) resultMap.get("token");
		
		if(token == null)
			token = " null ";
		
		UserTokenDTO tokenVo = tokenService.selectTokenByToken(token);
		
		if(tokenVo == null || tokenVo.getEmail() == null) {
			logger.warn("Token is not available ----> " + token);
			return true;
		}else {
			return true;
		}
	}

}
