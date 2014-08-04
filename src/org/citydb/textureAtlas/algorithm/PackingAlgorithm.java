package org.citydb.textureAtlas.algorithm;

import java.util.LinkedList;

import org.citydb.textureAtlas.model.AtlasRegion;
import org.citydb.textureAtlas.model.TextureAtlas;

public interface PackingAlgorithm {
	public TextureAtlas createTextureAtlas(LinkedList<AtlasRegion> regions);
}
