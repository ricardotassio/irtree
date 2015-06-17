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

package xxl.core.cursors.distincts;

import java.util.Comparator;
import java.util.Iterator;

import xxl.core.collections.queues.DistinctQueue;
import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.cursors.sorters.MergeSorter;
import xxl.core.functions.Function;
import xxl.core.predicates.Equal;
import xxl.core.predicates.Predicate;

/**
 * A sort-based implementation of a distinct operator, i.e., all duplicates
 * contained in the input cursor will be removed. The input cursor is traversed
 * and a predicate determines whether two successive input elements are equal.
 * If this is the case the duplicates will not be returned by calls to
 * <tt>next</tt> or <tt>peek</tt>.
 * 
 * <p><b>Note:</b> This operator can be used with a sorted or unsorted input
 * cursor! If the input cursor is not sorted a new
 * {@link xxl.core.cursors.sorters.MergeSorter merge sorter} will be created
 * using {@link xxl.core.collections.queues.DistinctQueue distinct} queues for an
 * early duplicate elimination. Therefore this operator only forwards the method
 * calls to the merge sorter. If an input iteration is given by an object of the
 * class {@link java.util.Iterator Iterator}, i.e., it does not support the
 * <tt>peek</tt> operation, it is internally wrapped to a cursor.</p>
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     SortBasedDistinct distinct = new SortBasedDistinct(
 *         new ArrayCursor(
 *             new Integer[] {
 *                 new Integer(1), new Integer(1),
 *                 new Integer(2), new Integer(2),
 *                 new Integer(3),
 *                 new Integer(4), new Integer(4), new Integer(4),
 *                 new Integer(5),
 *                 new Integer(6)
 *             }
 *         )
 *     );
 * 
 *     distinct.open();
 * 
 *     while (distinct.hasNext())
 *         System.out.println(distinct.next());
 * 
 *     distinct.close();
 * </pre>
 *
 * The input cursor contains the elements {1, 1, 2, 2, 3, 4, 4, 4, 5, 6}. If the
 * sort-based distinct operation is applied all duplicates will be removed, i.e.,
 * each delivered element returned by call to <tt>next</tt> is unique. This
 * example uses the {@link xxl.core.predicates.Equal equal} predicate by default,
 * therefore two elements are compared according to their integer values. If the
 * sort-based distinct operator is completely consumed, the output of this
 * example looks as follows:
 * <pre>
 *     1
 *     2
 *     3
 *     4
 *     5
 *     6
 * </pre></p>
 *
 * <p><b>Example usage (2): early duplicate elimination</b>
 * <pre>
 *     System.out.println("\nEarly duplicate elimination: ");
 * 
 *     distinct = new SortBasedDistinct(
 *         new RandomIntegers(10, 20),
 *         ComparableComparator.DEFAULT_INSTANCE,
 *         8,
 *         12*4096,
 *         4*4096
 *     );
 *
 *     distinct.open();
 *
 *     while (distinct.hasNext())
 *         System.out.println(distinct.next());
 *
 *     distinct.close();
 * </pre>
 * This example sorts twenty randomly distributed integer values of the interval
 * [0, 10) and removes all duplicates using
 * {@link xxl.core.collections.queues.DistinctQueue distinct} queues to store the
 * elements during run-creation.</p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.collections.queues.Queue
 * @see xxl.core.collections.queues.DistinctQueue
 * @see xxl.core.cursors.distincts.NestedLoopsDistinct
 * @see xxl.core.relational.cursors.SortBasedDistinct
 */
public class SortBasedDistinct extends AbstractCursor {

	/**
	 * The input cursor delivering the elements.
	 */
	protected Cursor cursor;

	/**
	 * The predicate determining if two successive input elements are equal.
	 */
	protected Predicate predicate;

	/**
	 * The last object returned by the input cursor.
	 */
	protected Object last;

	/**
	 * A flag that shows if the input cursor is initialized, i.e., if the first
	 * element has been returned already.
	 */
	protected boolean initialized = false;

	/**
	 * A flag signaling if an early duplicate elimination has been performed.
	 */
	protected boolean sorted = false;

	/**
	 * Function returning a new
	 * {@link xxl.core.collections.queues.DistinctQueue distinct queue} only
	 * needed if an early duplicate elimination during the sort-operation is
	 * performed.
	 */
	protected Function newDistinctQueue = new Function() {
		public Object invoke(Object functionInputBufferSize, Object functionOutputBufferSize) {
			return DistinctQueue.FACTORY_METHOD.invoke();
		}
	};

	/**
	 * Creates a new sort-based distinct operator.
	 *
	 * @param sortedIterator a sorted input iterator that delivers the elements.
	 * @param predicate a binary predicate that returns <tt>true</tt> if two
	 *        successive input elements are equal.
	 */
	public SortBasedDistinct(Iterator sortedIterator, Predicate predicate) {
		this.cursor = Cursors.wrap(sortedIterator);
		this.predicate = predicate;
	}

	/**
	 * Creates a new sort-based distinct operator, which uses an
	 * {@link xxl.core.predicates.Equal equal} predicate to compare two
	 * successive input elements.
	 *
	 * @param sortedIterator a sorted input iterator that delivers the elements.
	 */
	public SortBasedDistinct(Iterator sortedIterator) {
		this(sortedIterator, Equal.DEFAULT_INSTANCE);
	}

	/**
	 * Creates a new sort-based distinct operator and sorts the input at first
	 * using a {@link xxl.core.cursors.sorters.MergeSorter merge sorter} with
	 * {@link xxl.core.collections.queues.DistinctQueue distinct} queues. The
	 * merge sorter performs an early duplicate elimination during the sort
	 * operation.
	 *
	 * @param input the input iterator to be sorted.
	 * @param comparator the comparator used to compare the elements in the heap
	 *       (replacement selection).
	 * @param blockSize the size of a block (page).
	 * @param objectSize the size of an object in main memory.
	 * @param memSize the memory available to the merge sorter during the
	 *        open-phase.
	 * @param firstOutputBufferRatio the ratio of memory available to the output
	 *        buffer during run-creation (0.0: use only one page for the output
	 *        buffer and what remains is used for the heap; 1.0: use as much
	 *        memory as possible for the output buffer).
	 * @param outputBufferRatio the amount of memory available to the output
	 *        buffer during intermediate merges (not the final merge) (0.0: use
	 *        only one page for the output buffer, what remains is used for the
	 *        merger and the input buffer, inputBufferRatio determines how the
	 *        remaining memory is distributed between them; 1.0: use as much
	 *        memory as possible for the output buffer).
	 * @param inputBufferRatio the amount of memory available to the input buffer
	 *        during intermediate merges (not the final merge) (0.0: use only one
	 *        page for the input buffer, what remains is used for the merger
	 *        (maximal FanIn); 1.0: use as much memory as possible for the input
	 *        buffer).
	 * @param finalMemSize the memory available to the merge sorter during the
	 *        next-phase.
	 * @param finalInputBufferRatio the amount of memory available to the input
	 *        buffer of the final (online) merge (0.0: use the maximum number of
	 *        inputs (maximal fanIn), i.e., perform the online merge as early as
	 *        possible; 1.0: write the entire data into a final queue, the online
	 *        "merger" just reads the data from this queue).
	 * @param newQueuesQueue if this function is invoked, the queue, that should
	 *        contain the queues to be merged, is returned. The function takes an
	 *        iterator and the comparator <tt>queuesQueueComparator</tt> as
	 *        parameters, e.g.,
	 *        <pre>
	 *            new Function() {
	 *                public Object invoke(Object iterator, Object comparator) {
	 *                    return new DynamicHeap((Iterator)iterator, (Comparator)comparator);
	 *                }
	 *            }
	 *        </pre>
	 *        The queues contained in the iterator are inserted in the dynamic
	 *        heap using the given comparator for comparison.
	 * @param queuesQueueComparator this comparator determines the next queue
	 *        used for merging.
	 *
	 * @see xxl.core.cursors.sorters.MergeSorter
	 */
	public SortBasedDistinct(
		Iterator input,
		Comparator comparator,
		int blockSize,
		int objectSize,
		int memSize,
		double firstOutputBufferRatio,
		double outputBufferRatio,
		double inputBufferRatio,
		int finalMemSize,
		double finalInputBufferRatio,
		Function newQueuesQueue,
		Comparator queuesQueueComparator
	) {
		this.cursor = new MergeSorter(
			input,
			comparator,
			blockSize,
			objectSize,
			memSize,
			firstOutputBufferRatio,
			outputBufferRatio,
			inputBufferRatio,
			finalMemSize,
			finalInputBufferRatio,
			newDistinctQueue,
			newQueuesQueue,
			queuesQueueComparator,
			false
		);
		sorted = true;
	}

	/**
	 * Creates a new sort-based distinct operator and sorts the input at first
	 * using a {@link xxl.core.cursors.sorters.MergeSorter merge sorter} with
	 * {@link xxl.core.collections.queues.DistinctQueue distinct} queues. The
	 * merge sorter performs an early duplicate elimination during the sort
	 * operation.
	 *
	 * @param input the input iterator to be sorted.
	 * @param comparator the comparator used to compare the elements in the heap
	 *        (replacement selection).
	 * @param objectSize the size of an object in main memory.
	 * @param memSize the memory available to the merge sorter during the
	 *        open-phase.
	 * @param finalMemSize the memory available to the merge sorter during the
	 *        next-phase.
	 *
	 * @see xxl.core.cursors.sorters.MergeSorter
	 */
	public SortBasedDistinct(
		Iterator input,
		Comparator comparator,
		int objectSize,
		int memSize,
		int finalMemSize
	) {
		this.cursor = new MergeSorter(
			input,
			comparator,
			objectSize,
			memSize,
			finalMemSize,
			newDistinctQueue,
			false
		);
		sorted = true;
	}

	/**
	 * Opens the sort-based distinct operator, i.e., signals the cursor to
	 * reserve resources, open the input iteration, etc. Before a cursor has been
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
	 * Closes the sort-based distinct operator. Signals the cursor to clean up
	 * resources, close the input cursor, etc. After a call to <tt>close</tt>
	 * calls to methods like <tt>next</tt> or <tt>peek</tt> are not guaranteed
	 * to yield proper results. Multiple calls to <tt>close</tt> do not have any
	 * effect, i.e., if <tt>close</tt> was called the sort-based distinct
	 * operator remains in the state "closed".
	 */
	public void close() {
		super.close();
		cursor.close();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other words,
	 * returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would return an
	 * element rather than throwing an exception.)
	 * 
	 * <p>The first element can instantly be returned, after that the operator
	 * remains in the state <tt>initialized</tt>. If a further element exists it
	 * is checked by the given predicate invoked on the last and the next element
	 * of the input cursor. If the predicate returns <tt>true</tt> this next
	 * element is filtered out, otherwise it will be returned by a call to
	 * <tt>next</tt>.</p>
	 *
	 * @return <tt>true</tt> if the sort-based distinct operator has more
	 *         elements.
	 */
	protected boolean hasNextObject() {
		if (!initialized || sorted)
			return cursor.hasNext();
		else
			for (; cursor.hasNext(); cursor.next())
				if (!predicate.invoke(last, cursor.peek()))
					return true;
		return false;
	}

	/**
	 * Returns the next element in the iteration. This element will be removed
	 * from the iteration, if <tt>next</tt> is called. This method returns the
	 * next element of the input cursor, which has not been filtered out by the
	 * given predicate.
	 *
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		initialized = true;
		return last = cursor.next();
	}

	/**
	 * Removes from the underlying data structure the last element returned by
	 * the sort-based distinct operator (optional operation). This method can be
	 * called only once per call to <tt>next</tt> or <tt>peek</tt>. The behaviour
	 * of a sort-based distinct operator is unspecified if the underlying data
	 * structure is modified while the iteration is in progress in any way other
	 * than by calling this method.
	 *
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>remove</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is
	 *         not supported by the sort-based distinct operator, i.e., the
	 *         underlying cursor does not support the <tt>remove</tt> operation.
	 */
	public void remove() throws IllegalStateException, UnsupportedOperationException {
		super.remove();
		cursor.remove();
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the sort-based distinct operator. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsRemove() {
		return cursor.supportsRemove();
	}

	/**
	 * Replaces the object that was returned by the last call to <tt>next</tt>
	 * or <tt>peek</tt> (optional operation). This operation must not be called
	 * after a call to <tt>hasNext</tt>. It should follow a call to <tt>next</tt>
	 * or <tt>peek</tt>. This method should be called only once per call to
	 * <tt>next</tt> or <tt>peek</tt>. The behaviour of a sort-based distinct
	 * operator is unspecified if the underlying data structure is modified while
	 * the iteration is in progress in any way other than by calling this method.
	 *
	 * @param object the object that replaces the object returned by the last
	 *        call to <tt>next</tt> or <tt>peek</tt>.
	 * @throws IllegalStateException if there is no object which can be updated.
	 * @throws UnsupportedOperationException if the <tt>update</tt> operation is
	 *         not supported by the SortBasedDistinct operator, i.e., the
	 *         underlying cursor does not support the <tt>update</tt> operation.
	 */
	public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
		super.update(object);
		cursor.update(object);
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the sort-based distinct operator. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsUpdate() {
		return cursor.supportsUpdate();
	}

	/**
	 * Resets the sort-based distinct operator to its initial state (optional
	 * operation). So the caller is able to traverse the underlying data
	 * structure again. The modifications, removes and updates concerning the
	 * underlying data structure, are still persistent.
	 * 
	 * @throws UnsupportedOperationException if the <tt>reset</tt> method is not
	 *         supported by the sort-based distinct operator.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		cursor.reset();
		initialized = false;
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the sort-based distinct operator. Otherwise it returns <tt>false</tt>.
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
		
		SortBasedDistinct distinct = new SortBasedDistinct(
			new xxl.core.cursors.sources.ArrayCursor(
				new Integer[] {
					new Integer(1), new Integer(1),
					new Integer(2), new Integer(2),
					new Integer(3),
					new Integer(4),	new Integer(4), new Integer(4),
					new Integer(5),
					new Integer(6)
				}
			)
		);

		distinct.open();
		
		while (distinct.hasNext())
			System.out.println(distinct.next());
			
		distinct.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		System.out.println("\nEarly duplicate elimination: ");
		
		distinct = new SortBasedDistinct(
			new xxl.core.cursors.sources.RandomIntegers(10, 20),
			xxl.core.comparators.ComparableComparator.DEFAULT_INSTANCE,
			8,
			12*4096,
			4*4096
		);
		
		distinct.open();
		
		while (distinct.hasNext())
			System.out.println(distinct.next());
			
		distinct.close();
	}
}
