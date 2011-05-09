package org.citygml.textureAtlasAPI.packer.comparator;

import java.util.Comparator;

import org.citygml.textureAtlasAPI.packer.Rect;

public class StartHeightComparator implements Comparator<Rect> {

	@Override
	public int compare(Rect o1, Rect o2) {
		if (o1.y>o2.y)
			return 1;
		if (o1.y<o2.y)
			return -1;
		return 0;
	}

}
