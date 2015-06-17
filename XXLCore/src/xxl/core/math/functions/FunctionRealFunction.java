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

/** This class provides a wrapper for objects of type 
 * {@link xxl.core.functions.Function Function} working with
 * {@link java.lang.Number numerical data} for using them as a
 * {@link xxl.core.math.functions.RealFunction real-valued function}.
 * The wrapped {@link xxl.core.functions.Function Function} has to
 * consume objects of type <tt>Double</tt>
 * and to return objects of type {@link java.lang.Number Number}.
 *
 * @see xxl.core.math.functions.RealFunction
 * @see xxl.core.functions.Function
 */

public class FunctionRealFunction implements RealFunction {

	/** the {@link xxl.core.functions.Function Function} to wrap */
	protected Function function;

	/** Constructs a new object of this class.
	 * 
	 * @param function object of type {@link xxl.core.functions.Function Function} to wrap
	 */
	public FunctionRealFunction(Function function) {
		this.function = function;
	}

	/** Evaluates the function f at the double value x.
	 * 
	 * @param x function argument
	 * @return f(x)
	 */
	public double eval(double x) {
		return ((Number) function.invoke(new Double(x))).doubleValue();
	}

	/** Evaluates the function at the float value x.
	 * 
	 * @param x function argument
	 * @return f(x)
	 */
	public double eval(float x) {
		return ((Number) function.invoke(new Double(x))).doubleValue();
	}

	/** Evaluates the function at the int value x.
	 * 
	 * @param x function argument
	 * @return f(x)
	 */
	public double eval(int x) {
		return ((Number) function.invoke(new Double(x))).doubleValue();
	}

	/** Evaluates the function at the long value x.
	 * 
	 * @param x function argument
	 * @return f(x)
	 */
	public double eval(long x) {
		return ((Number) function.invoke(new Double(x))).doubleValue();
	}

	/** Evaluates the function at the byte value x.
	 * 
	 * @param x function argument
	 * @return f(x)
	 */
	public double eval(byte x) {
		return ((Number) function.invoke(new Double(x))).doubleValue();
	}
}
