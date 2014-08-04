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
package org.citydb.textureAtlas.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.citydb.textureAtlas.model.TextureImage;

public class ImageReader {
	
	private ImageReader() {
		// just to thwart instantiation
	}
	
	public static TextureImage read(File file) throws IOException {
		if (!file.isFile() || !file.exists() || !file.canRead())
			return null;

		return read(new FileInputStream(file));
	}

	public static TextureImage read(InputStream is) throws IOException {
		BufferedImage b = null;

		ImageInputStream imageIS = ImageIO.createImageInputStream(is);		
		b = ImageIO.read(imageIS);
		if (b == null) {
			imageIS.reset();
			b = RGBEncoder.getInstance().readRGB(imageIS);
		}

		return b != null ? new TextureImage(b) : null;
	}

	public static boolean isSupportedMIMEType(String mimeType) {
		if (mimeType == null)
			return false;
		
		if (ImageIO.getImageReadersByMIMEType(mimeType).hasNext())
			return true;
		else
			return RGBEncoder.getInstance().isSupportedMIMEType(mimeType);
	}

	public static boolean isSupportedFileSuffix(String suffix) {
		if (suffix == null)
			return false;
		
		if (ImageIO.getImageReadersBySuffix(suffix).hasNext())
			return true;
		else
			return RGBEncoder.getInstance().isSupportedFileSuffix(suffix);
	}
	
}
