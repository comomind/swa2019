package cmu.voip.service.call;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmu.voip.repository.account.UserAccountRepository;
import cmu.voip.repository.account.vo.UserAccountDTO;
import cmu.voip.service.TcpService;

@Service
public class TestService implements TcpService {
	
	private static final Logger logger = LogManager.getLogger(TestService.class);
	
	@Autowired
	UserAccountRepository userdao;
	
	@Override
	public String service(String input) {
		// TODO Auto-generated method stub
		
		List<UserAccountDTO> list = userdao.selectAllUsers();
		
		for(UserAccountDTO vo : list) {
			logger.debug("["+input+"]---------------------------> " + vo.toString());
		}
		
		return "success";
	}

}
