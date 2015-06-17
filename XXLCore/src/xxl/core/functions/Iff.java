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

package xxl.core.functions;

import xxl.core.predicates.Predicate;

/**
 * A functional If-clause.&nbsp;(like the java ?:-operator).
 *
 */

public class Iff extends Function {

	/** {@link xxl.core.predicates.Predicate predicate} providing the
	 * functionality of an if-clause */
	protected Predicate predicate;

	/** {@link xxl.core.functions.Function function} using in the case of TRUE */
	protected Function f1;

	/** {@link xxl.core.functions.Function function} using in the case of FALSE */
	protected Function f2;

	/** With this class one is able to compose a higher order function containing an if-clause.
	 * Constructs a new object of type {@link xxl.core.functions.Function Function}
	 * providing the functionality of an if-clause.
	 * If the given {@link xxl.core.predicates.Predicate predicate} returns <tt>true</tt>
	 * for the first argument the {@link xxl.core.functions.Function function} f1 will be invoked
	 * with this argument, otherwise the {@link xxl.core.functions.Function function} f2
	 * will be invoked. The function value will be returned.
	 * @param predicate {@link xxl.core.predicates.Predicate predicate} providing the functionality of an if-clause.
	 * If the {@link xxl.core.predicates.Predicate predicate} returns true, the if-branch will be executed resp. the
	 * first given function invoked, otherwise (false was been returned) the else-branch will be executed.
	 * @param f1 {@link xxl.core.functions.Function function} invoked in the if-branch
	 * @param f2 {@link xxl.core.functions.Function function} invoked in the else-branch
	 */
	public Iff( Predicate predicate, Function f1, Function f2){
		this.predicate = predicate;
		this.f1 = f1;
		this.f2 = f2;
	}

	/** Returns the result of the function as an object controlled by the given
	 * {@link xxl.core.predicates.Predicate predicate}. If the
	 * {@link xxl.core.predicates.Predicate predicate} returns <tt>true</tt> for the given
	 * <tt>arguments</tt> the {@link xxl.core.functions.Function function} of the
	 * {@link #f1 if-branch} will be invoked otherwise the
	 * {@link xxl.core.functions.Function function} of the
	 * {@link #f2 else-branch} will be invoked.
	 * @param arguments function arguments 
	 * @return the result of the function.
	 */
	public Object invoke( Object[] arguments) {
		return 	predicate.invoke( arguments ) ? 
				f1.invoke( arguments )
			:
				f2.invoke( arguments )
		;

		//old implementation:
		/*if( predicate.invoke( arguments))
			return f1.invoke( arguments);
		else
			return f2.invoke( arguments);/**/
	}
}
