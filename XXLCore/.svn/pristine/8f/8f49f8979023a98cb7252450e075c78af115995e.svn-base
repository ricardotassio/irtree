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

import xxl.core.functions.Function;
import xxl.core.io.Convertable;

/**
 * This class provides a converter that is able to read and write objects
 * that implements the Convertable interface. The objects are read and
 * written by calling their read and write methods. When reading an object
 * a factory method can be used for creating the object instead of
 * specifying it.<p>
 *
 * Example usage (1).
 * <pre>
 *     // catch IOExceptions
 *
 *     try {
 *
 *         // create a byte array output stream
 *
 *         ByteArrayOutputStream output = new ByteArrayOutputStream();
 *
 *         // create two convertable objects (bit sets)
 *
 *         BitSet b1 = new BitSet(13572l);
 *         BitSet b2 = new BitSet(-1l);
 *
 *         // create a factory method for bit sets
 *
 *         Function factory = new Function() {
 *             public Object invoke() {
 *                 return new BitSet();
 *             }
 *         };
 *
 *         // create a new convertable converter that converts bit sets
 *
 *         ConvertableConverter converter = new ConvertableConverter(factory);
 *
 *         // write the bit sets to the output stream
 *
 *         converter.write(new DataOutputStream(output), b1);
 *         converter.write(new DataOutputStream(output), b2);
 *
 *         // create a byte array input stream on the output stream
 *
 *         ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
 *
 *         // read the bit sets from the input stream and compare it to the original bit sets
 *
 *         System.out.println(b1.compareTo(converter.read(new DataInputStream(input))));
 *         System.out.println(b2.compareTo(converter.read(new DataInputStream(input))));
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
 * @see Function
 * @see IOException
 */
public class ConvertableConverter extends Converter {

	/**
	 * This instance can be used for getting a default instance of
	 * ConvertableConverter. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns, Prototype
	 * in <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of ConvertableConverter.
	 */
	public static final ConvertableConverter DEFAULT_INSTANCE = new ConvertableConverter();

	/**
	 * A factory method that is used for initializing the object to read.
	 * This function will be invoked when the read method is called
	 * without specifying an object to restore.
	 */
	protected Function function;


	/**
	 * Constructs a new ConvertableConverter and uses the specified
	 * function as factory method. For this reason the object to read must
	 * not be specified when calling the read method. In this case the
	 * object is initialized be invoking the specified function.
	 *
	 * @param function a factory method that is used for initializinge the
	 *       object to read when it is not specified.
	 */
	public ConvertableConverter (Function function) {
		this.function = function;
	}

	/**
	 * Constructs a new ConvertableConverter without a factory method.
	 * For this reason the objects to read must be explicitly specified
	 * when calling the read method.
	 */
	public ConvertableConverter () {
		this(null);
	}

	/**
	 * Reads the state (the attributes) for the specified object from the
	 * specified data input and returns the restored object. <br>
	 * This implementation uses the specified object (that implements the
	 * convertable interface) to call its read method. When this object is
	 * <tt>null</tt> it is initialized by invoking the function (factory
	 * method).
	 *
	 * @param dataInput the stream to read data from in order to restore
	 *        the object.
	 * @param object the object to be restored. If the object is null it
	 *        is initialized by invoking the function (factory method).
	 * @return the restored object.
	 * @throws IOException if I/O errors occur.
	 * @throws NullPointerException when the given object is null and no
	 *         factory method is specified.
	 */
	public Object read (DataInput dataInput, Object object) throws IOException {
		if (object==null) {
			if (function==null)
				throw new NullPointerException("missing factory method");
			object = function.invoke();
		}
		((Convertable)object).read(dataInput);        //fill the object with data
		return object;
	}

	/**
	 * Writes the state (the attributes) of the specified object to the
	 * specified data output. <br>
	 * This implementation calls the write method of the specified object
	 * (that implements the convertable interface).
	 *
	 * @param dataOutput the stream to write the state (the attributes) of
	 *        the object to.
	 * @param object the object whose state (attributes) should be written
	 *        to the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (DataOutput dataOutput, Object object) throws IOException {
		((Convertable)object).write(dataOutput);
	}

	/**
	 * The main method contains some examples how to use a
	 * ConvertableConverter. It can also be used to test the functionality
	 * of a ConvertableConverter.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// catch IOExceptions
		try {
			// create a byte array output stream
			java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
			// create two convertable objects (bit sets)
			xxl.core.util.BitSet b1 = new xxl.core.util.BitSet(13572l);
			xxl.core.util.BitSet b2 = new xxl.core.util.BitSet(-1l);
			// create a factory method for bit sets
			Function factory = new Function() {
				public Object invoke() {
					return new xxl.core.util.BitSet();
				}
			};
			// create a new convertable converter that converts bit sets
			ConvertableConverter converter = new ConvertableConverter(factory);
			// write the bit sets to the output stream
			converter.write(new java.io.DataOutputStream(output), b1);
			converter.write(new java.io.DataOutputStream(output), b2);
			// create a byte array input stream on the output stream
			java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(output.toByteArray());
			// read the bit sets from the input stream and compare it to
			// the original bit sets
			System.out.println(b1.compareTo(converter.read(new java.io.DataInputStream(input))));
			System.out.println(b2.compareTo(converter.read(new java.io.DataInputStream(input))));
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