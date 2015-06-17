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

/** 
 * Classes implementing this interface are either analytically or numerically
 * differentiable, i.e., the first derivative or an approximation of it 
 * can be computed by calling the method <code>derivative</code>. The derivative
 * is returned as a {@link xxl.core.math.functions.RealFunction real-valued function}.  
 *
 * @see xxl.core.math.functions.RealFunction
 * @see xxl.core.math.functions.RealFunctionArea
 */

public interface Differentiable {

	/** Returns the first derivative of the implementing class as
	 * a {@link xxl.core.math.functions.RealFunction real-valued function}.
	 *
	 * @return first derivative as {@link xxl.core.math.functions.RealFunction}
	 */
	public abstract RealFunction derivative();
}
