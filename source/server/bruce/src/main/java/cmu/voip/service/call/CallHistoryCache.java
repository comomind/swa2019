package cmu.voip.service.call;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cmu.voip.repository.call.vo.VoiceHistoryDTO;

public class CallHistoryCache {
	public static ConcurrentHashMap<String, VoiceHistoryDTO> callQueue = new ConcurrentHashMap<String, VoiceHistoryDTO>();
	
	public static void put(String key, VoiceHistoryDTO input) {
		callQueue.put(key, input);
	}
	
	public static VoiceHistoryDTO getByKey(String key) {
		return callQueue.get(key);
	}
	
	public static boolean remove(String key) {
		VoiceHistoryDTO result = callQueue.remove(key);
		if(result != null)
			return true;
		else
			return false;
	}
	
	public static synchronized VoiceHistoryDTO findCallByPhoneNumber(String phoneNum) {
		Set<String> keySet = callQueue.keySet();
		
		Iterator<String> it = keySet.iterator();
		
		VoiceHistoryDTO result = null;
		
		while(it.hasNext()) {
			String key = it.next();
			
			VoiceHistoryDTO tmp =  callQueue.get(key);
			
			if(tmp.getCallee().equals(phoneNum) || tmp.getCaller().equals(phoneNum)){
				result = tmp;
				break;
			}
		}
		
		return result;
	}
}
