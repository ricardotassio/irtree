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

/**
 * The class provides a reversed list. The list to reverse is internally
 * stored and the indexed access methods <tt>add</tt>, <tt>get</tt>,
 * <tt>set</tt> and <tt>remove</tt> are implemented. Everytime one of the
 * implemented methods is called on this list the specified index is
 * recalculated and a corresponding method is called on the internally
 * stored list. The iterators returned by the <tt>iterator</tt> and
 * <tt>listIterator</tt> methods are also reversed.<p>
 * 
 * The performance of the reversed list depends on the internally stored
 * list that is specified when the reversed list is constructed.<p>
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
 *     // create a new reversed list with the given list
 * 
 *     ReversedList reversedList = new ReversedList(list);
 * 
 *     // print all elements of the reversed list
 * 
 *     for (int i = 0; i < reversedList.size(); i++)
 *         System.out.println(reversedList.get(i));
 * </pre>
 * 
 * @see AbstractList
 * @see List
 */
public class ReversedList extends AbstractList {
	
	/**
	 * This field is used to store the list to reverse internally.
	 */
	protected List list;
	
	/**
	 * Constructs a new reversed list that reverses the specified list.
	 * 
	 * @param list the list to reverse.
	 */
	public ReversedList (List list) {
		this.list = list;
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
	 * Returns the element at the specified position in this reversed
	 * list. This method is equivalent to the call of
	 * <code>list.get(list.size()-1-index)</code>.
	 * 
	 * @param index index of element to return.
	 * @return the element at the specified position in this reversed
	 *         list.
	 * @throws UnsupportedOperationException if the remove method is not
	 *         supported by this list.
	 */
	public Object get (int index) throws UnsupportedOperationException {
		return list.get(list.size()-1-index);
	}
	
	/**
	 * Replaces the element at the specified position in this reversed
	 * list with the specified element.
	 * 
	 * @param index index of element to replace.
	 * @param object object to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * @throws UnsupportedOperationException if the set method is not
	 *         supported by this list.
	 * @throws ClassCastException if the class of the specified element
	 *         prevents it from being added to this list.
	 * @throws IllegalArgumentException if some aspect of the specified
	 *         element prevents it from being added to this list.
	 * @throws IndexOutOfBoundsException if the <tt>index</tt> is out of
	 *         range (<tt>index < 0 || index >= size()</tt>).
	 */
	public Object set (int index, Object object) throws ClassCastException, IllegalArgumentException, IndexOutOfBoundsException, UnsupportedOperationException {
		return list.set(list.size()-1-index, object);
	}
	
	/**
	 * Inserts the specified element at the specified position in this
	 * reversed list. Shifts the element currently at that position (if
	 * any) and any subsequent elements to the right (adds one to their
	 * indices).
	 * 
	 * @param index index at which the specified element is to be
	 *        inserted.
	 * @param object object to be inserted.
	 * @throws UnsupportedOperationException if the add method is not
	 *         supported by this list.
	 * @throws ClassCastException if the class of the specified element
	 *         prevents it from being added to this list.
	 * @throws IllegalArgumentException if some aspect of this element
	 *         prevents it from being added to this collection.
	 */
	public void add (int index, Object object) throws ClassCastException, IllegalArgumentException, UnsupportedOperationException {
		list.add(list.size()-index, object);
	}
	
	/**
	 * Removes the element at the specified position in this reversed
	 * list. Shifts any subsequent elements to the left (subtracts one
	 * from their indices). Returns the element that was removed from the
	 * list. This method is equivalent to the call of
	 * <code>list.remove(list.size()-1-index)</code>.
	 * 
	 * @param index the index of the element to remove.
	 * @return the element previously at the specified position.
	 * @throws IndexOutOfBoundsException if the <tt>index</tt> is out of
	 *         range (<tt>index < 0 || index >= size()</tt>).
	 */
	public Object remove (int index) throws IndexOutOfBoundsException {
		return list.remove(list.size()-1-index);
	}
	
	/**
	 * The main method contains some examples how to use a ReversedList.
	 * It can also be used to test the functionality of a ReversedList.
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
		// create a new reversed list with the given list
		ReversedList reversedList = new ReversedList(list);
		// print all elements of the reversed list
		for (int i = 0; i < reversedList.size(); i++)
			System.out.println(reversedList.get(i));
		System.out.println();
	}
}