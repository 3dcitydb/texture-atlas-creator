package org.citygml.textureAtlasAPI.imageIO;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class RGBEncoder {

	public RGBEncoder() {

	}

	public BufferedImage readRGB(String fileName) throws Exception {
		return readRGB(new File(fileName));
	}

	int[] tmpA = new int[4];

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

	public void decodeNormalLine(byte[] allData, int start, int len,
			byte channelShift, int[] result, boolean reset) {
		// if reset, clear the data.
		max = start + len;
		rCounter = 0;

		if (!reset) {
			while (start < max) {
				tmp = allData[start++] & 0xff;
				tmp = tmp << channelShift;
				result[rCounter] = result[rCounter] | tmp;
				rCounter++;
			}
		} else {// reset
			while (start < max) {
				tmp = allData[start++] & 0xff;
				tmp = tmp << channelShift;
				result[rCounter] = channelShift > 16 ? (int) tmp
						: (int) tmp | 0xff000000;
				rCounter++;
			}
		}

	}

	int max, count, rCounter, tmp;

	public void decodeRLE(byte[] allData, int start, int len,
			byte channelShift, int[] result, boolean reset) {
		// if reset, clear the data.
		max = start + len;
		rCounter = 0;

		if (!reset) {// not reset!
			while (start < max) {
				tmp = allData[start++] & 0xff;
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
				tmp = allData[start++] & 0xff;
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

	byte[][] ChannelsShifts = { {},// free
			{},// B/W
			{},// ?
			{ 16, 8, 0 },// RGB
			{ 16, 8, 0, 24 } // RGBA

	};
	
	BufferedImage bi;
	public BufferedImage readRGB(File file) throws Exception {
		if (!file.exists())
			return null;
		return readRGB(new FileInputStream(file),(int)file.length());
		
	}
	
	public BufferedImage readRGB(InputStream is, int fileLength)throws Exception {
		if (is==null)
			return null;
		
		RGBHeader header = readHeader(is);
//		System.out.println(header.toString());
		bi = new BufferedImage(header.xSize, header.ySize,
				header.chanels==4?BufferedImage.TYPE_INT_ARGB:BufferedImage.TYPE_INT_RGB);

		
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
					decodeRLE(all, (int) getLong(startTableB,
							(scanline + channel * header.ySize) * 4)
							- tmp, (int) getLong(lengthTableB,
							(scanline + channel * header.ySize) * 4),
							ChannelsShifts[header.chanels][channel], lineData,
							reset);
				else
					decodeNormalLine(all, (scanline + header.ySize * channel)
							* header.xSize, header.xSize,
							ChannelsShifts[header.chanels][channel], lineData,
							reset);
				channel--;
				reset = false;
			}
			bi.setRGB(0, header.ySize - scanline - 1, header.xSize, 1,
					lineData, 0, lineData.length);
			scanline--;
		}
		startTableB = null;
		lengthTableB = null;
		all = null;
		lineData = null;
		
		is.close();
		header=null;
		is=null;
//		System.out.println("finish");
		return bi;
	}

	private RGBHeader readHeader(InputStream fis) {
		byte[] headerR= new byte[512];
		RGBHeader header = new RGBHeader();
		try {
			fis.read(headerR);
			count=0;
			header.isMagic = (getShort(headerR, count) == 474);
			count+=2;
			header.isCompressed = (getByte(headerR, count++) == 1);
			header.BPC = (byte) getByte(headerR, count++);
			header.dimension = (byte) getShort(headerR, count);
			count+=2;
			header.xSize = getShort(headerR, count);
			header.ySize = getShort(headerR, count+2);
			header.chanels = getShort(headerR, count+4);
			count+=98;// skip Pixmin, pixmax, dummy, name
			header.colorMap = getLong(headerR, count);
			headerR = null;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return header;
	}
	public void freeMemory(){
		bi=null;	
	}
}

class RGBHeader {
	boolean isMagic;
	boolean isCompressed;
	byte BPC;
	byte dimension;
	int xSize, ySize;
	int chanels;
	long colorMap;

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