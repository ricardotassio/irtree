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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;


/**
 * This class decorates a ResultSet. It forwards all method calls to a
 * ResultSet that is passed at object creation time.
 *
 * It realizes a design pattern named <i>Decorator</i>. 
 * <p>
 * <b>Intent:</b><br>
 * "Attach additional responsibilities to an object dynamically.
 * Decorators provide a flexible alternative to subclassing for extending functionality." <br>
 * For further information see: "Gamma et al.: <i>DesignPatterns. Elements
 * of Reusable Object-Oriented Software.</i> Addision Wesley 1998."
 * </p>
 */
public class DecoratorResultSet implements ResultSet {

	/** 
	 * Internal variable for storing the internal ResultSet.
	 */
	protected ResultSet resultSet;

	/**
	 * Creates a DecoratorResultSet.
	 *
	 * @param resultSet ResultSet that is decorated.
	 */
	public DecoratorResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return resultSet.getMetaData();
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public int findColumn(String columnName) throws SQLException {
		return resultSet.findColumn(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Object getObject(int columnIndex) throws SQLException {
		return resultSet.getObject(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		return resultSet.getBytes(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public boolean next() throws SQLException {
		return resultSet.next();
	}

	/**
	 * A DecoratorResultSet method.
	 * @throws java.sql.SQLException if an error occurs
	 */
	public void close() throws SQLException {
		resultSet.close();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public boolean wasNull() throws SQLException {
		return resultSet.wasNull();
	}

	/**
	 * A DecoratorResultSet method.
	 * @throws java.sql.SQLException if an error occurs
	 */
	public void clearWarnings() throws SQLException {
		resultSet.clearWarnings();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public SQLWarning getWarnings() throws SQLException {
		 return resultSet.getWarnings();
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Object getObject(String columnName) throws SQLException {
		return resultSet.getObject(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public byte[] getBytes(String columnName) throws SQLException {
		return resultSet.getBytes(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return resultSet.getAsciiStream(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public InputStream getAsciiStream(String columnName) throws SQLException {
		return resultSet.getAsciiStream(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return resultSet.getAsciiStream(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public InputStream getBinaryStream(String columnName) throws SQLException {
		return resultSet.getBinaryStream(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public boolean getBoolean(int columnIndex) throws SQLException {
		return resultSet.getBoolean(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public boolean getBoolean(String columnName) throws SQLException {
		return resultSet.getBoolean(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public byte getByte(int columnIndex) throws SQLException {
		return resultSet.getByte(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public byte getByte(String columnName) throws SQLException {
		return resultSet.getByte(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Date getDate(int columnIndex) throws SQLException {
		return resultSet.getDate(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Date getDate(String columnName) throws SQLException {
		return resultSet.getDate(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public double getDouble(int columnIndex) throws SQLException {
		return resultSet.getDouble(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public double getDouble(String columnName) throws SQLException {
		return resultSet.getDouble(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public float getFloat(int columnIndex) throws SQLException {
		return resultSet.getFloat(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public float getFloat(String columnName) throws SQLException {
		return resultSet.getFloat(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public int getInt(int columnIndex) throws SQLException {
		return resultSet.getInt(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public int getInt(String columnName) throws SQLException {
		return resultSet.getInt(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public long getLong(int columnIndex) throws SQLException {
		return resultSet.getLong(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public long getLong(String columnName) throws SQLException {
		return resultSet.getLong(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public short getShort(int columnIndex) throws SQLException {
		return resultSet.getShort(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public short getShort(String columnName) throws SQLException {
		return resultSet.getShort(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public String getString(int columnIndex) throws SQLException {
		return resultSet.getString(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public String getString(String columnName) throws SQLException {
		return resultSet.getString(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Time getTime(int columnIndex) throws SQLException {
		return resultSet.getTime(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Time getTime(String columnName) throws SQLException {
		return resultSet.getTime(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return resultSet.getTimestamp(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Timestamp getTimestamp(String columnName) throws SQLException {
		return resultSet.getTimestamp(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public String getCursorName() throws SQLException {
		return resultSet.getCursorName();
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param cal the Calendar
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return resultSet.getDate(columnIndex, cal);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param cal the Calendar
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		return resultSet.getDate(columnName, cal);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param cal the Calendar
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return resultSet.getTime(columnIndex, cal);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param cal the Calendar
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		return resultSet.getTime(columnName, cal);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param cal the Calendar
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return resultSet.getTimestamp(columnIndex, cal);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param cal the Calendar
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
		return resultSet.getTimestamp(columnName, cal);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param row the row
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public boolean absolute(int row) throws SQLException {
		return resultSet.absolute(row);
	}

	/**
	 * A DecoratorResultSet method.
	 * @throws java.sql.SQLException if an error occurs
	 */
	public void afterLast() throws SQLException {
		resultSet.afterLast();
	}

	/**
	 * A DecoratorResultSet method.
	 * @throws java.sql.SQLException if an error occurs
	 */
	public void beforeFirst() throws SQLException {
		resultSet.beforeFirst();
	}

	/**
	 * A DecoratorResultSet method.
	 * @throws java.sql.SQLException if an error occurs
	 */
	public void cancelRowUpdates() throws SQLException {
		resultSet.cancelRowUpdates();
	}

	/**
	 * A DecoratorResultSet method.
	 * @throws java.sql.SQLException if an error occurs
	 */
	public void deleteRow() throws SQLException {
		resultSet.deleteRow();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public boolean first() throws SQLException {
		return resultSet.first();
	}

	/**
	 * A DecoratorResultSet method.
	 * @param i an integer
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Array getArray(int i) throws SQLException {
		return resultSet.getArray(i);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param colName the colName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public Array getArray(String colName) throws SQLException {
		return resultSet.getArray(colName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return resultSet.getBigDecimal(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param scale the scale
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 * @deprecated
	 */	
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return resultSet.getBigDecimal(columnIndex, scale);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return resultSet.getBigDecimal(columnName);
	}

	//deprecated
	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param scale the scale
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 * @deprecated
	 */	
	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		return resultSet.getBigDecimal(columnName, scale);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param i an integer
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public Blob getBlob(int i) throws SQLException {
		return resultSet.getBlob(i);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param colName the colName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public Blob getBlob(String colName) throws SQLException {
		return resultSet.getBlob(colName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return resultSet.getCharacterStream(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public Reader getCharacterStream(String columnName) throws SQLException {
		return resultSet.getCharacterStream(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param i an integer
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public Clob getClob(int i) throws SQLException {
		return resultSet.getClob(i);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public Clob getClob(String columnName) throws SQLException {
		return resultSet.getClob(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public int getConcurrency() throws SQLException {
		return resultSet.getConcurrency();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public int getFetchDirection() throws SQLException {
		return resultSet.getFetchDirection();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public int getFetchSize() throws SQLException {
		return resultSet.getFetchSize();
	}

	/**
	 * A DecoratorResultSet method.
	 * @param i an integer
	 * @param map a Map
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	                
	public Object getObject(int i, Map map) throws SQLException {
		return resultSet.getObject(i, map);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param colName the colName
	 * @param map the Map
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public Object getObject(String colName, Map map) throws SQLException {
		return resultSet.getObject(colName, map);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param i an integer
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public Ref getRef(int i) throws SQLException {
		return resultSet.getRef(i);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public Ref getRef(String columnName) throws SQLException {
		return resultSet.getRef(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public int getRow() throws SQLException {
		return resultSet.getRow();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public Statement getStatement() throws SQLException {
		return resultSet.getStatement();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public int getType() throws SQLException {
		return resultSet.getType();
	}

	//deprecated
	/**
	 * A DecoratorResultSet method.
	 * @param ColumnIndex the columnIndex
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 * @deprecated Use {@link #getCharacterStream(int)} instead.
	 */	
	public InputStream getUnicodeStream(int ColumnIndex) throws SQLException {
		return resultSet.getUnicodeStream(ColumnIndex);
	}

	//deprecated
	/**
	 * A DecoratorResultSet method.
	 * @param ColumnName the columnName
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 * @deprecated Use {@link #getCharacterStream(String)} instead.
	 */	
	public InputStream getUnicodeStream(String ColumnName) throws SQLException {
		return resultSet.getUnicodeStream(ColumnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void insertRow() throws SQLException {
		resultSet.insertRow();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public boolean isAfterLast() throws SQLException {
		return resultSet.isAfterLast();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public boolean isBeforeFirst() throws SQLException {
		return resultSet.isBeforeFirst();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public boolean isFirst() throws SQLException {
		return resultSet.isFirst();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public boolean isLast() throws SQLException {
		return resultSet.isLast();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public boolean last() throws SQLException {
		return resultSet.last();
	}

	/**
	 * A DecoratorResultSet method.
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void moveToCurrentRow() throws SQLException {
		resultSet.moveToCurrentRow();
	}

	/**
	 * A DecoratorResultSet method.
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void moveToInsertRow() throws SQLException {
		resultSet.moveToInsertRow();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public boolean previous() throws SQLException {
		return resultSet.previous();
	}

	/**
	 * A DecoratorResultSet method.
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void refreshRow() throws SQLException {
		resultSet.refreshRow();
	}

	/**
	 * A DecoratorResultSet method.
	 * @param rows the rows
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public boolean relative(int rows) throws SQLException {
		return resultSet.relative(rows);
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public boolean rowDeleted() throws SQLException {
		return resultSet.rowDeleted();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public boolean rowInserted() throws SQLException {
		return resultSet.rowInserted();
	}

	/**
	 * A DecoratorResultSet method.
	 * @return the result of the same method applied to the resultSet
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public boolean rowUpdated() throws SQLException {
		return resultSet.rowUpdated();
	}

	/**
	 * A DecoratorResultSet method.
	 * @param direction the direction
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void setFetchDirection(int direction) throws SQLException {
		resultSet.setFetchDirection(direction);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param rows the rows
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void setFetchSize(int rows) throws SQLException {
		resultSet.setFetchSize(rows);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x an InputStream
	 * @param length the length
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		resultSet.updateAsciiStream(columnIndex, x, length);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x an InputStream
	 * @param length the length
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
		resultSet.updateAsciiStream(columnName, x, length);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a BigDecimal
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		resultSet.updateBigDecimal(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a BigDecimal
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
		resultSet.updateBigDecimal(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x an InputStream
	 * @param length the length
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		resultSet.updateBinaryStream(columnIndex, x, length);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x an InputStream
	 * @param length the length
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
		resultSet.updateBinaryStream(columnName, x, length);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a Boolean
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		resultSet.updateBoolean(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a boolean
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		resultSet.updateBoolean(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a byte
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateByte(int columnIndex, byte x) throws SQLException {
		resultSet.updateByte(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a byte
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateByte(String columnName, byte x) throws SQLException {
		resultSet.updateByte(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a byte-array
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		resultSet.updateBytes(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a byte-array
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		resultSet.updateBytes(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a Reader
	 * @param length the length
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		resultSet.updateCharacterStream(columnIndex, x, length);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param reader a Reader
	 * @param length the length
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
		resultSet.updateCharacterStream(columnName, reader, length);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a Date
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateDate(int columnIndex, Date x) throws SQLException {
		resultSet.updateDate(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a Date
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateDate(String columnName, Date x) throws SQLException {
		resultSet.updateDate(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a double
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateDouble(int columnIndex, double x) throws SQLException {
		resultSet.updateDouble(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a double
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateDouble(String columnName, double x) throws SQLException {
		resultSet.updateDouble(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a float
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateFloat(int columnIndex, float x) throws SQLException {
		resultSet.updateFloat(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a float
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateFloat(String columnName, float x) throws SQLException {
		resultSet.updateFloat(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x an integer
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateInt(int columnIndex, int x) throws SQLException {
		resultSet.updateInt(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x an integer
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateInt(String columnName, int x) throws SQLException {
		resultSet.updateInt(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a long
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateLong(int columnIndex, long x) throws SQLException {
		resultSet.updateLong(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a long
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateLong(String columnName, long x) throws SQLException {
		resultSet.updateLong(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateNull(int columnIndex) throws SQLException {
		resultSet.updateNull(columnIndex);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateNull(String columnName) throws SQLException {
		resultSet.updateNull(columnName);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x an Object
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateObject(int columnIndex, Object x) throws SQLException {
		resultSet.updateObject(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x an Object
	 * @param scale the scale
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
		resultSet.updateObject(columnIndex, x, scale);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x an Object
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateObject(String columnName, Object x) throws SQLException {
		resultSet.updateObject(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x an Object
	 * @param scale the scale
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateObject(String columnName, Object x, int scale) throws SQLException {
		resultSet.updateObject(columnName, x, scale);
	}

	/**
	 * A DecoratorResultSet method.
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateRow() throws SQLException {
		resultSet.updateRow();
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a short
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateShort(int columnIndex, short x) throws SQLException {
		resultSet.updateShort(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a short
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateShort(String columnName, short x) throws SQLException {
		resultSet.updateShort(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a String
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateString(int columnIndex, String x) throws SQLException {
		resultSet.updateString(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a String
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateString(String columnName, String x) throws SQLException {
		resultSet.updateString(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a Time type
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateTime(int columnIndex, Time x) throws SQLException {
		resultSet.updateTime(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a Time type
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateTime(String columnName, Time x) throws SQLException {
		resultSet.updateTime(columnName, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnIndex the columnIndex
	 * @param x a Timestamp
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		resultSet.updateTimestamp(columnIndex, x);
	}

	/**
	 * A DecoratorResultSet method.
	 * @param columnName the columnName
	 * @param x a Timestamp
	 * @throws java.sql.SQLException if an error occurs
	 */	
	public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
		resultSet.updateTimestamp(columnName, x);
	}
	
	// for compatibility with SDK 1.4
	// this solution is also compatible with SDK 1.2 and 1.3, because it 
	// throws exceptions.
	/**
	 * Not implemented yet.
	 * @param x an integer
	 * @return an exception
	 * @throws SQLException
	 */		
	public java.net.URL getURL(int x) throws SQLException {
		throw new SQLException("URL getURL(int)"); 
	}

	/**
	 * Not implemented yet.
	 * @param a a java.lang.String
	 * @param b a java.sql.Array
	 * @throws SQLException
	 */		
	public void updateArray(java.lang.String a,java.sql.Array b) throws SQLException { 
		throw new SQLException("updateArray(java.lang.String,java.sql.Array)");
	}
	
	/**
	 * Not implemented yet.
	 * @param a an integer
	 * @param b a java.sql.Array
	 * @throws SQLException
	 */			
	public void updateArray(int a,java.sql.Array b) throws SQLException { 
		throw new SQLException("updateArray(int,java.sql.Array)"); 
	}
	
	/**
	 * Not implemented yet.
	 * @param s a java.lang.String
	 * @return an exception
	 * @throws SQLException
	 */		
	public java.net.URL getURL(java.lang.String s) throws SQLException {
		throw new SQLException("URL getURL(int)");
	}
	
	/**
	 * Not implemented yet.
	 * @param a an integer
	 * @param b a java.sql.Ref
	 * @throws SQLException
	 */	
	public void updateRef(int a,java.sql.Ref b) throws SQLException	{
		throw new SQLException("updateRef(int,java.sql.Ref)");
	}
	
	/**
	 * Not implemented yet.
	 * @param a a java.lang.String
	 * @param b a java.sql.Ref
	 * @throws SQLException
	 */	
	public void updateRef(java.lang.String a,java.sql.Ref b) throws SQLException { 
		throw new SQLException("updateRef(java.lang.String,java.sql.Ref)");
	}
	
	/**
	 * Not implemented yet.
	 * @param a an integer
	 * @param b a java.sql.Blob
	 * @throws SQLException
	 */	
	public void updateBlob(int a,java.sql.Blob b) throws SQLException {
		throw new SQLException("updateBlob(int,java.sql.Blob)");
	}
	
	/**
	 * Not implemented yet.
	 * @param a a java.lang.String
	 * @param b a java.sql.Blob
	 * @throws SQLException
	 */	
	public void updateBlob(java.lang.String a,java.sql.Blob b) throws SQLException {
		throw new SQLException("updateBlob(String,java.sql.Blob)");
	}
	
	/**
	 * Not implemented yet.
	 * @param a an integer
	 * @param b a java.sql.Clob
	 * @throws SQLException
	 */	
	public void updateClob(int a,java.sql.Clob b) throws SQLException {
		throw new SQLException("updateClob(int,java.sql.Clob)");
	}

	/**
	 * Not implemented yet.
	 * @param a a String
	 * @param b a java.sql.Clob
	 * @throws SQLException
	 */		
	public void updateClob(String a,java.sql.Clob b) throws SQLException {
		throw new SQLException("updateClob(String,java.sql.Clob)");
	}

    public RowId getRowId(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public RowId getRowId(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isClosed() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public NClob getNClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public NClob getNClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getNString(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getNString(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	// This is a better solution for SDK 1.4 but not compatible with SDK 1.2 and 1.3
	/*
	public java.net.URL getURL(int x) throws SQLException{
		return resultSet.getURL(x);
	}
	
	public void updateArray(java.lang.String a,java.sql.Array b) throws SQLException{ 
		resultSet.updateArray(a,b); 
	}
	
	public void updateArray(int a,java.sql.Array b) throws SQLException{ 
		resultSet.updateArray(a,b); 
	}
	
	public java.net.URL getURL(java.lang.String s) throws SQLException{
		return resultSet.getURL(s);
	}
	
	public void updateRef(int a,java.sql.Ref b) throws SQLException{ 
		resultSet.updateRef(a,b); 
	}
	
	public void updateRef(java.lang.String a,java.sql.Ref b) throws SQLException{ 
		resultSet.updateRef(a,b); 
	}
	
	public void  updateBlob(int a,java.sql.Blob b) throws SQLException{ 
		resultSet.updateBlob(a,b); 
	}
	
	public void  updateBlob(java.lang.String a,java.sql.Blob b) throws SQLException{ 
		resultSet.updateBlob(a,b); 
	}
	
	public void updateClob(int a,java.sql.Clob b) throws SQLException{ 
		resultSet.updateClob(a,b); 
	}
	
	public void updateClob(String a,java.sql.Clob b) throws SQLException{ 
		resultSet.updateClob(a,b); 
	}
	*/

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
