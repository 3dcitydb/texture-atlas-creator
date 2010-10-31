package org.citygml.bin;
import oracle.ord.im.OrdImage;

import javax.swing.JFrame;

import org.citygml.Control.Controller;
import org.citygml.GUI.UI;

public class TextureAtlasGenerator {
	
	Controller controller;
	UI frame;
	public TextureAtlasGenerator(){
		frame = new UI(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
	}
	public TextureAtlasGenerator(String atlasOption,String inputPath,String outputPath){
		controller = new Controller();
		controller.setProperties(atlasOption, inputPath, outputPath);
		if (!controller.validateArguments())
			syntaxError();
		else
			controller.start();
	}
	public void run(String atlasOption,String inputPath,String outputPath){
		controller = new Controller();
		controller.setProperties(atlasOption, inputPath, outputPath);
		if (!controller.validateArguments())
			syntaxError();
		else
			controller.start();
	}
	/**
	 * Note: do not use any space in the directory names.
	 * @param args
	 * args[0]:Output atlas options | Input(file/directory)
	 * args[1]:Input(file/directory)| Output(file/directory)
	 * args[2]:Output(file/directory)|null
	 */
	public static void main(String[] args) {
		System.out.print(System.currentTimeMillis());
		TextureAtlasGenerator mt;
		switch(args.length){
		case 2:// without  Output atlas options
			mt = new TextureAtlasGenerator("-atlasPNG",args[0],args[1]);
			break;
		case 3:
			mt = new TextureAtlasGenerator(args[0],args[1],args[2]);
			break;
		default:
//			syntaxError();
			mt = new TextureAtlasGenerator();
			
		}
	}
	
	
	private static void syntaxError(){
		System.out.println(
				"Usage:\r\n " +				
				"java -jar TextureAtlasGenerator.jar [Output atlas options]  <Input(file/directory)>   <Output(file/directory)>"+
				"\r\n"+
				"\r\n"+
				"Output atlas options:"+"\r\n"+				
				"-atlasPNG \t (Default)For all kinds of input texture images the texture atlas will be in the PNG image format."+"\r\n"+
				"-atlasJPG \t For all kinds of input texture images the texture atlas will be in the JPEG image format."+"\r\n"+
				"-atlasAuto \t Type of texture atlas depends on the input textures. For each building if there is any texture with PNG format, the texture atlas will be in PNG image format. Otherwise it will be in JPEG image format."+"\r\n"+
				"\r\n"+
				"Input(file/directory):"+"\r\n"+
				"File path \t Path of input GML file which contains one or more Building data."+"\r\n"+
				"Folder path \t Path of the folder which contains one or more GML files. The folder and all of its subfolders will be scand to convert all GML files and convert them."+"\r\n"+
				"\r\n"+
				"Output(file/directory):"+"\r\n"+
				"File path \t Path of output GML file which will be generated by this software and also its resources."+"\r\n"+
				"Folder path \t Path of output Folder which will contain all of the output GML files. They will be generated by this software."+"\r\n"+
				"\r\n"+
				"Sample:"+"\r\n"+
				"\tjava -jar TextureAtlasGenerator.jar -atlasJPG c:\\cityGML c:\\result"
				);		
				
		System.exit(0);
	}

	
	
}
