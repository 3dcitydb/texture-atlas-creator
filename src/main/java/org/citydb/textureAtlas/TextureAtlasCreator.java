/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2024
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
package org.citydb.textureAtlas;

import org.citydb.textureAtlas.model.TextureImagesInfo;
import org.citydb.textureAtlas.packer.Modifier;

public class TextureAtlasCreator {
    public static final int BASIC = 1;
    public static final int TPIM = 2;
    public static final int TPIM_WO_ROTATION = 3;

    private int packingAlgorithm;
    private int atlasMaxWidth;
    private int atlasMaxHeight;

    private boolean usePOTS = false;
    private double scaleFactor = 1;

    public TextureAtlasCreator() {
        this(BASIC, 1024, 1024);
    }

    public TextureAtlasCreator(int packingAlgorithm, int atlasMaxWidth, int atlasMaxHeight) {
        this(packingAlgorithm, atlasMaxWidth, atlasMaxHeight, false);
    }

    public TextureAtlasCreator(int packingAlgorithm, int atlasMaxWidth, int atlasMaxHeight, boolean usePOTS) {
        this.atlasMaxHeight = atlasMaxHeight;
        this.atlasMaxWidth = atlasMaxWidth;
        this.usePOTS = usePOTS;
        setPackingAlgorithm(packingAlgorithm);
        scaleFactor = 1;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public int getPackingAlgorithm() {
        return packingAlgorithm;
    }

    public void setPackingAlgorithm(int packingAlgorithm) {
        if (packingAlgorithm < 1 || packingAlgorithm > 3)
            packingAlgorithm = BASIC;

        this.packingAlgorithm = packingAlgorithm;
    }

    public int getAtlasMaxWidth() {
        return atlasMaxWidth;
    }

    public void setAtlasMaxWidth(int atlasMaxWidth) {
        this.atlasMaxWidth = atlasMaxWidth;
    }

    public int getAtlasMaxHeight() {
        return atlasMaxHeight;
    }

    public void setAtlasMaxHeight(int atlasMaxHeight) {
        this.atlasMaxHeight = atlasMaxHeight;
    }

    public void setUsePOTS(boolean usePOTS) {
        this.usePOTS = usePOTS;
    }

    public boolean isUsePOTS() {
        return usePOTS;
    }

    public void convert(TextureImagesInfo tii) {
        convert(tii, packingAlgorithm);
    }

    public void convert(TextureImagesInfo tii, int packingAlgorithm) {
        setPackingAlgorithm(packingAlgorithm);
        Modifier modifier = new Modifier(this.packingAlgorithm, atlasMaxWidth, atlasMaxHeight, usePOTS, scaleFactor);
        modifier.run(tii);
    }
}
