package org.citygml.test;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.citygml.textureAtlasAPI.imageIO.RGBEncoder;

public class RGBTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RGBEncoder en = new RGBEncoder();
		try {
			BufferedImage bi = en.readRGB("_tex2050070.rgb");
			File f=new File(
					bi.getType() == BufferedImage.TYPE_INT_ARGB ? "r.png"
							: "r.jpeg");
			ImageIO
					.write(
							bi,
							bi.getType() == BufferedImage.TYPE_INT_ARGB ? "png"
									: "jpeg",f);
			System.out.println("Finish!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
