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

package xxl.core.cursors.groupers;

import java.util.Iterator;

import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.functions.Function;
import xxl.core.predicates.FunctionPredicate;
import xxl.core.predicates.Predicate;

/**
 * A sort-based implementation of the group operator. A sort-based grouper
 * partitions input data into groups (lazy evaluation). A binary predicate (or
 * boolean function) is used to compare the current element with the last element
 * consumed from the underlying input iteration. If this predicate returns
 * <tt>true</tt> a new group starts, otherwise the next element delivered from
 * the underlying iteration belongs to the same group. A call to <tt>next</tt>
 * returns a cursor pointing to the next group!
 * 
 * <p><b>Precondition:</b> The input cursor has to be sorted!</p>
 * 
 * <p><b>Note:</b> If the input iteration is given by an object of the class
 * {@link java.util.Iterator}, i.e., it does not support the <tt>peek</tt>
 * operation, it is internally wrapped to a cursor.</p>
 * 
 * <p><b>Example usage:</b>
 * <pre>
 *     SortBasedGrouper sortBasedGrouper = new SortBasedGrouper(
 *         new RandomIntegers(100, 50),
 *         new Predicate() {
 *             public boolean invoke(Object previous, Object next) {
 *                 return ((Integer)previous).intValue() &gt; ((Integer)next).intValue();
 *             }
 *         }
 *     );
 * 
 *     sortBasedGrouper.open();
 * 
 *     Cursor sequence;
 *     while (sortBasedGrouper.hasNext()) {
 *         sequence = (Cursor)sortBasedGrouper.next();
 *         while (sequence.hasNext())
 *             System.out.print(sequence.next() + "; ");
 *         System.out.flush();
 *         System.out.println();
 *     }
 * 
 *     sortBasedGrouper.close();
 * </pre>
 * This example partitions 50 random integers with maximum value '99' into
 * groups. A new group starts if the predicate returns <tt>true</tt>. That is the
 * case when the next integer value delivered by the input cursor is lower than
 * the previously returned integer value. So this instance of a sort-based
 * grouper returns all monotonous ascending sequences contained in the input
 * iteration by lazy evaluation.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.groupers.HashGrouper
 * @see xxl.core.cursors.groupers.NestedLoopsGrouper
 */
public class SortBasedGrouper extends AbstractCursor {

	/**
	 * The input iteration to be grouped.
	 */
	protected Cursor input;

	/**
	 * A cursor iterating over the currently traversed group.
	 */
	protected Cursor currentGroup;

	/**
	 * The binary predicate deciding if a new group starts. The evaluated
	 * predicate returns <tt>true</tt> a new group starts, otherwise the next
	 * element of the underlying cursor belongs to the same group.
	 */
	protected Predicate predicate;

	/**
	 * The previous consumed object.
	 */
	protected Object previous;
	
	/**
	 * The pre-previous consumed object.
	 */
	protected Object prePrevious;

	/**
	 * The size of the current group.
	 */
	protected int groupSize = 0;

	/**
	 * A flag to signal if an <tt>update</tt> operation is possible.
	 */
	protected boolean updatePossible = false;

	/**
	 * Creates a new sort-based grouper. If an iterator is given to this
	 * constructor it is wrapped to a cursor.
	 *
	 * @param iterator the input iteration to be grouped.
	 * @param predicate the binary predicate deciding if a new group starts.
	 */
	public SortBasedGrouper(Iterator iterator, Predicate predicate) {
		this.input = Cursors.wrap(iterator);
		this.predicate = predicate;
	}

	/**
	 * Creates a new sort-based grouper. If an iterator is given to this
	 * constructor it is wrapped to a cursor and the given boolean function is
	 * internally wrapped to a predicate.
	 *
	 * @param iterator the input iteration to be grouped.
	 * @param function a boolean function deciding if a new group starts.
	 */
	public SortBasedGrouper(Iterator iterator, Function function) {
		this(iterator, new FunctionPredicate(function));
	}

	/**
	 * Opens the sort-based grouper, i.e., signals the cursor to reserve
	 * resources, open the input iteration, etc. Before a cursor has been
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
		input.open();
	}
	
	/**
	 * Closes the sort-based grouper. Signals it to clean up resources, close
	 * the input iteration, etc. After a call to <tt>close</tt> calls to methods
	 * like <tt>next</tt> or <tt>peek</tt> are not guarantied to yield proper
	 * results. Multiple calls to <tt>close</tt> do not have any effect, i.e., if
	 * <tt>close</tt> was called the sort-based grouper remains in the state
	 * "closed".
	 */
	public void close() {
		super.close();
		input.close();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other words,
	 * returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would return an
	 * element rather than throwing an exception.) This happens when the
	 * specified predicate evalutated for the previous element and the next
	 * element of the input iteration, determined by its <tt>peek</tt> method is
	 * <tt>true</tt>. Then a new group starts.
	 *
	 * @return <tt>true</tt> if the sort-based grouper has more elements,
	 *         otherwise <tt>false</tt>.
	 */
	protected boolean hasNextObject() {
		if (currentGroup != null) {
			while (currentGroup.hasNext())
				currentGroup.next();
			currentGroup = null;
		}
		return input.hasNext() && (previous == null || predicate.invoke(previous, input.peek()));
	}

	/**
	 * Returns the next element in the iteration (a cursor iterating over the
	 * next group!). In this case that means the next group of elements that are
	 * equal concerning the user defined predicate is returned as a new
	 * cursor.<br />
	 * <b>Important:</b> LAZY EVALUATION is used!<br />
	 * With the help of this cursor instance the caller is able to traverse and
	 * operate with the group of elements. The delivered cursor does definitely
	 * not support the methods <tt>close</tt> and <tt>reset</tt>.
	 *
	 * @return the next element in the iteration (a cursor iterating over the
	 *         next group!).
	 */
	protected Object nextObject() {
		groupSize = 0;
		return currentGroup = new AbstractCursor() {

			public boolean hasNextObject() {
				return groupSize == 0 || input.hasNext() && !predicate.invoke(previous, input.peek());
			}

			public Object nextObject() {
				updatePossible = true;
				groupSize++;
				prePrevious = previous;
				return previous = input.next();
			}

			public void remove() throws IllegalStateException, UnsupportedOperationException {
				super.remove();
				if (!updatePossible)
					throw new IllegalStateException();
				input.remove();
				updatePossible = false;
				previous = prePrevious;
				groupSize--;
			}
			
			public boolean supportsRemove() {
				return input.supportsRemove();
			}
			
			public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
				super.update(object);
				if (!updatePossible)
					throw new IllegalStateException();
				input.update(object);
				previous = object;
			}
			
			public boolean supportsUpdate() {
				return input.supportsUpdate();
			}
			
		};
	}

	/**
	 * Resets the sort-based grouper to its initial state (optional operation).
	 * So the caller is able to traverse the underlying data structure again.
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> method is not
	 *         supported by the input cursor.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		input.reset();
		groupSize = 0;
	}

	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the sort-based grouper. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the sort-based grouper, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return input.supportsReset();
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
		
		SortBasedGrouper sortBasedGrouper = new SortBasedGrouper(
			new xxl.core.cursors.sources.RandomIntegers(100, 50),
			new Predicate() {// the predicate used for comparison
				public boolean invoke(Object previous, Object next) {
					return ((Integer)previous).intValue() > ((Integer)next).intValue();
				}
			}
		);

		sortBasedGrouper.open();
		
		Cursor sequence = null;
		while (sortBasedGrouper.hasNext()) {
			sequence = (Cursor)sortBasedGrouper.next();
			// a cursor pointing to the next group
			while (sequence.hasNext())
				System.out.print(sequence.next() + "; ");
			System.out.flush();
			System.out.println();
		}
		
		sortBasedGrouper.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		sortBasedGrouper = new SortBasedGrouper(
			new xxl.core.cursors.sources.Enumerator(0, 100),
			new Predicate() {
				public boolean invoke(Object previous, Object next) {
					return ((Integer)previous).intValue()/10 != ((Integer)next).intValue()/10;
				}
			}
		);

		sortBasedGrouper.open();

		sequence = null; 
		while (sortBasedGrouper.hasNext()) {
			sequence = (Cursor)sortBasedGrouper.next();
			// a cursor pointing to the next group
			while (sequence.hasNext()) 
				System.out.print(sequence.next() + "; ");
			System.out.flush();
			System.out.println();
		}
		
		sortBasedGrouper.close();
	}
}
