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

import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.predicates.MetaDataPredicate;
import xxl.core.relational.JoinUtils;

/**
 * Operator that computes an intersection using the nested-loops
 * paradigm. 
 */
public class NestedLoopsIntersection extends xxl.core.cursors.intersections.NestedLoopsIntersection implements MetaDataCursor {

	/**
	 * Constructs a new NestedLoopsIntersection.
	 * 
	 * @param input0 the input MetaDataCursor that is traversed in the "outer"
	 * 		loop.
	 * @param input1 the input MetaDataCursor that is traversed in the "inner"
	 * 		loop.
	 * @param newCursor a function without parameters that delivers a new MetaDataCursor, when the
	 * 		Cursor <code>input1</code> cannot be reset, i.e.
	 * 		<code>input1.reset()</code> will cause a
	 * 		{@link java.lang.UnsupportedOperationException}.
	 */
	public NestedLoopsIntersection (MetaDataCursor input0, MetaDataCursor input1, Function newCursor) {
		super(input0, input1, newCursor, JoinUtils.naturalJoin((ResultSetMetaData)input0.getMetaData(), (ResultSetMetaData)input1.getMetaData()));
	}
	
	/**
	 * Constructs a new NestedLoopsIntersection.
	 * 
	 * @param resultSet0 the input ResultSet that is traversed in the "outer"
	 * 		loop.
	 * @param resultSet1 the input ResultSet that is traversed in the "inner"
	 * 		loop.
	 * @param newCursor a function without parameters that delivers a new ResultSet, when the
	 * 		Cursor <code>input1</code> cannot be reset, i.e.
	 * 		<code>input1.reset()</code> will cause a
	 * 		{@link java.lang.UnsupportedOperationException}.
	 */
	public NestedLoopsIntersection (ResultSet resultSet0, ResultSet resultSet1, Function newCursor) {
		this(new ResultSetMetaDataCursor(resultSet0), new ResultSetMetaDataCursor(resultSet1), newCursor);
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
