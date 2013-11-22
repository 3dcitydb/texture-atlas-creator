package org.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.citygml.textureAtlasAPI.imageIO.ImageReader;

public class ImageReaderTest {

	public static void main(String[] args) {
		ImageReader loader = new ImageReader();
		final SimpleDateFormat df = new SimpleDateFormat("[HH:mm:ss] "); 
		System.out.println(df.format(new Date()) + "los");
		try {
			File file = new File("images/tex2130001.rgb");
//			File file = new File("images/tex2130002.rgb");
//			File file = new File("images/tex1922529.jpeg");
//			File file = new File("images/ERLE01.png");
			BufferedImage b = loader.read(file);
//			BufferedImage b = loader.read(new FileInputStream(file));
//			BufferedImage b = ImageIO.read(new FileInputStream(file));
			System.out.println(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(df.format(new Date()) + "ende");

	}

}
