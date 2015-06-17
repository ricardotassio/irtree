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
 * This class provides a binary predicate which right (second) argument
 * is bound to a constant object. <p>
 *
 * Example:<br>
 * Consider the predicate {@link Less} that returns true if the first
 * argument is less than the second.<br>
 * By creating a <tt>RightBind</tt> instance
 * <pre>
 *     Predicate p = new RightBind(new Less(), new Integer(42));
 * </pre>
 * <tt>p</tt> can be evaluated by calling
 * <pre>
 *     p.invoke(new Integer(2));                   //predicate: 2 < 42
 * </pre>
 * which corresponds to the call
 * <pre>
 *     p.invoke(new Integer(2),new Integer(42));  //predicate: 2 < 42
 * </pre>
 *
 * @see LeftBind
 */
public class RightBind extends MetaDataPredicate {

	/**
	 * This object is used as constant right (second) object of this
	 * predicate's <tt>invoke</tt> methods.
	 */
	protected Object constArgument1;

	/**
	 * Creates a new predicate which binds the right (second) argument
	 * of the specified predicate to the given constant object.
	 *
	 * @param predicate the predicate which right (second) argument
	 *        should be bound.
	 * @param constArgument1 the constant argument to be used for the
	 *        right (second) argument of the predicate.
	 */
	public RightBind (Predicate predicate, Object constArgument1) {
		super(predicate);
		this.constArgument1 = constArgument1;
	}

	/**
	 * Creates a new predicate which binds the right (second) argument
	 * of the specified predicate to <tt>null</tt>.
	 *
	 * @param predicate the predicate which right (second) argument
	 *        should be bound to <tt>null</tt>.
	 */
	public RightBind (Predicate predicate) {
		this(predicate, null);
	}

	/**
	 * Sets the constant value to which the right (second) argument of
	 * the wrapped predicate should be bound and returns the changed
	 * predicate.
	 *
	 * @param constArgument1 the constant value to which the right
	 *        (second) argument of the wrapped predicate should be
	 *        bound.
	 * @return the predicate which right (second) argument is bound to
	 *         the specified object.
	 */
	public RightBind setRight (Object constArgument1) {
		this.constArgument1 = constArgument1;
		return this;
	}

	/**
	 * Returns the result of the underlying predicate's <tt>invoke</tt>
	 * method that is called with the bound right (second) argument.
	 * The implementation of this method is
	 * <pre>
	 *     return predicate.invoke(arguments[0], constArgument1);
	 * </pre>
	 *
	 * @param arguments the arguments to the underlying predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with the bound right (second)
	 *         argument.
	 */
	public boolean invoke (Object[] arguments) {
		return predicate.invoke(arguments[0], constArgument1);
	}

	/**
	 * Returns the result of the underlying predicate's <tt>invoke</tt>
	 * method that is called with the bound right (second) argument.
	 * The implementation of this method is
	 * <pre>
	 *     return predicate.invoke(argument, constArgument1);
	 * </pre>
	 *
	 * @param argument the left (first) argument to the underlying
	 *        predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with the bound right (second)
	 *         argument.
	 */
	public boolean invoke (Object argument) {
		return predicate.invoke(argument, constArgument1);
	}

	/**
	 * Returns the result of the underlying predicate's <tt>invoke</tt>
	 * method that is called with the bound right (second) argument.
	 * The implementation of this method is
	 * <pre>
	 *     return predicate.invoke(argument0, constArgument1);
	 * </pre>
	 *
	 * @param argument0 the left (first) argument to the underlying
	 *        predicate.
	 * @param argument1 the right argument to this predicate that is
	 *        replaced by the bound argument.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with the bound right (second)
	 *         argument.
	 */
	public boolean invoke (Object argument0, Object argument1) {
		return predicate.invoke(argument0, constArgument1);
	}

	/**
	 * Returns the meta data for this meta data predicate which is set
	 * to the constant right (second) argument of this predicate.
	 *
	 * @return the constant right (second) argument of this predicate.
	 */
	public Object getMetaData () {
		return constArgument1;
	}
}
