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

package xxl.core.math.queries;

/**
 * Classes implementing this interface are able to consume a <tt>window query</tt>
 * returning the selectivity or any other associated numerical value to the
 * given window. For instance, for a given <tt>window query</tt> the variance of the 
 * values could be regarded.  
 * A <tt>window query</tt> presumes numerical data at least over an interval scale,
 * i.e., data only supporting categorical scales like an ordinal scale or a nominal
 * scale could not be queried with a window query.
 * <br>A {@link xxl.core.math.queries.RangeQuery range query} differs from a  
 * {@link xxl.core.math.queries.WindowQuery window query} by the supported statistical scale.
 * <br>{@link xxl.core.math.queries.RangeQuery Range queries} presume an
 * ordered data space over a categorical scale, i.e., the difference between
 * the range borders is not defined. Moreover, in contrast to a <tt>window query</tt>
 * the right range border not belongs to the query range, i.e., a <tt>range query</tt>
 * could be expressed with <tt>[a,b)</tt> in contrast to a <tt>window query</tt> like <tt>[a,b]</tt>.
 *
 * @see xxl.core.math.queries.RangeQuery
 * @see xxl.core.math.queries.PointQuery
 */

public interface WindowQuery {

	/** Returns the selectivity (true selectivity or an estimation) or any other
	 * associated numerical value to the given window.
	 *
	 * @param a left border(s) of the queried window
	 * @param b right border(s) of the queried window	 
	 * @return a numerical value respectively the selectivity associated with the given query
	 */
	public abstract double windowQuery(Object a, Object b);
}