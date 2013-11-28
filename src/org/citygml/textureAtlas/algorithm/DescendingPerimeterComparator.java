package org.citygml.textureAtlas.algorithm;

import java.util.Comparator;

import org.citygml.textureAtlas.model.AtlasRegion;

public class DescendingPerimeterComparator implements Comparator<AtlasRegion> {
	@Override
	public int compare(AtlasRegion o1, AtlasRegion o2) {
		return o1.width + o1.height > o2.width + o2.height ? -1 : o1.width + o1.height == o2.width + o2.height ? 0 : 1;
	}
}
