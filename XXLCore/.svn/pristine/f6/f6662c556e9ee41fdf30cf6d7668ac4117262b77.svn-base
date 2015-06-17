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
import java.util.Iterator;
import java.util.List;

/**
 * The class provides static methods that supplements the collection
 * features of the Java-SDK (e.g. a <code>java.util.List</code> quick sort is
 * included).<p>
 *
 * Usage example:
 * <pre>
 *     // create a new iterator with 10000 random number in range [0,1000000)
 *
 *     rand = new RandomIntegers(1000000, 10000);
 *
 *     // create a new array list containing the 10000 random numbers
 *
 *     l = Cursors.toList(rand, new ArrayList(10000));
 *
 *     // sort the random numbers using the quick sort algorithm for lists
 *
 *     quickSort(l, ComparableComparator.DEFAULT_INSTANCE);
 * </pre>
 *
 * @see xxl.core.comparators.ComparableComparator
 * @see java.util.Comparator
 * @see java.util.Iterator
 * @see java.util.List
 */
public abstract class Collections {

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private Collections () {}

	/**
	 * Sort the given list by using the quick sort algorithm for lists.
	 * The specified comparator is used for comparing the elements of
	 * the list.
	 *
	 * @param data the list to be sorted.
	 * @param comparator the comparator that is used for comparing the
	 *        elements of the list.
	 */
	public static void quickSort (List data, Comparator comparator) {
		quickSort(data, 0, data.size()-1, comparator);
	}

	/**
	 * This method performs the quick sort algorithm for lists. The given
	 * list is sorted using the specified comparator. The specified
	 * indices determine the range of the list that should be sorted by
	 * the algorithm. For (ranges of) lists with no more than ten
	 * elements the bubble sort algorithm is used, other (ranges of)
	 * lists are sorted by a quick sort algorithm using the median of
	 * three as pivot element.
	 *
	 * @param data the list to be sorted.
	 * @param l the index of the list where the range to be sorted
	 *        starts.
	 * @param r the index of the list where the range to be sorted ends.
	 * @param comparator the comparator that is used for comparing the
	 *        elements of the list.
	 */
	public static void quickSort (List data, int l, int r, Comparator comparator) {
		int re, li, mid;
		Object tmp;

		//rekursiv, direktes Tauschen der Elemente
		if (r-l < 10)
			for (li = l+1; li <= r; li++) {
				tmp = data.get(li);
				for (re = li; re > l && comparator.compare(tmp, data.get(re-1)) < 0; re--)
					data.set(re, data.get(re-1));
				data.set(re, tmp);
			}
		else
			while (l < r) {
				li = l;
				re = r;
				mid = (li + re ) / 2; //>>1; // ???
				if (comparator.compare(data.get(li), data.get(mid)) >0 )
					swap(data, li, mid);
				if (comparator.compare(data.get(mid), data.get(re)) >0 )
					swap(data, mid, re);
				if (comparator.compare(data.get(li), data.get(mid)) >0 )
					swap(data, li, mid);
				tmp = data.get(mid);
				while (li <= re) {
					while (comparator.compare(data.get(li), tmp) <0 )
						li++;
					while (comparator.compare(tmp, data.get(re)) <0 )
						re--;
					if (li <= re) {
						swap(data, li, re);
						li++;
						re--;
					} // end of if
				} // end of while
				if (l < re)
					quickSort(data, l, re, comparator);
				l = li;
			} // end of while
	} //end of quickSort

	/**
	 * This metod swaps the elements of the given list at the specified
	 * indices.
	 *
	 * @param data the list containing the elements to be swapped.
	 * @param l the index of the first element to be swapped.
	 * @param r the index of the second element to be swapped.
	 */
	private static void swap (List data, int l, int r) {
		Object tmp = data.get(l);
		data.set(l, data.get(r));
		data.set(r, tmp);
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 * @param args the arguments for the <tt>main</tt> method.
	 */
	public static void main (String [] args) {
		xxl.core.cursors.Cursor rand;
		List l;
		long t1,t2;

		// Generate 10.000 Integers < 1.000.000 and sort them in
		// a) an array list
		// b) a linked list

		// ArrayList
		rand = new xxl.core.cursors.sources.RandomIntegers(1000000, 10000);
		l = xxl.core.cursors.Cursors.toList(rand, new java.util.ArrayList(10000));

		t1 = java.lang.System.currentTimeMillis();
		quickSort(l, xxl.core.comparators.ComparableComparator.DEFAULT_INSTANCE);
		t2 = java.lang.System.currentTimeMillis();

		java.lang.System.out.println("Time for sorting an array list (ms): " + (t2-t1));

		// Test it
		Iterator i = l.iterator();
		int last = -1;
		while (i.hasNext()) {
			int number = ((Integer) i.next()).intValue();
			if (number<last)
				java.lang.System.out.println("Error during sorting");
			last = number;
		}
		java.lang.System.out.println("Result of sorting is ok");

		// LinkedList
		rand = new xxl.core.cursors.sources.RandomIntegers(1000000, 10000);
		l = xxl.core.cursors.Cursors.toList(rand, new java.util.LinkedList());

		t1 = java.lang.System.currentTimeMillis();
		quickSort(l, xxl.core.comparators.ComparableComparator.DEFAULT_INSTANCE);
		t2 = java.lang.System.currentTimeMillis();

		java.lang.System.out.println("Time for sorting a linked list (ms): " + (t2-t1));
	}
}