package th.co.truemoney.product.api.aspect;

import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.util.MessageManager;
import th.co.truemoney.product.api.util.ResponseParameter;

@Aspect
public class ResponseHandlingAspect {
	
	@Autowired
	private MessageManager messageManager;
	
	@AfterReturning(
	      pointcut = "within(th.co.truemoney.product.api.controller..*)",
	      returning= "result")
	public void logAfterReturning(JoinPoint joinPoint, Map<String, Object> result) {
		String namespace = (String)result.get(ResponseParameter.NAMESPACE);
		String status = (String)result.get(ResponseParameter.STATUS);
		result.put(ResponseParameter.DESCRIPTION_EN, messageManager.getMessageEn(namespace, status));
		result.put(ResponseParameter.DESCRIPTION_TH, messageManager.getMessageTh(namespace, status));
   }
}
