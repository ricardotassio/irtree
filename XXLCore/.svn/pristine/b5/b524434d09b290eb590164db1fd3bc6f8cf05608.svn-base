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
import java.util.ArrayList;

import xxl.core.functions.Function;
import xxl.core.predicates.MetaDataPredicate;
import xxl.core.predicates.Predicate;
import xxl.core.predicates.Predicates;
import xxl.core.relational.metaData.MergedResultSetMetaData;
import xxl.core.util.WrappingRuntimeException;


/**
 * This class contains various <tt>static</tt> methods for
 * managing ResultSetMetaData, creating result Tuples and
 * composing Predicates during join operations.
 *
 * Most of these methods are used internally by the join
 * operation of this package.
 *
 * This class cannot become instantiated.
 *
 * @see xxl.core.relational.cursors.NestedLoopsJoin
 */
public class JoinUtils {

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private JoinUtils () {}

	/**
	 * Computes the metadata that emerge from a natural join operation.
	 *
	 * @param metaData1 metadata of the first join relation
	 * @param metaData2 metadata of the second join relation
	 * @return the metadata that emerge from a natural join operation
	 */
	public static MergedResultSetMetaData getNaturalJoinMetaData (final ResultSetMetaData metaData1, final ResultSetMetaData metaData2) {
		try {
			int[] helper = new int[metaData2.getColumnCount()];
			int counter = metaData1.getColumnCount();
			for (int i = 0; i < metaData2.getColumnCount(); i++) {
				for (int j = 1; j <= metaData1.getColumnCount(); j++)
					if (metaData1.getColumnName(j).equalsIgnoreCase(metaData2.getColumnName(i+1))) {
						helper[i] = j;
						break;
					}
				if(helper[i] == 0) helper[i] = ++counter;
			}// helper[i] == k  bedeutet:  Spalte (i+1) aus resultSet2 findet sich in Spalte k des Joins wieder.
			final int[] columnsFrom2 = new int[counter];
			for (int i = 0; i < metaData2.getColumnCount(); i++)
				columnsFrom2[helper[i]-1] = i+1;
			for (int i = 0; i < metaData1.getColumnCount(); i++)
				if (columnsFrom2[i]!=0) {
					if (!metaData1.getColumnTypeName(i+1).equals(metaData2.getColumnTypeName(columnsFrom2[i])))
						throw new IllegalArgumentException("columns \""+metaData1.getColumnName(i+1)+"\" and \""+metaData2.getColumnName(i+1)+"\" have a different type.");
					if (metaData1.getPrecision(i+1) != metaData2.getPrecision(columnsFrom2[i]))
						throw new IllegalArgumentException("columns \""+metaData1.getColumnName(i+1)+"\" and \""+metaData2.getColumnName(i+1)+"\"have a different precision.");
					if (metaData1.getScale(i+1) != metaData2.getScale(columnsFrom2[i]))
						throw new IllegalArgumentException("columns \""+metaData1.getColumnName(i+1)+"\" and \""+metaData2.getColumnName(i+1)+"\"have a different scale.");
				}
			return new MergedResultSetMetaData(metaData1, metaData2) {

				public int getColumnCount () throws SQLException {
					return columnsFrom2.length;
				}

				public int originalMetaData (int column) throws SQLException {
					if (column < 1 || column > columnsFrom2.length)
						throw new SQLException("Invalid column index.");
					return column > this.metaData1.getColumnCount() ? 2 : columnsFrom2[column-1] == 0 ? 1 : 0;
				}

				public int originalColumnIndexFrom1 (int column) {
					return column;
				}

				public int originalColumnIndexFrom2 (int column) {
					return columnsFrom2[column-1];
				}

                public <T> T unwrap(Class<T> iface) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public boolean isWrapperFor(Class<?> iface) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
			};
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Computes the metadata that emerge from a theta join operation.
	 * The metadata from both input relations is merged.
	 *
	 * @param metaData1 metadata of the first join relation
	 * @param metaData2 metadata of the second join relation
	 * @return the metadata that emerge from a theta join operation
	 */
	public static MergedResultSetMetaData getThetaJoinMetaData (final ResultSetMetaData metaData1, final ResultSetMetaData metaData2) {
		try {
			for (int i = 1; i <= metaData1.getColumnCount(); i++)
				for (int j = 1; j <= metaData2.getColumnCount(); j++)
					if (metaData1.getColumnName(i).equalsIgnoreCase(metaData2.getColumnName(j)))
						throw new IllegalArgumentException("Metadata information cannot be created, because column "+i+" from metaData1 and column "+j+" from metaData2 have the same column name.");

			return new MergedResultSetMetaData(metaData1, metaData2) {

				public int getColumnCount () throws SQLException {
					return this.metaData1.getColumnCount() + this.metaData2.getColumnCount();
				}

				public int originalMetaData (int column) throws SQLException {
					if (column < 1 || column > getColumnCount())
						throw new SQLException("Invalid column index.");
					return column > this.metaData1.getColumnCount() ? 2 : 1;
				}

				public int originalColumnIndexFrom1 (int column) {
					try {
						if (column > this.metaData1.getColumnCount())
							throw new SQLException("Invalid column index.");
						return column;
					}
					catch (SQLException e) {
						throw new WrappingRuntimeException(e);
					}
				}

				public int originalColumnIndexFrom2 (int column) {
					try {
						if (column < this.metaData1.getColumnCount())
							throw new SQLException("Invalid column index.");
						return column - this.metaData1.getColumnCount();
					}
					catch (SQLException e) {
						throw new WrappingRuntimeException(e);
					}
				}

                public <T> T unwrap(Class<T> iface) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public boolean isWrapperFor(Class<?> iface) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
			};
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Returns a function that builds up a result tuple after
	 * performing a natural join. To compute the resulting relation,
	 * this function has to be called for every pair of tuples
	 * that qualify for the result.
	 *
	 * @param createTuple Function to create a tuple. The FACTORY_METHOD of
	 * 	{@link ArrayTuple ArrayTuple} or {@link ListTuple ListTuple} can
	 *	be used.
	 * @param metaData metadata of the resulting relation.
	 * @return a function that builds up a result tuple after
	 * performing a natural join.
	 * @throws xxl.core.util.WrappingRuntimeException if the metadata cannot be accessed
	 */
	public static Function naturalJoinTuple (final Function createTuple, final MergedResultSetMetaData metaData) {
		return new Function () {
			public Object invoke (Object tuple1, Object tuple2) {
				try {
					Object[] result = new Object[metaData.getColumnCount()];
					if (tuple1 != null && tuple2 == null)
						for (int i = 0; i < result.length; i++)
							result[i] = metaData.originalMetaData(i+1) != 2 ? ((Tuple)tuple1).getObject(i+1) : null;
					if (tuple1 == null && tuple2 != null)
						for (int i = 0; i < result.length; i++)
							result[i] = metaData.originalMetaData(i+1) == 1 ? null : ((Tuple)tuple2).getObject(metaData.originalColumnIndexFrom2(i+1));
					if (tuple1 != null && tuple2 != null)
						for (int i = 0; i < result.length; i++)
							result[i] = metaData.originalMetaData(i+1) != 2 ? ((Tuple)tuple1).getObject(i+1) : ((Tuple)tuple2).getObject(metaData.originalColumnIndexFrom2(i+1));
					return createTuple.invoke(result, metaData);
				}
				catch (SQLException e) {
					throw new WrappingRuntimeException(e);
				}
			}
		};
	}

	/**
	 * Returns a function that builds up a result tuple after
	 * performing a semi join. To compute the resulting relation,
	 * this function has to be called for every pair of tuples
	 * that qualify for the result.
	 *
	 * @param createTuple Function to create a tuple. The FACTORY_METHOD of
	 * 	{@link ArrayTuple ArrayTuple} or {@link ListTuple ListTuple} can
	 *	be used.
	 * @param metaData metadata of the resulting relation.
	 * @return a function that builds up a result tuple after
	 * performing a semi join.
	 * @throws xxl.core.util.WrappingRuntimeException if the metadata cannot be accessed
	 */
	public static Function semiJoinTuple (final Function createTuple, final ResultSetMetaData metaData) {
		return new Function () {
			public Object invoke (Object tuple1, Object tuple2) {
				try {
					Object[] result = new Object[metaData.getColumnCount()];
					for (int i = 0; i < result.length; i++)
						result[i] = ((Tuple)tuple1).getObject(i+1);
					return createTuple.invoke(result, metaData);
				}
				catch (SQLException e) {
					throw new WrappingRuntimeException(e);
				}
			}
		};
	}

	/**
	 * Returns a Predicate that compares tuples of the two join relations.
	 *
	 * This predicate can be passed to the join routines of the
	 * cursor package. So far, this method is used only by methods declared
	 * in this class.
	 *
	 * @param metaData the metadata
	 * @return a Predicate that compares tuples of the two join relations.
	 */
	public static Predicate naturalJoinPredicate (final MergedResultSetMetaData metaData) {
		return new Predicate() {
			protected int[] joinColumns = null;

			public boolean invoke (Object tuple1, Object tuple2) {
				try {
					Predicate equals = Predicates.newNullSensitiveEqual(true);
					if (joinColumns == null) {
						ArrayList tmp = new ArrayList(1);
						for (int i = 1; i <= metaData.getMetaData(1).getColumnCount(); i++)
							if (metaData.originalColumnIndexFrom2(i) != 0)
								tmp.add(new Integer(i));
						joinColumns = new int[tmp.size()];
						for (int i = 0; i < tmp.size(); i++)
							joinColumns[i] = ((Integer)tmp.get(i)).intValue();
					}
					for (int i = 0; i < joinColumns.length; i++)
						if (!equals.invoke(((Tuple)tuple1).getObject(joinColumns[i]), ((Tuple)tuple2).getObject(metaData.originalColumnIndexFrom2(joinColumns[i]))))
							return false;
					return true;
				}
				catch (SQLException e) {
					throw new WrappingRuntimeException(e);
				}
			}
		};
	}

	/**
	 * Returns a MetaDataPredicate for a natural join.
	 *
	 * Consequently, a predicate and the metadata is computed.
	 *
	 * This class is used in the {@link xxl.core.relational.cursors.NestedLoopsJoin NestedLoopsJoin}.
	 * At this place, an object of this class is passed to the join routines
	 * of the cursor package.
	 *
	 * @param metaData1 metadata of the first join relation
	 * @param metaData2 metadata of the second join relation
	 * @return a MetaDataPredicate for a natural join.
	 */
	public static MetaDataPredicate naturalJoin (final ResultSetMetaData metaData1, final ResultSetMetaData metaData2) {
		final MergedResultSetMetaData metaData = getNaturalJoinMetaData(metaData1, metaData2);
		return new MetaDataPredicate(naturalJoinPredicate(metaData)) {
			public Object getMetaData () {
				return getNaturalJoinMetaData(metaData1, metaData2);
			}
		};
	}

	/**
	 * Returns a MetaDataPredicate for a semi join.
	 *
	 * Consequently, a predicate and the metadata is computed.
	 *
	 * This class is used in the {@link xxl.core.relational.cursors.NestedLoopsJoin NestedLoopsJoin}.
	 * At this place, an object of this class is passed to the join routines
	 * of the cursor package.
	 *
	 * @param metaData1 metadata of the first join relation
	 * @param metaData2 metadata of the second join relation
	 * @return a MetaDataPredicate for a semi join.
	 */
	public static MetaDataPredicate semiJoin (final ResultSetMetaData metaData1, final ResultSetMetaData metaData2) {
		final MergedResultSetMetaData metaData = getNaturalJoinMetaData(metaData1, metaData2);
		return new MetaDataPredicate(naturalJoinPredicate(metaData)) {
			public Object getMetaData () {
				return metaData1;
			}
		};
	}

	/**
	 * Returns a MetaDataPredicate for a theta join.
	 *
	 * Consequently, a predicate and the metadata is computed.
	 *
	 * This class is used in the {@link xxl.core.relational.cursors.NestedLoopsJoin NestedLoopsJoin}.
	 * At this place, an object of this class is passed to the join routines
	 * of the cursor package.
	 *
	 * @param theta Predicate that compares two Tuples of the input relations
	 * @param metaData1 metadata of the first join relation
	 * @param metaData2 metadata of the second join relation
	 * @return a MetaDataPredicate for a theta join.
	 */
	public static MetaDataPredicate thetaJoin (Predicate theta, final ResultSetMetaData metaData1, final ResultSetMetaData metaData2) {
		return new MetaDataPredicate (theta) {
			public Object getMetaData () {
				return getThetaJoinMetaData(metaData1, metaData2);
			}
		};
	}
}
