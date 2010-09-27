package org.citygml.TextureAtlasAPI.StripPacker;

/**
 * to do: change the id to integer value.
 * @author babak naderi
 *
 */
public class MyItem implements Comparable<MyItem>{
    public String id;
    public int  w;
    public int h;
    public int a; // area
    public int x;
    public int y;
    
    public Object level;
    
    /**
     * by babak
     */
    private Long surfaceID;
    private double[] coordinates;
    
    public void clear(){
    	id=null;
    	level=null;
    	coordinates=null;
    	surfaceID=null;
    }
  
    /**
	 * @return the surfaceID
	 */
	public Long getSurfaceID() {
		return surfaceID;
	}
	/**
	 * @param surfaceID the surfaceID to set
	 */
	public void setSurfaceID(Long surfaceID) {
		this.surfaceID = surfaceID;
	}
	/**
	 * @return the coordinates
	 */
	public double[] getCoordinates() {
		return coordinates;
	}
	/**
	 * @param coordinates the coordinates to set
	 */
	public void setCoordinates(double[] coordinates) {
		this.coordinates = coordinates;
	}
	public MyItem(String id, int width, int height) {
        this.id = new String(id);
        w = width;
        h = height;
        a = w*h;
    }
	
	/**
	 * by babak
	 * @param id
	 * @param width
	 * @param height
	 * @param surfaceID
	 * @param coordinates
	 */
	public MyItem(String id, int width, int height, Long surfaceID,double[] coordinates) {
       this(id,width,height);
       this.surfaceID=surfaceID;
       this.coordinates=coordinates;
    }
	
	
    public void setPOS(int x, int y, Object level){
    	this.x=x;
    	this.y=y;
    	this.level=level;
    }

    public void setYPos(int y){
    	this.y=y;
    }
    public String getId(){
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
    
    public Object getLevel(){
        return level;
    }

	public int compareTo(MyItem o) {
		return h < o.h ? -1 : h == o.h ? 0 : 1;
	}
    
}
