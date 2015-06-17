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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Makes a projection of a ResultSetMetaData Object to certain columns.
 */
public class ProjectionMetaData implements ResultSetMetaData {
	
	/**
	 * The metadata of the object to be projected.
	 */
	private ResultSetMetaData rsmd;
	
	/**
	 * The columns of the object that will be projected.
	 */
	private int columns[];
	
	/**
	 * The names of the projected columns after projection.
	 */
	private String newColumnNames[];
	
	/**
	 * Creates a ProjectionMetaData.
	 * @param rsmd metadata Object to be projected.
	 * @param columns Columns which will be available inside the new
	 *  ProjectionMetaData Object.
	 * @param newColumnNames names of the columns. If an entry (or the array) 
	 *  is null, then the original name is taken.
	 */
	public ProjectionMetaData (ResultSetMetaData rsmd, int columns[], String newColumnNames[]) {
		this.rsmd = rsmd;
		this.columns = columns;
		this.newColumnNames = newColumnNames;
	}

	/**
	 * Returns the name of the column.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return name of the column
	 * @throws SQLException
	 */	
	public String getColumnName(int column) throws SQLException {
		if (newColumnNames!=null) {
			String newColumnName = newColumnNames[column-1]; 
			if (newColumnName!=null)
				return newColumnName;
		}
		
		return rsmd.getColumnName(columns[column-1]);
	}

	/**
	 * Returns the precision of the column. 
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return precision of the column
	 * @throws SQLException
	 */	
	public int getPrecision(int column) throws SQLException {
		return rsmd.getPrecision(columns[column-1]);
	}
	
	/**
	 * Returns the scale of the column.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return scale of the column
	 * @throws SQLException
	 */	
	public int getScale(int column) throws SQLException {
		return (columns[column-1]);
	}

	/**
	 * Returns the number of columns of this object.
	 *
	 * @return the number of columns
	 */
	public int getColumnCount() {
		return columns.length;
	}

	/**
	 * Checks the column index. If the index does not exist, an SQLException is thrown.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public void checkColumnCount(int column) throws java.sql.SQLException {
		if (column<1 || column>getColumnCount())
			throw new SQLException("Invalid column index.");
	}

	/**
	 * Returns the name of the catalog. 
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return name of the catalog.
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public String getCatalogName(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.getCatalogName(columns[column-1]);
	}

	/**
	 * Returns the display size of the column.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return display size of the column
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public int getColumnDisplaySize(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.getColumnDisplaySize(columns[column-1]);
	}

	/**
	 * Returns the column label.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return name of the column label
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public String getColumnLabel(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.getColumnLabel(columns[column-1]);
	}

	/**
	 * Returns the type of the column.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return type of the column
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public int getColumnType(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.getColumnType(columns[column-1]);
	}

	/**
	 * Returns the type name of the column.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return type name of the column.
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public String getColumnTypeName(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.getColumnTypeName(columns[column-1]);
	}

	/**
	 * Returns the name the Java class that is associated with the column type. 
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return name of the column class: "java.lang.Number"
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public String getColumnClassName(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.getColumnClassName(columns[column-1]);
	}

	/**
	 * Returns the schema name of the column.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return schema name of the column: ""
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public String getSchemaName(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.getSchemaName(columns[column-1]);
	}

	/**
	 * Returns the table name of the column.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return table name of the column: ""
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public String getTableName(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.getTableName(columns[column-1]);
	}

	/**
	 * Returns if the column is an auto increment column.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return always false, because the column is not an auto increment column
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public boolean isAutoIncrement(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.isAutoIncrement(columns[column-1]);
	}

	/**
	 * Returns if the column is not case sensitive.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return always false, because the column is not case sensitive.
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public boolean isCaseSensitive(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.isCaseSensitive(columns[column-1]);
	}

	/**
	 * Returns if the column is assumed to be a currency.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return always true, because the column is assumed to be a currency.
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public boolean isCurrency(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.isCurrency(columns[column-1]);
	}

	/**
	 * Returns if the column is definitively writable.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return always false, because the column may not be writable.
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public boolean isDefinitelyWritable(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.isDefinitelyWritable(columns[column-1]);
	}

	/**
	 * Returns if the column is nullable.
	 *
	 * @param column number of the column: the first column is 1.
	 * @return java.sql.ResultSetMetaData.columnNullable, because the column may contain null values.
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */
	public int isNullable(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.isNullable(columns[column-1]);
	}

	/**
	 * Returns if the column is assumed to be writable.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return always false, because the column is assumed to be writable.
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public boolean isReadOnly(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.isReadOnly(columns[column-1]);
	}

	/**
	 * Returns if the column is searchable.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return always true, because the column is searchable.
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public boolean isSearchable(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.isSearchable(columns[column-1]);
	}

	/**
	 * Returns true if the column is signed.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return always true, because the column value is signed.
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public boolean isSigned(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.isSigned(columns[column-1]);
	}

	/**
	 * Returns if the column is writable.
	 * 
	 * @param column number of the column: the first column is 1.
	 * @return always true, because the column is assumed to be writable.
	 * @throws SQLException("Invalid column index.") if the column does not exist.
	 */	
	public boolean isWritable(int column) throws SQLException {
		checkColumnCount(column);
		return rsmd.isWritable(columns[column-1]);
	}

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
