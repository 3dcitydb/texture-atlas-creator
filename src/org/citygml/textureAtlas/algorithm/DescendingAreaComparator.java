package org.citygml.textureAtlas.algorithm;

import java.util.Comparator;

import org.citygml.textureAtlas.model.AtlasRegion;

public class DescendingAreaComparator implements Comparator<AtlasRegion> {
	@Override
	public int compare(AtlasRegion o1, AtlasRegion o2) {
		return o1.area > o2.area ? -1 : o1.area == o2.area ? 0 : 1;
	}
}
