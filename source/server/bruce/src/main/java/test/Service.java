package test;

import java.util.concurrent.CountDownLatch;


public class Service implements Runnable {
			
	private final String name;
	private final int timeToStart;
	private final CountDownLatch latch;
	
	public Service(String name, int timeToStart, CountDownLatch latch) {
		this.name = name;
		this.timeToStart = timeToStart;
		this.latch = latch;
	}
	
	@Override
	public void run() {
		try {
			for(int i=0;i<20;i++) {
				Thread.sleep(timeToStart);
				
				if(latch.getCount() > 0)
					latch.countDown();
				else
					System.out.println(Thread.currentThread().getName() + " , latch is finishied");
			}
		} catch (InterruptedException ex) {
			System.err.println(ex.toString());
		}
	}

}
