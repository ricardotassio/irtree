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

package xxl.core.collections.bags;

import java.util.Iterator;
import java.util.List;

import xxl.core.cursors.Cursor;
import xxl.core.functions.Function;
import xxl.core.predicates.Predicate;

/**
 * The interface bag represents a data type that is able to contain any
 * kind of object and performs no duplicate detection. More formally, this
 * interface models a kind of mathematical <i>multiset</i> abstraction.<p>
 *
 * The bag only provides methods for insertion of objetcs and removal of
 * all elements, but for accessing or removing <i>single</i> elements, a
 * cursor iterating over all elements of the bag must be created by
 * calling <tt>cursor()</tt>. The sequence of elements delivered by this
 * cursor has no predefined order. However, a subclass may define such an
 * order.
 *
 * @see Cursor
 * @see Function
 * @see Iterator
 */
public interface Bag {

	/**
	 * A factory method to create a default bag. Each concrete implementation of
	 * bag except for Listbag should have a function FACTORY_METHOD that implements three
	 * variants of <tt>invoke</tt><br>
	 * <ul>
	 * <dl>
	 * <dt><li><tt>Object invoke()</tt>:</dt>
	 * <dd>returns <tt>new Bag()</tt>.</dd>
	 * <dt><li><tt>Object invoke(Object iterator)</tt>:</dt>
	 * <dd>returns <tt>new Bag(iterator)</tt>.</dd>
 	 * <dt><li><tt>Object invoke(Object[] internalDataStructure)</tt>:</dt>
	 * <dd>returns <tt>new Bag((&lt;<i>InternalDataStructure&gt;</i>)internalDataStructure[0])</tt>.</dd>
	 * </dl>
	 * </ul>
	 * 
	 * This factory method creates a new ListBag. It may
	 * be invoked with an array (<i>parameter list</i>) (for further
	 * details see {@link Function}) of lists, an iterator or without any
	 * parameters. An array (<i>parameter list</i>) of lists will be used
	 * to initialize the internally used list with the list at index 0 and
	 * an iterator will be used to insert the contained elements into the
	 * new ListBag.
	 *
	 * @see Function
	 */
	public static final Function FACTORY_METHOD = new Function () {
		public Object invoke () {
			return new ListBag();
		}

		public Object invoke(Object iterator) {
			return new ListBag((Iterator) iterator);
		}

		public Object invoke (Object[] list) {
			return new ListBag((List)list[0]);
		}
	};

	/**
	 * Removes all of the elements from this bag. The bag will be empty
	 * after this call so that <tt>size() == 0</tt>.
	 */
	public abstract void clear ();

	/**
	 * Closes this bag and releases any system resources associated with
	 * it. This operation is idempotent, i.e., multiple calls of this
	 * method take the same effect as a single call. When needed, a
	 * closed bag can be implicit reopened by a consecutive call to one of
	 * its methods. Because of having an unspecified behavior when this
	 * bag is closed every cursor iterating over the elements of this bag
	 * must be closed.<br>
	 * <b>Note:</b> This method is very important for bags using external
	 * resources like files or JDBC resources.
	 */
	public abstract void close ();

	/**
	 * Returns a cursor to iterate over the elements in this bag without
	 * any predefined sequence. The cursor is specifying a <i>view</i> on
	 * the elements of this bag so that closing the cursor takes no
	 * effect on the bag (e.g., not closing the bag). The behavior
	 * of the cursor is unspecified if this bag is modified while the
	 * cursor is in progress in any way other than by calling the methods
	 * of the cursor. So, when the implementation of this cursor cannot
	 * guarantee that the cursor is in a valid state after modifing the
	 * underlying bag every method of the cursor except <tt>close()</tt>
	 * should throw a <tt>ConcurrentModificationException</tt>.
	 *
	 * @return a cursor to iterate over the elements in this bag without
	 *         any predefined sequence.
	 */
	public abstract Cursor cursor ();

	/**
	 * Adds the specified element to this bag. This method does not
	 * perform any kind of <i>duplicate detection</i>.
	 *
	 * @param object element to be added to this bag.
	 */
	public abstract void insert (Object object);

	/**
	 * Adds all of the elements in the specified iterator to this bag.
	 * This method does not perform any kind of <i>duplicate
	 * detection.</i> The behavior of this operation is unspecified if the
	 * specified iterator is modified while the operation is in progress.
	 *
	 * @param objects iterator whose elements are to be added to this bag.
	 */
	public abstract void insertAll (Iterator objects);

	/**
	 * Returns the number of elements in this bag (its cardinality). If
	 * this bag contains more than <tt>Integer.MAX_VALUE</tt> elements,
	 * <tt>Integer.MAX_VALUE</tt> is returned.
	 *
	 * @return the number of elements in this bag (its cardinality).
	 */
	public abstract int size ();

	/**
	 * Returns a cursor to iterate over all elements in this bag for which
	 * the given predicate returns <tt>true</tt>. This method is very
	 * similar to the cursor method except that its result is determined
	 * by a predicate. A possible implementation filters the result of the
	 * cursor method using the following code
	 * <pre>
	 * return new Filter(cursor(), predicate);
	 * </pre>
	 * Note, that this method is implemented in <i>AbstractBag</i>.<br>
	 * The default implementation of this method is not very interesting,
	 * but the method is very import for some bags. When the data
	 * structure that is internally used for storing the elements of this
	 * bag is able to handle with queries, this method can be implemented
	 * very efficient by passing the query to the data structure. For
	 * example a range query on a bag that internally uses a R-tree to
	 * store its elements will be more efficient when it is proceed on the
	 * R-tree itself.<br>
	 * Like the cursor returned by the cursor method, this cursor's
	 * behavior is unspecified if this bag is modified while the cursor is
	 * in progress in any way other than by calling the methods of the
	 * cursor.
	 *
	 * @param predicate a predicate that determines whether an element of
	 *        this bag should be returned or not.
	 * @return a cursor to iterate over all elements in this bag for which
	 *         the given predicate returns <tt>true</tt>.
	 */
	public abstract Cursor query (Predicate predicate);
}
