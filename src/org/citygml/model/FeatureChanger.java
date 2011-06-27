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
package org.citygml.model;

import java.util.ArrayList;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.citygml.textureAtlasAPI.dataStructure.TexImageInfo4GMLFile;
import org.citygml.util.Logger;
import org.citygml4j.util.child.ChildInfo;
import org.citygml4j.factory.CityGMLFactory;
import org.citygml4j.model.citygml.appearance.Appearance;
import org.citygml4j.model.citygml.appearance.AppearanceProperty;
import org.citygml4j.model.citygml.appearance.ParameterizedTexture;
import org.citygml4j.model.citygml.appearance.SurfaceDataProperty;
import org.citygml4j.model.citygml.appearance.TexCoordList;
import org.citygml4j.model.citygml.appearance.TextureAssociation;
import org.citygml4j.model.citygml.appearance.TextureCoordinates;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.util.walker.FeatureWalker;
/**
 * It will modify the cityModel with new configuration of ParameterizedTexture.
 *
 */
public class FeatureChanger extends FeatureWalker {
	private Appearance appearance;
	private CityGMLFactory citygml;
	private Hashtable<String, Hashtable<Integer,TexImageInfo4GMLFile>> buildings;
	private Hashtable<Integer,TexImageInfo4GMLFile> building;
	ChildInfo ci = new ChildInfo();
	boolean isEmpty=false;
	// Temporarily
	
	public void set(Hashtable<String, Hashtable<Integer,TexImageInfo4GMLFile>> buildings, CityGMLFactory cityGML){
		this.citygml =cityGML;
		this.buildings = buildings;
	}
	

	
	@Override
	public void visit(Appearance arg0) {
		
		this.appearance = arg0;
		//		appearance.unsetSurfaceDataMember();
//		AbstractBuilding abstractBuilding= ci.getParentCityObject(arg0, AbstractBuilding.class);
		
		AbstractCityObject parentCityObject= ci.getParentCityObject(arg0);
		
		
//		if (buildings!=null&&abstractBuilding!=null)
//			building= buildings.get(abstractBuilding.getId());
//		else
//			super.visit(arg0);

		
		if (buildings!=null&&parentCityObject!=null)
			building= buildings.get(parentCityObject.getId());
		else // it should be a global appearance 
			building= buildings.get(GMLModifier.unknowBuildingID);
		
		if (building==null){
			super.visit(arg0);
			return;
		}
		
		TextureCoordinates tc ;
		TexCoordList tcl;
		TextureAssociation ta ;

		SurfaceDataProperty sdpt;
		ParameterizedTexture parameterizedTexture;
		TexImageInfo4GMLFile texAtlasGroup;
		HashMap<String, ParameterizedTexture> paramtexGroup;
		HashMap<String, TexCoordList> TexCoordListHashMap = new HashMap<String, TexCoordList>();
		Enumeration<TexImageInfo4GMLFile> texGroupEnum=building.elements();
		// each texture group will be a SurfaceDataMember&ParameterizedTexture
		while(texGroupEnum.hasMoreElements()){
			texAtlasGroup = texGroupEnum.nextElement();
			
			if (texAtlasGroup.getGeneralProp()==null){
				building.remove(texAtlasGroup);
				continue;
			}
			if(arg0.getTheme()!=null && texAtlasGroup.getGeneralProp().getAppearanceTheme()!=null)
				if (!texAtlasGroup.getGeneralProp().getAppearanceTheme().equalsIgnoreCase(arg0.getTheme()))
					continue;
		
				
			
			sdpt = citygml.createSurfaceDataProperty();
			
			paramtexGroup= new HashMap<String, ParameterizedTexture>();
			
			HashMap<Object, String> texImageURIS=texAtlasGroup.getTexImageURIs();
			HashMap<Object, String> texCoordinates=texAtlasGroup.getTexCoordinates();
			if (texImageURIS==null||texCoordinates==null)						
				continue;
			
			// each URI refer to a parameterizedTexture
			
			Iterator<String> URIS= texImageURIS.values().iterator();
			String URI;
			
			// make all parameterizedTexture and add all of them to appearance
			while(URIS.hasNext()){
				URI=URIS.next();
				if (paramtexGroup.containsKey(URI))
					continue;
				parameterizedTexture = citygml.createParameterizedTexture();
				parameterizedTexture.setImageURI(URI);
				parameterizedTexture.setId(appearance.getId());
				//TODO change it. it should set from image convertor.
				parameterizedTexture.setMimeType(getMIMEType(URI,texAtlasGroup.getGeneralProp().getMIMEType()));
				parameterizedTexture.setBorderColor(texAtlasGroup.getGeneralProp().getBoarderColor());
				parameterizedTexture.setWrapMode(texAtlasGroup.getGeneralProp().getWrapModeType());
				parameterizedTexture.setTextureType(texAtlasGroup.getGeneralProp().getTextureType());
				parameterizedTexture.setIsFront(texAtlasGroup.getGeneralProp().isFront());
				paramtexGroup.put(URI, parameterizedTexture);
				sdpt = citygml.createSurfaceDataProperty();
				sdpt.setSurfaceData(parameterizedTexture);
				appearance.addSurfaceDataMember(sdpt);
			}
			URIS=null;
			
			// pars all coordinates and make the object for corresponding parameterizedTexture.
			Iterator<Object> targetRingIte= texImageURIS.keySet().iterator();
			String targetRing;
			String[]tr;
			while(targetRingIte.hasNext()){
				targetRing= (String)targetRingIte.next();
				tr= targetRing.split(" ");
				
				tc = citygml.createTextureCoordinates();
				tc.setRing(tr[1]);
				tc.setValue(convertCoordinates(texCoordinates.get(targetRing)));
				// if another TexCoordList for this target is available use that. to support holes. 
				if((tcl=TexCoordListHashMap.get(tr[0]))!=null){
					tcl.addTextureCoordinates(tc);
					tr=null;
					targetRing=null;
					continue;
				}
				tcl = citygml.createTexCoordList();
				tcl.addTextureCoordinates(tc);
				
				ta = citygml.createTextureAssociation();
				ta.setUri(tr[0]);
				ta.setTextureParameterization(tcl);
				paramtexGroup.get(texImageURIS.get(targetRing)).addTarget(ta);
				TexCoordListHashMap.put(tr[0],tcl);
				tr=null;
				targetRing=null;
			}
			targetRingIte=null;
			texAtlasGroup.clear();
			TexCoordListHashMap.clear();
		}
		if(parentCityObject !=null && arg0.getSurfaceDataMember().isEmpty()){
			parentCityObject.unsetAppearance((AppearanceProperty)arg0.getParent());
		}
			
		super.visit(arg0);
	}
	
	private List<Double> convertCoordinates(String coordinates){
		if(coordinates==null)
			return null;
		ArrayList<Double> list= new ArrayList<Double>();
		String[] ls= coordinates.split(" ");
		for(int i=0;i<ls.length;i++){
			try{
			list.add(new Double(ls[i]));
			}catch(Exception e){
				if (Logger.SHOW_STACK_PRINT)
					e.printStackTrace();
			}
			ls[i]=null;
		}
			ls=null;
		
		return list;
	}
	
	private String getMIMEType(String URI,String generalMIMEType){
		if (URI.lastIndexOf(".jpeg")>0)
			return "image/jpeg";
		if (URI.lastIndexOf(".png")>0)
			return "image/png";
		return generalMIMEType;
	}


	
}
