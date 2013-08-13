package th.co.truemoney.product.api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class ProductionEnvConfig  extends BaseEnvConfig {
	
	@Bean @Qualifier("apiHost")
	public String apiHost() {
		return "https://api.truemoney.co.th";
	}
	
	@Bean @Qualifier("mHost")
	public String mHost() {
		return "https://m.truemoney.co.th";
	}

	@Bean @Qualifier("profileImageSavePath")
	public String profileImageSavePath() {
		return "profile_images/";
	}
}
