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

package xxl.core.relational.cursors;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Comparator;

import xxl.core.collections.sweepAreas.SweepAreaImplementor;
import xxl.core.cursors.MetaDataCursor;
import xxl.core.predicates.MetaDataPredicate;
import xxl.core.relational.JoinUtils;

/**
 * Operator that computes an intersection using the sortmerge
 * paradigm. The input datasets have
 * to be sorted according to the comparator that is turned over.
 */
public class SortBasedIntersection extends xxl.core.cursors.intersections.SortBasedIntersection implements MetaDataCursor {
	
	/**
	 * Creates a new sort-based intersection operator backed on two sorted input
	 * iterations using the given sweep-area to store the elements of the first
	 * input iteration and probe with the elements of the second one for
	 * matchings.
	 * 
	 * @param sortedCursor0 the first sorted cursor to be intersected.
	 * @param sortedCursor1 the second sorted cursor to be intersected.
	 * @param impl the sweep-area implementor used for storing elements of the
	 *        first sorted input iteration (<tt>sortedInput0</tt>).
	 * @param comparator the comparator that is used for comparing elements of
	 *        the two sorted input iterations.
	 */
	public SortBasedIntersection (MetaDataCursor sortedCursor0, MetaDataCursor sortedCursor1, SweepAreaImplementor impl, Comparator comparator) {
		super(sortedCursor0, sortedCursor1, impl, comparator, JoinUtils.naturalJoin((ResultSetMetaData)sortedCursor0.getMetaData(), (ResultSetMetaData)sortedCursor1.getMetaData()));
	}
	
	/**
	 * Creates a new sort-based intersection operator backed on two sorted input
	 * iterations using the given sweep-area to store the elements of the first
	 * input iteration and probe with the elements of the second one for
	 * matchings.
	 * 
	 * @param sortedResultSet0 the first sorted result set to be intersected.
	 * @param sortedResultSet1 the second sorted result set to be intersected.
	 * @param impl the sweep-area implementor used for storing elements of the
	 *        first sorted input iteration (<tt>sortedInput0</tt>).
	 * @param comparator the comparator that is used for comparing elements of
	 *        the two sorted input iterations.
	 */
	public SortBasedIntersection (ResultSet sortedResultSet0, ResultSet sortedResultSet1, SweepAreaImplementor impl, Comparator comparator) {
		this(new ResultSetMetaDataCursor(sortedResultSet0), new ResultSetMetaDataCursor(sortedResultSet1), impl, comparator);
	}

	/**
	 * Returns the resulting metadata for this operator.
	 *
	 * Since the schemas have to be compatible, the
	 * metadata is identical to the input operator metadata.
	 *
	 * @return object of class ResultSetMetaData
	 */
	public Object getMetaData () {
		return ((MetaDataPredicate)equals).getMetaData();
	}

}
