package org.citygml.textureAtlas.algorithm;

import java.util.LinkedList;

import org.citygml.textureAtlas.model.AtlasRegion;
import org.citygml.textureAtlas.model.TextureAtlas;

public interface PackingAlgorithm {
	public TextureAtlas createTextureAtlas(LinkedList<AtlasRegion> regions);
}
