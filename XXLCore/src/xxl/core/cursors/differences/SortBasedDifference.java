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

package xxl.core.cursors.differences;

import java.util.Comparator;
import java.util.Iterator;

import xxl.core.comparators.ComparableComparator;
import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;

/**
 * A sort-based implementation of the difference operator
 * (<tt>input1 - input2</tt>). This operation can be performed in two different
 * ways, namely the first realization removes an element of <tt>input1</tt> if
 * the same element exists in <tt>input2</tt>. The second way of processing
 * removes all elements of <tt>input1</tt> that match with an element of
 * <tt>input2</tt>. This second approch implies that no duplicates will be
 * returned by the difference operator, whereas the first solution may contain
 * duplicates if the number of equal elements in cursor <tt>input1</tt> is
 * greater than that of <tt>input2</tt>.
 * 
 * <p>The boolean flag <tt>all</tt> signals if all elements of cursor
 * <tt>input1</tt> that are equal to an element of <tt>input2</tt> will be
 * removed. In contrast to setting the flag to <tt>false</tt>, only one element
 * will be removed. So depending on this flag the result of the difference
 * operation can be a <i>set</i>, i.e., if <tt>all</tt> is <tt>true</tt>  all
 * duplicates will be removed in the output of this cursor, otherwise only one
 * element is removed and the result may be a <i>multi-set</i>, i.e., duplicates
 * may occur in the output. If no comparator has been specified in the
 * constructor, a default
 * {@link xxl.core.comparators.ComparableComparator comparator} will be used
 * which assumes that the input iterators' elements implement the
 * {@link java.lang.Comparable comparable} interface.</p>
 * 
 * 
 * <p><b>Precondition:</b> The input cursors have to be sorted!</p>
 * 
 * <p><b>Note:</b> If an input iteration is given by an object of the class
 * {@link java.util.Iterator}, i.e., it does not support the <tt>peek</tt>
 * operation, it is internally wrapped to a cursor.</p>
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     SortBasedDifference difference = new SortBasedDifference(
 *         new Enumerator(21),
 *         new Filter(
 *             new Enumerator(21),
 *             new Predicate() {
 *                 public boolean invoke(Object next) {
 *                     return ((Integer)next).intValue() % 2 == 0;
 *                 }
 *             }
 *         ),
 *         ComparableComparator.DEFAULT_INSTANCE,
 *         true,
 *         true   //input sorted ascending
 *     );
 * 
 *     difference.open();
 * 
 *     while (difference.hasNext())
 *         System.out.println(difference.next());
 * 
 *     difference.close();
 * </pre>
 * This example shows how to remove all even numbers from a given enumerator with
 * range [0, 20]. A default instance of a
 * {@link xxl.core.comparators.ComparableComparator comparable-comparator} is
 * used to compare the elements of the two inputs. This kind of comparator is
 * also be chosen, if no comparator has been specified. The flag <tt>all</tt>,
 * which is set to <tt>true</tt> does not have any effect in this case due to
 * unique input elements.</p>
 * 
 * <p><b>Example usage (2):</b>
 * <pre>
 *     difference = new SortBasedDifference(
 *         new ArrayCursor(
 *             new Integer[] {
 *                 new Integer(1),
 *                 new Integer(2),
 *                 new Integer(2),
 *                 new Integer(2),
 *                 new Integer(3)
 *             }
 *         ),
 *         new ArrayCursor(
 *             new Integer[] {
 *                 new Integer(1),
 *                 new Integer(2),
 *                 new Integer(2),
 *                 new Integer(3)
 *             }
 *         ),
 *         false,
 *         true   //input sorted ascending
 *     );
 * 
 *     difference.open();
 * 
 *     while (difference.hasNext())
 *         System.out.println(difference.next());
 * 
 *     difference.close();
 * </pre>
 * The first input cursor contains the elements {1, 2, 2, 2, 3}. The second
 * cursor, that is to be subtracted, delivers the elements {1, 2, 2, 3}. So, in
 * this case the flag <tt>all</tt> plays an important role. If it is
 * <tt>false</tt>, as shown above, the sort-based difference operator delivers
 * the element {2} as the only result. If it has been set to <tt>true</tt> the
 * sort-based difference operator returns no elements.</p>
 * 
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see java.util.Comparator
 * @see xxl.core.comparators.ComparableComparator
 * @see xxl.core.cursors.differences.NestedLoopsDifference
 * @see xxl.core.relational.cursors.SortBasedDifference
 */
public class SortBasedDifference extends AbstractCursor {

	/**
	 * The first (or left) input cursor of the difference operator.
	 */
	protected Cursor input1;
	
	/**
	 * The second (or right) input cursor of the difference operator.
	 */
	protected Cursor input2;
	
	/**
	 * The comparator used to compare the elements of the two input cursors.
	 */
	protected Comparator comparator;

	/**
	 * A flag signaling if all matches returned by the comparator should be
	 * removed or only one element will be removed. So depending on this flag the
	 * result of difference operation can be a set, i.e., if <tt>all</tt> is
	 * <tt>true</tt> all duplicates will be removed in the resulting cursor,
	 * otherwise only one is removed and the result may be a multi-set, i.e.,
	 * duplicates may occur in the resulting cursor.
	 */
	protected boolean all;

	/**
	 * A flag showing if the input cursors have been sorted in ascending or
	 * descending order.
	 */
	protected boolean asc;

	/**
	 * Creates a new instance of the sort-based difference operator. Every
	 * iterator given to this constructor is wrapped to a cursor.
	 *
	 * @param sortedInput1 the first input iterator where the elements have to be
	 *        subtracted from.
	 * @param sortedInput2 the second input iterator containing the elements that
	 *        have to be subtracted.
	 * @param comparator a comparator comparing the elements of the two input
	 *        cursors.
	 * @param all a boolean flag signaling if all elements of cursor
	 *        <tt>input1</tt> that are equal to an element of <tt>input2</tt>
	 *        will be removed, otherwise only one element is removed.
	 * @param asc a flag showing if the input cursors have been sorted ascending
	 *        or descending.
	 */
	public SortBasedDifference(Iterator sortedInput1, Iterator sortedInput2, Comparator comparator, boolean all, boolean asc) {
		this.input1 = Cursors.wrap(sortedInput1);
		this.input2 = Cursors.wrap(sortedInput2);
		this.comparator = comparator;
		this.all = all;
		this.asc = asc;
	}

	/**
	 * Creates a new instance of the sort-based difference operator. Every
	 * iterator given to this constructor is wrapped to a cursor. Uses a default
	 * {@link xxl.core.comparators.ComparableComparator comparator} for the
	 * comparison of the input cursors' elements which assumes that these
	 * elements implement the {@link java.lang.Comparable comparable} interface.
	 *
	 * @param sortedInput1 the first input iterator where the elements have to be
	 *        subtracted from.
	 * @param sortedInput2 the second input iterator containing the elements that
	 *        have to be subtracted.
	 * @param all a boolean flag signaling if all elements of cursor
	 *        <tt>input1</tt> that are equal to an element of <tt>input2</tt>
	 *        will be removed, otherwise only one element is removed.
	 * @param asc a flag showing if the input cursors have been sorted ascending
	 *        or descending.
	 */
	public SortBasedDifference(Iterator sortedInput1, Iterator sortedInput2, boolean all, boolean asc) {
		this(sortedInput1, sortedInput2, ComparableComparator.DEFAULT_INSTANCE, all, asc);
	}

	/**
	 * Opens the sort-based difference operator, i.e., signals the cursor to
	 * reserve resources, open input iterations, etc. Before a cursor has been
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
		input1.open();
		input2.open();
	}
	
	/**
	 * Closes the sort-based difference operator, i.e., signals the cursor to
	 * clean up resources and close its input cursors. When a cursor has been
	 * closed calls to methods like <tt>next</tt> or <tt>peek</tt> are not
	 * guaranteed to yield proper results. Multiple calls to <tt>close</tt> do
	 * not have any effect, i.e., if <tt>close</tt> was called the cursor remains
	 * in the state <i>closed</i>.
	 * 
	 * <p>Note, that a closed cursor usually cannot be opened again because of
	 * the fact that its state generally cannot be restored when resources are
	 * released respectively files are closed.</p>
	 */
	public void close() {
		super.close();
		input1.close();
		input2.close();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other words,
	 * returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would return an
	 * element rather than throwing an exception.)
	 * 
	 * <p>If <tt>input2</tt> has no further elements all remaining elements of
	 * <tt>input1</tt> can be returned. If the input cursors are sorted
	 * ascending, also the elements of <tt>input1</tt> are returned, if they are
	 * smaller than the next element of <tt>input2</tt>. If the next element of
	 * <tt>input1</tt> and <tt>input2</tt> are equal, i.e., the given comparator
	 * returns 0, when comparing them, this element will not be returned.
	 * Depending on the flag <tt>all</tt>, all elements that are equal to the
	 * skipped element are skipped, too. So, if <tt>all</tt> is <tt>true</tt> the
	 * resulting cursor contains no duplicates. If the next element of
	 * <tt>input2</tt> is larger than the next one of <tt>input1</tt>,
	 * <tt>input2</tt> is consumed as long as this condition is fulfilled. If the
	 * input cursors are sorted descending the conditions explained above are
	 * negated and the computation runs the same way.
	 * 
	 * @return <tt>true</tt> if the sort-based difference operator has more
	 *         elements.
	 */
	protected boolean hasNextObject() {
		boolean exit;
		do {
			exit=true;
			if (!input2.hasNext())
			  	if (input1.hasNext()) {
			  		next = input1.next();
			  		return true;
			  	}
			  	else
			  		return false;
			else {
				if (input1.hasNext()) {
				  	int res = comparator.compare(input1.peek(), input2.peek());
					if ((asc && res < 0) || (!asc && res > 0)) {
						next = input1.next();
						return true;
					}
					else
						if (res == 0) {
							input1.next();
							if (all) // remove duplicates
								while(input1.hasNext() && comparator.compare(input1.peek(), input2.peek()) == 0)
									input1.next();
							else
								input2.next();
							exit = false;
						}
						else { // (asc && res > 0) || (!asc && res < 0)
							input2.next();
							while(input2.hasNext() && ((asc && comparator.compare(input1.peek(), input2.peek()) > 0) || (!asc && comparator.compare(input1.peek(), input2.peek()) < 0)))
								input2.next();
							exit = false;
						}
				}
				else
					return false;
			}
		}
		while (!exit);
		return false;
	}

	/**
	 * Returns the next element in the iteration. This element will be removed
	 * from the iteration, if <tt>next</tt> is called.
	 *
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return next;
	}

	/**
	 * Removes from the underlying data structure the last element returned by
	 * the sort-based difference operator (optional operation). This method can
	 * be called only once per call to <tt>next</tt> or <tt>peek</tt> and removes
	 * the element returned by this method. Note, that between a call to
	 * <tt>next</tt> and <tt>remove</tt> the invocation of <tt>peek</tt> or
	 * <tt>hasNext</tt> is forbidden. The behaviour of a cursor is unspecified
	 * if the underlying data structure is modified while the iteration is in
	 * progress in any way other than by calling this method.
	 *
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>remove</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is
	 *         not supported by the sort-based difference operator.
	 */
	public void remove() throws IllegalStateException, UnsupportedOperationException {
		super.remove();
		input1.remove();
	}

	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the sort-based difference operator. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsRemove() {
		return input1.supportsRemove();
	}

	/**
	 * Replaces the object that was returned by the last call to <tt>next</tt>
	 * or <tt>peek</tt> (optional operation). This operation must not be called
	 * after a call to <tt>hasNext</tt>. It should follow a call to <tt>next</tt>
	 * or <tt>peek</tt>. This method should be called only once per call to
	 * <tt>next</tt> or <tt>peek</tt>. The behaviour of a sort-based difference
	 * operator is unspecified if the underlying data structure is modified while
	 * the iteration is in progress in any way other than by calling this method.
	 * 
	 * @param object the object that replaces the object returned by the last
	 *        call to <tt>next</tt> or <tt>peek</tt>.
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>update</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>update</tt> operation is
	 *         not supported by the sort-based difference operator.
	 */
	public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
		super.update(object);
		input1.update(object);
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the sort-based difference operator. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsUpdate() {
		return input1.supportsUpdate();
	}

	/**
	 * Resets the sort-based difference operator to its initial state (optional
	 * operation). So the caller is able to traverse the underlying data
	 * structure again. The modifications, removes and updates concerning the
	 * underlying data structure, are still persistent. This method also resets
	 * the input iterations.
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the sort-based difference operator.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		input1.reset();
		input2.reset();
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the sort-based difference operator. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return input1.supportsReset() && input2.supportsReset();
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
		
		SortBasedDifference difference = new SortBasedDifference(
			new xxl.core.cursors.sources.Enumerator(21),
			//EmptyCursor.DEFAULT_INSTANCE,
			new xxl.core.cursors.filters.Filter(
				new xxl.core.cursors.sources.Enumerator(21),
				new xxl.core.predicates.Predicate() {
					public boolean invoke (Object next) {
						return ((Integer)next).intValue() % 2 == 0;
					}
				}
			),
			ComparableComparator.DEFAULT_INSTANCE,
			true,
			true
		);
		
		difference.open();

		while (difference.hasNext())
			System.out.println(difference.next());
		
		difference.close();

		System.out.println();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		difference = new SortBasedDifference(
			new xxl.core.cursors.sources.ArrayCursor(
				new Integer[] {
					new Integer(1),
					new Integer(2),
					new Integer(2),
					new Integer(2),
					new Integer(3)
				}
			),
			new xxl.core.cursors.sources.ArrayCursor(
				new Integer[] {
					new Integer(1),
					new Integer(2),
					new Integer(2),
					new Integer(3)
				}
			),
			false,
			true
		);
		difference.open();
		
		while (difference.hasNext())
			System.out.println(difference.next());
		
		difference.close();

	}
}
