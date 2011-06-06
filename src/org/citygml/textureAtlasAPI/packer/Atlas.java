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
