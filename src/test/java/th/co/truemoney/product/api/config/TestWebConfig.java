package th.co.truemoney.product.api.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import th.co.truemoney.product.api.util.MessageManager;
import th.co.truemoney.serviceinventory.ewallet.DirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;

@EnableWebMvc
@Configuration
@ComponentScan({"th.co.truemoney.product.api.controller", "th.co.truemoney.product.api.util"})
public class TestWebConfig {

	@Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

	@Bean
	public TmnProfileService profileService() {
		return Mockito.mock(TmnProfileService.class);
	}

	@Bean DirectDebitSourceOfFundService sourceOfFundService() {
		return Mockito.mock(DirectDebitSourceOfFundService.class);
	}

	@Bean
	public MessageManager messageManager() {
		return Mockito.mock(MessageManager.class);
	}

	@Bean
	public TopUpService topupService() {
		return Mockito.mock(TopUpService.class);
	}

	@Bean
	public P2PTransferService p2pTransferService() {
		return Mockito.mock(P2PTransferService.class);
	}

	@Bean @Qualifier("apiHost")
	public String apiHost() {
		return "http://localhost:8080";
	}

}
