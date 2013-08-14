package th.co.truemoney.product.api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalEnvConfig  extends BaseEnvConfig {

	@Bean @Qualifier("apiHost")
	public String apiHost() {
		return "http://localhost:8080/api";
	}
	
	@Bean @Qualifier("mHost")
	public String mHost() {
		return "http://localhost:8080/m";
	}

	@Bean @Qualifier("profileImageURLFormat")
	public String profileImageURLFormat() {
		return this.mHost() + "/profile/image/%s/%s/?t=%d&vkey=%s";
	}

	@Bean @Qualifier("profileImageURLSalt")
	public String profileImageURLSalt() {
		return "Pfu03-.G +W$T;k%*)Aw+-{R%5N{-}}:8z08|q-y}Z~[r{$5)@H]DUzBF Q.e~2_";
	}

	@Bean @Qualifier("profileImageSavePath")
	public String profileImageSavePath() {
		return "profile_images/";
	}

}
