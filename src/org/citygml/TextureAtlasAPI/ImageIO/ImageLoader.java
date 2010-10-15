package org.citygml.TextureAtlasAPI.ImageIO;

import java.awt.Image;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.lang.model.type.ErrorType;
import javax.swing.ImageIcon;

import org.citygml.TextureAtlasAPI.DataStructure.ErrorTypes;


public class ImageLoader {

	
	private Image loadImage(String path){
		Image img=null;
		try {
			
			
			img =new ImageIcon(ImageIO.read(new File(path))).getImage();;
			
		} catch (Exception e) {
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
	
	public static boolean isSupportedImageFormat(String MIMEType){
		if (SupportedImageMIMETypes==null)
			loadSupportedImageList();
		if (MIMEType==null)
			return false;
		
		boolean b=SupportedImageMIMETypes.contains(MIMEType.toUpperCase());
		System.out.println(b);
		return b;
	}
	
	private static ArrayList<String> SupportedImageMIMETypes=null;
	private static void loadSupportedImageList(){
		SupportedImageMIMETypes= new ArrayList<String>();
		String[] st= ImageIO.getReaderMIMETypes();
		for (int i=0;i<st.length;i++){
			SupportedImageMIMETypes.add(st[i].toUpperCase());
			System.out.println(st[i]);
			st[i]=null;
		}
		st=null;
	}
}
