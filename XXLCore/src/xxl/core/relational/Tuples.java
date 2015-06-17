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

package xxl.core.relational;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Comparator;

import xxl.core.comparators.ComparableComparator;
import xxl.core.comparators.Comparators;
import xxl.core.comparators.FeatureComparator;
import xxl.core.comparators.InverseComparator;
import xxl.core.comparators.LexicographicalComparator;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.mappers.Mapper;
import xxl.core.functions.Function;
import xxl.core.util.WrappingRuntimeException;

/**
 * This class contains various useful <tt>static</tt> methods for Tuples.
 * 
 * This includes the methods for comparing Tuples, accessing columns,
 * unpacking into an object array and calculating the size.
 *
 * This class cannot be instantiated.
 */
public class Tuples {

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private Tuples () {}

	/**
	 * Get the columns numbers of Strings in a metadata object.
	 *
	 * @param metaData metadata that is used.
	 * @return a new array of the appropriate length
	 */
	public static int[] getStringColumns (ResultSetMetaData metaData) {
		int[] columns = null;
		int count = 0;
		try {
			columns = new int[metaData.getColumnCount()];

			for (int i=0; i<metaData.getColumnCount(); i++)
				if (metaData.getColumnClassName(i+1).equals("java.lang.String"))
					columns[count++]=i+1;
		}
		catch (SQLException e) {}

		// construct a new array of the appropriate length
		return xxl.core.util.Arrays.copy(columns,0,count);
	}

	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	//////////             Comparators Tuples                //////////
	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////

	/**
	 * Returns a comparator that compares two Tuples according to a
	 * subset of column indices.
	 * 
	 * The comparison uses a lexicographical ordering of the column
	 * objects. The objects themselves have to be Comparable. The comparison
	 * can be processed in ascending or descending order.
	 *
	 * @param columns array of column indices: the first column is 1, the second is 2, ...
	 * @param ascending array of boolean that determines the order (ascending=true/descending=false)
	 *	for each dimension.
	 * @param caseInsensiveStringorderingColumns determines string columns that are compared using
	 *	a case insensitive ordering. If a string column is not listed here, the comparison
	 * 	is case sensitive as always. It is convenient to use the method getStringColumns.
	 * @return Comparator that compares two tuples
	 */
	public static Comparator getTupleComparator (int[] columns, boolean[] ascending, int[] caseInsensiveStringorderingColumns) {
		if (columns.length==0)
			throw new IllegalArgumentException("Array has length 0.");
		if (columns.length != ascending.length)
			throw new IllegalArgumentException("Given parameter arrays do not have the same length.");

		java.util.Arrays.sort(caseInsensiveStringorderingColumns);
		Comparator[] comparators = new Comparator[columns.length];
		Comparator c;
		for (int i = 0; i < columns.length; i++) {
			if (java.util.Arrays.binarySearch(caseInsensiveStringorderingColumns,columns[i])<0)
				c = ComparableComparator.DEFAULT_INSTANCE;
			else
				c = String.CASE_INSENSITIVE_ORDER;
			c = Comparators.newNullSensitiveComparator(c,false);
			c = new FeatureComparator(c, getObjectFunction(columns[i]));
			comparators[i] = ascending[i] ? c : new InverseComparator(c);
		}
		if (columns.length>1)
			return new LexicographicalComparator(comparators);
		else
			return comparators[0];
	}

	/**
	 * Returns a comparator that compares two Tuples according to a
	 * subset of column indices.
	 * 
	 * The comparison uses a lexicographical ordering of the column
	 * objects. The objects themselves have to be Comparable. The comparison
	 * can be processed in ascending or descending order.
	 *
	 * @param columns array of column indices: the first column is 1, the second is 2, ...
	 * @param ascending array of boolean that determines the order (ascending=true/descending=false)
	 *	for each dimension.
	 * @return Comparator that compares two tuples
	 */
	public static Comparator getTupleComparator (int[] columns, boolean[] ascending) {
		return getTupleComparator(columns,ascending,new int[]{});
	}

	/**
	 * Returns a comparator that compares two Tuples according to a
	 * subset of column indices.
	 * 
	 * The comparison uses a lexicographical ordering of the column
	 * objects. The objects themselves have to be Comparable.
	 *
	 * @param columns array of column indices: the first column is 1, the second is 2, ...
	 * @return Comparator that compares two tuples
	 */
	public static Comparator getTupleComparator (int[] columns) {
		return getTupleComparator(columns,xxl.core.util.Arrays.newBooleanArray(columns.length,true));
	}

	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	////////// Comparators for two different types of tuples //////////
	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a comparator that compares two Tuples of different type 
	 * according to a subset of column indices.
	 * 
	 * The comparison uses a lexicographical ordering of the column
	 * objects. The objects themselves have to be Comparable. The comparison
	 * can be processed in ascending or descending order.
	 *
	 * @param columns1 array of column indices of the first type of tuple: the first column is 1, the second is 2, ...
	 * @param columns2 array of column indices of the second type of tuple: the first column is 1, the second is 2, ...
	 * @param ascending array of boolean that determines the order (ascending=true/descending=false)
	 *	for each dimension.
	 * @param caseInsensiveStringorderingColumns determines string columns that are compared using
	 *	a case insensitive ordering. These indices have to be chosen according to the first type
	 *	of tuple. If a string column is not listed here, the comparison
	 * 	is case sensitive as always. It is convenient to use the method getStringColumns.
	 * @return Comparator that compares two tuples
	 */
	public static Comparator getTupleComparator (int[] columns1, int[] columns2, boolean[] ascending, int[] caseInsensiveStringorderingColumns) {
		if (columns1.length==0)
			throw new IllegalArgumentException("Array has length 0.");
		if (columns1.length != columns2.length)
			throw new IllegalArgumentException("Given parameter arrays do not have the same length.");
		if (columns1.length != ascending.length)
			throw new IllegalArgumentException("Given parameter arrays do not have the same length.");
			
		java.util.Arrays.sort(caseInsensiveStringorderingColumns);
		Comparator[] comparators = new Comparator[columns1.length];
		Comparator c;
		for (int i = 0; i < columns1.length; i++) {
			if (java.util.Arrays.binarySearch(caseInsensiveStringorderingColumns,columns1[i])<0)
				c = ComparableComparator.DEFAULT_INSTANCE;
			else
				c = String.CASE_INSENSITIVE_ORDER;
			c = Comparators.newNullSensitiveComparator(c,false);
			c = new FeatureComparator(c, getObjectFunction(columns1[i]), getObjectFunction(columns2[i]));
			comparators[i] = ascending[i] ? c : new InverseComparator(c);
		}
		if (columns1.length>1)
			return new LexicographicalComparator(comparators);
		else
			return comparators[0];
	}

	/**
	 * Returns a comparator that compares two Tuples of different type 
	 * according to a subset of column indices.
	 * 
	 * The comparison uses a lexicographical ordering of the column
	 * objects. The objects themselves have to be Comparable. The comparison
	 * can be processed in ascending or descending order.
	 *
	 * @param columns1 array of column indices of the first type of tuple: the first column is 1, the second is 2, ...
	 * @param columns2 array of column indices of the second type of tuple: the first column is 1, the second is 2, ...
	 * @param ascending array of boolean that determines the order (ascending=true/descending=false)
	 *	for each dimension.
	 * @return Comparator that compares two tuples
	 */
	public static Comparator getTupleComparator (int[] columns1, int[] columns2, boolean[] ascending) {
		return getTupleComparator(
			columns1,columns2,ascending, new int[]{}
		);
	}

	/**
	 * Returns a comparator that compares two Tuples of different type 
	 * according to a subset of column indices.
	 * 
	 * The comparison uses a lexicographical ordering of the column
	 * objects. The objects themselves have to be Comparable.
	 *
	 * @param columns1 array of column indices of the first type of tuple: the first column is 1, the second is 2, ...
	 * @param columns2 array of column indices of the second type of tuple: the first column is 1, the second is 2, ...
	 * @return Comparator that compares two tuples
	 */
	public static Comparator getTupleComparator (int[] columns1, int[] columns2) {
		return getTupleComparator(
			columns1,columns2,xxl.core.util.Arrays.newBooleanArray(columns1.length,true)
		);
	}

	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	//////////                  Other stuff                  //////////
	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////

	/**
	 * Returns a function that accesses a column of a tuple parameter.
	 *
	 * The function domains: Tuple --> Object (column)
	 *
	 * @param columnIndex index of the column: the first column is 1, the second is 2, ...
	 * @return Function that accesses the columns
	 */
	public static Function getObjectFunction (final int columnIndex) {
		return new Function () {
			public Object invoke (Object tuple) {
				try {
					return ((Tuple)tuple).getObject(columnIndex);
				}
				catch (IndexOutOfBoundsException e) {
					throw new IllegalStateException("TupleComparator: required column index is invalid: "+e.getMessage());
				}
			}
		};
	}

	/**
	 * Unpacks a tuple into an object array.
	 *
	 * @param tuple of which the size is computed
	 * @return Object[] that contains the columns of the Tuple
	 * @throws xxl.core.util.WrappingRuntimeException if the metadata of the tuple cannot be accessed
	 */
	public static Object[] getObjectArray (Tuple tuple) {
		ResultSetMetaData metaData = tuple.getMetaData();
		try {
			Object[] result = new Object[metaData.getColumnCount()];
			for (int i = 0; i < result.length; i++)
				result[i] = tuple.getObject(i+1);
			return result;
		}
		catch (SQLException se) {
			throw new WrappingRuntimeException(se);
		}
	}

	/**
	 * Returns the size of a tuple in columns.
	 *
	 * @param tuple of which the size is computed
	 * @return size of the tuple in columns
	 * @throws xxl.core.util.WrappingRuntimeException if the metadata of the tuple cannot be accessed
	 */
	public static int getSize (Tuple tuple) {
		ResultSetMetaData metaData = tuple.getMetaData();
		try {
			return metaData.getColumnCount();
		}
		catch (SQLException se) {
			throw new WrappingRuntimeException(se);
		}
	}
	
	/**
	 * Maps the Objects of the input cursor to ArrayTuples.
	 *
	 * @param cursor Input cursor delivering Objects
	 * @param metaData ResultSetMetaData that is the metadata for the output
	 * @return Cursor containing Tuples
	 */
	public static Cursor mapObjectsToTuples(Cursor cursor, final ResultSetMetaData metaData) {
		return new Mapper(
			cursor,
			new Function () {
				public Object invoke (Object[] arguments) {
					return ArrayTuple.FACTORY_METHOD.invoke (arguments, metaData);
				}
			}
		);	
	}
}
