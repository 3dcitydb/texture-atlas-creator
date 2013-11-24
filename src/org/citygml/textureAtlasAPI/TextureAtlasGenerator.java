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

import org.citygml.textureAtlasAPI.data.TextureImagesInfo;
import org.citygml.textureAtlasAPI.packer.Modifier;

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
    public static final int TPIM = 1;
    public static final int TPIM_WO_ROTATION = 2;
	
	private int packingAlgorithm;
	private int atlasMaxWidth;
	private int atlasMaxHeight;
	
	private boolean usePOTS = false;
	private double scaleFactor = 1;
	
	public TextureAtlasGenerator() {
		this(TPIM, 1024, 1024);
	}
	
	public TextureAtlasGenerator(int packingAlgorithm, int atlasMaxWidth, int atlasMaxHeight) {
		this(packingAlgorithm, atlasMaxWidth, atlasMaxHeight, false);
	}
	  
	public TextureAtlasGenerator(int packingAlgorithm, int atlasMaxWidth, int atlasMaxHeight, boolean usePOTS) {
		this.packingAlgorithm = packingAlgorithm;
		this.atlasMaxHeight = atlasMaxHeight;
		this.atlasMaxWidth= atlasMaxWidth;
		this.usePOTS = usePOTS;
		scaleFactor = 1;				
	}
	
	public double getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public int getPackingAlgorithm() {
		return packingAlgorithm;
	}

	public void setPackingAlgorithm(int packingAlgorithm) {
		this.packingAlgorithm = packingAlgorithm;
	}

	public int getAtlasMaxWidth() {
		return atlasMaxWidth;
	}

	public void setAtlasMaxWidth(int atlasMaxWidth) {
		this.atlasMaxWidth = atlasMaxWidth;
	}

	public int getAtlasMaxHeight() {
		return atlasMaxHeight;
	}

	public void setAtlasMaxHeight(int atlasMaxHeight) {
		this.atlasMaxHeight = atlasMaxHeight;
	}
	
	public void setUsePOTS(boolean usePOTS) {
		this.usePOTS = usePOTS;
	}

	public boolean isUsePOTS() {
		return usePOTS;
	}

	public void convert(TextureImagesInfo tii) {	
		convert(tii, packingAlgorithm);
	}
	
	public void convert(TextureImagesInfo tii, int packingAlgorithm){	
		this.packingAlgorithm = packingAlgorithm;
		Modifier modifier = new Modifier(packingAlgorithm, atlasMaxWidth, atlasMaxHeight, usePOTS, scaleFactor);
		
		 modifier.run(tii);
	}
}
