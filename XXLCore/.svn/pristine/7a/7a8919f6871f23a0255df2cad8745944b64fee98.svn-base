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

package xxl.core.comparators;

import java.io.Serializable;
import java.util.Comparator;
import xxl.core.util.Distance;

/**
 * The DistanceBasedComparator compares two objects by determining
 * their distances to a given reference object.
 * The object which is closer to the reference object based on the distance function will
 * be seen as smaller object.
 *
 * @see java.util.Comparator
 * @see xxl.core.util.Distance
 */

public class DistanceBasedComparator implements Comparator, Serializable {

	/** Distance Function to compute the distance of the given object to the reference object */
	protected Distance distance;

	/** Object used as reference for the DistanceBasedComparator */
	protected Object reference;

	/** 
	 * Constructs a new object of this class.
	 *
	 * @param reference reference object
	 * @param distance {@link xxl.core.util.Distance distance function} used for computing the distances
	 *        of the objects to compare
	 */
	public DistanceBasedComparator ( Object reference, Distance distance) {
		this.reference = reference;
		this.distance = distance;
	}

	/** 
	 * Compares two objects based on the distance to a given reference object.
	 * For details see {@link java.util.Comparator}.
	 * 
	 * @param o1 the first object to be compared
	 * @param o2 the second object to be compared
	 * 
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second wrt to the given
	 *         distance function and reference object
	 *
	 * @see java.util.Comparator
	 */
	public int compare (Object o1, Object o2) {
		double d1 = distance.distance ( o1, reference);
		double d2 = distance.distance ( o2, reference);
		return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
	}
}