package org.citygml.TextureAtlasAPI.DataStructure;


import org.citygml.TextureAtlasAPI.ImageIO.ImageLoader;
import org.citygml4j.model.citygml.appearance.TextureType;
import org.citygml4j.model.citygml.appearance.WrapMode;
import org.citygml4j.model.citygml.appearance.ColorPlusOpacity;


public class TexGeneralProperties {
	private TextureType textureType;
	private WrapMode wrapModeType;
	private ColorPlusOpacity boarderColor;
	private boolean isFront;
	private String appearanceID=null;
	private String MIMEType=null;
	private boolean supportedImageFormat;

	public TexGeneralProperties(){
		set(null,null,null,false,null,null);
	}
	public TexGeneralProperties(TextureType textureType, WrapMode wrapMode,
			ColorPlusOpacity boarderColor, boolean isFront, String appearanceID, String MIMEType) {
		set(textureType,wrapMode,boarderColor,isFront,appearanceID,MIMEType);
	}

	public void set(TextureType textureType, WrapMode wrapMode,
			ColorPlusOpacity boarderColor, boolean isFront, String appearanceID,String MIMEType) {
		this.textureType = textureType;
		this.wrapModeType = wrapMode;
		this.boarderColor = boarderColor;
		this.isFront = isFront;
		this.appearanceID=appearanceID;
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
	public String getAppearanceID(){
		return this.appearanceID;
	}
	
	public String getMIMEType(){
		return this.MIMEType;
	}
	
	public boolean isSupportedImageFormat(){
		return this.supportedImageFormat;
	}

	public boolean compareItTo(TextureType textureType, WrapMode wrapMode,
			ColorPlusOpacity boarderColor, boolean isFront,String appearanceID, String MIMEType) {
		if(this.isFront==isFront&&
				this.textureType.compareTo(textureType)==0&&
				this.wrapModeType.compareTo(wrapMode)==0&&
				compareColorPlusOpacity(this.boarderColor,boarderColor)&&
				(appearanceID!=null?this.appearanceID.equalsIgnoreCase(appearanceID):appearanceID==null)&&
				this.MIMEType!=null && MIMEType!=null&&
				(this.isSupportedImageFormat()?(ImageLoader.isSupportedImageFormat(MIMEType)?true:false):this.MIMEType.equalsIgnoreCase(MIMEType)))
			return true;
			
		return false;
	}
	

	public boolean compareItTo(TexGeneralProperties t1) {
		return Compare(this, t1);
//		if(this.isFront==t1.isFront()&&
//				this.textureType.compareTo(t1.getTextureType())==0&&
//				this.wrapModeType.compareTo(t1.getWrapModeType())==0&&
//				compareColorPlusOpacity(this.boarderColor,t1.getBoarderColor())&&
//				(appearanceID!=null?this.appearanceID.equalsIgnoreCase(t1.getAppearanceID()):t1.getAppearanceID()==null)&&
//				this.MIMEType!=null && t1.getMIMEType()!=null&&
//				(this.isSupportedImageFormat()?(t1.isSupportedImageFormat()?true:false):this.MIMEType.equalsIgnoreCase(t1.getMIMEType())))
//			return true;
//			
//		return false;
	}

	public static boolean Compare(TexGeneralProperties t1,TexGeneralProperties t2){
		if(t1.isFront()==t2.isFront&&
				t1.getTextureType().compareTo(t2.getTextureType())==0&&
				t1.getWrapModeType().compareTo(t2.getWrapModeType())==0&&
				compareColorPlusOpacity(t1.getBoarderColor(),t2.getBoarderColor())&&
				(t1.getAppearanceID()!=null?t1.getAppearanceID().equalsIgnoreCase(t2.getAppearanceID()):t2.getAppearanceID()==null)&&
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
		appearanceID=null;
	}
}
