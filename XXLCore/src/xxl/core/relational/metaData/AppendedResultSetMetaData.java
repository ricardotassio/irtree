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

import xxl.core.util.WrappingRuntimeException;

/**
 * Appends two metadata objects to another one and returns
 * a new ResultSetMetaData Object. 
 */
public class AppendedResultSetMetaData extends MergedResultSetMetaData {
	/**
	 * Creates an AppendedResultSetMetaData.
	 * @param rsmd1 First ResultSetMetaData Object 
	 * @param rsmd2 Second (appended) ResultSetMetaData Object
	 */
	public AppendedResultSetMetaData(ResultSetMetaData rsmd1, ResultSetMetaData rsmd2) {
		super (rsmd1, rsmd2);
	}
	
	/**
	 * Returns the number of columns. This is equal to the size of the
	 * arrays that has been passed to the constructor call.
	 *
	 * @return number of columns
	 * @throws SQLException
	 */	
	public int getColumnCount () throws SQLException {
		return metaData1.getColumnCount() + metaData2.getColumnCount();
	}
	
	/**
	 * Returns if the column is originated in the first (1), the second (2) or in both
	 * (0) underlying ResultSetMetaData objects. 
	 * 
	 * @param column number of the column: the first column is 1, the second is 2, ...
	 * @return returns 1 or 2 if the column is originated in the first respectively second ResultSetMetaData object.
	 *	Returns 0 if the column is originated in both ResultSetMetaData object.
	 * @throws SQLException
	 */
	public int originalMetaData (int column) throws SQLException {
		if (column < 1 || column > getColumnCount())
			throw new SQLException("Invalid column index.");
		return column > metaData1.getColumnCount() ? 2 : 1;
	}
	
	/**
	 * Determines the original column number from the first underlying ResultSetMetaData 
	 * object, on which the column of this object is based.
	 *
	 * @param column number of the column: the first column is 1, the second is 2, ...
	 *	If the colum does not originate in the first ResultSetMetaData object,
	 *	the return value has to be 0
	 * @return column number of the first ResultSetMetaData
	 */
	public int originalColumnIndexFrom1 (int column) {
		return column;
	}
	
	/**
	 * Determines the original column number from the second underlying ResultSetMetaData 
	 * object, on which the column of this object is based.
	 *
	 * @param column number of the column: the first column is 1, the second is 2, ...
	 *	If the colum does not originate in the second ResultSetMetaData object,
	 *	the return value has to be 0
	 * @return column number of the second ResultSetMetaData
	 */
	public int originalColumnIndexFrom2 (int column) {
		try {
			return column-metaData1.getColumnCount();
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
