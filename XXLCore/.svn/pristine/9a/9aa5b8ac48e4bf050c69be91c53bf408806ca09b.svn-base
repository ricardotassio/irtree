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

import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import xxl.core.comparators.ComparableComparator;
import xxl.core.cursors.SecureDecoratorCursor;
import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.functions.MetaDataFunction;
import xxl.core.predicates.MetaDataPredicate;
import xxl.core.predicates.Predicate;
import xxl.core.relational.JoinUtils;
import xxl.core.relational.Tuple;
import xxl.core.relational.Tuples;
import xxl.core.spatial.points.FloatPoint;
import xxl.core.spatial.KPEzCode;
import xxl.core.spatial.points.Point;
import xxl.core.spatial.cursors.Mappers;
import xxl.core.spatial.cursors.Orenstein.OrensteinSA;

/**
 *	Operator computing a spatial join with the method proposed by Jack Orenstein.
 *	<br><br>
 *	The spatial join algorithm based on space-filling curves proposed by Jack Orenstein.
 *	See: [Ore 91] Jack A. Orenstein: An Algorithm for Computing the Overlay of k-Dimensional Spaces. SSD 1991:
 *	381-400 for a detailed explanation. See: [DS 01]: Jens-Peter Dittrich, Bernhard Seeger: GESS: a Scalable Similarity-Join Algorithm for Mining Large Data Sets in High Dimensional
 *	Spaces. ACM SIGKDD-2001. for a review on Orensteins algorithm.
 *	<br><br>
 *	Orensteins algorithm is based on a binary recursive partitioning, where the binary code
 *	represents the so-called Z-ordering (z-codes).
 *	<br><br>
 *	Orensteins algorithm (ORE) assigns each hypercube of the input relations to disjoint
 *	subspaces of the recursive partitioning whose union entirely
 *	covers the hypercube. ORE sorts the two sets of
 *	hypercubes derived from the input relations (including the
 *	possible replicates) w.r.t. the lexicographical ordering of its
 *	binary code. After that, the relations are merged using two
 *	main-memory stacks Stack_R and Stack_S. It is guaranteed that for two adjacent
 *	hypercubes in the stack, the prefix property is satisfied for
 *	their associated codes. Only those hypercubes are joined
 *	that have the same prefix code.
 *	<br><br>
 *	A deficiency of ORE is that the different assignment strategies
 *	examined in [Ore91] cause substantial replication rates. This
 *	results in an increase of the problem space and hence, sorting
 *	will be very expensive. Furthermore, ORE has not addressed the
 *	problem of eliminating duplicates in the result set.
 *	<br><br>
 *	Note that the method <code>reorganize(final Object
 *	currentStatus)</code> could actually be implemented with only 1 LOC. For efficiency
 *	reasons we use a somewhat longer version of the method here.
 *	<br>
 *
 *	@see xxl.core.relational.cursors.SortMergeJoin
 *	@see xxl.core.spatial.cursors.Mappers
 *	@see xxl.core.spatial.cursors.GESS
 *	@see xxl.core.spatial.cursors.Replicator
 *	@see xxl.core.spatial.cursors.Orenstein
 */
public class Orenstein extends SortMergeJoin {

	/**
	 * Static function returning a mapper that gets a Tuple as its only input parameter
	 * and returns a {@link xxl.core.spatial.points.FloatPoint}.
	 */
	public static Function TUPLE_TO_FLOAT_POINT = new Function () {

		public Object invoke (Object o) {
			Tuple tuple = (Tuple)o;
			float[] values = new float[Tuples.getSize(tuple)];
			for (int i = 1; i <= values.length; i++)
				values[i-1] = tuple.getFloat(i);
			return new FloatPoint(values);
		}
	};

	/**
	 * Static inner class mapping a {@link xxl.core.spatial.cursors.Mappers#getFloatPointKPEzCodeMapper(Iterator,float,int)} 
	 * to a MetaDataCursor 
	 */
	public static class FloatPointKPEzCodeMapper extends SecureDecoratorCursor implements MetaDataCursor {

		/**
		 * Internal attribute for storing the metadata. 
		 */
		protected ResultSetMetaData metaData;

		/**
		 * Constructs a new FloatPointKPEzCodeMapper.
		 *
		 * @param input the input metadata of the mapper
		 * @param epsilon epsilon-distance
		 * @param maxLevel maximum level of the partitioning
		 */
		public FloatPointKPEzCodeMapper (MetaDataCursor input, float epsilon, int maxLevel) {
			super (Mappers.getFloatPointKPEzCodeMapper(input, epsilon, maxLevel));
			metaData = (ResultSetMetaData)input.getMetaData();
		}

		/** 
		 * Returns the metadata of the operation. It is the same
		 * than the input metadata of the mapper.
		 * 
		 * @return ResultSetMetaData object of the projection
		 */
		public Object getMetaData () {
			return metaData;
		}
	}

	/**
	 * Constructs a new Orenstein join algorithm.
	 *
	 * @param input1 the first input MetaDataCursor
	 * @param input2 the second input MetaDataCursor
	 * @param joinPredicate the join predicate to use (MetaDataPredicate!)
	 * @param newSorter function for sorting input cursors
	 * @param createTuple a factory method generating the desired tuples contained in the cursors
	 * @param initialCapacity the initial capacity of the sweep areas
	 * @param p fraction of elements to be used from the input
	 * @param seed the seed to be used for the Sampler
	 * @param epsilon epsilon-distance
	 * @param maxLevel maximum level of the partitioning
	 * @param type the join type (see {@link xxl.core.relational.cursors.SortMergeJoin}).
	 */
	private Orenstein(final MetaDataCursor input1, final MetaDataCursor input2, final MetaDataPredicate joinPredicate,
		Function newSorter, final Function createTuple, int initialCapacity, double p,
		long seed, float epsilon, int maxLevel, int type){

		super(
			(MetaDataCursor)newSorter.invoke(
				new FloatPointKPEzCodeMapper(
		       		new Sampler(new Mapper(input1,
						new MetaDataFunction(TUPLE_TO_FLOAT_POINT) {
							public Object getMetaData () {
								return input1.getMetaData();
							}
						}
					), p, seed),
					epsilon, maxLevel
				)
			),
		 	(MetaDataCursor)newSorter.invoke(
			 	new FloatPointKPEzCodeMapper(
		       		new Sampler(new Mapper(input2,
						new MetaDataFunction(TUPLE_TO_FLOAT_POINT) {
							public Object getMetaData () {
								return input2.getMetaData();
							}
						}
					), p, seed),
					epsilon, maxLevel
				)
			),
		 	joinPredicate,
			new OrensteinSA(0, initialCapacity, joinPredicate),
			new OrensteinSA(1, initialCapacity, joinPredicate),
			ComparableComparator.DEFAULT_INSTANCE,
			new Function () {
				protected ResultSetMetaData metaData = (ResultSetMetaData)joinPredicate.getMetaData();

				public Object invoke (Object o1, Object o2) {
					Point p1 = (Point)((KPEzCode)o1).getData();
					Point p2 = (Point)((KPEzCode)o2).getData();
					Object[] values = new Object[p1.dimensions() + p2.dimensions()];
					for (int i = 0; i < values.length; i++)
						values[i] = i < p1.dimensions() ? new Double(p1.getValue(i)) : new Double(p2.getValue(i-p1.dimensions()));
					return createTuple.invoke(values, metaData);
				}
			},
			type
		);
	}

	/**
	 * Constructs a new Orenstein join algorithm.
	 *
	 * @param input1 the first input MetaDataCursor
	 * @param input2 the second input MetaDataCursor
	 * @param joinPredicate the join predicate to use
	 * @param newSorter function for sorting input cursors
	 * @param createTuple a factory method generating the desired tuples contained in the cursors
	 * @param initialCapacity the initial capacity of the sweep areas
	 * @param p fraction of elements to be used from the input
	 * @param seed the seed to be used for the Sampler
	 * @param epsilon epsilon-distance
	 * @param maxLevel maximum level of the partitioning
	 * @param type the join type (see {@link xxl.core.relational.cursors.SortMergeJoin}).
	 *	Here, only the types THETA_JOIN, LEFT_OUTER_JOIN, RIGHT_OUTER_JOIN and OUTER_JOIN are allowed
	 */
	public Orenstein (final MetaDataCursor input1, final MetaDataCursor input2, Predicate joinPredicate,
		Function newSorter, final Function createTuple, int initialCapacity, double p,
		long seed, float epsilon, int maxLevel, int type){

		this(input1, input2,
			JoinUtils.thetaJoin(joinPredicate, (ResultSetMetaData)input1.getMetaData(), (ResultSetMetaData)input2.getMetaData()),
			newSorter, createTuple, initialCapacity, p, seed, epsilon, maxLevel, type
		);
		if (type < 0 || type > 3)
			throw new IllegalArgumentException ("Undefined type specified in used constructor. Only types THETA_JOIN, LEFT_OUTER_JOIN, RIGHT_OUTER_JOIN and OUTER_JOIN are allowed.");
	}

	/**
	 * Constructs a new Orenstein join algorithm.
	 *
	 * @param input1 the first input ResultSet
	 * @param input2 the second input ResultSet
	 * @param joinPredicate the join predicate to use
	 * @param newSorter function for sorting input cursors
	 * @param createTuple a factory method generating the desired tuples contained in the cursors
	 * @param initialCapacity the initial capacity of the sweep areas
	 * @param p fraction of elements to be used from the input
	 * @param seed the seed to be used for the Sampler
	 * @param epsilon epsilon-distance
	 * @param maxLevel maximum level of the partitioning
	 * @param type the join type (see {@link xxl.core.relational.cursors.SortMergeJoin}).
	 *	Here, only the types THETA_JOIN, LEFT_OUTER_JOIN, RIGHT_OUTER_JOIN and OUTER_JOIN are allowed
	 */
	public Orenstein (ResultSet input1, ResultSet input2, Predicate joinPredicate,
		Function newSorter, final Function createTuple, int initialCapacity, double p,
		long seed, float epsilon, int maxLevel, int type){

		this (new ResultSetMetaDataCursor(input1),
			new ResultSetMetaDataCursor(input2),
			joinPredicate, newSorter, createTuple,
			initialCapacity, p, seed, epsilon, maxLevel, type
		);
	}
}
