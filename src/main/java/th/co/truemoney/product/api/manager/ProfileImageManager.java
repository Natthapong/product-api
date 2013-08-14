package th.co.truemoney.product.api.manager;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import th.co.truemoney.product.api.util.FileUtil;
import th.co.truemoney.product.api.util.Utils;

@Component
public class ProfileImageManager {

	@Autowired
	@Qualifier("profileImageSavePath")
	private String profileImageSavePath;

	@Autowired
	@Qualifier("profileImageURLFormat")
	private String profileImageURLFormat;

	@Autowired
	@Qualifier("profileImageURLSalt")
	private String profileImageSalt;

	public String generateProfileImageURL(String accessToken, String imageName) {
		
		if (StringUtils.hasText(imageName)) {
			String imageNameNoExtension = extractFileName(imageName);
			long nowMilliTime = System.currentTimeMillis();
			String vKey = Utils.hashSHA1( String.format("%s%d%s%s", accessToken, nowMilliTime, imageNameNoExtension, profileImageSalt) );

			return String.format(profileImageURLFormat, "%@", imageNameNoExtension, nowMilliTime, vKey.toLowerCase());
		} else {
			return "";
		}
		
	}

	public String replaceProfileImage(String currentImageName, byte[] newProfileImageByte) throws IOException {

		String newImageName = generateNewImageName();
		String newImageFilePath = generateProfileImagePath(newImageName);
		String currentImageFilePath = generateProfileImagePath(currentImageName);

		ImageIcon newProfileImage = cropAndResizeProfileImage(newProfileImageByte);

		FileUtil fileUtil = new FileUtil();
		boolean status = fileUtil.saveImageJPGFile(newImageFilePath, newImageName, newProfileImage);

		if (!status)
			throw new IOException();

		if (currentImageFilePath != null && !"".equals(currentImageFilePath))
			fileUtil.deleteFile(currentImageFilePath + currentImageName);

		return newImageName;
	}

	private String generateNewImageName() {
		return UUID.randomUUID().toString().replace("-", "").toLowerCase() + ".jpg";
	}
	
	private static int TARGET_IMAGE_PIXEL = 200;
	
	private ImageIcon cropAndResizeProfileImage(byte[] originalImageBytes) {

		ImageIcon profileImage = new ImageIcon(originalImageBytes);
		int profileWidth = profileImage.getIconWidth();
		int profileHeight = profileImage.getIconHeight();

		//Check Image is square : if not Crop center
		if (profileHeight != profileWidth) {
			int newPosX = 0;
			int newPosY = 0;
			int newWidth = 0;
			int newHeight = 0;

			if (profileWidth > profileHeight) {
				newPosX = (profileWidth - profileHeight) / 2;
				newWidth = profileHeight;
				newHeight = profileHeight;
			} else if (profileHeight > profileWidth) {
				newPosY = (profileHeight - profileWidth) / 2;
				newWidth = profileWidth;
				newHeight = profileWidth;
			}

			BufferedImage bImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

			// create a graphics context from the BufferedImage and draw the icon's image into it.
			Graphics g = bImage.createGraphics();
			g.drawImage(profileImage.getImage(), -newPosX, -newPosY, null);
			g.dispose();

			profileImage = new ImageIcon(bImage);
			profileWidth = profileImage.getIconWidth();
			profileHeight = profileImage.getIconHeight();
		}

		//Check Image width/height > limit
		if (profileWidth > TARGET_IMAGE_PIXEL || profileHeight > TARGET_IMAGE_PIXEL) {
			//--- Resize Profile Image ---//
			Image img = profileImage.getImage() ;
			Image newImage = img.getScaledInstance( TARGET_IMAGE_PIXEL, TARGET_IMAGE_PIXEL, Image.SCALE_SMOOTH ) ;

			profileImage = new ImageIcon( newImage );
		}

		return profileImage;
	}

	private String generateProfileImagePath(String imageName) {
		if (StringUtils.hasText(imageName)) {
			StringBuilder imagePath = new StringBuilder("");
			String fname = extractFileName(imageName);
			
			for (int i=0; i<fname.length()-1; ++i) {
				imagePath.append( fname.charAt(i) + "/" );
			}
			return profileImageSavePath + imagePath.toString();
		} else {
			return "";
		}
	}
	
	private String extractFileName(String filenameWithExtension) {
		String[] ff = filenameWithExtension.split(".");
		if (ff.length > 0) {
			return ff[0];
		} else {
			return "";
		}
	}
}
