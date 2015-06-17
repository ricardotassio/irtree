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

package xxl.core.cursors;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A cursor extends the interface {@link java.util.Iterator} by adding
 * functionality for opening and closing the cursor, i.e., reserving and
 * releasing of system resources, taking a look at the next element that would
 * be returned by call to <tt>next</tt>, updating elements of the underlying
 * data structure and traversing it another time without constructing a new
 * cursor.
 * 
 * <p>Cursors <i>differ</i> from iterators in some points:
 * <ul>
 *     <li>
 *         <tt>open</tt>: before using a cursor the caller must open it in order
 *         to reserve resources, open files, etc.
 *     </li>
 *     <li>
 *         <tt>close</tt>: after using a cursor it must be closed by the caller
 *         in order to clean up resources, close files, etc.
 *     </li>
 *     <li>
 *         <tt>peek</tt>: cursors allow the caller to show the next element in
 *         the iteration without proceeding the iteration.
 *     </li>
 *     <li>
 *         <tt>supportsPeek</tt>: a method that returns <tt>true</tt> if an
 *         instance of a cursor supports the <tt>peek</tt> operation, otherwise
 *         it returns <tt>false</tt>.
 *     </li>
 *     <li>
 *         <tt>supportsRemove</tt>: a method that returns <tt>true</tt> if an
 *         instance of a cursor supports the <tt>remove</tt> operation, otherwise
 *         it returns <tt>false</tt>.
 *     </li>
 *     <li>
 *         <tt>update</tt>: cursors allow the caller to update the element in 
 *         the iteration returned by the <tt>next</tt> or <tt>peek</tt> method
 *         and replacing it in the underlying collection.
 *     </li>
 *     <li>
 *         <tt>supportsUpdate</tt>: a method that returns <tt>true</tt> if an
 *         instance of a cursor supports the <tt>update</tt> operation, 
 *         otherwise it returns <tt>false</tt>.
 *     </li>
 *     <li>
 *         <tt>reset</tt>: cursors allow the caller to traverse the underlying 
 *         collection again without constructing a new cursor.
 *     </li>
 *     <li>
 *         <tt>supportsReset</tt>: a method that returns <tt>true</tt> if an
 *         instance of a cursor supports the <tt>reset</tt> operation, 
 *         otherwise it returns <tt>false</tt>.
 *     </li>
 * </ul></p>
 * 
 * <p><b>General contract:</b><br />
 * If an exception is thrown by a cursor consecutive calls to methods are not
 * guaranteed to produce correct results.</p>
 * 
 * <p>
 * <b>Important:</b> In order to guarantee a certain semantics, an implementation of a <tt>Cursor</tt>
 * has to ensure that a call of <tt>hasNext()</tt> always returns <tt>false</tt> after the first time 
 * <tt>false</tt>
 * is delivered. Thus, it should not be possible to receive an element by calling <tt>hasNext()</tt>
 * and <tt>next()</tt> at a later point in time, if <tt>hasNext()</tt> returned <tt>false</tt> 
 * before (even if the underlying data structure received a new element meanwhile). 
 * </p>
 * 
 * <p><b>General order of method invocations:</b>
 * <pre>
 *     // creating a new instance of an arbitrary cursor
 * 
 *     Cursor cursor = ...
 * 
 *     // opening the cursor for first use
 * 
 *     cursor.open();
 * 
 *     // consuming the cursor in a loop; checking if there is a next element
 * 
 *     while(cursor.hasNext()) {
 * 
 *         ...
 * 
 *         // taking a look at the next element, but do not remove it from the
 *         // underlying collection (optional)
 * 
 *         Object peek = cursor.peek();
 * 
 *         ...
 * 
 *         // consuming the next element
 * 
 *         Object next = cursor.next();
 * 
 *         ...
 * 
 *         // removing object 'next' from the underyling collection (optional)
 * 
 *         cursor.remove();
 * 
 *     }
 * 
 *     ...
 * 
 *     // cursor will be used again, so reset it (optional)
 * 
 *     cursor.reset();
 * 
 *     ...
 * 
 *     // cursor is not needed any more; release resources
 * 
 *     cursor.close(); 
 * </pre></p>
 *
 * @see java.util.Iterator
 */
public interface Cursor extends Iterator {

	/**
	 * Opens the cursor, i.e., signals the cursor to reserve resources, open
	 * files, etc. Before a cursor has been opened calls to methods like
	 * <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield proper
	 * results. Therefore <tt>open</tt> must be called before a cursor's data
	 * can be processed. Multiple calls to <tt>open</tt> do not have any effect,
	 * i.e., if <tt>open</tt> was called the cursor remains in the state
	 * <i>opened</i> until its <tt>close</tt> method is called.
	 * 
	 * <p>Note, that a call to the <tt>open</tt> method of a closed cursor
	 * usually does not open it again because of the fact that its state
	 * generally cannot be restored when resources are released respectively
	 * files are closed.</p>
	 */
	public abstract void open();

	/**
	 * Closes the cursor, i.e., signals the cursor to clean up resources, close
	 * files, etc. When a cursor has been closed calls to methods like
	 * <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield proper
	 * results. Multiple calls to <tt>close</tt> do not have any effect, i.e.,
	 * if <tt>close</tt> was called the cursor remains in the state
	 * <i>closed</i>.
	 * 
	 * <p>Note, that a closed cursor usually cannot be opened again because of
	 * the fact that its state generally cannot be restored when resources are
	 * released respectively files are closed.</p>
	 */
	public abstract void close();

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * <p>This operation should be implemented idempotent, i.e., consequent
	 * calls to <tt>hasNext</tt> do not have any effect.</p>
	 * <b>Important:</b> In order to guarantee a certain semantics, an implementation of a <tt>Cursor</tt>
	 * has to ensure that a call of <tt>hasNext()</tt> always returns <tt>false</tt> after the first time 
	 * <tt>false</tt>
 	 * is delivered. Thus, it should not be possible to receive an element by calling <tt>hasNext()</tt>
 	 * and <tt>next()</tt> at a later point in time, if <tt>hasNext()</tt> returned <tt>false</tt> 
 	 * before (even if the underlying data structure received a new element meanwhile). 
	 *
	 * @return <tt>true</tt> if the cursor has more elements.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 */
	public abstract boolean hasNext() throws IllegalStateException;

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 *
	 * @return the next element in the iteration.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 * @throws NoSuchElementException if the iteration has no more elements.
	 */
	public abstract Object next() throws IllegalStateException, NoSuchElementException;

	/**
	 * Shows the next element in the iteration without proceeding the iteration
	 * (optional operation). After calling <tt>peek</tt> the returned element is
	 * still the cursor's next one such that a call to <tt>next</tt> would be
	 * the only way to proceed the iteration. But be aware that an
	 * implementation of this method uses a kind of buffer-strategy, therefore
	 * it is possible that the returned element will be removed from the
	 * <i>underlying</i> iteration, e.g., the caller can use an instance of a
	 * cursor depending on an iterator, so the next element returned by a call
	 * to <tt>peek</tt> will be removed from the underlying iterator which does
	 * not support the <tt>peek</tt> operation and therefore the iterator has to
	 * be wrapped and buffered.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors. After calling the <tt>peek</tt> method a call to <tt>next</tt>
	 * is strongly recommended.</p> 
	 *
	 * @return the next element in the iteration.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 * @throws NoSuchElementException iteration has no more elements.
	 * @throws UnsupportedOperationException if the <tt>peek</tt> operation is
	 *         not supported by the cursor.
	 */
	public abstract Object peek() throws IllegalStateException, NoSuchElementException, UnsupportedOperationException;

	/**
	 * Returns <tt>true</tt> if the <tt>peek</tt> operation is supported by the
	 * cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>peek</tt> operation is supported by the
	 *         cursor, otherwise <tt>false</tt>.
	 */
	public abstract boolean supportsPeek();

	/**
	 * Removes from the underlying data structure the last element returned by
	 * the cursor (optional operation). This method can be called only once per
	 * call to <tt>next</tt> or <tt>peek</tt> and removes the element returned
	 * by this method. Note, that between a call to <tt>next</tt> and
	 * <tt>remove</tt> the invocation of <tt>peek</tt> or <tt>hasNext</tt> is
	 * forbidden. The behaviour of a cursor is unspecified if the underlying
	 * data structure is modified while the iteration is in progress in any way
	 * other than by calling this method.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>remove</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is
	 *         not supported by the cursor.
	 */
	public abstract void remove() throws IllegalStateException, UnsupportedOperationException;

	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public abstract boolean supportsRemove();

	/**
	 * Replaces the last element returned by the cursor in the underlying data
	 * structure (optional operation). This method can be called only once per
	 * call to <tt>next</tt> or <tt>peek</tt> and updates the element returned
	 * by this method. Note, that between a call to <tt>next</tt> and
	 * <tt>update</tt> the invocation of <tt>peek</tt> or <tt>hasNext</tt> is
	 * forbidden. The behaviour of a cursor is unspecified if the underlying
	 * data structure is modified while the iteration is in progress in any way
	 * other than by calling this method.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @param object the object that replaces the last element returned by the
	 *        cursor.
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>update</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>update</tt> operation is
	 *         not supported by the cursor.
	 */
	public abstract void update(Object object) throws IllegalStateException, UnsupportedOperationException;

	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public abstract boolean supportsUpdate();

	/**
	 * Resets the cursor to its initial state such that the caller is able to
	 * traverse the underlying data structure again without constructing a new
	 * cursor (optional operation). The modifications, removes and updates
	 * concerning the underlying data structure, are still persistent.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the cursor.
	 */
	public abstract void reset() throws UnsupportedOperationException;

	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public abstract boolean supportsReset();

}
