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

package xxl.core.math.statistics.nonparametric.aggregates;

import xxl.core.cursors.mappers.Aggregator;
import xxl.core.cursors.mappers.ReservoirSampler;
import xxl.core.cursors.sources.ContinuousRandomNumber;
import xxl.core.functions.Function;
import xxl.core.math.functions.RealFunction;
import xxl.core.math.statistics.nonparametric.EmpiricalCDF;
import xxl.core.math.statistics.parametric.aggregates.OnlineAggregation;
import xxl.core.util.random.JavaContinuousRandomWrapper;

/** In the context of online aggregation, running aggregates are built. Given an 
 * iterator of data, an {@link xxl.core.cursors.mappers.Aggregator Aggregator}
 * computes iteratively aggregates. For instance, the current maximum
 * of the already processed data is determined. An internal aggregation function processes
 * the computation of the new element by consuming the old aggregate and the new element
 * from the input cursor.
 * 
 * Generally, each aggregation function must support a function call of the following type:<br>
 * <tt>agg_n = f (agg_n-1, next)</tt>. <br>
 * There, <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps,
 * <tt>f</tt> represents the aggregation function,
 * <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps
 * and <tt>next</tt> the next object to use for computation.
 * <br>
 * 
 * This class implements an aggregation function that computes the <tt>empirical cumulative distribution function</tt>. 
 * Given an old and a new sample as processed objects, this function computes the empirical cdf based on the new sample. 
 * <br>
 * Consider the following example that displays a concrete application of a
 * empirical cdf aggregation function combined with an aggregator:
 * <code><pre>
 	Aggregator aggregator =
			new Aggregator(
				new Aggregator( input, mapSamplingStrategy (sampleSize, samplingType)),
				new EmpiricalCDFAggregateFunction()
			);
 * </pre></code>
 * 
 * @see xxl.core.cursors.mappers.Aggregator
 * @see xxl.core.math.functions.AdaptiveAggregationFunction
 * @see xxl.core.math.statistics.nonparametric.aggregates.Aggregators
 * @see xxl.core.math.statistics.nonparametric.EmpiricalCDF
 */

public class EmpiricalCDFAggregateFunction extends Function implements OnlineAggregation {

	/** internal cdf based upon the empirical distribution function*/
	protected EmpiricalCDF empse;

	/** internal sample storage */
	protected Object[] sample;

	/** Constructs a new EmpiricalCDFAggregateFunction.
	 *
	 */
	public EmpiricalCDFAggregateFunction() {}

	/** Two-figured function call for supporting aggregation by this function.
	 * Each aggregation function must support a function call like <tt>agg_n = f (agg_n-1, next)</tt>,
	 * where <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps, <tt>f</tt>
	 * the aggregation function, <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps
	 * and <tt>next</tt> the next object to use for computation.
	 * This method delivers only <tt>null</tt> as aggregation result as long as the aggregation
	 * has not yet initialized.
	 * As result of the aggregation the empirical cdf, that relies on the current block, is returned.
	 * 
	 * @param old result of the aggregation function in the previous computation step
	 * @param next next object used for computation
	 * @return aggregation value after n steps
	 * (an {@link xxl.core.math.statistics.nonparametric.EmpiricalCDF empirical cdf}
	 */
	public Object invoke(Object old, Object next) { // next = sample
		if (next == null)
			return null;
		// if given next (Object[]) != null, all fields of the array are filled
		sample = (Object[]) next;
		if (empse == null)
			empse = new EmpiricalCDF(sample);
		else
			empse.setSample(sample);
		return empse;
	}

	/** Returns the current status of the on-line aggregation function
	 * implementing the OnlineAggregation interface.
	 * 
	 * @return the current state of this function
	 */
	public Object getState() {
		if (empse != null) {
			return sample;
		} else
			return null;
	}

	/** Sets a new status of the on-line aggregation function
	 * implementing the OnlineAgggregation interface (optional).
	 * This method is not supported by this class.
	 * Thus, it is implemented by throwing an UnsupportedOperationException.
	 * 
	 * @param status current state of the function
	 * @throws UnsupportedOperationException if this method is not supported by this class
	 */
	public void setState(Object status) {
		throw new UnsupportedOperationException("not supported");
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
		/*                            Example                                */
		/*********************************************************************/
		System.out.println("Computing empirical cdf iteratively over a Cursor with random number based on reservoir sampling:");
		int N = 2000;
		xxl.core.cursors.Cursor cursor = new ContinuousRandomNumber(new JavaContinuousRandomWrapper(), N);
		Aggregator aggregator =
			new Aggregator(
				new Aggregator(
					cursor,
					xxl.core.math.statistics.nonparametric.aggregates.Aggregators.mapSamplingStrategy(
						100,
						ReservoirSampler.RTYPE
					)
				),
				new EmpiricalCDFAggregateFunction()
			);
		double[] grid = xxl.core.util.DoubleArrays.equiGrid(0.0, 1.0, 100);
		RealFunction last = (RealFunction) aggregator.last();
		// --- output ---
		System.out.println("");
		System.out.println("Evaluating resulting empirical cdf:");
		for (int i = 0; i < grid.length; i++) {
			System.out.print(grid[i] + "\t" + " " + last.eval(grid[i]));
			System.out.println();
		}
	}
}
