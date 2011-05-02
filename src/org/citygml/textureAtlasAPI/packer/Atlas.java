package org.citygml.textureAtlasAPI.packer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Atlas {
	private Map <Integer,List<AbstractRect>> levelMap; 
	private int bindingBoxHeight = 0;
	private int bindingBoxWidth = 0;
	private boolean fourChanel=false;
    


	// Constructors
    public Atlas(){
    	levelMap=new HashMap<Integer,List<AbstractRect>>();
    }
    public boolean isFourChanel() {
		return fourChanel;
	}

	public void setFourChanel(boolean fourChanel) {
		this.fourChanel = fourChanel;
	}      
    // set methods
    public void setBindingBox(int w,int h){
    	bindingBoxHeight=h;
    	bindingBoxWidth=w;
    }
    public void setBindingBoxWidth(int w){
    	bindingBoxWidth=w;
    }
    public void setBindingBoxHeight(int h){
    	bindingBoxHeight=h;
    }

    public int getBindingBoxHeight(){
    	return bindingBoxHeight;
    }
    public int getBindingBoxWidth(){
    	return bindingBoxWidth;
    }

    //get methods
    public Map <Integer,List<AbstractRect>> getLevelMap(){
    	return levelMap;
    }
    // add Items
    public boolean addItem(AbstractRect item){
    	Integer level = item.getLevel();
        if (!levelMap.containsKey(level)){
            levelMap.put(level, new ArrayList<AbstractRect>());
        }
        return levelMap.get(level).add(item);
    }
    
    public ArrayList<AbstractRect> getAllItems(){
    	ArrayList<AbstractRect> all = new ArrayList<AbstractRect>();
 
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
