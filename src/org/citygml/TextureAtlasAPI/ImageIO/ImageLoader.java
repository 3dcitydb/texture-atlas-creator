package org.citygml.TextureAtlasAPI.ImageIO;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class ImageLoader {

	
	private Image loadImage(String path){
		Image img=null;
		try {
			
			img =new ImageIcon(ImageIO.read(new File(path))).getImage();;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("PATHHHH:"+path);
		}
		path=null;
		return img;
	}
	
	public HashMap<String, Image> loadAllImage(HashMap<String,String> imageLocalPath){
		HashMap<String, Image> texImages = new HashMap<String, Image>();
		Iterator<String> imageURI=imageLocalPath.keySet().iterator();
		String URI;
		while(imageURI.hasNext()){
			URI=imageURI.next();
			texImages.put(URI, loadImage(imageLocalPath.get(URI)));
			URI=null;
		}
		imageURI=null;
		imageLocalPath=null;
		return texImages ;
	}
}
