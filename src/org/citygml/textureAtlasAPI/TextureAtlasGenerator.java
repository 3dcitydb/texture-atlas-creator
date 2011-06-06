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
		if (tii instanceof TexImageInfo && tii!=null)
			imageLoader.setImageLoader(tii.getTexImages());
			
		this.PackingAlgorithm=PackingAlgorithm;
		// check tii.isImagesReady()
		modifier.setGeneralSettings(this.PackingAlgorithm, this.ImageMaxWidth, this.ImageMaxHeight);
		return modifier.run(tii);
	}
	
	public HashMap<Object, ErrorTypes> getLOG() {
		return modifier.getLOG();
	}
	
	public String getLOGInText(){
		
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
		if (sb.length()==0)
			return null;
		return sb.toString();
	}
}
