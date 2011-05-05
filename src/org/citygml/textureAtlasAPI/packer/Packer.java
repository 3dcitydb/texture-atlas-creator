package org.citygml.textureAtlasAPI.packer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import org.citygml.textureAtlasAPI.TextureAtlasGenerator;
import org.citygml.textureAtlasAPI.packer.comparator.HeightComparator;
//import org.citygml.util.Logger;


public class Packer  {
	
	private List <Rect> items;
	// Can be used just for 2D Strip Packers also
    private int binWidth = 0;
    // in the case that using 2dB
    private int binHeight = 0;
    
    private boolean fourChanel=false;
    
    private int algorithm=TextureAtlasGenerator.FFDH;

    private HeuristicBinPacking tpPacker;
    
    public Packer(int width, int height,int algorithm,boolean is4Chanel){
    	items = new LinkedList<Rect>();
    	this.algorithm=algorithm;
    	this.fourChanel=is4Chanel;
    	setSize(width, height);
    }

    public boolean isFourChanel() {
		return fourChanel;
	}

	public void setFourChanel(boolean fourChanel) {
		this.fourChanel = fourChanel;
	}

	public void setSize(int width, int height) {
    	this.binWidth= width;
    	this.binHeight=height;
	}
    
	public boolean addRect(String URI, int width, int height) {
			return items.add(new Rect(URI, width, height));
	}
	
	public boolean addRect(Rect mi){
		return items.add(mi);
	}
	
	public boolean removeRect(String URI){
		return items.remove(new Rect(URI,0,0));
	}
	
	public void reset(){
		items.clear();
	}
	
	public int getSize(){
		if (items!=null)
			return items.size();
		else
			return 0;
	}
	
	public Atlas pack() throws Exception {
		Atlas res = new Atlas();
		res.setBindingBoxWidth(binWidth);
        switch (algorithm){
            case TextureAtlasGenerator.FFDH:
            	FFDH(res);
                break;
            case TextureAtlasGenerator.NFDH:
                NFDH(res);
                break;
            case TextureAtlasGenerator.SLEA:
                SLEA(res);
                break;
            case TextureAtlasGenerator.TPIM:
                if (tpPacker==null)
                	tpPacker= new HeuristicBinPacking(binWidth,binHeight);
                else
                	tpPacker.init(binWidth, binHeight);
                tpPacker.setUseRotation(true);
                res= tpPacker.insert(items);
                
                break;
                
            case TextureAtlasGenerator.TPIM_WITHOUT_ROTATION:
                if (tpPacker==null)
                	tpPacker= new HeuristicBinPacking(binWidth,binHeight);
                else
                	tpPacker.init(binWidth, binHeight);
                tpPacker.setUseRotation(false);
                res= tpPacker.insert(items);
            
                break;
          
            default:
                throw new Exception(algorithm + " is not supported.");
        }
        res.setFourChanel(fourChanel);
		return res;
	}
	
	private void FFDH(Atlas res) {
		List <Integer> levels = new ArrayList<Integer>();
        List <Integer> levelFill = new ArrayList<Integer>();
        int topLev = 0;
        int nextTopLev = 0;       // der nächste level wird bei dieser höhe beginnen

        Collections.sort(items, new HeightComparator());    
        Collections.reverse(items);     //da sort() aufsteigend sortiert
        Iterator <Rect> iter=items.iterator();

        levels.add(new Integer(0));    // die starthöhen der jeweiligen level
        levelFill.add(new Integer(0)); // die füllstände der level

        if (iter.hasNext()){
        	Rect i = iter.next();
            nextTopLev = i.getHeight();
            levelFill.set(0,Integer.valueOf(i.getWidth()) );
            i.setPOS(0, 0, new Integer(0));
            res.addItem(i);
            
        }
        while (iter.hasNext()){
        	Rect i = iter.next();
            int itemWidth = i.getWidth();
            int curLev;
            for (curLev = 0; curLev <= topLev; curLev++){   // versuche das item in einen möglichst niedrigen level zu packen
                int curFill;
                if ((curFill = levelFill.get(curLev)) <= (binWidth - itemWidth)){
                	i.setPOS(curFill, levels.get(curLev).intValue(), new Integer(curLev));
                	res.addItem(i);
                    levelFill.set(curLev, new Integer(curFill + itemWidth));
                    break;
                }
            }
            if (curLev > topLev) {  // neuen level anlegen
                topLev = topLev + 1;
                levelFill.add(i.getWidth());
                levels.add(new Integer(nextTopLev));
                i.setPOS(0, nextTopLev, new Integer(topLev));
                res.addItem(i);
                nextTopLev = i.getHeight() + nextTopLev;
            }
        }
        res.setBindingBoxHeight(nextTopLev);    // die max. packungshöhe
	}

	private void NFDH(Atlas res) {
	     Integer levelCount = new Integer(0);
	        int levelFill = 0;     // die bereits ausgefüllte breite des strips auf dem akt. level
	        int currLevel = 0;     // die untere grenze des akt. levels
	        int nextLevel = 0;     // die untere grenze des nächsten levels

	        Collections.sort(items,new HeightComparator());    //TODO: woanders hinstecken
	        Collections.reverse(items);     //da sort() aufsteigend sortiert
	        Iterator <Rect> iter = items.iterator();

	        if (iter.hasNext()){    // das erste item passt immer, da der strip leer ist und die items nicht breiter als der strip sind
	        	Rect i = iter.next();
	            i.setPOS(levelFill, currLevel, levelCount);
	            res.addItem(i);
	            levelFill = i.getWidth();
	            nextLevel = i.getHeight();
	        }
	        while (iter.hasNext()){
	        	Rect i = iter.next();
	            if (levelFill + i.getWidth() > binWidth){   // das neue item ist zu breit für den level
	                levelCount = levelCount + 1;
	                levelFill = 0;
	                currLevel = nextLevel;
	                nextLevel = currLevel + i.getHeight();
	            }
	            i.setPOS(levelFill, currLevel, levelCount);
	            res.addItem(i);
	            levelFill = levelFill + i.getWidth();
	        }
	        res.setBindingBoxHeight(nextLevel);
	}

	private void SLEA(Atlas res) {
		 LinkedList <Rect> smallerItems = new LinkedList<Rect>();
	        Iterator <Rect> iter = items.iterator();
	        int nextX = 0, nextY = 0;
	        Integer level = new Integer(0);
	        
	        while (iter.hasNext()){     // grundlinie, d.h. die breiten items
	        	Rect item = iter.next();
	            int iWidth = item.getWidth();
	            if (iWidth > (0.5 * binWidth)){  // es wird natürlich vorausgesetzt, dass 0 <= iWidth <= stripWidth gilt
	            	item.setPOS(nextX, nextY, level);
	                res.addItem(item);
	                nextY= nextY + item.getHeight();
	            } else {
	                smallerItems.add(item);
	            }
	        }
	        res.setBindingBoxHeight(nextY);
	        
	        Collections.sort(smallerItems,new HeightComparator()); //TODO: war vielleicht eh schon sortiert (wahr, wenn, die anderen TODOs erledigt sind)
	        Collections.reverse(smallerItems);
	        
	        iter = smallerItems.iterator();
	        if (iter.hasNext()){
	        	Rect item = iter.next();
	            level++;
	            int nextLeftLevel = nextY + item.getHeight();    // entspr. h0 + h1
	            assert nextX == 0;
	            item.setPOS(nextX, nextY, level);
	            res.addItem(item);    // erstes item auf h0
	            nextX = item.getWidth();
	            while (iter.hasNext() && (nextX < 0.5 * binWidth)){    // auf h0 bis min. halbe breite füllen
	                item = iter.next();
	                item.setPOS(nextX, nextY, level);
	                res.addItem(item);
	                nextX = nextX + item.getWidth();
	            }
	            int nextRightLevel = nextY + item.getHeight();   // entspr. h0 + d1
	            while (iter.hasNext()){
	                item = iter.next();
	                int width = item.getWidth();
	                if (nextX < (0.5 * binWidth) && (nextX + width) >= (0.5 * binWidth)){   // wahr, wenn das item zu breit für die akt. linke Spalte ist
	                    nextX = binWidth/2;
	                    nextY = nextRightLevel;
	                    nextRightLevel = nextY + item.getHeight();
	                    level++;
	                } else if (nextX >= (0.5 * binWidth) && (nextX + width) >= binWidth){   //     ""   ... rechte Spalte
	                    nextX = 0;
	                    nextY = nextLeftLevel;
	                    nextLeftLevel = nextY + item.getHeight();
	                    level++;
	                }
	                item.setPOS(nextX, nextY, level);
	                res.addItem(item);
	                nextX = nextX + item.getWidth();
	            }
	            res.setBindingBoxHeight((nextRightLevel > nextLeftLevel) ? nextRightLevel : nextLeftLevel);
	        }
	}
 
}
