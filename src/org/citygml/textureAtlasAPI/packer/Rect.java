/*******************************************************************************
 * This file is part of the Texture Atlas Generation Tool.
 * Copyright (c) 2010 - 2011
 * Institute for Geodesy and Geoinformation Science
 * Technische Universitaet Berlin, Germany
 * http://www.gis.tu-berlin.de/
 * 
 * The Texture Atlas Generation Tool is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * @author Babak Naderi <b.naderi@mailbox.tu-berlin.de>
 ******************************************************************************/
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
    int score1=Integer.MIN_VALUE;
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
