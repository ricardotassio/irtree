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
 * This class provides a wrapper that wraps a {@link Function function}
 * to a {@link Predicate predicate}. Only functions that's {@link Function#invoke(Object)} method return
 * nothing but <tt>Boolean</tt> object can be wrapped.
 *
 * @see xxl.core.functions.PredicateFunction
 */
public class FunctionPredicate extends Predicate {

	/**
	 * A reference to the wrapped function. This reference is used to
	 * perform method calls on the underlying function.
	 */
	protected Function function;

	/**
	 * Constructs a new function predicate that wraps the specified
	 * function.
	 *
	 * @param function the function to be wrapped.
	 */
	public FunctionPredicate (Function function) {
		this.function = function;
	}

	/**
	 * Returns the result of the function as a primitive boolean value.
	 * <br>
	 * This implementation simply calls the <tt>invoke</tt> method of
	 * the underlying function with the given arguments and returns the
	 * primitive <tt>boolean</tt> value of it's result.
	 *
	 * @param arguments the arguments to the function.
	 * @return the result of the function as a primitive boolean value.
	 */
	public boolean invoke (Object[] arguments) {
		return ((Boolean)function.invoke(arguments)).booleanValue();
	}

	/**
	 * Returns the result of the function as a primitive boolean value.
	 * <br>
	 * This implementation simply calls the <tt>invoke</tt> method of
	 * the underlying function and returns the primitive
	 * <tt>boolean</tt> value of it's result.
	 *
	 * @return the result of the function as a primitive boolean value.
	 */
	public boolean invoke () {
		return ((Boolean)function.invoke()).booleanValue();
	}

	/**
	 * Returns the result of the function as a primitive boolean value.
	 * <br>
	 * This implementation simply calls the <tt>invoke</tt> method of
	 * the underlying function with the given argument and returns the
	 * primitive <tt>boolean</tt> value of it's result.
	 *
	 * @param argument the argument to the function.
	 * @return the result of the function as a primitive boolean value.
	 */
	public boolean invoke (Object argument) {
		return ((Boolean)function.invoke(argument)).booleanValue();
	}

	/**
	 * Returns the result of the function as a primitive boolean value.
	 * <br>
	 * This implementation simply calls the <tt>invoke</tt> method of
	 * the underlying function with the given arguments and returns the
	 * primitive <tt>boolean</tt> value of it's result.
	 *
	 * @param argument0 the first argument to the function.
	 * @param argument1 the second argument to the function.
	 * @return the result of the function as a primitive boolean value.
	 */
	public boolean invoke (Object argument0, Object argument1) {
		return ((Boolean)function.invoke(argument0, argument1)).booleanValue();
	}
}
