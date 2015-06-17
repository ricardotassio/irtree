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

import xxl.core.cursors.Cursors;
import xxl.core.cursors.SecureDecoratorCursor;
import xxl.core.functions.Function;
import xxl.core.predicates.FunctionPredicate;
import xxl.core.predicates.Predicate;

/**
 * A filter is a {@link xxl.core.cursors.SecureDecoratorCursor decorator-cursor} that
 * selects the iteration's elements with the help of a user defined predicate (or
 * boolean function for backward compatibility). Only those elements, for which
 * the specified unary function applied on that element returns
 * {@link java.lang.Boolean#TRUE}, are returned by <tt>next</tt> or
 * <tt>peek</tt>. If a predicate is used instead, the elements of the decorated
 * input cursor are selected by evaluating this predicate for each element. So
 * only the elements are returned, where the predicate's evaluation result is
 * <tt>true</tt>.
 *
 * <p>The implementation of the <tt>next</tt> method is as follows:
 * <pre>
 *     if (computedHasNext)
 *         return hasNext;
 *     for (; super.hasNext(); super.next())
 *         if (predicate.invoke(super.peek()))
 *             return true;
 *     return false;
 * </pre></p>
 * 
 * <p><b>Note:</b> If the input iteration is given by an object of the class
 * {@link java.util.Iterator}, i.e., it does not support the <tt>peek</tt>
 * operation, it is internally wrapped to a cursor.</p>
 * 
 * <p><b>Example usage :</b>
 * <pre>
 *     Filter filter = new Filter(
 *         new Enumerator(11),
 *         new Function() {
 *             public Object invoke(Object next) {
 *                 return new Boolean(((Integer)next).intValue() % 2 == 0);
 *             }
 *         }
 *     );
 * 
 *     filter.open();
 * 
 *     while(filter.hasNext())
 *         System.out.println(filter.next());
 * 
 *     filter.close();
 * </pre>
 * This example demonstrates the filter functionality. This filter returns only
 * the even elements of the input enumerator, because the defined unary function
 * only returns Boolean.TRUE, if the integer value of the object modulo 2 is
 * equal to 0.</p>
 * 
 * <p><b>Example usage when defining a predicate instead:</b>
 * <pre>
 *     Filter filter = new Filter(
 *         new Enumerator(11),
 *         new Predicate() {
 *             public boolean invoke(Object next) {
 *                 return (((Integer)next).intValue() % 2 == 0;
 *             }
 *         }
 *     );
 * 
 *     filter.open();
 * 
 *     while(filter.hasNext())
 *         System.out.println(filter.next());
 * 
 *     filter.close();
 * </pre>
 * This example demonstrates the filter functionality implemented by using an
 * unary predicate. The result is exactly the same as in the first example!</p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.SecureDecoratorCursor
 * @see xxl.core.functions.Function
 * @see xxl.core.predicates.Predicate
 */
public class Filter extends SecureDecoratorCursor {

	/**
	 * The unary predicate used to evaluate an object.
	 */
	protected Predicate predicate;

	/**
	 * Creates a new filter by decorating the input iterator. The input iterator
	 * is wrapped to a {@link xxl.core.cursors.wrappers.IteratorCursor cursor},
	 * if the <tt>peek</tt> functionality is not supported.
	 *
	 * @param iterator the input iterator containing the elements to be filtered.
	 * @param predicate the unary predicate used to select the elements.
	 */
	public Filter(Iterator iterator, Predicate predicate) {
		super(Cursors.wrap(iterator));
		this.predicate = predicate;
	}

	/**
	 * Creates a new filter by decorating the input iterator. The input iterator
	 * is wrapped to a {@link xxl.core.cursors.wrappers.IteratorCursor cursor},
	 * if the <tt>peek</tt> functionality is not supported. The boolean function
	 * is internally wrapped to a predicate.
	 *
	 * @param iterator the input iterator containing the elements to be filtered.
	 * @param function the boolean, unary function used to select the elements.
	 */
	public Filter(Iterator iterator, Function function) {
		this(iterator, new FunctionPredicate(function));
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other words,
	 * returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would return an
	 * element rather than throwing an exception.)
	 * 
	 * <p>The implementation is as follows:
	 * <pre>
	 *     if (computedHasNext)
	 *         return hasNext;
	 *     for (; super.hasNext(); super.next())
	 *         if (predicate.invoke(super.peek()))
	 *             return true;
	 *     return false;
	 * </pre>
	 * Only those elements for which the specified predicate returns
	 * <tt>true</tt> are returned by <tt>next</tt> or <tt>peek</tt>.
	 *
	 * @return <tt>true</tt> if the filter has more elements.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 */
	public boolean hasNext() throws IllegalStateException {
		if (computedHasNext)
			return hasNext;
		for (; super.hasNext(); super.next()) 
			if (predicate.invoke(super.peek()))
				return true;
		return false;
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
		
		Filter filter = new Filter(
		    new xxl.core.cursors.sources.Enumerator(11),
		    new Predicate() {
				public boolean invoke(Object next) {
					return ((Integer)next).intValue() % 2 == 0;
				}
			}
		);
		
		filter.open();
		
		while (filter.hasNext())
			System.out.println(filter.next());
		
		filter.close();
	}
}
