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

package xxl.core.util;

import xxl.core.functions.Function;
import xxl.core.math.Maths;
import xxl.core.spatial.LpMetric;

/**
	An interface for determining the distance between two objects. (see also Comparable <--> Comparator)

	@see xxl.core.util.DistanceTo
*/
public interface Distance {

	/**
	 * A kind of factory method that returns a new distance function
	 * according to the L_1 metric ("manhatten" metric).
	 */
	public static final Function MANHATTEN = new Function () {
		public Object invoke () {
			return LpMetric.MANHATTEN;
		}
	};

	/**
	 * A kind of factory method that returns a new distance function
	 * according to the L_2 metric ("euclidean" metric).
	 */
	public static final Function EUCLIDEAN = new Function () {
		public Object invoke () {
			return LpMetric.EUCLIDEAN;
		}
	};

	/**
	 * A kind of factory method that returns a new distance function
	 * according to the Levenshtein distance.
	 */
	public static final Function LEVENSHTEIN = new Function () {
		public Object invoke () {
			return new Distance () {
				public double distance (Object object1, Object object2) {
					return Maths.levenshteinDistance((String)object1, (String)object2);
				}
			};
		}
	};

	/**
	 * Computes the distance between the given objects.
	 * @param object1 first object
	 * @param object2 second object
	 * @return returns the distance between given objects 
	 */
	public abstract double distance (Object object1, Object object2);

}
