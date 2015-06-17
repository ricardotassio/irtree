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

package xxl.core.spatial.cursors;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import xxl.core.collections.queues.ListQueue;
import xxl.core.collections.queues.io.RandomAccessFileQueue;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.filters.Sampler;
import xxl.core.cursors.sorters.MergeSorter;
import xxl.core.functions.Function;
import xxl.core.functions.Tuplify;
import xxl.core.io.IOCounter;
import xxl.core.predicates.And;
import xxl.core.predicates.FeaturePredicate;
import xxl.core.predicates.Predicate;
import xxl.core.spatial.KPEzCode;
import xxl.core.spatial.SpaceFillingCurves;
import xxl.core.spatial.points.FloatPoint;
import xxl.core.spatial.points.Point;
import xxl.core.spatial.predicates.DistanceWithinMaximum;
import xxl.core.util.BitSet;
import xxl.core.util.WrappingRuntimeException;

/**
 *	This class provides the similarity-join algorithm "GESS: Generic External Space Sweep"
 *	(see "[DS 01] GESS: a Scalable Algorithm for Mining Large Datasets in High Dimensional Spaces
 *	by Jens-Peter Dittrich and Bernhard Seeger, ACM SIGKDD 2001." for a
 *	detailed description of this method).
 *	<br><br>
 *	The most important component of this algorithm is the Replicator-Engine
 *	({@link xxl.core.spatial.cursors.Replicator Replicator})
 *	which determines the partition(s) for incoming points and maps
 *	Points to KPEzCodes.
 *	<br><br>
 *	The use-case implemented in the main-method of this class reads 1 or
 *	2 inputs and computes the similarity-join using GESS. The use-case
 *	provided with this class reads files containing FloatPoints.
 *	<br><br>
 *	Note that GESS works on arbitrary data as long as the user provides
 *	a mapping to the internally used FixedPointRectangle-type (see
 *	parameter "inputMapping").
 *
 *	@see xxl.core.spatial.cursors.Replicator
 *	@see xxl.core.spatial.cursors.Orenstein
 *	@see xxl.core.spatial.cursors.MSJ
 *	@see xxl.core.spatial.points.FixedPoint
 *	@see xxl.core.spatial.rectangles.FixedPointRectangle
 *	@see xxl.core.spatial.KPEzCode
 *	@see xxl.core.cursors.joins.SortMergeJoin
 *
 */
public class GESS extends Orenstein{

	/** 
	 *  This class provides the Reference Point Method of GESS.
	 *	Since GESS allows hypercubes to get replicated, we have to provide
	 *	a method to eliminate possible duplicates from the result set.
	 *   <br><br>
	 *	There are two principal approaches for eliminating duplicate
	 *	results. The first is to use a hash-table that stores the entire
	 *	set of result tuples. The memory requirements of this approach
	 *	however are O(n). A second approach is to apply external sorting
	 *	to the result set. This causes additional I/O cost. In
	 *	addition, the sorting operation could not report any result until
	 *	all results had been reported by the merging algorithm.
	 *   <br><br>
	 *	Instead of using these standard techniques
	 *	we propose an inexpensive on-line method termed
	 *	Reference Point Method (RPM). This method neither allocates
	 *	additional memory nor does it cause any additional I/O operations.
	 *   <br><br>
	 *	The basic idea of RPM is to define a reference point
	 *	which is contained in the section of two hypercubes.
	 *   <br><br>
	 *	See [DS 01] GESS: a Scalable Algorithm for Mining Large Datasets in High Dimensional Spaces
	 *	by Jens-Peter Dittrich and Bernhard Seeger, ACM SIGKDD 2001. San Francisco. pages: 47-56" for a
	 *	detailed description of this algorithm.
	 *   <br><br>
	 *	Usage:
	 *	The main-method of this class contains an elaborate similarity-join use-case.
	 *   <br><br>
	 *	To make GESS work using RPM simply modify the joinPredicate using the And-predicate:
	 *	<code><pre>
	 *	Predicate joinPredicate =
	 *		new And(
	 *			new FeaturePredicate(
	 *				new DistanceWithinMaximum(epsilon),
	 *				new Function(){
	 *					public Object invoke(Object object){
	 *						return ((KPEzCode)object).getData();
	 *					}
	 *				}
	 *			),
	 *			new GESS.ReferencePointMethod(epsilonDiv2)	//duplicate removal (modify GESS to work with reference point method)
	 *		);
	 *
	 *
	 *	@see xxl.core.spatial.cursors.Replicator
	 *	@see xxl.core.predicates.Predicate
	 *	@see xxl.core.predicates.And
	 * 	@see xxl.core.spatial.predicates.DistanceWithinMaximum
	 *	@see xxl.core.cursors.joins.SortMergeJoin
	 */
	public static class ReferencePointMethod extends Predicate {

		/** The epsilon (query) distance of the Similarity Join divided by 2.
		 */
		protected double epsilonDiv2;

		/** Constructs a new ReferencePointMethod instance.
		 *
		 *  @param epsilonDiv2 the epsilon distance of the Similarity Join divided by 2
		 */
		public ReferencePointMethod(double epsilonDiv2){
			this.epsilonDiv2 = epsilonDiv2;
		}

		/** Takes a tuple containing two KPEzCodes as its input and
		 *  checks whether a certain reference point is contained
		 *  in the partition currently processed.
		 *
		 *  Contains an optimization that applies RPM only in those cases
		 *  when at least one of the inputs is a replicate.
		 *  @param object is a two-dimensional array whith the two KPEzCodes
		 * @return return true if a certain reference point is contained in the 
		 * cell of the two points given by the KPEzCodes (see Paper)
		 */
		public boolean invoke(Object[] object){
			KPEzCode k0 = (KPEzCode)object[0];
			KPEzCode k1 = (KPEzCode)object[1];

			if( k0.getIsReplicate() || k1.getIsReplicate()){		//optimization: apply RPM only in case one of the inputs is a replicate
				final BitSet currentZCode = k0.getzCode();
				float[] p1 = (float[]) ((Point)k0.getData()).getPoint();
				float[] p2 = (float[]) ((Point)k1.getData()).getPoint();
				long[] rp = new long[p1.length];
				for(int i=0; i<p1.length; i++){
					rp[i] = xxl.core.math.Maths.doubleToNormalizedLongBits( Math.max( p1[i], p2[i] ) - epsilonDiv2 );
				}
				return SpaceFillingCurves.zCode2( rp, currentZCode.precision() ).compare(currentZCode) == 0;
			}
			else
				return true;
		}
	}

	/** Creates a new GESS-operator (Constructor for two inputs).
	 *
	 *	@param input0 first (unsorted) input
	 *	@param input1 second (unsorted) input
	 *	@param inputMapping a Function used to map incoming objects of arbitrary type to a FixedPointRectangle (internally used by the replication engine)
	 *	@param joinPredicate the join predicate to be used by this join	(e.g. DistanceWithin-predicate)
	 *	@param splitAllowed the replication strategy to be applied by GESS (see inner class Replicator.Split)
	 *	@param minBitIndex the minimal bit-index to be considered for the replication-process ( 0 <= bitIndex <= 62 )
	 *	@param newSorter a factory-Function that returns a sorting-operator (e.g. {@link xxl.core.cursors.sorters.MergeSorter})
	 *	@param newResult a factory-Function that is used to create the result-tuples that are returned by this operator (e.g. {@link xxl.core.functions.Tuplify})
	 *	@param dimensions the dimensionality of the data
	 *	@param initialCapacity the maximum number of elements that can be stored inside main-memory (i.e. by the SweepArea)
	 */
	public GESS(Iterator input0, Iterator input1, Function inputMapping, Predicate joinPredicate, Predicate splitAllowed, int minBitIndex, Function newSorter, Function newResult, int dimensions, int initialCapacity){
		super(
				new Replicator(inputMapping, input0, dimensions, splitAllowed, minBitIndex),
				new Replicator(inputMapping, input1, dimensions, splitAllowed, minBitIndex),
				joinPredicate,
				newSorter,
				newResult,
				initialCapacity
		);
	}

	/** Creates a new GESS-operator (Constructor for a self-join).
	 *
	 *	@param input (unsorted) input
	 *	@param inputMapping a Function used to map incoming objects of arbitrary type to a FixedPointRectangle (internally used by the replication engine)
	 *	@param joinPredicate the join predicate to be used by this join	(e.g. DistanceWithin-predicate)
	 *	@param splitAllowed the replication strategy to be applied by GESS (see inner class Replicator.Split)
	 *	@param minBitIndex the minimal bit-index to be considered for the replication-process ( 0 <= bitIndex <= 62 )
	 *	@param newSorter a factory-Function that returns a sorting-operator (e.g. {@link xxl.core.cursors.sorters.MergeSorter})
	 *	@param newResult a factory-Function that is used to create the result-tuples that are returned by this operator (e.g. {@link xxl.core.functions.Tuplify})
	 *	@param dimensions the dimensionality of the data
	 *	@param initialCapacity the maximum number of elements that can be stored inside main-memory (i.e. by the SweepArea)
	 */
/*	
	public GESS(Iterator input, Function inputMapping, Predicate joinPredicate, Predicate splitAllowed, int minBitIndex, Function newSorter, Function newResult, int dimensions, int initialCapacity){
		super(
			new Replicator(inputMapping, input, dimensions, splitAllowed, minBitIndex),
			joinPredicate,
			newSorter,
			newResult,
			initialCapacity
		);
	}
*/

	///use-case://///////////////////////////////////////////////////////////////////////////////////////////////////////

	/** 
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 *  Use-case: similarity-join for point data.
	 *	Please execute java xxl.core.spatial.cursors.GESS to get help on class usage.
	 */
	public static void main(String[] args){
	    
	    if (args.length == 0) {
	        args = new String[9];
	        String dataPath = xxl.core.util.XXLSystem.getDataPath(new String[] {"geo"}); 
		    args[0] = dataPath+File.separator+"rr_small.bin";
		    args[1] = dataPath+File.separator+"st_small.bin";
		    args[2] = "2";
		    args[3] = "256000";
		    args[4] = "0.2";
		    args[5] = "16";
		    args[6] = "8";
		    args[7] = "0.1";
		    args[8] = "false";
	    }
	        
		if(args.length < 8 || args.length > 10){
			System.out.println("java xxl.core.spatial.cursors.GESS <file-name0> <file-name1> <dim> <main memory> <epsilon-distance> <msl> <k> <p> <external computation=false>");
			System.out.println("    <file-name0>           : first input file");
			System.out.println("    <file-name1>           : second input file");
			System.out.println("               (Note: if both file-names are equal a self-join is performed (uses some optimizations)");
			System.out.println();
			System.out.println("    <dim>                  : dimensionality of the data");
			System.out.println("    <main memory>          : available main memory in bytes");
			System.out.println("    <epsilon-distance>     : epsilon query-distance");
			System.out.println("    <msl>                  : maximum split level");
			System.out.println("    <k>                    : maximum splits per level");
			System.out.println("    <p>                    : fraction of elements to be used from the input>");
			System.out.println("    <external>             : if true: perform external memory algorithm");
			System.out.println();
			return;
		}

		boolean selfJoin = false;
		final String input0 = args[0];
		final String input1 = args[1];
		if(input0.equals(input1)){
			System.out.print("APPLYING_SELF_JOIN_OPTIMIZATION\t");
			selfJoin = true;
		}
		final int dim = Integer.parseInt(args[2]);
		final int mem = Integer.parseInt(args[3]);
		final float epsilon = Float.parseFloat(args[4]);
		final float epsilonDiv2 = epsilon/2;
		final int msl = Integer.parseInt(args[5]);
		final int k = Integer.parseInt(args[6]);
		final double p = Double.parseDouble(args[7]);   //fraction of elements to be used from the input data
		final boolean external = args.length == 8 ? false : (args[8].equals("true"));
		final String path = args.length <10  ? null: args[9];
		if(external)
			System.out.print("EXTERNAL_ALG\t"+path+"\t");

		final int objectSize;
		try {
			objectSize = xxl.core.util.XXLSystem.getObjectSize(new KPEzCode(new FloatPoint(dim), new BitSet(32)));
		}
		catch (Exception e) {
			throw new WrappingRuntimeException(e);
		}
		
		final int initialCapacity = 30000;

		final int seed = 42; //note: same seed as in NestedLoopsJoin use-case!!

		Iterator r = new Sampler(new PointInputCursor(new File(input0), PointInputCursor.FLOAT_POINT, dim, 1024*1024), p, seed);


		Iterator s = null;

		if(!selfJoin)
			s = new Sampler(new PointInputCursor(new File(input1), PointInputCursor.FLOAT_POINT, dim, 1024*1024), p, seed);

		final IOCounter counter = new IOCounter();


		final Function newQueue = new Function() {
			protected int no = 0;
			
			public Object invoke(Object inputBufferSize, Object outputBufferSize) {
				if (external) {
					File file = null;
					try {
						file = File.createTempFile("RAF", ".queue", new File(path));
					}
					catch (IOException ioe) {
						System.out.println(ioe);
					}
					return new RandomAccessFileQueue(
						file,
						new Function() {
							public Object invoke() {
								return new KPEzCode(new FloatPoint(dim));
							}
						},
						(Function)inputBufferSize,
						(Function)outputBufferSize
					) {
						public void enqueueObject(Object object) {
							counter.incWrite();
							super.enqueueObject(object);
						}

						public Object dequeueObject(){
							counter.incRead();
							return super.dequeueObject();
						}
					};
				}
				else
					return new ListQueue();
			}
		};

		Function newSorter = new Function(){
		                public Object invoke(Object object){
		                       return new MergeSorter((Iterator)object, objectSize, mem, (int)(mem*0.4),newQueue, false);
		                }
		};

		Predicate joinPredicate =
			new And(
				new FeaturePredicate(
					new DistanceWithinMaximum(epsilon),
					new Function(){
						public Object invoke(Object object){
							return ((KPEzCode)object).getData();
						}
					}
				),
				new GESS.ReferencePointMethod(epsilonDiv2)	//duplicate removal (modify GESS to work with reference point method)
			);

		//default strategy:
//		maxLevel = Math.max(0, (- (int)(Math.log(epsilon)/Math.log(2.0))) -3 );
		Predicate splitAllowed = new And(new Replicator.MaxSplitsPerLevel(k), new Replicator.MaxSplitLevel(msl));

		final int minBitIndex = 63- (64/dim) ;

		long start = System.currentTimeMillis();
		Cursor gess = null;
		Function inputMapping = Mappers.getPointFixedPointRectangleMappingFunction(epsilonDiv2);

/*
			if(selfJoin)	//note: self-join excludes trivial results, i.e. tuples like (ID42, ID42)
				gess = new GESS(r, inputMapping, joinPredicate, splitAllowed, minBitIndex, newSorter, Tuplify.DEFAULT_INSTANCE, dim, initialCapacity);
			else
*/			gess = new GESS(r, s, inputMapping, joinPredicate, splitAllowed, minBitIndex, newSorter, Tuplify.DEFAULT_INSTANCE, dim, initialCapacity);

		int res = 0;
		while(gess.hasNext()){
			res++;
			/* Object[] tuple= (Object[]) */ gess.next();
			//System.out.print( ((KPE)tuple[0]).getID() +"\t"+ ((KPE)tuple[1]).getID() +"\t");
//			System.out.println(Points.maxDistance( (Point) ((KPEzCode)tuple[0]).getData(), (Point) ((KPEzCode)tuple[1]).getData() ) );
		}
		System.out.print( xxl.core.util.Strings.toString(args) +"\t");
		System.out.print("RES:\t"+res+"\t");
		System.out.print("runtime(sec):\t"+(System.currentTimeMillis()-start)/1000.0+"\t");
		System.out.print("element-comparisons:\t"+comparisons.counter+"\t");
		if(external)
			System.out.println("IOs(object-count)\tRead:\t"+counter.getReadIO()+"\tWrite:\t"+counter.getWriteIO());
		else
			System.out.println();
	}
}
