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
import java.util.LinkedList;
import java.util.List;

import xxl.core.functions.Function;
import xxl.core.util.WrappingRuntimeException;


/**
 * Implements a list tuple where the column objects are saved 
 * in a list.
 *
 * This is implementation might be significantly slower than
 * the {@link xxl.core.relational.ArrayTuple ArrayTuple} implementation,
 * especially when a lot of column accesses occur.
 *
 * For convenience, the implementation extends AbstractTuple.
 *
 * <pre><code>
 * Tuple tuple = new ListTuple(resultSet);
 * </code></pre>
 * creates a tuple for the given ResultSet. 
 * A LinkedList is used to store elements.
 * <p>
 * <pre><code>
 * ListTuple.FACTORY_METHOD.invoke(resultSet)
 * </code></pre>
 * does the same.
 */

public class ListTuple extends AbstractTuple {

	/**A list containing column objects.*/
	protected List tuple;

	/** 
	 * Factory method that constructs objects of the class ListTuple.
	 * The function can be called with one or two parameters
	 * (similar (not equal) to the two constructors of the class
	 * - equal to the parameters of {@link xxl.core.relational.ArrayTuple 
	 * ArrayTuple}):
	 *
	 * <ul>
	 * <li>
	 *	resultSet: Object of class {@link java.sql.ResultSet ResultSet}
	 *	from which data and metadata are taken. The type of the List is
	 *	a java.util.LinkedList.
	 	<br>
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
			return new ListTuple((ResultSet) resultSet, new LinkedList());
		}
		
		public Object invoke (Object objects, Object metaData) {
			return new ListTuple((Object[])objects, (ResultSetMetaData)metaData);
		}

	};

	/**
	 * Constructs a ListTuple containing the column objects of the current row
	 * of the ResultSet. The metadata is also taken from the ResultSet.
	 * With the second parameter it is possible to define the type of list
	 * that is used internally.
	 *
	 * @param resultSet underlying ResultSet object.
	 * @param list empty list of the desired type.
	 * @throws xxl.core.util.WrappingRuntimeException when accessing resultSet fails.
	 */
	public ListTuple (ResultSet resultSet, List list) {
		super(resultSet);
		this.tuple = list;
		try {
			for (int i = 1; i <= metaData.getColumnCount(); i++)
				tuple.add(resultSet.getObject(i));
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Constructs a ListTuple containing the column objects of an
	 * Object array. The metadata is taken from the passed ResultSetMetaData Object.
	 * With the third parameter it is possible to define the type of list
	 * that is used internally.
	 *
	 * @param tuple array containing column objects
	 * @param metaData metadata
	 * @param list List that is used for storing the tuple
	 * @throws xxl.core.util.WrappingRuntimeException when accessing the metadata fails.
	 */
	public ListTuple (Object[] tuple, ResultSetMetaData metaData, List list) {
		super(metaData);
		this.tuple = list;
		try {
			for (int i = 1; i <= metaData.getColumnCount(); i++)
				this.tuple.add(tuple[i]);
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
		this.metaData = metaData;
	}

	/**
	 * Constructs a ListTuple containing the column objects of an
	 * Object array. The metadata is taken from the passed ResultSetMetaData
	 * Object. The columns are stored in an {@link java.util.LinkedList}.
	 *
	 * @param tuple array containing column objects
	 * @param metaData metadata
	 * @throws xxl.core.util.WrappingRuntimeException when accessing the metadata fails.
	 */
	public ListTuple (Object[] tuple, ResultSetMetaData metaData) {
		this(tuple, metaData, new LinkedList());
	}

	/** 
	 * Returns the object of the given column.
	 *
     	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return object of the column
	 */
	public Object getObject (int columnIndex) {
		return tuple.get(columnIndex-1);
	}

}
