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
package org.citydb.textureAtlas.packer;


import java.util.LinkedList;

import org.citydb.textureAtlas.TextureAtlasCreator;
import org.citydb.textureAtlas.algorithm.LightmapAlgorithm;
import org.citydb.textureAtlas.algorithm.PackingAlgorithm;
import org.citydb.textureAtlas.algorithm.TouchingPerimeterAlgorithm;
import org.citydb.textureAtlas.model.AtlasRegion;
import org.citydb.textureAtlas.model.TextureAtlas;

public class Packer {
	private final LinkedList<AtlasRegion> regions;
	private int binWidth;
	private int binHeight;
	private final boolean fourChannel;
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
		PackingAlgorithm packingAlgorithm = null;

		switch(algorithm) {
		case TextureAtlasCreator.BASIC:
			packingAlgorithm = new LightmapAlgorithm(binWidth, binHeight, true);
			break;
		case TextureAtlasCreator.TPIM:
		case TextureAtlasCreator.TPIM_WO_ROTATION:
			packingAlgorithm = new TouchingPerimeterAlgorithm(binWidth, binHeight, usePOT, algorithm == TextureAtlasCreator.TPIM);
			break;
		default:
			// Type of algorithm is not correctly set.
			return null;
		}

		atlas = packingAlgorithm.createTextureAtlas(regions);
		atlas.setFourChannels(fourChannel);
		
		return atlas;
	}

}
