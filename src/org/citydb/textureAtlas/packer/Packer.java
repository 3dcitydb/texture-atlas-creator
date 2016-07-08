/*
 * 3D City Database Texture Atlas Creator
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2016
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.gis.bgu.tum.de/
 * 
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 * 
 * virtualcitySYSTEMS GmbH, Berlin <http://www.virtualcitysystems.de/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
