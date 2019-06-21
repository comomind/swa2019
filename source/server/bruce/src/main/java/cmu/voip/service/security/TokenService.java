package cmu.voip.service.security;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cmu.voip.com.StringUtility;
import cmu.voip.repository.security.UserTokenRepository;
import cmu.voip.repository.security.vo.UserTokenDTO;

@Service
public class TokenService {
	
	private static final Logger logger = LogManager.getLogger(TokenService.class);
	
	private final static long duration = 100000;
	
	@Autowired
	UserTokenRepository tokendao;
	
	public static long getDuration() {
		return duration;
	}
	
	public static String getTokenUser(String init) {
		
		Random rand = new Random(); 
		
		StringBuffer buffer = new StringBuffer(StringUtility.getStringPadding(init, 14));
		buffer.append(StringUtility.getNumberPadding(Calendar.getInstance().getTimeInMillis(),13));
		buffer.append(StringUtility.getNumberPadding(rand.nextInt(1000), 3));
		return buffer.toString();
	}
	
	public UserTokenDTO selectTokenByEmail(String email) throws Exception {
		return tokendao.selectTokenByEmail(email);
	}
	
	public UserTokenDTO selectTokenByToken(String token) throws Exception {
		return tokendao.selectTokenByToken(token);
	}
	
	@Transactional
	public String insertToken(UserTokenDTO token, String name) throws Exception {
		
		String strtoken = TokenService.getTokenUser(name);
		
		token.setToken(strtoken);
		token.setStatus(1);
		token.setCreated(new Date());
		token.setDuration(duration);

		logger.debug("Insert Token to "+token.getEmail()+" , Token : " + strtoken);
		
		int result = tokendao.insertTokenEachUser(token);
		
		if(result == 0)
			strtoken = null;
		
		return strtoken;
	}
	
	@Transactional
	public int updateTokenByEmail(UserTokenDTO token) throws Exception{
		return tokendao.updateTokenByEmail(token);
	}
	
	@Transactional
	public int updateTokenStatusByEmail(UserTokenDTO token) throws Exception{
		return tokendao.updateTokenStatusByEmail(token);
	}
	
	@Transactional
	public int updateTokenLogffByEmail(UserTokenDTO token) throws Exception{
		return tokendao.updateTokenLogffByEmail(token);
	}
	
	public List<UserTokenDTO> selectAllTokens(){
		return tokendao.selectAllTokens();
	}
	
	public boolean checkTokenStr(String strToken) {

		boolean result = false;
		
		UserTokenDTO token = null;
		
		try {
			token = selectTokenByToken(strToken);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(token == null) {
			return result;
		}
		
		long duration = token.getDuration();
		long start = token.getCreated().getTime();
		long now = new Date().getTime();
		
		long gap = now - start;
		
		if(gap > duration)
			result = false;
		else
			result = true;
		
		return result;
	}
	
	public boolean checkToken(UserTokenDTO token) {

		boolean result = false;
		
		if(token == null) {
			return result;
		}
		
		long duration = token.getDuration();
		long start = token.getCreated().getTime();
		long now = new Date().getTime();
		
		long gap = now - start;
		
		if(gap > duration)
			result = false;
		else
			result = true;
		
		return result;
	}
}
