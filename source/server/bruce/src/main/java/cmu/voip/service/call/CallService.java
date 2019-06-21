package cmu.voip.service.call;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmu.voip.repository.account.UserAccountRepository;
import cmu.voip.repository.account.vo.UserCallInfoDTO;
import cmu.voip.repository.call.CallRepository;

@Service
public class CallService {
	
	@Autowired
	CallRepository callRepository;
	
	public boolean registerCallInfo(String phoneNum,UserCallInfoDTO vo) throws Exception {
		boolean result=false;
		
		vo.setLastcall(new Date());
		
		UserCallInfoDTO call = callRepository.selectUserCallInfoByPhonenum(phoneNum);
		
		if(call == null || call.getPhonenum() == null) {
			callRepository.insertUserCallInfo(vo);
		}else {
			callRepository.updateCallInfoByPhoneNumber(vo);
		}
		
		return result;
	}
}
