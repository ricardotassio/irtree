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

package xxl.core.cursors.filters;

import java.util.Iterator;

import xxl.core.cursors.SecureDecoratorCursor;
import xxl.core.functions.Function;
import xxl.core.predicates.FunctionPredicate;
import xxl.core.predicates.Predicate;

/**
 * A while-taker returns the elements of an input iteration as long as an unary
 * predicate (or boolean function) evaluated on each input element equals to a
 * user defined flag. More precisely, it returns the elements of an input
 * iteration as long as the result of the user defined predicate applied to the
 * next element of the input iteration equals to the boolean flag
 * <tt>asLongAs</tt> the caller specified in the constructor.
 * 
 * <p>The implementation of the <tt>hasNext</tt> method is as follows:
 * <pre>
 *     return super.hasNext() && asLongAs ^ !predicate.invoke(super.peek());
 * </pre></p>
 * 
 * <p><b>Note:</b> If the input iteration is given by an object of the class
 * {@link java.util.Iterator}, i.e., it does not support the <tt>peek</tt>
 * operation, it is internally wrapped to a cursor. If a boolean function is
 * specified in a constructor it is also internally wrapped to a predicate.</p>
 * 
 * <p><b>Example usage:</b>
 * <pre>
 *     WhileTaker whileTaker = new WhileTaker(
 *         new Enumerator(21),
 *         new Predicate() {
 *             public boolean invoke(Object object) {
 *                 return ((Integer)object).intValue() &lt; 10;
 *             }
 *         },
 *         true
 *     );
 * 
 *     whileTaker.open();
 * 
 *     while (whileTaker.hasNext())
 *         System.out.print(whileTaker.next() + "; ");
 *     System.out.flush();
 * 
 *     whileTaker.close();
 * </pre>
 * This instance of a while-taker uses an enumerator as input cursor containing
 * the elements 0,...,20. A predicate is defined which is evaluated for each
 * element of the enumerator and which returns <tt>true</tt> if the integer value
 * of this element is lower than 10. So all elements of the enumerator that are
 * lower than 10 will be returned, because the user specified flag
 * <tt>asLongAs</tt> has been set to <tt>true</tt>. The remaining elements of the
 * enumerator are printed to the output stream. So the output is:
 * <pre>
 *     0; 1; 2; 3; 4; 5; 6; 7; 8; 9;
 * </pre><p>
 * 
 * Please compare this output and functionality with that of a
 * {@link xxl.core.cursors.filters.WhileDropper while-dropper}. A while-taker
 * returns the input iteration's elements as long as the user defined predicate
 * evaluated for each of these elements equals to the user specified flag
 * <tt>asLongAs</tt>. A while-dropper drops the input iteration's elements as
 * long as the user defined predicate invoked on each of these elements equals to
 * the user specified flag <tt>asLongAs</tt> and returns the remaining elements.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.SecureDecoratorCursor
 * @see xxl.core.cursors.filters.WhileDropper
 */
public class WhileTaker extends SecureDecoratorCursor {

	/**
	 * The predicate used to select the input iteration's elements. As long as
	 * this predicate equals to the user specified flag <tt>asLongAs</tt> the
	 * elements of the input iteration will be returned.
	 */
	protected Predicate predicate;

	/**
	 * A kind of escape sequence for the predicate. The elements of the input
	 * iteration are returned as long as the predicate's evaluation result is
	 * equal to this flag.
	 */
	protected boolean asLongAs;

	/**
	 * Creates a new while-taker. Every iterator given to this constructor is
	 * wrapped to a cursor.
	 *
	 * @param iterator the input iteration.
	 * @param predicate the unary predicate determining how long the input
	 *        elements will be returned.
	 * @param asLongAs the elements of the input iteration are returned as long
	 *        as the predicate's evaluation result is equal to this flag.
	 */
	public WhileTaker(Iterator iterator, Predicate predicate, boolean asLongAs) {
		super(iterator);
		this.predicate = predicate;
		this.asLongAs = asLongAs;
	}

	/**
	 * Creates a new while-taker. Every iterator given to this constructor is
	 * wrapped to a cursor and the elements of the input iteration are returned
	 * as long as the predicate's evaluation result is <tt>true</tt>.
	 *
	 * @param iterator the input iteration.
	 * @param predicate the unary predicate determining how long the input
	 *        elements will be returned.
	 */
	public WhileTaker(Iterator iterator, Predicate predicate) {
		this(iterator, predicate, true);
	}

	/**
	 * Creates a new while-taker. Every iterator given to this constructor is
	 * wrapped to a cursor and the boolean function is internally wrapped to a
	 * predicate.
	 *
	 * @param iterator the input iteration.
	 * @param function the boolean function determining how long the input
	 *        elements will be returned.
	 * @param asLongAs the elements of the input iteration are returned as long
	 *        as the predicate's evaluation result is equal to this flag.
	 * @see xxl.core.functions.PredicateFunction
	 */
	public WhileTaker(Iterator iterator, Function function, boolean asLongAs) {
		this(iterator, new FunctionPredicate(function), asLongAs);
	}

	/**
	 * Creates a new while-taker. Every iterator given to this constructor is
	 * wrapped to a cursor and the boolean function is internally wrapped to a
	 * predicate. The elements of the input iteration are returned as long as the
	 * predicate's evaluation result is <tt>true</tt> (the boolean function
	 * returns {@link java.lang.Boolean#TRUE}).
	 *
	 * @param iterator the input iteration.
	 * @param function the boolean function determining how long the input
	 *        elements will be returned.
	 */
	public WhileTaker(Iterator iterator, Function function) {
		this(iterator, new FunctionPredicate(function), true);
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other words,
	 * returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would return an
	 * element rather than throwing an exception.) This happens when the input
	 * iteration has more elements and the result of the predicate applied on the
	 * input iteration's next element equals to the caller specified flag
	 * <tt>asLongAs</tt>. The implementation is as follows:
	 * <pre>
	 *     return super.hasNext() && asLongAs ^ !predicate.invoke(super.peek());
	 * </pre>
	 *
	 * @return <tt>true</tt> if the while-taker has more elements, otherwise
	 *         <tt>false</tt>.
	 */
	public boolean hasNext() {
		return super.hasNext() && asLongAs ^ !predicate.invoke(super.peek());
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
		
		WhileTaker whileTaker = new WhileTaker(
			new xxl.core.cursors.sources.Enumerator(21),
			new Predicate() {
				public boolean invoke(Object object) {
					return ((Integer)object).intValue() < 10;
				}
			},
			true
		);
		
		whileTaker.open();
		
		while (whileTaker.hasNext())
			System.out.print(whileTaker.next() + "; ");
		System.out.flush();
		
		whileTaker.close();
	}
}
