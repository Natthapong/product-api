package th.co.truemoney.product.api.aspect;

import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ResponseHandlingAspect {
	
	@AfterReturning(
	      pointcut = "execution(* th.co.truemoney.product.api.controller.UserActionController.login(..))",
	      returning= "result")
	public void logAfterReturning(JoinPoint joinPoint, Map<String, Object> result) {
		/*
		System.out.println("logAfterReturning() is running!");
		System.out.println("hijacked : " + joinPoint.getSignature().getName());
		System.out.println("Method returned value is : " + result);
		System.out.println("******");
		*/
		//Map<String, Object> resp = (Map<String, Object>)result;
		result.put("foo", "barrr");
   }
}
