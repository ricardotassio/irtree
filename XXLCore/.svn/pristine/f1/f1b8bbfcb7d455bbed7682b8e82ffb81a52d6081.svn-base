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
 * of <tt>short</tt> values. First the converter reads or writes the
 * length of the <tt>short</tt> array. Thereafter the <tt>short</tt>
 * values are read or written.<p>
 *
 * Example usage (1).
 * <pre>
 *     // create a short array
 *
 *     short [] array = {1024, 255, 1923, 0};
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
 *         ShortArrayConverter.DEFAULT_INSTANCE.write(new DataOutputStream(output), array);
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
 *         array = (short[])ShortArrayConverter.DEFAULT_INSTANCE.read(new DataInputStream(input));
 *
 *         // print the array
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
public class ShortArrayConverter extends Converter {

	/**
	 * This instance can be used for getting a default instance of
	 * ShortArrayConverter. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns, Prototype
	 * in <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of ShortArrayConverter.
	 */
	public static final ShortArrayConverter DEFAULT_INSTANCE = new ShortArrayConverter();

	/**
	 * Sole constructor. (For invocation by subclass constructors,
	 * typically implicit.)
	 */
	public ShortArrayConverter() {
	}

	/**
	 * Reads an array of <tt>short</tt> values from the specified data
	 * input and returns the restored <tt>short</tt> array. <br>
	 * This implementation ignores the specified <tt>short</tt> array and
	 * returns a new array of <tt>short</tt> values. So it does not matter
	 * when the specified array is <tt>null</tt>.
	 *
	 * @param dataInput the stream to read the <tt>short</tt> array from.
	 * @param object the <tt>short</tt> array to be restored. In this
	 *        implementation it is ignored.
	 * @return the read array of <tt>short</tt> values.
	 * @throws IOException if I/O errors occur.
	 */
	public Object read (DataInput dataInput, Object object) throws IOException {
		short [] array = new short[dataInput.readInt()];

		for (int i=0; i<array.length; i++)
			array[i] = dataInput.readShort();
		return array;
	}

	/**
	 * Writes the specified array of <tt>short</tt> values to the
	 * specified data output. <br>
	 * This implementation first writes the length of the array to the
	 * data output. Thereafter the <tt>short</tt> values are written.
	 *
	 * @param dataOutput the stream to write the <tt>short</tt> array to.
	 * @param object the <tt>short</tt> array that should be written to
	 *        the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (DataOutput dataOutput, Object object) throws IOException {
		short [] array = (short [])object;

		dataOutput.writeInt(array.length);
		for (int i=0; i<array.length; i++)
			dataOutput.writeShort(array[i]);
	}

	/**
	 * Determines the size of the short array in bytes.
	 * @param o short array
	 * @return the size in bytes
	 * @see xxl.core.io.converters.SizeConverter#getSerializedSize(java.lang.Object)
	 */
	public int getSerializedSize(Object o) {
		return 4+2*((short [])o).length;
	}

	/**
	 * The main method contains some examples how to use a
	 * ShortArrayConverter. It can also be used to test the functionality
	 * of a ShortArrayConverter.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create a short array
		short [] array = {1024, 255, 1923, 0};
		// catch IOExceptions
		try {
			// create a byte array output stream
			java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
			// write array to the output stream
			ShortArrayConverter.DEFAULT_INSTANCE.write(new java.io.DataOutputStream(output), array);
			// create a byte array input stream on the output stream
			java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(output.toByteArray());
			// reset the array
			array = null;
			// read array from the input stream
			array = (short[])ShortArrayConverter.DEFAULT_INSTANCE.read(new java.io.DataInputStream(input));
			// print the array
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