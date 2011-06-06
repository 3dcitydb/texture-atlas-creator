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
package org.citygml.Model;


import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.JAXBBuilder;
import org.citygml4j.factory.CityGMLFactory;

import org.citygml4j.model.citygml.core.CityModel;


import org.citygml4j.model.citygml.core.AbstractCityObject;

import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReader;
import org.citygml4j.xml.io.writer.CityGMLWriter;


import org.citygml4j.util.child.ChildInfo;

import org.citygml4j.model.citygml.appearance.Appearance;
import org.citygml4j.model.citygml.appearance.ParameterizedTexture;
import org.citygml4j.model.citygml.appearance.SurfaceDataProperty;
import org.citygml4j.model.citygml.appearance.TexCoordList;
import org.citygml4j.model.citygml.appearance.TextureAssociation;
import org.citygml4j.model.citygml.appearance.TextureCoordinates;
import org.citygml4j.model.citygml.building.AbstractBuilding;


import org.citygml4j.model.module.citygml.CityGMLVersion;
import org.citygml4j.model.module.citygml.CoreModule;
import org.citygml4j.util.walker.FeatureWalker;

import org.citygml4j.xml.io.CityGMLOutputFactory;

import org.citygml.textureAtlasAPI.dataStructure.*;
import org.citygml.textureAtlasAPI.TextureAtlasGenerator;
import org.citygml.util.Logger;


public class GMLModifier {
	
	private int texturePackerType=org.citygml.textureAtlasAPI.TextureAtlasGenerator.TPIM;
	private TextureAtlasGenerator atlasGenerator;
	
	/**
	 * formated /
	 */
	private String inputGML;
	private String outputGML;
	private String inputParentPath;
	private String outputParentPath;
		
	// cityGML4j components
	private CityGMLContext ctx = new CityGMLContext();
	private JAXBBuilder builder;
	final CityGMLFactory citygml = new CityGMLFactory();
	
	
	private int maxImageH,maxImageW;
	public final static String unknowBuildingID="UNKNOWN";
	
	public GMLModifier(){
		
		inputGML=null;
		outputGML=null;
	}
	
	/**
	 * formated
	 * @param input --> /
	 * @param output--> /
	 */
	public void setProperties(String input, String output,int packingAlgo, int maxw, int maxh){
	
		this.inputGML =input;
		this.outputGML = output;
		this.texturePackerType=packingAlgo;
		this.maxImageH=maxh;
		this.maxImageW=maxw;
		initialize();
		
	}

	private void initialize() {
		if (atlasGenerator==null)
			atlasGenerator = new TextureAtlasGenerator();
		atlasGenerator.setImageMaxWidth(maxImageW);
		atlasGenerator.setImageMaxHeight(maxImageH);
		atlasGenerator.setPackingAlgorithm(texturePackerType);
		
		inputParentPath= getDirectory(inputGML);
		outputParentPath = getDirectory(outputGML);
	}
	
	private String getNumber(int c){
		if (c<14 && c>10 )
			return c+"th";
		switch(c%10){
		case 1: return c+"st";
		case 2: return c+"nd";
		case 3: return c+"rd";
		default: return c+"th";
		}
	}
	
	/**
	 * Read the GML file from which is in the address inputGML. 
	 * Change the format and also merge the textures to make a texture atlas for each building.
	 * write the modified data in a new GML file in the address outputGML. 
	 */
	public void modify(){
		
		try {
			// Load a cityGML file
			CityModel cityModel = readGMLFile(inputGML);
			
			// scan the cityModel object to obtain all surface data of each building separately.
			// each gml file may contain more than one building.
			// each building may need more than one textureatlas.
			Hashtable<String, Hashtable<Integer,TexImageInfo4GMLFile>> buildings=newCityModelScaner(cityModel);
			int bildingSize = buildings.size();
			if (buildings.containsKey(unknowBuildingID))
				bildingSize--;
			Logger.getInstance().log(Logger.TYPE_INFO,"   Contains "+bildingSize+" building(s).");

			Hashtable<Integer,TexImageInfo4GMLFile> building;
			TexImageInfo4GMLFile texGroup;
			Integer tmpKey;
			// simple counter
			int cc=1;
			String log;
			// for all buildings in the current cityGML file
			for(String buildingID: buildings.keySet()){
				if (buildingID.equals(unknowBuildingID)){
					Logger.getInstance().log(Logger.TYPE_INFO,"       Working on general appearances.");
					cc--;
				}
				else
					Logger.getInstance().log(Logger.TYPE_INFO,"       Working on "+getNumber(cc)+" building.("+buildingID+")");
				building=buildings.get(buildingID);
				Enumeration<Integer> texGroupIDS= building.keys();
				// for each potential group of textures: 
				while(texGroupIDS.hasMoreElements()){
					tmpKey= texGroupIDS.nextElement();
					texGroup = building.get(tmpKey);
					// using the TextureAtlas API
					texGroup= (TexImageInfo4GMLFile) atlasGenerator.convert(texGroup);
					log = atlasGenerator.getLOGInText();
					if (log!=null){
						Logger.getInstance().log(Logger.TYPE_ERROR,log.replaceAll("<", "                  <").replaceFirst("                  <",  "       <"));
						log=null;
					}
					// gml file file modify at the end.
					building.put(tmpKey, texGroup);
					// But images will be written immediately. 
					writeImageFiles(texGroup.getTexImages());
				}
				cc++;
			
			}
			
			/* After finishing with all buildings inside of current citygml file
			 * modifies the citygml file.
			 */
			
			FeatureChanger myFeatureWalker = new FeatureChanger();
			myFeatureWalker.set(buildings,citygml);
			cityModel.accept(myFeatureWalker);

			myFeatureWalker=null;
			Logger.getInstance().log(Logger.TYPE_INFO,"   Writing the output on file...");
			// write the citygml file.
			writeGMLFile(cityModel, outputGML);
			
		} catch (Exception e) {
			Logger.getInstance().log(Logger.TYPE_ERROR,"Error in modification...\n"+e.getMessage());
			if (Logger.SHOW_STACK_PRINT)
				e.printStackTrace();
		}

	}

	/**
	 * @param fileAddress
	 * @return
	 */
	private String getDirectory(String fileAddress){
		int idx;
		return fileAddress.substring(0,(idx=fileAddress.lastIndexOf('/'))>0?idx:0);
		
	}
	
	/**
	 * scan a cityModel object and make a TexImageInfo object for each building which is included in
	 * that cityModel. Objects will be distinguished by their building ID.  
	 * TODO change it by chunk wise reader to support mobility of elements.
	 * @author Babak Naderi
	 * @param citymodel
	 * @return
	 */
	private Hashtable<String, Hashtable<Integer,TexImageInfo4GMLFile>> newCityModelScaner(
			CityModel citymodel) {

		// Each element refer to a group of ParametrizedTexture in a CityObject(e.g. building) which share same features
		// instead of imageURI and coordinates.
		final Hashtable<String, Hashtable<Integer,TexImageInfo4GMLFile>> buildings = new Hashtable<String, Hashtable<Integer,TexImageInfo4GMLFile>>();
		// imagine all text have a same properties.
		FeatureWalker loader = new FeatureWalker() {
			Hashtable<Integer,TexImageInfo4GMLFile> building;
			TexImageInfo4GMLFile texGroup = null;
			TexGeneralProperties tmpProp=null;
			String buildingID=null;
			TexGeneralProperties genProp = null;

			Appearance currentApp;
			AbstractCityObject parentCityObject;
			ChildInfo ci = new ChildInfo();

			
			public void visit(ParameterizedTexture parameterizedTexture) {
				// cityGML structure
				currentApp= ci.getParentFeature(parameterizedTexture, Appearance.class);			
				parentCityObject= ci.getParentCityObject(parameterizedTexture);
				if (parentCityObject==null)
					buildingID = unknowBuildingID;
				else
					buildingID=parentCityObject.getId();
				
				
				// my structure
				building =buildings.get(buildingID);
				if (building==null){
					addNewBuilding(buildingID);
				}else  if (building.size()!=0){
					texGroup = building.get(new Integer(building.size()-1));
					genProp=texGroup.getGeneralProp();
				}else{
					//else is impossible
					texGroup = new TexImageInfo4GMLFile();
					building.put(new Integer(building.size()), texGroup);
					genProp = null;
				}
				// find (or make) corresponding TexImageInfo4GMLFile which share a same GeneralProperties with 
				// current parameterizedTexture.
				if (genProp==null){
					genProp = new TexGeneralProperties(parameterizedTexture
							.getTextureType(), parameterizedTexture
							.getWrapMode(), parameterizedTexture
							.getBorderColor(), parameterizedTexture
							.getIsFront(),currentApp.getTheme(),
							parameterizedTexture.getMimeType(),buildingID,getExtension(parameterizedTexture.getImageURI()));
					texGroup.setGeneralProp(genProp);
				}else if(!genProp.compareItTo(tmpProp = new TexGeneralProperties (parameterizedTexture
						.getTextureType(), parameterizedTexture
						.getWrapMode(), parameterizedTexture
						.getBorderColor(), parameterizedTexture
						.getIsFront(),currentApp.getTheme(),
						parameterizedTexture.getMimeType(),buildingID,getExtension(parameterizedTexture.getImageURI())))){
					// find corresponding texGroup
					texGroup = findTextGroupInBuilding(tmpProp);
					if (texGroup!=null){
						// it is found.
						genProp= texGroup.getGeneralProp();
						tmpProp.clear();
						tmpProp=null;
					}else{
						// there is not any similar texture in this building.
						genProp =tmpProp;
						tmpProp=null;
						texGroup = new TexImageInfo4GMLFile();
						texGroup.setGeneralProp(genProp);
						building.put(new Integer(building.size()), texGroup);
					}
					
				}
				texGroup.addTexImages(parameterizedTexture.getImageURI(), getCompliteImagePath(parameterizedTexture.getImageURI()));
				
				Iterator<TextureAssociation> targets = parameterizedTexture
						.getTarget().iterator();
				while (targets.hasNext()) {
					TextureAssociation ta = targets.next();
					// maybe it is TexCoordGen !?!?!? or not?
					if (ta.getTextureParameterization() instanceof TexCoordList){
					Iterator<TextureCoordinates> tcs = ((TexCoordList) (ta
							.getTextureParameterization()))
							.getTextureCoordinates().iterator();
					while (tcs.hasNext()) {
						TextureCoordinates tc = tcs.next();
						texGroup.addAll(ta.getUri(), tc.getRing(), parameterizedTexture
								.getImageURI(), double2String(tc.getValue()));
					}}
				}
				// in the case of land use, Appearances without model..
				if (currentApp!=null && parameterizedTexture.getParent()!=null)
					currentApp.unsetSurfaceDataMember((SurfaceDataProperty) parameterizedTexture.getParent());
				super.visit(parameterizedTexture);
			}

			public void visit(AbstractBuilding abstractBuilding) {
				if ((building=buildings.get(abstractBuilding.getId()))!=null ){
					if (building.size()>0){
						texGroup= building.get(new Integer(building.size()-1));
						genProp = texGroup.getGeneralProp();
					}else{
						texGroup = new TexImageInfo4GMLFile();
						building.put(new Integer(building.size()), texGroup);
						genProp = null;
					}
				}else
					addNewBuilding(abstractBuilding.getId());
				super.visit(abstractBuilding);
			}
			private void addNewBuilding(String gId){
					building = new Hashtable<Integer,TexImageInfo4GMLFile>();
					texGroup = new TexImageInfo4GMLFile();
					building.put(new Integer(building.size()), texGroup);
					genProp = null;
					buildings.put(gId, building);			
			}
			
			private TexImageInfo4GMLFile findTextGroupInBuilding(TexGeneralProperties genP){
				Enumeration<TexImageInfo4GMLFile> e= building.elements();
				TexImageInfo4GMLFile tmp;
				while(e.hasMoreElements()){
					tmp = e.nextElement();
					if (TexGeneralProperties.Compare(genP, tmp.getGeneralProp())){
						e=null;
						return tmp;
					}
				}
				e=null;
				genP=null;
				tmp=null;
				return null;
			}
			
			private String getExtension(String path){
				if (path!=null)
					return path.substring(path.lastIndexOf('.')+1,path.length());
				return null;
			}

		};

		// pars to get information is a structured data format.
		citymodel.accept(loader);
		return buildings;

	}
	
	/**
	 * Load cityGML file completely (not efficient in the case of extremely huge file). 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private CityModel readGMLFile(String path) throws Exception{
		
		builder = ctx.createJAXBBuilder();
		CityGMLInputFactory in = builder.createCityGMLInputFactory();
		CityGMLReader reader = in.createCityGMLReader(new File(path));
		CityModel cityModel = (CityModel)reader.nextFeature();
		reader.close();
		return cityModel;
	}

	/**
	 * @param cityModel
	 * @param path
	 * @throws Exception
	 */
	private void writeGMLFile(CityModel cityModel,String path)throws Exception{
		File file = new File(path);
		if (!file.exists() &&file.getParent()!=null)
			file.getParentFile().mkdirs();
		
		CityGMLOutputFactory out = builder.createCityGMLOutputFactory(CityGMLVersion.v1_0_0);
		CityGMLWriter writer = out.createCityGMLWriter(file);
		writer.setPrefixes(CityGMLVersion.v1_0_0);
		writer.setDefaultNamespace(CoreModule.v1_0_0);
		writer.setSchemaLocations(CityGMLVersion.v1_0_0);
		writer.setIndentString("  ");
	
		writer.write(cityModel);
		writer.close();
	}
	
	private String getCompliteImagePath(String path){
		return inputParentPath+(inputParentPath==null||inputParentPath.length()==0?"":"/")+path.replace('\\', '/');
	}
	
	private String double2String(List<Double> coordinates){
		if (coordinates==null)
			return null;
		StringBuffer sb= new StringBuffer(coordinates.size()*20);
		Iterator<Double> coord =coordinates.iterator();
		while(coord.hasNext()){
			sb.append(coord.next());
			sb.append(' ');
		}
		coord=null;
		coordinates=null;
		return sb.toString();
	}
	
	/**
	 * write all the images in the correct place with respect to output GML address. 
	 * NOTE: It will clear texImage. 
	 * @param texImage
	 */
	private void writeImageFiles(HashMap<String, TexImage> texImage){
		if (texImage==null)
			return;
		BufferedImage bim;	
		
		String outPath;
		int chanels=0;
		BufferedImage bi = new BufferedImage(maxImageW, maxImageH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g=bi.createGraphics();
		for(String path: texImage.keySet()){
			
		
			bim= texImage.get(path).getBufferedImage();
			chanels=texImage.get(path).getChanels();
			
			outPath=outputParentPath+(outputParentPath==null||outputParentPath.length()==0?"":"/")+path.replace('\\', '/');
			// copy file exactly in the new place
			if (bim==null){	
				copyFile(getCompliteImagePath(path), outPath);
				outPath=null;
				continue;
			}
			
			if (chanels==3)
				outPath= outPath.substring(0, outPath.lastIndexOf('.'))+".jpeg";
			else
				outPath= outPath.substring(0, outPath.lastIndexOf('.'))+".png";
			
			File file = new File(outPath);
			if (!file.exists() &&file.getParent()!=null)
				file.getParentFile().mkdirs();
			try{
				
					ImageIO.write(bim, chanels ==3?"jpeg":"png", file);
					bim.flush();
					bim = null;
				

			}catch(Exception e){
				if (Logger.SHOW_STACK_PRINT)
					e.printStackTrace();
			}
			outPath=null;

		}
		g.dispose();
		bi.flush();
		bi = null;
		texImage.clear();
	}
	
	/**
	 * In the case that an image format is not supported, it will directly copy from input folder
	 * to output folder. 
	 * @param pathIn
	 * @param pathOut
	 */
	private void copyFile(String pathIn, String pathOut){
		int size;
		
		File fin=new File(pathIn);
		if (fin.exists()&& fin.canRead()){
			File fout =new File(pathOut);
			if (!fout.exists() &&fout.getParent()!=null)
				fout.getParentFile().mkdirs();
			try {
				FileOutputStream fos = new FileOutputStream(fout);
				FileInputStream fis = new FileInputStream(fin);
				byte[] mb = new byte[1024];
				size=1;
				while(size>0){
					size=fis.read(mb);
					if (size>0)
					fos.write(mb,0,size);
				}
				fis.close();
				fos.flush();
				fos.close();
				mb=null;
				fis=null;
				fos=null;
				fout=null;
				fin=null;
				
			} catch (Exception e) {
				if (Logger.SHOW_STACK_PRINT)
					e.printStackTrace();
			}
			
		}
	}
	
	
	
}
