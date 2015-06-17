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

package xxl.core.collections.containers;

import java.util.Iterator;
import java.util.NoSuchElementException;

import xxl.core.cursors.Cursor;
import xxl.core.cursors.identities.UnmodifiableCursor;
import xxl.core.functions.Function;

/**
 * This class provides a decorator for a given container that cannot be
 * modified. The methods of this class call the corresponding methods of
 * the internally stored container except the methods that modify the bag.
 * These methods (<tt>insert</tt>, <tt>remove</tt>, <tt>update</tt> and
 * <tt>clear</tt>) throws an <tt>UnsupportedOperationException</tt>.<p>
 *
 * Note that the <tt>ids</tt> and <tt>objects</tt> method returns an
 * UnmodifiableCursor.<p>
 *
 * Usage example (1).
 * <pre>
 *     // create a new container
 *
 *     MapContainer inputContainer = new MapContainer();
 *
 *     // create an iteration over 20 random Integers (between 0 and 100)
 *
 *     Iterator iterator = new RandomIntegers(100, 20);
 *
 *     // insert all elements of the given iterator
 *
 *     while (iterator.hasNext())
 *         inputContainer.insert(iterator.next());
 *
 *     // create a new unmodifiable container with the given container
 *
 *     UnmodifiableContainer container = new UnmodifiableContainer(inputContainer);
 *
 *     // generate a cursor that iterates over all elements of the container
 *
 *     Cursor cursor = container.objects();
 *
 *     // print all elements of the cursor (container)
 *
 *     while (cursor.hasNext())
 *         System.out.println(cursor.next());
 *
 *     // close the open iterator, cursor and container after use
 *
 *     ((Cursor)iterator).close();
 *     cursor.close();
 *     container.close();
 * </pre>
 *
 * @see Cursor
 * @see Function
 * @see Iterator
 * @see NoSuchElementException
 * @see UnmodifiableCursor
 */
public class UnmodifiableContainer extends ConstrainedDecoratorContainer {

	/**
	 * A factory method to create a new unmodifiable container. It
	 * may only be invoked with an array (<i>parameter list</i>) (for
	 * further details see Function) of containers. The array
	 * (<i>parameter list</i>) will be used to initialize the decorated
	 * container by calling the constructor
	 * <code>UnmodifiableContainer((Container) array[0])</code>.
	 *
	 * @see Function
	 */
	public static final Function FACTORY_METHOD = new Function() {
		public Object invoke (Object [] array) {
			return new UnmodifiableContainer((Container) array[0]);
		}
	};

	/**
	 * Constructs a new unmodifiable container that decorates the
	 * specified container.
	 *
	 * @param container the container to be decorated.
	 */
	public UnmodifiableContainer (Container container) {
		super(container);
	}

	/**
	 * Removes all elements from the Container. After a call of this
	 * method, <tt>size()</tt> will return 0. This implementation always
	 * throws an <tt>UnsupportedOperationException</tt>.
	 *
	 * @throws UnsupportedOperationException when the method is not
	 *         supported.
	 */
	public void clear () throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Inserts a new object into the container and returns the unique
	 * identifier that the container has been associated to the object.
	 * The identifier can be reused again when the object is deleted from
	 * the buffer. If unfixed, the object can be removed from the buffer.
	 * Otherwise, it has to be kept in the buffer until an
	 * <tt>unfix()</tt> is called.<br>
	 * This implementation always throws an
	 * <tt>UnsupportedOperationException</tt>.
	 *
	 * @param object is the new object.
	 * @param unfix signals whether the object can be removed from the
	 *        underlying buffer.
	 * @return the identifier of the object.
	 * @throws UnsupportedOperationException when the method is not
	 *         supported.
	 */
	public Object insert (Object object, boolean unfix) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes the object with identifier <tt>id</tt>. An exception is
	 * thrown when an object with an identifier <tt>id</tt> is not in the
	 * container.<br>
	 * This implementation always throws an
	 * <tt>UnsupportedOperationException</tt>.
	 *
	 * @param id an identifier of an object.
	 * @throws NoSuchElementException if an object with an identifier
	 *         <tt>id</tt> is not in the container.
	 * @throws UnsupportedOperationException when the method is not
	 *         supported.
	 */
	public void remove (Object id) throws NoSuchElementException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Overwrites an existing (id,*)-element by (id, object). This method
	 * throws an exception if an object with an identifier <tt>id</tt>
	 * does not exist in the container.<br>
	 * This implementation always throws an
	 * <tt>UnsupportedOperationException</tt>.
	 *
	 * @param id identifier of the element.
	 * @param object the new object that should be associated to
	 *        <tt>id</tt>.
	 * @param unfix signals whether the object can be removed from the
	 *        underlying buffer.
	 * @throws NoSuchElementException if an object with an identifier
	 *         <tt>id</tt> does not exist in the container.
	 * @throws UnsupportedOperationException when the method is not
	 *         supported.
	 */
	public void update (Object id, Object object, boolean unfix) throws NoSuchElementException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns an unmodifiable iterator that deliver the identifiers of
	 * all objects of the container.
	 *
	 * @return an unmodifiable iterator of object identifiers.
	 * @see UnmodifiableCursor
	 */
	public Iterator ids () {
		return new UnmodifiableCursor(container.ids());
	}

	/**
	 * Returns an unmodifiable cursor containing all objects of the
	 * container. The order in which the cursor delivers the objects is
	 * not specified.
	 *
	 * @return an unmodifiable cursor containing all objects of the
	 *         container.
	 * @see UnmodifiableCursor
	 */
	public Cursor objects () {
		return new UnmodifiableCursor(container.objects());
	}

	/**
	 * The main method contains some examples how to use an
	 * UnmodifiableContainer. It can also be used to test the
	 * functionality of an UnmodifiableContainer.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create a new container
		MapContainer inputContainer = new MapContainer();
		// create an iteration over 20 random Integers (between 0 and 100)
		Iterator iterator = new xxl.core.cursors.sources.RandomIntegers(100, 20);
		// insert all elements of the given iterator
		while (iterator.hasNext())
			inputContainer.insert(iterator.next());
		// create a new unmodifiable container with the given container
		UnmodifiableContainer container = new UnmodifiableContainer(inputContainer);
		// generate a cursor that iterates over all elements of the container
		Cursor cursor = container.objects();
		// print all elements of the cursor (container)
		while (cursor.hasNext())
			System.out.println(cursor.next());
		System.out.println();
		// close the open iterator, cursor and container after use
		((Cursor)iterator).close();
		cursor.close();
		container.close();
	}
}
