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

import java.util.Comparator;
import java.util.Iterator;

import xxl.core.collections.queues.Heap;
import xxl.core.comparators.ComparableComparator;
import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;

/**
 * The replacement-selection operator takes an iteration as its input and creates
 * sorted runs as its output. This technique is described in "Donald Knuth.:
 * <i>Sorting and Searching</i>. Addison-Wesley 1970." The replacement-selection
 * algorithm is especially useful for external sorting. Recall, that an external
 * merge-sort is performed by first producing <tt>n</tt> sorted input-runs. These
 * runs are then recursively merged to a single output-run. The runs produced by
 * replacement-selection operator tend to be twice as big as the available memory
 * or even bigger.
 * 
 * <p><b>Implementation details:</b> When initializing the replacement-selection
 * operator the input iteration's elements are inserted in a array with length
 * <tt>size</tt> until this array is filled up or the input iteration has no more
 * elements. With the help of this array and the given comparator a new
 * {@link xxl.core.collections.queues.Heap heap} is created with the intention to
 * order the elements. If a default
 * {@link xxl.core.comparators.ComparableComparator comparator} which assumes
 * that the elements implement the {@link java.lang.Comparable comparable}
 * interface is used, the elements will be returned in a natural ascending order,
 * because they will be organized in a min-heap. But if an
 * {@link xxl.core.comparators.InverseComparator inverse} comparator is used
 * instead, they will be organized in a max-heap and a descending order will
 * result. The method <tt>check</tt> verifies whether there are more elements to
 * process. If so and the heap is empty, it creates a new heap using the elements
 * that are reside in memory. The integer field <tt>n</tt> displays the current
 * position in the array. The array is used for the creation of a new heap. It is
 * initialized with <tt>size</tt>, namely the number of elements that can be kept
 * in memory. This array is builds up a new heap during the run-creation.
 * Consider the example a comparable comparator defines the order in the heap, so
 * a min-heap manages the inserted elements of the input cursor. If such an
 * element is lower than the <tt>peek</tt> element of the heap a new run has to
 * start, therefore this element is written to the array. If the current heap has
 * been emptied, a new heap is instantly created by the method <tt>check</tt>
 * called in the method <tt>next</tt> of the replacement-selection operator. So a
 * new heap is built up during the other heap is consumed. The next element to be
 * returned is computed as follows:<br /> A next element only exists if
 * <tt>n &lt; array.length</tt> or the heap contains further elements. If this is
 * the case the method <tt>check</tt> is performed. If the input cursor contains
 * more elements and the <tt>peek</tt> method of the heap returns an element that
 * is lower than or equal to the next elemet of the input iteration concerning
 * the used comparator, the next element of the heap is returned after the next
 * element of the input iteration has been inserted into the heap. If the
 * comparator returned a value greater than 0, the next element of the heap is
 * returned and the next element of the input iteration is inserted in the array
 * at position <tt>n</tt>. The array builds up a new heap for the next run. After
 * that <tt>n</tt> is decremented. If the input iteration does not contain
 * further elements the next element of the heap is returned.</p>
 * 
 * <p><b>Note:</b> If the input iteration is given by an object of the class
 * {@link java.util.Iterator}, i.e., it does not support the <tt>peek</tt>
 * operation, it is internally wrapped to a cursor.</p>
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     ReplacementSelection cursor = new ReplacementSelection(
 *         new Enumerator(11),
 *         3
 *     );
 * 
 *     cursor.open();
 * 
 *     while (cursor.hasNext())
 *         System.out.print(cursor.next() + "; ");
 *     System.out.flush();
 * 
 *     cursor.close();
 * </pre>
 * This instance of a replacement-selection operator sorts the enumerator's
 * elements with range [0,11[ by using a memory size of 3, i.e., the heap
 * consists of a maximum of three elements. In this case a default instance of a
 * {@link xxl.core.comparators.ComparableComparator comparator} which assumes
 * that the elements implement the {@link java.lang.Comparable comparable}
 * interface is used, so the elements are sorted in a natural order. If the whole
 * replacement-selection operator is consumed, only one single run containing all
 * of the underlying enumerator in ascending order is created. That is the fact
 * because using a comparable comparator causes that the heap is organized as a
 * min-heap and therefore the next element of the heap (minimum) is returned
 * every time. Due to the <tt>peek</tt> element of the input iteration is greater
 * than the <tt>peek</tt> element of the heap. The next element of the input
 * iteration is inserted in the heap and then the heap is reorganized. Because
 * the enumerator's elements are deliverd in an ascending order a heap has to be
 * build up only for one time. The generated output looks as follows:
 * <pre>
 *    0; 1; 2; 3; 4; 5; 6; 7; 8; 9; 10;
 * </pre></p>
 *
 * <p><b>Example usage (2):</b>
 * <pre>
 *     cursor = new ReplacementSelection(
 *         new Permutator(20),
 *         3,
 *         ComparableComparator.DEFAULT_INSTANCE
 *     );
 * 
 *     cursor.open();
 * 
 *     int last = 0;
 *     boolean first = true;
 *     while (cursor.hasNext())
 *         if (last &gt; ((Integer)cursor.peek()).intValue() || first) {
 *             System.out.println();
 *             System.out.print(" Run: ");
 *             last = ((Integer)cursor.next()).intValue();
 *             System.out.print(last +"; ");
 *             first = false;
 *         }
 *         else {
 *             last = ((Integer)cursor.next()).intValue();
 *             System.out.print(last + "; ");
 *         }
 *     System.out.flush();
 *     
 *     cursor.close();
 * </pre>
 * This instance of a replacement-selection uses a
 * {@link xxl.core.cursors.sources.Permutator permutator} with range [0,20[ and
 * memory size of 3, that is also the heap size. In this case a comparable
 * comparator is specified to compare two elements, i.e., a natural order will
 * result and the used heap is a min-heap, too. This example shows that more than
 * only one run can be created. A new run starts if the <tt>peek</tt> element of
 * the input iteration (permutator) is lower than the <tt>peek</tt> element of
 * the heap, i.e., the minimal element. The output demonstrates the different
 * created runs each having an ascending order concerning the permutator's
 * elements.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 * @see xxl.core.cursors.sorters.MergeSorter
 */
public class ReplacementSelection extends AbstractCursor {

	/**
	 * The input iteration the runs should be created of.
	 */
	protected Cursor input;

	/**
	 * The array used to create a heap. The length of the array is specified by
	 * the parameter <tt>size</tt>.
	 */
	protected Object[] array;

	/**
	 * The number of elements that can be kept in memory.
	 */
	protected int size;

	/**
	 * Current position in the array.
	 */
	protected int n;

	/**
	 * The comparator used to compare two elements of the input iteration.
	 */
	protected Comparator comparator;

	/** The heap used for the replacement-selection algorithm, ordering the
	 * elements.
	 */
	protected Heap heap;

	/**
	 * Creates a new instance of the replacement-selection operator.
	 *
	 * @param iterator the input iteration the sorted runs should be created of.
	 * @param size the number of elements that can be kept in memory.
	 * @param comparator the comparator used to compare two elements.
	 */
	public ReplacementSelection(Iterator iterator, int size, Comparator comparator) {
		this.input = Cursors.wrap(iterator);
		this.comparator = comparator;
		this.size = size;
	}

	/**
	 * Creates a new instance of the replacement-selection operator using a
	 * default {@link xxl.core.comparators.ComparableComparator comparator} which
	 * assumes that the elements implement the
	 * {@link java.lang.Comparable comparable} interface.
	 *
	 * @param iterator the input iteration the sorted runs should be created of.
	 * @param size the number of elements that can be kept in memory.
	 */
	public ReplacementSelection(Iterator iterator, int size) {
		this(iterator, size, ComparableComparator.DEFAULT_INSTANCE);
	}

	/**
	 * Opens the replacement-selection operator, i.e., signals the cursor to
	 * reserve resources, open the input iteration and initializing the
	 * internally used heap. Before a cursor has been opened calls to methods
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
		if (!isOpened)
			init();
		super.open();
	}
	
	/**
	 * Initializes the replacement-selection operator. The implementation of this
	 * method is as follows:
	 * <pre>
	 *     array = new Object[n = size];
	 *     input.open();
	 *     while (input.hasNext() && n &gt; 0)
	 *         array[--n] = input.next();
	 *     (heap = new Heap(array, 0, comparator)).open();
	 * </pre>
	 * The input iteration's elements are inserted in an array with length
	 * <tt>size</tt> until this array is filled up or the input iteration has no
	 * more elements. With the help of this array and the given comparator a new
	 * heap is created with the intention to order the elements.
	 */
	protected void init() {
		array = new Object[n = size];
		input.open();
		while (input.hasNext() && n > 0)
			array[--n] = input.next();
		(heap = new Heap(array, 0, comparator)).open();
	}

	/**
	 * Closes the replacement-selection operator, i.e., signals the cursor to
	 * clean up resources, close the input iteration and the internally used
	 * heap. When a cursor has been closed calls to methods like <tt>next</tt> or
	 * <tt>peek</tt> are not guaranteed to yield proper results. Multiple calls
	 * to <tt>close</tt> do not have any effect, i.e., if <tt>close</tt> was
	 * called the cursor remains in the state <i>closed</i>.
	 * 
	 * <p>Note, that a closed cursor usually cannot be opened again because of
	 * the fact that its state generally cannot be restored when resources are
	 * released respectively files are closed.</p>
	 */
	public void close() {
		super.close();
		input.close();
		heap.close();
	}

	/**
	 * Checks whether there are more elements to process. If so and the heap is
	 * empty, it creates a new heap using the elements that are reside in memory
	 * and sets <tt>n</tt> to <tt>array.length</tt>.
	 */
	protected void check() {
		if (heap.isEmpty()) {
			System.arraycopy(
				array,
				Math.max(n, array.length - n),
				array,
				0,
				Math.min(n, array.length - n)
			);
			(heap = new Heap(array, array.length - n, comparator)).open();
			n = array.length;
		}
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the cursor has more elements.
	 */
	protected boolean hasNextObject() {
		return n < array.length || !heap.isEmpty();
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * <p>Such an element exists if <tt>n &lt; array.length</tt> or the heap
	 * contains further elements. If this is the case the method <tt>check</tt>
	 * is performed. If the input iteration contains more elements and the next
	 * element of the  heap is lower than or equal to the next element of the
	 * input iteration concerning the used comparator, the next element of the
	 * heap is returned after the next element of the input iteration has been
	 * inserted into the heap. If the comparator returned a value greater than 0,
	 * the next element of the heap is returned and the next element of the input
	 * iteration is inserted in the array at position <tt>n</tt>. The array
	 * builds up a new heap for the next run. After that <tt>n</tt> is
	 * decremented. So a new heap is built up during the other heap is consumed.
	 * If the input cursor does not contain further elements the next element of
	 * the heap is returned.
	 *
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		check();
		if (input.hasNext())
			if (comparator.compare(heap.peek(), input.peek()) <= 0)
				return heap.replace(input.next());
			else {
				Object result = heap.dequeue();
				array[--n] = input.next();
				return result;
			}
		else
			return heap.dequeue();
	}

	/**
	 * Resets the replacement-selection to its initial state
	 * (optional operation). So the caller is able to traverse the underlying
	 * iteration again.
	 * 
	 * @throws UnsupportedOperationException if the <tt>reset</tt> method is not
	 *         supported by the replacement-selection operator.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		input.reset();
		heap.close();
		init();
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
		
		ReplacementSelection cursor = new ReplacementSelection(
			new xxl.core.cursors.sources.Enumerator(11),
			3
		);
		
		cursor.open();
		
		while (cursor.hasNext())
			System.out.print(cursor.next() + "; ");
		System.out.flush();
		System.out.println();
		
		cursor.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		cursor = new ReplacementSelection(
			new xxl.core.cursors.sources.Permutator(20),
			3,
			ComparableComparator.DEFAULT_INSTANCE
		);
		
		cursor.open();
		
		int last = 0;
		boolean first = true;
		while (cursor.hasNext())
			if (last > ((Integer)cursor.peek()).intValue() || first) {
				System.out.println();
				System.out.print(" Run: ");
				last = ((Integer)cursor.next()).intValue();
				System.out.print(last + "; ");
				first = false;
			}
			else {
				last = ((Integer)cursor.next()).intValue();
				System.out.print(last + "; ");
			}
		System.out.flush();
		
		cursor.close();
	}
}
