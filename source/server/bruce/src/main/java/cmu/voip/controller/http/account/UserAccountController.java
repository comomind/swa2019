package cmu.voip.controller.http.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cmu.voip.controller.http.RestfulRequestVO;
import cmu.voip.controller.http.RestfulResponseVO;
import cmu.voip.controller.http.account.vo.RegisterUserInfo;
import cmu.voip.controller.http.account.vo.RegisterUserResVO;
import cmu.voip.controller.http.account.vo.RegisteredResultListVO;
import cmu.voip.repository.account.vo.UserAccountDTO;
import cmu.voip.repository.account.vo.UserCallInfoDTO;
import cmu.voip.repository.security.vo.UserTokenDTO;
import cmu.voip.service.account.UserAccountService;
import cmu.voip.service.call.CallService;
import cmu.voip.service.security.TokenService;

@RestController
public class UserAccountController {

	private static final Logger logger = LogManager.getLogger(UserAccountController.class);

	@Resource
	UserAccountService userAccountService;
	
	@Resource
	CallService callService;

	@Resource
	TokenService tokenService;

	@RequestMapping(method = RequestMethod.POST, value = "/user/register")
	public RestfulResponseVO registerUser(@RequestBody RestfulRequestVO<RegisterUserInfo> user) {

		logger.debug("[CONTROLLER] Register USER" + user.toString());

		UserAccountDTO vo = new UserAccountDTO();

		RegisterUserInfo rui = user.getBody();

		vo.setEmail(rui.getEmail());
		vo.setName(rui.getName());
		vo.setLevel(1);
		vo.setPassword(rui.getPassword());
		vo.setSecurityanswer(rui.getSecurityAnswer());
		vo.setSecurityquestion(rui.getSecurityQuestion());

		int result;

		try {
			result = userAccountService.insertUser(vo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = 0;
		}

		RestfulResponseVO resultvo = new RestfulResponseVO();
		RegisterUserResVO resvo = new RegisterUserResVO();
		String clientResVo = "RegisterResult";

		if (result == 1) {
			resultvo.setResult(1);
			resultvo.setType(clientResVo);
			resultvo.setMessage("Register Success");
			resvo.setEmail(vo.getEmail());
			resvo.setName(vo.getName());
			resvo.setPhoneNum(vo.getPhonenum());
			resultvo.setBody(resvo);
		} else {
			resultvo.setResult(0);
			resultvo.setMessage("Register Failed");
		}
		return resultvo;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/user/login")
	public RestfulResponseVO loginUser(@RequestBody RestfulRequestVO<RegisterUserInfo> user) {
		
		logger.debug("[CONTROLLER] login USER" + user.toString());
		
		UserAccountDTO vo = new UserAccountDTO();

		RegisterUserInfo rui = user.getBody();

		vo.setEmail(rui.getEmail());
		vo.setPassword(rui.getPassword());

		RestfulResponseVO resultvo = new RestfulResponseVO();

		try {
			UserAccountDTO result = userAccountService.selectUserForLogin(vo);

			if (result != null) {
				
				UserTokenDTO has = tokenService.selectTokenByEmail(result.getEmail());

				String strToken = null;

				if (has != null) {
					UserTokenDTO token = new UserTokenDTO();

					token.setEmail(result.getEmail());
					String strtoken = TokenService.getTokenUser(result.getName());

					token.setToken(strtoken);
					token.setStatus(1);
					token.setDuration(TokenService.getDuration());

					int updated = tokenService.updateTokenByEmail(token);

					if (updated > 0)
						strToken = strtoken;
					else
						strToken = null;
				} else {
					UserTokenDTO token = new UserTokenDTO();
					token.setEmail(result.getEmail());
					strToken = tokenService.insertToken(token, result.getName());
				}
				
				UserCallInfoDTO callinfo = new UserCallInfoDTO();
				callinfo.setPhonenum(result.getPhonenum());
				callinfo.setIp(rui.getIp());
				callinfo.setPort(rui.getPort());
				callinfo.setStatus(0);
				callinfo.setLastcall(new Date());
				
				callService.registerCallInfo(result.getPhonenum(),callinfo);
				
				if (strToken != null) {

					logger.debug("callinfo ------------> " + callinfo.toString());
					
					List<UserAccountDTO> list = userAccountService.selectAllUsers();

					resultvo.setToken(strToken);

					RegisteredResultListVO<RegisterUserResVO> invo = new RegisteredResultListVO<RegisterUserResVO>();

					ArrayList<RegisterUserResVO> inList = new ArrayList<RegisterUserResVO>();

					String clientResVo = "LoginResult";
					resultvo.setType(clientResVo);
					for (UserAccountDTO v : list) {
						RegisterUserResVO svo = new RegisterUserResVO();
						svo.setEmail(v.getEmail());
						svo.setName(v.getName());
						svo.setPhoneNum(v.getPhonenum());
						svo.setLoginStatus(v.getStatus());
						inList.add(svo);
					}
					
					RegisterUserResVO myinfo = new RegisterUserResVO();
					
					myinfo.setEmail(result.getEmail());
					myinfo.setName(result.getName());
					myinfo.setPhoneNum(result.getPhonenum());
					
					invo.setList(inList);
					invo.setMyInfo(myinfo);
					
					resultvo.setResult(1);
					resultvo.setMessage("Login Success");
					resultvo.setBody(invo);
				}else {
					resultvo.setResult(0);
					resultvo.setMessage("Login Failed (Token is not issued)");
				}
			} else {
				resultvo.setResult(0);
				resultvo.setMessage("Login Failed (Not Registered)");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			resultvo.setResult(0);
			resultvo.setMessage("Login Failed (System Error)");
		}
		
		return resultvo;
	}
}
