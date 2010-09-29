package org.citygml.TextureAtlasAPI.DataStructure;



import java.util.HashMap;


/**
 * TODO Read data from ImagesLocalPath and fill the texImages hashmap. 
 * @author babak naderi
 *
 */
public class TexImageInfo4GMLFile extends TexImageInfo {
	
	HashMap<String, String> texParIdentifier;
	HashMap<String,String> ImagesLocalPath;
	public HashMap<String, String> getImagesLocalPath() {
		return ImagesLocalPath;
	}

	TexGeneralProperties generalProp; 
	
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
		setImagesReady(false);
	}
	
	public void addTexParIdentifier(String Target_Ring,String parameterizedTextureID){
		if (texParIdentifier ==null)
			texParIdentifier = new HashMap<String, String>();
		texParIdentifier.put(Target_Ring, parameterizedTextureID);
	}
	
	/**
	 * @return the texParIdentifier
	 */
	public HashMap<String, String> getTexParIdentifier() {
		return texParIdentifier;
	}

	/**
	 * @param texParIdentifier the texParIdentifier to set
	 */
	public void setTexParIdentifier(HashMap<String, String> texParIdentifier) {
		this.texParIdentifier = texParIdentifier;
	}
	
	public void addAll(String parmTextID,String Target,String ring, String imageURI, String coordinates){
		String tr= getTargetRing(Target, ring);
		addTexImageURI(tr,imageURI);
		addTexCoordinates(tr,coordinates);
		addTexParIdentifier(tr,parmTextID);
		tr=null;
	}
	
	public static String getTargetRing(String Target, String Ring){
		return Target+' '+Ring;
	}
	
}
