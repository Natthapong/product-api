package th.co.truemoney.product.api.controller;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;

@Controller
public class UploadImageController extends BaseController {

    private static final Integer MOBILE_CHANNEL_ID = 40;


	@Autowired
	@Qualifier("profileImageSavePath")
	private String profileImageSavePath;

	@Autowired
	private TmnProfileService profileService;

    @RequestMapping(value = "/profile/image/{accessToken}", method = RequestMethod.POST)
    @ResponseBody
    public ProductResponse uploadImageProfile(@PathVariable String accessToken,
                                              @RequestParam("profile_image") MultipartFile file) {

		TmnProfile tmnProfile = profileService.getTruemoneyProfile(accessToken);
		String currentImageName = tmnProfile.getImageFileName();

		try {
			ImageIcon profileImage = cropAndResizeProfileImage(file.getBytes());

			//Generate New File name
			String newImageName = UUID.randomUUID().toString().replace("-", "").toLowerCase() + ".jpg";

			//Write New File
			saveProfileImage(newImageName, profileImage);

			//Delete Current Name
			deleteOldProfileImage(currentImageName);

			//Update New File name On Core system
			profileService.changeProfileImage(accessToken, newImageName);

		} catch (Exception ex) {
			System.out.println("Error : " + ex);
		}

        return this.responseFactory.createSuccessProductResonse(null);
    }

	private ImageIcon cropAndResizeProfileImage(byte[] originalImageBytes) {

		int resizePixel = 200;

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
		if (profileWidth > resizePixel || profileHeight > resizePixel) {
			//--- Resize Profile Image ---//
			Image img = profileImage.getImage() ;
			Image newImage = img.getScaledInstance( resizePixel, resizePixel,  java.awt.Image.SCALE_SMOOTH ) ;

			profileImage = new ImageIcon( newImage );
		}

		return profileImage;
	}

	private void saveProfileImage(String imageName, ImageIcon image) {

		String imagePath = generateProfileImagePath(imageName);

		File tempDir = new File(imagePath);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		// write JPG file
		BufferedImage bImage = new BufferedImage(image.getIconWidth(), image.getIconWidth(), BufferedImage.TYPE_INT_RGB);

		// create a graphics context from the BufferedImage and draw the icon's image into it.
		Graphics g = bImage.createGraphics();
		g.drawImage(image.getImage(), 0, 0, null);
		g.dispose();

		try {
			// create a file to write the image to (make sure it exists), then use the ImageIO class
			// to write the RenderedImage to disk as a PNG file.
			String fileStringPath = imagePath + imageName;
			File imageFile = new File(fileStringPath);
			imageFile.createNewFile();
			ImageIO.write(bImage, "jpg", imageFile);
		} catch (Exception ex) {
			System.out.println("Error : " + ex);
		}
	}

	private void deleteOldProfileImage(String imageName) {
		if ("".equals(imageName))
			return;

		String imagePath = generateProfileImagePath(imageName);

		File imageFile = new File(imagePath + imageName);
		imageFile.delete();
	}

	private String generateProfileImagePath(String imageName) {

		StringBuilder imagePath = new StringBuilder("");
		String fname = imageName.substring(0, imageName.indexOf("."));

		for (int i=0; i<fname.length()-1; ++i) {
			imagePath.append( fname.charAt(i) + "/" );
		}

		return profileImageSavePath + imagePath.toString();
	}
}
