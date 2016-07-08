/*
 * 3D City Database Texture Atlas Creator
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2016
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.gis.bgu.tum.de/
 * 
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 * 
 * virtualcitySYSTEMS GmbH, Berlin <http://www.virtualcitysystems.de/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
