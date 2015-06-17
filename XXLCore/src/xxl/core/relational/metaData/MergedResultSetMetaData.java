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
 *	wraps two given ResultSetMetaData objects.
 *
 *	This is useful especially for Joins.
 *	There are four abstract methods:
 *	<ul>
 *		<li><code>int <B>getColumnCount</B>()</code></li>
 *		<li><code>int <B>originalMetaData</B>(int column)</code></li>
 *		<li><code>int <B>originalColumnIndexFrom1</B>(int column)</code></li>
 *		<li><code>int <B>originalColumnIndexFrom2</B>(int column)</code></li>
 *	</ul>
 *
 *	These functions define a mapping between the original metadata objects
 *	and an object of this class.
 *
 *	For each call to <code>getXXX(int column)</code> it is first called
 *	<code>originalMetaData(column)</code> to decide whether the column
 *	<ul>
 *		<li>belongs to the first MetaData or</li>
 *		<li>belongs to the second MetaData or</li>
 *		<li>it is a merged(computed) column.</li>
 *	</ul>
 *
 *	For columns that belong to the first(second) metadata the
 *	<code>getXXX(int column)</code>-call is	redirected to 
 *	<code>getXXX(originalColumnIndexFrom1(column))</code>
 *	(<code>originalColumnIndexFrom2</code> respectively).<BR>
 *	For merged columns the behaviour of a method depends on the kind
 *	of underlying column. 
 *	For example <code>columnDisplaySize</code> is the maximum of the 
 *	<codecolumnDisplaySizes</code> of the corresponding columns from both 
 *	underlying MetaData objects.
 */
public abstract class MergedResultSetMetaData implements ResultSetMetaData {
	
	/**The first ResultSetMetaData object*/
	protected ResultSetMetaData metaData1;
	/**The second ResultSetMetaData object*/
	protected ResultSetMetaData metaData2;

	/**
	 * Constructs an MergedResultSetMetaData object that wraps two given
	 * ResultSetMetaData objects. 
	 *
	 * @param metaData1 First ResultSetMetaData object
	 * @param metaData2 Second ResultSetMetaData object
	 */

	public MergedResultSetMetaData (ResultSetMetaData metaData1, ResultSetMetaData metaData2) {
		this.metaData1 = metaData1;
		this.metaData2 = metaData2;
	}

	/**
	 * Returns if the column is originated in the first (1), the second (2) or in both
	 * (0) underlying ResultSetMetaData objects. If you derive a class from
	 * this class, this method defines your mapping from the columns
	 * of the two ResultSetMetaData objects to the MergedResultSetMetaData object.
	 *
	 * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return returns 1 or 2 if the column is originated in the first respectively second ResultSetMetaData object.
	 *	Returns 0 if the column is originated in both ResultSetMetaData object.
	 * @throws SQLException
	 */
	public abstract int originalMetaData(int column) throws SQLException;
	
	/**
	 * Determines the original column number from the first underlying ResultSetMetaData 
	 * object, on which the column of this object is based.
	 *
	 * @param column number of the column: the first column is 1, the second is 2, ...
	 *	If the colum does not originate in the first ResultSetMetaData object,
	 *	the return value has to be 0
	 * @return column number of the first ResultSetMetaData
	 * @throws SQLException
	 */
	public abstract int originalColumnIndexFrom1(int column) throws SQLException;
	
	/**
	 * Determines the original column number from the second underlying ResultSetMetaData 
	 * object, on which the column of this object is based.
	 *
	 * @param column number of the column: the first column is 1, the second is 2, ...
	 *	If the colum does not originate in the second ResultSetMetaData object,
	 *	the return value has to be 0
	 * @return column number of the second ResultSetMetaData
	 * @throws SQLException
	 */
	public abstract int originalColumnIndexFrom2(int column) throws SQLException;

	/**
	 * Returns the number of columns of this ResultSetMetaData object.
	 *
	 * @return the number of columns of this ResultSetMetaData object
	 * @throws SQLException
	 */
	public abstract int getColumnCount() throws SQLException;

	/**
	 * Returns the name the catalog. If the column is originated in the
	 * second metadata object, this call is redirected to it - else it is
	 * redirected to the first metadata object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return name of the catalog
	 * @throws SQLException
	 */	
	public String getCatalogName(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 2:  return metaData2.getCatalogName(originalColumnIndexFrom2(column));
			default: return metaData1.getCatalogName(originalColumnIndexFrom1(column));
		}
	}

	/**
	 * Returns the name the Java class that is associated with the column type. 
	 * If the column is originated in the
	 * second metadata object, this call is redirected to it - else it is
	 * redirected to the first metadata object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return name of the column class
	 * @throws SQLException
	 */	
	public String getColumnClassName(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 2:  return metaData2.getColumnClassName(originalColumnIndexFrom2(column));
			default: return metaData1.getColumnClassName(originalColumnIndexFrom1(column));
		}
	}

	/**
	 * Returns the display size of the column. If the column is originated in the
	 * first/second metadata object, this call is redirected to the first/second 
	 * metadata object. If the column is originated in both metadata objects, the
	 * display size is the maximum of both display sizes.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return display size of the column
	 * @throws SQLException
	 */	
	public int getColumnDisplaySize(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 1:  return metaData1.getColumnDisplaySize(originalColumnIndexFrom1(column));
			case 2:  return metaData2.getColumnDisplaySize(originalColumnIndexFrom2(column));
			default: return Math.max(metaData1.getColumnDisplaySize(originalColumnIndexFrom1(column)), metaData2.getColumnDisplaySize(originalColumnIndexFrom2(column)));
		}
	}

	/**
	 * Returns the column label. If the column is originated in the
	 * second metadata object, this call is redirected to it - else it is
	 * redirected to the first metadata object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return name of the column label
	 * @throws SQLException
	 */	
	public String getColumnLabel(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 2:  return metaData2.getColumnLabel(originalColumnIndexFrom2(column));
			default: return metaData1.getColumnLabel(originalColumnIndexFrom1(column));
		}
	}

	/**
	 * Returns the name of the column. If the column is originated in the
	 * second metadata object, this call is redirected to it - else it is
	 * redirected to the first metadata object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return name of the column
	 * @throws SQLException
	 */	
	public String getColumnName(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 2:  return metaData2.getColumnName(originalColumnIndexFrom2(column));
			default: return metaData1.getColumnName(originalColumnIndexFrom1(column));
		}
	}

	/**
	 * Returns the type of the column. If the column is originated in the
	 * second metadata object, this call is redirected to it - else it is
	 * redirected to the first metadata object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return type of the column
	 * @throws SQLException
	 */	
	public int getColumnType(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 2:  return metaData2.getColumnType(originalColumnIndexFrom2(column));
			default: return metaData1.getColumnType(originalColumnIndexFrom1(column));
		}
	}

	/**
	 * Returns the type name of the column. If the column is originated in the
	 * second metadata object, this call is redirected to it - else it is
	 * redirected to the first metadata object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return type name of the column
	 * @throws SQLException
	 */	
	public String getColumnTypeName(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 2:  return metaData2.getColumnTypeName(originalColumnIndexFrom2(column));
			default: return metaData1.getColumnTypeName(originalColumnIndexFrom1(column));
		}
	}

	/**
	 * Returns the precision of the column. If the column is originated in the
	 * second metadata object, this call is redirected to it - else it is
	 * redirected to the first metadata object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return precision of the column
	 * @throws SQLException
	 */	
	public int getPrecision(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 2:  return metaData2.getPrecision(originalColumnIndexFrom2(column));
			default: return metaData1.getPrecision(originalColumnIndexFrom1(column));
		}
	}

	/**
	 * Returns the scale of the column. If the column is originated in the
	 * second metadata object, this call is redirected to it - else it is
	 * redirected to the first metadata object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return scale of the column
	 * @throws SQLException
	 */	
	public int getScale(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 2:  return metaData2.getScale(originalColumnIndexFrom2(column));
			default: return metaData1.getScale(originalColumnIndexFrom1(column));
		}
	}

	/**
	 * Returns the schema name of the column. If the column is originated in the
	 * second metadata object, this call is redirected to it - else it is
	 * redirected to the first metadata object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return schema name of the column
	 * @throws SQLException
	 */	
	public String getSchemaName(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 2:  return metaData2.getSchemaName(originalColumnIndexFrom2(column));
			default: return metaData1.getSchemaName(originalColumnIndexFrom1(column));
		}
	}

	/**
	 * Returns the table name of the column. If the column is originated in the
	 * second metadata object, this call is redirected to it - else it is
	 * redirected to the first metadata object.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return table name of the column
	 * @throws SQLException
	 */	
	public String getTableName(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 2:  return metaData2.getTableName(originalColumnIndexFrom2(column));
			default: return metaData1.getTableName(originalColumnIndexFrom1(column));
		}
	}

	/**
	 * Returns if the column is an auto increment column. If the column is originated in the
	 * first/second metadata object, this call is redirected to the first/second 
	 * metadata object. If the column is originated in both metadata objects, the
	 * column cannot be an auto increment column (false is returned).
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is an auto increment column
	 * @throws SQLException
	 */	
	public boolean isAutoIncrement(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 1:  return metaData1.isAutoIncrement(originalColumnIndexFrom1(column));
			case 2:  return metaData2.isAutoIncrement(originalColumnIndexFrom2(column));
			default: return false;
		}
	}

	/**
	 * Returns if the column is case sensitive. If the column is originated in the
	 * first/second metadata object, this call is redirected to the first/second 
	 * metadata object. If the column is originated in both metadata objects, 
	 * it is case sensitive, if one of the original columns is case sensitive.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is case sensitive
	 * @throws SQLException
	 */	
	public boolean isCaseSensitive(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 1:  return metaData1.isCaseSensitive(originalColumnIndexFrom1(column));
			case 2:  return metaData2.isCaseSensitive(originalColumnIndexFrom2(column));
			default: return (metaData1.isCaseSensitive(originalColumnIndexFrom1(column)) || metaData2.isCaseSensitive(originalColumnIndexFrom2(column)));
		}
	}

	/**
	 * Returns if the column contains a currency value. If the column is originated in the
	 * first/second metadata object, this call is redirected to the first/second 
	 * metadata object. If the column is originated in both metadata objects, it
	 * is a currency value if both original values are currency values.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column contains a currency value
	 * @throws SQLException
	 */	
	public boolean isCurrency(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 1:  return metaData1.isCurrency(originalColumnIndexFrom1(column));
			case 2:  return metaData2.isCurrency(originalColumnIndexFrom2(column));
			default: return (metaData1.isCurrency(originalColumnIndexFrom1(column)) && metaData2.isCurrency(originalColumnIndexFrom2(column)));
		}
	}

	/**
	 * Returns if the column is definitively writable. If the column is originated in the
	 * first/second metadata object, this call is redirected to the first/second 
	 * metadata object. If the column is originated in both metadata objects, it
	 * is definitively writable if both original columns are definitively writable.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is definitively writable
	 * @throws SQLException
	 */	
	public boolean isDefinitelyWritable(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 1:  return metaData1.isDefinitelyWritable(originalColumnIndexFrom1(column));
			case 2:  return metaData2.isDefinitelyWritable(originalColumnIndexFrom2(column));
			default: return (metaData1.isDefinitelyWritable(originalColumnIndexFrom1(column)) && metaData2.isDefinitelyWritable(originalColumnIndexFrom2(column)));
		}
	}

	/**
	 * Returns if the column is nullable. If the column is originated in the
	 * first/second metadata object, this call is redirected to the first/second 
	 * metadata object. If the column is originated in both metadata objects, 
	 * the column is nullabe if one of the underlying column is nullable.
	 * If both underlying columns do not allow null values (<code>columnNoNulls</code>),
	 * the column also does not allow null values. In any other case, the return
	 * value is <code>columnNullableUnknown</code>.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return one of the constants defined in {@link java.sql.ResultSetMetaData}: 
	 *	columnNoNulls, columnNullable or columnNullableUnknown
	 * @throws SQLException
	 */	
	public int isNullable(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 1:  return metaData1.isNullable(originalColumnIndexFrom1(column));
			case 2:  return metaData2.isNullable(originalColumnIndexFrom2(column));
			default: {
				if (metaData1.isNullable(originalColumnIndexFrom1(column))==ResultSetMetaData.columnNullable ||
				    metaData2.isNullable(originalColumnIndexFrom2(column))==ResultSetMetaData.columnNullable)
					return ResultSetMetaData.columnNullable;
				if (metaData1.isNullable(originalColumnIndexFrom1(column))==ResultSetMetaData.columnNoNulls &&
				    metaData2.isNullable(originalColumnIndexFrom2(column))==ResultSetMetaData.columnNoNulls)
					return ResultSetMetaData.columnNoNulls;
				return ResultSetMetaData.columnNullableUnknown;
			}
		}
	}

	/**
	 * Returns if the column is read only. If the column is originated in the
	 * first/second metadata object, this call is redirected to the first/second 
	 * metadata object. If the column is originated in both metadata objects, 
	 * it is read only, if one of the original columns is read only.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is read only
	 * @throws SQLException
	 */	
	public boolean isReadOnly(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 1:  return metaData1.isReadOnly(originalColumnIndexFrom1(column));
			case 2:  return metaData2.isReadOnly(originalColumnIndexFrom2(column));
			default: return (metaData1.isReadOnly(originalColumnIndexFrom1(column)) || metaData2.isReadOnly(originalColumnIndexFrom2(column)));
		}
	}

	/**
	 * Returns if the column is searchable. If the column is originated in the
	 * first/second metadata object, this call is redirected to the first/second 
	 * metadata object. If the column is originated in both metadata objects, it
	 * is searchable if both original columns are searchable.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is searchable
	 * @throws SQLException
	 */	
	public boolean isSearchable(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 1:  return metaData1.isSearchable(originalColumnIndexFrom1(column));
			case 2:  return metaData2.isSearchable(originalColumnIndexFrom2(column));
			default: return (metaData1.isSearchable(originalColumnIndexFrom1(column)) && metaData2.isSearchable(originalColumnIndexFrom2(column)));
		}
	}

	/**
	 * Returns if the column is signed. If the column is originated in the
	 * first/second metadata object, this call is redirected to the first/second 
	 * metadata object. If the column is originated in both metadata objects, 
	 * it is signed, if one of the original columns is signed.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column signed
	 * @throws SQLException
	 */	
	public boolean isSigned(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 1:  return metaData1.isSigned(originalColumnIndexFrom1(column));
			case 2:  return metaData2.isSigned(originalColumnIndexFrom2(column));
			default: return (metaData1.isSigned(originalColumnIndexFrom1(column)) || metaData2.isSigned(originalColumnIndexFrom2(column)));
		}
	}

	/**
	 * Returns if the column is writable. If the column is originated in the
	 * first/second metadata object, this call is redirected to the first/second 
	 * metadata object. If the column is originated in both metadata objects, it
	 * is writable if both original columns are writable.
	 * 
     * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return true if the column is writable
	 * @throws SQLException
	 */	
	public boolean isWritable(int column) throws SQLException {
		switch (originalMetaData(column)) {
			case 1:  return metaData1.isWritable(originalColumnIndexFrom1(column));
			case 2:  return metaData2.isWritable(originalColumnIndexFrom2(column));
			default: return (metaData1.isWritable(originalColumnIndexFrom1(column)) && metaData2.isWritable(originalColumnIndexFrom2(column)));
		}
	}
	
	 /**
		 * Returns the first or the second metadata set  
		 * according to the input variable.
		 * 
		 * @param input the number of the metadata set: the accepted values are 1 and 2, all other values will lead to an Exception.
		 * @return the metadata object. 
		 * @throws IllegalArgumentException the input is less than 0 or more than 2.
		 */	
	public ResultSetMetaData getMetaData(int input) throws IllegalArgumentException {
		if (input < 0 || input > 2)
			throw new IllegalArgumentException("Invalid input specified.");
		return input == 1 ? metaData1 : metaData2;
	}

}
