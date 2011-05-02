package org.citygml.textureAtlasAPI.packer.sort;

import java.util.Comparator;

import org.citygml.textureAtlasAPI.packer.AbstractRect;

public class StartHeightComparator implements Comparator<AbstractRect> {

	@Override
	public int compare(AbstractRect o1, AbstractRect o2) {
		if (o1.getYPos()>o2.getYPos())
			return 1;
		if (o1.getYPos()<o2.getYPos())
			return -1;
		return 0;
	}

}
