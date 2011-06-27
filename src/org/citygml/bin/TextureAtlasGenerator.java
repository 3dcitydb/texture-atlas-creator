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
package org.citygml.bin;

import org.citygml.control.Controller;
import org.citygml.util.Logger;

/**
 * This is the main class and starting point of Texture Atlas Creator standalone tool. 
 */
public class TextureAtlasGenerator {
	
	Controller controller;
	public TextureAtlasGenerator(){

	}
	public TextureAtlasGenerator(String atlasOption,String inputPath,String outputPath,int algorithm
			, int w, int h){
		controller = new Controller();
		controller.setProperties(atlasOption, inputPath, outputPath,algorithm,w,h);
		if (!controller.validateArguments())
			Logger.getInstance().log(Logger.TYPE_ERROR, "Problem with input/output path. No file exist.");
		else
			controller.start();
	}

	/**
	 * Note: do not use any space in the directory names.
	 * @param args
	 */
	public static void main(String[] args) {

		String input="";
		String output="";
		int packing= org.citygml.textureAtlasAPI.TextureAtlasGenerator.TPIM;
		int w=2048;
		int h=2048;
		if (args.length==0)
			syntaxError();
		try{
		for (int i=0;i<args.length;i++){
			if (args[i].equalsIgnoreCase("/A")){
				i++;
				packing= getAlgo(args[i]);
				if (packing==-1){
					syntaxError();
					return;
				}
			}else 
				if (args[i].equalsIgnoreCase("/W")){
					i++;
					w = Integer.parseInt(args[i]);
					i++;
					if (!args[i].equalsIgnoreCase("/H")){
						syntaxError();
						return;
					}
					i++;
					h = Integer.parseInt(args[i]);	
				}else{
					input = args[i];
					i++;
					output= args[i];
				}
					
		}
		}catch(Exception e){
			syntaxError();
			if (Logger.SHOW_STACK_PRINT)
				e.printStackTrace();
		}
		TextureAtlasGenerator mt;
		mt = new TextureAtlasGenerator("-atlasPNG",input,output,packing,w,h);
	}
	private static int getAlgo(String name){
		if (name.equalsIgnoreCase("TPIM"))
			return org.citygml.textureAtlasAPI.TextureAtlasGenerator.TPIM;
		if (name.equalsIgnoreCase("TPIM_WOR"))
			return org.citygml.textureAtlasAPI.TextureAtlasGenerator.TPIM_WITHOUT_ROTATION;
		if (name.equalsIgnoreCase("SLEA"))
			return org.citygml.textureAtlasAPI.TextureAtlasGenerator.SLEA;
		if (name.equalsIgnoreCase("NFDH"))
			return org.citygml.textureAtlasAPI.TextureAtlasGenerator.NFDH;
		if (name.equalsIgnoreCase("FFDH"))
			return org.citygml.textureAtlasAPI.TextureAtlasGenerator.FFDH;
		return -1;
	}
	
	private static void syntaxError(){
		System.out.println(
				"\r\n"+
				"Modify CityGML files by making atlases from textures of each building.\r\n"+
				"\r\n"+
				"TAC [/A algorithm] [/W max_width /H max_height] input output\r\n"+
				"\r\n"+
				"[/A algorithm]  Choose a packing algorithm from following list. It will be used in atlas creation.\r\n"+
				"algorithm\r\n"+
				"        \t TPIM \t TPIM_WOR\r\n"+
				"        \t SLEA \t NFDH\r\n"+
				"        \t FFDH\r\n"+
				"        \t (Default algorithm is TPIM)\r\n \r\n"+
				"[/W max_width /H max_height] Maximum size of texture atlas.\r\n"+
				"                             (Default size  width:2048 height:2048)\r\n \r\n"+
				"input	 \t input file or folder.\r\n"+
				"output  \t output file or folder\r\n"+
				"\r\n"+
				"\r\n"+
				"Example:\r\n"+
				"\t TAC /A TPIM /W 2048 /H 2048 c:/test_case c:/results\r\n"+
				"\t TAC c:/test_case c:/results\r\n"+
				"\r\n"+
				"\r\n"				
				);		
				
		System.exit(0);
	}

	
	
}
