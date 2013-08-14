package th.co.truemoney.product.api.manager;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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
public class TestProfileImageManager {

	@Autowired
	private ProfileImageManager profileImageManager;
	
	@Test
	public void testGenerateProfileURL() {

		String profileImageURL = profileImageManager.generateProfileImageURL("accesstoken", "testgenerateurl.jpg");
		assertNotNull(profileImageURL);
	}
	
	@Test
	public void testReplaceProfileImage() throws IOException {
		//prepare
		FileUtils.copyFile(new File("src/test/resources/images/default.jpeg"), new File("target/upload/1/2/3/a/b/c/123abc.jpg"));
		
		byte[] imgBytes = FileUtils.readFileToByteArray(new File("src/test/resources/images/default.jpeg"));
		String newImageName = profileImageManager.replaceProfileImage("123abc.jpg", imgBytes);
		assertNotNull(newImageName);
		
		//clean up
		FileUtils.deleteDirectory(new File("target/upload"));
	}
}
