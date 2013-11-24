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
package org.citygml.textureAtlasAPI.data;

/**
 * For packing algorithms each texture will be represented as an object of this class.
 * As a result of packing process these variables should be set: x,y, level, and rotated.
 * Because of optimization SET/GET methods are neglected and variables defined in public domain.
 *
 */
public class AtlasRegion {
	// must be equal to the name of the texture image
	public String texImageName;
	public int width;
	public int height;
	public int area; 
	public int x;
	public int y;
	public boolean isRotated;
	public int score = Integer.MIN_VALUE;
	public short level;

	public AtlasRegion(String texImageName, int width, int height) {
		this.texImageName = texImageName;
		this.width = width;
		this.height = height;
		this.area = width*height;
	}

	public AtlasRegion(AtlasRegion r) {
		this.x = r.x;
		this.y = r.y;
		this.texImageName = r.texImageName;
		this.width = r.width;
		this.height = r.height;
		this.score = r.score;
		this.isRotated = r.isRotated;
	}

	public void rotate() {
		int tmp = width;
		width = height;
		height = tmp;
		isRotated = !isRotated;
	}

	public void setPosition(int x, int y, short level){
		this.x = x;
		this.y = y;
		this.level = level;
	}

	public String getTexImageName(){
		return texImageName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AtlasRegion) {
			if (((AtlasRegion)obj).getTexImageName() != null && ((AtlasRegion)obj).getTexImageName().equalsIgnoreCase(this.texImageName))
				return true;
		}
		
		return false;
	}

	public void clear() {
		texImageName = null;
	}

}
