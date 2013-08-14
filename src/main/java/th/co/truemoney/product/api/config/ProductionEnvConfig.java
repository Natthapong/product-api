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

	@Bean @Qualifier("profileImageURLFormat")
	public String profileImageURLFormat() {
		return this.mHost() + "/profile/image/%s/%s/?t=%d&vkey=%s";
	}

	@Bean @Qualifier("profileImageURLSalt")
	public String profileImageURLSalt() {
		return "nx4+6m^{XET.UD+jqdin(6Ls+%DNyHG7h{$A[`kJ,BMQ/NybqqS1u5)9ir9qO=?B";
	}

	@Bean @Qualifier("profileImageSavePath")
	public String profileImageSavePath() {
		return "/share/images/";
	}
}
