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

import java.util.Iterator;

import xxl.core.cursors.AbstractCursor;

/**
 * The iterator-cursor wraps an {@link java.util.Iterator iterator} to a cursor,
 * i.e., the wrapped iterator can be accessed via the
 * {@link xxl.core.cursors.Cursor cursor} interface. An iterator-cursor tries to
 * pass all method calls to the underlying iterator. If an operation is not
 * supported by the underlying iterator (<tt>update</tt> and <tt>reset</tt>) an
 * {@link java.lang.UnsupportedOperationException} is thrown.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 */
public class IteratorCursor extends AbstractCursor {

	/**
	 * The internally used iterator that is wrapped to a cursor.
	 */
	protected Iterator iterator;

	/**
	 * Creates a new iterator-cursor.
	 *
	 * @param iterator the iterator to be wrapped to a cursor.
	 */
	public IteratorCursor (Iterator iterator) {
		this.iterator = iterator;
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the iterator-cursor has more elements.
	 */
	protected boolean hasNextObject() {
		return iterator.hasNext();
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the iterator-cursor's methods, e.g., <tt>update</tt>
	 * or <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return iterator.next();
	}

	/**
	 * Removes from the underlying data structure the last element returned by
	 * the iterator-cursor (optional operation). This method can be called only
	 * once per call to <tt>next</tt> or <tt>peek</tt> and removes the element
	 * returned by this method. Note, that between a call to <tt>next</tt> and
	 * <tt>remove</tt> the invocation of <tt>peek</tt> or <tt>hasNext</tt> is
	 * forbidden. The behaviour of an iterator-cursor is unspecified if the
	 * underlying data structure is modified while the iteration is in progress
	 * in any way other than by calling this method.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>remove</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is
	 *         not supported by the iterator-cursor.
	 */
	public void remove () throws IllegalStateException, UnsupportedOperationException {
		super.remove();
		iterator.remove();
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the iterator-cursor. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the iterator-cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsRemove() {
		return true;
	}

}
