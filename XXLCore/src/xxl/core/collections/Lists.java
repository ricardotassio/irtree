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

import java.util.Comparator;
import java.util.List;

import xxl.core.comparators.ComparableComparator;

/**
 * This class contains various methods for manipulating lists.<p>
 *
 * The documentation of the searching methods contained in this class
 * includes a brief description of the implementation. Such descriptions
 * should be regarded as implementation notes, rather than parts of the
 * specification. Implementors should feel free to substitute other
 * algorithms, as long as the specification itself is adhered to.
 *
 * @see ComparableComparator
 * @see Comparator
 * @see List
 */
public class Lists {

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private Lists() {
	}

	/**
	 * Searches the specified list for the first or last appearance of the
	 * specified object using the binary search algorithm. The list
	 * <b>must</b> be sorted into ascending order according to the
	 * specified comparator before calling this method. If it is not
	 * sorted, the results are undefined. If the list contains multiple
	 * elements equal to the specified object, the boolean value
	 * <tt>last</tt> decides whether the first or last appearance will be
	 * found.
	 *
	 * @param list the list to be searched.
	 * @param object the object to be searched for.
	 * @param comparator the comparator by which the list is ordered.
	 * @param last if false, the first appearance will be found; if true,
	 *        the last appearance will be found.
	 * @return index of the search object, if it is contained in the list;
	 *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.<br>
	 *         The <i>insertion point</i> is defined as the point at which
	 *         the object would be inserted into the list: the index of
	 *         the first element greater than the object, or
	 *         <tt>list.size()</tt>, if all elements in the list are less
	 *         than the specified object. Note that this guarantees that
	 *         the return value will be &ge; 0 only if the key is
	 *         found.
	 * @throws ClassCastException if the list contains
	 *         elements that are not <i>mutually comparable</i> using the
	 *         specified comparator, or the search object is not
	 *         <i>mutually comparable</i> with the elements of the list
	 *         using this comparator.
	 */
	public static int indexOf (List list, Object object, Comparator comparator, boolean last) {
		return last ? lastIndexOf(list, object, comparator) : firstIndexOf(list, object, comparator);
	}

	/**
	 * Searches the specified list for the first or last appearance of the
	 * specified object using the binary search algorithm. The list
	 * <b>must</b> be sorted into ascending order according to the
	 * <i>natural ordering</i> of its elements before calling this method.
	 * If it is not sorted, the results are undefined. If the list
	 * contains multiple elements equal to the specified object, the
	 * boolean value <tt>last</tt> decides whether the first or last
	 * appearance will be found.
	 *
	 * @param list the list to be searched.
	 * @param object the object to be searched for.
	 * @param last if false, the first appearance will be found; if true,
	 *        the last appearance will be found.
	 * @return index of the search object, if it is contained in the list;
	 *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.<br>
	 *         The <i>insertion point</i> is defined as the point at which
	 *         the object would be inserted into the list: the index of
	 *         the first element greater than the object, or
	 *         <tt>list.size()</tt>, if all elements in the list are less
	 *         than the specified object. Note that this guarantees that
	 *         the return value will be &ge; 0 only if the key is
	 *         found.
	 * @throws ClassCastException if the list contains
	 *         elements that are not <i>mutually comparable</i>, or the
	 *         search object is not <i>mutually comparable</i> with the
	 *         elements of the list.
	 */
	public static int indexOf (List list, Object object, boolean last) {
		return indexOf(list, object, ComparableComparator.DEFAULT_INSTANCE, last);
	}

	/**
	 * Searches the specified list for the first appearance of the
	 * specified object using the binary search algorithm. The list
	 * <b>must</b> be sorted into ascending order according to the
	 * specified comparator before calling this method. If it is not
	 * sorted, the results are undefined. If the list contains multiple
	 * elements equal to the specified object, the first appearance will
	 * be found.
	 *
	 * @param list the list to be searched.
	 * @param object the object to be searched for.
	 * @param comparator the comparator by which the list is ordered.
	 * @return index of the search object, if it is contained in the list;
	 *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.<br>
	 *         The <i>insertion point</i> is defined as the point at which
	 *         the object would be inserted into the list: the index of
	 *         the first element greater than the object, or
	 *         <tt>list.size()</tt>, if all elements in the list are less
	 *         than the specified object. Note that this guarantees that
	 *         the return value will be &ge; 0 only if the key is
	 *         found.
	 * @throws ClassCastException if the list contains
	 *         elements that are not <i>mutually comparable</i> using the
	 *         specified comparator, or the search object is not
	 *         <i>mutually comparable</i> with the elements of the list
	 *         using this comparator.
	 */
	public static int firstIndexOf (List list, Object object, Comparator comparator) {
		int left = 0, right = list.size()-1;

		while (left <= right) {
			int median = (left+right)/2, comparison = comparator.compare(list.get(median), object);

			if (comparison < 0)
				left = median+1;
			else
				if (comparison > 0 || left < median && comparator.compare(list.get(median-1), object) == 0)
					right = median-1;
				else
					return median;
		}
		return -left-1;
	}

	/**
	 * Searches the specified list for the first appearance of the
	 * specified object using the binary search algorithm. The list
	 * <b>must</b> be sorted into ascending order according to the
	 * <i>natural ordering</i> of its elements before calling this method.
	 * If it is not sorted, the results are undefined. If the list
	 * contains multiple elements equal to the specified object, the first
	 * appearance will be found.
	 *
	 * @param list the list to be seached.
	 * @param object the object to be searched for.
	 * @return index of the search object, if it is contained in the list;
	 *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.<br>
	 *         The <i>insertion point</i> is defined as the point at which
	 *         the object would be inserted into the list: the index of
	 *         the first element greater than the object, or
	 *         <tt>list.size()</tt>, if all elements in the list are less
	 *         than the specified object. Note that this guarantees that
	 *         the return value will be &ge; 0 only if the key is
	 *         found.
	 * @throws ClassCastException if the list contains
	 *         elements that are not <i>mutually comparable</i>, or the
	 *         search object is not <i>mutually comparable</i> with the
	 *         elements of the list.
	 */
	public static int firstIndexOf (List list, Object object) {
		return firstIndexOf(list, object, ComparableComparator.DEFAULT_INSTANCE);
	}

	/**
	 * Searches the specified list for the last appearance of the
	 * specified object using the binary search algorithm. The list
	 * <b>must</b> be sorted into ascending order according to the
	 * specified comparator before calling this method. If it is not
	 * sorted, the results are undefined. If the list contains multiple
	 * elements equal to the specified object, the last appearance will be
	 * found.
	 *
	 * @param list the list to be searched.
	 * @param object the object to be searched for.
	 * @param comparator the comparator by which the list is ordered.
	 * @return index of the search object, if it is contained in the list;
	 *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.<br>
	 *         The <i>insertion point</i> is defined as the point at which
	 *         the object would be inserted into the list: the index of
	 *         the first element greater than the object, or
	 *         <tt>list.size()</tt>, if all elements in the list are less
	 *         than the specified object. Note that this guarantees that
	 *         the return value will be &ge; 0 only if the key is
	 *         found.
	 * @throws ClassCastException if the list contains
	 *         elements that are not <i>mutually comparable</i> using the
	 *         specified comparator, or the search object in not
	 *         <i>mutually comparable</i> with the elements of the list
	 *         using this comparator.
	 */
	public static int lastIndexOf (List list, Object object, Comparator comparator) {
		int left = 0, right = list.size()-1;

		while (left <= right) {
			int median = (left+right)/2, comparison = comparator.compare(list.get(median), object);

			if (comparison > 0)
				right = median-1;
			else
				if (comparison < 0 || median < right && comparator.compare(list.get(median+1), object) == 0)
					left = median+1;
				else
					return median;
		}
		return -left-1;
	}

	/**
	 * Searches the specified list for the last appearance of the
	 * specified object using the binary search algorithm. The list
	 * <b>must</b> be sorted into ascending order according to the
	 * <i>natural ordering</i> of its elements before calling this method.
	 * If it is not sorted, the results are undefined. If the list
	 * contains multiple elements equal to the specified object, the last
	 * appearance will be found.
	 *
	 * @param list the list to be searched.
	 * @param object the object to be searched for.
	 * @return index of the search object, if it is contained in the list;
	 *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.<br>
	 *         The <i>insertion point</i> is defined as the point at which
	 *         the object would be inserted into the list: the index of
	 *         the first element greater than the object, or
	 *         <tt>list.size()</tt>, if all elements in the list are less
	 *         than the specified object. Note that this guarantees that
	 *         the return value will be &ge; 0 only if the key is
	 *         found.
	 * @throws ClassCastException if the list contains
	 *         elements that are not <i>mutually comparable</i>, or the
	 *         search object is not <i>mutually comparable</i> with the
	 *         elements of the list.
	 */
	public static int lastIndexOf (List list, Object object) {
		return lastIndexOf(list, object, ComparableComparator.DEFAULT_INSTANCE);
	}
}