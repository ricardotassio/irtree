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

import java.util.Arrays;
import java.util.Iterator;

import xxl.core.cursors.Cursors;
import xxl.core.cursors.filters.Filter;
import xxl.core.cursors.filters.Remover;
import xxl.core.cursors.sources.EmptyCursor;
import xxl.core.predicates.Equal;
import xxl.core.predicates.Not;
import xxl.core.predicates.Predicate;
import xxl.core.predicates.Predicates;
import xxl.core.predicates.RightBind;

/**
 * A SweepArea is a highly dynamic datastructure with flexible
 * insertion, retrieval and reorganization capabilities. It is 
 * utilized to remain the state of an operation. <br>
 * Its implementation relies on the design pattern <i>Bridge</i>
 * with the intention to "decouple an abstraction from its 
 * implementation so that the two can vary independently". 
 * For further information see: "Gamma et al.: <i>DesignPatterns.
 * Elements of Reusable Object-Oriented Software.</i> Addision 
 * Wesley 1998."
 * 
 * @see SweepAreaImplementor
 * @see xxl.core.cursors.joins.SortMergeJoin
 * @see xxl.core.cursors.joins.MultiWaySortMergeJoin
 * @see xxl.core.predicates.Predicate
 * @see xxl.core.predicates.RightBind
 * @see java.util.Iterator
 */
public class SweepArea {
	
	/**
	 * A default ID for a SweepArea. This is typically
	 * used in unary operations.
	 */
	public static final short DEFAULT_ID = 0;

	/**
	 * The underlying implementor.
	 */
	protected final SweepAreaImplementor impl;
	
	/**
	 * The ID of this SweepArea.
	 */
	protected final int ID;
	
	/**
	 * This flag determines if this SweepArea
	 * can reorganize itself. To trigger this event, the
	 * <tt>ID</tt> passed to the reorganize call
	 * has to be identical with the internal ID of this 
	 * SweepArea.
	 */
	protected final boolean selfReorganize;
	
	/**
	 * Binary predicates used to query this SweepArea and 
	 * its underlying SweepAreaImplementor, respectively.
	 * To offer a retrieval depending on the ID passed
	 * to the query calls, a SweepArea requires such an 
	 * array of predicates.
	 */
	protected final Predicate[] queryPredicates; 
	
	/**
	 * Binary predicates determining if an element of a 
	 * SweepArea and its underlying SweepAreaImplementor, 
	 * respectively, can be removed. 
	 * To offer a removal depending on the ID passed
	 * to the reorganization or expiration calls, a SweepArea 
	 * requires such an array of predicates. <br>
	 */
	protected final Predicate[] removePredicates;
	
	/**
	 * The default implementation removes all expired
	 * elements from a SweepArea by sequentially iterating
	 * over all elements. The iteration is performed by
	 * a {@link xxl.core.cursors.filters.Filter Filter} applying
	 * an unary selection predicate. For this purpose,
	 * the constructors of this class wrap the array
	 * of binary remove-predicates to an array of unary
	 * remove-predicates <code>removeRightBinds</code>.
	 * The element passed to the reorganization and
	 * expiration, respectively, is implicitly set as
	 * right argument. 
	 */
	protected final RightBind[] removeRightBinds;
	
	/**
	 * Constructs a new SweepArea.
	 * 
	 * @param impl The underlying implementor.
	 * @param ID The ID of this SweepArea.
	 * @param selfReorganize A flag to determine if this SweepArea can reorganize itself.
	 * @param queryPredicates An array of binary query-predicates used to probe this SweepArea. 
	 * 		  Depending on the ID passed to the query-call, different query-predicates can be chosen
	 * 		  for retrieval.
	 * @param removePredicates An array of binary remove-predicates utilized during the reorganization.
	 * 		  With the help of this predicates, the elements of a SweepArea are probed for expiration.
	 * 		  In analogy to the query-predicates, the predicate actually applied to this SweepArea 
	 * 		  depends on the ID passed to the expiration and reorganization call, respectively.
	 */
	public SweepArea(SweepAreaImplementor impl, int ID, boolean selfReorganize, Predicate[] queryPredicates, Predicate[] removePredicates) {
		this.impl = impl;
		this.ID = ID;
		this.selfReorganize = selfReorganize;
		this.queryPredicates = queryPredicates;
		this.removePredicates = removePredicates;
		this.removeRightBinds = new RightBind[removePredicates.length];
		for (int i = 0; i < removePredicates.length; i++)
			this.removeRightBinds[i] = new RightBind(removePredicates[i], null);
		this.impl.initialize(ID, this.queryPredicates);
	}
	
	
	/**
	 * Constructs a new SweepArea and uses the predicate <code>removePredicate</code> as
	 * default during reorganization. 
	 * 
	 * 
	 * @param impl The underlying implementor.
	 * @param ID The ID of this SweepArea.
	 * @param selfReorganize A flag to determine if this SweepArea can reorganize itself.
	 * @param queryPredicates An array of binary query-predicates used to probe this SweepArea. 
	 * 		  Depending on the ID passed to the query-call, different query-predicates can be chosen
	 * 		  for retrieval.
	 * @param removePredicate Default predicate for reorganization, which is applied independently 
	 * 		  from the ID passed to reorganization and expiration calls.
	 */
	public SweepArea(SweepAreaImplementor impl, int ID, boolean selfReorganize, Predicate[] queryPredicates, Predicate removePredicate) {
		this.impl = impl;
		this.ID = ID;
		this.selfReorganize = selfReorganize;
		this.queryPredicates = queryPredicates;
		this.removePredicates = new Predicate[queryPredicates.length];
		this.removeRightBinds = new RightBind[queryPredicates.length];
		for (int i = 0; i < removePredicates.length; i++) {
			this.removePredicates[i] = removePredicate;
			this.removeRightBinds[i] = new RightBind(removePredicate, null);
		}
		this.impl.initialize(ID, this.queryPredicates);
	}
	
	/**
	 * Constructs a new SweepArea, but performs no reorganization. 
	 * This is achieved by internally reorganizing with 
	 * <code>Predicates.FALSE</code>.
	 * 
	 * @param impl The underlying implementor.
	 * @param ID The ID of this SweepArea.
	 * @param selfReorganize A flag to determine if this SweepArea can reorganize itself.
	 * @param queryPredicates An array of binary query-predicates used to probe this SweepArea. 
	 * 		  Depending on the ID passed to the query-call, different query-predicates can be chosen
	 * 		  for retrieval.
	 */
	public SweepArea(SweepAreaImplementor impl, int ID, boolean selfReorganize, Predicate[] queryPredicates) {
		this(impl, ID, selfReorganize, queryPredicates, Predicates.FALSE);
	}	
			
	/**
	 * Constructs a new SweepArea with a generally applicable query- and reorganization predicate. 
	 * 
	 * @param impl The underlying implementor.
	 * @param ID The ID of this SweepArea.
	 * @param selfReorganize A flag to determine if this SweepArea can reorganize itself.
	 * @param queryPredicate A generally applicable binary query-predicate used to probe this SweepArea.
	 * 		  Hence, this predicate is applied independently from the ID passed to query calls.
	 * @param removePredicate A generally applicable binary remove-predicates utilized during the reorganization.
	 * 		  With the help of this predicates, the elements of a SweepArea are probed for expiration.
	 * 		  This predicate is applied independently from the ID passed to reorganization and expiration calls.
	 * @param dim The dimensionality of this SweepArea, i.e., the number of possible inputs or in other words
	 * 		  the number of different IDs that can be passed to method calls of this SweepArea. 
	 */
	public SweepArea(SweepAreaImplementor impl, int ID, boolean selfReorganize, Predicate queryPredicate, Predicate removePredicate, int dim) {
		this.impl = impl;
		this.ID = ID;
		this.selfReorganize = selfReorganize;
		this.queryPredicates = new Predicate[dim];
		Arrays.fill(queryPredicates, queryPredicate);
		this.removePredicates = new Predicate[dim];
		this.removeRightBinds = new RightBind[dim];
		for (int i = 0; i < removePredicates.length; i++) {
			this.removePredicates[i] = removePredicate;
			this.removeRightBinds[i] = new RightBind(removePredicate, null);
		}
		this.impl.initialize(ID, queryPredicates);
	}
	
	/**
	 * Constructs a new SweepArea with a generally applicable query- and reorganization predicate.
	 * This SweepArea performs no reorganization. This is achieved by internally reorganizing with 
	 * <code>Predicates.FALSE</code>.
	 * 
	 * @param impl The underlying implementor.
	 * @param ID The ID of this SweepArea.
	 * @param selfReorganize A flag to determine if this SweepArea can reorganize itself.
	 * @param queryPredicate A generally applicable binary query-predicate used to probe this SweepArea.
	 * 		  Hence, this predicate is applied independently from the ID passed to query calls.
	 * @param dim The dimensionality of this SweepArea, i.e., the number of possible inputs or in other words
	 * 		  the number of different IDs that can be passed to method calls of this SweepArea. 
	 */
	public SweepArea(SweepAreaImplementor impl, int ID, boolean selfReorganize, Predicate queryPredicate, int dim) {
		this(impl, ID, selfReorganize, queryPredicate, Predicates.FALSE, dim);
	}	
	
	/**
	 * Constructs a new SweepArea with a generally applicable query- and reorganization predicate.
	 * This SweepArea uses <code>Equal.DEFAULT_INSTANCE</code> for querying and 
	 * <code>new Not(Equal.DEFAULT_INSTANCE)</code> for reorganization. A self-reorganization
	 * is permitted.
	 * 
	 * @param impl The underlying implementor.
	 * @param ID The ID of this SweepArea.
	 * @param dim The dimensionality of this SweepArea, i.e., the number of possible inputs or in other words
	 * 		  the number of different IDs that can be passed to method calls of this SweepArea. 
	 */
	public SweepArea(SweepAreaImplementor impl, int ID, int dim) {
		this(impl, ID, true, Equal.DEFAULT_INSTANCE, new Not(Equal.DEFAULT_INSTANCE), dim);
	}
	
	/**
	 * Inserts the given element into this SweepArea. The default implementation
	 * simply forwards this call to the underlying implementor. Thus,
	 * it calls <code>impl.insert(o)</code>.
	 * 
	 * @param o The object to be inserted.
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong with the insertion due to the passed argument.
	 */
	public void insert(Object o) throws IllegalArgumentException {
		impl.insert(o);
	}
	
	/**
	 * Clears this SweepArea by clearing the underlying
	 * implementor. Calls <code>impl.clear()</code>. <br>
	 * This method should remove all elements of a 
	 * SweepArea, but holds its allocated resources.
	 */
	public void clear() {
		impl.clear();
	}
	
	/**
	 * Closes this SweepArea by closing the underlying
	 * implementor. Calls <code>impl.close()</code>.<br>
	 * This method should release all allocated resources
	 * of a SweepArea.
	 */
	public void close() {
		impl.close();
	}
	
	/**
	 * Returns the size of this SweepArea by
	 * determining the size of the underlying implementor.
	 * Returns <code>impl.size()</code>.
	 * 
	 * @return The size of this SweepArea.
	 */
	public int size() {
		return impl.size();
	}
	
	/**
	 * Returns an iterator over this SweepArea by
	 * delivering an iterator over the underlying implementor.
	 * Returns <code>impl.iterator()</code>.
	 * 
	 * @return An iterator over this SweepArea.
	 * @throws UnsupportedOperationException if this SweepArea is not able
	 *         to provide an iteration over its elements.
	 */
	public Iterator iterator() throws UnsupportedOperationException {
		return impl.iterator();
	}
	
	/**
	 * Queries this SweepArea with the help of the
	 * specified query object <code>o</code>. Returns all 
	 * matching elements as an iterator. The default implementation 
	 * of this method directly passes the call to the underlying implementor,
	 * i.e., it returns <code>impl.query(o, ID)</code>. <br>
	 * <i>Note:</i>
	 * This iterator should not be used to remove any elements of a
	 * SweepArea!
	 * 
	 * @param o The query object. This object is typically probed against
	 * 		the elements contained in this SweepArea.
	 * @param ID An ID determining from which input this method
	 * 		is triggered.
	 * @return All matching elements of this SweepArea are returned as an iterator. 
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong due to the passed arguments during retrieval.
	 */
	public Iterator query(Object o, int ID) throws IllegalArgumentException {
		return impl.query(o, ID);
	}

	/**
	 * Queries this SweepArea with the help of the
	 * specified query objects <code>os</code>. Returns all matching elements
	 * as an iterator. This version of query additionally allows to use partially
	 * filled arrays and specifies how many entries of such a partially
	 * filled array are valid.<br> 
	 * The default implementation of this method
	 * directly passes the call to the underlying implementor and
	 * returns <code>impl.query(os, IDs, valid)</code>.
	 * 
	 * @param os The query objects. These objects are typically probed against
	 * 		the elements contained in this SweepArea.
	 * @param IDs IDs determining from which input the query objects come from.
	 * @param valid Determines how many entries at the beginning of
	 *        <tt>os</tt> and <tt>IDs</tt> are valid and therefore taken into account.
	 * @return All matching elements of this SweepArea are returned as an iterator. 
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong due to the passed arguments during retrieval.
	 */
	public Iterator query(Object [] os, int [] IDs, int valid) throws IllegalArgumentException {
		return impl.query(os, IDs, valid);
	}

	/**
	 * Queries this SweepArea with the help of the
	 * specified query objects <code>os</code>. Returns all matching elements
	 * as an iterator. <br>	The default implementation of this method
	 * directly passes the call to the underlying implementor.
	 * Returns <code>impl.query(os, IDs)</code>.
	 * 
	 * @param os The query objects. These objects are typically probed against
	 * 		the elements contained in this SweepArea.
	 * @param IDs IDs determining from which input the query objects come from.
	 * @return All matching elements of this SweepArea are returned as an iterator. 
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong due to the passed arguments during retrieval.
	 */
	public Iterator query(Object [] os, int [] IDs) throws IllegalArgumentException {
		return impl.query(os, IDs);
	}

	/**
	 * Determines which elements in this SweepArea expire with respect to the object
	 * <tt>currentStatus</tt> and an <tt>ID</tt>. The latter is commonly used
	 * to differ by which input this reorganization step is initiated.<br>
	 * If no elements qualify for removal, an empty cursor is returned and all 
	 * elements are remained. <br>
	 * In order to remove the expired elements, either the returned iterator has to 
	 * support and execute the remove operation for each expired element during traversal
	 * or the {@link #reorganize(Object, int)} has to be overwritten to perform 
	 * the final removal. <p>
	 * The default implementation in this class performs a sequential scan:
	 * <code><pre>
	 * 	new Remover(new Filter(iterator(), removeRightBinds[ID].setRight(currentStatus)));
	 * </code></pre>
	 * Hence, specialized SweepAreas should overwrite this method to gain a more
	 * efficient reorganization.
	 * 
	 * @param currentStatus The object containing the necessary information
	 * 		to detect expired elements.
	 * @param ID An ID determining from which input this method
	 * 		is triggered.
	 * @return an iteration over the elements which expire with respect to the
	 *         object <tt>currentStatus</tt> and an <tt>ID</tt>.
	 * @throws UnsupportedOperationException An UnsupportedOperationException is thrown, if
	 * 		this method is not supported by this SweepArea.
	 * @throws IllegalStateException Throws an IllegalStateException if
	 * 		   this method is called at an invalid state.
	 */
	public Iterator expire (Object currentStatus, int ID) throws UnsupportedOperationException, IllegalStateException {
		if (ID == this.ID && !selfReorganize)
			return EmptyCursor.DEFAULT_INSTANCE;
		return new Remover(new Filter(iterator(), removeRightBinds[ID].setRight(currentStatus)));
	}
	
	/**
	 * In contrast to the method {@link #expire(Object, int)}, this method removes
	 * all expired elements from a SweepArea without returning them. 
	 * The default implementation removes all elements returned by a call to 
	 * {@link #expire(Object, int)}.<BR>
	 * In order to perform a more efficient removal, this method should
	 * be overwritten, e.g., by implementing a bulk deletion. 
	 * 
	 * @param currentStatus The object containing the necessary information
	 * 		  to perform the reorganization step.
	 * @param ID An ID determining from which input this reorganization step
	 * 		   is triggered.
	 * @throws UnsupportedOperationException An UnsupportedOperationException is thrown, if
	 * 		   is method is not supported by this SweepArea.
	 * @throws IllegalStateException Throws an IllegalStateException if
	 * 		   this method is called at an invalid state.
	 */
	public void reorganize(Object currentStatus, int ID) throws UnsupportedOperationException, IllegalStateException {
		Cursors.consume(expire(currentStatus, ID));	
	}
	
	/**
	 * Returns a reference to the underlying implementor.
	 * 
	 * @return The underlying implementor.
	 * @throws UnsupportedOperationException If this operation is not supported.
	 */
	public SweepAreaImplementor getImplementor() throws UnsupportedOperationException {
		return impl;
	}
	
	/**
	 * Returns the ID of this SweepArea.
	 * 
	 * @return The ID of this SweepArea.
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * Returns the array of binary query-predicates.
	 * 
	 * @return The Array of query-predicates.
	 * @throws UnsupportedOperationException If this operation is not supported.
	 */
	public Predicate[] getQueryPredicates() throws UnsupportedOperationException {
		return this.queryPredicates;
	}
	
	/**
	 * Returns the array of binary remove-predicates.
	 * 
	 * @return The Array of remove-predicates
	 * @throws UnsupportedOperationException If this operation is not supported.
	 */
	public Predicate[] getRemovePredicates() throws UnsupportedOperationException {
		return this.removePredicates;
	}
	
	/**
	 * Returns <tt>true</tt> if this SweepArea permits
	 * self-reorganization. In this case, the
	 * <tt>ID</tt> passed to the reorganize call
	 * is the same as the internal ID of this SweepArea.
	 * Otherwise <tt>false</tt> is returned.
	 * 
	 * @return <tt>True</tt> if this SweepArea permits
	 * 		self-reorganization, otherwise <tt>false</tt>.
	 */
	public boolean allowsSelfReorganize() {
		return selfReorganize;
	}
		
}
