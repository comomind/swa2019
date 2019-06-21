package cmu.voip.batch.security;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import cmu.voip.com.tcp.CallStatConst;
import cmu.voip.repository.account.vo.UserAccountDTO;
import cmu.voip.repository.security.vo.UserTokenDTO;
import cmu.voip.service.account.UserAccountService;
import cmu.voip.service.security.TokenService;

public class UserTokenProcessTasklet implements Tasklet {
	private static final Logger logger = LogManager.getLogger(UserTokenProcessTasklet.class);

	@Resource
	TokenService tokenService;
	
	@Resource
	UserAccountService userAccountService;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("UserTokenProcessTasklet is started");

		List<UserTokenDTO> list = tokenService.selectAllTokens();

		for (UserTokenDTO vo : list) {
			String originaltoken = vo.getToken();
			Date start = vo.getCreated();
			int status = vo.getStatus();
			long duration = vo.getDuration();
			Date now = new Date();
			
			long starttime = start.getTime();
			long endtime = now.getTime();
			
			long gap = endtime - starttime;
			
			//status 1 : on
			//status 0 or etc : off
			
			if(gap > duration && status == 1) {
				UserAccountDTO user = userAccountService.selectUserByEmail(vo.getEmail());
				String newToken = TokenService.getTokenUser(user.getName());
				vo.setToken(newToken);
				vo.setCreated(now);
				
				//Tcp communication to user's phone to notify new token
				//If user check new token, start to update new token.
				
				//TCP Update to phone
				
				int updated = tokenService.updateTokenByEmail(vo);
				
				if(updated != 1) {
					logger.info("----> Update Token is abnormal !!! ["+vo.getEmail()+"]["+originaltoken+"]");
				}else {
					logger.info("----> Token Updated "+vo.getEmail()+"["+originaltoken+"] -> ["+newToken+"]");
				}
			}
		}

		return RepeatStatus.FINISHED;
	}
}
