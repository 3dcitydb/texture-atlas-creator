package org.citygml.textureAtlasAPI.packer;

/**
 * For packing algorithms each texture will represent as a object of this class.
 * As a result of packing process these variables should be set: x,y, level, and rotated.
 * Because of optimization SET/GET methods are neglected and variables defined in public domain.
 * 
 * @author babak
 *
 */
public class Rect {
	
	// it is equal to URI of texture
	public String id;
    public int width;
    public int height;
    public int area; 
    public int x;
    public int y;
    public boolean rotated=false;
    // In the case that packing algorithm score the current position of rectangle
    // score1 is in normal, and score2 is 90d rotated. 
    int score1=Integer.MAX_VALUE, score2=Integer.MAX_VALUE;
    // in the case that algorithms is level base
    public short level;
    
    public void clear(){
    	id=null;
    }
  
	public Rect(String URI, int width, int height) {
        this.id = new String(URI);
        this.width = width;
        this.height = height;
        this.area = width*height;
    }
	
	Rect(Rect r){
		this.x=r.x;
		this.y=r.y;
		this.id= r.id;
		this.width=r.width;
		this.height=r.height;
		this.score1=r.score1;
		this.score2=r.score2;
		this.rotated=r.rotated;
	}
	Rect(){
		 this.id = new String("");
	      this.width = 0;
	      this.height = 0;
	      this.area = 0;
  		
  	}
	
	public Rect clone(){
		Rect r = new Rect();
		r.x=this.x;
		return r;
	}
	
	public void rotate(){
		int tmp=width;
		width=height;
		height=tmp;
		rotated=true;
	}
	
	public static boolean isContainedIn(Rect a, Rect b){
		return a.x >= b.x && a.y >= b.y 
			&& a.x+a.width <= b.x+b.width 
			&& a.y+a.height <= b.y+b.height;
	}
	// setPOS 
	public void setPosition(int x, int y, short level){
    	this.x=x;
    	this.y=y;
    	this.level=level;
    }

    public String getURI(){
        return id;
    } 
    // id or URI of each textureatlas should be unique.
    public boolean equals(Object obj) {
    	if (obj instanceof Rect){
    		if (((Rect)obj).getURI()!=null&&((Rect)obj).getURI().equalsIgnoreCase(this.id))
    			return true;
    	}
		return false;
	}

	protected void finalize() throws Throwable {
		super.finalize();
		this.id=null;
	}
    
}
