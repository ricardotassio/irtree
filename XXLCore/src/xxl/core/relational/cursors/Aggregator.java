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
import java.sql.Types;

import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.functions.MetaDataFunction;
import xxl.core.math.statistics.parametric.aggregates.Average;
import xxl.core.math.statistics.parametric.aggregates.Count;
import xxl.core.math.statistics.parametric.aggregates.CountAll;
import xxl.core.math.statistics.parametric.aggregates.Maximum;
import xxl.core.math.statistics.parametric.aggregates.Minimum;
import xxl.core.math.statistics.parametric.aggregates.StandardDeviation;
import xxl.core.math.statistics.parametric.aggregates.Sum;
import xxl.core.math.statistics.parametric.aggregates.Variance;
import xxl.core.relational.Tuple;
import xxl.core.relational.metaData.AssembledResultSetMetaData;
import xxl.core.relational.metaData.MergedResultSetMetaData;
import xxl.core.relational.metaData.MetaData;
import xxl.core.util.WrappingRuntimeException;

/**
 * The aggregator computes aggregates like SUM, COUNT and VARIANCE for an
 * input MetaDataCursor.
 * This aggregator is based on the implementation of {@link xxl.core.cursors.mappers.Aggregator}.
 * <p>
 * Some standard aggregates are defined in this class, but new
 * functionality can simply be included by using your own
 * {@link MetaDataFunction}.
 *
 * Important: an Aggregator executes no group- or sort-operations! Normally,
 * a {@link SortBasedGrouper} or {@link xxl.core.cursors.groupers.SortBasedGrouper}
 * is applied before the aggregator is used.
 */
public class Aggregator extends xxl.core.cursors.mappers.Aggregator implements MetaDataCursor {

	/**
	 * Metadata of the output.
	 */
	protected ResultSetMetaData metaData = null;

	/**
	 * MetaDataFunction that can be used as aggregate function to compute the count-aggregate.
	 * The column name is set to <code>COUNT</code>. Precision is 20.
	 */
	public static final MetaDataFunction COUNT = new MetaDataFunction(new Count()) {

		public Object getMetaData () {
			return new MetaData (Types.BIGINT,"COUNT",20,0);
		}
	};

	/**
	 * MetaDataFunction that can be used as aggregate function to compute the countAll-aggregate.
	 * The column name is set to <code>COUNT_ALL</code>. Precision is 20.
	 */
	public static final MetaDataFunction COUNT_ALL = new MetaDataFunction(new CountAll()) {

		public Object getMetaData () {
			return new MetaData (Types.BIGINT,"COUNT_ALL",20,0);
		}
	};

	/**
	 * MetaDataFunction that can be used as aggregate function to compute the sum-aggregate.
	 * The column name is set to <code>SUM</code>. Precision is 52.
	 */
	public static final MetaDataFunction SUM = new MetaDataFunction(new Sum()) {

		public Object getMetaData () {
			return new MetaData (Types.DOUBLE,"SUM",52,0);
		}

	};

	/**
	 * MetaDataFunction that can be used as aggregate function to compute the min-aggregate.
	 * The column name is set to <code>MIN</code>. Precision is 52.
	 */
	public static final MetaDataFunction MIN = new MetaDataFunction(new Minimum()) {

		public Object getMetaData () {
			return new MetaData (Types.NUMERIC,"MIN",52,0);
		}
	};

	/**
	 * MetaDataFunction that can be used as aggregate function to compute the max-aggregate.
	 * The column name is set to <code>MAX</code>. Precision is 52.
	 */
	public static final MetaDataFunction MAX = new MetaDataFunction(new Maximum()) {

		public Object getMetaData () {
			return new MetaData (Types.NUMERIC,"MAX",52,0);
		}
	};

	/**
	 * MetaDataFunction that can be used as aggregate function to compute the average-aggregate.
	 * The column name is set to <code>AVG</code>. Precision is 52.
	 */
	public static final MetaDataFunction AVG = new MetaDataFunction(new Average()) {

		public Object getMetaData () {
			return new MetaData (Types.DOUBLE,"AVG",52,0);
		}
	};

	/**
	 * MetaDataFunction that can be used as aggregate function to compute the standard-deviation-aggregate.
	 * The column name is set to <code>STDDEV</code>. Precision is 52.
	 */
	public static final MetaDataFunction STDDEV = new MetaDataFunction(new StandardDeviation()) {

		public Object getMetaData () {
			return new MetaData (Types.DOUBLE,"STDDEV",52,0);
		}
	};

	/**
	 * MetaDataFunction that can be used as aggregate function to compute the variance-aggregate.
	 * The column name is set to <code>VAR</code>. Precision is 52.
	 */
	public static final MetaDataFunction Variance = new MetaDataFunction(new Variance()) {

		public Object getMetaData () {
			return new MetaData (Types.DOUBLE,"VAR",52,0);
		}
	};

	/**
	 * Creates a new aggregator.
	 *
	 * @param cursor the input MetaDataCursor delivering the input element
	 * @param aggregates Array of MetaDataFunctions that each compute an aggregate on the input data.
	 * @param columnIndices Column indices determining which aggregate function is applied on which
	 *	column. This array must have the same length than aggregates.
	 * @param newColumnNames Column names of the aggregate columns. This array must have the same 
	 *	length than aggregates. If an entry equals null, the name determined by the aggregate
	 *	function is taken.
	 * @param projectedColumns Columns to which the input MetaDataCursor is projected.
	 * @param createOutputTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 */
	public Aggregator (MetaDataCursor cursor, final MetaDataFunction[] aggregates, final int[] columnIndices, final String[] newColumnNames, final int[] projectedColumns, final Function createOutputTuple) {
		super(Cursors.wrap(cursor), aggregates);
		final ResultSetMetaData[] aggregateMetaDatas = new ResultSetMetaData[aggregates.length];
		ResultSetMetaData originalMetaData;
		ResultSetMetaData projectedMetaData;
		try {
			originalMetaData = (ResultSetMetaData)cursor.getMetaData();
			projectedMetaData = (ResultSetMetaData)new Projection(cursor, new xxl.core.functions.Projection(projectedColumns),null).getMetaData();
			if (aggregates.length != columnIndices.length)
				throw new IllegalArgumentException("Different number of of aggregate functions and columnIndices.");
			for (int i = 0; i < columnIndices.length; i++) {
				if (columnIndices[i] < 1 || columnIndices[i] > originalMetaData.getColumnCount())
					throw new IllegalArgumentException("Invalid column index: " + columnIndices[i]);
				if (aggregates[i] == null)
					throw new IllegalArgumentException("Aggregate function is null.");
			}
			for (int i = 0; i < aggregates.length; i++)
				aggregateMetaDatas[i] = (ResultSetMetaData)aggregates[i].getMetaData();
			for (int i = 0; i < originalMetaData.getColumnCount(); i++)
				for (int j = 0; j < aggregates.length; j++)
					if (originalMetaData.getColumnName(i+1).equals(aggregateMetaDatas[j].getColumnName(1)))
						throw new IllegalArgumentException("Column "+(i+1)+" and the new aggregate's column "+aggregateMetaDatas[j].getColumnName(1)+" have the same name.");
			for (int i = 0; i < newColumnNames.length; i++)
				if (newColumnNames[i] == null)
					newColumnNames[i] = ((ResultSetMetaData)aggregates[i].getMetaData()).getColumnName(columnIndices[i]);
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
		this.metaData = new MergedResultSetMetaData(projectedMetaData, new AssembledResultSetMetaData(aggregateMetaDatas, newColumnNames)) {

			public int getColumnCount() throws SQLException {
				return projectedColumns.length + aggregateMetaDatas.length;
			}

			public int originalMetaData(int column) throws SQLException {
				if (column < 1 || column > projectedColumns.length + aggregateMetaDatas.length)
					throw new SQLException("Invalid column index.");
				return (column <= projectedColumns.length) ? 1 : 2;
			}

			public int originalColumnIndexFrom1(int column) throws SQLException {
				return column;
			}

			public int originalColumnIndexFrom2(int column) throws SQLException {
				return column - projectedColumns.length;
			}

            public <T> T unwrap(Class<T> iface) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
            }
		};
		if(super.input.peek() instanceof Cursor) {
			function = new Function () { // cursor function

				public Object invoke (Object aggregate, Object cursor) {
					boolean[] initialized = new boolean[aggregates.length];
					Object[] result = new Object[projectedColumns.length + aggregates.length];
					while(((Cursor)cursor).hasNext()) {
						Object tuple = ((Cursor)cursor).next();
						for (int i = 0; i < projectedColumns.length; i++)
							result[i] = ((Tuple)tuple).getObject(projectedColumns[i]);
						for (int i = projectedColumns.length; i < result.length; i++) {
							Object agg = initialized[i-projectedColumns.length] ? ((Tuple)aggregate).getObject(i+1) : null;
							result[i] = aggregates[i-projectedColumns.length].invoke(agg,
								((Tuple)tuple).getObject(columnIndices[i-projectedColumns.length]));
							initialized[i-projectedColumns.length] = result[i] != null;
						}
						aggregate = createOutputTuple.invoke(result, metaData);
					}
					return aggregate;
				}
			};
		}
		else { // tuple function
			function = new Function () {

				protected boolean[] initialized;

				public Object invoke (Object aggregate, Object tuple) {
					if (initialized == null) initialized = new boolean[aggregates.length];
					Object[] result = new Object[projectedColumns.length + aggregates.length];
					for (int i = 0; i < projectedColumns.length; i++)
						result[i] = ((Tuple)tuple).getObject(projectedColumns[i]);
					for (int i = projectedColumns.length; i < result.length; i++) {
						Object agg = initialized[i-projectedColumns.length] ? ((Tuple)aggregate).getObject(i+1) : null;
						result[i] = aggregates[i-projectedColumns.length].invoke(agg,
							((Tuple)tuple).getObject(columnIndices[i-projectedColumns.length]));
						initialized[i-projectedColumns.length] = result[i] != null;
					}
					aggregate = createOutputTuple.invoke(result, metaData);
					return aggregate;
				}
			};
		}
	}

	/**
	 * Creates a new aggregator. The column names of the aggregates are determined by the aggregate functions.
	 *
	 * @param cursor the input MetaDataCursor delivering the input element
	 * @param aggregates Array of MetaDataFunctions that each compute an aggregate on the input data.
	 * @param columnIndices Column indices determining which aggregate function is applied on which
	 *	column. This array must have the same length than aggregates.
	 * @param projectedColumns Columns to which the input MetaDataCursor is projected.
	 * @param createOutputTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 */
	public Aggregator (MetaDataCursor cursor, MetaDataFunction[] aggregates, int[] columnIndices, int[] projectedColumns, Function createOutputTuple) {
		this(cursor, aggregates, columnIndices, new String[columnIndices.length], projectedColumns, createOutputTuple);
	}

	/**
	 * Creates a new aggregator.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param aggregates Array of MetaDataFunctions that each compute an aggregate on the input data.
	 * @param columnIndices Column indices determining which aggregate function is applied on which
	 *	column. This array must have the same length than aggregates.
	 * @param newColumnNames Column names of the aggregate columns. This array must have the same 
	 *	length than aggregates. If an entry equals null, the name determined by the aggregate
	 *	function is taken.
	 * @param projectedColumns Columns to which the input MetaDataCursor is projected.
	 * @param createOutputTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 */
	public Aggregator (ResultSet resultSet, MetaDataFunction[] aggregates, int[] columnIndices, String[] newColumnNames, int[] projectedColumns, Function createOutputTuple) {
		this(new ResultSetMetaDataCursor(resultSet), aggregates, columnIndices, newColumnNames, projectedColumns, createOutputTuple);
 	}

	/**
	 * Creates a new aggregator. The column names of the aggregates are determined by the aggregate functions.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param aggregates Array of MetaDataFunctions that each compute an aggregate on the input data.
	 * @param columnIndices Column indices determining which aggregate function is applied on which
	 *	column. This array must have the same length than aggregates.
	 * @param projectedColumns Columns to which the input MetaDataCursor is projected.
	 * @param createOutputTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 */
	public Aggregator (ResultSet resultSet, MetaDataFunction[] aggregates, int[] columnIndices, int[] projectedColumns, Function createOutputTuple) {
		this(resultSet, aggregates, columnIndices, new String[columnIndices.length], projectedColumns, createOutputTuple);
	}

	/**
	 * Returns the resulting metadata for this operator.
	 *
	 * The metadata of both inputs is wrapped into a MergedResultSetMetaData object.
	 *
	 * @return object of class ResultSetMetaData
	 */
	public Object getMetaData () {
		return metaData;
	}
}
