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
import org.citygml.textureAtlasAPI.imageIO.ImageLoader;

/**
 * It is a starting point for using Textureatlas API.
 * After setting properties, call the convert(TexImageInfo,...) method.
 * It will return modified TexImageInfo. Check getLOG() or getLOGInText
 * to see the message related to current conversion.
 * 
 * Note that all the textures in TexImageInfo should be potentially combinable.
 * 
 * In the case of standalone tool, TexImageInfo4GMLFile should be used instead of TexImageInfo.
 * In this case images will be loaded in this API. 
 * 
 * To see how should input data structured please see TexImageInfo or TexImageInfo4GMLFile according 
 * to type of your usage.
 * 
 * User can also set the packing algorithm which should be used and maximum size of atlas. 
 */
public class TextureAtlasGenerator {
	/**
	 * Different packing algorithm (more info in readme file)
	 * FFDH: First-Fit Decreasing Height
	 * NFDH: Next-Fit Decreasing Height
	 * SLEA: Sleator's algorithm
	 * TPIM: Improved version of Touching Perimeter algorithm.
	 * TPIM_WITHOUT_ROTATION: TPIM algorithm without rotating textures.
	 */
	public static final int FFDH = 0;
	public static final int NFDH = 1;
	public static final int SLEA = 2;

	
	//Touching Perimeter+ improved
	public static final int TPIM = 5;
	public static final int TPIM_WITHOUT_ROTATION = 6;
	
	
	private int packingAlgorithm;
	
	private int imageMaxWidth=2048;
	private int imageMaxHeight=2048;
	
	private Modifier modifier;
	private ImageLoader imageLoader;
	private boolean usePOTS=false;
	
	private double scaleFactor;

	public TextureAtlasGenerator() {
		packingAlgorithm= FFDH;
		imageMaxWidth=2048;
		imageMaxHeight=2048;
		scaleFactor=1;
		modifier = new Modifier(packingAlgorithm, imageMaxWidth, imageMaxHeight, usePOTS);
		imageLoader= new ImageLoader();
	}
	
	public TextureAtlasGenerator(int PackingAlg, int atlasMaxWidth, int atlasMaxHeight) {
		usePOTS = false;
		setGeneralProp(PackingAlg, atlasMaxWidth, atlasMaxHeight, usePOTS);
		modifier = new Modifier(packingAlgorithm, imageMaxWidth, imageMaxHeight, usePOTS);
		imageLoader= new ImageLoader();
	}
	
	//-----------------
	public TextureAtlasGenerator(int PackingAlg, int atlasMaxWidth, int atlasMaxHeight, boolean usePOTS) {
		setGeneralProp(PackingAlg, atlasMaxWidth, atlasMaxHeight,usePOTS);
		modifier = new Modifier(this.packingAlgorithm, this.imageMaxWidth, 
				this.imageMaxHeight,this.usePOTS);
		imageLoader= new ImageLoader();
	}
	
	private void setGeneralProp(int PackingAlg, int atlasMaxWidth, int atlasMaxHeight, boolean usePOTS){
		this.scaleFactor=1;
		this.usePOTS = usePOTS;
		this.packingAlgorithm = PackingAlg;
		this.imageMaxHeight = atlasMaxHeight;
		this.imageMaxWidth= atlasMaxWidth;
	}
	public double getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	//-----------------
	public int getPackingAlgorithm() {
		return packingAlgorithm;
	}

	public void setPackingAlgorithm(int packingAlgorithm) {
		this.packingAlgorithm = packingAlgorithm;
	}

	public int getImageMaxWidth() {
		return imageMaxWidth;
	}

	public void setImageMaxWidth(int imageMaxWidth) {
		this.imageMaxWidth = imageMaxWidth;
	}

	public int getImageMaxHeight() {
		return imageMaxHeight;
	}

	public void setImageMaxHeight(int imageMaxHeight) {
		this.imageMaxHeight = imageMaxHeight;
	}

	public TexImageInfo convert(TexImageInfo tii){	
		return convert(tii,packingAlgorithm);
	}
	
	public TexImageInfo convert(TexImageInfo tii, int packingAlgorithm){	
		modifier.reset();
		
		if (tii!=null)
			imageLoader.setImageLoader(tii.getTexImages());
			
		this.packingAlgorithm=packingAlgorithm;
		// check tii.isImagesReady()
		modifier.setGeneralSettings(this.packingAlgorithm, this.imageMaxWidth, this.imageMaxHeight, this.usePOTS, this.scaleFactor);
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

	public void setUsePOTS(boolean usePOTS) {
		this.usePOTS = usePOTS;
	}

	public boolean isUsePOTS() {
		return usePOTS;
	}


}
