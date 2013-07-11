package th.co.truemoney.product.api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevelopEnvConfig extends BaseEnvConfig {

	@Bean @Qualifier("apiHost")
	public String apiHost() {
		return "https://secure.truemoney-dev.com/api";
	}
	
	@Bean @Qualifier("mHost")
	public String mHost() {
		return "https://secure.truemoney-dev.com/m";
	}
}
