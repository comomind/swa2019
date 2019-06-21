package cmu.voip.batch.security;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import cmu.voip.com.tcp.CallStatConst;
import cmu.voip.com.tcp.vo.TcpCallCommonVO;
import cmu.voip.com.tcp.vo.TcpCallHeader;
import cmu.voip.controller.http.account.vo.UserNetworkInfoVO;
import cmu.voip.controller.tcp.client.TcpToWilliSend;
import cmu.voip.repository.security.vo.UserTokenDTO;
import cmu.voip.service.account.UserAccountService;
import cmu.voip.service.call.CallService;
import cmu.voip.service.call.vo.TcpCallSignalVO;
import cmu.voip.service.security.TokenService;

public class UserHealthCheckTasklet implements Tasklet {

	private static final Logger logger = LogManager.getLogger(UserHealthCheckTasklet.class);

	@Autowired
	TcpToWilliSend tcpToWilliSend;

	@Resource
	UserAccountService userAccountService;

	@Resource
	CallService callService;

	@Resource
	TokenService tokenService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// TODO Auto-generated method stub

		logger.debug("UserHealthCheckTasklet is started");

		List<UserNetworkInfoVO> list = userAccountService.selectAllNetworkUsers();

		ObjectMapper mapper = new ObjectMapper();

		for (UserNetworkInfoVO vo : list) {
			String email = vo.getEmail();
			String name = vo.getName();
			String phoneNum = vo.getPhonenum();
			String ipaddr = vo.getIp();
			int port = vo.getPort();
			int status = vo.getStatus();
			String token = vo.getToken();

			try {
				if (status == CallStatConst.CALL_LOGIN || status == CallStatConst.CALL_NET_FAIL) {
					TcpCallCommonVO<TcpCallSignalVO> request = new TcpCallCommonVO<TcpCallSignalVO>();

					TcpCallHeader requestHeader = new TcpCallHeader();
					requestHeader.setToken(token);
					requestHeader.setType("H");
					requestHeader.setTrantype("S");
					requestHeader.setReqtype(1);
					requestHeader.setSvctype(1);

					TcpCallSignalVO body = new TcpCallSignalVO();

					request.setHeader(requestHeader);
					request.setBody(body);

					String strRequest = mapper.writeValueAsString(request);

					tcpToWilliSend.send(strRequest, ipaddr, port);

					UserTokenDTO tokenVO = new UserTokenDTO();

					tokenVO.setEmail(email);
					tokenVO.setStatus(CallStatConst.CALL_LOGIN);
					tokenService.updateTokenStatusByEmail(tokenVO);
					
					logger.debug(name+"[" +phoneNum + "] health check success !! ~~~~~~~~~~~~~~~~~~~~~~~~~");
					
				} else {
					logger.debug(name+"[" +phoneNum + "] is not on the line !! ~~~~~~~~~~~~~~~~~~~~~~~~~");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				UserTokenDTO tokenVO = new UserTokenDTO();

				tokenVO.setEmail(email);
				tokenVO.setStatus(CallStatConst.CALL_NET_FAIL);
				tokenService.updateTokenStatusByEmail(tokenVO);
				
				logger.debug("[" + name + "] is failed to communicated !! ~~~~~~~~~~~~~~~~~~~~~~~~~");
			}
		}

		return null;
	}

}
