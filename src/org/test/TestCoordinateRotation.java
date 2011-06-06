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
package org.test;

public class TestCoordinateRotation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String coord ="1.0 4.44089209850063E-16 1.0 1.0 -1.13686837721616E-13 1.0 -1.13686837721616E-13 4.44089209850063E-16 1.0 4.44089209850063E-16";
		System.out.println("++>"+coord);
		double[] cl = formatCoordinates(coord);
		String result = getCoordinate(cl, 0, 0, 100, 100, 100, 100, true);
		System.out.println("-->"+result);
		

	}
	private static double[] formatCoordinates(String coordinates){
		if (coordinates==null)
			return null;
		String[] sc = coordinates.split(" ");
		double[]c= new double[sc.length];
		for (int i=0;i<sc.length;i++){
			c[i] = Double.parseDouble(sc[i]);
//			if (c[i]<-0.0005||c[i]>1.0005){
			if (c[i]<-0.1||c[i]>1.1){
				sc=null;
				return null;
			}
		}
		sc=null;
		coordinates=null;
		return c;
	}

	private static String getCoordinate(double[]coordinates, double posX, double posY, double imW,double imH ,double atlasw, double atlasH, boolean rotated){
		
		StringBuffer sb = new StringBuffer(coordinates.length*15);
		double tmp;
		if (rotated){
			System.out.println("++++ rotated");
			tmp = imW;
			imW=imH;
			imH=tmp;
		}
		
		for (int j = 0; j < coordinates.length; j += 2) {
			if (rotated){
				tmp =coordinates[j];
				coordinates[j]= coordinates[j+1];
				coordinates[j+1]= 1-tmp;
			}
			// Horizontal
			coordinates[j] = (posX+(coordinates[j] * imW))/atlasw;
			// corner as a origin,but cityGML used left down corner.
			coordinates[j + 1] =1-((1-coordinates[j+1])*imH+posY)/atlasH; 
			sb.append(coordinates[j]);
			sb.append(' ');
			sb.append(coordinates[j+1]);
			sb.append(' ');	
		}
		
		return sb.substring(0, sb.length()-1);
	}
}
