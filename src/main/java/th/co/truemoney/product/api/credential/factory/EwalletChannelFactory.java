package th.co.truemoney.product.api.credential.factory;

import org.springframework.stereotype.Component;

@Component
public class EwalletChannelFactory {

	public static final int IOS_APP_CHANNEL_ID = 40;
	public static final int ANDROID_APP_CHANNEL_ID = 41;
	
	public Integer createCredential(String deviceOS) {
		if ("android".equals(deviceOS)) {
			return ANDROID_APP_CHANNEL_ID;
		} else {
			return IOS_APP_CHANNEL_ID;
		} 
	}
	
}
