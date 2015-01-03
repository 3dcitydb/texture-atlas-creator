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
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;

import org.citydb.textureAtlas.model.TextureImage;

public class ImageReader {
	private boolean isSupportRGB;

	public ImageReader() {
		// just to thwart instantiation
	}

	public boolean isSupportRGB() {
		return isSupportRGB;
	}

	public void setSupportRGB(boolean supportRGB) {
		this.isSupportRGB = supportRGB;
	}

	public TextureImage read(File file) throws IOException {
		if (!file.isFile() || !file.exists() || !file.canRead())
			return null;

		return read(new FileInputStream(file));
	}

	public TextureImage read(InputStream is) throws IOException {
		BufferedImage b = null;

		ImageInputStream imageIS = ImageIO.createImageInputStream(is);
		if (imageIS == null)
			return null;

		Iterator<javax.imageio.ImageReader> iter = ImageIO.getImageReaders(imageIS);
		if (!iter.hasNext())
			return null;

		javax.imageio.ImageReader imageReader = iter.next();
		ImageReadParam param = imageReader.getDefaultReadParam();
		imageReader.setInput(imageIS, true, true);

		try {
			b = imageReader.read(0, param);
			if (b == null && isSupportRGB) {
				imageIS.reset();
				b = RGBEncoder.getInstance().readRGB(imageIS);
			}
		} finally {
			imageReader.dispose();
			imageIS.close();
		}

		return b != null ? new TextureImage(b) : null;
	}

	public boolean isSupportedMIMEType(String mimeType) {
		if (mimeType == null)
			return false;

		if (ImageIO.getImageReadersByMIMEType(mimeType).hasNext())
			return true;
		else
			return isSupportRGB ? RGBEncoder.getInstance().isSupportedMIMEType(mimeType) : false;
	}

	public boolean isSupportedFileSuffix(String suffix) {
		if (suffix == null)
			return false;

		if (ImageIO.getImageReadersBySuffix(suffix).hasNext())
			return true;
		else
			return isSupportRGB ? RGBEncoder.getInstance().isSupportedFileSuffix(suffix) : false;
	}

}
