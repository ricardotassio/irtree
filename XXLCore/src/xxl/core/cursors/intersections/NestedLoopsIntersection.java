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

import java.util.BitSet;
import java.util.Iterator;

import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.functions.Function;
import xxl.core.predicates.Equal;
import xxl.core.predicates.Predicate;

/**
 * A nested-loops implementation of the intersection operator. The nested-loops
 * intersection operator is based on a loop iteration and therefore it has no
 * special conditions with regard to the order of the elements contained in the
 * two input iterations. The input iteration <tt>input0</tt> is traversed in the
 * "outer" loop (only for one time) and the input iteration <tt>input1</tt> is
 * repeatedly consumed in the "inner" loop (for a maximum of times determined by
 * the elements of input iteration <tt>input0</tt>). An user defined predicate is
 * used to decide whether two elements of the input iterations are equal
 * concerning their values.
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
 *     NestedLoopsIntersection intersection = new NestedLoopsIntersection(
 *         l1.listIterator(),
 *         l2.listIterator(),
 *         new Function() {
 *             public Object invoke() {
 *                 return l2.listIterator();
 *             }
 *         }
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
 *     intersection = new NestedLoopsIntersection(
 *         list1.iterator(),
 *         list2.iterator(),
 *         new Function() {
 *             public Object invoke() {
 *                 return list2.iterator();
 *             }
 *         }
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
 * @see xxl.core.cursors.AbstractCursor
 * @see xxl.core.cursors.intersections.SortBasedIntersection
 * @see xxl.core.relational.cursors.NestedLoopsIntersection
 */
public class NestedLoopsIntersection extends AbstractCursor {

	/**
	 * The first (or "outer") input iteration of the intersection operator.
	 */
	protected Cursor input0;
	
	/**
	 * The second (or "inner") input iteration of the intersection operator.
	 */
	protected Cursor input1;
	
	/**
	 * A parameterless that resets the "inner" loop (the iteration
	 * <tt>input1</tt>). Such a function must be specified, if the "inner"
	 * iteration is not resetable, i.e., the <tt>reset</tt> method of
	 * <tt>input1</tt> will cause an
	 * {@link java.lang.UnsupportedOperationException}. A call to the
	 * <tt>invoke</tt> method of this function must deliver the "inner" iteration
	 * again, if it has to be traversed for an other time.
	 */
	protected Function resetInput1;

	/**
	 * A binary predicate that selects the matching tuples, i.e., the tuples
	 * that will be returned when the <tt>next</tt> method is called. Therfore
	 * the predicate is evaluated for an element of each input iteration. Only
	 * the tuples, where the predicate's evaluation result is <tt>true</tt>,
	 * have been qualified to be a result of the intersection operation.
	 */
	protected Predicate equals;
	
	/**
	 * A boolean flag indicating whether the second input iteration should be
	 * reseted next time.
	 */
	protected boolean reset = false;
	
	/**
	 * A bit set storing for every element in the "inner" loop (the iteration
	 * <tt>input1</tt>) a single bit. The <tt>n</tt>-th bit of the bit set is set
	 * to <tt>1</tt> if the <tt>n</tt>-th element of the "inner" loop has found
	 * a matching element in the "outer" loop.
	 */
	protected BitSet bitVector = null;
	
	/**
	 * The position of the "inner" loop, i.e., if <tt>position</tt> is set to
	 * <tt>n</tt>, the <tt>n</tt>-th element of the iteration <tt>input1</tt> is
	 * actually tested for a matching.
	 */
	protected int position = 0;
	
	/**
	 * Creates a new nested-loops intersection backed on two iterations using a
	 * user defined predicate to decide whether two tuples match. This
	 * constructor also supports the handling of a non-resetable input iteration
	 * <tt>input1</tt>, because a parameterless function can be defined that
	 * returns this input iteration again.
	 *
	 * @param input0 the first input iteration that is traversed in the "outer"
	 *        loop.
	 * @param input1 the second input iteration that is traversed in the "inner"
	 *        loop.
	 * @param resetInput1 a parameterless function that delivers the second input
	 *        iteration again, when it cannot be reseted, i.e., the
	 *        <tt>reset</tt> method of <tt>input1</tt> will cause a
	 *        {@link java.lang.UnsupportedOperationException}. If the second
	 *        input iteration supports the <tt>reset</tt> operation this argument
	 *        can be set to <tt>null</tt>.
	 * @param equals the binary predicate evaluated for each tuple of elements
	 *        backed on one element of each input iteration in order to select
	 *        them. Only these tuples where the predicate's evaluation result is
	 *        <tt>true</tt> have been qualified to be a result of the
	 *        intersection operation.
	 */
	public NestedLoopsIntersection(Iterator input0, Iterator input1, Function resetInput1, Predicate equals) {
		this.input0 = Cursors.wrap(input0);
		this.input1 = Cursors.wrap(input1);
		this.resetInput1 = resetInput1;
		this.equals = equals;
		
		int counter = 0;
		for (; input1.hasNext(); counter++)
			input1.next();
		bitVector = new BitSet(counter);
		resetInput1();
	}
	
	/**
	 * Creates a new nested-loops intersection backed on two iterations using a
	 * default {@link xxl.core.predicates.Equal equality} predicate to decide
	 * whether two tuples match. This constructor also supports the handling of a
	 * non-resetable input iteration <tt>input1</tt>, because a parameterless
	 * function can be defined that returns this input iteration again.
	 *
	 * @param input0 the first input iteration that is traversed in the "outer"
	 *        loop.
	 * @param input1 the second input iteration that is traversed in the "inner"
	 *        loop.
	 * @param resetInput1 a parameterless function that delivers the second input
	 *        iteration again, when it cannot be reseted, i.e., the
	 *        <tt>reset</tt> method of <tt>input1</tt> will cause a
	 *        {@link java.lang.UnsupportedOperationException}. If the second
	 *        input iteration supports the <tt>reset</tt> operation this argument
	 *        can be set to <tt>null</tt>.
	 */
	public NestedLoopsIntersection(Iterator input0, Iterator input1, Function resetInput1) {
		this(input0, input1, resetInput1, Equal.DEFAULT_INSTANCE);
	}
	
	/**
	 * Creates a new nested-loops intersection backed on two iterations using a
	 * user defined predicate to decide whether two tuples match. This
	 * constructor does not support the handling of a non-resetable input
	 * iteration <tt>input1</tt>, so the <tt>reset</tt> operation must be
	 * guaranteed.
	 *
	 * @param input0 the first input iteration that is traversed in the "outer"
	 *        loop.
	 * @param input1 the second input iteration that is traversed in the "inner"
	 *        loop.
	 * @param equals the binary predicate evaluated for each tuple of elements
	 *        backed on one element of each input iteration in order to select
	 *        them. Only these tuples where the predicate's evaluation result is
	 *        <tt>true</tt> have been qualified to be a result of the
	 *        intersection operation.
	 */
	public NestedLoopsIntersection(Iterator input0, Iterator input1, Predicate equals) {
		this(input0, input1, null, equals);
	}
	
	/**
	 * Creates a new nested-loops intersection backed on two iterations using a
	 * default {@link xxl.core.predicates.Equal equality} predicate to decide
	 * whether two tuples match. This constructor does not support the handling
	 * of a non-resetable input iteration <tt>input1</tt>, so the <tt>reset</tt>
	 * operation must be guaranteed.
	 *
	 * @param input0 the first input iteration that is traversed in the "outer"
	 *        loop.
	 * @param input1 the second input iteration that is traversed in the "inner"
	 *        loop.
	 */
	public NestedLoopsIntersection(Iterator input0, Iterator input1) {
		this(input0, input1, null, Equal.DEFAULT_INSTANCE);
	}
	
	/**
	 * Resets the "inner" loop (the iteration <tt>input1</tt>) of the
	 * nested-loops intersection operator. If the function <tt>resetInput1</tt>
	 * is specified, it is invoked, else the <tt>reset</tt> method of
	 * <tt>input1</tt> is called.
	 */
	private void resetInput1() {
		if (resetInput1 != null)
			input1 = Cursors.wrap((Iterator)resetInput1.invoke());
		else
			input1.reset();
		position = 0;
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
		input0.open();
		input1.open();
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
		input0.close();
		input1.close();
	}
	
	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * <p>The implementation of this method is as follows:
	 * <pre>
	 *     Object next0;
	 *     while (input0.hasNext()) {
	 *         next0 = input0.next();
	 *         if (reset)
	 *             resetInput1();
	 *         while (input1.hasNext()) {
	 *             if (equals.invoke(next0, input1.next()) && !bitVector.get(position)) {
	 *                 next = next0;
	 *                 return true;
	 *             }
	 *             position++;
	 *         }
	 *         reset = true;
	 *     }
	 *     return false;
	 * </pre>
	 * The complete second input iteration is checked in the inner loop against
	 * each object of the first input iteration for matches.</p>
	 * 
	 * @return <tt>true</tt> if the intersection operator has more elements.
	 */
	protected boolean hasNextObject() {
		Object next0;
		while (input0.hasNext()) {
			next0 = input0.next();
			if (reset)
				resetInput1();
			while (input1.hasNext()) {
				if (equals.invoke(next0, input1.next()) && !bitVector.get(position)) {
					next = next0;
					return true;
				}
				position++;
			}
			reset = true;
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
		bitVector.set(position++);
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
		input0.reset();
		resetInput1();
		reset = false;
		bitVector.clear();
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the intersection operator. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the intersection operator, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return input0.supportsReset();
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
		
		NestedLoopsIntersection intersection = new NestedLoopsIntersection(
			l1.listIterator(),
			l2.listIterator(),
			new Function() {
				public Object invoke() {
					return l2.listIterator();
				}
			}
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
		
		System.out.println("\nExample 2:");
		
		intersection = new NestedLoopsIntersection(
			list1.iterator(),
			list2.iterator(),
			new Function() {
				public Object invoke() {
					return list2.iterator();
				}
			}
		);
		
		intersection.open();
		
		Cursors.println(intersection);
		
		intersection.close();
	}
}
