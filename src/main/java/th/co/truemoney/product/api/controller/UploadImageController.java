package th.co.truemoney.product.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.co.truemoney.product.api.domain.ProductResponse;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UploadImageController extends BaseController {

    private static final Integer MOBILE_CHANNEL_ID = 40;

    @RequestMapping(value = "/profile/avatar/{accessToken}", method = RequestMethod.POST)
    @ResponseBody
    public ProductResponse uploadImageProfile(@PathVariable String accessToken,
                                              @RequestParam("profile_image") MultipartFile file) {

		try {
			ImageIcon profileImage = corpAndResizeProfileImage(file.getBytes());

			writeJPGFile(profileImage);

		} catch (Exception ex) {
			System.out.println("Error : " + ex);
		}

        return this.responseFactory.createSuccessProductResonse(null);
    }

	private ImageIcon corpAndResizeProfileImage(byte[] originalImageBytes) {

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

	private void writeJPGFile(ImageIcon profileImage) {
		BufferedImage bImage = new BufferedImage(profileImage.getIconWidth(), profileImage.getIconWidth(), BufferedImage.TYPE_INT_RGB);

		// create a graphics context from the BufferedImage and draw the icon's image into it.
		Graphics g = bImage.createGraphics();
		g.drawImage(profileImage.getImage(), 0, 0, null);
		g.dispose();

		try {
			// create a file to write the image to (make sure it exists), then use the ImageIO class
			// to write the RenderedImage to disk as a PNG file.
			File imageFile = new File("/Users/codebanban/Desktop/myImageFile.png");
			imageFile.createNewFile();
			ImageIO.write(bImage, "jpg", imageFile);
		} catch (Exception ex) {
			System.out.println("Error : " + ex);
		}
	}

}
