package org.citygml.Model;

import java.util.ArrayList;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.citygml.textureAtlasAPI.dataStructure.TexImageInfo4GMLFile;
import org.citygml4j.commons.child.ChildInfo;
import org.citygml4j.factory.CityGMLFactory;
import org.citygml4j.model.citygml.appearance.Appearance;
import org.citygml4j.model.citygml.appearance.AppearanceProperty;
import org.citygml4j.model.citygml.appearance.ParameterizedTexture;
import org.citygml4j.model.citygml.appearance.SurfaceDataProperty;
import org.citygml4j.model.citygml.appearance.TexCoordList;
import org.citygml4j.model.citygml.appearance.TextureAssociation;
import org.citygml4j.model.citygml.appearance.TextureCoordinates;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.visitor.walker.FeatureWalker;
/**
 * it will clear the input data structure
 * @author babak naderi
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
	public void accept(Appearance arg0) {
		
		this.appearance = arg0;
		//		appearance.unsetSurfaceDataMember();
		AbstractBuilding abstractBuilding= ci.getParentCityObject(arg0, AbstractBuilding.class);
		if (buildings!=null)
			building= buildings.get(abstractBuilding.getId());
		else
			super.accept(arg0);
		
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
			
			if(arg0.getTheme()!=null)
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
		if(arg0.getSurfaceDataMember().isEmpty()){
			abstractBuilding.unsetAppearance((AppearanceProperty)arg0.getParent());
		}
			
		super.accept(arg0);
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
	/**
	@Override
	public void accept(ParameterizedTexture parameterizedTexture) {
		
		if (pcc == 0) {
			
			appearance.unsetSurfaceDataMember();
			parameterizedTexture.unsetTarget();
			
			
			// write all data
			TextureCoordinates tc ;
			TexCoordList tcl;
			TextureAssociation ta ;
			
			SurfaceDataProperty sdpt = citygml
			.createSurfaceDataProperty();
			
			// for all textures
			SimpleSurfaceDataMember simpleSDM=null;
			Iterator<SimpleSurfaceDataMember> data = currentBuildingData
					.iterator();
		
			String imgURL=null;
			String mimeType=null;
			
			
			while (data.hasNext()) {
				
				simpleSDM = (SimpleSurfaceDataMember) data.next();
				if (imgURL==null){
					imgURL=simpleSDM.getImageURI();
					mimeType=simpleSDM.getImageMIMEType();
				}
				
				if (!imgURL.equals(simpleSDM.getImageURI())){
					parameterizedTexture.setImageURI(simpleSDM==null?"":imgURL);
					parameterizedTexture.setMimeType(simpleSDM==null?"":mimeType);					
					sdpt.setSurfaceData(parameterizedTexture);
					appearance.addSurfaceDataMember(sdpt);

					parameterizedTexture = citygml.createParameterizedTexture();
					sdpt=citygml.createSurfaceDataProperty();
					
					imgURL=simpleSDM.getImageURI();
					mimeType=simpleSDM.getImageMIMEType();					
				}
								
				tc = citygml.createTextureCoordinates();
				tc.setRing(simpleSDM.getRing());
				tc.setValue(simpleSDM.getDoubleCoordinates());
				
				tcl = citygml.createTexCoordList();
				tcl.addTextureCoordinates(tc);
				
				ta = citygml.createTextureAssociation();
				ta.setUri(simpleSDM.getTargetURI());
				ta.setTextureParameterization(tcl);
				
				
				parameterizedTexture.addTarget(ta);
				
			}
			parameterizedTexture.setImageURI(simpleSDM==null?"":imgURL);
			parameterizedTexture.setMimeType(simpleSDM==null?"":mimeType);
			
			sdpt.setSurfaceData(parameterizedTexture);
			
			
			appearance.addSurfaceDataMember(sdpt);
			pcc++;
		}
		super.accept(parameterizedTexture);
	}**/

	
}
