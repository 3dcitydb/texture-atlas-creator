package org.citygml.textureAtlasAPI.dataStructure;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import org.citygml.textureAtlasAPI.imageIO.ImageLoader;
import org.citygml.util.Logger;

import oracle.ord.im.OrdImage;

/**
 * for the OrdImage the MIME type should be set.
 * 
 * @author babak naderi
 * 
 */
public class TexImage {
	public final static int ORD_IMAGE = 1;
	public final static int IMAGE = 2;
	private int type;
	private BufferedImage image;
	private OrdImage ordImage;
	private static ImageLoader imageLoader = new ImageLoader();
	private int chanels=3;


	public TexImage(BufferedImage bi){
		this.image=bi;
		this.type=IMAGE;
		if (bi!=null)
			this.chanels = getChanel(bi.getType());	
		else
			this.chanels =-1;
	}
		
	
	
	
	public TexImage(OrdImage ordImage) {
		this.ordImage = ordImage;
		this.type = ORD_IMAGE;
	}

	
	public BufferedImage getBufferedImage(){
		if (this.image == null){
			if (this.ordImage == null)
				return null;
			try {	
				byte[] mb=ordImage.getDataInByteArray();
			
				this.image= imageLoader.loadImage(ordImage.getDataInStream(), ordImage
						.getMimeType(), mb.length);
				mb=null;
				this.chanels=imageLoader.getChanels();
			} catch (Exception e) {
				if (Logger.SHOW_STACK_PRINT)
					e.printStackTrace();
				e = null;
				return null;
			}
		}
		return this.image;
//		if (image==null)
//			return null;
//		
//		BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), chanels==3? BufferedImage.TYPE_INT_RGB:BufferedImage.TYPE_INT_ARGB);
//		Graphics2D g=bi.createGraphics();
//		g.drawImage(image, 0, 0,null);
//		g.dispose();
//		return bi;
	}
	
	
	// before that you should set db connection in a static variable
	public OrdImage getOrdImage() {
		if (this.type == ORD_IMAGE)
			return this.ordImage;
		else {
			if (this.image == null)
				return null;
			return null;
		}
	}

	public void setImage(BufferedImage bImage) {
		this.image = bImage;
		if (bImage!=null)
			this.chanels = getChanel(bImage.getType());
		else
			this.chanels = -1;
	}

	public void setImage(OrdImage ordImage) {
		this.ordImage = ordImage;
	}
	public int getChanels(){
		if (this.image==null)
			getBufferedImage();
		return this.chanels;
	}
	
	public int getChanel(int BuffImageType){
		switch(BuffImageType){
		case BufferedImage.TYPE_BYTE_GRAY:
			return 1;
		case BufferedImage.TYPE_INT_ARGB:
		case BufferedImage.TYPE_INT_ARGB_PRE:
		case BufferedImage.TYPE_4BYTE_ABGR:
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			return 4;
		default:
			return 3;
		}
	}
	
	public void freeMemory(){
		// how to finalize it!?
		ordImage=null;
		if (image!=null){
			image.flush();
			image=null;
		}
		
	}
}
