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

package xxl.core.cursors.sources;

import xxl.core.cursors.AbstractCursor;

/**
 * An enumerator returns an ascending or descending sequence of integer objects
 * within an optional given range. There are three different ways to generate
 * integer objects with an enumerator:
 * <ul>
 *     <li>
 *         Specifying a range, i.e., the start- and end-position are user
 *         defined.
 *     </li>
 *     <li>
 *         Specifying only the end-position; <tt>start</tt> = <tt>0</tt>.
 *     </li>
 *     <li>
 *         Specifying no range; <tt>start</tt> = <tt>0</tt>, <tt>end</tt> =
 *         {@link java.lang.Integer#MAX_VALUE}.
 *     </li>
 * </ul>
 * In the first case an ascending or a descending sequence can be generated
 * depending on the given start- and end-position.<br />
 * <b>Note:</b> The start-position of the integer sequence will be returned by
 * the enumerator, but not the end-position! So only the integer elements of the
 * interval [<tt>start</tt>, <tt>end</tt>) will be returned by the enumerator.
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     Enumerator enumerator = new Enumerator(0, 11);
 * 
 *     enumerator.open();
 * 
 *     while (enumerator.hasNext())
 *         System.out.println(enumerator.next());
 * 
 *     enumerator.close();
 * </pre>
 * This example prints the numbers 0,...,10 to the output stream.</p>
 * 
 * <p><b>Example usage (2):</b>
 * <pre>
 *     enumerator = new Enumerator(10, -1);
 * 
 *     enumerator.open();
 * 
 *     while (enumerator.hasNext())
 *         System.out.println(enumerator.next());
 * 
 *     enumerator.close();
 * </pre>
 * This example prints the numbers 10,...,0 to the output stream.</p>
 * 
 * <p><b>Example usage (3):</b>
 * <pre>
 *     enumerator = new Enumerator(11);
 * 
 *     enumerator.open();
 * 
 *     while (enumerator.hasNext())
 *         System.out.println(enumerator.next());
 * 
 *     enumerator.close();
 * </pre>
 * This example prints the numbers 0,...,10 to the output stream using only a
 * specified end position.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 */
public class Enumerator extends AbstractCursor {

	/**
	 * The start of the returned integer sequence (inclusive).
	 */
	protected int from;

	/**
	 * The end of the returned integer sequence (exclusive).
	 */
	protected int to;

	/**
	 * The int value returned by the next call to <tt>next</tt> or <tt>peek</tt>.
	 */
	protected int nextInt;

	/**
	 * If <tt>true</tt> the sequence is ascending, else the sequence is
	 * descending.
	 */
	protected boolean up;

	/**
	 * Creates a new enumerator instance with a specified range, i.e., the start-
	 * and an end-position must be defined.
	 *
	 * @param from start of the returned integer sequence (inclusive).
	 * @param to end of the returned integer sequence (exclusive).
	 */
	public Enumerator(int from, int to) {
		this.from = from;
		this.to = to;
		this.up = from <= to;
		this.nextInt = from;
	}

	/**
	 * Creates a new enumerator instance with a user defined end position, i.e.,
	 * the returned integer sequence starts with <tt>0</tt> and ends with
	 * <tt>number-1</tt>.
	 *
	 * @param number the end of the returned integer sequence (exclusive).
	 */
	public Enumerator(int number) {
		this(0, number);
	}

	/**
	 * Creates an enumerator instance. The returned integer sequence starts with
	 * <tt>0</tt> and ends with {@link java.lang.Integer#MAX_VALUE}<tt>-1</tt>.
	 */
	public Enumerator() {
		this(0, Integer.MAX_VALUE);
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the enumerator has more elements.
	 */
	protected boolean hasNextObject() {
		return up ? nextInt < to : nextInt > to;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the enumerator's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject () {
		return new Integer(up ? nextInt++ : nextInt--);
	}

	/**
	 * Resets the enumerator to its initial state such that the caller is able to
	 * traverse the iteration again without constructing a new enumerator
	 * (optional operation).
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the enumerator.
	 */
	public void reset () throws UnsupportedOperationException {
		super.reset();
		nextInt = from;
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the enumerator. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the enumerator, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return true;
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
		
		Enumerator enumerator = new Enumerator(0, 11);
		
		enumerator.open();
		
		while (enumerator.hasNext())
			System.out.println(enumerator.next());
		
		enumerator.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		enumerator = new Enumerator(10, -1);
		
		enumerator.open();
		
		while (enumerator.hasNext())
			System.out.println(enumerator.next());
		
		enumerator.close();

		/*********************************************************************/
		/*                            Example 3                              */
		/*********************************************************************/
		
		enumerator = new Enumerator(11);
		
		enumerator.open();
		
		while (enumerator.hasNext())
			System.out.println(enumerator.next());
		
		enumerator.close();
	}
}
