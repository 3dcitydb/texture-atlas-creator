package org.citygml.textureAtlasAPI.dataStructure;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import org.citygml.textureAtlasAPI.imageIO.ImageLoader;
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

//	public TexImage(Image bi, int chanels) {
//		this.image = bi;
//		this.type = IMAGE;
//		this.chanels= chanels;
//	}
	
	public TexImage(BufferedImage bi){
		this.image=bi;
		this.type=IMAGE;
		this.chanels = (bi.getType()==(BufferedImage.TYPE_4BYTE_ABGR)||(bi.getType()==BufferedImage.TYPE_INT_ARGB)?4:3);	
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				e = null;
				return null;
			}catch (OutOfMemoryError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		this.chanels = (bImage.getType()==(BufferedImage.TYPE_4BYTE_ABGR)||(bImage.getType()==BufferedImage.TYPE_INT_ARGB)?4:3);
	}

	public void setImage(OrdImage ordImage) {
		this.ordImage = ordImage;
	}
	public int getChanels(){
		if (this.image==null)
			getBufferedImage();
		return this.chanels;
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
