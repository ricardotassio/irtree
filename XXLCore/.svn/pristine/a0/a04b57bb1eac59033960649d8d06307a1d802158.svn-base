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

import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.predicates.Equal;
import xxl.core.predicates.Predicate;
import xxl.core.util.WrappingRuntimeException;

/**
 * Operator computing the difference of two MetaDataCursors. This class
 * uses the algorithm of xxl.core.cursors.differences.NestedLoopsDifference and additionally computes the
 * metadata. The metadata are checked whether they fit.
 *
 * Caution: the runtime is quadratic!
 */
public class NestedLoopsDifference extends xxl.core.cursors.differences.NestedLoopsDifference implements MetaDataCursor {

	/**
	 * Constructs a NestedLoopsDifference Operator (MetaDataCursor) that computes the difference
	 * between  two input MetaDataCursors (R-S). The parameters are the same compared to
	 * {@link xxl.core.cursors.differences.NestedLoopsDifference}.
	 *
	 * Determines the maximum number of elements that can be stored
	 * in the bag used for the temporal storage of the elements of
	 * <tt>input1</tt> in main memory:
	 * <code>maxTuples = memSize / objectSize - 1</code>. <br>
	 *
	 * @param input1 first input relation R (MetaDataCursor)
	 * @param input2 second input relation S (MetaDataCursor) containing the elements
	 *	that become substracted. This cursor has to support <code>reset</code>.
	 * @param memSize the maximum amount of available main memory that can be used for the bag.
	 * @param objectSize the size (bytes) needed to store one object of an input cursor.
	 * @param newBag a parameterless function delivering an empty bag on demand. This bag is used to
	 * 		store the elements of MetaDataCursor <tt>input1</tt>.
	 * @param predicate a binaray predicate that has to determine a match between
	 * 		an element of <tt>input1</tt> and an element of <tt>input2</tt>.
	 * @param all mode of the difference operation.
	 *		If the mode is true, all tuples of the first relation, which have a
	 *		counterpart in the second relation, are removed.
	 *		If the mode is false, for each tuple of the second relation,
	 *		only one tuple of the first relation is removed at most.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsDifference (MetaDataCursor input1, MetaDataCursor input2, int memSize, int objectSize, Function newBag, Predicate predicate, boolean all) {
		super(input1, input2, memSize, objectSize, newBag, predicate, all);
	}

	/**
	 * Constructs a NestedLoopsDifference Operator (MetaDataCursor) that computes the difference
	 * between  two input MetaDataCursors (R-S). The parameters are the same compared to
	 * {@link xxl.core.cursors.differences.NestedLoopsDifference}.
	 *
	 * Determines the maximum number of elements that can be stored
	 * in the bag used for the temporal storage of the elements of
	 * <tt>input1</tt> in main memory:
	 * <code>maxTuples = memSize / objectSize - 1</code>. <br>
	 * 
	 * This constructor uses the Equal.DEFAULT_INSTANCE predicate to determine if two
	 * objects are equal.
	 *
	 * @param input1 first input relation R (MetaDataCursor)
	 * @param input2 second input relation S (MetaDataCursor) containing the elements
	 *	that become substracted. This cursor has to support <code>reset</code>.
	 * @param memSize the maximum amount of available main memory that can be used for the bag.
	 * @param objectSize the size (bytes) needed to store one object of an input cursor.
	 * @param newBag a parameterless function delivering an empty bag on demand. This bag is used to
	 * 		store the elements of MetaDataCursor <tt>input1</tt>.
	 * @param all mode of the difference operation.
	 *		If the mode is true, all tuples of the first relation, which have a
	 *		counterpart in the second relation, are removed.
	 *		If the mode is false, for each tuple of the second relation,
	 *		only one tuple of the first relation is removed at most.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsDifference (MetaDataCursor input1, MetaDataCursor input2, int memSize, int objectSize, Function newBag, boolean all) {
		super(input1, input2, memSize, objectSize, newBag, Equal.DEFAULT_INSTANCE, all);
	}

	/**
	 * Constructs a NestedLoopsDifference Operator (MetaDataCursor) that computes the difference
	 * between two input ResultSets (R-S). Each ResultSet is wrapped internally to a MetaDataCursor.
	 *
	 * Determines the maximum number of elements that can be stored
	 * in the bag used for the temporal storage of the elements of
	 * <tt>input1</tt> in main memory:
	 * <code>maxTuples = memSize / objectSize - 1</code>. <br>
	 *
	 * @param input1 first input relation R (ResultSet)
	 * @param input2 second input relation S (ResultSet) containing the elements
	 *	that become subtracted. This ResultSet has to support <code>beforeFirst</code>.
	 * @param memSize the maximum amount of available main memory that can be used for the bag.
	 * @param objectSize the size (bytes) needed to store one object of an input cursor.
	 * @param newBag a parameterless function delivering an empty bag on demand. This bag is used to
	 * 		store the elements of MetaDataCursor <tt>input1</tt>.
	 * @param predicate a binaray predicate that has to determine a match between
	 * 		an element of <tt>input1</tt> and an element of <tt>input2</tt>.
	 * @param all mode of the difference operation.
	 *		If the mode is true, all tuples of the first relation, which have a
	 *		counterpart in the second relation, are removed.
	 *		If the mode is false, for each tuple of the second relation,
	 *		only one tuple of the first relation is removed at most.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsDifference (ResultSet input1, ResultSet input2, int memSize, int objectSize, Function newBag, Predicate predicate, boolean all) {
		super(new ResultSetMetaDataCursor(input1), new ResultSetMetaDataCursor(input2), memSize, objectSize, newBag, predicate, all);
	}

	/**
	 * Constructs a NestedLoopsDifference Operator (MetaDataCursor) that computes the difference
	 * between  two input ResultSets (R-S). Each ResultSet is wrapped internally to a MetaDataCursor.
	 *
	 * Determines the maximum number of elements that can be stored
	 * in the bag used for the temporal storage of the elements of
	 * <tt>input1</tt> in main memory:
	 * <code>maxTuples = memSize / objectSize - 1</code>. <br>
	 * 
	 * This constructor uses the Equal.DEFAULT_INSTANCE predicate to determine if two
	 * objects are equal.
	 *
	 * @param input1 first input relation R (ResultSet)
	 * @param input2 second input relation S (ResultSet) containing the elements
	 *	that become substracted. This cursor has to support <code>beforeFirst</code>.
	 * @param memSize the maximum amount of available main memory that can be used for the bag.
	 * @param objectSize the size (bytes) needed to store one object of an input cursor.
	 * @param newBag a parameterless function delivering an empty bag on demand. This bag is used to
	 * 		store the elements of MetaDataCursor <tt>input1</tt>.
	 * @param all mode of the difference operation.
	 *		If the mode is true, all tuples of the first relation, which have a
	 *		counterpart in the second relation, are removed.
	 *		If the mode is false, for each tuple of the second relation,
	 *		only one tuple of the first relation is removed at most.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsDifference (ResultSet input1, ResultSet input2, int memSize, int objectSize, Function newBag, boolean all) {
		super(new ResultSetMetaDataCursor(input1), new ResultSetMetaDataCursor(input2), memSize, objectSize, newBag, Equal.DEFAULT_INSTANCE, all);
	}

	/**
	 * Returns the metadata for a SortBasedDifference.
	 *
	 * The metadata is checked for correctness. That means the two relations must
	 * have the same number of columns and each corresponding column must have the same type.
	 * The resulting metadata is the metadata of the first relation.
	 *
	 * @return object of class ResultSetMetaData
	 * @throws xxl.core.util.WrappingRuntimeException if the metadata cannot be accessed
	 */
	public Object getMetaData () {
		try {
			ResultSetMetaData metaData1 = (ResultSetMetaData)((ResultSetMetaDataCursor)input1).getMetaData();
			ResultSetMetaData metaData2 = (ResultSetMetaData)((ResultSetMetaDataCursor)input2).getMetaData();
			if (metaData1.getColumnCount() != metaData2.getColumnCount())
				throw new IllegalArgumentException("ResultSets have a different number of columns.");
			for (int i = 1; i <= metaData1.getColumnCount(); i++) {
				if (!metaData1.getColumnTypeName(i).equals(metaData2.getColumnTypeName(i)))
					throw new IllegalArgumentException("ResultSets have different column types.");
				if (metaData1.getPrecision(i) < metaData2.getPrecision(i))
					System.err.println("Possible loss of precision: Second ResultSet has a higher precision in one column.");
			}
			return metaData1;
		}
		catch (SQLException se) {
			throw new WrappingRuntimeException(se);
		}
	}
}
