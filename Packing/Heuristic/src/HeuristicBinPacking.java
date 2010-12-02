import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class HeuristicBinPacking {

	int binWidth;
	int binHeight;
	ArrayList<Rect> usedRectangles = new ArrayList<Rect>();
	ArrayList<Rect> freeRectangles = new ArrayList<Rect>();
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

	ArrayList<Rect> insert(ArrayList<RectSize> rects,
			FreeRectChoiceHeuristic method) {
		
		Collections.sort(rects);
		
		
		ArrayList<Rect> dst = new ArrayList<Rect>();
		Rect newNode;
		int bestScore1 = Integer.MAX_VALUE;
//		int bestScore2 = Integer.MAX_VALUE;
		int bestRectIndex = -1;
		Rect bestNode = null;
		while (rects.size() > 0) {
			bestScore1 = Integer.MAX_VALUE;
			bestNode = null;
			bestRectIndex = -1;
			for (int i = 0; i < rects.size(); ++i) {
				
				newNode = scoreRect(rects.get(i).width,
						rects.get(i).height, method);

				if (newNode.score1 < bestScore1
						|| (newNode.score1 == bestScore1 && newNode.score2 < bestScore1)) {
//					if ((newNode.score1 == bestScore1 && newNode.score2 < bestScore1))
//						newNode.rotate();
					bestScore1 = newNode.score1;
					// bestScore2 = newNode.score2;
					bestNode = newNode;
					bestRectIndex = i;					
						
				}
			}

			if (bestRectIndex == -1)
				return dst;

			placeRect(bestNode);
			if (bestNode.x+bestNode.width>maxW)
				maxW=bestNode.x+bestNode.width;
			if (bestNode.y+bestNode.height>maxH)
				maxH=bestNode.y+bestNode.height;
			
			dst.add(bestNode);
			rects.remove(bestRectIndex);
		}
		return dst;
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

	Rect scoreRect(int width, int height, FreeRectChoiceHeuristic method) {
		Rect newNode = null;
		switch (method) {
		case RectContactPointRule:
			newNode = touchingPerimeter(width, height);
			newNode.score1 = -1 * newNode.score1;// Reverse since we are
													// minimizing, but for
													// contact point score
													// bigger is better.
			break;
		}

		// Cannot fit the current rectangle.
		if (newNode.height == 0) {
			newNode.score1 = Integer.MAX_VALUE;
			newNode.score2 = Integer.MAX_VALUE;
		}

		return newNode;
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
	Rect touchingPerimeter(int width, int height) {
		Rect bestNode = new Rect();

		int bestContactScore = -1;

		for (int i = 0; i < freeRectangles.size(); ++i) {
			// not rotated
			if (freeRectangles.get(i).width >= width
					&& freeRectangles.get(i).height >= height) {
				int score = contactPointScoreNode(freeRectangles.get(i).x,
						freeRectangles.get(i).y, width, height);
				if (score > bestContactScore) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestNode.rotated=false;
					bestContactScore = score;
				}
			}
			// 90d rotation
			if (freeRectangles.get(i).width >= height
					&& freeRectangles.get(i).height >= width) {
				int score = contactPointScoreNode(freeRectangles.get(i).x,
						freeRectangles.get(i).y, height, width);
				if (score > bestContactScore) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestNode.rotate();
					bestContactScore = score;
				}
			}
		}
		bestNode.score1 = bestContactScore;
		return bestNode;
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
