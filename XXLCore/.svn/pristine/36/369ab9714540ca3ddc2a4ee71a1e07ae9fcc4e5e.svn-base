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

package xxl.core.collections.bags;

import xxl.core.cursors.Cursor;
import xxl.core.functions.Function;

/**
 * The interface LIFO bag represents a LIFO (<i>last in, first out</i>)
 * iteration over the elements of a bag. This interface predefines a
 * <i>LIFO strategy</i> for addition of elements.
 *
 * @see Function
 */
public interface LIFOBag extends Bag {

	/**
	 * Returns a cursor representing a LIFO (<i>last in, first out</i>)
	 * iteration over the elements in this bag. The cursor is specifying
	 * a <i>view</i> on the elements of this bag so that closing the
	 * cursor takes no effect on the bag (e.g., not closing the bag). The
	 * behavior of the cursor is unspecified if this bag is modified while
	 * the cursor is in progress in any way other than by calling the
	 * methods of the cursor. So, when the implementation of this cursor
	 * cannot guarantee that the cursor is in a valid state after modifing
	 * the underlying bag every method of the cursor except
	 * <tt>close()</tt> should throw a
	 * <tt>ConcurrentModificationException</tt>.
	 *
	 * @return a cursor representing a LIFO (<i>last in, first out</i>)
	 *         iteration over the elements in this bag.
	 */
	public abstract Cursor lifoCursor ();
}