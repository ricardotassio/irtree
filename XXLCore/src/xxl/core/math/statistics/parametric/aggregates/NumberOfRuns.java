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

import java.util.Comparator;

import xxl.core.comparators.ComparableComparator;
import xxl.core.cursors.mappers.Aggregator;
import xxl.core.cursors.sources.RandomIntegers;
import xxl.core.functions.Function;

/**
 * Computes the number of runs in a given sequence with respect to an ordering 
 * imposed by a {@link java.util.Comparator comparator}. There, a run is an ordered
 * sequence.
 * For an overview of adaptive sorting strategies and sortness measures of datasets have a look at<BR>
 * Vladimir Estivill-Castro and Derick Wood:  A Survey of Adaptive Sorting Algorithms, ACM Computing Surveys,
 * volume 24, number 4, pages 441 - 476, 1992.<BR>
 *
 * <br>
 * <p><b>Objects of this type are recommended for the usage with aggregator cursors!</b></p>
 * <br>
 * Each aggregation function must support a function call of the following type:<br>
 * <tt>agg_n = f (agg_n-1, next)</tt>, <br>
 * where <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps,
 * <tt>f</tt> the aggregation function,
 * <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps
 * and <tt>next</tt> the next object to use for computation.
 * An aggregation function delivers just <tt>null</tt> as aggregation result as long as the aggregation
 * function has not yet fully initialized.
 * <br>
 * Objects of this class use internally stored informations about the sequence seen so far.
 * Thus, objects of this type have a status.
 * 
 * Consider the following example:
 * <code><pre>
 * Aggregator aggregator = new Aggregator( 
		new RandomIntegers( 20, 40), 
		new NumberOfRuns()
	);
 * <\code><\pre>
 * <br>
 * 
 * @see xxl.core.cursors.mappers.Aggregator
 * @see xxl.core.functions.Function
 * @see java.util.Comparator
 */

public class NumberOfRuns extends Function {

	/** comparator imposing the total ordering used for determining the sequences of the given data */
	protected Comparator comparator;

	/** stores the last seen object for comparing */
	protected Object lastSeenObject = null;

	/** Constructs a new object of this class.
	 * 
	 * @param comparator imposes the total ordering used for determining the sequences of the given data
	 */
	public NumberOfRuns(Comparator comparator) {
		this.comparator = comparator;
	}

	/** Constructs a new object of this class using the 'natural ordering' of the treated objects.
	 * 
	 * @see xxl.core.comparators.ComparableComparator
	 * @see xxl.core.comparators.ComparableComparator#DEFAULT_INSTANCE
	 */
	public NumberOfRuns() {
		this(ComparableComparator.DEFAULT_INSTANCE);
	}

	/** Two-figured function call for supporting aggregation by this function.
	 * Each aggregation function must support a function call like <tt>agg_n = f (agg_n-1, next)</tt>,
	 * where <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps, <tt>f</tt>
	 * the aggregation function, <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps
	 * and <tt>next</tt> the next object to use for computation.
	 * This method delivers just <tt>null</tt> as aggregation result as long as the aggregation
	 * has not yet initialized.
	 * 
	 * @param old result of the aggregation function in the previous computation step (number of runs counted so far)
	 * @param next next object used for computation
	 * @return aggregation value after n steps
	 */
	public Object invoke(Object old, Object next) {
		if (old == null) { // initializing
			lastSeenObject = next;
			return new Integer(1);
		} else {
			if (lastSeenObject == null) { // reinitializing if null-Objects are given
				lastSeenObject = next;
				return new Integer(1);
			} else {
				int runs = ((Number) old).intValue();
				if (comparator.compare(next, lastSeenObject) < 0)
					runs++;
				lastSeenObject = next;
				return new Integer(runs);
			}
		}
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main(String[] args) {
		/* ------------------------------------------------------------------------
		   - Computes the number of runs in a random sequence of Integer-Objects  -
		   ------------------------------------------------------------------------ */
		java.util.List l = xxl.core.cursors.Cursors.toList(new RandomIntegers(20, 40));
		xxl.core.cursors.mappers.Aggregator aggregator = new Aggregator(l.iterator(), new NumberOfRuns());
		java.util.Iterator it = l.iterator();
		System.out.println("sequence\tnumber of runs counted so far");
		while (aggregator.hasNext())
			System.out.println(it.next() + ":\t" + aggregator.next());
	}
}