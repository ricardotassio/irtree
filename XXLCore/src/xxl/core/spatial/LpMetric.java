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

package xxl.core.spatial;

import xxl.core.spatial.points.Point;
import xxl.core.spatial.points.Points;
import xxl.core.util.Distance;

/**
 *	The L_p-Metric.
 *
 * @see xxl.core.spatial.points.Point
 *
 */
public class LpMetric implements Distance{

	/** The L_1 metric ("manhatten" metric).
	*/
	public static final LpMetric MANHATTEN = new LpMetric(1);

	/** The L_2 metric ("euclidean" metric).
	*/
	public static final LpMetric EUCLIDEAN = new LpMetric(2);

	/** The value p of the L_p metric.
	 */
	protected int p;

	/** Creates a new instance of the LpMetric.
	 *
	 * @param p the value for L_p.
	 */
	public LpMetric(int p){
		this.p = p;	
	}

	/** Returns the L_p distance of the given objects.
	 *  <br><br>
	 *  Implementation:
	 *  <code><pre>
	 *
	 *  public double distance(Object o1, Object o2){
	 *     return Points.lpDistance( (Point)o1, (Point)o2, p );
	 *  }
	 *
	 *  </code></pre>
	 * @param o1 first object
	 * @param o2 second object  
	 * @return returns the L_p distance of the given objects
	 */
	public double distance(Object o1, Object o2){
		return Points.lpDistance( (Point)o1, (Point)o2, p );
	}
}
