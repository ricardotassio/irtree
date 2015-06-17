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

package xxl.core.cursors.groupers;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import xxl.core.collections.MapEntry;
import xxl.core.comparators.ComparableComparator;
import xxl.core.cursors.mappers.Aggregator;
import xxl.core.functions.Function;

/**
 * The minimator is an {@link xxl.core.cursors.mappers.Aggregator aggregator}
 * that computes the minimal element for a given input iteration (lazy
 * evaluation). The minimator uses
 * {@link xxl.core.collections.MapEntry map entries} to arrange the elements
 * delivered by the input cursor. So the <tt>next</tt> and <tt>peek</tt> method
 * return instances of the class <tt>MapEntry</tt>. The minimator computes the
 * next minimal element concerning the underlying iteration by lazy evaluation,
 * i.e., the global minimum is definitely detected if the whole input iteration
 * has been consumed. A map entry consists of a key and a value. The values are
 * implemented as {@link java.util.LinkedList linked lists} and the key is
 * determined by a user defined function, named <tt>mapping</tt>. For each input
 * element a key is computed applying the mapping function and the element is
 * inserted in the linked list refering to this key. Furthermore a comparator can
 * be specified, which is used to compare two keys of map entries. So a minimator
 * minimizes the given input iteration according to the keys of map entries that
 * were determined when a user defined, unary mapping function is applied on an
 * input element. A minimator may also detect the maximum of the input iteration
 * using an {@link xxl.core.comparators.InverseComparator inverse} comparator
 * instead (see example (2)).
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     Minimator minimator = new Minimator(new RandomIntegers(100, 10));
 * 
 *     minimator.open();
 * 
 *     while (minimator.hasNext())
 *         System.out.print(((MapEntry)minimator.next()).getKey() + "; ");
 *     System.out.flush();
 * 
 *     minimator.close();
 * </pre>
 * This minimator returns the minimal key of 10 randomly distributed integers
 * with range [0,...,100[. The <tt>next</tt> method returns a map entry, namely
 * the first aggregation value. The key's, the integer elements are mapped to,
 * are delivered by the identity function. And the keys are compared by a default
 * {@link xxl.core.comparators.ComparableComparator comparator} which assumes
 * that these keys implement the {@link java.lang.Comparable comparable}
 * interface. Since lazy evaluation is performed the absolute minimum is
 * definitively determined, when all elements of the input iteration have been
 * consumed.</p>
 * 
 * <p><b>Example usage (2):</b>
 * <pre>
 *     // Object[0] --> name, Object[1] --> age
 * 
 *     Object[] persons = new Object[] {
 *         new Object[] {new String("Tobias"), new Integer(23)},
 *         new Object[] {new String("Juergen"), new Integer(23)},
 *         new Object[] {new String("Martin"), new Integer(26)},
 *         new Object[] {new String("Bjoern"), new Integer(28)},
 *         new Object[] {new String("Jens"), new Integer(27)},
 *         new Object[] {new String("Bernhard"), new Integer(35)},
 *         new Object[] {new String("Jochen"), new Integer(29)},
 *     };
 *     minimator = new Minimator(
 *         new ArrayCursor(persons),
 *         new Function() {
 *             public Object invoke(Object person) {
 *                 return (Integer)((Object[])person)[1];
 *             }
 *         },
 *         InverseComparator.DEFAULT_INSTANCE
 *     );
 * 
 *     minimator.open();
 * 
 *     Iterator results;
 *     while (minimator.hasNext()) {
 *         results = ((LinkedList)((MapEntry)minimator.next()).getValue()).listIterator(0);
 *         while (results.hasNext())
 *             System.out.print("Name: " + ((Object[])results.next())[0] + "; ");
 *         System.out.flush();
 *     }
 * 
 *     minimator.close();
 * </pre>
 * This minimator uses an array cursor which contains object arrays and each
 * object array defines a person in the following way:
 * <ul>
 *     <li>
 *         object[0] contains the name of a person
 *     </li>
 *     <li>
 *         object[1] contains the age of a person
 *     </li>
 * </ul>
 * The defined function maps an input object, a person, to a certain key, namely
 * the person's age. These keys are compared with an inverse comparator, i.e.,
 * the person with the maximal age will be returned at last by the minimator
 * using lazy evalutation. Since a minimator returns map entries, the next
 * element returned by a minimator has to be casted to a map entry. The key of
 * the returned map entry is the person's age. With the intention to receive the
 * value of the returned map entry, namely a linked list of objects sharing the
 * same key, this value has to be casted to a linked list and for further use
 * this list is converted to an iterator by calling its method
 * <tt>listIterator</tt>. The elements of the result-iteration are object arrays
 * (persons), so the name of each person belonging to the key (age) is printed to
 * the output stream.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.mappers.Aggregator
 * @see xxl.core.collections.MapEntry
 */
public class Minimator extends Aggregator {

	/**
	 * The function used to map an input element to a certain key.
	 */
	protected Function mapping;

	/**
	 * Creates a new minimator. If an iterator is given to this constructor it
	 * is wrapped to a cursor.
	 *
	 * @param iterator the input iteration providing the data to be minimized.
	 * @param mapping the function used to map an input element to a certain key.
	 * @param comparator the comparator used to determine whether the current
	 *        object is smaller than the current minima.
	 */
	public Minimator(Iterator iterator, final Function mapping, final Comparator comparator) {
		// calling Aggregator(iterator, function)
		super(
			iterator,
			new Function() {
				public Object invoke(Object aggregate, Object object) {
					if (aggregate == null) {
						// initializing aggregate by creating a new MapEntry
						LinkedList objects = new LinkedList();
						objects.add(object);
						return new MapEntry(mapping.invoke(object), objects);
					}
					else {
						MapEntry mapEntry = (MapEntry)aggregate;
						// using mapping function to determine the value of the current object
						Object value = mapping.invoke(object);
						// comparing current object with the MapEntry's key
						int comparison = comparator.compare(value, mapEntry.getKey());
						// MapEntry's value is a List
						LinkedList objects = (LinkedList)mapEntry.getValue();
	
						if (comparison <= 0) {
							if (comparison < 0) {
								objects.clear();
								mapEntry.setKey(value);
							}
							objects.add(object); // if comparison <= 0 object is added to the List
						}
						return mapEntry;
					}
				}
			}
		);
		this.mapping = mapping;
	}

	/**
	 * Creates a new minimator using a default
	 * {@link xxl.core.comparators.ComparableComparator comparator} which
	 * assumes that the keys implement the
	 * {@link java.lang.Comparable comparable} interface to compare the next
	 * input element with the current minima. If an iterator is given to this
	 * constructor it is wrapped to a cursor.
	 *
	 * @param iterator the input iteration providing the data to be minimized.
	 * @param mapping the function used to map an input element to a certain key.
	 */
	public Minimator(Iterator iterator, Function mapping) {
		this(iterator, mapping, ComparableComparator.DEFAULT_INSTANCE);
	}

	/**
	 * Creates a new minimator. The keys of the map entries are determined using
	 * the {@link xxl.core.functions.Function#IDENTITY identity} function. If an
	 * iterator is given to this constructor it is wrapped to a cursor.
	 *
	 * @param iterator the input iteration providing the data to be minimized.
	 * @param comparator the comparator used to determine whether the current
	 *        object is smaller than the current minima.
	 */
	public Minimator(Iterator iterator, Comparator comparator) {
		this(iterator, Function.IDENTITY, comparator);
	}

	/**
	 * Creates a new minimator using a default
	 * {@link xxl.core.comparators.ComparableComparator comparator} which
	 * assumes that the keys implement the
	 * {@link java.lang.Comparable comparable} interface to compare the next
	 * input element with the current minima. The keys of the map entries are
	 * determined using the {@link xxl.core.functions.Function#IDENTITY identity}
	 * function. If an iterator is given to this constructor it is wrapped to a
	 * cursor.
	 *
	 * @param iterator the input iteration providing the data to be minimized.
	 */
	public Minimator(Iterator iterator) {
		this(iterator, ComparableComparator.DEFAULT_INSTANCE);
	}

	/**
	 * The main method contains some examples to demonstrate the usage and the
	 * functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to submit
	 *        parameters when the main method is called.
	 */
	public static void main(String[] args) {

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		
		Minimator minimator = new Minimator(
			new xxl.core.cursors.sources.RandomIntegers(100, 10)
		);
		
		minimator.open();
		
		while (minimator.hasNext())
			System.out.print(((MapEntry)minimator.next()).getKey() +"; ");
		System.out.flush();
		System.out.println();
		
		minimator.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		Object[] persons = new Object[] {
			new Object[] {new String("Tobias"), new Integer(23)},
			new Object[] {new String("Juergen"), new Integer(23)},
			new Object[] {new String("Martin"), new Integer(26)},
			new Object[] {new String("Bjoern"), new Integer(28)},
			new Object[] {new String("Jens"), new Integer(27)},
			new Object[] {new String("Bernhard"), new Integer(35)},
			new Object[] {new String("Jochen"), new Integer(29)},
		};// Object[0] --> name, Object[1] --> age
		minimator = new Minimator(
			new xxl.core.cursors.sources.ArrayCursor(persons),
			new Function() {
				public Object invoke(Object person) {
					return (Integer)((Object[])person)[1];
				}
			},
			xxl.core.comparators.InverseComparator.DEFAULT_INSTANCE
		);
		
		minimator.open();
		
		Iterator results;
		while (minimator.hasNext()) {
			results = ((LinkedList)((MapEntry)minimator.next()).getValue()).listIterator(0);
			while (results.hasNext())
				System.out.print("Name: " + ((Object[])results.next())[0] + "; ");
			System.out.flush();
			System.out.println();
		}
		
		minimator.close();
	}
}
