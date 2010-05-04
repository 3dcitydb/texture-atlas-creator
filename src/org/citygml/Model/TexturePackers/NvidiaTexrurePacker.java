package org.citygml.Model.TexturePackers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

import org.citygml.Model.GMLModifier;
import org.citygml.Model.DataStructures.TexturePropertiesInAtlas;

public class NvidiaTexrurePacker extends AbstractTexturePacker {

	public NvidiaTexrurePacker(){
		setTexturePackerType(AbstractTexturePacker.NVIDIA);
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public Hashtable<String, TexturePropertiesInAtlas> run() {
		// TODO Auto-generated method stub
		try{
		Runtime run = Runtime.getRuntime();
		run.exec(GMLModifier.atlasCreationToolPath+" -o "+getAtlasPath().replace('/', '\\')+" "+getImageList().replace('/', '\\').replace(';', ' '));
		return getTexturePositions();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	private Hashtable<String, TexturePropertiesInAtlas> getTexturePositions(){
		Hashtable<String, TexturePropertiesInAtlas> ht= new Hashtable<String, TexturePropertiesInAtlas>();
		TexturePropertiesInAtlas ip;
		try {
			String output="!!!!!";
			File f= new File(output);
			while(!f.exists()){}
			FileReader fr = new FileReader(f);

			BufferedReader br = new BufferedReader(fr);
			String line =br.readLine();
			while(line!=null){
				if(line.indexOf('#')==0||line.length()==0){
					line =br.readLine();
					continue;
				}
				line= line.replaceAll("		",",");
				String[] sp=line.split(",");
				ip= new TexturePropertiesInAtlas();
				//Path
				ip.setImagePath(sp[0]);
				//w offset
				ip.setHorizontalOffset(Double.parseDouble(sp[4]));
				// H offset
				ip.setVerticalOffset(Double.parseDouble(sp[5]));
				
				ip.setWidth(Double.parseDouble(sp[7]));
				
				ip.setHeight(Double.parseDouble(sp[8]));

				String st = sp[0].replace('\\', '/').replaceAll(getPrefixAddress().replace('\\', '/'), "").replace('/', '\\');
				ht.put(st, ip);
				sp=null;
				line =br.readLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return ht;
		
	}
}
