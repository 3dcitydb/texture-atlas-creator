package org.citygml.textureAtlasAPI.packer;

/**
 * @author babak naderi
 *
 */
public class Rect extends AbstractRect{
  	Rect(AbstractRect r) {
		super(r);
		// TODO Auto-generated constructor stub
	}
  	Rect(String id, int width, int height) {
  		super(id,width,height);
  	}
  	Rect(){
  		super("",0,0);
  	}

	public int compareTo(AbstractRect o) {
		return area > o.area ? -1 : area == o.area ? 0 : 1;
		
	}
    
}
