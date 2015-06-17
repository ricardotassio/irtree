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
 * This class provides a binary predicate that determines whether two
 * given arguments are equal. The <tt>invoke</tt> method of the
 * predicate is based on the {@link Object#equals(Object) equals}
 * method of the class {@link Object}.
 *
 */

public class Equal extends Predicate {

	/**
	 * This instance can be used for getting a default instance of
	 * Equal. It is similar to the <i>Singleton Design Pattern</i> (for
	 * further details see Creational Patterns, Prototype in <i>Design
	 * Patterns: Elements of Reusable Object-Oriented Software</i> by
	 * Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides)
	 * except that there are no mechanisms to avoid the creation of
	 * other instances of Equal.
	 */
	public static final Equal DEFAULT_INSTANCE = new Equal();


	/**
	 * Creates a new Equal instance.
	*/
	public Equal() {}

	/**
	 * Returns whether the given arguments are equal or not. 
	 * If a <tt>null</tt> value is allowed to be passed as
	 * argument, the user has to wrap this predicate by calling 
	 * {@link Predicates#newNullSensitiveEqual(boolean)}.
	 * <br>
	 * The exact implementation of this method is:
	 * <pre>
	 * 	return argument0.equals(argument1);
	 * </pre>
	 *
	 * @param argument0 the first argument to the predicate.
	 * @param argument1 the second argument to the predicate.
	 * @return <tt>true</tt> if the given arguments are equal,
	 *         otherwise <tt>false</tt>.
	 */
	public boolean invoke (Object argument0, Object argument1) {
		return argument0.equals(argument1);
	}
}