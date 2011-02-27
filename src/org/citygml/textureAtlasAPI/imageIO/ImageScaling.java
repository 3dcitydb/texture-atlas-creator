package org.citygml.textureAtlasAPI.imageIO;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class ImageScaling {
	/*
	public static GraphicsConfiguration getDefaultConfiguration() {
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gd = ge.getDefaultScreenDevice();
	    return gd.getDefaultConfiguration();
	}
	 
	public static BufferedImage toCompatibleImage(BufferedImage image, GraphicsConfiguration gc) {
	    if (gc == null)
	        gc = getDefaultConfiguration();
	    int w = image.getWidth();
	    int h = image.getHeight();
	    int transparency = image.getColorModel().getTransparency();
	    BufferedImage result = gc.createCompatibleImage(w, h, transparency);
	    Graphics2D g2 = result.createGraphics();
	    g2.drawRenderedImage(image, null);
	    g2.dispose();
	    return result;
	}*/
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
	



}
