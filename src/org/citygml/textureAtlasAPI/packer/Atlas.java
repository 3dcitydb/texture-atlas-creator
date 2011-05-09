package org.citygml.textureAtlasAPI.packer;

import java.util.ArrayList;


public class Atlas { 
	private ArrayList<Rect> allData;
	private int bindingBoxHeight = 0;
	private int bindingBoxWidth = 0;
	private boolean fourChanel=false;
    
    public Atlas(){
    	allData=new ArrayList<Rect>();
    }
    public boolean isFourChanel() {
		return fourChanel;
	}

	public void setFourChanel(boolean fourChanel) {
		this.fourChanel = fourChanel;
	}      

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

    public void addRect(Rect rect){
    	allData.add(rect);
    }
    
    public ArrayList<Rect> getAllItems(){
       
    	return allData;
    }
    
}
