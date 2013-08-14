package th.co.truemoney.product.api.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.Security;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.StringUtils;

import th.co.truemoney.product.api.config.TestWebConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public class SecurityManagerTest {
	
	@Autowired
	private SecurityManager manager;
	
	@BeforeClass
	public static void setup() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	@Test
	public void testGetPublicKey() {
		assertTrue(StringUtils.hasLength(manager.getPublicKey()));
	}

	@Test
	public void testGetPrivateKey() {
		assertTrue(StringUtils.hasLength(manager.getPrivateKey()));
	}

	@Test
	public void testDecryptRSA() {
		String cipherText = "3e52c494b0edf66fb4e60e30b68ffaa65726fd81e8be0a01cb15d71b27da95eab598d840e6444b430ef85ec9ae17a25af847d62d85aac35ed387d29347745fe9acc3c72eca6d7fde2c63f217f588bb5128a50669956a69a42abd83501e38e77e6ddfb2f79f994488d8f89f2d748b5f77fb8f77cfb4231f175e83fb073eef1e99";
		String normalText = manager.decryptRSA(cipherText);
		assertEquals("thisisapassword", normalText);
	}

	@Test
	public void testEncryptRSA() {
		String cipherText = manager.encryptRSA("thisisapassword");
		assertTrue(StringUtils.hasLength(cipherText));
	}

}
