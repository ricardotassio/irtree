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

package xxl.core.cursors.sources;

import xxl.core.cursors.AbstractCursor;
import xxl.core.functions.Function;
import xxl.core.predicates.FunctionPredicate;
import xxl.core.predicates.Predicate;

/**
 * Given a tuple of start values (<tt>a<sub>0</sub></tt> to
 * <tt>a<sub>n</sub></tt>) the inductor incrementally computes the value
 * <tt>a<sub>(n+1+i)</sub></tt> (<tt>i</tt> &ge; 0) by calling
 * the function <tt>next</tt> with the parameters <tt>a<sub>i</sub></tt> to
 * <tt>a<sub>(n+i)</sub></tt>. Optionally, the user can specify the predicate
 * <tt>hasNext</tt> (called with parameters <tt>a<sub>i</sub></tt> to
 * <tt>a<sub>(n+i)</sub></tt>) which determines whether a next value exists. With
 * the help of an inductor a sequence of elements can be computed represented as
 * an iteration.
 * 
 * <p><b>Example usage (1): the <i>Fibonacci</i> sequence</b>
 * <pre>
 *     Inductor fibonacci = new Inductor(
 *         new Object[] {
 *             new Integer(1),
 *             new Integer(1)
 *         },
 *         new Function() {
 *             public Object invoke(Object fib_1, Object fib_2) {
 *                 return new Integer(((Integer)fib_1).intValue() + ((Integer)fib_2).intValue());
 *             }
 *         }
 *     );
 * 
 *     fibonacci.open();
 * 
 *     System.out.println(Cursors.nth(fibonacci, 7));
 * 
 *     fibonacci.close();
 * </pre>
 * This example computes the 7th number of the fibonacci sequence by initializing
 * an object array with two integer objects with value 1 (beginning of the
 * induction sequence; <tt>n</tt> = 1, 2). All following elements are computed
 * with the following function:
 * <pre>
 *     fib(n) = fib(n-1) + fib(n-2);
 * </pre>
 * <b>Implemenation details:</b> If the next element is available, i.e., the flag
 * <tt>nextAvailable</tt> is <tt>true</tt>, the function <tt>next</tt> is invoked
 * on the array <tt>objects</tt>. This array is shifted left for one position by
 * copying the values. Then the returned object is added to this array as the
 * last element with regard to the further computation of the inductor sequence.
 * The first element of the array <tt>objects</tt> is returned, because this is
 * the next element that has be returned in the inductor sequence. So this
 * inductor would return the next number of the fibonacci sequence as long as the
 * <tt>next</tt> method is called. In this case the static method <tt>nth()</tt>
 * is used to return only the 7th number of the sequence, but also an other
 * function <tt>hasNext</tt> can be specified in a constructor with the intention
 * to terminate such an inductor sequence.</p>
 * 
 * <p><b>Example usage (2): the factorial method</b>
 * <pre>
 *     Inductor factorial = new Inductor(
 *         new Object[] {
 *             new Integer(1)
 *         },
 *         new Function() {
 *             int factor = 1;
 *             
 *             public Object invoke(Object n) {
 *                 return new Integer(((Integer)n).intValue() * factor++);
 *             }
 *         }
 *     );
 * 
 *     factorial.open();
 * 
 *     System.out.println(Cursors.nth(factorial, 3));
 * 
 *     factorial.close();
 * </pre>
 * This example computes the 3rd number of the factorial method (3! = 6). The
 * start-array is initialized by one integer object with value 1
 * (<tt>n</tt> = 0). Further elements are iteratively computed by invoking the
 * above defined function:
 * <pre>
 *     fac(n) = n * fac(n-1);
 * </pre>
 * But the factor, i.e., the next integer to multiply the next element
 * <tt>fac(n-1)</tt> by, which is written back to the array, has to be stored (in
 * the anonymous class). For further examples see
 * {@link xxl.core.cursors.sources.Inductors}.</p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 * @see xxl.core.cursors.sources.Inductors
 * @see xxl.core.predicates.Predicate
 * @see xxl.core.functions.Function
 */
public class Inductor extends AbstractCursor {

	/**
	 * The predicate that is used to determine an end of the inductor sequence.
	 */
	protected Predicate hasNext;

	/**
	 * The function that is used to compute the next element of the induction
	 * sequence.
	 */
	protected Function next;

	/**
	 * An array storing the parameters <tt>a<sub>i</sub></tt> to
	 * <tt>a<sub>(n+i)</sub></tt>] required for the computation of the value
	 * <tt>a<sub>(n+1+i)</sub></tt>.
	 */
	protected Object[] objects;

	/**
	 * The number of values of the induction sequence that are available from the
	 * array <tt>objects</tt>.
	 */
	protected int available;

	/**
	 * A flag to signal if the next element in the sequence is available. The
	 * implementation used to set this flag is as follows:
	 * <pre>
	 *     nextAvailable = objects.length==available-- && hasNext.invoke(objects);
	 * </pre>
	 * So the length of the <tt>objects</tt> array has to be the start-length,
	 * namely <tt>available</tt>, and the predicate <tt>hasNext</tt> must be
	 * <tt>true</tt>.
	 */
	protected boolean nextAvailable = false;

	/**
	 * Creates a new inductor with a user defined predicate delivering the end of
	 * the induction sequence.
	 *
	 * @param objects the start-values <tt>a<sub>0</sub></tt> to
	 *        <tt>a<sub>n</sub></tt> of the induction sequence.
	 * @param hasNext the predicate which determines whether a next value exists.
	 * @param next the function using parameters <tt>a<sub>i</sub></tt> to
	 *        <tt>a<sub>(n+i)</sub></tt> (<tt>i</tt> &ge; 0) to compute the
	 *        result-value <tt>a<sub>(n+1+i)</sub></tt>.
	 */
	public Inductor(Object[] objects, Predicate hasNext, Function next) {
		this.hasNext = hasNext;
		this.next = next;
		this.objects = objects;
		this.available = objects.length;
	}

	/**
	 * Creates a new inductor with a user defined boolean function delivering
	 * the end of the induction sequence. The boolean function <tt>hasNext</tt>
	 * is internally wrapped to a predicate.
	 *
	 * @param objects the start-values <tt>a<sub>0</sub></tt> to
	 *        <tt>a<sub>n</sub></tt> of the induction sequence.
	 * @param hasNext the boolean function which determines whether a next value
	 *        exists.
	 * @param next the function using parameters <tt>a<sub>i</sub></tt> to
	 *        <tt>a<sub>(n+i)</sub></tt> (<tt>i</tt> &ge; 0) to compute the
	 *        result-value <tt>a<sub>(n+1+i)</sub></tt>.
	 */
	public Inductor(Object[] objects, Function hasNext, Function next) {
		this(objects, new FunctionPredicate(hasNext), next);
	}

	/**
	 * Creates a new inductor delivering an infinite induction sequence, i.e.,
	 * the predicate which determines whether a next value exists is given by
	 * {@link xxl.core.predicates.Predicate#TRUE}.
	 *
	 * @param objects the start-values <tt>a<sub>0</sub></tt> to
	 *        <tt>a<sub>n</sub></tt> of the induction sequence.
	 * @param next the function using parameters <tt>a<sub>i</sub></tt> to
	 *        <tt>a<sub>(n+i)</sub></tt> (<tt>i</tt> &ge; 0) to compute the
	 *        result-value <tt>a<sub>(n+1+i)</sub></tt>.
	 */
	public Inductor(Object[] objects, Function next) {
		this(objects, Predicate.TRUE, next);
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the indutor has more elements.
	 */
	protected boolean hasNextObject() {
		return nextAvailable || available > 0;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the inductor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		if (available < objects.length) {
			Object nextObject = null;
			if (nextAvailable)
				nextObject = next.invoke(objects);
			System.arraycopy(objects, 1, objects, 0, available);
			if (nextAvailable)
				objects[available++] = nextObject;
		}
		nextAvailable = objects.length==available-- && hasNext.invoke(objects);
		return objects[0];
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
		
		Inductor fibonacci = new Inductor(
			new Object[] {
				new Integer(1),
				new Integer(1)
			},
			new Function() {
				public Object invoke(Object fib_1, Object fib_2) {
					return new Integer(((Integer)fib_1).intValue() + ((Integer)fib_2).intValue());
				}
			}
		);
		
		fibonacci.open();
		
		System.out.println(xxl.core.cursors.Cursors.nth(fibonacci, 7));
		
		fibonacci.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		Inductor factorial = new Inductor(
			new Object[] {
				new Integer(1)
			},
			new Function() {
				int factor = 1;
				
				public Object invoke(Object n) {
					return new Integer(((Integer)n).intValue() * factor++);
				}
			}
		);
		
		factorial.open();
		
		System.out.println(xxl.core.cursors.Cursors.nth(factorial, 3));
		
		factorial.close();
	}
}
