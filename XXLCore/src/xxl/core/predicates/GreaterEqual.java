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

import java.util.Comparator;

/**
 * This class provides a binary predicate that returns <tt>true</tt> if
 * the first given argument is greater than or equal to the second. In
 * other words, when <tt>argument0</tt> and <tt>argument1</tt> are the
 * given arguments, the predicate returns <tt>true</tt> if
 * (<tt>argument0&ge;argument1</tt>) holds.
 */
public class GreaterEqual extends Or {

	/**
	 * This instance can be used for getting a default instance of
	 * GreaterEqual. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns,
	 * Prototype in <i>Design Patterns: Elements of Reusable
	 * Object-Oriented Software</i> by Erich Gamma, Richard Helm, Ralph
	 * Johnson, and John Vlissides) except that there are no mechanisms
	 * to avoid the creation of other instances of GreaterEqual.
	 */
	public static final GreaterEqual DEFAULT_INSTANCE = new GreaterEqual();

	/**
	 * Creates a new binary predicate that determines whether the first
	 * given argument is greater than or equal to the second. Both
	 * arguments must implement the <tt>Comparable</tt> interface.
	 */
 	public GreaterEqual () {
		super(Greater.DEFAULT_INSTANCE, Equal.DEFAULT_INSTANCE);
	}

	/**
	 * Creates a new binary predicate that determines whether the first
	 * given argument is greater than or equal to the second.
	 *
	 * @param comparator the comparator that should be used for
	 *        comparing objects.
	 */
	public GreaterEqual (Comparator comparator) {
		super(new Greater(comparator), Equal.DEFAULT_INSTANCE);
	}
}
