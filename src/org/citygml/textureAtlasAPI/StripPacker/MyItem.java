package org.citygml.textureAtlasAPI.stripPacker;


/**
 * to do: change the id to integer value.
 * @author babak naderi
 *
 */
public class MyItem implements Comparable<MyItem>{
    @Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
    	if (obj instanceof MyItem){
    		if (((MyItem)obj).getURI()!=null&&((MyItem)obj).getURI().equalsIgnoreCase(this.id))
    			return true;
    	}
		return false;
	}

	public String id;
    public int  w;
    public int h;
    public int a; // area
    public int x;
    public int y;
    
    public Integer level;
//    public String URI;
//    private ArrayList<Object> surfaceIDList;
//    private ArrayList<double[]> coordinateList;
//    private boolean suitable;
    
    public void clear(){
    	id=null;
    	level=null;
    }
  
	public MyItem(String URI, int width, int height) {
        this.id = new String(URI);
        w = width;
        h = height;
//        surfaceIDList= new ArrayList<Object>();
//        surfaceIDList.add(surfaceID);
        a = w*h;
//        this.URI=URI;
//        suitable=true;
    }
	
//	public void addSurface(Object surfaceID){
//		surfaceIDList.add(surfaceID);
//	}
//	public ArrayList<Object> getSurfaceIDList(){
//		return surfaceIDList;
//	}
//	
//    public boolean isSuitable() {
//		return suitable;
//	}
//
//	public void setSuitable(boolean suitable) {
//		this.suitable = suitable;
//	}

	public void setPOS(int x, int y, Integer level){
    	this.x=x;
    	this.y=y;
    	this.level=level;
    }

    public void setYPos(int y){
    	this.y=y;
    }
    public String getURI(){
        return id;
    }
    
    
    public int getWidth(){
        return w;
    }
    
    public int getHeight(){
        return h;
    }
    
    public int getArea(){
        return a;
    }
    public int getXPos(){
        return x;
    }
    
    public int getYPos(){
        return y;
    }
    
    public Integer getLevel(){
        return level;
    }

	public int compareTo(MyItem o) {
		return h < o.h ? -1 : h == o.h ? 0 : 1;
	}
    
}
