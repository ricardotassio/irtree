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

import xxl.core.functions.Projection;

/**
 * This class provides a decorator predicate that applies a projection
 * to the arguments of the underlying predicate. Everytime arguments
 * are passed to the <tt>invoke</tt> method of this class, the
 * arguments are first projected using a given projection and
 * afterwards passed to the <tt>invoke</tt> method of the underlying
 * predicate.
 *
 * @see Projection
 */
public class ProjectionPredicate extends FeaturePredicate {

	/**
	 * Creates a new projection predicate that applies the specified
	 * projection to the arguments of it's <tt>invoke</tt> methods
	 * before the <tt>invoke</tt> method of the given predicate is
	 * called.
	 *
	 * @param predicate the predicate wich input arguments should be
	 *        mapped.
	 * @param projection the projection that is applied to the
	 *        arguments of the wrapped predicate.
	 */
	public ProjectionPredicate (Predicate predicate, Projection projection) {
		super(predicate, projection);
	}
}
