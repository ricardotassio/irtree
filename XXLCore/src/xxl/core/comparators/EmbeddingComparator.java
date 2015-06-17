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

package xxl.core.comparators;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The EmbeddingComparator adds a bound to a given Comparator.
 *  
 * @see java.util.Comparator
 */
public class EmbeddingComparator implements Comparator, Serializable {

	/**
	 * This instance can be used for getting a default instance of
	 * EmbeddingComparator. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns, Prototype
	 * in <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of EmbeddingComparator.
	 */
	public final static EmbeddingComparator DEFAULT_INSTANCE = new EmbeddingComparator();

	/** 
	 * The Comparator to be wrapped.
	 */
	protected Comparator comparator;

	/** 
	 * The bound of the Comparator.
	 */
	protected Object bound;

	/** 
	 * Determines whether the bound is the lower bound.
	 */
	protected boolean lowerBound;

	/** Creates a new EmbeddingComparator.
		@param comparator the Comparator to be wrapped
		@param bound the bound of the comparator
		@param lowerBound determines whether the bound is the lower bound
	 */
	public EmbeddingComparator (Comparator comparator, Object bound, boolean lowerBound) {
		this.comparator = comparator;
		this.bound = bound;
		this.lowerBound = lowerBound;
	}

	/** 
	 * Creates a new EmbeddingComparator.
	 */
	public EmbeddingComparator () {
		this(true);
	}

	/** Creates a new EmbeddingComparator.
		@param lowerBound determines whether the bound is the lower bound
	*/
	public EmbeddingComparator (boolean lowerBound) {
		this(ComparableComparator.DEFAULT_INSTANCE, lowerBound);
	}

	/** Creates a new EmbeddingComparator.
		@param comparator the Comparator to be wrapped
	*/
	public EmbeddingComparator (Comparator comparator) {
		this(comparator, null);
	}

	/** Creates a new EmbeddingComparator.
		@param bound the bound of the comparator
	*/
	public EmbeddingComparator (Object bound) {
		this(bound, true);
	}

	/** Creates a new EmbeddingComparator.
		@param comparator the Comparator to be wrapped
		@param lowerBound determines whether the bound is the lower bound
	*/
	public EmbeddingComparator (Comparator comparator, boolean lowerBound) {
		this(comparator, null, lowerBound);
	}

	/** Creates a new EmbeddingComparator.
		@param comparator the Comparator to be wrapped
		@param bound the bound of the comparator
	*/
	public EmbeddingComparator (Comparator comparator, Object bound) {
		this(comparator, bound, true);
	}

	/** Creates a new EmbeddingComparator.
		@param bound the bound of the comparator
		@param lowerBound determines whether the bound is the lower bound
	*/
	public EmbeddingComparator (Object bound, boolean lowerBound) {
		this(ComparableComparator.DEFAULT_INSTANCE, bound, lowerBound);
	}

	/** Compares its two arguments for order.
	 * @param object1 the first object to be compared
	 * @param object2 the second object to be compared
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
	*/
	public int compare (Object object1, Object object2) {
		if (bound==object1 || (bound!=null? bound.equals(object1): object1.equals(bound)))
			return lowerBound? Integer.MIN_VALUE: Integer.MAX_VALUE;
		if (bound==object2 || (bound!=null? bound.equals(object2): object2.equals(bound)))
			return lowerBound? Integer.MAX_VALUE: Integer.MIN_VALUE;
		return comparator.compare(object1, object2);
	}
}