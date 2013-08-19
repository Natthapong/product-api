package th.co.truemoney.product.api.manager;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

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
public class TestEPINConfigurationManager {
	
	@Autowired
	private EPINConfigurationManager configuration;
	
	private static List<String> price = Arrays.asList("50", "90", "150", "300", "500", "1000");
		
	@Test
	public void testLoadConfigFile() {
		List<String> config = configuration.getEpinPrice();
		int i = 0;
		for (String epinPrice: config) {
			assertEquals(price.get(i++), epinPrice);
		}
	}

}
