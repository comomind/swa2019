package cmu.voip.account.service;

import cmu.voip.account.vo.User;

public interface UserService {
	public abstract User selectUserById(User user);
}
