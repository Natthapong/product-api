package th.co.truemoney.product.api.credential.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Component
public class CredentialFactory {

	@Autowired @Qualifier("iOSAppLogin")
	private ClientCredential iOSAppLogin;
	
	@Autowired @Qualifier("androidAppLogin")
	private ClientCredential androidAppLogin;

	public ClientCredential createCredential(String deviceType) {
		if ("android".equals(deviceType)) {
			return androidAppLogin;
		} else if (deviceType != null && deviceType.startsWith("iP"))  {
			return iOSAppLogin;
		} else {
			throw new ServiceInventoryException();
		}
	}
}
