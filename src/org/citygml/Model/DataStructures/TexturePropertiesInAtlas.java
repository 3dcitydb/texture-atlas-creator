package org.citygml.Model.DataStructures;

/**
 * This is a simple data structure to save a texture's properties -mostly dimensions-
 * in the texture atlas. In addition, the ImagePath is a key of this object. 
 * @author babak naderi
 *
 */
public class TexturePropertiesInAtlas {

	private double woffset;
	private double hoffset;
	private double width;
	private double height;
	private String ImagePath;
	private String atlasPath;
	
	/**
	 * @return the atlasPath
	 */
	public String getAtlasPath() {
		return atlasPath;
	}
	/**
	 * @param atlasPath the atlasPath to set
	 */
	public void setAtlasPath(String atlasPath) {
		this.atlasPath = atlasPath;
	}
	/**
	 * @return the imagePath
	 */
	public String getImagePath() {
		return ImagePath;
	}
	/**
	 * @param imagePath the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		ImagePath = imagePath;
	}
	/**
	 * @return the woffset
	 */
	public double getHorizontalOffset() {
		return woffset;
	}
	/**
	 * @param woffset the woffset to set
	 */
	public void setHorizontalOffset(double woffset) {
		this.woffset = woffset;
	}
	/**
	 * @return the hoffset
	 */
	public double getVerticalOffeset() {
		return hoffset;
	}
	/**
	 * @param hoffset the hoffset to set
	 */
	public void setVerticalOffset(double hoffset) {
		this.hoffset = hoffset;
	}
	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}
	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}
	
	public String toString(){

		return ImagePath+"|"+woffset+"|"+hoffset+"|"+width+"|"+height;
	}
	
	public void release(){
		ImagePath=null;
		atlasPath=null;
	}
}















