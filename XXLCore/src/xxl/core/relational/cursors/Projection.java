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
import java.util.NoSuchElementException;

import xxl.core.cursors.SecureDecoratorCursor;
import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.relational.ArrayTuple;
import xxl.core.relational.ListTuple;
import xxl.core.relational.Tuple;
import xxl.core.relational.Tuples;
import xxl.core.relational.metaData.WrappedResultSetMetaData;
import xxl.core.util.WrappingRuntimeException;

/**
 * Straight forward implementation of the operator projection (renaming 
 * of columns included).
 * <p>
 * Logically, the renaming is done before the projection!
 * <p>
 * In earlier versions of XXL it was possible to hand over a string array
 * to the constructor of the operators instead of an array of indices. 
 * To get this functionality, use ResultSets.getColumnIndices(resultSet, onColumns).
 * <p>
 * The example in the main method wraps an Enumerator cursor (integers 0 to 9)
 * to a MetaDataCursor using xxl.core.cursors.Cursors.wrapToMetaDataCursor. 
 * Then, the column becomes renamed to "NewName" and the cursor is printed
 * on System.out. The interesting call is: 
 *
 * <code><pre>
 *	cursor = new Projection(cursor,
 *		new int[] {1},
 *		ArrayTuple.FACTORY_METHOD,
 *		new int[] {1},
 *		new String[] {"NewName"}
 *	);
 * </pre></code>
 */
public class Projection extends SecureDecoratorCursor implements MetaDataCursor {

	/** Projection */
	protected xxl.core.functions.Projection projection;
	/** Function that maps an object array and a ResultSetMetaData object to a result Tuple */
	protected Function createResTuple;
	/** New Names of the columns */
	protected String[] renamedColumnNames = null;
	/** MetaData of the projected cursor */
	ResultSetMetaData metaData;
	
	/**
	 * Creates a new instance of Projection.
	 *
	 * @param cursor the input MetaDataCursor delivering the input element
	 * @param projection an function of type Projection that maps an object array to an object array
	 * @param createResTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. xxl.core.relational.ArrayTuple.FACTORY_METHOD
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, xxl.core.relational.ArrayTuple.FACTORY_METHOD is used.
	 */
	public Projection (MetaDataCursor cursor, xxl.core.functions.Projection projection, Function createResTuple) {
		super(cursor);
		int[] indices = projection.getIndices();
		int[] decrementedIndices = new int[indices.length];
		for (int i = 0; i < indices.length; i++)
			decrementedIndices[i] = indices[i]-1;
		this.projection = new xxl.core.functions.Projection(decrementedIndices);
		if (createResTuple==null)
			this.createResTuple = cursor.supportsPeek() ? 
				cursor.peek() instanceof ArrayTuple ? ArrayTuple.FACTORY_METHOD : ListTuple.FACTORY_METHOD 
			: ArrayTuple.FACTORY_METHOD;
		else
			this.createResTuple = createResTuple;
	}
	
	/**
	 * Creates a new instance of Projection.
	 *
	 * @param cursor the input MetaDataCursor delivering the input element. 
	 * @param columnIndices array of indices of the projection: the first column is 1, the second is 2, ...
	 * @param createResTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. xxl.core.relational.ArrayTuple.FACTORY_METHOD
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, xxl.core.relational.ArrayTuple.FACTORY_METHOD is used.
	 */
	public Projection (MetaDataCursor cursor, int[] columnIndices, Function createResTuple) {
		this(cursor, new xxl.core.functions.Projection(columnIndices), createResTuple);
	}

	/**
	 * Creates a new instance of Projection. If the type of tuple (ArrayTuple or ListTuple) can be determined
	 * (if the cursor supports the peek method) the appropriate createTuple-Factory-Method is chosen
	 * automatically. If it cannot be determined, ArrayTuple.FACTORY_METHOD is taken.
	 *
	 * @param cursor the input MetaDataCursor delivering the input element
	 * @param projection an function of type Projection that maps an object array to an object array
	 * @param createResTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. xxl.core.relational.ArrayTuple.FACTORY_METHOD
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, xxl.core.relational.ArrayTuple.FACTORY_METHOD is used.
	 * @param columnsToRename array of column numbers that become renamed (column numbers before the projection!)
	 * @param newColumnNames array of the new column names (same length than columnsToRename)
	 */
	public Projection (MetaDataCursor cursor, xxl.core.functions.Projection projection, Function createResTuple, int[] columnsToRename, String[] newColumnNames) {
		this(cursor, projection, createResTuple);
		renamedColumnNames = renameColumns(columnsToRename, newColumnNames, (ResultSetMetaData)cursor.getMetaData());
	}

	/**
	 * Creates a new instance of Projection. If the type of tuple (ArrayTuple or ListTuple) can be determined
	 * (if the cursor supports the peek method) the appropriate createTuple-Factory-Method is chosen
	 * automatically. If it cannot be determined, ArrayTuple.FACTORY_METHOD is taken.
	 *
	 * @param cursor the input MetaDataCursor delivering the input element
	 * @param columnIndices array of indices of the projection: the first column is 1, the second is 2, ...
	 * @param createResTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. xxl.core.relational.ArrayTuple.FACTORY_METHOD
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, xxl.core.relational.ArrayTuple.FACTORY_METHOD is used.
	 * @param columnsToRename array of column numbers that become renamed (column numbers before the projection!)
	 * @param newColumnNames array of the new column names (same length than columnsToRename)
	 */
	public Projection (MetaDataCursor cursor, int[] columnIndices, Function createResTuple, int[] columnsToRename, String[] newColumnNames) {
		this(cursor, new xxl.core.functions.Projection(columnIndices), createResTuple, columnsToRename, newColumnNames);
	}

	/**
	 * Creates a new instance of Projection.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param projection an function of type Projection that maps an object array to an object array
	 * @param createResTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. xxl.core.relational.ArrayTuple.FACTORY_METHOD
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, xxl.core.relational.ArrayTuple.FACTORY_METHOD is used.
	 */
	public Projection (ResultSet resultSet, xxl.core.functions.Projection projection, Function createResTuple) {
		this(new ResultSetMetaDataCursor(resultSet), projection, createResTuple);
	}

	/**
	 * Creates a new instance of Projection.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param columnIndices array of indices of the projection: the first column is 1, the second is 2, ...
	 * @param createResTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. xxl.core.relational.ArrayTuple.FACTORY_METHOD
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, xxl.core.relational.ArrayTuple.FACTORY_METHOD is used.
	 */
	public Projection (ResultSet resultSet, int[] columnIndices, Function createResTuple) {
		this(resultSet, new xxl.core.functions.Projection(columnIndices), createResTuple);
	}

	/**
	 * Creates a new instance of Projection.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param projection an function of type Projection that maps an object array to an object array
	 * @param createResTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. xxl.core.relational.ArrayTuple.FACTORY_METHOD
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, xxl.core.relational.ArrayTuple.FACTORY_METHOD is used.
	 * @param columnsToRename array of column numbers that become renamed (column numbers before the projection!)
	 * @param newColumnNames array of the new column names (same length than columnsToRename)
	 */
	public Projection (ResultSet resultSet, xxl.core.functions.Projection projection, Function createResTuple, int[] columnsToRename, String[] newColumnNames) {
		this(new ResultSetMetaDataCursor(resultSet), projection, createResTuple, columnsToRename, newColumnNames);
	}

	/**
	 * Creates a new instance of Projection.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param columnIndices array of indices of the projection: the first column is 1, the second is 2, ...
	 * @param createResTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. xxl.core.relational.ArrayTuple.FACTORY_METHOD
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, xxl.core.relational.ArrayTuple.FACTORY_METHOD is used.
	 * @param columnsToRename array of column numbers that become renamed (column numbers before the projection!)
	 * @param newColumnNames array of the new column names (same length than columnsToRename)
	 */
	public Projection (ResultSet resultSet, int[] columnIndices, Function createResTuple, int[] columnsToRename, String[] newColumnNames) {
		this(resultSet, new xxl.core.functions.Projection(columnIndices), createResTuple, columnsToRename, newColumnNames);
	}

	/**
	 * Delivers an array of the column names after the columns became renamed.
	 *
	 * @param columnsToRename array of column numbers that become renamed (column numbers before projection!)
	 * @param newColumnNames array of the new column names (same length than columnsToRename)
	 * @param metaData metadata after the projection
	 * @return new column names after the projection
	 * @throws IllegalArgumentException if the arrays do not have the same length, the column name
	 *	array contains duplicates or invalid column index has been passed
	 * @throws xxl.core.util.WrappingRuntimeException if an SQLException has happened while accessing 
	 *	the metadata
	 */
	protected String[] renameColumns (int[] columnsToRename, String[] newColumnNames, ResultSetMetaData metaData) {
		String[] columnNames = new String[projection.getIndices().length];
		try {
			if (columnsToRename.length != newColumnNames.length)
				throw new IllegalArgumentException("Renaming arrays do not have the same length.");
			for (int i = 0; i < columnsToRename.length; i++)
				for (int j = 0; j < projection.getIndices().length; j++) {
					if (columnsToRename[i] == (projection.getIndices()[j] + 1)) {
						columnNames[j] = newColumnNames[i];
						break;
					}
				}
			for (int i = 0; i < columnNames.length; i++)
				if (columnNames[i] == null)
					columnNames[i] = metaData.getColumnName(projection.getIndices()[i] + 1);
			for (int i = 0; i < columnNames.length; i++)
				for (int j = 0; j < columnNames.length; j++)
					if (j != i)
						if (columnNames[i].equalsIgnoreCase(columnNames[j]))
							throw new IllegalArgumentException("A column name has been duplicated.");
		}
		catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid column for renaming.");
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
		return columnNames;
	}

	/**
	 * Shows the next element in the iteration without removing it from the underlying
	 * collection.
	 * (optional operation) <br>
	 *
	 * @return the next element in the iteration.
	 * @throws java.util.NoSuchElementException iteration has no more elements.
	 * @throws java.lang.UnsupportedOperationException if the <tt>peek</tt> operation is
	 * 		not supported by the input cursor.
	 */
	public Object peek () throws NoSuchElementException {
		return createResTuple.invoke(projection.invoke(Tuples.getObjectArray((Tuple)super.peek())), getMetaData());
	}

	/**
	 * Returns the next element in the iteration. <br>
	 * This element will be removed from the underlying collection, if
	 * <tt>next</tt> is called. <br>
	 *
	 * @return the next element in the iteration.
	 * @throws java.util.NoSuchElementException if the iteration has no more elements.
	 */
	public Object next () throws NoSuchElementException {
		return createResTuple.invoke(projection.invoke(Tuples.getObjectArray((Tuple)super.next())), getMetaData());
	}

	/** 
	 * Returns the metadata of the projection. The metadata is computed at the first call 
	 * and then stored in an internal variable. Internally, WrappedResultSetMetaData is used.
	 * 
	 * @return ResultSetMetaData object of the projection
	 */
	public Object getMetaData () {
		if (metaData==null) {
			metaData = new WrappedResultSetMetaData((ResultSetMetaData)((MetaDataCursor)getDecoratedCursor()).getMetaData()) {
				public int getColumnCount() {
					return projection.getIndices().length;
				}

				public int originalColumnIndex(int column) throws SQLException {
					try {
						return (projection.getIndices()[column-1]) + 1;
					}
					catch (ArrayIndexOutOfBoundsException e) {
						throw new SQLException("Invalid column index.");
					}
				}

				public String getColumnName(int column) throws SQLException {
					if(renamedColumnNames == null)
						return this.metaData.getColumnName(originalColumnIndex(column));
					return renamedColumnNames[column-1];
				}

                public <T> T unwrap(Class<T> iface) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public boolean isWrapperFor(Class<?> iface) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
			};
		}
		
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
			Wraps an Enumerator cursor (integers 0 to 9)
			to a MetaDataCursor using {@link xxl.core.cursors.Cursors#wrapToMetaDataCursor}. 
			Then, the column becomes renamed.
		*/
		System.out.println("Example 1: Performing a renaming of a column");
		ResultSetMetaData metaData = (ResultSetMetaData) xxl.core.relational.metaData.MetaData.NUMBER_META_DATA_FACTORY_METHOD.invoke();
		
		MetaDataCursor cursor = xxl.core.cursors.Cursors.wrapToMetaDataCursor(
			Tuples.mapObjectsToTuples(
				new xxl.core.cursors.sources.Enumerator(0,10),
				metaData),
			    metaData
		);
		
		cursor = new Projection(cursor,
			new int[] {1},
			ArrayTuple.FACTORY_METHOD,
			new int[] {1},
			new String[] {"NewName"}
		);
		
		xxl.core.cursors.Cursors.println(cursor);
	}
}
