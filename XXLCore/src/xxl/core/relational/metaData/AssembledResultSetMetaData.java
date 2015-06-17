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

package xxl.core.relational.metaData;

import java.sql.SQLException;
import java.sql.ResultSetMetaData;

/**
 *	This class is a {@link ResultSetMetaData} implementation that wraps a given
 *	array "<code>columnMetaDatas</code>" of one column ResultSetMetaData 
 *	Objects.
 *	
 *	Calls to <code>getXXX(int column)</code>-methods are redirected to
 *	<code>columnMetaDatas[column-1].getXXX(1)</code>.
 *	So, the entries of <code>columnMetaDatas</code> are assumed to have 
 *	exactly one column.
 *	
 *	An object of this class merges the metadata of n one column ResultSetMetaData
 *	objects and represents the metadata of a n-column ResultSet.
 */
public class AssembledResultSetMetaData implements ResultSetMetaData {

	/** An array of one columns ResultSetMetaData Objects */
	protected ResultSetMetaData[] columnMetaDatas;
	
	/** The new column names */
	protected String[] newColumnNames;

	/**
	 * Constructs an AssembledResultSetMedaData object that wraps  a given
	 * array of one column ResultSetMetaData objects. If the original
	 * ResultSetMetaData object contains more than one column, only
	 * the first is used.
	 *
	 * @param columnMetaDatas array of one column ResultSetMetaData objects
	 * @param newColumnNames array containing new names of the columns. This array
	 *	must have the same size than columnMetaDatas. If an entry equals 
	 *	null, the original name of the column is used
	 * @throws IllegalArgumentException if the parameter arrays do not have the same size or
	 *	a null-value is passed in the metadata array
	 */
	public AssembledResultSetMetaData (ResultSetMetaData[] columnMetaDatas, String[] newColumnNames) {
		this.columnMetaDatas = columnMetaDatas;
		this.newColumnNames = newColumnNames;
		if (columnMetaDatas.length != newColumnNames.length)
			throw new IllegalArgumentException("Parameter arrays do not have the same length.");
		for (int i = 0; i < columnMetaDatas.length; i++)
			if (columnMetaDatas[i] == null)
				throw new IllegalArgumentException("A null-value in metadata array.");
	}

	/**
	 * Constructs an AssembledResultSetMedaData object that wraps  a given
	 * array of one column ResultSetMetaData objects. If the original
	 * ResultSetMetaData object contains more than one column, only
	 * the first is used. The names of the columns stay the same than in the
	 * original ResultSetMetaDatas.
	 *
	 * @param columnMetaDatas array of one column ResultSetMetaData objects
	 * @throws IllegalArgumentException if
	 *	a null-value is passed in the metadata array
	 */
	public AssembledResultSetMetaData (ResultSetMetaData[] columnMetaDatas) {
		this (columnMetaDatas, new String[columnMetaDatas.length]);
	}

	/**
	 * Returns the number of columns. This is equal to the size of the
	 * arrays that has been passed to the constructor call.
	 *
	 * @return number of columns
	 */	
	public int getColumnCount () {
		return columnMetaDatas.length;
	}

	/**
	 * Returns the name of a column. If a new name has been passed to the constructor call
	 * this name is returned.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return name of the column
	 * @throws SQLException
	 */	
	public String getColumnName (int column) throws SQLException {
		return newColumnNames[column-1] == null || newColumnNames[column-1].equals("") ? columnMetaDatas[column-1].getColumnName(1) : newColumnNames[column-1];
	}

	/**
	 * Returns the name the catalog. This call is redirected to the
	 * getCatalogName(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return name of the catalog
	 * @throws SQLException
	 */	
	public String getCatalogName (int column) throws SQLException {
		return columnMetaDatas[column-1].getCatalogName(1);
	}

	/**
	 * Returns the name the class name. This call is redirected to the
	 * getColumnClassName(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return name of the class name
	 * @throws SQLException
	 */	
	public String getColumnClassName (int column) throws SQLException {
		return columnMetaDatas[column-1].getColumnClassName(1);
	}

	/**
	 * Returns the display size. This call is redirected to the
	 * getColumnDisplaySize(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return display size of the column
	 * @throws SQLException
	 */	
	public int getColumnDisplaySize (int column) throws SQLException {
		return columnMetaDatas[column-1].getColumnDisplaySize(1);
	}

	/**
	 * Returns the label of a column. This call is redirected to the
	 * getColumnLabel(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return column label of the column
	 * @throws SQLException
	 */	
	public String getColumnLabel (int column) throws SQLException {
		return columnMetaDatas[column-1].getColumnLabel(1);
	}

	/**
	 * Returns the type of a column. This call is redirected to the
	 * getColumnType(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return type of the column
	 * @throws SQLException
	 */	
	public int getColumnType (int column) throws SQLException {
		return columnMetaDatas[column-1].getColumnType(1);
	}

	/**
	 * Returns the type name of a column. This call is redirected to the
	 * getColumnTypeName(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return type name of the column
	 * @throws SQLException
	 */	
	public String getColumnTypeName (int column) throws SQLException {
		return columnMetaDatas[column-1].getColumnTypeName(1);
	}

	/**
	 * Returns the precision of a column. This call is redirected to the
	 * getPrecision(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return precision of the column
	 * @throws SQLException
	 */	
	public int getPrecision (int column) throws SQLException {
		return columnMetaDatas[column-1].getPrecision(1);
	}

	/**
	 * Returns the scale of a column. This call is redirected to the
	 * getScale(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return scale of the column
	 * @throws SQLException
	 */	
	public int getScale (int column) throws SQLException {
		return columnMetaDatas[column-1].getScale(1);
	}

	/**
	 * Returns the schema name of a column. This call is redirected to the
	 * getSchemaName(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return schema name of the column
	 * @throws SQLException 
	 */	
	public String getSchemaName (int column) throws SQLException {
		return columnMetaDatas[column-1].getSchemaName(1);
	}

	/**
	 * Returns the table name of a column. This call is redirected to the
	 * getTableName(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return table name of the column
	 * @throws SQLException 
	 */	
	public String getTableName (int column) throws SQLException {
		return columnMetaDatas[column-1].getTableName(1);
	}

	/**
	 * Returns if the column is an auto increment column. This call is redirected to the
	 * isAutoIncrement(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is an auto increment column
	 * @throws SQLException
	 */	
	public boolean isAutoIncrement (int column) throws SQLException {
		return columnMetaDatas[column-1].isAutoIncrement(1);
	}

	/**
	 * Returns if the column is case sensitive. This call is redirected to the
	 * isCaseSensitive(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is case sensitive
	 * @throws SQLException
	 */	
	public boolean isCaseSensitive (int column) throws SQLException {
		return columnMetaDatas[column-1].isCaseSensitive(1);
	}

	/**
	 * Returns if the column contains a currency value. This call is redirected to the
	 * isCurrency(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column contains a currency value
	 * @throws SQLException
	 */	
	public boolean isCurrency(int column) throws SQLException {
		return columnMetaDatas[column-1].isCurrency(1);
	}

	/**
	 * Returns if the column is definitively writable. This call is redirected to the
	 * isDefinitivelyWritable(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is definitively writable
	 * @throws SQLException
	 */	
	public boolean isDefinitelyWritable(int column) throws SQLException {
		return columnMetaDatas[column-1].isDefinitelyWritable(1);
	}

	/**
	 * Returns if the column may contain null values. This call is redirected to the
	 * isNullable(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return one of the constants defined in {@link java.sql.ResultSetMetaData}: 
	 *	columnNoNulls, columnNullable or columnNullableUnknown
	 * @throws SQLException
	 */	
	public int isNullable(int column) throws SQLException {
		return columnMetaDatas[column-1].isNullable(1);
	}

	/**
	 * Returns if the column is read only. This call is redirected to the
	 * isReadOnly(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is read only
	 * @throws SQLException
	 */	
	public boolean isReadOnly(int column) throws SQLException {
		return columnMetaDatas[column-1].isReadOnly(1);
	}

	/**
	 * Returns if the column is searchable. This call is redirected to the
	 * isSearchable(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is searchable
	 * @throws SQLException
	 */	
	public boolean isSearchable(int column) throws SQLException {
		return columnMetaDatas[column-1].isSearchable(1);
	}

	/**
	 * Returns if the type of the column is a signed type. This call is redirected to the
	 * isSigned(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the type of the column is a signed type
	 * @throws SQLException
	 */	
	public boolean isSigned(int column) throws SQLException {
		return columnMetaDatas[column-1].isSigned(1);
	}

	/**
	 * Returns if the column is writable. This call is redirected to the
	 * isWritable(1) call of the ResultSetMetaData object from which
	 * the column is originated.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is writable
	 * @throws SQLException
	 */	
	public boolean isWritable(int column) throws SQLException {
		return columnMetaDatas[column-1].isWritable(1);
	}

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
