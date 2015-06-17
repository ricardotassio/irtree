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
import java.math.BigDecimal;

/**
 * This class provides a converter that is able to read and write
 * <tt>BigDecimal</tt> objects. 
 *
 * Example usage (1).
 * <pre>
 *	// create a byte array output stream
 *	java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
 *
 *	// write two values to the output stream
 *	BigDecimalConverter.DEFAULT_INSTANCE.write(new java.io.DataOutputStream(output), new BigDecimal("2.7236512"));
 *	BigDecimalConverter.DEFAULT_INSTANCE.write(new java.io.DataOutputStream(output), new BigDecimal("6.123853"));
 *
 *	// create a byte array input stream on the output stream
 *	java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(output.toByteArray());
 *
 *	// read the values from the input stream
 *	BigDecimal d1 = (BigDecimal)BigDecimalConverter.DEFAULT_INSTANCE.read(new java.io.DataInputStream(input));
 *	BigDecimal d2 = (BigDecimal)BigDecimalConverter.DEFAULT_INSTANCE.read(new java.io.DataInputStream(input));
 *
 *	// print the objects
 *	System.out.println(d1);
 *	System.out.println(d2);
 *
 *	// close the streams after use
 *	input.close();
 *	output.close();
 * </pre>
 *
 * @see DataInput
 * @see DataOutput
 * @see IOException
 */
public class BigDecimalConverter extends Converter {

	/**
	 * This instance can be used for getting a default instance of
	 * BigDecimalConverter. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns, Prototype
	 * in <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of BigDecimalConverter.
	 */
	public static final BigDecimalConverter DEFAULT_INSTANCE = new BigDecimalConverter();

	/**
	 * Sole constructor. (For invocation by subclass constructors,
	 * typically implicit.)
	 */
	public BigDecimalConverter() {
	}

	/**
	 * Reads the <tt>BigDecimal</tt> value for the specified (<tt>BigDecimal</tt>)
	 * object from the specified data input and returns the restored
	 * object. <br>
	 * This implementation ignores the specified object and returns a new
	 * <tt>BigDecimal</tt> object. So it does not matter when the specified
	 * object is <tt>null</tt>.
	 *
	 * @param dataInput the stream to read the <tt>BigDecimal</tt> value from
	 *        in order to return a <tt>BigDecimal</tt> object.
	 * @param object the (<tt>BigDecimal</tt>) object to be restored. In this
	 *        implementation it is ignored.
	 * @return the read <tt>BigDecimal</tt> object.
	 * @throws IOException if I/O errors occur.
	 */
	public Object read (DataInput dataInput, Object object) throws IOException {
		return new BigDecimal(dataInput.readUTF());
	}

	/**
	 * Writes the <tt>BigDecimal</tt> value of the specified <tt>BigDecimal</tt>
	 * object to the specified data output. <br>
	 *
	 * @param dataOutput the stream to write the <tt>double</tt> value of
	 *        the specified <tt>Double</tt> object to.
	 * @param object the <tt>BigDecimal</tt> object that <tt>BigDecimal</tt> value
	 *        should be written to the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (DataOutput dataOutput, Object object) throws IOException {
		dataOutput.writeUTF(((BigDecimal)object).toString());
	}

	/**
	 * The main method contains some examples how to use a
	 * BigDecimalConverter. It can also be used to test the functionality
	 * of a BigDecimalConverter.
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
			// write two values to the output stream
			BigDecimalConverter.DEFAULT_INSTANCE.write(new java.io.DataOutputStream(output), new BigDecimal("2.7236512"));
			BigDecimalConverter.DEFAULT_INSTANCE.write(new java.io.DataOutputStream(output), new BigDecimal("6.123853"));
			// create a byte array input stream on the output stream
			java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(output.toByteArray());
			// read the values from the input stream
			BigDecimal d1 = (BigDecimal)BigDecimalConverter.DEFAULT_INSTANCE.read(new java.io.DataInputStream(input));
			BigDecimal d2 = (BigDecimal)BigDecimalConverter.DEFAULT_INSTANCE.read(new java.io.DataInputStream(input));
			// print the objects
			System.out.println(d1);
			System.out.println(d2);
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
