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
package org.citygml.textureAtlasAPI.imageIO;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

//import org.citygml.util.Logger;

/**
 * This is an encoder for the SGI RGB Image format. 
 * It is developed base on file format specification version 1.00 written by Paul Haeberli 
 * from Silicon Graphics Computer Systems. The encoder supports most of RGB images, but not 
 * all of them. For more information about the file format please refer to 
 * http://paulbourke.net/dataformats/sgirgb/sgiversion.html (active in 2011).
 * 
 */
public class RGBEncoder {
	private int[] tmpA = new int[4];
	private byte[][] channelsShifts = { {},// free
			{0},// B/W
			{},// ?
			{ 16, 8, 0 },// RGB
			{ 16, 8, 0, 24 } // RGBA
	};

	public RGBEncoder() {

	}

	public BufferedImage readRGB(String fileName) throws IOException {
		return readRGB(new File(fileName));
	}

	private long getLong(byte[] mb, int offset) {
		tmpA[0] = 0xFF & (int) mb[offset];
		tmpA[1] = 0xFF & (int) mb[offset + 1];
		tmpA[2] = 0xFF & (int) mb[offset + 2];
		tmpA[3] = 0xFF & (int) mb[offset + 3];

		long l2 = ((long) (tmpA[0] << 24 | tmpA[1] << 16 | tmpA[2] << 8 | tmpA[3]));
		return l2;
	}

	private int getShort(byte[] mb, int offset) {
		tmpA[0] = 0xFF & (int) mb[offset];
		tmpA[1] = 0xFF & (int) mb[offset + 1];

		int l1 = (int) (tmpA[0] << 8 | tmpA[1]);
		return l1;
	}

	private int getByte(byte[] mb, int offset){
		return 0xFF & (int) mb[offset];
	}

	public void decodeNormalLine(RGBHeader header, byte[] allData, int start, int len, byte channelShift, int[] result, boolean reset) {
		// if reset, clear the data.
		int max = start + len;
		int rCounter = 0;
		
		if (header.chanels == 1){
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

	public void decodeRLE(byte[] allData, int start, int len, byte channelShift, int[] result, boolean reset) {
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
						result[rCounter] = channelShift > 16 ? (int) tmp
								: (int) tmp | 0xff000000;
						rCounter++;
						count--;
					}
				} else {// copy next byte count times.
					tmp = (int) allData[start++] & 0xff;
					tmp = tmp << channelShift;
					while (count != 0) {
						result[rCounter] = channelShift > 16 ? (int) tmp
								: (int) tmp | 0xff000000;
						rCounter++;
						count--;
					}
				}
			}
		}
		//		System.err.println("terminated by me!");
		return;

	}
	
	public BufferedImage readRGB(File file) throws IOException {
		if (!file.exists() || !file.canRead())
			return null;

		return readRGB(new FileInputStream(file), (int)file.length());
	}

	public BufferedImage readRGB(InputStream is, int fileLength) throws IOException {
		if (is == null)
			return null;

		BufferedImage bi = null;
		RGBHeader header = readHeader(is);
		if (header.BPC == 2 || // 2 byte per pixel 
				!header.isMagic ||// it is not RGB
				header.dimension==1 // one row of data
				) {
			// not supported
			return null;
		}

		switch(header.chanels) {
		case 1:bi = new BufferedImage(header.xSize, header.ySize, BufferedImage.TYPE_BYTE_GRAY);break;
		case 3:bi = new BufferedImage(header.xSize, header.ySize, BufferedImage.TYPE_INT_RGB);break;
		case 4:bi = new BufferedImage(header.xSize, header.ySize, BufferedImage.TYPE_INT_ARGB);break;
		default: return null;
		}

		int tmp = 0;
		byte[] startTableB = null;
		byte[] lengthTableB = null;
		if (header.isCompressed) {
			tmp = 4 * header.ySize * header.chanels;
			startTableB = new byte[tmp];
			lengthTableB = new byte[tmp];
			is.read(startTableB);
			is.read(lengthTableB);
		}
		tmp = tmp * 2 + 512;

		byte[] all = new byte[fileLength - tmp];
		is.read(all, 0, all.length);

		int[] lineData = new int[header.xSize];
		int scanline = header.ySize - 1;
		byte channel;
		boolean reset;

		while (scanline >= 0) {
			channel = (byte) (header.chanels - 1);
			reset = true;
			while (channel >= 0) {
				if (header.isCompressed)
					decodeRLE(all, 
							(int) getLong(startTableB, (scanline + channel * header.ySize) * 4) - tmp,
							(int) getLong(lengthTableB, (scanline + channel * header.ySize) * 4),
							channelsShifts[header.chanels][channel], 
							lineData,
							reset);
				else
					decodeNormalLine(header, 
							all, 
							(scanline + header.ySize * channel) * header.xSize, 
							header.xSize,
							channelsShifts[header.chanels][channel], 
							lineData,
							reset);
				channel--;
				reset = false;
			}
			bi.setRGB(0, header.ySize - scanline - 1, header.xSize, 1,
					lineData, 0, lineData.length);
			scanline--;
		}

		is.close();
		header=null;
		return bi;
	}

	private RGBHeader readHeader(InputStream fis) {
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
			header.chanels = getShort(headerR, count+4);
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

	protected class RGBHeader {
		boolean isMagic;
		boolean isCompressed;
		byte BPC;
		byte dimension;
		int xSize, ySize;
		int chanels;
		long colorMap;
		long pimin;
		long pimax;
		long pSize;

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("isMagic:");
			sb.append(isMagic);
			sb.append("\r\n");

			sb.append("isCompressed:");
			sb.append(isCompressed);
			sb.append("\r\n");

			sb.append("BPC:");
			sb.append(BPC);
			sb.append("\r\n");

			sb.append("dimension:");
			sb.append(dimension);
			sb.append("\r\n");

			sb.append("size(x,y):");
			sb.append(xSize + "," + ySize);
			sb.append("\r\n");

			sb.append("chanels:");
			sb.append(chanels);
			sb.append("\r\n");

			sb.append("colorMap:");
			sb.append(colorMap);
			sb.append("\r\n");
			return sb.toString();
		}
	}	
}

