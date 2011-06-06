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
package org.citygml.textureAtlasAPI.dataStructure;

public enum ErrorTypes {
	IMAGE_FORMAT_NOT_SUPPORTED("IMAGE_FORMAT_NOT_SUPPORTED"),
	// Texture is shared between several targets, and at least one of them are not combinable.  
	TARGET_PT_NOT_SUPPORTED("TARGET_PT_NOT_SUPPORTED"),
	ERROR_IN_COORDINATES("Wrapping coordinates"),
	IMAGE_IS_NOT_AVAILABLE("Image file/path is not valid"),
	IMAGE_UNBONDED_SIZE("IMAGE_UNBONDED_SIZE");
	String name;
	ErrorTypes(String name){
		this.name=name;
	}
	public String toString(){
		return name;
	}
	}
