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

import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.functions.Function;

/**
 * This class provides an implementation of the Bag interface that
 * internally uses an array with fixed size to store its elements. <p>
 *
 * Each ArrayBag instance has a fixed capacity. The capacity is the size
 * of the array used to store the elements in the bag. It is always at
 * least as large as the bag size.<p>
 *
 * The cursors returned by this class' <tt>cursor</tt>,
 * <tt>fifoCursor</tt>, <tt>lifoCursor</tt> and <tt>query</tt> methods are
 * <i>fail-fast</i>: if the bag is structurally modified at any time after
 * the cursor is created, in any way except through the cursor's own
 * methods, the cursor will throw a ConcurrentModificationException. Thus,
 * in the face of concurrent modification, the cursor fails quickly and
 * cleanly, rather than risking arbitrary, non-deterministic behavior at
 * an undetermined time in the future.<p>
 *
 * Usage example (1).
 * <pre>
 *     // create a new array bag
 *
 *     ArrayBag bag = new ArrayBag();
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
 *     // create a new array bag that contains all elements of the given iterator
 *
 *     bag = new ArrayBag(Cursors.toArray(iterator));
 *
 *     // create a cursor that iterates over the elements of the bag
 *
 *     cursor = bag.cursor();
 *
 *     // remove every even Integer from the cursor (and the underlying array bag)
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
 * @see AbstractCursor
 * @see Cursor
 * @see Function
 */
public class ArrayBag extends AbstractBag implements FIFOBag, LIFOBag {

	/**
	 * A factory method to create a new ArrayBag (see contract for
	 * {@link Bag#FACTORY_METHOD FACTORY_METHOD} in interface Bag). In
	 * contradiction to the contract in Bag it may only be invoked with an
	 * array (<i>parameter list</i>) (for further details see Function) of
	 * object arrays. The array (<i>parameter list</i>) of object arrays
	 * will be used to initialize the internally used array with the
	 * object array at index 0.
	 *
	 * @see xxl.core.functions.Function
	 */
	public static final Function FACTORY_METHOD = new Function () {
		public Object invoke (Object[] array) {
			return new ArrayBag((Object[]) array[0]);
		}
	};

	/**
	 * The array is internally used to store the elements of the bag.
	 * Before an insertion and after the removal of an element this array
	 * is automatically resized by an ArrayResizer.
	 */
	protected Object [] array;

	/**
	 * An <tt>int</tt> field storing the number of elements in this bag.
	 */
	protected int last = -1;

	/**
	 * Constructs an array bag containing the elements of the specified
	 * array. The specified array has two different functions. First the
	 * bag depends on this array and is not able to contain more elements
	 * than the array is able to. Second it is used to initialize the bag.
	 * The size arguments only specifies the number of elements of the
	 * specified array which should be used to initialize the bag.
	 * The field <tt>array</tt> is set to the specified array and the
	 * field <tt>last</tt> is set to the specified size - 1.
	 *
	 * @param size the number of elements of the specified array which
	 *        should be used to initialize the bag.
	 * @param array the object array that is used to store the bag and
	 *        initialize the internally used array.
	 * @throws IllegalArgumentException if the specified size argument is
	 *         negative, or if it is greater than the length of the
	 *         specified array.
	 */
	public ArrayBag (int size, Object[] array) {
		if (array.length < size || size < 0)
			throw new IllegalArgumentException();
		this.array = array;
		last = size-1;
	}
	
	/**
	 * Constructs an array bag containing the elements of the specified
	 * array. This constructor is equivalent to the call of
	 * <code>ArrayBag(array, array.length)</code>.
	 *
	 * @param array the object array that is used to store the bag and
	 *        initialize the internally used array.
	 */
	public ArrayBag (Object[] array) {
		this(array.length, array);
	}

	/**
	 * Constructs an empty array bag with a capacity of <tt>size</tt>
	 * elements. This constructor is equivalent to the call of
	 * <code>ArrayBag(0, new Object[size])</code>.
	 *
	 * @param size the maximal number of elements the bag is able to
	 *        contain.
	 */
	public ArrayBag (int size) {
		this(0, new Object[size]);
	}

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
	public Cursor cursor () {
		return new AbstractCursor() {
			int pos;

			{
				reset();
			}

			public void reset(){
				super.reset();
				pos = 0;
			}
			
			public boolean supportsReset() {
				return true;
			}

			public boolean hasNextObject() {
				return pos <= last;
			}

			public Object nextObject() {
				return array[pos++];
			}

			public void remove () throws IllegalStateException {
				super.remove();
				System.arraycopy(array, pos, array, pos-1, (last--)+1-(pos--));
			}
			
			public boolean supportsRemove() {
				return true;
			}

			public void update (Object object) throws IllegalStateException {
				super.update(object);
				array[pos-1] = object;
			}
			
			public boolean supportsUpdate() {
				return true;
			}
		};
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
		return new AbstractCursor() {
			int pos;
			
			{
				reset();
			}

			public void reset(){
				super.reset();
				pos = last;
			}
			
			public boolean supportsReset() {
				return true;
			}

			public boolean hasNextObject() {
				return pos >= 0;
			}
			
			public Object nextObject() {
				return array[pos--];
			}

			public void remove () throws IllegalStateException {
				super.remove();
				//System.arraycopy(array, pos+2, array, pos+1, (--last)-pos); //DEBUG
				last--; //DEBUG: NEW: System.arraycopy ist sehr teuer!
			}
			
			public boolean supportsRemove() {
				return true;
			}

			public void update (Object object) throws IllegalStateException {
				super.update(object);
				array[pos+1] = object;
			}
			
			public boolean supportsUpdate() {
				return true;
			}
		};
	}

	/**
	 * Adds the specified element to this bag. This method does not
	 * perform any kind of <i>duplicate detection</i>.
	 *
	 * @param object element to be added to this bag.
	 */
	public void insert (Object object) {
		array[++last] = object;
	}

	/**
	 * Returns the number of elements in this bag (its cardinality). If
	 * this bag contains more than <tt>Integer.MAX_VALUE</tt> elements,
	 * <tt>Integer.MAX_VALUE</tt> is returned.
	 *
	 * @return the number of elements in this bag (its cardinality).
	 */
	public int size () {
		return last+1;
	}

	/**
	 * Removes all of the elements from this bag. The bag will be empty
	 * after this call so that <tt>size() == 0</tt>.<br>
	 * This implementation creates a cursor by calling the cursor method
	 * and iterates over it, removing each element using the
	 * {@link Cursor#remove() Cursor.remove} operation. Most
	 * implementations will probably choose to override this method for
	 * efficiency.
	 */
	public void clear () {
		last = -1;
	}

	/**
	 * The main method contains some examples how to use an ArrayBag. It
	 * can also be used to test the functionality of an ArrayBag.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create a new array bag
		ArrayBag bag = new ArrayBag(20);
		// create an iteration over 20 random Integers (between 0 and 100)
		java.util.Iterator iterator = new xxl.core.cursors.sources.RandomIntegers(100, 20);
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
		// create a new array bag that contains all elements of the given
		// iterator
		bag = new ArrayBag(xxl.core.cursors.Cursors.toArray(iterator));
		// create a cursor that iterates over the elements of the bag
		cursor = bag.cursor();
		// remove every even Integer from the cursor (and the underlying
		// array bag)
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
	}
}
