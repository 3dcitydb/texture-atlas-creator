package org.citygml.textureAtlasAPI.packer;

public class Item extends AbstractRect {

	Item(AbstractRect r) {
		super(r);
		
	}
	Item(String id, int width, int height) {
  		super(id,width,height);
  	}

	@Override
	public int compareTo(AbstractRect arg0) {
		return height < arg0.height ? -1 : height == arg0.height ? 0 : 1;
	}

}
