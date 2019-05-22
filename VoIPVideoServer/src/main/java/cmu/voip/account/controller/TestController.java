package cmu.voip.account.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cmu.voip.account.vo.User;

@Controller
public class TestController {
	@RequestMapping(method=RequestMethod.GET, value="/test")
	public User selectUserById() {
		User user = new User();
		user.setId("1");
		
		System.out.println("Test");
		
		return user;
	}
}
