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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import xxl.core.cursors.sources.EmptyCursor;
import xxl.core.cursors.unions.Sequentializer;
import xxl.core.functions.Function;
import xxl.core.functions.Functions;

/**
 * A hash-based implementation of the interface
 * {@link SweepAreaImplementor}. The hash table
 * references {@link java.util.List lists} modelling
 * the buckets. 
 * 
 * @see SweepAreaImplementor
 * @see xxl.core.functions.Function
 * @see java.util.List
 */
public class HashListSAImplementor extends AbstractSAImplementor {

	/**
	 * The hash table.
	 */
	protected ArrayList hashTable;

	/**
	 * An array of hash functions offering insertion, 
	 * retrieval and reorganization depending on the 
	 * ID passed to the method calls. Each hash function
	 * is a map from Object -> Integer. 
	 */
	protected Function[] hashFunctions;

	/**
	 * A parameterless function that delivers
	 * an empty {@link java.util.List List} representing 
	 * a new bucket.
	 */
	protected Function newList;

	/**
	 * Constructs a new HashListSAImplementor.
	 * 
	 * @param hashFunctions The array of hash functions. Each is a 
	 * 		  map from Object -> Integer. 
	 * @param newList A parameterless function that returns a new 
	 * 		  list at each invocation. These lists represent the
	 * 		  buckets of the hash table.
	 * 
	 */
	public HashListSAImplementor(Function[] hashFunctions, Function newList) {
		this.hashFunctions = hashFunctions;
		this.hashTable = new ArrayList();
		this.newList = newList;
	}
	
	/**
	 * Constructs a new HashListSAImplementor which uses
	 * the specified hash function independently from the ID 
	 * passed to query, expire and reorganize calls.
	 * 
	 * @param hashFunction The hash function, which is a 
	 * 		  map from Object -> Integer. 
	 * @param newList A parameterless function that returns a new 
	 * 		  list at each invocation. These lists represent the
	 * 		  buckets of the hash table.
	 * @param dim The number of possible inputs or in other words,
	 * 		  the number of different IDs that can be passed to 
	 * 		  method calls of this implementor. 
	 */
	public HashListSAImplementor(Function hashFunction, Function newList, int dim) {
		this.hashTable = new ArrayList();
		this.newList = newList;
		this.hashFunctions = new Function[dim];
		Arrays.fill(this.hashFunctions, hashFunction);
	}
	
	/**
	 * Constructs a new HashListSAImplementor which uses
	 * the specified hash function independently from the ID 
	 * passed to query, expire and reorganize calls.
	 * The function creating the buckets delivers
	 * instances of the class {@link java.util.LinkedList LinkedList}.
	 *  
	 * @param hashFunction The hash function, which is a 
	 * 		  map from Object -> Integer. 
	 * @param dim The number of possible inputs or in other words,
	 * 		  the number of different IDs that can be passed to 
	 * 		  method calls of this implementor. 
	 */
	public HashListSAImplementor(Function hashFunction, int dim) {
		this(hashFunction, 
			new Function() {
				public Object invoke() {
					return new LinkedList();
				}
			}, dim
		);
	}

	/**
	 * Constructs a new HashListSAImplementor which uses
	 * the method {@link java.lang.Object#hashCode()} to determine
	 * the hash value of an object. The function creating the buckets delivers
	 * instances of the class {@link java.util.LinkedList LinkedList}.
	 *  
	 * @param dim The number of possible inputs or in other words,
	 * 		  the number of different IDs that can be passed to 
	 * 		  method calls of this implementor. 
	 */
	public HashListSAImplementor(int dim) {
		this(Functions.hash(), dim);
	}

	/**
	 * Inserts the given element into the corresponding
	 * bucket of the hash table. The bucket number is
	 * determined by <code>((Integer)hashFunctions[ID].invoke(o)).intValue()</code>.
	 * If the hash table does not contains a bucket with this
	 * number, a new bucket is created by invoking
	 * the function <code>newList</code>.
	 * 
	 * @param o The object to be inserted.
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong with the insertion due to the passed argument.
	 */
	public void insert(Object o) throws IllegalArgumentException {
		int bucketNo = ((Integer)hashFunctions[ID].invoke(o)).intValue();
		List list;
		try {
			list = (List)hashTable.get(bucketNo);
		} catch(IndexOutOfBoundsException e) {
			list = (List)newList.invoke();
			for (int i = hashTable.size(); i < bucketNo; i++)
				hashTable.add(i, newList.invoke());
			hashTable.add(bucketNo, list);
		}
		list.add(o);
	}

	/**
	 * Removes the specified element from the hash table.
	 * Tries to access the corresponding bucket and to remove
	 * the element <code>o</code>.
	 * 
	 * @param o The object to be removed.
	 * @return <tt>True</tt> if the removal has been successful, otherwise <tt>false</tt>.
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong with the removal due to the passed argument.
	 */
	public boolean remove(Object o) throws IllegalArgumentException {
		try {
			return ((List)hashTable.get(((Integer)hashFunctions[ID].invoke(o)).intValue())).remove(o);
		}  catch(IndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Checks if element <tt>o1</tt> is contained and 
	 * if <tt>true</tt> updates it with </tt>o2</tt>. 
	 * 
	 * @param o1 The object to be replaced.
	 * @param o2 The new object.
	 * @return The updated object is returned.
	 * @throws IllegalArgumentException Throws an IllegalArgumentException
	 * 		if something goes wrong with the update operation due to the passed arguments.
	 * @throws UnsupportedOperationException Throws an UnsupportedOperationException
	 * 		if this method is not supported.
	 */
	public Object update(Object o1, Object o2) throws IllegalArgumentException {
		int hashValue1 = ((Integer)hashFunctions[ID].invoke(o1)).intValue();
		if (hashValue1 != ((Integer)hashFunctions[ID].invoke(o2)).intValue()) 
			throw new IllegalArgumentException("Incompatible hash values!");
		try {
			List list = (List)hashTable.get(hashValue1);
			return list.set(list.indexOf(o1), o2);
		}
		catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Object o1 is not contained.");
		}
	}

	/**
	 * Clears this implementor by clearing all buckets
	 * as well as the hash table.
	 */
	public void clear() {
		for (int i = 0; i < hashTable.size(); i++)
			try {
				((List)hashTable.get(i)).clear();
			} catch(IndexOutOfBoundsException e) {}
		hashTable.clear();
	}

	/**
	 * Closes this implementor. In this case,
	 * only {@link #clear()} is executed.
	 */
	public void close() {
		clear();
	}

	/**
	 * Returns the size of this implementor which
	 * corresponds to the sum of the bucket sizes.
	 * 
	 * @return The size.
	 */
	public int size() {
		int size = 0;
		for (int i = 0; i < hashTable.size(); i++) {
			try {
				size += ((List)hashTable.get(i)).size();
			} catch(IndexOutOfBoundsException e) {}
		}
		return size;	
	}

	/**
	 * Returns an iterator over the elements of this
	 * implementor. Consequently, this iterator is 
	 * a concatenation of the bucket iterators.
	 * 
	 * @return An iterator over the elements of this HashListSAImplementor.
	 * @throws UnsupportedOperationException If this operation is not supported.
	 */
	public Iterator iterator() {
		int size = 0;
		Iterator[] tmp = new Iterator[hashTable.size()];
		for (int i = 0; i< hashTable.size(); i++)
			try {
				tmp[i] = ((List)hashTable.get(i)).iterator();
				size++;
			} catch(IndexOutOfBoundsException e) {}
		Iterator[] iterators = new Iterator[size];
		for (int i = 0; i < hashTable.size(); i++)
			if (tmp[i] != null)
				iterators[i] = tmp[i];
		return new Sequentializer(iterators);
	}

	/**
	 * Queries this implementor with the help of the
	 * specified query object <code>o</code> and the query-predicates
	 * set during initialization, see method
	 * {@link #initialize(int, xxl.core.predicates.Predicate[])}. 
	 * At first, the corresponding bucket for retrieval
	 * is determined by applying the hash function on <code>o</code>.
	 * Then this bucket is filtered for matching elements which
	 * are returned as a cursor. <br>
	 * <i>Note:</i>
	 * This iterator should not be used to remove any elements from this
	 * implementor SweepArea!
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
	public Iterator query(Object o, int ID) throws IllegalArgumentException {
		int bucketNo = ((Integer)hashFunctions[ID].invoke(o)).intValue();
		try {
			List list = (List)hashTable.get(bucketNo);
			if (list.size()==0) 
				return EmptyCursor.DEFAULT_INSTANCE;
			return filter(list.iterator(), o , ID);
		} catch(IndexOutOfBoundsException e) {
			return EmptyCursor.DEFAULT_INSTANCE;
		}
	}
	
}
