package th.co.truemoney.product.api.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import th.co.truemoney.product.api.config.TestWebConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public class TestProfileImageManager {

	@Autowired
	private ProfileImageManager profileImageManager;

	@Test
	public void testGenerateProfileURL() {

		String profileImageURL = profileImageManager.generateProfileImageURL("accesstoken", "testgenerateurl.jpg");
		assertNotSame("", profileImageURL);
	}
}
