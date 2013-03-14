package th.co.truemoney.product.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import th.co.truemoney.product.api.aspect.LoggingAspect;
import th.co.truemoney.product.api.aspect.ResponseHandlingAspect;
import th.co.truemoney.serviceinventory.service.LoginService;
import th.co.truemoney.serviceinventory.service.impl.LoginServiceImpl;

@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

	@Bean
	public LoggingAspect loggingAspect() {
		return new LoggingAspect();
	}

	@Bean
	public ResponseHandlingAspect responseHandlingAspect() {
		return new ResponseHandlingAspect();
	}

	@Bean
	public LoginService loginService() {
		return new LoginServiceImpl();
	}

}
