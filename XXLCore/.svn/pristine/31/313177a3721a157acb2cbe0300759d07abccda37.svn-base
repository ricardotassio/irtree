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
import java.io.Serializable;

/**
 * The class Converter is used for serializing objects that cannot
 * implement the Convertable interface (e.g., predefined classes of the
 * SDK). Like the Convertable interface it prevents the two drawbacks of
 * the SDK serialization mechanism:
 * <ul>
 * <li>it causes overhead by writing additional data like the identity of
 *     class to the output stream.
 * <li>it does not accept raw data input streams because it expects the
 *     written additional data.<p>
 * </ul>
 *
 * The <tt>write</tt> and <tt>read</tt> methods give an implementation of
 * Converter complete control over the format and contents of the stream
 * for an object and its supertypes. These methods must explicitly
 * coordinate with the supertype to save its state.
 *
 * @see java.io.DataInput
 * @see java.io.DataOutput
 * @see java.io.IOException
 */
public abstract class Converter implements Serializable {

	/**
	 * Sole constructor. (For invocation by subclass constructors,
	 * typically implicit.)
	 */
	public Converter() {
	}

	/**
	 * Reads the state (the attributes) for the specified object from the
	 * specified data input and returns the restored object. The state
	 * of the specified object before the call of <tt>read</tt> will be
	 * lost. When <tt>object == null</tt> a new object should be created
	 * and restored.<br>
	 * The <tt>read</tt> method must read the values in the same sequence
	 * and with the same types as were written by <tt>write</tt>.
	 *
	 * @param dataInput the stream to read data from in order to restore
	 *        the object.
	 * @param object the object to be restored. When <tt>object ==
	 *        null</tt> a new object should be created and restored.
	 * @return the restored object.
	 * @throws IOException if I/O errors occur.
	 */
	public abstract Object read (DataInput dataInput, Object object) throws IOException;

	/**
	 * Creates a new object by reading the state (the attributes) from
	 * the specified data input and returns the restored object.
	 * The <tt>read</tt> method must read the values in the same sequence
	 * and with the same types as were written by <tt>write</tt>.
	 *
	 * @param dataInput the stream to read data from in order to restore
	 *        the object.
	 * @return the restored object.
	 * @throws IOException if I/O errors occur.
	 */
	public Object read (DataInput dataInput) throws IOException {
		return read(dataInput, null);
	}

	/**
	 * Writes the state (the attributes) of the specified object to the
	 * specified data output. This method should serialize the state of
	 * this object without calling another <tt>write</tt> method in order
	 * to prevent recursions.
	 *
	 * @param dataOutput the stream to write the state (the attributes) of
	 *        the object to.
	 * @param object the object whose state (attributes) should be written
	 *        to the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public abstract void write (DataOutput dataOutput, Object object) throws IOException;
}