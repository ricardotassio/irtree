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

package xxl.core.spatial.converters;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import xxl.core.cursors.Cursor;
import xxl.core.cursors.sources.io.FileInputCursor;
import xxl.core.functions.Function;
import xxl.core.io.Convertables;
import xxl.core.io.converters.Converter;
import xxl.core.spatial.points.DoublePoint;
import xxl.core.spatial.points.FloatPoint;

/**
 *	An AsciiPointConverter can be used to convert Point-Objects that are stored in
 *	ASCII-format to a binary representation.
 *
 *	The data is assumed to contain fields separated by whitespaces.
 *	The first field contains the ID (or key) of the point which is assumed to be an integer.
 *	The remaining dim fields contain the actual scalar values of the Point which are converted
 *	using a call to <tt>java.lang.Double.parseDouble()</tt>.
 *	
 *  @see xxl.core.spatial.points.Point
 *  @see xxl.core.spatial.points.FloatPoint
 *  @see xxl.core.spatial.points.DoublePoint
 *
 */
public class AsciiPointConverter extends Converter{

	/** Dimension of a point
	*/
	protected int dim;

	/** The mapping used to create output-points
	*/
	protected Function outputMapping;
	
	/** Creates a new AsciiPointConverter.
	 *
	 *	@param dim dimension of the data
	 *	@param outputMapping a binary Function providing a output mapping. This mapping is applied to each object read from the DataInput.
	 */
	public AsciiPointConverter(int dim, Function outputMapping){
		this.dim = dim;
		this.outputMapping = outputMapping;
	}

	/** Creates a new AsciiPointConverter. Provides a default implementation of the outputMapping
	 *  which returns a FloatPoint.
	 *
	 *	@param dim dimension of the data
	 *  @see xxl.core.spatial.points.FloatPoint
	 */
	public AsciiPointConverter(int dim){
		this(dim,
			new Function(){
				public Object invoke(Object point, Object ID){
					return new FloatPoint( new DoublePoint( (double[]) point) );
				}
			}		
		);
	}

	/** Reads an Object from the given DataInput.
	 *
	 * @param dataInput source dataInput
	 * @param object the Object for which attributes should be
	 *		  read. If object==null the method will create a
	 *		  new Object;
	 *	@return the Object that was 'filled' with data from the given DataInput is
	 *		returned.
	 * @throws IOException if an I/O error occurs.
	 */
	public Object read (DataInput dataInput, Object object) throws IOException{
		double[] p = new double[dim];
		String s;
		do{
			s=dataInput.readLine();
			if(s==null)
				throw new EOFException();
		}while( s.startsWith("#") );
		
		StringTokenizer st = new StringTokenizer(s);

		int ID = Integer.parseInt(st.nextToken());

		for(int i=0; i<dim; i++){
			p[i] = Double.parseDouble(st.nextToken());
		}

		return outputMapping.invoke(p, new Integer(ID));
	}

	/** Writes an Object to the given DataOutput (unsupported operation).
	 * @param dataOutput destination dataOutput object
	 * @param object an object to write
	 * @throws IOException if an I/O error occurs.
	*/
	public void write (DataOutput dataOutput, Object object) throws IOException{
		throw new UnsupportedOperationException();
	}

	/**
	 *	Convertes an input-file of Ascii-points to an output-file of ConvertableTuple.
	 *  @param args array of <tt>String</tt> arguments. It can be used to
	 * 		   submit parameters when the main method is called.
	 *	       args[0] is the input-file name, args[1] - dimensionality, 
	 *         args[2] - output-file name
	 */
	public static void main(String[] args) {

		if(args.length != 3){
			System.out.println("usage: java xxl.core.spatial.converts.AsciiPointConverter <input file-name> <dim> <output file-name>");
			return;	
		}

		Cursor c = new FileInputCursor(new AsciiPointConverter(Integer.parseInt(args[1])), new File(args[0]), 4096*1024);

		System.out.println("No. of elements converted:\t"+  Convertables.write( args[2], c ) );
		c.close();
	}
}
