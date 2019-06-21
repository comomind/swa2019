package cmu.voip.service.account;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cmu.voip.com.StringUtility;
import cmu.voip.controller.http.account.vo.UserNetworkInfoVO;
import cmu.voip.repository.account.UserAccountRepository;
import cmu.voip.repository.account.vo.UserAccountDTO;

@Service
public class UserAccountService {
	private static final Logger logger = LogManager.getLogger(UserAccountService.class);

	@Autowired
	UserAccountRepository userdao;
	
	@Transactional
	public int insertUser(UserAccountDTO vo) throws Exception {
		
		logger.debug("Start Allocate Phone Number for "+vo.getEmail());
		
		int result = userdao.insertUser(vo);
		UserAccountDTO inserted = userdao.selectUserByEmail(vo.getEmail());
		
		vo.setPhonenum(StringUtility.getString0LeftPadding(inserted.getPhonenum(),4));
		
		logger.debug("INSERT USER : " + vo.toString() + " : " + result);

		return result;
	}
	
	public UserAccountDTO selectUserForLogin(UserAccountDTO vo) throws Exception{
		return userdao.selectUserForLogin(vo);
	}
	
	public UserAccountDTO selectUserByEmail(String email) {
		return userdao.selectUserByEmail(email);
	}
	
	public List<UserAccountDTO> selectAllUsers(){
		return userdao.selectAllUsers();
	}
	
	public List<UserNetworkInfoVO> selectAllNetworkUsers(){
		return userdao.selectAllNetworkUsers();
	}
}
