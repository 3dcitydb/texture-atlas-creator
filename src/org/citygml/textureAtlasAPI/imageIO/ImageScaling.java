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
package org.citygml.textureAtlasAPI.imageIO;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * In the case that image size is bigger than maximum supported size, 
 * it will resize.
 *
 */
public class ImageScaling {

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

}
