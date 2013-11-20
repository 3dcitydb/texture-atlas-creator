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
 * @author Claus Nagel <c.nagel@virtualcitysystems.de>
 ******************************************************************************/
package org.citygml.textureAtlasAPI.imageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * This class is responsible for loading different image formats. 
 * Currently all j2se 6 supported image formats and SGI RGB Image format are included.
 * In the case of adding other encoders, they should announced in here.
 */

public class ImageLoader {	
	private RGBEncoder rgbEncoder;
	private ArrayList<String> supportedMIMETypes;
	private ArrayList<String> supportedExtensions;

	public ImageLoader() {
		rgbEncoder= new RGBEncoder();
		initSupportedFileFormats();
	}

	public BufferedImage loadImage(File file) throws IOException {
		if (!file.isFile() || !file.exists() || !file.canRead())
			return null;

		BufferedImage b = null;
		if (file.getName().toUpperCase().endsWith(".RGB") ||
				file.getName().toUpperCase().endsWith(".RGBA"))
			b = rgbEncoder.readRGB(file);
		else
			b = ImageIO.read(file);

		return b;
	}

	public BufferedImage loadImage(InputStream is, String mimeType, int size) throws IOException {
		if (!isSupportedMIMEType(mimeType))
			return null;

		BufferedImage b = null;
		if (mimeType.toUpperCase().endsWith("RGB") ||
				mimeType.toUpperCase().endsWith("RGBA"))
			b = rgbEncoder.readRGB(is, size);
		else
			b = ImageIO.read(is);

		return b;
	}

	public boolean isSupportedMIMEType(String mimeType) {
		return mimeType != null ? supportedMIMETypes.contains(mimeType.toUpperCase()) : false;
	}

	public boolean isSupportedFileExtension(String extension) {
		return extension != null ? supportedExtensions.contains(extension.toUpperCase()) : false;
	}
	
	private void initSupportedFileFormats(){
		supportedMIMETypes= new ArrayList<String>();
		supportedExtensions = new ArrayList<String>();
		
		for (String mimeType : ImageIO.getReaderMIMETypes())
			supportedMIMETypes.add(mimeType.toUpperCase());
		
		for (String extension : ImageIO.getReaderFileSuffixes())
			supportedExtensions.add(extension.toUpperCase());
			
		supportedMIMETypes.add("IMAGE/RGB");
		supportedMIMETypes.add("IMAGE/X-RGB");
		supportedMIMETypes.add("IMAGE/RGBA");
		supportedExtensions.add("RGB");
		supportedExtensions.add("RGBA");
	}
	
	//	public HashMap<String, TexImage> loadAllImage(HashMap<String,String> imageLocalPath){
	//		HashMap<String, TexImage> texImages = new HashMap<String, TexImage>();
	//		if (imageLocalPath==null)
	//			return null;
	//		Iterator<String> imageURI=imageLocalPath.keySet().iterator();
	//		String URI;
	//		while(imageURI.hasNext()){
	//			URI=imageURI.next();
	//			texImages.put(URI,new TexImage(loadImage(imageLocalPath.get(URI))));
	//			URI=null;
	//		}
	//		imageURI=null;
	//		imageLocalPath=null;
	//		return texImages ;
	//	}

	//	/**
	//	 * set this instance in all TexImage in input HashMap.
	//	 * @param basic
	//	 */
	//	public void setImageLoader(HashMap<String, TexImage> basic) {
	//		if (basic==null)
	//			return ;
	//		if (basic.values()!=null){
	//			Iterator<TexImage> tximag=basic.values().iterator();
	//			while(tximag.hasNext()){
	//				tximag.next().setImageLoader(this);
	//			}
	//		}
	//	}

	/*
	private void chanelDetector(BufferedImage bImage){
		switch(bImage.getType()){
		case BufferedImage.TYPE_BYTE_INDEXED:
		case BufferedImage.TYPE_BYTE_GRAY:
			this.chanels=1;break;

		case BufferedImage.TYPE_INT_ARGB:
		case BufferedImage.TYPE_INT_ARGB_PRE:
		case BufferedImage.TYPE_4BYTE_ABGR:
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			this.chanels=4;
			break;
		default:
			this.chanels=3;
		}

	}

	public int getChanels(){
		return this.chanels;
	}
	 */

}
