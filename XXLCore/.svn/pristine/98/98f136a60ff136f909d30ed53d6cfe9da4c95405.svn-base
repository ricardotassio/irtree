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

package xxl.core.cursors.intersections;

import java.util.Comparator;
import java.util.Iterator;

import xxl.core.collections.sweepAreas.SortMergeEquiJoinSA;
import xxl.core.collections.sweepAreas.SweepAreaImplementor;
import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.predicates.Equal;
import xxl.core.predicates.Predicate;

/**
 * A sort-based implementation of the intersection operator. The sort-based
 * intersection operator is based on a step-by-step processing of the two input
 * iterations in consideration of their sort-order. The sweep-line status
 * structure, here called sweep-area, consists of a bag with an additional method
 * for reorganisation and is used to store the elements of the first input
 * iteration. When an element of the second input iteration is processed, it is
 * used to query the sweep-area for matching elements that can be returned as
 * result of the intersection operator. Therefor an user defined predicate is
 * used to decide whether two elements of the input iterations are equal
 * concerning their values.
 * 
 * <p><b>Precondition:</b> The input cursors have to be sorted!</p>
 * 
 * <p><b>Note:</b> When the given input iteration only implements the interface
 * {@link java.util.Iterator} it is wrapped to a cursor by a call to the static
 * method {@link xxl.core.cursors.Cursors#wrap(Iterator) wrap}.</p>
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     LinkedList l1 = new LinkedList();
 *     final LinkedList l2 = new LinkedList();
 *     for (int i = 0; i &lt;= 10; i++) {
 *         if (i%2 == 0)
 *             l2.add(new Integer(i));
 *         l1.add(new Integer(i));
 *     }
 * 
 *     System.out.println("Example 1:");
 * 
 *     SortBasedIntersection intersection = new SortBasedIntersection(
 *         l1.listIterator(),
 *         l2.listIterator(),
 *         new xxl.core.collections.sweepAreas.ListSAImplementor(),
 *         xxl.core.comparators.ComparableComparator.DEFAULT_INSTANCE
 *     );
 * 
 *     intersection.open();
 * 
 *     Cursors.println(intersection);
 * 
 *     intersection.close();
 * </pre>
 * The input iteration for the intersection operation are given by two
 * list-iterators. The the first one contains all integer elements of the
 * interval [0, 10]. The second one delivers only the even elements of the same
 * interval. So the intersection operator should return the same integer values
 * as contained in the second input cursor. Because an iterator is not resetable,
 * a function reseting the second input cursor has to be provided, because the
 * second input cursor is traversed in the inner loop and has to be reseted as
 * often as the first input iterator contains elements. The reseting function has
 * to implement the parameterless <tt>invoke</tt> method and in this case it
 * delivers a newlist iterator iterating over the second input list.</p>
 * 
 * <p><b>Example usage (2):</b>
 * <pre>
 *     LinkedList list1 = new LinkedList();
 *     list1.add(new Integer(2));
 *     list1.add(new Integer(2));
 *     final LinkedList list2 = new LinkedList();
 *     list2.add(new Integer(2));
 *     list2.add(new Integer(2));
 *     list2.add(new Integer(2));
 * 
 *     System.out.println("\nExample 2:");
 * 
 *     intersection = new SortBasedIntersection(
 *         list1.listIterator(),
 *         list2.listIterator(),
 *         new xxl.core.collections.sweepAreas.ListSAImplementor(),
 *         xxl.core.comparators.ComparableComparator.DEFAULT_INSTANCE
 *     );
 * 
 *     intersection.open();
 * 
 *     Cursors.println(intersection);
 * 
 *     intersection.close();
 * </pre>
 * The second example usage computes the intersection of the input iterations
 * {2,2} and {2,2,2}. The intersection is created in the same way as in the
 * previous example. Conforming with the correct duplicate handling the result
 * will be {2,2}.</p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.intersections.NestedLoopsIntersection
 * @see xxl.core.relational.cursors.SortBasedIntersection
 * @see xxl.core.collections.sweepAreas.SweepArea
 */
public class SortBasedIntersection extends AbstractCursor {

	/**
	 * The two sorted input iteration of the sort-based intersection operator.
	 */
	protected Cursor[] inputs = new Cursor[2];
	
	/**
	 * The sweep-area that is used for storing the elements of the first input
	 * iteration (<tt>inputs[0]</tt>) and that is probed with elements of the
	 * second input iteration (<tt>inputs[1]</tt>).
	 */
	protected SortMergeEquiJoinSA sweepArea;
	
	/**
	 * The comparator used to compare the elements of the two input iterations.
	 */
	protected Comparator comparator;
	
	/**
	 * A binary predicate evaluated for each tuple of elements backed on one
	 * element of each input iteration in order to select them. Only these tuples
	 * where the predicate's evaluation result is <tt>true</tt> have been
	 * qualified to be a result of the intersection operation.
	 */
	protected Predicate equals;
	
	/**
	 * Creates a new sort-based intersection operator backed on two sorted input
	 * iterations using the given sweep-area to store the elements of the first
	 * input iteration and probe with the elements of the second one for
	 * matchings.
	 * 
	 * <p><b>Precondition:</b> The input iterations have to be sorted!</p>
	 * 
	 * <p>The given binary predicate to decide whether two tuples match will be
	 * used along with the sweep-area implementor to create a new sweep-area.
	 * Every iterator given to this constructor is wrapped to a cursor.<p>
	 *
	 * @param sortedInput0 the first sorted input iteration to be intersected.
	 * @param sortedInput1 the second sorted input iteration to be intersected.
	 * @param impl the sweep-area implementor used for storing elements of the
	 *        first sorted input iteration (<tt>sortedInput0</tt>).
	 * @param comparator the comparator that is used for comparing elements of
	 *        the two sorted input iterations.
	 * @param equals the binary predicate evaluated for each tuple of elements
	 *        backed on one element of each input iteration in order to select
	 *        them. Only these tuples where the predicate's evaluation result is
	 *        <tt>true</tt> have been qualified to be a result of the
	 *        intersection operation.
	 */
	public SortBasedIntersection(Iterator sortedInput0, Iterator sortedInput1, SweepAreaImplementor impl, Comparator comparator, Predicate equals) {
		this.inputs[0] = Cursors.wrap(sortedInput0);
		this.inputs[1] = Cursors.wrap(sortedInput1);
		this.sweepArea = new SortMergeEquiJoinSA(impl, 0, 2, equals);
		this.comparator = comparator;
		this.equals = equals;
	}
	
	/**
	 * Creates a new sort-based intersection operator backed on two sorted input
	 * iterations using the given sweep-area to store the elements of the first
	 * input iteration and probe with the elements of the second one for
	 * matchings.
	 * 
	 * <p><b>Precondition:</b> The input iterations have to be sorted!</p>
	 * 
	 * <p>A default {@link xxl.core.predicates.Equal equality} predicate to
	 * decide whether two tuples match will be used along with the given
	 * sweep-area implementor to create a new sweep-area. Every iterator given to
	 * this constructor is wrapped to a cursor.<p>
	 *
	 * @param sortedInput0 the first sorted input iteration to be intersected.
	 * @param sortedInput1 the second sorted input iteration to be intersected.
	 * @param impl the sweep-area implementor used for storing elements of the
	 *        first sorted input iteration (<tt>sortedInput0</tt>).
	 * @param comparator the comparator that is used for comparing elements of
	 *        the two sorted input iterations.
	 */
	public SortBasedIntersection(Iterator sortedInput0, Iterator sortedInput1, SweepAreaImplementor impl, Comparator comparator) {
		this(sortedInput0, sortedInput1, impl, comparator, Equal.DEFAULT_INSTANCE);
	}
	
	/**
	 * Opens the intersection operator, i.e., signals the cursor to reserve
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
		inputs[0].open();
		inputs[1].open();
	}
	
	/**
	 * Closes the intersection operator, i.e., signals the cursor to clean up
	 * resources, close the input iterations, etc. When a cursor has been closed
	 * calls to methods like <tt>next</tt> or <tt>peek</tt> are not guaranteed to
	 * yield proper results. Multiple calls to <tt>close</tt> do not have any
	 * effect, i.e., if <tt>close</tt> was called the cursor remains in the state
	 * <i>closed</i>.
	 * 
	 * <p>Note, that a closed cursor usually cannot be opened again because of
	 * the fact that its state generally cannot be restored when resources are
	 * released respectively files are closed.</p>
	 */
	public void close() {
		super.close();
		inputs[0].close();
		inputs[1].close();
		sweepArea.close();
	}
	
	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * <p>The implementation of this method is as follows:
	 * <pre>
	 *     while (inputs[0].hasNext() || inputs[1].hasNext()) {
	 *         int j = !inputs[0].hasNext() ?
	 *             1 :
	 *             !inputs[1].hasNext() ?
	 *                 0 :
	 *                 comparator.compare(inputs[0].peek(), inputs[1].peek()) &le; 0 ?
	 *                     0 :
	 *                     1;
	 *         Object queryObject = inputs[j].next();
	 *         sweepArea.reorganize(queryObject, j);
	 *         if (j == 0)
	 *             sweepArea.insert(queryObject);
	 *         else {
	 *             Iterator results = sweepArea.query(queryObject, j);
	 *             if (results.hasNext()) {
	 *                 next = results.next();
	 *                 results.remove();
	 *                 return true;
	 *             }
	 *         }
	 *     }
	 *     return false;
	 * </pre>
	 * The int value <t>j</tt> holds the index of the input iteration that
	 * delivers the next object to be proceeded (according to the sort-order of
	 * the input iterations) and <tt>queryObject</tt> stores this object.
	 * Thereafter the sweep-area is reorganized in order to remove object that
	 * cannot find a match in the second input iteration any more. Finally
	 * <tt>queryObject</tt> will be inserted into the sweep-area, if it comes
	 * from the first input iteration, or it will be used to query the sweep-area
	 * for matches, if it comes from the second one.</p>
	 * 
	 * @return <tt>true</tt> if the intersection operator has more elements.
	 */
	protected boolean hasNextObject() {
		while (inputs[0].hasNext() || inputs[1].hasNext()) {
			int j = !inputs[0].hasNext() ?
				1 :
				!inputs[1].hasNext() ?
					0 :
					comparator.compare(inputs[0].peek(), inputs[1].peek()) <= 0 ?
						0 :
						1;
			Object queryObject = inputs[j].next();
			sweepArea.reorganize(queryObject, j);
			if (j == 0)
				sweepArea.insert(queryObject);
			else {	
				Iterator results = sweepArea.query(queryObject, j);
				if (results.hasNext()) {
					next = results.next();
					results.remove();
					return true;
				} 
			}
		}
		return false;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the intersection operator's methods, e.g.,
	 * <tt>update</tt> or <tt>remove</tt>, until a call to <tt>next</tt> or
	 * <tt>peek</tt> occurs. This is calling <tt>next</tt> or <tt>peek</tt>
	 * proceeds the iteration and therefore its previous element will not be
	 * accessible any more.
	 *
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return next;
	}
	
	/**
	 * Resets the intersection operator to its initial state such that the caller
	 * is able to traverse the join again without constructing a new intersection
	 * operator (optional operation).
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 * 
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the intersection operator.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		inputs[0].reset();
		inputs[1].reset();
		sweepArea.clear();
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the intersection operator. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the intersection operator, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return inputs[0].supportsReset() && inputs[1].supportsReset();
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
		
		java.util.LinkedList l1 = new java.util.LinkedList();
		final java.util.LinkedList l2 = new java.util.LinkedList();
		for (int i = 0; i <= 10; i++) {
			if (i%2 == 0)
				l2.add(new Integer(i));
			l1.add(new Integer(i));
		}
		
		System.out.println("Example 1:");
		
		SortBasedIntersection intersection = new SortBasedIntersection(
			l1.listIterator(),
			l2.listIterator(),
			new xxl.core.collections.sweepAreas.ListSAImplementor(),
			xxl.core.comparators.ComparableComparator.DEFAULT_INSTANCE
		);
		
		intersection.open();
		
		Cursors.println(intersection);
		
		intersection.close();
		
		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		java.util.LinkedList list1 = new java.util.LinkedList();
		list1.add(new Integer(2));
		list1.add(new Integer(2));
		final java.util.LinkedList list2 = new java.util.LinkedList();
		list2.add(new Integer(2));
		list2.add(new Integer(2));
		list2.add(new Integer(2));
		
		intersection = new SortBasedIntersection(
			list1.listIterator(),
			list2.listIterator(),
			new xxl.core.collections.sweepAreas.ListSAImplementor(),
			xxl.core.comparators.ComparableComparator.DEFAULT_INSTANCE
		);
		
		System.out.println("\nExample 2:");
		
		intersection.open();
		
		Cursors.println(intersection);
		
		intersection.close();
	}
}
