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

/**
 * This class provides a binary predicate which left (first) argument
 * is bound to a constant object. <p>
 *
 * Example:<br>
 * Consider the predicate {@link Less} that returns true if the first
 * argument is less than the second.<br>
 * By creating a <tt>LeftBind</tt> instance
 * <pre>
 *     Predicate p = new LeftBind(new Less(), new Integer(42));
 * </pre>
 * <tt>p</tt> can be evaluated by calling
 * <pre>
 *     p.invoke(new Integer(2));                   //predicate: 42 < 2
 * </pre>
 * which corresponds to the call
 * <pre>
 *     p.invoke(new Integer(42),new Integer(2));  //predicate: 42 < 2
 * </pre>
 *
 * @see RightBind
 */
public class LeftBind extends MetaDataPredicate {

	/**
	 * This object is used as constant left (first) object of this
	 * predicate's <tt>invoke</tt> methods.
	 */
	protected Object constArgument0;

	/**
	 * Creates a new predicate which binds the left (first) argument of
	 * the specified predicate to the given constant object.
	 *
	 * @param predicate the predicate which left (first) argument
	 *        should be bound.
	 * @param constArgument0 the constant argument to be used for the
	 *        left (first) argument of the predicate.
	 */
	public LeftBind (Predicate predicate, Object constArgument0) {
		super(predicate);
		this.constArgument0 = constArgument0;
	}

	/**
	 * Creates a new predicate which binds the left (first) argument of
	 * the specified predicate to <tt>null</tt>.
	 *
	 * @param predicate the predicate which left (first) argument
	 *        should be bound to <tt>null</tt>.
	 */
	public LeftBind (Predicate predicate) {
		this(predicate, null);
	}

	/**
	 * Sets the constant value to which the left (first) argument of
	 * the wrapped predicate should be bound and returns the changed
	 * predicate.
	 *
	 * @param constArgument0 the constant value to which the left
	 *        (first) argument of the wrapped predicate should be
	 *        bound.
	 * @return the predicate which left (first) argument is bound to
	 *         the specified object.
	 */
	public LeftBind setLeft (Object constArgument0) {
		this.constArgument0 = constArgument0;
		return this;
	}

	/**
	 * Returns the result of the underlying predicate's <tt>invoke</tt>
	 * method that is called with the bound left (first) argument. The
	 * implementation of this method is
	 * <pre>
	 *     return predicate.invoke(constArgument0, arguments[1]);
	 * </pre>
	 *
	 * @param arguments the arguments to the underlying predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with the bound left (first)
	 *         argument.
	 */
	public boolean invoke (Object [] arguments) {
		return predicate.invoke(constArgument0, arguments[1]);
	}

	/**
	 * Returns the result of the underlying predicate's <tt>invoke</tt>
	 * method that is called with the bound left (first) argument. The
	 * implementation of this method is
	 * <pre>
	 *     return predicate.invoke(constArgument0, argument);
	 * </pre>
	 *
	 * @param argument the right (second) argument to the underlying
	 *        predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with the bound left (first)
	 *         argument.
	 */
	public boolean invoke (Object argument) {
		return predicate.invoke(constArgument0, argument);
	}

	/**
	 * Returns the result of the underlying predicate's <tt>invoke</tt>
	 * method that is called with the bound left (first) argument. The
	 * implementation of this method is
	 * <pre>
	 *     return predicate.invoke(constArgument0, argument1);
	 * </pre>
	 *
	 * @param argument0 the left argument to this predicate that is
	 *        replaced by the bound argument.
	 * @param argument1 the right (second) argument to the underlying
	 *        predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with the bound left (first)
	 *         argument.
	 */
	public boolean invoke (Object argument0, Object argument1) {
		return predicate.invoke(constArgument0, argument1);
	}

	/**
	 * Returns the meta data for this meta data predicate which is set
	 * to the constant left (first) argument of this predicate.
	 *
	 * @return the constant left (first) argument of this predicate.
	 */
	public Object getMetaData () {
		return constArgument0;
	}
}
