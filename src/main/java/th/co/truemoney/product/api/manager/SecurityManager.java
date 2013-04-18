package th.co.truemoney.product.api.manager;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
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