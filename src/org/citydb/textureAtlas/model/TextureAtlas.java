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
