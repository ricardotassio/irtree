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

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import xxl.core.cursors.Cursor;
import xxl.core.cursors.MetaDataCursor;
import xxl.core.cursors.mappers.Mapper;
import xxl.core.functions.Function;
import xxl.core.util.WrappingRuntimeException;

/**
 * This class contains various useful <tt>static</tt> methods for 
 * managing columns of ResultSets.
 *
 * Most of these methods are used internally by the join
 * operation of this package.
 *
 * This class cannot become instantiated.
 */
public class ResultSets {

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private ResultSets () {}

	/**
	 * Transfers an array of column names into an array of indices.
	 * For every column, the method findColumn from the class
	 * {@link java.sql.ResultSet} is called exactly once.
	 *
	 * This method is identical to
	 * <code>getColumnNames(resultSet.getMetaData())</code> (except
	 * ExceptionHandling if <code>getMetaData()</code> itself throws an 
	 * exception).
	 *
	 * @param resultSet the ResultSet object that is used
	 * @param columnNames array of strings that contains the names of some columns
	 * @return array of int-values containing indices.
	 * @throws xxl.core.util.WrappingRuntimeException if a findColumn call throws an
	 *	SQLException.
	 */
	public static int[] getColumnIndices (ResultSet resultSet, String[] columnNames) {
		int[] indices = new int[columnNames.length];
		try {
			for (int i = 0; i < columnNames.length; i++)
				indices[i] = resultSet.findColumn(columnNames[i]);
		}
		catch (SQLException se) {
			throw new WrappingRuntimeException(se);
		}
		return indices;
	}

	/**
	 * Transfers an array of indices into an array of column names.
	 * To get the column names, this method uses the ResultSetMetaData
	 * of the ResultSet.
	 *
	 * @param resultSet the ResultSet object that is used
	 * @param columnIndices array of int-values that contains indices of columns
	 * @return array of String objects containing the column names.
	 * @throws xxl.core.util.WrappingRuntimeException if accessing the
	 *	metadata produced a SQLException.
	 */
	public static String[] getColumnNames (ResultSet resultSet, int[] columnIndices) {
		try {
			return getColumnNames(resultSet.getMetaData(), columnIndices);
		}
		catch (SQLException se) {
			throw new WrappingRuntimeException(se);
		}
	}

	/**
	 * Transfers an array of column names into an array of indices.
	 * For every column, a partner is searched in the 
	 * {@link java.sql.ResultSetMetaData} object.
	 *
	 * @param metaData the ResultSetMetaData object that is used
	 * @param columnNames array of strings that contains the names of some columns
	 * @return array of int-values containing indices.
	 * @throws xxl.core.util.WrappingRuntimeException if call to the ResultSetMetaData 
	 *	object throws an SQLException.
	 */
	public static int[] getColumnIndices (ResultSetMetaData metaData, String[] columnNames) {
		int[] indices = new int[columnNames.length];
		try {
			for (int i = 0; i < columnNames.length; i++)
				for (int j = 0; j < metaData.getColumnCount(); j++)
					if(columnNames[i].equalsIgnoreCase(metaData.getColumnName(j+1)))
						indices[i] = j+1;
		}
		catch (SQLException se) {
			throw new WrappingRuntimeException(se);
		}
		return indices;
	}

	/**
	 * Transfers a column name into an indice.
	 * A partner is searched in the 
	 * {@link java.sql.ResultSetMetaData} object.
	 *
	 * @param metaData the ResultSetMetaData object that is used
	 * @param columnName string that contains the name of the column
	 * @return indice.
	 * @throws xxl.core.util.WrappingRuntimeException if call to the ResultSetMetaData 
	 *	object throws an SQLException.
	 */
	public static int getColumnIndice (ResultSetMetaData metaData, String columnName) {
		return getColumnIndices(metaData, new String[]{columnName})[0];
	}

	/**
	 * Transfers an array of indices into an array of column names.
	 * To get the column names, this method uses the ResultSetMetaData
	 * that is passed to the method call.
	 *
	 * @param metaData the ResultSetMetaData object that is used
	 * @param columnIndices array of int-values that contains indices of columns
	 * @return array of String objects containing the column names.
	 * @throws xxl.core.util.WrappingRuntimeException if accessing the
	 *	metadata produced a SQLException.
	 */
	public static String[] getColumnNames (ResultSetMetaData metaData, int[] columnIndices) {
		String[] strings = new String[columnIndices.length];
		try {
			for (int i = 0; i < metaData.getColumnCount(); i++)
				strings[i] = metaData.getColumnName(columnIndices[i]);
		}
		catch (SQLException se) {
			throw new WrappingRuntimeException(se);
		}
		return strings;
	}

	/**
	 * Creates a SQL query string for the creation of a table with
	 * the given name and the schema given as a ResultSetMetaData Object.
	 * @param tableName Name of the table to be created.
	 * @param rsmd The schema of the new table.
	 * @return The query string. 
	 */
	public static String getCreateTableQuery(String tableName, ResultSetMetaData rsmd) {
		try {
			StringBuffer sb = new StringBuffer("create table ");
			sb.append(tableName);
			sb.append("(\n");
			
			for (int i=1; i<=rsmd.getColumnCount(); i++) {
				if (i>1)
					sb.append(",\n");
				sb.append("\t");
				sb.append(rsmd.getColumnName(i));
				sb.append(" ");
				sb.append(rsmd.getColumnTypeName(i));
			}
			sb.append("\n)");
			
			return sb.toString();
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Returns a Prepared Statement which is used to insert data of a
	 * given schema into a database. 
	 * @param con The connection to the database.
	 * @param tableName The name of the table where the data is inserted.
	 * @param rsmd Relational schema of the table of the database.
	 * @return The PreparedStatement which can be used for insertion.
	 */
	public static PreparedStatement getPreparedInsertStatement(Connection con, String tableName, ResultSetMetaData rsmd) {
		try {
			if (rsmd.getColumnCount()==0)
				return null;
			
			StringBuffer sb = new StringBuffer("insert into ");
			sb.append(tableName);
			sb.append("values(");
			
			for (int i=1; i<rsmd.getColumnCount(); i++)
				sb.append("?,");
			sb.append("?)");
			
			return con.prepareStatement(sb.toString());
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Inserts a complete MetaDataCursor (with relational metadata) into
	 * a table inside a database. Inside this method, a Prepared Statement is used.
	 * @param mdc The input data which will be written into the database.
	 * @param con The connection to the database.
	 * @param tableName The name of the table where the data is inserted.
	 */
	public static void insertIntoTable(MetaDataCursor mdc, Connection con, String tableName) {
		try {
			ResultSetMetaData rsmd = (ResultSetMetaData) mdc.getMetaData();
			PreparedStatement ps = getPreparedInsertStatement(con, tableName, rsmd);
			while (mdc.hasNext()) {
				Tuple t = (Tuple) mdc.next();
				
				for (int i=1; i<=rsmd.getColumnCount(); i++)
					ps.setObject(i, t.getObject(i));
				
				ps.executeUpdate();
			}
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Returns a function which transforms a Tuple (the only parameter
	 * with which the Function has to be called) into a SQL insert query
	 * string.
	 * @param tableName Name of the table into which the tuple should be inserted.
	 * @return The conversion Function.
	 */
	public static Function getMapFunctionTupleToInsertQuery(final String tableName) {
		return new Function() {
			public Object invoke (Object o) {
				Tuple t = (Tuple) o;
				ResultSetMetaData rsmd = t.getMetaData();

				try {
					if (rsmd.getColumnCount()==0)
						return null;
					
					StringBuffer sb = new StringBuffer("insert into ");
					sb.append(tableName);
					sb.append(" values (");
					
					for (int i=1; i<=rsmd.getColumnCount(); i++) {
						if (i>1)
							sb.append(",");
						Object currentObject = t.getObject(i);
						if (currentObject instanceof String) sb.append("'");
						if (currentObject instanceof Character) sb.append("'");
						if (currentObject instanceof Date) sb.append("#");
						sb.append(currentObject);
						if (currentObject instanceof String) sb.append("'");
						if (currentObject instanceof Character) sb.append("'");
						if (currentObject instanceof Date) sb.append("#");
					}
					sb.append(")");
					
					return sb.toString();
				}
				catch (SQLException e) {
					throw new WrappingRuntimeException(e);
				}
				
			}
		};
	}

	/**
	 * Creates a table in a database which is compatible with the given
	 * schema.
	 * @param tableName Name of the table to be created.
	 * @param rsmd The schema of the new table.
	 * @param con The connection to the database.
	 * @param sqlLog PrintStream to which SQL query strings are written.
	 * @return true iff the table was created successfully.
	 */
	public static boolean createTable(String tableName, ResultSetMetaData rsmd, Connection con, PrintStream sqlLog) {
		String createQuery = getCreateTableQuery(tableName, rsmd);
		sqlLog.println(createQuery);
		
		Statement stmt=null;
		try {
			stmt = con.createStatement();
			stmt.execute(createQuery);
			stmt.close();
			return true;
		}
		catch (SQLException e) {
			if (stmt!=null) {
				try {
					stmt.close();
				}
				catch (SQLException e2) {}
			}
			return false;
		}
	}

	/**
	 * Inserts the tuples of a given MetaDataCursor into a table of
	 * a database. For each tuple, a new insert query is created.
	 * This method does not use PreparedStatements.
	 * @param tableName Name of the table where the tuples are inserted.
	 * @param mdc Input MetaDataCursor which contains the tuples to be inserted.
	 * @param con The connection to the database.
	 * @param sqlLog PrintStream to which SQL query strings are written.
	 * @return The number of tuples inserted into the database.
	 */
	public static int insertIntoTable(String tableName, MetaDataCursor mdc, Connection con, PrintStream sqlLog) {
		Statement stmt = null;
		
		int count=0;
		try {
			Cursor sqlStrings = 
				new Mapper(
					mdc,
					getMapFunctionTupleToInsertQuery(tableName)
				);
			// Cursors.println(sqlStrings);
			stmt = con.createStatement();

			while (sqlStrings.hasNext()) {
				String query = (String) sqlStrings.next();
				sqlLog.println(query);
				stmt.execute(query);
				count++;
			}
			stmt.close();
			
			return count;
		}
		catch (SQLException e) {
			if (stmt!=null) {
				try {
					stmt.close();
				}
				catch (SQLException e2) {}
			}
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Writes the MetaDataCursor to a PrintStream. Each Tuple is separated with a
	 * given String (use "\t" for Tab separation).
	 * To be compatible with GnuPlot, use for example:
	 * <code>
	 * writeToPrintStream(mdc, new PrintStream(new FileOutputStream("test.plt")), false, "\t"); 
	 * </code>
	 * @param mdc Input MetaDataCursor which is processed.
	 * @param ps PrintStream where the output is sent.
	 * @param writeHeadline Write a first line with the column names?
	 * @param separator Separator which separates a column of a tuple from the
	 * 	next column (also used for the column names if writeHeadline is true).
	 */
	public static void writeToPrintStream(MetaDataCursor mdc, PrintStream ps, 
			boolean writeHeadline, String separator) {
		
		ResultSetMetaData rsmd = (ResultSetMetaData) mdc.getMetaData();
		try {
			int columnCount = rsmd.getColumnCount();
			
			if (writeHeadline) {
				for (int i=1; i<=columnCount; i++) {
					if (i>1)
						ps.print(separator);
					ps.print(rsmd.getColumnName(i));
				}
				ps.println();
			}
				
			while (mdc.hasNext()) {
				Tuple t = (Tuple) mdc.next();
				for (int i=1; i<=columnCount; i++) {
					if (i>1)
						ps.print(separator);
					ps.print(t.getObject(i));
				}
				ps.println();
			}
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}
}
