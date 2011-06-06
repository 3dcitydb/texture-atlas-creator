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
package org.citygml.Control;

import java.io.File;
/**import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;**/
import java.util.Stack;
//import javax.management.Notification;
//import javax.management.*;
import org.citygml.Model.GMLModifier;
import org.citygml.util.Logger;

/**
 * This class is responsible for:
 * 	- Checking the input arguments, and set properties.
 *  - Finding input file(es) and asking for modifying all gmal files.  
 * @author Babak Naderi
 *
 */
public class Controller {
		
	private String atlasOptions;
	private String inputPath;
	private String outputPath;
	
	private GMLModifier gmlModifier;

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
	
	/**
	 * Looks entire folder and sub-folders for ".gmal" files and modifies each of them separately. 
	 * @param folder
	 */
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
//   for quality analyzing. 		
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


