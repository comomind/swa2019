package cmu.voip.controller.http.account.vo;

import java.util.ArrayList;
import java.util.List;

public class RegisteredResultListVO<T> {
	
	RegisterUserResVO myInfo;
	
	public RegisterUserResVO getMyInfo() {
		return myInfo;
	}

	public void setMyInfo(RegisterUserResVO myInfo) {
		this.myInfo = myInfo;
	}

	List<T> list = new ArrayList<T>();

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "RegisteredResultListVO [myInfo=" + myInfo + ", list=" + list + "]";
	}
	
	
}
