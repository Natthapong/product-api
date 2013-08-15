package th.co.truemoney.product.api.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: codebanban
 * Date: 8/13/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {

	public boolean saveImageJPGFile(String filePath, String fileName, ImageIcon image) throws IOException, Exception {

		try {
			File fPath = new File(filePath);
			if (!fPath.exists())
				fPath.mkdirs();

			// write JPG file
			BufferedImage bImage = new BufferedImage(image.getIconWidth(), image.getIconWidth(), BufferedImage.TYPE_INT_RGB);

			// create a graphics context from the BufferedImage and draw the icon's image into it.
			Graphics g = bImage.createGraphics();
			g.drawImage(image.getImage(), 0, 0, null);
			g.dispose();

			// create a file to write the image to (make sure it exists), then use the ImageIO class
			// to write the RenderedImage to disk as a PNG file.

			File imageFile = new File(filePath + fileName);
			imageFile.createNewFile();
			ImageIO.write(bImage, "jpg", imageFile);

			return true;
		} catch (IOException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		}
	}

	public boolean deleteFile(String filePath) {
		File imageFile = new File(filePath);

		return imageFile.delete();
	}
}
