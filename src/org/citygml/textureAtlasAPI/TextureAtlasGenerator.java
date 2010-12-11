package org.citygml.textureAtlasAPI;

import java.util.HashMap;

import org.citygml.textureAtlasAPI.dataStructure.ErrorTypes;
import org.citygml.textureAtlasAPI.dataStructure.TexImageInfo;
import org.citygml.textureAtlasAPI.dataStructure.TexImageInfo4GMLFile;
import org.citygml.textureAtlasAPI.imageIO.ImageLoader;

/**
 * Todo:
 * 2. remove every image.. ( if it works good).
 * @author babak naderi
 *
 */
public class TextureAtlasGenerator {
	/**
	 * different packing algorithm
	 */
	public static  final int FFDH = 0;
	public static  final int NFDH = 1;
	public static  final int SLEA = 2;
	/**public static  final int BOLE = 3;
	public static  final int STBG = 4;**/
	//Touching Perimeter+ improved
	public static  final int TPIM = 5;
	public static  final int TPIM_WITHOUT_ROTATION = 6;
	
	
	private int PackingAlgorithm;
	
	private int ImageMaxWidth=2048;
	private int ImageMaxHeight=2048;
	
	private Modifier modifier;
	private ImageLoader imageLoader;
	

	public TextureAtlasGenerator() {
		PackingAlgorithm= FFDH;
		ImageMaxWidth=2048;
		ImageMaxHeight=2048;
		modifier = new Modifier(PackingAlgorithm, ImageMaxWidth, ImageMaxHeight);
		imageLoader= new ImageLoader();
	}
	
	public TextureAtlasGenerator(int PackingAlg, int atlasMaxWidth, int atlasMaxHeight) {
		setGeneralProp(PackingAlg, atlasMaxWidth, atlasMaxHeight);
		modifier = new Modifier(PackingAlgorithm, ImageMaxWidth, ImageMaxHeight);
		imageLoader= new ImageLoader();
	}
	
	private void setGeneralProp(int PackingAlg, int atlasMaxWidth, int atlasMaxHeight){
		this.PackingAlgorithm = PackingAlg;
		this.ImageMaxHeight=atlasMaxHeight;
		this.ImageMaxWidth= atlasMaxWidth;
	}
	
	public int getPackingAlgorithm() {
		return PackingAlgorithm;
	}

	public void setPackingAlgorithm(int packingAlgorithm) {
		this.PackingAlgorithm = packingAlgorithm;
	}

	public int getImageMaxWidth() {
		return ImageMaxWidth;
	}

	public void setImageMaxWidth(int imageMaxWidth) {
		this.ImageMaxWidth = imageMaxWidth;
	}

	public int getImageMaxHeight() {
		return ImageMaxHeight;
	}

	public void setImageMaxHeight(int imageMaxHeight) {
		this.ImageMaxHeight = imageMaxHeight;
	}

	public TexImageInfo convert(TexImageInfo tii){	
		return convert(tii,PackingAlgorithm);
	}
	
	public TexImageInfo convert(TexImageInfo tii, int PackingAlgorithm){	
		modifier.reset();
		if( tii instanceof TexImageInfo4GMLFile){
			if( !((TexImageInfo4GMLFile)tii).isImageLoaded()){
				tii.setTexImages(imageLoader.loadAllImage(((TexImageInfo4GMLFile)tii).getImagesLocalPath()));
				
			}
		}
		this.PackingAlgorithm=PackingAlgorithm;
		// check tii.isImagesReady()
		modifier.setGeneralSettings(this.PackingAlgorithm, this.ImageMaxWidth, this.ImageMaxHeight);
		return modifier.run(tii);
	}
	
	public HashMap<Object, ErrorTypes> getLOG() {
		return modifier.getLOG();
	}
	
	public String getLOGInText(){
		return "";/**
		HashMap<Object, ErrorTypes> LOG= modifier.getLOG();
		StringBuffer sb = new StringBuffer();
		for(Object key: LOG.keySet()){
			sb.append("<");
			sb.append(key.toString());
			sb.append(": ");
			sb.append(LOG.get(key));
			sb.append(">\r\n");
		}
		LOG=null;
		return sb.toString();**/
	}
}
