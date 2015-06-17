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
import xxl.core.functions.Function;

/**
 *	The FeatureComparator compares the 'feature's of two given Objects.
 *	The feature of each Object is extracted with a mapping-Function, i.e.
 *	this classs implements the <code>compare</code>-method as follows:
 *
 *	<code><pre>
 *	public int compare (Object o1, Object o2) {
 *		return comparator.compare(function1.invoke(o1), function2.invoke(o2));
 *	}
 *	</code></pre>
 *
 *	<br>
 *	If the two objects are of the same type it is more convenient to
 *	use hand over only one mapping function.
 *	<br><br>
 *
 *	@see java.util.Comparator
 *	@see xxl.core.functions.Function
 */
public class FeatureComparator implements Comparator, Serializable {

	/**The function that maps the first object to a new object that is compared.*/
	protected Function function1;
	/**The function that maps the second object to a new object that is compared.*/
	protected Function function2;
	/**The comparator that is used for comparison.*/
	protected Comparator comparator;

	/** 
	 * Constructs a new FeatureComparator.
	 * 
	 * @param comparator Comparator that is used for comparison.
	 * @param function1 that maps the first object to a new object that is compared.
	 * @param function2 that maps the second object to a new object that is compared.
	 */
	public FeatureComparator (Comparator comparator, Function function1, Function function2) {
		this.function1 = function1;
		this.function2 = function2;
		this.comparator = comparator;
	}

	/** 
	 * Constructs a new FeatureComparator.
	 * Internally, ComparableComparator.DEFAULT_INSTANCE is used for comparison.
	 * 
	 * @param function1 that maps the first object to a new object that is compared.
	 * @param function2 that maps the second object to a new object that is compared.
	 */
	public FeatureComparator (Function function1, Function function2) {
		this (ComparableComparator.DEFAULT_INSTANCE, function1, function2);
	}

	/** 
	 * Constructs a new FeatureComparator.
	 * 
	 * @param comparator Comparator that is used for comparison.
	 * @param function that maps each object to a new object that is compared.
	 */
	public FeatureComparator (Comparator comparator, Function function) {
		this(comparator,function,function);
	}

	/** 
	 * Constructs a new FeatureComparator.
	 * Internally, ComparableComparator.DEFAULT_INSTANCE is used for comparison.
	 *
	 * @param function that maps each object to a new object that is compared.
	 */
	public FeatureComparator (Function function) {
		this(ComparableComparator.DEFAULT_INSTANCE, function);
	}

	/**
	 * Compares two objects.
	 *
	 * @param o1 first object.
	 * @param o2 second object.
	 * @return integer (0 if the objects are equal, -1 if o1<o2, +1 if o1>o2).
	 */
	public int compare (Object o1, Object o2) {
		return comparator.compare(function1.invoke(o1), function2.invoke(o2));
	}
}
