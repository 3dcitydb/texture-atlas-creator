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


import java.util.Collections;
import java.util.LinkedList;


import org.citygml.textureAtlasAPI.TextureAtlasGenerator;
import org.citygml.textureAtlasAPI.packer.comparator.HeightComparator;
//import org.citygml.util.Logger;

/**
 * It is the responsible class for packing the textures. Each texture will be represented as 
 * a Rect instance. Based on selected packing algorithm, and bin size, it will pack the textures 
 * in an atlas. Each atlas may have different levels.  
 */
public class Packer  {
	
	private LinkedList <Rect> rects;
    private int binWidth = 0;
    private int binHeight = 0;
    private boolean fourChanel=false;
    private int algorithm=TextureAtlasGenerator.FFDH;
    private TouchingPerimeterPacking tpPacker;
    
    public Packer(int binWidth, int binHeight,int algorithm,boolean is4Chanel){
    	rects = new LinkedList<Rect>();
    	this.algorithm=algorithm;
    	this.fourChanel=is4Chanel;
    	setBinSize(binWidth, binHeight);
    }

    public boolean isFourChanel() {
		return fourChanel;
	}

	public void setFourChanel(boolean fourChanel) {
		this.fourChanel = fourChanel;
	}

	public void setBinSize(int width, int height) {
    	this.binWidth= width;
    	this.binHeight=height;
	}
    
	public boolean addRect(String URI, int width, int  height) {
		return rects.add(new Rect(URI, width, height));
	}
	
	public boolean addRect(Rect mi){
		return rects.add(mi);
	}
	
	public boolean removeRect(String URI){
		return rects.remove(new Rect(URI,0,0));
	}
	
	public void reset(){
		rects.clear();
	}
	
	public int getSize(){
		return rects!=null?rects.size():0;
	}
	
	public Atlas pack(boolean takePOT) {
		Atlas atlas;
        switch (algorithm){
        case TextureAtlasGenerator.NFDH:
        	atlas=NFDH(binWidth);
            break;
        case TextureAtlasGenerator.FFDH:
            	atlas=FFDH(binWidth);
                break;
            case TextureAtlasGenerator.SLEA:
            	atlas=SLEA(binWidth);
                break;
            case TextureAtlasGenerator.TPIM:
                if (tpPacker==null)
                	tpPacker= new TouchingPerimeterPacking(binWidth,binHeight,takePOT);
                else
                	tpPacker.init(binWidth, binHeight,takePOT);
                tpPacker.setUseRotation(true);
                atlas= tpPacker.insert(rects);
                break;
                
            case TextureAtlasGenerator.TPIM_WITHOUT_ROTATION:
                if (tpPacker==null)
                	tpPacker= new TouchingPerimeterPacking(binWidth,binHeight,takePOT);
                else
                	tpPacker.init(binWidth, binHeight,takePOT);
                tpPacker.setUseRotation(false);
                atlas= tpPacker.insert(rects);
                break;
          
            default:
            	// Type of algorithm is not correctly set.
                return null;
        }
        atlas.setFourChanel(fourChanel);
		return atlas;
	}
	/**
	 * <I>The Next-Fit Decreasing Height (NFDH) algorithm packs the next item, left justified, 
	 * on the current level (initially, the bottom of the strip), if it fits. Otherwise, 
	 * the level is 'closed', a new current level is created (as a horizontal line drawn on 
	 * the top of the tallest item packed on the current level), and the item is packed, left 
	 * justified, on it.</I>
	 * 
	 * Lodi, Andrea, Martello, Silvano and Monaci, Michele, (2002), Two-dimensional packing 
	 * problems: A survey, European Journal of Operational Research, 141, issue 2, p. 241-252
	 *  
	 * @param bindingBoxWidth
	 * @return
	 */
	private Atlas NFDH(int bindingBoxWidth) {
		int levelWidth = 0;
		int levelFloor = 0;
		int levelRoof = 0;
		short currentLevelNum=0;
		
		Atlas atlas = new Atlas();
		atlas.setBindingBoxWidth(bindingBoxWidth);
		
		// sort the rectangles in "Decreasing Height"
		Collections.sort(rects, new HeightComparator());
		
		
		if (rects.size()>0)
			levelRoof=rects.getFirst().height;
		
		
		for (Rect rect : rects) {
			if (levelWidth + rect.width > bindingBoxWidth) {
				// new level is necessary
				currentLevelNum++;
				levelWidth = 0;
				levelFloor = levelRoof;
				levelRoof+=rect.height;
			}
			
			rect.setPosition(levelWidth, levelFloor, currentLevelNum);
			atlas.addRect(rect);
			levelWidth += rect.width;
			
		}
		atlas.setBindingBoxHeight(levelRoof);
		return atlas;
	}

	/**
	 * <I>The First-Fit Decreasing Height (FFDH) algorithm packs the next item, left justified, 
	 * on the first level where it fits, if any. If no level can accommodate it, a new level 
	 * is created as in NFDH. 
	 * 
	 * Lodi, Andrea, Martello, Silvano and Monaci, Michele, (2002), Two-dimensional packing 
	 * problems: A survey, European Journal of Operational Research, 141, issue 2, p. 241-252
	 * 
	 * @param bindingBoxWidth
	 * @return
	 */
	private Atlas FFDH(int bindingBoxWidth) {
		
		int binRoof = 0;
		short numLevels=1;
		
		int[] levelsRemainSpace =new int[10];
		int[] levelsFloor =new int[10];
		
		Atlas atlas = new Atlas();
		atlas.setBindingBoxWidth(bindingBoxWidth);
		
		// initialization
		levelsRemainSpace[0]=bindingBoxWidth;
		levelsFloor[0]=0;
			
		// sort the rectangles in "Decreasing Height"
        Collections.sort(rects, new HeightComparator());    
        
        
        if (rects.size()>0)
        	binRoof=rects.getFirst().height;
        
        short counter;
        boolean found;
        for (Rect rect:rects){
        	found=false;
        	for (counter=0;counter<numLevels;counter++){
        		if (levelsRemainSpace[counter]>=rect.width){
        			rect.setPosition(bindingBoxWidth-levelsRemainSpace[counter], 
        					levelsFloor[counter],counter);
        			atlas.addRect(rect);
        			levelsRemainSpace[counter]-=rect.width;
        			found=true;
                    break;
        		}
        	}
        	if (!found){		
        		if (numLevels>=levelsRemainSpace.length){
        			int[] tmpLRS =new int[numLevels*2];
        			int[] tmpLF =new int[numLevels*2];
        			System.arraycopy(levelsRemainSpace, 0, tmpLRS, 0, levelsRemainSpace.length);
        			System.arraycopy(levelsFloor, 0, tmpLF, 0, levelsFloor.length);
        			levelsRemainSpace=tmpLRS;
        			levelsFloor=tmpLF;
        			tmpLF=null;tmpLRS=null;
        		}
        		levelsRemainSpace[numLevels]=bindingBoxWidth;
        		levelsFloor[numLevels]=binRoof;
        		
    			rect.setPosition(0, 
    					levelsFloor[numLevels],numLevels);
    			atlas.addRect(rect);
    			levelsRemainSpace[numLevels]-=rect.width;
    			binRoof+=rect.height;
    			numLevels++;        		
        	}
        }        
        atlas.setBindingBoxHeight(binRoof); 
        levelsRemainSpace=null;
		levelsFloor=null;
        return atlas;
	}

	/**
	 *  Sleator's algorithm is a two-dimentional bin packing. It starts by packing
	 * 	all the items which has a width greater than half of the bin's width on top of
	 * 	each other. Then the remaining items will be sorted according to nonincreasing
	 * 	height. 
	 * 	The bin will be split to two halves. Next items will be placed from left to right 
	 * 	in the left half and when it can not accommodate, the left half will be close and items 
	 * 	will be placed in right half. Next level of halves, will be started with different 
	 * 	bottom height according to maximum height in previous level of left and right side.

	 * 	For more information please refer to:
	 * 	D. Sleator , A 2.5 times optimal algorithm for packing in two
	 * 	dimensions. Information Processing Letters 10 (1980), pp. 37-40.
	 * 
	 * @param bindingBoxWidth
	 * @return
	 */
	private Atlas SLEA(int bindingBoxWidth) {
		LinkedList<Rect> smallerThanHalfRects = new LinkedList<Rect>();
		int levelRoof = 0;
		short currentLevel = 0;
		int threshold = bindingBoxWidth / 2;

		Atlas atlas = new Atlas();
		atlas.setBindingBoxWidth(bindingBoxWidth);
		// step 1. Stack all the pieces of width greater than 4 on top of one
		// another in the bottom of the bin.
		for (Rect rect : rects) {
			if (rect.width > threshold) {
				rect.setPosition( 0, levelRoof, currentLevel);
				atlas.addRect(rect);
				levelRoof += rect.height;
			} else
				smallerThanHalfRects.add(rect);
		}
		// as a result h0:levelRoof
		
		// sort the remaining rectangles in "Decreasing Height"
		Collections.sort(smallerThanHalfRects, new HeightComparator());
		     
	    
		currentLevel++;
		int leftRoof,leftFloor,rightRoof,rightFloor;
		int levelWidth=0,levelFloor;
		leftFloor=rightFloor=levelFloor=rightRoof=leftRoof=levelRoof;
		if (smallerThanHalfRects.size()>0){
			leftRoof=(leftFloor+smallerThanHalfRects.getFirst().height);
		}
		for (Rect rect : smallerThanHalfRects) {
			 if ( levelWidth< threshold && (levelWidth + rect.width) >= threshold){
				 // should be set in half of right hand side. 
				 levelWidth = threshold;
				 rightFloor = rightRoof;
				 levelFloor=rightFloor;
				 rightRoof += rect.height;
             } else if (levelWidth >= threshold && levelWidth+rect.width >= bindingBoxWidth){
            	 // new level should be added and this rect should be placed on left hand side half.
            	 levelWidth = 0;
            	 leftFloor = leftRoof;
            	 levelFloor= leftFloor;
            	 leftRoof+=rect.height;
            	 currentLevel++;
             }
			 rect.setPosition(levelWidth,levelFloor,currentLevel);
			 atlas.addRect(rect);
             levelWidth+=rect.width;
             
		}
		atlas.setBindingBoxHeight(Math.max(leftRoof, rightRoof));
		smallerThanHalfRects.clear();
	    return atlas;
	}
 
}
