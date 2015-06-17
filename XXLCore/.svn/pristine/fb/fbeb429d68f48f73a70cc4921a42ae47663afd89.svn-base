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
 * A Swapper decorates (wraps) a Function and swaps arguments when
 * calling <code>invoke(argument0, argument1)</code>
 * if demanded by the user or a given predicate.
 * 
 */

public class Swapper extends DecoratorFunction {

	/** Predicate determining if a swap is performed */
	protected Predicate swapPredicate;

	/** Constructs a new Swapper that swap the arguments
	 * driven by a given predicate.
	 *
	 * @param function Function to wrap
	 * @param swapPredicate predicate that determines whether to swap or not
	 */
	public Swapper( Function function, Predicate swapPredicate) {
		super( function);
		this.swapPredicate = swapPredicate;
	}

	/** Constructs a new Swapper that whether always swaps the arguments
	 * or never.
	 *
	 * @param function Function to wrap
	 * @param swap boolean indicating whether to swap or not
	 */
	public Swapper( Function function, boolean swap) {
		/* OLD FUNCITONALITY
		this(function);
		this.swap = swap;
		*/
		this(function, swap ? Predicate.TRUE : Predicate.FALSE);
	}

	/** Constructs a new Swapper that never swaps
	 *
	 * @param function Function to wrap
	 */
	public Swapper( Function function) {
		/* OLD FUNCTIONALITY
		super(function);
		*/
		this(function, Predicate.FALSE);
	}

	/** This method provides enhanced functionality to this Function by given the possibility of
	 * overruling the predicate controlling the swaps.
	 * If parameter <tt>doSwap==true</tt> the given arguments will be swapped otherwise not.
	 *
	 * @param doSwap overrules the internally stored predicate
	 * @param argument0 first argument to pass to the wrapped function
	 * @param argument1 second argument to pass to the wrapped function
	 * @return the returned <tt>Object</tt> of the wrapped function
	 */
	public Object invoke( boolean doSwap, Object argument0, Object argument1) {
		return doSwap ?
				function.invoke(argument1, argument0)	//return call to decorated function where arguments are swapped
			:
				function.invoke(argument0, argument1);	//return call to decorated function
	}

	/** This method invokes the wrapped function with the given arguments.
	 * The ordering of the arguments passed to the wrapped function
	 * is controlled by the given predicate.
	 * The predicate will be called in the given order of the arguments:<br>
	 * <code>
	 	swapPredicate.invoke( argument0, argument1)
	 * </code><br>
	 *
	 * @param argument0 first argument to pass to the wrapped function
	 * @param argument1 second argument to pass to the wrapped function
	 * @return the returned <tt>Object</tt> of the wrapped function
	 */
	public Object invoke( Object argument0, Object argument1) {
		return invoke( swapPredicate.invoke( argument0, argument1), argument0, argument1);
	}
}