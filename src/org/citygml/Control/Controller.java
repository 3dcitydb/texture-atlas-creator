package org.citygml.Control;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Stack;

import javax.management.Notification;

import org.citygml.Model.GMLModifier;
import javax.management.*;


public class Controller {
	

	
	private String atlasOptions;
	private String inputPath;
	private String outputPath;
	
	private GMLModifier gmlModifier;
	
	// path of NVIDIA AtlasTexture Generator --- @remove
	String atlasCreationToolPath="E:/CityGML/atlasCreationTool.exe";
	
	public Controller(){
		gmlModifier= new GMLModifier();
		reset();
	}
	
	public void setProperties(String atlasOptions, String input, String output){	
		this.atlasOptions = atlasOptions;
		this.inputPath=input;
		this.outputPath=output;
	}
	
	public boolean validateArguments(){
		// check the atlasOptions
		if (atlasOptions==null)
			return false;
		
		// ---- validate & set the image's format of the output texture atlas.
		atlasOptions= atlasOptions.toLowerCase();
		inputPath=inputPath.toLowerCase().replace('\\','/');
		outputPath=outputPath.toLowerCase().replace('\\','/');
		
//		if(atlasOptions.equals("-atlaspng"))
//			gmlModifier.setAtlasTextureOutputFormat(GMLModifier.PNG);
//		else
//			if(atlasOptions.equals("-atlasjpg"))
//				gmlModifier.setAtlasTextureOutputFormat(GMLModifier.JPG);
//			else	
//				if(atlasOptions.equals("-atlasauto"))
//					gmlModifier.setAtlasTextureOutputFormat(GMLModifier.AUTO);
//				else
//					return false; // or maybe just set the default value!
		
		
		// validate input file/folder
		File input =new File(inputPath);
		if (!input.exists())
			return false;
		if(input.isFile())
			// check whether the input & output files are the GML files.
			if (inputPath.indexOf(".gml")<=0 ||outputPath.indexOf(".gml")<=0)
				return false;
		input =null;
		return true;
	}
	
	public void start(){
		
		File input =new File(inputPath);
		
		if (input.isDirectory()){ // input is a directory
			multiGMLFiles(input);
		}else{// input is a file
			gmlModifier.setProperties(inputPath, outputPath);
			gmlModifier.modify();
		}			
		System.out.println("Complete!");
		//System.out.println(System.currentTimeMillis());
	}
	

	private void multiGMLFiles(File folder){
		try{
			
			Stack<File> openFolders= new Stack<File>();
			Stack<String> gmlFiles= new Stack<String>();
			openFolders.push(folder);
			int i;
			File[] list;
			
			// scan the openFolders to find entire GML files and push all of them to gmlFiles stack.
			while(!openFolders.empty()){
				folder = openFolders.pop();
				if (folder.isDirectory()){
					list =folder.listFiles();
					if (list==null)
						continue;
					for (i=0;i<list.length;i++){
						if (list[i].isDirectory())
							openFolders.push(list[i]);
						else
							if(list[i].getName().indexOf(".gml")!=-1){
								gmlFiles.push(list[i].getPath().replace('\\', '/'));
								
							}
					}
					list=null;
				}
			}
			String path;
			// do merge textures for each gml file.
			System.out.println("Amount of GML files: "+gmlFiles.size());
			int counter=0;
			while(!gmlFiles.empty()){			
				path = gmlFiles.pop();
				counter++;
				System.out.println(counter+".Modifing "+path);
				String t =path.replaceFirst(inputPath, outputPath);
				gmlModifier.setProperties(path, t);
				gmlModifier.modify();
				System.out.println("..................... OK");
			}
						
		}catch (Exception e){
			e.printStackTrace();
			
		}	
		
//		System.out.println(System.currentTimeMillis());
//		System.out.println(ManagementFactory.getRuntimeMXBean().getName());
//		MemoryUsage mu =ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
//		System.out.println("init:"+mu.getInit()+", max:"+mu.getMax()+", commited:"+mu.getCommitted()+",Used:"+mu.getUsed());


	}
	
	
	public void reset(){
		atlasOptions=null;
		inputPath=null;
		outputPath=null;
	}

}
class MyListener implements javax.management.NotificationListener {
	@Override
	public void handleNotification(Notification arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
}


