package cmu.voip.repository.account;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import cmu.voip.controller.http.account.vo.UserNetworkInfoVO;
import cmu.voip.repository.account.vo.UserAccountDTO;

@Repository
public class UserAccountRepository {
	@Resource(name="sqlSession")
	SqlSession sqlsession;
	
	public int insertUser(UserAccountDTO user) throws Exception  {
		int result = sqlsession.insert("voip.user.insertUser", user);
		return result;
	}
	
	public UserAccountDTO selectUserByEmail(String email) {
		return sqlsession.selectOne("voip.user.selectUserByEmail", email);
	}
	
	public UserAccountDTO selectUserByPhoneNumber(String phonenum) {
		return sqlsession.selectOne("voip.user.selectUserByPhoneNum", phonenum);
	}
	
	public UserAccountDTO selectUserForLogin(UserAccountDTO user) {
		return sqlsession.selectOne("voip.user.selectUserForLogin", user);
	}
	
	public List<UserAccountDTO> selectAllUsers(){
		return sqlsession.selectList("voip.user.selectAllUsers");
	}
	
	public long selectCountWithPhonenum(String phonenum) {
		Map<String, Long> result = sqlsession.selectOne("voip.user.selectCountWithPhoneNum", phonenum);
		
		Set<String> keySet = result.keySet();
		
		Iterator<String> it = (Iterator<String>) keySet.iterator();
		
		while(it.hasNext()) {
			String key = it.next();
			System.out.println("----------------------> " + key);
		}
		
		return result.get("COUNT").longValue();
	}
	
	public List<UserNetworkInfoVO> selectAllNetworkUsers(){
		return sqlsession.selectList("voip.user.selectAllUserNetworkInfo");
	}
}
