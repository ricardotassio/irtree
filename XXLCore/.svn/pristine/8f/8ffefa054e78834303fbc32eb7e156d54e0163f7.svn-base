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

import xxl.core.util.Distance;
import xxl.core.util.DistanceTo;

/**
 * This class provides a binary predicate that determines whether the
 * distance between two given objects is less or equal a given epsilon
 * distance. For determining the distance between two objects, a
 * distance function must be specified during the creation of the
 * predicate. When no distance function has been specified, the
 * predicate tries to cast the first given object to the class
 * <tt>DistanceTo</tt> and calls the <tt>distance</tt> method of it.
 *
 * @see xxl.core.util.Distance
 * @see xxl.core.util.DistanceTo
 */
public class DistanceWithin extends Predicate {

	/**
	 * The distance function that should be used for determining the
	 * distance between two objects.
	 */
	protected Distance distance;

	/**
	 * The epsilon distance represents the maximum distance between two
	 * objects such that the predicate returns <tt>true</tt>. In other
	 * words, the predicate returns <tt>true</tt> if the result of the
	 * distance function is less or equal the given epsilon distance.
	 */
	protected double epsilon;

	/**
	 * Creates a new binary predicate that determines whether the
	 * distance between two given objects is within a specified epsilon
	 * distance.
	 *
	 * @param distance the distance function that should be used for
	 *        determining the distance between two objects.
	 * @param epsilon the double value represents the maximum distance
	 *        between two objects such that the predicate returns
	 *        <tt>true</tt>.
	 */
	public DistanceWithin (Distance distance, double epsilon) {
		this.epsilon = epsilon;
		this.distance = distance;
	}

	/**
	 * Creates a new binary predicate that determines whether the
	 * distance between two given objects is within a specified epsilon
	 * distance. The constructed predicate expects objects of the type
	 * <tt>DistanceTo</tt> in order to determine their distance. The
	 * exact implementation of the used distance function is shown in
	 * the following listing:
	 * <pre>
	 *     new Distance() {
	 *         public double distance (Object object0, Object object1) {
	 *             return ((DistanceTo)object0).distance(object1);
	 *         }
	 *     };
	 * </pre>
	 *
	 * @param epsilon the double value represents the maximum distance
	 *        between two objects such that the predicate returns
	 *        <tt>true</tt>.
	 */
	protected DistanceWithin(double epsilon){
		this(
			new Distance() {
				public double distance (Object object0, Object object1) {
					return ((DistanceTo)object0).distanceTo(object1);
				}
			},
			epsilon
		);
	}

	/**
	 * Returns whether the first specified object lies within an
	 * epsilon distance of the second specified object. In other words,
	 * determine the distance between the given objects and returns
	 * <tt>true</tt> if this distance is less or equal the given
	 * epsilon distance.
	 *
	 * @param argument0 the object which epsilon distance should be
	 *        regarded.
	 * @param argument1 the object that should be tested whether it
	 *        lies within the epsilon distance of the first object.
	 * @return <tt>true</tt> if the second object lies within the
	 *         epsilon distance of the first object, otherwise
	 *         <tt>false</tt>.
	 */
	public boolean invoke (Object argument0, Object argument1) {
		return distance.distance(argument0, argument1) <= epsilon;
	}
}
