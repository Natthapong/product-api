package th.co.truemoney.product.api.aspect;

import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.ResponseParameter;
import th.co.truemoney.product.api.util.MessageManager;

@Aspect
public class ResponseHandlingAspect {
	
	@Autowired
	private MessageManager messageManager;
	
	@AfterReturning(
	      pointcut = "execution(* th.co.truemoney.product.api.controller.UserActionController.login(..))",
	      returning= "result")
	public void logAfterReturning(JoinPoint joinPoint, Map<String, Object> result) {
		String namespace = (String)result.get(ResponseParameter.NAMESPACE);
		String status = (String)result.get(ResponseParameter.STATUS);
		result.put(ResponseParameter.DESCRIPTION_EN, messageManager.getMessageEn(namespace, status));
		result.put(ResponseParameter.DESCRIPTION_TH, messageManager.getMessageTh(namespace, status));
   }
}
