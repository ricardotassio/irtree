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

/** 
 * This class provides some RealFunctions realizing different weighting strategies
 * for an adaptive online aggregation. There, in each step two objects are convex-linear combined 
 * depending on a weighting strategy. Different strategies allow a time- or user-dependent emphasis of
 * objects, e.g., later objects are 'more' weighted.    
 *
 * @see RealFunction
 * @see AdaptiveAggregationFunction
 */

public class AdaptiveWeightFunctions {

	/** This class provides an arithmetic weighting, i.e., equal weights in every
	 * step. This is done by returning the inverse value of the given step 1/j.
	 */
	public static class ArithmeticWeights implements RealFunction {
		
	/** Evaluates the real-valued function.
	 *
	 * @param j function argument
	 * @return function value
	 */
		public double eval(double j) {
			return 1.0 / j;
		}
	}

	/**
	 * This class provides a geometric weighting, i.e., in each step the same weight alpha
	 * respectively (1-alpha) is assigned.
	 *
	 */
	public static class GeometricWeights implements RealFunction {
		/**
		 * Parameter for the weighting: <code>alpha</code>.
		 */
		protected double alpha;
		
		/** Constructs an object of this type.
		 * 
		 * @param alpha weighting parameter
		 */
		public GeometricWeights(double alpha) {
			this.alpha = alpha;
		}
		
	/** Evaluates the real-valued function.
	 *
	 * @param j function argument
	 * @return function value
	 */
		public double eval(double j) {
			return 1.0 / alpha;
		}
	}

	/**
	 * This class provides a logarithm weighting. 
	 *
	 */
	public static class LogarithmicWeights implements RealFunction {
		
	/** Evaluates the real-valued function.
	 *
	 * @param j function argument
	 * @return function value
	 */
		public double eval(double j) {
			return Math.log(1.0 + j);
		}
	}

	/**
	 * This class provides a progressive respectively degressive weighting with a parameter alpha.
	 * For alpha in (0,1) the weighting is progressive and for alpha > 1 degressive.
	 *
	 */
	public static class ProgressiveDegressiveWeights implements RealFunction {
		/**
		 * Parameter for the weighting: <code>alpha</code>.
		 */
		protected double alpha;
		
		/** Constructs an object of thia type.
		 * 
		 * @param alpha weighting parameter
		 */
		public ProgressiveDegressiveWeights(double alpha) {
			this.alpha = alpha;
		}
		
	/** Evaluates the real-valued function.
	 *
	 * @param j function argument
	 * @return function value
	 */
		public double eval(double j) {
			return 1.0 / Math.pow(j, alpha);
		}
	}

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private AdaptiveWeightFunctions() {}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main(String[] args) {

		double c = 2.0;
		double alpha1 = 2.0;
		double alpha2 = 0.5;

		RealFunction wa = new ArithmeticWeights();
		RealFunction wc = new GeometricWeights(c);
		RealFunction wl = new LogarithmicWeights();
		RealFunction wp = new ProgressiveDegressiveWeights(alpha2);
		RealFunction wd = new ProgressiveDegressiveWeights(alpha1);

		System.out.println("j \tarithm\tgeom.(" + c + ")\tlog.\tprog.(" + alpha2 + ")\tdegr.(" + alpha1 + ")");
		for (int j = 1; j <= 100; j++) {
			System.out.print(j + "\t");
			System.out.print(wa.eval(j) + "\t");
			System.out.print(wc.eval(j) + "\t");
			System.out.print(wl.eval(j) + "\t");
			System.out.print(wp.eval(j) + "\t");
			System.out.print(wd.eval(j) + "\n");
		}
	}
}