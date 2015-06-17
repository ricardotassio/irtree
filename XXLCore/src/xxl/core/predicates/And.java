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
 * This class provides a logical AND predicate. The AND predicate
 * represents the conjunction of two predicates to a new predicate that
 * returns <tt>true</tt> if and only if both underlying predicates
 * return <tt>true</tt>.
 */
public class And extends BinaryPredicate {

	/**
	 * Creates a new AND predicate that represents the conjunction of
	 * the specified predicates.
	 *
	 * @param predicate0 the first predicate of the conjunction.
	 * @param predicate1 the second predicate of the conjunction.
	 */
	public And (Predicate predicate0, Predicate predicate1) {
		super(predicate0, predicate1);
	}

	/**
	 * Returns <tt>true</tt> if and only if both underlying predicates
	 * return <tt>true</tt>, otherwise <tt>false</tt> is returned.
	 *
	 * @param arguments the arguments to the underlying predicates.
	 * @return <tt>true</tt> if and only if both underlying predicates
	 *         return <tt>true</tt>, otherwise <tt>false</tt>.
	 */
	public boolean invoke (Object [] arguments) {
		return predicate0.invoke(arguments) && predicate1.invoke(arguments);
	}

	/**
	 * Returns <tt>true</tt> if and only if both underlying predicates
	 * return <tt>true</tt>, otherwise <tt>false</tt> is returned.
	 *
	 * @return <tt>true</tt> if and only if both underlying predicates
	 *         return <tt>true</tt>, otherwise <tt>false</tt>.
	 */
	public boolean invoke () {
		return predicate0.invoke() && predicate1.invoke();
	}

	/**
	 * Returns <tt>true</tt> if and only if both underlying predicates
	 * return <tt>true</tt>, otherwise <tt>false</tt> is returned.
	 *
	 * @param argument the argument to the underlying predicates.
	 * @return <tt>true</tt> if and only if both underlying predicates
	 *         return <tt>true</tt>, otherwise <tt>false</tt>.
	 */
	public boolean invoke (Object argument) {
		return predicate0.invoke(argument) && predicate1.invoke(argument);
	}

	/**
	 * Returns <tt>true</tt> if and only if both underlying predicates
	 * return <tt>true</tt>, otherwise <tt>false</tt> is returned.
	 *
	 * @param argument0 the first arguments to the underlying
	 *        predicates.
	 * @param argument1 the second arguments to the underlying
	 *        predicates.
	 * @return <tt>true</tt> if and only if both underlying predicates
	 *         return <tt>true</tt>, otherwise <tt>false</tt>.
	 */
	public boolean invoke (Object argument0, Object argument1) {
		return predicate0.invoke(argument0, argument1) && predicate1.invoke(argument0, argument1);
	}
}