/*
 * 3D City Database Texture Atlas Creator
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2016
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.gis.bgu.tum.de/
 * 
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 * 
 * virtualcitySYSTEMS GmbH, Berlin <http://www.virtualcitysystems.de/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.citydb.textureAtlas.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.citydb.textureAtlas.model.AtlasRegion;
import org.citydb.textureAtlas.model.TextureAtlas;

// TODO: Auto-generated Javadoc
/**
 * The Class LightmapAlgorithm.
 */
public class LightmapAlgorithm implements PackingAlgorithm {
	
	/** The atlas width. */
	private int atlasWidth;
	
	/** The atlas height. */
	private int atlasHeight;
	
	/** The use rotation. */
	private boolean useRotation;

	/**
	 * Instantiates a new lightmap algorithm.
	 *
	 * @param atlasWidth the atlas width
	 * @param atlasHeight the atlas height
	 * @param useRotation the use rotation
	 */
	public LightmapAlgorithm(int atlasWidth, int atlasHeight, boolean useRotation) {
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		this.useRotation = useRotation;
	}

	/* (non-Javadoc)
	 * @see org.citydb.textureAtlas.algorithm.PackingAlgorithm#createTextureAtlas(java.util.LinkedList)
	 */
	@Override
	public TextureAtlas createTextureAtlas(LinkedList<AtlasRegion> regions) {
		TextureAtlas atlas = new TextureAtlas();
		atlas.setBindingBox(atlasWidth, atlasHeight);

		// sort regions
		Collections.sort(regions, new DescendingDiagonalComparator());

		List<Node> roots = new ArrayList<Node>();
		roots.add(new Node(0, 0, atlasWidth, atlasHeight));

		for (AtlasRegion region : regions) {
			boolean added = false;

			for (Node root : roots) {
				if (root.insert(region) != null) {
					added = true;
					break;
				}
			}

			if (!added) {
				Node root = new Node(0, 0, atlasWidth, atlasHeight);
				root.insert(region);
				roots.add(root);
			}
		}

		// fill texture atlas from root nodes
		for (int i = 0; i < roots.size(); i++)
			fillAtlas(atlas, roots.get(i), i);

		return atlas;
	}

	/**
	 * Fill atlas.
	 *
	 * @param atlas the atlas
	 * @param node the node
	 * @param level the level
	 */
	private void fillAtlas(TextureAtlas atlas, Node node, int level) {
		if (node == null)
			return;

		if (node.region.texImageName != null) {
			node.region.level = level;
			atlas.addRegion(node.region);
		}

		fillAtlas(atlas, node.childs[0], level);
		fillAtlas(atlas, node.childs[1], level);
	}

	/**
	 * The Class Node.
	 */
	private class Node {
		
		/** The childs. */
		private Node[] childs;
		
		/** The region. */
		private AtlasRegion region;
		
		/** The level. */
		private short level;

		/**
		 * Instantiates a new node.
		 *
		 * @param x the x
		 * @param y the y
		 * @param width the width
		 * @param height the height
		 */
		private Node(int x, int y, int width, int height) {
			childs = new Node[2];
			region = new AtlasRegion(null, x, y, width, height);
		}

		/**
		 * Checks if is leaf.
		 *
		 * @return true, if is leaf
		 */
		private boolean isLeaf() {
			return childs[0] == null && childs[1] == null;
		}

		/**
		 * Insert.
		 *
		 * @param candidate the candidate
		 * @return the node
		 */
		private Node insert(AtlasRegion candidate) {
			if (!isLeaf()) {
				Node node = childs[0].insert(candidate);
				if (node != null)
					return node;

				return childs[1].insert(candidate);
			}

			else {
				if (region.texImageName != null)
					return null;

				if (!fitsInRegion(candidate))
					return null;

				if (candidate.width == region.width && candidate.height == region.height) {
					candidate.x = region.x;
					candidate.y = region.y;
					candidate.level = level;
					region = candidate;
					return this;
				}

				int dw = region.width - candidate.width;
				int dh = region.height - candidate.height;

				if (dw > dh) {
					childs[0] = new Node(region.x, region.y, candidate.width, region.height);
					childs[1] = new Node(region.x + candidate.width, region.y, region.width - candidate.width, region.height);
				} else {
					childs[0] = new Node(region.x, region.y, region.width, candidate.height);
					childs[1] = new Node(region.x, region.y + candidate.height, region.width, region.height - candidate.height);
				}

				return childs[0].insert(candidate);
			}
		}

		/**
		 * Fits in region.
		 *
		 * @param candidate the candidate
		 * @return true, if successful
		 */
		private boolean fitsInRegion(AtlasRegion candidate) {
			boolean fits = candidate.width <= region.width && candidate.height <= region.height;
			if (!fits && useRotation) {
				fits = candidate.height <= region.width && candidate.width <= region.height;
				if (fits)
					candidate.rotate();
			}

			return fits;
		}

	}

}
