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
import java.util.Comparator;

import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.predicates.Predicate;

/**
 *	Sort-based implementation of the operator "Distinct".
 *	This class uses the algorithm of {@link xxl.core.cursors.distincts.SortBasedDistinct}
 *	and additionally forwards the metadata.
 *	<P>
 *	To get a correct result, the input relations have to be sorted when using
 *	the first or the second constructor. To sort a
 *	MetaDataCursor use the {@link MergeSorter MergeSorter}.
 *	The last two constructors perform an early duplicate removal
 *	and do not need a sorted input.
 *
 *	Example:
 *
 *	<pre><code>
 *	new SordBasedDistinct(cursor, Equal.DEFAULT_INSTANCE);
 *	</code></pre>
 */
public class SortBasedDistinct extends xxl.core.cursors.distincts.SortBasedDistinct implements MetaDataCursor {

	/**
	 * Creates a new instance of SortBasedDistinct.
	 *
	 * @param sortedCursor the input MetaDataCursor (must be sorted) delivering the elements.
	 * @param predicate a binary predicate that has to determine a match between
	 * 		two elements of the input iteration.
	 */
	public SortBasedDistinct (MetaDataCursor sortedCursor, Predicate predicate) {
		super(sortedCursor, predicate);
	}

	/**
	 * Creates a new instance of SortBasedDistinct.
	 *
	 * @param sortedResultSet the input ResultSet (must be sorted) delivering the elements. The ResultSet is wrapped internally
	 * 	to MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param predicate a binary predicate that has to determine a match between
	 * 	two elements of the input iteration.
	 */
	public SortBasedDistinct (ResultSet sortedResultSet, Predicate predicate) {
		this(new ResultSetMetaDataCursor(sortedResultSet), predicate);
	}

	/**
	 * Creates a new instance of SortBasedDistinct operator and sorts the input at first
	 * using a {@link xxl.core.cursors.sorters.MergeSorter MergeSorter} with {@link xxl.core.collections.queues.DistinctQueue DistinctQueues}.
	 * So, an object constructed with this constructor is not really a SortBased operator!
	 * <b>Performs an early duplicate elimination during the sort operation.</b>
	 *
	 * @param cursor the input MetaDataCursor delivering the elements.
	 * @param comparator the Comparator used to compare the elements in the heap (Replacement-Selection).
	 * @param blockSize the size of a block (page).
	 * @param objectSize the size of an Object in main memory.
	 * @param memSize the memory available to the MergeSorter during the open-phase.
	 * @param firstOutputBufferRatio the ratio of memory available to the outputBuffer during run-creation
	 * 		(0.0: use only one page for the outputBuffer and what remains for the heap;
	 * 		 1.0: use as much memory as possible for the outputBuffer).
	 * @param outputBufferRatio the amount of memory available to the outputBuffer during intermediate merges (not the final merge)
	 * 		(0.0: use only one page for the outputBuffer, what remains is used for the Merger and the input-buffers,
	 * 		inputBufferRatio determines how the remaining memory is distributed between them;
	 * 		 1.0: use as much memory as possible for the outputBuffer).
	 * @param inputBufferRatio the amount of memory available to the inputBuffers during intermediate merges (not the final merge)
	 * 		(0.0: use only one page for the inputBuffer, what remains is used for the Merger (maximal FanIn);
	 * 		 1.0: use as much memory as possible for the inputBuffers).
	 * @param finalMemSize the memory available to the MergeSorter during the next-phase.
	 * @param finalInputBufferRatio the amount of memory available to the input buffers of the final (online) merge
	 * 		(0.0: use the maximum number of inputs (maximal fanIn), i.e. perform the online merge as early as possible;
	 * 		 1.0: write the entire data into a final queue, the online "merger" just reads the data from
	 * 		 this queue).
	 * @param newQueuesQueue If this function is invoked, the queue, that should contain
	 * 		the queues to be merged, is returned. The function takes an Iterator and the comparator
	 *		<tt>queuesQueueComparator</tt> as parameters. <br>
	 *		E.g. <code><pre>
	 * 			new Function(){
	 *				public Object invoke(Object iterator, Object comparator){
	 *					return new DynamicHeap((Iterator)iterator, (Comparator)comparator);
	 *				}
	 * 		</code></pre>
	 * 		The queues contained in the iterator are inserted in the DynamicHeap using the given
	 * 		comparator for comparison.
	 * @param queuesQueueComparator this comparator determines the next queue used for merging.
	 * @see xxl.core.cursors.sorters.MergeSorter
	 */
	public SortBasedDistinct (MetaDataCursor cursor, Comparator comparator, int blockSize, int objectSize,
		int memSize, double firstOutputBufferRatio, double outputBufferRatio, double inputBufferRatio,
		int finalMemSize, double finalInputBufferRatio, Function newQueuesQueue, Comparator queuesQueueComparator) {

		super(cursor);
		this.cursor = new MergeSorter (cursor, comparator, blockSize, objectSize, memSize, firstOutputBufferRatio,
			outputBufferRatio, inputBufferRatio, finalMemSize, finalInputBufferRatio, newDistinctQueue, newQueuesQueue, queuesQueueComparator, false);
		sorted = true;
	}

	/**
	 * Creates a new instance of SortBasedDistinct operator and sorts the input at first
	 * using a {@link xxl.core.cursors.sorters.MergeSorter MergeSorter} with {@link xxl.core.collections.queues.DistinctQueue DistinctQueues}.
	 * <b>Performs an early duplicate elimination during the sort operation.</b>
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally
	 * 	to MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param comparator the Comparator used to compare the elements in the heap (Replacement-Selection).
	 * @param blockSize the size of a block (page).
	 * @param objectSize the size of an Object in main memory.
	 * @param memSize the memory available to the MergeSorter during the open-phase.
	 * @param firstOutputBufferRatio the ratio of memory available to the outputBuffer during run-creation
	 * 		(0.0: use only one page for the outputBuffer and what remains for the heap;
	 * 		 1.0: use as much memory as possible for the outputBuffer).
	 * @param outputBufferRatio the amount of memory available to the outputBuffer during intermediate merges (not the final merge)
	 * 		(0.0: use only one page for the outputBuffer, what remains is used for the Merger and the input-buffers,
	 * 		inputBufferRatio determines how the remaining memory is distributed between them;
	 * 		 1.0: use as much memory as possible for the outputBuffer).
	 * @param inputBufferRatio the amount of memory available to the inputBuffers during intermediate merges (not the final merge)
	 * 		(0.0: use only one page for the inputBuffer, what remains is used for the Merger (maximal FanIn);
	 * 		 1.0: use as much memory as possible for the inputBuffers).
	 * @param finalMemSize the memory available to the MergeSorter during the next-phase.
	 * @param finalInputBufferRatio the amount of memory available to the input buffers of the final (online) merge
	 * 		(0.0: use the maximum number of inputs (maximal fanIn), i.e. perform the online merge as early as possible;
	 * 		 1.0: write the entire data into a final queue, the online "merger" just reads the data from
	 * 		 this queue).
	 * @param newQueuesQueue If this function is invoked, the queue, that should contain
	 * 		the queues to be merged, is returned. The function takes an Iterator and the comparator
	 *		<tt>queuesQueueComparator</tt> as parameters. <br>
	 *		E.g. <code><pre>
	 * 			new Function(){
	 *				public Object invoke(Object iterator, Object comparator){
	 *					return new DynamicHeap((Iterator)iterator, (Comparator)comparator);
	 *				}
	 * 		</code></pre>
	 * 		The queues contained in the iterator are inserted in the DynamicHeap using the given
	 * 		comparator for comparison.
	 * @param queuesQueueComparator this comparator determines the next queue used for merging.
	 * @see xxl.core.cursors.sorters.MergeSorter
	 */
	public SortBasedDistinct (ResultSet resultSet, Comparator comparator, int blockSize, int objectSize,
		int memSize, double firstOutputBufferRatio, double outputBufferRatio, double inputBufferRatio,
		int finalMemSize, double finalInputBufferRatio, Function newQueuesQueue, Comparator queuesQueueComparator) {

		this(new ResultSetMetaDataCursor(resultSet), comparator, blockSize, objectSize, memSize, firstOutputBufferRatio,
			outputBufferRatio, inputBufferRatio, finalMemSize, finalInputBufferRatio, newQueuesQueue, queuesQueueComparator);
	}

	/**
	 * Creates a new SortBasedDistinct operator and sorts the input at first
	 * using a {@link xxl.core.cursors.sorters.MergeSorter MergeSorter} with {@link xxl.core.collections.queues.DistinctQueue DistinctQueues}.
	 * Performs an early duplicate elimination during the sort operation.
	 *
	 * @param cursor the input cursor to be sorted.
	 * @param comparator the Comparator used to compare the elements in the heap (Replacement Selection).
	 * @param objectSize the size of an object in main memory.
	 * @param memSize the memory available to the MergeSorter during the open-phase.
	 * @param finalMemSize the memory available to the MergeSorter during the next-phase.
	 *
	 * @see xxl.core.cursors.sorters.MergeSorter
	 */
	public SortBasedDistinct (MetaDataCursor cursor, Comparator comparator, int objectSize, int memSize, int finalMemSize) {
		super(cursor);
		this.cursor = new MergeSorter (cursor, comparator, objectSize, memSize, finalMemSize, newDistinctQueue, false);
	 	sorted = true;
	 }

	/**
	 * Creates a new SortBasedDistinct operator and sorts the input at first
	 * using a {@link xxl.core.cursors.sorters.MergeSorter MergeSorter} with {@link xxl.core.collections.queues.DistinctQueue DistinctQueues}.
	 * Performs an early duplicate elimination during the sort operation.
	 *
	 * @param resultSet the input result set to be sorted.
	 * @param comparator the Comparator used to compare the elements in the heap (Replacement Selection).
	 * @param objectSize the size of an object in main memory.
	 * @param memSize the memory available to the MergeSorter during the open-phase.
	 * @param finalMemSize the memory available to the MergeSorter during the next-phase.
	 *
	 * @see xxl.core.cursors.sorters.MergeSorter
	 */
	public SortBasedDistinct (ResultSet resultSet, Comparator comparator, int objectSize, int memSize, int finalMemSize) {
		this(new ResultSetMetaDataCursor(resultSet), comparator, objectSize, memSize, finalMemSize);
	}

	/**
	 * Returns the metadata for a SortBasedDistinct. The metadata is the same
	 * than the metadata of the input.
	 *
	 * @return object of class ResultSetMetaData
	 */
	public Object getMetaData () {
		return ((MetaDataCursor)cursor).getMetaData();
	}
}
