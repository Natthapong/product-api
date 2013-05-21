package th.co.truemoney.product.api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class ProductionEnvConfig {
	
	@Bean @Qualifier("apiHost")
	public String apiHost() {
		return "https://secure.truemoney-dev.com"; //TODO
	}
}
