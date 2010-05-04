package org.citygml.Model.TexturePackers;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.citygml.Model.GMLModifier;
import org.citygml.Model.DataStructures.TexturePropertiesInAtlas;

public class TexturePacker_1_Back extends AbstractTexturePacker {
	
	public TexturePacker_1_Back(){
		setTexturePackerType(AbstractTexturePacker.TEXTUREPACKER_1);
	}
	
	public Hashtable<String, TexturePropertiesInAtlas> run(){
		
		if (getImageList()==null)
			return null;
		String[] imgsPath= getImageList().split(";");
		Image[] imgs = new Image[imgsPath.length];
		int maxHeigth=0,totalWidth=0;
		double[]widths= new double[imgs.length];
		double[]heights= new double[imgs.length];
		
		ImageIcon ii;
		
		for (int i = 0; i < imgs.length; i++) {
            ii = new ImageIcon(imgsPath[i]);
            imgs[i] = ii.getImage();
            widths[i] = imgs[i].getWidth(ii.getImageObserver());
            heights[i] = imgs[i].getHeight(ii.getImageObserver());
            totalWidth+=widths[i];
            if (heights[i]>maxHeigth)
                maxHeigth = (int)heights[i];
              
            
        }
		int[] order= getImagesBestOrder(widths, heights);
		
		BufferedImage bi = new BufferedImage(totalWidth, maxHeigth,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		int w = 0;
		
		Hashtable<String, TexturePropertiesInAtlas> ht= new Hashtable<String, TexturePropertiesInAtlas>();
		TexturePropertiesInAtlas ip;
		for (int i = 0; i < imgs.length; i++) {
			// draw image
			g.drawImage(imgs[order[i]], w, 0, null);

			// make the coordinates
			ip= new TexturePropertiesInAtlas();
			ip.setImagePath(imgsPath[order[i]]);
			ip.setHorizontalOffset((double)w/(double)totalWidth);
			ip.setVerticalOffset(0);
			ip.setWidth(widths[order[i]]/(double)totalWidth);
			ip.setHeight(heights[order[i]]/(double)maxHeigth);
			String st = imgsPath[order[i]].replaceFirst(getPrefixAddress()+"/", "");
			ht.put(st, ip);
			
			w += widths[order[i]];
		}
		try{
		File file = new File(getAtlasPath());
		if (!file.exists() &&file.getParent()!=null)
			file.getParentFile().mkdirs();
		
		ImageIO.write(bi,getAtlasFormat()==GMLModifier.JPG?"jpg":"png", file);
		g.dispose();
		}catch(IOException e){
			e.printStackTrace();
		}
		return ht;
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
