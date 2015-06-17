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

package xxl.core.math.statistics.nonparametric.kernels;

/** Classes implementing this interface provide a n-dimensional 
 * {@link xxl.core.math.statistics.nonparametric.kernels.KernelFunction kernel function} 
 * evaluating objects of type <tt>double []</tt> representing a n-dimensional real-valued vector.
 * A <tt>kernel function of dimension n</tt> is defined as <br>f : R^n --> R with x |--> f(x). 
 * <br>
 * There are two common ways to implement kernel functions of higher dimensions.
 * In the first one a {@link xxl.core.math.statistics.nonparametric.kernels.ProductKernelFunction product kernel function}
 * is easily obtained by combining n one-dimensional {@link xxl.core.math.statistics.nonparametric.kernels.KernelFunction kernel functions}.
 * The second one directly realizes a spherical kernel function.
 *
 * @see xxl.core.math.statistics.nonparametric.kernels.KernelFunction
 * @see xxl.core.math.statistics.nonparametric.kernels.ProductKernelFunction
 */

public abstract interface KernelFunctionND {

	/** Evaluates the kernel function at given real-valued n-dimensional point x given 
	 * as an object of type <tt>double []</tt>.
	 * 
	 * @param x function argument given as <tt>double []</tt>
	 * @return f(x) with f : R^n --> R
	 */
	public abstract double eval(double[] x);
}
