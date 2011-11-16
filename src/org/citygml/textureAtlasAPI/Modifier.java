/*******************************************************************************
 * This file is part of the Texture Atlas Generation Tool.
 * Copyright (c) 2010 - 2011
 * Institute for Geodesy and Geoinformation Science
 * Technische Universitaet Berlin, Germany
 * http://www.gis.tu-berlin.de/
 * 
 * The Texture Atlas Generation Tool is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * @author Babak Naderi <b.naderi@mailbox.tu-berlin.de>
 ******************************************************************************/
package org.citygml.textureAtlasAPI;

import java.awt.Graphics2D;


import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Vector;
import org.citygml.textureAtlasAPI.dataStructure.ErrorTypes;
import org.citygml.textureAtlasAPI.dataStructure.TexGeneralProperties;
import org.citygml.textureAtlasAPI.dataStructure.TexImageInfo;
import org.citygml.textureAtlasAPI.dataStructure.TexImageInfo4GMLFile;
import org.citygml.textureAtlasAPI.dataStructure.TexImage;

import org.citygml.textureAtlasAPI.imageIO.ImageScaling;
import org.citygml.textureAtlasAPI.packer.Rect;
import org.citygml.textureAtlasAPI.packer.Atlas;


import org.citygml.textureAtlasAPI.packer.Packer;
import org.citygml.textureAtlasAPI.packer.comparator.StartHeightComparator;
//import org.citygml.util.Logger;


/**
 * This class is responsible for creating atlases, modifying coordinates and names for an instance of 
 * TexImageInfo. However, it does not modify unloaded TexImages and the one which has coordinates out of 
 * the rage [0,1].
 * Remaining textures will be divide in two groups: with/without alpha channel textures. They will be 
 * combined separately. As a result, atlases will be in PNG/JPEG image formats.
 * 
 */
public class Modifier {
	private int ImageMaxWidth;
	private int ImageMaxHeight;
	private int packingAlgorithm;
	boolean isDebug=false;
	
	
//	BufferedImage bi;
//	BufferedImage tmp;
//	Graphics g;
	int fileCounter=0;
	String completeAtlasPath;
	boolean overBorder=false;
	boolean userPOTS=false;
	
	
	
	public Modifier(int PackingAlg,  int atlasMaxWidth, int atlasMaxHeight, boolean userPOTS){
		setGeneralSettings(PackingAlg, atlasMaxWidth, atlasMaxHeight,userPOTS);
	}
	
	public void setGeneralSettings(int PackingAlg, int atlasMaxWidth, int atlasMaxHeight, boolean userPOTS){
		this.packingAlgorithm = PackingAlg;
		this.ImageMaxHeight=atlasMaxHeight;
		this.ImageMaxWidth= atlasMaxWidth;
		this.userPOTS=userPOTS;
		//TODO remove it just for debug
		if (isDebug)
			this.userPOTS=true;
		if (this.userPOTS){
			ImageMaxHeight= (int) Math.pow(2, Math.floor(Math.log10(ImageMaxHeight)/Math.log10(2)));
			ImageMaxWidth= (int) Math.pow(2, Math.floor(Math.log10(ImageMaxWidth)/Math.log10(2)));
			
		}
	}
	
	HashMap<Object, ErrorTypes> LOG;
	
	public HashMap<Object, ErrorTypes> getLOG(){
		return this.LOG;
	}
	
	/**
	 * main method which does the modification.
	 * @param ti
	 * @return
	 */
	public TexImageInfo run(TexImageInfo ti){

		fileCounter=0;
		completeAtlasPath=null;

		// Object as a key, coordinates 
		HashMap<Object, String> coordinatesHashMap =ti.getTexCoordinates();
		// URI , Image
		HashMap<String, TexImage> textImage= ti.getTexImages();
		// Object as a key, URI
		HashMap<Object, String> textUri= ti.getTexImageURIs();
		
		if (LOG==null)
			this.LOG =new HashMap<Object, ErrorTypes>(); 
		else
			LOG.clear();
		
		//list of objects which point to a same texture.
		HashMap<String, ArrayList<Object>> uri2Object = new HashMap<String, ArrayList<Object>>();
	
		// if the Image in the URI is not accepted it should be marked in here. 
		HashMap<String,Boolean> isImageAcceptable = new HashMap<String, Boolean>();
		// URI dictionary: <oldURI,newURI> 
		HashMap<String, String> URIDic= new HashMap<String, String>();
		// Object as a key, array of coordinates
		HashMap< Object, double[]> doubleCoordinateList= new HashMap<Object, double[]>();
		
		// statistic about width of 3 channels textures.  
		int totalWidth3c=0,maxw3c=0;
		// statistic about width of 4 channels textures.  		
		int totalWidth4c=0,maxw4c=0;
		// total needed area for 3chanel and 4chanel textures. 
		long area3c=0,area4c=0;
		
		// packers for 3 and 4 channels textures.
		Packer packer3C = new Packer(ImageMaxWidth,ImageMaxHeight,packingAlgorithm,false);
		Packer packer4C = new Packer(ImageMaxWidth,ImageMaxHeight,packingAlgorithm,true);
		
		int width, height;
		String URI,tmpURI;
		ArrayList<Object> list;
		Boolean b;
		TexImage tmpTextureImage;
		BufferedImage tmp;
		boolean is4Chanel=false;
		double[] coordinate;
		
		if (textUri==null){
			// it does not contain any texture!
			return ti;
		}
		// for all objects (the surface-geometry id (Long) in API | TargetURI+' '+Ring(String) in standalone tool)
		for (Object key : textUri.keySet()){
			URI= textUri.get(key);
				
			
			//Check whether this URI is changed before.
			if((tmpURI=URIDic.get(URI))!=null){
				// this URI previously have been changed, so textImage should also be changed before.
				textUri.put(key,tmpURI);
				URI=tmpURI;
			}else{
				tmpTextureImage= textImage.get(URI);
				if (tmpTextureImage!=null && tmpTextureImage.getBufferedImage()!=null ){
					// set a new uri based the number of channels in result.
					tmpURI= makeNewURI(URI, tmpTextureImage.getChanels());
					textUri.put(key,tmpURI);
					textImage.remove(URI);
					textImage.put(tmpURI, tmpTextureImage);
					URIDic.put(URI, tmpURI);
					URI=tmpURI;
				}
			}	
			tmpURI=null;
			tmpTextureImage=null;
			
			// The image was loaded before;
			if((b=isImageAcceptable.get(URI))!=null){
				if (!b.booleanValue()){
					// previously another object which has the same URI was rejected. Therefore this object
					// also will be rejected.
					//LOG.put(key,ErrorTypes.TARGET_PT_NOT_SUPPORTED);
					continue;
				}
				// the coordinates should be linked to the key and then continue.
				coordinate= formatCoordinates(coordinatesHashMap.get(key));
				if (coordinate==null){
					// previous coordinate was accepted but this one has a problem with coordinates
		        	isImageAcceptable.put(URI, new Boolean(false));
		        	width= textImage.get(URI).getBufferedImage().getWidth(null);
		        	
		        	// remove Item from list
		        	
		        	if (packer3C.removeRect(URI)){
			        	totalWidth3c-=width;
			        	// just for complicated cases.
			        	if (totalWidth3c<maxw3c)
			        		maxw3c=totalWidth3c;
		        		
		        	}
		        	if(packer4C.removeRect(URI)){
			        	totalWidth4c-=width;
			        	// just for complicated cases.
			        	if (totalWidth4c<maxw4c)
			        		maxw4c=totalWidth4c;
		        		
		        	}		        	
		        	continue;
		        }
				doubleCoordinateList.put(key, coordinate);
				uri2Object.get(URI).add(key);
				continue;
			}

			// report bug
			if ((tmp= textImage.get(URI).getBufferedImage())==null){
				// image is not available 
				isImageAcceptable.put(URI, new Boolean(false));
				LOG.put(key,ErrorTypes.IMAGE_IS_NOT_AVAILABLE);
				continue;			
			}
			is4Chanel=textImage.get(URI).getChanels()==4;
			 
			width= tmp.getWidth();
	        height= tmp.getHeight();
	        // check whether the size of image is acceptable
	        if (!acceptATexture(width,height)){
	        	tmp = ImageScaling.rescale(tmp, ImageMaxWidth, ImageMaxHeight);
	        	if (tmp==null||!acceptATexture(width= tmp.getWidth(null),height= tmp.getHeight(null))){
					isImageAcceptable.put(URI, new Boolean(false));
					LOG.put(key, ErrorTypes.IMAGE_UNBONDED_SIZE);
					continue;
	        	}
	        	textImage.get(URI).setImage(tmp);
	        }
	        coordinate= formatCoordinates(coordinatesHashMap.get(key));
	        //LOG if coordinates have any problem, do not touch it! (like n. available or wrapping textures)
	        if (coordinate==null){
	        	isImageAcceptable.put(URI, new Boolean(false));
	        	LOG.put(key,ErrorTypes.ERROR_IN_COORDINATES);
	        	continue;
	        }
	        // setting the name of atlas.
	        if (completeAtlasPath==null)
				completeAtlasPath= URI.substring(0,URI.lastIndexOf('.'))+"_%1d.";
	        

	        // everything is alright with the current Key object. so the values will be set in data structures. 
	        doubleCoordinateList.put(key, coordinate);
            if (is4Chanel){
            	packer4C.addRect(URI,width, height);
            	if (width>maxw4c)
    	        	maxw4c=width;	
                totalWidth4c+=width;
                area4c+=width*height;
            }else{
            	packer3C.addRect(URI, width,height);
            	if (width>maxw3c)
    	        	maxw3c=width;	
                totalWidth3c+=width;
                area3c+=width*height;
            }
            
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

		/**
		 * Until know textures are grouped in:
		 *   - unsupported ones. they will not touched.
		 *   - 3channels images
		 *   - 4channels images.
		 * Atlas will be generate for both 3channels and 4channels groups.  
		 */
		ArrayList<Atlas> atlasMR = new ArrayList<Atlas>();
		if (packer3C.getSize()!=0)
			atlasMR.add(iterativePacker(packer3C, maxw3c,totalWidth3c,area3c));
		if (packer4C.getSize()!=0)
			atlasMR.add(iterativePacker(packer4C, maxw4c,totalWidth4c,area4c));
		
		// for all available atlases modify the coordinates and URIs.
		try{
			// Power Of Two size
			int potW,potH;
		for (Atlas mr:atlasMR){	
			if (Math.min(mr.getBindingBoxHeight(), mr.getBindingBoxWidth())<1)
				continue;
			is4Chanel=mr.isFourChanel();
			BufferedImage bi = new BufferedImage(Math.min(getMinCoveredPOT(mr.getBindingBoxWidth()),
					ImageMaxWidth), Math.min(getMinCoveredPOT(mr.getBindingBoxHeight()),ImageMaxHeight),
					is4Chanel ? BufferedImage.TYPE_INT_ARGB
							: BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bi.createGraphics();
			// each atlas may be divided to several ones.
			// @TODO The result atlas should be fixed and not divided to more atlases.
			// start to make atlas. prevH: amount of height of strip which was written in file before.
			int x,y, prevH=0;
			int atlasW=0,atlasH=0;
			// list of all items which will be drawn in a same atlas.
			Vector<Rect> frame = new Vector<Rect>();
			
			//going in side of Result
			ArrayList<Rect>  allItems=mr.getAllItems();			
			if (this.packingAlgorithm!= TextureAtlasGenerator.TPIM&&
					this.packingAlgorithm!= TextureAtlasGenerator.TPIM_WITHOUT_ROTATION)
				Collections.sort(allItems, new StartHeightComparator());
			Iterator<Rect> all= allItems.iterator();
			
			Rect item=null;
			
			int currentLevel=0;
			// for all rects inside of this atlas:
			while(all.hasNext()){
				item = all.next();		
				if (item.rotated){
					BufferedImage bif = textImage.get(item.getURI()).getBufferedImage();
					AffineTransform at = new AffineTransform();
					at.translate ( 0,bif.getWidth() ) ;
			        at.rotate(Math.toRadians(-90));
			        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);		       
					BufferedImage bb= new BufferedImage(bif.getHeight(), bif.getWidth(), bif.getType());
					ato.filter(bif, bb);
			        textImage.get(item.getURI()).setImage(bb);
			        bif=null;
				}
				
	        	 x= item.x;
	        	 y= item.y;
	        	 // check whether the current atlas is full.
	        	 if (y-prevH+item.height>ImageMaxHeight||((this.packingAlgorithm==TextureAtlasGenerator.TPIM||this.packingAlgorithm==TextureAtlasGenerator.TPIM_WITHOUT_ROTATION)&&currentLevel!=item.level)){
	        		 // set Image in Hashmap and write it to file.
	        		 potW=getMinCoveredPOT(atlasW);
	        		 potH=getMinCoveredPOT(atlasH);
	        			
	        		 textImage.put(String.format(completeAtlasPath,fileCounter)+(is4Chanel?"png":"jpeg"),new TexImage(bi.getSubimage(0, 0,potW , potH)));
	        		 g.dispose();
	        		 fileCounter++;
	        		 bi=null;
	        		 g=null;
	        		 
	        		 bi = new BufferedImage(Math.min(getMinCoveredPOT(mr.getBindingBoxWidth()),
	        					ImageMaxWidth), Math.min(getMinCoveredPOT(mr.getBindingBoxHeight()), ImageMaxHeight),
	        					is4Chanel ? BufferedImage.TYPE_INT_ARGB
	        							: BufferedImage.TYPE_INT_RGB);
	        		 g = bi.createGraphics();
	        		 // modify coordinate for all textures which are fixed in the current atlas.
	        		 modifyNewCorrdinates(frame,coordinatesHashMap,doubleCoordinateList,uri2Object,potW, potH);
	//        		 analyzeOccupation(frame,atlasW,atlasH);
	        		 frame.clear();
	        		 atlasW=0;
	        		 atlasH=0;
	        		 prevH=y;
	        		 currentLevel=item.level;
	        		 if(this.packingAlgorithm==TextureAtlasGenerator.TPIM||this.packingAlgorithm==TextureAtlasGenerator.TPIM_WITHOUT_ROTATION)
	        			 prevH=0; 
	        	 }
	        	 g.drawImage(textImage.get(item.getURI()).getBufferedImage(), x, y-prevH, null);
	        	 textImage.remove(item.getURI()).freeMemory();
	        	 item.y=(y-prevH);
	        	 frame.add(item);
	        	 if (atlasW<x+item.width)
	        		 atlasW=x+item.width;
	        	 if (atlasH<y-prevH+item.height)
	        		 atlasH=y-prevH+item.height;

	        	 // set the URI
	        	 for(Object obj:uri2Object.get(item.getURI())){	
	        		 textUri.put(obj, String.format(completeAtlasPath,fileCounter)+(is4Chanel?"png":"jpeg"));
	        	 }
			}
			if (atlasH!=0||atlasW!=0){
				 potW=getMinCoveredPOT(atlasW);
        		 potH=getMinCoveredPOT(atlasH);
        		 
				textImage.put(String.format(completeAtlasPath,fileCounter)+(is4Chanel?"png":"jpeg"),new TexImage(bi.getSubimage(0, 0,potW , potH)));
				fileCounter++;
				 // set the new coordinates
				 modifyNewCorrdinates(frame,coordinatesHashMap,doubleCoordinateList,uri2Object,potW, potH);
	//			 analyzeOccupation(frame,atlasW,atlasH);
				 frame.clear();
			}
			
			if (ti instanceof TexImageInfo4GMLFile){
				if (((TexImageInfo4GMLFile) ti).getGeneralProp()==null)
					((TexImageInfo4GMLFile) ti).setGeneralProp(new TexGeneralProperties());
				((TexImageInfo4GMLFile) ti).getGeneralProp().setMIMEType(is4Chanel?"image/png":"image/jpeg");
			}
			bi=null;
			g.dispose();
			g=null;
			bi=null;
		}}
		catch(Exception e){
			e.printStackTrace();
		}
		uri2Object.clear();
		isImageAcceptable.clear();
		doubleCoordinateList.clear();
		URIDic.clear();
		ti.setTexCoordinates(coordinatesHashMap);
		ti.setTexImages(textImage);
		ti.setTexImageURIs(textUri);

		return ti;
	}
	
	private String makeNewURI(String prevURI, int chanel){
		return prevURI.substring(0, prevURI.lastIndexOf('.'))+(chanel==3?".jpeg":".png");
	}
	
	private int getMinCoveredPOT(int len){
		if (!userPOTS)
			return len;
		int minPOT=(int) Math.floor(Math.log10(len)/Math.log10(2));
		if (Math.pow(2,minPOT)== len)
			return len;
		
		if (isDebug){
			int tmp=(int)Math.pow(2,minPOT +1);
			return tmp;
		}
		return (int)Math.pow(2, minPOT+1);
	}
	
	
	/**
	 * size checking
	 * @param width
	 * @return
	 */
	private boolean acceptATexture(int width, int heigth){
		if (width<=ImageMaxWidth && heigth<=ImageMaxHeight)
			return true;
		return false;
		
	}
	
	private Atlas iterativePacker(Packer msp, int maxw, int totalw, long area){
		if (this.packingAlgorithm==TextureAtlasGenerator.TPIM||this.packingAlgorithm==TextureAtlasGenerator.TPIM_WITHOUT_ROTATION)
			msp.setBinSize(ImageMaxWidth,ImageMaxHeight);
		else{
			
			if (!userPOTS)
				msp.setBinSize(Math.min((totalw-maxw)/2+maxw,ImageMaxWidth),ImageMaxHeight);
			else{
				int floorPOT= (int)Math.floor(Math.sqrt(area));
				floorPOT=Math.min(floorPOT,ImageMaxWidth);
				// minimum possible POT that can can cover all tetxures. (2^minPOT>maxw)
	//			int minPower= (int) Math.floor(Math.log10(maxw)/Math.log10(2))+1;
	//			int maxPower= (int) Math.min(Math.floor(Math.log10(floorPOT)/Math.log10(2))+1,
	//					Math.floor(Math.log10(ImageMaxWidth)/Math.log10(2)));
	//			
	//			long minWastedArea=Long.MAX_VALUE;
	//			int minWastedAreaWidthPower=minPower;
	//			int numberOfAtlas=Integer.MAX_VALUE-2;
	//			int candidate=minPower;
	//			long tmparea;
	//			long wastarea;
	//			while(candidate<=maxPower){
	//				tmparea= (long)Math.pow(2, candidate);
	//				wastarea= tmparea- (area%tmparea);
	//				if (wastarea<minWastedArea || ){
	//					minWastedAreaWidthPower=candidate;
	//					minWastedArea=wastarea;
	//				}
	//				candidate=candidate*2;
	//			}
				
				msp.setBinSize((int)Math.min(Math.pow(2, (int) Math.floor(Math.log10(floorPOT)/Math.log10(2))+1),ImageMaxWidth),
						ImageMaxHeight);
			}
		}try{
			return msp.pack(userPOTS);	
		}catch(Exception e){
//			if (Logger.SHOW_STACK_PRINT)
//				e.printStackTrace();
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
			if (c[i]<-0.1||c[i]>1.1){
				sc=null;
				return null;
			}
		}
		sc=null;
		coordinates=null;
		return c;
	}
	
//	private BufferedImage getImage(int w, int h, BufferedImage bi){
//		return bi.getSubimage(0, 0,getMinCoveredPOT(w) , getMinCoveredPOT(h));
//	}
	
	private void modifyNewCorrdinates(Vector<Rect> items, HashMap<Object, String> coordinatesHashMap,HashMap<Object, double[]>doubleCoordinateList,HashMap<String,ArrayList<Object>> URI2OBJ, int atlasWidth, int atlasHeigth){
		Iterator<Rect> itr= items.iterator();
		Rect mit;
		while(itr.hasNext()){
			mit = itr.next();
			for(Object obj:URI2OBJ.get(mit.getURI())){
				coordinatesHashMap.put(obj,getCoordinate(doubleCoordinateList.get(obj),mit.x,mit.y, mit.width,mit.height,atlasWidth, atlasHeigth,mit.rotated));

			}
			mit.clear();
		}
		itr=null;
	}
	/**
	private void analyzeOccupation(Vector<Rect> items,int atlasW,int atlasH){
		Iterator<Rect> itr= items.iterator();
		Rect mit;
		int sumArea=0;
		int num=0;
		while(itr.hasNext()){
			num++;
			mit = itr.next();
			sumArea+= mit.area;
		}
		itr=null;
		
		System.out.println(num+","+(float)(sumArea)/(atlasH*atlasW));
	}**/
	
	private String getCoordinate(double[]coordinates, double posX, double posY, double imW,double imH ,double atlasw, double atlasH, boolean rotated){		
		StringBuffer sb = new StringBuffer(coordinates.length*15);
		double tmp;
		
		for (int j = 0; j < coordinates.length; j += 2) {
			if (rotated){
				
				tmp =coordinates[j];
				coordinates[j]=1- coordinates[j+1];
				coordinates[j+1]= tmp;
			
			}
			// Horizontal
			coordinates[j] = (posX+(coordinates[j] * imW))/atlasw;
			// corner as a origin,but cityGML used left down corner.
			coordinates[j + 1] =1-((1-coordinates[j+1])*imH+posY)/atlasH;
			
//			coordinates[j + 1] =((atlasH-posY-imH)+coordinates[j+1]*imH)/atlasH;

			sb.append(coordinates[j]);
			sb.append(' ');
			sb.append(coordinates[j+1]);
			sb.append(' ');	
		}
//		System.out.println("#"+sb.substring(0, sb.length()-1));
		return sb.substring(0, sb.length()-1);
	}
	
	public void reset(){
		completeAtlasPath=null;
	}
}
