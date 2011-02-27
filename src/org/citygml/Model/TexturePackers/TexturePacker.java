package org.citygml.Model.TexturePackers;

import java.awt.Graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import org.citygml.Model.DataStructures.TexturePropertiesInAtlas;
import org.citygml.util.Logger;

public class TexturePacker extends AbstractTexturePacker {
	public  static int ImageMaxWidth=2048;
	public  static int ImageMaxHeight=1024;
	
	BufferedImage bi;
	BufferedImage tmp;
	Graphics g;
	int fileCounter=0;
	String completeAtlasPath;
	boolean overBorder=false;
	
	public TexturePacker(){
		setTexturePackerType(AbstractTexturePacker.TEXTUREPACKER_1);
		// Just JPEG is supported
		bi=new BufferedImage(ImageMaxWidth, ImageMaxHeight,BufferedImage.TYPE_INT_RGB);
		g = bi.getGraphics();
	}
	
	public Hashtable<String, TexturePropertiesInAtlas> run(){
		fileCounter=0;
		
		
		g.clearRect(0, 0, ImageMaxWidth, ImageMaxHeight);
		if (getImageList()==null)
			return null;
		String[] imgsPath= getImageList().split(";");
		BufferedImage[] imgs = new BufferedImage[imgsPath.length];
		int maxHeigth=0,totalWidth=0;
		double[]widths= new double[imgs.length];
		double[]heights= new double[imgs.length];
		

		boolean isAnyTransparent=false;
		
		for (int i = 0; i < imgs.length; i++) {
			try {
				imgs[i]=ImageIO.read(new File(imgsPath[i]));
			} catch (IOException e) {
				if (Logger.SHOW_STACK_PRINT)
					e.printStackTrace();
				imgs[i]= new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
			}			
			
            widths[i] = imgs[i].getWidth();
            heights[i] = imgs[i].getHeight();
            if (heights[i]<ImageMaxHeight){
            	totalWidth+=widths[i];
            	if (heights[i]>maxHeigth)
                    maxHeigth = (int)heights[i];
            }
            	
           
            
            if (imgs[i].isAlphaPremultiplied())
            	isAnyTransparent=true;
            // just for test
        }
		int[] order= getImagesBestOrder(widths, heights);
		completeAtlasPath=getAtlasPath()+"_"+fileCounter+"."+getAtlasFormat(isAnyTransparent);
		
		int w = 0;
		Hashtable<String, TexturePropertiesInAtlas> ht= new Hashtable<String, TexturePropertiesInAtlas>();
		TexturePropertiesInAtlas ip;
		for (int i = 0; i < imgs.length; i++) {

			if (ImageMaxWidth<widths[order[i]]+w){
				writeImage(isAnyTransparent,ImageMaxWidth,maxHeigth);
				totalWidth-=w;
				w=0;
				
			}
			if (heights[order[i]]>ImageMaxHeight){
			// at first write image in a new position
				overBorder= true;
				File file = new File(getAtlasPath()+"_"+(fileCounter+1)+"."+getAtlasFormat(isAnyTransparent));
				if (!file.exists() &&file.getParent()!=null)
					file.getParentFile().mkdirs();
				try {
					ImageIO.write(imgs[order[i]],getAtlasFormat(isAnyTransparent), file);
				} catch (IOException e) {
					if (Logger.SHOW_STACK_PRINT)
						e.printStackTrace();
				}
			// fill the object.
				ip= new TexturePropertiesInAtlas();
				ip.setImagePath(imgsPath[order[i]]);
				ip.setHorizontalOffset(0);
				ip.setVerticalOffset(0);
				ip.setWidth(1);
				ip.setHeight(1);
				ip.setAtlasPath(getAtlasPath()+"_"+(fileCounter+1)+"."+getAtlasFormat(isAnyTransparent));
				String st = imgsPath[order[i]].replaceFirst(getPrefixAddress()+"/", "");		
				imgs[order[i]].flush();
				ht.put(st, ip);
//				totalWidth-=widths[order[i]];
				continue;
				
			}
			// draw image
			g.drawImage(imgs[order[i]], w, 0, null);
			// make the coordinates
			ip= new TexturePropertiesInAtlas();
			ip.setImagePath(imgsPath[order[i]]);
			ip.setHorizontalOffset((double)w/(double)Math.min(ImageMaxWidth,totalWidth) );
			ip.setVerticalOffset(0);
			ip.setWidth(widths[order[i]]/(double)Math.min(ImageMaxWidth,totalWidth));
			ip.setHeight(heights[order[i]]/(double)maxHeigth);
			ip.setAtlasPath(completeAtlasPath);
			String st = imgsPath[order[i]].replaceFirst(getPrefixAddress()+"/", "");		
			imgs[order[i]].flush();
			ht.put(st, ip);
			w += widths[order[i]];
			
		}
		writeImage(isAnyTransparent, w, maxHeigth);
		imgs = null;
		return ht;
	}
	
	private void writeImage(boolean isAnyTransparent,int w, int h){
		try{
		File file = new File(completeAtlasPath);
		if (!file.exists() &&file.getParent()!=null)
			file.getParentFile().mkdirs();
		tmp=bi.getSubimage(0, 0, w, h);
		ImageIO.write(tmp,getAtlasFormat(isAnyTransparent), file);
		tmp.releaseWritableTile(0, 0);
		tmp.flush();
		completeAtlasPath=null;
		fileCounter++;
		if (overBorder)
			fileCounter++;
		completeAtlasPath= getAtlasPath()+"_"+fileCounter+"."+getAtlasFormat(isAnyTransparent);
		g.clearRect(0, 0, ImageMaxWidth, ImageMaxHeight);
		overBorder=false;
		}catch(Exception e){
			if (Logger.SHOW_STACK_PRINT)
				e.printStackTrace();
		}
	}
	
	
	private int[] getImagesBestOrder(double[] widths,double[] heights){
		int[] order = new int[widths.length];
		for(int i=0;i<order.length;i++)
			order[i]=i;
		return order;
	}
	
	public void reset(){
		
	}
	
}
