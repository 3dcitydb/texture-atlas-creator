/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2021
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.lrg.tum.de/gis/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * Virtual City Systems, Berlin <https://vc.systems/>
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
package org.citydb.textureAtlas.model;

import java.util.LinkedList;

public class TextureAtlas { 
	private LinkedList<AtlasRegion> regions;
	private int bindingBoxHeight;
	private int bindingBoxWidth;
	private boolean hasFourChannels;
    
    public TextureAtlas(){
    	regions = new LinkedList<AtlasRegion>();
    }
    
    public boolean hasFourChannels() {
		return hasFourChannels;
	}

	public void setFourChannels(boolean hasFourChannels) {
		this.hasFourChannels = hasFourChannels;
	}      

    public void setBindingBox(int w, int h) {
    	bindingBoxHeight = h;
    	bindingBoxWidth = w;
    }
    
    public void setBindingBoxWidth(int w) {
    	bindingBoxWidth = w;
    }
    
    public void setBindingBoxHeight(int h) {
    	bindingBoxHeight = h;
    }

    public int getBindingBoxHeight() {
    	return bindingBoxHeight;
    }
    
    public int getBindingBoxWidth() {
    	return bindingBoxWidth;
    }

    public void addRegion(AtlasRegion region) {
    	regions.add(region);
    }
    
    public void setRegions(LinkedList<AtlasRegion> regions) {
    	this.regions = regions;
    }
    
    public LinkedList<AtlasRegion> getRegions() {
    	return regions;
    }
    
}
