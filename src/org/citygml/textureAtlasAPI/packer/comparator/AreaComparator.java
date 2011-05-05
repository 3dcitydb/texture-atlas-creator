package org.citygml.textureAtlasAPI.packer.comparator;

import java.util.Comparator;

import org.citygml.textureAtlasAPI.packer.Rect;

public class AreaComparator implements Comparator<Rect> {

	@Override
	public int compare(Rect arg0, Rect arg1) {
		return arg0.area > arg1.area ? -1 : arg0.area == arg1.area ? 0 : 1;
	}

}
