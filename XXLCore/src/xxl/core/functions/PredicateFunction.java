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

/** Provides a wrapper for {@link xxl.core.predicates.Predicate Predicates} to
 * {@link xxl.core.functions.Function Functions}.
 * 
 * @see xxl.core.predicates.FunctionPredicate
 * @see xxl.core.functions.Function
 * @see xxl.core.predicates.Predicate
 *
 */

public class PredicateFunction extends Function{

	/** {@link xxl.core.predicates.Predicate Predicate} to wrap */
	protected Predicate predicate;

	/** Constructs a new wrapper for {@link xxl.core.predicates.Predicate Predicates}
	 * to {@link xxl.core.functions.Function Functions} wrapping the given predicate.
	 *
	 * @param predicate {@link xxl.core.predicates.Predicate Predicate} to wrap
	 */
	public PredicateFunction( Predicate predicate){
		this.predicate = predicate;
	}

	/** Invokes the function by calling the wrapped predicate and returning the
	 * corresponding value of the Boolean-Object.
	 * @param arguments passed to the wrapped predicate
	 * @return Boolean.TRUE if the wrapped predicate returns true,
	 * otherwise Boolean.FALSE.
	 */
	public Object invoke( Object [] arguments) {
		return predicate.invoke(arguments) ? Boolean.TRUE : Boolean.FALSE;
	}
}