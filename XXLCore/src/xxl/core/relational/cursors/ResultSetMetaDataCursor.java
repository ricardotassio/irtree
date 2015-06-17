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

package xxl.core.relational.cursors;

import java.sql.ResultSet;
import java.sql.SQLException;

import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.relational.ArrayTuple;
import xxl.core.relational.Tuple;
import xxl.core.util.WrappingRuntimeException;

/**
 *	This class wraps a ResultSet to a MetaDataCursor.
 *	ResultSet and MetaDataCursor are equivalent concepts.
 *	Both handle sets of objects with metadata.
 *	The different direction is done by the class
 *	{@link xxl.core.relational.MetaDataCursorResultSet}.
 */
public class ResultSetMetaDataCursor extends AbstractCursor implements MetaDataCursor {

	/** ResultSet that is wrapped into a MetaDataCursor */
	protected ResultSet resultSet = null;
	/** 
	 * Function that maps a (row of the) ResultSet to a Tuple. The function
	 * gets a ResultSet and maps the current row to a Tuple. It is
	 * forbidden to call the next, update and similar methods. 
	 */
	protected Function createTuple;

	/**
	 * Constructs a Wrapper that wraps a MetaDataCursor into a ResultSet.
	 * Therefore, a function can be passed that translates a row into an
	 * object.
	 *
	 * @param resultSet ResultSet that is wrapped into a MetaDataCursor
	 * @param createTuple Function that maps a (row of the) ResultSet to a Tuple. 
	 *	If null is passed, the factory method of ArrayTuple is used.
	 *	The function gets a ResultSet and maps the current row to a Tuple. It is
	 * 	forbidden to call the next, update and similar methods of the ResultSet!
	 */
	public ResultSetMetaDataCursor (ResultSet resultSet, Function createTuple) {
		this.resultSet = resultSet;
		this.createTuple = createTuple;
	}

	/**
	 * Constructs a Wrapper that wraps a MetaDataCursor into a ResultSet.
	 * An ArrayTuple (<code>ArrayTuple.FACTORY_METHOD</code>) is used to represent 
	 * a row in the Cursor's objects.
	 *
	 * @param resultSet ResultSet that is wrapped into a MetaDataCursor
	 */
	public ResultSetMetaDataCursor (ResultSet resultSet) {
		this(resultSet, ArrayTuple.FACTORY_METHOD);
	}

	/**
	 * Returns <tt>true</tt> if the underlying ResultSet has more elements.
	 * (In other words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 *
	 * @return <tt>true</tt> if the cursor has more elements.
	 * @throws xxl.core.util.WrappingRuntimeException if a SQLException is reported
	 *	by the underlying ResultSet.
	 */
	public boolean hasNextObject() {
		try {
			return resultSet.next();
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Returns the next element of the underlying ResultSet.
	 *
	 * @return the next element of the underlying ResultSet. The element is
	 *	mapped to an object using the createTuple Function (see the 
	 *	Constructors).
	 * @throws java.util.NoSuchElementException if the iteration has no more elements.
	 */
	public Object nextObject() {
		return createTuple.invoke(resultSet); //Abb.: ResultSet -> Tuple
	}

	/**
	 * Closes the underlying ResultSet. It is important to call this method when
	 * the Cursor is not used any longer.
	 *
	 * @throws xxl.core.util.WrappingRuntimeException if the underlying ResultSet
	 *	reports a SQLException.
	 */
	public void close () {
		super.close();
		try {
			resultSet.close();
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Returns the underlying ResultSet. Be careful: calling methods 
	 * of the ResultSet may influence the ResultSetMetaDataCursor object.
	 *
	 * @return the underlying ResultSet
	 */
	public ResultSet getResultSet () {
		return resultSet;
	}

	/**
	 * Removes from the underlying collection the last element returned by the
	 * cursor. This call does only work if the underlying ResultSet supports
	 * the deleteRow-call of JDBC 2.0.
	 * This method can be called only once per call to <tt>next</tt> or <tt>peek</tt>.
	 * The behavior of a cursor is unspecified if the underlying ResultSet is modified
	 * while the iteration is in progress in any way other than by calling this method.
	 *
	 * @throws xxl.core.util.WrappingRuntimeException if the underlying ResultSet
	 *	reports a SQLException.
	 */
	public void remove () throws WrappingRuntimeException {
		super.remove();
		try {
			resultSet.deleteRow();
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}
	
	/** Returns true if remove is supported. This method always returns true 
	 *  because this cursor supports remove. 
	 * 
	 * @return true if remove is supported */
	public boolean supportsRemove() {
		return true;
	}

	/**
	 * Resets the cursor to its initial state. <br>
	 * So the caller is able to traverse the underlying collection again.
	 * The modifications, removes and updates concerning the underlying collection,
	 * are still persistent. 
	 * This call does only work if the underlying ResultSet supports
	 * the beforeFirst-call of JDBC 2.0.
	 *
	 * @throws xxl.core.util.WrappingRuntimeException if the underlying ResultSet
	 *	reports a SQLException.
	 */
	public void reset () throws WrappingRuntimeException {
		super.reset();
		try {
			resultSet.beforeFirst();
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}
	/** Returns true if reset is supported. This method always returns true 
	 *  because this cursor is considered as resetable. 
	 * 
	 * @return true if reset is supported */
	public boolean supportsReset() {
		return true;
	}

	/**
	 * Replaces the object that was returned by the last call to <tt>next</tt>
	 * or <tt>peek</tt>.
	 * This operation must not be called after a call to
	 * <tt>hasNext</tt>. It should follow a call to <tt>next</tt> or <tt>peek</tt>.
	 * This method should be called only once per call to <tt>next</tt> or <tt>peek</tt>. <br>
	 * The behavior is unspecified if the underlying ResultSet
	 * is modified while the iteration is in progress in any way other than by calling
	 * this method. 
	 * This call does only work if the underlying ResultSet supports
	 * the updateObject-call of JDBC 2.0.
	 *
	 * @param object the object that replaces the object returned
	 * 		by the last call to <tt>next</tt> or <tt>peek</tt>.
	 * @throws xxl.core.util.WrappingRuntimeException if the underlying ResultSet
	 *	reports a SQLException.
	 */
	public void update (Object object) throws WrappingRuntimeException {
		super.update(object);
		try {
			Tuple tuple = (Tuple)object;
			for(int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
				resultSet.updateObject(i, tuple.getObject(i));
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}
	
	/** Returns true if update is supported. This method always returns true 
	 *  because this cursor supports update. 
	 * 
	 * @return true if update is supported */
	public boolean supportsUpdate() {
		return true;
	}

	/**
	 * Returns the ResultSetMetaData information for this MetaDataCursor.
	 *
	 * @return an Object of the type ResultSetMetaData representing metadata information for this Cursor.
	 * @throws xxl.core.util.WrappingRuntimeException if the underlying ResultSet
	 *	reports a SQLException.
	 */
	public Object getMetaData () {
		try {
			return resultSet.getMetaData();
		}
		catch (SQLException e) {
			throw new WrappingRuntimeException(e);
		}
	}
}
