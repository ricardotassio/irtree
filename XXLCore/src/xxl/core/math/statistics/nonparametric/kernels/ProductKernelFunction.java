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

import java.util.Iterator;

import xxl.core.util.DoubleArrays;
import xxl.core.util.Strings;

/** This class provides the product of a given one-dimensional 
 * {@link xxl.core.math.statistics.nonparametric.kernels.KernelFunction kernel function}
 * for using it as a n-dimensional real-valued kernel function as modelled in
 * {@link xxl.core.math.statistics.nonparametric.kernels.KernelFunctionND KernelFunctionND}. 
 * For concrete applications
 * the n-dimensional Epanechnikow kernel as well as the n-dimensional Biweight kernel
 * are provided as static classes.
 *
 * @see xxl.core.math.statistics.nonparametric.kernels.KernelFunctionND
 * @see xxl.core.math.statistics.nonparametric.kernels.AdaBandKernelDensityEstimatorND
 * @see xxl.core.math.statistics.nonparametric.kernels.EpanechnikowKernel
 * @see xxl.core.math.statistics.nonparametric.kernels.BiweightKernel
 * @see xxl.core.math.statistics.nonparametric.kernels.TriweightKernel
 * @see xxl.core.math.statistics.nonparametric.kernels.CosineArchKernel
 * @see xxl.core.math.statistics.nonparametric.kernels.GaussianKernel
 */

public class ProductKernelFunction implements KernelFunctionND {

	/** This class provides a n-dimensional product kernel function based upon the one-dimensional
	 * {@link xxl.core.math.statistics.nonparametric.kernels.EpanechnikowKernel epanechnikow kernel}.
	 */
	public static class Epanechnikow extends ProductKernelFunction {

		/** Constructs a new instance of an epanechnikow product kernel with given dimension.
		 * 
		 * @param dim dimension of the product kernel function based upon the one-dimensional
		 * {@link xxl.core.math.statistics.nonparametric.kernels.EpanechnikowKernel epanechnikow kernel}
		 */
		public Epanechnikow(int dim) {
			super(new EpanechnikowKernel(), dim);
		}
	}

	/** This class provides a n-dimensional product kernel function based upon the one-dimensional
	 * {@link xxl.core.math.statistics.nonparametric.kernels.BiweightKernel biweight kernel}.
	 */
	public static class Biweight extends ProductKernelFunction {

		/** Constructs a new instance of a biweight product kernel with given dimension.
		 * 
		 * @param dim dimension of the product kernel function based upon the one-dimensional
		 * {@link xxl.core.math.statistics.nonparametric.kernels.BiweightKernel biweight kernel}
		 */
		public Biweight(int dim) {
			super(new BiweightKernel(), dim);
		}
	}

	/** used one-dimensional kernel function to build up a n-dimensional 
	 * product kernel function 
	 * */
	protected KernelFunction kernel;

	/** dimension of the product kernel function */
	protected int dim;

	/** Constructs a new object of this class.
	 * 
	 * @param kernel used one dimensional {@link xxl.core.math.statistics.nonparametric.kernels.KernelFunction kernel function}
	 * to build up a n-dimensional product kernel function
	 * @param dim dimension of the product kernel function
	 */
	public ProductKernelFunction(KernelFunction kernel, int dim) {
		this.kernel = kernel;
		this.dim = dim;
	}

	/** Evaluates the product kernel function at given real-valued n-dimensional point x and returns
	 * f(x) with f : R^n --> R.
	 * 
	 * @param x real-valued function argument given as <tt>double []</tt>
	 * @return f(x)
	 * @throws IllegalArgumentException if the dimension of the given argument doesn't match
	 * the dimension of the kernel function
	 */
	public double eval(double[] x) throws IllegalArgumentException {
		if (x.length != dim)
			throw new IllegalArgumentException("wrong dimension in argument!");
		double r = 1.0;
		for (int i = 0; i < dim; i++)
			r *= kernel.eval(x[i]);
		return r;
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main(String[] args) {
		int example = 0;
		if ((args.length == 0))
			example = 1;
		else {
			if (args[0].startsWith("epa"))
				example = 1;
			if (args[0].startsWith("biw"))
				example = 2;
		}
		// ---
		switch (example) {
			case 1 :
				/*********************************************************************/
				/*                            Example 1                              */
				/*********************************************************************/
				// evaluate the epanechnikow product kernel from (-1,-1) to (1,1)
				Iterator realgrid1 =
					DoubleArrays.realGrid(new double[] { -1.0, -1.0 }, new double[] { 1.05, 1.05 }, 40);
				KernelFunctionND kf1 = new ProductKernelFunction.Epanechnikow(2);
				while (realgrid1.hasNext()) {
					double[] next = (double[]) realgrid1.next();
					System.out.println(Strings.toString(next, "\t") + "\t" + kf1.eval(next));
				}
				break;
			case 2 :
				/*********************************************************************/
				/*                            Example 2                              */
				/*********************************************************************/
				// evaluate the biweight product kernel from (-1,-1) to (1,1)
				Iterator realgrid2 =
					DoubleArrays.realGrid(new double[] { -1.0, -1.0 }, new double[] { 1.05, 1.05 }, 40);
				KernelFunctionND kf2 = new ProductKernelFunction.Biweight(2);
				while (realgrid2.hasNext()) {
					double[] next = (double[]) realgrid2.next();
					System.out.println(Strings.toString(next, "\t") + "\t" + kf2.eval(next));
				}
				break;
			default :
				throw new IllegalArgumentException("unknown kernel function given");
		}
	}
}