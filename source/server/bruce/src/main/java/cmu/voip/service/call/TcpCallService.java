package cmu.voip.service.call;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.integration.MessageTimeoutException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cmu.voip.com.tcp.CallStatConst;
import cmu.voip.com.tcp.vo.TcpCallCommonVO;
import cmu.voip.com.tcp.vo.TcpCallHeader;
import cmu.voip.controller.tcp.client.TcpToWilliSend;
import cmu.voip.repository.account.UserAccountRepository;
import cmu.voip.repository.account.vo.UserAccountDTO;
import cmu.voip.repository.account.vo.UserCallInfoDTO;
import cmu.voip.repository.call.CallRepository;
import cmu.voip.repository.call.vo.VoiceHistoryDTO;
import cmu.voip.repository.security.UserTokenRepository;
import cmu.voip.repository.security.vo.UserTokenDTO;
import cmu.voip.service.TcpService;
import cmu.voip.service.call.vo.TcpCallSignalVO;
import cmu.voip.service.security.TokenService;

@Service
public class TcpCallService implements TcpService {

	private static final Logger logger = LogManager.getLogger(TcpCallService.class);

	@Autowired
	TcpToWilliSend tcpToWilliSend;

	@Autowired
	UserTokenRepository userTokenRepository;
	
	@Resource
	TokenService tokenService;
	
	@Autowired
	UserAccountRepository userAccountRepository;

	@Autowired
	CallRepository callRepository;

	@Autowired
	EhCacheCacheManager cacheManager;

	@SuppressWarnings("unused")
	@Override
	public Object service(String input) throws Exception {
		// TODO Auto-generated method stub

		logger.info("Caller Payload : " + input);

		TcpCallSignalVO result = new TcpCallSignalVO();

		ObjectMapper mapper = new ObjectMapper();
		// convert input to object
		TypeReference<TcpCallCommonVO<TcpCallSignalVO>> ref = new TypeReference<TcpCallCommonVO<TcpCallSignalVO>>() {
		};

		String calleeIpaddr = null;
		int calleePort = 0;

		TcpCallCommonVO<TcpCallSignalVO> inputVO = mapper.readValue(input, ref);
		TcpCallHeader callerHeader = inputVO.getHeader();
		String token = callerHeader.getToken();

		TcpCallSignalVO callerBody = inputVO.getBody();

		// Caller Info
		UserTokenDTO callerToken = userTokenRepository.selectTokenByToken(token);
		UserAccountDTO callerUser = userAccountRepository.selectUserByEmail(callerToken.getEmail());

		logger.info("Caller Info : [" + token + "]" + callerUser.toString());

		// Check Callee Validation
		UserAccountDTO callee = userAccountRepository.selectUserByPhoneNumber(callerBody.getCalleePhoneNum());

		// If Callee is not existed, Error
		if (callee == null) {
			logger.error("Recevier is not existed phone number : [" + callerBody.getCalleePhoneNum() + "]");
			throw new Exception("Recevier is not existed");
		}

		UserCallInfoDTO callinfo = callRepository.selectUserCallInfoByPhonenum(callerBody.getCalleePhoneNum());

		if (callinfo == null || callinfo.getStatus() != CallStatConst.CALL_STAT_CALL_EN) { 
			// Call info is not existed or call status is not normal
			logger.error("Recevier[" + callerBody.getCalleePhoneNum() + "] is not available Staus["
					+ (callinfo == null ? "null" : callinfo.getStatus()) + "]");
			throw new Exception("Recevier[" + callerBody.getCalleePhoneNum() + "] is not available");
		}

		UserTokenDTO calleeToken = userTokenRepository.selectTokenByEmail(callee.getEmail());

		calleeIpaddr = callinfo.getIp();
		calleePort = callinfo.getPort();

		// Client Send
		TcpCallSignalVO calleeReqbody = new TcpCallSignalVO();
		TcpCallCommonVO<TcpCallSignalVO> calleeRequest = new TcpCallCommonVO<TcpCallSignalVO>();

		// Make Callee's information

		TcpCallCommonVO<TcpCallSignalVO> resultRes = null;

		String strResult = null;

		TcpCallSignalVO response = new TcpCallSignalVO();

		long callId = callRepository.getSequenceCallID();
		
		TcpCallHeader requestHeader = new TcpCallHeader();
		requestHeader.setToken(calleeToken.getToken());
		requestHeader.setType("E");
		requestHeader.setTrantype("S");
		requestHeader.setReqtype(1);
		requestHeader.setSvctype(1);

		TcpCallSignalVO calleeBody = new TcpCallSignalVO();
		calleeBody.setCmd(CallStatConst.CALLREQS2C);
		calleeBody.setCalleePhoneNum(callerBody.getCalleePhoneNum());
		calleeBody.setCallerPhoneNum(callerBody.getCallerPhoneNum());
		calleeBody.setIpaddr(callerHeader.getIpaddr());
		calleeBody.setUdpAudioPort(callerBody.getUdpAudioPort());
		calleeBody.setUdpVideoPort(callerBody.getUdpVideoPort());

		calleeBody.setCallId(callId);
		calleeRequest.setBody(calleeBody);
		calleeRequest.setHeader(requestHeader);

		logger.info("Caller Requested " + callerBody.getCallerPhoneNum() + " --> Callee " + callerBody.getCalleePhoneNum() + " : " + calleePort);

		// Create Call History in advance and save later
		VoiceHistoryDTO callHistory = new VoiceHistoryDTO();
		callHistory.setId(callId);
		callHistory.setCaller(callerBody.getCallerPhoneNum());
		callHistory.setCallee(callerBody.getCalleePhoneNum());
		callHistory.setStatus(CallStatConst.CALL_HISTORY_STAT_CALLOPEN);
		callHistory.setCreated(new Date());
		callHistory.setCalleraport(callerBody.getUdpAudioPort());
		callHistory.setCalleeaport(callerBody.getUdpAudioPort());
		callHistory.setCallervport(callerBody.getUdpVideoPort());
		callHistory.setCalleevport(callerBody.getUdpVideoPort());
		// Cache Call History	
		CallHistoryCache.put(callId + "", callHistory);
		
		try {			
			String strRequest = mapper.writeValueAsString(calleeRequest);
			
			logger.info("Request Callee Packet : " + strRequest);
			
			// Send TCP to Callee
			strResult = tcpToWilliSend.send(strRequest, calleeIpaddr, calleePort);

			logger.info("Callee Response Packet: " + strResult);

			TypeReference<TcpCallCommonVO<TcpCallSignalVO>> refRes = new TypeReference<TcpCallCommonVO<TcpCallSignalVO>>() {
			};

			TcpCallCommonVO<TcpCallSignalVO> resVo = mapper.readValue(strResult, refRes);

			TcpCallSignalVO resBody = resVo.getBody();

			UserCallInfoDTO callerStatus = new UserCallInfoDTO();
			UserCallInfoDTO calleeStatus = new UserCallInfoDTO();

			switch (resBody.getCmd()) {
			case CallStatConst.CALLACCC2S:

				logger.info("Callee Accept Call ------------->  KEY ID : " + callId);
				// Save Call History
				callRepository.insertVoiceCallHistory(callHistory);

				// Update Call Status for caller and callee
				callerStatus.setPhonenum(callerBody.getCallerPhoneNum());
				callerStatus.setStatus(CallStatConst.CALL_STAT_CALL_NON);
				callRepository.updateCallStatusByPhoneNumber(callerStatus);

				calleeStatus.setPhonenum(callerBody.getCalleePhoneNum());
				calleeStatus.setStatus(CallStatConst.CALL_STAT_CALL_NON);
				callRepository.updateCallStatusByPhoneNumber(calleeStatus);

				response.setCallId(callId);
				response.setCmd(CallStatConst.CALLACCS2C);
				response.setCalleePhoneNum(callerBody.getCalleePhoneNum());
				response.setCallerPhoneNum(callerBody.getCallerPhoneNum());
				response.setIpaddr(resBody.getIpaddr());
				response.setUdpAudioPort(resBody.getUdpAudioPort());
				response.setUdpVideoPort(resBody.getUdpVideoPort());
				break;
			case CallStatConst.CALLREJC2S:
				logger.info("Callee Accept Reject Call ");

				// Update Call Status for caller and callee
				callerStatus.setPhonenum(callerBody.getCallerPhoneNum());
				callerStatus.setStatus(CallStatConst.CALL_STAT_CALL_EN);
				callRepository.updateCallStatusByPhoneNumber(callerStatus);

				calleeStatus.setPhonenum(callerBody.getCallerPhoneNum());
				calleeStatus.setStatus(CallStatConst.CALL_STAT_CALL_EN);
				callRepository.updateCallStatusByPhoneNumber(calleeStatus);

				response.setCmd(CallStatConst.CALLREJS2C);
				response.setCalleePhoneNum(callerBody.getCalleePhoneNum());
				response.setCallerPhoneNum(callerBody.getCallerPhoneNum());
				response.setIpaddr(resBody.getIpaddr());
				response.setUdpAudioPort(resBody.getUdpAudioPort());
				response.setUdpVideoPort(resBody.getUdpVideoPort());
				break;
			default:
				logger.info("Callee Responsed Abnormal Command --> Return Back !! ");

				response.setCmd(resBody.getCmd());
				response.setCalleePhoneNum(callerBody.getCalleePhoneNum());
				response.setCallerPhoneNum(callerBody.getCallerPhoneNum());
				response.setIpaddr(resBody.getIpaddr());
				response.setUdpAudioPort(resBody.getUdpAudioPort());
				response.setUdpVideoPort(resBody.getUdpVideoPort());
				break;
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();

			response.setCmd(CallStatConst.CALLFAILS2C);
			response.setCalleePhoneNum(callerBody.getCalleePhoneNum());
			response.setCallerPhoneNum(callerBody.getCallerPhoneNum());
			response.setCallId(callId);
			
			UserCallInfoDTO callerStatus = new UserCallInfoDTO();
			UserCallInfoDTO calleeStatus = new UserCallInfoDTO();
			
			callerStatus.setPhonenum(callerBody.getCallerPhoneNum());
			callerStatus.setStatus(CallStatConst.CALL_STAT_CALL_EN);
			callRepository.updateCallStatusByPhoneNumber(callerStatus);

			calleeStatus.setPhonenum(callerBody.getCallerPhoneNum());
			calleeStatus.setStatus(CallStatConst.CALL_STAT_CALL_EN);
			callRepository.updateCallStatusByPhoneNumber(calleeStatus);
			
			TcpCallCommonVO<TcpCallSignalVO> calleeRequestF = new TcpCallCommonVO<TcpCallSignalVO>();
			
			TcpCallHeader requestHeaderF = new TcpCallHeader();
			requestHeaderF.setToken(calleeToken.getToken());
			requestHeaderF.setType("E");
			requestHeaderF.setTrantype("S");
			requestHeaderF.setReqtype(1);
			requestHeaderF.setSvctype(1);

			TcpCallSignalVO calleeBodyF = new TcpCallSignalVO();
			calleeBodyF.setCmd(CallStatConst.CALLFAILS2C);
			calleeBodyF.setCalleePhoneNum(callerBody.getCalleePhoneNum());
			calleeBodyF.setCallerPhoneNum(callerBody.getCallerPhoneNum());
			calleeBodyF.setIpaddr(callerHeader.getIpaddr());
			calleeBodyF.setUdpAudioPort(callerBody.getUdpAudioPort());
			calleeBodyF.setUdpVideoPort(callerBody.getUdpVideoPort());

			calleeBodyF.setCallId(callId);
			calleeRequestF.setBody(calleeBodyF);
			calleeRequestF.setHeader(requestHeaderF);
			
			//If Callee do not response, FAILED send to callee (for 30 seconds)
			String strRequestF = mapper.writeValueAsString(calleeRequestF);
			try {
				strResult = tcpToWilliSend.send(strRequestF, calleeIpaddr, calleePort);
			}catch(Exception ee) {
				ee.printStackTrace();
				logger.error("Reject Call Signal to Callee is Failed After Timeout");
			}
		}finally {
			// Remove Call Hisotry from cache
			CallHistoryCache.remove(callId + "");
		}

		return response;
	}

}
