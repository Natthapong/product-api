package th.co.truemoney.product.api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalEnvConfig {

	@Bean @Qualifier("apiHost")
	public String apiHost() {
		return "http://localhost:8080";
	}
}
