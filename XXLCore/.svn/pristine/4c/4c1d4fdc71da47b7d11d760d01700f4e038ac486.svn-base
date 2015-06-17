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

package xxl.core.math.statistics.parametric.aggregates;

import xxl.core.cursors.mappers.Aggregator;
import xxl.core.functions.Function;

/**
 * Stores the last 'seen' n objects.
 * <br>
 * <p><b>Objects of this type are recommended for the usage with aggregator cursors!</b></p>
 * <br>
 * Each aggregation function must support a function call of the following type:<br>
 * <tt>agg_n = f (agg_n-1, next)</tt>, <br>
 * where <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps,
 * <tt>f</tt> the aggregation function,
 * <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps
 * and <tt>next</tt> the next object to use for computation.
 * An aggregation function delivers only <tt>null</tt> as aggregation result as long as the aggregation
 * function has not yet fully initialized, meaning to this class the first (n-1)-th delivered
 * objects are <tt>null</tt>!
 * 
 * Consider the following example:
 * <code><pre>
 * Aggregator agg = new Aggregator(
		it ,			// input-Cursor
		new LastN( 3)	// aggregate function
	);
 * <\code><\pre>
 * <br>
 *
 * @see xxl.core.cursors.mappers.Aggregator
 * @see xxl.core.functions.Function
 */

public class LastN extends Function {

	/** number of objects to store */
	protected int n;

	/** position of the number in the internally stored array to substitute next */
	protected int pos;

	/** internally used object storage */
	protected Object[] store;

	/** indicates whether the storage has been initialized or not */
	protected boolean init;

	/** Constructs a new object of this type.
	 * 
	 * @param n number of objects to store
	 * @throws IllegalArgumentException if a number less or equal 0 is given
	 */
	public LastN(int n) throws IllegalArgumentException {
		if (n < 1)
			throw new IllegalArgumentException("Can't store " + n + " numbers! There must be given a number n >= 1!");
		this.n = n;
		pos = 0;
		init = false;
		store = new Object[n];
	}

	/** Two-figured function call for supporting aggregation by this function.
	 * Each aggregation function must support a function call like <tt>agg_n = f (agg_n-1, next)</tt>,
	 * where <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps, <tt>f</tt>
	 * the aggregation function, <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps
	 * and <tt>next</tt> the next object to use for computation.
	 * This method delivers only <tt>null</tt> as aggregation result as long as the aggregation
	 * has not yet initialized.
	 * 
	 * @param oldStore result of the aggregation function of the last n seen 
	 * numbers in the previous computation step
	 * @param next next object used for computation
	 * @return object array with the last n seen elements
	 */
	public Object invoke(Object oldStore, Object next) {
		// reinit if a previous aggregation value == null is given
		// and the aggregation function has already been initialized
		if ((oldStore == null) && (init)) {
			pos = 0;
			init = false;
			store = new Object[n];
		}
		store[pos] = next;
		pos++;
		if (pos == n) {
			init = true;
			pos = 0;
		}
		return init ? store : null;
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main(String[] args) {
		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		int n = 3;
		java.util.Iterator it = xxl.core.cursors.sources.Inductors.naturalNumbers(0, 20);
		System.out.println("Establishing temporal memory of size " + n);
		Aggregator agg = 
			new Aggregator(
				it, // the input-Cursor
				new LastN(3) // aggregate function
			);
		while (agg.hasNext()) {
			System.out.println(
				"Last seen " + n + " objects: " + xxl.core.util.Strings.toString((Object[]) agg.next(), ", "));
		}
		agg.close();
	}
}