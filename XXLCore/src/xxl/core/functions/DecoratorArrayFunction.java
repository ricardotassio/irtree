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
 * A DecoratorArrayFunction invokes each function of the given
 * function array in the constructor on the given parameters
 * when calling the invoke()-method.
 * Meaning this class defines a function from an one dimensional object
 * space to a n-dimensonal object space<br>
 * <pre>
 * <tt>f:O -->O^n</tt> with <tt>f=( f1(a), f2(a), ... , fn(a))</tt>
 * </pre>
 * for given functions <tt>f1, ... , fn</tt> with <tt>fi:O-->O, i=1,...,n</tt>
 * <br>
 * by composing them in a vector-like manner.
 * <br><b>Note</b>:<br>
 * Equivalent to: <code>IDENTITY.compose(functions)</code>
 */

public class DecoratorArrayFunction extends Function {

	/** {@link xxl.core.functions.Function functions} used for composition */
	protected Function [] functions;

	/** Constructs a new object of this class.
	 * @param functions {@link xxl.core.functions.Function functions} used for composition
	 */
	public DecoratorArrayFunction( Function[] functions){
		this.functions = functions;
	}

	/** Invokes the composed {@link xxl.core.functions.Function function}
	 * with the given function-array. The composed function is defined by
	 * <br>
	 * <tt>f:O -->O^n</tt> with <tt>f=( f1(a), f2(a), ... , fn(a))</tt>
	 * <br>
	 * with the given arguments.
	 * @param arguments arguments passed to the given functions
	 * @return an <code>Object[]</code> containing the results of the
	 * invoked functions
	 */
	public Object invoke( Object [] arguments) {
		Object [] result = new Object [functions.length];
		for (int i=0; i < functions.length; i++)
			result[i] = functions[i].invoke( arguments);
		return result;
	}
}