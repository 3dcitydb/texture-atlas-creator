package org.citygml.TextureAtlasAPI.StripPacker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MyStPacker  {
	
	static public final int FFDH = 0;
    static public final int NFDH = 1;
    static public final int SLEA = 2;
    static public final int BOLE = 3;
    static public final int STBG = 4;
    static public final String[] algNames = {"FFDH", "NFDH", "Sleator", "BL", "Steinberg"};

	private List <MyItem> items;
    private int stripWidth = 0;
    
    public MyStPacker(){
    	items = new LinkedList<MyItem>();
    }

    public void setStripWidth(int stripWidth) {
		this.stripWidth=stripWidth;

	}
    
//	public boolean addItem(String id, int width, int height) {
//		return items.add(new MyItem(id, width, height));
//	}

	public boolean addItem(String id, int width, int height, Long surfaceID, double[] coordinates) {
		return items.add(new MyItem(id, width, height,surfaceID,coordinates));
	}
	public void reset(){
		items.clear();
	}

	public MyResult getResult(int algo) throws Exception {
		MyResult res = new MyResult(stripWidth);
        switch (algo){
            case FFDH:
                calcFFDH(res);
                break;
            case NFDH:
                calcNFDH(res);
                break;
            case SLEA:
                calcSLEA(res);
                break;
            case BOLE:
                calcBOLE(res);
                break;
            case STBG:
                calcSTBG(res);
                break;
            default:
                throw new Exception(algo + " ist kein bekannter Algorithmus");
        }
		return res;
	}
	
	private void calcFFDH(MyResult res) {
		List <Integer> levels = new ArrayList<Integer>();
        List <Integer> levelFill = new ArrayList<Integer>();
        int topLev = 0;
        int nextTopLev = 0;       // der nächste level wird bei dieser höhe beginnen

        Collections.sort(items);    //TODO: unpassende stelle, da die items bei jedem aufruf einer calc-methode neu sortiert werden
        Collections.reverse(items);     //da sort() aufsteigend sortiert
        Iterator <MyItem> iter = items.iterator();

        levels.add(new Integer(0));    // die starthöhen der jeweiligen level
        levelFill.add(new Integer(0)); // die füllstände der level

        if (iter.hasNext()){
            MyItem i = iter.next();
            nextTopLev = i.getHeight();
            levelFill.set(0,Integer.valueOf(i.getWidth()) );
            i.setPOS(0, 0, new Integer(0));
            res.addItem(i);
            
        }
        while (iter.hasNext()){
            MyItem i = iter.next();
            int itemWidth = i.getWidth();
            int curLev;
            for (curLev = 0; curLev <= topLev; curLev++){   // versuche das item in einen möglichst niedrigen level zu packen
                int curFill;
                if ((curFill = levelFill.get(curLev)) <= (stripWidth - itemWidth)){
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
                nextTopLev = i.getHeight() + nextTopLev;
            }
        }
        res.setFinalHeight(nextTopLev);    // die max. packungshöhe
	}

	private void calcNFDH(MyResult res) {

	}

	private void calcSLEA(MyResult res) {

	}

	private void calcBOLE(MyResult res) {

	}

	private void calcSTBG(MyResult res) {

	}
}
