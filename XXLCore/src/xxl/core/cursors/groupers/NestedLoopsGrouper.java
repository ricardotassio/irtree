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

package xxl.core.cursors.groupers;

import java.util.Iterator;
import java.util.Map;

import xxl.core.collections.bags.Bag;
import xxl.core.collections.queues.Queue;
import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.cursors.wrappers.QueueCursor;
import xxl.core.functions.Function;

/**
 * A nested-loops implementation of the group operator, i.e., all elements of the
 * input iteration get partitioned according to a user defined function.
 * Depending on the specified memory size and object size as many elements as
 * possible will be inserted into temporal bags. Each bag refers to a special key
 * stored in a {@link java.util.Map map} in main memory. If enough main memory is
 * available the bags used to store the elements may also reside in main memory,
 * otherwise they should be located on external memory. A key, a bag refers to,
 * is returned when the user defined unary mapping function is applied to an
 * element of the input iteration. If the map's size gets larger than
 * <pre>
 *     maxTuples = ((memSize - objectSize) / keySize) - 1
 * </pre>
 * the remaining elements of the input iteration are temporary stored in a queue
 * that is typically resided in external memory.
 * 
 * <p><b>Note:</b> If the input iteration is given by an object of the class
 * {@link java.util.Iterator}, i.e., it does not support the <tt>peek</tt>
 * operation, it is internally wrapped to a cursor.</p>
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     NestedLoopsGrouper grouper = new NestedLoopsGrouper(
 *         new Enumerator(21),
 *         new Function() {
 *             public Object invoke(Object next) {
 *                 return new Integer(((Integer)next).intValue() % 5);
 *             }
 *         },
 *         new BinarySearchTreeMap(),
 *         32,
 *         4,
 *         8,
 *         Bag.FACTORY_METHOD,
 *         Queue.FACTORY_METHOD
 *     );
 * 
 *     grouper.open();
 * 
 *     while (grouper.hasNext()) {
 *         Cursor nextGroup = (Cursor)grouper.next();
 *         System.out.println("Next group: ");
 *         while (nextGroup.hasNext())
 *             System.out.println(nextGroup.next());
 *     }
 * 
 *     grouper.close();
 * </pre>
 * The enumerator shown in this example delivers the integer numbers from 0 to
 * 20. The main memory size is set to 32 bytes and the object size to 4 bytes.
 * The size of a key for an element is unrealistically set to 8 bytes. So the
 * {@link xxl.core.collections.BinarySearchTreeMap binary-search-tree-map} used
 * to store the keys in main memory can hold two elements. Therefore only two
 * {@link xxl.core.collections.bags.ListBag list-bags} returned by the factory
 * method of the class {@link xxl.core.collections.bags.Bag} can be allocated by
 * a call to <tt>hasNext</tt>.<br />
 * Due to the fact that not all keys can be stored in the main-memory map the
 * remaining elements that cannot be stored in the two allocated bags will be
 * stored in an {@link xxl.core.collections.queues.ArrayQueue array-queue}
 * returned by the factory method of the class
 * {@link xxl.core.collections.queues.Queue}. Normally this queue should be
 * placed in external memory. The mapping function maps an element of the input
 * iteration to a certain key. In this case it is realized as: (integer value)
 * modulo 5. So the output is:
 * <pre>
 *     Next group: 0; 5; 10; 15; 20
 *     Next group: 1; 6; 11; 16
 *     Next group: 2; 7; 12; 17
 *     Next group: 3; 8; 13; 18
 *     Next group: 4; 9; 14; 19
 * </pre></p>
 * 
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.groupers.HashGrouper
 * @see xxl.core.cursors.groupers.SortBasedGrouper
 */
public class NestedLoopsGrouper extends AbstractCursor {

	/**
	 * The input iteration delivering the elements to group.
	 */
	protected Cursor input;

	/**
	 * A cursor iterating over all bags, i.e., it contains cursors generated by
	 * calls to <code>bag.cursor()</code>.
	 */
	protected Cursor bagIterator = null;

	/**
	 * A queue storing all elements of the input iteration that could not be
	 * stored due to a lack of main-memory size.
	 */
	protected Queue remainder = null;

	/**
	 * A map used to store a key for each bag. Usally located in main memory.
	 */
	protected Map map;

	/**
	 * An unary function returning a key for each given value.
	 */
	protected Function mapping;

	/**
	 * A parameterless function returning an empty bag.
	 */
	protected Function newBag;

	/**
	 * A parameterless function returning an empty queue.
	 */
	protected Function newQueue;

	/**
	 * The maximum number of elements (keys) that can be stored in main memory.
	 */
	protected int maxTuples;

	/**
	 * A flag determining if the input is still the given input iteration or the
	 * queue <tt>remainder</tt>.
	 */
	protected boolean initialized = false;

	/**
	 * Creates a new nested-loops grouper. Determines the maximum number of keys
	 * that can be stored in the main memory map:
	 * <pre>
	 *     ((memSize - objectSize) / keySize) - 1
	 * </pre>
	 * This formula is based on the assumption that only the keys, i.e., the map,
	 * is stored in main memory whereas the bags storing the input iteration's
	 * elements are located in external memory.
	 *
	 * @param input the input iteration delivering the elements to be grouped.
	 * @param mapping an unary mapping function returning a key to a given value.
	 * @param map the map which is used for storing the keys in main memory.
	 * @param memSize the maximum amount of available main memory (bytes) for the
	 *        map.
	 * @param objectSize the size (bytes) needed to store one element.
	 * @param keySize the size (bytes) a key needs in main memory.
	 * @param newBag a parameterless function returning an empty bag.
	 * @param newQueue a parameterless function returning an empty queue.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsGrouper(Iterator input, Function mapping, Map map, int memSize, int objectSize, int keySize, Function newBag, Function newQueue) throws IllegalArgumentException {
		this.input = Cursors.wrap(input);
		this.mapping = mapping;
		this.map = map;
		this.newBag = newBag;
		this.newQueue = newQueue;
		this.maxTuples = ((memSize - objectSize) / keySize) - 1;
		if (memSize < 2*keySize + objectSize)
			throw new IllegalArgumentException("insufficient main memory available.");
	}

	/**
	 * Creates a new nested-loops grouper. Determines the maximum number of keys
	 * that can be stored in the main memory map:
	 * <pre>
	 *     ((memSize - objectSize) / keySize) - 1
	 * </pre>
	 * This formula is based on the assumption that only the keys, i.e., the map,
	 * is stored in main memory whereas the bags storing the input iteration's
	 * elements are located in external memory. Uses the factory methods for
	 * bags, {@link xxl.core.collections.bags.Bag#FACTORY_METHOD}, and queues,
	 * {@link xxl.core.collections.queues.Queue#FACTORY_METHOD}.
	 *
	 * @param input the input iterator delivering the elements to be grouped.
	 * @param mapping an unary mapping function returning a key to a given value.
	 * @param map the map which is used for storing the keys in main memory.
	 * @param memSize the maximum amount of available main memory (bytes) for the
	 *        map.
	 * @param objectSize the size (bytes) needed to store one element.
	 * @param keySize the size (bytes) a key needs in main memory.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsGrouper(Iterator input, Function mapping, Map map, int memSize, int objectSize, int keySize) throws IllegalArgumentException {
		this(input, mapping, map, memSize, objectSize, keySize, Bag.FACTORY_METHOD, Queue.FACTORY_METHOD);
	}

	/**
	 * Opens the nested-loops grouper, i.e., signals the cursor to reserve
	 * resources, open the input iteration, etc. Before a cursor has been
	 * opened calls to methods like <tt>next</tt> or <tt>peek</tt> are not
	 * guaranteed to yield proper results. Therefore <tt>open</tt> must be called
	 * before a cursor's data can be processed. Multiple calls to <tt>open</tt>
	 * do not have any effect, i.e., if <tt>open</tt> was called the cursor
	 * remains in the state <i>opened</i> until its <tt>close</tt> method is
	 * called.
	 * 
	 * <p>Note, that a call to the <tt>open</tt> method of a closed cursor
	 * usually does not open it again because of the fact that its state
	 * generally cannot be restored when resources are released respectively
	 * files are closed.</p>
	 */
	public void open() {
		super.open();
		input.open();
	}
	
	/**
	 * Closes the nested-loops grouper. Signals it to clean up resources,
	 * close queues and cursors, etc. After a call to <tt>close</tt> calls to
	 * methods like <tt>next</tt> or <tt>peek</tt> are not guarantied to yield
	 * proper results. Multiple calls to <tt>close</tt> do not have any effect,
	 * i.e., if <tt>close</tt> was called the nested-loops grouper remains in the
	 * state "closed".
	 */
	public void close() {
		super.close();
		input.close();
		bagIterator.close();
		if (remainder != null)
			remainder.close();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other words,
	 * returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would return an
	 * element rather than throwing an exception.) Inserts as many elements as
	 * possible in bags determining a bag for each element by invoking the
	 * mapping function. The returned key is stored in the main memory map and
	 * the element is inserted in the appropriate bag. If no bag exists for a
	 * key, a new bag is allocated. If the map's size is greater or equal to
	 * <tt>maxTuples</tt> the remaining elements of the input cursor are
	 * inserted into a temporal queue, which delivers the input elements when
	 * the <tt>bagIterator</tt> has completely been traversed. The
	 * <tt>bagIterator</tt> contains the elements that will be returned by calls
	 * to <tt>next</tt> or <tt>peek</tt>.
	 *
	 * @return <tt>true</tt> if the nested-loops grouper has more elements.
	 */
	protected boolean hasNextObject() {
		if (bagIterator == null || !bagIterator.hasNext()) {
			Cursor input = initialized ?
				(Cursor)new QueueCursor(remainder) :
				this.input;
			int counter = 0;
			if (initialized && remainder != null)
				counter = remainder.size();
			while ((!initialized && input.hasNext()) || (initialized && counter-- > 0)) {
				Object next = input.next();
				Object key = mapping.invoke(next);
				if (!map.containsKey(key))
					if (map.size() < maxTuples)
						map.put(key, newBag.invoke());
					else {
						if (remainder == null)
							(remainder = (Queue)newQueue.invoke()).open();
						remainder.enqueue(next);
						continue;
					}
				Bag bag = (Bag)map.get(key);
				bag.insert(next);
			}
			initialized = true;
			return (bagIterator = Cursors.wrap(map.values().iterator())).hasNext();
		}
		return true;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more. Returns
	 * the next group delivered by the <tt>bagIterator</tt>, a cursor, and
	 * removes the corresponding bag from the map.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		Cursor result = ((Bag)bagIterator.next()).cursor();
		bagIterator.remove();
		return result;
	}

	/**
	 * Resets the cursor to its initial state such that the caller is able to
	 * traverse the underlying data structure again without constructing a new
	 * cursor (optional operation). The modifications, removes and updates
	 * concerning the underlying data structure, are still persistent.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the nested-loops grouper.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		input.reset();
		bagIterator.close();
		bagIterator = null;
		if (remainder != null)
			remainder.clear();
		initialized = false;
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the nested-loops grouper. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the nested-loops grouper, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return input.supportsReset();
	}

	/**
	 * The main method contains some examples to demonstrate the usage and the
	 * functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to submit
	 *        parameters when the main method is called.
	 */
	public static void main(String[] args) {

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		
		NestedLoopsGrouper grouper = new NestedLoopsGrouper(
			new xxl.core.cursors.sources.Enumerator(21),
			new Function() {
				public Object invoke(Object next) {
					return new Integer(((Integer)next).intValue() % 5);
				}
			},
			new xxl.core.collections.BinarySearchTreeMap(),
			32,
			4,
			8,
			Bag.FACTORY_METHOD,
			Queue.FACTORY_METHOD
		);

		grouper.open();
		
		while (grouper.hasNext()) {
			Cursor nextGroup = (Cursor)grouper.next();
			System.out.println("Next group: ");
			while (nextGroup.hasNext())
				System.out.println(nextGroup.next());
		}
		
		grouper.close();
	}
}
