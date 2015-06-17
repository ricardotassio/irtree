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

import xxl.core.collections.bags.Bag;
import xxl.core.collections.bags.ListBag;
import xxl.core.cursors.sources.EmptyCursor;
import xxl.core.predicates.Equal;
import xxl.core.predicates.LeftBind;

/**
 * An implementation of the interface {@link SweepAreaImplementor}
 * based on the interface {@link xxl.core.collections.bags.Bag Bag}.
 * 
 * @see SweepAreaImplementor
 * @see xxl.core.collections.bags.Bag 
 */
public class BagSAImplementor extends AbstractSAImplementor {

	/**
	 * The bag storing the elements.
	 */
	protected Bag bag;
	
	/**
	 * Constructs a new BagSAImplementor.
	 * 
	 * @param bag The underlying bag.
	 */
	public BagSAImplementor(Bag bag) {
		this.bag = bag;
	}

	/**
	 * Constructs a new BagSAImplementor
	 * based on a {@link xxl.core.collections.bags.ListBag ListBag}.
	 */
	public BagSAImplementor() {
		this(new ListBag());
	}
	
	/**
	 * Inserts the given element into the bag.
	 * 
	 * @param o The object to be inserted.
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong with the insertion due to the passed argument.
	 */
	public void insert (Object o) throws IllegalArgumentException {
		bag.insert(o);
	}
	
	/**
	 * Removes the specified element from the bag.
	 * This is achieved by querying the bag for <code>o</code>
	 * and calling <tt>remove()</tt> if the cursor returned
	 * by the query contains an element. Otherwise, <tt>false</tt>
	 * is returned. 
	 * 
	 * @param o The object to be removed.
	 * @return <tt>True</tt> if the removal has been successful, otherwise <tt>false</tt>.
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong with the removal due to the passed argument.
	 */
	public boolean remove(Object o) throws IllegalArgumentException {
		Iterator it = bag.query(new LeftBind(Equal.DEFAULT_INSTANCE, o));
		if (it.hasNext()) {
			it.next();
			it.remove();
			return true;
		}
		return false;
	}

	/**
	 * Returns the size of this implementor, i.e.,
	 * the size of the bag.
	 * 
	 * @return The size.
	 */
	public int size () {
		return bag.size();
	}

	/**
	 * Clears this implementor, i.e., 
	 * clears the bag.
	 */
	public void clear() {
		bag.clear();
	}

	/**
	 * Closes this implementor, i.e.,
	 * closes the bag.
	 */
	public void close() {
		bag.close();
	}

	/**
	 * Returns a cursor over the elements of the bag.
	 * 
	 * @return A cursor over the elements of the bag.
	 */
	public Iterator iterator() {
		return bag.cursor();
	}
	
	/**
	 * Queries this implementor with the help of the
	 * specified query object <code>o</code> and the query-predicates
	 * set during initialization, see method
	 * {@link #initialize(int, xxl.core.predicates.Predicate[])}. 
	 * If the bag is empty, an empty cursor is returned. Otherwise,
	 * the bag is filtered by calling 
	 * <code>filter(bag.cursor(), o, ID)</code> which produces
	 * a cursor delivering all matching elements.
	 *  <br>
	 * <i>Note:</i>
	 * The returned iterator should not be used to remove any elements 
	 * from this implementor!
	 * 
	 * @param o The query object. This object is typically probed against
	 * 		the elements contained in this implementor.
	 * @param ID An ID determining from which input this method
	 * 		is triggered.
	 * @return All matching elements of this implementor are returned as an iterator. 
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong due to the passed arguments during retrieval.
	 * @see #filter(Iterator, Object, int)
	 */
	public Iterator query (Object o, int ID) {
		if (bag.size()==0) return EmptyCursor.DEFAULT_INSTANCE;
		return filter(bag.cursor(), o, ID);
	}

}
