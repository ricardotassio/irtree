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

import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.relational.Tuples;

/**
 * 	Merge sort implementation of a sort operator.
 *
 *	This class uses the algorithm of the {@link xxl.core.cursors.sorters.MergeSorter}
 *	and additionally forwards the metadata. A detailed
 *	description of the algorithm is contained in {@link xxl.core.cursors.sorters.MergeSorter}.
 *	<p>
 *	In earlier versions of XXL it was possible to hand over a string array
 *	instead of an array of indices. To get this functionality, use
 *	{@link xxl.core.relational.ResultSets}.getColumnIndices(resultSet,onColumns).
 *	</p>
 */
public class MergeSorter extends xxl.core.cursors.sorters.MergeSorter implements MetaDataCursor {

	/** Metadata */
	protected ResultSetMetaData metaData;

	/**
	 * Creates a new MergeSorter.
	 *
	 * @param cursor the input MetaDataCursor to be sorted.
	 * @param comparator the Comparator used to compare the elements in the heap (Replacement Selection).
	 * @param blockSize the size of a block (page).
	 * @param objectSize the size of an object in main memory.
	 * @param memSize the memory available to the MergeSorter during the open-phase.
	 * @param firstOutputBufferRatio the ratio of memory available to the output buffer during run-creation
	 * 		(0.0: use only one page for the output buffer and what remains is used for the heap;
	 * 		 1.0: use as much memory as possible for the output buffer).
	 * @param outputBufferRatio the amount of memory available to the output buffer during intermediate merges (not the final merge)
	 * 		(0.0: use only one page for the output buffer, what remains is used for the merger and the input buffer,
	 * 		inputBufferRatio determines how the remaining memory is distributed between them;
	 * 		 1.0: use as much memory as possible for the output buffer).
	 * @param inputBufferRatio the amount of memory available to the input buffer during intermediate merges (not the final merge)
	 * 		(0.0: use only one page for the input buffer, what remains is used for the Merger (maximal FanIn);
	 * 		 1.0: use as much memory as possible for the input buffer).
	 * @param finalMemSize the memory available to the MergeSorter during the next-phase.
	 * @param finalInputBufferRatio the amount of memory available to the input buffer of the final (online) merge
	 * 		(0.0: use the maximum number of inputs (maximal fanIn), i.e. perform the online merge as early as possible;
	 * 		 1.0: write the entire data into a final queue, the online "merger" just reads the data from
	 * 		 this queue).
	 * @param newQueue the function <code>newQueue</code> should return a queue, which is used by the algorithm
	 * 		to materialize the internal runs, i.e. this function determines whether the sort operator
	 * 		works on queues based on external storage or in main memory (useful for testing and counting).
	 *		The function takes two without parameters functions <tt>getInputBufferSize()</tt> and <tt>getOutputBufferSize()</tt> as
	 *		parameters.
	 * @param newQueuesQueue if this function is invoked, the queue, that should contain
	 * 		the queues to be merged, is returned. The function takes an iterator and the comparator
	 *		<tt>queuesQueueComparator</tt> as parameters. <br>
	 *		E.g. <code><pre>
	 * 			new Function(){
	 *				public Object invoke(Object iterator, Object comparator){
	 *					return new DynamicHeap((Iterator)iterator, (Comparator)comparator);
	 *				}
	 * 		</code></pre>
	 * 		The queues contained in the iterator are inserted in the DynamicHeap using the given
	 * 		Comparator for comparison.
	 * @param queuesQueueComparator this Comparator determines the next queue used for merging.
	 * @param verbose if the <code>verbose</code> flag set to <code>true</code>
	 * 		the MergeSorter displays how the memory was distributed internally. In addition,
	 * 		the number of merges is displayed.
	 */
	public MergeSorter (MetaDataCursor cursor, final Comparator comparator,
		final int blockSize, final int objectSize,
		final int memSize, double firstOutputBufferRatio, double outputBufferRatio,
		double inputBufferRatio, int finalMemSize, double finalInputBufferRatio,
		final Function newQueue, Function newQueuesQueue,
		final Comparator queuesQueueComparator, final boolean verbose) {
		super(cursor,
			 comparator, blockSize, objectSize,
			 memSize, firstOutputBufferRatio, outputBufferRatio,
			 inputBufferRatio, finalMemSize, finalInputBufferRatio,
			 newQueue, newQueuesQueue, queuesQueueComparator, verbose
		);
		this.metaData = (ResultSetMetaData)cursor.getMetaData();
	}

	/**
	 * Creates a new MergeSorter based on a {@link xxl.core.collections.queues.ListQueue ListQueue} for materializing the
	 * internal runs. Further <tt>inputBufferRatio, finalInputBufferRatio,
	 * firstOutputBufferRatio, outputBufferRatio = 0.0</tt> is used. That means only one
	 * page is reserved for input- and output-buffer and the maximal fan-in is used during
	 * the intermediate merges and for the final merge. No output information about
	 * the merges and the buffers is created.
	 * The queues to be merged are inserted in a {@link xxl.core.collections.queues.DynamicHeap DynamicHeap}
	 * and they are compared according their sizes.
	 *
	 * @param cursor the input MetaDataCursor to be sorted.
	 * @param comparator the Comparator used to compare the elements in the heap (Replacement Selection).
	 * @param objectSize the size of an object in main memory.
	 * @param memSize the memory available to the MergeSorter during the open-phase.
	 * @param finalMemSize the memory available to the MergeSorter during the next-phase.
	 */
	public MergeSorter (MetaDataCursor cursor, Comparator comparator, int objectSize, int memSize, int finalMemSize) {
		super(cursor, comparator, objectSize, memSize, finalMemSize);
		this.metaData = (ResultSetMetaData)cursor.getMetaData();
	}

	/**
	 * Creates a new MergeSorter using <tt>inputBufferRatio, finalInputBufferRatio,
	 * firstOutputBufferRatio, outputBufferRatio = 0.0</tt>. That means only one
	 * page is reserved for input- and output-buffer and the maximal fan-in is used during
	 * the intermediate merges and for the final merge.
	 * The queues to be merged are inserted in a {@link xxl.core.collections.queues.DynamicHeap DynamicHeap} and
	 * they are compared according their sizes.
	 *
	 * @param cursor the input MetaDataCursor to be sorted.
	 * @param comparator the Comparator used to compare the elements in the heap (Replacement Selection).
	 * @param objectSize the size of an object in main memory.
	 * @param memSize the memory available to the MergeSorter during the open-phase.
	 * @param finalMemSize the memory available to the MergeSorter during the next-phase.
	 * @param newQueue the function <code>newQueue</code> should return a queue, which is used by the algorithm
	 * 		to materialize the internal runs, i.e. this function determines whether the sort operator
	 * 		works on queues based on external storage or in main memory (useful for testing and counting).
	 *		The function takes two without parameters functions <tt>getInputBufferSize()</tt> and <tt>getOutputBufferSize()</tt> as
	 *		parameters.
	 * @param verbose if the <code>verbose</code>-flag set to <code>true</code>
	 * 		the MergeSorter displays how the memory was distributed internally. In addition,
	 * 		the number of merges is displayed.
	 */
	public MergeSorter (MetaDataCursor cursor, Comparator comparator, int objectSize, int memSize, int finalMemSize,
						Function newQueue, boolean verbose) {
		super(cursor, comparator, objectSize, memSize, finalMemSize, newQueue, verbose);
		this.metaData = (ResultSetMetaData)cursor.getMetaData();
	}

	/**
	 * Creates a new MergeSorter based on a {@link xxl.core.collections.queues.ListQueue ListQueue} for materializing the
	 * internal runs. Further <tt>inputBufferRatio, finalInputBufferRatio,
	 * firstOutputBufferRatio, outputBufferRatio = 0.0</tt> is used. That means only one
	 * page is reserved for input- and output-buffer and the maximal fan-in is used during
	 * the intermediate merges and for the final merge. No output information about
	 * the merges and the buffers is created.
	 * The queues to be merged are inserted in a {@link xxl.core.collections.queues.DynamicHeap DynamicHeap}
	 * and they are compared according their sizes.
	 *
	 * The tuples are compared using the Comparator returned by the <code>Tuples.getTupleComparator</code> method.
	 * <b>This is the type of constructor that is used in most cases.</b>
	 *
	 * @param cursor the input MetaDataCursor to be sorted.
	 * @param onColumns array of column indices: the first column is 1, the second is 2, ...
	 * @param ascending array of boolean that determines the order (ascending=true/descending=false)
	 *	for each dimension.
	 * @param objectSize the size of an object in main memory.
	 * @param memSize the memory available to the MergeSorter during the open-phase.
	 * @param finalMemSize the memory available to the MergeSorter during the next-phase.
	 */
	public MergeSorter (MetaDataCursor cursor, int[] onColumns, boolean[] ascending, int objectSize, int memSize, int finalMemSize) {
		this(cursor, Tuples.getTupleComparator(onColumns, ascending),
			objectSize, memSize, finalMemSize
		);
	}

	/**
	 * Creates a new MergeSorter based on a {@link xxl.core.collections.queues.ListQueue ListQueue} for materializing the
	 * internal runs. Further <tt>inputBufferRatio, finalInputBufferRatio,
	 * firstOutputBufferRatio, outputBufferRatio = 0.0</tt> is used. That means only one
	 * page is reserved for input- and output-buffer and the maximal fan-in is used during
	 * the intermediate merges and for the final merge. No output information about
	 * the merges and the buffers is created.
	 * The queues to be merged are inserted in a {@link xxl.core.collections.queues.DynamicHeap DynamicHeap}
	 * and they are compared according their sizes.
	 *
	 * The <code>objectSize</code> is set to 128 Bytes, the memSize to 48KB and the finalMemSize to 16KB.
	 * The tuples are compared using the Comparator returned by the <code>Tuples.getTupleComparator</code> method.
	 *
	 * @param cursor the input MetaDataCursor to be sorted.
	 * @param onColumns array of column indices: the first column is 1, the second is 2, ...
	 * @param ascending array of boolean that determines the order (ascending=true/descending=false)
	 *	for each dimension.
	 */
	public MergeSorter (MetaDataCursor cursor, int[] onColumns, boolean[] ascending) {
		this(cursor, onColumns, ascending, 128, 12*4096, 4*4096);
	}

	/**
	 * Creates a new MergeSorter based on a {@link xxl.core.collections.queues.ListQueue ListQueue} for materializing the
	 * internal runs. Further <tt>inputBufferRatio, finalInputBufferRatio,
	 * firstOutputBufferRatio, outputBufferRatio = 0.0</tt> is used. That means only one
	 * page is reserved for input- and output-buffer and the maximal fan-in is used during
	 * the intermediate merges and for the final merge. No output information about
	 * the merges and the buffers is created.
	 * The queues to be merged are inserted in a {@link xxl.core.collections.queues.DynamicHeap DynamicHeap}
	 * and they are compared according their sizes.
	 *
	 * The <code>objectSize</code> is set to 128 Bytes, the memSize to 48KB and the finalMemSize to 16KB.
	 *
	 * @param cursor the input MetaDataCursor to be sorted.
	 * @param comparator the Comparator used to compare the elements in the heap (Replacement Selection).
	 */
	public MergeSorter (MetaDataCursor cursor, Comparator comparator) {
		this(cursor, comparator, 128, 12*4096, 4*4096);
	}

	/**
	 * Creates a new MergeSorter.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param createTuple Function that maps a (row of the) ResultSet to a Tuple.
	 *	The function gets a ResultSet and maps the current row to a Tuple. It is
	 * 	forbidden to call the next, update and similar methods of the ResultSet
	 *	from inside the function!
	 * @param comparator the Comparator used to compare the elements in the heap (Replacement Selection).
	 * @param blockSize the size of a block (page).
	 * @param objectSize the size of an object in main memory.
	 * @param memSize the memory available to the MergeSorter during the open-phase.
	 * @param firstOutputBufferRatio the ratio of memory available to the output buffer during run-creation
	 * 		(0.0: use only one page for the output buffer and what remains is used for the heap;
	 * 		 1.0: use as much memory as possible for the output buffer).
	 * @param outputBufferRatio the amount of memory available to the output buffer during intermediate merges (not the final merge)
	 * 		(0.0: use only one page for the output buffer, what remains is used for the merger and the input buffer,
	 * 		inputBufferRatio determines how the remaining memory is distributed between them;
	 * 		 1.0: use as much memory as possible for the output buffer).
	 * @param inputBufferRatio the amount of memory available to the input buffer during intermediate merges (not the final merge)
	 * 		(0.0: use only one page for the input buffer, what remains is used for the Merger (maximal FanIn);
	 * 		 1.0: use as much memory as possible for the input buffer).
	 * @param finalMemSize the memory available to the MergeSorter during the next-phase.
	 * @param finalInputBufferRatio the amount of memory available to the input buffer of the final (online) merge
	 * 		(0.0: use the maximum number of inputs (maximal fanIn), i.e. perform the online merge as early as possible;
	 * 		 1.0: write the entire data into a final queue, the online "merger" just reads the data from
	 * 		 this queue).
	 * @param newQueue the function <code>newQueue</code> should return a queue, which is used by the algorithm
	 * 		to materialize the internal runs, i.e. this function determines whether the sort operator
	 * 		works on queues based on external storage or in main memory (useful for testing and counting).
	 *		The function takes two without parameters functions <tt>getInputBufferSize()</tt> and <tt>getOutputBufferSize()</tt> as
	 *		parameters.
	 * @param newQueuesQueue if this function is invoked, the queue, that should contain
	 * 		the queues to be merged, is returned. The function takes an iterator and the comparator
	 *		<tt>queuesQueueComparator</tt> as parameters. <br>
	 *		E.g. <code><pre>
	 * 			new Function(){
	 *				public Object invoke(Object iterator, Object comparator){
	 *					return new DynamicHeap((Iterator)iterator, (Comparator)comparator);
	 *				}
	 * 		</code></pre>
	 * 		The queues contained in the iterator are inserted in the DynamicHeap using the given
	 * 		Comparator for comparison.
	 * @param queuesQueueComparator this Comparator determines the next queue used for merging.
	 * @param verbose if the <code>verbose</code> flag set to <code>true</code>
	 * 		the MergeSorter displays how the memory was distributed internally. In addition,
	 * 		the number of merges is displayed.
	 */
	public MergeSorter (ResultSet resultSet, Function createTuple, final Comparator comparator,
		final int blockSize, final int objectSize,
		final int memSize, double firstOutputBufferRatio, double outputBufferRatio,
		double inputBufferRatio, int finalMemSize, double finalInputBufferRatio,
		final Function newQueue, Function newQueuesQueue,
		final Comparator queuesQueueComparator, final boolean verbose) {

		this(new ResultSetMetaDataCursor(resultSet, createTuple),
			 comparator, blockSize, objectSize,
			 memSize, firstOutputBufferRatio, outputBufferRatio,
			 inputBufferRatio, finalMemSize, finalInputBufferRatio,
			 newQueue, newQueuesQueue, queuesQueueComparator, verbose
		);
	}

	/**
	 * Creates a new MergeSorter based on a {@link xxl.core.collections.queues.ListQueue ListQueue} for materializing the
	 * internal runs. Further <tt>inputBufferRatio, finalInputBufferRatio,
	 * firstOutputBufferRatio, outputBufferRatio = 0.0</tt> is used. That means only one
	 * page is reserved for input- and output-buffer and the maximal fan-in is used during
	 * the intermediate merges and for the final merge. No output information about
	 * the merges and the buffers is created.
	 * The queues to be merged are inserted in a {@link xxl.core.collections.queues.DynamicHeap DynamicHeap}
	 * and they are compared according their sizes.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param createTuple Function that maps a (row of the) ResultSet to a Tuple.
	 *	The function gets a ResultSet and maps the current row to a Tuple. It is
	 * 	forbidden to call the next, update and similar methods of the ResultSet
	 *	from inside the function!
	 * @param comparator the Comparator used to compare the elements in the heap (Replacement Selection).
	 * @param objectSize the size of an object in main memory.
	 * @param memSize the memory available to the MergeSorter during the open-phase.
	 * @param finalMemSize the memory available to the MergeSorter during the next-phase.
	 */
	public MergeSorter (ResultSet resultSet, Function createTuple, Comparator comparator, int objectSize, int memSize, int finalMemSize) {
		this(new ResultSetMetaDataCursor(resultSet, createTuple), comparator, objectSize, memSize, finalMemSize);
	}

	/**
	 * Creates a new MergeSorter based on a {@link xxl.core.collections.queues.ListQueue ListQueue} for materializing the
	 * internal runs. Further <tt>inputBufferRatio, finalInputBufferRatio,
	 * firstOutputBufferRatio, outputBufferRatio = 0.0</tt> is used. That means only one
	 * page is reserved for input- and output-buffer and the maximal fan-in is used during
	 * the intermediate merges and for the final merge. No output information about
	 * the merges and the buffers is created.
	 * The queues to be merged are inserted in a {@link xxl.core.collections.queues.DynamicHeap DynamicHeap}
	 * and they are compared according their sizes.
	 *
	 * The tuples are compared using the Comparator returned by the <code>Tuples.getTupleComparator</code> method.
	 * <b>This is the type of constructor that is used in most cases.</b>
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param createTuple Function that maps a (row of the) ResultSet to a Tuple.
	 *	The function gets a ResultSet and maps the current row to a Tuple. It is
	 * 	forbidden to call the next, update and similar methods of the ResultSet
	 *	from inside the function!
	 * @param onColumns array of column indices: the first column is 1, the second is 2, ...
	 * @param ascending array of boolean that determines the order (ascending=true/descending=false)
	 *	for each dimension.
	 * @param objectSize the size of an object in main memory.
	 * @param memSize the memory available to the MergeSorter during the open-phase.
	 * @param finalMemSize the memory available to the MergeSorter during the next-phase.
	 */
	public MergeSorter (ResultSet resultSet, Function createTuple, int[] onColumns, boolean[] ascending, int objectSize, int memSize, int finalMemSize) {
		this(resultSet, createTuple,
			Tuples.getTupleComparator(onColumns, ascending),
			objectSize, memSize, finalMemSize
		);
	}

	/**
	 * Creates a new MergeSorter based on a {@link xxl.core.collections.queues.ListQueue ListQueue} for materializing the
	 * internal runs. Further <tt>inputBufferRatio, finalInputBufferRatio,
	 * firstOutputBufferRatio, outputBufferRatio = 0.0</tt> is used. That means only one
	 * page is reserved for input- and output-buffer and the maximal fan-in is used during
	 * the intermediate merges and for the final merge. No output information about
	 * the merges and the buffers is created.
	 * The queues to be merged are inserted in a {@link xxl.core.collections.queues.DynamicHeap DynamicHeap}
	 * and they are compared according their sizes.
	 *
	 * The tuples are compared using the Comparator returned by the <code>Tuples.getTupleComparator</code> method.
	 * The <code>objectSize</code> is set to 128 Bytes, the memSize to 48KB and the finalMemSize to 16KB.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param createTuple Function that maps a (row of the) ResultSet to a Tuple.
	 *	The function gets a ResultSet and maps the current row to a Tuple. It is
	 * 	forbidden to call the next, update and similar methods of the ResultSet
	 *	from inside the function!
	 * @param onColumns array of column indices: the first column is 1, the second is 2, ...
	 * @param ascending array of boolean that determines the order (ascending=true/descending=false)
	 *	for each dimension.
	 */
	public MergeSorter (ResultSet resultSet, Function createTuple, int[] onColumns, boolean[] ascending) {
		this(resultSet, createTuple, onColumns, ascending, 128, 12*4096, 4*4096);
	}

	/**
	 * Returns the resulting metadata for this operator.
	 *
	 * The metadata the same as for the input operator.
	 *
	 * @return object of class ResultSetMetaData
	 */
	public Object getMetaData () {
		return metaData;
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args){

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		/* 
			Wraps a RandomIntegers cursor (100 integers up to 1000)
			to a MetaDataCursor using {@link xxl.core.cursors.Cursors#wrapToMetaDataCursor}. 
			Then, the cursor becomes sorted.
		*/
		System.out.println("Example 1: Sorting randomly generated Integers");
		ResultSetMetaData metaData = (ResultSetMetaData) xxl.core.relational.metaData.MetaData.NUMBER_META_DATA_FACTORY_METHOD.invoke();
		
		MetaDataCursor cursor = xxl.core.cursors.Cursors.wrapToMetaDataCursor(
			Tuples.mapObjectsToTuples(
				new xxl.core.cursors.sources.RandomIntegers(1000,100),
				metaData),
			    metaData
		);
		
		cursor = new MergeSorter(cursor,new int[]{1},new boolean[]{true});
		
		xxl.core.cursors.Cursors.println(cursor);
	}
}
