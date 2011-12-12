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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * In the case that image size is bigger than maximum supported size, 
 * it will resize.
 *
 */
public class ImageScaling {
	
	public static BufferedImage rescale(BufferedImage source,int maxW,int maxH) {
		
		int nw,nh;
		if (source.getWidth()>source.getHeight()){
			nw=maxW;
			nh = nw*source.getHeight()/source.getWidth();
		}else{
			nh=maxH;
			nw = nh*source.getWidth()/source.getHeight();
		}

		
		BufferedImage target = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g2 = target.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    double scalex = (double) target.getWidth()/ source.getWidth(null);
	    double scaley = (double) target.getHeight()/ source.getHeight(null);
	    AffineTransform xform = AffineTransform.getScaleInstance(scalex, scaley);
	    g2.drawRenderedImage(source, xform);
	    g2.dispose();
	    source=null;
	    return target;
	}
	
	public static BufferedImage rescale(BufferedImage source,double scalefactor) {	
		if (source==null) return null;
		BufferedImage target = new BufferedImage((int)Math.floor(source.getWidth()*scalefactor)+1, (int)Math.floor(source.getHeight()*scalefactor)+1, source.getType());
	    Graphics2D g2 = target.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    double scalex = (double) target.getWidth()/ source.getWidth(null);
	    double scaley = (double) target.getHeight()/ source.getHeight(null);
	    
	    AffineTransform xform = AffineTransform.getScaleInstance(scalex, scaley);
	    g2.drawRenderedImage(source, xform);
	    g2.dispose();
	    source=null;
	    return target;
	}



}
