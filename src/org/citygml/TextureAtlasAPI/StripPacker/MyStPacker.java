package org.citygml.TextureAtlasAPI.StripPacker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


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

	public boolean addItem(String URI, int width, int height) {
		return items.add(new MyItem(URI, width, height));
	}
	
	public boolean addItem(MyItem mi){
		return items.add(mi);
	}
	public boolean removeItem(String URI){
		return items.remove(new MyItem(URI,0,0));
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
        int nextTopLev = 0;       // der n�chste level wird bei dieser h�he beginnen

        Collections.sort(items);    //TODO: unpassende stelle, da die items bei jedem aufruf einer calc-methode neu sortiert werden
        Collections.reverse(items);     //da sort() aufsteigend sortiert
        Iterator <MyItem> iter = items.iterator();

        levels.add(new Integer(0));    // die starth�hen der jeweiligen level
        levelFill.add(new Integer(0)); // die f�llst�nde der level

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
            for (curLev = 0; curLev <= topLev; curLev++){   // versuche das item in einen m�glichst niedrigen level zu packen
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
                res.addItem(i);
                nextTopLev = i.getHeight() + nextTopLev;
            }
        }
        res.setFinalHeight(nextTopLev);    // die max. packungsh�he
	}

	private void calcNFDH(MyResult res) {
	     Integer levelCount = new Integer(0);
	        int levelFill = 0;     // die bereits ausgef�llte breite des strips auf dem akt. level
	        int currLevel = 0;     // die untere grenze des akt. levels
	        int nextLevel = 0;     // die untere grenze des n�chsten levels

	        Collections.sort(items);    //TODO: woanders hinstecken
	        Collections.reverse(items);     //da sort() aufsteigend sortiert
	        Iterator <MyItem> iter = items.iterator();

	        if (iter.hasNext()){    // das erste item passt immer, da der strip leer ist und die items nicht breiter als der strip sind
	            MyItem i = iter.next();
	            i.setPOS(levelFill, currLevel, levelCount);
	            res.addItem(i);
	            levelFill = i.getWidth();
	            nextLevel = i.getHeight();
	        }
	        while (iter.hasNext()){
	            MyItem i = iter.next();
	            if (levelFill + i.getWidth() > stripWidth){   // das neue item ist zu breit f�r den level
	                levelCount = levelCount + 1;
	                levelFill = 0;
	                currLevel = nextLevel;
	                nextLevel = currLevel + i.getHeight();
	            }
	            i.setPOS(levelFill, currLevel, levelCount);
	            res.addItem(i);
	            levelFill = levelFill + i.getWidth();
	        }
	        res.setFinalHeight(nextLevel);
	}

	private void calcSLEA(MyResult res) {
		 LinkedList <MyItem> smallerItems = new LinkedList();
	        Iterator <MyItem> iter = items.iterator();
	        int nextX = 0, nextY = 0;
	        Integer level = new Integer(0);
	        
	        while (iter.hasNext()){     // grundlinie, d.h. die breiten items
	        	MyItem item = iter.next();
	            int iWidth = item.getWidth();
	            if (iWidth > (0.5 * stripWidth)){  // es wird nat�rlich vorausgesetzt, dass 0 <= iWidth <= stripWidth gilt
	            	item.setPOS(nextX, nextY, level);
	                res.addItem(item);
	                nextY= nextY + item.getHeight();
	            } else {
	                smallerItems.add(item);
	            }
	        }
	        res.setFinalHeight(nextY);
	        
	        Collections.sort(smallerItems); //TODO: war vielleicht eh schon sortiert (wahr, wenn, die anderen TODOs erledigt sind)
	        Collections.reverse(smallerItems);
	        
	        iter = smallerItems.iterator();
	        if (iter.hasNext()){
	            MyItem item = iter.next();
	            level++;
	            int nextLeftLevel = nextY + item.getHeight();    // entspr. h0 + h1
	            assert nextX == 0;
	            item.setPOS(nextX, nextY, level);
	            res.addItem(item);    // erstes item auf h0
	            nextX = item.getWidth();
	            while (iter.hasNext() && (nextX < 0.5 * stripWidth)){    // auf h0 bis min. halbe breite f�llen
	                item = iter.next();
	                item.setPOS(nextX, nextY, level);
	                res.addItem(item);
	                nextX = nextX + item.getWidth();
	            }
	            int nextRightLevel = nextY + item.getHeight();   // entspr. h0 + d1
	            while (iter.hasNext()){
	                item = iter.next();
	                int width = item.getWidth();
	                if (nextX < (0.5 * stripWidth) && (nextX + width) >= (0.5 * stripWidth)){   // wahr, wenn das item zu breit f�r die akt. linke Spalte ist
	                    nextX = stripWidth/2;
	                    nextY = nextRightLevel;
	                    nextRightLevel = nextY + item.getHeight();
	                    level++;
	                } else if (nextX >= (0.5 * stripWidth) && (nextX + width) >= stripWidth){   //     ""   ... rechte Spalte
	                    nextX = 0;
	                    nextY = nextLeftLevel;
	                    nextLeftLevel = nextY + item.getHeight();
	                    level++;
	                }
	                item.setPOS(nextX, nextY, level);
	                res.addItem(item);
	                nextX = nextX + item.getWidth();
	            }
	            res.setFinalHeight((nextRightLevel > nextLeftLevel) ? nextRightLevel : nextLeftLevel);
	        }
	}

	private void calcBOLE(MyResult res) {
			Stack <Integer> top = new Stack<Integer>();   // die beiden stacks h�ngen zusammen (immer beide zugleich push()en und pop()en)
	        Stack <Integer> right = new Stack<Integer>(); // das sind die 'top-right-corner' der obersten, linken items
	        top.push(Integer.MAX_VALUE);
	        right.push(0);
	        
	        Collections.sort(items);    //TODO: unpassende stelle, da die items bei jedem aufruf einer calc-methode neu sortiert werden
	        Collections.reverse(items);     //da sort() aufsteigend sortiert
	        Iterator <MyItem> iter = items.iterator();

	        int x = 0, y = 0;
	        while (iter.hasNext()){
	        	MyItem item = iter.next();
	            if (stripWidth - x >= item.w){
	                if (top.peek() != y+item.h){    // anderenfalls: (eh uninteressant + w�rde aufw�ndige sonderbehandlung ben�tigen)
	                    right.push(x+item.w);
	                    top.push(y+item.h);    //wg. der vorherigen sortierung werden die oberen, rechten ecken der items mit absteigendem y+item.h abgelegt
	                } else {
	                    right.pop();
	                    right.push(x+item.w);
	                }
	            } else {    // das item passt nicht mehr auf diesen 'level'
	                try {
	                    y = top.peek();     // das akt. item wird mindestens auf h�he der oberkante des letzten items eingef�gt
	                    while (stripWidth - right.peek() < item.w){  // hier darf der stack nicht unerwartet leer laufen - und sollte es auch nicht
	                        right.pop();
	                        y=top.pop();
	                    }
	                    x=right.peek();
	                    int nextTop, nextRight;
	                    do {    // entferne alle BL-Punkte, die unterhalb od. gleich der aktuellen(neuen) grundlinie liegen
	                        nextTop = top.pop();
	                        nextRight = right.pop();
	                    } while(nextTop <= y + item.h);
	                    top.push(nextTop);
	                    right.push(nextRight);
	                    top.push(y + item.h);
	                    right.push(x + item.w);
	                    
	                } catch (EmptyStackException e) {
	                    System.err.println("irgendwie ists doch passiert ... : " + e);
	                }              // das d�rfte eh nicht passieren ...
	            }
	            item.setPOS(x, y, "0");
	            res.addItem(item);
	            x = x + item.w;
	        }
	        res.setFinalHeight(top.elementAt(1));
	}

	private void calcSTBG(MyResult res) {/*
		 // die listen enthalten die (teil-)listen von items nach breite, h�he und fl�che sortiert
        Stack <LinkedList<MyItem>> wsLists = new Stack();
        Stack <LinkedList<MyItem>> hsLists = new Stack();
        Stack <LinkedList<MyItem>> asLists = new Stack();
        Stack <Rectangle2D.Double> qStack = new Stack();
        Stack <Integer> SLStack = new Stack();
        
        
        
        addSortedLists(items, wsLists, hsLists, asLists);
        {   // damit die variablenamen nur hier g�ltig sind
            //bestimme (minimale) h�he des initialen Q
            int SL = 0;
            Iterator <MyItem> it = items.iterator();
            while (it.hasNext()){
                SL += it.next().a;
            }
            SLStack.push(SL);
            MyItem wi = wsLists.peek().getFirst();   //item mit der gr��ten breite
            MyItem hi = hsLists.peek().getFirst();   //item mit der gr��ten h�he
            MyItem ai = asLists.peek().getFirst();    //item mit der gr��ten fl�che
            int h =(int)( 2*SL + Math.max((2-1/wi.w),0.0) * Math.max((hi.h-SL),0.0));
            h = Math.max(hi.h, h);
            // damit ist automatisch die bedingung (1.2) aus theorem 1.1 erf�llt (ungepr�fte voraussetzung: kein item ist breiter als der strip)
            qStack.push(new Rectangle2D.Double(0,0,stripWidth,h));    // initiales Q
            res.setFinalHeight(h);   // die packungsh�he ist ja bereits bekannt ...
        }
        
        while(!qStack.isEmpty()){
            Rectangle2D.Double q = qStack.pop();    //dieses elem. gibt es wg. der while-bedingung immer (und damit auch die anderen)
            int SL = SLStack.pop();
            LinkedList <MyItem> wSorted = wsLists.pop();
            LinkedList <MyItem> hSorted = hsLists.pop();
            LinkedList <MyItem> aSorted = asLists.pop();
            Integer aL = wSorted.getFirst().w, bL = hSorted.getFirst().h;
            
            System.err.println(q);
            
            if (aL >= 0.5*q.width){ // (C1)
                Point2D.Double nextPos = new Point2D.Double(q.x, q.y);  // untere linke ecke f das n�chste item
                do {
                	MyItem it = wSorted.remove(); //erstes element der liste entfernen (das n�chstbreiteste)
                    hSorted.remove(it);     // die anders geordneten listen 'synchronisieren'
                    aSorted.remove(it);
                    SL -= it.a;
                    it.setPOS((int)nextPos.x, (int)nextPos.y, new Integer(0));
                    res.addItem(it);
                    nextPos.y = nextPos.y + it.h;
                } while (!wSorted.isEmpty() && wSorted.getFirst().w >= 0.5*q.width);
                if (!wSorted.isEmpty()){    // der fall 'm < l'; sonst: fertig
                    int vb =(int) (q.height-(nextPos.y-q.y));  // entspricht v'
                    if (hSorted.getFirst().h <= vb){    // der fall "bt1 <= v'"
                        wsLists.push(wSorted);
                        hsLists.push(hSorted);
                        asLists.push(aSorted);
                        q.height = q.height + q.y - nextPos.y;  // das neue Q'
                        q.y = nextPos.y;
                        qStack.push(q);
                        SLStack.push(SL);
                    } else {
                        Point2D.Double nextPos2 = new Point2D.Double(q.x + q.width, q.y + q.height);    // obere rechte ecke f das n�chte item
                        do {
                            MyItem it = hSorted.remove();
                            wSorted.remove(it);
                            aSorted.remove(it);
                            SL -= it.a;
                            it.setPOS((int)nextPos2.x-it.w, (int)nextPos2.y-it.h, new Integer(0));
                            res.addItem(it);
                            nextPos2.x = nextPos2.x - it.w;
                        } while(!hSorted.isEmpty() && hSorted.getFirst().h > vb);
                        if (!hSorted.isEmpty()){    // der fall 'n <= l-m'
                            wsLists.push(wSorted);
                            hsLists.push(hSorted);
                            asLists.push(aSorted);
                            q.height = q.height + q.y - nextPos.y;  // das neue Q'
                            q.y = nextPos.y;
                            q.width = nextPos2.x - q.x;
                            qStack.push(q);
                            SLStack.push(SL);
                        }
                    }
                }
            } else if (bL >= 0.5*q.height){  // (C-1)
                Point2D.Double nextPos = new Point2D.Double(q.x, q.y);  // untere linke ecke f das n�chste item
                do {
                    MyItem it = hSorted.remove(); //erstes element der liste entfernen (das n�chsth�chste)
                    wSorted.remove(it);     // die anders geordneten listen 'synchronisieren'
                    aSorted.remove(it);
                    SL -= it.a;
                    it.setPOS((int)nextPos.x, (int)nextPos.y, new Integer(0));
                    res.addItem(it);
                    nextPos.y = nextPos.x + it.w;
                } while (!hSorted.isEmpty() && hSorted.getFirst().h >= 0.5*q.height);
                if (!hSorted.isEmpty()){    // der fall 'm < l'; sonst: fertig
                    double ub = q.width - (nextPos.x - q.x);  // entspricht u'
                    if (wSorted.getFirst().w <= ub){    // der fall "al <= u'"
                        wsLists.push(wSorted);
                        hsLists.push(hSorted);
                        asLists.push(aSorted);
                        q.width = q.width + q.x - nextPos.x;  // das neue Q'
                        q.x = nextPos.x;
                        qStack.push(q);
                        SLStack.push(SL);
                    } else {
                        Point2D.Double nextPos2 = new Point2D.Double(q.x + q.width, q.y + q.height);    // obere rechte ecke f das n�chte item
                        do {
                            MyItem it = wSorted.remove();
                            hSorted.remove(it);
                            aSorted.remove(it);
                            SL -= it.a;
                            res.addItem(new ItemWithPos(it, nextPos2.x-it.w, nextPos2.y-it.h, new Integer(0)));
                            nextPos2.y = nextPos2.y - it.h;
                        } while(!wSorted.isEmpty() && wSorted.getFirst().w > ub);
                        if (!wSorted.isEmpty()){    // der fall 'n <= l-m'
                            wsLists.push(wSorted);
                            hsLists.push(hSorted);
                            asLists.push(aSorted);
                            q.width = q.width + q.x - nextPos.x;  // das neue Q'
                            q.x = nextPos.x;
                            q.height = nextPos2.y - q.y;
                            qStack.push(q);
                            SLStack.push(SL);
                        }
                    }
                }
            
            } else {    //  alle anderen f�lle; d.h. aL<=.5*u und bL<=.5*v
                double sum = 0.0;
                double Z = 0.0;
                int m = 0;
                for (ListIterator<Item> iter = wSorted.listIterator();iter.hasNext();sum += iter.next().a){
                    m = iter.nextIndex();
                    if (wSorted.get(m).w <= .25*q.width && m > 1){
                        Z = sum;
                        m--;
                        break;
                    }
                }
                if (Z != 0.0 && SL-.25*q.width*q.height <= Z && Z <= .375*q.width*q.height){   // (C3)
                    double u1 = Math.max(.5*q.width, 2*Z/q.height);
                    qStack.push(new Rectangle2D.Double(q.x, q.y, u1, q.height));
                    SLStack.push(Z);
                    addSortedLists(wSorted.subList(0, m+1), wsLists, hsLists, asLists, true, false);
                    q.x += u1;
                    q.width -= u1;
                    qStack.push(q);
                    SLStack.push(SL-Z);
                    addSortedLists(wSorted.subList(m+1, wSorted.size()), wsLists, hsLists, asLists, true, false);
                } else {
                    double sum2 = 0.0;
                    double Z2 = 0.0;
                    int n = 0;
                    for (ListIterator<Item> iter = hSorted.listIterator();iter.hasNext();sum2 += iter.next().a){
                        n = iter.nextIndex();
                        if (hSorted.get(n).h <= .25*q.height && n > 1){
                            Z2 = sum2;
                            n--;
                            break;
                        }
                    }
                    if (Z2 != 0.0 && SL-.25*q.width*q.height <= Z2 && Z2 <= .375*q.width*q.height){    // (C-3)
                        double v1 = Math.max(.5*q.height, 2*Z2/q.width);
                        qStack.push(new Rectangle2D.Double(q.x, q.y, q.width, v1));
                        SLStack.push(Z2);
                        addSortedLists(hSorted.subList(0, n+1), wsLists, hsLists, asLists, false, true);
                        q.y += v1;
                        q.height -= v1;
                        qStack.push(q);
                        SLStack.push(SL-Z2);
                        addSortedLists(hSorted.subList(n+1, hSorted.size()), wsLists, hsLists, asLists, false, true);
                    } else {    // (C2), (C-2) und (C0)
                        int i = 0, j = 0;
                        Item ii = null, ij = null, ik;
                        double u = q.width, v = q.height, uv = u*v;
                        int siz = aSorted.size();
                        boolean found = false;
                        blubb:
                        for (i = 0; i < siz 
                                && (ii = aSorted.get(i)).a >= .0625*uv
                                && ii.w >= .25*u && ii.h >= .25*v; i++){
                            for (j = i+1; j < siz
                                    && (ij = aSorted.get(j)).a >= .0625*uv
                                    && ij.w >= .25*u && ij.h >= .25*v; j++){
                                ik = ii.w >= ij.w ? ij : ii;    // "swap" so dass ii.w >= ij.w gilt
                                ii = ii.w >= ij.w ? ii : ij;
                                ij = ii.w >= ij.w ? ij : ik;
                                if (2*(SL - ij.a - ii.a) <= uv - ii.w*v){   //  (C2)
                                    res.addItem(new ItemWithPos(ii, q.x, q.y, new Integer(0)));
                                    res.addItem(new ItemWithPos(ij, q.x, q.y + ii.h, new Integer(0)));
                                    q.x += ii.w;
                                    q.width -= ii.w;
                                    found = true;
                                    break blubb;
                                }
                                ik = ii.h >= ij.h ? ij : ii;    // "swap" so dass ii.h >= ij.h gilt
                                ii = ii.h >= ij.h ? ii : ij;
                                ij = ii.h >= ij.h ? ij : ik;
                                if (2*(SL - ij.a - ii.a) <= uv - ii.h*u){   //  (C-2)
                                    res.addItem(new ItemWithPos(ii, q.x, q.y, new Integer(0)));
                                    res.addItem(new ItemWithPos(ij, q.x + ii.w, q.y, new Integer(0)));
                                    q.y += ii.h;
                                    q.height -= ii.h;
                                    found = true;
                                    break blubb;
                                }
                            }
                        }
                        if (found == true){ 
                            if (siz != 2) {
                                qStack.push(q);
                                SLStack.push(SL-ii.a-ij.a);
                                aSorted.remove(i);
                                aSorted.remove(j);
                                asLists.push(aSorted);
                                wSorted.remove(ii);
                                wSorted.remove(ij);
                                wsLists.push(wSorted);
                                hSorted.remove(ii);
                                hSorted.remove(ij);
                                hsLists.push(hSorted);
                            }
                        } else if (SL-0.25*q.width*q.height <= aSorted.getFirst().a){   // bleibt eigentlich nur noch (C0)
                            Item it = aSorted.remove();
                            hSorted.remove(it);
                            wSorted.remove(it);
                            res.addItem(new ItemWithPos(it, q.x, q.y, new Integer(0)));
                            if (!wSorted.isEmpty()){
                                wsLists.push(wSorted);
                                hsLists.push(hSorted);
                                asLists.push(aSorted);
                                q.x += it.w;
                                q.width -= it.w;
                                qStack.push(q);
                                SLStack.push(SL-it.a);
                            }
                        } else {
                            throw new Exception("Steinberg: kein (C) ist eingetreten ...");
                        }
                    }
                }
            }
        }*/
	}
	  private void addSortedLists(List <MyItem> src, Stack <LinkedList<MyItem>> wsLists,
	            Stack <LinkedList<MyItem>> hsLists, Stack <LinkedList<MyItem>> asLists) {
	            addSortedLists(src, wsLists, hsLists, asLists, false, false);
	    }
	    
	    private void addSortedLists(List <MyItem> src, Stack <LinkedList<MyItem>> wsLists,
	            Stack <LinkedList<MyItem>> hsLists, Stack <LinkedList<MyItem>> asLists, boolean isWSorted, boolean isHSorted) {
	        wsLists.push(new LinkedList(src));
	        if (isWSorted == false){
	            Collections.sort(wsLists.peek(), new Comparator<MyItem>(){   // nach breite sortiert
	                public int compare(MyItem o1, MyItem o2) { 
	                    return o1.w > o2.w ? -1 : o1.w == o2.w ? 0 : 1;}});
	        }

	        hsLists.push(new LinkedList(src));
	        if (isHSorted == false){
	            Collections.sort(hsLists.peek());    // nach h�he sortieren
	            Collections.reverse(hsLists.peek());
	        }
	        
	        asLists.push(new LinkedList(src));
	        Collections.sort(asLists.peek(), new Comparator<MyItem>(){    // nach fl�che sortiert
	            public int compare(MyItem o1, MyItem o2){
	                return (o1.a) > (o2.a) ? -1 : (o1.a) == (o2.a) ? 0 : 1;}});
	    }
}
