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

import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.predicates.Predicate;

/**
 *	Nested-loops implementation of the operator "Distinct".
 *	This class uses the algorithm of {@link xxl.core.cursors.distincts.NestedLoopsDistinct} 
 *	and additionally forwards the metadata.
 *	<P>
 * 	
 *	Depending on the specified memory size and object size as many elements as possible
 * 	will be inserted into a temporal Bag (typically located in main memory).
 * 	To guarantee that no duplicates will be inserted
 * 	the Bag is searched for duplicates with the help of a Predicate specified in the constructor.
 * 	If not all elements can be inserted into the Bag they will be stored in a Queue
 * 	and will be inserted when the Bag emptied due to calls to <code>this.next()</code>.
 *
 *	Example:
 *
 *	<pre><code>
 *	new NestedLoopsDistinct(cursor, 100000, 200);
 *		// 100000: amount of memory used in Bytes
 *		// 200: size of each Tuple
 *	</code></pre>
 *
 *	This distinct-operator handles up to (100000/200)-1 Tuples in main memory at 
 *	one time. Additional tuples will be stored in a bag.
 */
public class NestedLoopsDistinct extends xxl.core.cursors.distincts.NestedLoopsDistinct implements MetaDataCursor {

	/**
	 * Creates a new instance of NestedLoopsDistinct.
	 * Determines the maximum number of elements that can be stored
	 * in the Bag used for the temporal storage of the elements of the input Cursor:
	 * <code>maxTuples = memSize / objectSize - 1</code>.
	 *
	 * @param cursor the input MetaDataCursor delivering the elements.
	 * @param memSize the available memory size (bytes) for the Bag.
	 * @param objectSize the size (bytes) needed to store one element.
	 * @param predicate the Predicate returning <tt>true</tt> if two elements are equal.
	 * @param newBag a Function without parameters returning an empty Bag
	 *	whose <code>bag.cursor()</code> must support <code>remove()</code>.
	 * @param newQueue a Function without parameters returning an empty Queue.
	 * @throws IllegalArgumentException if not enough memory is available.
	 */
	public NestedLoopsDistinct (MetaDataCursor cursor, int memSize, int objectSize, Predicate predicate, Function newBag, Function newQueue) {
		super(cursor, memSize, objectSize, predicate, newBag, newQueue);
	}

	/**
	 * Creates a new instance of NestedLoopsDistinct.
	 * Determines the maximum number of elements that can be stored
	 * in the Bag used for the temporal storage of the elements of the input Cursor:
	 * <code>maxTuples = memSize / objectSize - 1</code>.
	 * Uses the Predicate <code>Equal.DEFAULT_INSTANCE</code> and the factory
	 * methods for the classes Bag and Queue.
	 *
	 * @param cursor the input MetaDataCursor delivering the elements.
	 * @param memSize the available memory size (bytes) for the Bag.
	 * @param objectSize the size (bytes) needed to store one element.
	 * @throws IllegalArgumentException if not enough memory is available.
	 */
	public NestedLoopsDistinct (MetaDataCursor cursor, int memSize, int objectSize) {
		super(cursor, memSize, objectSize);
	}

	/**
	 * Creates a new instance of NestedLoopsDistinct.
	 * Determines the maximum number of elements that can be stored
	 * in the Bag used for the temporal storage of the elements of the input Cursor:
	 * <code>maxTuples = memSize / objectSize - 1</code>.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped to 
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param memSize the available memory size (bytes) for the Bag.
	 * @param objectSize the size (bytes) needed to store one element.
	 * @param predicate the Predicate returning <tt>true</tt> if two elements are equal.
	 * @param newBag a Function without parameters returning an empty Bag
	 *	whose <code>bag.cursor()</code> must support <code>remove()</code>.
	 * @param newQueue a Function without parameters returning an empty Queue.
	 * @throws IllegalArgumentException if not enough memory is available.
	 */
	public NestedLoopsDistinct (ResultSet resultSet, int memSize, int objectSize, Predicate predicate, Function newBag, Function newQueue) {
		this(new ResultSetMetaDataCursor(resultSet), memSize, objectSize, predicate, newBag, newQueue);
	}

	/**
	 * Creates a new instance of NestedLoopsDistinct.
	 * Determines the maximum number of elements that can be stored
	 * in the Bag used for the temporal storage of the elements of the input Cursor:
	 * <code>maxTuples = memSize / objectSize - 1</code>.
	 * Uses the Predicate <code>Equal.DEFAULT_INSTANCE</code> and the factory
	 * methods for the classes Bag and Queue.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped to 
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param memSize the available memory size (bytes) for the Bag.
	 * @param objectSize the size (bytes) needed to store one element.
	 * @throws IllegalArgumentException if not enough memory is available.
	 */
	public NestedLoopsDistinct (ResultSet resultSet, int memSize, int objectSize) {
		this(new ResultSetMetaDataCursor(resultSet), memSize, objectSize);
	}

	/**
	 * Returns the resulting metadata for this operator.
	 *
	 * The metadata the same as for the input operator.
	 *
	 * @return object of class ResultSetMetaData
	 */
	public Object getMetaData () {
		return ((MetaDataCursor)cursor).getMetaData();
	}

}