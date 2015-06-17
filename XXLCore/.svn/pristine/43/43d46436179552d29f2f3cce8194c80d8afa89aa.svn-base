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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import xxl.core.util.WrappingRuntimeException;


/**
*	This class is a skeleton for writing non-abstract classes that implement the
*	ResultSet-interface. This class is very useful to integrate new sources
*	into xxl.
*	<p>
*
*	Only JDBC 1.0 is supported.<BR>
*	All methods of JDBC 2.0 throw an <code>SQLException</code>.
*	<p>
*
*	Only seven methods have to be implemented to get a non-abstract ResultSet:
*
*	<ul>
*		<li> ResultSetMetaData <B>getMetaData</B>();
*		<li> int <B>findColumn</B>(String columnName);
*		<li> Object <B>getObject</B>(int columnIndex);
*		<li> byte[] <B>getBytes</B>(int columnIndex);
*		<li> boolean <B>next</B>();
*		<li> void <B>close</B>();
*		<li> boolean <B>wasNull</B>();
*	</ul>
*
*	Then the getXXX methods call <code>getObject</code> and cast the result properly.
*	<p>
*	For a complete description of the methods read @see java.sql.ResultSet.
*	<p>
*
*	Example for a non-abstract subclass of AbstractResultSet:
*
*	<pre><code>
*	class MySelection extends AbstractResultSet {
*		protected ResultSet resultSet;
*		protected Function predicate;
*
*		public MySelection (Function predicate, ResultSet resultSet) {
*			this.resultSet = resultSet;
*			this.predicate = predicate;
*		}
*
*		public boolean next() throws SQLException {
*			boolean next = resultSet.next();
*			while (next && !((Boolean)predicate.invoke(resultSet)).booleanValue())
*				next = resultSet.next();
*			return next;
*		}
*
*		public ResultSetMetaData getMetaData() throws SQLException {
*			return resultSet.getMetaData();
*		}
*
*		public int findColumn(String columnName) throws SQLException {
*			return resultSet.findColumn(columnName);
*		}
*
*		public Object getObject(int columnIndex) throws SQLException {
*			return resultSet.getObject(columnIndex);
*		}
*
*		public byte[] getBytes(int columnIndex) throws SQLException {
*			return resultSet.getBytes(columnIndex);
*		}
*
*		public boolean wasNull() throws SQLException {
*			return resultSet.wasNull();
*		}
*
*		public void close() throws SQLException {
*			resultSet.close();
*		}
*	}
*	</code></pre>
*
*/
public abstract class AbstractResultSet implements ResultSet {

	/** 
	 * Retrieves the number, types and properties of a ResultSet's columns.
	 *
     	 * @return the description of a ResultSet's columns
     	 * @throws java.sql.SQLException if a database access error occurs
     	 */
     	public abstract ResultSetMetaData getMetaData() throws SQLException;
     	/**
	 * Maps the given Resultset column name to its ResultSet column index.
	 *
     	 * @param columnName the name of the column
     	 * @return the column index
     	 * @throws java.sql.SQLException if a database access error occurs	
     	 */
     	public abstract int findColumn(String columnName) throws SQLException;
	/**
	 * Gets the value of a column in the current row as a Java object. 
	 *
	 * This method will return the value of the given column as a Java object. 
	 * The type of the Java object will be the default Java object type corresponding 
	 * to the column's SQL type, following the mapping for built-in types specified in 
	 * the JDBC spec. 
	 *
     	 * This method may also be used to read datatabase-specific abstract data types. 
     	 * In the JDBC 2.0 API, the behavior of method getObject is extended 
     	 * to materialize data of SQL user-defined types. When the column contains a 
     	 * structured or distinct value, the behavior of this method is as if it were a 
     	 * call to: getObject(columnIndex, this.getStatement().getConnection().getTypeMap()).
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a java.lang.Object holding the column value
     	 * @throws java.sql.SQLException if a database access error occurs
     	 */
	public abstract Object getObject(int columnIndex) throws SQLException;
	/**
	 * Gets the value of a column in the current row as a Java byte array. The bytes 
	 * represent the raw values returned by the driver.
     	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return the column value; if the value is SQL NULL, the result is null
     	 * @throws java.sql.SQLException - if a database access error occurs
     	 */
        public abstract byte[] getBytes(int columnIndex) throws SQLException;
	/** 
	 * Moves the cursor down one row from its current position. A ResultSet cursor is 
	 * initially positioned before the first row; the first call to next makes the first 
	 * row the current row; the second call makes the second row the current row, and so on. 
	 * 
	 * If an input stream is open for the current row, a call to the method next will 
	 * implicitly close it. The ResultSet's warning chain is cleared when a new row is read.
     	 *
     	 * @return true if the new current row is valid; false if there are no more rows
     	 * @throws java.sql.SQLException if a database access error occurs
     	 */
        public abstract boolean next() throws SQLException;
	/** 
	 * Releases this ResultSet object's database and JDBC resources immediately instead 
	 * of waiting for this to happen when it is automatically closed. 
	 *
	 * Note: A ResultSet is automatically closed by the Statement that generated it 
	 * when that Statement is closed, re-executed, or is used to retrieve the next 
	 * result from a sequence of multiple results. A ResultSet is also automatically 
	 * closed when it is garbage collected.
	 *
	 * @throws java.sql.SQLException if a database access error occurs
	 */
	public abstract void close() throws SQLException;
	/**
	 * Reports whether the last column read had a value of SQL NULL. Note that you must 
	 * first call getXXX on a column to try to read its value and then call wasNull() 
	 * to see if the value read was SQL NULL.
     	 *
     	 * @return true if last column read was SQL NULL and false otherwise
     	 * @throws java.sql.SQLException if a database access error occurs
     	 */
     	public abstract boolean wasNull() throws SQLException;

    /**Counts the columns of the result set.
     * 
     * @param resultSet the given result set
     * @return the number of columns
     * @throws WrappingRuntimeException
     * @see ResultSet */
	public static int getColumnCount(ResultSet resultSet) {
		try {
			return resultSet.getMetaData().getColumnCount();
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

     /**
	 * Not implemented yet.
	 * @throws SQLException
	 */	
	public void clearWarnings() throws SQLException {}
	
	/**
	 * Not implemented yet.
	 * @return allways null
	 * @throws SQLException
	 */	
	public SQLWarning getWarnings() throws SQLException { return null; }

     /**
	 * Gets the value of a column in the current row as a Java object. 
	 *
	 * This method will return the value of the given column as a Java object. 
	 * The type of the Java object will be the default Java object type corresponding 
	 * to the column's SQL type, following the mapping for built-in types specified in 
	 * the JDBC spec. 
	 *
     	 * This method may also be used to read datatabase-specific abstract data types. 
     	 * In the JDBC 2.0 API, the behavior of method getObject is extended 
     	 * to materialize data of SQL user-defined types. When the column contains a 
     	 * structured or distinct value, the behavior of this method is as if it were a 
     	 * call to: getObject(columnIndex, this.getStatement().getConnection().getTypeMap()).
     	 * @param columnName the column name
     	 * @return a java.lang.Object holding the column value
     	 * @throws java.sql.SQLException if a database access error occurs
     	 */
	public Object getObject(String columnName) throws SQLException {
		return getObject(findColumn(columnName));
	}

	/** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public byte[] getBytes(String columnName) throws SQLException {
		return getBytes(findColumn(columnName));
	}

	/** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return new ByteArrayInputStream(getBytes(columnIndex));
	}
	
	/** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public InputStream getAsciiStream(String columnName) throws SQLException {
		return getAsciiStream(findColumn(columnName));
	}

	/** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return new ByteArrayInputStream(getBytes(columnIndex));
	}
	
	/** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public InputStream getBinaryStream(String columnName) throws SQLException {
		return getBinaryStream(findColumn(columnName));
	}


    /** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public boolean getBoolean(int columnIndex) throws SQLException {
		Object o = getObject(columnIndex);
		return (o==null) ? false :
			(o instanceof BigDecimal) ? (((BigDecimal)o).signum()!=0) :
			(o instanceof Number) ? (((Number)o).doubleValue()!=0) :
			(o instanceof Boolean) ? ((Boolean)o).booleanValue() :
			new Boolean(o.toString()).booleanValue();
	}

    /** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public boolean getBoolean(String columnName) throws SQLException {
		return getBoolean(findColumn(columnName));
	}

    /** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public byte getByte(int columnIndex) throws SQLException {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Byte) ? ((Byte)o).byteValue() :
			Byte.parseByte(o.toString());
	}
	
    /** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public byte getByte(String columnName) throws SQLException {
		return getByte(findColumn(columnName));
	}

    /** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public Date getDate(int columnIndex) throws SQLException {
		Object o = getObject(columnIndex);
		return (o==null) ? null :
			(o instanceof Date) ? (Date)o :
			Date.valueOf(o.toString());
	}

    /** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public Date getDate(String columnName) throws SQLException {
		return getDate(findColumn(columnName));
	}

    /** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public double getDouble(int columnIndex) throws SQLException {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Double) ? ((Double)o).doubleValue() :
			Double.parseDouble(o.toString());
	}
	
    /** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public double getDouble(String columnName) throws SQLException {
		return getDouble(findColumn(columnName));
	}
	
    /** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public float getFloat(int columnIndex) throws SQLException {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Float) ? ((Float)o).floatValue() :
			Float.parseFloat(o.toString());
	}
	
    /** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public float getFloat(String columnName) throws SQLException {
		return getFloat(findColumn(columnName));
	}
	
    /** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public int getInt(int columnIndex) throws SQLException {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Integer) ? ((Integer)o).intValue() :
			Integer.parseInt(o.toString());
	}
	
    /** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public int getInt(String columnName) throws SQLException {
		return getInt(findColumn(columnName));
	}
	
    /** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public long getLong(int columnIndex) throws SQLException {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Long) ? ((Long)o).longValue() :
			Long.parseLong(o.toString());
	}
	
    /** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public long getLong(String columnName) throws SQLException {
		return getLong(findColumn(columnName));
	}
	
    /** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public short getShort(int columnIndex) throws SQLException {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Short) ? ((Short)o).shortValue() :
			Short.parseShort(o.toString());
	}
	
    /** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public short getShort(String columnName) throws SQLException {
		return getShort(findColumn(columnName));
	}
	
    /** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public String getString(int columnIndex) throws SQLException {
		Object o = getObject(columnIndex);
		return (o==null) ? null : o.toString();
	}
	
    /** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public String getString(String columnName) throws SQLException {
		return getString(findColumn(columnName));
	}
	
    /** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public Time getTime(int columnIndex) throws SQLException {
		Object o = getObject(columnIndex);
		return (o==null) ? null :
			(o instanceof Time) ? (Time)o :
			Time.valueOf(o.toString());
	}
	
    /** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public Time getTime(String columnName) throws SQLException {
		return getTime(findColumn(columnName));
	}
	
    /** Casts a column value to the requested type.
     * @param columnIndex indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		Object o = getObject(columnIndex);
		return (o==null) ? null :
			(o instanceof Timestamp) ? (Timestamp)o :
			Timestamp.valueOf(o.toString());
	}
	
    /** Casts a column value to the requested type.
     * @param columnName indicates the column that should be casted.
     * @return the column value 
     * @throws SQLException
    */
	public Timestamp getTimestamp(String columnName) throws SQLException {
		return getTimestamp(findColumn(columnName));
	}


	/**
     * Not implemented yet.
     * @return an exception
	 * @throws SQLException
     */
	public String getCursorName() throws SQLException
		{ throw new SQLException("getCursorName()"); }
		
	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param cal the Calendar
	 * @return an exception
	 * @throws SQLException
	 */	
	public Date getDate(int columnIndex, Calendar cal) throws SQLException
		{ throw new SQLException("getDate(int, Calendar)"); }
	
	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param cal the Calendar
	 * @return an exception
	 * @throws SQLException
	 */
	public Date getDate(String columnName, Calendar cal) throws SQLException
		{ throw new SQLException("getDate(String Calendar)"); }
		
	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param cal the Calendar
	 * @return an exception
	 * @throws SQLException
	 */			
	public Time getTime(int columnIndex, Calendar cal) throws SQLException
		{ throw new SQLException("getTime(int, Calendar)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param cal the Calendar
	 * @return an exception
	 * @throws SQLException
	 */	
	public Time getTime(String columnName, Calendar cal) throws SQLException
		{ throw new SQLException("getTime(String, Calendar)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param cal the Calendar
	 * @return an exception
	 * @throws SQLException
	 */	
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException
		{ throw new SQLException("getTimestamp(int, Calendar)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param cal the Calendar
	 * @return an exception
	 * @throws SQLException
	 */	
	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException
		{ throw new SQLException("getTimestamp(String, Calendar)"); }

	/**
	 * Not implemented yet.
	 * @param row the row
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean absolute(int row) throws SQLException
		{ throw new SQLException("absolute(int)"); }

	/**
	 * Not implemented yet.
	 * @throws SQLException
	 */	
	public void afterLast() throws SQLException
		{ throw new SQLException("afterLast()"); }

	/**
	 * Not implemented yet.
	 * @throws SQLException
	 */	
	public void beforeFirst() throws SQLException
		{ throw new SQLException("beforeFirst()"); }

	/**
	 * Not implemented yet.
	 * @throws SQLException
	 */	
	public void cancelRowUpdates() throws SQLException
		{ throw new SQLException("cancelRowUpdates()"); }
		
	/**
	 * Not implemented yet.
	 * @throws SQLException
	 */	
	public void deleteRow() throws SQLException
		{ throw new SQLException("deleteRow()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean first() throws SQLException
		{ throw new SQLException("first()"); }

	/**
	 * Not implemented yet.
	 * @param i an integer
	 * @return an exception
	 * @throws SQLException
	 */	
	public Array getArray(int i) throws SQLException
		{ throw new SQLException("getArray(int)"); }

	/**
	 * Not implemented yet.
	 * @param colName the colName
	 * @return an exception
	 * @throws SQLException
	 */	
	public Array getArray(String colName) throws SQLException
		{ throw new SQLException("getArray(String)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @return an exception
	 * @throws SQLException
	 */	
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException
		{ throw new SQLException("getBigDecimal(int)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param scale the scale
	 * @return an exception
	 * @throws SQLException
	 */	
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException
		{ throw new SQLException("getBigDecimal(int, int)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @return an exception
	 * @throws SQLException
	 */	
	public BigDecimal getBigDecimal(String columnName) throws SQLException
		{ throw new SQLException("getBigDecimal(String)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param scale the scale
	 * @return an exception
	 * @throws SQLException
	 */	
	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException
		{ throw new SQLException("getBigDecimal(String, int)"); }

	/**
	 * Not implemented yet.
	 * @param i an integer
	 * @return an exception
	 * @throws SQLException
	 */	
	public Blob getBlob(int i) throws SQLException
		{ throw new SQLException("getBlob(int)"); }

	/**
	 * Not implemented yet.
	 * @param colName the colName
	 * @return an exception
	 * @throws SQLException
	 */	
	public Blob getBlob(String colName) throws SQLException
		{ throw new SQLException("getBlob(String)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @return an exception
	 * @throws SQLException
	 */	
	public Reader getCharacterStream(int columnIndex) throws SQLException
		{ throw new SQLException("getCharacterStream(int)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @return an exception
	 * @throws SQLException
	 */	
	public Reader getCharacterStream(String columnName) throws SQLException
		{ throw new SQLException("getCharacterStream(String)"); }

	/**
	 * Not implemented yet.
	 * @param i an integer
	 * @return an exception
	 * @throws SQLException
	 */	
	public Clob getClob(int i) throws SQLException
		{ throw new SQLException("getClob(int)"); }

	/**
	 * Not implemented yet.
	 * @param colName the colName
	 * @return an exception
	 * @throws SQLException
	 */	
	public Clob getClob(String colName) throws SQLException
		{ throw new SQLException("getClob(String)"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public int getConcurrency() throws SQLException
		{ throw new SQLException("getConcurrency()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public int getFetchDirection() throws SQLException
		{ throw new SQLException("getFetchDirection()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public int getFetchSize() throws SQLException
		{ throw new SQLException("getFetchSize()"); }

	/**
	 * Not implemented yet.
	 * @param i an integer
	 * @param map a Map
	 * @return an exception
	 * @throws SQLException
	 */	
	public Object getObject(int i, Map map) throws SQLException
		{ throw new SQLException("getObject(int, Map)"); }

	/**
	 * Not implemented yet.
	 * @param colName the colName
	 * @param map a Map
	 * @return an exception
	 * @throws SQLException
	 */	
	public Object getObject(String colName, Map map) throws SQLException
		{ throw new SQLException("getObject(String, Map)"); }

	/**
	 * Not implemented yet.
	 * @param i an integer
	 * @return an exception
	 * @throws SQLException
	 */	
	public Ref getRef(int i) throws SQLException
		{ throw new SQLException("getRef(int)"); }

	/**
	 * Not implemented yet.
	 * @param colName the colName
	 * @return an exception
	 * @throws SQLException
	 */	
	public Ref getRef(String colName) throws SQLException
		{ throw new SQLException("getRef(String)"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public int getRow() throws SQLException
		{ throw new SQLException("getRow()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public Statement getStatement() throws SQLException
		{ throw new SQLException("getStatement()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public int getType() throws SQLException
		{ throw new SQLException("getType()"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @return an exception
	 * @throws SQLException
	 */	
	public InputStream getUnicodeStream(int columnIndex) throws SQLException
		{ throw new SQLException("getUnicodeStream(int)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @return an exception
	 * @throws SQLException
	 */	
	public InputStream getUnicodeStream(String columnName) throws SQLException
		{ throw new SQLException("getUnicodeStream(String)"); }

	/**
	 * Not implemented yet.
	 * @throws SQLException
	 */	
	public void insertRow() throws SQLException
		{ throw new SQLException("insertRow()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean isAfterLast() throws SQLException
		{ throw new SQLException("isAfterLast()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean isBeforeFirst() throws SQLException
		{ throw new SQLException("isBeforeFirst()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean isFirst() throws SQLException
		{ throw new SQLException("isFirst()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean isLast() throws SQLException
		{ throw new SQLException("isLast()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean last() throws SQLException
		{ throw new SQLException("last()"); }

	/**
	 * Not implemented yet.
	 * @throws SQLException
	 */	
	public void moveToCurrentRow() throws SQLException
		{ throw new SQLException("moveToCurrentRow()"); }

	/**
	 * Not implemented yet.
	 * @throws SQLException
	 */	
	public void moveToInsertRow() throws SQLException
		{ throw new SQLException("moveToInsertRow()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean previous() throws SQLException
		{ throw new SQLException("previous()"); }

	/**
	 * Not implemented yet.
	 * @throws SQLException
	 */	
	public void refreshRow() throws SQLException
		{ throw new SQLException("refreshRow()"); }

	/**
	 * Not implemented yet.
	 * @param rows the rows
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean relative(int rows) throws SQLException
		{ throw new SQLException("relative(int)"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean rowDeleted() throws SQLException
		{ throw new SQLException("rowDeleted()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean rowInserted() throws SQLException
		{ throw new SQLException("rowInserted()"); }

	/**
	 * Not implemented yet.
	 * @return an exception
	 * @throws SQLException
	 */	
	public boolean rowUpdated() throws SQLException
		{ throw new SQLException("rowUpdated()"); }

	/**
	 * Not implemented yet.
	 * @param direction the direction
	 * @throws SQLException
	 */	
	public void setFetchDirection(int direction) throws SQLException
		{ throw new SQLException("setFetchDirection(int)"); }

	/**
	 * Not implemented yet.
	 * @param rows the rows
	 * @throws SQLException
	 */	
	public void setFetchSize(int rows) throws SQLException
		{ throw new SQLException("setFetchSize(int)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x an InputStream
	 * @param length the length
	 * @throws SQLException
	 */	
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException
		{ throw new SQLException("updateAsciiStream(int, InputStream, int)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x an InputStream
	 * @param length the length
	 * @throws SQLException
	 */	
	public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException
		{ throw new SQLException("updateAsciiStream(String, InputStream, int)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a BigDecimal
	 * @throws SQLException
	 */		
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
		{ throw new SQLException("updateBigDecimal(int, BigDecimal)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a BigDecimal
	 * @throws SQLException
	 */		
	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException
		{ throw new SQLException("updateBigDecimal(String, BigDecimal)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x an InputStream
	 * @param length the length
	 * @throws SQLException
	 */	
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException
		{ throw new SQLException("updateBinaryStream(int, InputStream, int)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x an InputStream
	 * @param length the length
	 * @throws SQLException
	 */	
	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException
		{ throw new SQLException("updateBinaryStream(String, InputStream, int)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a Boolean
	 * @throws SQLException
	 */	
	public void updateBoolean(int columnIndex, boolean x) throws SQLException
		{ throw new SQLException("updateBoolean(int, boolean)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a Boolean
	 * @throws SQLException
	 */	
	public void updateBoolean(String columnName, boolean x) throws SQLException
		{ throw new SQLException("updateBoolean(String, boolean)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a byte
	 * @throws SQLException
	 */	
	public void updateByte(int columnIndex, byte x) throws SQLException
		{ throw new SQLException("updateByte(int, byte)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a byte
	 * @throws SQLException
	 */	
	public void updateByte(String columnName, byte x) throws SQLException
		{ throw new SQLException("updateByte(String, byte)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a byte-array
	 * @throws SQLException
	 */	
	public void updateBytes(int columnIndex, byte[] x) throws SQLException
		{ throw new SQLException("updateBytes(int, byte[])"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a byte-array
	 * @throws SQLException
	 */	
	public void updateBytes(String columnName, byte[] x) throws SQLException
		{ throw new SQLException("updateBytes(String, byte[])"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a Reader
	 * @param length the length
	 * @throws SQLException
	 */	
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException
		{ throw new SQLException("updateCharacterStream(int, Reader, int)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param reader a Reader
	 * @param length the length
	 * @throws SQLException
	 */	
	public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException
		{ throw new SQLException("updateCharacterStream(String, Reader, int)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a Date
	 * @throws SQLException
	 */	
	public void updateDate(int columnIndex, Date x) throws SQLException
		{ throw new SQLException("updateDate(int, Date)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a Date
	 * @throws SQLException
	 */	
	public void updateDate(String columnName, Date x) throws SQLException
		{ throw new SQLException("updateDate(String, Date)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a double
	 * @throws SQLException
	 */	
	public void updateDouble(int columnIndex, double x) throws SQLException
		{ throw new SQLException("updateDouble(int, double)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a double
	 * @throws SQLException
	 */	
	public void updateDouble(String columnName, double x) throws SQLException
		{ throw new SQLException("updateDouble(String, double)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a float
	 * @throws SQLException
	 */	
	public void updateFloat(int columnIndex, float x) throws SQLException
		{ throw new SQLException("updateFloat(int, float)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a float
	 * @throws SQLException
	 */	
	public void updateFloat(String columnName, float x) throws SQLException
		{ throw new SQLException("updateFloat(String, float)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x an integer
	 * @throws SQLException
	 */	
	public void updateInt(int columnIndex, int x) throws SQLException
		{ throw new SQLException("updateInt(int, int)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x an integer
	 * @throws SQLException
	 */	
	public void updateInt(String columnName, int x) throws SQLException
		{ throw new SQLException("updateInt(String, int)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a long
	 * @throws SQLException
	 */	
	public void updateLong(int columnIndex, long x) throws SQLException
		{ throw new SQLException("updateLong(int, long)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a long
	 * @throws SQLException
	 */	
	public void updateLong(String columnName, long x) throws SQLException
		{ throw new SQLException("updateLong(String, long)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @throws SQLException
	 */	
	public void updateNull(int columnIndex) throws SQLException
		{ throw new SQLException("updateNull(int)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @throws SQLException
	 */	
	public void updateNull(String columnName) throws SQLException
		{ throw new SQLException("updateNull(String)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x an Object
	 * @throws SQLException
	 */	
	public void updateObject(int columnIndex, Object x) throws SQLException
		{ throw new SQLException("updateObject(int, Object)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x an Object
	 * @param scale the scale
	 * @throws SQLException
	 */	
	public void updateObject(int columnIndex, Object x, int scale) throws SQLException
		{ throw new SQLException("updateObject(int, Object, int)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x an Object
	 * @throws SQLException
	 */	
	public void updateObject(String columnName, Object x) throws SQLException
		{ throw new SQLException("updateObject(String, Object)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x an Object
	 * @param scale the scale
	 * @throws SQLException
	 */	
	public void updateObject(String columnName, Object x, int scale) throws SQLException
		{ throw new SQLException("updateObject(String, Object, int)"); }

	/**
	 * Not implemented yet.
	 * @throws SQLException
	 */	
	public void updateRow() throws SQLException
		{ throw new SQLException("updateRow()"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a short
	 * @throws SQLException
	 */	
	public void updateShort(int columnIndex, short x) throws SQLException
		{ throw new SQLException("updateShort(int, short)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a short
	 * @throws SQLException
	 */	
	public void updateShort(String columnName, short x) throws SQLException
		{ throw new SQLException("updateShort(String, short)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a String
	 * @throws SQLException
	 */	
	public void updateString(int columnIndex, String x) throws SQLException
		{ throw new SQLException("updateString(int, String)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a String
	 * @throws SQLException
	 */	
	public void updateString(String columnName, String x) throws SQLException
		{ throw new SQLException("updateString(String, String)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a Time type
	 * @throws SQLException
	 */	
	public void updateTime(int columnIndex, Time x) throws SQLException
		{ throw new SQLException("updateTime(int, Time)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a Time type
	 * @throws SQLException
	 */	
	public void updateTime(String columnName, Time x) throws SQLException
		{ throw new SQLException("updateTime(String, Time)"); }

	/**
	 * Not implemented yet.
	 * @param columnIndex the columnIndex
	 * @param x a Timestamp
	 * @throws SQLException
	 */	
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
		{ throw new SQLException("updateTimestamp(int, Timestamp)"); }

	/**
	 * Not implemented yet.
	 * @param columnName the columnName
	 * @param x a Timestamp
	 * @throws SQLException
	 */	
	public void updateTimestamp(String columnName, Timestamp x) throws SQLException
		{ throw new SQLException("updateTimestamp(String, Timestamp)"); }

	// for compatibility with SDK 1.4
	/**
	 * Not implemented yet.
	 * @param x an integer
	 * @return an exception
	 * @throws SQLException
	 */		
	public java.net.URL getURL(int x) throws SQLException
		{ throw new SQLException("URL getURL(int)"); }

	/**
	 * Not implemented yet.
	 * @param a a java.lang.String
	 * @param b a java.lang.Array
	 * @throws SQLException
	 */	
	public void updateArray(java.lang.String a,java.sql.Array b) throws SQLException
		{ throw new SQLException("updateArray(java.lang.String,java.sql.Array)"); }

	/**
	 * Not implemented yet.
	 * @param a an integer
	 * @param b a java.lang.Array
	 * @throws SQLException
	 */	
	public void updateArray(int a,java.sql.Array b) throws SQLException
		{ throw new SQLException("updateArray(int,java.sql.Array)"); }

	/**
	 * Not implemented yet.
	 * @param s a java.lang.String
	 * @return an exception
	 * @throws SQLException
	 */	
	public java.net.URL getURL(java.lang.String s) throws SQLException
		{ throw new SQLException("URL getURL(int)"); }

	/**
	 * Not implemented yet.
	 * @param a an integer
	 * @param b a java.sql.Ref
	 * @throws SQLException
	 */	
	public void updateRef(int a,java.sql.Ref b) throws SQLException
		{ throw new SQLException("updateRef(int,java.sql.Ref)"); }

	/**
	 * Not implemented yet.
	 * @param a a java.lang.String
	 * @param b a java.sql.Ref
	 * @throws SQLException
	 */	
	public void updateRef(java.lang.String a,java.sql.Ref b) throws SQLException
		{ throw new SQLException("updateRef(java.lang.String,java.sql.Ref)"); }

	/**
	 * Not implemented yet.
	 * @param a an integer
	 * @param b a java.sql.Blob
	 * @throws SQLException
	 */	
	public void  updateBlob(int a,java.sql.Blob b) throws SQLException
		{ throw new SQLException("updateBlob(int,java.sql.Blob)"); }

	/**
	 * Not implemented yet.
	 * @param a a java.lang.String
	 * @param b a java.sql.Blob
	 * @throws SQLException
	 */	
	public void  updateBlob(java.lang.String a,java.sql.Blob b) throws SQLException
		{ throw new SQLException("updateBlob(String,java.sql.Blob)"); }

	/**
	 * Not implemented yet.
	 * @param a an integer
	 * @param b a java.sql.Clob
	 * @throws SQLException
	 */	
	public void updateClob(int a,java.sql.Clob b) throws SQLException
		{ throw new SQLException("updateClob(int,java.sql.Clob)"); }

	/**
	 * Not implemented yet.
	 * @param a a String
	 * @param b a java.sql.Clob
	 * @throws SQLException
	 */	
	public void updateClob(String a,java.sql.Clob b) throws SQLException
		{ throw new SQLException("updateClob(String,java.sql.Clob)"); }
}
