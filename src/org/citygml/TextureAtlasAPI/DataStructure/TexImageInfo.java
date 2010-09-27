package org.citygml.TextureAtlasAPI.DataStructure;

import java.awt.Image;
import java.util.HashMap;



/**
 * This class will be used to carry all information of a Building object which are necessary 
 * for generating a texture atlas. This class also will be used as a data structure to carry
 * result which is texture atlas. 
 *  
 * This is a data structure for set/get data to Texture Atlas Generator API.
 * @author babak naderi
 *
 */
public class TexImageInfo {
	/**
	 * texImgeURIs contains the surface-geometry id (Long) and corresponding imageURI(String)
	 */
	protected HashMap<Long, String> texImageURIs ;
	
	/**
	 * texCoordingates contains the surface-geometry id (long) and corresponding texture coordinates
	 * in the same format as in the database (string tokenizable via whitespace)  
	 */
	protected HashMap<Long, String> texCoordinates;
	
	/**
	 * textImages contains an imageURI(String) and the corresponding Image object which is a texture
	 * for the corresponding surface-geometry. 
	 */
	protected HashMap<String, Image> texImages;
	
	/**
	 * In the case that something unexpected happens during the atlas generation, the error code(Integer)
	 * for corresponding surface-geometry, which recognized by its id(Long), will be reported.
	 */
	private HashMap<Long, ErrorTypes> LOG;

	/**
	 * @return the texImageURIs
	 */
	public HashMap<Long, String> getTexImageURIs() {
		return texImageURIs;
	}

	/**
	 * @param texImageURIs the texImageURIs to set
	 */
	public void setTexImageURIs(HashMap<Long, String> texImageURIs) {
		this.texImageURIs = texImageURIs;
	}

	/**
	 * @return the texCoordinates
	 */
	public HashMap<Long, String> getTexCoordinates() {
		return texCoordinates;
	}

	/**
	 * @param texCoordinates the texCoordinates to set
	 */
	public void setTexCoordinates(HashMap<Long, String> texCoordinates) {
		this.texCoordinates = texCoordinates;
	}

	/**
	 * @return the texImages
	 */
	public HashMap<String, Image> getTexImages() {
		return texImages;
	}

	/**
	 * @param texImages the texImages to set
	 */
	public void setTexImages(HashMap<String, Image> texImages) {
		this.texImages = texImages;
	}

	/**
	 * @return the lOG
	 */
	public HashMap<Long, ErrorTypes> getLOG() {
		return LOG;
	}

	/**
	 * @param lOG the lOG to set
	 */
	public void setLOG(HashMap<Long, ErrorTypes> lOG) {
		LOG = lOG;
	}
	

}
