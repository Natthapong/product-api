package th.co.truemoney.product.api.credential.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;

@Component
public class CredentialFactory {

	@Autowired @Qualifier("iOSAppLogin")
	private ClientCredential iOSAppLogin;
	
	@Autowired @Qualifier("androidAppLogin")
	private ClientCredential androidAppLogin;

	public ClientCredential createCredential(String deviceOS) {
		if ("android".equals(deviceOS)) {
			return androidAppLogin;
		} else {
			return iOSAppLogin;
		} 
	}
}
