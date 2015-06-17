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
import xxl.core.cursors.unions.Sequentializer;
import xxl.core.functions.Function;
import xxl.core.relational.metaData.MergedResultSetMetaData;
import xxl.core.util.WrappingRuntimeException;

/**
 * Union is a straight forward implementation of the operator union. 
 * It is based on {@link xxl.core.cursors.unions.Sequentializer}. The elements
 * of both input relations become appended. The metadata of both
 * inputs has to be compatible.
 * <p>
 * This operator is also applicable if the MetaDataCursor 
 * contains Objects that are not Tuples.
 * <p>
 * The example in the main method wraps two Enumerator cursors 
 * (integers 0 to 9 and integers 10 to 19) to MetaDataCursors 
 * using xxl.core.cursors.Cursor.wrapToMetaDataCursor. 
 * Then, the union operator is applied.
 * The interesting call is: 
 *
 * <code><pre>
 *		MetaDataCursor cursor = new Union(cursor1, cursor2);
 * </pre></code>
 */
public class Union extends Sequentializer implements MetaDataCursor {

	/** Metadata associated with the output */
	ResultSetMetaData metaData;

	/**
	 * Constructs an instance of the Union operator.
	 *
	 * @param cursor1 the first MetaDataCursor containing elements
	 * @param cursor2 the second MetaDataCursor containing elements
	 */
	public Union (MetaDataCursor cursor1, MetaDataCursor cursor2) {
		super(cursor1, cursor2);
		try {
			final ResultSetMetaData metaData1 = (ResultSetMetaData)cursor1.getMetaData();
			ResultSetMetaData metaData2 = (ResultSetMetaData)cursor2.getMetaData();

			if (metaData1.getColumnCount() != metaData2.getColumnCount())
				throw new IllegalArgumentException("ResultSets have different number of columns.");
			for (int i = 1; i <= metaData1.getColumnCount(); i++)
				if (!metaData1.getColumnName(i).equalsIgnoreCase(metaData2.getColumnName(i)) ||
					!metaData1.getColumnTypeName(i).equals(metaData2.getColumnTypeName(i)))
						throw new IllegalArgumentException("ResultSets have different column names or types.");

			metaData = new MergedResultSetMetaData(metaData1, metaData2) {

				public int getColumnCount() throws SQLException {
					return this.metaData1.getColumnCount();
				}

				public int originalMetaData(int column) {
					return 0;
				}

				public int originalColumnIndexFrom1(int column) {
					return column;
				}

				public int originalColumnIndexFrom2(int column) {
					return column;
				}

                public <T> T unwrap(Class<T> iface) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public boolean isWrapperFor(Class<?> iface) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
			};
		}
		catch(SQLException se) {
			throw new WrappingRuntimeException(se);
		}
	}

	/**
	 * Constructs an instance of the Union operator.
	 *
	 * @param resultSet1 the first input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param resultSet2 the second input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param createInputTuple Function that maps a (row of the) ResultSet to a Tuple. 
	 *	The function gets a ResultSet and maps the current row to a Tuple. 
	 *	If null is passed, the factory method of ArrayTuple is used.
	 *	It is forbidden to call the next, update and similar methods of the ResultSet
	 *	from inside the function!
	 */
	public Union (ResultSet resultSet1, ResultSet resultSet2, Function createInputTuple ) {
		this(new ResultSetMetaDataCursor(resultSet1, createInputTuple), new ResultSetMetaDataCursor(resultSet2, createInputTuple));
	}

	/**
	 * Returns the resulting metadata for this operator.
	 *
	 * The metadata of both inputs are wrapped into a MergedResultSetMetaData object.
	 *
	 * @return object of class ResultSetMetaData
	 */
	public Object getMetaData () {
		return metaData;
	}
	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 * 
     * @param args the arguments
	 */
	public static void main(String[] args){

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		/* 
			Wraps two Enumerator cursors (integers 0 to 9 and integers 10 to 19)
			to MetaDataCursors using {@link xxl.core.cursors.Cursors#wrapToMetaDataCursor}. 
			Then, the union operator is applied.
		*/
		System.out.println("Example 1: Generating a sample of approximately 10% of size");
		ResultSetMetaData metaData = (ResultSetMetaData) xxl.core.relational.metaData.MetaData.NUMBER_META_DATA_FACTORY_METHOD.invoke();
		
		MetaDataCursor cursor1 = xxl.core.cursors.Cursors.wrapToMetaDataCursor(
			new xxl.core.cursors.sources.Enumerator(0,10),
			 metaData
		);
		
		MetaDataCursor cursor2 = xxl.core.cursors.Cursors.wrapToMetaDataCursor(
			new xxl.core.cursors.sources.Enumerator(10,20),
			 metaData
		);

		MetaDataCursor cursor = new Union(cursor1, cursor2);
		
		xxl.core.cursors.Cursors.println(cursor);
	}

}