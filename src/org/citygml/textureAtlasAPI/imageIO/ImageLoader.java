package org.citygml.textureAtlasAPI.imageIO;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;


import org.citygml.textureAtlasAPI.dataStructure.TexImage;
//import org.citygml.util.Logger;




public class ImageLoader {
	RGBEncoder rgbEncoder;
	public ImageLoader(){
		rgbEncoder= new RGBEncoder();
	}
	File f;
	BufferedImage b;
//	ImageIcon ii;
	int chanels;
	
	private BufferedImage loadImage(String path){
		try {
			f= new File(path);
			if (!f.exists()){
//				Logger.getInstance().log(Logger.TYPE_ERROR,"File not exists. ("+path+")");
				return null;
			}
			if (path.lastIndexOf(".rgb")>0){
				b=rgbEncoder.readRGB(f);
				rgbEncoder.freeMemory();
			}
			else
				b= ImageIO.read(f);
			if (b!=null){
				chanelDetector(b);
			}else {
				chanels=0;
				//Logger.getInstance().log(Logger.TYPE_ERROR,"Error in loading an image. ("+path+")");
			}
			
		} catch (Exception e) {
//			if (Logger.SHOW_STACK_PRINT)
//				e.printStackTrace();
//			Logger.getInstance().log(Logger.TYPE_ERROR,"Error in loading image. ("+path+")");
		}
		f=null;
		path=null;
		return b;
	}
	
	public BufferedImage loadImage(InputStream is, String MIME_Type,String extension, int size){
		if (!isSupportedImageFormat(MIME_Type,extension))
			return null;
		try {
			if (MIME_Type.lastIndexOf("rgb") > 0) {
				b = rgbEncoder.readRGB(is, size);
				rgbEncoder.freeMemory();
			} else
				b = ImageIO.read(is);
			if (b != null) {
				chanelDetector(b);

			} else
				chanels = 0;
		} catch (Exception e) {
			//if (Logger.SHOW_STACK_PRINT)
//				e.printStackTrace();
		}
		return b;
	}	
	
	public void setImageLoader(HashMap<String, TexImage> basic){
		if (basic==null)
			return ;
		if (basic.values()!=null){
		Iterator<TexImage> tximag=basic.values().iterator();
			while(tximag.hasNext()){
				tximag.next().setImageLoader(this);
			}
		}
	}
	
	private void chanelDetector(BufferedImage bImage){
		switch(bImage.getType()){
		case BufferedImage.TYPE_BYTE_INDEXED:
		case BufferedImage.TYPE_BYTE_GRAY:
			this.chanels=1;break;
		
		case BufferedImage.TYPE_INT_ARGB:
		case BufferedImage.TYPE_INT_ARGB_PRE:
		case BufferedImage.TYPE_4BYTE_ABGR:
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			this.chanels=4;
			break;
		default:
			this.chanels=3;
		}
		
	}
	public int getChanels(){
		return this.chanels;
	}
	
	public HashMap<String, TexImage> loadAllImage(HashMap<String,String> imageLocalPath){
		HashMap<String, TexImage> texImages = new HashMap<String, TexImage>();
		if (imageLocalPath==null)
			return null;
		Iterator<String> imageURI=imageLocalPath.keySet().iterator();
		String URI;
		while(imageURI.hasNext()){
			URI=imageURI.next();
			texImages.put(URI,new TexImage(loadImage(imageLocalPath.get(URI))));
			URI=null;
		}
		imageURI=null;
		imageLocalPath=null;
		return texImages ;
	}
	
	public static boolean isSupportedImageFormat(String MIMEType,String extension){
		if (SupportedImageMIMETypes==null)
			loadSupportedImageList();
		if (MIMEType==null){
			if (extension!=null)
				return SupportedImageExtensions.contains(extension.toUpperCase());
			return false;
		}
		return SupportedImageMIMETypes.contains(MIMEType.toUpperCase());
	}
	
	private static ArrayList<String> SupportedImageMIMETypes=null;
	private static ArrayList<String> SupportedImageExtensions=null;
	private static void loadSupportedImageList(){
		SupportedImageMIMETypes= new ArrayList<String>();
		String[] st= ImageIO.getReaderMIMETypes();
		for (int i=0;i<st.length;i++){
			SupportedImageMIMETypes.add(st[i].toUpperCase());
			st[i]=null;
		}
		SupportedImageMIMETypes.add("IMAGE/RGB");
		SupportedImageMIMETypes.add("IMAGE/X-RGB");
		SupportedImageExtensions = new ArrayList<String>();
		SupportedImageExtensions.add("JPG");
		SupportedImageExtensions.add("JPEG");
		SupportedImageExtensions.add("RGB");
		SupportedImageExtensions.add("PNG");
		SupportedImageExtensions.add("GIF");
		
		st=null;
	}
}
