package org.citygml.textureAtlasAPI.stripPacker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyResult {
	private Map <Integer,List<MyItem>> levelMap; 
	private int finalHeight = 0;
	private int stripWidth = 0;
    
    // Constructors
    public MyResult(){
    	levelMap=new HashMap<Integer,List<MyItem>>();
    }
    public MyResult(int imageWidth){
    	stripWidth= imageWidth;
    	levelMap=new HashMap<Integer,List<MyItem>>();
    }
    
    // set methods
    public void setFinalHeight(int h){
    	finalHeight=h;
    }
    //get methods
    public Map <Integer,List<MyItem>> getLevelMap(){
    	return levelMap;
    }
    public int getFinalHeight(){
    	return finalHeight;
    }
    public int getWidth(){
    	return stripWidth;
    }
    // add Items
    public boolean addItem(MyItem item){
    	Integer level = item.getLevel();
        if (!levelMap.containsKey(level)){
            levelMap.put(level, new ArrayList<MyItem>());
        }
        return levelMap.get(level).add(item);
    }
    
    public ArrayList<MyItem> getAllItems(){
    	ArrayList<MyItem> all = new ArrayList<MyItem>();
 
    	List<Integer> sortedKeys=new ArrayList<Integer>(this.levelMap.keySet());
    	Collections.sort(sortedKeys);
    	
    	Iterator<Integer> lIter = sortedKeys.iterator();
        while (lIter.hasNext()){
            Integer level = (Integer) lIter.next();
            all.addAll(levelMap.get(level));            
        }
        lIter=null;
        sortedKeys=null;
    	return all;
    }
    /**
    // to check output **** just for test
    public String toString(){
    	 StringBuffer str = new StringBuffer("< max value=\"" + finalHeight + "\">\n");
         Iterator lIter = this.levelMap.keySet().iterator();
         while (lIter.hasNext()){
        	 Integer level = (Integer) lIter.next();
             str.append("\t<level id=\"" + level.intValue() + "\">\n");
             Iterator <MyItem> iIter = levelMap.get(level).iterator();
             while (iIter.hasNext()){
            	 MyItem item = iIter.next();
                 str.append("\t\t<item id=\"" + item.getURI() + "\" x=\"" + item.getXPos() + "\" y=\"" + item.getYPos() + "\" />\n");
             }
             str.append("\t</level>\n");
         }
         str.append("</algorithm>\n");
         return str.toString();
    }**/
    
}
