package org.citygml.Model;

import java.awt.Image;
import java.io.File;
import java.io.IOException;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;


import org.citygml.Model.DataStructures.TexturePropertiesInAtlas;
import org.citygml.Model.DataStructures.SimpleSurfaceDataMember;
import org.citygml.Model.TexturePackers.AbstractTexturePacker;

import org.citygml.Model.TexturePackers.TexturePacker;
import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.JAXBBuilder;
import org.citygml4j.factory.CityGMLFactory;

import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReader;
import org.citygml4j.xml.io.writer.CityGMLWriter;


import org.citygml4j.commons.gmlid.DefaultGMLIdManager;
import org.citygml4j.commons.gmlid.GMLIdManager;

import org.citygml4j.model.citygml.appearance.ParameterizedTexture;
import org.citygml4j.model.citygml.appearance.TexCoordList;
import org.citygml4j.model.citygml.appearance.TextureAssociation;
import org.citygml4j.model.citygml.appearance.TextureCoordinates;
import org.citygml4j.model.citygml.building.AbstractBuilding;

import org.citygml4j.model.module.citygml.CityGMLVersion;
import org.citygml4j.model.module.citygml.CoreModule;
import org.citygml4j.visitor.walker.FeatureWalker;

import org.citygml4j.xml.io.CityGMLOutputFactory;

import org.citygml.TextureAtlasAPI.TextureAtlasGenerator;
import org.citygml.TextureAtlasAPI.DataStructure.*;
public class GMLModifier {
	
//	public final static int PNG=1;
//	public final static int JPG=2;
////set JPEG. just change it to PNG if there is a alpha value in the input.
//	public final static int AUTO=3;// it is not supported yet!
//	
//	private int atlasTextureOutputFormat;
	
	private int texturePackerType=org.citygml.TextureAtlasAPI.StripPacker.MyStPacker.FFDH;
	private TextureAtlasGenerator atlasGenerator;
	
	
	private String inputGML;
	private String outputGML;
		
	// cityGML4j components
	private CityGMLContext ctx = new CityGMLContext();
	private JAXBBuilder builder;
	final GMLIdManager gmlIdManager = DefaultGMLIdManager.getInstance();
	final CityGMLFactory citygml = new CityGMLFactory();
	
	
	public GMLModifier(){
		inputGML=null;
		outputGML=null;
//		atlasTextureOutputFormat= GMLModifier.PNG;
	}
	
	public void setProperties(String input, String output){
	
		this.inputGML =input;
		this.outputGML = output;
		initialize();
		
	}

	public void initialize() {
		if (atlasGenerator==null)
			atlasGenerator = new TextureAtlasGenerator();
		atlasGenerator.setGeneralProp(texturePackerType, 2048, 2048);
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
//			Hashtable<String, ArrayList<SimpleSurfaceDataMember>> buildingsSurfaceData = scanCityModel(cityModel);
			Hashtable<String, Hashtable<Integer,TexImageInfo4GMLFile>> buildings=newCityModelScaner(cityModel);
			System.out.println(buildings.size());
			
			
//			Iterator<ArrayList<SimpleSurfaceDataMember>> buildingsIterator= buildingsSurfaceData.values().iterator();
			Iterator<Hashtable<Integer,TexImageInfo4GMLFile>> buildingsIter =buildings.values().iterator();
			// for each building merge textures and modify the coordinates and other properties.
//			ArrayList<SimpleSurfaceDataMember> buildingSurfaces;
//			String atlasURI;
//			
//			System.out.println("   Amount of buildings in this file: "+buildingsSurfaceData.size());
			// building counter
			
			Hashtable<Integer,TexImageInfo4GMLFile> building;
			TexImageInfo4GMLFile texGroup;
			Integer tmpKey;
			while(buildingsIter.hasNext()){
				building=buildingsIter.next();
				Enumeration<Integer> texGroupIDS= building.keys();
				while(texGroupIDS.hasMoreElements()){
					tmpKey= texGroupIDS.nextElement();
					texGroup = building.get(tmpKey);
//					texGroup= atlasGenerator.convertor4GMLF(texGroup);
					building.put(tmpKey, texGroup);
				}
			
			}
//			int bc=1; 
//			while(buildingsIterator.hasNext()){
//				buildingSurfaces = buildingsIterator.next();
//				if (buildingSurfaces==null||buildingSurfaces.size()==0)
//					continue;
				// without .xxx
////				atlasURI= getSuitableAtlasName(((SimpleSurfaceDataMember)buildingSurfaces.get(0)).getImageURI());// atlas texture path( its name is as same as the first texture's name)			
////				texturePacker.set(generatePicturesPath(buildingSurfaces),// textures' path
////						atlasURI,
////						getDirectory(inputGML),// prefix
////						atlasTextureOutputFormat);
////				Hashtable<String, TexturePropertiesInAtlas> newTextureProperties= texturePacker.run();
////				modifyCoordinates(buildingSurfaces,newTextureProperties);
//				newTextureProperties.clear();
//				newTextureProperties=null;
//				atlasURI=null;
//				System.out.println(bc);
//				bc++;
//			}
			// write new result based for each building
			FeatureChanger myFeatureWalker = new FeatureChanger();
			myFeatureWalker.set(buildings,citygml);
			cityModel.visit(myFeatureWalker);
			myFeatureWalker=null;
			writeGMLFile(cityModel, outputGML);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	

	/**
	 * !!!OK
	 * @param fileAddress
	 * @return
	 */
	public String getDirectory(String fileAddress){
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
	public Hashtable<String, Hashtable<Integer,TexImageInfo4GMLFile>> newCityModelScaner(
			CityModel citymodel) {

		// Each element refer to a group of ParametrizedTexture in a building which share all of the features
		// instead of imageURI and coordinates.
		final Hashtable<String, Hashtable<Integer,TexImageInfo4GMLFile>> buildings = new Hashtable<String, Hashtable<Integer,TexImageInfo4GMLFile>>();
		// imagine all text have a same properties.
		FeatureWalker loader = new FeatureWalker() {
			Hashtable<Integer,TexImageInfo4GMLFile> building;
			TexImageInfo4GMLFile texGroup = null;
			String target_ring;
			int counter =0;
			TexGeneralProperties genProp = null;
			public void accept(ParameterizedTexture parameterizedTexture) {
				if (genProp==null){
					genProp = new TexGeneralProperties(parameterizedTexture
							.getTextureType(), parameterizedTexture
							.getWrapMode(), parameterizedTexture
							.getBorderColor(), parameterizedTexture
							.getIsFront());
					texGroup.setGeneralProp(genProp);
				}else if(!genProp.compareItTo(parameterizedTexture
						.getTextureType(), parameterizedTexture
						.getWrapMode(), parameterizedTexture
						.getBorderColor(), parameterizedTexture
						.getIsFront())){
					// find corresponding texGroup
					texGroup = findTextGroupInBuilding(new TexGeneralProperties(parameterizedTexture
						.getTextureType(), parameterizedTexture
						.getWrapMode(), parameterizedTexture
						.getBorderColor(), parameterizedTexture
						.getIsFront()));
					if (texGroup!=null){
						// it is found.
						genProp= texGroup.getGeneralProp();
					}else{
						// there is not any similar texture in this building.
						genProp = new TexGeneralProperties(parameterizedTexture
								.getTextureType(), parameterizedTexture
								.getWrapMode(), parameterizedTexture
								.getBorderColor(), parameterizedTexture
								.getIsFront());
						counter++;
						texGroup = new TexImageInfo4GMLFile();
						texGroup.setGeneralProp(genProp);
						building.put(new Integer(counter), texGroup);
					}
					
				}
				texGroup.addTexImages(parameterizedTexture.getImageURI(), getCompliteImagePath(parameterizedTexture.getImageURI()));
				
				Iterator<TextureAssociation> targets = parameterizedTexture
						.getTarget().iterator();
				while (targets.hasNext()) {
					TextureAssociation ta = targets.next();
					// maybe it is TexCoordGen !?!?!? or not?
					Iterator<TextureCoordinates> tcs = ((TexCoordList) (ta
							.getTextureParameterization()))
							.getTextureCoordinates().iterator();
					while (tcs.hasNext()) {
						TextureCoordinates tc = tcs.next();
						texGroup.addAll(parameterizedTexture.getId(), ta
								.getUri(), tc.getRing(), parameterizedTexture
								.getImageURI(), double2String(tc.getValue()));
					}
				}
				super.accept(parameterizedTexture);
			}

			public void accept(AbstractBuilding abstractBuilding) {
				building = new Hashtable<Integer,TexImageInfo4GMLFile>();
				texGroup = new TexImageInfo4GMLFile();
				counter=0;
				building.put(new Integer(counter), texGroup);
				genProp = null;
				
				buildings.put(abstractBuilding.getId(), building);
				super.accept(abstractBuilding);
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

		};

		// pars to get information is a structured data format.
		citymodel.visit(loader);
		return buildings;

	}
	/**
	 * !!!OK
	 * Parse the input cityModel object to find list of all surfaceDataMembers for each building.
	 * @param cityModel
	 * @return Is a list of buildings. Each item of that is a list of  SimpleSurfaceDataMembers of a building.
	 */
	public Hashtable<String, ArrayList<SimpleSurfaceDataMember>> scanCityModel(CityModel cityModel) {
		// just one building in each file.
		// Each element refer to data of a building in this file.
		final Hashtable<String, ArrayList<SimpleSurfaceDataMember>> buildings = new Hashtable<String, ArrayList<SimpleSurfaceDataMember>>();
//		final ArrayList<ArrayList<SimpleSurfaceDataMember>> buildings = new ArrayList<ArrayList<SimpleSurfaceDataMember>>();

		FeatureWalker loader = new FeatureWalker() {
			ArrayList<SimpleSurfaceDataMember> building= null;
			
			public void accept(ParameterizedTexture parameterizedTexture) {
				Iterator<TextureAssociation> targets= parameterizedTexture.getTarget().iterator();
				while(targets.hasNext()){
				TextureAssociation ta= targets.next();
				TextureCoordinates tc= ((TexCoordList) (ta.getTextureParameterization())).getTextureCoordinates().get(0);
							
				// save data!
				SimpleSurfaceDataMember simpleSurfaceMember= new SimpleSurfaceDataMember(parameterizedTexture.getImageURI(), tc.getValue(),ta.getUri(), tc.getRing());
				building.add(simpleSurfaceMember);
				}
				super.accept(parameterizedTexture);
			}
			public void accept(AbstractBuilding abstractBuilding){
//				if (building==null)// first building
					building = new ArrayList<SimpleSurfaceDataMember>();
//				else{// the building contains some surfaceDataMembers. 
					buildings.put(abstractBuilding.getId(), building);
//					buildings.add(building);
//					building = new ArrayList<SimpleSurfaceDataMember>();
//				}
				super.accept(abstractBuilding);
			}

		};
		
		// pars to get information is a structured data format.
		cityModel.visit(loader);
		return buildings;
	}

	
	/**
	 * !!OK
	 * combine all textures' path with a ';' delimiter. the first one will be the texture atlas path.
	 * @return
	 */
	public String generatePicturesPath(ArrayList<SimpleSurfaceDataMember> bulding){
		StringBuffer sb= new StringBuffer();
		String prefixAddress= getDirectory(inputGML);
		
		Iterator<SimpleSurfaceDataMember> data= bulding.iterator();
		while(data.hasNext()){
			sb.append(prefixAddress+(prefixAddress==null||prefixAddress.length()==0?"":"/")+((SimpleSurfaceDataMember)data.next()).getImageURI()+";");
		}
		data=null;
		prefixAddress=null;
		return sb.toString().replace('\\', '/');
	}
	/**
	 * !!OK
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public CityModel readGMLFile(String path) throws Exception{
		
		builder = ctx.createJAXBBuilder();
		CityGMLInputFactory in = builder.createCityGMLInputFactory();
		CityGMLReader reader = in.createCityGMLReader(new File(path));
		CityModel cityModel = (CityModel)reader.nextFeature();
		reader.close();
		return cityModel;
	}
/**
 * !!!OK
 * @param cityModel
 * @param path
 * @throws Exception
 */
	public void writeGMLFile(CityModel cityModel,String path)throws Exception{
		
		CityGMLOutputFactory out = builder.createCityGMLOutputFactory(CityGMLVersion.v1_0_0);
		CityGMLWriter writer = out.createCityGMLWriter(new File(path));
		writer.setPrefixes(CityGMLVersion.v1_0_0);
		writer.setDefaultNamespace(CoreModule.v1_0_0);
		writer.setSchemaLocations(CityGMLVersion.v1_0_0);
		writer.setIndentString("  ");
	
		writer.write(cityModel);
		writer.close();
	}
	public String getCompliteImagePath(String path){
		String prefixAddress= getDirectory(inputGML);
		String fullpath=prefixAddress+(prefixAddress==null||prefixAddress.length()==0?"":"/")+path;
		prefixAddress=null;
		return fullpath.replace('\\', '/');
	}
	
	public String double2String(List<Double> coordinates){
		if (coordinates==null)
			return null;
		StringBuffer sb= new StringBuffer(coordinates.size()*20);
		Iterator<Double> coord =coordinates.iterator();
		while(coord.hasNext()){
			sb.append(coord.next());
		}
		coord=null;
		coordinates=null;
		return sb.toString();
	}
}
