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

package xxl.core.io.converters;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * This class provides a converter that is able to read and write arrays
 * of <tt>Object</tt>s. Therefor the ObjectArrayConverter needs a
 * converter that is able to read and write the single objects. First the
 * converter reads or writes the length of the <tt>Object</tt> array.
 * Thereafter the objects are read or written.<p>
 *
 * Example usage (1).
 * <pre>
 *     // create an array of 3 object
 *
 *     Object [] array = new Object [3];
 *
 *     // initialize the objects with vectors and fill them with strings
 *
 *     array[0] = new Vector();
 *     ((Vector)array[0]).add("This");
 *     ((Vector)array[0]).add("is");
 *     ((Vector)array[0]).add("a");
 *     ((Vector)array[0]).add("vector.");
 *     array[1] = new Vector();
 *     ((Vector)array[1]).add("This");
 *     ((Vector)array[1]).add("also.");
 *     array[2] = new Vector();
 *     ((Vector)array[2]).add("No");
 *     ((Vector)array[2]).add("it");
 *     ((Vector)array[2]).add("does");
 *     ((Vector)array[2]).add("not");
 *     ((Vector)array[2]).add("really");
 *     ((Vector)array[2]).add("make");
 *     ((Vector)array[2]).add("any");
 *     ((Vector)array[2]).add("sense.");
 *
 *     // create a converter for vectors
 *
 *     Converter converter = new Converter() {
 *
 *         // how to write a vector
 *
 *         public void write (DataOutput dataOutput, Object object) throws IOException {
 *
 *             // write the size of the vector at first
 *
 *             IntegerConverter.DEFAULT_INSTANCE.writeInt(dataOutput, ((Vector)object).size());
 *
 *             // thereafter write the elements of the vector
 *
 *             for (int i = 0; i < ((Vector)object).size(); i++)
 *                 StringConverter.DEFAULT_INSTANCE.write(dataOutput, ((Vector)object).get(i));
 *         }
 *
 *         // how to read a vector
 *
 *         public Object read (DataInput dataInput, Object object) throws IOException {
 *
 *             // create a new vector
 *
 *             Vector vector = new Vector();
 *
 *             // read the size of the vector at first
 *
 *             int size = IntegerConverter.DEFAULT_INSTANCE.readInt(dataInput);
 *
 *             // thereafter read the elements of the vector
 *
 *             for (int i = 0; i < size; i++)
 *                 vector.add(StringConverter.DEFAULT_INSTANCE.read(dataInput));
 *
 *             // return the restored vector
 *
 *             return vector;
 *         }
 *     };
 *
 *     // create an object array converter that is able to read and write arrays of vectors
 *
 *     ObjectArrayConverter arrayConverter = new ObjectArrayConverter(converter);
 *
 *     // catch IOExceptions
 *
 *     try {
 *
 *         // create a byte array output stream
 *
 *         ByteArrayOutputStream output = new ByteArrayOutputStream();
 *
 *         // write array to the output stream
 *
 *         arrayConverter.write(new DataOutputStream(output), array);
 *
 *         // create a byte array input stream on the output stream
 *
 *         ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
 *
 *         // reset the array
 *
 *         array = null;
 *
 *         // read array from the input stream
 *
 *         array = (Object[])arrayConverter.read(new DataInputStream(input));
 *
 *         // print the array (the data of the vectors)
 *
 *         for (int i = 0; i < array.length; i++)
 *             System.out.println(array[i]);
 *
 *         // close the streams after use
 *
 *         input.close();
 *         output.close();
 *     }
 *     catch (IOException ioe) {
 *         System.out.println("An I/O error occured.");
 *     }
 * </pre>
 *
 * @see DataInput
 * @see DataOutput
 * @see IOException
 */
public class ObjectArrayConverter extends Converter {

	/**
	 * This instance can be used for getting a default instance of
	 * ObjectArrayConverter. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns, Prototype
	 * in <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of ObjectArrayConverter.
	 */
	public static final ObjectArrayConverter DEFAULT_INSTANCE = new ObjectArrayConverter();

	/**
	 * The field <tt>converter</tt> refers to a converter that is able to
	 * read and write the single objects.
	 */
	protected Converter converter;

	/**
	 * Constructs a new ObjectArrayConverter that is able to read and
	 * write <tt>Object</tt> arrays. The specified converter is used for
	 * reading and writing the single objects.
	 *
	 * @param converter a converter that is able to read and write the
	 *        single objects.
	 */
	public ObjectArrayConverter (Converter converter) {
		this.converter = converter;
	}

	/**
	 * Constructs a new ObjectArrayConverter that is able to read and
	 * write <tt>Object</tt> arrays. Because the ConvertableConverter is
	 * used for reading and writing the single objects, this converter can
	 * only read and write array of objects that implement the Convertable
	 * interface.
	 */
	public ObjectArrayConverter () {
		this(ConvertableConverter.DEFAULT_INSTANCE);
	}

	/**
	 * Reads an array of <tt>Object</tt>s from the specified data input
	 * and returns the restored <tt>Object</tt> array. <br>
	 * In case that the specified object is not <tt>null</tt>, this
	 * implementation calls the converter with each object of this array
	 * on reading.
	 *
	 * @param dataInput the stream to read the <tt>Object</tt> array from.
	 * @param object the <tt>Object</tt> array to be restored. When it is
	 *        null a new <tt>Object</tt> array is created.
	 * @return the restored array of <tt>Object</tt>s.
	 * @throws IOException if I/O errors occur.
	 */
	public Object read (DataInput dataInput, Object object) throws IOException {
		int size = dataInput.readInt();
		Object [] array = object==null? new Object[size]: (Object[])object;

		for (int i=0; i<array.length; i++)
			array[i] = converter.read(dataInput, array[i]);
		return array;
	}

	/**
	 * Writes the specified array of <tt>Object</tt>s to the specified
	 * data output. <br>
	 * This implementation first writes the length of the array to the
	 * data output. Thereafter the <tt>Object</tt>s are written by calling
	 * the write method of the internally used converter.
	 *
	 * @param dataOutput the stream to write the <tt>Object</tt> array to.
	 * @param object the <tt>Object</tt> array that should be written to
	 *        the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (DataOutput dataOutput, Object object) throws IOException {
		Object [] array = (Object [])object;
		dataOutput.writeInt(array.length);
		for (int i=0; i<array.length; i++)
			converter.write(dataOutput, array[i]);
	}

	/**
	 * The main method contains some examples how to use an
	 * ObjectArrayConverter. It can also be used to test the functionality
	 * of an ObjectArrayConverter.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create an array of 3 object
		Object [] array = new Object [3];
		// initialize the objects with vectors and fill them with strings
		array[0] = new java.util.Vector();
		((java.util.Vector)array[0]).add("This");
		((java.util.Vector)array[0]).add("is");
		((java.util.Vector)array[0]).add("a");
		((java.util.Vector)array[0]).add("vector.");
		array[1] = new java.util.Vector();
		((java.util.Vector)array[1]).add("This");
		((java.util.Vector)array[1]).add("also.");
		array[2] = new java.util.Vector();
		((java.util.Vector)array[2]).add("No");
		((java.util.Vector)array[2]).add("it");
		((java.util.Vector)array[2]).add("does");
		((java.util.Vector)array[2]).add("not");
		((java.util.Vector)array[2]).add("really");
		((java.util.Vector)array[2]).add("make");
		((java.util.Vector)array[2]).add("any");
		((java.util.Vector)array[2]).add("sense.");
		// create a converter for vectors
		Converter converter = new Converter() {
			// how to write a vector
			public void write (DataOutput dataOutput, Object object) throws IOException {
				// write the size of the vector at first
				IntegerConverter.DEFAULT_INSTANCE.writeInt(dataOutput, ((java.util.Vector)object).size());
				// thereafter write the elements of the vector
				for (int i = 0; i<((java.util.Vector)object).size(); i++)
					StringConverter.DEFAULT_INSTANCE.write(dataOutput, ((java.util.Vector)object).get(i));
			}
			// how to read a vector
			public Object read (DataInput dataInput, Object object) throws IOException {
				// create a new vector
				java.util.Vector vector = new java.util.Vector();
				// read the size of the vector at first
				int size = IntegerConverter.DEFAULT_INSTANCE.readInt(dataInput);
				// thereafter read the elements of the vector
				for (int i = 0; i<size; i++)
					vector.add(StringConverter.DEFAULT_INSTANCE.read(dataInput));
				// return the restored vector
				return vector;
			}
		};
		// create an object array converter that is able to read and write
		// arrays of vectors
		ObjectArrayConverter arrayConverter = new ObjectArrayConverter(converter);
		// catch IOExceptions
		try {
			// create a byte array output stream
			java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
			// write array to the output stream
			arrayConverter.write(new java.io.DataOutputStream(output), array);
			// create a byte array input stream on the output stream
			java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(output.toByteArray());
			// reset the array
			array = null;
			// read array from the input stream
			array = (Object[])arrayConverter.read(new java.io.DataInputStream(input));
			// print the array (the data of the vectors)
			for (int i = 0; i<array.length; i++)
				System.out.println(array[i]);
			// close the streams after use
			input.close();
			output.close();
		}
		catch (IOException ioe) {
			System.out.println("An I/O error occured.");
		}
		System.out.println();
	}
}