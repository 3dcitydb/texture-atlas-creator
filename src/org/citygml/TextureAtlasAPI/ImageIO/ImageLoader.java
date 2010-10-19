package org.citygml.TextureAtlasAPI.ImageIO;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;




public class ImageLoader {
	RGBEncoder rgbEncoder;
	public ImageLoader(){
		rgbEncoder= new RGBEncoder();
	}
	File f;
	BufferedImage b;
	ImageIcon ii;
	private Image loadImage(String path){
		Image img=null;
		try {
			
			f= new File(path);
			if (path.lastIndexOf(".rgb")>0){
				b=rgbEncoder.readRGB(f);
				rgbEncoder.freeMemory();
			}
			else
				b= ImageIO.read(f);
			
			if (b!=null){
				ii= new ImageIcon(b);
				img =ii.getImage();
				b.flush();
				b=null;
				ii=null;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("PATHHHH:"+path);
		}
		f=null;
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
		return SupportedImageMIMETypes.contains(MIMEType.toUpperCase());
	}
	
	private static ArrayList<String> SupportedImageMIMETypes=null;
	private static void loadSupportedImageList(){
		SupportedImageMIMETypes= new ArrayList<String>();
		String[] st= ImageIO.getReaderMIMETypes();
		for (int i=0;i<st.length;i++){
			SupportedImageMIMETypes.add(st[i].toUpperCase());
			st[i]=null;
		}
		SupportedImageMIMETypes.add("IMAGE/RGB");
		SupportedImageMIMETypes.add("IMAGE/X-RGB");
		
		st=null;
	}
}
