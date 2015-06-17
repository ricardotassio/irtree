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
 * Computes the variance in a recursive manner
 * using an algorithm based upon [Wes79]:
 * D.H.D. West, Updating mean and Variance Estimates: An improved Method, 
 * Comm. Assoc. Comput. Mach., 22:532-535, 1979 <BR> and <BR>
 * [CGL83]: Chan, T.F., G.H. Golub, & R.J. LeVeque,
 * Algorithms for Computing the Sample Variance: Analysis and Recommendations,
 * The American Statistician Vol 37 1983: 242-247. <BR>
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
 * As result of the aggregation, an estimator for the true variance is returned, not the sample variance!
 * Also objects of this class are using internally stored information to obtain the standard deviation, 
 * objects of this type don't support on-line features.
 * See {@link xxl.core.math.statistics.parametric.aggregates.OnlineAggregation OnlineAggregation} for further details about 
 * aggregation function using internally stored information supporting <tt>on-line aggregation</tt>.
 *
 * 
 * @see xxl.core.cursors.mappers.Aggregator
 * @see xxl.core.functions.Function
 * @see xxl.core.math.statistics.parametric.aggregates.StandardDeviation
 */

public class Variance extends Function {

	/**
	 * value of the next object
	 */
	private double xi;

	/**
	 * 
	 */
	private double sk;

	/**
	 * variance
	 */
	private double vk;

	/**
	 * number of steps
	 */
	protected long n;

	/** Constructs a new Object of type Variance */
	public Variance() {}

	/** Two-figured function call for supporting aggregation by this function.
	 * Each aggregation function must support a function call like <tt>agg_n = f (agg_n-1, next)</tt>,
	 * where <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps, <tt>f</tt>
	 * the aggregation function, <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps
	 * and <tt>next</tt> the next object to use for computation.
	 * This method delivers only <tt>null</tt> as aggregation result as long as the aggregation
	 * has not yet initialized.
	 * Objects of this type have a two-step phase for initialization.
	 * 
	 * @param variance result of the aggregation function in the previous computation step
	 * @param next next object used for computation
	 * @return aggregation value after n steps
	 */
	public Object invoke(Object variance, Object next) {
		xi = ((Number) next).doubleValue();
		if (variance == null) {
			n = 1;
			sk = 0.0;
			sk = sk + xi;
			vk = 0.0;
			return new Double(0.0);
		}
		n++;
		vk += (Math.pow((sk - (n - 1) * xi), 2.0) / n) / (n - 1);
		sk += xi;
		return new Double(vk / (n)); // returning the true variance, not the sample variance (an estimator)
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
				new Variance() // aggregate functions
			);
		Object l1 = aggregator1.last();
		aggregator1.close();
		System.out.println("The result of the variance aggregation of 100 randomly distributed integers is: " + l1);

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		Aggregator aggregator2 =
			new Aggregator(
				xxl.core.cursors.sources.Inductors.naturalNumbers(1, 100), // the input-Cursor
				new Variance() // aggregate function
			);
		System.out.println(
			"\nThe result of the variance aggregation of the natural numbers from 1 to 100 is: " + aggregator2.last());
		aggregator2.close();
	}
}