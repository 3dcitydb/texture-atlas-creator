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
package org.citygml.textureAtlasAPI.dataStructure;

import java.awt.image.BufferedImage;
import org.citygml.textureAtlasAPI.imageIO.ImageLoader;
//import org.citygml.util.Logger;

import oracle.ord.im.OrdImage;

/**
 * This class represents a texture image. It supports OrdImage and load them by using
 * and instance of ImageLoader class. 
 * 
 */
public class TexImage {
	public final static int ORD_IMAGE = 1;
	public final static int IMAGE = 2;
	private int type;
	private BufferedImage image;
	private OrdImage ordImage;
	private ImageLoader imageLoader;
	private int chanels=3;


	public TexImage(BufferedImage bi){
		this.image=bi;
		this.type=IMAGE;
		if (bi!=null)
			this.chanels = getChanel(bi.getType());	
		else
			this.chanels =-1;
	}
	
	public TexImage(OrdImage ordImage) {
		this.ordImage = ordImage;
		this.type = ORD_IMAGE;
	}

	public void setImageLoader(ImageLoader imgLoader){
		this.imageLoader=imgLoader;
	}
	
	public BufferedImage getBufferedImage(){
		if (this.image == null){
			if (this.ordImage == null)
				return null;
			try {	
				// TODO: change the way ASAP.
				byte[] mb=ordImage.getDataInByteArray();
				if (imageLoader==null)
					imageLoader= new ImageLoader();
				this.image= imageLoader.loadImage(ordImage.getDataInStream(), ordImage
						.getMimeType(), mb.length);
				mb=null;
				this.chanels=imageLoader.getChanels();
			} catch (Exception e) {
//				if (Logger.SHOW_STACK_PRINT)
//					e.printStackTrace();
				e = null;
				return null;
			}
			imageLoader=null;
		}
		
		return this.image;
	}
	
	public OrdImage getOrdImage() {
		if (this.type == ORD_IMAGE)
			return this.ordImage;
		else {
			if (this.image == null)
				return null;
			return null;
		}
	}

	public void setImage(BufferedImage bImage) {
		this.image = bImage;
		if (bImage!=null)
			this.chanels = getChanel(bImage.getType());
		else
			this.chanels = -1;
	}

	public void setImage(OrdImage ordImage) {
		this.ordImage = ordImage;
	}
	public int getChanels(){
		if (this.image==null)
			getBufferedImage();
		return this.chanels;
	}
	
	public int getChanel(int BuffImageType){
		switch(BuffImageType){
		case BufferedImage.TYPE_BYTE_GRAY:
			return 1;
		case BufferedImage.TYPE_INT_ARGB:
		case BufferedImage.TYPE_INT_ARGB_PRE:
		case BufferedImage.TYPE_4BYTE_ABGR:
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			return 4;
		default:
			return 3;
		}
	}
	
	public void freeMemory(){
		// how to finalize it!?
		ordImage=null;
		if (image!=null){
			image.flush();
			image=null;
		}
		imageLoader=null;		
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		ordImage=null;
		if (image!=null){
			image.flush();
			image=null;
		}
		imageLoader=null;
	}
}
