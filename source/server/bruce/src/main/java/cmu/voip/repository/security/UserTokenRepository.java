package cmu.voip.repository.security;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import cmu.voip.repository.account.vo.UserAccountDTO;
import cmu.voip.repository.security.vo.UserTokenDTO;

@Repository
public class UserTokenRepository {
	
	@Resource(name="sqlSession")
	SqlSession sqlsession;
	
	public int insertTokenEachUser(UserTokenDTO token) throws Exception  {
		int result = sqlsession.insert("voip.user.insertTokenEachUser", token);
		return result;
	}
	
	public UserTokenDTO selectTokenByEmail(String email) throws Exception  {
		UserTokenDTO result = sqlsession.selectOne("voip.user.selectTokenByEmail", email);
		return result;
	}
	
	public UserTokenDTO selectTokenByToken(String token) throws Exception  {
		UserTokenDTO result = sqlsession.selectOne("voip.user.selectTokenByToken", token);
		return result;
	}
	
	public int updateTokenByEmail(UserTokenDTO token) throws Exception  {
		int result = sqlsession.update("voip.user.updateTokenByEmail", token);
		return result;
	}
	
	public int updateTokenStatusByEmail(UserTokenDTO token) throws Exception  {
		int result = sqlsession.update("voip.user.updateTokenStatusByEmail", token);
		return result;
	}
	
	public int updateTokenLogffByEmail(UserTokenDTO token) throws Exception  {
		int result = sqlsession.update("voip.user.updateTokenLogffByEmail", token);
		return result;
	}
	public List<UserTokenDTO> selectAllTokens(){
		return sqlsession.selectList("voip.user.selectAllTokens");
	}
}
