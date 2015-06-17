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

import java.util.Iterator;

import xxl.core.collections.bags.Bag;
import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.functions.Function;
import xxl.core.predicates.Equal;
import xxl.core.predicates.Predicate;
import xxl.core.util.BitSet;

/**
 * A nested-loops implementation of the difference operator
 * (<tt>input1 - input2</tt>). This operation can be performed in two different
 * ways, namely the first realization removes an element of <tt>input1</tt> if
 * the same element exists in <tt>input2</tt>. The second way of processing
 * removes all elements of <tt>input1</tt> that match with an element of
 * <tt>input2</tt>. This second approch implies that no duplicates will be
 * returned by the difference operator, whereas the first solution may contain
 * duplicates if the number of equal elements in cursor <tt>input1</tt> is
 * greater than that of <tt>input2</tt>.
 * 
 * <p>The difference operator implemented by this class supports realization
 * depending on a boolean flag <tt>all</tt> that signals if all elements of
 * cursor <tt>input1</tt> that fulfill the given predicate will be removed or
 * only one element will be removed. As mentioned above a predicate is used to
 * determine if an element of <tt>input2</tt> matches with an element of
 * <tt>input1</tt>. If no predicate has been specified, internally the
 * {@link xxl.core.predicates.Equal equality} predicate is used by default. The
 * function <tt>newBag</tt> generates an empty bag each time it is invoked. This
 * bag should reside in main memory and contains as much elements of
 * <tt>input1</tt> as possible. Each element of <tt>input2</tt> gets checked for
 * a match with an element contained in this bag. The size of the bag depends on
 * the specified arguments <tt>memSize</tt> and <tt>objectSize</tt>. The maximum
 * number of elements this bag is able to contain is computed by the
 * formula:<br />
 * <pre>
 *     maxTuples = memSize / objectSize - 1
 * </pre>
 * One element is subtracted due to the reason that a minimum of one element of
 * cursor <tt>input2</tt> has also to be located in main memory.</p>
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     NestedLoopsDifference difference = new NestedLoopsDifference(
 *         new Enumerator(21),
 *         new Filter(
 *             new Enumerator(21),
 *             new Predicate() {
 *                 public boolean invoke(Object next) {
 *                     return ((Integer)next).intValue() % 2 == 0;
 *                 }
 *             }
 *         ),
 *         32,
 *         8,
 *         Bag.FACTORY_METHOD,
 *         new Predicate() {
 *             public boolean invoke(Object previous, Object next) {
 *                 return ((Integer)previous).intValue() == ((Integer)next).intValue();
 *             }
 *         },
 *         true
 *     );
 * 
 *     difference.open();
 *
 *     while (difference.hasNext())
 *         System.out.println(difference.next());
 * 
 *     difference.close();
 * </pre>
 * This nested-loops difference substracts all even numbers contained in the
 * interval [0, 21) from all numbers of the same interval (input1). The available
 * mememory size is set to 32 bytes and an object has the size of 8 bytes. So a
 * maximum of 3 elements can be stored in the temporal main memory bag. The
 * FACTORY_METHOD of the class {@link xxl.core.collections.bags.Bag} delivers
 * a new empty bag, therefore a
 * {@link xxl.core.collections.bags.ListBag list-bag} will be used to store the
 * elements of cursor <tt>input1</tt>. The specified predicate returns
 * <tt>true</tt> if an element of <tt>input1</tt> and an element of
 * <tt>input2</tt> are equal in their integer values. In this example the flag
 * <tt>all</tt> can be specified arbitrary due to <tt>input1</tt> contains no
 * duplicates. But if the first input cursor would contain all elements from 0 to
 * 20 twice the result would be the same.</p>
 * 
 * <p><b>Example usage (2):</b>
 * <pre>
 *     java.util.LinkedList l1 = new java.util.LinkedList();
 * 
 *     l1.add(new Integer(1));
 *     l1.add(new Integer(2));
 *     l1.add(new Integer(2));
 *     l1.add(new Integer(3));
 * 
 *     final java.util.LinkedList l2 = new java.util.LinkedList();
 * 
 *     l2.add(new Integer(1));
 *     l2.add(new Integer(2));
 *     l2.add(new Integer(3));
 * 
 *     difference = new NestedLoopsDifference(
 *         l1.iterator(),
 *         l2.iterator(),
 *         32,
 *         8,
 *         false,
 *         new Function() {
 *             public Object invoke() {
 *                 return l2.iterator();
 *             }
 *         }
 *     );
 * 
 *     difference.open();
 * 
 *     while (difference.hasNext())
 *         System.out.println(difference.next());
 * 
 *     difference.close();
 * </pre>
 * This example computes the difference between to iterators based on the lists
 * {1, 2, 2, 3} and {1, 2, 3}. The memory usage is equal to that in example 1,
 * but in this case the flag <tt>all</tt> leads to different results.
 * <ul>
 *     <li>
 *         If <tt>all == true</tt> this operator delivers no results, because
 *         each element of <tt>input2</tt> is equal to an element of
 *         <tt>input1</tt>.
 *     </li>
 *     <li>
 *         But if <tt>all == false</tt> the element '2' is returned because it
 *         is only substracted once from <tt>input1</tt>.
 *     </li>
 * </ul>
 * The function specified above resets and returns the input cursor
 * <tt>input2</tt>, because it supports no <tt>reset</tt> operation, but has to
 * be traversed several times (inner loop). The predicate comparing two elements
 * concerning equality is set to
 * {@link xxl.core.predicates.Equal#DEFAULT_INSTANCE} by default.</p>
 *
 * <p><b>Note:</b> If an input iteration is given by an object of the class
 * {@link java.util.Iterator}, i.e., it does not support the <tt>peek</tt>
 * operation, it is internally wrapped to a cursor.</p>
 * 
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.functions.Function
 * @see xxl.core.predicates.Predicate
 * @see xxl.core.predicates.Equal
 * @see xxl.core.collections.bags.Bag
 * @see xxl.core.cursors.differences.SortBasedDifference
 * @see xxl.core.relational.cursors.NestedLoopsDifference
 */
public class NestedLoopsDifference extends AbstractCursor {

	/**
	 * The first (or left) input cursor of the difference operator.
	 */
	protected Cursor input1;
	
	/**
	 * The second (or right) input cursor of the difference operator.
	 */
	protected Cursor input2;
	
	/**
	 * The output cursor of the difference operator.
	 */
	protected Cursor results = null;

	/**
	 * A parameterless function returning a new bag on demand.
	 */
 	protected Function newBag;

	/**
	 * The maximum number of elements that can be stored in the bag returned by
	 * the function <tt>newBag</tt>.
	 */
	protected int maxTuples;


	/**
	 * The predicate used to determine a match between an element of
	 * <tt>input2</tt> and an element of <tt>input1</tt>.
	 */
	protected Predicate predicate;

	/**
	 * A flag signaling if all matches or only one matche returned by the
	 * predicate should be removed from the internal bag.
	 */
	protected boolean all;

	/**
	 * A parameterless function used to reset and return the second input cursor.
	 */
	protected Function resetInput2;

	/**
	 * A bit set storing which elements of cursor <tt>input2</tt> have been
	 * removed from cursor <tt>input1</tt> already.
	 */
	protected BitSet removedElements;

	/**
	 * Creates a new instance of the nested-loops difference operator. Every
	 * input iterator is wrapped to a cursor. Determines the maximum number of
	 * elements that can be stored in the bag used for the temporal storage of
	 * the elements of <tt>input1</tt> in main memory:
	 * <pre>
	 *     maxTuples = memSize / objectSize - 1
	 * </pre>
	 * This constructor should only be used if cursor <tt>input2</tt> is not
	 * resetable.
	 *
	 * @param input1 the first input iterator where the elements have to be
	 *        subtracted from.
	 * @param input2 the second input iterator containing the elements that have
	 *        to be subtracted.
	 * @param memSize the maximum amount of available main memory that can be
	 *        used for the bag.
	 * @param objectSize the size (bytes) needed to store one object of an input
	 *        cursor.
	 * @param newBag a parameterless function delivering an empty bag on demand.
	 *        This bag is used to store the elements of cursor <tt>input1</tt>.
	 * @param resetInput2 a parameterless function that delivers the second input
	 *        cursor again. This constructor should only be used if the second
	 *        input cursor does not support the <tt>reset</tt> functionality.
	 * @param predicate a binaray predicate that has to determine a match between
	 *        an element of <tt>input1</tt> and an element of <tt>input2</tt>.
	 * @param all a boolean flag signaling if all elements contained in the bag
	 *        that have a positiv match concerning the predicate will be removed
	 *        or only one element will be removed.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsDifference(Iterator input1, Iterator input2, int memSize, int objectSize, Function newBag, final Function resetInput2, Predicate predicate, boolean all) {
		this.input1 = Cursors.wrap(input1);
		this.input2 = Cursors.wrap(input2);
		this.newBag = newBag;
		this.resetInput2 = new Function() {
			public Object invoke() {
	 			return Cursors.wrap((Iterator)resetInput2.invoke());
	 		}
		};
		this.predicate = predicate;
		this.all = all;
		this.maxTuples = memSize / objectSize - 1;
		if (memSize < 2*objectSize)
			throw new IllegalArgumentException("Insufficient main memory available.");
		if (!all) {
			int counter = 0;
			for ( ; input2.hasNext(); counter++)
				input2.next();
			removedElements = new BitSet(counter);
			input2 = (Cursor)this.resetInput2.invoke();
		}
	}

	/**
	 * Creates a new instance of the nested-loops difference operator. Every
	 * input iterator is wrapped to a cursor. Determines the maximum number of
	 * elements that can be stored in the bag used for the temporal storage of
	 * the elements of <tt>input1</tt> in main memory:
	 * <pre>
	 *     maxTuples = memSize / objectSize - 1
	 * </pre>
	 * Uses the factory method for bags,
	 * {@link xxl.core.collections.bags.Bag#FACTORY_METHOD}. Determines the
	 * equality between an element of <tt>input1</tt> and <tt>input2</tt> with
	 * the help of the default instance of the
	 * {@link xxl.core.predicates.Equal equality} predicate. This constructor
	 * should only be used if cursor <tt>input2</tt> is not resetable.
	 *
	 * @param input1 the first input iterator where the elements have to be
	 *        subtracted from.
	 * @param input2 the second input iterator containing the elements that have
	 *        to be subtracted.
	 * @param memSize the maximum amount of available main memory that can be
	 *        used for the bag.
	 * @param objectSize the size (bytes) needed to store one object of an input
	 *        cursor.
	 * @param resetInput2 a parameterless function that delivers the second input
	 *        cursor again. This constructor should only be used if the second
	 *        input cursor does not support the <tt>reset</tt> functionality.
	 * @param all a boolean flag signaling if all elements contained in the bag
	 *        that have a positiv match concerning the predicate will be removed
	 *        or only one element will be removed.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsDifference(Iterator input1, Iterator input2, int memSize, int objectSize, Function resetInput2, boolean all) {
		this(input1, input2, memSize, objectSize, Bag.FACTORY_METHOD, resetInput2, Equal.DEFAULT_INSTANCE, all);
	}

	/**
	 * Creates a new instance of the nested-loops difference operator. The first
	 * input iterator is wrapped to a cursor. Determines the maximum number of
	 * elements that can be stored in the bag used for the temporal storage of
	 * the elements of <tt>input1</tt> in main memory:
	 * <pre>
	 *     maxTuples = memSize / objectSize - 1
	 * </pre>
	 * <tt>Input2</tt> has to support the <tt>reset</tt> operation, otherwise an
	 * {@link java.lang.UnsupportedOperationException} will be thrown!
	 *
	 * @param input1 the first input iterator where the elements have to be
	 *        subtracted from.
	 * @param input2 the second input cursor containing the elements that have to
	 *        be subtracted.
	 * @param memSize the maximum amount of available main memory that can be
	 *        used for the bag.
	 * @param objectSize the size (bytes) needed to store one object of an input
	 *        cursor.
	 * @param newBag a parameterless function delivering an empty bag on demand.
	 *        This bag is used to store the elements of cursor <tt>input1</tt>.
	 * @param predicate a binaray predicate that has to determine a match between
	 *        an element of <tt>input1</tt> and an element of <tt>input2</tt>.
	 * @param all a boolean flag signaling if all elements contained in the bag
	 *        that have a positiv match concerning the predicate will be removed
	 *        or only one element will be removed.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsDifference(Iterator input1, final Cursor input2, int memSize, int objectSize, Function newBag, Predicate predicate, boolean all) {
		this(
			input1,
			input2,
			memSize,
			objectSize,
			newBag,
			new Function() {
				public Object invoke() {
					input2.reset();
					return input2;
				}
			},
			predicate,
			all
		);
	}

	/**
	 * Creates a new instance of the nested-loops difference operator. The first
	 * input iterator is wrapped to a cursor. Determines the maximum number of
	 * elements that can be stored in the bag used for the temporal storage of
	 * the elements of <tt>input1</tt> in main memory:
	 * <pre>
	 *     maxTuples = memSize / objectSize - 1
	 * </pre>
	 * Uses the factory method for bags,
	 * {@link xxl.core.collections.bags.Bag#FACTORY_METHOD}. Determines the
	 * equality between an element of <tt>input1</tt> and <tt>input2</tt> with
	 * the help of the default instance of the
	 * {@link xxl.core.predicates.Equal equality} predicate. <tt>Input2</tt> has
	 * to support the <tt>reset</tt> operation, otherwise an
	 * {@link java.lang.UnsupportedOperationException} will be thrown!
	 *
	 * @param input1 the first input iterator where the elements have to be
	 *        subtracted from.
	 * @param input2 the second input cursor containing the elements that have to
	 *        be subtracted.
	 * @param memSize the maximum amount of available main memory that can be
	 *        used for the bag.
	 * @param objectSize the size (bytes) needed to store one object of an input
	 *        cursor.
	 * @param all a boolean flag signaling if all elements contained in the bag
	 *        that have a positiv match concerning the predicate will be removed
	 *        or only one element will be removed.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsDifference(Iterator input1, Cursor input2, int memSize, int objectSize, boolean all) {
		this(input1, input2, memSize, objectSize, Bag.FACTORY_METHOD, Equal.DEFAULT_INSTANCE, all);
	}

	/**
	 * Opens the nested-loops difference operator, i.e., signals the cursor to
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
	 * Closes the nested-loops difference operator, i.e., signals the cursor to
	 * clean up resources and close its input and output cursors. When a cursor
	 * has been closed calls to methods like <tt>next</tt> or <tt>peek</tt> are
	 * not guaranteed to yield proper results. Multiple calls to <tt>close</tt>
	 * do not have any effect, i.e., if <tt>close</tt> was called the cursor
	 * remains in the state <i>closed</i>.
	 * 
	 * <p>Note, that a closed cursor usually cannot be opened again because of
	 * the fact that its state generally cannot be restored when resources are
	 * released respectively files are closed.</p>
	 */
	public void close() {
		super.close();
		input1.close();
		input2.close();
		results.close();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other words,
	 * returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would return an
	 * element rather than throwing an exception.)
	 * 
	 * <p>Builds a temporal bag calling <tt>newBag.invoke()</tt> and stores as
	 * much elements of cursor <tt>input1</tt> in this bag as possible. After
	 * that each element of the second input cursor is taken and with the help
	 * of the bag's <tt>query</tt> method a cursor containing all elements that
	 * have to be removed from <tt>input1</tt> are determined. Depending on the
	 * flag <tt>all</tt> all elements contained in that cursor are removed from
	 * the bag or only one element is removed. At last the bag's <tt>cursor</tt>
	 * method is called and the result cursor's reference is set to this cursor.
	 * If the result cursor contains any elements, <tt>true</tt> is returned,
	 * otherwise <tt>false</tt>. If the cursor <tt>input1</tt> contains further
	 * elements the whole procedure is returned.
	 *
	 * @return <tt>true</tt> if the nested-loops difference operator has more
	 *         elements.
	 */
	protected boolean hasNextObject() {
		if (results == null || !results.hasNext()) {
			Bag tmpBag = (Bag)newBag.invoke();
			while (input1.hasNext()) {
				if (tmpBag.size() < maxTuples)
					tmpBag.insert(input1.next());
				Cursor tmpCursor;
				int position = 0;
				while (input2.hasNext()) {
					tmpCursor = tmpBag.query(
						new Predicate() {
							public boolean invoke(Object o) {
								return predicate.invoke(o, input2.peek());
							}
						}
					);
					while (tmpCursor.hasNext()) {
						tmpCursor.next();
						if (!all) {
							if (!removedElements.get(position)) {
								tmpCursor.remove();
								removedElements.set(position);
							}
							break;
						}
						else
							tmpCursor.remove();
					}
					if (input2.hasNext()) {
						input2.next();
						position++;
					}
				}
				input2 = (Cursor)resetInput2.invoke();
				if (tmpBag.size() == maxTuples)
					break;
			}
			results = tmpBag.cursor();
			return results.hasNext();
		}
		return true;
	}

	/**
	 * Returns the next element in the iteration. This element will be removed
	 * from the iteration, if <tt>next</tt> is called. This method returns the
	 * next element of the result cursor and removes it from the underlying bag.
	 *
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		Object result = results.next();
		results.remove();
		return result;
	}

	/**
	 * Removes from the underlying data structure the last element returned by
	 * the nested-loops difference operator (optional operation). This method can
	 * be called only once per call to <tt>next</tt> or <tt>peek</tt> and removes
	 * the element returned by this method. Note, that between a call to
	 * <tt>next</tt> and <tt>remove</tt> the invocation of <tt>peek</tt> or
	 * <tt>hasNext</tt> is forbidden. The behaviour of a cursor is unspecified
	 * if the underlying data structure is modified while the iteration is in
	 * progress in any way other than by calling this method. This method is
	 * only supported if the bag's size is limited to only one element, otherwise
	 * an {@link java.lang.UnsupportedOperationException} will be thrown.
	 *
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>remove</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is
	 *         not supported by the nested-loops difference operator, i.e., the
	 *         bag's size is greater than 1.
	 */
	public void remove() throws IllegalStateException, UnsupportedOperationException {
		super.remove();
		results.remove();
		input1.remove();
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the nested-loops difference operator. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsRemove() {
		return maxTuples == 1 && input1.supportsRemove();
	}

	/**
	 * Replaces the object that was returned by the last call to <tt>next</tt>
	 * or <tt>peek</tt> (optional operation). This operation must not be called
	 * after a call to <tt>hasNext</tt>. It should follow a call to <tt>next</tt>
	 * or <tt>peek</tt>. This method should be called only once per call to
	 * <tt>next</tt> or <tt>peek</tt>. The behaviour of a nested-loops difference
	 * operator is unspecified if the underlying data structure is modified while
	 * the iteration is in progress in any way other than by calling this method.
	 * This method is only supported if the bag's size is limited to only one
	 * element, otherwise an {@link java.lang.UnsupportedOperationException}
	 * will be thrown.
	 *
	 * @param object the object that replaces the object returned by the last
	 *        call to <tt>next</tt> or <tt>peek</tt>.
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>update</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>update</tt> operation is
	 *         not supported by the nested-loops difference operator, i.e., the
	 *         bag's size is greater than 1.
	 */
	public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
		super.update(object);
		results.update(object);
		input1.update(object);
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the nested-loops difference operator. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsUpdate() {
		return maxTuples == 1 && input1.supportsRemove();
	}

	/**
	 * Resets the nested-loops difference operator to its initial state (optional
	 * operation). So the caller is able to traverse the underlying data
	 * structure again. The modifications, removes and updates concerning the
	 * underlying data structure, are still persistent. This method resets the
	 * input cursors, closes the result cursor and sets it to <tt>null</tt>.
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the nested-loops difference operator.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		input1.reset();
		input2.reset();
		results.close();
		results = null;
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the nested-loops difference operator. Otherwise it returns <tt>false</tt>.
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
		
		NestedLoopsDifference difference = new NestedLoopsDifference(
			new xxl.core.cursors.sources.Enumerator(21),
			//EmptyCursor.DEFAULT_INSTANCE,
			new xxl.core.cursors.filters.Filter(
				new xxl.core.cursors.sources.Enumerator(21),
				new Predicate() {
					public boolean invoke(Object next) {
						return ((Integer)next).intValue() % 2 == 0;
					}
				}
			),
			32,
			8,
			Bag.FACTORY_METHOD,
			new Predicate() {
				public boolean invoke(Object previous, Object next) {
					return ((Integer)previous).intValue() == ((Integer)next).intValue();
				}
			},
			false
		);
		difference.open();
		
		while (difference.hasNext())
			System.out.println(difference.next());
		
		difference.close();

		System.out.println();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/

		java.util.LinkedList l1 = new java.util.LinkedList();

		l1.add(new Integer(1));
		l1.add(new Integer(2));
		l1.add(new Integer(2));
		l1.add(new Integer(3));

		final java.util.LinkedList l2 = new java.util.LinkedList();

		l2.add(new Integer(1));
		l2.add(new Integer(2));
		l2.add(new Integer(3));

		difference.open();
		
		difference = new NestedLoopsDifference(
			l1.iterator(),
			l2.iterator(),
			32,
			8,
			new Function() {
				public Object invoke() {
					return l2.iterator();
				}
			},
			false
		);
		
		while (difference.hasNext())
			System.out.println(difference.next());
		
		difference.close();
	}
}
