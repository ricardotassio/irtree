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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import xxl.core.functions.Function;
import xxl.core.util.WrappingRuntimeException;

/**
 * Implements an array tuple where the column objects are packed together 
 * in an Object array.
 *
 * This is an efficient implementation of the Tuple interface using arrays. 
 * For convenience, the implementation extends AbstractTuple.
 *
 * <pre><code>
 * Tuple tuple = new ArrayTuple(resultSet);
 * </code></pre>
 * creates a tuple for the given ResultSet. 
 * An ArrayList is used to store elements.
 * <p>
 *	
 * <pre><code>
 * ArrayTuple.FACTORY_METHOD.invoke(resultSet)
 * </code></pre>
 * does the same.
 */
public class ArrayTuple extends AbstractTuple {

	/**An array containing column objects*/
	protected Object[] tuple;

	/** 
	 * Factory method that constructs objects of the class ArrayTuple.
	 * The function can be called with one or two parameters
	 * (corresponding to the two constructors of the class):
	 *
	 * <ul>
	 * <li>
	 *	resultSet: Object of class {@link java.sql.ResultSet ResultSet}
	 *	from which data and metadata are taken.<br>
	 * <code>
	 * 	java.sql.ResultSet resultSet = ...;
	 *	Tuple t = xxl.core.relational.ArrayTuple.FACTORY_METHOD.invoke(resultSet);
	 * </code>
	 * </li>
	 * <li>
	 *	objects: Object array containing the objects.<br>
	 *	metaData: Object of class {@link java.sql.ResultSetMetaData ResultSetMetaData}
	 *	from which the metadata is taken.<br>
	 * 	Caution: tuple and metadata are linked (not copied). So, changes to the original
	 *	variables will cause changes to the tuple!
	 * <code>
	 * 	java.sql.ResultSetMetaData resultSetMetaData = ...;
	 *	Tuple t = xxl.core.relational.ArrayTuple.FACTORY_METHOD.invoke(objects,resultSetMetaData);
	 * </code>
	 * </li>
	 * </ul>
	 * Short:<br>
	 * 	
	 * ResultSet --> Tuple<br>
	 * Object[] x ResultSetMetaData --> Tuple
	 * <p>	 
 	 * This function can always be used when a createTuple-Function is needed.
	 */
	public static final Function FACTORY_METHOD = new Function () {

		public Object invoke (Object resultSet) {
			return new ArrayTuple((ResultSet) resultSet);
		}

		public Object invoke (Object objects, Object metaData) {
			return new ArrayTuple((Object[])objects, (ResultSetMetaData)metaData);
		}

	};

	/**
	 * Constructs an ArrayTuple containing the column objects of the current row
	 * of the ResultSet. The metadata is also taken from the ResultSet.
	 *
	 * @param resultSet underlying ResultSet object.
	 * @throws xxl.core.util.WrappingRuntimeException when accessing resultSet fails.
	 */
	public ArrayTuple (ResultSet resultSet) {
		super(resultSet);
		try {
			tuple = new Object[metaData.getColumnCount()];
			for (int i = 1; i <= metaData.getColumnCount(); i++)
				tuple[i-1] = resultSet.getObject(i);
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Constructs an ArrayTuple containing the column objects of an
	 * Object array. The metadata is taken from the passed ResultSetMetaData
	 * Object.
	 *
	 * @param tuple array containing column objects. Caution: the tuple
	 *	is linked (not copied). So, changes to tuple will cause
	 *	changes in the tuple!
	 * @param metaData metadata
	 */
	public ArrayTuple (Object[] tuple, ResultSetMetaData metaData) {
		super(metaData);
		this.tuple = tuple;
		this.metaData = metaData;
	}

	/** 
	 * Returns the object of the given column.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return object of the column
	 */
	public Object getObject (int columnIndex) {
		return tuple[columnIndex-1];
	}
}
