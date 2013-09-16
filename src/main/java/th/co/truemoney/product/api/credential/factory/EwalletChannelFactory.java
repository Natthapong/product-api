package th.co.truemoney.product.api.credential.factory;

import org.springframework.stereotype.Component;

import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Component
public class EwalletChannelFactory {

	public static final int IOS_APP_CHANNEL_ID = 40;
	public static final int ANDROID_APP_CHANNEL_ID = 41;
	
	public Integer createCredential(String deviceType) {
		if ("android".equals(deviceType)) {
			return ANDROID_APP_CHANNEL_ID;
		} else if (deviceType != null && deviceType.startsWith("iP"))  {
			return IOS_APP_CHANNEL_ID;
		} else {
			throw new ServiceInventoryException();
		}
	}
	
}
