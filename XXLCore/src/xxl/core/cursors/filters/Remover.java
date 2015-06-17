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

package xxl.core.cursors.filters;

import java.util.Iterator;
import java.util.NoSuchElementException;

import xxl.core.cursors.SecureDecoratorCursor;

/**
 * The remover removes all elements of a given input iteration in a lazy way,
 * i.e., the <tt>remove</tt> method is called for all elements of the input
 * iterator that are returned by a call to the <tt>next</tt> method. Each call
 * to the input iteration's <tt>next</tt> method implies that its <tt>remove</tt> is
 * called thereafter. So the element returned by <tt>next</tt> is automatically
 * removed from the underlying data structure. Therefore the caller is not able
 * to traverse the underlying collection again by invoking the <tt>reset</tt>
 * method.
 * 
 * <p><b>Note:</b> If the input iteration is given by an object of the class
 * {@link java.util.Iterator}, i.e., it does not support the <tt>peek</tt>
 * operation, it is internally wrapped to a cursor.</p>
 * 
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.SecureDecoratorCursor
 */
public class Remover extends SecureDecoratorCursor {

	/**
	 * Creates a new remover which removes every element of the input iteration
	 * that is returned by its <tt>next</tt> method.
	 * 
	 * @param iterator the input iteration which elements should be removed.
	 */
	public Remover(Iterator iterator) {
		super(iterator);
	}

	/**
	 * Returns the next element in the iteration. This element will <b>not</b> be
	 * accessible by some of the cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs,
	 * because it is removed from the underlying data structure at the same time.
	 *
	 * @return the next element in the iteration.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 * @throws NoSuchElementException if the iteration has no more elements.
	 */
	public Object next() throws IllegalStateException, NoSuchElementException {
		Object next = super.next();
		super.remove();
		return next;
	}
	
}
