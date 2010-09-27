package org.citygml.TextureAtlasAPI;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import org.citygml.TextureAtlasAPI.DataStructure.TexImageInfo;
import org.citygml.TextureAtlasAPI.StripPacker.MyItem;
import org.citygml.TextureAtlasAPI.StripPacker.MyResult;
import org.citygml.TextureAtlasAPI.StripPacker.MyStPacker;

public class Modifier {
	private int ImageMaxWidth;
	private int ImageMaxHeight;
	private int packingAlgorithm;
	private String AtlasName;
	
	BufferedImage bi;
	BufferedImage tmp;
	Graphics g;
	int fileCounter=0;
	String completeAtlasPath;
	boolean overBorder=false;
	
	
	public Modifier(int PackingAlg,  int atlasMaxWidth, int atlasMaxHeight){
		setGeneralSettings(PackingAlg, atlasMaxWidth, atlasMaxHeight);
	}
	
	public void setGeneralSettings(int PackingAlg, int atlasMaxWidth, int atlasMaxHeight){
		this.packingAlgorithm = PackingAlg;
		this.ImageMaxHeight=atlasMaxHeight;
		this.ImageMaxWidth= atlasMaxWidth;
		
		bi=new BufferedImage(ImageMaxWidth, ImageMaxHeight,BufferedImage.TYPE_INT_RGB);
		g = bi.getGraphics();
	}
	
	/**
	 * TODO check the code whether for several texCoordiLists for a parameterizedTexture.
	 * @param ti
	 * @return
	 */
	
	public TexImageInfo run(TexImageInfo ti){
		fileCounter=0;
		AtlasName=null;
		g.clearRect(0, 0, ImageMaxWidth, ImageMaxHeight);

		HashMap<Long, String> coordinatesHashMap =ti.getTexCoordinates();
		HashMap<String, Image> textImage= ti.getTexImages();
		HashMap<Long, String> textUri= ti.getTexImageURIs();
		
		
		Image[] imgs = new Image[textImage.size()];
		int totalWidth=0,maxw=0;
//		Long[] surfacs_ID = new Long[texImageCount];
		//!?!?! should I make it again?
		MyStPacker myPack = new MyStPacker();
		// target URI: surface ID
		Iterator<Long> surfacesID= coordinatesHashMap.keySet().iterator();
		int counter =0;
		Long ID;
		Image tmp;
		int width, height;
		
		double[] coordinate;
		while(surfacesID.hasNext()){
			ID = surfacesID.next();
			if (AtlasName==null){
				AtlasName= textUri.get(ID);
				tmp = textImage.get(AtlasName);
				AtlasName= AtlasName.substring(0,AtlasName.lastIndexOf('.'));
				System.out.println("Atlas NAme:"+AtlasName);
			}else
				tmp = textImage.get(textUri.get(ID));
//			surfacs_ID[counter]=ID;
			if (tmp==null)
				continue;
			imgs[counter]=tmp;
			width= tmp.getWidth(null);
	        height= tmp.getHeight(null);
	        coordinate= formatCoordinates(coordinatesHashMap.get(ID));
	        //LOG if coordinates have any problem, do not touch it! (like n. available or wrapping textures)
	        if (coordinate==null)
	        	continue;
	        //LOG if the image is not accepted, do not touch it! size problem
	        if (!acceptATexture(width,height))
	        	continue;
	        if (width>maxw)
	        	maxw=width;	
            totalWidth+=width;
            myPack.addItem(""+counter, width, height,ID,coordinate);
//            ID=null;//??
//            coordinate=null;
//            tmp.flush();//???
	        counter++;
		}
		
		MyResult mr=iterativePacker(null, maxw,totalWidth);
		
		// start to make atlas. prevH: amount of height of strip which was written in file before.
		int recID,x,y, prevH=0;
		int atlasW=0,atlasH=0;
		// list of all items which will be drawn in a same atlas.
		Vector<MyItem> frame = new Vector<MyItem>();
		completeAtlasPath=AtlasName+"_%1d.jpg";
		//going in side of Result
		Iterator lIter = mr.getLevelMap().keySet().iterator();
        while (lIter.hasNext()){
        	Object level = lIter.next();
             Iterator <MyItem> iIter = mr.getLevelMap().get(level).iterator();
             while (iIter.hasNext()){
            	 MyItem item = iIter.next();
            	 recID =Integer.parseInt(item.getId());
            	 x= item.getXPos();
            	 y= item.getYPos();
            	 if (y-prevH+item.getHeight()>ImageMaxHeight){
            		 // set Image in Hashmap and write it to file.
            		 textImage.put(String.format(completeAtlasPath,fileCounter),writeImage(atlasW, atlasH));
            		 // set the new coordinates
            		 modifyNewCorrdinates(frame,coordinatesHashMap,atlasW, atlasH);
            		 frame.clear();
            		 atlasW=0;
            		 atlasH=0;
            		 prevH=y;
            	 }
            	 g.drawImage(imgs[recID], x, y-prevH, null);
            	 item.setYPos(y-prevH);
            	 frame.add(item);
            	 if (atlasW<x+item.getWidth())
            		 atlasW=x+item.getWidth();
            	 if (atlasH<y-prevH+item.getHeight())
            		 atlasH=y-prevH+item.getHeight();
            	 
            	 // set the properties.
            	 // 	set the URI 
            	 textUri.put(item.getSurfaceID(), String.format(completeAtlasPath,fileCounter));
             }    
         }
         
        textImage.put(String.format(completeAtlasPath,fileCounter),writeImage(atlasW, atlasH));
		 // set the new coordinates
		 modifyNewCorrdinates(frame,coordinatesHashMap,atlasW, atlasH);
		 frame.clear();
		
		imgs = null;
		
		ti.setTexCoordinates(coordinatesHashMap);
		ti.setTexImages(textImage);
		ti.setTexImageURIs(textUri);
		return ti;
	}
	
	/**
	 * also can add the textures.
	 * @param width
	 * @return
	 */
	private boolean acceptATexture(int width, int heigth){
		if (width<=ImageMaxWidth && heigth<=ImageMaxHeight)
			return true;
		return false;
		
	}
	
	private MyResult iterativePacker(MyStPacker msp, int maxw, int totalw){
		msp.setStripWidth((totalw-maxw)/2);
		try{
			return msp.getResult(packingAlgorithm);	
		}catch(Exception e){
			return null;
		}
		
	}
	
	private double[] formatCoordinates(String coordinates){
		if (coordinates==null)
			return null;
		String[] sc = coordinates.split(" ");
		double[]c= new double[sc.length];
		for (int i=0;i<sc.length;i++){
			c[i] = Double.parseDouble(sc[i]);
			if (c[i]<0.0005||c[i]>1.0005){
				sc=null;
				return null;
			}
		}
		sc=null;
		coordinates=null;
		return c;
	}
	
	private Image writeImage(int w, int h){
		try{
		
		File file = new File(String.format(completeAtlasPath,fileCounter));
		if (!file.exists() &&file.getParent()!=null)
			file.getParentFile().mkdirs();
		tmp=bi.getSubimage(0, 0, w, h);
		ImageIO.write(tmp,"jpeg", file);
		Image result =Toolkit.getDefaultToolkit().createImage(tmp.getSource());
		tmp.releaseWritableTile(0, 0);
		tmp.flush();
		fileCounter++;
		g.clearRect(0, 0, ImageMaxWidth, ImageMaxHeight);
		return result;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private void modifyNewCorrdinates(Vector items, HashMap<Long, String> coordinatesHashMap, int atlasWidth, int atlasHeigth){
		Iterator<MyItem> itr= items.iterator();
		MyItem mit;
		while(itr.hasNext()){
			mit = itr.next();
			coordinatesHashMap.put(mit.getSurfaceID(),getCoordinate(mit.getCoordinates(),mit.getXPos(),mit.getYPos(), mit.getWidth(),mit.getHeight(),atlasWidth, atlasHeigth));
			mit.clear();
		}
		itr=null;
	}
	
	private String getCoordinate(double[]coordinates, double posX, double posY, double imW,double imH ,double atlasw, double atlasH){
		StringBuffer sb = new StringBuffer(coordinates.length*15);
		for (int j = 0; j < coordinates.length; j += 2) {
			// Horizontal
			coordinates[j] = (posX+(coordinates[j] * imW))/atlasw;
			// corner as a origin,but cityGML used left down corner.
			coordinates[j + 1] =1-((1-coordinates[j+1])*imH+posY)/atlasH; 
			sb.append(coordinates[j]);
			sb.append(' ');
			sb.append(coordinates[j+1]);
			
		}
		return sb.toString();
	}
	
	public void reset(){
		AtlasName=null;
		if(bi!=null){
		bi.flush();
		bi=null;
		}
		if(tmp!=null){
			tmp.flush();
			tmp=null;
		}
		g=null;
		completeAtlasPath=null;
	}
}
