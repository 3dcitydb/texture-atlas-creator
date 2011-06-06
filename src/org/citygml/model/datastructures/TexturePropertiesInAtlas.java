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















