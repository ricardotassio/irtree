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
 * This class provides a higher-order predicate (in other words, this
 * predicate decorates two input predicates). The binary predicate is
 * the basis for every predicate implementing a logical binary
 * operator or the combination of two predicates.
 */
public abstract class BinaryPredicate extends Predicate {

	/**
	 * A reference to the first decorated predicate. This reference is
	 * used to perform method calls on this predicate.
	 */
	protected Predicate predicate0;

	/**
	 * A reference to the second decorated predicate. This reference is
	 * used to perform method calls on this predicate.
	 */
	protected Predicate predicate1;


	/**
	 * Creates a new binary predicate that decorates the specified
	 * predicates.
	 *
	 * @param predicate0 the first predicate to be decorated.
	 * @param predicate1 the second predicate to be decorated.
	 */
	public BinaryPredicate (Predicate predicate0, Predicate predicate1) {
		this.predicate0 = predicate0;
		this.predicate1 = predicate1;
	}
}
