/*
 * 3D City Database Texture Atlas Creator
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2016
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.gis.bgu.tum.de/
 * 
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 * 
 * virtualcitySYSTEMS GmbH, Berlin <http://www.virtualcitysystems.de/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.citydb.textureAtlas.model;

import java.util.HashMap;

/**
 * This class is used to carry all information of a Building object (or CityObject) which are necessary 
 * for generating a texture atlas. This class also will be used as a data structure to carry
 * result which is texture atlas. 
 *  
 * This is a data structure for set/get data to Texture Atlas Generator API.
 * 
 * <I>HashMap<Object, String> texImageURIs</I>
 * texImgeURIs contains  a key (the surface-geometry id (Long) in DB usage and TargetURI+' '+Ring(String) in GML loader)
 * and corresponding imageURI(String). By using imageURI as a key for texImages hashmap, texture will be available.
 * 
 * <I>HashMap<Object, String> texCoordinates</I>
 * texCoordingates contains a key (the surface-geometry id (Long) in DB usage and TargetURI+' '+Ring(String) in GML loader)
 * and corresponding texture coordinates in the same format as in the database (string tokenizable via whitespace)
 * 
 * <I>HashMap<String, TexImage> texImages</I>
 * textImages contains an imageURI(String) and the corresponding TexImage object which is a texture.
 */
public class TextureImagesInfo {
	/**
	 * texImgeURIs contains  a key (the surface-geometry id (Long) in DB usage and TargetURI+' '+Ring(String) in GML loader)
	 * and corresponding imageURI(String). By using imageURI as a key for texImages hashmap, texture will be available.   
	 */
	protected HashMap<Object, String> texImageURIs ;
	
	/**
	 * texCoordingates contains a key (the surface-geometry id (Long) in DB usage and TargetURI+' '+Ring(String) in GML loader)
	 *  and corresponding texture coordinates in the same format as in the database (string tokenizable via whitespace)  
	 */
	protected HashMap<Object, String> texCoordinates;
	
	/**
	 * textImages contains an imageURI(String) and the corresponding TexImage object which is a texture. 
	 */
	protected HashMap<String, TextureImage> texImages;
	
	/**
	 * @return the texImageURIs
	 */
	public HashMap<Object, String> getTexImageURIs() {
		return texImageURIs;
	}

	/**
	 * @param texImageURIs the texImageURIs to set
	 */
	public void setTexImageURIs(HashMap<Object, String> texImageURIs) {
		this.texImageURIs = texImageURIs;
	}

	/**
	 * @return the texCoordinates
	 */
	public HashMap<Object, String> getTexCoordinates() {
		return texCoordinates;
	}

	/**
	 * @param texCoordinates the texCoordinates to set
	 */
	public void setTexCoordinates(HashMap<Object, String> texCoordinates) {
		this.texCoordinates = texCoordinates;
	}

	/**
	 * @return the texImages
	 */
	public HashMap<String, TextureImage> getTexImages() {
		return texImages;
	}

	/**
	 * @param texImages the texImages to set
	 */
	public void setTexImages(HashMap<String, TextureImage> texImages) {
		this.texImages = texImages;
	}
}
