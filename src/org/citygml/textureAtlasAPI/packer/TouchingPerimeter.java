/*******************************************************************************
 * This file is part of the Texture Atlas Generation Tool.
 * Copyright (c) 2010 - 2011
 * Institute for Geodesy and Geoinformation Science
 * Technische Universitaet Berlin, Germany
 * http://www.gis.tu-berlin.de/
 * 
 * The Texture Atlas Generation Tool is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * @author Babak Naderi <b.naderi@mailbox.tu-berlin.de>
 ******************************************************************************/
package org.citygml.textureAtlasAPI.packer;

/**
 * This algorithm is based on source code developed and released to the public by Jukka Jylänki.
 * This class is responsible for packing in TPIM and TPIM_WO_R mode. 
 * TPIM:
		TPIM is customized version of Touching Perimeter algorithm 
		as a heuristic two-dimensional bin packing with support of rotation. 
		During tests 83.35% of result atlas was occupied. This algorithm is 
		based on source code developed and released to the public by Jukka Jylänki.

		TPIM starts by sorting the items according to nonincreasing area and 
		their normal orientation. It initializes a bin with maximume acceptable 
		size and packs one item at a time. In the case that it is not possible 
		to add a new item to the current bin, a new bin will be initialized. The first 
		item packed in a bin is always placed in the bottom left corner. However 
		in the result atlas the origin will be in the top left corner. Each item is 
		packed in a way that its bottom and left edges touching either the the bin 
		or the edge of another item.

		Each potential position for the new item will be scored as the amount 
		of its touching edges. Touching the bin edges is more valuable to avoid 
		inhomogeneous shape of bin. For each candidate (position) the score will be 
		calculated twice (normal orientation and 90 degree rotated) and the highest 
		value will be taken.

		For more information about Touching Perimeter algorithm please refer to:
		Lodi A, Martello S, Vigo D. Heuristic and Metaheuristic Approaches 
		for a Class of Two-Dimensional Bin Packing Problems. INFORMS J on 
		Computing 1999;11:345-357.

	TPIM_WITHOUT_ROTATION:
		It is an extension of TPIM algorithm which does not rotate textures.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.citygml.textureAtlasAPI.packer.comparator.DecreasingAreaComparator;

public class TouchingPerimeter {
	private int binWidth;
	private int binHeight;
	private boolean useRotation = false;	

	private List<AtlasRegion> usedRectangles = new ArrayList<AtlasRegion>();
	private List<AtlasRegion> freeRectangles = new ArrayList<AtlasRegion>();

	private boolean usePOTDimension = false;
	private int maxH = 0, maxW = 0;

	public TouchingPerimeter(int width, int height, boolean usePOTDimension) {
		binWidth = width;
		binHeight = height;
		this.usePOTDimension = usePOTDimension;

		reset();	
	}

	public void setUseRotation(boolean useRotation) {
		this.useRotation = useRotation;
	}

	public void reset(){
		AtlasRegion freeRegion = new AtlasRegion("", binWidth, binHeight);
		freeRegion.x = 0;
		freeRegion.y = 0;

		usedRectangles.clear();
		freeRectangles.clear();
		freeRectangles.add(freeRegion);
		maxH = 0; 
		maxW = 0;
	}

	/**
	 * Main method. At first size of bin should be set and in the case of  TPIM_WITHOUT_ROTATION the 
	 * setUseRotation(false) method should be called before this method.
	 * @param regions
	 * @return
	 */
	public TextureAtlas insert(List<AtlasRegion> regions) {		
		Collections.sort(regions, new DecreasingAreaComparator());
		TextureAtlas atlas = new TextureAtlas();
		short level = 0;

		AtlasRegion region = null;
		int bestScore, bestRegionIndex;

		// Between all remaining Rects...
		while (regions.size() > 0) {
			bestScore = Integer.MIN_VALUE;
			bestRegionIndex = -1;

			// find the one that fits as best as possible (max score) to the current configuration.
			for (int i = 0; i < regions.size(); i++) {
				region = regions.get(i);

				// may be in previous round it was rotated.
				if (region.isRotated)
					region.rotate();

				// compute score of rect.
				scoreRegion(region);
				if (region.score > bestScore) {
					bestScore = region.score;				
					bestRegionIndex = i;
				}
			}

			// check whether it is finished or not.
			if (bestRegionIndex == -1) {
				if (regions.size() > 0){
					// one atlas is complete but some other rects are remaining. add one level and reset.
					level++;
					atlas.setBindingBox(atlas.getBindingBoxWidth() + maxW, atlas.getBindingBoxHeight() + maxH);
					reset();
					continue;
				} else
					return atlas;
			}

			// put the best texture in its position.
			region = regions.get(bestRegionIndex);			
			putRegion((AtlasRegion)region);

			if (region.x + region.width > maxW)
				maxW = region.x + region.width;
			if (region.y + region.height > maxH)
				maxH = region.y + region.height;

			region.level= level;
			// add it to the atlas and remove it from the list.
			atlas.addRegion(region);
			regions.remove(bestRegionIndex);
		}

		atlas.setBindingBox(atlas.getBindingBoxWidth()+maxW,atlas.getBindingBoxHeight()+maxH );
		return atlas;
	}

	/**
	 * rec.x and rec.y are set previously. 
	 * calculate new free areas which are made by adding this rectangle.
	 * @param rec
	 */
	private void putRegion(AtlasRegion region) {	
		for (int i = 0; i < freeRectangles.size(); i++) {	
			if (isAffected(freeRectangles.get(i), region)) {
				freeRectangles.remove(i);
				i--;
			}
		}

		removeDuplicate();
		usedRectangles.add(region);
	}

	private void scoreRegion(AtlasRegion region) {
		if (!findBestFreePosition(region))
			region.score = Integer.MIN_VALUE;
	}

	private int getMinCoveredPOT(int len) {
		int pot = (int) Math.floor(Math.log10(len) / Math.log10(2));
		if (Math.pow(2, pot) == len)
			return pot - 1;

		return pot;
	}

	/**
	 * score based amount of contact. input parameters are candidate position.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private int scorePlace(int x, int y, int width, int height) {
		int score = 0;
		int tmps;
		double trick = 1.5;
		double beInPOT = 0.2;

		tmps = binWidth;

		if (usePOTDimension && ( x + width < maxW || getMinCoveredPOT(x) == getMinCoveredPOT(x + width) ))
			score += beInPOT * tmps;
		if (usePOTDimension && ( y + height < maxH || getMinCoveredPOT(y) == getMinCoveredPOT(y + height) ))
			score += beInPOT * tmps;
		if (usePOTDimension && score == 0 && maxW + maxH > 0 )
			score -= beInPOT * tmps;

		for (AtlasRegion occupied : usedRectangles) {
			if (occupied.x == x + width || occupied.x + occupied.width == x) {
				tmps = adjacentLength(occupied.y, occupied.y + occupied.height, y, y + height);
				score += occupied.x == 0 ? tmps * trick : tmps;
			}

			if (occupied.y == y + height || occupied.y + occupied.height == y) {
				tmps = adjacentLength(occupied.x, occupied.x + occupied.width, x, x + width);
				score += occupied.y == 0 ? tmps * trick : tmps;	
			}
		}

		return score;
	}

	private int adjacentLength(int p1start, int p1end, int p2start, int p2end) {
		if (p1end < p2start || p2end < p1start)
			return 0;

		return Math.min(p1end, p2end) - Math.max(p1start, p2start);
	}

	/**
	 * Try to find best position for the input Rect from all free positions in freeRectangles.
	 * put the best score in Rect.score1
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	private boolean findBestFreePosition(AtlasRegion rect) {
		AtlasRegion bestPosition = null;
		boolean doRotate = false;
		int bestContactScore = -1;
		int score;

		for (AtlasRegion potentialPlace : freeRectangles) {

			if (potentialPlace.width >= rect.width && potentialPlace.height >= rect.height) {
				score = scorePlace(potentialPlace.x, potentialPlace.y, rect.width, rect.height);
				if (score > bestContactScore) {
					doRotate = false;
					bestContactScore = score;
					bestPosition = potentialPlace;
				}
			}

			if (useRotation) {
				// 90d rotation
				if (potentialPlace.width >= rect.height && potentialPlace.height >= rect.width) {
					score = scorePlace(potentialPlace.x,potentialPlace.y, rect.height, rect.width);
					if (score > bestContactScore) {
						doRotate = true;
						bestContactScore = score;
						bestPosition = potentialPlace;
					}
				}
			}
		}

		if (bestContactScore != -1) {
			rect.score = bestContactScore;
			rect.x = bestPosition.x;
			rect.y = bestPosition.y;
			if (doRotate)
				rect.rotate();
			
			return true;
		} else
			return false;
	}
	
	/**
	 * check whether the freeRecr is affected by the new position of newRect.	
	 * @param freeRect
	 * @param newRect
	 * @return
	 */
	private boolean isAffected(AtlasRegion freeRect, AtlasRegion newRect) {
		// check whether they are intersecting
		if (newRect.x >= freeRect.x + freeRect.width
				|| newRect.x + newRect.width <= freeRect.x
				|| newRect.y >= freeRect.y + freeRect.height
				|| newRect.y + newRect.height <= freeRect.y)
			return false;
		
		// intersecting in x direction.
		if (newRect.x < freeRect.x + freeRect.width
				&& newRect.x + newRect.width > freeRect.x) {
			// new rectangle is in the top side of free existing rectangle.
			if (newRect.y > freeRect.y
					&& newRect.y < freeRect.y + freeRect.height) {
				AtlasRegion newNode = new AtlasRegion(freeRect) ;
				newNode.height = newRect.y - newNode.y;
				freeRectangles.add(newNode);
			}

			// new rectangle is in the bottom side of existing free rectangle.
			if (newRect.y + newRect.height < freeRect.y + freeRect.height) {
				AtlasRegion newNode = new AtlasRegion(freeRect) ;
				newNode.y = newRect.y + newRect.height;
				newNode.height = freeRect.y + freeRect.height - (newRect.y + newRect.height);
				freeRectangles.add(newNode);
			}
		}
		
		// intersecting in y direction
		if (newRect.y < freeRect.y + freeRect.height
				&& newRect.y + newRect.height > freeRect.y) {
			// New node at the left side of the used node.
			if (newRect.x > freeRect.x
					&& newRect.x < freeRect.x + freeRect.width) {
				AtlasRegion newNode = new AtlasRegion(freeRect) ;
				newNode.width = newRect.x - newNode.x;
				freeRectangles.add(newNode);
			}

			// New node at the right side of the used node.
			if (newRect.x + newRect.width < freeRect.x + freeRect.width) {
				AtlasRegion newNode = new AtlasRegion(freeRect) ;
				newNode.x = newRect.x + newRect.width;
				newNode.width = freeRect.x + freeRect.width - (newRect.x + newRect.width);
				freeRectangles.add(newNode);
			}
		}

		return true;
	}

	/**
	 * may be in intersection part, duplicated rectangles are added.
	 * remove them in here.
	 */
	private void removeDuplicate() {
		int i = 0, j = 0;
		while (i < freeRectangles.size()) {
			j = i + 1;
			while (j < freeRectangles.size()) {
				if (BcontainsA(freeRectangles.get(i), freeRectangles.get(j))) {
					freeRectangles.remove(i);
					i--;
					break;
				}
				
				if (BcontainsA(freeRectangles.get(j), freeRectangles.get(i))){
					freeRectangles.remove(j);
					j--;
				}
				
				j++;
			}
			
			i++;
		}
	}
	
	private boolean BcontainsA(AtlasRegion a, AtlasRegion b) {
		return a.x >= b.x && a.y >= b.y 
				&& a.x + a.width <= b.x + b.width 
				&& a.y + a.height <= b.y + b.height;
	}
}
