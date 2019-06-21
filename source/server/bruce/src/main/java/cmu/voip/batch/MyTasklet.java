package cmu.voip.batch;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import cmu.voip.repository.security.vo.UserTokenDTO;
import cmu.voip.service.security.TokenService;

public class MyTasklet implements Tasklet {
	
	private static final Logger logger = LogManager.getLogger(MyTasklet.class);
	
	@Resource
	TokenService tokenService;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("------------------------------> MyTasklet is started");
		
		List<UserTokenDTO> list = tokenService.selectAllTokens();
		
		for(UserTokenDTO vo : list) {
			
		}
		//User result = userdao.selectUserByEmail(user);
		//logger.debug("------------------------------> " + result.toString());
		
		return RepeatStatus.FINISHED;
	}

}
