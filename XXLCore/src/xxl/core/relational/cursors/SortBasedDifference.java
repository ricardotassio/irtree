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
import java.sql.SQLException;
import java.util.Comparator;

import xxl.core.cursors.MetaDataCursor;
import xxl.core.util.WrappingRuntimeException;

/**
 * Operator computing the difference of two sorted MetaDataCursors. This class
 * uses the algorithm of xxl.core.cursors.differences.SortBasedDifference and additionally computes the
 * metadata. The metadata is checked whether it fits.
 *
 * Caution: the input iterations have to be sorted! To sort a
 * MetaDataCursor use the {@link MergeSorter MergeSorter}.
 *
 */
public class SortBasedDifference extends xxl.core.cursors.differences.SortBasedDifference implements MetaDataCursor {
	
	/** Metadata describing the output Tuples */
	ResultSetMetaData metaData;
	
	/**
	 * Constructs a SortBasedDifference Operator (MetaDataCursor) that computes the difference
	 * between two sorted input MetaDataCursors. The parameters are the same compared to
	 *  xxl.core.cursors.differences.SortBasedDifference.
	 *
	 * To get a correct result, the input relations have to be sorted. To sort a
	 * MetaDataCursor use the {@link MergeSorter MergeSorter}.
	 *
	 * See also {@link xxl.core.relational.Tuples} for static methods delivering
	 * ready to use tuple comparators.
	 *
	 * @param sortedInput1 first input relation
	 * @param sortedInput2 second input relation
	 * @param comparator Comparator used to compare two tuples. To get a Comparator that
	 *	compares Tuples after some specified columns use Tuples.getTupleComparator.
	 * @param all mode of the difference operation.
	 *		If the mode is true, all tuples of the first relation, which have a
	 *		counterpart in the second relation, are removed.
	 *		If the mode is false, for each tuple of the second relation,
	 *		only one tuple of the first relation is removed at most.
	 * @param asc a flag showing if the input cursors have been sorted ascending or not.
	 */
	public SortBasedDifference (MetaDataCursor sortedInput1, MetaDataCursor sortedInput2, Comparator comparator, boolean all, boolean asc) {
		super(sortedInput1, sortedInput2, comparator, all, asc);
		metaData = (ResultSetMetaData) getMetaData();
	}

	/**
	 * Constructs a SortBasedDifference Operator (MetaDataCursor) that computes the difference
	 * bewteen two sorted input ResultSets. The ResultSets are wrapped to MetaDataCursors using
	 * {@link ResultSetMetaDataCursor ResultSetMetaDataCursor}.
	 *
	 * To get a correct result, the input relations have to be sorted. To sort a
	 * MetaDataCursor use the {@link MergeSorter MergeSorter}.
	 *
	 * See also {@link xxl.core.relational.Tuples} for static methods delivering
	 * ready to use tuple comparators.
	 *
	 * @param sortedResultSet1 first input relation
	 * @param sortedResultSet2 second input relation
	 * @param comparator Comparator used to compare two tuples. To get a Comparator that
	 *	compares Tuples after some specified columns use Tuples.getTupleComparator.
	 * @param all mode of the difference operation.
	 *		If the mode is true, all tuples of the first relation, which have a
	 *		counterpart in the second relation, are removed.
	 *		If the mode is false, for each tuple of the second relation,
	 *		only one tuple of the first relation is removed at most.
	 * @param asc a flag showing if the input cursors have been sorted ascending or not.
	 */
	public SortBasedDifference (ResultSet sortedResultSet1, ResultSet sortedResultSet2, Comparator comparator, boolean all, boolean asc) {
		this(new ResultSetMetaDataCursor(sortedResultSet1), new ResultSetMetaDataCursor(sortedResultSet2), comparator, all, asc);
	}

	/**
	 * Returns the metadata for a SortBasedDifference.
	 *
	 * The metadata is checked for correctness. That means the two relations must
	 * have the same number of columns and each corresponding column must have the same type.
	 * The resulting metadata is the metadata of the first relation. If the presision
	 * is not equal, a message is printed on System.err.
	 *
	 * @return object of class ResultSetMetaData
	 * @throws xxl.core.util.WrappingRuntimeException if the metadata cannot be accessed
	 */
	public Object getMetaData () {
		if (metaData==null) {
			try {
				ResultSetMetaData metaData1 = (ResultSetMetaData)((MetaDataCursor)input1).getMetaData();
				ResultSetMetaData metaData2 = (ResultSetMetaData)((MetaDataCursor)input2).getMetaData();
				if (metaData1.getColumnCount() != metaData2.getColumnCount())
					throw new IllegalArgumentException("ResultSets have a different number of columns.");
				for (int i = 1; i <= metaData1.getColumnCount(); i++) {
					if (!metaData1.getColumnTypeName(i).equals(metaData2.getColumnTypeName(i)))
						throw new IllegalArgumentException("ResultSets have different column types.");
					if (metaData1.getPrecision(i) < metaData2.getPrecision(i))
						System.err.println("Possible loss of precision: Second ResultSet has a higher precision in one column.");
				}
				metaData=metaData1;
			}
			catch (SQLException se) {
				throw new WrappingRuntimeException(se);
			}
		}
		return metaData;
	}
}
