package th.co.truemoney.product.api.manager;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SecurityManager {

	@Autowired
	private Environment env;
	
	public String getPublicKey() {
		return env.getProperty("publicKey");
	}

	public String getPrivateKey() {
		return env.getProperty("privateKey");
	}

	public String decryptRSA(String encryptedTxt) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");

			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(hex2Byte(getPrivateKey()));
			RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

			Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");

			cipher.init(Cipher.DECRYPT_MODE, privKey);
			byte[] plainText = cipher.doFinal(hex2Byte(encryptedTxt));

			return new String(plainText, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String encryptRSA(String password) {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		try {
			byte[] input = password.getBytes("UTF-8");

			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");

			EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(hex2Byte(getPublicKey()));
			PublicKey pubKey = keyFactory.generatePublic(publicKeySpec);

			Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");

			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] cipherText = cipher.doFinal(input);
			return byte2Hex(cipherText);
		} catch (Exception e) {
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

}