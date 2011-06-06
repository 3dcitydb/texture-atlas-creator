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
 * For implementation sample code of Jukka Jylänki is used, which published for public use.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.citygml.textureAtlasAPI.packer.comparator.AreaComparator;

public class TouchingPerimeterPacking {
	private int binWidth;
	private int binHeight;
	private boolean useRotation=false;
	

	private List<Rect> usedRectangles = new ArrayList<Rect>();
	private List<Rect> freeRectangles = new ArrayList<Rect>();
	
	private int maxH=0, maxW=0;
	TouchingPerimeterPacking() {
		binWidth = 0;
		binHeight = 0;
	}

	
	TouchingPerimeterPacking(int width, int height) {
		init(width, height);
	}

	public void init(int width, int height) {
		binWidth = width;
		binHeight = height;
		clear();
	}
		
	public void setUseRotation(boolean useRotation) {
		this.useRotation = useRotation;
	}
	
	public void clear(){
		Rect n = new Rect("",binWidth,binHeight);
		n.x = 0;
		n.y = 0;

		usedRectangles.clear();
		freeRectangles.clear();

		freeRectangles.add(n);
		maxH=0;maxW=0;
	}

	public Atlas insert(List<Rect> rects) {
		
		Collections.sort(rects,new AreaComparator());
		Atlas res= new Atlas();
		short level = 0;
		
		Rect rect=null;
		int bestScore1,bestRectIndex;

		while (rects.size() > 0) {
			bestScore1 = Integer.MIN_VALUE;
			bestRectIndex = -1;
			for (int i = 0; i < rects.size(); i++) {
				rect= rects.get(i);
				if (rect.rotated){
					// may be in previous round it was rotated.
					rect.rotate();
					rect.rotated=false;
				}
				// compute score of rect.
				scoreRect(rect);
				if (rect.score1 > bestScore1) {
					bestScore1 = rect.score1;				
					bestRectIndex = i;					
						
				}
			}
			// check whether it is finished or not.
			if (bestRectIndex == -1)
				if (rects.size()>0){
					// one atlas is complete but some other rects are remaining.
					level++;
					res.setBindingBox(res.getBindingBoxWidth()+maxW,res.getBindingBoxHeight()+maxH );
					clear();
					continue;
				}
				else
					return res;
			rect = rects.get(bestRectIndex);
			putRect((Rect)rect);
			if (rect.x+rect.width>maxW)
				maxW=rect.x+rect.width;
			if (rect.y+rect.height>maxH)
				maxH=rect.y+rect.height;
			rect.level= level;
			res.addRect(rect);
			rects.remove(bestRectIndex);
		}
		res.setBindingBox(res.getBindingBoxWidth()+maxW,res.getBindingBoxHeight()+maxH );
		return res;
	}
	/**
	 * rec.x and rec.y are set previously. 
	 * calculate new free areas which are made by adding this rectangle.
	 * @param rec
	 */
	private void putRect(Rect rect) {	
		for (int i=0;i<freeRectangles.size();i++){	
			if (isAffected(freeRectangles.get(i),rect)){
				freeRectangles.remove(i);
				i--;
			}
		}
		removeDuplicate();
		usedRectangles.add(rect);
	}

	private void scoreRect(Rect rect) {
		if (!findBestFreePosition(rect)){
			rect.score1 = Integer.MIN_VALUE;
			
		}

//		if (findBestFreePosition(rect))
//			rect.score1 = rect.score1;
//		else{
//			rect.score1 = Integer.MIN_VALUE;
//			rect.score2 = Integer.MIN_VALUE;
//		}
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
		double trick=1.5;
		for (Rect occupied:usedRectangles){
			if (occupied.x == x + width || occupied.x + occupied.width == x){
				tmps= adjacentLength(occupied.y, occupied.y + occupied.height, y, y
						+ height);
				score +=occupied.x==0?tmps*trick:tmps;
			}
			if (occupied.y == y + height || occupied.y + occupied.height == y){
				tmps=adjacentLength(occupied.x, occupied.x + occupied.width, x, x + width);
				score += occupied.y==0?tmps*trick:tmps;
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
	private boolean findBestFreePosition(Rect rect) {
		
		boolean isRotated=false;
		int bestContactScore = -1;
		int score;
		Rect bestPosition=null;
		
		for (Rect potentialPlace:freeRectangles){
			// not rotated
			if (potentialPlace.width >= rect.width
					&& potentialPlace.height >= rect.height) {
				 score= scorePlace(potentialPlace.x,potentialPlace.y, rect.width, rect.height);
				if (score > bestContactScore) {
					isRotated=false;
					bestContactScore = score;
					bestPosition=null;
					bestPosition=potentialPlace;
				}
			}
			if (useRotation){
				// 90d rotation
				if (potentialPlace.width >= rect.height
						&& potentialPlace.height >= rect.width) {
					score = scorePlace(potentialPlace.x,potentialPlace.y, rect.height, rect.width);
					if (score > bestContactScore) {
						isRotated=true;
						bestContactScore = score;
						bestPosition=null;
						bestPosition=potentialPlace;
					}
				}
			}
		}
		
		if (bestContactScore!=-1){
			rect.score1 = bestContactScore;
			rect.x = bestPosition.x;
			rect.y = bestPosition.y;
			bestPosition=null;
			if (isRotated)
					rect.rotate();
			return true;
		}else
			return false;

	}
		
	private boolean isAffected(Rect freeRect, Rect newRect) {
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
				Rect newNode = new Rect(freeRect) ;
				newNode.height =(newRect.y - newNode.y);
				freeRectangles.add(newNode);
			}

			// new rectangle is in the bottom side of existing free rectangle.
			if (newRect.y + newRect.height < freeRect.y + freeRect.height) {
				Rect newNode = new Rect(freeRect) ;
				newNode.y = newRect.y + newRect.height;
				newNode.height = freeRect.y + freeRect.height
						- (newRect.y + newRect.height);
				freeRectangles.add(newNode);
			}
		}
		// intersecting in y direction
		if (newRect.y < freeRect.y + freeRect.height
				&& newRect.y + newRect.height > freeRect.y) {
			// New node at the left side of the used node.
			if (newRect.x > freeRect.x
					&& newRect.x < freeRect.x + freeRect.width) {
				Rect newNode = new Rect(freeRect) ;
				newNode.width = newRect.x - newNode.x;
				freeRectangles.add(newNode);
			}

			// New node at the right side of the used node.
			if (newRect.x + newRect.width < freeRect.x + freeRect.width) {
				Rect newNode = new Rect(freeRect) ;
				newNode.x = newRect.x + newRect.width;
				newNode.width = freeRect.x + freeRect.width
						- (newRect.x + newRect.width);
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
		int i = 0;
		int j;
		while (i < freeRectangles.size()){
			j = i + 1;
			while(j < freeRectangles.size()) {
				if (BcontainsA (freeRectangles.get(i),freeRectangles.get(j))){
					freeRectangles.remove(i);
					i--;
					break;
				}
				if (BcontainsA (freeRectangles.get(j),freeRectangles.get(i))){
					freeRectangles.remove(j);
					j--;
				}
				j++;
			}
			i++;
		}
	}
	/**
	 * Does this rectangle contains rectangle a?
	 * @param a
	 * @return
	 */
	private boolean BcontainsA(Rect a,Rect b){
		return a.x >= b.x && a.y >=b.y 
			&& a.x+a.width <= b.x+b.width 
			&& a.y+a.height <= b.y+b.height;
	}
}
