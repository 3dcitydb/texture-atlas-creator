/*
 * 3D City Database Texture Atlas Creator
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2016
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.gis.bgu.tum.de/
 * 
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 * 
 * virtualcitySYSTEMS GmbH, Berlin <http://www.virtualcitysystems.de/>
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
package org.citydb.textureAtlas.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

/**
 * This is an encoder for the SGI RGB Image format. 
 * It is developed base on file format specification version 1.00 written by Paul Haeberli 
 * from Silicon Graphics Computer Systems. The encoder supports most of RGB images, but not 
 * all of them. For more information about the file format please refer to 
 * http://paulbourke.net/dataformats/sgirgb/sgiversion.html (active in 2011).
 * 
 */
public class RGBEncoder {
	private static RGBEncoder instance = null;
	
	private RGBEncoder() {
		
	}
	
	public static synchronized RGBEncoder getInstance() {
		if (instance == null)
			instance = new RGBEncoder();
		
		return instance;
	}

	public BufferedImage readRGB(ImageInputStream is) throws IOException {
		if (is == null)
			return null;

		BufferedImage bi = null;
		RGBHeader header = readHeader(is);
		if (header.BPC == 2 || // 2 byte per pixel 
				!header.isMagic ||// it is not RGB
				header.dimension == 1 // one row of data
				) {
			return null;
		}

		switch(header.channels) {
		case 1: bi = new BufferedImage(header.xSize, header.ySize, BufferedImage.TYPE_BYTE_GRAY); break;
		case 3: bi = new BufferedImage(header.xSize, header.ySize, BufferedImage.TYPE_INT_RGB); break;
		case 4: bi = new BufferedImage(header.xSize, header.ySize, BufferedImage.TYPE_INT_ARGB); break;
		default: return null;
		}

		byte[][] channelsShifts = { {},// free
				{0},// B/W
				{},// ?
				{ 16, 8, 0 },// RGB
				{ 16, 8, 0, 24 } // RGBA
		};

		int tmp = 0;
		byte[] startTableB = null;
		byte[] lengthTableB = null;
		if (header.isCompressed) {
			tmp = 4 * header.ySize * header.channels;
			startTableB = new byte[tmp];
			lengthTableB = new byte[tmp];
			is.read(startTableB);
			is.read(lengthTableB);
		}
		tmp = tmp * 2 + 512;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) != -1)
			bos.write(buffer, 0 , length);

		byte[] all = bos.toByteArray();	

		int[] lineData = new int[header.xSize];
		int scanline = header.ySize - 1;
		byte channel;
		boolean reset;

		while (scanline >= 0) {
			channel = (byte) (header.channels - 1);
			reset = true;
			while (channel >= 0) {
				if (header.isCompressed)
					decodeRLE(all, 
							(int) getLong(startTableB, (scanline + channel * header.ySize) * 4) - tmp,
							(int) getLong(lengthTableB, (scanline + channel * header.ySize) * 4),
							channelsShifts[header.channels][channel], 
							lineData,
							reset);
				else
					decodeNormalLine(header, 
							all, 
							(scanline + header.ySize * channel) * header.xSize, 
							header.xSize,
							channelsShifts[header.channels][channel], 
							lineData,
							reset);
				channel--;
				reset = false;
			}
			bi.setRGB(0, header.ySize - scanline - 1, header.xSize, 1, lineData, 0, lineData.length);
			scanline--;
		}

		is.close();
		return bi;
	}

	private RGBHeader readHeader(ImageInputStream fis) {
		byte[] headerR= new byte[512];
		RGBHeader header = new RGBHeader();

		try {
			fis.read(headerR);
			int count=0;
			header.isMagic = (getShort(headerR, count) == 474);
			count+=2;
			header.isCompressed = (getByte(headerR, count++) == 1);
			header.BPC = (byte) getByte(headerR, count++);
			header.dimension = (byte) getShort(headerR, count);
			count+=2;
			header.xSize = getShort(headerR, count);
			header.ySize = getShort(headerR, count+2);
			header.channels = getShort(headerR, count+4);
			count+=6;
			header.pimin=getLong(headerR, count);
			header.pimax=getLong(headerR, count+4);
			header.pSize=header.pimax-header.pimin;
			count+=84;// skip dummy, name
			//			count+=98;// skip Pixmin, pixmax, dummy, name
			header.colorMap = getLong(headerR, count);
			headerR = null;
		} catch (IOException e) {
			return null;
		}

		return header;
	}

	private long getLong(byte[] mb, int offset) {
		int[] tmpA = new int[4];
		tmpA[0] = 0xFF & (int) mb[offset];
		tmpA[1] = 0xFF & (int) mb[offset + 1];
		tmpA[2] = 0xFF & (int) mb[offset + 2];
		tmpA[3] = 0xFF & (int) mb[offset + 3];

		long l2 = ((long) (tmpA[0] << 24 | tmpA[1] << 16 | tmpA[2] << 8 | tmpA[3]));
		return l2;
	}

	private int getShort(byte[] mb, int offset) {
		int[] tmpA = new int[4];
		tmpA[0] = 0xFF & (int) mb[offset];
		tmpA[1] = 0xFF & (int) mb[offset + 1];

		int l1 = (int) (tmpA[0] << 8 | tmpA[1]);
		return l1;
	}

	private int getByte(byte[] mb, int offset){
		return 0xFF & (int) mb[offset];
	}

	private void decodeNormalLine(RGBHeader header, byte[] allData, int start, int len, byte channelShift, int[] result, boolean reset) {
		// if reset, clear the data.
		int max = start + len;
		int rCounter = 0;

		if (header.channels == 1){
			while (start < max) {
				int tmp = allData[start++] & 0xff;
				// tmp = (int)(((tmp-header.pimin)*255)/header.pSize);
				result[rCounter] = (tmp << 16) | (tmp << 8) | tmp;
				rCounter++;
			}
			return;
		}

		if (!reset) {
			while (start < max) {
				int tmp = allData[start++] & 0xff;
				tmp = tmp << channelShift;
				result[rCounter] = result[rCounter] | tmp;
				rCounter++;
			}
		} else {// reset
			while (start < max) {
				int tmp = allData[start++] & 0xff;
				tmp = tmp << channelShift;
				result[rCounter] = channelShift > 16 ? (int) tmp : (int) tmp | 0xff000000;
				rCounter++;
			}
		}
	}

	private void decodeRLE(byte[] allData, int start, int len, byte channelShift, int[] result, boolean reset) {
		// if reset, clear the data.
		int max = start + len;
		int rCounter = 0;
		int count;

		if (!reset) {// not reset!
			while (start < max) {
				int tmp = allData[start++] & 0xff;

				if ((count = (int) tmp & 0x7f) == 0)
					return;

				if ((tmp & 0x80) == 0x80) {
					// copy count byte
					while (count != 0) {
						tmp = (int) allData[start++] & 0xff;
						tmp = tmp << channelShift;
						result[rCounter] = result[rCounter] | (int) tmp;
						rCounter++;
						count--;
					}
				} else {// copy next byte count times.
					tmp = (int) allData[start++] & 0xff;
					tmp = tmp << channelShift;
					while (count != 0) {
						result[rCounter] = result[rCounter] | (int) tmp;
						rCounter++;
						count--;
					}
				}
			}
		} else { // reset!
			while (start < max) {
				int tmp = allData[start++] & 0xff;

				if ((count = (int) tmp & 0x7f) == 0)
					return;

				if ((tmp & 0x80) == 0x80) {
					// copy count byte
					while (count != 0) {
						tmp = (int) allData[start++] & 0xff;
						tmp = tmp << channelShift;
						result[rCounter] = channelShift > 16 ? (int) tmp : (int) tmp | 0xff000000;
						rCounter++;
						count--;
					}
				} else {// copy next byte count times.
					tmp = (int) allData[start++] & 0xff;
					tmp = tmp << channelShift;
					while (count != 0) {
						result[rCounter] = channelShift > 16 ? (int) tmp : (int) tmp | 0xff000000;
						rCounter++;
						count--;
					}
				}
			}
		}

		return;

	}

	protected class RGBHeader {
		boolean isMagic;
		boolean isCompressed;
		byte BPC;
		byte dimension;
		int xSize, ySize;
		int channels;
		long colorMap;
		long pimin;
		long pimax;
		long pSize;
	}
	
	public boolean isSupportedMIMEType(String mimeType) {
		if (mimeType == null)
			return false;

		String tmp = mimeType.toUpperCase();
		return ("IMAGE/RGB".equals(tmp) || "IMAGE/X-RGB".equals(tmp) || "IMAGE/RGBA".equals(tmp));
	}

	public boolean isSupportedFileSuffix(String suffix) {
		if (suffix == null)
			return false;

		String tmp = suffix.toUpperCase();
		return ("RGB".equals(tmp) || "RGBA".equals(tmp));
	}
}

