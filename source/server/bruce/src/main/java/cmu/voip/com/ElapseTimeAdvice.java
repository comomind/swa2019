package cmu.voip.com;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ElapseTimeAdvice {
	private static final Logger logger = LogManager.getLogger(ElapseTimeAdvice.class);
	
	@Around("execution(* cmu.voip..*Controller.*(..)) or execution(* cmu.voip..*Service.*(..)) or execution(* cmu.voip..*Repository.*(..))")
	public Object calculateElapseTimeForService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
		
		long startTime = System.currentTimeMillis();
		String type = proceedingJoinPoint.getSignature().getDeclaringTypeName();
		String method = proceedingJoinPoint.getSignature().toShortString();
		String name = "";
		
		if(type.contains("Controller"))
			name = "Controller";
		else if(type.contains("Repository"))
			name = "Repository";
		else
			name = "Service";
		
		Object result = proceedingJoinPoint.proceed();
		long endTime = System.currentTimeMillis();
		logger.info("------------------------------------------------------------------------------------------------------");
		logger.info(name+"."+method + " ["+type+"] Running Time = ["+(endTime-startTime)+"] ms");
		logger.info("------------------------------------------------------------------------------------------------------");
		return result;
	}
}
