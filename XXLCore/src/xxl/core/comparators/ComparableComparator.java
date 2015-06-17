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
 * The ComparableComparator uses the <code>compareTo()</code>-method
 * of a given Comparable-Object to compare two elements.
 * This means that Objects that implement the Comparable-interface
 * are wrapped by a ComparableComparator to be used as a Comparator.
 * 
 * @see java.util.Comparator
 */

public class ComparableComparator implements Comparator, Serializable {

	/**
	 * This instance can be used for getting a default instance of
	 * ComparableComparator. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns, Prototype
	 * in <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of ComparableComparator.
	 */
	public static final ComparableComparator DEFAULT_INSTANCE = new ComparableComparator();


	/** 
	 * Compares its two arguments for order. If a <tt>null</tt> value is allowed 
	 * to be passed as argument, the user has to wrap this comparator by calling 
	 * {@link Comparators#newNullSensitiveComparator(java.util.Comparator,boolean)}.
	 * <br>
	 * The exact implementation of this method is:
	 * <pre>
	 * 		return ((Comparable)o1).compareTo(o2);
	 * </pre>
	 * @param o1 the first object
	 * @param o2 the second object
	 * @return the comparison result
	 */
	public int compare (Object o1, Object o2) {
		return ((Comparable)o1).compareTo(o2);
	}
}