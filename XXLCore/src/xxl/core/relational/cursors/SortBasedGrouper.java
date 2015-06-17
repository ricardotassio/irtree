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
import xxl.core.relational.Tuple;
import xxl.core.relational.Tuples;

/**
 * SortBasedGrouper is an implementation of the group operator. 
 * It is based on {@link xxl.core.cursors.groupers.SortBasedGrouper}. The 
 * input relation has to be sorted so that all elements 
 * belonging to the same group have to be in sequence.
 * Then, a group is defined by a predicate that returns
 * false at the end of such a sequence.
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
public class SortBasedGrouper extends xxl.core.cursors.groupers.SortBasedGrouper implements MetaDataCursor {

	/**
	 * Constructs an instance of the SortBasedGrouper operator.
	 *
	 * @param sortedCursor the sorted MetaDataCursor containing elements.
	 * @param predicate the predicate that determines the borders of the groups.
	 */
	public SortBasedGrouper (MetaDataCursor sortedCursor, Predicate predicate) {
		super(sortedCursor, predicate);
	}

	/**
	 * Constructs an instance of the SortBasedGrouper operator.
	 *
	 * @param sortedCursor the sorted MetaDataCursor containing elements.
	 * @param byColumns if the values in the passed column numbers differ, a new
	 *	group is created. The first column is 1, ...
	 */
	public SortBasedGrouper (MetaDataCursor sortedCursor, final int[] byColumns) {
		this(sortedCursor,
			new Predicate () {
				Comparator comparator = Tuples.getTupleComparator(byColumns);

				public boolean invoke (Object previous, Object next) {
					return comparator.compare(previous, next) != 0 ? true : false ;
				}
			}
		);
	}

	/**
	 * Constructs an instance of the SortBasedGrouper operator.
	 *
	 * @param sortedResultSet the sorted ResultSet containing elements.
	 * @param createTuple Function that maps a (row of the) ResultSet to a Tuple. 
	 *	The function gets a ResultSet and maps the current row to a Tuple. 
	 *	If null is passed, the factory method of ArrayTuple is used.
	 *	It is forbidden to call the next, update and similar methods of the ResultSet
	 *	from inside the function!
	 * @param predicate the predicate that determines the borders of the groups.
	 */
	public SortBasedGrouper (ResultSet sortedResultSet, Function createTuple, Predicate predicate) {
		this(new ResultSetMetaDataCursor(sortedResultSet, createTuple), predicate);
	}

	/**
	 * Constructs an instance of the SortBasedGrouper operator.
	 *
	 * @param sortedResultSet the sorted ResultSet containing elements.
	 * @param createTuple Function that maps a (row of the) ResultSet to a Tuple. 
	 *	The function gets a ResultSet and maps the current row to a Tuple. 
	 *	If null is passed, the factory method of ArrayTuple is used.
	 *	It is forbidden to call the next, update and similar methods of the ResultSet
	 *	from inside the function!
	 * @param byColumns if the values in the passed column numbers differ, a new
	 *	group is created. The first column is 1, ...
	 */
	public SortBasedGrouper (ResultSet sortedResultSet, Function createTuple, final int[] byColumns) {
		this(new ResultSetMetaDataCursor(sortedResultSet, createTuple), byColumns);
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
			to a MetaDataCursor using {@link xxl.core.cursors.Cursors#wrapToMetaDataCursor}. 
			A SortBasedGrouper is used to group the objects according
			to their first digit. Then, the first group is sent to System.out.
		*/
		System.out.println("Example 1: Grouping 00, 01, ..., 99 after the first digit");
		java.sql.ResultSetMetaData metaData = (java.sql.ResultSetMetaData) xxl.core.relational.metaData.MetaData.NUMBER_META_DATA_FACTORY_METHOD.invoke();
		
		MetaDataCursor cursor = xxl.core.cursors.Cursors.wrapToMetaDataCursor(
			Tuples.mapObjectsToTuples(
				new xxl.core.cursors.sources.Enumerator(0,100),
				metaData),
			    metaData
		);
		
		cursor = new SortBasedGrouper(cursor,
			new Predicate() {
				public boolean invoke(Object previous,Object next) {
					Tuple t1 = (Tuple) previous;
					Tuple t2 = (Tuple) next;
					return ((t1.getInt(1)/10)!=(t2.getInt(1)/10));
				}
			}
		);
		
		System.out.println("Printing the elements of the first group that is returned.");
		xxl.core.cursors.Cursor firstGroup = null;
		if (cursor.hasNext()) {
			firstGroup = (xxl.core.cursors.Cursor) cursor.next();
			xxl.core.cursors.Cursors.println(firstGroup);
		}
		else
			throw new RuntimeException("Error in SortBasedGrouper (first group)!!!");
	
		System.out.println("Accessing two more groups");
		xxl.core.cursors.Cursor secondGroup = (xxl.core.cursors.Cursor) cursor.next();
		xxl.core.cursors.Cursor thirdGroup = (xxl.core.cursors.Cursor) cursor.next();
		
		if (secondGroup.hasNext())
			throw new RuntimeException("Error in SortBasedGrouper (could access erlier groups)");
	
		System.out.println("Testing a group 3");
		if (thirdGroup.hasNext()) {
			Tuple t = (Tuple) thirdGroup.next();
			if (t.getInt(1)/10!=2) 
				throw new RuntimeException("Error in SortBasedGrouper (object of third cursor does not belong to this group)!!!");
		}
		
		System.out.println("Counting the groups...");
		int groupsLeft = xxl.core.cursors.Cursors.count(cursor);
		System.out.println("Groups left (7 is ok): "+groupsLeft);
		if (groupsLeft!=7)
			throw new RuntimeException("Error in SortBasedGrouper (number of groups)!!!");
			
	}
}
