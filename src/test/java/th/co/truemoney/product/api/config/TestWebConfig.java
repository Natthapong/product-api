package th.co.truemoney.product.api.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.buy.BuyProductService;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.DirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;

@EnableWebMvc
@Configuration
@ComponentScan({
	"th.co.truemoney.product.api.credential",
	"th.co.truemoney.product.api.controller",
	"th.co.truemoney.product.api.manager",
	"th.co.truemoney.product.api.handler",
	"th.co.truemoney.product.api.util"})
public class TestWebConfig extends BaseEnvConfig {

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
	public TopUpService topupService() {
		return Mockito.mock(TopUpService.class);
	}

	@Bean
	public P2PTransferService p2pTransferService() {
		return Mockito.mock(P2PTransferService.class);
	}
	
	@Bean BillPaymentService billPaymentService() {
		return Mockito.mock(BillPaymentService.class);
	}
	
	@Bean TopUpMobileService topUpMobileService() {
		return Mockito.mock(TopUpMobileService.class);
	}
	
	@Bean ActivityService activityService() {
		return Mockito.mock(ActivityService.class);
	}

	@Bean FavoriteService favoriteService(){
		return Mockito.mock(FavoriteService.class);
	}

	@Bean TransactionAuthenService transactionAuthenService() {
		return Mockito.mock(TransactionAuthenService.class);
	}
	
	@Bean BuyProductService buyProductService() {
		return Mockito.mock(BuyProductService.class);
	}
	
	@Bean @Qualifier("apiHost")
	public String apiHost() {
		return "http://localhost:8080";
	}
	
	@Bean @Qualifier("mHost")
	public String mHost() {
		return "http://localhost:8080";
	}

	@Bean @Qualifier("profileImageURLFormat")
	public String profileImageURLFormat() {
		return this.mHost() + "/%s/%s/?t=%d&vkey=%s";
	}

	@Bean @Qualifier("profileImageURLSalt")
	public String profileImageURLSalt() {
		return "Pfu03-.G +W$T;k%*)Aw+-<R%5N<->>:8z08|q-y}Z~[r<&5)@H]DUzBF Q.e~2_";
	}

	@Bean @Qualifier("profileImageSavePath")
	public String profileImageSavePath() {
		return "target/profile_images/";
	}
	
	@Bean @Qualifier("iOSAppLogin")
	public ClientCredential iOSAppLogin() {
		return new ClientCredential("f7cb0d495ea6d989", "MOBILE_IPHONE", "IPHONE+1", "IOS_APP", "IOS_APP");
	}
	
	@Bean @Qualifier("androidAppLogin")
	public ClientCredential androidAppLogin() {
		return new ClientCredential("f7cb0d495ea6d989", "MOBILE_IPHONE", "IPHONE+1", "Android", "Android");
	}
	
}
