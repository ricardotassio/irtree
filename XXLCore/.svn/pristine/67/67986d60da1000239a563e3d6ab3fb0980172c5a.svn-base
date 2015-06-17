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
import xxl.core.spatial.points.DoublePoint;
import xxl.core.spatial.points.FixedPoint;
import xxl.core.spatial.points.FloatPoint;
import xxl.core.spatial.points.Points;

/**
 * The PointInputCursor constructs an Iterator of Points
 * for a given flat File of Point-Objects.
 *
 */
public class PointInputCursor extends FileInputCursor{

	/**
	 * DOUBLE_POINT is a constant that indicates the dimensions of  a point being of type double
	 */
	public static final int DOUBLE_POINT = 0;
	/**
	 * FLOAT_POINT is a constant that indicates the dimensions of  a point being of type float
	 */	
	public static final int FLOAT_POINT  = 1;
	/**
	 * FIXED_POINT is a constant that indicates the dimensions of a point being of type
	 * bitsequence.
	 */
	public static final int FIXED_POINT  = 2;
	
	/**
	 * The method newDoublePoint creates a factory for DoublePoint.
	 * @param dim dim refers to the dimension of the point
	 * @return a functional factory for generating new points
	 */
	public static Function newDoublePoint(final int dim) {
		return new Function() {
			public Object invoke(){
				return new DoublePoint(dim);
			}
		};
	}
	
	/**
	 * The method newFloatPoint creates a factory for FloatPoint.
	 * @param dim dim refers to the dimension of the point
	 * @return a functional factory for generating new points
	 */
	public static Function newFloatPoint(final int dim) {
		return new Function() {
			public Object invoke(){
				return new FloatPoint(dim);
			}
		};
	}
	
	/**
	 * The method newFixedPoint creates a factory for FixedPoint.
	 * @param dim dim refers to the dimension of the point
	 * @return a functional factory for generating new points
	 */
	public static Function newFixedPoint(final int dim) {
		return new Function() {
			public Object invoke(){
				return new FixedPoint(dim);
			}
		};
	}
	
	/** Creates a new PointInputCursor.
	 *
	 *  @param newPoint the factory for creating a new point
	 * 	@param file the file containing the input-data
	 *	@param bufferSize the buffer-size to be allocated for reading the data
	 */
	public PointInputCursor(Function newPoint, File file, int bufferSize){
		super(newPoint, file, bufferSize);
	}

	/** Creates a new PointInputCursor. The bufferSize is set to 1MB bytes.
	 *
	 *	@param file the file containing the input-data
	 *  @param newPoint the factory for creating a new point
	*/
	public PointInputCursor(Function newPoint, File file){
		this(newPoint, file, 1024*1024);
	}
	
	/**
	 * 
	 * @param file the file containing the input data
	 * @param TYPE the type of the dimension of the point (must be 0,1 or 2)
	 * @param dim the dimension of the point
	 * @param bufferSize the buffer-size to be allocated for reading the data
	 */
	public PointInputCursor(File file, int TYPE, int dim, int bufferSize) {
		this(TYPE == 0 ? newDoublePoint(dim) : TYPE == 1 ? newFloatPoint(dim) : TYPE == 2 ? newFixedPoint(dim) : null,
			file, bufferSize
		);
		if (TYPE < 0 || TYPE > 2)
			throw new IllegalArgumentException("Undefined type specified.");
	}
	
	/**
	 * A constructor for PointInputCursor where the buffer-size is 1MB
	 * @param file the file containing the input data
	 * @param TYPE the type of the dimension of the point (must be 0,1 or 2)
	 * @param dim the dimension of the point
	 */
	public PointInputCursor(File file, int TYPE, int dim) {
		this(file, TYPE, dim, 1024*1024);
	}

	/**	Use-case. Read float-points from a flat file
	 *
	 *	@param args: args[0] file-name, args[1] dimensionality of the input-data
	*/
	public static void main(String[] args){

		if(args.length != 2){
			System.out.println("usage: java xxl.core.spatial.cursors.PointInputCursor <file-name> <dim>");
			return;	
		}

		//get an InputIterator for input-Points:
		Cursor c = Points.newFloatPointInputCursor( new File(args[0]), Integer.parseInt(args[1]) );

		while(c.hasNext()){
			System.out.println(c.next());
		}
		c.close();
		
	}
}

