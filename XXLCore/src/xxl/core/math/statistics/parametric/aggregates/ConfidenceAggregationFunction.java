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
import xxl.core.math.Statistics;

/** In the context of online aggregation aggregates are iteratively computed. The data is successively
 * consumed and the current estimator respectively aggregate bases on the already processed elements. In order to provide
 * a quality statement for the current estimate, confidence intervals can be computed.
 * Let Y_n be for instance the current estimate, alpha the confidence level and epsilon the current
 * confidence. Then the final result (i.e. the estimate based on all data) is in [Y_n-epsilon,Y_n+epsilon] 
 * with probability p=1-alpha.   
 * <br>
 * This class provides a framework for aggregation functions providing a confidence interval for the
 * current aggregate.
 * Furthermore, this class contains a collection of factories for concrete aggregation functions. 
 * <br>
 * A more detailed coverage of the theory and the resulting formulas is given in <BR>
 * [Haa97]: Peter J. Haas: Large-Sample and Deterministic Confidence Intervals for Online Aggregation,
 * Ninth International Conference on Scientific and Statistical Database Management, Proceedings, 1997. 
 * <BR>
 *
 * @see xxl.core.cursors.mappers.Aggregator
 * @see xxl.core.functions.Function
 */

public abstract class ConfidenceAggregationFunction extends Function {

	/** Provides a factory that computes confidence intervals for the current estimate of
	 * the variance estimator. The bounds of the confidence interval base on the central limit
	 * theorem (CLT). See [Haa97] for further details.
	 * 
	 * @param alpha confidence level
	 * @return a function that computes epsilon for the current estimate
	 *
	 * @see xxl.core.math.statistics.parametric.aggregates.VarianceEstimator
	 * @see xxl.core.math.statistics.parametric.aggregates.FourthCentralMomentEstimator
	 */
	public static ConfidenceAggregationFunction largeSampleConfidenceVarianceEstimator(final double alpha) {

		/** internal variance estimator */
		final VarianceEstimator var = new VarianceEstimator();

		/** internal estimator of the fourth central moment */
		final Function mom = new FourthCentralMomentEstimator();

		/** p = 1- alpha */
		final double p = 1 - alpha;

		return new ConfidenceAggregationFunction() {

			/** internally stored value of the variance */
			Double v = null;

			/** internally stored value of the fourth central moment */
			Double m = null;

			/** Computes iteratively the current estimate. */
			public Object invoke(Object old, Object next) {
				v = (Double) var.invoke(old, next);
				m = (Double) mom.invoke(m, next);
				return v;
			}

			/** Computes and returns as a <tt>Double</tt> the current epsilon. */
			public Object epsilon() throws IllegalStateException {
				if (var.n < 1)
					throw new IllegalStateException(
						"Computation a confidence interval not possible yet (n="
						+ var.n	+ ")!");
				else {
					double r =
						(m.doubleValue() - Math.pow(v.doubleValue(), 2.0)) / var.n;
					double z = Statistics.normalQuantil((p + 1.0) * 0.5);
					r = Math.sqrt(r);
					r = r * z;
					return new Double(r);
				}
			}

			/** Returns 1-alpha. */
			public double confidence() {
				return p;
			}

			/** Returns a description of the estimator. */
			public String toString() {
				return "VarianceEstimator (sample variance) with confidence support (large sample confidence/CLT-based)";
			}
		};
	}

	/** Provides a factory that computes confidence intervals for the current estimate of
	 * the standard deviation estimator. The bounds of the confidence interval base on the central limit
	 * theorem. See [Haa97] for further details.
	 * 
	 * @param alpha confidence level
	 * @return a function that computes epsilon for the current estimate
	 *
	 * @see xxl.core.math.statistics.parametric.aggregates.VarianceEstimator
	 * @see xxl.core.math.statistics.parametric.aggregates.FourthCentralMomentEstimator
	 */
	public static ConfidenceAggregationFunction largeSampleConfidenceStandardDeviationEstimator(final double alpha) {

		/** internal variance estimator */
		final VarianceEstimator var = new VarianceEstimator();

		/** internal estimator of the fourth central moment */
		final Function mom = new FourthCentralMomentEstimator();

		/** p = 1- alpha */
		final double p = 1 - alpha;

		return new ConfidenceAggregationFunction() {

			/** internally stored stored value of the std-dev */
			Double v = null;

			/** internally stored stored value of the forth central moment */
			Double m = null;

			/** Computes iteratively the current estimate. */
			public Object invoke(Object old, Object next) {
				v = (Double) var.invoke(old, next);
				m = (Double) mom.invoke(m, next);
				return new Double(Math.sqrt(v.doubleValue()));
			}

			/** Computes and returns as a <tt>Double</tt> the current epsilon. */
			public Object epsilon() throws IllegalStateException {
				if (var.n < 1)
					throw new IllegalStateException(
						"Computation a confidence interval not possible yet (n="
						+ var.n	+ ")!");
				else {
					double r =
						(m.doubleValue() - Math.pow(v.doubleValue(), 2.0))
							/ (4.0 * var.n);
					r = r / Math.sqrt(v.doubleValue());
					double z = Statistics.normalQuantil((p + 1.0) * 0.5);
					r = Math.sqrt(r);
					r = r * z;
					return new Double(r);
				}
			}

			/** Returns 1-alpha. */
			public double confidence() {
				return p;
			}

			/** Returns a description of the estimator. */
			public String toString() {
				return "Std-Dev.-Estimator (sample std-dev) with confidence support (large sample confidence/CLT-based)";
			}
		};
	}

	/** Provides a factory that computes confidence intervals for the current estimate of
	 * the average estimator. The bounds of the confidence interval base on the central limit
	 * theorem. See [Haa97] for further details.
	 * 
	 * @param alpha confidence level
	 * @return a function that computes epsilon for the current estimate
	 *
	 * @see xxl.core.math.statistics.parametric.aggregates.VarianceEstimator
	 * @see xxl.core.math.statistics.parametric.aggregates.Average
	 */
	public static ConfidenceAggregationFunction largeSampleConfidenceAverage(final double alpha) {

		/** internal variance estimator */
		final VarianceEstimator var = new VarianceEstimator();

		/** internal average estimator */
		final Average avg = new Average();

		/** p=1-alpha */
		final double p = 1 - alpha;

		return new ConfidenceAggregationFunction() {

			/** internally stored value of the variance */
			Double v = null;

			/** internally stored value of the average */
			Double a = null;

			/** Computes iteratively the current estimate. */
			public Object invoke(Object old, Object next) {
				v = (Double) var.invoke(v, next);
				a = (Double) avg.invoke(old, next);
				return a;
			}

			/** Computes and returns as a <tt>Double</tt> the current epsilon. */
			public Object epsilon() throws IllegalStateException {
				if (avg.count < 1)
					throw new IllegalStateException(
						"Computation a confidence interval not possible yet (n="
						+ avg.count	+ ")!");
				else {
					double r = Math.sqrt(v.doubleValue() / avg.count);
					r = r * Statistics.normalQuantil((p + 1.0) * 0.5);
					return new Double(r);
				}
			}

			/** Returns 1-alpha. */
			public double confidence() {
				return p;
			}

			/** Returns a description of the estimator. */
			public String toString() {
				return "Average (Estimator) with confidence support (large sample confidence/CLT-based)";
			}
		};
	}

	/** Provides a factory that computes confidence intervals for the current estimate of
	 * the standard deviation estimator. The bounds of the confidence interval are deterministic and 
	 * base on Hoeffding's Inequality. See [Haa97] for further details.
	 * 
	 * @param alpha confidence level
	 * @param a minimum of the data domain
	 * @param b maximum of the data domain
	 * @return a function that computes epsilon for the current estimate
	 *
	 * @see xxl.core.math.statistics.parametric.aggregates.VarianceEstimator
	 * @see xxl.core.math.statistics.parametric.aggregates.FourthCentralMomentEstimator
	 */
	public static ConfidenceAggregationFunction conservativeConfidenceAverage(
		final double alpha,
		final double a,
		final double b) {

		/** internal variance estimator */
		final Average avg = new Average();

		/** p=1-alpha */
		final double p = 1 - alpha;

		return new ConfidenceAggregationFunction() {

			/** internally stored value of the average */
			Double av = null;

			/** Computes iteratively the current estimate. */
			public Object invoke(Object old, Object next) {
				av = (Double) avg.invoke(old, next);
				return av;
			}

			/** Computes and returns as a <tt>Double</tt> the current epsilon. */
			public Object epsilon() throws IllegalStateException {
				if (avg.count < 1)
					throw new IllegalStateException(
						"Computation a confidence interval not possible yet (n="
						+ avg.count	+ ")!");
				else {
					double r = Math.log(2.0 / alpha);
					r = r / (2.0 * avg.count);
					r = Math.sqrt(r);
					r = r * (b - a);
					return new Double(r);
				}
			}

			/** Returns 1-alpha. */
			public double confidence() {
				return p;
			}

			/** Returns a description of the estimator. */
			public String toString() {
				return "Average (Estimator) with confidence support (conservative confidence/based upon Hoeffding's inequality)";
			}
		};
	}

	/** Provides a factory that computes confidence intervals for the current estimate of
	 * the standard deviation estimator. The bounds of the confidence interval are deterministic. 
	 * See [Haa97] for further details.
	 * 
	 * @param a minimum of the data domain
	 * @param b maximum of the data domain
	 * @param N number of elements of the entirety
	 * @return a function that computes epsilon for the current estimate
	 *
	 * @see xxl.core.math.statistics.parametric.aggregates.Average
	 */
	public static ConfidenceAggregationFunction deterministicConfidenceAverage(
		final double a,
		final double b,
		final long N) {

		/** internal average estimator */
		final Average avg = new Average();

		return new ConfidenceAggregationFunction() {

			/** internally stored value of the average */
			Double av = null;

			/** Computes iteratively the current estimate. */
			public Object invoke(Object old, Object next) {
				av = (Double) avg.invoke(old, next);
				return av;
			}

			/** Computes and returns the current confidence interval. */
			public Object epsilon() throws IllegalStateException {
				if (avg.count < 1)
					throw new IllegalStateException(
						"Computation a confidence interval not possible yet (n="
						+ avg.count	+ ")!");
				else {
					double[] r = new double[] {
							(N - avg.count),
							(N - avg.count)
						};
					r[0] *= (av.doubleValue() - a) / N;
					r[1] *= (av.doubleValue() - b) / N;
					return new Double[] { new Double(r[0]), new Double(r[1])};
				}
			}

			/** Returns a description of the estimator. */
			public String toString() {
				return "Average (Estimator) with confidence support (deterministic confidence)";
			}
		};
	}

	/** Provides a factory that computes confidence intervals for the current estimate of
	 * the sum estimator. The bounds of the confidence interval are deterministic. 
	 * See [Haa97] for further details.
	 * 
	 * @param a minimum of the data domain
	 * @param b maximum of the data domain
	 * @param N number of elements of the entirety
	 * @return a function that computes epsilon for the current estimate
	 *
	 * @see xxl.core.math.statistics.parametric.aggregates.Average
	 */
	public static ConfidenceAggregationFunction deterministicConfidenceSumEstimator(
		final double a,
		final double b,
		final long N) {

		/** internal sum estimator */
		final SumEstimator se = new SumEstimator(N);

		return new ConfidenceAggregationFunction() {

			/** internally stored value of the sum */
			Double sev = null;

			/** Computes iteratively the current estimate. */
			public Object invoke(Object old, Object next) {
				sev = (Double) se.invoke(old, next);
				return sev;
			}

			/** Computes and returns the current confidence interval. */
			public Object epsilon() throws IllegalStateException {
				if (se.n < 1)
					throw new IllegalStateException(
						"Computation a confidence interval not possible yet (n="
						+ se.n + ")!");
				else {
					double[] r = new double[] {
							(N - se.n) / a,
							(N - se.n) / b 
						};
					return new Double[] { new Double(r[0]), new Double(r[1])};
				}
			}

			/** Returns a description of the estimator. */
			public String toString() {
				return "Estimated sum with confidence support (deterministic confidence)";
			}
		};
	}

	/** Provides a factory that computes confidence intervals for the current estimate of
	 * the variance estimator. The bounds of the confidence interval are deterministic. 
	 * See [Haa97] for further details.
	 * 
	 * @param a minimum of the data domain
	 * @param b maximum of the data domain
	 * @param N number of elements of the entirety
	 * @return a function that computes epsilon for the current estimate
	 *
	 * @see xxl.core.math.statistics.parametric.aggregates.Average
	 */
	public static ConfidenceAggregationFunction deterministicConfidenceVarianceEstimator(
		final double a,
		final double b,
		final long N) {

		/** internal variance estimator */
		final VarianceEstimator var = new VarianceEstimator();

		/** internal average estimator */
		final Average avg = new Average();

		return new ConfidenceAggregationFunction() {

			/** internally stored value of the variance */
			Double v = null;

			/** internally stored value of the variance */
			Double av = null;

			/** Computes iteratively the current estimate. */
			public Object invoke(Object old, Object next) {
				v = (Double) var.invoke(old, next);
				av = (Double) avg.invoke(av, next);
				return v;
			}

			/** Computes and returns the current confidence interval. */
			public Object epsilon() throws IllegalStateException {
				if (var.n < 1)
					throw new IllegalStateException(
						"Computation a confidence interval not possible yet (n="
						+ var.n	+ ")!");
				else {
					double e_down =
						((double) (N - var.n) / (double) (N - 1))
							* v.doubleValue();
					double e_up = ConfidenceAggregationFunction.lambda(N,var.n,a,b,av.doubleValue()) - e_down;
					return new Double[] { new Double(e_down), new Double(e_up)};
				}
			}

			/** Returns a description of the estimator. */
			public String toString() {
				return "VarianceEstimator (sample variance) with confidence support (deterministic confidence)";
			}
		};
	}

	/** Provides a factory that computes confidence intervals for the current estimate of
	 * the standard deviation estimator. The bounds of the confidence interval are deterministic. 
	 * See [Haa97] for further details.
	 * 
	 * @param a minimum of the data domain
	 * @param b maximum of the data domain
	 * @param N number of elements of the entirety
	 * @return a function that computes epsilon for the current estimate
	 *
	 * @see xxl.core.math.statistics.parametric.aggregates.Average
	 */
	public static ConfidenceAggregationFunction deterministicConfidencestandardDeviationEstimator(
		final double a,
		final double b,
		final long N) {

		/** internal variance estimator */
		final VarianceEstimator var = new VarianceEstimator();

		/** internal average estimator */
		final Average avg = new Average();

		return new ConfidenceAggregationFunction() {

			/** internally stored value of the variance */
			Double v = null;

			/** internally stored value of the average */
			Double av = null;

			/** Computes iteratively the current estimate. */
			public Object invoke(Object old, Object next) {
				v = (Double) var.invoke(old, next);
				av = (Double) avg.invoke(av, next);
				return new Double(Math.sqrt(v.doubleValue()));
			}

			/** Computes and returns the current confidence interval. */
			public Object epsilon() throws IllegalStateException {
				if (var.n < 1)
					throw new IllegalStateException(
						"Computation a confidence interval not possible yet (n="
						+ var.n	+ ")!");
				else {
					double q =
						((double) (var.n - 1) / (double) (N - 1))
							* v.doubleValue();
					double e_down = Math.sqrt(v.doubleValue()) - Math.sqrt(q);
					double e_up =
						Math.sqrt(q	- ConfidenceAggregationFunction.lambda(	N, var.n, a, b,	av.doubleValue()))
							- Math.sqrt(v.doubleValue());
					return new Double[] { new Double(e_down), new Double(e_up)};
				}
			}

			/** Returns a description of the estimator. */
			public String toString() {
				return "Standard Deviation Estimator (sample std-dev) with confidence support (deterministic confidence)";
			}
		};
	}

	/** Provides a factory that computes confidence intervals for the current estimate of
	 * the variance estimator. The bounds of the confidence interval are deterministic. 
	 * See [Haa97] for further details.
	 * 
	 * @param alpha confidence level
	 * @param a minimum of the data domain
	 * @param b maximum of the data domain
	 * @param sN number of elements of the entirety
	 * @return a function that computes epsilon for the current estimate
	 */
	public static ConfidenceAggregationFunction conservativeConfidenceVarianceEstimator(
		final double alpha,
		final double a,
		final double b,
		final long sN) {

		/** internal variance estimator*/
		final VarianceEstimator var = new VarianceEstimator();

		/** p=1-alpha*/
		final double p = 1 - alpha;

		return new ConfidenceAggregationFunction() {

			/** internally stored value of the variance */
			Double v = null;

			/** Computes iteratively the current estimate. */
			public Object invoke(Object old, Object next) {
				v = (Double) var.invoke(old, next);
				return v;
			}

			/** Computes and returns the current confidence interval. */
			public Object epsilon() throws IllegalStateException {
				if (var.n < 1)
					throw new IllegalStateException(
						"Computation a confidence interval not possible yet (n="
						+ var.n	+ ")!");
				else {
					double r =
						sN
							/ (sN - 1.0)
							* Math.pow(ConfidenceAggregationFunction.Bn(var.n, a, b, p), 0.5);
					r += (sN / Math.pow((sN - 1.0), 2.0))
						* (Math.pow(b - a, 2.0) / 4.0);
					return new Double(r);
				}
			}

			/** Returns 1-alpha. */
			public double confidence() {
				return p;
			}

			/** Returns a description of the estimator. */
			public String toString() {
				return "VarianceEstimator (sample variance) with confidence support (conservative confidence/based upon Hoeffding's inequality)";
			}
		};
	}

	/** Provides a factory that computes confidence intervals for the current estimate of
	 * the standard deviation estimator. The bounds of the confidence interval are deterministic. 
	 * See [Haa97] for further details.
	 * 
	 * @param alpha confidence level
	 * @param a minimum of the data domain
	 * @param b maximum of the data domain
	 * @param sN number of elements of the entirety
	 * @return a function that computes epsilon for the current estimate
	 */
	public static ConfidenceAggregationFunction conservativeConfidenceStandardDeviationEstimator(
		final double alpha,
		final double a,
		final double b,
		final long sN) {

		/** internal variance estimator*/
		final VarianceEstimator var = new VarianceEstimator();

		/** p=1-alpha */
		final double p = 1 - alpha;

		return new ConfidenceAggregationFunction() {

			/** internally stored value for the variance */
			Double v = null;

			/** Computes iteratively the current estimate. */
			public Object invoke(Object old, Object next) {
				v = (Double) var.invoke(old, next);
				return new Double(Math.sqrt(v.doubleValue()));
			}

			/** Computes and returns the current epsilon. */
			public Object epsilon() throws IllegalStateException {
				if (var.n < 1)
					throw new IllegalStateException(
						"Computation a confidence interval not possible yet (n="
						+ var.n	+ ")!");
				else {
					double r =
						sN
							/ (sN - 1.0)
							* Math.pow(
								ConfidenceAggregationFunction.Bn( var.n, a, b, p), 0.25);
					r += (sN / Math.pow((sN - 1.0), 2.0))
						* (Math.pow(b - a, 2.0) / 4.0);
					return new Double(r);
				}
			}

			/** Returns 1-alpha. */
			public double confidence() {
				return p;
			}

			/** Returns a description of the estimator. */
			public String toString() {
				return "Std-Dev.-Estimator (sample std-dev) with confidence support (conservative confidence/based upon Hoeffding's inequality)";
			}
		};
	}

	/** Provides a factory that computes confidence intervals for the current estimate of
	 * the sum estimator. The bounds of the confidence interval are deterministic. 
	 * See [Haa97] for further details.
	 * 
	 * @param alpha confidence level
	 * @param N number of elements of the entirety
	 * @return a function that computes epsilon for the current estimate
	 */
	public static ConfidenceAggregationFunction largeSampleConfidenceSumEstimator(
		final double alpha,
		final long N) {

		/** internal sum estimator*/
		final SumEstimator se = new SumEstimator(N);

		/** internal variance estimator */
		final VarianceEstimator svar = new VarianceEstimator();

		/** p=1-alpha */
		final double p = 1 - alpha;

		return new ConfidenceAggregationFunction() {

			/** internally stored value for the sum */
			Double s = null;

			/** internally stored value for the variance */
			Double sv = null;

			/** Computes iteratively the current estimate. */
			public Object invoke(Object old, Object next) {
				s = (Double) se.invoke(s, next);
				sv = (Double) svar.invoke(
						sv,
						new Double(N * ((Number) next).doubleValue()));
				return s;
			}

			/** Computes and returns the current epsilon. */
			public Object epsilon() throws IllegalStateException {
				if (svar.n < 1)
					throw new IllegalStateException(
						"Computation a confidence interval not possible yet (n="
						+ svar.n + ")!");
				else {
					double r = Math.sqrt(sv.doubleValue() / svar.n);
					r = r * Statistics.normalQuantil((p + 1.0) * 0.5);
					return new Double(r);
				}
			}

			/** Returns 1-alpha. */
			public double confidence() {
				return p;
			}

			/** Returns a description of the estimator. */
			public String toString() {
				return "SumEstimator with confidence support (large sample confidence/CLT-based)";
			}
		};
	}

	/** Computes lambda_n = ( 1- n/N) * ( (b-a)^4/4 + (n/N) * max( avg_n-a, b-avg_n)^2).
	 * 
	 * @param N long value
	 * @param n long value
	 * @param a double value
	 * @param b double value
	 * @param avg double value
	 * @return lambda_n
	 */
	protected static double lambda(
		long N,
		long n,
		double a,
		double b,
		double avg) {
		double q = ((double) n / (double) N);
		double r = q * (Math.pow(Math.max(avg - a, b - avg), 2.0));
		r += Math.pow(b - a, 4.0) / 4.0;
		r *= 1.0 - q;
		return r;
	}

	/** Computes a temporal variable.
	 *  
	 * @param n long value
	 * @param a double value
	 * @param b double value
	 * @param p double value
	 * @return Bn
	 */
	protected static double Bn(long n, double a, double b, double p) {
		double r = 	1.0	/ Math.floor(n / 2.0)
				* Math.log(2.0 / (1.0 - p));
		double r2 = Math.pow(b - a, 4.0) / 8.0;
		if (a * b < 0.0) {
			r2 =
				Math.max( r2, 0.5 * Math.max(Math.abs(a), b) * (Math.abs(a) + b) 
				- 0.25 * Math.pow(Math.max(Math.abs(a), b), 2.0));
		}
		return r * r2;
	}

	/**
	 * If an inheriting class doesn't overwrite this method and thus provides the computation of confidence intervals,
	 * this operation is not supported.
	 * 
	 * @return current epsilon
	 * @throws IllegalStateException no valid state
	 * @throws UnsupportedOperationException if computation of confidence intervals not supported
	 */
	public Object epsilon()
		throws IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException("Computation of confidence intervals not supported by this estimator!");
	}

	/**
	 * If an inheriting class doesn't overwrite this method and thus provides the computation of confidence intervals,
	 * this operation is not supported.
	 * 
	 * @return p=1-alpha
	 * @throws IllegalStateException no valid state
	 * @throws UnsupportedOperationException if computation of confidence intervals is not supported
	 */
	public double confidence() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Computation confidence intervals not supported by this estimator!");

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
		// Building Function-Array containing confidence-supporting aggregation functions
		ConfidenceAggregationFunction[] f =
			new ConfidenceAggregationFunction[] {
				largeSampleConfidenceVarianceEstimator(0.05),
				largeSampleConfidenceStandardDeviationEstimator(0.05),
				largeSampleConfidenceAverage(0.05),
				conservativeConfidenceAverage(0.05, 1.0, 100.0),
				deterministicConfidenceAverage(1.0, 100.0, 100),
				deterministicConfidenceSumEstimator(1.0, 100.0, 100),
				deterministicConfidenceVarianceEstimator(1.0, 100.0, 100),
				deterministicConfidencestandardDeviationEstimator(
					1.0,
					100.0,
					100),
				conservativeConfidenceVarianceEstimator(0.04, 1.0, 100.0, 100),
				conservativeConfidenceStandardDeviationEstimator(
					0.04,
					1.0,
					100.0,
					100),
				largeSampleConfidenceSumEstimator(0.05, 100)};
		// Building aggregator
		Aggregator aggregator =
			new Aggregator(
				xxl.core.cursors.sources.Inductors.naturalNumbers(1, 100), // the input-Cursor
				f // aggregation functions
			);
		System.out.println(
			"Processing sorted natural numbers from 1 to 100 ...\n");
		// getting aggregation results
		Object[] aggResult = (Object[]) aggregator.last();
		// printing aggregation results
		for (int i = 0; i < aggResult.length; i++) {
			String eps = "";
			try {
				Object[] eps2 = (Object[]) f[i].epsilon();
				for (int j = 0; j < eps2.length; j++)
					eps += eps2[j].toString() + "  ";
			} catch (ClassCastException cce) {
				eps = f[i].epsilon().toString();
			}
			System.out.println(
				f[i] + " --> " + aggResult[i] + " with epsilon ---> " + eps);
		}
	}
}