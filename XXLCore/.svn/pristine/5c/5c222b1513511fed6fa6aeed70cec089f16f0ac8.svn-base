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

package xxl.core.cursors.joins;

import java.util.Iterator;

import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.functions.Function;
import xxl.core.predicates.Predicate;
import xxl.core.util.BitSet;

/**
 * A nested-loops implementation of the join operator. This class provides a
 * generic, untyped nested-loops join algorithm. Furthermore it provides support
 * for left, right and outer joins. A nested-loops join is based on a loop
 * iteration and therefore it has no special conditions with regard to the order
 * of the elements contained in the two input iterations. The input iteration
 * <tt>input0</tt> is traversed in the "outer" loop (only for one time) and the
 * input iteration <tt>input1</tt> is repeatedly consumed in the "inner" loop
 * (for a maximum of times determined by the elements of input iteration
 * <tt>input0</tt>).
 * 
 * <p><i>The algorithm works as follows:</i> For each element of the "outer"
 * iteration all elements of the "inner" iteration are checked to fulfill the
 * user defined predicate evaluated for each tuple of elements backed on the
 * element of "outer" iteration and the current element of the "inner" iteration.
 * Only these tuples where the predicate's evaluation result is <tt>true</tt>
 * have been qualified to be a result of the join operation. Because internally
 * an {@link xxl.core.cursors.AbstractCursor abstract cursor} is used to
 * implement this join, the next element that will be returned to the caller is
 * stored in the field <tt>next</tt>. But be aware, that the function
 * <tt>newResult</tt> is invoked on the qualifying tuple before it is stored in
 * the field.<br />
 * An incomplete extract of the implemenation:
 * <pre>
 *     while (input0.hasNext()) {
 *         ...
 *         while (input1.hasNext()) {
 *             ...
 *             if (predicate.invoke(input0.peek(), input1.peek())) {
 *                 ...
 *                 next = newResult.invoke(input0.peek(), input1.next());
 *                 return true;
 *             }
 *             ...
 *             input1.next();
 *             ...
 *         }
 *         ...
 *         input0.next();
 *         resetInput1();
 *         ...
 *     }
 * </pre>The implementation is a bit more complex due to addional checks of
 * join-types and the generation of result-tuples where the evaluated join
 * predicate returned <tt>false</tt>.</p>
 * 
 * <p>Some information about the parameters used in the constructors:
 * <ul>
 *     <li>
 *         A binary predicate can be specified in some constructors with the
 *         intention to select the qualifying tuples, i.e., the tuples that will
 *         be returned when the <tt>next</tt> method is called. Therfore the
 *         predicate is evaluated for a tuple built of one element of each input
 *         iteration. Only the tuples, where the predicate's evaluation result is
 *         <tt>true</tt>, have been qualified to be a result of the join
 *         operation.<br />
 *         If the <i>Cartesian</i> product should be computed with the help of
 *         this join, the predicate always returns <tt>true</tt>.
 *     </li>
 *     <li>
 *         If the "inner" iteration is not resetable, i.e., the <tt>reset</tt>
 *         method of <tt>input1</tt> will cause an
 *         {@link java.lang.UnsupportedOperationException}, a parameterless
 *         function, <tt>resetInput1</tt>, can be specified in special
 *         constructors to deliver this iteration again, if it has to be
 *         traversed for an other time.
 *     </li>
 *     <li>
 *         Another function, named <tt>newResult</tt>, that is invoked on each
 *         qualifying tuple before it is returned to the caller concerning a call
 *         to the <tt>next</tt> method, has to be specified in each constructor.
 *         This binary function works like a kind of factory method modelling the
 *         resulting object (tuple). Be aware that this function possibly has to
 *         handle <tt>null</tt> values in cases of outer joins.
 *     </li>
 * </ul></p>
 * 
 * <p><b>Note:</b> When the given input iteration only implements the interface
 * {@link java.util.Iterator} it is wrapped to a cursor by a call to the static
 * method {@link xxl.core.cursors.Cursors#wrap(Iterator) wrap}.</p>
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     LinkedList l1 = new LinkedList();
 *     final LinkedList l2 = new LinkedList();
 *     for (int i = 1; i &le; 10; i++) {
 *         if (i%2 == 0)
 *             l2.add(new Integer(i));
 *         l1.add(new Integer(i));
 *     }
 * 
 *     NestedLoopsJoin join = new NestedLoopsJoin(
 *         l1.listIterator(),
 *         l2.listIterator(),
 *         new Function() {
 *             public Object invoke() {
 *                 return l2.listIterator();
 *             }
 *         },
 *         new Predicate() {
 *             public boolean invoke(Object o1, Object o2) {
 *                 return ((Integer)o1).intValue() == ((Integer)o2).intValue();
 *             }
 *         },
 *         new Function() {
 *             public Object invoke(Object o1, Object o2) {
 *                 return new Object[] {o1, o2};
 *             }
 *         },
 *         OUTER_JOIN
 *     );
 * 
 *     join.open();
 * 
 *     while (join.hasNext()) {
 *         Object[] result = (Object[])join.next();
 *         System.out.println("Tuple: (" + result[0] + ", " + result[1] + ")");
 *     }
 * 
 *     join.close();
 * </pre>
 * This example illustrates the constructor call for a full outer join. At first,
 * two {@link java.util.LinkedList linked lists} are created delivering the two
 * input iterations. The first list contains all elements from 0 to 10 and the
 * second one only the even elements. The Function <tt>resetInput1</tt> which
 * resets the second input iteration is implemented by returning an iteration
 * over the second list. The predicate that determines the tuples qualifying for
 * join results is <tt>true</tt>, if the integer values of an element of the
 * first and an element of the second input iteration are equal. A new
 * result-tuple is build by inserting both qualifying elements in an object
 * array. At last the type is set to OUTER_JOIN, so all tuples for which the
 * predicate is <tt>true</tt> will be returned as well as all elements that not
 * refer to a join result will be returned by filling the missing element of the
 * result-tuple with a <tt>null</tt> value. The output of this example is:
 * <pre>
 *     Tuple: (1, null)
 *     Tuple: (2, 2)
 *     Tuple: (3, null)
 *     Tuple: (4, 4)
 *     Tuple: (5, null)
 *     Tuple: (6, 6)
 *     Tuple: (7, null)
 *     Tuple: (8, 8)
 *     Tuple: (9, null)
 *     Tuple: (10, 10)
 * </pre></p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 * @see xxl.core.cursors.joins.SortMergeJoin
 * @see xxl.core.relational.cursors.NestedLoopsJoin
 * @see xxl.core.spatial.cursors.NestedLoopsJoin
 */
public class NestedLoopsJoin extends AbstractCursor {

	/**
	 * A constant specifying a theta-join. Only the tuples for which the
	 * specified predicate is <tt>true</tt> will be returned.
	 */
	public static final int THETA_JOIN = 0;

	/**
	 * A constant specifying a left outer-join. The tuples for which the
	 * specified predicate is <tt>true</tt> as well as all elements of
	 * <tt>input0</tt> not qualifying concerning the predicate will be returned.
	 * The function <tt>newResult</tt> is called with an element of
	 * <tt>input0</tt> and the <tt>null</tt> value.
	 */
	public static final int LEFT_OUTER_JOIN = 1;

	/**
	 * A constant specifying a right outer-join. The tuples for which the
	 * specified predicate is <tt>true</tt> as well as all elements of
	 * <tt>input1</tt> not qualifying concerning the predicate will be returned.
	 * The function <tt>newResult</tt> is called with an element of
	 * <tt>input1</tt> and the <tt>null</tt> value.
	 */
	public static final int RIGHT_OUTER_JOIN = 2;

	/**
	 * A constant specifying a full outer-join. The tuples for which the
	 * specified predicate is <tt>true</tt> as well as all tuples additionally
	 * returned by the left and right outer-join will be returned.
	 */
	public static final int OUTER_JOIN = 3;

	/**
	 * The first (or "outer") input iteration of the join operator.
	 */
	protected Cursor input0;
	
	/**
	 * The second (or "inner") input iteration of the join operator.
	 */
	protected Cursor input1;
	
	/**
	 * A boolean flag determining whether an "inner" loop (the iteration
	 * <tt>input1</tt>) has more elements or if it is finished and must be
	 * reseted.
	 */
	protected boolean flag = false;
	
	/**
	 * A boolean flag determining whether the actual element of the "outer" loop
	 * (the iteration <tt>input0</tt>) has already found a matching element in
	 * the "inner" loop (the iteration <tt>input1</tt>). This information is
	 * necessary for providing (left) outer joins.
	 */
	protected boolean match = false;
	
	/**
	 * A bit set storing for every element in the "inner" loop (the iteration
	 * <tt>input1</tt>) a single bit. The <tt>n</tt>-th bit of the bit set is set
	 * to <tt>1</tt> if the <tt>n</tt>-th element of the "inner" loop has found
	 * a matching element in the "outer" loop. This information is necessary for
	 * providing (right) outer joins.
	 */
	protected BitSet bitVector = null;
	
	/**
	 * The position of the "inner" loop, i.e., if <tt>position</tt> is set to
	 * <tt>n</tt>, the <tt>n</tt>-th element of the iteration <tt>input1</tt> is
	 * actually tested for a matching.
	 */
	protected int position = 0;
	
	/**
	 * A parameterless that resets the "inner" loop (the iteration
	 * <tt>input1</tt>). Such a function must be specified, if the "inner"
	 * iteration is not resetable, i.e., the <tt>reset</tt> method of
	 * <tt>input1</tt> will cause an
	 * {@link java.lang.UnsupportedOperationException}. A call to the
	 * <tt>invoke</tt> method of this function must deliver the "inner" iteration
	 * again, if it has to be traversed for an other time.
	 */
	protected Function resetInput1 = null;
	
	/**
	 * A binary predicate that selects the qualifying tuples, i.e., the tuples
	 * that will be returned when the <tt>next</tt> method is called. Therfore
	 * the predicate is evaluated for a tuple built of one element of each input
	 * iteration. Only the tuples, where the predicate's evaluation result is
	 * <tt>true</tt>, have been qualified to be a result of the join
	 * operation.<br />
	 * If the <i>Cartesian</i> product should be computed with the help of this
	 * join, the predicate always returns <tt>true</tt>.
	 */
	protected Predicate predicate = null;
	
	/**
	 * A function that is invoked on each qualifying tuple before it is returned
	 * to the caller concerning a call to the <tt>next</tt> method. This binary
	 * function works like a kind of factory method modelling the resulting
	 * object (tuple). Be aware that this function possibly has to handle
	 * <tt>null</tt> values in cases of outer joins.
	 */
	protected Function newResult = null;
	
	/**
	 * The type of this nested-loops join operator. Determines whether it
	 * calculates a theta- or an  outer-join.
	 */
	protected int type;	

	/**
	 * Creates a new nested-loops join backed on two iterations using a user
	 * defined predicate to select the resulting tuples. This constructor also
	 * supports the handling of a non-resetable input iteration <tt>input1</tt>,
	 * because a parameterless function can be defined that returns this input
	 * iteration again. Furthermore a function named <tt>newResult</tt> can be
	 * specified that is invoked on each qualifying tuple before it is returned
	 * to the caller concerning a call to the <tt>next</tt> method. This function
	 * is a kind of factory method to model the resulting object.
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
	 * @param predicate the binary predicate evaluated for each tuple of elements
	 *        backed on one element of each input iteration in order to select
	 *        them. Only these tuples where the predicate's evaluation result is
	 *        <tt>true</tt> have been qualified to be a result of the join
	 *        operation.
	 * @param newResult a factory method (function) that takes two parameters as
	 *        argument and is invoked on each tuple where the predicate's
	 *        evaluation result is <tt>true</tt>, i.e., on each qualifying tuple
	 *        before it is returned to the caller concerning a call to the
	 *        <tt>next</tt> method.
	 * @param type the type of this join; use one of the public constants defined
	 *        in this class.
	 * @throws IllegalArgumentException if the specified type is not valid.
	 */
	public NestedLoopsJoin(Iterator input0, Iterator input1, Function resetInput1, Predicate predicate, Function newResult, int type) throws IllegalArgumentException {
		this.input0 = Cursors.wrap(input0);
		this.input1 = Cursors.wrap(input1);
		this.resetInput1 = resetInput1;
		this.predicate = predicate;
		this.newResult = newResult;
		this.type = type;	
		if (type != THETA_JOIN && type != LEFT_OUTER_JOIN && type != RIGHT_OUTER_JOIN && type != OUTER_JOIN)
			throw new IllegalArgumentException("invalid join type has been specified in constructor.");
	}

	/**
	 * Creates a new nested-loops join realizing a theta-join backed on two
	 * iterations using a user defined predicate to select the resulting tuples.
	 * This constructor also supports the handling of a non-resetable input
	 * iteration <tt>input1</tt>, because a parameterless function can be defined
	 * that returns this input iteration again. Furthermore a function named
	 * <tt>newResult</tt> can be specified that is invoked on each qualifying
	 * tuple before it is returned to the caller concerning a call to the
	 * <tt>next</tt> method. This function is a kind of factory method to model
	 * the resulting object.
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
	 * @param predicate the binary predicate evaluated for each tuple of elements
	 *        backed on one element of each input iteration in order to select
	 *        them. Only these tuples where the predicate's evaluation result is
	 *        <tt>true</tt> have been qualified to be a result of the join
	 *        operation.
	 * @param newResult a factory method (function) that takes two parameters as
	 *        argument and is invoked on each tuple where the predicate's
	 *        evaluation result is <tt>true</tt>, i.e., on each qualifying tuple
	 *        before it is returned to the caller concerning a call to the
	 *        <tt>next</tt> method.
	 */
	public NestedLoopsJoin(Iterator input0, Iterator input1, Function resetInput1, Predicate predicate, Function newResult) {
		this(input0, input1, resetInput1, predicate, newResult, THETA_JOIN);
	}

	/**
	 * Creates a new nested-loops join backed on two iterations using a user
	 * defined predicate to select the resulting tuples. This constructor does
	 * not support the handling of a non-resetable input iteration
	 * <tt>input1</tt>, so the <tt>reset</tt> operation must be guaranteed.
	 * Furthermore a function named <tt>newResult</tt> can be specified that is
	 * invoked on each qualifying tuple before it is returned to the caller
	 * concerning a call to the <tt>next</tt> method. This function is a kind of
	 * factory method to model the resulting object.
	 *
	 * @param input0 the first input iteration that is traversed in the "outer"
	 *        loop.
	 * @param input1 the second input iteration that is traversed in the "inner"
	 *        loop.
	 * @param predicate the binary predicate evaluated for each tuple of elements
	 *        backed on one element of each input iteration in order to select
	 *        them. Only these tuples where the predicate's evaluation result is
	 *        <tt>true</tt> have been qualified to be a result of the join
	 *        operation.
	 * @param newResult a factory method (function) that takes two parameters as
	 *        argument and is invoked on each tuple where the predicate's
	 *        evaluation result is <tt>true</tt>, i.e., on each qualifying tuple
	 *        before it is returned to the caller concerning a call to the
	 *        <tt>next</tt> method.
	 * @param type the type of this join; use one of the public constants defined
	 *        in this class.
	 * @throws IllegalArgumentException if the specified type is not valid.
	 */
	public NestedLoopsJoin(Iterator input0, Cursor input1, Predicate predicate, Function newResult, int type) throws IllegalArgumentException {
		this(input0, input1, null, predicate, newResult, type);
	}

	/**
	 * Creates a new nested-loops join realizing a self-join backed on one
	 * iteration using a user defined predicate to select the resulting tuples.
	 * The second input iteration for the join is retrieved by calling the
	 * function <tt>resetInput</tt>, which is also used to substitute the
	 * <tt>reset</tt> functionality. Furthermore a function named
	 * <tt>newResult</tt> can be specified that is invoked on each qualifying
	 * tuple before it is returned to the caller concerning a call to the
	 * <tt>next</tt> method. This function is a kind of factory method to model
	 * the resulting object.
	 *
	 * @param input the input iteration for the self join.
	 * @param resetInput a parameterless function that delivers the input
	 *        iteration again.
	 * @param predicate the binary predicate evaluated for each tuple of elements
	 *        backed on one element of each input iteration in order to select
	 *        them. Only these tuples where the predicate's evaluation result is
	 *        <tt>true</tt> have been qualified to be a result of the join
	 *        operation.
	 * @param newResult a factory method (function) that takes two parameters as
	 *        argument and is invoked on each tuple where the predicate's
	 *        evaluation result is <tt>true</tt>, i.e., on each qualifying tuple
	 *        before it is returned to the caller concerning a call to the
	 *        <tt>next</tt> method.
	 * @param type the type of this join; use one of the public constants defined
	 *        in this class.
	 * @throws IllegalArgumentException if the specified type is not valid.
	 */
	public NestedLoopsJoin(Iterator input, Function resetInput, Predicate predicate, Function newResult, int type) throws IllegalArgumentException {
		this(input, (Iterator)resetInput.invoke(), resetInput, predicate, newResult, type);
	}

	/**
	 * Creates a new nested-loops join realizing a theta-join backed on two
	 * iterations using a {@link xxl.core.predicates.Predicate#TRUE true}
	 * predicate which leads the computation of the <i>Cartesian</i> product.
	 * This constructor does not support the handling of a non-resetable input
	 * iteration <tt>input1</tt>, so the <tt>reset</tt> operation must be
	 * guaranteed. Furthermore a function named <tt>newResult</tt> can be
	 * specified that is invoked on each qualifying tuple before it is returned
	 * to the caller concerning a call to the <tt>next</tt> method. This function
	 * is a kind of factory method to model the resulting object.
	 *
	 * @param input0 the first input iteration that is traversed in the "outer"
	 *        loop.
	 * @param input1 the second input iteration that is traversed in the "inner"
	 *        loop.
	 * @param newResult a factory method (function) that takes two parameters as
	 *        argument and is invoked on each tuple where the predicate's
	 *        evaluation result is <tt>true</tt>, i.e., on each qualifying tuple
	 *        before it is returned to the caller concerning a call to the
	 *        <tt>next</tt> method.
	 */
	public NestedLoopsJoin(Iterator input0, Cursor input1, Function newResult) {
		this(input0, input1, Predicate.TRUE, newResult, THETA_JOIN);
	}

	/**
	 * Creates a new nested-loops join realizing a theta-self-join backed on one
	 * iteration using a {@link xxl.core.predicates.Predicate#TRUE true}
	 * predicate which leads the computation of the <i>Cartesian</i> product. The
	 * second input iteration for the join is retrieved by calling the function
	 * <tt>resetInput</tt>, which is also used to substitute the <tt>reset</tt>
	 * functionality. Furthermore a function named <tt>newResult</tt> can be
	 * specified that is invoked on each qualifying tuple before it is returned
	 * to the caller concerning a call to the <tt>next</tt> method. This function
	 * is a kind of factory method to model the resulting object.
	 *
	 * @param input the input iteration for the self join.
	 * @param resetInput a parameterless function that delivers the input
	 *        iteration again.
	 * @param newResult a factory method (function) that takes two parameters as
	 *        argument and is invoked on each tuple where the predicate's
	 *        evaluation result is <tt>true</tt>, i.e., on each qualifying tuple
	 *        before it is returned to the caller concerning a call to the
	 *        <tt>next</tt> method.
	 */
	public NestedLoopsJoin(Cursor input, Function resetInput, Function newResult) {
		this(input, resetInput, Predicate.TRUE, newResult, THETA_JOIN);
	}
	
	/**
	 * Resets the "inner" loop (the iteration <tt>input1</tt>) of the
	 * nested-loops join operator. If the function <tt>resetInput1</tt> is
	 * specified, it is invoked, else the <tt>reset</tt> method of
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
	 * Opens the join operator, i.e., signals the cursor to reserve resources,
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
		input0.open();
		input1.open();
	}
	
	/**
	 * Closes the join operator, i.e., signals the cursor to clean up resources,
	 * close the input iterations, etc. When a cursor has been closed calls to
	 * methods like <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield
	 * proper results. Multiple calls to <tt>close</tt> do not have any effect,
	 * i.e., if <tt>close</tt> was called the cursor remains in the state
	 * <i>closed</i>.
	 * 
	 * <p>Note, that a closed cursor usually cannot be opened again because of
	 * the fact that its state generally cannot be restored when resources are
	 * released respectively files are closed.</p>
	 */
	public void close () {
		super.close();
		input0.close();
		input1.close();
	}
	
	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * <p>An incomplete extract of the implemenation:
	 * <pre>
	 *     while (input0.hasNext()) {
	 *         ...
	 *         while (input1.hasNext()) {
	 *             ...
	 *             if (predicate.invoke(input0.peek(), input1.peek())) {
	 *                 ...
	 *                 next = newResult.invoke(input0.peek(), input1.next());
	 *                 return true;
	 *             }
	 *             ...
	 *             input1.next();
	 *             ...
	 *         }
	 *         ...
	 *         input0.next();
	 *         resetInput1();
	 *         ...
	 *     }
	 * </pre>
	 * The implementation is a bit more complex due to addional checks of
	 * join-types and the generation of result-tuples where the evaluated join
	 * predicate returned <tt>false</tt>.</p>
	 * 
	 * @return <tt>true</tt> if the join operator has more elements.
	 */
	protected boolean hasNextObject() {
		if ((type == RIGHT_OUTER_JOIN || type == OUTER_JOIN) && bitVector == null) {
			bitVector = new BitSet(Cursors.count(input1));
			resetInput1();
		}
		while (flag || input0.hasNext()) {
			flag = true;
			while (input1.hasNext()) {
				if (predicate.invoke(input0.peek(), input1.peek())) {
					match = true;
					if (type == RIGHT_OUTER_JOIN || type == OUTER_JOIN)
						bitVector.set(position);
					next = newResult.invoke(input0.peek(), input1.next());
					return true;
				}
				position++;
				input1.next();
			}
			if ((type == LEFT_OUTER_JOIN || type == OUTER_JOIN) && !match)
				next = newResult.invoke(input0.peek(), null);
			flag = false;
			input0.next();
			resetInput1();
			if ((type == LEFT_OUTER_JOIN || type == OUTER_JOIN) && !match)
				return true;
			match = false;
		}
		if (type == LEFT_OUTER_JOIN || type == RIGHT_OUTER_JOIN || type == OUTER_JOIN)
			while (input1.hasNext()) {
				if (!bitVector.get(position++)) {
					next = newResult.invoke(null, input1.next());
					return true;
				}
				input1.next();
			}
		return false;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the join operator's methods, e.g., <tt>update</tt>
	 * or <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 *
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return next;
	}
	
	/**
	 * Resets the join operator to its initial state such that the caller is able
	 * to traverse the join again without constructing a new join operator
	 * (optional operation).
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 * 
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the cursor.
	 */
	public void reset () throws UnsupportedOperationException {
		super.reset();
		input0.reset();
		resetInput1();
		if (type == LEFT_OUTER_JOIN || type == RIGHT_OUTER_JOIN || type == OUTER_JOIN)
			bitVector = null;
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the join operator. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the join operator, otherwise <tt>false</tt>.
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
		for (int i = 1; i <= 10; i++) {
			if (i%2 == 0)
				l2.add(new Integer(i));
			l1.add(new Integer(i));
		}

		NestedLoopsJoin join = new NestedLoopsJoin(
			l1.listIterator(),
			l2.listIterator(),
			new Function() {
				public Object invoke() {
					return l2.listIterator();
				}
			},
			new Predicate() {
				public boolean invoke(Object o1, Object o2) {
					return ((Integer)o1).intValue() == ((Integer)o2).intValue();
				}
			},
			new Function() {
				public Object invoke(Object o1, Object o2) {
					return new Object[] {o1, o2};
				}
			},
			OUTER_JOIN
		);

		join.open();
		
		while (join.hasNext()) {
			Object[] result = (Object[])join.next();
			System.out.println("Tuple: (" + result[0] + ", " + result[1] + ")");
		}
		
		join.close();
	}
}
