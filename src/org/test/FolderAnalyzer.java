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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;

public class FolderAnalyzer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
try{
			
			Stack<File> openFolders= new Stack<File>();
			Stack<String> gmlFiles= new Stack<String>();
			File folder= new File(args[0]);
			
			Hashtable< String, Long> resultSize= new Hashtable<String, Long>();
			Hashtable< String, Long> resultCount= new Hashtable<String, Long>();  
			Long num;
			openFolders.push(folder);
			int i;
			File[] list;
			String suffix;
			// scan the openFolders to find entire GML files and push all of them to gmlFiles stack.
			while(!openFolders.empty()){
				folder = openFolders.pop();
				if (folder.isDirectory()){
					list =folder.listFiles();
					if (list==null)
						continue;
					for (i=0;i<list.length;i++){
						if (list[i].isDirectory()){
							openFolders.push(list[i]);
							suffix="folder";
						}
						else{
							suffix =list[i].getName().substring(list[i].getName().lastIndexOf('.'),list[i].getName().length());
						}
							num = resultCount.get(suffix);
							if (num==null)
								num= new Long(0);
							resultCount.put(suffix, new Long(num.longValue()+1));
							num =  resultSize.get(suffix);
							if (num==null)
								num= new Long(0);
							resultSize.put(suffix, new Long(num.longValue()+list[i].length()));
																
							
						
					}
					list=null;
				}
			}
			Iterator<String> iter = resultSize.keySet().iterator() ; 
		    String name;
		     while ( iter.hasNext (  )  )  {  
		    	 name =iter.next();
		       System.out.println ( " Size  "+name+ " , " +((Long)resultSize.get(name)).longValue()) ; 
		      }  

				Iterator<String> iter2 = resultCount.keySet().iterator() ; 
			   
			     while ( iter2.hasNext (  )  )  {  
			    	 name =iter2.next();
			       System.out.println ( " Count  "+name+ " , " +((Long)resultCount.get(name)).longValue()) ; 
			      }  

			
		}catch (Exception e){
			e.printStackTrace();
			
		}	
		

	}

}
