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
import java.util.Comparator;

import xxl.core.collections.bags.Bag;
import xxl.core.collections.sweepAreas.BagSAImplementor;
import xxl.core.collections.sweepAreas.SweepArea;
import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.predicates.MetaDataPredicate;
import xxl.core.predicates.Not;
import xxl.core.predicates.Predicate;
import xxl.core.predicates.Predicates;
import xxl.core.relational.JoinUtils;
import xxl.core.relational.metaData.MergedResultSetMetaData;

/**
 * This class realizes a Join operator based on the sort-merge paradigm.
 * The two input relations have to be sorted. The algorithm of
 * {@link xxl.core.cursors.joins.SortMergeJoin} is used internally.
 * <br>
 * There are easy-to-use constructors and others, that need more
 * knowledge about the class {@link xxl.core.collections.sweepAreas.SweepArea}.
 * Note: tuples can only find join partners in a SweepArea. So, the
 * predicate and the comparator have to be chosen carefully!
 * <br>
 * Compared to {@link NestedLoopsJoin} this class does not directly support
 * a Cartesian product.
 */
public class SortMergeJoin extends xxl.core.cursors.joins.SortMergeJoin implements MetaDataCursor {

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

	// Default implementation for reorganization.
	// Will possibly not work correct for all SortMergeJoins!!!
	/**This class extends (@link sweepArea) and defines an inverse predicate.*/
	public static class PredicateBasedSA extends SweepArea {
		/**The inverse predicate*/
		protected Predicate inversePredicate;
		
		/**Construct an new PredicateBasedSA. 
		 * 
		 * @param bag the given bag.
		 * @param predicate the given predicate.
		 * @param ID the id of this PredicateBasedSA.*/
		public PredicateBasedSA (Bag bag, Predicate predicate, int ID) {
			super(new BagSAImplementor(bag), ID, false, 
				ID == 0 ? 
					new Predicate[] {null, predicate} : 
					new Predicate[] {Predicates.swapArguments(predicate), null},
				ID == 0 ? 
					new Predicate[] {null, new Not(predicate)} : 
					new Predicate[] {Predicates.swapArguments(new Not(predicate)), null}				
			);
		}
	}

	/**
	 * Internal static method computing the MetaData for all
	 * types of joins except theta joins.
	 *
	 * @param sortedCursor1 the input MetaDataCursor delivering the elements of the first relation.
	 * @param sortedCursor2 the input MetaDataCursor delivering the elements of the second relation.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined above in this class.
	 * @return MetaData that is appropriate for the type of join.
	 * @throws IllegalArgumentException
	 */
	public static ResultSetMetaData computeMetaData(MetaDataCursor sortedCursor1, MetaDataCursor sortedCursor2, int type) throws IllegalArgumentException {
		switch (type) {
			case THETA_JOIN :
				return JoinUtils.getThetaJoinMetaData((ResultSetMetaData)sortedCursor1.getMetaData(), (ResultSetMetaData)sortedCursor2.getMetaData());
			case LEFT_OUTER_JOIN :
			case RIGHT_OUTER_JOIN :
			case OUTER_JOIN :
			case NATURAL_JOIN :
				return JoinUtils.getNaturalJoinMetaData((ResultSetMetaData)sortedCursor1.getMetaData(), (ResultSetMetaData)sortedCursor2.getMetaData());
			case SEMI_JOIN :
				return (ResultSetMetaData)sortedCursor1.getMetaData();
			default :
				throw new IllegalArgumentException ("Undefined type specified in used method.");
		}
	}

	/**
	 * Internal static method computing the MetaDataPredicate for all
	 * types of joins except theta joins.
	 *
	 * @param sortedCursor1 the input MetaDataCursor delivering the elements of the first relation.
	 * @param sortedCursor2 the input MetaDataCursor delivering the elements of the second relation.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined above in this class.
	 * @return MetaDataPredicate that is appropriate for the type of join.
	 * @throws IllegalArgumentException
	 */
	public static MetaDataPredicate computeMetaDataPredicate(MetaDataCursor sortedCursor1, MetaDataCursor sortedCursor2, int type) throws IllegalArgumentException {
		switch (type) {
			case THETA_JOIN :
				throw new IllegalArgumentException ("for using a theta join, a join predicate must be specified");
			case LEFT_OUTER_JOIN :
			case RIGHT_OUTER_JOIN :
			case OUTER_JOIN :
			case NATURAL_JOIN :
				return JoinUtils.naturalJoin((ResultSetMetaData)sortedCursor1.getMetaData(), (ResultSetMetaData)sortedCursor2.getMetaData());
			case SEMI_JOIN :
				return JoinUtils.semiJoin((ResultSetMetaData)sortedCursor1.getMetaData(), (ResultSetMetaData)sortedCursor2.getMetaData());
			default :
				throw new IllegalArgumentException ("Undefined type specified in used method.");
		}
	}

	/**
	 * Internal Variable to hold metadata.
	 */
	protected ResultSetMetaData metaData;

	/**
	 * Constructs a new SortMergeJoin.
	 *
	 * @param sortedCursor1 the input MetaDataCursor delivering the elements of the first relation.
	 * @param sortedCursor2 the input MetaDataCursor delivering the elements of the second relation.
     * @param metaData the given metadata.
	 * @param sweepArea1 the first sweeparea.
	 * @param sweepArea2 the second sweeparea.
	 * @param comparator Comparator that gets an object from every input relation and determines, which
	 *	tuple has to be inserted into the SweepArea first.
	 * @param createTuple Function that maps an Object array (column values) and a
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined above in this class.
	 */
	protected SortMergeJoin (MetaDataCursor sortedCursor1, MetaDataCursor sortedCursor2, ResultSetMetaData metaData, SweepArea sweepArea1, SweepArea sweepArea2, Comparator comparator, Function createTuple, int type) {
		super(
			sortedCursor1,
			sortedCursor2,
			sweepArea1,
			sweepArea2,
			comparator,
			type == THETA_JOIN || type == NATURAL_JOIN ?
				JoinUtils.naturalJoinTuple(createTuple, (MergedResultSetMetaData)metaData) :
				JoinUtils.semiJoinTuple(createTuple, metaData)
		);
		if (type != THETA_JOIN && type != NATURAL_JOIN && type != SEMI_JOIN)
			throw new IllegalArgumentException ("Undefined type specified in used constructor.");
		this.metaData = metaData;	
	}

	// MetaDataPredicate is used to provide the right join metadata!
	// It is not used in the SweepAreas. This has to be done explicitely 
	// by the developer who specifies the SweepAreas.
	
	/**
	 * Constructs a new SortMergeJoin.
	 *
	 * @param sortedCursor1 the input MetaDataCursor delivering the elements of the first relation.
	 * @param sortedCursor2 the input MetaDataCursor delivering the elements of the second relation.
	 * @param theta predicate that determines if two tuples qualify for the result.
	 * @param sweepArea1 the first sweeparea.
	 * @param sweepArea2 the second sweeparea.
	 * @param comparator Comparator that gets an object from every input relation and determines, which
	 *	tuple has to be inserted into the SweepArea first.
	 * @param createTuple Function that maps an Object array (column values) and a
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined above in this class.
	 */
	public SortMergeJoin (MetaDataCursor sortedCursor1, MetaDataCursor sortedCursor2, MetaDataPredicate theta, SweepArea sweepArea1, SweepArea sweepArea2, Comparator comparator, Function createTuple, int type) {
		this(
			sortedCursor1,
			sortedCursor2,
			(ResultSetMetaData)theta.getMetaData(),
			sweepArea1,
			sweepArea2,
			comparator,
			createTuple,
			type
		);
	}
	
	/**
	 * Constructs a new SortMergeJoin.
	 *
	 * @param sortedCursor1 the input MetaDataCursor delivering the elements of the first relation.
	 * @param sortedCursor2 the input MetaDataCursor delivering the elements of the second relation.
	 * @param sweepArea1 the first sweeparea.
	 * @param sweepArea2 the second sweeparea.
	 * @param comparator Comparator that gets an object from every input relation and determines, which
	 *	tuple has to be inserted into the SweepArea first.
	 * @param createTuple Function that maps an Object array (column values) and a
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined above in this class.
	 */
	public SortMergeJoin (MetaDataCursor sortedCursor1, MetaDataCursor sortedCursor2, SweepArea sweepArea1, SweepArea sweepArea2, Comparator comparator, Function createTuple, int type) {
		this(
			sortedCursor1,
			sortedCursor2,
			computeMetaData(sortedCursor1, sortedCursor2, type),
			sweepArea1,
			sweepArea2,
			comparator,
			createTuple,
			type
		);
	}

	/**
	 * Constructs a new SortMergeJoin.
	 *
	 * @param sortedCursor1 the input MetaDataCursor delivering the elements of the first relation.
	 * @param sortedCursor2 the input MetaDataCursor delivering the elements of the second relation.
	 * @param theta MetaDataPredicate that determines if two tuples qualify for the result. The getMetaData
	 *	method of this predicate computes the metadata of the join.
	 * @param newBag Factory method returning a bag that is used inside a SweepArea. Both SweepAreas are
	 *	constructed by using SWEEPAREA_FACTORY_METHOD from above.
	 * @param comparator Comparator that gets an object from every input relation and determines, which
	 *	tuple has to be inserted into the SweepArea first.
	 * @param createTuple Function that maps an Object array (column values) and a
	 *	ResultSetMetaData object to a new result Tuple. xxl.core.relational.ArrayTuple.FACTORY_METHOD
	 *	can be used.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined above in this class.
	 */
	public SortMergeJoin (MetaDataCursor sortedCursor1, MetaDataCursor sortedCursor2, MetaDataPredicate theta, Function newBag, Comparator comparator, Function createTuple, int type) {
		// Converting constructor call to constructor in package cursors. Here,
		// that means that createTuple and TYPE has to become transformed.
		this(
			sortedCursor1,
			sortedCursor2, 
			theta,
			new PredicateBasedSA((Bag)newBag.invoke(), theta, 0),
			new PredicateBasedSA((Bag)newBag.invoke(), theta, 1),
			comparator,
			createTuple,
			type
		);
	}
	
	/**
	 * Constructs a new SortMergeJoin.
	 *
	 * @param sortedCursor1 the input MetaDataCursor delivering the elements of the first relation.
	 * @param sortedCursor2 the input MetaDataCursor delivering the elements of the second relation.
	 * @param theta predicate that determines if two tuples qualify for the result.
     * @param newBag Factory method returning a bag that is used inside a SweepArea. Both SweepAreas are
	 *	constructed by using SWEEPAREA_FACTORY_METHOD from above.
	 * @param comparator Comparator that gets an object from every input relation and determines, which
	 *	tuple has to be inserted into the SweepArea first.
	 * @param createTuple Function that maps an Object array (column values) and a
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined in the class {@link xxl.core.cursors.joins.SortMergeJoin}.
	 */
	public SortMergeJoin (MetaDataCursor sortedCursor1, MetaDataCursor sortedCursor2, Predicate theta, Function newBag, Comparator comparator, Function createTuple, int type) {
		this(
			sortedCursor1,
			sortedCursor2,
			JoinUtils.thetaJoin(theta, (ResultSetMetaData)sortedCursor1.getMetaData(), (ResultSetMetaData)sortedCursor2.getMetaData()),
			newBag,
			comparator,
			createTuple,
			type
		);
	}

	/**
	 * Constructs a new SortMergeJoin. The predicate is computed automatically. <b>This is
	 * the constructor that is used most</b>.
	 *
	 * @param sortedCursor1 the input MetaDataCursor delivering the elements of the first relation.
	 * @param sortedCursor2 the input MetaDataCursor delivering the elements of the second relation.
	 * @param newBag Factory method returning a bag that is used inside a SweepArea. Both SweepAreas are
	 *	constructed by using SWEEPAREA_FACTORY_METHOD from above.
	 * @param comparator Comparator that gets an object from every input relation and determines, which
	 *	tuple has to be inserted into the SweepArea first.
	 * @param createTuple Function that maps an Object array (column values) and a
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined above in this class.
	 */
	public SortMergeJoin (MetaDataCursor sortedCursor1, MetaDataCursor sortedCursor2, Function newBag, Comparator comparator, Function createTuple, int type) {
		this(
			sortedCursor1,
			sortedCursor2,
			computeMetaDataPredicate(sortedCursor1, sortedCursor2, type),
			newBag,
			comparator,
			createTuple,
			type
		);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////// ResultSet Constructors following !!! //////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////

	// MetaDataPredicate is used to provide the right join metadata!
	// It is not used in the SweepAreas. This has to be done explicitely 
	// by the developer who specifies the SweepAreas.
	
	/**
	 * Constructs a new SortMergeJoin.
	 *
	 * @param sortedResultSet1 the input ResultSet delivering the elements of the first relation.
	 * @param sortedResultSet2 the input ResultSet delivering the elements of the second relation.
	 * @param theta predicate that determines if two tuples qualify for the result.
	 * @param sweepArea1 the first sweeparea.
	 * @param sweepArea2 the second sweeparea.
	 * @param comparator Comparator that gets an object from every input relation and determines, which
	 *	tuple has to be inserted into the SweepArea first.
	 * @param createTuple Function that maps an Object array (column values) and a
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined above in this class.
	 */
	public SortMergeJoin (ResultSet sortedResultSet1, ResultSet sortedResultSet2, MetaDataPredicate theta, SweepArea sweepArea1, SweepArea sweepArea2, Comparator comparator, Function createTuple, int type) {
		this(
			new ResultSetMetaDataCursor(sortedResultSet1),
			new ResultSetMetaDataCursor(sortedResultSet2),
			theta,
			sweepArea1,
			sweepArea2,
			comparator,
			createTuple,
			type
		);
	}
		
	/**
	 * Constructs a new SortMergeJoin.
	 *
	 * @param sortedResultSet1 the input ResultSet delivering the elements of the first relation.
	 * @param sortedResultSet2 the input ResultSet delivering the elements of the second relation.
	 * @param sweepArea1 the first sweeparea.
	 * @param sweepArea2 the second sweeparea.
	 * @param comparator Comparator that gets an object from every input relation and determines, which
	 *	tuple has to be inserted into the SweepArea first.
	 * @param createTuple Function that maps an Object array (column values) and a
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined above in this class.
	 */
	
	public SortMergeJoin (ResultSet sortedResultSet1, ResultSet sortedResultSet2, SweepArea sweepArea1, SweepArea sweepArea2, Comparator comparator, Function createTuple, int type) {
		this(
			new ResultSetMetaDataCursor(sortedResultSet1),
			new ResultSetMetaDataCursor(sortedResultSet2),
			sweepArea1,
			sweepArea2,
			comparator,
			createTuple,
			type
		);
	}
		
	/**
	 * Constructs a new SortMergeJoin.
	 *
	 * @param sortedResultSet1 the input ResultSet delivering the elements of the first relation.
	 * @param sortedResultSet2 the input ResultSet delivering the elements of the second relation.
	 * @param theta MetaDataPredicate that determines if two tuples qualify for the result. The getMetaData
	 *	method of this predicate computes the metadata of the join.
	 * @param newBag Factory method returning a bag that is used inside a SweepArea. Both SweepAreas are
	 *	constructed by using SWEEPAREA_FACTORY_METHOD from above.
	 * @param comparator Comparator that gets an object from every input relation and determines, which
	 *	tuple has to be inserted into the SweepArea first.
	 * @param createTuple Function that maps an Object array (column values) and a
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined above in this class.
	 */
	public SortMergeJoin (ResultSet sortedResultSet1, ResultSet sortedResultSet2, MetaDataPredicate theta, Function newBag, Comparator comparator, Function createTuple, int type) {
		this (
			new ResultSetMetaDataCursor(sortedResultSet1),
			new ResultSetMetaDataCursor(sortedResultSet2),
			theta,
			newBag,
			comparator,
			createTuple,
			type
		);
	}

	/**
	 * Constructs a new SortMergeJoin.
	 *
	 * @param sortedResultSet1 the input ResultSet delivering the elements of the first relation.
	 * @param sortedResultSet2 the input ResultSet delivering the elements of the second relation.
	 * @param theta Predicate that determines if two tuples qualify for the result.
	 * @param newBag Factory method returning a bag that is used inside a SweepArea. Both SweepAreas are
	 *	constructed by using SWEEPAREA_FACTORY_METHOD from above.
	 * @param comparator Comparator that gets an object from every input relation and determines, which
	 *	tuple has to be inserted into the SweepArea first.
	 * @param createTuple Function that maps an Object array (column values) and a
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined in the class {@link xxl.core.cursors.joins.SortMergeJoin}.
	 */
	public SortMergeJoin (ResultSet sortedResultSet1, ResultSet sortedResultSet2, Predicate theta, Function newBag, Comparator comparator, Function createTuple, int type) {
		this(
			new ResultSetMetaDataCursor(sortedResultSet1),
			new ResultSetMetaDataCursor(sortedResultSet2),
			theta,
			newBag,
			comparator,
			createTuple,
			type
		);
	}

	/**
	 * Constructs a new SortMergeJoin. The predicate is computed automatically. <b>This is
	 * the constructor that is used most</b>.
	 *
	 * @param sortedResultSet1 the input ResultSet delivering the elements of the first relation.
	 * @param sortedResultSet2 the input ResultSet delivering the elements of the second relation.
	 * @param newBag Factory method returning a bag that is used inside a SweepArea. Both SweepAreas are
	 *	constructed by using SWEEPAREA_FACTORY_METHOD from above.
	 * @param comparator Comparator that gets an object from every input relation and determines, which
	 *	tuple has to be inserted into the SweepArea first.
	 * @param createTuple Function that maps an Object array (column values) and a
	 *	ResultSetMetaData object to a new result Tuple. xxl.core.relational.ArrayTuple.FACTORY_METHOD
	 *	can be used.
	 * @param type the type of the join operation. Possible values are the values of
	 *	the constants defined above in this class.
	 */
	public SortMergeJoin (ResultSet sortedResultSet1, ResultSet sortedResultSet2, Function newBag, Comparator comparator, Function createTuple, int type) {
		this (
			new ResultSetMetaDataCursor(sortedResultSet1),
			new ResultSetMetaDataCursor(sortedResultSet2),
			newBag,
			comparator,
			createTuple,
			type
		);
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
