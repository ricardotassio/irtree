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
import xxl.core.cursors.Cursors;
import xxl.core.cursors.sources.io.FileInputCursor;
import xxl.core.functions.Function;
import xxl.core.spatial.rectangles.DoublePointRectangle;

/**
 *	The RectangleInputIterator constructs an Iterator of rectangles
 *	for a given flat File of Rectangle-Objects.
 *  <br><br>
 *  The flat file is assumed to contain rectangles based on DoublePoints.
 *  <br><br>
 *  Implementation:
 *
 *	<code><pre>
 *	public RectangleInputIterator(File file, int bufferSize, final int dim){
 *		super(new Function(){
 *			public Object invoke(){
 *				return new DoublePointRectangle(dim);
 *			}
 *		}, file, bufferSize);
 *	}
 *	</code></pre>
 *
 * <br><br>
 *	Use-case: (create iterator of rectangles, i.e. read rectangless from a flat file)
 *	<code><pre>
 *	public static void main(String[] args){
 *		
 *		Iterator it = new RectangleInputIterator(new File(args[0]), 1024*1024, Integer.parseInt(args[1]));
 *
 *		Cursors.println(it);	//print elements of Cursor to standard output
 *	}
 *	</code></pre>
 *
 *
 *  @see xxl.core.spatial.rectangles.Rectangles
 *  @see xxl.core.spatial.points.DoublePoint
 *
 */
public class RectangleInputIterator extends FileInputCursor {

	/** Creates a new RectangleInputIterator.
     *
	 *	@param file the file containing the input-data
	 *	@param bufferSize the buffer-size to be allocated for reading the data
	 *	@param dim the dimensionality of the input-Rectangles
	 */
	public RectangleInputIterator(File file, int bufferSize, final int dim){
		super(
			new Function(){
				public Object invoke(){
					return new DoublePointRectangle(dim);
				}
			},
			file, bufferSize
		);
	}

	/** use-case: read rectangles and print them to standard out
	 *
	 * @param args: args[0] file-name, args[1] dimension of the data
	 */
	public static void main(String[] args){

		if(args.length != 2){
			System.out.println("usage: java xxl.core.spatial.cursors.RectangleInputIterator <file-name> <dim>");
			return;	
		}

		Cursor c = new RectangleInputIterator(new File(args[0]), 1024*1024, Integer.parseInt(args[1]));

		Cursors.println(c);	//print elements of Cursor to standard output
		c.close();
	}
}
