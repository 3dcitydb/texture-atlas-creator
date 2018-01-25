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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageProcessor {

	public static BufferedImage rescale(BufferedImage source, int maxW, int maxH) {
		if (source == null)
			return null;
		
		int newW, newH;
		
		if (source.getWidth() > source.getHeight()) {
			newW = maxW;
			newH = newW * source.getHeight() / source.getWidth();
			if (newH == 0)
				newH = 1;
		} else {
			newH = maxH;
			newW = newH * source.getWidth() / source.getHeight();
			if (newW == 0)
				newW = 1;
		}

		int type = (source.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage target = new BufferedImage(newW, newH, type);
		
		return rescale(source, target);
	}
	
	public static BufferedImage rescale(BufferedImage source, double scaleFactor) {	
		if (source == null) 
			return null;
		
		int type = (source.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage target = new BufferedImage(
				(int)Math.floor(source.getWidth() * scaleFactor) + 1, 
				(int)Math.floor(source.getHeight() * scaleFactor) + 1, type);
		
		return rescale(source, target);
	}
	
	private static BufferedImage rescale(BufferedImage source, BufferedImage target) {
		double scaleX = (double) target.getWidth() / source.getWidth();
		double scaleY = (double) target.getHeight() / source.getHeight();
		AffineTransform transform = AffineTransform.getScaleInstance(scaleX, scaleY);
		
		Graphics2D graphics = target.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics.drawRenderedImage(source, transform);
		graphics.dispose();

		return target;
	}
	
	public static BufferedImage rotate(BufferedImage source) {
		if (source == null) 
			return null;
		
		int type = source.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		int width = source.getWidth();
		int height = source.getHeight();
		
		BufferedImage target = new BufferedImage(source.getHeight(), source.getWidth(), type);

		// swap pixels
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				target.setRGB(j, width-i-1, source.getRGB(i, j));
		
		return target;
	}

}
