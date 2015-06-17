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
import xxl.core.cursors.filters.Filter;
import xxl.core.functions.Function;
import xxl.core.predicates.Predicate;

/**
 * Straight forward implementation of the operator selection.
 * <p>
 * This operator is also applicable if the MetaDataCursor 
 * contains Objects that are not Tuples.
 * <p>
 * Example:
 * <code><pre>
 * 	MetaDataCursor cursor = xxl.core.cursors.Cursors.wrapToMetaDataCursor(
 *		new xxl.core.cursors.RandomIntegers(1000,100),
 *		xxl.core.relational.MetaData.NUMBER_META_DATA_FACTORY_METHOD
 *	);
 *	
 *	cursor = new Selection(cursor,
 *		new Predicate() {
 *			public boolean invoke(Object arg) {
 *				int value = ((Integer)arg).intValue();
 *				return (value>=100) && (value<=200);
 *			}
 *		}
 *	);
 *		
 *	xxl.core.cursors.Cursors.println(cursor);
 * </pre></code>
 *
 * Wraps a RandomIntegers cursor (100 integers up to 1000)
 * to a MetaDataCursor using xxl.core.cursors.Cursor.wrapToMetaDataCursor. 
 * Then, all Integers between 100 and 200 are selected.
 */
public class Selection extends Filter implements MetaDataCursor {

	/**
	 * Creates a new instance of Selection.
	 *
	 * @param cursor the input MetaDataCursor delivering the input element. 
	 * @param predicate an unary predicate that has to determine if an element
	 *	qualifies for the result.
	 */
	public Selection (MetaDataCursor cursor, Predicate predicate) {
		super(cursor, predicate);
	}

	/**
	 * Creates a new instance of Selection.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param createTuple Function that maps a (row of the) ResultSet to a Tuple. 
	 *	The function gets a ResultSet and maps the current row to a Tuple. 
	 *	If null is passed, the factory method of ArrayTuple is used.
	 *	It is forbidden to call the next, update and similar methods of the ResultSet
	 *	from inside the function!
	 * @param predicate an unary predicate that has to determine if an element
	 *	qualifies for the result.
	 */
	public Selection (ResultSet resultSet, Function createTuple, Predicate predicate) {
		this(new ResultSetMetaDataCursor(resultSet, createTuple), predicate);
	}

	/**
	 * Returns the resulting metadata for this operator.
	 *
	 * The metadata the same as for the input operator.
	 *
	 * @return object of class ResultSetMetaData
	 */
	public Object getMetaData () {
		return ((MetaDataCursor)getDecoratedCursor()).getMetaData();
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
			to a MetaDataCursor using xxl.core.cursors.Cursor.wrapToMetaDataCursor. 
			Then, all Integers between 100 and 200 are selected.
		*/
		System.out.println("Example 1: Performing a selection >=100 and <=200 on random integers (100 integers up to 1000)");
		MetaDataCursor cursor = xxl.core.cursors.Cursors.wrapToMetaDataCursor(
			new xxl.core.cursors.sources.RandomIntegers(1000,100),
			xxl.core.relational.metaData.MetaData.NUMBER_META_DATA_FACTORY_METHOD
		);
		
		cursor = new Selection(cursor,
			new Predicate() {
				public boolean invoke(Object arg) {
					int value = ((Integer)arg).intValue();
					return (value>=100) && (value<=200);
				}
			}
		);
		
		xxl.core.cursors.Cursors.println(cursor);
	}
}
