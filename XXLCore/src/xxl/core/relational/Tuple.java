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

import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * 	A Tuple is a hard copy (materialization) of an (existing) tuple of a ResultSet.
 *	<p>
 * 
 *	The getXXX methods retrieve the column values like ResultSet does. You can retrieve
 *	values using either the index number of the column or the name of the column. In
 *	general, using the column index will be more efficient. Columns are numbered from 1.
 *	<P>
 * 
 *	A Tuple throws only RuntimeExceptions.<br>
 *	If a column is requested, that does not exist, an <code>IndexOutOfBoundsException</code>
 *	is thrown.
 *	<P>
 *
 *	Empty Tuples are not allowed.<br>
 *	By creating a Tuple from a ResultSet, it must be ensured, that the ResultSet is not
 *	empty. If so, the constructor of the Tuple-class throws a RuntimeException.
 */
public interface Tuple {

	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a boolean representation of the column object.
     	 */
	public boolean getBoolean(int columnIndex);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return a boolean representation of the column object.
     	 */
	public boolean getBoolean(String columnName);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a byte representation of the column object.
     	 */
	public byte getByte(int columnIndex);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return a byte representation of the column object.
     	 */
	public byte getByte(String columnName);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a Date representation of the column object.
     	 */
	public Date getDate(int columnIndex);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return a Date representation of the column object.
     	 */
	public Date getDate(String columnName);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a double representation of the column object.
     	 */
	public double getDouble(int columnIndex);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return a double representation of the column object.
     	 */
	public double getDouble(String columnName);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a float representation of the column object.
     	 */
	public float getFloat(int columnIndex);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return a float representation of the column object.
     	 */
	public float getFloat(String columnName);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return an int representation of the column object.
     	 */
	public int getInt(int columnIndex);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return an int representation of the column object.
     	 */
	public int getInt(String columnName);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a long representation of the column object.
     	 */
	public long getLong(int columnIndex);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return a long representation of the column object.
     	 */
	public long getLong(String columnName);
	/** 
	 * Returns the metadata the tuple obbeys.
	 *
	 * @return the metadata
	 */
	public ResultSetMetaData getMetaData();
	/** 
	 * Returns the object of the column that matches the given columnName.
	 *
     * @param columnIndex the column name 
	 * @return object of the column
	 */
	public Object getObject(int columnIndex);
	/** 
	 * Returns the object of the column that matches the given columnName.
	 *
     * @param columnName the first column is 1, the second is 2, ...
	 * @return object of the column
	 */
	public Object getObject(String columnName);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a short representation of the column object.
     	 */
	public short getShort(int columnIndex);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return a short representation of the column object.
     	 */
	public short getShort(String columnName);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a String representation of the column object.
     	 */
	public String getString(int columnIndex);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return a String representation of the column object.
     	 */
	public String getString(String columnName);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a Time representation of the column object.
     	 */
	public Time getTime(int columnIndex);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return a Time representation of the column object.
     	 */
	public Time getTime(String columnName);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return a Timestamp representation of the column object.
     	 */
	public Timestamp getTimestamp(int columnIndex);
	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return a Timestamp representation of the column object.
     	 */
	public Timestamp getTimestamp(String columnName);
	/**
	 * Compares the column object to <code>null</code>.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
     	 * @return true if the column object is equal to <code>null</code>.
     	 */
	public boolean isNull(int columnIndex);
	/**
	 * Compares the column object to <code>null</code>.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name 
     	 * @return true if the column object is equal to <code>null</code>.
     	 */
	public boolean isNull(String columnName);
}
