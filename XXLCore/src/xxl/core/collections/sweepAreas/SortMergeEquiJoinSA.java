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

import xxl.core.cursors.filters.Remover;
import xxl.core.cursors.sources.EmptyCursor;
import xxl.core.predicates.Equal;
import xxl.core.predicates.Predicate;

/**
 * A SweepArea for sort-based equijoins. In this special case of a join, the retrieval as well as the 
 * reorganization and expiration, respectively, can be improved with regard to efficiency. 
 * Querying a SortMergeEquiJoinSA simply returns an iterator over the complete 
 * SweepArea, whereas expiration and reorganization benefits from the fact that all elements 
 * not equal to the passed argument <code>currentStatus</code> can be removed from the SortMergeEquiJoinSA 
 * instantaneously.
 *  
 * @see xxl.core.cursors.joins.SortMergeEquivalenceJoin
 * @see xxl.core.predicates.Predicate
 * @see xxl.core.predicates.Equal
 */
public class SortMergeEquiJoinSA extends SweepArea {

	/**
	 * Binary predicate that decides if its
	 * two arguments are equal.
	 */
	protected Predicate equals;

	/**
	 * Constructs a new SortMergeEquiJoinSA with the specified query-predicate <code>equals</code>.
	 * SortMergeEquiJoinSAs permit self-reorganization.
	 * 
	 * @param impl The underlying implementor.
	 * @param ID The ID of this SortMergeEquiJoinSA.
	 * @param dim The dimensionality of this SweepArea, i.e., the number of possible inputs or in other words
	 * 		  the number of different IDs that can be passed to method calls of this SortMergeEquiJoinSA. 
	 * @param equals Binary predicate that decides if its two arguments are equal.
	 */
	public SortMergeEquiJoinSA(SweepAreaImplementor impl, int ID, int dim, Predicate equals) {
		super(impl, ID, true, null, null, dim);
		this.equals = equals;
	}

	/**
	 * Constructs a new SortMergeEquiJoinSA using {@link xxl.core.predicates.Equal#DEFAULT_INSTANCE} as
	 * query-predicate. SortMergeEquiJoinSAs permit self-reorganization.
	 * 
	 * @param impl The underlying implementor.
	 * @param ID The ID of this SortMergeEquiJoinSA.
	 * @param dim The dimensionality of this SweepArea, i.e., the number of possible inputs or in other words
	 * 		  the number of different IDs that can be passed to method calls of this SortMergeEquiJoinSA. 
	 */
	public SortMergeEquiJoinSA(SweepAreaImplementor impl, int ID, int dim) {
		this(impl, ID, dim, Equal.DEFAULT_INSTANCE);
	}

	/**
	 * Returns an iterator over the complete SortMergeEquiJoinSA by
	 * calling the method {@link #iterator()}.
	 * 
	 * @param o The query object. This object is typically probed against
	 * 		the elements contained in this SortMergeEquiJoinSA.
	 * @param ID An ID determining from which input this method
	 * 		is triggered.
	 * @return All elements of this SortMergeEquiJoinSA are returned as an iterator. 
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong due to the passed arguments during retrieval.
	 */
	public Iterator query(Object o, int ID) throws IllegalArgumentException {
		return impl.iterator();
	}

	/**
	 * Returns an iterator over the complete SortMergeEquiJoinSA by
	 * calling the method {@link #iterator()}. This version of 
	 * query additionally allows to use partially
	 * filled arrays and specifies how many entries of such a partially
	 * filled array are valid.
	 * 
	 * @param os The query objects. These objects are typically probed against
	 * 		the elements contained in this SortMergeEquiJoinSA.
	 * @param IDs IDs determining from which input the query objects come from.
	 * @param valid Determines how many entries at the beginning of
	 *        <tt>os</tt> and <tt>IDs</tt> are valid and therefore taken into account.
	 * @return All elements of this SortMergeEquiJoinSA are returned as an iterator. 
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong due to the passed arguments during retrieval.
	 */
	public Iterator query(Object[] os, int[] IDs, int valid) throws IllegalArgumentException {
		return impl.iterator();
	}

	/**
	 * Returns an iterator over the complete SortMergeEquiJoinSA by
	 * calling the method {@link #iterator()}.  
	 *  
	 * @param os The query objects. These objects are typically probed against
	 * 		the elements contained in this SortMergeEquiJoinSA.
	 * @param IDs IDs determining from which input the query objects come from.
	 * @return All elements of this SortMergeEquiJoinSA are returned as an iterator. 
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong due to the passed arguments during retrieval.
	 */
	public Iterator query(Object[] os, int[] IDs) throws IllegalArgumentException {
		return impl.iterator();
	}

	/**
	 * In an SortMergeEquiJoin all elements that are not equal
	 * to <code>currentStatus</code> can be removed from this
	 * SortMergeEquiJoinSA. Therefore, this method
	 * checks if this SortMergeEquiJoinSA contains any
	 * element or the first element is equal to <code>currentStatus</code>.
	 * In both cases, an empty cursor is returned. Otherwise,
	 * all elements of this SortMergeEquiJoinSA are returned
	 * as a cursor and removed during traversal.
	 * 
	 * @param currentStatus The object containing the necessary information
	 * 		to detect expired elements.
	 * @param ID An ID determining from which input this method
	 * 		is triggered.
	 * @return an iteration over the elements which expire with respect to the
	 *         object <tt>currentStatus</tt> and an <tt>ID</tt>.
	 * @throws IllegalStateException Throws an IllegalStateException if
	 * 		   this method is called at an invalid state.
	 */
	public Iterator expire (Object currentStatus, int ID) throws IllegalStateException {
		Iterator it = impl.iterator();
		if (!it.hasNext() || equals.invoke(it.next(), currentStatus)) 
			return EmptyCursor.DEFAULT_INSTANCE;
		return new Remover(iterator());
	}
	
	/**
	 * In contrast to the method {@link #expire(Object, int)}, this method 
	 * directly removes all expired elements from a SweepArea 
	 * without returning them. Consequently, this SortMergeEquiJoinSA
	 * is cleared whenever <code>currentStatus</code> is not 
	 * equal to the first element of this SortMergeEquiJoinSA.
	 * 
	 * @param currentStatus The object containing the necessary information
	 * 		  to perform the reorganization step.
	 * @param ID An ID determining from which input this reorganization step
	 * 		   is triggered.
	 * @throws IllegalStateException Throws an IllegalStateException if
	 * 		   this method is called at an invalid state.
	 */
	public void reorganize (Object currentStatus, int ID) throws IllegalStateException {
		Iterator it = impl.iterator();
		if (!it.hasNext() || equals.invoke(it.next(), currentStatus)) 
			return;
		clear();
	}

}
