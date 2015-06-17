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
package xxl.core.collections.sweepAreas;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import xxl.core.cursors.sources.EmptyCursor;

/**
 * A list-based implementation of the interface
 * {@link SweepAreaImplementor}.
 * 
 * @see SweepAreaImplementor
 * @see java.util.Iterator
 * @see java.util.List
 */
public class ListSAImplementor extends AbstractSAImplementor {

	/**
	 * The list storing the elements.
	 */
	protected List list;
	
	/**
	 * Constructs a new ListSAImplementor.
	 * 
	 * @param list The list used to store the elements.
	 */
	public ListSAImplementor(List list) {
		this.list = list;
	}

	/**
	 * Constructs a new ListSAImplementor based
	 * on a LinkedList.
	 */
	public ListSAImplementor() {
		this(new LinkedList());
	}
	
	/**
	 * Appends the given element to the list.
	 * 
	 * @param o The object to be inserted.
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong with the insertion due to the passed argument.
	 */
	public void insert (Object o) throws IllegalArgumentException {
		list.add(list.size(), o);
	}
	
	/**
	 * Removes the specified element from the list.
	 * 
	 * @param o The object to be removed.
	 * @return <tt>True</tt> if the removal has been successful, otherwise <tt>false</tt>.
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong with the removal due to the passed argument.
	 */
	public boolean remove(Object o) throws IllegalArgumentException {
		return list.remove(o);
	}
	
	/**
	 * Checks if element <tt>o1</tt> is contained in the list and 
	 * if <tt>true</tt> replaces it by </tt>o2</tt>. 
	 * 
	 * @param o1 The object to be replaced.
	 * @param o2 The new object.
	 * @return The updated object is returned.
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong with the update operation due to the passed arguments.
	 */
	public Object update(Object o1, Object o2) throws IllegalArgumentException {
		return list.set(list.indexOf(o1), o2);
	}

	/**
	 * Returns the size of the list.
	 * 
	 * @return The size of the list.
	 */
	public int size () {
		return list.size();
	}

	/**
	 * Clears the list.
	 */
	public void clear() {
		list.clear();
	}

	/**
	 * Clears the list.
	 */
	public void close() {
		clear();
	}

	/**
	 * Returns an iterator over the elements of the list.
	 * 
	 * @return An iterator over the elements of the list.
	 */	
	public Iterator iterator() {
		return list.iterator();
	}
	
	/**
	 * Queries the list by performing a sequential scan.
	 * Returns all elements that match with the given 
	 * element <tt>o</tt> according to the user-defined
	 * binary query-predicate. The query-predicates are 
	 * set via the
	 * {@link #initialize(int, xxl.core.predicates.Predicate[])} 
	 * method, which is typically called inside the
	 * constructor of a SweepArea. <br>
	 * <i>Note:</i>
	 * The returned iterator should not be used to remove any 
	 * elements from this implementor!
	 *  
	 * @param o The query object.
	 * @param ID An ID determining from which input this method
	 * 		is triggered.
	 * @return An iterator delivering all matching elements.
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong with the query operation due to the passed argument.
	 * @see #filter(Iterator, Object, int)
	 */	
	public Iterator query (final Object o, final int ID) throws IllegalArgumentException {
		if (list.size()==0) 
			return EmptyCursor.DEFAULT_INSTANCE;
		return filter(list.iterator(), o, ID);	
	}

}
