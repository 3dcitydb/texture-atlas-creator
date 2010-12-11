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
			String name="tex2050042";
			BufferedImage bi = en.readRGB(name+".rgb");
			File f=new File(
					bi.getType() == BufferedImage.TYPE_INT_ARGB ? name+".png"
							: name+".jpeg");
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
