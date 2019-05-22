package cmu.voip.account.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cmu.voip.account.service.UserService;
import cmu.voip.account.vo.User;

@RestController
public class UserController {

	@Resource
	private UserService userService;
	
	@RequestMapping(method=RequestMethod.POST, value="/user/list/{id}")
	public User selectUserById(@PathVariable String id) {
		User user = new User();
		user.setId(id);
		
		User result = userService.selectUserById(user);
		
		return result;
	}
}
