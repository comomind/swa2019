package cmu.voip.account.service.impl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmu.voip.account.repository.UserDao;
import cmu.voip.account.service.UserService;
import cmu.voip.account.vo.User;

@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
	
	@Autowired
	UserDao userdao;
	
	@Override
	public User selectUserById(User user) {
		
		return userdao.selectUserById(user);
	}

}
