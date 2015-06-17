/*
 * XXL: The eXtensible and fleXible Library for data processing
 * 
 * Copyright (C) 2000-2004 Prof. Dr. Bernhard Seeger Head of the Database
 * Research Group Department of Mathematics and Computer Science University of
 * Marburg Germany
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 * 
 * http://www.xxl-library.de
 * 
 * bugs, requests for enhancements: request@xxl-library.de
 * 
 * If you want to be informed on new versions of XXL you can subscribe to our
 * mailing-list. Send an email to
 * 
 * xxl-request@lists.uni-marburg.de
 * 
 * without subject and the word "subscribe" in the message body.
 */
package xxl.core.spatial.cursors;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import xxl.core.collections.bags.ArrayBag;
import xxl.core.collections.bags.LIFOBag;
import xxl.core.collections.queues.ListQueue;
import xxl.core.collections.queues.io.RandomAccessFileQueue;
import xxl.core.collections.sweepAreas.BagSAImplementor;
import xxl.core.collections.sweepAreas.SweepArea;
import xxl.core.comparators.ComparableComparator;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.filters.Sampler;
import xxl.core.cursors.joins.SortMergeJoin;
import xxl.core.cursors.sorters.MergeSorter;
import xxl.core.cursors.wrappers.IteratorCursor;
import xxl.core.functions.Function;
import xxl.core.functions.Tuplify;
import xxl.core.io.IOCounter;
import xxl.core.predicates.FeaturePredicate;
import xxl.core.predicates.Predicate;
import xxl.core.spatial.KPEzCode;
import xxl.core.spatial.points.FloatPoint;
import xxl.core.spatial.predicates.DistanceWithinMaximum;
import xxl.core.spatial.predicates.OverlapsPredicate;
import xxl.core.util.BitSet;
import xxl.core.util.WrappingRuntimeException;
/**
 * The spatial join algorithm based on space-filling curves proposed by Jack
 * Orenstein. See: [Ore 91] Jack A. Orenstein: An Algorithm for Computing the
 * Overlay of k-Dimensional Spaces. SSD 1991: 381-400 for a detailed
 * explanation. See: [DS 01]: Jens-Peter Dittrich, Bernhard Seeger: GESS: a
 * Scalable Similarity-Join Algorithm for Mining Large Data Sets in High
 * Dimensional Spaces. ACM SIGKDD-2001. for a review on Orensteins algorithm.
 * <br>
 * <br>
 * Orensteins algorithm is based on a binary recursive partitioning, where the
 * binary code represents the so-called Z-ordering (z-codes). <br>
 * <br>
 * Orensteins algorithm (ORE) assigns each hypercube of the input relations to
 * disjoint subspaces of the recursive partitioning whose union entirely covers
 * the hypercube. ORE sorts the two sets of hypercubes derived from the input
 * relations (including the possible replicates) w.r.t. the lexicographical
 * ordering of its binary code. After that, the relations are merged using two
 * main-memory stacks Stack_R and Stack_S. It is guaranteed that for two
 * adjacent hypercubes in the stack, the prefix property is satisfied for their
 * associated codes. Only those hypercubes are joined that have the same prefix
 * code. <br>
 * <br>
 * A deficiency of ORE is that the different assignment strategies examined in
 * [Ore91] cause substantial replication rates. This results in an increase of
 * the problem space and hence, sorting will be very expensive. Furthermore, ORE
 * has not addressed the problem of eliminating duplicates in the result set.
 * <br>
 * <br>
 * Note that the method <code>reorganize(final Object
 *	currentStatus)</code>
 * could actually be implemented with only 1 LOC. For efficiency reasons we use
 * a somewhat longer version of the method here. <br>
 * <br>
 * Use-case: <br>
 * The main-method of this class contains the complete code to compute a
 * similarity join of two sets of points using Orensteins algorithm.
 * 
 * @see xxl.core.cursors.joins.SortMergeJoin
 * @see xxl.core.spatial.cursors.Mappers
 * @see xxl.core.spatial.cursors.GESS
 * @see xxl.core.spatial.cursors.Replicator
 *  
 */
public class Orenstein extends SortMergeJoin {
	/**
	 * The sweep area used by the Orenstein algorithm.
	 */
	public static class OrensteinSA extends SweepArea {
		/**
		 * internal cursor to the bag (for reasons of efficiency)
		 */
		protected LIFOBag bag;
		/**
		 * Creates a new Orenstein SweepArea
		 * 
		 * @param ID
		 *            ID of the SweepArea
		 * @param lifoBag
		 *            the lifobag for organizing the SweepArea
		 * @param joinPredicate
		 *            the predicate of the join
		 */
		public OrensteinSA(int ID, LIFOBag lifoBag, Predicate joinPredicate) {
			super(new BagSAImplementor(lifoBag), ID, false, joinPredicate, 2);
			this.bag = lifoBag;
		}
		/**
		 * Creates a new OrensteinSweepArea. Uses an ArrayBag to store elements.
		 * 
		 * @param ID
		 *            ID of the SweepArea
		 * @param initialCapacity
		 *            the initial capacity of the ArrayBag which is used for
		 *            organizing the SweepArea
		 * @param joinPredicate
		 *            the predicate of the join
		 */
		public OrensteinSA(int ID, int initialCapacity, Predicate joinPredicate) {
			this(ID, new ArrayBag(initialCapacity), joinPredicate);
		}
		
		/**
		 * In contrast to the method {@link #expire(Object, int)}, this method removes
		 * all expired elements from a SweepArea without returning them. 
		 * The default implementation removes all elements returned by a call to 
		 * {@link #expire(Object, int)}.<BR>
		 * In order to perform a more efficient removal, this method should
		 * be overwritten, e.g., by implementing a bulk deletion. 
		 * 
		 * @param currentStatus The object containing the necessary information
		 * 		  to perform the reorganization step.
		 * @param ID An ID determining from which input this reorganization step
		 * 		   is triggered.
		 * @throws UnsupportedOperationException An UnsupportedOperationException is thrown, if
		 * 		   is method is not supported by this SweepArea.
		 * @throws IllegalStateException Throws an IllegalStateException if
		 * 		   this method is called at an invalid state.
		 */
		// fourth version of this method: low level implementation of delete
		// (fastest version)
		public void reorganize(final Object currentStatus, int ID)
				throws IllegalStateException { //check nesting-condition
			for (Cursor cursor = bag.lifoCursor(); cursor.hasNext();) {
				BitSet top = ((KPEzCode) cursor.next()).getzCode();
				BitSet query = ((KPEzCode) currentStatus).getzCode();
				if ((query.precision() < top.precision())
						|| (query.compare(top) != 0))
					cursor.remove();
				else
					break;
			}
		}
		/**
		 * This method counts the number of comparisons required for processing
		 * the query.
		 * 
		 * @see xxl.core.collections.sweepAreas.SweepArea#query(java.lang.Object,
		 *      int)
		 * @param o
		 *            The query object. This object is typically probed against
		 *            the elements contained in this SweepArea.
		 * @param ID
		 *            An ID determining from which input this method is called.
		 * @return All matching elements of this SweepArea are returned as an
		 *         iterator.
		 */
		public Iterator query(Object o, int ID) {
			comparisons.counter += impl.size();
			return super.query(o, ID);
		}
		
		/**
		 * Inserts the given element into this SweepArea. The default implementation
		 * simply forwards this call to the underlying implementor. Thus,
		 * it calls <code>impl.insert(o)</code>.
		 * 
		 * @param object The object to be inserted.
		 * @throws IllegalArgumentException Throws an IllegalArgumentException
		 * 		if something goes wrong with the insertion due to the passed argument.
		 */
		public void insert(Object object) {
			super.insert(object);
			MAX_SWEEPAREA_SIZE = Math.max(MAX_SWEEPAREA_SIZE, size()); 
			//determine maximum size of the sweep area
		}
	}
	
	/**
	 * A class for counting
	 */
	public static class Counter {
		/**
		 * Internal counter
		 */
		public long counter = 0;
	}
	/**
	 * Counter for coomparison operations
	 */
	public static final Counter comparisons = new Counter();
	/**
	 * Maximum size of the sweep area (number of elements)
	 */
	public static int MAX_SWEEPAREA_SIZE = 0;
	/**
	 * Constructs an object of the class Orenstain:
	 * 
	 * @param input0
	 *            the first input cursor
	 * @param input1
	 *            the second input cursor
	 * @param joinPredicate
	 *            the join predicate
	 * @param newSorter
	 *            provides a function that returns sorted inputs
	 * @param newResult
	 *            is a function for creating the final result object
	 * @param initialCapacity
	 *            the initial capacity of the ArrayBag that is used for
	 *            organiting the SweepAreas
	 */
	public Orenstein(Cursor input0, Cursor input1, Predicate joinPredicate,
			Function newSorter, Function newResult, final int initialCapacity) {
		super(input0, input1, newSorter, newSorter, new OrensteinSA(0,
				initialCapacity, joinPredicate), new OrensteinSA(1,
				initialCapacity, joinPredicate),
				ComparableComparator.DEFAULT_INSTANCE, newResult);
	}
	/*
	 * top-level constructor for a self-join
	 */
	/*
	 * public Orenstein(Cursor input, Predicate joinPredicate, Function
	 * newSorter, Function newResult, final int initialCapacity, final int
	 * type){ super(input, newSorter, joinPredicate, new
	 * OrensteinSweepArea(initialCapacity), newResult, type ); }
	 */
	/**
	 * Constructs an object of the class Orenstain that wraps input iterators as
	 * cursors.
	 * 
	 * @param input0
	 *            the first input iterator
	 * @param input1
	 *            the second input iterator
	 * @param joinPredicate
	 *            the join predicate
	 * @param newSorter
	 *            provides a function that returns sorted inputs
	 * @param newResult
	 *            is a function for creating the final result object
	 * @param initialCapacity
	 *            the initial capacity of the ArrayBag that is used for
	 *            organiting the SweepAreas
	 */
	public Orenstein(Iterator input0, Iterator input1, Predicate joinPredicate,
			Function newSorter, Function newResult, final int initialCapacity) {
		this(new IteratorCursor(input0), new IteratorCursor(input1),
				joinPredicate, newSorter, newResult, initialCapacity);
	}
	/**
	 * Constructs an object of the class Orenstain that wraps input iterators as
	 * cursors. The join predicate test for overlaps. The method does not apply
	 * a function for creating the final object.
	 * 
	 * @param input0
	 *            the first input iterator
	 * @param input1
	 *            the second input iterator
	 * @param newSorter
	 *            provides a function that returns sorted inputs
	 * @param initialCapacity
	 *            the initial capacity of the ArrayBag that is used for
	 *            organiting the SweepAreas
	 */
	public Orenstein(Iterator input0, Iterator input1, Function newSorter,
			final int initialCapacity) {
		this(input0, input1, OverlapsPredicate.DEFAULT_INSTANCE, newSorter,
				Tuplify.DEFAULT_INSTANCE, initialCapacity);
	}
	/**
	 * constructor for a self-join
	 */
	/*
	 * public Orenstein(Iterator input, Predicate joinPredicate, Function
	 * newSorter, Function newResult, final int initialCapacity){ this(new
	 * BufferedCursor(input), joinPredicate, newSorter, newResult,
	 * initialCapacity, SortMergeJoin.THETA_JOIN); }
	 */
	///use-case://///////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * use-case: similarity-join for point data.
	 * 
	 * @param args
	 *            args[0] file-name 0, args[1] file-name 1, args[2]
	 *            dimensionality of the data, args[3] main-memory available (in
	 *            bytes), args[4] epsilon-distance, args[5] maximum level of the
	 *            partitioning, args[6] fraction of elements to be used from the
	 *            input, args[7] external computation (true | default=false)
	 */
	public static void main(String[] args) {
		
	    if (args.length == 0) {
	        args = new String[8];
	        String dataPath = xxl.core.util.XXLSystem.getDataPath(new String[] {"geo"}); 
		    args[0] = dataPath+File.separator+"rr_small.bin";
		    args[1] = dataPath+File.separator+"st_small.bin";
		    args[2] = "2";
		    args[3] = "256000";
		    args[4] = "0.2";
		    args[5] = "16";
		    args[6] = "0.1";
		    args[7] = "false";
	    }
	      	    
		if (args.length < 7 || args.length > 9) {
			System.out.println("usage: java xxl.core.spatial.cursors.Orenstein <file-name0> <file-name1> <dim> <main memory> <epsilon-distance> <maximum level of the partitioning> <fraction of elements to be used from the input> <external computation=false>");
			return;
		}
		boolean selfJoin = false;
		final String input0 = args[0];
		final String input1 = args[1];
		if (input0.equals(input1)) {
			System.out.print("APPLYING_SELF_JOIN_OPTIMIZATION\t");
			selfJoin = true;
		}
		final int dim = Integer.parseInt(args[2]);
		final int mem = Integer.parseInt(args[3]);
		final float epsilon = Float.parseFloat(args[4]);
		final int maxLevel = Integer.parseInt(args[5]);
		final double p = Double.parseDouble(args[6]); //fraction of
													  // elements to be used
													  // from the input data
		final boolean external = args.length == 7 ? false : (args[7]
				.equals("true"));
		final String path = args.length < 9 ? null : args[8];
		if (external)
			System.out.print("EXTERNAL_ALG\t" + path + "\t");
		final int objectSize;
		
		try {
			objectSize = xxl.core.util.XXLSystem.getObjectSize(new KPEzCode(new FloatPoint(dim), new BitSet(32)));
		}
		catch (Exception e) {
			throw new WrappingRuntimeException(e);
		}
		
		final int initialCapacity = 30000;
		final int seed = 42; //note: same seed as in NestedLoopsJoin
							 // use-case!!
		Iterator r = Mappers.getFloatPointKPEzCodeMapper(new Sampler(
				new PointInputCursor(new File(input0),
						PointInputCursor.FLOAT_POINT, dim, 1024 * 1024), p,
				seed), epsilon, maxLevel);
		Iterator s = null;
		if (!selfJoin) {
			s = Mappers.getFloatPointKPEzCodeMapper(
					new Sampler(
							new PointInputCursor(new File(input1),
									PointInputCursor.FLOAT_POINT, dim,
									1024 * 1024), p, seed), epsilon,
					maxLevel);
		}
		final IOCounter counter = new IOCounter();
		final Function newQueue = new Function() {
			protected int no = 0;
			public Object invoke(Object inputBufferSize,
					Object outputBufferSize) {
				if (external) {
					File file = null;
					try {
						file = File.createTempFile("RAF", ".queue",
								new File(path));
					} catch (IOException ioe) {
						System.out.println(ioe);
					}
					return new RandomAccessFileQueue(file, new Function() {
						public Object invoke() {
							return new KPEzCode(new FloatPoint(dim));
						}
					}, (Function) inputBufferSize,
							(Function) outputBufferSize) {
						public void enqueueObject(Object object) {
							counter.incWrite();
							super.enqueueObject(object);
						}
						public Object dequeueObject() {
							counter.incRead();
							return super.dequeueObject();
						}
					};
				} else
					return new ListQueue();
			}
		};
		Function newSorter = new Function() {
			public Object invoke(Object object) {
				return new MergeSorter((Iterator) object, objectSize, mem,
						(int) (mem * 0.4), newQueue, false);
			}
		};
		Predicate joinPredicate = new FeaturePredicate(
				new DistanceWithinMaximum(epsilon), new Function() {
					public Object invoke(Object object) {
						return ((KPEzCode) object).getData();
					}
				});
		long start = System.currentTimeMillis();
		Orenstein orenstein = null;
		//		if(selfJoin) //note: self-join excludes trivial results, i.e.
		// tuples like (ID42, ID42)
		//			orenstein = new Orenstein(r, joinPredicate, newSorter,
		// Tuplify.DEFAULT_INSTANCE, initialCapacity);
		//		else
		orenstein = new Orenstein(r, s, joinPredicate, newSorter,
				Tuplify.DEFAULT_INSTANCE, initialCapacity);
		int res = 0;
		while (orenstein.hasNext()) {
			res++;
			/* Object[] tuple= (Object[]) */orenstein.next();
			//			System.out.print( ((KPE)tuple[0]).getID() +"\t"+
			// ((KPE)tuple[1]).getID() +"\t");
			//			System.out.println(Points.maxDistance( (Point)
			// ((KPEzCode)tuple[0]).getData(), (Point)
			// ((KPEzCode)tuple[1]).getData() ) );
		}
		System.out.print(xxl.core.util.Strings.toString(args) + "\t");
		System.out.print("RES:\t" + res + "\t");
		System.out.print("runtime(sec):\t"
				+ (System.currentTimeMillis() - start) / 1000.0 + "\t");
		System.out.print("element-comparisons:\t" + comparisons.counter
				+ "\t");
		if (external)
			System.out.println("IOs(object-count)\tRead:\t"
					+ counter.getReadIO() + "\tWrite:\t"
					+ counter.getWriteIO());
		else
			System.out.println();
	}
}
