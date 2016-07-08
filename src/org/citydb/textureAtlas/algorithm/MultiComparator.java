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
package org.citydb.textureAtlas.algorithm;

import java.util.Comparator;

import org.citydb.textureAtlas.model.AtlasRegion;

// TODO: Auto-generated Javadoc
/**
 * The Class MultiComparator.
 */
public class MultiComparator implements Comparator<AtlasRegion> {
	
	/** The comparators. */
	private Comparator<AtlasRegion>[] comparators;
	
	/**
	 * Instantiates a new multi comparator.
	 *
	 * @param comparators the comparators
	 */
	@SuppressWarnings("unchecked")
	protected MultiComparator(Comparator<AtlasRegion>... comparators) {
		this.comparators = comparators;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(AtlasRegion o1, AtlasRegion o2) {
		for (Comparator<AtlasRegion> comparator : comparators) {
            int result = comparator.compare(o1, o2);
            if (result != 0) 
            	return result;
        }
		
        return 0;
	}

}
