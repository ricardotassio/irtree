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

/**
 * This class provides a wrapper for a converter that reads and writes
 * uniform objects. Whenever the read method is called with an object that
 * is equal to <tt>null</tt>, the underlying converter is called with an
 * object that has been delivered by invoking function.<p>
 *
 * Example usage (1).
 * <pre>
 *     // catch IOExceptions
 *
 *     try {
 *
 *         // create two map entries
 *
 *         MapEntry me1 = new MapEntry(new Integer(42), "Hello world.");
 *         MapEntry me2 = new MapEntry(new Integer(4711), "That's all, folks!");
 *
 *         // create a converter that only stores the value of a map entry
 *
 *         Converter converter = new Converter() {
 *
 *             // how to write a map entry
 *
 *             public void write (DataOutput dataOutput, Object object) throws IOException {
 *
 *                 // write the value of the map entry
 *
 *                 SerializableConverter.DEFAULT_INSTANCE.write(dataOutput, ((MapEntry)object).getValue());
 *             }
 *
 *             // how to read a map entry
 *
 *             public Object read (DataInput dataInput, Object object) throws IOException {
 *
 *                 // read the value of the map entry
 *
 *                 ((MapEntry)object).setValue(SerializableConverter.DEFAULT_INSTANCE.read(dataInput));
 *                 return object;
 *             }
 *         };
 *
 *         // create a factory method that produces map entries with keys of increasing integer
 *         // objects
 *
 *         Function factory = new Function() {
 *
 *             // a count for the returned keys
 *
 *             int i = 0;
 *
 *             // how to create a map entry
 *
 *             public Object invoke () {
 *
 *                 // return a map entry with an integer wrapping the counter as key and null as
 *                 // value
 *
 *                 return new MapEntry(new Integer(i++), null);
 *             }
 *         };
 *
 *         // create an uniform converter with ...
 *
 *         UniformConverter uniformConverter = new UniformConverter(
 *
 *             // the created converter
 *
 *             converter,
 *
 *             // the created factory method
 *
 *             factory
 *         );
 *
 *         // create a byte array output stream
 *
 *         ByteArrayOutputStream output = new ByteArrayOutputStream();
 *
 *         // write two strings to the output stream
 *
 *         uniformConverter.write(new ObjectOutputStream(output), me1);
 *         uniformConverter.write(new ObjectOutputStream(output), me2);
 *
 *         // create a byte array input stream on the output stream
 *
 *         ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
 *
 *         // read two strings from the input stream
 *
 *         me1 = (MapEntry)uniformConverter.read(new ObjectInputStream(input));
 *         me2 = (MapEntry)uniformConverter.read(new ObjectInputStream(input));
 *
 *         // print the value and the object
 *
 *         System.out.println(me1.getKey()+" & "+me1.getValue());
 *         System.out.println(me2.getKey()+" & "+me2.getValue());
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
public class UniformConverter extends Converter {

	/**
	 * The converter that is wrapped. The converter must be able to read
	 * and write uniform objects.
	 */
	protected Converter converter;

	/**
	 * A factory method that is used for initializing the object to read.
	 * This function will be invoked when the read method is called
	 * without specifying an object to restore.
	 */
	protected Function function;

	/**
	 * Constructs a new UniformConverter that wraps the specified
	 * converter and uses the specified function as factory method.
	 *
	 * @param converter the converter to be wrapped.
	 * @param function a factory method that is used for initializing the
	 *       object to read when it is not specified.
	 */
	public UniformConverter (Converter converter, Function function) {
		this.converter = converter;
		this.function = function;
	}

	/**
	 * Reads the state (the attributes) for the specified object from the
	 * specified data input and returns the restored object. <br>
	 * This implementation calls the read method of the wrapped converter.
	 * When the specified object is <tt>null</tt> it is initialized by
	 * invoking the function (factory method).
	 *
	 * @param dataInput the stream to read data from in order to restore
	 *        the object.
	 * @param object the object to be restored. If the object is null it
	 *        is initialized by invoking the function (factory method).
	 * @return the restored object.
	 * @throws IOException if I/O errors occur.
	 */
	public Object read (DataInput dataInput, Object object) throws IOException {
		return converter.read(dataInput, object==null && function!=null? function.invoke(): object);
	}

	/**
	 * Writes the state (the attributes) of the specified object to the
	 * specified data output. <br>
	 * This implementation calls the write method of the wrapped
	 * converter.
	 *
	 * @param dataOutput the stream to write the state (the attributes) of
	 *        the object to.
	 * @param object the object whose state (attributes) should be written
	 *        to the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (DataOutput dataOutput, Object object) throws IOException {
		converter.write(dataOutput, object);
	}

	/**
	 * The main method contains some examples how to use an
	 * UniformConverter. It can also be used to test the functionality of
	 * an UniformConverter.
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
			// create two map entries
			xxl.core.collections.MapEntry me1 = new xxl.core.collections.MapEntry(new Integer(42), "Hello world.");
			xxl.core.collections.MapEntry me2 = new xxl.core.collections.MapEntry(new Integer(4711), "That's all, folks!");
			// create a converter that only stores the value of a map
			// entry
			Converter converter = new Converter() {
				// how to write a map entry
				public void write (DataOutput dataOutput, Object object) throws IOException {
					// write the value of the map entry
					SerializableConverter.DEFAULT_INSTANCE.write(dataOutput, ((xxl.core.collections.MapEntry)object).getValue());
				}
				// how to read a map entry
				public Object read (DataInput dataInput, Object object) throws IOException {
					// read the value of the map entry
					((xxl.core.collections.MapEntry)object).setValue(SerializableConverter.DEFAULT_INSTANCE.read(dataInput));
					return object;
				}
			};
			// create a factory method that produces map entries with keys
			// of increasing integer objects
			Function factory = new Function() {
				// a count for the returned keys
				int i = 0;
				// how to create a map entry
				public Object invoke () {
					// return a map entry with an integer wrapping the
					// counter as key and null as value
					return new xxl.core.collections.MapEntry(new Integer(i++), null);
				}
			};
			// create an uniform converter with ...
			UniformConverter uniformConverter = new UniformConverter(
				// the created converter
				converter,
				// the created factory method
				factory
			);
			// create a byte array output stream
			java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
			// write two strings to the output stream
			uniformConverter.write(new java.io.ObjectOutputStream(output), me1);
			uniformConverter.write(new java.io.ObjectOutputStream(output), me2);
			// create a byte array input stream on the output stream
			java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(output.toByteArray());
			// read two strings from the input stream
			me1 = (xxl.core.collections.MapEntry)uniformConverter.read(new java.io.ObjectInputStream(input));
			me2 = (xxl.core.collections.MapEntry)uniformConverter.read(new java.io.ObjectInputStream(input));
			// print the value and the object
			System.out.println(me1.getKey()+" & "+me1.getValue());
			System.out.println(me2.getKey()+" & "+me2.getValue());
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