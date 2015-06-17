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

package xxl.core.cursors.sorters;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import xxl.core.collections.MapEntry;
import xxl.core.comparators.FeatureComparator;
import xxl.core.cursors.SecureDecoratorCursor;
import xxl.core.cursors.mappers.Mapper;
import xxl.core.cursors.sources.DiscreteRandomNumber;
import xxl.core.functions.Function;
import xxl.core.util.random.JavaDiscreteRandomWrapper;

/**
 * A shuffle-cursor is a
 * {@link xxl.core.cursors.SecureDecoratorCursor decorator-cursor}, that permutes the
 * input iteration's elements randomly. This cursor is semilazy. Do not use it
 * with potentially infinite streams. This operator internally builds
 * {@link xxl.core.collections.MapEntry map entries} consisting of an element of
 * the input iteration (key) and a randomly distributed integer value (value)
 * delivered by the given iteration over
 * {@link xxl.core.cursors.sources.DiscreteRandomNumber discrete} random numbers.
 * These map entries are sorted according to their values and the result is a
 * shuffled output of the input iteration.
 * 
 * <p><b>Example usage :</b>
 * <pre>
 *     ShuffleCursor shuffler = new ShuffleCursor(
 *         new Enumerator(11),
 *         new DiscreteRandomNumber(
 *             new JavaDiscreteRandomWrapper(new Random())
 *         )
 *     );
 * 
 *     shuffler.open();
 * 
 *     while (shuffler.hasNext())
 *         System.out.println(shuffler.next());
 * 
 *     shuffler.close();
 * </pre>
 * This example demonstrates the shuffle-cursor functionality. The shuffle-cursor
 * returns the same set of elements of the input enumerator, but in a random
 * order. The random numbers are taken from an iteration over discrete random
 * number that has to be passed at construction time.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.SecureDecoratorCursor
 */
public class ShuffleCursor extends SecureDecoratorCursor {

	/**
	 * Creates a new shuffle-cursor.
	 *
	 * @param iterator the input iteration containing the elements to be
	 *        shuffled.
	 * @param randomCursor a cursor that delivers an infinite stream of randomly
	 *        distributed integer objects (discrete random numbers).
	 */
	public ShuffleCursor(Iterator iterator, final DiscreteRandomNumber randomCursor) {
		// Elemente verpacken
		super(
			new MergeSorter(
				new Mapper(
					iterator,
					new Function() {
						public Object invoke(Object o) {
							return new MapEntry(o, randomCursor.next());
						}
					}
				),
				new FeatureComparator(
					new Function() {
						public Object invoke(Object mapEntry) {
							return ((MapEntry)mapEntry).getValue();
						}
					}
				),
				12,
				12*4096,
				4*4096
			)
		);
	}

	/**
	 * Creates a new ShuffleCursor with the random number generator provided by
	 * Java.
	 *
	 * @param iterator the input iteration containing the elements to be
	 *        shuffled.
	 */
	public ShuffleCursor(Iterator iterator) {
		this(iterator, new DiscreteRandomNumber(new JavaDiscreteRandomWrapper(new Random())));
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the shuffle-cursor's methods, e.g., <tt>update</tt>
	 * or <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 *
	 * @return the next element in the iteration.
	 * @throws IllegalStateException if the shuffle-cursor is already closed when
	 *         this method is called.
	 * @throws NoSuchElementException if the iteration has no more elements.
	 */
	public Object next() throws IllegalStateException, NoSuchElementException {
		return ((MapEntry)super.next()).getKey();
	}

	/**
	 * Shows the next element in the iteration without proceeding the iteration
	 * (optional operation). After calling <tt>peek</tt> the returned element is
	 * still the shuffle-cursor's next one such that a call to <tt>next</tt>
	 * would be the only way to proceed the iteration.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors. After calling the <tt>peek</tt> method a call to <tt>next</tt>
	 * is strongly recommended.</p> 
	 *
	 * @return the next element in the iteration.
	 * @throws IllegalStateException if the shuffle-cursor is already closed when
	 *         this method is called.
	 * @throws NoSuchElementException iteration has no more elements.
	 * @throws UnsupportedOperationException if the <tt>peek</tt> operation is
	 *         not supported by the shuffle-cursor.
	 */
	public Object peek() throws IllegalStateException, NoSuchElementException, UnsupportedOperationException {
		return ((MapEntry)super.peek()).getKey();
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
		
		ShuffleCursor shuffler = new ShuffleCursor(
			new xxl.core.cursors.sources.Enumerator(11),
			new DiscreteRandomNumber(
				new JavaDiscreteRandomWrapper(
					new Random()
				)
			)
		);
		
		shuffler.open();
		
		while (shuffler.hasNext())
			System.out.println(shuffler.next());
		
		shuffler.close();
	}
}
