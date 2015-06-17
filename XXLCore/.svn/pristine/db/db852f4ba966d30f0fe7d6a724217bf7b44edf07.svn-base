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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import xxl.core.util.WrappingRuntimeException;

/**
 *	This class is a skeleton-implementation for Tuple.
 *	<p>
 *
 *	The constructor of this class calls the getObject method for each column from the given
 *	input-ResultSet (which must not be empty).
 *	and adds the returned Objects to a List.
 *
 *	<P>
 *
 *	Two column values <code>x</code> and <code>y</code> are equal, if they are both
 *	<code>null</code> or they are both not <code>null</code> and <code>x.equals(y)</code>
 *	holds.
 *
 *	Tuples are equal, if they have the same number of columns and they are equal on
 *	each column value.<br>
 *
 *	<P>
 *
 *	The getXXX methods retrieve the values with the given index from the internal List, and
 *	return the casted result.
 *
 */
public abstract class AbstractTuple implements Tuple {

	/**Object containing metadata*/
	protected ResultSetMetaData metaData;

	/**
	 * Uses the metadata from a ResultSetMetaData object.
	 *
	 * @param metaData object containing metadata
	 */
	public AbstractTuple (ResultSetMetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * Uses the metadata from a ResultSet object. The method getMetaData
	 * is used to get the metadata.
	 *
	 * @param resultSet ResultSet object from which the metadata should
	 * 	be used.
	 * @throws xxl.core.util.WrappingRuntimeException if the ResultSet does not
	 *	support a call to getMetaData.
	 */
	public AbstractTuple (ResultSet resultSet) {
		try {
			metaData = resultSet.getMetaData();
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Provides access to the packed objects in the Tuple.
	 * Abstract method that has to be overwritten to construct a non-abstract
	 * tuple.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return column object
	 * @see xxl.core.relational.ArrayTuple
	 * @see xxl.core.relational.ListTuple
	 */
	public abstract Object getObject (int columnIndex);

	/**
	 * Copies the objects of the tuple into a new object array.
	 *
	 * @return array containing the objects of the tuple
	 * @throws xxl.core.util.WrappingRuntimeException if the operation could not
	 *	be performed correctly.
	 */
	public Object[] getObjectArray () {
		try {
			Object[] result = new Object[metaData.getColumnCount()];
			for (int i = 0; i < result.length; i++)
				result[i] = getObject(i+1);
			return result;
		}
		catch (SQLException se) {
			throw new WrappingRuntimeException(se);
		}
	}

	/**
	 * Compares two tuples.
	 *
	 * Tuples are equal, if they have the same number of columns and they are equal on
	 * each column value.<br>
	 *
	 * Two column values <code>x</code> and <code>y</code> are equal, if they are both
	 * <code>null</code> or they are both not <code>null</code> and <code>x.equals(y)</code>
 	 * holds.<br>
 	 *
 	 * Due to incompatibilities of different database systems the metadata does not
 	 * have to match.
 	 *
 	 * @param o object with which the current object is compared
 	 * @return true if the two objects are equal in the sense explained above.
	 * @throws xxl.core.util.WrappingRuntimeException if an SQLException occurs while doing the task.
	 */
	public boolean equals (Object o) {
		try {
			if (o == null)
				return false;
			Tuple t = (Tuple)o;
			ResultSetMetaData metaData2 = t.getMetaData();
			if (metaData != metaData2) {
				if (metaData.getColumnCount() != metaData2.getColumnCount())
					return false;
				/* for (int i = 1; i <= metaData.getColumnCount(); i++)
					if (!metaData.getColumnTypeName(i).equals(metaData2.getColumnTypeName(i)) ||
						metaData.getPrecision(i)!=metaData2.getPrecision(i) ||
						metaData.getScale(i)!=metaData2.getScale(i))
							return false;*/
			}
			Object o1, o2;
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				o1 = getObject(i);
				o2 = t.getObject(i);
				// System.out.println(o1.getClass().getName()+" - "+o2.getClass().getName());
				if ((o1!=null && !o1.equals(o2)) || (o1==null && o2!=null)) {
					// System.out.println(o1+" - "+o2);
					return false;
				}
			}
			return true;
		}
		catch (ClassCastException e) {
			return false;
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

    	/**
	 * Maps the given column name to its ResultSet column index. This method
	 * needs O(number of columns) time!
	 *
 	 * @param columnName the column name
 	 * @return the column index
 	 * @throws xxl.core.util.WrappingRuntimeException if no column of the specified name is found
 	 */
	public int findColumn(String columnName) {
		try {
			for(int i = 1; i <= metaData.getColumnCount(); i++)
				if(metaData.getColumnName(i).equalsIgnoreCase(columnName))
					return i;
			throw new SQLException("No column with specified columnName found.");
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Returns the object of the column that matches the given columnName.
	 *
     * @param columnName the column name
	 * @return object of the column
	 */
	public Object getObject(String columnName) {
		return getObject(findColumn(columnName));
	}

	/**
	 * Compares the column object to <code>null</code>.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return true if the column object is equal to <code>null</code>.
 	 */
	public boolean isNull(int columnIndex) {
		return getObject(columnIndex)==null;
	}

	/**
	 * Compares the column object to <code>null</code>.
	 * This method uses findColumn internally!
	 *
 	 * @param columnName the column name
 	 * @return true if the column object is equal to <code>null</code>.
 	 */
	public boolean isNull(String columnName) {
		return isNull(findColumn(columnName));
	}

	/**
	 * Returns the metadata the tuple obeys.
	 *
	 * @return the metadata
	 */
	public ResultSetMetaData getMetaData() {
		return metaData;
	}

	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return a boolean representation of the column object.
 	 */
	public boolean getBoolean(int columnIndex) {
		Object o = getObject(columnIndex);
		return (o==null) ? false :
			(o instanceof BigDecimal) ? (((BigDecimal)o).signum()!=0) :
			(o instanceof Number) ? (((Number)o).doubleValue()!=0) :
			(o instanceof Boolean) ? ((Boolean)o).booleanValue() :
			new Boolean(o.toString()).booleanValue();
	}

	/**
	 * Column access method that corresponds to getBoolean in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
 	 * @param columnName the column name
 	 * @return a boolean representation of the column object.
 	 */
	public boolean getBoolean(String columnName) {
		return getBoolean(findColumn(columnName));
	}

	/**
	 * Column access method that corresponds to getByte in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return a byte representation of the column object.
 	 */
	public byte getByte(int columnIndex) {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Byte) ? ((Byte)o).byteValue() :
			Byte.parseByte(o.toString());
	}

	/**
	 * Column access method that corresponds to getByte in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
 	 * @param columnName the column name
 	 * @return a byte representation of the column object.
 	 */
	public byte getByte(String columnName) {
		return getByte(findColumn(columnName));
	}

	/**
	 * Column access method that corresponds to getDate in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return a Date representation of the column object.
 	 */
	public Date getDate(int columnIndex) {
		Object o = getObject(columnIndex);
		return (o==null) ? null :
			(o instanceof Date) ? (Date)o :
			Date.valueOf(o.toString());
	}

	/**
	 * Column access method that corresponds to getDate in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
 	 * @param columnName the column name
 	 * @return a Date representation of the column object.
 	 */
	public Date getDate(String columnName) {
		return getDate(findColumn(columnName));
	}

	/**
	 * Column access method that corresponds to getDouble in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return a double representation of the column object.
 	 */
	public double getDouble(int columnIndex) {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Double) ? ((Double)o).doubleValue() :
			Double.parseDouble(o.toString());
	}

	/**
	 * Column access method that corresponds to getDouble in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
 	 * @param columnName the column name
 	 * @return a double representation of the column object.
 	 */
	public double getDouble(String columnName) {
		return getDouble(findColumn(columnName));
	}

	/**
	 * Column access method that corresponds to getFloat in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return a float representation of the column object.
 	 */
	public float getFloat(int columnIndex) {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Float) ? ((Float)o).floatValue() :
			Float.parseFloat(o.toString());
	}

	/**
	 * Column access method that corresponds to getFloat in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
 	 * @param columnName the column name
 	 * @return a float representation of the column object.
 	 */
	public float getFloat(String columnName) {
		return getFloat(findColumn(columnName));
	}

	/**
	 * Column access method that corresponds to getInt in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return an int representation of the column object.
 	 */
	public int getInt(int columnIndex) {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Integer) ? ((Integer)o).intValue() :
			Integer.parseInt(o.toString());
	}

	/**
	 * Column access method that corresponds to getInt in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
     	 * @param columnName the column name
     	 * @return an int representation of the column object.
     	 */
	public int getInt(String columnName) {
		return getInt(findColumn(columnName));
	}

	/**
	 * Column access method that corresponds to getLong in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return a long representation of the column object.
 	 */
	public long getLong(int columnIndex) {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Long) ? ((Long)o).longValue() :
			Long.parseLong(o.toString());
	}

	/**
	 * Column access method that corresponds to getLong in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
 	 * @param columnName the column name
 	 * @return a long representation of the column object.
 	 */
	public long getLong(String columnName) {
		return getLong(findColumn(columnName));
	}

	/**
	 * Column access method that corresponds to getShort in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return a short representation of the column object.
 	 */
	public short getShort(int columnIndex) {
		Object o = getObject(columnIndex);
		return (o==null) ? 0 :
			(o instanceof Short) ? ((Short)o).shortValue() :
			Short.parseShort(o.toString());
	}

	/**
	 * Column access method that corresponds to getShort in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
 	 * @param columnName the column name
 	 * @return a short representation of the column object.
 	 */
	public short getShort(String columnName) {
		return getShort(findColumn(columnName));
	}

	/**
	 * Column access method that corresponds to getString in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return a String representation of the column object.
 	 */
	public String getString(int columnIndex) {
		Object o = getObject(columnIndex);
		return (o==null) ? null : o.toString();
	}

	/**
	 * Column access method that corresponds to getString in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
 	 * @param columnName the column name
 	 * @return a String representation of the column object.
 	 */
	public String getString(String columnName) {
		return getString(findColumn(columnName));
	}

	/**
	 * Column access method that corresponds to getTime in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return a Time representation of the column object.
 	 */
	public Time getTime(int columnIndex) {
		Object o = getObject(columnIndex);
		return (o==null) ? null :
			(o instanceof Time) ? (Time)o :
			Time.valueOf(o.toString());
	}

	/**
	 * Column access method that corresponds to getTime in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
 	 * @param columnName the column name
 	 * @return a Time representation of the column object.
 	 */
	public Time getTime(String columnName) {
		return getTime(findColumn(columnName));
	}

	/**
	 * Column access method that corresponds to getTimestamp in {@link java.sql.ResultSet java.sql.ResultSet}.
	 *
 	 * @param columnIndex the first column is 1, the second is 2, ...
 	 * @return a Timestamp representation of the column object.
 	 */
	public Timestamp getTimestamp(int columnIndex) {
		Object o = getObject(columnIndex);
		return (o==null) ? null :
			(o instanceof Timestamp) ? (Timestamp)o :
			Timestamp.valueOf(o.toString());
	}

	/**
	 * Column access method that corresponds to getTimestamp in {@link java.sql.ResultSet java.sql.ResultSet}.
	 * This method uses findColumn internally!
	 *
 	 * @param columnName the column name
 	 * @return a Timestamp representation of the column object.
 	 */
	public Timestamp getTimestamp(String columnName) {
		return getTimestamp(findColumn(columnName));
	}

	/**
	 * Outputs the content of the Tuple inclusive column names
	 *
	 * @return String representation of a tuple.
	 */
	public String toString() {
		String s = new String();
		try {
			for (int i=0 ; i<metaData.getColumnCount() ; i++) {
				s = s.concat(metaData.getColumnName(i+1)+": ");
				Object o = getObject(i+1);
				if (o!=null)
					s = s.concat(o.toString()+" ");
				else
					s = s.concat("NULL ");
			}
		}
		catch (SQLException e) {
		}
		return s;
	}
}
