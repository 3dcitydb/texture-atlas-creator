package org.citygml.textureAtlasAPI.packer;

public class Rect implements Comparable<Rect> {

	public String id;
    public int width;
    public int height;
    public int area; 
    public int x;
    public int y;
    public boolean rotated=false;
    int score1=Integer.MAX_VALUE, score2=Integer.MAX_VALUE;
    
    public Integer level;
    
    public void clear(){
    	id=null;
    	level=null;
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
	
	public void setPOS(int x, int y, Integer level){
    	this.x=x;
    	this.y=y;
    	this.level=level;
    }

    public void setYPos(int y){
    	this.y=y;
    }
    public void setLevel(Integer l){
        level=l;
    }

    public String getURI(){
        return id;
    } 
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }
    
    public int getArea(){
        return area;
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

    public boolean equals(Object obj) {
    	if (obj instanceof Rect){
    		if (((Rect)obj).getURI()!=null&&((Rect)obj).getURI().equalsIgnoreCase(this.id))
    			return true;
    	}
		return false;
	}

	@Override
	public int compareTo(Rect o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
