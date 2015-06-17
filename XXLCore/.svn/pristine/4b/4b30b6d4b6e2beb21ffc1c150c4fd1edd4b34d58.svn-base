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

package xxl.core.math.statistics.nonparametric.kernels;

import xxl.core.functions.Function;

/**
 * This class implements an abstract preimplementation 
 * of a <tt>kernel based pdf estimator</tt> 
 * {@link xxl.core.math.statistics.nonparametric.kernels.AbstractKernelDensityEstimator AbstractKernelDensityEstimator}
 * that doesn't use an explicit technique for avoiding boundary effects.
 * Additionally, this class provides a FACTORY for generating <tt>native kernel based density estimators</tt>.
 * 
 * @see xxl.core.math.statistics.nonparametric.kernels.KernelFunction
 * @see xxl.core.math.statistics.nonparametric.kernels.KernelBandwidths
 * @see xxl.core.math.functions.AbstractRealFunctionFunction
 * @see xxl.core.math.statistics.nonparametric.kernels.AbstractKernelDensityEstimator
 * @see xxl.core.math.statistics.nonparametric.kernels.ReflectionKernelDensityEstimator
 *
 */

public class NativeKernelDensityEstimator extends AbstractKernelDensityEstimator {

	/** Provides a factory for a
	 * {@link xxl.core.math.statistics.nonparametric.kernels.NativeKernelDensityEstimator native kernel density estimator}
	 * , i.e.,&nbsp;an estimator
	 * without any boundary treatment technique.
	 * The parameters needed for construction are passed to the factory by an
	 * <tt>Object[]</tt> <code>o</code> containing: <BR>
	 * <code>o[0]</code>: used {@link xxl.core.math.statistics.nonparametric.kernels.KernelFunction kernel function} <BR>
	 * <code>o[1]</code>: used sample as <tt>Object[]</tt> containing numerical data <BR>
	 * <code>o[2]</code>: used bandwidth as Object of type <tt>Number</tt> <BR>
	 * <code>o[3]</code>: confidence level as Object of type <tt>Number</tt> <BR>
	 */
	public static Function FACTORY = new Function() {
		public Object invoke(Object[] o) {
			return new NativeKernelDensityEstimator(
				 (KernelFunction) o[0],// kernel function
				 (Object[]) o[1], // sample
				 ((Number) o[2]).doubleValue() // bandwidth
			);
		}
	};

	/**
	 * Constructs an estimator for a density function using the given
	 * kernel function.
	 * This class implements a native kernel density estimator, meaning
	 * no boundary treatment is used nor any other optimizations.
	 *
	 * @param kf used {@link xxl.core.math.statistics.nonparametric.kernels.KernelFunction kernel function}.
	 * @param sample sample of a data set given as <tt>Object []</tt> containing
	 * objects of type <tt>Number</tt>.
	 * @param h used bandwidth for computing the estimation
	 * @param alpha confidence level used for computing an asymptotic pointwise confidence interval
	 */
	public NativeKernelDensityEstimator(KernelFunction kf, Object[] sample, double h, double alpha) {
		super(kf, sample, h, alpha);
	}

	/**
	 * Constructs an estimator for a density function using the given
	 * kernel function.
	 * This class implements a native kernel density estimator, meaning
	 * no boundary treatment is used nor any other optimizations.
	 *
	 * @param kf used {@link xxl.core.math.statistics.nonparametric.kernels.KernelFunction Kernel function}.
	 * @param sample sample of a data set given as <tt>Object []</tt> containing
	 * objects of type <tt>Number</tt>.
	 * @param h used bandwidth for computing the estimation
	 */
	public NativeKernelDensityEstimator(KernelFunction kf, Object[] sample, double h) {
		this(kf, sample, h, -1.0);
	}

	/** Evaluates the kernel based density estimator at given point x.
	 * 
	 * @param x argument where to evaluate the density estimation
	 * @return value of the estimated density at x
	 */
	public double evalKDE(double x) {
		double xi = 0.0;
		int size = sample.length;
		double r = 0.0;
		for (int i = 0; i < size; i++) {
			xi = ((Number) sample[i]).doubleValue();
			r = r + kf.eval((x - xi) / h);
		}
		r = r / size;
		r = r / h;
		return r;
	}
}