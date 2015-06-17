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

package xxl.core.cursors.filters;

import java.util.Iterator;
import java.util.NoSuchElementException;

import xxl.core.cursors.SecureDecoratorCursor;

/**
 * A taker is a {@link xxl.core.cursors.SecureDecoratorCursor decorator-cursor} that
 * returns the next <tt>number</tt> elements of a given iteration. The
 * <tt>number</tt> of elements that should be delivered from the given iteration
 * is defined by the user as well as the input iteration the elements should be
 * taken from.
 * 
 * <p><b>Implementation details:</b> The taker only contains further elements,
 * i.e., its <tt>hasNext</tt> method returns <tt>true</tt>, if the underlying
 * iteration has more elements and the number of elements returned by the taker
 * is lower than the given <tt>number</tt>. If <tt>next</tt> is called the next
 * element in the iteration is returned and the <tt>number</tt> of elements to be
 * delivered from the underlying input iteration is decremented.</p>
 * 
 * <p><b>Note:</b> If the input iteration is given by an object of the class
 * {@link java.util.Iterator}, i.e., it does not support the <tt>peek</tt>
 * operation, it is internally wrapped to a cursor.</p>
 *
 * <p><b>Example usage:</b>
 * <pre>
 *     Taker taker = new Taker(new Enumerator(11), 5);
 * 
 *     taker.open();
 * 
 *     while (taker.hasNext())
 *         System.out.print(taker.next() +"; ");
 *     System.out.flush();
 * 
 *     taker.close();
 * </pre>
 * This instance of a taker delivers the first five elements of the given
 * {@link xxl.core.cursors.sources.Enumerator enumerator} which contains the
 * elements 0,...,10. So the output that results after consuming the entire
 * taker is:
 * <pre>
 *     0; 1; 2; 3; 4;
 * </pre></p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.SecureDecoratorCursor
 */
public class Taker extends SecureDecoratorCursor {

	/**
	 * The number of elements that still have to be delivered.
	 */
	protected int number;

	/**
	 * The initial number of elements to be delivered.
	 */
	protected int initialNumber;

	/**
	 * Creates a new taker.
	 *
	 * @param iterator the iterator which contains the elements.
	 * @param number the number of elements which should be delivered from the
	 *        given iterator.
	 */
	public Taker(Iterator iterator, int number) {
		super(iterator);
		this.number = initialNumber = number;
	}

	 /**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other words,
	 * returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would return an
	 * element rather than throwing an exception.) In this case <tt>hasNext</tt>
	 * returns <tt>true</tt> if the underlying iteration has more elements and
	 * the number of elements returned by the taker is lower than the given
	 * <tt>number</tt>.
	 *
	 * @return <tt>true</tt> if the taker has more elements.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 */
	public boolean hasNext() throws IllegalStateException {
		return super.hasNext() && number>0;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more. In this
	 * case the number of elements that still have to be returned is decremented.
	 *
	 * @return the next element in the iteration.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 * @throws NoSuchElementException if the iteration has no more elements.
	 */
	public Object next() throws IllegalStateException, NoSuchElementException {
		number--;
		return super.next();
	}

	/**
	 * Resets the taker to its initial state such that the caller is able to
	 * traverse the underlying data structure again without constructing a new
	 * cursor (optional operation). The modifications, removes and updates
	 * concerning the underlying data structure, are still persistent. In this
	 * case the input iteration has to be reset and the number of elements that
	 * have to be returned by this taker has to be set to its initial value.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the taker.
	 */
	public void reset () throws UnsupportedOperationException {
		super.reset();
		number = initialNumber;
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
		
		Taker taker = new Taker(new xxl.core.cursors.sources.Enumerator(11), 5);
		
		taker.open();
		
		while (taker.hasNext())
			System.out.print(taker.next() +"; ");
		System.out.flush();
		
		taker.close();
	}
}
