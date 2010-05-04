package org.citygml.Model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.citygml.Model.DataStructures.SimpleSurfaceDataMember;
import org.citygml4j.factory.CityGMLFactory;
import org.citygml4j.model.citygml.appearance.Appearance;
import org.citygml4j.model.citygml.appearance.ParameterizedTexture;
import org.citygml4j.model.citygml.appearance.SurfaceDataProperty;
import org.citygml4j.model.citygml.appearance.TexCoordList;
import org.citygml4j.model.citygml.appearance.TextureAssociation;
import org.citygml4j.model.citygml.appearance.TextureCoordinates;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.visitor.walker.FeatureWalker;

public class FeatureChanger extends FeatureWalker {
	private Appearance appearance;
	private CityGMLFactory citygml;
	private Hashtable<String, ArrayList<SimpleSurfaceDataMember>> buildingsSurfaceData;
	private ArrayList<SimpleSurfaceDataMember> currentBuildingData;
	
	int pcc = 0;
	
	public void set(Hashtable<String, ArrayList<SimpleSurfaceDataMember>> buildingsSurfaceData, CityGMLFactory cityGML){
		this.citygml =cityGML;
		this.buildingsSurfaceData = buildingsSurfaceData;
		pcc=0;
	}
	
	
	
	@Override
	public void accept(AbstractBuilding arg0) {
		// TODO Auto-generated method stub
		currentBuildingData= buildingsSurfaceData.get(arg0.getId());
		pcc=0;
		super.accept(arg0);
	}

	
	@Override
	public void accept(Appearance arg0) {
		this.appearance = arg0;
		super.accept(arg0);
	}

	
	@Override
	public void accept(ParameterizedTexture parameterizedTexture) {
		
		if (pcc == 0) {
			appearance.unsetSurfaceDataMember();
			parameterizedTexture.unsetTarget();
			
			
			// write all data
			TextureCoordinates tc = citygml
					.createTextureCoordinates();
			TexCoordList tcl = citygml.createTexCoordList();
			TextureAssociation ta = citygml
					.createTextureAssociation();
			SurfaceDataProperty sdpt = citygml
			.createSurfaceDataProperty();
			
			// for all textures
			SimpleSurfaceDataMember simpleSDM=null;
			Iterator<SimpleSurfaceDataMember> data = currentBuildingData
					.iterator();
			
			
			while (data.hasNext()) {
				simpleSDM = (SimpleSurfaceDataMember) data.next();
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
			parameterizedTexture.setImageURI(simpleSDM==null?"":simpleSDM.getImageURI());
			parameterizedTexture.setMimeType(simpleSDM==null?"":simpleSDM.getImageMIMEType());
			sdpt.setSurfaceData(parameterizedTexture);
			
			
			appearance.addSurfaceDataMember(sdpt);
			pcc++;
		}
		super.accept(parameterizedTexture);
	}

	
}
