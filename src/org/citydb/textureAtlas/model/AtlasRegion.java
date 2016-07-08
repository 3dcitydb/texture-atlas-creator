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
package org.citydb.textureAtlas.model;

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
	public int level;

	public AtlasRegion(String texImageName, int width, int height) {
		this.texImageName = texImageName;
		this.width = width;
		this.height = height;
		this.area = width * height;
	}
	
	public AtlasRegion(String texImageName, int x, int y, int width, int height) {
		this(texImageName, width, height);
		this.x = x;
		this.y = y;
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

	public void setPosition(int x, int y, short level) {
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
