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

package xxl.core.math.functions;

import xxl.core.functions.Function;

/** This class provides a skeletal implementation for classes extending
 * {@link xxl.core.functions.Function Function}
 * and also implementing {@link xxl.core.math.functions.RealFunction RealFunction}.
 * The {@link #invoke(Object) invoke method} of this class expects Objects of type
 * {@link java.lang.Number Number} and returns Objects of type <tt>Double</tt>.
 * <br>
 * <code><pre>
 	public Object invoke( Object value){
		return new Double( eval( ((Number) value).doubleValue() ));
	}
 * </pre></code>
 * <br>
 * The abstract {@link #eval(double) eval method} of this class models the evaluation of 
 * a one-dimensional real-valued function, that returns real values, i.e., 
 * a double value is expected and a double value is also returned. 
 *
 * @see xxl.core.functions.Function
 * @see xxl.core.math.functions.RealFunction
 * @see xxl.core.math.functions.RealFunctionFunction
 * @see xxl.core.math.functions.FunctionRealFunction
 */

public abstract class AbstractRealFunctionFunction extends Function implements RealFunction {

	/** 
	 * Evaluates the real-valued function.
	 *
	 * @param x function argument
	 * @return function value 
	 */
	public abstract double eval(double x);

	/** 
	 * Converts the given Object to a double value and returns the evaluated
	 * function value given by the {@link #eval(double) eval method}.
	 * 
	 * @param value Object of type {@link java.lang.Number Number}
	 * @return Object of type <tt>Double</tt> representing the function value
	 */
	public Object invoke(Object value) {
		return new Double(eval(((Number) value).doubleValue()));
	}
}