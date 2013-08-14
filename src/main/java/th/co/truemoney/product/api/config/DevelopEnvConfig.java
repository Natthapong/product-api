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

	@Bean @Qualifier("profileImageURLFormat")
	public String profileImageURLFormat() {
		return this.mHost() + "/profile/image/%s/%s/?t=%d&vkey=%s";
	}

	@Bean @Qualifier("profileImageURLSalt")
	public String profileImageURLSalt() {
		return "vI;-^FG1-_@@fj~#9sy{NK6-V^#N6mLHcFyEa2JL-0*Z9m06j,gCIG9#1i$EE--^";
	}

	@Bean @Qualifier("profileImageSavePath")
	public String profileImageSavePath() {
		return "/share/images/";
	}
}
