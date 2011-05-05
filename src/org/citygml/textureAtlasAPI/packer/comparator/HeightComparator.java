package org.citygml.textureAtlasAPI.packer.comparator;

import java.util.Comparator;

import org.citygml.textureAtlasAPI.packer.Rect;

public class HeightComparator implements Comparator<Rect> {

	@Override
	public int compare(Rect o1, Rect o2) {
		return o1.height < o2.height ? -1 : o1.height == o2.height ? 0 : 1;
	}

}
