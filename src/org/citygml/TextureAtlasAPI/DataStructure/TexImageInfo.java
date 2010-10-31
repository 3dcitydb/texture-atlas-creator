package org.citygml.TextureAtlasAPI.DataStructure;

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
	 * texImgeURIs contains  a key (the surface-geometry id (Long) in DB usage and TargetURI+' '+Ring(String) in GML loader)
	 * and corresponding imageURI(String)
	 */
	protected HashMap<Object, String> texImageURIs ;
	
	/**
	 * texCoordingates contains a key (the surface-geometry id (Long) in DB usage and TargetURI+' '+Ring(String) in GML loader)
	 *  and corresponding texture coordinates in the same format as in the database (string tokenizable via whitespace)  
	 */
	protected HashMap<Object, String> texCoordinates;
	
	/**
	 * textImages contains an imageURI(String) and the corresponding Image object which is a texture
	 * for the corresponding surface-geometry. 
	 */
	protected HashMap<String, TextureImage> texImages;
	
	/**
	 * In the case that something unexpected happens during the atlas generation, the error code(Integer)
	 * for corresponding surface-geometry, which recognized by its id(Long), will be reported.
	 */
	protected HashMap<Object, ErrorTypes> LOG;
	
	/**
	 * check if the images are ready.
	 * IN DB mode: whenever all the images are converted to Java Image object it will be true.
	 * In StandAlone: whenever all images are loaded in a java Image format, it will be true.
	 */
	private boolean ImagesReady=false;

	public boolean isImagesReady() {
		return ImagesReady;
	}

	public void setImagesReady(boolean imagesReady) {
		ImagesReady = imagesReady;
	}

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

	/**
	 * @return the lOG
	 */
	public HashMap<Object, ErrorTypes> getLOG() {
		return LOG;
	}

	/**
	 * @param lOG the lOG to set
	 */
	public void setLOG(HashMap<Object, ErrorTypes> lOG) {
		LOG = lOG;
	}
	
	public void clear(){
		texCoordinates.clear();
		texImages.clear();
		texImageURIs.clear();
		LOG.clear();
		texCoordinates=null;
		texImages=null;
		texImageURIs=null;
		LOG=null;
	}
	
	public String getLOGInText(){
		StringBuffer sb = new StringBuffer();
		for(Object key: LOG.keySet()){
			sb.append("<");
			sb.append(key.toString());
			sb.append(": ");
			sb.append(LOG.get(key));
			sb.append(">\r\n");
		}
		return sb.toString();
	}

}
