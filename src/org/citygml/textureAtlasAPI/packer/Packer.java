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
package org.citygml.textureAtlasAPI.packer;


import java.util.LinkedList;

import org.citygml.textureAtlasAPI.TextureAtlasGenerator;
import org.citygml.textureAtlasAPI.data.AtlasRegion;
import org.citygml.textureAtlasAPI.data.TextureAtlas;

/**
 * It is the responsible class for packing the textures. Each texture will be represented as 
 * a Rect instance. Based on selected packing algorithm, and bin size, it will pack the textures 
 * in an atlas. Each atlas may have different levels.  
 */
public class Packer {
	private LinkedList<AtlasRegion> regions;
	private int binWidth;
	private int binHeight;
	private boolean fourChannel;
	private int algorithm;

	public Packer(int binWidth, int binHeight, int algorithm, boolean fourChannel) {
		regions = new LinkedList<AtlasRegion>();
		this.algorithm = algorithm;
		this.fourChannel = fourChannel;
		this.binWidth = binWidth;
		this.binHeight = binHeight;
	}

	public boolean isFourChannel() {
		return fourChannel;
	}

	public void setFourChannel(boolean fourChannel) {
		this.fourChannel = fourChannel;
	}

	public void setBinSize(int width, int height) {
		this.binWidth = width;
		this.binHeight = height;
	}

	public boolean addRegion(String uri, int width, int  height) {
		return regions.add(new AtlasRegion(uri, width, height));
	}

	public boolean addRegion(AtlasRegion region) {
		return regions.add(region);
	}

	public boolean removeRegion(String uri) {
		return regions.remove(new AtlasRegion(uri, 0, 0));
	}

	public int getRegions() {
		return regions != null ? regions.size() : 0;
	}

	public TextureAtlas pack(boolean usePOT) {
		TextureAtlas atlas = null;

		switch(algorithm) {
		case TextureAtlasGenerator.TPIM:
		case TextureAtlasGenerator.TPIM_WO_ROTATION:
			TouchingPerimeter tpPacker = new TouchingPerimeter(binWidth, binHeight, usePOT);
			tpPacker.setUseRotation(algorithm == TextureAtlasGenerator.TPIM);
			atlas = tpPacker.insert(regions);
			break;
		default:
			// Type of algorithm is not correctly set.
			return null;
		}

		atlas.setFourChannels(fourChannel);
		return atlas;
	}

}
