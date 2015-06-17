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
	The InverseComparator inverts the ordering of a given Comparator.
	@see java.util.Comparator
*/
public class InverseComparator implements Comparator, Serializable {

	/**
	 * This instance can be used for getting a default instance of
	 * InverseComparator. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns, Prototype
	 * in <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of InverseComparator.
	 */
	public static final InverseComparator DEFAULT_INSTANCE = new InverseComparator(ComparableComparator.DEFAULT_INSTANCE);

	/** 
	 * The Comparator to be inverted.
	 */
	protected Comparator comparator;

	/** Creates a new InverseComparator.
		@param comparator the Comparator to be inverted
	 */
	public InverseComparator (Comparator comparator) {
		this.comparator = comparator;
	}

	/** 
	 * Compares its two arguments for order.
	 * @param object1 the first object to be compared
	 * @param object2 the second object to be compared
	 * @return the comparison result
	 */
	public int compare (Object object1, Object object2) {
		return comparator.compare(object2, object1);
	}
}