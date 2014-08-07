package org.citydb.textureAtlas.algorithm;

import java.util.Comparator;

import org.citydb.textureAtlas.model.AtlasRegion;

public class MultiComparator implements Comparator<AtlasRegion> {
	private Comparator<AtlasRegion>[] comparators;
	
	@SuppressWarnings("unchecked")
	protected MultiComparator(Comparator<AtlasRegion>... comparators) {
		this.comparators = comparators;
	}
	
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