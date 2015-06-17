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

package xxl.core.collections;

import java.util.AbstractList;
import java.util.List;

import xxl.core.functions.Function;

/**
 * The class provides a mapping of a list with a given function. The list
 * to map and the mapping function are internally stored and the indexed
 * access methods <tt>get</tt> and <tt>remove</tt> are implemented. Every
 * method that adds or sets elements throws an
 * UnsupportedOperationException (the element to insert cannot be
 * <i>back-mapped</i>). Everytime one of the implemented methods is called
 * on this list the mapping function is invoked with the corresponding
 * element of the internally used list. The iterators returned by the
 * <tt>iterator</tt> and <tt>listIterator</tt> methods are also mapped.<p>
 *
 * The performance of the mapped list depends on the internally stored
 * list that is specified when the mapped list is constructed.<p>
 *
 * Usage example (1).
 * <pre>
 *     // create a new list
 *
 *     List list = new ArrayList();
 *
 *     // insert the Integers between 0 and 19 into the list
 *
 *     for (int i = 0; i < 20; i++)
 *         list.add(new Integer(i));
 *
 *     // create a function that multplies every odd Integer with 10 and divides every even
 *     // Integer by 2
 *
 *     Function function = new Function() {
 *         public Object invoke(Object o) {
 *             int i = ((Integer)o).intValue();
 *             if (i%2 != 0)
 *                 return new Integer(i*10);
 *             else
 *                 return new Integer(i/2);
 *         }
 *     };
 *
 *     // create a new mapped list that maps the given list with the given function
 *
 *     MappedList mappedList = new MappedList(list, function);
 *
 *     // print all elements of the mapped list
 *
 *     for (int i = 0; i < mappedList.size(); i++)
 *         System.out.println(mappedList.get(i));
 * </pre>
 *
 * @see AbstractList
 * @see Function
 * @see List
 */
public class MappedList extends AbstractList {

	/**
	 * This field is used to store the list to map internally.
	 */
	protected List list;

	/**
	 * This field is used to store the mapping function.
	 */
	protected Function function;

	/**
	 * Constructs a new mapped list that maps the specified list with the
	 * specified function.
	 *
	 * @param list the list to map.
	 * @param function the mapping function.
	 */
	public MappedList (List list, Function function) {
		this.list = list;
		this.function = function;
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return the number of elements in this list.
	 */
	public int size () {
		return list.size();
	}

	/**
	 * Returns the mapped element at the specified position in this list.
	 * This method is equivalent to the call of
	 * <code>function.invoke(list.get(index))</code>.
	 *
	 * @param index index of element to return.
	 * @return the mapped element at the specified position in this list.
	 * @throws UnsupportedOperationException if the remove method is not
	 *         supported by this list.
	 */
	public Object get (int index) throws UnsupportedOperationException {
		return function.invoke(list.get(index));
	}

	/**
	 * Removes the element at the specified position in this list. Shifts
	 * any subsequent elements to the left (subtracts one from their
	 * indices). Returns the element that was removed from the list.
	 * This method is equivalent to the call of
	 * <code>function.invoke(list.remove(index))</code>.
	 *
	 * @param index the index of the element to remove.
	 * @return the mapped element previously at the specified position.
	 * @throws IndexOutOfBoundsException if the <tt>index</tt> is out of
	 *         range (<tt>index < 0 || index >= size()</tt>).
	 */
	public Object remove (int index) throws IndexOutOfBoundsException {
		return function.invoke(list.remove(index));
	}

	/**
	 * The main method contains some examples how to use a MappedList. It
	 * can also be used to test the functionality of a MappedList.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create a new list
		List list = new java.util.ArrayList();
		// insert the Integers between 0 and 19 into the list
		for (int i = 0; i < 20; i++)
			list.add(new Integer(i));
		// create a function that multplies every odd Integer with 10 and
		// divides every even Integer by 2
		Function function = new Function() {
			public Object invoke(Object o) {
				int i = ((Integer)o).intValue();
				if (i%2 != 0)
					return new Integer(i*10);
				else
					return new Integer(i/2);
			}
		};
		// create a new mapped list that mapped the given list with the
		// given function
		MappedList mappedList = new MappedList(list, function);
		// print all elements of the mapped list
		for (int i = 0; i < mappedList.size(); i++)
			System.out.println(mappedList.get(i));
		System.out.println();
	}
}