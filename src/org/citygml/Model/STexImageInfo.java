package org.citygml.Model;


import java.awt.Image;
import java.util.HashMap;

import org.citygml.TextureAtlasAPI.DataStructure.TexImageInfo;
import org.citygml4j.model.citygml.appearance.ParameterizedTexture;

public class STexImageInfo extends TexImageInfo {
	/**
	 * String is a id of SurfaceDataMemeber. 
	 */
	private HashMap<String, ParameterizedTexture> ParamTextures;

	/**
	 * @return the paramTextures
	 */
	public HashMap<String, ParameterizedTexture> getParamTextures() {
		return ParamTextures;
	}

	/**
	 * @param paramTextures the paramTextures to set
	 */
	public void setParamTextures(HashMap<String, ParameterizedTexture> paramTextures) {
		ParamTextures = paramTextures;
	}
	
	public void addParametrizedTexture(String surfaceDataMameberID, ParameterizedTexture parametrizedTexture){
		if (ParamTextures ==null)
			ParamTextures = new HashMap<String, ParameterizedTexture>();
		ParamTextures.put(surfaceDataMameberID, parametrizedTexture);
	}
	
	public void addTexImageURI(Long surfaceID,String URI){
		if (texImageURIs ==null)
			texImageURIs = new HashMap<Long, String>();
		texImageURIs.put(surfaceID, URI);
		
	}
	
	public void addTexCoordinates(Long surfaceID, String formatedcoordinates){
		if (texCoordinates ==null)
			texCoordinates = new HashMap<Long, String>();
		texCoordinates.put(surfaceID, formatedcoordinates);
	}
	
	public void addTexImages(String URI, Image image){
		if (texImages ==null)
			texImages = new HashMap<String, Image>();
		texImages.put(URI, image);
	}
}
