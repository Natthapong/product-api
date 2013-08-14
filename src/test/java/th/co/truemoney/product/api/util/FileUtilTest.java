package th.co.truemoney.product.api.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

public class FileUtilTest {
	
	private FileUtil util = new FileUtil();	
	
	private String sourceFolder = "src/test/resources/";
	
	private String targetFolder = "target/upload/";
	
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(new File(targetFolder));
	}
	
	@Test
	public void testSaveImageJPGFile() throws IOException {
		util.saveImageJPGFile(targetFolder + "a/b/c/d/", "abcd.jpg", new ImageIcon(sourceFolder + "/images/default.jpeg"));
		
		assertTrue(new File(targetFolder + "a/b/c/d/abcd.jpg").exists());
	}

	@Test
	public void testDeleteFile() throws IOException {
		FileUtils.copyFile(new File(sourceFolder + "/images/default.jpeg"), new File(targetFolder + "a/e/f/g/aefg.jpg"));
		
		util.deleteFile(targetFolder + "a/e/f/g/aefg.jpg");
		
		assertFalse(new File(targetFolder + "a/e/f/g/aefg.jpg").exists());
	}

}
