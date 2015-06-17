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

package xxl.core.predicates;

import xxl.core.functions.Function;

/**
 * This class provides a decorator predicate that applies a mapping to
 * the arguments of the underlying predicate. Everytime arguments are
 * passed to an <tt>invoke</tt> method of this class, the arguments are
 * first mapped using a given mapping function and afterwards passed to
 * the <tt>invoke</tt> method of the underlying predicate.
 */
public class FeaturePredicate extends DecoratorPredicate {

	/**
	 * A reference to the mapping function that is applied to the
	 * arguments of this predicate's <tt>invoke</tt> methods before the
	 * <tt>invoke</tt> method of the underlying predicate is called.
	 */
	protected Function mapping;

	/**
	 * Creates a new feature predicate that applies the specified
	 * mapping to the arguments of it's <tt>invoke</tt> methods before
	 * the <tt>invoke</tt> method of the given predicate is called.
	 *
	 * @param predicate the predicate which input arguments should be
	 *        mapped.
	 * @param mapping the mapping function that is applied to the
	 *        arguments of the wrapped predicate.
	 */
	public FeaturePredicate (Predicate predicate, Function mapping) {
		super(predicate);
		this.mapping = mapping;
	}

	/**
	 * Returns the result of the predicate as a primitive boolean
	 * value. The mapped arguments of this method are passed to the
	 * underlying predicate's <tt>invoke</tt> method.
	 *
	 * @param arguments the arguments to the predicate.
	 * @return the result of the predicate as a primitive boolean
	 *         value.
	 */
	public boolean invoke (Object [] arguments) {
		return predicate.invoke(mapping.invoke(arguments));
	}

	/**
	 * Returns the result of the predicate as a primitive boolean
	 * value. The mapped argument of this method is passed to the
	 * underlying predicate's <tt>invoke</tt> method.
	 *
	 * @param argument the argument to the predicate.
	 * @return the result of the predicate as a primitive boolean
	 *         value.
	 */
	public boolean invoke (Object argument) {
		return predicate.invoke(mapping.invoke(argument));
	}

	/**
	 * Returns the result of the predicate as a primitive boolean
	 * value. The mapped arguments of this method are passed to the
	 * underlying predicate's <tt>invoke</tt> method.
	 *
	 * @param argument0 the first argument to the predicate.
	 * @param argument1 the second argument to the predicate.
	 * @return the result of the predicate as a primitive boolean
	 *         value.
	 */
	public boolean invoke (Object argument0, Object argument1) {
		return predicate.invoke(mapping.invoke(argument0), mapping.invoke(argument1));
	}
}