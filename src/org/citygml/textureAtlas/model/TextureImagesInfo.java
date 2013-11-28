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
package org.citygml.textureAtlas.model;

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
