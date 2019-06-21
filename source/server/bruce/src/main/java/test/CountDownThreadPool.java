package test;

import java.util.concurrent.ConcurrentHashMap;

public class CountDownThreadPool {
	
	private static ConcurrentHashMap<String, CountDownEntry> threadMap = new ConcurrentHashMap<String, CountDownEntry>();
	
	public static void push(String key, CountDownEntry t) {
		threadMap.put(key, t);
	}
	
	public static CountDownEntry get(String key) {
		return threadMap.get(key);
	}
}
