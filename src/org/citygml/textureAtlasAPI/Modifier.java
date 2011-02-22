package org.citygml.textureAtlasAPI;

import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Vector;

import javax.imageio.ImageIO;

import org.citygml.textureAtlasAPI.dataStructure.ErrorTypes;
import org.citygml.textureAtlasAPI.dataStructure.TexImageInfo;
import org.citygml.textureAtlasAPI.dataStructure.TexImageInfo4GMLFile;
import org.citygml.textureAtlasAPI.dataStructure.TexImage;
import org.citygml.textureAtlasAPI.imageIO.ImageScaling;
import org.citygml.textureAtlasAPI.packer.AbstractRect;
import org.citygml.textureAtlasAPI.packer.Atlas;
import org.citygml.textureAtlasAPI.packer.Packer;
import org.citygml.textureAtlasAPI.packer.Rect;

public class Modifier {
	private int ImageMaxWidth;
	private int ImageMaxHeight;
	private int packingAlgorithm;

	
//	BufferedImage bi;
//	BufferedImage tmp;
//	Graphics g;
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
	}
	
	/**
	 * TODO check the code whether for several texCoordiLists for a parameterizedTexture.
	 * TODO add try catch to get errors.
	 * @param ti
	 * @return
	 */
	HashMap<Object, ErrorTypes> LOG;
	
	public HashMap<Object, ErrorTypes> getLOG(){
		return this.LOG;
	}
	public TexImageInfo run(TexImageInfo ti){

//		if (bi==null){
//			bi=new BufferedImage(ImageMaxWidth, ImageMaxHeight,BufferedImage.TYPE_INT_RGB);
//			g = bi.getGraphics();
//		}
		fileCounter=0;
		completeAtlasPath=null;
//		g.clearRect(0, 0, ImageMaxWidth, ImageMaxHeight);

		HashMap<Object, String> coordinatesHashMap =ti.getTexCoordinates();
		HashMap<String, TexImage> textImage= ti.getTexImages();
		HashMap<Object, String> textUri= ti.getTexImageURIs();
		if (LOG==null)
			this.LOG =new HashMap<Object, ErrorTypes>(); 
		else
			LOG.clear();
		
		HashMap<String, ArrayList<Object>> uri2Object = new HashMap<String, ArrayList<Object>>();
	
		HashMap<String,Boolean> isImageAcceptable = new HashMap<String, Boolean>();
		HashMap<String, String> URIDic= new HashMap<String, String>();
		HashMap< Object, double[]> doubleCoordinateList= new HashMap<Object, double[]>();
		

		int totalWidth3c=0,maxw3c=0;
		int totalWidth4c=0,maxw4c=0;
		
		//!?!?! should I make it again?
		Packer packer3C = new Packer(ImageMaxWidth,ImageMaxHeight,packingAlgorithm,false);
		Packer packer4C = new Packer(ImageMaxWidth,ImageMaxHeight,packingAlgorithm,true);
		// target URI: surface ID
		int width, height;
		String URI,tmpURI;
		ArrayList<Object> list;
		Boolean b;
		TexImage tmpTextureImage;
		BufferedImage tmp;
		boolean is4Chanel=false;
		double[] coordinate;
		if (textUri==null){
			System.err.println("------ NO Texture");
			return ti;
		}
		for (Object key : textUri.keySet()){
			URI= textUri.get(key);

			//Check whether this URI is changed before.
			if((tmpURI=URIDic.get(URI))!=null){
				// this URI previously have been changed, so textImage should also be changed before.
				textUri.put(key,tmpURI);
				URI=tmpURI;
			}else{
				tmpTextureImage= textImage.get(URI);
				if (tmpTextureImage!=null){
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
			
			// The image was read before;
			if((b=isImageAcceptable.get(URI))!=null){
				if (!b.booleanValue()){
					//LOG.put(key,ErrorTypes.TARGET_PT_NOT_SUPPORTED);
					continue;
				}
				// the coordinates should be added and then continue.
				coordinate= formatCoordinates(coordinatesHashMap.get(key));
				// previous coordinate was accepted but this one has a problem with coordinates
				if (coordinate==null){
		        	isImageAcceptable.put(URI, new Boolean(false));
		        	width= textImage.get(URI).getBufferedImage().getWidth(null);
		        	
		        	// remove Item from list
		        	
		        	if (packer3C.removeItem(URI)){
			        	totalWidth3c-=width;
			        	// just for complicated cases.
			        	if (totalWidth3c<maxw3c)
			        		maxw3c=totalWidth3c;
		        		
		        	}
		        	if(packer4C.removeItem(URI)){
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
			
			
			if (completeAtlasPath==null)
				completeAtlasPath= URI.substring(0,URI.lastIndexOf('.'))+"_%1d.";
			
			// report bug
			if ((tmp= textImage.get(URI).getBufferedImage())==null){
				isImageAcceptable.put(URI, new Boolean(false));
				LOG.put(key,ErrorTypes.IMAGE_IS_NOT_AVAILABLE);
				continue;			
			}
			is4Chanel=textImage.get(URI).getChanels()==4;
			 
			width= tmp.getWidth();
	        height= tmp.getHeight();
	        //LOG if the image is not accepted, do not touch it! size problem
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
	        doubleCoordinateList.put(key, coordinate);
            if (is4Chanel){
            	packer4C.addItem(URI, width, height);
            	if (width>maxw4c)
    	        	maxw4c=width;	
                totalWidth4c+=width;
            }else{
            	packer3C.addItem(URI, width, height);
            	if (width>maxw3c)
    	        	maxw3c=width;	
                totalWidth3c+=width;
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
		
		

		
		ArrayList<Atlas> atlasMR = new ArrayList<Atlas>();
		if (packer3C.getSize()!=0)
			atlasMR.add(iterativePacker(packer3C, maxw3c,totalWidth3c));
		if (packer4C.getSize()!=0)
			atlasMR.add(iterativePacker(packer4C, maxw4c,totalWidth4c));
		
		for (Atlas mr:atlasMR){	
			if (Math.min(mr.getBindingBoxHeight(), mr.getBindingBoxWidth())<1)
				continue;
			is4Chanel=mr.isFourChanel();
			BufferedImage bi = new BufferedImage(Math.min(mr.getBindingBoxWidth(),
					ImageMaxWidth), Math.min(mr.getBindingBoxHeight(),ImageMaxHeight),
					is4Chanel ? BufferedImage.TYPE_INT_ARGB
							: BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bi.createGraphics();
			// start to make atlas. prevH: amount of height of strip which was written in file before.
			int x,y, prevH=0;
			int atlasW=0,atlasH=0;
			// list of all items which will be drawn in a same atlas.
			Vector<AbstractRect> frame = new Vector<AbstractRect>();
			
			//going in side of Result
			Iterator<AbstractRect> all= mr.getAllItems().iterator();
			AbstractRect item;
			
			int currentLevel=0;
			while(all.hasNext()){
	
				item = all.next();		
				if (item.rotated){
					// just for test mode.
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
				
	        	 x= item.getXPos();
	        	 y= item.getYPos();
	        	 if (y-prevH+item.getHeight()>ImageMaxHeight||((this.packingAlgorithm==TextureAtlasGenerator.TPIM||this.packingAlgorithm==TextureAtlasGenerator.TPIM_WITHOUT_ROTATION)&&currentLevel!=item.getLevel().intValue())){
	        		 // set Image in Hashmap and write it to file.
	//        		 textImage.put(String.format(completeAtlasPath,fileCounter)+(is4Chanel?"png":"jpeg"),new TexImage(getImage(atlasW, atlasH,bi),is4Chanel?4:3));
	        		 textImage.put(String.format(completeAtlasPath,fileCounter)+(is4Chanel?"png":"jpeg"),new TexImage(getImage(atlasW, atlasH,bi)));
	        		 g.dispose();
	        		 fileCounter++;
	        		 bi=null;
	        		 g=null;
	        		 
	        		 bi = new BufferedImage(Math.min(mr.getBindingBoxWidth(),
	        					ImageMaxWidth), Math.min(mr.getBindingBoxHeight(), ImageMaxHeight),
	        					is4Chanel ? BufferedImage.TYPE_INT_ARGB
	        							: BufferedImage.TYPE_INT_RGB);
	        		 g = bi.createGraphics();
	        		 // set the new coordinates
	        		 modifyNewCorrdinates(frame,coordinatesHashMap,doubleCoordinateList,uri2Object,atlasW, atlasH);
	//        		 analyzeOccupation(frame,atlasW,atlasH);
	        		 frame.clear();
	        		 atlasW=0;
	        		 atlasH=0;
	        		 prevH=y;
	        		 currentLevel=item.getLevel().intValue();
	        		 if(this.packingAlgorithm==TextureAtlasGenerator.TPIM||this.packingAlgorithm==TextureAtlasGenerator.TPIM_WITHOUT_ROTATION)
	        			 prevH=0; 
	        	 }
	        	 g.drawImage(textImage.get(item.getURI()).getBufferedImage(), x, y-prevH, null);
	        	 textImage.remove(item.getURI()).freeMemory();
	        	 item.setYPos(y-prevH);
	        	 frame.add(item);
	        	 if (atlasW<x+item.getWidth())
	        		 atlasW=x+item.getWidth();
	        	 if (atlasH<y-prevH+item.getHeight())
	        		 atlasH=y-prevH+item.getHeight();
	        	 
	        	 // set the properties.
	        	 // set the URI
	        	 for(Object obj:uri2Object.get(item.getURI())){	
	        		 textUri.put(obj, String.format(completeAtlasPath,fileCounter)+(is4Chanel?"png":"jpeg"));
	        	 }
			}
			if (atlasH!=0||atlasW!=0){
	//	        textImage.put(String.format(completeAtlasPath,fileCounter)+(is4Chanel?"png":"jpeg"),new TexImage(getImage(atlasW, atlasH,bi),is4Chanel?4:3));
				textImage.put(String.format(completeAtlasPath,fileCounter)+(is4Chanel?"png":"jpeg"),new TexImage(getImage(atlasW, atlasH,bi)));
				fileCounter++;
				 // set the new coordinates
				 modifyNewCorrdinates(frame,coordinatesHashMap,doubleCoordinateList,uri2Object,atlasW, atlasH);
	//			 analyzeOccupation(frame,atlasW,atlasH);
				 frame.clear();
			}
			
			if (ti instanceof TexImageInfo4GMLFile)
				((TexImageInfo4GMLFile) ti).getGeneralProp().setMIMEType(is4Chanel?"image/png":"image/jpeg");
			bi=null;
			g.dispose();
			g=null;
			bi=null;
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
	private void testImageWriter(BufferedImage bitext){
		try{
			ImageIO.write(bitext,"jpeg",new File("C:/test1.jpg"));
		
		}catch(Exception e){
			e.printStackTrace();
		}
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
	
	private Atlas iterativePacker(Packer msp, int maxw, int totalw){
		if (this.packingAlgorithm==TextureAtlasGenerator.TPIM||this.packingAlgorithm==TextureAtlasGenerator.TPIM_WITHOUT_ROTATION)
			msp.setSize(ImageMaxWidth,ImageMaxHeight);
		else
			msp.setSize(Math.min((totalw-maxw)/2+maxw,ImageMaxWidth),ImageMaxHeight);
		try{
			return msp.getResult();	
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
			if (c[i]<-0.1||c[i]>1.1){
				sc=null;
				return null;
			}
		}
		sc=null;
		coordinates=null;
		return c;
	}
	
	private BufferedImage getImage(int w, int h, BufferedImage bi){
		return bi.getSubimage(0, 0, w, h);
		/**
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
		}**/
	}
	
	private void modifyNewCorrdinates(Vector<AbstractRect> items, HashMap<Object, String> coordinatesHashMap,HashMap<Object, double[]>doubleCoordinateList,HashMap<String,ArrayList<Object>> URI2OBJ, int atlasWidth, int atlasHeigth){
		Iterator<AbstractRect> itr= items.iterator();
		AbstractRect mit;
		while(itr.hasNext()){
			mit = itr.next();
			for(Object obj:URI2OBJ.get(mit.getURI())){
				coordinatesHashMap.put(obj,getCoordinate(doubleCoordinateList.get(obj),mit.getXPos(),mit.getYPos(), mit.getWidth(),mit.getHeight(),atlasWidth, atlasHeigth,mit.rotated));
//				coordinatesHashMap.put(obj,getCoordinate2(doubleCoordinateList.get(obj),mit.getXPos(),mit.getYPos(), mit.getWidth(),mit.getHeight(),atlasWidth, atlasHeigth,mit.rotated));
			}
			mit.clear();
		}
		itr=null;
	}
	
	private void analyzeOccupation(Vector<AbstractRect> items,int atlasW,int atlasH){
		Iterator<AbstractRect> itr= items.iterator();
		AbstractRect mit;
		int sumArea=0;
		int num=0;
		while(itr.hasNext()){
			num++;
			mit = itr.next();
			sumArea+= mit.area;
		}
		itr=null;
		
		System.out.println(num+","+(float)(sumArea)/(atlasH*atlasW));
		
	}
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
	
	private String getCoordinate2(double[]coordinates, double posX, double posY, double imW,double imH ,double atlasw, double atlasH, boolean rotated){
		StringBuffer sb = new StringBuffer(coordinates.length*15);
		
		BigDecimal bdPosX= BigDecimal.valueOf(posX);
		BigDecimal bdPosY= BigDecimal.valueOf(posY);
		BigDecimal bdImW= BigDecimal.valueOf(imW);
		BigDecimal bdImH= BigDecimal.valueOf(imH);
		BigDecimal bdAtlasw= BigDecimal.valueOf(atlasw);
		BigDecimal bdAtlash= BigDecimal.valueOf(atlasH);
		BigDecimal cor1;
		BigDecimal cor2;
		double tmp;

		for (int j = 0; j < coordinates.length; j += 2) {
			cor1=null;
			cor2=null;
			cor1= BigDecimal.valueOf(coordinates[j]);
			cor2= BigDecimal.valueOf(coordinates[j+1]);
//			System.out.println(cor1.doubleValue());
//			System.out.println(coordinates[j]);
			if (rotated){			
				cor1= BigDecimal.ONE.subtract(cor2);
				cor2=BigDecimal.valueOf(coordinates[j]);
				tmp =coordinates[j];
//				coordinates[j]=1- coordinates[j+1];
//				coordinates[j+1]= tmp;		
			}
			
			
			// Horizontal
//			System.out.println(bdPosX.doubleValue()+","+cor1.doubleValue()+","+bdImW.doubleValue());
//			System.out.println(posX+","+coordinates[j]+","+imW);
//			
//			System.out.println((bdPosX.add(cor1.multiply(bdImW))).doubleValue());
//			System.out.println((posX+(coordinates[j] * imW)));
//			
//			System.out.println((bdPosX.add(cor1.multiply(bdImW))).divide(bdAtlasw,30,BigDecimal.ROUND_DOWN).doubleValue());
//			System.out.println((posX+(coordinates[j] * imW))/atlasw);
			
			sb.append((bdPosX.add(cor1.multiply(bdImW))).divide(bdAtlasw,30,BigDecimal.ROUND_CEILING).doubleValue());
			sb.append(' ');
			
			
			// corner as a origin,but cityGML used left down corner.
			sb.append(BigDecimal.ONE.subtract((BigDecimal.ONE.subtract(cor2).multiply(bdImH).add(bdPosY)).divide(bdAtlash,30,BigDecimal.ROUND_CEILING)).doubleValue());
			sb.append(' ');
//			System.out.println(BigDecimal.ONE.subtract((BigDecimal.ONE.subtract(cor2).multiply(bdImH).add(bdPosY)).divide(bdAtlash,30,BigDecimal.ROUND_CEILING)).doubleValue());
//			System.out.println(1-((1-coordinates[j+1])*imH+posY)/atlasH);
			
		}
		System.out.println("*"+sb.substring(0, sb.length()-1));
		bdPosX=null;
		bdPosY=null;
		bdImW=null;
		bdImH=null;
		bdAtlasw=null;
		bdAtlash=null;
		cor1=null;
		cor2=null;

	
		return sb.substring(0, sb.length()-1);
		//return null;
		
	}
	
	public void reset(){
/**
		if(bi!=null){
		bi.flush();
		bi=null;
		}
		if(tmp!=null){
			tmp.flush();
			tmp=null;
		}
		
		g=null;**/
		completeAtlasPath=null;
	}
}
