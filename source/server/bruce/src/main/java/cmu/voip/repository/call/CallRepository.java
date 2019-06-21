package cmu.voip.repository.call;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import cmu.voip.repository.account.vo.UserCallInfoDTO;
import cmu.voip.repository.call.vo.VoiceHistoryDTO;

@Repository
public class CallRepository {
	@Resource(name="sqlSession")
	SqlSession sqlsession;
	
	public int insertUserCallInfo(UserCallInfoDTO call) throws Exception  {
		int result = sqlsession.insert("voip.user.insertUserCallInfo", call);
		return result;
	}
	
	public UserCallInfoDTO selectUserCallInfoByPhonenum(String phonenum)  throws Exception{
		return sqlsession.selectOne("voip.user.selectUserCallInfoByPhonenum", phonenum);
	}
	
	public int updateCallInfoByPhoneNumber(UserCallInfoDTO call) throws Exception  {
		int result = sqlsession.update("voip.user.updateCallInfoByPhoneNumber", call);
		return result;
	}
	public int updateCallLastDateByPhoneNumber(UserCallInfoDTO call) throws Exception  {
		int result = sqlsession.update("voip.user.updateCallLastDateByPhoneNumber", call);
		return result;
	}
	public int updateCallStatusByPhoneNumber(UserCallInfoDTO call) throws Exception  {
		int result = sqlsession.update("voip.user.updateCallStatusByPhoneNumber", call);
		return result;
	}
	
	public int insertVoiceCallHistory(VoiceHistoryDTO input) throws Exception {
		int result = sqlsession.insert("voip.user.insertVoiceCallHistory", input);
		return result;
	}
	
	public VoiceHistoryDTO selectVoiceCallHistoryById(long id) throws Exception{
		return sqlsession.selectOne("voip.user.selectVoiceCallHistoryById", id);
	}
	
	public int updateVoiceCallStatusHistory(VoiceHistoryDTO input) throws Exception  {
		int result = sqlsession.update("voip.user.updateVoiceCallStatusHistory", input);
		return result;
	}
	
	public int updateVoiceCallClosedHistory(VoiceHistoryDTO input) throws Exception  {
		int result = sqlsession.update("voip.user.updateVoiceCallClosedHistory", input);
		return result;
	}
	
	public long getSequenceCallID() throws Exception{
		return sqlsession.selectOne("voip.user.getSequenceCallID");
	}
}
