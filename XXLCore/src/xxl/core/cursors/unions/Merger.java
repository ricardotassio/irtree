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

package xxl.core.cursors.unions;

import java.util.Comparator;
import java.util.Iterator;

import xxl.core.collections.queues.Heap;
import xxl.core.collections.queues.Queue;
import xxl.core.comparators.ComparableComparator;
import xxl.core.comparators.FeatureComparator;
import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.functions.Function;

/**
 * A merger serializes input iterations with respect to a given
 * {@link java.util.Comparator comparator} or
 * {@link xxl.core.collections.queues.Queue queue}. The input iterations are
 * inserted into a queue, e.g., a {@link xxl.core.collections.queues.Heap heap},
 * defining a strategy for merging them. Some queues need a comparator to put
 * their elements in order, so the caller is able to specify his queue and his
 * comparator with special constructors. The serialization of the input
 * iteration's elements is depending on the queue's implementation, e.g., the
 * queue can use a FIFO-strategy with the intention to receive a cyclic order of
 * the input iteration's elements.
 * <pre>
 *     cursors[0].next(),
 *     ...,
 *     cursors[cursors-1].next(),
 *     cursors[0].next(),
 *     ...
 * </pre>
 * But be aware, that the merger works with lazy-evaluation, so calling the 
 * <tt>next</tt> or <tt>peek</tt> method accesses the next cursor, named
 * <tt>minCursor</tt> delivered by the queue and calls its <tt>next</tt> or
 * <tt>peek</tt> method. If this iteration contains further elements it will be
 * added to the queue for an other time. The position this iteration is inserted
 * in the queue is defined by the queue's strategy.
 * 
 * <p><b>Note:</b> When the given input iteration only implements the interface
 * {@link java.util.Iterator} it is wrapped to a cursor by a call to the static
 * method {@link xxl.core.cursors.Cursors#wrap(Iterator) wrap}.</p>
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     HashGrouper hashGrouper = new HashGrouper(
 *         new Enumerator(21),
 *         new Function() {
 *             public Object invoke(Object next) {
 *                 return new Integer(((Integer)next).intValue() % 5);
 *             }
 *         }
 *     );
 * 
 *     hashGrouper.open();
 * 
 *     Cursor[] cursors = new Cursor[5];
 *     for (int i = 0; hashGrouper.hasNext(); i++)
 *         cursors[i] = (Cursor)hashGrouper.next();
 * 
 *     Merger merger = new Merger(
 *         cursors,
 *         new Comparator() {
 *             public int compare(Object o1, Object o2) {
 *                 return ((Integer)o1).compareTo(o2);
 *             }
 *         }
 *     );
 * 
 *     merger.open();
 * 
 *     while (merger.hasNext())
 *         System.out.print(merger.next() +"; ");
 *     System.out.flush();
 * 
 *     merger.close();
 *     hashGrouper.close();
 * </pre>
 * This example uses a hash-grouper to partition the input data, i.e., the
 * function (object modulo 5) is invoked on each element of the enumerator with
 * range 0,...,20. Because the <tt>next</tt> method of the hash-grouper returns a
 * cursor pointing to the next group, i.e., the next bucket in the hash-map, all
 * returned cursors are stored in a cursor array named <tt>cursors</tt>. For more
 * detailed information see {@link xxl.core.cursors.groupers.HashGrouper}. This
 * cursor array is given to the constructor of the merger internally using the
 * defined comparator that compares two integer objects. This instance of a
 * merger uses a {@link xxl.core.collections.queues.Heap heap} to arrange the
 * cursors, because no queue has been specified in the constructor. Because of
 * the specified comparator and the implementation of the heap realizing a
 * min-heap, the elements in the buckets of the hash-map are merged and returned
 * in ascending order. The generated output is as follows:
 * <pre>
 *     0; 1; 2; 3; 4; 5; 6; 7; 8; 9; 10; 11; 12; 13; 14; 15; 16; 17; 18; 19; 20;
 * </pre>
 * 
 * <p><b>Example usage (2):</b>
 * <pre>
 *     Merger merger2 = new Merger(
 *         new Enumerator(11),
 *         new Enumerator(11, 21),
 *         (LIFOQueue)LIFOQueue.FACTORY_METHOD.invoke()
 *     );
 * 
 *     merger2.open();
 * 
 *     while (merger2.hasNext())
 *         System.out.print(merger2.next() + "; ");
 *     System.out.flush();
 *     
 *     merger2.close();
 * </pre>
 * In this case, the used queue realizes a LIFO-queue (last in first out), so the
 * second enumerator is completely consumed at first, then the elements of the
 * first enumerator are returned. So the elements are printed to the output
 * stream in the following order:
 * <pre>
 *     11; 12; 13; 14; 15; 16; 17; 18; 19; 20; 0; 1; 2; 3; 4; 5; 6; 7; 8; 9; 10;
 * </pre>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 * @see java.util.Comparator
 */
public class Merger extends AbstractCursor {

	/**
	 * The array containing the input iterations to be merged.
	 */
	protected Cursor[] cursors;

	/**
	 * The queue used to define an order for the merge of the input iterations.
	 */
	protected Queue queue;

	/**
	 * The iteration representing the next element of the queue. All method calls
	 * concerning the actual element of the merger are redirected to this
	 * iteration.
	 */
	protected Cursor minCursor = null;

	/**
	 * Creates a new merger backed on an input iteration array and a queue
	 * delivering the strategy used for merging the input iterations. All input
	 * iterations are inserted into the given queue. Every iterator given to this
	 * constructor is wrapped to a cursor.
	 *
	 * @param iterators the input iterations to be merged.
	 * @param queue the queue defining the strategy the input iterations are
	 *        accessed.
	 */
	public Merger(Iterator[] iterators, Queue queue) {
		this.cursors = new Cursor[iterators.length];
		for (int i = 0; i < iterators.length; i++)
			cursors[i] = Cursors.wrap(iterators[i]);
		this.queue = queue;
	}

	/**
	 * Creates a new merger backed on an input iteration array and a
	 * {@link xxl.core.collections.queues.Heap heap} for merging the input
	 * iterations. The order is defined by the specified comparator in that way,
	 * that a new
	 * {@link xxl.core.comparators.FeatureComparator feature-comparator} is used
	 * calling the <tt>compare</tt> method of the specified comparator in order
	 * to compare two elements delivered by the input iterations. So the heap
	 * manages the iterations' elements, but the order is defined by the
	 * <tt>next</tt> element that would be returned by these iterations. Every
	 * iterator given to this constructor is wrapped to a cursor.
	 *
	 * @param iterators the input iterations to be merged.
	 * @param comparator the comparator used to compare two elements of the input
	 *        iteration.
	 */
	public Merger(Iterator[] iterators, Comparator comparator) {
		this(
			iterators,
			new Heap(
				new Object[iterators.length],
				0,
				new FeatureComparator(
					comparator,
					new Function() {
						public Object invoke(Object object) {
							return ((Cursor)object).peek();
						}
					}
				)
			)
		);
	}

	/**
	 * Creates a new merger backed on an input iteration array and a
	 * {@link xxl.core.collections.queues.Heap heap} for merging the input
	 * iterations. The order is defined by default
	 * {@link xxl.core.comparators.ComparableComparator comparator} which assumes
	 * that the input iterations' elements implement the
	 * {@link java.lang.Comparable comparable} interface. So the heap manages
	 * cursor objects, but the order is defined by the <tt>next</tt> element that
	 * would be returned by these iterations. Every iteration given to this
	 * constructor is wrapped to a cursor.
	 *
	 * @param iterators the input iterations to be merged.
	 */
	public Merger(Iterator[] iterators) {
		this(iterators, ComparableComparator.DEFAULT_INSTANCE);
	}

	/**
	 * Creates a new merger backed on two input iterations and the a user
	 * specified queue for merging them. Every iterator given to this constructor
	 * is wrapped to a cursor.
	 *
	 * @param iterator0 the first input iteration.
	 * @param iterator1 the seconde input iteration.
	 * @param queue the queue defining the strategy the input iterations are
	 *        accessed.
	 */
	public Merger(Iterator iterator0, Iterator iterator1, Queue queue) {
		this(new Iterator[] {iterator0, iterator1}, queue);
	}

	/**
	 * Creates a new merger backed on two input iterations and a
	 * {@link xxl.core.collections.queues.Heap heap} for merging the input
	 * iterations. The order is defined by the specified comparator in that way,
	 * that a new
	 * {@link xxl.core.comparators.FeatureComparator feature-comparator} is used
	 * calling the <tt>compare</tt> method of the specified comparator in order
	 * to compare two elements delivered by the input iterations. So the heap
	 * manages the input iterations' elements, but the order is defined by the
	 * <tt>next</tt> element that would be returned by these iterations. Every
	 * iterator given to this constructor is wrapped to a cursor.
	 *
	 * @param iterator0 the first input iteration.
	 * @param iterator1 the second input iteration.
	 * @param comparator the comparator used to compare two elements of the input
	 *        iterations.
	 */
	public Merger(Iterator iterator0, Iterator iterator1, Comparator comparator) {
		this(new Iterator[] {iterator0, iterator1}, comparator);
	}

	/**
	 * Creates a new merger backed on two input iterations and a
	 * {@link xxl.core.collections.queues.Heap heap} for merging the input
	 * iterations. The order is defined by default
	 * {@link xxl.core.comparators.ComparableComparator comparator} which assumes
	 * that the input iterations' elements implement the
	 * {@link java.lang.Comparable comparable} interface. So the heap manages
	 * cursor objects, but the order is defined by the <tt>next</tt> element that
	 * would be returned by these iterations. Every iteration given to this
	 * constructor is wrapped to a cursor.
	 *
	 *
	 * @param iterator0 the first input iteration.
	 * @param iterator1 the second input iteration.
	 */
	public Merger(Iterator iterator0, Iterator iterator1) {
		this(iterator0, iterator1, ComparableComparator.DEFAULT_INSTANCE);
	}

	/**
	 * Opens the merger, i.e., signals the cursor to reserve resources, open
	 * input iterations, etc. Before a cursor has been opened calls to methods
	 * like <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield proper
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
		for (int i = 0; i < cursors.length; i++)
			cursors[i].open();
		queue.open();
		for (int i = 0; i < cursors.length; i++)
			if (cursors[i].hasNext())
				queue.enqueue(cursors[i]);
	}

	/**
	 * Closes the merger, i.e., signals the cursor to clean up resources, close
	 * input iterations, etc. When a cursor has been closed calls to methods like
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
		for (int i = 0; i < cursors.length; i++)
			cursors[i].close();
		queue.close();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the merger has more elements.
	 */
	protected boolean hasNextObject() {
		return queue.size() > 0;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the merger's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * <p>A next element is available if the queue, which contains the input
	 * iterations, is not empty. The queue realizes a strategy, so delivers the
	 * the input iterations in a specific order. Therefore the next element is
	 * determined by accessing the next element of the queue's <tt>peek</tt>
	 * element, the cursor <tt>minCursor</tt>. If this cursor returned by the
	 * queue contains further elements, <tt>queue.replace(minCursor)</tt> is
	 * performed, otherwise the next cursor in the queue, returned by the
	 * queue's <tt>next</tt> method, will be consumed.</p>
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		minCursor = (Cursor)queue.dequeue();
		Object minimum = minCursor.next();
		if (minCursor.hasNext())
			queue.enqueue(minCursor);
		return minimum;
	}

	/**
	 * Removes from the underlying data structure the last element returned by
	 * the merger (optional operation). This method can be called only once per
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
	 *         not supported by the merger.
	 */
	public void remove() throws IllegalStateException, UnsupportedOperationException {
		super.remove();
		if (minCursor == null)
			throw new IllegalStateException();
		minCursor.remove();
		minCursor = null;
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the merger. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the merger, otherwise <tt>false</tt>.
	 */
	public boolean supportsRemove() {
		return minCursor != null ?
			minCursor.supportsRemove() :
			false;
	}

	/**
	 * Replaces the last element returned by the merger in the underlying data
	 * structure (optional operation). This method can be called only once per
	 * call to <tt>next</tt> or <tt>peek</tt> and updates the element returned
	 * by this method. Note, that between a call to <tt>next</tt> and
	 * <tt>update</tt> the invocation of <tt>peek</tt> or <tt>hasNext</tt> is
	 * forbidden. The behaviour of a merger is unspecified if the underlying
	 * data structure is modified while the iteration is in progress in any way
	 * other than by calling this method.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @param object the object that replaces the last element returned by the
	 *        merger.
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>update</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>update</tt> operation is
	 *         not supported by the merger.
	 */
	public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
		super.update(object);
		if (minCursor == null)
			throw new IllegalStateException();
		minCursor.update(object);
		minCursor = null;
	}

	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the merger. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the merger, otherwise <tt>false</tt>.
	 */
	public boolean supportsUpdate() {
		return minCursor != null ?
			minCursor.supportsUpdate() :
			false;
	}

	/**
	 * Resets the merger to its initial state such that the caller is able to
	 * traverse the underlying data structure again without constructing a new
	 * merger (optional operation). The modifications, removes and updates
	 * concerning the underlying data structure, are still persistent.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the merger.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		for (int i = 0; i < cursors.length; i++)
			cursors[i].reset();
		queue.clear();
		for (int i = 0; i < cursors.length; i++)
			if (cursors[i].hasNext())
				queue.enqueue(cursors[i]);
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the merger. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the merger, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		for (int i = 0; i < cursors.length; i++)
			if (!cursors[i].supportsReset())
				return false;
		return true;
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main(String[] args) {

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		
		xxl.core.cursors.groupers.HashGrouper hashGrouper = new xxl.core.cursors.groupers.HashGrouper(
			new xxl.core.cursors.sources.Enumerator(21),
			new Function() {
				public Object invoke(Object next) {
					return new Integer(((Integer)next).intValue() % 5);
				}
			}
		);
		
		hashGrouper.open();
		
		Cursor[] cursors = new Cursor[5];
		for (int i = 0; hashGrouper.hasNext(); i++)
			cursors[i] = (Cursor)hashGrouper.next();

		Merger merger = new Merger(
			cursors,
			new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((Integer)o1).compareTo((Integer)o2);
				}
			}
		);

		merger.open();
		
		while (merger.hasNext())
			System.out.print(merger.next() + "; ");
		System.out.flush();
		
		merger.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		System.out.println();
		
		Merger merger2 = new Merger(
			new xxl.core.cursors.sources.Enumerator(11),
			new xxl.core.cursors.sources.Enumerator(11, 21),
			(xxl.core.collections.queues.LIFOQueue)xxl.core.collections.queues.LIFOQueue.FACTORY_METHOD.invoke()
		);
		
		merger2.open();
		
		while (merger2.hasNext())
			System.out.print(merger2.next() + "; ");
		System.out.flush();
		
		merger2.close();
	}
}
