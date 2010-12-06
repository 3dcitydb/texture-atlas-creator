package org.citygml.textureAtlasAPI.packer;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HeuristicBinPacking {
	int binWidth;
	int binHeight;
	boolean useRotation=false;
	

	List<Rect> usedRectangles = new ArrayList<Rect>();
	List<Rect> freeRectangles = new ArrayList<Rect>();
	int maxH=0; int maxW=0;
	HeuristicBinPacking() {
		binWidth = 0;
		binHeight = 0;
	}

	
	HeuristicBinPacking(int width, int height) {
		init(width, height);
	}

	public void init(int width, int height) {
		binWidth = width;
		binHeight = height;
		clear();
	}
	public Dimension getBoundingBox(){
		return new Dimension(maxW, maxH);
	}
	
	public void setUseRotation(boolean useRotation) {
		this.useRotation = useRotation;
	}
	public void clear(){
		Rect n = new Rect();
		n.x = 0;
		n.y = 0;
		n.width = binWidth;
		n.height = binHeight;

		usedRectangles.clear();

		freeRectangles.clear();
		freeRectangles.add(n);
		maxH=0;maxW=0;
	}

	Atlas insert(List<AbstractRect> rects) {
		
		Collections.sort(rects);
		Atlas res= new Atlas();
		Integer level = new Integer(0);
		
		List<Rect> dst = new ArrayList<Rect>();
		AbstractRect selectedNode;
		int bestScore1 = Integer.MAX_VALUE;
		int bestRectIndex = -1;
		
		while (rects.size() > 0) {
			bestScore1 = Integer.MAX_VALUE;
			bestRectIndex = -1;
			for (int i = 0; i < rects.size(); ++i) {
				selectedNode= rects.get(i);
				if (selectedNode.rotated){
					selectedNode.rotate();
					selectedNode.rotated=false;
				}
				scoreRect(selectedNode);

				if (selectedNode.score1 < bestScore1
						|| (selectedNode.score1 == bestScore1 && selectedNode.score2 < bestScore1)) {
					bestScore1 = selectedNode.score1;				
					bestRectIndex = i;					
						
				}
			}

			if (bestRectIndex == -1)
				if (rects.size()>0){
					level= new Integer(level.intValue()+1);
					res.setBindingBox(res.getBindingBoxWidth()+maxW,res.getBindingBoxHeight()+maxH );
					clear();
					continue;
					
				}
				else
					return res;
			selectedNode = rects.get(bestRectIndex);
			placeRect((Rect)selectedNode);
			if (selectedNode.x+selectedNode.width>maxW)
				maxW=selectedNode.x+selectedNode.width;
			if (selectedNode.y+selectedNode.height>maxH)
				maxH=selectedNode.y+selectedNode.height;
			selectedNode.setLevel(level);
			res.addItem(selectedNode);
			rects.remove(bestRectIndex);
		}
		res.setBindingBox(res.getBindingBoxWidth()+maxW,res.getBindingBoxHeight()+maxH );
		return res;
	}

	void placeRect(Rect node) {
		int numRectanglesToProcess = freeRectangles.size();
//		for (int i = 0; i < numRectanglesToProcess; ++i) {
		int i=0;
		while(i<numRectanglesToProcess){
			if (splitFreeNode(freeRectangles.get(i), node)) {
				freeRectangles.remove(i);
				--i;
				--numRectanglesToProcess;
			}
			i++;
		}
		pruneFreeList();
		usedRectangles.add(node);
	}

	void scoreRect(AbstractRect rect) {
		if (touchingPerimeter(rect))
			rect.score1 = -1 * rect.score1;
		else{
			rect.score1 = Integer.MAX_VALUE;
			rect.score2 = Integer.MAX_VALUE;
		}
	}

	float occupancy() {
		long usedSurfaceArea = 0;
		for (int i = 0; i < usedRectangles.size(); ++i)
			usedSurfaceArea += usedRectangles.get(i).width
					* usedRectangles.get(i).height;

		return (float) usedSurfaceArea / (maxH * maxW);
	}

	int commonIntervalLength(int i1start, int i1end, int i2start, int i2end) {
		if (i1end < i2start || i2end < i1start)
			return 0;
		return Math.min(i1end, i2end) - Math.max(i1start, i2start);
	}

	int contactPointScoreNode(int x, int y, int width, int height) {
		
		int score = 0;


		Rect r;
		int tmps;
		double cc=1.5;
		for (int i = 0; i < usedRectangles.size(); ++i) {
			r = usedRectangles.get(i);
			if (r.x == x + width || r.x + r.width == x){
				tmps= commonIntervalLength(r.y, r.y + r.height, y, y
						+ height);
				score +=r.x==0?tmps*cc:tmps;
			}
			if (r.y == y + height || r.y + r.height == y){
				tmps=commonIntervalLength(r.x, r.x + r.width, x, x + width);
				score += r.y==0?tmps*cc:tmps;
			}
		}
		return score;
	}

	/**
	 * put the best score in Rect.score1
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	boolean touchingPerimeter(AbstractRect rect) {
		
		boolean isRotated=false;
		int nodeID=-1;
		int bestContactScore = -1;
		int score;
		for (int i = 0; i < freeRectangles.size(); ++i) {
			// not rotated
			if (freeRectangles.get(i).width >= rect.width
					&& freeRectangles.get(i).height >= rect.height) {
				 score= contactPointScoreNode(freeRectangles.get(i).x,
						freeRectangles.get(i).y, rect.width, rect.height);
				if (score > bestContactScore) {
					isRotated=false;
					bestContactScore = score;
					nodeID=i;
				}
			}
			if (useRotation){
			// 90d rotation
			if (freeRectangles.get(i).width >= rect.height
					&& freeRectangles.get(i).height >= rect.width) {
				score = contactPointScoreNode(freeRectangles.get(i).x,
						freeRectangles.get(i).y, rect.height, rect.width);
				if (score > bestContactScore) {
					isRotated=true;
					bestContactScore = score;
					nodeID=i;
				}
			}
			}
		}
		if (bestContactScore!=-1){
		rect.score1 = bestContactScore;
		rect.x = freeRectangles.get(nodeID).x;
		rect.y = freeRectangles.get(nodeID).y;
		if (isRotated)
				rect.rotate();
		return true;
		}else
			return false;

	}

	boolean splitFreeNode(Rect freeNode, Rect usedNode) {
		// Test with SAT if the rectangles even intersect.
		if (usedNode.x >= freeNode.x + freeNode.width
				|| usedNode.x + usedNode.width <= freeNode.x
				|| usedNode.y >= freeNode.y + freeNode.height
				|| usedNode.y + usedNode.height <= freeNode.y)
			return false;

		if (usedNode.x < freeNode.x + freeNode.width
				&& usedNode.x + usedNode.width > freeNode.x) {
			// New node at the top side of the used node.
			if (usedNode.y > freeNode.y
					&& usedNode.y < freeNode.y + freeNode.height) {
				Rect newNode = new Rect(freeNode) ;
				newNode.height = usedNode.y - newNode.y;
				freeRectangles.add(newNode);
			}

			// New node at the bottom side of the used node.
			if (usedNode.y + usedNode.height < freeNode.y + freeNode.height) {
				Rect newNode = new Rect(freeNode) ;
				newNode.y = usedNode.y + usedNode.height;
				newNode.height = freeNode.y + freeNode.height
						- (usedNode.y + usedNode.height);
				freeRectangles.add(newNode);
			}
		}

		if (usedNode.y < freeNode.y + freeNode.height
				&& usedNode.y + usedNode.height > freeNode.y) {
			// New node at the left side of the used node.
			if (usedNode.x > freeNode.x
					&& usedNode.x < freeNode.x + freeNode.width) {
				Rect newNode = new Rect(freeNode) ;
				newNode.width = usedNode.x - newNode.x;
				freeRectangles.add(newNode);
			}

			// New node at the right side of the used node.
			if (usedNode.x + usedNode.width < freeNode.x + freeNode.width) {
				Rect newNode = new Rect(freeNode) ;
				newNode.x = usedNode.x + usedNode.width;
				newNode.width = freeNode.x + freeNode.width
						- (usedNode.x + usedNode.width);
				freeRectangles.add(newNode);
			}
		}

		return true;
	}

	void pruneFreeList() {
		/*
		 * /// Would be nice to do something like this, to avoid a Theta(n^2)
		 * loop through each pair. /// But unfortunately it doesn't quite cut
		 * it, since we also want to detect containment. /// Perhaps there's
		 * another way to do this faster than Theta(n^2).
		 * 
		 * if (freeRectangles.size() > 0)
		 * clb::sort::QuickSort(&freeRectangles[0], freeRectangles.size(),
		 * NodeSortCmp);
		 * 
		 * for(size_t i = 0; i < freeRectangles.size()-1; ++i) if
		 * (freeRectangles[i].x == freeRectangles[i+1].x && freeRectangles[i].y
		 * == freeRectangles[i+1].y && freeRectangles[i].width ==
		 * freeRectangles[i+1].width && freeRectangles[i].height ==
		 * freeRectangles[i+1].height) {
		 * freeRectangles.erase(freeRectangles.begin() + i); --i; }
		 */

		// / Go through each pair and remove any rectangle that is redundant.
		int i = 0;
		int j;
		while (i < freeRectangles.size()){
			j = i + 1;
			while(j < freeRectangles.size()) {
				if (Rect.isContainedIn(freeRectangles.get(i), freeRectangles
						.get(j))) {
					freeRectangles.remove(i);
					--i;
					break;
				}
				if (Rect.isContainedIn(freeRectangles.get(j), freeRectangles
						.get(i))) {
					freeRectangles.remove(j);
					--j;
				}
				j++;
			}
			i++;
		}
	}
}
