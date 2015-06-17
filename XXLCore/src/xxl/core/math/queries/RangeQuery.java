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
 * This interface provides the support for the estimation of range queries respectively
 * the association of a numerical value with a <tt>range query<tt>.
 * For instance, for a given <tt>range query</tt> the variance of the 
 * values could be regarded. 
 * <br> 
 * A <tt>range query</tt> is performed on categorical data over an ordinal scale,
 * i.e., a total ordering provided by a {@link java.util.Comparator comparator}
 * is necessary. A <tt>range query</tt> contains all objects within the range
 * <tt>[ l, r)</tt> whereby the left-most object <tt>l</tt> is included and the
 * right-most object <tt>r</tt> is excluded. As an estimation of
 * the <tt>range query<\tt>, a double value is returned.
 *
 * @see xxl.core.math.queries.WindowQuery
 * @see xxl.core.math.queries.PointQuery
 */

public interface RangeQuery {

	/** Performs an estimation of a given <tt>range query</tt>.
	 *
	 * @param a left object of the range to query (inclusively)
	 * @param b right object of the range to query (exclusively)	 
	 * @return a numerical value to be associated with the query
	 */
	public abstract double rangeQuery(Object a, Object b);
}