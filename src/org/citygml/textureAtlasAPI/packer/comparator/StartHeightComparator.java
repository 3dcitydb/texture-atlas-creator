package org.citygml.textureAtlasAPI.packer.comparator;

import java.util.Comparator;

import org.citygml.textureAtlasAPI.packer.Rect;

public class StartHeightComparator implements Comparator<Rect> {

	@Override
	public int compare(Rect o1, Rect o2) {
		if (o1.getYPos()>o2.getYPos())
			return 1;
		if (o1.getYPos()<o2.getYPos())
			return -1;
		return 0;
	}

}
