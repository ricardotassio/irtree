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
 * This class provides a converter that is able to read and write
 * <tt>Byte</tt> objects. In addition to the read and write methods that
 * read or write <tt>Byte</tt> objects this class contains readByte and
 * writeByte methods that convert the <tt>Byte</tt> object after reading
 * or before writing it to its primitive <tt>byte</tt> type.<p>
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
 *         // write a Byte and a byte value to the output stream
 *
 *         ByteConverter.DEFAULT_INSTANCE.write(new DataOutputStream(output), new Byte((byte)-1));
 *         ByteConverter.DEFAULT_INSTANCE.writeByte(new DataOutputStream(output), (byte)27);
 *
 *         // create a byte array input stream on the output stream
 *
 *         ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
 *
 *         // read a byte value and a Byte from the input stream
 *
 *         byte b1 = ByteConverter.DEFAULT_INSTANCE.readByte(new DataInputStream(input));
 *         Byte b2 = (Byte)ByteConverter.DEFAULT_INSTANCE.read(new DataInputStream(input));
 *
 *         // print the value and the object
 *
 *         System.out.println(b1);
 *         System.out.println(b2);
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
public class ByteConverter extends FixedSizeConverter {

	/**
	 * This instance can be used for getting a default instance of
	 * ByteConverter. It is similar to the <i>Singleton Design Pattern</i>
	 * (for further details see Creational Patterns, Prototype in
	 * <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of ByteConverter.
	 */
	public static final ByteConverter DEFAULT_INSTANCE = new ByteConverter();

	/**
	 * This field contains the number of bytes needed to serialize the
	 * <tt>byte</tt> value of a <tt>Byte</tt> object. Because this size is
	 * predefined it must not be measured each time.
	 */
	public static final int SIZE = 1;

	/**
	 * Sole constructor. (For invocation by subclass constructors,
	 * typically implicit.)
	 */
	public ByteConverter() {
		super(1);
	}

	/**
	 * Reads the <tt>byte</tt> value for the specified (<tt>Byte</tt>)
	 * object from the specified data input and returns the restored
	 * object. <br>
	 * This implementation ignores the specified object and returns a new
	 * <tt>Byte</tt> object. So it does not matter when the specified
	 * object is <tt>null</tt>.
	 *
	 * @param dataInput the stream to read the <tt>byte</tt> value from in
	 *        order to return a <tt>Byte</tt> object.
	 * @param object the (<tt>Byte</tt>) object to be restored. In this
	 *        implementation it is ignored.
	 * @return the read <tt>Byte</tt> object.
	 * @throws IOException if I/O errors occur.
	 */
	public Object read (DataInput dataInput, Object object) throws IOException {
		return new Byte(dataInput.readByte());
	}

	/**
	 * Reads the <tt>byte</tt> value from the specified data input and
	 * returns it. <br>
	 * This implementation uses the read method and converts the returned
	 * <tt>Byte</tt> object to its primitive <tt>byte</tt> type.
	 *
	 * @param dataInput the stream to read the <tt>byte</tt> value from.
	 * @return the read <tt>byte</tt> value.
	 * @throws IOException if I/O errors occur.
	 */
	public byte readByte (DataInput dataInput) throws IOException {
		return ((Byte)read(dataInput)).byteValue();
	}

	/**
	 * Writes the <tt>byte</tt> value of the specified <tt>Byte</tt>
	 * object to the specified data output. <br>
	 * This implementation calls the write method of the data output with
	 * the <tt>byte</tt> value of the object.
	 *
	 * @param dataOutput the stream to write the <tt>byte</tt> value of
	 *        the specified <tt>Byte</tt> object to.
	 * @param object the <tt>Byte</tt> object that <tt>byte</tt> value
	 *        should be written to the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (DataOutput dataOutput, Object object) throws IOException {
		dataOutput.write(((Byte)object).byteValue());
	}

	/**
	 * Writes the specified <tt>byte</tt> value to the specified data
	 * output. <br>
	 * This implementation calls the write method with a <tt>Byte</tt>
	 * object wrapping the specified <tt>byte</tt> value.
	 *
	 * @param dataOutput the stream to write the specified
	 *        <tt>byte</tt> value to.
	 * @param b the <tt>byte</tt> value that should be written to the data
	 *        output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void writeByte (DataOutput dataOutput, byte b) throws IOException {
		write(dataOutput, new Byte(b));
	}

	/**
	 * The main method contains some examples how to use a ByteConverter.
	 * It can also be used to test the functionality of a ByteConverter.
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
			// write a Byte and a byte value to the output stream
			ByteConverter.DEFAULT_INSTANCE.write(new java.io.DataOutputStream(output), new Byte((byte)-1));
			ByteConverter.DEFAULT_INSTANCE.writeByte(new java.io.DataOutputStream(output), (byte)27);
			// create a byte array input stream on the output stream
			java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(output.toByteArray());
			// read a byte value and a Byte from the input stream
			byte b1 = ByteConverter.DEFAULT_INSTANCE.readByte(new java.io.DataInputStream(input));
			Byte b2 = (Byte)ByteConverter.DEFAULT_INSTANCE.read(new java.io.DataInputStream(input));
			// print the value and the object
			System.out.println(b1);
			System.out.println(b2);
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