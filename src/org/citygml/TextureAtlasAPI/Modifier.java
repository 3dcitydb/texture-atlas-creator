package org.citygml.TextureAtlasAPI;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.citygml.TextureAtlasAPI.DataStructure.ErrorTypes;
import org.citygml.TextureAtlasAPI.DataStructure.ImageScaling;
import org.citygml.TextureAtlasAPI.DataStructure.TexImageInfo;
import org.citygml.TextureAtlasAPI.StripPacker.MyItem;
import org.citygml.TextureAtlasAPI.StripPacker.MyResult;
import org.citygml.TextureAtlasAPI.StripPacker.MyStPacker;

public class Modifier {
	private int ImageMaxWidth;
	private int ImageMaxHeight;
	private int packingAlgorithm;

	
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
	 * TODO add try catch to get errors.
	 * @param ti
	 * @return
	 */
	
	public TexImageInfo run(TexImageInfo ti){
		if (bi==null){
			bi=new BufferedImage(ImageMaxWidth, ImageMaxHeight,BufferedImage.TYPE_INT_RGB);
			g = bi.getGraphics();
		}
		fileCounter=0;
		completeAtlasPath=null;
		g.clearRect(0, 0, ImageMaxWidth, ImageMaxHeight);

		HashMap<Object, String> coordinatesHashMap =ti.getTexCoordinates();
		HashMap<String, Image> textImage= ti.getTexImages();
		HashMap<Object, String> textUri= ti.getTexImageURIs();
		HashMap<Object, ErrorTypes> LOG= ti.getLOG();
		if (LOG==null)
			LOG= new HashMap<Object, ErrorTypes>();
		
		HashMap<String, ArrayList<Object>> uri2Object = new HashMap<String, ArrayList<Object>>();
		HashMap<String,Boolean> isImageAcceptable = new HashMap<String, Boolean>();
		HashMap< Object, double[]> doubleCoordinateList= new HashMap<Object, double[]>();
		

		int totalWidth=0,maxw=0;
		
		//!?!?! should I make it again?
		MyStPacker myPack = new MyStPacker();
		// target URI: surface ID
		int width, height;
		String URI;
		ArrayList<Object> list;
		Boolean b;
		Image tmp;
		
		double[] coordinate;
		for (Object key : textUri.keySet()){
			URI= textUri.get(key);
			// The image was read before;
			if((b=isImageAcceptable.get(URI))!=null){
				if (!b.booleanValue()){
					LOG.put(key,ErrorTypes.TARGET_PT_NOT_SUPPORTED);
					continue;
				}
				// the coordinates should be added and then continue.
				coordinate= formatCoordinates(coordinatesHashMap.get(key));
				// previous coordinate was accepted but this one have problem
				if (coordinate==null){
		        	isImageAcceptable.put(URI, new Boolean(false));
		        	// remve Item from list
		        	myPack.removeItem(URI);
		        	width= textImage.get(URI).getWidth(null);
		        	totalWidth-=width;
		        	// just for complicated cases.
		        	if (totalWidth<maxw)
		        		maxw=totalWidth;
		        	LOG.put(key,ErrorTypes.IMAGE_FORMAT_NOT_SUPPORTED);
		        	for (Object obj: uri2Object.get(URI))
		        		LOG.put(obj,ErrorTypes.IMAGE_FORMAT_NOT_SUPPORTED);
		        	continue;
		        }
				doubleCoordinateList.put(key, coordinate);
				uri2Object.get(URI).add(key);
				continue;
			}
			
			
			if (completeAtlasPath==null)
				completeAtlasPath= URI.substring(0,URI.lastIndexOf('.'))+"_%1d.jpg";
			
			// report bug
			if ((tmp= textImage.get(URI))==null){
				isImageAcceptable.put(URI, new Boolean(false));
				LOG.put(key,ErrorTypes.IMAGE_IS_NOT_AVAILABLE);
				continue;			
			}
			width= tmp.getWidth(null);
	        height= tmp.getHeight(null);
	        //LOG if the image is not accepted, do not touch it! size problem
	        if (!acceptATexture(width,height)){
	        	tmp = ImageScaling.rescale(tmp, ImageMaxWidth, ImageMaxHeight);
	        	if (tmp==null||!acceptATexture(width= tmp.getWidth(null),height= tmp.getHeight(null))){
					isImageAcceptable.put(URI, new Boolean(false));
					LOG.put(key, ErrorTypes.IMAGE_UNBONDED_SIZE);
					continue;
	        	}
	        	textImage.put(URI, tmp);
	        }
	        coordinate= formatCoordinates(coordinatesHashMap.get(key));
	        //LOG if coordinates have any problem, do not touch it! (like n. available or wrapping textures)
	        if (coordinate==null){
	        	isImageAcceptable.put(URI, new Boolean(false));
	        	LOG.put(key,ErrorTypes.ERROR_IN_COORDINATES);
	        	continue;
	        }
	        doubleCoordinateList.put(key, coordinate);
	        
	     
	        if (width>maxw)
	        	maxw=width;	
            totalWidth+=width;
            myPack.addItem(URI, width, height);
            isImageAcceptable.put(URI, new Boolean(true));
            if ((list=uri2Object.get(URI))==null){
            	list=new ArrayList<Object>();
            	list.add(key);
            	uri2Object.put(URI,list);
            	list=null;
            }
            else{
            	list.add(key);
            	list=null;
            }

		}
		
		MyResult mr=iterativePacker(myPack, maxw,totalWidth);
		
		// start to make atlas. prevH: amount of height of strip which was written in file before.
		int x,y, prevH=0;
		int atlasW=0,atlasH=0;
		// list of all items which will be drawn in a same atlas.
		Vector<MyItem> frame = new Vector<MyItem>();
		
		//going in side of Result
		Iterator<MyItem> all= mr.getAllItems().iterator();
		while(all.hasNext()){
			 MyItem item = all.next();
//        	 recID =Integer.parseInt(item.getURI());
        	 x= item.getXPos();
        	 y= item.getYPos();
        	 if (y-prevH+item.getHeight()>ImageMaxHeight){
        		
        		 // set Image in Hashmap and write it to file.
        		 textImage.put(String.format(completeAtlasPath,fileCounter),writeImage(atlasW, atlasH));
        		 // set the new coordinates
        		 modifyNewCorrdinates(frame,coordinatesHashMap,doubleCoordinateList,uri2Object,atlasW, atlasH);
        		 frame.clear();
        		 atlasW=0;
        		 atlasH=0;
        		 prevH=y;
        	 }
        	 g.drawImage(textImage.get(item.getURI()), x, y-prevH, null);
        	 textImage.remove(item.getURI()).flush();
        	 item.setYPos(y-prevH);
        	 frame.add(item);
        	 if (atlasW<x+item.getWidth())
        		 atlasW=x+item.getWidth();
        	 if (atlasH<y-prevH+item.getHeight())
        		 atlasH=y-prevH+item.getHeight();
        	 
        	 // set the properties.
        	 // set the URI
        	 for(Object obj:uri2Object.get(item.getURI())){	
        		 textUri.put(obj, String.format(completeAtlasPath,fileCounter));
        	 }
		}
		if (atlasH!=0||atlasW!=0){
	        textImage.put(String.format(completeAtlasPath,fileCounter),writeImage(atlasW, atlasH));
			 // set the new coordinates
			 modifyNewCorrdinates(frame,coordinatesHashMap,doubleCoordinateList,uri2Object,atlasW, atlasH);
			 frame.clear();
		}
		

		uri2Object.clear();
		isImageAcceptable.clear();
		doubleCoordinateList.clear();
		ti.setTexCoordinates(coordinatesHashMap);
		ti.setTexImages(textImage);
		ti.setTexImageURIs(textUri);
		ti.setLOG(LOG);
		bi.flush();
		g.dispose();
		g=null;
		bi=null;
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
		msp.setStripWidth(Math.min((totalw-maxw)/2+maxw,ImageMaxWidth));
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
			if (c[i]<-0.0005||c[i]>1.0005){
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
			ImageIcon ii;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bi.getSubimage(0, 0, w, h),"jpeg",baos);
			ii=new ImageIcon(baos.toByteArray());
			Image result =ii.getImage();
			ii=null;
			baos.flush();
			baos=null;
			
			fileCounter++;
			g.clearRect(0, 0, w, h);
		
		return result;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private void modifyNewCorrdinates(Vector<MyItem> items, HashMap<Object, String> coordinatesHashMap,HashMap<Object, double[]>doubleCoordinateList,HashMap<String,ArrayList<Object>> URI2OBJ, int atlasWidth, int atlasHeigth){
		Iterator<MyItem> itr= items.iterator();
		MyItem mit;
		while(itr.hasNext()){
			mit = itr.next();
			for(Object obj:URI2OBJ.get(mit.getURI())){
				coordinatesHashMap.put(obj,getCoordinate(doubleCoordinateList.get(obj),mit.getXPos(),mit.getYPos(), mit.getWidth(),mit.getHeight(),atlasWidth, atlasHeigth));
			}
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
			sb.append(' ');	
		}
		
		return sb.substring(0, sb.length()-1);
	}
	
	public void reset(){

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
