package org.citygml.Model.TexturePackers;

//import java.io.File;
import java.util.Hashtable;
//import java.util.Iterator;

//import org.citygml.Model.DataStructures.SimpleSurfaceDataMember;
import org.citygml.Model.GMLModifier;
import org.citygml.Model.DataStructures.TexturePropertiesInAtlas;

public abstract class AbstractTexturePacker {
	
	
	private String imageList=null;
	private String atlasPath=null;
	private String prefixAddress=null;
	
	private int atlasFormat;



	/**
	 * show type of texture packer algorithm.
	 */
	private int texturePackerType;
	public final static int NVIDIA=1;
	public final static int TEXTUREPACKER_1=2;
	
	/**
	 * 
	 * @param imageList: image list in a complete path format and with ';' as a delimiter. 
	 * 			 
	 * @param atlasPath : where will be the position of atlas image.
	 * @param prefixAddress : the prefix address of image files. It should removed in the result Hashtable.
	 * @param atlasFormat: type of atlas format and image converting
	 */
	public void set(String imageList, String atlasPath, String prefixAddress, int atlasFormat){
		this.imageList = imageList;
		this.atlasPath = atlasPath;
		this.prefixAddress= prefixAddress;
		this.atlasFormat =atlasFormat;
	}
	
	public abstract Hashtable<String, TexturePropertiesInAtlas> run();
		
	
	public abstract void reset();
	
	public int getPackerType(){
		return texturePackerType;
	}

	/**
	 * @param texturePackerType the texturePackerType to set
	 */
	public void setTexturePackerType(int texturePackerType) {
		this.texturePackerType = texturePackerType;
	}
	

	/**
	 * @return the imageList
	 */
	public String getImageList() {
		return imageList;
	}
	/**
	 * @return the atlasPath
	 */
	public String getAtlasPath() {
		return atlasPath;
	}
	/**
	 * @return the prefixAddress
	 */
	public String getPrefixAddress() {
		return prefixAddress;
	}
	/**
	 * @return the atlasFormat
	 */
	public int getAtlasFormat() {
		return atlasFormat;
	}
	
	public String getAtlasFormat(boolean alphaEnable){
		if (alphaEnable)
			return "PNG";
		switch(atlasFormat){
		case GMLModifier.JPG:
			return "jpg";
		case GMLModifier.PNG:
			return "PNG";
		case GMLModifier.AUTO:
			return "jpg";
		}
		return "jpg";
	}
}
