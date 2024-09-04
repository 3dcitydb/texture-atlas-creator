/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2021
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.lrg.tum.de/gis/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * Virtual City Systems, Berlin <https://vc.systems/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.citydb.textureAtlas.packer;

import org.citydb.textureAtlas.image.ImageProcessor;
import org.citydb.textureAtlas.model.AtlasRegion;
import org.citydb.textureAtlas.model.TextureAtlas;
import org.citydb.textureAtlas.model.TextureImage;
import org.citydb.textureAtlas.model.TextureImagesInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Modifier {
    private final int atlasMaxWidth;
    private final int atlasMaxHeight;
    private final int packingAlgorithm;
    private final boolean usePOTS;
    private final double scaleFactor;

    public Modifier(int packingAlgorithm, int atlasMaxWidth, int atlasMaxHeight, boolean usePOTS, double scaleFactor) {
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

    public void run(TextureImagesInfo ti) {
        int atlasCounter = 0;
        String atlasName = null;

        HashMap<String, TextureImage> texImages = ti.getTexImages();
        HashMap<Object, String> object2texCoords = ti.getTexCoordinates();
        HashMap<Object, String> object2texImage = ti.getTexImageURIs();

        HashMap<String, ArrayList<Object>> texImage2objects = new HashMap<String, ArrayList<Object>>();
        HashMap<String, Boolean> acceptedTexImages = new HashMap<String, Boolean>();
        HashMap<String, String> texImageNameMapping = new HashMap<String, String>();
        HashMap<Object, double[]> texCoordsList = new HashMap<Object, double[]>();

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
                    texImages.remove(texImageName);
                    texImages.put(mapping, texImage);
                    texImageNameMapping.put(texImageName, mapping);
                } else
                    continue;
            }

            // update texture image mapping
            texImageName = mapping;
            object2texImage.put(objectId, mapping);

            // check whether texture image could be read
            if (texImage == null && (texImage = texImages.get(texImageName)) == null)
                continue;

            // check whether texture image can be decoded
            if (texImage.getBufferedImage() == null) {
                acceptedTexImages.put(texImageName, false);
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

                    // remove the texture image from the packer
                    if (hasFourChannels) {
                        packer4C.removeRegion(texImageName);
                    } else {
                        packer3C.removeRegion(texImageName);
                    }

                    continue;
                }

                // texture coordinates could be successfully parsed. add this object to the list
                // of objects affected by this texture image
                texCoordsList.put(objectId, texCoords);
                texImage2objects.get(texImageName).add(objectId);
                continue;
            }

            // rescale image if requested
            if (scaleFactor != 1)
                texImage.setImage(ImageProcessor.rescale(texImage.getBufferedImage(), scaleFactor));

            // check texture coordinates of object
            double[] texCoords = checkAndGetTexCoordinates(object2texCoords.get(objectId));
            if (texCoords == null) {
                acceptedTexImages.put(texImageName, false);
                continue;
            }

            // rescale texture image if it exceeds the maximum width or height of the atlas
            if (!imageFitsIntoAtlas(texImage.getBufferedImage())) {
                BufferedImage scaledImage = ImageProcessor.rescale(texImage.getBufferedImage(), atlasMaxWidth, atlasMaxHeight);
                if (scaledImage == null || !imageFitsIntoAtlas(scaledImage)) {
                    acceptedTexImages.put(texImageName, false);
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
            int width = texImage.getWidth();
            int height = texImage.getHeight();

            if (hasFourChannels) {
                packer4C.addRegion(texImageName, width, height);
            } else {
                packer3C.addRegion(texImageName, width, height);
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
            atlasMR.add(pack(packer3C));
        if (packer4C.getRegions() != 0)
            atlasMR.add(pack(packer4C));

        // for all available atlases modify the coordinates and image names
        for (TextureAtlas atlas : atlasMR) {
            if (Math.min(atlas.getBindingBoxHeight(), atlas.getBindingBoxWidth()) < 1)
                continue;

            // create atlas image
            boolean hasFourChannels = atlas.hasFourChannels();
            BufferedImage atlasImage = new BufferedImage(
                    Math.min(getMinCoveredPOT(atlas.getBindingBoxWidth()), atlasMaxWidth),
                    Math.min(getMinCoveredPOT(atlas.getBindingBoxHeight()), atlasMaxHeight),
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
                    texImage.setImage(ImageProcessor.rotate(texImage.getBufferedImage()));
                }

                // check whether the current atlas is full
                if (currentLevel != region.level) {
                    int potW = getMinCoveredPOT(atlasWidth);
                    int potH = getMinCoveredPOT(atlasHeight);

                    // put atlas into texture image map
                    texImages.put(String.format(atlasName, atlasCounter) + (hasFourChannels ? "png" : "jpeg"),
                            new TextureImage(atlasImage.getSubimage(0, 0, potW, potH)));

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
                        new TextureImage(atlasImage.getSubimage(0, 0, potW, potH)));

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

    private int getMinCoveredPOT(int len) {
        if (!usePOTS)
            return len;

        int minPOT = (int) Math.floor(Math.log10(len) / Math.log10(2));
        if (Math.pow(2, minPOT) == len)
            return len;

        return (int) Math.pow(2, minPOT + 1);
    }

    private boolean imageFitsIntoAtlas(BufferedImage image) {
        return (image.getWidth() <= atlasMaxWidth && image.getHeight() <= atlasMaxHeight);
    }

    private TextureAtlas pack(Packer packer) {
        packer.setBinSize(atlasMaxWidth, atlasMaxHeight);
        return packer.pack(usePOTS);
    }

    private double[] checkAndGetTexCoordinates(String coordinates) {
        if (coordinates == null || coordinates.length() == 0)
            return null;

        String[] sc = coordinates.split(" ");
        if ((sc.length & 1) == 1)
            return null;

        double[] c = new double[sc.length];
        for (int i = 0; i < sc.length; i++) {
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
                                         HashMap<String, ArrayList<Object>> texImage2objects,
                                         int atlasWidth, int atlasHeigth) {
        for (AtlasRegion region : frame) {
            for (Object obj : texImage2objects.get(region.getTexImageName())) {
                double[] texCoords = doubleCoordinateList.get(obj);
                StringBuilder builder = new StringBuilder();

                for (int j = 0; j < texCoords.length; j += 2) {
                    if (region.isRotated) {
                        double tmp = texCoords[j];
                        texCoords[j] = 1 - texCoords[j + 1];
                        texCoords[j + 1] = tmp;
                    }

                    texCoords[j] = (region.x + (texCoords[j] * region.width)) / atlasWidth;
                    texCoords[j + 1] = 1 - ((1 - texCoords[j + 1]) * region.height + region.y) / atlasHeigth;

                    builder.append(texCoords[j]);
                    builder.append(' ');
                    builder.append(texCoords[j + 1]);
                    builder.append(' ');
                }

                object2texCoords.put(obj, builder.substring(0, builder.length() - 1));
            }

            region.clear();
        }
    }

}
