package cmu.voip.service.call;

import java.util.Date;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
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
import net.sf.ehcache.Element;

@Service
public class TcpCallReject implements TcpService {

	private static final Logger logger = LogManager.getLogger(TcpCallReject.class);

	@Autowired
	TcpToWilliSend tcpToWilliSend;

	@Autowired
	UserTokenRepository userTokenRepository;

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
		logger.info("Sender Reject Payload : " + input);
		TcpCallSignalVO result = new TcpCallSignalVO();

		ObjectMapper mapper = new ObjectMapper();
		// convert input to object
		TypeReference<TcpCallCommonVO<TcpCallSignalVO>> ref = new TypeReference<TcpCallCommonVO<TcpCallSignalVO>>() {
		};

		TcpCallCommonVO<TcpCallSignalVO> inputVO = mapper.readValue(input, ref);

		TcpCallHeader senderHeader = inputVO.getHeader();
		TcpCallSignalVO senderBody = inputVO.getBody();

		String cmd = senderBody.getCmd();
		long keyID = senderBody.getCallId();

		TcpCallSignalVO response = new TcpCallSignalVO();

		UserCallInfoDTO senderStatus = new UserCallInfoDTO();
		UserCallInfoDTO receiverStatus = new UserCallInfoDTO();

		long callId = 0;

		// Caller Info
		logger.info("Sender Token : " + senderHeader.getToken());

		UserTokenDTO senderToken = userTokenRepository.selectTokenByToken(senderHeader.getToken());
		UserAccountDTO senderUser = userAccountRepository.selectUserByEmail(senderToken.getEmail());

		VoiceHistoryDTO cachedCallHistory = CallHistoryCache.findCallByPhoneNumber(senderUser.getPhonenum());
		
		logger.info("Sender Info : " + senderUser.toString());
		
		try {
			VoiceHistoryDTO callHistory = null;

			if (cachedCallHistory == null) {
				// If Callee Accept Call, so get from database
				logger.info("------------------->  Callee Accept and someone(caller or callee) try Reject");
				callHistory = callRepository.selectVoiceCallHistoryById(keyID);
				callHistory.setStatus(CallStatConst.CALL_HISTORY_STAT_CALLCLOSED);
				callHistory.setClosed(new Date());
				callRepository.updateVoiceCallStatusHistory(callHistory);
				callRepository.updateVoiceCallClosedHistory(callHistory);

			} else {
				logger.info("------------------->  Callee is Accepting and Rejected !!");
				callHistory = cachedCallHistory;
			}

			if (callHistory == null)
				new Exception("There is no calling status !!");

			callId = callHistory.getId();

			logger.info("Call History Info : " + callHistory.toString());

			// 전화 건 놈
			String senderPhone = senderUser.getPhonenum();
			String receiverPhone = null;

			// Caller, Callee
			String callerPhoneNumber = callHistory.getCaller();
			String calleePhoneNumber = callHistory.getCallee();

			if (senderPhone.equals(callerPhoneNumber)) {
				receiverPhone = calleePhoneNumber;
			} else {
				receiverPhone = callerPhoneNumber;
			}

			// receiver Info
			UserAccountDTO receiverUser = userAccountRepository.selectUserByPhoneNumber(receiverPhone);
			// Callee Token
			UserTokenDTO receiverToken = userTokenRepository.selectTokenByEmail(receiverUser.getEmail());
			// Callee call info
			UserCallInfoDTO receiverCallInfo = callRepository
					.selectUserCallInfoByPhonenum(receiverUser.getPhonenum());

			// Update Sender call status
			senderStatus.setPhonenum(senderUser.getPhonenum());
			senderStatus.setStatus(CallStatConst.CALL_STAT_CALL_EN);
			callRepository.updateCallStatusByPhoneNumber(senderStatus);

			TcpCallCommonVO<TcpCallSignalVO> receiverRequest = new TcpCallCommonVO<TcpCallSignalVO>();

			TcpCallHeader requestHeader = new TcpCallHeader();
			requestHeader.setToken(receiverToken.getToken());
			requestHeader.setType("E");
			requestHeader.setTrantype("S");
			requestHeader.setReqtype(1);
			requestHeader.setSvctype(1);

			TcpCallSignalVO receiverBody = new TcpCallSignalVO();
			receiverBody.setCmd(CallStatConst.CALLREJS2C);
			receiverBody.setCalleePhoneNum(senderBody.getCalleePhoneNum());
			receiverBody.setCallerPhoneNum(senderBody.getCallerPhoneNum());
			receiverBody.setUdpAudioPort(senderBody.getUdpAudioPort());
			receiverBody.setUdpVideoPort(senderBody.getUdpVideoPort());

			receiverRequest.setHeader(requestHeader);
			receiverRequest.setBody(receiverBody);

			String strRequest = mapper.writeValueAsString(receiverRequest);

			logger.info("Sender Send Reject Packet to Receiver : " + strRequest);

			String strResult = null;

			try {
				strResult = tcpToWilliSend.send(strRequest, receiverCallInfo.getIp(), receiverCallInfo.getPort());

				logger.info("Receiver Reject Response Packet: " + strResult);

				TypeReference<TcpCallCommonVO<TcpCallSignalVO>> refRes = new TypeReference<TcpCallCommonVO<TcpCallSignalVO>>() {
				};

				TcpCallCommonVO<TcpCallSignalVO> resVo = mapper.readValue(strResult, refRes);

				TcpCallSignalVO resBody = resVo.getBody();

				switch (resBody.getCmd()) {
				case CallStatConst.CALLREJC2S:
					response.setCallId(callId);
					response.setCmd(CallStatConst.CALLREJS2C);

					// Update receiver status
					receiverCallInfo.setStatus(CallStatConst.CALL_STAT_CALL_EN);
					callRepository.updateCallStatusByPhoneNumber(receiverCallInfo);
					break;
				default:
					logger.info("Callee Responsed Abnormal Command --> Return Back !! ");
					response.setCmd(resBody.getCmd());
					response.setCalleePhoneNum(resBody.getCalleePhoneNum());
					response.setCallerPhoneNum(resBody.getCallerPhoneNum());
					response.setIpaddr(resBody.getIpaddr());
					response.setUdpAudioPort(resBody.getUdpAudioPort());
					response.setUdpVideoPort(resBody.getUdpVideoPort());
					
					// Update receiver status
					receiverCallInfo.setStatus(CallStatConst.CALL_STAT_CALL_EN);
					callRepository.updateCallStatusByPhoneNumber(receiverCallInfo);
					break;
				}
			} catch (Exception ee) {
				ee.printStackTrace();
				logger.error("Call To Receiver is Failed ");
			}

			if (cachedCallHistory != null) {
				CallHistoryCache.remove(callId + "");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			response.setCmd(CallStatConst.CALLFAILS2C);
			response.setCalleePhoneNum(senderBody.getCalleePhoneNum());
			response.setCallerPhoneNum(senderBody.getCallerPhoneNum());
			response.setCallId(0);

			if (cachedCallHistory != null) {
				CallHistoryCache.remove(callId + "");
			}
		}
		return response;
	}

}
