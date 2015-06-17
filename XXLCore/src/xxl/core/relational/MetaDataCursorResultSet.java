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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.NClob;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.NoSuchElementException;

import xxl.core.cursors.MetaDataCursor;
import xxl.core.io.converters.Converter;
import xxl.core.io.converters.SerializableConverter;
import xxl.core.util.WrappingRuntimeException;

/**
 *	This class wraps a MetaDataCursor to a ResultSet.
 *	ResultSet and MetaDataCursor are equivalent concepts.
 *	Both handle sets of objects with metadata.
 *	The different direction is done by the class
 *	{@link xxl.core.relational.cursors.ResultSetMetaDataCursor}.
 */
public class MetaDataCursorResultSet extends AbstractResultSet {

	/** MetaDataCursor that is wrapped into a ResultSet */
	protected MetaDataCursor metaDataCursor = null;
	/** Metadata that is used */
	protected ResultSetMetaData metaData = null;
	/** Contains the next Tuple */
	protected Tuple next;
	/** Contains the index of the last column that became accessed */
	protected int lastColumnIndex = 0;

	/**
	 * Constructs a Wrapper that wraps a MetaDataCursor into a ResultSet.
	 *
	 * @param metaDataCursor MetaDataCursor that is wrapped into a ResultSet
	 */
	public MetaDataCursorResultSet (MetaDataCursor metaDataCursor) {
		this.metaDataCursor = metaDataCursor;
		this.metaData = (ResultSetMetaData)metaDataCursor.getMetaData();
	}

	/** 
	 * Retrieves the number, types and properties of a ResultSet's columns.
	 *
     	 * @return the description of a ResultSet's columns
     	 * @throws java.sql.SQLException if a database access error occurs
     	 */
	public ResultSetMetaData getMetaData () throws SQLException {
		return metaData;
	}

     	/**
	 * Maps the given Resultset column name to its ResultSet column index.
	 *
     	 * @param columnName the name of the column
     	 * @return the column index
     	 * @throws java.sql.SQLException if the name cannot be found	
     	 */
	public int findColumn (String columnName) throws SQLException {
		for (int i = 1; i <= metaData.getColumnCount(); i++)
			if(metaData.getColumnName(i).equals(columnName))
				return i;
		throw new SQLException("Column with specified columnName cannot be found.");
	}

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
     	 * to materialize data of SQL user-defined types. When the a column contains a 
     	 * structured or distinct value, the behavior of this method is as if it were a 
     	 * call to: getObject(columnIndex, this.getStatement().getConnection().getTypeMap()).
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a java.lang.Object holding the column value
     	 * @throws java.sql.SQLException if a database access error occurs
     	 */
	public Object getObject (int columnIndex) throws SQLException {
		lastColumnIndex = columnIndex;
		return next.getObject(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a Java byte array.
     	 *
	 * To serialize an object, the <code>DEFAULT_INSTANCE</code> of
	 * {@link xxl.core.io.converters.SerializableConverter} is used.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return the column value; if the value is SQL NULL, the result is null
     	 * @throws SQLException - if an error occurs during deserialization
     	 */
	public byte[] getBytes (int columnIndex) throws SQLException {
		return getBytes(columnIndex, SerializableConverter.DEFAULT_INSTANCE);
	}

	/**
	 * Gets the value of a column in the current row as a Java byte array.
     	 *
	 * To serialize an object, a {@link xxl.core.io.converters.Converter} object can be
	 * passed to the call of this method.
	 * <b>This method is not included in the {@link java.sql.ResultSet} Interface.</b>
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @param converter Converter object that is used to deserialize the object
     	 * @return the column value; if the value is SQL NULL, the result is null
     	 * @throws SQLException - if an error occurs during deserialization
     	 */
	public byte[] getBytes (int columnIndex, Converter converter) throws SQLException {
		try {
			java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
			converter.write(new java.io.ObjectOutputStream(output), getObject(columnIndex));
			byte[] result = output.toByteArray();
			output.close();
			return result;
		}
		catch (IOException ie) {
			throw new WrappingRuntimeException(ie);
		}
	}

	/** 
	 * Moves the cursor down one row from its current position. A ResultSet cursor is 
	 * initially positioned before the first row; the first call to next makes the first 
	 * row the current row; the second call makes the second row the current row, and so on. 
	 * 
     	 * @return true if the new current row is valid; false if there are no more rows
     	 * @throws SQLException
     	 */
	public boolean next () throws SQLException {
		if(metaDataCursor.hasNext()) {
			next = (Tuple)metaDataCursor.next();
			return true;
		}
		return false;
	}

	/** 
	 * Closes the ResultSet's underlying MetaDataCursor and releases its resources.
	 * 
	 * @throws SQLException
	 */
	public void close () throws SQLException {
		metaDataCursor.close();
	}

	/**
	 * Reports whether the last column read had a value of SQL NULL. Note that you must 
	 * first call getXXX on a column to try to read its value and then call wasNull() 
	 * to see if the value read was SQL NULL.
     	 *
     	 * @return true if last column read was SQL NULL and false otherwise
     	 * @throws SQLException
     	 */
	public boolean wasNull () throws SQLException {
		return next.getObject(lastColumnIndex) == null ? true : false;
	}

	/**
	 * Updates the designated column with an Object value. The updateXXX methods are 
	 * used to update column values in the current row or the insert row. The updateXXX 
	 * methods do not update the underlying database; instead the updateRow or insertRow 
	 * methods are called to update the database.
	 *
	 * <b>So far, this method is not supported by XXL. A call to this method will
	 * result in an SQLException.</b>
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param object the new column value
	 * @throws SQLException in every case!
	 */
	public void updateObject (int columnIndex, Object object) throws SQLException {
		throw new SQLException();
	}

	/**
	 * Updates the designated column with an Object value. The updateXXX methods are 
	 * used to update column values in the current row or the insert row. The updateXXX 
	 * methods do not update the underlying database; instead the updateRow or insertRow 
	 * methods are called to update the database.
	 *
	 * <b>So far, this method is not supported by XXL. A call to this method will
	 * result in an SQLException.</b>
	 *
	 * @param columnName name of the column
	 * @param object the new column value
	 * @throws SQLException in every case!
	 */
	public void updateObject (String columnName, Object object) throws SQLException {
		updateObject(findColumn(columnName), object);
	}

	/**
	 * Deletes the current row from this ResultSet object and from the underlying 
	 * MetaDataCursor. This method cannot be called when the cursor is on the insert row.
	 *
	 * This call is redirected to the remove method of the underlying MetaDataCursor.
	 *
	 * @throws xxl.core.util.WrappingRuntimeException the underlying cursor does not
	 *	contain an object anymore.
	 * @throws SQLException
	 */
	public void deleteRow () throws SQLException {
		if(!metaDataCursor.hasNext())
			throw new WrappingRuntimeException(new NoSuchElementException());
		else
			metaDataCursor.remove();
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

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
