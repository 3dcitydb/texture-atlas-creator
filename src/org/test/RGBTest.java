/*******************************************************************************
 * This file is part of the Texture Atlas Generation Tool.
 * Copyright (c) 2010 - 2011
 * Institute for Geodesy and Geoinformation Science
 * Technische Universitaet Berlin, Germany
 * http://www.gis.tu-berlin.de/
 * 
 * The Texture Atlas Generation Tool is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * @author Babak Naderi <b.naderi@mailbox.tu-berlin.de>
 ******************************************************************************/
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
