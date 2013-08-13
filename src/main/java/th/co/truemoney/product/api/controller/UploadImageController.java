package th.co.truemoney.product.api.controller;

import java.awt.Graphics;
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
import th.co.truemoney.product.api.manager.ProfileImageManager;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;

@Controller
public class UploadImageController extends BaseController {

    private static final Integer MOBILE_CHANNEL_ID = 40;

	@Autowired
	private TmnProfileService profileService;

	@Autowired
	private ProfileImageManager profileImageManager;

    @RequestMapping(value = "/profile/image/{accessToken}", method = RequestMethod.POST)
    @ResponseBody
    public ProductResponse uploadImageProfile(@PathVariable String accessToken,
                                              @RequestParam("profile_image") MultipartFile file) {

		TmnProfile tmnProfile = profileService.getTruemoneyProfile(accessToken);
		String currentImageName = tmnProfile.getImageFileName();

		try {
			String newImageName = profileImageManager.replaceProfileImage(currentImageName, file.getBytes());

			//Update New File name On Core system
			profileService.changeProfileImage(accessToken, newImageName);

		} catch (Exception ex) {
			System.out.println("Error : " + ex);
		}

        return this.responseFactory.createSuccessProductResonse(null);
    }

}
