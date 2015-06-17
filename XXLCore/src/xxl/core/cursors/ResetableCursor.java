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

import java.util.ArrayList;
import java.util.Iterator;

import xxl.core.cursors.identities.TeeCursor;
import xxl.core.functions.Function;

/**
 * This class provides a wrapper that enhance a
 * {@link xxl.core.cursors.Cursor cursor} (or
 * {@link java.util.Iterator iterator}) that is not resetable by a reset
 * functionality. For this reason, the cursor is wrapped by a
 * {@link xxl.core.cursors.identities.TeeCursor tee-cursor} that stores the
 * elements returned by it. When the <tt>reset</tt> method is called on the
 * resetable cursor, it iterates over the stored elements at first and continues
 * the iteration with the elements of the wrapped cursor afterwards.
 * 
 * <p><b>Note</b> that the elements returned by a resetable cursor are stored in
 * the storage area of a tee cursor completely. So be aware of the potential size
 * of the wrapped cursor in order to estimate memory usage.</p>
 * 
 * <p>When a cursor has been wrapped to a resetable cursor, the working of the
 * <tt>reset</tt> method can be guaranteed and the cursor can be used as in the
 * example shown below.
 * <pre>
 *     // Create a cursor with ten random numbers.
 * 
 *     Cursor randomNumbers = new DiscreteRandomNumber(
 *         new JavaDiscreteRandomWrapper(),
 *         10
 *     );
 * 
 *     // Wrap the cursor to a resetable cursor.
 * 
 *     ResetableCursor bufferedCursor = new ResetableCursor(randomNumbers);
 * 
 *     // Process five elements of the cursor (they will be stored internally).
 * 
 *     bufferedCursor.open();
 *     System.out.println("get 5 elements:");
 *     for (int i = 1; i&lt;5 && bufferedCursor.hasNext(); i++)
 *         System.out.println(bufferedCursor.next());
 * 
 *     // Now reset the cursor and process all elements.
 * 
 *     System.out.println("reset buffered cursor, and get all elements:");
 *     bufferedCursor.reset();
 *     Cursors.println(bufferedCursor);
 *     
 *     // Another time.
 * 
 *     System.out.println("reset buffered cursor, and get all elements:");
 *     bufferedCursor.reset();
 *     Cursors.println(bufferedCursor);
 * 
 *     // Finally close the cursor!
 * 
 *     bufferedCursor.close();
 * </pre></p>
 * 
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.identities.TeeCursor
 */
public class ResetableCursor extends AbstractCursor {

	/**
	 * A factory method to create a default tee cursor. The factory method
	 * creates a tee-cursor that is internally used to store the elements of the
	 * wrapped cursor. The factory implements an
	 * {@link xxl.core.functions.Function#invoke(Object) invoke method} that
	 * expects the cursor to be wrapped as argument.<br />
	 * The default factory produces a tee cursor that uses a
	 * {@link java.util.List list} to store the wrapped cursor's elements.
	 * 
	 * @see xxl.core.functions.Function#invoke(Object)
	 * @see xxl.core.cursors.identities.TeeCursor
	 */
	public static Function DEFAULT_TEECURSOR_FACTORY = new Function() {
		public Object invoke(Object iterator) {
			return new TeeCursor((Iterator)iterator);
		}
	};

	/**
	 * The tee cursor that is internally used to store the elments of the
	 * (usually) not resetable cursor.
	 */
	protected TeeCursor teeCursor;
	
	/**
	 * A cursor iterating over the elements internally stored in the storage
	 * area of the tee cursor.
	 */
	protected Cursor bufferedCursor;
	
	/**
	 * Creates a new resetable cursor that enhances the given iterator by a
	 * reset functionality and uses the specified factory method to get a tee
	 * cursor that stores the wrapped cursor's elements. The factory must
	 * implements an
	 * {@link xxl.core.functions.Function#invoke(Object) invoke method} that
	 * expects the iterator to be wrapped as argument.
	 *
	 * @param iterator the iterator that should be enhance by a reset
	 *        functionality.
	 * @param teeCursorFactory a factory method which returns a tee cursor. The
	 *        factory must implements an
	 *        {@link xxl.core.functions.Function#invoke(Object) invoke method}
	 *        that expects the iterator to be wrapped as argument.
	 */
	public ResetableCursor(Iterator iterator, Function teeCursorFactory) {
		teeCursor = (TeeCursor)teeCursorFactory.invoke(iterator);
	}

	/**
	 * Creates a new resetable cursor that enhances the given iterator by a
	 * reset functionality. For getting a tee cursor to store the wrapped
	 * iterator's elements a {@link #DEFAULT_TEECURSOR_FACTORY default} factory
	 * method is used.
	 *
	 * @param iterator the iterator that should be enhance by a reset
	 *        functionality.
	 */
	public ResetableCursor(Iterator iterator) {
		this(iterator, DEFAULT_TEECURSOR_FACTORY);
	}

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
	public void open() {
		super.open();
		teeCursor.open();
		bufferedCursor = null;
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
	 */
	public void close() {
		super.close();
		teeCursor.close();
		bufferedCursor = null;
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the cursor has more elements.
	 */
	protected boolean hasNextObject() {
		if (bufferedCursor != null)
			if (bufferedCursor.hasNext())
				return true;
			else
				bufferedCursor = null;
		return teeCursor.hasNext();
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		if (bufferedCursor != null)
			return bufferedCursor.next();
		else
			return teeCursor.next();
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
	public void remove() throws IllegalStateException, UnsupportedOperationException {
		if (bufferedCursor != null)
			throw new IllegalStateException("remove cannot be performed on an element that is already buffered");
		else {
			super.remove();
			teeCursor.remove();
		}
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsRemove() {
		return teeCursor.supportsRemove();
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
	public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
		if (bufferedCursor != null)
			throw new IllegalStateException("update cannot be performed on an element that is already buffered");
		else {
			super.update(object);
			teeCursor.update(object);
		}
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsUpdate() {
		return teeCursor.supportsReset();
	}
	
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
	public void reset() throws UnsupportedOperationException {
		super.reset();
		bufferedCursor = teeCursor.cursor();
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return true;
	}

	/**
	 * The main method contains some examples how to use a resetable cursor. It
	 * can also be used to test the functionality of a resetable cursor.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main(String[] args) {

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/

		xxl.core.cursors.sources.DiscreteRandomNumber randomNumbers = new xxl.core.cursors.sources.DiscreteRandomNumber(
			new xxl.core.util.random.JavaDiscreteRandomWrapper(),
			10
		);
		
		ResetableCursor bufferedCursor = new ResetableCursor(randomNumbers);
		
		bufferedCursor.open();
		System.out.println("get 5 elements:");
		for (int i = 1; i < 5 && bufferedCursor.hasNext(); i++)
			System.out.println(bufferedCursor.next());
		
		System.out.println("reset buffered cursor, and get all elements:");
		bufferedCursor.reset();
		Cursors.println(bufferedCursor);
		
		System.out.println("reset buffered cursor, and get all elements:");
		bufferedCursor.reset();
		Cursors.println(bufferedCursor);
		
		bufferedCursor.close();
	}

}
