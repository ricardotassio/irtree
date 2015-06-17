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

import xxl.core.functions.Function;
import xxl.core.predicates.Predicate;

/**
 * A while-remover removes the elements of an input cursor with the help of a
 * user defined predicate (or boolean function). It removes the elements as long
 * as the result of the user defined, unary predicate applied to the next element
 * of the input cursor is <tt>true</tt>. An instance of a while-remover is
 * created as follows:
 * <pre>
 *     super(new WhileTaker(iterator, predicate));
 * </pre>
 * So simply a new {@link xxl.core.cursors.filters.WhileTaker while-taker} is
 * created that selects the elements with the help of the given predicate and
 * this instance of a while-taker is used as the input cursor of a
 * {@link xxl.core.cursors.filters.Remover remover}.
 * 
 * <p><b>Note:</b> If the input iteration is given by an object of the class
 * {@link java.util.Iterator}, i.e., it does not support the <tt>peek</tt>
 * operation, it is internally wrapped to a cursor. If a boolean function is
 * specified in a constructor it is also internally wrapped to a predicate.</p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.filters.Remover
 * @see xxl.core.cursors.filters.WhileTaker
 */
public class WhileRemover extends Remover {

	/**
	 * Creates a new while-remover backed on an iteration and an unary predicate
	 * applied on the iteration's elements. The iteration's elements will be
	 * removed from the underlying data structure as long as the specified
	 * predicate returns <tt>true</tt>.
	 * 
	 * <p>The implementation of this constructor is as follows:
	 * <pre>
	 *     super(new WhileTaker(iterator, predicate));
	 * </pre>
	 * So a {@link xxl.core.cursors.filters.WhileTaker while-taker} is created
	 * that selects the elements with the help of the given predicate, and this
	 * instance of a while-taker is used as the input for a
	 * {@link xxl.core.cursors.filters.Remover remover}.
	 *
	 * @param iterator the input iteration the elements will be removed from as
	 *        long as the specified predicate applied on the current element
	 *        returns <tt>true</tt>.
	 * @param predicate the unary predicate used to select the elements of the
	 *        input iteration.
	 */
	public WhileRemover(Iterator iterator, Predicate predicate) {
		super(new WhileTaker(iterator, predicate));
	}
	
	/**
	 * Creates a new while-remover backed on an iteration and an unary boolean
	 * function applied on the iteration's elements. The iteration's elements
	 * will be removed from the underlying data structure as long as the
	 * specified function returns {@link java.lang.Boolean#TRUE}.
	 * 
	 * <p>The implementation of this constructor is as follows:
	 * <pre>
	 *     super(new WhileTaker(iterator, function));
	 * </pre>
	 * So a {@link xxl.core.cursors.filters.WhileTaker while-taker} is created
	 * that selects the elements with the help of the given function, and this
	 * instance of a while-taker is used as the input for a
	 * {@link xxl.core.cursors.filters.Remover remover}.
	 *
	 * @param iterator the input iteration the elements will be removed from as
	 *        long as the specified predicate applied on the current element
	 *        returns <tt>true</tt>.
	 * @param function the unary boolean function used to select the elements of
	 *        the input iteration.
	 */
	public WhileRemover(Iterator iterator, Function function) {
		super(new WhileTaker(iterator, function));
	}
}
