package org.citygml.textureAtlas.algorithm;

import java.util.Comparator;

import org.citygml.textureAtlas.model.AtlasRegion;

public class AscendingLengthComparator implements Comparator<AtlasRegion> {
	@Override
	public int compare(AtlasRegion o1, AtlasRegion o2) {
		return o1.level < o2.level ? -1 : o1.level == o2.level ? 0 : 1;
	}
}
