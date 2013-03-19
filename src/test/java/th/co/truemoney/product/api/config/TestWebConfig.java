package th.co.truemoney.product.api.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import th.co.truemoney.product.api.util.MessageManager;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;

@EnableWebMvc
@Configuration
@ComponentScan({"th.co.truemoney.product.api.controller"})
public class TestWebConfig {
	
	@Bean
	public TmnProfileService profileService() {
		return Mockito.mock(TmnProfileService.class);
	}
	
	@Bean SourceOfFundService sourceOfFundService() {
		return Mockito.mock(SourceOfFundService.class);
	}
	
	@Bean
	public MessageManager messageManager() {
		return Mockito.mock(MessageManager.class);
	}
	
	@Bean
	public TopUpService topupService() {
		return Mockito.mock(TopUpService.class);
	}
	
}
