package th.co.truemoney.product.api.config;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.product.api.util.MessageManager;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;

@Configuration
public class TestAppConfig {
	
	@Bean
	public TmnProfileService profileService() {
		return mock(TmnProfileService.class);
	} 
	
	@Bean 
	public MessageManager messageManager() {
		return mock(MessageManager.class);
	}
}
