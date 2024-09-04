/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2021
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.lrg.tum.de/gis/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * Virtual City Systems, Berlin <https://vc.systems/>
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

/**
 * This algorithm is based on source code developed and released to the public by Jukka Jylänki.
 * This class is responsible for packing in TPIM and TPIM_WO_R mode.
 * TPIM:
 * TPIM is customized version of Touching Perimeter algorithm
 * as a heuristic two-dimensional bin packing with support of rotation.
 * During tests 83.35% of result atlas was occupied. This algorithm is
 * based on source code developed and released to the public by Jukka Jylänki.
 * <p>
 * TPIM starts by sorting the items according to nonincreasing area and
 * their normal orientation. It initializes a bin with maximume acceptable
 * size and packs one item at a time. In the case that it is not possible
 * to add a new item to the current bin, a new bin will be initialized. The first
 * item packed in a bin is always placed in the bottom left corner. However
 * in the result atlas the origin will be in the top left corner. Each item is
 * packed in a way that its bottom and left edges touching either the the bin
 * or the edge of another item.
 * <p>
 * Each potential position for the new item will be scored as the amount
 * of its touching edges. Touching the bin edges is more valuable to avoid
 * inhomogeneous shape of bin. For each candidate (position) the score will be
 * calculated twice (normal orientation and 90 degree rotated) and the highest
 * value will be taken.
 * <p>
 * For more information about Touching Perimeter algorithm please refer to:
 * Lodi A, Martello S, Vigo D. Heuristic and Metaheuristic Approaches
 * for a Class of Two-Dimensional Bin Packing Problems. INFORMS J on
 * Computing 1999;11:345-357.
 * <p>
 * TPIM_WITHOUT_ROTATION:
 * It is an extension of TPIM algorithm which does not rotate textures.
 */

import org.citydb.textureAtlas.model.AtlasRegion;
import org.citydb.textureAtlas.model.TextureAtlas;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

// TODO: Auto-generated Javadoc

/**
 * The Class TouchingPerimeterAlgorithm.
 */
public class TouchingPerimeterAlgorithm implements PackingAlgorithm {

    /** The bin width. */
    private int binWidth;

    /** The bin height. */
    private int binHeight;

    /** The use rotation. */
    private boolean useRotation = false;

    /** The used rectangles. */
    private LinkedList<AtlasRegion> usedRectangles = new LinkedList<AtlasRegion>();

    /** The free rectangles. */
    private LinkedList<AtlasRegion> freeRectangles = new LinkedList<AtlasRegion>();

    /** The use POT dimension. */
    private boolean usePOTDimension = false;

    /** The max W. */
    private int maxH = 0, maxW = 0;

    /**
     * Instantiates a new touching perimeter algorithm.
     *
     * @param width the width
     * @param height the height
     * @param usePOTDimension the use POT dimension
     * @param useRotation the use rotation
     */
    public TouchingPerimeterAlgorithm(int width, int height, boolean usePOTDimension, boolean useRotation) {
        binWidth = width;
        binHeight = height;
        this.usePOTDimension = usePOTDimension;
        this.useRotation = useRotation;

        reset();
    }

    /**
     * Reset.
     */
    public void reset() {
        AtlasRegion freeRegion = new AtlasRegion("", binWidth, binHeight);
        freeRegion.x = 0;
        freeRegion.y = 0;

        usedRectangles.clear();
        freeRectangles.clear();
        freeRectangles.add(freeRegion);
        maxH = 0;
        maxW = 0;
    }

    /* (non-Javadoc)
     * @see org.citydb.textureAtlas.algorithm.PackingAlgorithm#createTextureAtlas(java.util.LinkedList)
     */
    @Override
    public TextureAtlas createTextureAtlas(LinkedList<AtlasRegion> regions) {
        // sort regions by area in descending order
        Collections.sort(regions, new DescendingAreaComparator());

        TextureAtlas atlas = new TextureAtlas();
        int level = 0;

        AtlasRegion region = null;
        int bestScore, bestRegionIndex;

        // Between all remaining Rects...
        while (regions.size() > 0) {
            bestScore = Integer.MIN_VALUE;
            bestRegionIndex = -1;

            // find the one that fits as best as possible (max score) to the current configuration.
            int i = 0;
            Iterator<AtlasRegion> iter = regions.iterator();
            while (iter.hasNext()) {
                region = iter.next();

                // may be in previous round it was rotated.
                if (region.isRotated)
                    region.rotate();

                // compute score of rect.
                scoreRegion(region);
                if (region.score > bestScore) {
                    bestScore = region.score;
                    bestRegionIndex = i;
                }

                i++;
            }

            // check whether it is finished or not.
            if (bestRegionIndex == -1) {
                if (regions.size() > 0) {
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
            putRegion(region);

            if (region.x + region.width > maxW)
                maxW = region.x + region.width;
            if (region.y + region.height > maxH)
                maxH = region.y + region.height;

            region.level = level;
            // add it to the atlas and remove it from the list.
            atlas.addRegion(region);
            regions.remove(bestRegionIndex);
        }

        atlas.setBindingBox(atlas.getBindingBoxWidth() + maxW, atlas.getBindingBoxHeight() + maxH);
        return atlas;
    }

    /**
     * rec.x and rec.y are set previously.
     * calculate new free areas which are made by adding this rectangle.
     *
     * @param region the region
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

    /**
     * Score region.
     *
     * @param region the region
     */
    private void scoreRegion(AtlasRegion region) {
        if (!findBestFreePosition(region))
            region.score = Integer.MIN_VALUE;
    }

    /**
     * Gets the min covered POT.
     *
     * @param len the len
     * @return the min covered POT
     */
    private int getMinCoveredPOT(int len) {
        int pot = (int) Math.floor(Math.log10(len) / Math.log10(2));
        if (Math.pow(2, pot) == len)
            return pot - 1;

        return pot;
    }

    /**
     * score based amount of contact. input parameters are candidate position.
     *
     * @param x the x
     * @param y the y
     * @param width the width
     * @param height the height
     * @return the int
     */
    private int scorePlace(int x, int y, int width, int height) {
        int score = 0;
        int tmps;
        double trick = 1.5;
        double beInPOT = 0.2;

        tmps = binWidth;

        if (usePOTDimension && (x + width < maxW || getMinCoveredPOT(x) == getMinCoveredPOT(x + width)))
            score += beInPOT * tmps;
        if (usePOTDimension && (y + height < maxH || getMinCoveredPOT(y) == getMinCoveredPOT(y + height)))
            score += beInPOT * tmps;
        if (usePOTDimension && score == 0 && maxW + maxH > 0)
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

    /**
     * Adjacent length.
     *
     * @param p1start the p 1 start
     * @param p1end the p 1 end
     * @param p2start the p 2 start
     * @param p2end the p 2 end
     * @return the int
     */
    private int adjacentLength(int p1start, int p1end, int p2start, int p2end) {
        if (p1end < p2start || p2end < p1start)
            return 0;

        return Math.min(p1end, p2end) - Math.max(p1start, p2start);
    }

    /**
     * Try to find best position for the input Rect from all free positions in freeRectangles.
     * put the best score in Rect.score1
     *
     * @param rect the rect
     * @return true, if successful
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
                    score = scorePlace(potentialPlace.x, potentialPlace.y, rect.height, rect.width);
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
     *
     * @param freeRect the free rect
     * @param newRect the new rect
     * @return true, if is affected
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
                AtlasRegion newNode = new AtlasRegion(freeRect);
                newNode.height = newRect.y - newNode.y;
                freeRectangles.add(newNode);
            }

            // new rectangle is in the bottom side of existing free rectangle.
            if (newRect.y + newRect.height < freeRect.y + freeRect.height) {
                AtlasRegion newNode = new AtlasRegion(freeRect);
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
                AtlasRegion newNode = new AtlasRegion(freeRect);
                newNode.width = newRect.x - newNode.x;
                freeRectangles.add(newNode);
            }

            // New node at the right side of the used node.
            if (newRect.x + newRect.width < freeRect.x + freeRect.width) {
                AtlasRegion newNode = new AtlasRegion(freeRect);
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

                if (BcontainsA(freeRectangles.get(j), freeRectangles.get(i))) {
                    freeRectangles.remove(j);
                    j--;
                }

                j++;
            }

            i++;
        }
    }

    /**
     * Bcontains A.
     *
     * @param a the a
     * @param b the b
     * @return true, if successful
     */
    private boolean BcontainsA(AtlasRegion a, AtlasRegion b) {
        return a.x >= b.x && a.y >= b.y
                && a.x + a.width <= b.x + b.width
                && a.y + a.height <= b.y + b.height;
    }
}
