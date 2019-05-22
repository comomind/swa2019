package cmu.voip.account.repository;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import cmu.voip.account.vo.User;

@Repository
public class UserDao {
	
	//@Resource(name="sqlSession")
	SqlSession sqlsession;
	
	public User selectUserById(User user) {
		User result = sqlsession.selectOne("voip.user.selectUserById",user);
		return result;
	}
}
