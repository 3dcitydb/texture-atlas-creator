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
package org.citygml.textureAtlasAPI.dataStructure;


import org.citygml.textureAtlasAPI.imageIO.ImageLoader;
import org.citygml4j.model.citygml.appearance.TextureType;
import org.citygml4j.model.citygml.appearance.WrapMode;
import org.citygml4j.model.citygml.appearance.ColorPlusOpacity;

/**
 * Each ParameterizedTexture has some properties. In the case of combining several textures,
 * they should have same properties. An instance of this class presents properties of its
 * corresponding texture. In addition it is possible to compare two instance of this class.
 * @author babak
 *
 */

public class TexGeneralProperties {
	private TextureType textureType;
	private WrapMode wrapModeType;
	private ColorPlusOpacity boarderColor;
	private boolean isFront;
	private String appearanceTheme=null;
	private String MIMEType=null;
	private boolean supportedImageFormat;
	private String buildingID=null;
	
	public TexGeneralProperties(){
		set(null,null,null,false,null,null,null,null);
	}
	public TexGeneralProperties(TextureType textureType, WrapMode wrapMode,
			ColorPlusOpacity boarderColor, boolean isFront, String appearanceTheme, String MIMEType,String buildingID,String extension) {
		set(textureType,wrapMode,boarderColor,isFront,appearanceTheme,MIMEType,buildingID,extension);
	}

	public String getBuildingID() {
		return buildingID;
	}
	public void set(TextureType textureType, WrapMode wrapMode,
			ColorPlusOpacity boarderColor, boolean isFront, String appearanceTheme,String MIMEType,String buildingID,String extension) {
		this.textureType = textureType;
		this.wrapModeType = wrapMode;
		this.boarderColor = boarderColor;
		this.isFront = isFront;
		this.appearanceTheme=appearanceTheme;
		this.MIMEType = MIMEType;
		this.supportedImageFormat=ImageLoader.isSupportedImageFormat(MIMEType,extension);
		this.buildingID=buildingID;
	}
	
	public TextureType getTextureType() {
		return textureType;
	}

	public WrapMode getWrapModeType() {
		return wrapModeType;
	}

	public ColorPlusOpacity getBoarderColor() {
		return boarderColor;
	}

	public boolean isFront() {
		return isFront;
	}
	public String getAppearanceTheme(){
		return this.appearanceTheme;
	}
	
	public String getMIMEType(){
		return this.MIMEType;
	}

	public void setMIMEType(String MIMEType){
		this.MIMEType=MIMEType;
	}
	
	public boolean isSupportedImageFormat(){
		return this.supportedImageFormat;
	}

	public boolean compareItTo(TextureType textureType, WrapMode wrapMode,
			ColorPlusOpacity boarderColor, boolean isFront,String appearanceTheme, String MIMEType,String buildingID,String extension) {		
		return Compare(this, new TexGeneralProperties(textureType, wrapMode, boarderColor, isFront, appearanceTheme, MIMEType, buildingID,extension));

	}
	
	
	public boolean compareItTo(TexGeneralProperties t1) {
		return Compare(this, t1);

	}

	public static boolean Compare(TexGeneralProperties t1,TexGeneralProperties t2){
		boolean result =true;
		// parent buildingid
		if (t1.getBuildingID()!=null)
			result= result && t1.getBuildingID().equalsIgnoreCase(t2.getBuildingID());
		else
			result= result &&t1.getBuildingID()==null;

		// isFront
		result= result && t1.isFront()==t2.isFront;
		
		// texture type
		if (t1.getTextureType()!=null)
			result = result && t1.getTextureType().compareTo(t2.getTextureType())==0;
		else
			result= result && t2.getTextureType()==null ;

		if (t1.getWrapModeType()!=null)
			result = result &&t1.getWrapModeType().compareTo(t2.getWrapModeType())==0 ;
		else
			result= result && t2.getWrapModeType()==null;
		
		if (t1.getAppearanceTheme()!=null)
			result = result && t1.getAppearanceTheme().equalsIgnoreCase(t2.getAppearanceTheme());
		else
			result= result && t2.getAppearanceTheme()==null;
		
		result= result &&compareColorPlusOpacity(t1.getBoarderColor(),t2.getBoarderColor());
		
		if (t1.getMIMEType()!=null)
			result = result && (t1.isSupportedImageFormat()?(t2.isSupportedImageFormat()?true:false):t1.getMIMEType().equalsIgnoreCase(t2.getMIMEType()));
		else
			result= result && t2.getMIMEType()==null ;
		
		return result;
				
		
	}
	
	private static boolean compareColorPlusOpacity(ColorPlusOpacity c1,ColorPlusOpacity c2){
		if (c1==null)
			return c2==null;
		if (c1.getBlue().equals(c2.getBlue())&&
				c1.getGreen().equals(c2.getGreen())&&
				c1.getOpacity().equals(c2.getOpacity())&&
				c1.getRed().equals(c2.getRed()))
			return true;
		return false;
	}
	public void clear(){
		textureType=null;
		wrapModeType=null;
		boarderColor=null;	
		MIMEType=null;
		appearanceTheme=null;
		buildingID=null;
	}
}
