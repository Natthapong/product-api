package th.co.truemoney.product.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.client.SourceOfFundServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;

@Configuration
public class AppConfig {

	@Bean
	public TmnProfileService profileService() {
		return new TmnProfileServiceClient();
	}

	@Bean
	public SourceOfFundService sourceOfFundService() {
		return new SourceOfFundServiceClient();
	}

}
