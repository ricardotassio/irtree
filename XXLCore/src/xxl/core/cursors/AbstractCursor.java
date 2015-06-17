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

import java.util.NoSuchElementException;

/**
 * An abstract cursor implements the methods of the interface
 * {@link xxl.core.cursors.Cursor} and is useful in those cases when you produce
 * elements and want to <i>return</i> them by a cursor. For example consider a
 * query-operation on an index structure that returns an iteration over its
 * results. It is sufficient then to create an abstract cursor and implement the
 * abstract methods <tt>hasNextObject</tt> and <tt>nextObject</tt>.<br />
 * Both methods are protected ones that are only called internally by the
 * abstract cursor whenever a new result is requested on that cursor. The
 * contract of these methods is that:
 * <ul>
 *     <li>
 *         <tt>hasNextObject</tt> determines whether the iteration has more
 *         elements.
 *     </li>
 *     <li>
 *         <tt>nextObject</tt> computes the next object in the iteration.
 *     </li>
 * </ul>
 * 
 * <b>Important:</b> In order to guarantee a certain semantics, an extension of an <tt>AbstractCursor</tt>
 * has to ensure that a call of <tt>hasNext()</tt> always returns <tt>false</tt> after the first time 
 * <tt>false</tt>
 * is delivered. Thus, it should not be possible to receive an element by calling <tt>hasNext()</tt>
 * and <tt>next()</tt> at a later point in time, if <tt>hasNext()</tt> returned <tt>false</tt> 
 * before (even if the underlying data structure received a new element meanwhile). 
 * 
 * <p><b>Example usage:</b>
 * The cursor {@link xxl.core.cursors.sources.RandomIntegers} produces a certain
 * number (<tt>noOfObjects</tt>) of random integer objects by calling
 * {@link java.util.Random#nextInt()}. Therefore <tt>RandomIntegers</tt> extends
 * <tt>AbstractCursor</tt> and the implementation of its abstract methods is as
 * follows:
 * <pre>
 *     protected boolean hasNextObject() {
 *         return noOfObjects == -1 || noOfObjects &gt; 0;
 *     }
 * 
 *     protected Object nextObject() {
 *         if (noOfObjects &gt; 0)
 *             noOfObjects--;
 *         return new Integer(random.nextInt(maxValue));
 *     }
 * </pre>
 * This means, as long as <tt>noOfObjects&nbsp;&gt;&nbsp;0</tt> holds (or
 * <tt>noOfObjects&nbsp;==&nbsp;-1</tt> for an infinite number of random integer
 * objects), the cursor has more elements and the next element will be set to
 * <tt>new Integer(random.nextInt(maxValue))</tt>.</p>
 *
 * <p>Note, that the abstract methods of the cursor interface are implemented in
 * this class, i.e., an abstract cursor already provides the mechanisms that are
 * required for its functionality.
 * <ul>
 *     <li>
 *         An abstract cursor provides a <tt>hasNext</tt>/<tt>next</tt>
 *         mechanism guaranteeing a call to the method <tt>hasNext</tt> before
 *         the next element in the iteration will be accessed, i.e., there will
 *         be at least on call to the abstract method <tt>hasNextObject</tt>
 *         before the second abstract method <tt>nextObject</tt> is called.
 *     </li>
 *     <li>
 *         It also provides an <tt>open</tt>/<tt>close</tt> mechanism. Due to
 *         the decoupling of a cursor's construction and its open phase, it must
 *         be guaranteed that a cursor is opened before it is accessed. For this
 *         reason the <tt>hasNext</tt> method checks whether the cursor has
 *         already been opened and opens it explicitly if not. When a cursor has
 *         been closed, its elements cannot be accessed any more, i.e., a call to
 *         the methods <tt>peek</tt> and <tt>next</tt> will throw an exception
 *         rather than returning an element. Also a closed cursor cannot be
 *         re-opened by a call to its <tt>open</tt> method. Both methods
 *         (<tt>open</tt> and <tt>close</tt> are implemented idempotent, i.e.,
 *         consecutive calls to this methods will have the same effect as a
 *         single call.
 *     </li>
 * </ul>
 *
 * @see xxl.core.cursors.Cursor
 */
public abstract class AbstractCursor implements Cursor {

	/**
	 * A flag indicating whether the iteration has more elements.
	 */
	protected boolean hasNext = false;
	
	/**
	 * A flag indicating whether the <tt>hasNext</tt> method has already been
	 * called for the next element in the iteration.
	 */
	protected boolean computedHasNext = false;
		
	/**
	 * A flag indicating whether the element returned by the last call to the
	 * <tt>next</tt> or <tt>peek</tt> method is valid any longer, i.e., it has
	 * not been removed or updated since that time.
	 */
	protected boolean isValid = true;
	
	/**
	 * The next element in the iteration. This object will be determined by the
	 * first call to the <tt>next</tt> or <tt>peek</tt> method and simply
	 * returned by consecutive calls.
	 */
	protected Object next = null;
	
	/**
	 * A flag indicating whether the element stored by the field {@link #next}
	 * is already returned by a call to the <tt>next</tt> method.
	 */
	protected boolean assignedNext = false;
		
	/**
	 * A flag indicating whether the cursor is already opened.
	 */
	protected boolean isOpened = false;
	
	/**
	 * A flag indicating whether the cursor is already closed.
	 */
	protected boolean isClosed = false;
	
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
	 * 
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public void open() {
	 *         if (!isOpened)
	 *             isOpened = true;
	 *     }
	 * </pre></p>
	 */
	public void open() {
		if (!isOpened)
			isOpened = true;
	}

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
	 * 
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public void close() {
	 *         if (!isClosed) {
	 *             hasNext = false;
	 *             computedHasNext = false;
	 *             isValid = false;
	 *             isClosed = true;
	 *         }
	 *     }
	 * </pre></p>
	 */
	public void close() {
		if (!isClosed) {
			hasNext = false;
			computedHasNext = false;
			isValid = false;
			isClosed = true;
		}
	}

	/**
	 * Returns <tt>true</tt> if the cursor has been closed.
	 * @return <tt>true</tt> if the cursor has been closed.
	 */
	public boolean isClosed() {
		return isClosed();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * <p>This operation is implemented idempotent, i.e., consequent calls to
	 * <tt>hasNext</tt> do not have any effect.</p>
	 * 
	 * <b>Important:</b> In order to guarantee a certain semantics, an extension of an <tt>AbstractCursor</tt>
	 * has to ensure that a call of <tt>hasNext()</tt> always returns <tt>false</tt> after the first time 
	 * <tt>false</tt>
	 * is delivered. Thus, it should not be possible to receive an element by calling <tt>hasNext()</tt>
	 * and <tt>next()</tt> at a later point in time, if <tt>hasNext()</tt> returned <tt>false</tt> 
	 * before (even if the underlying data structure received a new element meanwhile). 
	 * 
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public final boolean hasNext() throws IllegalStateException {
	 *         if (isClosed)
	 *             throw new IllegalStateException();
	 *         if (!isOpened)
	 *             open();
	 *         if (!computedHasNext) {
	 *             hasNext = hasNextObject();
	 *             computedHasNext = true;
	 *             isValid = false;
	 *             assignedNext = false;
	 *         }
	 *         return hasNext;
	 *     }
	 * </pre></p>
	 *
	 * @return <tt>true</tt> if the cursor has more elements.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 */
	public final boolean hasNext() throws IllegalStateException {
		if (isClosed)
			throw new IllegalStateException();	
		if (!isOpened)
			open();
		if (!computedHasNext) {
			hasNext = hasNextObject();
			computedHasNext = true;
			isValid = false;
			assignedNext = false;
		}
		return hasNext;
	}
	
	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * <p>This abstract operation should implement the core functionality of
	 * the <tt>hasNext</tt> method which secures that the cursor is in a proper
	 * state when this method is called. Due to this the <tt>hasNextObject</tt>
	 * method need not to deal with exception handling.</p>
	 * 
	 * @return <tt>true</tt> if the cursor has more elements.
	 */
	protected abstract boolean hasNextObject();

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public final Object next() throws IllegalStateException,
	 *                                       NoSuchElementException {
	 *         if (!computedHasNext)
	 *             hasNext();
	 *         if (!hasNext)
	 *             throw new NoSuchElementException();
	 *         if (!assignedNext) {
	 *             next = nextObject();
	 *             assignedNext = true;
	 *         }
	 *         hasNext = false;
	 *         computedHasNext = false;
	 *         isValid = true;
	 *         return next;
	 *     }
	 * </pre></p>
	 *
	 * @return the next element in the iteration.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 * @throws NoSuchElementException if the iteration has no more elements.
	 */
	public final Object next() throws IllegalStateException, NoSuchElementException {
		if (!computedHasNext)
			hasNext();
		if (!hasNext)
			throw new NoSuchElementException();
		if (!assignedNext) {
			next = nextObject();
			assignedNext = true;
		} 
		hasNext = false;
		computedHasNext = false;
		isValid = true;
		return next;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * <p>This abstract operation should implement the core functionality of
	 * the <tt>next</tt> method which secures that the cursor is in a proper
	 * state when this method is called. Due to this the <tt>nextObject</tt>
	 * method need not to deal with exception handling.</p>
	 *
	 * @return the next element in the iteration.
	 */
	protected abstract Object nextObject();

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
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public final Object peek() throws IllegalStateException,
	 *                                       NoSuchElementException,
	 *                                       UnsupportedOperationException {
	 *         if (!supportsPeek())
	 *             throw new UnsupportedOperationException();
	 *         if (!computedHasNext)
	 *             hasNext();
	 *         if (!hasNext)
	 *             throw new NoSuchElementException();
	 *         if (!assignedNext) {
	 *             next = nextObject();
	 *             assignedNext = true;
	 *         }
	 *         isValid = true;
	 *         return next;
	 *     }
	 * </pre></p>
	 *
	 * @return the next element in the iteration.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 * @throws NoSuchElementException iteration has no more elements.
	 * @throws UnsupportedOperationException if the <tt>peek</tt> operation is
	 *         not supported by the cursor.
	 */
	public final Object peek() throws IllegalStateException, NoSuchElementException, UnsupportedOperationException {
		if (!supportsPeek())
			throw new UnsupportedOperationException();
		if (!computedHasNext)
			hasNext();
		if (!hasNext)
			throw new NoSuchElementException();
		if (!assignedNext) {
			next = nextObject(); 
			assignedNext = true;
		}
		isValid = true;
		return next;
	}

	/**
	 * Returns <tt>true</tt> if the <tt>peek</tt> operation is supported by the
	 * cursor. Otherwise it returns <tt>false</tt>.
	 * 
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public final boolean supportsPeek() {
	 *         return true;
	 *     }
	 * </pre></p>
	 *
	 * @return <tt>true</tt> if the <tt>peek</tt> operation is supported by the
	 *         cursor, otherwise <tt>false</tt>.
	 */
	public final boolean supportsPeek() {
		return true;
	}

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
	 * <p>Note, that this operation is optional and does not work for this
	 * cursor.</p>
	 *
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public void remove() throws IllegalStateException,
	 *                                 UnsupportedOperationException {
	 *         if (!supportsRemove())
	 *             throw new UnsupportedOperationException();
	 *         if (!isValid)
	 *             throw new IllegalStateException();
	 *         hasNext = false;
	 *         computedHasNext = false;
	 *         isValid = false;
	 *         assignedNext = false;
	 *     }
	 * </pre></p>
	 *
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>remove</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is
	 *         not supported by the cursor.
	 */
	public void remove() throws IllegalStateException, UnsupportedOperationException {
		if (!supportsRemove())
			throw new UnsupportedOperationException();
		if (!isValid)
			throw new IllegalStateException();
		hasNext = false;
		computedHasNext = false;
		isValid = false;
		assignedNext = false;
	}

	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 * 
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public boolean supportsRemove() {
	 *         return false;
	 *     }
	 * </pre></p>
	 * 
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsRemove() {
		return false;
	}
	
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
	 * <p>Note, that this operation is optional and does not work for this
	 * cursor.</p>
	 *
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public void update(Object object) throws IllegalStateException,
	 *                                              UnsupportedOperationException {
	 *         if (!supportsUpdate())
	 *             throw new UnsupportedOperationException();
	 *         if (!isValid)
	 *             throw new IllegalStateException();
	 *         hasNext = false;
	 *         computedHasNext = false;
	 *         isValid = false;
	 *         assignedNext = false;
	 *     }
	 * </pre></p>
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
	public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
		if (!supportsUpdate())
			throw new UnsupportedOperationException();
		if (!isValid)
			throw new IllegalStateException();
		hasNext = false;
		computedHasNext = false;
		isValid = false;
		assignedNext = false;
	}

	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 * 
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public boolean supportsUpdate() {
	 *         return false;
	 *     }
	 * </pre></p>
	 * 
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsUpdate() {
		return false;
	}

	/**
	 * Resets the cursor to its initial state such that the caller is able to
	 * traverse the underlying data structure again without constructing a new
	 * cursor (optional operation). The modifications, removes and updates
	 * concerning the underlying data structure, are still persistent.
	 * 
	 * <p>Note, that this operation is optional and does not work for this
	 * cursor.</p>
	 *
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public void reset() throws IllegalStateException,
	 *                                UnsupportedOperationException {
	 *         if (!supportsReset())
	 *             throw new UnsupportedOperationException();
	 *         hasNext = false;
	 *         computedHasNext = false;
	 *         isValid = false;
	 *         assignedNext = false;
	 *     }
	 * </pre></p>
	 * 
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the cursor.
	 */
	public void reset() throws UnsupportedOperationException {
		if (!supportsReset())
			throw new UnsupportedOperationException();
		hasNext = false;
		computedHasNext = false;
		isValid = false;
		assignedNext = false;
	}

	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * <p>The current implementation of this method is as follows:
	 * <pre>
	 *     public boolean supportsReset() {
	 *         return false;
	 *     }
	 * </pre></p>
	 * 
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return false;
	}
}
