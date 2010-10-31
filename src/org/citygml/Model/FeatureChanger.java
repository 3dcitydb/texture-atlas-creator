package org.citygml.Model;

import java.util.ArrayList;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.citygml.TextureAtlasAPI.DataStructure.TexImageInfo4GMLFile;
import org.citygml4j.builder.copy.DeepCopyBuilder;
import org.citygml4j.factory.CityGMLFactory;
import org.citygml4j.model.citygml.appearance.Appearance;
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
	
	
	int pcc = 0;
	
	public void set(Hashtable<String, Hashtable<Integer,TexImageInfo4GMLFile>> buildings, CityGMLFactory cityGML){
		this.citygml =cityGML;
		this.buildings = buildings;
		pcc=0;
	}
	
	
	
	@Override
	public void accept(AbstractBuilding arg0) {
		// TODO Auto-generated method stub
		building= buildings.get(arg0.getId());
		pcc=0;
		super.accept(arg0);
	}

	
	@Override
	public void accept(Appearance arg0) {
		this.appearance = arg0;		
		appearance.unsetSurfaceDataMember();

		TextureCoordinates tc ;
		TexCoordList tcl;
		TextureAssociation ta ;
		
		SurfaceDataProperty sdpt;
		ParameterizedTexture parameterizedTexture;
		TexImageInfo4GMLFile texAtlasGroup;
		HashMap<String, ParameterizedTexture> paramtexGroup;
		Enumeration<TexImageInfo4GMLFile> texGroupEnum=building.elements();
		// each texture group will be a sSurfaceDaraMember&ParameterizedTexture
		while(texGroupEnum.hasMoreElements()){
			texAtlasGroup = texGroupEnum.nextElement();
			if(arg0.getId()!=null)
				if (!texAtlasGroup.getGeneralProp().getAppearanceID().equalsIgnoreCase(arg0.getId()))
					continue;
				
			
			sdpt = citygml.createSurfaceDataProperty();
			
			paramtexGroup= new HashMap<String, ParameterizedTexture>();
			
			HashMap<Object, String> texImageURIS=texAtlasGroup.getTexImageURIs();
			HashMap<Object, String> texCoordinates=texAtlasGroup.getTexCoordinates();
			
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
				parameterizedTexture.setMimeType(texAtlasGroup.getGeneralProp().getMIMEType());
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
				
				tcl = citygml.createTexCoordList();
				tcl.addTextureCoordinates(tc);
				
				ta = citygml.createTextureAssociation();
				ta.setUri(tr[0]);
				ta.setTextureParameterization(tcl);
				paramtexGroup.get(texImageURIS.get(targetRing)).addTarget(ta);
				tr=null;
				targetRing=null;
			}
			targetRingIte=null;
			texAtlasGroup.clear();
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
