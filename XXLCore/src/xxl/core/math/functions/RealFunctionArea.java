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

/** This class wraps a {@link xxl.core.math.functions.RealFunction RealFunction} 
 * implementing {@link xxl.core.math.functions.Integrable Integrable} in order to compute
 * the area 'under' a given interval of the function.
 * Real-valued functions not implementing {@link xxl.core.math.functions.Integrable}
 * could be integrated by applying an algorithm for numerical
 * integration like the {@link xxl.core.math.numerics.integration.TrapezoidalRuleRealFunctionArea Trapezoidal rule}
 * or the {@link xxl.core.math.numerics.integration.SimpsonsRuleRealFunctionArea Simpson's rule}.
 *
 * @see xxl.core.math.functions.RealFunction
 * @see xxl.core.math.numerics.integration.TrapezoidalRuleRealFunctionArea
 * @see xxl.core.math.numerics.integration.SimpsonsRuleRealFunctionArea
 */

public class RealFunctionArea {

	/** real-valued function to integrate */
	protected RealFunction realFunction;

	/** Constructs a new Object of this type.
	 *
	 * @param realFunction {@link xxl.core.math.functions.RealFunction RealFunction} to wrap
	 */
	public RealFunctionArea(RealFunction realFunction) {
		this.realFunction = realFunction;
	}

	/** Evaluates the area "under" a given {@link xxl.core.math.functions.RealFunction RealFunction}
	 * of a given interval and returns the evaluation of \int_a^b f(x) dx.
	 * 
	 * @param a left interval border of the area to compute
	 * @param b right interval border of the area to compute	 
	 * @return area "under" the given real-valued function \int_a^b f(x) dx
	 * @throws IllegalArgumentException invalid parameters
	 */
	public double eval(double a, double b) throws IllegalArgumentException {
		return ((Integrable) realFunction).primitive().eval(b) - ((Integrable) realFunction).primitive().eval(a);
	}
}
