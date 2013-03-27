package th.co.truemoney.product.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.client.P2PTransferServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnSourceOfFundServiceClient;

@Configuration
public class AppConfig {

	@Bean
	public TmnProfileService profileService() {
		return new TmnProfileServiceClient();
	}

	@Bean
	public SourceOfFundService sourceOfFundService() {
		return new TmnSourceOfFundServiceClient();
	}
	
	@Bean
	public P2PTransferService p2pTransferService() {
		return new P2PTransferServiceClient();
	}
	
}
