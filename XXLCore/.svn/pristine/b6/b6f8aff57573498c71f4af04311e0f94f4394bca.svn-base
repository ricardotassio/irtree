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
 * Maps 2 arguments to a tuple (=Object[2]).
 * If the input-argument is an array of length 2
 * the argument is returned, otherwise an <tt>Exception</tt> occurs.<br>
 * The process of tuplifying is performed by the recursive invokation
 * of {@link xxl.core.functions.Function Function}. Calling<br>
 * <code>
	Object [] newTuple = Typlify.DEFAULT_INSTANCE.invoke( arg1, arg2);
 * </code><br>
 * causes a recursive call of the {@link #invoke(Object[]) invoke( Object [])-method}
 * returning the arguments as <tt>array</tt>.
 *
 */
public class Tuplify extends Function{

	/**
	 * This instance can be used for getting a default instance of
	 * Tuplify. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns, Prototype
	 * in <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of Tuplify.
	 */
	public static final Tuplify DEFAULT_INSTANCE = new Tuplify();

	/** Tuplifys the given arguments by getting called from the binary invokation method.
	 * <br>
	 * <b>Note</b>: Do not call this method directly. Use always the binary function call
	 * {@link xxl.core.functions.Function#invoke(Object,Object) invoke( arg1, arg2)} from 
	 * {@link xxl.core.functions.Function Function} because of the recursive aspect of Tuplify.
	 * 
	 * @param arguments array of dimension 2 to return
	 * @throws IllegalArgumentException if no argument is given or if too many arguments are given.
	 * There is no sense in typlifying three or more <tt>Objects</tt>.
	 * @return arguments
	 */
	public Object invoke( Object [] arguments) throws IllegalArgumentException{
		if ( arguments == null) throw new IllegalArgumentException("You can't tuplify null!"); 
		if ( arguments.length != 2) throw new IllegalArgumentException("Youn can't typlify " + arguments.length + " number of objects");
		return arguments;
	}
}