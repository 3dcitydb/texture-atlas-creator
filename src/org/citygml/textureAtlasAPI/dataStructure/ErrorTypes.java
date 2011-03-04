package org.citygml.textureAtlasAPI.dataStructure;

public enum ErrorTypes {
	IMAGE_FORMAT_NOT_SUPPORTED("IMAGE_FORMAT_NOT_SUPPORTED"),
	// Because of other target,it is impossible to add the target parametrizedTexture to the other.  
	TARGET_PT_NOT_SUPPORTED("TARGET_PT_NOT_SUPPORTED"),
	ERROR_IN_COORDINATES("Wrapping coordinates"),
	IMAGE_IS_NOT_AVAILABLE("Image file/path is not valid"),
	IMAGE_UNBONDED_SIZE("IMAGE_UNBONDED_SIZE");
	String name;
	ErrorTypes(String name){
		this.name=name;
	}
	public String toString(){
		return name;
	}
	}
