package org.citygml.Model.DataStructures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleSurfaceDataMember {
	// To get Image URI
	private String imagePath ;
	
	// To get coordinates' list. It contains some objects of TextureCoordinates class.  
	private List<Double> coordinates;
	
	private String targetURI;
	
	private String ring;
	
	private String imageMIMEType=null;
	

	public SimpleSurfaceDataMember(String imagePath ,List<Double> textureCoordinates, String targetURI, String ring){
		this.imagePath=imagePath.replace('\\', '/');
		this.coordinates =textureCoordinates;
		this.targetURI= targetURI;
		this.ring = ring;
	}
	
	/**
	 * @return the targetURI
	 */
	public String getTargetURI() {
		return targetURI;
	}

	/**
	 * @return the ring
	 */
	public String getRing() {
		return ring;
	}

	/**
	 * @return the ParameterizedTexture's image path.
	 */
	public String getImageURI() {
		return imagePath;
	}
	/**
	 * Set ParameterizedTexture's Image Path
	 */
	public void setImageURI(String path) {
		imagePath=path;
	}
	//????
	public int getAmountofTextureCoordinates(){
		return coordinates==null?0:coordinates.size();
	}
	
	/**
	 * return a simple array which contains all coordinates. 
	 * 
	 * @return list of coordinates.
	 */
	public double[] getCoordinates(){
		double[] d = new double[coordinates.size()];
		Iterator< Double> it= coordinates.iterator();
		int i=0;
		while(it.hasNext()){
			d[i] =  it.next().doubleValue();
			i++;
		}
			
		return d;
	}
	
	// why list?
	public List<Double> getDoubleCoordinates(){		
		return coordinates;
	}

	/**
	 * @return the imageMIMEType
	 */
	public String getImageMIMEType() {
		return imageMIMEType;
	}

	/**
	 * @param imageMIMEType the imageMIMEType to set
	 */
	public void setImageMIMEType(String imageMIMEType) {
		this.imageMIMEType = imageMIMEType;
	}
	
	/**
	 * set correct coordinates for the  textureCoordinates. 
	 * @param coordinates
	 */
	public void setCoordinates(double[] coordinates){
		this.coordinates=null;
		this.coordinates= new ArrayList<Double>();
		for (int i=0;i<coordinates.length;i++){
			this.coordinates.add(new Double(coordinates[i]));			
		}
		
	}
	
	public void modifyCoordinates(TexturePropertiesInAtlas ip){
		
	}

}
