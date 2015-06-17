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

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import xxl.core.functions.Function;

/** 
 * An implementation of ResultSetMetaData for Numbers and Varchars.
 * <p>
 * Instances of this class represent one-column number metadata objects.
 * NumberMetaData objects can be composed to more complex
 * structures using {@link xxl.core.relational.metaData.AssembledResultSetMetaData}.
 * <p>
 * All parameters can be passed to a constructor call.
 * <p>
 * Some methods may have to be overridden to get 
 * useful metadata for specific applications.
 */
public class MetaData implements ResultSetMetaData {

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
	/** Type of SQL-Type entries. */
	public static class SQLTypeRecord {
		
		/**The name of the datatype in SQL.*/
		protected String name;
		/**The name of the Class in Java.*/
		protected String className;
		/**The number of the datatype in SQL.*/
		protected int typeNumber;
		/**The Class in Java.*/
		protected Class javaClass;
		
		/**
		 * Creates a new type for SQL-Type entries.
		 * 
		 * @param name the name of the datatype in SQL.
		 * @param className the name of the class in Java.
		 * @param typeNumber the number of the datatype in SQL.
		 * @param javaClass the class in Java.
		 */
		SQLTypeRecord (String name, String className, int typeNumber, Class javaClass) {
			this.name = name;
			this.className = className;
			this.typeNumber = typeNumber;
			this.javaClass = javaClass;
		}
		
		/**
		 * Returns the name of the Class in Java.
		 * @return The name of the Class in Java.
		 */
		public String getClassName() {
			return className;
		}

		/**
		 * Returns the name of the datatype in SQL.
		 * @return The name of the datatype in SQL.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the number of the datatype in SQL.
		 * @return The number of the datatype in SQL.
		 */
		public int getTypeNumber() {
			return typeNumber;
		}

		/**
		 * Returns the Class in Java.
		 * @return The Class in Java.
		 */
		public Class getJavaClass() {
			return javaClass;
		}
	}
	
	/** Array of SQL-Types in Java */
	public static SQLTypeRecord javaSQLtypes [] = new SQLTypeRecord[] {
		//new SQLTypeRecord("COUNTER","java.lang.Integer",java.sql.Types.INTEGER, Integer.class),
		new SQLTypeRecord("LONGCHAR","java.lang.String",12, String.class), // java.sql.Types.VARCHAR oder LONGVARCHAR oder CHAR?
		new SQLTypeRecord("NUMERIC","java.math.BigDecimal",java.sql.Types.NUMERIC, BigDecimal.class),
		new SQLTypeRecord("INTEGER","java.lang.Integer",java.sql.Types.INTEGER, Integer.class),						
		new SQLTypeRecord("DECIMAL","java.math.BigDecimal",java.sql.Types.DECIMAL, BigDecimal.class),
		new SQLTypeRecord("SMALLINT","java.lang.Short",java.sql.Types.SMALLINT, Short.class),
		new SQLTypeRecord("CHAR","java.lang.String",java.sql.Types.CHAR, String.class),
		new SQLTypeRecord("VARCHAR","java.lang.String",java.sql.Types.VARCHAR, String.class),
		new SQLTypeRecord("LONGVARCHAR","java.lang.String",java.sql.Types.LONGVARCHAR, String.class),
		new SQLTypeRecord("BIT","java.lang.Boolean",java.sql.Types.BIT, Boolean.class),
		new SQLTypeRecord("TINYINT","java.lang.Byte",java.sql.Types.TINYINT, Byte.class),
		new SQLTypeRecord("BIGINT","java.lang.Long",java.sql.Types.BIGINT, Long.class),
		new SQLTypeRecord("REAL","java.lang.Float",java.sql.Types.REAL, Float.class),
		new SQLTypeRecord("FLOAT","java.lang.Double",java.sql.Types.FLOAT, Double.class),
		new SQLTypeRecord("DOUBLE","java.lang.Double",java.sql.Types.DOUBLE, Double.class),
		new SQLTypeRecord("BINARY","byte[]",java.sql.Types.BINARY, byte[].class),
		new SQLTypeRecord("VARBINARY","byte[]",java.sql.Types.VARBINARY, byte[].class),
		new SQLTypeRecord("LONGVARBINARY","byte[]",java.sql.Types.LONGVARBINARY, byte[].class),
		new SQLTypeRecord("DATE","java.sql.Date",java.sql.Types.DATE, java.sql.Date.class),
		new SQLTypeRecord("TIME","java.sql.Time",java.sql.Types.TIME, java.sql.Time.class),
		new SQLTypeRecord("TIMESTAMP","java.sql.Timestamp",java.sql.Types.TIMESTAMP, java.sql.Timestamp.class)
	};

	/**
	 * Searches for the specified type in the javaSQLtypes-array and 
	 * returns the index if the type is found.
	 *
	 * @param type SQL type
	 * @return index (-1 if type is not found)
	 */
	public static int searchSQLtypesArray(int type) {
		for (int i=0; i<javaSQLtypes.length; i++)
			if (javaSQLtypes[i].typeNumber==type)
				return i;
		
		return -1;
	}

	/**
	 * Searches for the specified type in the javaSQLtypes-array 
	 * for a class (If it is a number class, use the class type of the
	 * wrapper classes) and returns the index if the type is found.
	 *
	 * @param javaType Java type
	 * @return index (-1 if type is not found)
	 */
	public static int searchSQLtypesArray(Class javaType) {
		for (int i=0; i<javaSQLtypes.length; i++)
			if (javaSQLtypes[i].javaClass==javaType)
				return i;
		
		return -1;
	}

	/** Precision of the type */
	protected int precision;
	/** Scale of the type */
	protected int scale;
	/** Name of the column */
	protected String columnName;
	/** true if the column is case sensitive */
	protected boolean isCaseSensitive;
	/** true if the column is a currency */
	protected boolean isCurrency;
	/** true if the column is nullable */
	protected int columnNullable;

	/** Variable determining the SQL type */
	protected int index;

	/**
	 * Default Instance for number metadata representing the metadata 
	 * of 32-bit Integers (and "" as column name).
	 */
	public static final MetaData NUMBER_META_DATA_DEFAULT_INSTANCE = new MetaData (Types.NUMERIC,"",9,0);

	/**
	 * Factory method returning number metadata objects. The factory method 
	 * gets zero to four parameters. Without parameters it returns the
	 * metadata of 32-bit Numeric Type (Precision 9, Scale 0, "" as column name). 
	 * The following parameters can be passed (scale can only be passed if precision
	 * has been passed, and so on):
	 * <ul>
	 * <li>1. columnName (String)</li>
	 * <li>2. precision (Integer)</li>
	 * <li>3. scale (Integer)</li>
	 * <li>4. Java SQL type (@see java.sql.Types)</li>
	 * </ul>
	 */
	public static final Function NUMBER_META_DATA_FACTORY_METHOD = new Function () {
		public Object invoke (final Object arg[]) {
			return new MetaData(
				arg.length>=4?((Integer)arg[3]).intValue():Types.NUMERIC,
				arg.length>=1?(String) arg[0]:"",
				arg.length>=2?((Integer)arg[1]).intValue():9,
				arg.length>=2?((Integer)arg[2]).intValue():0
			);
		}
		public Object invoke () {
			return invoke(new Object[] {""});
		}
		public Object invoke (Object arg) {
			return invoke(new Object[] {(String)arg,new Integer(9),new Integer(0)});
		}
	};

	/**
	 * Construct a new one-column ResultSetMetaData object, that normally 
	 * represents number metadata.
	 * 
	 * @param type type from java.sql.Types, for example Types.NUMERIC
	 * @param columnName name of the column
	 * @param precision precision of the column
	 * @param scale scale of the column
	 */
	public MetaData (int type, String columnName, int precision, int scale) {
		this.columnName = columnName;
		this.precision = precision;
		this.scale = scale;
		this.index = searchSQLtypesArray(type);
		if (index<0)
			throw new IllegalArgumentException();
		
		this.isCaseSensitive = false;
		this.isCurrency = true;
		this.columnNullable = ResultSetMetaData.columnNullable;
	}
	
	/**
	 * Construct a new one-column ResultSetMetaData object, that normally 
	 * represents character metadata.
	 * 
	 * @param type type from java.sql.Types, for example Types.VARCHAR
	 * @param columnName name of the column
	 * @param maxChars maximal number of chars in the strings
	 */
	public MetaData (int type, String columnName, int maxChars) {
		this.columnName = columnName;
		this.precision = maxChars;
		this.scale = 0;
		this.index = searchSQLtypesArray(type);
		if (index<0)
			throw new IllegalArgumentException();

		this.isCaseSensitive = true;
		this.isCurrency = false;
		this.columnNullable = ResultSetMetaData.columnNullable;
	}

	/**
	 * Abstract method that should return the name of the column.
	 * 
     * @param column number of the column: the first column is 1, all other values should lead to an Exception.
	 * @return name of the column
	 * @throws SQLException
	 */	
	public String getColumnName(int column) throws SQLException {
		return columnName;
	}
	
	/**
	 * Abstract method that should return the precision of the column. 
	 * 
     * @param column number of the column: the first column is 1, all other values should lead to an Exception.
	 * @return precision of the column
	 * @throws SQLException
	 */	
	public int getPrecision(int column) throws SQLException {
		return precision;
	}
	
	/**
	 * Abstract method that should return the scale of the column.
	 * 
     * @param column number of the column: the first column is 1, all other values should lead to an Exception.
	 * @return scale of the column
	 * @throws SQLException
	 */	
	public int getScale(int column) throws SQLException {
		return scale;
	}

	/**
	 * Returns the number of columns of this NumberMetaData object and this
	 * is always 1.
	 *
	 * @return the number of columns (always 1)
	 */
	public int getColumnCount() {
		return 1;
	}

	/**
	 * Checks the column index. If the index does not equal 1, an SQLException is thrown.
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public void checkColumnCount(int column) throws java.sql.SQLException {
		if (column<1 || column>getColumnCount())
			throw new SQLException("Invalid column index.");
	}

	/**
	 * Returns the name the catalog that is always "". 
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return name of the catalog: ""
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public String getCatalogName(int column) throws SQLException {
		checkColumnCount(column);
		return "";
	}

	/**
	 * Returns the display size of the column. This call
	 * is redirected to getPrecision, because the Precision
	 * should be a good match.
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return display size of the column
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public int getColumnDisplaySize(int column) throws SQLException {
		checkColumnCount(column);
		return getPrecision(column);
	}

	/**
	 * Returns the column label. This call
	 * is redirected to getColumnName, because the column name
	 * should be a good match.
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return name of the column label
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public String getColumnLabel(int column) throws SQLException {
		checkColumnCount(column);
		return getColumnName(column);
	}

	/**
	 * Returns the type of the column (java.sql.Types.NUMERIC).
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return type of the column: java.sql.Types.NUMERIC
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public int getColumnType(int column) throws SQLException {
		checkColumnCount(column);
		return javaSQLtypes[index].typeNumber;
	}

	/**
	 * Returns the type name of the column ("NUMBER").
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return type name of the column: "NUMBER"
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public String getColumnTypeName(int column) throws SQLException {
		checkColumnCount(column);
		return javaSQLtypes[index].name;
	}

	/**
	 * Returns the name the Java class that is associated with the column type
	 * ("java.lang.Number"). 
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return name of the column class: "java.lang.Number"
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public String getColumnClassName(int column) throws SQLException {
		checkColumnCount(column);
		return javaSQLtypes[index].className;
	}

	/**
	 * Returns the schema name of the column ("").
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return schema name of the column: ""
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public String getSchemaName(int column) throws SQLException {
		checkColumnCount(column);
		return "";
	}

	/**
	 * Returns the table name of the column ("").
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return table name of the column: ""
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public String getTableName(int column) throws SQLException {
		checkColumnCount(column);
		return "";
	}

	/**
	 * Returns false, because the column is not an auto increment column.
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return always false, because the column is not an auto increment column
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public boolean isAutoIncrement(int column) throws SQLException {
		checkColumnCount(column);
		return false;
	}

	/**
	 * Returns false, because the column is not case sensitive.
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return always false, because the column is not case sensitive.
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public boolean isCaseSensitive(int column) throws SQLException {
		checkColumnCount(column);
		return isCaseSensitive;
	}

	/**
	 * Returns true, because the column is assumed to be a currency.
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return always true, because the column is assumed to be a currency.
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public boolean isCurrency(int column) throws SQLException {
		checkColumnCount(column);
		return isCurrency;
	}

	/**
	 * Returns false, because the column may not be writable.
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return always false, because the column may not be writable.
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public boolean isDefinitelyWritable(int column) throws SQLException {
		checkColumnCount(column);
		return false;
	}

	/**
	 * Returns java.sql.ResultSetMetaData.columnNullable. It is assumed that 
	 * the column may contain null values.
	 *
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return java.sql.ResultSetMetaData.columnNullable, because the column may contain null values.
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */
	public int isNullable(int column) throws SQLException {
		checkColumnCount(column);
		return columnNullable;
	}

	/**
	 * Returns false, because the column is assumed to be writable.
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return always false, because the column is assumed to be writable.
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public boolean isReadOnly(int column) throws SQLException {
		checkColumnCount(column);
		return false;
	}

	/**
	 * Returns true, because the column is searchable.
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return always true, because the column is searchable.
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public boolean isSearchable(int column) throws SQLException {
		checkColumnCount(column);
		return true;
	}

	/**
	 * Returns true, because the column value is signed.
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return always true, because the column value is signed.
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public boolean isSigned(int column) throws SQLException {
		checkColumnCount(column);
		return true;
	}

	/**
	 * Returns true, because the column is assumed to be writable.
	 * 
     	 * @param column number of the column: the first column is 1, all other values will lead to an Exception.
	 * @return always true, because the column is assumed to be writable.
     	 * @throws SQLException("Invalid column index.") if the column does not equal 1.
	 */	
	public boolean isWritable(int column) throws SQLException {
		checkColumnCount(column);
		return true;
	}
}
