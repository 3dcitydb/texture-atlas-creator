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
package org.citygml.textureAtlasAPI.dataStructure;

import java.util.HashMap;

/**
 * In the case of loading CityGMl file, TexImageInfo is not sufficient. 
 * ImagesLocalPath, and TexGeneralProperties should be added.
 * ImagesLocalPath: Images should be load inside of API (RGB decoder). So it contains local path 
 * of each image which is accessible by imageURI(String). (refer to TexImageInfo for more info.)
 * TexGeneralProperties: General properties are the features that all the textures which are 
 * gathered in this object are sharing. 
 */
public class TexImageInfo4GMLFile extends TexImageInfo {
	
	// <imageURI, image local path>
	HashMap<String,String> ImagesLocalPath;
	
	public HashMap<String, String> getImagesLocalPath() {
		return ImagesLocalPath;
	}

	TexGeneralProperties generalProp; 
	
	public boolean isImageLoaded(){
		if (texImages==null||texImages.size()==0)
			return false;
		return true;
	}
	
	public TexGeneralProperties getGeneralProp() {
		return generalProp;
	}

	public void setGeneralProp(TexGeneralProperties generalProp) {
		this.generalProp = generalProp;
	}

	public void addTexImageURI(String Targer_Ring,String URI){
		if (texImageURIs ==null)
			texImageURIs = new HashMap<Object, String>();
		texImageURIs.put(Targer_Ring, URI);
		
	}
	
	public void addTexCoordinates(String Targer_Ring, String formatedcoordinates){
		if (texCoordinates ==null)
			texCoordinates = new HashMap<Object, String>();
		texCoordinates.put(Targer_Ring, formatedcoordinates);
	}
	
	public void addTexImages(String URI, String completePath){
		if (ImagesLocalPath ==null)
			ImagesLocalPath = new HashMap<String, String>();
		ImagesLocalPath.put(URI, completePath);
		// it was removed. is...
	}

	
	public void addAll(String Target,String ring, String imageURI, String coordinates){
		String tr= getTargetRing(Target, ring);
		addTexImageURI(tr,imageURI);
		addTexCoordinates(tr,coordinates);
		tr=null;
	}
	
	public static String getTargetRing(String Target, String Ring){
		return Target+' '+Ring;
	}
	
	public void clear(){
		if(texCoordinates!=null)
			texCoordinates.clear();
		if(texImages!=null)
			texImages.clear();
		if(texImageURIs!=null)
			texImageURIs.clear();
		if (ImagesLocalPath!=null)
			ImagesLocalPath.clear();
		texCoordinates=null;
		texImages=null;
		texImageURIs=null;	
		ImagesLocalPath=null;
	}
}
