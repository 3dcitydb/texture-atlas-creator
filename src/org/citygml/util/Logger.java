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
package org.citygml.util;

import java.text.DecimalFormat;
import java.util.Calendar;



public class Logger {
	public final static boolean SHOW_STACK_PRINT=false;
	public final static int TYPE_INFO=1;
	public final static int TYPE_ERROR=2;
	public final static int TYPE_NESS=3;
	
	private Calendar cal;
	private DecimalFormat df = new DecimalFormat("00");
	public boolean showDetail=true;
	
	private static Logger INSTANCE = new Logger();
	
	public static Logger getInstance() {
		return INSTANCE;
	}
	
	public  String getPrefix() {
		cal = Calendar.getInstance();

		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		
		

		StringBuffer prefix = new StringBuffer();
		prefix.append("[");
		prefix.append(df.format(h));
		prefix.append(":");
		prefix.append(df.format(m));
		prefix.append(":");
		prefix.append(df.format(s));
		prefix.append("] ");

		return prefix.toString();
	}
	
	public  void log(int type,String log){
		if ((type==TYPE_INFO && this.showDetail)|| type==TYPE_NESS)
			System.out.println(getPrefix()+log);
		else if (type==TYPE_ERROR){
			System.out.println(getPrefix()+log);
		}
	}
	
	public void setShowDetail(boolean b){
		this.showDetail=b;
	}
}
