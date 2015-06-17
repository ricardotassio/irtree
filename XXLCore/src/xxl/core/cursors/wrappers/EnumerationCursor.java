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

package xxl.core.cursors.wrappers;

import java.util.Enumeration;

import xxl.core.cursors.AbstractCursor;

/**
 * This class provides a wrapper for {@link java.util.Enumeration enumerations},
 * i.e., an emumeration can be accessed via the
 * {@link xxl.core.cursors.Cursor cursor} interface. The constructor of this
 * class takes an enumeration and wraps it to a cursor. The cursor functionality
 * is projected on the enumeration, therefore the methods <tt>remove</tt>,
 * <tt>update</tt> and <tt>reset</tt> are not supported.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 * @see java.util.Enumeration
 */
public class EnumerationCursor extends AbstractCursor {

	/**
	 * The internally used enumeration that is wrapped to a cursor.
	 */
	protected Enumeration enumeration;

	/**
	 * Creates a new enumeration-cursor.
	 *
	 * @param enumeration the enumeration to be wrapped to a cursor.
	 */
	public EnumerationCursor(Enumeration enumeration) {
		this.enumeration = enumeration;
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the enumeration-cursor has more elements.
	 */
	protected boolean hasNextObject() {
		return enumeration.hasMoreElements();
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the enumeration-cursor's methods, e.g.,
	 * <tt>update</tt> or <tt>remove</tt>, until a call to <tt>next</tt> or
	 * <tt>peek</tt> occurs. This is calling <tt>next</tt> or <tt>peek</tt>
	 * proceeds the iteration and therefore its previous element will not be
	 * accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return enumeration.nextElement();
	}
	
}
