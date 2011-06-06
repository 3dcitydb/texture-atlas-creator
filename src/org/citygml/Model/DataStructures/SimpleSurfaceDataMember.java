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

}
