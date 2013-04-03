package th.co.truemoney.product.api.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import th.co.truemoney.product.api.config.TestWebConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public class TestSecurityManager {
	
	@Autowired
	SecurityManager securityManager;
	
	@Test
	public void encryptRSASuccess() {
		//SecurityManager securityManager = new SecurityManager();
		assertNotNull(securityManager.getPublicKey());
	}

}
