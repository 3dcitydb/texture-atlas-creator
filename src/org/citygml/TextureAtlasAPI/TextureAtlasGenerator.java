package org.citygml.TextureAtlasAPI;

import org.citygml.TextureAtlasAPI.DataStructure.TexImageInfo;
import org.citygml.TextureAtlasAPI.DataStructure.TexImageInfo4GMLFile;
import org.citygml.TextureAtlasAPI.ImageIO.ImageLoader;

import org.citygml.TextureAtlasAPI.StripPacker.MyStPacker;

public class TextureAtlasGenerator {
	
	private int PackingAlgorithm;
	
	private int ImageMaxWidth=2048;
	private int ImageMaxHeight=2048;
	
	private Modifier modifier;
	private ImageLoader imageLoader;
	public TextureAtlasGenerator() {
		PackingAlgorithm= MyStPacker.FFDH;
		ImageMaxWidth=2048;
		ImageMaxHeight=2048;
		modifier = new Modifier(PackingAlgorithm, ImageMaxWidth, ImageMaxHeight);
		imageLoader= new ImageLoader();
	}
	
	public void setGeneralProp(int PackingAlg, int atlasMaxWidth, int atlasMaxHeight){
		this.PackingAlgorithm = PackingAlg;
		this.ImageMaxHeight=atlasMaxHeight;
		this.ImageMaxWidth= atlasMaxWidth;
	}
	
	
	public TexImageInfo convert(TexImageInfo tii, boolean loadImage){
		return convert(tii,PackingAlgorithm,loadImage);
	}
	
	public TexImageInfo convert(TexImageInfo tii, int PackingAlgorithm,boolean loadImage){
		modifier.reset();
		if (loadImage){
			if(!tii.isImagesReady()&& tii instanceof TexImageInfo4GMLFile){
				tii.setTexImages(imageLoader.loadAllImage(((TexImageInfo4GMLFile)tii).getImagesLocalPath()));
				tii.setImagesReady(true);
			}
		}
		// check tii.isImagesReady()
		modifier.setGeneralSettings(this.PackingAlgorithm, this.ImageMaxWidth, this.ImageMaxHeight);
		return modifier.run(tii);
	}

	
//	public TexImageInfo4GMLFile convertor4GMLF(TexImageInfo4GMLFile tii){
//		return convertor4GMLF(tii,PackingAlgorithm);
//	}
//	
//	public TexImageInfo4GMLFile convertor4GMLF(TexImageInfo4GMLFile tii, int PackingAlgorithm){
//		modifier.reset();
//		
//		modifier.setGeneralSettings(this.PackingAlgorithm, this.ImageMaxWidth, this.ImageMaxHeight);
//		TexImageInfo tmp =modifier.run(tii);
//		tii.setLOG(tmp.getLOG());
//		tii.setTexCoordinates(tmp.getTexCoordinates());
//		tii.setTexImages(tmp.getTexImages());
//		tii.setTexImageURIs(tmp.getTexImageURIs());
//		return tii;
//	}


}
