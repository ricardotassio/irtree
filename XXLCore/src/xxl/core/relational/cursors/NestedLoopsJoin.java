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
import java.sql.ResultSetMetaData;

import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.predicates.MetaDataPredicate;
import xxl.core.predicates.Predicate;
import xxl.core.relational.JoinUtils;
import xxl.core.relational.metaData.MergedResultSetMetaData;

/**
 * Nested loops implementation of a flexible join operator.
 * 
 * The following types of joins are available. For a complete discussion
 * of join operations read the adequate chapter of a good book on database 
 * systems, e.g. "Datenbanksysteme" from Kemper, Eickler.
 * <br><br>
 *
 *	<table border>
 *	<tr><th>Static variable</th></tr>
 * 	<tr><td>THETA_JOIN</td></tr>
 *	<tr><td>LEFT_OUTER_JOIN</td></tr>
 *	<tr><td>RIGHT_OUTER_JOIN</td></tr>
 *	<tr><td>OUTER_JOIN</td></tr>
 *	<tr><td>NATURAL_JOIN</td></tr>
 *	<tr><td>SEMI_JOIN</td></tr>
 *	<tr><td>CARTESIAN_PRODUCT</td></tr>
 *	</table>
 *
 * <br>
 * Updates and removes are not supported.
 */
public class NestedLoopsJoin extends xxl.core.cursors.joins.NestedLoopsJoin implements MetaDataCursor {

	/**
	 * Constant specifying a theta join.
	 * Only the tuples for which the specified Predicate is <tt>true</tt>
	 * will be returned.
	 */
	public static final int THETA_JOIN = 0;
	/**
	 * Constant specifying a left outer join.
	 * The tuples for which the specified Predicate is <tt>true</tt>
	 * as well as all elements of the <tt>cursor1</tt> not qualifying concerning the Predicate will be returned.
	 * The Function <tt>newResult</tt> is called with arguments <code>cursor1.peek(), null</code>.
	 */
	public static final int LEFT_OUTER_JOIN = 1;
	/**
	 * Constant specifying a right outer join.
	 * The tuples for which the specified Predicate is <tt>true</tt>
	 * as well as all elements of the <tt>cursor2</tt> not qualifying concerning the Predicate will be returned.
	 * The Function <tt>newResult</tt> is called with arguments <code>null, cursor2.peek()</code>.
	 */
	public static final int RIGHT_OUTER_JOIN = 2;
	/**
	 * Constant specifying a full outer join.
	 * The tuples for which the specified Predicate is <tt>true</tt>
	 * as well as all tuples of the LEFT and RIGHT OUTER_JOIN will be returned.
	 */
	public static final int OUTER_JOIN = 3;
	/**
	 * Constant specifying a natural join.
	 * The tuples are compared using their common columns.
	 */
	public static final int NATURAL_JOIN = 4;
	/**
	 * Constant specifying a semi join.
	 * The tuples are compared using their common columns. The
	 * results become projected to the columns of the first
	 * input relation.
	 */
	public static final int SEMI_JOIN = 5;
	/**
	 * Constant specifying a Cartesian product.
	 * Every combination of tuples of the two relations is returned.
	 */
	public static final int CARTESIAN_PRODUCT = 6;

	/**
	 * Internal variable used for storing metadata 
	 */
	protected ResultSetMetaData metaData;

	/**
	 * Private constructor creating a new NestedLoopsJoin with lazy evaluation backed on
	 * two cursors using a MetaDataPredicate to select the resulting tuples.
	 * This constructor also supports the handling
	 * of a non-resetable input MetaDataCursor, <tt>cursor2</tt>, because a parameterless
	 * Function can be defined that returns this input MetaDataCursor. Furthermore
	 * a Function can be specified that is invoked on each qualifying tuple
	 * before it is returned to the caller concerning a call to <tt>next()</tt>.
	 * This Function is a kind of factory-method to model the resulting object.
	 *
	 * @param cursor1 the input MetaDataCursor that is traversed in the "outer"
	 * 		loop.
	 * @param cursor2 the input MetaDataCursor that is traversed in the "inner"
	 * 		loop.
	 * @param newCursor a parameterless function that delivers a new MetaDataCursor, when the
	 * 		Cursor <code>cursor2</code> cannot be reset, i.e.
	 * 		<code>cursor2.reset()</code> will cause a
	 * 		{@link java.lang.UnsupportedOperationException}.
	 * @param predicate the predicate evaluated for each tuple
	 * 		of elements backed on one element of each input-Cursor
	 * 		in order to select them. Only these tuples where the
	 * 		predicate's evaluation result is <tt>true</tt> have been
	 * 		qualified to be a result of the join-operation.
	 * @param createTuple a factory method (Function) that takes two parameters as arguments
	 * 		and is invoked on each tuple where the predicate's evaluation result
	 * 		is <tt>true</tt>, i.e. on each qualifying tuple before it is
	 * 		returned to the caller concerning a call to <tt>next()</tt>.
	 * @param TYPE the type of the join operation. Possible values are the values of 
	 *		the constants defined above in this class.
	 * @throws IllegalArgumentException if the specified type is not valid.
	 * @throws UnsupportedOperationException if input MetaDataCursor <tt>cursor1</tt> or <tt>cursor2</tt> cannot be reseted.
	 */
	private NestedLoopsJoin (MetaDataCursor cursor1, MetaDataCursor cursor2, Function newCursor, MetaDataPredicate predicate, Function createTuple, int TYPE) {
		super(cursor1, cursor2, newCursor, predicate,
			TYPE >= 0 && TYPE != 5 ? JoinUtils.naturalJoinTuple(createTuple, (MergedResultSetMetaData)predicate.getMetaData()) :
				JoinUtils.semiJoinTuple(createTuple, (ResultSetMetaData)predicate.getMetaData()),
			TYPE > 3 ? 0 : TYPE
		);
		if (TYPE < 0 || TYPE > 6)
			throw new IllegalArgumentException ("Undefined type specified in used constructor.");
		metaData = (ResultSetMetaData)predicate.getMetaData();
	}

	/**
	 * Creates a new NestedLoopsJoin which performs a theta join. 
	 * Lazy evaluation is used backed on
	 * two cursors using a Predicate to select the resulting tuples.
	 * This constructor also supports the handling
	 * of a non-resetable input MetaDataCursor, <tt>cursor2</tt>, because a parameterless
	 * Function can be defined that returns this input MetaDataCursor. Furthermore
	 * a Function can be specified that is invoked on each qualifying tuple
	 * before it is returned to the caller concerning a call to <tt>next()</tt>.
	 * This Function is a kind of factory-method to model the resulting object.
	 *
	 * @param cursor1 the input MetaDataCursor that is traversed in the "outer"
	 * 		loop.
	 * @param cursor2 the input MetaDataCursor that is traversed in the "inner"
	 * 		loop.
	 * @param newCursor a parameterless function that delivers a new MetaDataCursor, when the
	 * 		Cursor <code>cursor2</code> cannot be reset, i.e.
	 * 		<code>cursor2.reset()</code> will cause a
	 * 		{@link java.lang.UnsupportedOperationException}.
	 * @param theta the predicate evaluated for each tuple
	 * 		of elements backed on one element of each input-Cursor
	 * 		in order to select them. Only these tuples where the
	 * 		predicate's evaluation result is <tt>true</tt> have been
	 * 		qualified to be a result of the join-operation.
	 * @param createTuple a factory method (Function) that takes two parameters as arguments
	 * 		and is invoked on each tuple where the predicate's evaluation result
	 * 		is <tt>true</tt>, i.e. on each qualifying tuple before it is
	 * 		returned to the caller concerning a call to <tt>next()</tt>.
	 * @throws IllegalArgumentException if the specified type is not valid.
	 * @throws UnsupportedOperationException if input MetaDataCursor <tt>cursor1</tt> or <tt>cursor2</tt> cannot be reseted.
	 */
	public NestedLoopsJoin (MetaDataCursor cursor1, MetaDataCursor cursor2, Function newCursor, Predicate theta, Function createTuple) {
		this(cursor1, cursor2, newCursor,
			JoinUtils.thetaJoin(theta, (ResultSetMetaData)cursor1.getMetaData(), (ResultSetMetaData)cursor2.getMetaData()),
			createTuple,
			THETA_JOIN
		);
	}

	/**
	 * Creates a new NestedLoopsJoin with lazy evaluation backed on
	 * two cursors using a Predicate to select the resulting tuples.
	 * This constructor also supports the handling
	 * of a non-resetable input MetaDataCursor, <tt>cursor2</tt>, because a parameterless
	 * Function can be defined that returns this input MetaDataCursor. Furthermore
	 * a Function can be specified that is invoked on each qualifying tuple
	 * before it is returned to the caller concerning a call to <tt>next()</tt>.
	 * This Function is a kind of factory-method to model the resulting object.
	 *
	 * @param cursor1 the input MetaDataCursor that is traversed in the "outer"
	 * 		loop.
	 * @param cursor2 the input MetaDataCursor that is traversed in the "inner"
	 * 		loop.
	 * @param newCursor a parameterless function that delivers a new MetaDataCursor, when the
	 * 		Cursor <code>cursor2</code> cannot be reset, i.e.
	 * 		<code>cursor2.reset()</code> will cause a
	 * 		{@link java.lang.UnsupportedOperationException}.
	 * @param createTuple a factory method (Function) that takes two parameters as arguments
	 * 		and is invoked on each tuple where the predicate's evaluation result
	 * 		is <tt>true</tt>, i.e. on each qualifying tuple before it is
	 * 		returned to the caller concerning a call to <tt>next()</tt>.
	 * @param TYPE the type of the join operation. Possible values are the values of 
	 *		the constants defined above in this class.
	 * @throws IllegalArgumentException if the specified type is not valid.
	 * @throws UnsupportedOperationException if input MetaDataCursor <tt>cursor1</tt> or <tt>cursor2</tt> cannot be reseted.
	 */
	public NestedLoopsJoin (MetaDataCursor cursor1, MetaDataCursor cursor2, Function newCursor, Function createTuple, int TYPE) {
		this(cursor1, cursor2, newCursor,
			TYPE > 0 && TYPE < 5 ? JoinUtils.naturalJoin((ResultSetMetaData)cursor1.getMetaData(), (ResultSetMetaData)cursor2.getMetaData()) :
			TYPE == 5 ? JoinUtils.semiJoin((ResultSetMetaData)cursor1.getMetaData(), (ResultSetMetaData)cursor2.getMetaData()) :
			TYPE == 6 ? JoinUtils.thetaJoin(Predicate.TRUE, (ResultSetMetaData)cursor1.getMetaData(), (ResultSetMetaData)cursor2.getMetaData()) : null,
			createTuple,
			TYPE
		);
	}

	/**
	 * Creates a new NestedLoopsJoin which performs a theta join. 
	 * Lazy evaluation is used backed on
	 * two cursors using a Predicate to select the resulting tuples.
	 * This constructor also supports the handling
	 * of a non-resetable input MetaDataCursor, <tt>cursor2</tt>, because a parameterless
	 * Function can be defined that returns this input MetaDataCursor. Furthermore
	 * a Function can be specified that is invoked on each qualifying tuple
	 * before it is returned to the caller concerning a call to <tt>next()</tt>.
	 * This Function is a kind of factory-method to model the resulting object.
	 *
	 * @param resultSet1 the input ResultSet that is traversed in the "outer"
	 * 		loop.
	 * @param resultSet2 the input ResultSet that is traversed in the "inner"
	 * 		loop.
	 * @param newCursor a parameterless function that delivers a new ResultSet, when the
	 * 		Cursor <code>cursor2</code> cannot be reset, i.e.
	 * 		<code>cursor2.reset()</code> will cause a
	 * 		{@link java.lang.UnsupportedOperationException}.
	 * @param predicate the predicate evaluated for each tuple
	 * 		of elements backed on one element of each input-Cursor
	 * 		in order to select them. Only these tuples where the
	 * 		predicate's evaluation result is <tt>true</tt> have been
	 * 		qualified to be a result of the join-operation.
	 * @param createTuple a factory method (Function) that takes two parameters as arguments
	 * 		and is invoked on each tuple where the predicate's evaluation result
	 * 		is <tt>true</tt>, i.e. on each qualifying tuple before it is
	 * 		returned to the caller concerning a call to <tt>next()</tt>.
	 * @throws IllegalArgumentException if the specified type is not valid.
	 * @throws UnsupportedOperationException if input MetaDataCursor <tt>cursor1</tt> or <tt>cursor2</tt> cannot be reseted.
	 */
	public NestedLoopsJoin (ResultSet resultSet1, ResultSet resultSet2, Function newCursor, Predicate predicate, Function createTuple) {
		this(new ResultSetMetaDataCursor(resultSet1), new ResultSetMetaDataCursor(resultSet2), newCursor, predicate, createTuple);
	}

	/**
	 * Creates a new NestedLoopsJoin with lazy evaluation backed on
	 * two cursors using a Predicate to select the resulting tuples.
	 * This constructor also supports the handling
	 * of a non-resetable input MetaDataCursor, <tt>cursor2</tt>, because a parameterless
	 * Function can be defined that returns this input MetaDataCursor. Furthermore
	 * a Function can be specified that is invoked on each qualifying tuple
	 * before it is returned to the caller concerning a call to <tt>next()</tt>.
	 * This Function is a kind of factory-method to model the resulting object.
	 *
	 * @param resultSet1 the input ResultSet that is traversed in the "outer"
	 * 		loop.
	 * @param resultSet2 the input ResultSet that is traversed in the "inner"
	 * 		loop.
	 * @param newCursor a parameterless function that delivers a new ResultSet, when the
	 * 		Cursor <code>cursor2</code> cannot be reset, i.e.
	 * 		<code>cursor2.reset()</code> will cause a
	 * 		{@link java.lang.UnsupportedOperationException}.
	 * @param createTuple a factory method (Function) that takes two parameters as arguments
	 * 		and is invoked on each tuple where the predicate's evaluation result
	 * 		is <tt>true</tt>, i.e. on each qualifying tuple before it is
	 * 		returned to the caller concerning a call to <tt>next()</tt>.
	 * @param TYPE the type of the join operation. Possible values are the values of 
	 *		the constants defined above in this class.
	 * @throws IllegalArgumentException if the specified type is not valid.
	 * @throws UnsupportedOperationException if input MetaDataCursor <tt>cursor1</tt> or <tt>cursor2</tt> cannot be reseted.
	 */
	public NestedLoopsJoin (ResultSet resultSet1, ResultSet resultSet2, Function newCursor, Function createTuple, int TYPE) {
		this(new ResultSetMetaDataCursor(resultSet1), new ResultSetMetaDataCursor(resultSet2), newCursor, createTuple, TYPE);
	}

	/**
	 * Returns the resulting metadata for the join operation.
	 *
	 * @return object of class ResultSetMetaData
	 */
	public Object getMetaData () {
		return metaData;
	}
}
