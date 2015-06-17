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
import xxl.core.cursors.sources.RandomIntegers;
import xxl.core.functions.Function;

/**
 * Computes the standard deviation step-by-step on 
 * {@link java.lang.Number numerical data} without any error control
 * using {@link xxl.core.math.statistics.parametric.aggregates.Variance}.
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
 * function has not yet fully initialized.
 * Objects of this type have a two-step phase for initialization.
 * <br>
 * Also objects of this class are using internally stored information to obtain the standard deviation, 
 * objects of this type don't support online features.
 * See {@link xxl.core.math.statistics.parametric.aggregates.OnlineAggregation OnlineAggregation} for further details about 
 * aggregation function using internally stored information supporting <tt>online aggregation</tt>.
 *
 * @see xxl.core.cursors.mappers.Aggregator
 * @see xxl.core.functions.Function
 */

public class StandardDeviation extends Function {

	/** internally used Function for recursive computing of internally used variance */
	protected Function var;

	/** internally used variable storing the variance of the processed data */
	protected Double v;

	/** Two-figured function call for supporting aggregation by this function.
	 * Each aggregation function must support a function call like <tt>agg_n = f (agg_n-1, next)</tt>,
	 * where <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps, <tt>f</tt>
	 * the aggregation function, <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps
	 * and <tt>next</tt> the next object to use for computation.
	 * This method delivers only <tt>null</tt> as aggregation result as long as the aggregation
	 * has not yet initialized.
	 * 
	 * Objects of this type have a two-step phase for initialization.
	 * @param stddev result of the aggregation function in the previous computation step
	 * @param next next object used for computation
	 * @return aggregation value after n steps
	 * (the new computed standard deviation based upon the internally stored
	 * status and the next given object as an object of type <tt>Double</tt>).
	 */
	public Object invoke(Object stddev, Object next) {
		// reinit if a previous aggregation value == null is given
		if (stddev == null) {
			var = new Variance();
			v = (Double) var.invoke(null, next);
			return new Double(0.0);
		} else {
			v = (Double) var.invoke(v, next);
			return new Double(Math.sqrt(v.doubleValue()));
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

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		Aggregator aggregator1 = 
			new Aggregator(
				new RandomIntegers(100, 50), // the input-Cursor
				new StandardDeviation() // aggregate function
			);
		System.out.println(
			"The result of the std-dev aggregation of 100 randomly distributed integers is: " + aggregator1.last());
		aggregator1.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		Aggregator aggregator2 =
				new Aggregator(
					xxl.core.cursors.sources.Inductors.naturalNumbers(1, 100), // the input-Cursor
					new StandardDeviation() // aggregate function
				);
		System.out.println(
			"\nThe result of the std-dev aggregation of the natural numbers from 1 to 100 is: " + aggregator2.last());
		aggregator2.close();
	}
}