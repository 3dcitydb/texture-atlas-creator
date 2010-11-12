package org.citygml.textureAtlasAPI.imageIO;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

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
	public static Image rescale(Image source,int maxW,int maxH) {
		
		int nw,nh;
		if (source.getWidth(null)>source.getHeight(null)){
			nw=maxW;
			nh = nw*source.getHeight(null)/source.getWidth(null);
		}else{
			nh=maxH;
			nw = nh*source.getWidth(null)/source.getHeight(null);
		}
		BufferedImage sourceBuffer = new BufferedImage(source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g= sourceBuffer.createGraphics();
		g.drawImage(source, 0,0,null);
		g.dispose();
		
		BufferedImage target = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g2 = target.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    double scalex = (double) target.getWidth()/ source.getWidth(null);
	    double scaley = (double) target.getHeight()/ source.getHeight(null);
	    AffineTransform xform = AffineTransform.getScaleInstance(scalex, scaley);
	    g2.drawRenderedImage(sourceBuffer, xform);
	    g2.dispose();
	    sourceBuffer=null;
	    return new ImageIcon(target).getImage();
	}
	



}
