3D City Database Texture Atlas Creator v1.2 

  This software is free software and is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. 
  
0. Index
--------

1. License
2. Copyright
3. About
4. Developers
5. Contact
6. Websites
7. Disclaimer


1. License
----------

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this software except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0


2. Copyright
------------

(C) 2013 - 2016
Chair of Geoinformatics
Technical University of Munich, Germany
https://www.gis.bgu.tum.de/


3. About
--------
The 3D City Database Texture Atlas Creator is a library for compacting several
texture images together into one or several atlases (depending on 
atlas size settings) and adapting texture coordinates accordingly. 

As a result the overall size of the resources will be significantly decreased.
 
Feature list:
	* In each run, group textures, which have similar properties,
	  to one or more texture atlases.
	* Maximum atlas size as an input.
	* Three different packing algorithms.
	* Different image formats as input like RGB, JPEG, PNG. Oracle's OrdImage 
	is also supported.
	* Different image output formats based on transparency of its content.

Different packing algorithms:
	TPIM:
		TPIM is customized version of Touching Perimeter algorithm 
		as a heuristic two-dimensional bin packing with support of rotation. 
		During tests 83.35% of result atlas was occupied. This algorithm is 
		based on source code developed and released to the public by Jukka Jylänki.
					
		TPIM starts by sorting the items according to non-increasing area and 
		their normal orientation. It initializes a bin with maximume acceptable 
		size and packs one item at a time. In the case that it is not possible 
		to add a new item to the current bin, a new bin will be initialized. The first 
		item packed in a bin is always placed in the bottom left corner. However 
		in the result atlas the origin will be in the top left corner. Each item is 
		packed in a way that its bottom and left edges touching either the the bin 
		or the edge of another item.

		Each potential position for the new item will be scored as the amount 
		of its touching edges. Touching the bin edges is more valuable to avoid 
		inhomogeneous shape of bin. For each candidate (position) the score will be 
		calculated twice (normal orientation and 90 degree rotated) and the highest 
		value will be taken.
		 
		For more information about Touching Perimeter algorithm please refer to:
		Lodi A, Martello S, Vigo D. Heuristic and Metaheuristic Approaches 
		for a Class of Two-Dimensional Bin Packing Problems. INFORMS J on 
		Computing 1999;11:345-357.
		
	TPIM_WITHOUT_ROTATION:
		It is an extension of TPIM algorithm which does not rotate textures.
			
	BASIC:
		An implementation the lightmap packing algorithm presented by 
		Jim Scott at http://www.blackpawn.com/texts/lightmaps/.

RGB Endocder:
	The SGI RGB Image format is supported by this API. An encoder is developed based on file format
	specification version 1.00 written by Paul Haeberli from Silicon Graphics Computer Systems.
	The encoder supports most of RGB images, but not all of them. For more information about the
	file format please refer to http://paulbourke.net/dataformats/sgirgb/sgiversion.html (active 
	in 2011).
	
	  
4. Developers
-------------

Claus Nagel <cnagel@virtualcitysystems.de>
Babak Naderi


5. Contact
----------

cnagel@virtualcitysystems.de


6. Websites
-----------

Official 3D City Database website: 
http://www.3dcitydb.org/

Related websites:
https://github.com/3dcitydb/
https://www.gis.bgu.tum.de/
http://www.citygml.org/
http://www.citygmlwiki.org/
http://www.opengeospatial.org/standards/citygml


7. Disclaimer
-------------

THIS SOFTWARE IS PROVIDED BY THE CHAIR OF GEOINFORMATION FROM TU MUNICH
(TUMGI) "AS IS" AND "WITH ALL FAULTS." 
TUMGI MAKES NO REPRESENTATIONS OR WARRANTIES OF ANY KIND CONCERNING THE 
QUALITY, SAFETY OR SUITABILITY OF THE SOFTWARE, EITHER EXPRESSED OR 
IMPLIED, INCLUDING WITHOUT LIMITATION ANY IMPLIED WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT.

TUMGI MAKES NO REPRESENTATIONS OR WARRANTIES AS TO THE TRUTH, ACCURACY OR 
COMPLETENESS OF ANY STATEMENTS, INFORMATION OR MATERIALS CONCERNING THE 
SOFTWARE THAT IS CONTAINED ON AND WITHIN ANY OF THE WEBSITES OWNED AND 
OPERATED BY TUMGI.

IN NO EVENT WILL TUMGI BE LIABLE FOR ANY INDIRECT, PUNITIVE, SPECIAL, 
INCIDENTAL OR CONSEQUENTIAL DAMAGES HOWEVER THEY MAY ARISE AND EVEN IF 
TUMGI HAVE BEEN PREVIOUSLY ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.