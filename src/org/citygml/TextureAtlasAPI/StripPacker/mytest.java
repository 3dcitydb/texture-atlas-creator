package org.citygml.TextureAtlasAPI.StripPacker;

public class mytest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyStPacker myPack = new MyStPacker();
		/**
		 * <a name ="strippacking">
				<tk name="test1">
				<data>
				<strip width="25"/>
				<item id="1" width="15" height="15"/>
				<item id="2" width="10" height="5"/>
				<item id="3" width="3" height="5"/>
				<item id="4" width="5" height="5"/>
				<item id="5" width="25" height="2"/>
				<item id="6" width="5" height="4"/>
				<item id="7" width="20" height="5"/>
				<item id="8" width="5" height="10"/>
				<item id="9" width="5" height="4"/>
				<item id="10" width="15" height="5"/>
				<item id="11" width="12" height="5"/>
				<item id="12" width="5" height="4"/>
				<item id="13" width="5" height="15"/>
				<item id="14" width="5" height="4"/>
				</data>
				</tk>
			</a>
		 */
	
		myPack.setStripWidth(25);
		myPack.addItem("1",15,15,null,null);
		myPack.addItem("2",10,5,null,null);
		try {
			MyResult r = myPack.getResult(MyStPacker.FFDH);
			System.out.println(r.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
