package th.co.truemoney.product.api.manager;

import static org.junit.Assert.assertTrue;

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
	
	private static List<Integer> priceList = Arrays.asList(50, 90, 150, 300, 500, 1000);
		
	@Test
	public void testLoadConfigFile() {
		List<Integer> config = configuration.getEpinPrice();
		for (Integer price: config) {
			assertTrue(priceList.contains(price));
		}
	}

}
