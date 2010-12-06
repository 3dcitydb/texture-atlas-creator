package org.citygml.textureAtlasAPI.dataStructure;



import java.util.HashMap;


/**
 * TODO Read data from ImagesLocalPath and fill the texImages hashmap. 
 * @author babak naderi
 *
 */
public class TexImageInfo4GMLFile extends TexImageInfo {
	
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
