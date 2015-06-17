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

/**
 * A DecoratorFunction decorates (wraps) a Function by passing
 * given arguments to the corresponding invoke-methods of the wrapped function.
 * To enhance the functionality of a Function just inherit from this class and override the
 * invoke-method or your choice.
 */

public class DecoratorFunction extends Function{

	/** Functiion to decorate */
	protected Function function;

	/** Constructs a new Object of this class.
	 * @param function to decorate or to wrap
	 */
	public DecoratorFunction( Function function){
		this.function = function;
	}

	/** Constructs a new Object of this class without a Function to decorate.
	 * This constuctor is only used by classes inherited from this class
	 * providing an enhanced functionality.<br>
	 * <b>Note</b>: Do not use this constructor by calling it directly.
	 */
	public DecoratorFunction() {
		this( null);
	}

	/** Passes the argument(s) to the decorated resp. wrapped function
	 * by calling the corresponding invoke-method by the wrapped function.
	 *
	 * @return the returned object of the wrapped function
	 */
	public Object invoke() {
		return function.invoke();
	}

	/** Passes the argument(s) to the decorated resp. wrapped function
	 * by calling the corresponding invoke-method by the wrapped function.
	 * 
	 * @param o argument passed to the wrapped function
	 * @return the returned object of the wrapped function
	 */
	public Object invoke( Object o) {
		return function.invoke( o);
	}

	/** Passes the argument(s) to the decorated resp. wrapped function
	 * by calling the corresponding invoke-method by the wrapped function.
	 *
	 * @param o1 argument passed to the wrapped function
	 * @param o2 argument passed to the wrapped function
	 * @return the returned object of the wrapped function
	 */
	public Object invoke(Object o1, Object o2) {
		return function.invoke( o1, o2);
	}

	/** Passes the argument(s) to the decorated resp. wrapped function
	 * by calling the corresponding invoke-method by the wrapped function.
	 *
	 * @param arguments arguments passed to the wrapped function
	 * @return the returned object of the wrapped function
	 */
	public Object invoke( Object [] arguments) {
		return function.invoke( arguments);
	}

	/** This method declares a new function <tt>h</tt> by composing an array of functions
	 * <tt>f_1, ..., f_n</tt> with this function <tt>g</tt>.<br>
	 * <b>Note</b>: This method does not execute the code of the new function <tt>h</tt>.
	 * The invocation of the function <tt>h</tt> is triggered by a call of its invoke method.
	 * Then, the input parameters are passed to each function of the array
	 * <tt>(f_1, ..., f_n)</tt>, and the returned objects are used as the input parameters
	 * of this function <tt>h</tt>. The invoke method of <tt>h</tt> returns the final result.
	 * @param functions the functions to be composed with this function.
	 * @return the result of the composition.
	 */
	public Function compose(final Function [] functions) {
		return function.compose( functions);
	}
	/** This method declares a new function <tt>h</tt> by composing a function
	 * <tt>f</tt> with this function <tt>g</tt>.<br>
	 * <b>Note</b>: This method does not execute the code of the new function <tt>h</tt>.
	 * The invocation of the function <tt>h</tt> is triggered by a call of its invoke method.
	 * Then, the input parameters are passed to the function <tt>f</tt>,
	 * and the returned objects are used as the input parameters
	 * of this function <tt>h</tt>. The invoke method of <tt>h</tt> returns the final result.
	 * @param function the function to compose with this function.
	 * @return the result of the composition.
	 */
	public Function compose( Function function) {
		return function.compose( function);
	}
}