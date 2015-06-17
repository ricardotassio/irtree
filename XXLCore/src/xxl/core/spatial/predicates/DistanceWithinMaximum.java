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

package xxl.core.spatial.predicates;

import xxl.core.spatial.points.Point;
import xxl.core.spatial.points.Points;

/**
 *	A distance predicate based on the
 *	euclidean distance-measure (This distance-measure corresponds to the
 *	L_infinity-metric).
 *
 *  We provide a separate class since it uses some optimizations.
 *
 *  @see xxl.core.predicates.DistanceWithin
 *  @see xxl.core.spatial.points.Points
 *
 */
public class DistanceWithinMaximum extends xxl.core.predicates.DistanceWithin{

	/** Creates a new DistanceWithinMaximum instance.
     *
	 * @param epsilon the double value represents the maximum distance
	 *        between two objects such that the predicate returns
	 *        <tt>true</tt>.
	 */
	public DistanceWithinMaximum(double epsilon){
		super(epsilon);
	}

	/** Checks the distance of two given objects (assumed to be of type Point).
	 *
	 * This implementation is based on <tt>Points.withinMaximumDistance(Point,Point)</tt>.
	 * 
	 * @param o1 first object (Point)
	 * @param o2 second object (Point)
	 * @return returns <tt>true</tt> if the coordinate difference between the points in
	 *  any dimension is not bigger than the given distance	 
	 */
	public boolean invoke(Object o1, Object o2){
		return Points.withinMaximumDistance( (Point)o1, (Point)o2, epsilon );
	}
}
