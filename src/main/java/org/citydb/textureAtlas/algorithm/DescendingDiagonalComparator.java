/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2024
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
package org.citydb.textureAtlas.algorithm;

import org.citydb.textureAtlas.model.AtlasRegion;

import java.util.Comparator;

// TODO: Auto-generated Javadoc

/**
 * The Class DescendingDiagonalComparator.
 */
public class DescendingDiagonalComparator implements Comparator<AtlasRegion> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(AtlasRegion o1, AtlasRegion o2) {
        int diagonal1 = (o1.width * o1.width) + (o1.height * o1.height);
        int diagonal2 = (o2.width * o2.width) + (o2.height * o2.height);

        return diagonal1 > diagonal2 ? -1 : diagonal1 == diagonal2 ? 0 : 1;
    }
}
