package test;

import java.util.concurrent.CountDownLatch;

public class CountDownEntry {
	
	private final String name;
	private final CountDownLatch latch;
	
	public CountDownEntry(String name,CountDownLatch latch ) {
		this.name = name;
		this.latch = latch;
	}
	
	public String getName() {
		return name;
	}
	
	public CountDownLatch getCountDownLatch() {
		return latch;
	}
}
