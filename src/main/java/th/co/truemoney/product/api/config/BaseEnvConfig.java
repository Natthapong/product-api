package th.co.truemoney.product.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:rsa_public.key")
public class BaseEnvConfig {

	@Autowired
	Environment env;
	
}
