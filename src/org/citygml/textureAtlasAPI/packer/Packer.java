package org.citygml.textureAtlasAPI.packer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.citygml.textureAtlasAPI.TextureAtlasGenerator;
//import org.citygml.util.Logger;


public class Packer  {
	/**
	static public final int FFDH = 0;
    static public final int NFDH = 1;
    static public final int SLEA = 2;
    static public final int BOLE = 3;
    static public final int STBG = 4;**/
//    static public final String[] algNames = {"FFDH", "NFDH", "Sleator", "BL", "Steinberg"};

	private List <AbstractRect> items;
	// Can be used just for 2D Strip Packers also
    private int binWidth = 0;
    // in the case that using 2dB
    private int binHeight = 0;
    
    private boolean fourChanel=false;
    
    private int algorithm=TextureAtlasGenerator.FFDH;
    
    
    private HeuristicBinPacking tpPacker;
    
    public Packer(int width, int height,int algorithm,boolean is4Chanel){
    	items = new LinkedList<AbstractRect>();
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
    
	public boolean addItem(String URI, int width, int height) {
		if (algorithm==TextureAtlasGenerator.TPIM||algorithm==TextureAtlasGenerator.TPIM_WITHOUT_ROTATION)
			return items.add(new Rect(URI, width, height));
		return items.add(new Item(URI, width, height));
	}
	
	public boolean addItem(AbstractRect mi){
		return items.add(mi);
	}
	
	public boolean removeItem(String URI){
		return items.remove(new Item(URI,0,0));
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
	
	public Atlas getResult() throws Exception {
		Atlas res = new Atlas();
		res.setBindingBoxWidth(binWidth);
        switch (algorithm){
            case TextureAtlasGenerator.FFDH:
                calcFFDH(res);
                break;
            case TextureAtlasGenerator.NFDH:
                calcNFDH(res);
                break;
            case TextureAtlasGenerator.SLEA:
                calcSLEA(res);
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
	
	private void calcFFDH(Atlas res) {
		List <Integer> levels = new ArrayList<Integer>();
        List <Integer> levelFill = new ArrayList<Integer>();
        int topLev = 0;
        int nextTopLev = 0;       // der nächste level wird bei dieser höhe beginnen

        Collections.sort(items);    
        Collections.reverse(items);     //da sort() aufsteigend sortiert
        Iterator <AbstractRect> iter=items.iterator();

        levels.add(new Integer(0));    // die starthöhen der jeweiligen level
        levelFill.add(new Integer(0)); // die füllstände der level

        if (iter.hasNext()){
        	AbstractRect i = iter.next();
            nextTopLev = i.getHeight();
            levelFill.set(0,Integer.valueOf(i.getWidth()) );
            i.setPOS(0, 0, new Integer(0));
            res.addItem(i);
            
        }
        while (iter.hasNext()){
        	AbstractRect i = iter.next();
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

	private void calcNFDH(Atlas res) {
	     Integer levelCount = new Integer(0);
	        int levelFill = 0;     // die bereits ausgefüllte breite des strips auf dem akt. level
	        int currLevel = 0;     // die untere grenze des akt. levels
	        int nextLevel = 0;     // die untere grenze des nächsten levels

	        Collections.sort(items);    //TODO: woanders hinstecken
	        Collections.reverse(items);     //da sort() aufsteigend sortiert
	        Iterator <AbstractRect> iter = items.iterator();

	        if (iter.hasNext()){    // das erste item passt immer, da der strip leer ist und die items nicht breiter als der strip sind
	        	AbstractRect i = iter.next();
	            i.setPOS(levelFill, currLevel, levelCount);
	            res.addItem(i);
	            levelFill = i.getWidth();
	            nextLevel = i.getHeight();
	        }
	        while (iter.hasNext()){
	        	AbstractRect i = iter.next();
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

	private void calcSLEA(Atlas res) {
		 LinkedList <AbstractRect> smallerItems = new LinkedList<AbstractRect>();
	        Iterator <AbstractRect> iter = items.iterator();
	        int nextX = 0, nextY = 0;
	        Integer level = new Integer(0);
	        
	        while (iter.hasNext()){     // grundlinie, d.h. die breiten items
	        	AbstractRect item = iter.next();
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
	        
	        Collections.sort(smallerItems); //TODO: war vielleicht eh schon sortiert (wahr, wenn, die anderen TODOs erledigt sind)
	        Collections.reverse(smallerItems);
	        
	        iter = smallerItems.iterator();
	        if (iter.hasNext()){
	        	AbstractRect item = iter.next();
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

	

	
	  private void addSortedLists(List <Rect> src, Stack <LinkedList<Rect>> wsLists,
	            Stack <LinkedList<Rect>> hsLists, Stack <LinkedList<Rect>> asLists) {
	            addSortedLists(src, wsLists, hsLists, asLists, false, false);
	    }
	    
	    private void addSortedLists(List <Rect> src, Stack <LinkedList<Rect>> wsLists,
	            Stack <LinkedList<Rect>> hsLists, Stack <LinkedList<Rect>> asLists, boolean isWSorted, boolean isHSorted) {
	        wsLists.push(new LinkedList(src));
	        if (isWSorted == false){
	            Collections.sort(wsLists.peek(), new Comparator<Rect>(){   // nach breite sortiert
	                public int compare(Rect o1, Rect o2) { 
	                    return o1.width > o2.width ? -1 : o1.width == o2.width ? 0 : 1;}});
	        }

	        hsLists.push(new LinkedList(src));
	        if (isHSorted == false){
	            Collections.sort(hsLists.peek());    // nach höhe sortieren
	            Collections.reverse(hsLists.peek());
	        }
	        
	        asLists.push(new LinkedList(src));
	        Collections.sort(asLists.peek(), new Comparator<Rect>(){    // nach fläche sortiert
	            public int compare(Rect o1, Rect o2){
	                return (o1.area) > (o2.area) ? -1 : (o1.area) == (o2.area) ? 0 : 1;}});
	    }
}
