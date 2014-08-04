package org.citydb.textureAtlas.algorithm;

import java.util.Comparator;

import org.citydb.textureAtlas.model.AtlasRegion;

public class DescendingDiagonalComparator implements Comparator<AtlasRegion> {
	@Override
	public int compare(AtlasRegion o1, AtlasRegion o2) {
		int diagonal1 = (o1.width * o1.width) + (o1.height * o1.height);
		int diagonal2 = (o2.width * o2.width) + (o2.height * o2.height);
		
		return diagonal1 > diagonal2 ? -1 : diagonal1 == diagonal2 ? 0 : 1;
	}
}
