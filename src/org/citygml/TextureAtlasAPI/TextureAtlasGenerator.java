package org.citygml.TextureAtlasAPI;

import org.citygml.TextureAtlasAPI.DataStructure.TexImageInfo;
import org.citygml.TextureAtlasAPI.StripPacker.MyStPacker;

public class TextureAtlasGenerator {
	
	private int PackingAlgorithm;
	
	private int ImageMaxWidth=2048;
	private int ImageMaxHeight=2048;
	
	private Modifier modifier;
	public TextureAtlasGenerator() {
		PackingAlgorithm= MyStPacker.FFDH;
		ImageMaxWidth=2048;
		ImageMaxHeight=2048;
		modifier = new Modifier(PackingAlgorithm, ImageMaxWidth, ImageMaxHeight);
	}
	
	public void setGeneralProp(int PackingAlg, int atlasMaxWidth, int atlasMaxHeight){
		this.PackingAlgorithm = PackingAlg;
		this.ImageMaxHeight=atlasMaxHeight;
		this.ImageMaxWidth= atlasMaxWidth;
	}
	
	
	public TexImageInfo convertor(TexImageInfo tii){
		return convertor(tii,PackingAlgorithm);
	}
	
	public TexImageInfo convertor(TexImageInfo tii, int PackingAlgorithm){
		modifier.reset();
		modifier.setGeneralSettings(this.PackingAlgorithm, this.ImageMaxWidth, this.ImageMaxHeight);
		return modifier.run(tii);
	}
	

}
