package org.citygml.bin;
import oracle.ord.im.OrdImage;

import javax.swing.JFrame;

import org.citygml.Control.Controller;
import org.citygml.GUI.UI;
import org.citygml.util.Logger;

public class TextureAtlasGenerator {
	
	Controller controller;
	UI frame;
	public TextureAtlasGenerator(){
		frame = new UI(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
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
	/**public void run(String atlasOption,String inputPath,String outputPath){
		controller = new Controller();
		controller.setProperties(atlasOption, inputPath, outputPath);
		if (!controller.validateArguments())
			syntaxError();
		else
			controller.start();
	}**/
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
