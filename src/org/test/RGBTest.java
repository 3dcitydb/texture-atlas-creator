package org.test;

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
			String name="tex5400125";
			BufferedImage bi = en.readRGB(name+".rgb");
//			BufferedImage bi = new BufferedImage(20,20, BufferedImage.TYPE_BYTE_GRAY);
//			for (int i=0;i<20;i++){
//				for (int j=0;j<20;j++){
//					int level = i*10;
//					int gray = (level << 16) | (level << 8) | level;
//					bi.setRGB(i, j, level);
//				}
//			}
			
			File f=new File(
					bi.getType() == BufferedImage.TYPE_INT_RGB ? name+".jpeg"
							: name+".png");
			ImageIO
					.write(
							bi,
							bi.getType() == BufferedImage.TYPE_INT_RGB ? "jpeg"
									: "png",f);
			System.out.println("Finish!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
