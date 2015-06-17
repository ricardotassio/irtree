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
import java.util.Map;

import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.relational.Tuple;
import xxl.core.relational.Tuples;

/**
 * NestedLoopsGrouper is an implementation of the group operator. 
 * It is based on {@link xxl.core.cursors.groupers.NestedLoopsGrouper}. 
 * Then, a group is defined by ...
 * <p>
 * A call to the <code>next</code>-Method returns a Cursor
 * containing all elements of a group.
 * <p>
 * Usually, an {@link xxl.core.relational.cursors.Aggregator} is applied
 * on the output of a grouper.
 * <p>
 * This operator is also applicable if the MetaDataCursor 
 * contains Objects that are not Tuples (first constructor).
 */
public class NestedLoopsGrouper extends xxl.core.cursors.groupers.NestedLoopsGrouper implements MetaDataCursor {

	/**
	 * Constructs an instance of the NestedLoopsGrouper operator.
	 * Determines the maximum number of keys that can be stored
	 * in the main memory map: <br>
	 * <code>((memSize - objectSize) / keySize) - 1</code>. <br>
	 * This formula is based on the assumption that only the keys, i.e., the Map,
	 * is stored in main memory whereas the bags storing the input cursor's
	 * elements are located in external memory.
	 *
	 * @param cursor the MetaDataCursor containing input elements.
	 * @param mapping an unary mapping function returning a key to a given value.
	 * @param map the map which is used for storing the keys in main memory.
	 * @param memSize the maximum amount of available main memory (bytes) for the map.
	 * @param objectSize the size (bytes) needed to store one element.
	 * @param keySize the size (bytes) a key needs in main memory.
	 * @param newBag a parameterless function returning an empty bag.
	 * @param newQueue a parameterless function returning an empty queue.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsGrouper (MetaDataCursor cursor, Function mapping, Map map, int memSize, int objectSize, int keySize, Function newBag, Function newQueue) {
		super(cursor, mapping, map, memSize, objectSize, keySize, newBag, newQueue);
	}

	/**
	 * Constructs an instance of the NestedLoopsGrouper operator.
	 * Determines the maximum number of keys that can be stored
	 * in the main memory map: <br>
	 * <code>((memSize - objectSize) / keySize) - 1</code>. <br>
	 * This formula is based on the assumption that only the keys, i.e., the Map,
	 * is stored in main memory whereas the bags storing the input cursor's
	 * elements are located in external memory. <br>
	 * Uses the factory methods for bags, {@link xxl.core.collections.bags.Bag#FACTORY_METHOD}, and
	 * queues, {@link xxl.core.collections.queues.Queue#FACTORY_METHOD}.
	 *
	 * @param cursor the MetaDataCursor containing input elements.
	 * @param mapping an unary mapping function returning a key to a given value.
	 * @param map the map which is used for storing the keys in main memory.
	 * @param memSize the maximum amount of available main memory (bytes) for the map.
	 * @param objectSize the size (bytes) needed to store one element.
	 * @param keySize the size (bytes) a key needs in main memory.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsGrouper (MetaDataCursor cursor, Function mapping, Map map, int memSize, int objectSize, int keySize) {
		super(cursor, mapping, map, memSize, objectSize, keySize);
	}

	/**
	 * Constructs an instance of the NestedLoopsGrouper operator.
	 * Determines the maximum number of keys that can be stored
	 * in the main memory map: <br>
	 * <code>((memSize - objectSize) / keySize) - 1</code>. <br>
	 * This formula is based on the assumption that only the keys, i.e., the Map,
	 * is stored in main memory whereas the bags storing the input cursor's
	 * elements are located in external memory.
	 *
	 * @param resultSet the ResultSet containing input elements.
	 * @param mapping an unary mapping function returning a key to a given value.
	 * @param map the map which is used for storing the keys in main memory.
	 * @param memSize the maximum amount of available main memory (bytes) for the map.
	 * @param objectSize the size (bytes) needed to store one element.
	 * @param keySize the size (bytes) a key needs in main memory.
	 * @param newBag a parameterless function returning an empty bag.
	 * @param newQueue a parameterless function returning an empty queue.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsGrouper (ResultSet resultSet, Function mapping, Map map, int memSize, int objectSize, int keySize, Function newBag, Function newQueue) {
		super(new ResultSetMetaDataCursor(resultSet), mapping, map, memSize, objectSize, keySize, newBag, newQueue);
	}

	/**
	 * Constructs an instance of the NestedLoopsGrouper operator.
	 * Determines the maximum number of keys that can be stored
	 * in the main memory map: <br>
	 * <code>((memSize - objectSize) / keySize) - 1</code>. <br>
	 * This formula is based on the assumption that only the keys, i.e., the Map,
	 * is stored in main memory whereas the bags storing the input cursor's
	 * elements are located in external memory. <br>
	 * Uses the factory methods for bags, {@link xxl.core.collections.bags.Bag#FACTORY_METHOD}, and
	 * queues, {@link xxl.core.collections.queues.Queue#FACTORY_METHOD}.
	 *
	 * @param resultSet the ResultSet containing input elements.
	 * @param mapping an unary mapping function returning a key to a given value.
	 * @param map the map which is used for storing the keys in main memory.
	 * @param memSize the maximum amount of available main memory (bytes) for the map.
	 * @param objectSize the size (bytes) needed to store one element.
	 * @param keySize the size (bytes) a key needs in main memory.
	 * @throws IllegalArgumentException if not enough main memory is available.
	 */
	public NestedLoopsGrouper (ResultSet resultSet, Function mapping, Map map, int memSize, int objectSize, int keySize) {
		super(new ResultSetMetaDataCursor(resultSet), mapping, map, memSize, objectSize, keySize);
	}

	/**
	 * Returns the resulting metadata for this operator.
	 *
	 * This is the same as for the input relation.
	 *
	 * @return object of class ResultSetMetaData
	 */
	public Object getMetaData () {
		return ((MetaDataCursor)input).getMetaData();
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
			Wraps an Enumerator cursor (integers 0 to 9)
			to a MetaDataCursor using xxl.core.cursors.Cursor.wrapToMetaDataCursor. 
			A NestedLoopsGrouper is used to group the objects according
			to their last digit. Then, the first group is sent to System.out.
		*/
		System.out.println("Example 1: Grouping 0, 1, ..., 99 after the last digit");
		java.sql.ResultSetMetaData metaData = (java.sql.ResultSetMetaData) xxl.core.relational.metaData.MetaData.NUMBER_META_DATA_FACTORY_METHOD.invoke();
		
		MetaDataCursor cursor = xxl.core.cursors.Cursors.wrapToMetaDataCursor(
			Tuples.mapObjectsToTuples(
				new xxl.core.cursors.sources.Enumerator(0,100),
				metaData),
			    metaData
		);
		
		cursor = new NestedLoopsGrouper(cursor,
			new Function() {
				public Object invoke(Object o) {
					Tuple t = (Tuple) o;
					return new Integer(t.getInt(1)%10);
				}
			},
			new java.util.HashMap(),
			10000,
			4,
			4
		);
		
		System.out.println("Printing the elements of the first group that is returned.");
		if (cursor.hasNext())
			xxl.core.cursors.Cursors.println((xxl.core.cursors.Cursor) cursor.next());
		else
			throw new RuntimeException("Error in NestedLoopsGrouper (first group)!!!");
		
		int groupsLeft = xxl.core.cursors.Cursors.count(cursor);
		System.out.println("Groups left (9 is ok): "+groupsLeft);
		if (groupsLeft!=9)
			throw new RuntimeException("Error in NestedLoopsGrouper (number of groups)!!!");		
	}
}
