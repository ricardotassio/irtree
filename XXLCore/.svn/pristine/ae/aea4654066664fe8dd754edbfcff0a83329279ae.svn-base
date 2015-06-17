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
import xxl.core.collections.queues.Queue;
import xxl.core.collections.queues.Queues;
import xxl.core.collections.queues.io.RandomAccessFileQueue;
import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.filters.Sampler;
import xxl.core.cursors.sorters.MergeSorter;
import xxl.core.cursors.unions.Merger;
import xxl.core.cursors.wrappers.IteratorCursor;
import xxl.core.cursors.wrappers.QueueCursor;
import xxl.core.functions.Constant;
import xxl.core.functions.Function;
import xxl.core.functions.Tuplify;
import xxl.core.io.IOCounter;
import xxl.core.predicates.FeaturePredicate;
import xxl.core.predicates.Predicate;
import xxl.core.spatial.KPEzCode;
import xxl.core.spatial.points.FloatPoint;
import xxl.core.spatial.predicates.DistanceWithinMaximum;
import xxl.core.util.BitSet;
import xxl.core.util.WrappingRuntimeException;

/**
 *	This class provides the <bold>MSJ</bold> (Multidimensional Spatial Join) similarity-join algorithm
 *	proposed by Koudas and Sevick in "[KS 98] Nick Koudas, Kenneth C.
 *	Sevcik: High Dimensional Similarity Joins: Algorithms and Performance
 *	Evaluation. ICDE 1998: 466-475" which is the multi-dimensional
 *	extension of <bold>S3J</bold> (Size Separation Spatial Join) proposed by the same
 *	authors in "Nick Koudas, Kenneth C. Sevcik: Size Separation Spatial Join.
 *	SIGMOD Conference 1997: 324-335". (This implementation corresponds to 
 *	the S3J-algorithm if you set the dimensionality to 2).
 *	<br><br>
 *	MSJ/S3J performs similar to Orenstein's algorithm with two main differences: First,
 *	replication is not allowed and second, an I/O strategy based on
 *	so-called <it>level-files</it> is employed. Moreover, an n-ary recursive
 *	partitioning is used where n = 2^d (quadtree-partitioning).
 *
 *	<br><br>
 *	The algorithm starts by partitioning the hypercubes of the input
 *	relations into level-files according to their levels. Hence, a 
 *	hypercube of level l is kept in the l-th level-file. Then, the level-files are sorted w.r.t.
 *	the code of the hypercubes. Finally, the Merge algorithm of
 *	Orenstein is called.
 *
 *	<br><br>
 *	Deficiencies of this method for high-dimensional intersection
 *	joins are that a high fraction of the input relation will be in 
 *	level 0. The hypercubes in level 0, however, need to  
 *	be tested against the entire input relation in a nested-loop
 *	manner. Moreover, [Dittrich and Seeger, ICDE 2000]  showed for two dimensions that a modest rate of
 *	replication considerably speeds up the overall execution time of MSJ.
 *
 *	<br><br>
 *	(See "[DS 01] GESS: a Scalable Algorithm for Mining Large Datasets in High Dimensional Spaces"
 *        by Jens-Peter Dittrich and Bernhard Seeger, ACM SIGKDD 2001. pages 47-56. for a
 *	review of MSJ).
 *	<br><br>
 *
 *	@see xxl.core.spatial.cursors.Orenstein
 *	@see xxl.core.spatial.cursors.GESS
 *  @see xxl.core.spatial.cursors.Mappers
 */
public class MSJ extends Orenstein {
	
	/** This class provides the I/O-strategy of MSJ
	 */
	public static class MSJSorter extends AbstractCursor {

		/**
		 * The input iteration holding the data to be sorted.
		 */
		private Cursor cursor;
		
		/**
		 * Sorts the input data for the multidimensional spatial join.
		 * @param input the input iterator
		 * @param maxLevel the maximum level of the grid
		 * @param dim the dimension of the objects
		 * @param newQueue a functional factory for generating queues
		 * @param mem the available size in main memory
		 */
		public MSJSorter(Iterator input, int maxLevel, final int dim, Function newQueue, final int mem){
			Queue[] queues = new Queue[maxLevel+1];
			int objectSize = 0;
			try{
				objectSize = xxl.core.util.XXLSystem.getObjectSize(new KPEzCode(new FloatPoint(dim), new BitSet(32)));
			}	
			catch (Exception e){System.out.println(e);}

			Function inputBufferSize =  new Constant((int)(0.2*mem));		//determine buffer-size for reading data from disk
			Function outputBufferSize = new Constant(mem/queues.length);		//determine buffer-size for writing data to disk

			for(int i=0; i<=maxLevel; i++)						//initialize output-queues
				(queues[i] = (Queue) newQueue.invoke( inputBufferSize, outputBufferSize )).open();

			while(input.hasNext()){							//write data into "level-files" (see paper of Koudas and Sevcik!)
				KPEzCode next = (KPEzCode) input.next();
				int level = Math.min(next.getzCode().precision()/dim,maxLevel);	//determine queue
				queues[ level ].enqueue(next);					//insert object into queue
			}

			Iterator[] iterators = new Iterator[maxLevel+1];

			for(int i=0; i<=maxLevel; i++){						//for each level-file: sort w.r.t. space-filling curve
				Queue tmp  = (Queue) newQueue.invoke( inputBufferSize, outputBufferSize );	//get new Queue for this level-file
				tmp.open();
				//sort level-file and materialize result into queue
				Queues.enqueueAll(
					tmp, 
					new MergeSorter(
						new QueueCursor(queues[i]),
						objectSize,
						(int)(mem*0.8),
						mem/(maxLevel+1),
						newQueue,
						false
					)
				);
				iterators[i] = new QueueCursor(tmp);
			}

			cursor = new Merger(iterators);						//merge sorted streams
		}
		/**
		 * @return true if there is another object available
		 */
		public boolean hasNextObject() {
			return cursor.hasNext();
		}
		
		/**
		 * @return the next object
		 */
		public Object nextObject() {
			return cursor.next();
		}
	}

	
	 
	/**
	 * The top-level constructor for MSJ
	 * @param input0 ths first input
	 * @param input1 the second input
	 * @param predicate the join predicate
	 * @param newResult a function for mapping the output to an object
	 * @param initialCapacity the initial capacity of a bucket
	 * @param maxLevel the maximum level of the grid
	 * @param dim the dimension of the objects
	 * @param newQueue a functional factory for creating queues
	 * @param mem the available main memory
	 */
	public MSJ(Cursor input0, Cursor input1, Predicate predicate, Function newResult, final int initialCapacity, final int maxLevel, final int dim, final Function newQueue, final int mem){
		super(
			input0,
			input1,
			predicate,
			new Function(){
        public Object invoke(Object input){
		        return new MSJSorter((Iterator)input, maxLevel, dim, newQueue, mem);
        }
      },
			newResult,
			initialCapacity
		);
	}

	/** top-level constructor for a self-join
	*/
/*	
	public MSJ(Cursor input, Predicate predicate, Function newResult, final int initialCapacity, final int type, final int maxLevel, final int dim, final Function newQueue, final int mem){
		super( input, predicate,
			new Function(){
                public Object invoke(Object input){
                        return new MSJSorter((Iterator)input, maxLevel, dim, newQueue, mem);
                }
            },
			newResult, initialCapacity, type
		);
	}
*/

	/**
	 * The top-level constructor for MSJ
	 * @param input0 ths first input
	 * @param input1 the second input
	 * @param predicate the join predicate
	 * @param newResult a function for mapping the output to an object
	 * @param initialCapacity 
	 * @param maxLevel the maximum level of the grid
	 * @param dim the dimension of the objects
	 * @param newQueue a functional factory for creating queues
	 * @param mem the available main memory
	 */
	public MSJ(Iterator input0, Iterator input1, Predicate predicate, Function newResult, final int initialCapacity, final int maxLevel, final int dim, final Function newQueue, final int mem){
		this( new IteratorCursor(input0), new IteratorCursor(input1), predicate, newResult, initialCapacity, maxLevel, dim, newQueue, mem);
	}

	/** constructor for a self-join
	*/
/*
	public MSJ(Iterator input, Predicate predicate, Function newResult, final int initialCapacity, final int maxLevel, final int dim, final Function newQueue, final int mem){
		this( new BufferedCursor(input), predicate, newResult, initialCapacity, THETA_JOIN, maxLevel, dim, newQueue, mem);
	}
*/
	///use-case://///////////////////////////////////////////////////////////////////////////////////////////////////////

	/** use-case: similarity-join for point data.
	 * @param args the input arguments of the main method
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
			System.out.println("usage: java xxl.core.spatial.cursors.MSJ <file-name0> <file-name1> <dim> <main memory> <epsilon-distance> <maximum level of the partitioning> <fraction of elements to be used from the input> <external computation=false>");
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
		try {
			xxl.core.util.XXLSystem.getObjectSize(new KPEzCode(new FloatPoint(dim), new BitSet(32)));
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
							return new KPEzCode(new FloatPoint(dim),
									new BitSet());
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
		Predicate joinPredicate = new FeaturePredicate(
				new DistanceWithinMaximum(epsilon), new Function() {
					public Object invoke(Object object) {
						return ((KPEzCode) object).getData();
					}
				});
		long start = System.currentTimeMillis();
		MSJ msj = null;
		/*
		 * if(selfJoin) //note: self-join excludes trivial results, i.e.
		 * tuples like (ID42, ID42) msj = new MSJ(r, joinPredicate,
		 * Tuplify.DEFAULT_INSTANCE, initialCapacity, maxLevel, dim,
		 * newQueue, mem); else
		 */
		msj = new MSJ(r, s, joinPredicate, Tuplify.DEFAULT_INSTANCE,
				initialCapacity, maxLevel, dim, newQueue, mem);
		int res = 0;
		while (msj.hasNext()) {
			res++;
			/* Object[] tuple = (Object[]) */ msj.next();
			//System.out.print( ((KPE)tuple[0]).getID() +"\t"+
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

