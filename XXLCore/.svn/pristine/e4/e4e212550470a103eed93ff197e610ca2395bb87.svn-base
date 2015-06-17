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

package xxl.core.math.functions;

import java.util.Iterator;

import xxl.core.cursors.mappers.Aggregator;
import xxl.core.cursors.mappers.Mapper;
import xxl.core.cursors.sources.RandomIntegers;
import xxl.core.functions.Function;
import xxl.core.functions.Functions;
import xxl.core.math.Statistics;
import xxl.core.math.numerics.splines.CubicBezierSpline;
import xxl.core.math.numerics.splines.RB1CubicBezierSpline;
import xxl.core.math.statistics.nonparametric.aggregates.NKDEAggregateFunction;
import xxl.core.math.statistics.nonparametric.kernels.EpanechnikowKernel;
import xxl.core.math.statistics.parametric.aggregates.Maximum;
import xxl.core.math.statistics.parametric.aggregates.Minimum;
import xxl.core.math.statistics.parametric.aggregates.OnlineAggregation;
import xxl.core.math.statistics.parametric.aggregates.ReservoirSample;
import xxl.core.math.statistics.parametric.aggregates.Variance;
import xxl.core.predicates.Predicate;
import xxl.core.util.DoubleArrays;
import xxl.core.util.Strings;

/**
 * This class provides a compression algorithm based upon the approximation of real-valued
 * one-dimensional functions by a cubic Bezier-Spline interpolate
 * used as an aggregation 
 * function for online aggregation
 * of given {@link java.lang.Number numerical data} without any error control.
 * One is able to reduce the required memory to
 * store a real-valued function as a result of a aggregation step by substituting the
 * function by an approximating cubic Bezier-Spline interpolate with the first boundary condition.
 * <br><br>
 * Unlike aggregation functions provided by {@link xxl.core.math.statistics.parametric.aggregates.ConfidenceAggregationFunction}
 * objects of
 * this class don't compute confidence intervals for the processed data.
 * Furthermore, this class implements the interface given by
 * {@link xxl.core.math.statistics.parametric.aggregates.OnlineAggregation OnlineAggregation}.
 * This interface provides special functionality of aggregation functions by supporting
 * <tt>online</tt> features like watching and controlling .
 * See {@link xxl.core.math.statistics.parametric.aggregates.OnlineAggregation OnlineAggregation} for further details.
 * <br>
 * <p><b>Objects of this type are recommended for the usage with aggregator cursors!</b></p>
 * <br>
 * This class shows how one can use an
 * estimation function as an aggregate function using the
 * {@link xxl.core.cursors.mappers.Aggregator aggregator cursor}.
 * {@link xxl.core.functions.Function Functions} used in the context of online
 * aggregation must support two-figured function calls.
 *
 * Each aggregation function must support a function call of the following type:<br>
 * <tt>agg_n = f (agg_n-1, next)</tt>, <br>
 * where <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps,
 * <tt>f</tt> the aggregation function,
 * <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps,
 * and <tt>next</tt> the next object to use for computation.
 * An aggregation function delivers only <tt>null</tt> as aggregation result as long as the aggregation
 * function has not yet fully initialized.
 *
 * @see xxl.core.functions.Function
 * @see xxl.core.math.statistics.parametric.aggregates.OnlineAggregation
 * @see xxl.core.math.statistics.nonparametric.kernels.NativeKernelDensityEstimator
 * @see xxl.core.cursors.mappers.Aggregator
 * @see xxl.core.math.numerics.splines.CubicBezierSpline
 * @see xxl.core.math.numerics.splines.RB1CubicBezierSpline
 */

public class SplineCompressedFunctionAggregateFunction extends Function implements OnlineAggregation {

	/** aggregation function delivering real-valued one-dimensional functions
	 */
	protected Function function;

	/** function to compress, delivered by {@link #function} */
	protected Object na = null;

	/** used predicate to determine when to build up a new spline */
	protected Predicate buildup;

	/** indicates whether the internally used estimator function is initialized or not */
	protected boolean initialized = false;

	/** left interval border to evaluate the given function and
	 * also the support of the splines to build */
	protected double a;

	/** right interval border to evaluate the given function and
	 * also the support of the splines to build */
	protected double b;

	/** number of steps used to build the spline for compression*/
	protected int n;

	/** used grid for evaluating the function */
	protected double[] grid;

	/** internally built spline */
	protected CubicBezierSpline spline;

	/** Indicates built splines will be in cdf mode, i.e., evaluating
	 * the spline at x > maximum causes the spline to return 1.0 instead of 0.0.
	 * That is necessary for the approximation of a cumulative distribution function.
	 */
	protected boolean cdfMode;

	/** function delivering the left border */
	protected Function minimum = null;
	
	/** function delivering the right border */
	protected Function maximum = null;
	
	/** function determining the grid points */
	protected Function gridPoints = null;
	
	/**
	 * number of build blocks 
	 */
	int no=0;

	/** Constructs a new Object of this class.
	 * 
	 * @param function function to compress
	 * @param buildup predicate determining when to build up a new spline
	 * @param minimum function delivering the left border 
	 * @param maximum function delivering the right border
	 * @param gridPoints function determining the grid points
	 * @param cdfMode indicates whether the spline is in cdf mode
	 */
	public SplineCompressedFunctionAggregateFunction(
		Function function,
		Predicate buildup,
		Function minimum,
		Function maximum,
		Function gridPoints,
		boolean cdfMode) {
		this.function = function;
		this.buildup = buildup;
		grid = null;
		spline = null;
		this.minimum = minimum;
		this.maximum = maximum;
		this.gridPoints = gridPoints;
		this.cdfMode = cdfMode;
	}

	/** Constructs a new Object of this class. 
	 * 
	 * @param function function to compress
	 * @param buildup predicate determining when to build up a new spline
	 * @param a left border of the supported real-valued interval
	 * @param b right border of the supported real-valued interval
	 * @param n number of evaluation points used
	 * @param cdfMode indicates whether the spline is in cdf mode
	 */
	public SplineCompressedFunctionAggregateFunction(
		Function function,
		Predicate buildup,
		double a,
		double b,
		int n,
		boolean cdfMode) {
		this.function = function;
		this.buildup = buildup;
		this.a = a;
		this.b = b;
		this.n = n;
		grid = null;
		spline = null;
		this.cdfMode = cdfMode;
	}

	/** Constructs a new Object of this class.
	 * 
	 * @param function function to compress
	 * @param a left border of the supported real-valued interval
	 * @param b right border of the supported real-valued interval
	 * @param n number of evaluation points used
	 * @param cdfMode indicates whether the spline is in cdf mode
	 */
	public SplineCompressedFunctionAggregateFunction(Function function, double a, double b, int n, boolean cdfMode) {
		this.function = function;
		buildup = new Predicate() {// indicates whether the current object and the last seen object are equal
			Object last = null;
			public boolean invoke(Object o) {
				boolean r = !(o == last);
				last = o;
				return r;
			}
		};
		this.a = a;
		this.b = b;
		this.n = n;
		grid = null;
		spline = null;
		this.cdfMode = cdfMode;
	}

	/** Constructs a new Object of this class.
	 * 
	 * @param function function to compress
	 * @param buildup predicate determining when to build up a new spline
	 * @param grid sorted grid determining the evaluation points
	 * @param cdfMode indicates whether the spline is in cdf mode
	 */
	public SplineCompressedFunctionAggregateFunction(
		Function function,
		Predicate buildup,
		double[] grid,
		boolean cdfMode) {
		this.function = function;
		this.buildup = buildup;
		this.n = -1;
		this.grid = grid;
		spline = null;
		this.cdfMode = cdfMode;
	}

	/** Two-figured function call for supporting aggregation by this function.
	 * Each aggregation function must support a function call like <tt>agg_n = f (agg_n-1, next)</tt>,
	 * where <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps, <tt>f</tt>
	 * the aggregation function, <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps
	 * and <tt>next</tt> the next object to use for computation.
	 * This method delivers only <tt>null</tt> as aggregation result as long as the aggregation
	 * has not yet initialized.
	 * 
	 * @param old result of the aggregation function in the previous computation step
	 * @param next next object used for computation
	 * @return aggregation value after n steps 
	 */
	public Object invoke(Object old, Object next) {
		na = function.invoke(old, next);
		if (na == null) {
			spline = null;
			buildup.invoke( na);
		} else {
			initialized = true;
			if (buildup.invoke(na)) {
				if (grid == null) {
					if (minimum != null)
						a = ((Number) minimum.invoke(next)).doubleValue();
					if (maximum != null)
						b = ((Number) maximum.invoke(next)).doubleValue();
					if (gridPoints != null)
						n = ((Number) gridPoints.invoke(next)).intValue();
					// building up spline with new (?) grid
					spline =
						new RB1CubicBezierSpline(
							a,
							b,
							n,
							Statistics.evalRealFunction(a, b, n, (RealFunction) na),
							cdfMode);
				} else {
					System.out.println("Build block: "+(++no));
					spline =
						new RB1CubicBezierSpline(grid, Statistics.evalRealFunction(grid, (RealFunction) na), cdfMode);
				}
			}
		}
		return spline;
	}

	/** Returns the current status of the online aggregation function
	 * implementing the OnlineAggregation interface.
	 * 
	 * @return current status of this function (here the current function to compress)
	 */
	public Object getState() {
		if (initialized) {
			return na;
		} else
			return null;
	}

	/** Sets a new status of the online aggregation function
	 * implementing the OnlineAgggregation interface (optional).
	 * This method is not supported by this class.
	 * It is implemented by throwing an UnsupportedOperationException.
	 * 
	 * @param state current state of the function
	 * @throws UnsupportedOperationException if this method is not supported by this class
	 */
	public void setState(Object state) {
		throw new UnsupportedOperationException("not supported");
	}

	/** Sets a new evaluation grid.
	 * 
	 * @param newGrid new evaluation grid
	 */
	public void setGrid(double[] newGrid) {
		n = -1;
		grid = newGrid;
	}

	/** Sets a new evaluation grid.
	 * 
	 * @param a left border of the supported real-valued interval
	 * @param b right border of the supported real-valued interval
	 * @param n number of evaluation points used
	 */
	public void setGrid(double a, double b, int n) {
		this.a = a;
		this.b = b;
		this.n = n;
		grid = null;
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
		// estimate the density function of a uniformly distributed entirety of
		// double values between 0 and 20

		Iterator it = 
			new Mapper(
				new RandomIntegers(20000, 100000), 
				new Function() {
					protected double span = 1000.0;
					public Object invoke(Object o) {
						return new Double(((Number) o).doubleValue() / span);
					}
				}
			);
		int sampleSize = 1000;
		final double left = 0.0;
		final double right = 20.0;
		final int nr = 10;
		Aggregator agg =
			new Aggregator(
				new Aggregator(
					it,
					new Function[] {
						new ReservoirSample(sampleSize, new ReservoirSample.XType(sampleSize)),
						new Variance()
					}
				),
				new SplineCompressedFunctionAggregateFunction(
					new NKDEAggregateFunction(
						new EpanechnikowKernel()
					),
					left, right, nr, false
				)
			);

		Function f = null;
		double[] grid = DoubleArrays.equiGrid(left, right, (int) (2.3 * nr));
		// 2.3 * n => eval. points don't fall on Bezier coefficients
		int c = 0;
		while (agg.hasNext()) {
			f = (Function) agg.next();
			if (f != null) {
				if (((++c) % 100) == 0)
					System.out.println(
						"step " + c + " = " + Strings.toString(Statistics.evalReal1DFunction(grid, f), ", "));
			}
		}
		System.out.println("step " + c + " (last) = " + Strings.toString(Statistics.evalReal1DFunction(grid, f), ", "));

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		// combination of an AdaptiveAggregationFunction and SplineCompressedFunctionAggregateFunction
		final int n = 1000;
		final double a = -1.0;
		final double b = 1.0;
		final int blockSize = 100;
		it = new java.util.Iterator() {
			int c = 0;
			public boolean hasNext() {
				return c <= n ? true : false;
			}
			public Object next() {
				if (hasNext())
					return new Double(a + (c++) * (b - a) / (n - 1));
				else
					throw new java.util.NoSuchElementException("No further numbers available!");
			}
			public void remove() {
				throw new UnsupportedOperationException("Not supported!");
			}
		};
		Function af = new Function() {
			int c = 0;
			int block = 0;
			Function maxF = Functions.aggregateUnaryFunction(new Maximum());
			Function minF = Functions.aggregateUnaryFunction(new Minimum());
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			public Object invoke(Object old, Object next) {
				c++;
				min = ((Double) minF.invoke(next)).doubleValue();
				max = ((Double) maxF.invoke(next)).doubleValue();
				if (c % blockSize == 0) {
					maxF = Functions.aggregateUnaryFunction(new Maximum());
					minF = Functions.aggregateUnaryFunction(new Minimum());
					block++;
					return new RealFunction() {
						final int b = block;
						final double mi = min;
						final double ma = max;
						public double eval(double x) {
							return ((x >= mi) & (x <= ma)) ? xxl.core.math.Statistics.cosineArch(x) : 0.0;
						}
						public String toString() {
							return ("estimator for block #" + b + " supporting [" + mi + "," + ma + "]");
						}
					};
				} else
					return old;
			}
		};
		xxl.core.cursors.mappers.Aggregator ag =
			new xxl.core.cursors.mappers.Aggregator(
				it, 
				new SplineCompressedFunctionAggregateFunction(
					new AdaptiveAggregationFunction(
						af, 
						new AdaptiveWeightFunctions.GeometricWeights(2.0), 
						true
					),
					-1.0, 1.0, 10, false
				)
			);
		RealFunction r = null;
		while (ag.hasNext()) {
			r = (RealFunction) ag.next();
		}
		grid = xxl.core.util.DoubleArrays.equiGrid(a, b, n);
		double[] e = xxl.core.math.Statistics.evalRealFunction(grid, r);
		System.err.println("Last function : " + r);
		for (int i = 0; i < e.length; i++)
			System.out.println(grid[i] + "\t" + e[i]);

	}
}