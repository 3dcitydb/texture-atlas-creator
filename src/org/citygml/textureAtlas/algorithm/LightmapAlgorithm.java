package org.citygml.textureAtlas.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.citygml.textureAtlas.model.AtlasRegion;
import org.citygml.textureAtlas.model.TextureAtlas;

public class LightmapAlgorithm implements PackingAlgorithm {
	private int atlasWidth;
	private int atlasHeight;
	private boolean useRotation;

	public LightmapAlgorithm(int atlasWidth, int atlasHeight, boolean useRotation) {
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		this.useRotation = useRotation;
	}

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

	private class Node {
		private Node[] childs;
		private AtlasRegion region;
		private short level;

		private Node(int x, int y, int width, int height) {
			childs = new Node[2];
			region = new AtlasRegion(null, x, y, width, height);
		}

		private boolean isLeaf() {
			return childs[0] == null && childs[1] == null;
		}

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
