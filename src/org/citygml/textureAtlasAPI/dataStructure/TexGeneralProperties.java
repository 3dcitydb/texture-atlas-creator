package org.citygml.textureAtlasAPI.dataStructure;


import org.citygml.textureAtlasAPI.imageIO.ImageLoader;
import org.citygml4j.model.citygml.appearance.TextureType;
import org.citygml4j.model.citygml.appearance.WrapMode;
import org.citygml4j.model.citygml.appearance.ColorPlusOpacity;


public class TexGeneralProperties {
	private TextureType textureType;
	private WrapMode wrapModeType;
	private ColorPlusOpacity boarderColor;
	private boolean isFront;
	private String appearanceTheme=null;
	private String MIMEType=null;
	private boolean supportedImageFormat;

	public TexGeneralProperties(){
		set(null,null,null,false,null,null);
	}
	public TexGeneralProperties(TextureType textureType, WrapMode wrapMode,
			ColorPlusOpacity boarderColor, boolean isFront, String appearanceTheme, String MIMEType) {
		set(textureType,wrapMode,boarderColor,isFront,appearanceTheme,MIMEType);
	}

	public void set(TextureType textureType, WrapMode wrapMode,
			ColorPlusOpacity boarderColor, boolean isFront, String appearanceTheme,String MIMEType) {
		this.textureType = textureType;
		this.wrapModeType = wrapMode;
		this.boarderColor = boarderColor;
		this.isFront = isFront;
		this.appearanceTheme=appearanceTheme;
		this.MIMEType = MIMEType;
		this.supportedImageFormat=ImageLoader.isSupportedImageFormat(MIMEType);
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
			ColorPlusOpacity boarderColor, boolean isFront,String appearanceTheme, String MIMEType) {
		if(this.isFront==isFront&&
				this.textureType.compareTo(textureType)==0&&
				this.wrapModeType.compareTo(wrapMode)==0&&
				compareColorPlusOpacity(this.boarderColor,boarderColor)&&
				(appearanceTheme!=null?this.appearanceTheme.equalsIgnoreCase(appearanceTheme):appearanceTheme==null)&&
				this.MIMEType!=null && MIMEType!=null&&
				(this.isSupportedImageFormat()?(ImageLoader.isSupportedImageFormat(MIMEType)?true:false):this.MIMEType.equalsIgnoreCase(MIMEType)))
			return true;
			
		return false;
	}
	
	
	public boolean compareItTo(TexGeneralProperties t1) {
		return Compare(this, t1);

	}

	public static boolean Compare(TexGeneralProperties t1,TexGeneralProperties t2){
		if(t1.isFront()==t2.isFront&&
				t1.getTextureType().compareTo(t2.getTextureType())==0&&
				t1.getWrapModeType().compareTo(t2.getWrapModeType())==0&&
				compareColorPlusOpacity(t1.getBoarderColor(),t2.getBoarderColor())&&
				(t1.getAppearanceTheme()!=null?t1.getAppearanceTheme().equalsIgnoreCase(t2.getAppearanceTheme()):t2.getAppearanceTheme()==null)&&
				t1.getMIMEType()!=null&& t2.getMIMEType()!=null&&
				(t1.isSupportedImageFormat()?(t2.isSupportedImageFormat()?true:false):t1.getMIMEType().equalsIgnoreCase(t2.getMIMEType())))
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
	public void clear(){
		textureType=null;
		wrapModeType=null;
		boarderColor=null;	
		MIMEType=null;
		appearanceTheme=null;
	}
}
