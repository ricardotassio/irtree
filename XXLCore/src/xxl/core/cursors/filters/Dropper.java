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

import xxl.core.cursors.SecureDecoratorCursor;

/**
 * A dropper drops a user defined number of the given iterator's elements. The
 * implementation of the method <tt>drop</tt> is as follows:
 * <pre>
 *     while (number--&gt;0 && super.hasNext());
 *         super.next();
 * </pre>
 * The first <tt>number</tt> elements are removed from the wrapped iteration,
 * because the <tt>next</tt> method is called <tt>number</tt> times.
 * 
 * <p><b>Example usage:</b>
 * <pre>
 *     Dropper dropper = new Dropper(new Enumerator(11), 5);
 *     
 *     dropper.open();
 * 
 *     while(dropper.hasNext())
 *         System.out.println(dropper.next());
 * 
 *     dropper.close();
 * </pre>
 * This example creates a new dropper by using an
 * {@link xxl.core.cursors.sources.Enumerator enumerator} delivering integer
 * elements from 0 to 10, where the first five elements are dropped. Therefore
 * the generated output is:
 * <pre>
 *     5
 *     6
 *     7
 *     8
 *     9
 *     10
 * </pre>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.SecureDecoratorCursor
 */
public class Dropper extends SecureDecoratorCursor {

	/**
	 * The initial number of elements to be dropped.
	 */
	protected int initialNumber;

	/**
	 * The number of elements that still have to be dropped.
	 */
	protected int number;

	/**
	 * Creates a new dropper that drops the first <tt>number</tt> elements of the
	 * given iteration.
	 *
	 * @param iterator the input iterator the elements are dropped of.
	 * @param number the number of elements to be dropped.
	 */
	public Dropper(Iterator iterator, int number) {
		super(iterator);
		this.number = initialNumber = number;
	}

	/**
	 * Dropping the first <tt>number</tt> elements of the wrapped iteration.
	 */
	public void drop() {
		while (number-->0 && super.hasNext())
			super.next();
	}
	
	/**
	 * Opens the cursor, i.e., signals the dropper to drop the first
	 * <tt>number</tt> elements. Before a cursor has been opened calls to methods
	 * like <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield proper
	 * results. Therefore <tt>open</tt> must be called before a cursor's data can
	 * be processed. Multiple calls to <tt>open</tt> do not have any effect,
	 * i.e., if <tt>open</tt> was called the cursor remains in the state
	 * <i>opened</i> until its <tt>close</tt> method is called.
	 * 
	 * <p>Note, that a call to the <tt>open</tt> method of a closed cursor
	 * usually does not open it again because of the fact that its state
	 * generally cannot be restored when resources are released respectively
	 * files are closed.</p>
	 */
	public void open() {
		super.open();
		drop();
	}

	/**
	 * Resets the input cursor to its initial state, and drops the first
	 * <tt>number</tt> elements again (optional operation). That means the caller
	 * is able to traverse the underlying data structure again. The
	 * modifications, removes and updates concerning the underlying data
	 * structure, are still persistent.
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> method is not
	 *         supported by this dropper, i.e., if the input cursor does not
	 *         support the <tt>reset</tt> operation.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		number = initialNumber;
		drop();
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
		
		Dropper dropper = new Dropper(new xxl.core.cursors.sources.Enumerator(11), 5);
		
		dropper.open();
		
		while(dropper.hasNext())
			System.out.println(dropper.next());
		
		dropper.close();
	}
}
