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

package xxl.core.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import xxl.core.io.Convertable;
import xxl.core.io.converters.Converter;

/** 
	A Tuple whose elements can be serialized using XXLs Convertable/Converter-mechanism.
*/
public class ConvertableTuple extends AbstractTuple implements Convertable {

	/**
	 * dataConverter array
	 */
	protected Converter[] dataConverter;

	/**
	 * Creates a new Convertable Tuple.
	 * @param objects The array with objects which should be inside the
	 * 	tuple.
	 * @param dataConverter A converter for each row. If a converter
	 * 	is not provides for a certain row, then this row has to contain
	 * 	objects which are Convertable.
	 */
	public ConvertableTuple(Object[] objects, Converter[] dataConverter){
		super(objects);
		this.dataConverter = dataConverter;
	}
	
	/**
	 * Creates a new Tuple which gets the same values
	 * than an existing one (used for cloning).
	 * @param convertableTuple
	 */
	public ConvertableTuple(ConvertableTuple convertableTuple){
		this( (Object[]) convertableTuple.objects.clone(), (Converter[]) convertableTuple.dataConverter.clone() );
	}

	/**
	 * Creates a new empty tuple with length rows.
	 * @param length The number of rows.
	 */
	public ConvertableTuple(int length){
		this( new Object[length], new Converter[length] );
	}

	/**
	 * Clones the tuple.
	 * @return The cloned Object.
	 */
	public Object clone(){
		return new ConvertableTuple( this );
	}

	/**
	 * Updates this tuple at the given index with the given object. <br>
	 *
	 * @param index The dataConverter of this tuple at the given index is being updated.
	 * @param object Updates the dataConverter of this tuple at the given index with the given dataConverter.
	 * @return The given dataConverter.
	 * @throws IndexOutOfBoundsException Throws an {@link java.lang.IndexOutOfBoundsException} if
	 * 		the index is negative or greater than or equal to <code>length()</code>.
	*/
	public Converter setDataConverter (int index, Converter object) throws IndexOutOfBoundsException{
		return dataConverter[index] = object;
	}

	/**
	 * Gets the dataConverter stored at the given index.
	 *
	 * @param index the dataConverter is stored at the specified index.
	 * @return Returns the dataConverter stored at the given index.
	 * @throws IndexOutOfBoundsException Throws an {@link java.lang.IndexOutOfBoundsException} if
	 * 		the index is negative or greater than or equal to <code>length()</code>.
	 */
	public Converter getDataConverter (int index) throws IndexOutOfBoundsException {
		return dataConverter[index];
	}

	/**
	 * Reads the state (the attributes) for an object of this class from
	 * the specified data input and restores the calling object. The state
	 * of the object before calling <tt>read</tt> will be lost.<br>
	 * The <tt>read</tt> method must read the values in the same sequence
	 * and with the same types as were written by <tt>write</tt>.
	 *
	 * @param dataInput the stream to read data from in order to restore
	 *        the object.
	 * @throws IOException if I/O errors occur.
	 */
	public void read (DataInput dataInput) throws IOException {
		for(int i=0; i<length(); i++)
			if (dataConverter[i]!=null)							//if a converter is provided
				set(i, dataConverter[i].read(dataInput, get(i)));	//use the converter to read the data
			else 													//else
				((Convertable)get(i)).read(dataInput);				//try to cast the data-object to type Convertable and call read on that Convertable
	}

	/**
	 * Writes the state (the attributes) of the calling object to the
	 * specified data output. This method should serialize the state of
	 * this object without calling another <tt>write</tt> method in order
	 * to prevent recursions.
	 *
	 * @param dataOutput the stream to write the state (the attributes) of
	 *        the object to.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (DataOutput dataOutput) throws IOException {
		for(int i=0; i<length(); i++)
			if(dataConverter[i] != null)					//if a converter is provided
				dataConverter[i].write(dataOutput, get(i));	//use the converter to write the data
			else											//else
				((Convertable)get(i)).write(dataOutput);	//try to cast the data-object to type Convertable and call write on that Convertable
	}
}
