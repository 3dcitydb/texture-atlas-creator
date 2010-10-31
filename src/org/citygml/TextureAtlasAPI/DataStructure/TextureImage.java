package org.citygml.TextureAtlasAPI.DataStructure;

import java.awt.Image;
import java.sql.SQLException;
import org.citygml.TextureAtlasAPI.ImageIO.ImageLoader;
import oracle.ord.im.OrdImage;

/**
 * for the OrdImage the MIME type should be set.
 * 
 * @author babak naderi
 * 
 */
public class TextureImage {
	public final static int ORD_IMAGE = 1;
	public final static int IMAGE = 2;
	private int type;
	private Image image;
	private OrdImage ordImage;
	private static ImageLoader imageLoader = new ImageLoader();

	public TextureImage(Image bi) {
		this.image = bi;
		this.type = IMAGE;
	}

	public TextureImage(OrdImage ordImage) {
		this.ordImage = ordImage;
		this.type = ORD_IMAGE;
	}

	public Image getImage() {
		if (this.image == null){
			if (this.ordImage == null)
				return null;
			try {
				this.image= imageLoader.loadImage(ordImage.getDataInStream(), ordImage
						.getMimeType(), ordImage.getContentLength());
				

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				e = null;
				return null;
			}
		}
		return this.image;

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

	public void setImage(Image bImage) {
		this.image = bImage;
	}

	public void setImage(OrdImage ordImage) {
		this.ordImage = ordImage;
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
