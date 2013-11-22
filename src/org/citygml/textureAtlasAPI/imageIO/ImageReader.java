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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

/**
 * This class is responsible for loading different image formats. 
 * Currently all j2se 6 supported image formats and SGI RGB Image format are included.
 * In the case of adding other encoders, they should announced in here.
 */

public class ImageReader {	
	private RGBEncoder rgbEncoder;
	private ArrayList<String> supportedMIMETypes;
	private ArrayList<String> supportedExtensions;

	public ImageReader() {
		rgbEncoder= new RGBEncoder();
		initSupportedFileFormats();
	}

	public BufferedImage read(File file) throws IOException {
		if (!file.isFile() || !file.exists() || !file.canRead())
			return null;

		BufferedImage b = null;
		ImageInputStream imageIS = ImageIO.createImageInputStream(new FileInputStream(file));	

		b = ImageIO.read(imageIS);
		if (b == null) {
			imageIS.reset();
			b = rgbEncoder.readRGB(imageIS);
		}

		return b;
	}

	public BufferedImage read(InputStream is) throws IOException {
		BufferedImage b = null;

		ImageInputStream imageIS = ImageIO.createImageInputStream(is);		
		b = ImageIO.read(imageIS);
		if (b == null) {
			imageIS.reset();
			b = rgbEncoder.readRGB(imageIS);
		}

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

}
