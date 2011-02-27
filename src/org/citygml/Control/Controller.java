package org.citygml.Control;

import java.io.File;
/**import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;**/
import java.util.Stack;

import javax.management.Notification;

import org.citygml.Model.GMLModifier;
import org.citygml.util.Logger;

//import javax.management.*;


public class Controller {
	

	
	private String atlasOptions;
	private String inputPath;
	private String outputPath;
	
	private GMLModifier gmlModifier;
	
	// path of NVIDIA AtlasTexture Generator --- @remove
	String atlasCreationToolPath="E:/CityGML/atlasCreationTool.exe";
	private int texturePackerType,maxH,maxW;
	public Controller(){
		gmlModifier= new GMLModifier();
		reset();
	}
	
	public void setProperties(String atlasOptions, String input, String output,int packingAlgo,int maxW, int maxH){
		
		this.atlasOptions = atlasOptions;
		this.inputPath=input;
		this.outputPath=output;
		this.texturePackerType=packingAlgo;
		this.maxH=maxH;
		this.maxW=maxW;
	}
	
	public boolean validateArguments(){
		// check the atlasOptions
		if (atlasOptions==null)
			return false;
		
		// ---- validate & set the image's format of the output texture atlas.
		if (inputPath.startsWith("/")) 
			inputPath=System.getProperty("user.dir")+inputPath;
		if (outputPath.startsWith("/")) 
			outputPath=System.getProperty("user.dir")+outputPath;
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
		System.out.println("\r\n");
		Logger.getInstance().log(Logger.TYPE_NESS,"Texture Atlas Creator is started...");
		File input =new File(inputPath);
		
		if (input.isDirectory()){ // input is a directory
			multiGMLFiles(input);
		}else{// input is a file
			gmlModifier.setProperties(inputPath, outputPath,texturePackerType,maxW,maxH);
			gmlModifier.modify();
		}
		Logger.getInstance().log(Logger.TYPE_NESS,"All available GML files compiled.");
		System.out.println("\r\n");
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
			Logger.getInstance().log(Logger.TYPE_NESS,gmlFiles.size()+(gmlFiles.size()>1?" GML files are found.":" GML file is found."));
			Logger.getInstance().log(Logger.TYPE_INFO,"Start to modifying ...");
			int counter=0;
			while(!gmlFiles.empty()){			
				path = gmlFiles.pop();
				counter++;
				Logger.getInstance().log(Logger.TYPE_INFO,"   "+counter+")Modifying "+path);
				String t =path.replaceFirst(inputPath, outputPath);
				gmlModifier.setProperties(path, t,texturePackerType,maxW,maxH);
				gmlModifier.modify();
				Logger.getInstance().log(Logger.TYPE_INFO,"   Finished with current file.");
			}
						
		}catch (Exception e){
			if (Logger.SHOW_STACK_PRINT)
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


