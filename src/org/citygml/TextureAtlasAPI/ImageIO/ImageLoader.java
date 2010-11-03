package org.citygml.TextureAtlasAPI.ImageIO;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.citygml.TextureAtlasAPI.DataStructure.TextureImage;




public class ImageLoader {
	RGBEncoder rgbEncoder;
	public ImageLoader(){
		rgbEncoder= new RGBEncoder();
	}
	File f;
	BufferedImage b;
	ImageIcon ii;
	int chanels;
	
	private Image loadImage(String path){
		Image img=null;
		try {
			
			f= new File(path);
			if (!f.exists()){
				System.err.println("file is not available:"+path);
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
				ii= new ImageIcon(b);
				img =ii.getImage();
				b.flush();
				b=null;
				ii=null;
			}else chanels=0;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("PATHHHH:"+path);
		}
		f=null;
		path=null;
		return img;
	}
	
	public Image loadImage(InputStream is, String MIME_Type, int size){
		Image img=null;
		if (!isSupportedImageFormat(MIME_Type))
			return null;
		try {
		if (MIME_Type.lastIndexOf("rgb")>0){
				b=rgbEncoder.readRGB(is,size);
				rgbEncoder.freeMemory();
		}
			else
				b= ImageIO.read(is);
			if (b!=null){
				chanelDetector(b);		
				ii= new ImageIcon(b);
				img =ii.getImage();
				b.flush();
				b=null;
				ii=null	;
			}else chanels=0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}

	
	private void chanelDetector(BufferedImage bImage){
		switch(bImage.getType()){
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
	
	public HashMap<String, TextureImage> loadAllImage(HashMap<String,String> imageLocalPath){
		HashMap<String, TextureImage> texImages = new HashMap<String, TextureImage>();
		Iterator<String> imageURI=imageLocalPath.keySet().iterator();
		String URI;
		while(imageURI.hasNext()){
			URI=imageURI.next();
			texImages.put(URI,new TextureImage(loadImage(imageLocalPath.get(URI)),chanels));
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
