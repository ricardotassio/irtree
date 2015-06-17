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
import java.util.LinkedList;
import java.util.List;

import xxl.core.collections.ReversedList;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.wrappers.IteratorCursor;
import xxl.core.functions.Function;

/**
 * This class provides an implementation of the Bag interface that
 * internally uses a list to store its elements. <p>
 *
 * The performance of the bag depends on the performance of the internally
 * used list (e.g., an ArrayList guaratees for insertion an amortized
 * constant time, that is, adding n elements requires O(n) time).<p>
 *
 * The cursors returned by this class' <tt>cursor</tt> and <tt>query</tt>
 * methods are <i>fail-fast</i>: if the bag is structurally modified at
 * any time after the cursor is created, in any way except through the
 * cursor's own methods, the cursor will throw a
 * ConcurrentModificationException. Thus, in the face of concurrent
 * modification, the cursor fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.<p>
 *
 * Usage example (1).
 * <pre>
 *     // create a new list bag (that uses a linked list to store its elements per default)
 *
 *     ListBag bag = new ListBag();
 *
 *     // create an iteration over 20 random Integers (between 0 and 100)
 *
 *     Iterator iterator = new RandomIntegers(100, 20);
 *
 *     // insert all elements of the given iterator
 *
 *     bag.insertAll(iterator);
 *
 *     // create a cursor that iterates over the elements of the bag
 *
 *     Cursor cursor = bag.cursor();
 *
 *     // print all elements of the cursor (bag)
 *
 *     while (cursor.hasNext())
 *         System.out.println(cursor.next());
 *
 *     // close the open iterator, cursor and bag after use
 *
 *     ((Cursor)iterator).close();
 *     cursor.close();
 *     bag.close();
 * </pre>
 *
 * Usage example (2).
 * <pre>
 *     // create an iteration over the Integer between 0 and 19
 *
 *     iterator = new Enumerator(20);
 *
 *     // create a new list bag (that uses a linked list to store its elements per default) that
 *     // contains all elements of the given iterator
 *
 *     bag = new ListBag(iterator);
 *
 *     // create a cursor that iterates over the elements of the bag
 *
 *     cursor = bag.cursor();
 *
 *     // remove every even Integer from the cursor (and the underlying list bag)
 *
 *     while (cursor.hasNext()) {
 *         int i = ((Integer)cursor.next()).intValue();
 *         if (i%2 == 0)
 *             cursor.remove();
 *     }
 *
 *     // create a cursor that iterates over the elements of the bag
 *
 *     cursor = bag.cursor();
 *
 *     // print all elements of the cursor (bag)
 *
 *     while (cursor.hasNext())
 *         System.out.println(cursor.next());
 *
 *     // close the open iterator, cursor and bag after use
 *
 *     ((Cursor)iterator).close();
 *     cursor.close();
 *     bag.close();
 * </pre>
 *
 * Usage example (3).
 * <pre>
 *     // create a new list bag that uses an array list to store its elements
 *
 *     bag = new ListBag(new ArrayList());
 *
 *     // create an iteration over 20 random Integers (between 0 and 100)
 *
 *     iterator = new RandomIntegers(100, 20);
 *
 *     // insert all elements of the given iterator
 *
 *     bag.insertAll(iterator);
 *
 *     // create a cursor that iterates over the elements of the bag
 *
 *     cursor = bag.cursor();
 *
 *     // print all elements of the cursor (bag)
 *
 *     while (cursor.hasNext())
 *         System.out.println(cursor.next());
 *
 *     // close the open iterator, cursor and bag after use
 *
 *     ((Cursor)iterator).close();
 *     cursor.close();
 *     bag.close();
 * </pre>
 *
 * @see Cursor
 * @see Iterator
 * @see IteratorCursor
 * @see Function
 * @see LinkedList
 * @see List
 */
public class ListBag extends AbstractBag implements FIFOBag, LIFOBag {

	/**
	 * The list is internally used to store the elements of the bag.
	 */
	protected List list;

	/**
	 * Constructs a bag containing the elements of the list. The specified
	 * list is internally used to store the elements of the bag.
	 *
	 * @param list the list that is used to initialize the internally used
	 *        list.
	 */
	public ListBag (List list) {
		this.list = list;
	}

	/**
	 * Constructs an empty bag. This bag instantiates a new LinkedList in
	 * order to store its elements. This constructor is equivalent to the
	 * call of <code>ListBag(new LinkedList())</code>.
	 */
	public ListBag () {
		this(new LinkedList());
	}

	/**
	 * Constructs a bag containing the elements of the list and the
	 * specified iterator. For the most cases the list is used to
	 * initialize the internally used list in order to guartantee a
	 * desired performance. This constructor calls the constructor with
	 * the specified list and uses the insertAll method to insert the
	 * elements of the specified iterator.
	 *
	 * @param list the list that is used to initialize the internally used
	 *        list.
	 * @param iterator the iterator whose elements are to be placed into
	 *        this bag.
	 */
	public ListBag(List list, Iterator iterator){
		this(list);
		insertAll(iterator);
	}

	/**
	 * Constructs a bag containing the elements of the specified iterator.
	 * This bag instantiates a new LinkedList in order to store its
	 * elements. This constructor is equivalent to the call of
	 * <code>ListBag(new LinkedList(), iterator)</code>.
	 *
	 * @param iterator the iterator whose elements are to be placed into
	 *        this bag.
	 */
	public ListBag(Iterator iterator){
		this(new LinkedList(), iterator);
	}

	/**
	 * Removes all elements from this bag. The bag will be empty
	 * after this call so that <tt>size() == 0</tt>.
	 */
	public void clear () {
		list.clear();
	}

	/**
	 * Returns a cursor to iterate over the elements in this bag without
	 * any predefined sequence. The cursor is specifying a <i>view</i> on
	 * the elements of this bag so that closing the cursor takes no
	 * effect on the bag (e.g., not closing the bag). The behavior
	 * of the cursor is unspecified if this bag is modified while the
	 * cursor is in progress in any way other than by calling the methods
	 * of the cursor. In this case every method of the cursor except
	 * <tt>close()</tt> throws a <tt>ConcurrentModificationException</tt>.
	 *
	 * @return a cursor to iterate over the elements in this bag without
	 *         any predefined sequence.
	 */
	public Cursor cursor () {
		return new IteratorCursor(list.iterator());
	}

	/**
	 * Returns a cursor representing a FIFO (<i>first in, first out</i>)
	 * iteration over the elements in this bag. The cursor is specifying
	 * a <i>view</i> on the elements of this bag so that closing the
	 * cursor takes no effect on the bag (e.g., not closing the bag). The
	 * behavior of the cursor is unspecified if this bag is modified while
	 * the cursor is in progress in any way other than by calling the
	 * methods of the cursor. So, when the implementation of this cursor
	 * cannot guarantee that the cursor is in a valid state after modifing
	 * the underlying bag every method of the cursor except
	 * <tt>close()</tt> should throw a
	 * <tt>ConcurrentModificationException</tt>.
	 *
	 * @return a cursor representing a FIFO (<i>first in, first out</i>)
	 *         iteration over the elements in this bag.
	 */
	public Cursor fifoCursor () {
		return cursor();
	}

	/**
	 * Returns a cursor representing a LIFO (<i>last in, first out</i>)
	 * iteration over the elements in this bag. The cursor is specifying
	 * a <i>view</i> on the elements of this bag so that closing the
	 * cursor takes no effect on the bag (e.g., not closing the bag). The
	 * behavior of the cursor is unspecified if this bag is modified while
	 * the cursor is in progress in any way other than by calling the
	 * methods of the cursor. So, when the implementation of this cursor
	 * cannot guarantee that the cursor is in a valid state after modifing
	 * the underlying bag every method of the cursor except
	 * <tt>close()</tt> should throw a
	 * <tt>ConcurrentModificationException</tt>.
	 *
	 * @return a cursor representing a LIFO (<i>last in, first out</i>)
	 *         iteration over the elements in this bag.
	 */
	public Cursor lifoCursor () {
		return new IteratorCursor(new ReversedList(list).iterator());
	}
	
	/**
	 * Adds the specified element to this bag. This method does not
	 * perform any kind of <i>duplicate detection</i>.
	 *
	 * @param object element to be added to this bag.
	 */
	public void insert (Object object) {
		list.add(object);
	}

	/**
	 * Returns the number of elements in this bag (its cardinality). If
	 * this bag contains more than <tt>Integer.MAX_VALUE</tt> elements,
	 * <tt>Integer.MAX_VALUE</tt> is returned.
	 *
	 * @return the number of elements in this bag (its cardinality).
	 */
	public int size () {
		return list.size();
	}

	/**
	 * The main method contains some examples how to use a ListBag. It can
	 * also be used to test the functionality of a ListBag.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create a new list bag (that uses a linked list to store its
		// elements per default)
		ListBag bag = new ListBag();
		// create an iteration over 20 random Integers (between 0 and 100)
		Iterator iterator = new xxl.core.cursors.sources.RandomIntegers(100, 20);
		// insert all elements of the given iterator
		bag.insertAll(iterator);
		// create a cursor that iterates over the elements of the bag
		Cursor cursor = bag.cursor();
		// print all elements of the cursor (bag)
		while (cursor.hasNext())
			System.out.println(cursor.next());
		System.out.println();
		// close the open iterator, cursor and bag after use
		((Cursor)iterator).close();
		cursor.close();
		bag.close();

		//////////////////////////////////////////////////////////////////
		//                      Usage example (2).                      //
		//////////////////////////////////////////////////////////////////

		// create an iteration over the Integer between 0 and 19
		iterator = new xxl.core.cursors.sources.Enumerator(20);
		// create a new list bag (that uses a linked list to store its
		// elements per default) that contains all elements of the given
		// iterator
		bag = new ListBag(iterator);
		// create a cursor that iterates over the elements of the bag
		cursor = bag.cursor();
		// remove every even Integer from the cursor (and the underlying
		// list bag)
		while (cursor.hasNext()) {
			int i = ((Integer)cursor.next()).intValue();
			if (i%2 == 0)
				cursor.remove();
		}
		// create a cursor that iterates over the elements of the bag
		cursor = bag.cursor();
		// print all elements of the cursor (bag)
		while (cursor.hasNext())
			System.out.println(cursor.next());
		System.out.println();
		// close the open iterator, cursor and bag after use
		((Cursor)iterator).close();
		cursor.close();
		bag.close();

		//////////////////////////////////////////////////////////////////
		//                      Usage example (3).                      //
		//////////////////////////////////////////////////////////////////

		// create a new list bag that uses an array list to store its
		// elements
		bag = new ListBag(new java.util.ArrayList());
		// create an iteration over 20 random Integers (between 0 and 100)
		iterator = new xxl.core.cursors.sources.RandomIntegers(100, 20);
		// insert all elements of the given iterator
		bag.insertAll(iterator);
		// create a cursor that iterates over the elements of the bag
		cursor = bag.cursor();
		// print all elements of the cursor (bag)
		while (cursor.hasNext())
			System.out.println(cursor.next());
		System.out.println();
		// close the open iterator, cursor and bag after use
		((Cursor)iterator).close();
		cursor.close();
		bag.close();
	}
}