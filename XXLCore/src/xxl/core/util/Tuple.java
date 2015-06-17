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

/**
	A tuple interface.
*/
public interface Tuple extends Cloneable{

	/**
	 * Returns the number of columns (i.e. the length of the tuple, cardinality).
	 *
	 * @return the length of the tuple (tuple's cardinality).
	 */
	public abstract int length ();

	/**
	 * Updates this tuple at the given index with the given object. <br>
	 *
	 * @param index The object of this tuple at the given index is being updated.
	 * @param object Updates the object of this tuple at the given index with the give object.
	 * @return The given object.
	 * @throws IndexOutOfBoundsException Throws an {@link java.lang.IndexOutOfBoundsException} if
	 * 		the index is negative or greater than or equal to <code>length()</code>.
	*/
	public abstract Object set (int index, Object object) throws IndexOutOfBoundsException;

	/**
	 * Gets the object stored at the given index.
	 *
	 * @param index the object is stored at the specified index.
	 * @return Returns the object stored at the given index.
	 * @throws IndexOutOfBoundsException Throws an {@link java.lang.IndexOutOfBoundsException} if
	 * 		the index is negative or greater than or equal to <code>length()</code>.
	 */
	public abstract Object get (int index) throws IndexOutOfBoundsException;

	/**
	 * Creates the copy of this Tuple
	 * @return returns copy of the Tuple
	 */
	public abstract Object clone();
}
