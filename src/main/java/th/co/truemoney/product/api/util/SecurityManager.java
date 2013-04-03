package th.co.truemoney.product.api.util;

import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

@Component
public class SecurityManager implements MessageSourceAware {

	@Autowired
	private MessageSource messageSource;
	
	public String getPublicKey() {
		return this.messageSource.getMessage("publicKey", null, null);
	}
	
	public String encryptRSA(String password) {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			byte[] input = password.getBytes("UTF-8");

			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");

			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
					hex2Byte(getPublicKey()));
			RSAPublicKey pubKey = (RSAPublicKey) keyFactory
					.generatePublic(publicKeySpec);

			Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");

			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] cipherText = cipher.doFinal(input);
			// System.out.println("encrypted: " + byte2Hex(cipherText)); //
			// 48a29d8f292cc466a077acc712d9581b757143357e5d5b91628143629e9d3ff1fa62a14f8b86bcd49545c115f89b7e4e64973b0b2389a97213f1ea2f80d132af2d30c1d0d038b91d2dcdc654a6abcbbb6a6335647d1014d5a06ebf2505227c1f546ed8f9471a9ea74b046711068a46501698bd04adac3b72c3c62a973eaee28e
			return byte2Hex(cipherText);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	private String byte2Hex(byte[] src_byte) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < src_byte.length; i++) {
			String temp = Integer.toHexString((int) src_byte[i]);
			if (temp.length() > 2) {
				temp = temp.substring(temp.length() - 2, temp.length());
			} else if (temp.length() < 2) {
				temp = "0" + temp;
			}
			sb.append(temp);
		}
		return sb.toString().trim().toLowerCase();
	}

	private byte[] hex2Byte(String src_hex) {
		byte[] des_byte = new byte[src_hex.length() / 2];
		for (int i = 0, j = 0; i < src_hex.length(); i += 2, j++) {
			des_byte[j] = (byte) Integer.parseInt(src_hex.substring(i, i + 2),
					16);
		}
		return des_byte;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}