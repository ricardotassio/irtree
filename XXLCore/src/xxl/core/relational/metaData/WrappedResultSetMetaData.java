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
 *	This abstract class is a ResultSetMetaData skeleton that
 *	wraps a given ResultSetMetaData.
 *	Calls to <code>getXXX(int column)</code>-methods are redirected to
 *	<code>getXXX(<b>originalColumnIndex(column)</b>)</code>.<BR>
 *	So, a mapping between Columns can be defined. This is useful 
 *	especially for projections.
 *	<p>
 *	There are two abstract methods:
 *	<ul>
 *		<li><code>int <B>getColumnCount</B>()</code>
 *		<li><code>int <B>originalColumnIndex</B>(int column)</code>
 *	</ul>
*/
public abstract class WrappedResultSetMetaData implements ResultSetMetaData {

	/** Metadata object to which calls are forwarded. */
	protected ResultSetMetaData metaData;

	/**
     * Constructs a WrappedResultSetMedaData object that wraps a given
     * ResultSetMetaData object.
	 * 
	 * @param metaData ResultSetMetaData object
	 */
	public WrappedResultSetMetaData (ResultSetMetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * Returns the number of columns.
	 *
	 * @return number of columns
	 * @throws SQLException
	 */	
	public abstract int getColumnCount() throws SQLException;
	
	/**
	 * Returns the column number of the original ResultSetMetaData object that has been mapped
	 * to the column number value that is passed to the call.
	 *
	 * @param column column number of the mapped WrappedResultSetMetaData object
	 * @return column number of the original ResultSetMetaData object
	 * @throws SQLException
	 */
	public abstract int originalColumnIndex(int column) throws SQLException;

	/**
	 * Returns the name the catalog. This call is redirected to the
	 * getCatalogName(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return name of the catalog
	 * @throws SQLException
	 */	
	public String getCatalogName(int column) throws SQLException {
		return metaData.getCatalogName(originalColumnIndex(column));
	}

	/**
	 * Returns the name the class name. This call is redirected to the
	 * getColumnClassName(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return name of the class name
	 * @throws SQLException
	 */	
	public String getColumnClassName(int column) throws SQLException {
		return metaData.getColumnClassName(originalColumnIndex(column));
	}

	/**
	 * Returns the display size. This call is redirected to the
	 * getColumnDisplaySize(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return display size of the column
	 * @throws SQLException
	 */	
	public int getColumnDisplaySize(int column) throws SQLException {
		return metaData.getColumnDisplaySize(originalColumnIndex(column));
	}

	/**
	 * Returns the label of a column. This call is redirected to the
	 * getColumnLabel(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return column label of the column
	 * @throws SQLException
	 */	
	public String getColumnLabel(int column) throws SQLException {
		return metaData.getColumnLabel(originalColumnIndex(column));
	}

	/**
	 * Returns the name of a column.  This call is redirected to the
	 * getColumnName(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return name of the column
	 * @throws SQLException
	 */	
	public String getColumnName(int column) throws SQLException {
		return metaData.getColumnName(originalColumnIndex(column));
	}

	/**
	 * Returns the type of a column. This call is redirected to the
	 * getColumnType(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return type of the column
	 * @throws SQLException
	 */	
	public int getColumnType(int column) throws SQLException {
		return metaData.getColumnType(originalColumnIndex(column));
	}

	/**
	 * Returns the type name of a column. This call is redirected to the
	 * getColumnTypeName(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return type name of the column
	 * @throws SQLException
	 */	
	public String getColumnTypeName(int column) throws SQLException {
		return metaData.getColumnTypeName(originalColumnIndex(column));
	}

	/**
	 * Returns the precision of a column. This call is redirected to the
	 * getPrecision(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return precision of the column
	 * @throws SQLException
	 */	
	public int getPrecision(int column) throws SQLException {
		return metaData.getPrecision(originalColumnIndex(column));
	}

	/**
	 * Returns the scale of a column. This call is redirected to the
	 * getScale(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return scale of the column
	 * @throws SQLException
	 */	
	public int getScale(int column) throws SQLException {
		return metaData.getScale(originalColumnIndex(column));
	}

	/**
	 * Returns the schema name of a column. This call is redirected to the
	 * getSchemaName(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return schema name of the column
	 * @throws SQLException
	 */	
	public String getSchemaName(int column) throws SQLException {
		return metaData.getSchemaName(originalColumnIndex(column));
	}

	/**
	 * Returns the table name of a column. This call is redirected to the
	 * getTableName(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return table name of the column
	 * @throws SQLException
	 */	
	public String getTableName(int column) throws SQLException {
		return metaData.getTableName(originalColumnIndex(column));
	}

	/**
	 * Returns if the column is an auto increment column. This call is redirected to the
	 * isAutoIncrement(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is an auto increment column
	 * @throws SQLException
	 */	
	public boolean isAutoIncrement(int column) throws SQLException {
		return metaData.isAutoIncrement(originalColumnIndex(column));
	}

	/**
	 * Returns if the column is case sensitive. This call is redirected to the
	 * isCaseSensitive(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is case sensitive
	 * @throws SQLException
	 */	
	public boolean isCaseSensitive(int column) throws SQLException {
		return metaData.isCaseSensitive(originalColumnIndex(column));
	}

	/**
	 * Returns if the column contains a currency value. This call is redirected to the
	 * isCurrency(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column contains a currency value
	 * @throws SQLException
	 */	
	public boolean isCurrency(int column) throws SQLException {
		return metaData.isCurrency(originalColumnIndex(column));
	}

	/**
	 * Returns if the column is definitively writable. This call is redirected to the
	 * isDefinitelyWritable(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is definitively writable
	 * @throws SQLException
	 */	
	public boolean isDefinitelyWritable(int column) throws SQLException {
		return metaData.isDefinitelyWritable(originalColumnIndex(column));
	}

	/**
	 * Returns if the column may contain null values. This call is redirected to the
	 * isNullable(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return one of the constants defined in {@link java.sql.ResultSetMetaData}: 
	 *	columnNoNulls, columnNullable or columnNullableUnknown
	 * @throws SQLException
	 */	
	public int isNullable(int column) throws SQLException {
		return metaData.isNullable(originalColumnIndex(column));
	}

	/**
	 * Returns if the column is read only. This call is redirected to the
	 * isReadOnly(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is read only
	 * @throws SQLException
	 */	
	public boolean isReadOnly(int column) throws SQLException {
		return metaData.isReadOnly(originalColumnIndex(column));
	}

	/**
	 * Returns if the column is searchable. This call is redirected to the
	 * isSearchable(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is searchable
	 * @throws SQLException
	 */	
	public boolean isSearchable(int column) throws SQLException {
		return metaData.isSearchable(originalColumnIndex(column));
	}

	/**
	 * Returns if the type of the column is a signed type. This call is redirected to the
	 * isSigned(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the type of the column is a signed type
	 * @throws SQLException
	 */	
	public boolean isSigned(int column) throws SQLException {
		return metaData.isSigned(originalColumnIndex(column));
	}

	/**
	 * Returns if the column is writable. This call is redirected to the
	 * isWritable(originalColumnIndex(column))-call of the ResultSetMetaData object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is writable
	 * @throws SQLException
	 */	
	public boolean isWritable(int column) throws SQLException {
		return metaData.isWritable(originalColumnIndex(column));
	}
}
