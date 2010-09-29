package org.citygml.TextureAtlasAPI.DataStructure;

import org.citygml4j.model.citygml.appearance.TextureType;
import org.citygml4j.model.citygml.appearance.WrapMode;
import org.citygml4j.model.citygml.appearance.ColorPlusOpacity;

;
public class TexGeneralProperties {
	private TextureType textureType;
	private WrapMode wrapModeType;
	private ColorPlusOpacity boarderColor;
	private boolean isFront;

	public TexGeneralProperties(){
		set(null,null,null,false);
	}
	public TexGeneralProperties(TextureType textureType, WrapMode wrapMode,
			ColorPlusOpacity boarderColor, boolean isFront) {
		this.textureType = textureType;
		this.wrapModeType = wrapMode;
		this.boarderColor = boarderColor;
		this.isFront = isFront;
	}

	public void set(TextureType textureType, WrapMode wrapMode,
			ColorPlusOpacity boarderColor, boolean isFront) {
		this.textureType = textureType;
		this.wrapModeType = wrapMode;
		this.boarderColor = boarderColor;
		this.isFront = isFront;
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

	public boolean compareItTo(TextureType textureType, WrapMode wrapMode,
			ColorPlusOpacity boarderColor, boolean isFront) {
		if(this.isFront==isFront&&
				this.textureType.compareTo(textureType)==0&&
				this.wrapModeType.compareTo(wrapMode)==0&&
				compareColorPlusOpacity(this.boarderColor,boarderColor))
			return true;
			
		return false;
	}
	

	public static boolean Compare(TexGeneralProperties t1,TexGeneralProperties t2){
		if(t1.isFront()==t2.isFront&&
				t1.getTextureType().compareTo(t2.getTextureType())==0&&
				t1.getWrapModeType().compareTo(t2.getWrapModeType())==0&&
				compareColorPlusOpacity(t1.getBoarderColor(),t2.getBoarderColor()))
			return true;
		
		return false;
	}
	
	private static boolean compareColorPlusOpacity(ColorPlusOpacity c1,ColorPlusOpacity c2){
		if (c1.getBlue().equals(c2.getBlue())&&
				c1.getGreen().equals(c2.getGreen())&&
				c1.getOpacity().equals(c2.getOpacity())&&
				c1.getRed().equals(c2.getRed()))
			return true;
		return false;
	}
}
