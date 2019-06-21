package test;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
	 public static void main(String args[]) {
	       final CountDownLatch latch1 = new CountDownLatch(5);
	       final CountDownLatch latch2 = new CountDownLatch(7);
	       final CountDownLatch latch3 = new CountDownLatch(10);
	       Thread cacheService = new Thread(new Service("CacheService", 1000, latch1));
	       CountDownEntry c1 = new CountDownEntry("CacheService", latch1);
	       CountDownThreadPool.push("CacheService",c1);
	       Thread alertService = new Thread(new Service("AlertService", 1000, latch2));
	       CountDownEntry c2 = new CountDownEntry("AlertService", latch2);
	       CountDownThreadPool.push("AlertService",c2);
	       Thread validationService = new Thread(new Service("ValidationService", 1000, latch3));
	       CountDownEntry c3 = new CountDownEntry("ValidationService", latch3);
	       CountDownThreadPool.push("ValidationService",c3);
	      
	       cacheService.start(); //separate thread will initialize CacheService
	       alertService.start(); //another thread for AlertService initialization
	       validationService.start();
	      
	       // application should not start processing any thread until all service is up
	       // and ready to do there job.
	       // Countdown latch is idle choice here, main thread will start with count 3
	       // and wait until count reaches zero. each thread once up and read will do
	       // a count down. this will ensure that main thread is not started processing
	       // until all services is up.
	      
	       //count is 3 since we have 3 Threads (Services)
	      
	       try{
	    	   for(int i=0;i<3;i++) {
					Thread.sleep(1000);
					System.out.println(CountDownThreadPool.get("CacheService").getCountDownLatch().getCount());
					System.out.println(CountDownThreadPool.get("AlertService").getCountDownLatch().getCount());
					System.out.println(CountDownThreadPool.get("ValidationService").getCountDownLatch().getCount());
				}
	    	   
	    	   latch1.await();
	    	   System.out.println("latch1 waked up");
	    	   latch2.await();
	    	   System.out.println("latch2 waked up");
	    	   latch3.await();
	    	   System.out.println("latch3 waked up");
	            System.out.println("All services are up, Application is starting now");
	       }catch(InterruptedException ie){
	           ie.printStackTrace();
	       }
	 }
	      
}
