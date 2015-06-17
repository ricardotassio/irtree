/* XXL: The eXtensible and fleXible Library for data processing

Copyright (C) 2000-2004 Prof. Dr. Bernhard Seeger
                        Head of the Database Research Group
                        Department of Mathematics and Computer Science
                        University of Marburg
                        Germany

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307,
USA

	http://www.xxl-library.de

bugs, requests for enhancements: request@xxl-library.de

If you want to be informed on new versions of XXL you can 
subscribe to our mailing-list. Send an email to 
	
	xxl-request@lists.uni-marburg.de

without subject and the word "subscribe" in the message body. 
*/

package xxl.core.spatial.cursors;

import java.io.File;

import xxl.core.cursors.Cursor;
import xxl.core.cursors.sources.io.FileInputCursor;
import xxl.core.functions.Function;
import xxl.core.spatial.KPE;
import xxl.core.spatial.rectangles.DoublePointRectangle;

/**
 *	The KPEInputCursor constructs an Iterator of KPEs
 *	for a given flat File containing KPE-Objects.
 *
 *  @see xxl.core.spatial.KPE
 *
 *
 */
public class KPEInputCursor extends FileInputCursor {

	/** Creates a new KPEInputCursor.
	 *
	 *	@param file the flat file containing the input-data
	 *	@param bufferSize the buffer-size to be allocated for reading the data
	 *	@param dim the dimensionality of the input-KPEs
	 */
	public KPEInputCursor(File file, int bufferSize, final int dim){
		super(new Function(){
			public Object invoke(){
				return new KPE(new DoublePointRectangle(dim));
			}
		}, file, bufferSize);
	}

	/** Creates a new KPEInputCursor.
	 *
	 *  The bufferSize is set to 4096 bytes.
	 *
	 *	@param file the file containing the input-data
	 *	@param dim the dimensionality of the input-KPEs
	 */
	public KPEInputCursor(File file, final int dim){
		this(file, 4096, dim);
	}

	/** use-case: read KPEs from a flat file.
	 *
	 *	@param args: args[0] file name, args[1] dimension
	 */
	public static void main(String[] args){

		if(args.length != 2){
			System.out.println("usage: java xxl.core.spatial.cursors.KPEInputCursor <file-name> <dim>");
			return;	
		}

		Cursor cursor = new KPEInputCursor(new File(args[0]), 1024*1024, Integer.parseInt(args[1]));

		while(cursor.hasNext()){
			System.out.println(cursor.next());
		}
		cursor.close();
	}
}
