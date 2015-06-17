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

import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.functions.Function;
import xxl.core.predicates.FunctionPredicate;
import xxl.core.predicates.Predicate;

/**
 * A while-dropper swallows the elements of an input iteration as long as an
 * unary predicate (or boolean function) is equal to a user defined flag. The
 * remaining elements of the input iteration are returned. The while-dropper
 * drops the elements of an input iteration as long as the result of a user
 * defined predicate applied to the next element of the input iteration equals
 * to the boolean flag <tt>asLongAs</tt> specified in the constructor. The
 * following part of the implementation of the method <tt>hasNext</tt> shows
 * this dropping:
 * <pre>
 *     while (cursor.hasNext() && asLongAs ^ !predicate.invoke(cursor.peek()))
 *         cursor.next();
 * </pre>
 * The remaining elements of the input iteration are returned.
 * 
 * <p><b>Note:</b> If the input iteration is given by an object of the class
 * {@link java.util.Iterator}, i.e., it does not support the <tt>peek</tt>
 * operation, it is internally wrapped to a cursor. If a boolean function is
 * specified in a constructor it is also internally wrapped to a predicate.</p>
 * 
 * <p><b>Example usage:</b>
 * <pre>
 *     WhileDropper whileDropper = new WhileDropper(
 *         new Enumerator(21),
 *         new Predicate() {
 *             public boolean invoke(Object object) {
 *                 return ((Integer)object).intValue() &lt; 10;
 *             }
 *         },
 *         true
 *     );
 * 
 *     whileDropper.open();
 * 
 *     while (whileDropper.hasNext())
 *         System.out.print(whileDropper.next() +"; ");
 *     System.out.flush();
 * 
 *     whileDropper.close();
 * </pre>
 * This instance of a while-dropper uses an
 * {@link xxl.core.cursors.sources.Enumerator enumerator} as input iteration
 * containing the elements 0, ..., 20. A predicate is defined which is applied to
 * each element of the enumerator and returns <tt>true</tt> if the integer value
 * of this element is lower than 10. So all elements of the enumerator that are
 * lower than 10 will be dropped, because the user specified flag
 * <tt>asLongAs</tt> has been set to <tt>true</tt>. The remaining elements of
 * the enumerator are printed to the output stream. So the output is:
 * <pre>
 *     10; 11; 12; 13; 14; 15; 16; 17; 18; 19; 20;
 * </pre></p>
 * 
 * <p>Please compare this output and functionality with that of a
 * {@link xxl.core.cursors.filters.WhileTaker while-taker}. A while-taker returns
 * the input iteration's elements as long as the user defined predicate evaluated
 * for each of these elements equals to the user specified flag
 * <tt>asLongAs</tt>. A while-dropper drops the input iteration's elements as
 * long as the user defined predicate invoked on each of these elements equals
 * to the user specified flag <tt>asLongAs</tt> and returns the remaining
 * elements.</p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 */
public class WhileDropper extends AbstractCursor {

	/**
	 * The input cursor.
	 */
	protected Cursor cursor;

	/**
	 * A flag to signal if all elements selected by the given predicate have
	 * already been dropped.
	 */
	protected boolean skipped = false;

	/**
	 * The predicate used to select the input cursor's elements. As long as this
	 * predicate equals to the user specified flag <tt>asLongAs</tt> the elements
	 * of the input cursor will be dropped.
	 */
	protected Predicate predicate;

	/**
	 * A kind of escape sequence for the predicate. The elements of the input
	 * cursor are dropped as long as the predicate's evaluation result is equal
	 * to this flag.
	 */
	protected boolean asLongAs;

	/**
	 * Creates a new while-dropper. Every iterator given to this constructor is
	 * wrapped to a cursor.
	 *
	 * @param iterator the input iterator.
	 * @param predicate the unary predicate determining how long the input
	 *        elements will be dropped.
	 * @param asLongAs the elements of the input cursor are dropped as long as
	 *        the predicate's evaluation result is equal to this flag.
	 */
	public WhileDropper(Iterator iterator, Predicate predicate, boolean asLongAs) {
		this.cursor = Cursors.wrap(iterator);
		this.predicate = predicate;
		this.asLongAs = asLongAs;
	}

	/**
	 * Creates a new while-dropper. Every iterator given to this constructor is
	 * wrapped to a cursor. The boolean function is internally wrapped to a
	 * predicate.
	 *
	 * @param iterator the input iterator.
	 * @param function the boolean function determining how long the input
	 *        elements will be dropped.
	 * @param asLongAs the elements of the input cursor are dropped as long as
	 *        the predicate's evaluation result is equal to this flag.
	 */
	public WhileDropper(Iterator iterator, Function function, boolean asLongAs) {
		this(iterator, new FunctionPredicate(function), asLongAs);
	}

	/**
	 * Opens the while-dropper, i.e., signals the cursor to reserve resources,
	 * open the input iteration, etc. Before a cursor has been
	 * opened calls to methods like <tt>next</tt> or <tt>peek</tt> are not
	 * guaranteed to yield proper results. Therefore <tt>open</tt> must be called
	 * before a cursor's data can be processed. Multiple calls to <tt>open</tt>
	 * do not have any effect, i.e., if <tt>open</tt> was called the cursor
	 * remains in the state <i>opened</i> until its <tt>close</tt> method is
	 * called.
	 * 
	 * <p>Note, that a call to the <tt>open</tt> method of a closed cursor
	 * usually does not open it again because of the fact that its state
	 * generally cannot be restored when resources are released respectively
	 * files are closed.</p>
	 */
	public void open() {
		super.open();
		cursor.open();
	}
	
	/**
	 * Closes the while-dropper, i.e., signals the cursor to clean up resources,
	 * close the input iteration, etc. When a cursor has been closed calls to
	 * methods like <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield
	 * proper results. Multiple calls to <tt>close</tt> do not have any effect,
	 * i.e., if <tt>close</tt> was called the cursor remains in the state
	 * <i>closed</i>.
	 * 
	 * <p>Note, that a closed cursor usually cannot be opened again because of
	 * the fact that its state generally cannot be restored when resources are
	 * released respectively files are closed.</p>
	 */
	public void close() {
		super.close();
		cursor.close();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * <p>All elements of the while-dropper are swallowed as long as the given
	 * predicate returns <tt>true</tt>, i.e., this happens when the input cursor
	 * has more elements and the result of the predicate applied on the input
	 * cursor's next element equals to the caller specified flag
	 * <tt>asLongAs</tt>. If there remain any elements in the underlying cursor
	 * the <tt>hasNext</tt> method returns <tt>true</tt>.</p>
	 * 
	 * @return <tt>true</tt> if the while-dropper has more elements.
	 */
	protected boolean hasNextObject() {
		if (!skipped) {
			while (cursor.hasNext() && asLongAs ^ !predicate.invoke(cursor.peek()))
				cursor.next();
			skipped = true;
		}
		return cursor.hasNext();
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * <p>When all elements, whose evaluation of the user defined predicate equal
	 * to the user specified flag <tt>asLongAs</tt> have been dropped, the
	 * <tt>next</tt> method is applied to the cursor, so the remaining elements
	 * are returned to the caller by lazy evaluation. The returned element will
	 * be removed from the iteration, if <tt>next</tt> is called.</p>
	 *
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return cursor.next();
	}

	/**
	 * Removes from the underlying data structure the last element returned by
	 * the while-dropper (optional operation). This method can be called only 
	 * once per call to <tt>next</tt> or <tt>peek</tt> and removes the element
	 * returned by this method. Note, that between a call to <tt>next</tt> and
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
		super.remove();
		cursor.remove();
	}

	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the while-dropper. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsRemove() {
		return cursor.supportsRemove();
	}

	/**
	 * Replaces the last element returned by the while-dropper in the underlying
	 * data structure (optional operation). This method can be called only once
	 * per call to <tt>next</tt> or <tt>peek</tt> and updates the element
	 * returned by this method. Note, that between a call to <tt>next</tt> and
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
		super.update(object);
		cursor.update(object);
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the while-dropper. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsUpdate() {
		return cursor.supportsUpdate();
	}

	/**
	 * Resets the while-dropper to its initial state such that the caller is able
	 * to traverse the underlying data structure again without constructing a new
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
		skipped = false;
		cursor.reset();
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the while-dropper. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return cursor.supportsReset();
	}

	/**
	 * The main method contains some examples to demonstrate the usage and the
	 * functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to submit
	 *        parameters when the main method is called.
	 */
	public static void main(String[] args) {

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		
		WhileDropper whileDropper = new WhileDropper(
			new xxl.core.cursors.sources.Enumerator(21),
			new Predicate() {
				public boolean invoke(Object object) {
					return ((Integer)object).intValue() < 10;
				}
			},
			true
		);
		
		whileDropper.open();
		
		while (whileDropper.hasNext())
			System.out.print(whileDropper.next() + "; ");
		System.out.flush();
		
		whileDropper.close();
	}
}
