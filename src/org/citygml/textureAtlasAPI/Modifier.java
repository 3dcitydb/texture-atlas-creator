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
package org.citygml.textureAtlasAPI;

import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.citygml.textureAtlasAPI.dataStructure.TextureImage;
import org.citygml.textureAtlasAPI.dataStructure.TextureImagesInfo;
import org.citygml.textureAtlasAPI.imageIO.ImageScaling;
import org.citygml.textureAtlasAPI.packer.AtlasRegion;
import org.citygml.textureAtlasAPI.packer.Packer;
import org.citygml.textureAtlasAPI.packer.TextureAtlas;
//import org.citygml.util.Logger;
import org.citygml.util.ErrorTypes;


/**
 * This class is responsible for creating atlases, modifying coordinates and names for an instance of 
 * TexImageInfo. However, it does not modify unloaded TexImages and the one which has coordinates out of 
 * the rage [0,1].
 * Remaining textures will be divide in two groups: with/without alpha channel textures. They will be 
 * combined separately. As a result, atlases will be in PNG/JPEG image formats.
 * 
 */
public class Modifier {
	private final int atlasMaxWidth;
	private final int atlasMaxHeight;
	private final int packingAlgorithm;	
	private final boolean usePOTS;
	private final double scaleFactor;

	private HashMap<Object, ErrorTypes> log;

	public Modifier(int packingAlgorithm,  int atlasMaxWidth, int atlasMaxHeight, boolean usePOTS, double scaleFactor) {
		this.packingAlgorithm = packingAlgorithm;
		this.atlasMaxHeight = atlasMaxHeight;
		this.atlasMaxWidth = atlasMaxWidth;
		this.usePOTS = usePOTS;
		this.scaleFactor = scaleFactor;

		if (usePOTS) {
			atlasMaxHeight = (int) Math.pow(2, Math.floor(Math.log10(atlasMaxHeight) / Math.log10(2)));
			atlasMaxWidth = (int) Math.pow(2, Math.floor(Math.log10(atlasMaxWidth) / Math.log10(2)));
		}
	}

	public HashMap<Object, ErrorTypes> getLOG(){
		return log;
	}

	public void run(TextureImagesInfo ti) {
		int atlasCounter = 0;
		String atlasName = null;

		HashMap<String, TextureImage> texImages = ti.getTexImages();
		HashMap<Object, String> object2texCoords = ti.getTexCoordinates();
		HashMap<Object, String> object2texImage = ti.getTexImageURIs();

		if (log == null)
			this.log =new HashMap<Object, ErrorTypes>(); 
		else
			log.clear();

		HashMap<String, ArrayList<Object>> texImage2objects = new HashMap<String, ArrayList<Object>>();
		HashMap<String, Boolean> acceptedTexImages = new HashMap<String, Boolean>();
		HashMap<String, String> texImageNameMapping = new HashMap<String, String>();
		HashMap<Object, double[]> texCoordsList = new HashMap<Object, double[]>();

		int totalWidth3c = 0, maxw3c = 0;
		int totalWidth4c = 0, maxw4c = 0;
		long area3c = 0, area4c = 0;

		// create packers for 3 and 4 channels textures.
		Packer packer3C = new Packer(atlasMaxWidth, atlasMaxHeight, packingAlgorithm, false);
		Packer packer4C = new Packer(atlasMaxWidth, atlasMaxHeight, packingAlgorithm, true);

		if (object2texImage == null || object2texImage.isEmpty()) {
			// it does not contain any texture!
			return;
		}

		// step 1: go through all objects having texture information and
		// check whether the (rescaled) texture image fits the binding box
		// of the atlas and whether the texture coordinates are sane
		for (Entry<Object, String> entry : object2texImage.entrySet()) {
			Object objectId = entry.getKey();
			String texImageName = entry.getValue();
			TextureImage texImage = null;

			// change the name of the texture image if not already done so
			String mapping = texImageNameMapping.get(texImageName);
			if (mapping == null) {
				texImage = texImages.get(texImageName);

				if (texImage != null && texImage.getBufferedImage() != null) {
					mapping = getNewTexImageName(texImageName, texImage.getChannels());
					object2texImage.put(objectId, mapping);
					texImages.remove(texImageName);
					texImages.put(mapping, texImage);
					texImageNameMapping.put(texImageName, mapping);
				}
			}

			// update image
			texImageName = mapping;
			if (texImage == null && (texImage = texImages.get(texImageName)) == null)
				continue;

			// check whether buffered image is available
			if (texImage.getBufferedImage() == null) {
				acceptedTexImages.put(texImageName, false);
				log.put(objectId,ErrorTypes.IMAGE_IS_NOT_AVAILABLE);				
				continue;			
			}

			// get number of channels
			boolean hasFourChannels = texImage.getChannels() == 4;

			// the image was accepted before
			Boolean isAccepted = acceptedTexImages.get(texImageName);
			if (isAccepted != null) {
				if (!isAccepted.booleanValue()) {
					// the texture images has already been rejected
					continue;
				}

				// if the image has already been accepted,
				// we only need to check the texture coordinates 
				double[] texCoords = checkAndGetTexCoordinates(object2texCoords.get(objectId));
				if (texCoords == null) {
					// in a previous run the texture coordinates of another object pointing to this texture image
					// could be successfully parsed. Now we failed to parse the texture coordinates of this object.
					// Hence, the texture image is not accepted in order to keep the original texturing.
					acceptedTexImages.put(texImageName, false);
					log.put(objectId, ErrorTypes.ERROR_IN_COORDINATES);
					int width = texImage.getBufferedImage().getWidth();

					// remove the texture image from the packer and adapt statistics
					if (hasFourChannels) {
						if (packer4C.removeRegion(texImageName)) {
							totalWidth4c -= width;
							if (totalWidth4c < maxw4c)
								maxw4c = totalWidth4c;
						}
					} else {
						if (packer3C.removeRegion(texImageName)) {
							totalWidth3c -= width;
							if (totalWidth3c < maxw3c)
								maxw3c = totalWidth3c;
						}
					}

					continue;
				}

				// texture coordinates could be successfully parsed. add this object to the list
				// of objects affected by this texture image
				texCoordsList.put(objectId, texCoords);
				texImage2objects.get(texImageName).add(objectId);	
				continue;
			}

			// check texture coordinates of object
			double[] texCoords = checkAndGetTexCoordinates(object2texCoords.get(objectId));
			if (texCoords == null) {
				acceptedTexImages.put(texImageName, false);
				log.put(objectId, ErrorTypes.ERROR_IN_COORDINATES);
				continue;
			}

			// rescale image if requested
			if (scaleFactor != 1)
				texImage.setImage(ImageScaling.rescale(texImage.getBufferedImage(), scaleFactor));

			// rescale texture image if it exceeds the maximum width or height of the atlas
			if (!imageFitsIntoAtlas(texImage.getBufferedImage())) {
				BufferedImage scaledImage = ImageScaling.rescale(texImage.getBufferedImage(), atlasMaxWidth, atlasMaxHeight);
				if (scaledImage == null || !imageFitsIntoAtlas(scaledImage)){
					acceptedTexImages.put(texImageName, false);
					log.put(objectId, ErrorTypes.IMAGE_UNBOUNDED_SIZE);
					continue;
				}

				texImage.setImage(scaledImage);
			}

			// set the name of the atlas
			if (atlasName == null)
				atlasName = "textureAtlas_" + ti.hashCode() + "_%1d."; 

			// the current object and its texture information is fine.
			// udpate the data structures correspondingly
			texCoordsList.put(objectId, texCoords);
			int width = texImage.getBufferedImage().getWidth();
			int height = texImage.getBufferedImage().getHeight();

			if (hasFourChannels) {
				packer4C.addRegion(texImageName, width, height);
				if (width > maxw4c)
					maxw4c = width;

				totalWidth4c += width;
				area4c += width * height;
			} else {
				packer3C.addRegion(texImageName, width,height);
				if (width > maxw3c)
					maxw3c = width;

				totalWidth3c += width;
				area3c += width * height;
			}

			acceptedTexImages.put(texImageName, true);

			ArrayList<Object> tmp = texImage2objects.get(texImageName);
			if (tmp == null) {
				tmp = new ArrayList<Object>();
				tmp.add(objectId);
				texImage2objects.put(texImageName, tmp);
			} else
				tmp.add(objectId);
		}

		// step 2: the previous step resulted in
		// - unsupported images; they will not be touched
		// - a group of 3-channel images
		// - a group of 4-channel images
		// next, atlases will be generated for both 3-channel and 4-channel groups.  

		ArrayList<TextureAtlas> atlasMR = new ArrayList<TextureAtlas>(2);
		if (packer3C.getRegions() != 0)
			atlasMR.add(pack(packer3C, maxw3c, totalWidth3c, area3c));
		if (packer4C.getRegions() != 0)
			atlasMR.add(pack(packer4C, maxw4c, totalWidth4c, area4c));

		// for all available atlases modify the coordinates and image names
		for (TextureAtlas atlas : atlasMR) {	
			if (Math.min(atlas.getBindingBoxHeight(), atlas.getBindingBoxWidth()) < 1)
				continue;

			// create atlas image
			boolean hasFourChannels = atlas.hasFourChannels();
			BufferedImage atlasImage = new BufferedImage(
					Math.min(getMinCoveredPOT(atlas.getBindingBoxWidth()), atlasMaxWidth),
					Math.min(getMinCoveredPOT(atlas.getBindingBoxHeight()),atlasMaxHeight),
					hasFourChannels ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);

			// obtain graphics object for drawing
			Graphics2D g = atlasImage.createGraphics();

			// create the atlas
			// note: each atlas may be divided into several ones
			int atlasWidth = 0, atlasHeight = 0;

			// list of all items which will be drawn in a same atlas
			List<AtlasRegion> frame = new ArrayList<AtlasRegion>();
			List<AtlasRegion> regions = atlas.getRegions();	
			int currentLevel = 0;

			for (AtlasRegion region : regions) {
				TextureImage texImage = texImages.get(region.getTexImageName());

				if (region.isRotated) {
					// rotate texture image
					BufferedImage image = texImage.getBufferedImage();
					int type = (image.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

					int width = image.getWidth();
					int height = image.getHeight();
					BufferedImage rotatedImage = new BufferedImage(height, width, type);

					// swap pixels
					for (int i = 0; i < width; i++)
						for (int j = 0; j < height; j++)
							rotatedImage.setRGB(j, width-i-1, image.getRGB(i, j));

					texImage.setImage(rotatedImage);
				}

				// check whether the current atlas is full
				if (currentLevel != region.level) {
					int potW = getMinCoveredPOT(atlasWidth);
					int potH = getMinCoveredPOT(atlasHeight);

					// put atlas into texture image map
					texImages.put(String.format(atlasName, atlasCounter) + (hasFourChannels ? "png" : "jpeg"),
							new TextureImage(atlasImage.getSubimage(0, 0, potW , potH)));

					// dispose graphics object
					g.dispose();
					atlasCounter++;

					// create new atlas image
					atlasImage = new BufferedImage(
							Math.min(getMinCoveredPOT(atlas.getBindingBoxWidth()), atlasMaxWidth), 
							Math.min(getMinCoveredPOT(atlas.getBindingBoxHeight()), atlasMaxHeight),
							hasFourChannels ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);

					// reobtain graphics object
					g = atlasImage.createGraphics();

					// adapt texture coordinate for all textures which are fixed in the current atlas.
					adaptTextureCoordinates(frame, object2texCoords, texCoordsList, texImage2objects, potW, potH);

					// reset metadata
					frame.clear();
					atlasWidth = 0;
					atlasHeight = 0;
					currentLevel = region.level;
				}

				// draw image into atlas and remove from list of texture images
				g.drawImage(texImage.getBufferedImage(), region.x, region.y, null);
				texImages.remove(region.getTexImageName());

				frame.add(region);

				if (atlasWidth < region.x + region.width)
					atlasWidth = region.x + region.width;
				if (atlasHeight < region.y + region.height)
					atlasHeight = region.y + region.height;

				// update texImage2objects map
				for (Object obj : texImage2objects.get(region.getTexImageName()))	
					object2texImage.put(obj, String.format(atlasName, atlasCounter) + (hasFourChannels ? "png" : "jpeg"));
			}

			if (atlasHeight != 0 || atlasWidth != 0) {
				int potW = getMinCoveredPOT(atlasWidth);
				int potH = getMinCoveredPOT(atlasHeight);

				texImages.put(String.format(atlasName, atlasCounter) + (hasFourChannels ? "png" : "jpeg"),
						new TextureImage(atlasImage.getSubimage(0, 0, potW , potH)));

				atlasCounter++;

				// adapt texture coordinate for all textures which are fixed in the current atlas.
				adaptTextureCoordinates(frame, object2texCoords, texCoordsList, texImage2objects, potW, potH);

				frame.clear();
			}

			g.dispose();
		}

		texImage2objects.clear();
		acceptedTexImages.clear();
		texCoordsList.clear();
		texImageNameMapping.clear();
	}

	private String getNewTexImageName(String prevURI, int channel) {
		return prevURI.substring(0, prevURI.lastIndexOf('.')) + (channel == 3 ? ".jpeg" : ".png");
	}

	private int getMinCoveredPOT(int len){
		if (!usePOTS)
			return len;

		int minPOT=(int) Math.floor(Math.log10(len) / Math.log10(2));
		if (Math.pow(2,minPOT) == len)
			return len;

		return (int)Math.pow(2, minPOT + 1);
	}

	private boolean imageFitsIntoAtlas(BufferedImage image){
		return (image.getWidth() <= atlasMaxWidth && image.getHeight() <= atlasMaxHeight);
	}

	private TextureAtlas pack(Packer packer, int maxWidth, int totalWidth, long area) {
		packer.setBinSize(atlasMaxWidth, atlasMaxHeight);
		return packer.pack(usePOTS);
	}

	private double[] checkAndGetTexCoordinates(String coordinates){
		if (coordinates==null || coordinates.length() == 0)
			return null;

		String[] sc = coordinates.split(" ");
		if ((sc.length & 1) == 1)
			return null;

		double[]c= new double[sc.length];
		for (int i=0;i<sc.length;i++){
			c[i] = Double.parseDouble(sc[i]);

			// check for texture wrapping 
			// this is not supported by a texture atlas
			if (c[i] < -0.1 || c[i] > 1.1)
				return null;
		}

		return c;
	}

	private void adaptTextureCoordinates(List<AtlasRegion> frame, 
			HashMap<Object, String> object2texCoords, 
			HashMap<Object, double[]> doubleCoordinateList,
			HashMap<String,ArrayList<Object>> texImage2objects, 
			int atlasWidth, int atlasHeigth) {
		Iterator<AtlasRegion> rectIter = frame.iterator();
		while (rectIter.hasNext()) {
			AtlasRegion rect = rectIter.next();
			for (Object obj : texImage2objects.get(rect.getTexImageName())) {
				double[] texCoords = doubleCoordinateList.get(obj);
				StringBuilder builder = new StringBuilder();

				for (int j = 0; j < texCoords.length; j += 2) {
					if (rect.isRotated){
						double tmp =texCoords[j];
						texCoords[j]=1- texCoords[j+1];
						texCoords[j+1]= tmp;
					}

					texCoords[j] = (rect.x + (texCoords[j] * rect.width)) / atlasWidth;
					texCoords[j + 1] = 1 - ((1 - texCoords[j+1]) * rect.height + rect.y) / atlasHeigth;

					builder.append(texCoords[j]);
					builder.append(' ');
					builder.append(texCoords[j+1]);
					builder.append(' ');	
				}

				object2texCoords.put(obj, builder.substring(0, builder.length() - 1));
			}

			rect.clear();
		}
	}

}
