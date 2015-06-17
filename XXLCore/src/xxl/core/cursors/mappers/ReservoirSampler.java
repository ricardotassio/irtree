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

package xxl.core.cursors.mappers;

import java.util.Iterator;

import xxl.core.functions.Function;
import xxl.core.math.statistics.parametric.aggregates.ReservoirSample;
import xxl.core.util.random.ContinuousRandomWrapper;
import xxl.core.util.random.DiscreteRandomWrapper;
import xxl.core.util.random.JavaContinuousRandomWrapper;
import xxl.core.util.random.JavaDiscreteRandomWrapper;

/**
 * This class shows how to make use of the
 * {@link xxl.core.math.statistics.parametric.aggregates.ReservoirSample reservoir-sampling function}.
 * A reservoir-sampler is an
 * {@link xxl.core.cursors.mappers.Aggregator aggregator} using the
 * reservoir-sampling function with a given strategy for <tt>on-line
 * sampling</tt>. There are three types of strategies available based on
 * [Vit85]: Jeffrey Scott Vitter, <i>Random Sampling with a Reservoir</i>, in
 * ACM Transactions on Mathematical Software, Vol. 11, NO. 1, March 1985,
 * Pages 37-57.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.mappers.Aggregator
 */
public class ReservoirSampler extends Aggregator {

	/**
	 * Indicates the use of type R for sampling strategy.
	 * 
	 * @see xxl.core.math.statistics.parametric.aggregates.ReservoirSample
	 * @see xxl.core.math.statistics.parametric.aggregates.ReservoirSample.RType
	 */
	public static final int RTYPE = 0;

	/**
	 * Indicates the use of type X for sampling strategy.
	 * 
	 * @see xxl.core.math.statistics.parametric.aggregates.ReservoirSample
	 * @see xxl.core.math.statistics.parametric.aggregates.ReservoirSample.XType
	 */
	public static final int XTYPE = 1;

	/**
	 * Indicates the use of type Y for sampling strategy. This type is not
	 * available so far due to the lack of information about the used
	 * distribution to determine the position in the reservoir for a sampled
	 * object.
	 * 
	 * @see xxl.core.math.statistics.parametric.aggregates.ReservoirSample
	 */
	public static final int YTYPE = 2;

	/**
	 * Indicates the use of type Z for sampling strategy.
	 * 
	 * @see xxl.core.math.statistics.parametric.aggregates.ReservoirSample
	 * @see xxl.core.math.statistics.parametric.aggregates.ReservoirSample.ZType
	 */
	public static final int ZTYPE = 3;

	/**
	 * Constructs a new aggregator that provides a reservoir-sampling as output.
	 *
	 * @param input input iteration to draw the sample from.
	 * @param n size of the sample to draw.
	 * @param type strategy used to determine the position of the treated object
	 *        in the sampling reservoir.
	 * @param crw used PRNG for computing continuous random numbers.
	 * @param drw used PRNG for computing discrete random numbers.
	 * @throws IllegalArgumentException if an unknown sampling strategy has been
	 *         given.
	 */
	public ReservoirSampler(Iterator input, int n, int type, ContinuousRandomWrapper crw, DiscreteRandomWrapper drw) throws IllegalArgumentException {
		super(input, new Function());
		switch (type) {
			case RTYPE:
				this.function = new ReservoirSample(
					n,
					new ReservoirSample.RType(n, crw, drw)
				);
			break;
			case XTYPE:
				this.function = new ReservoirSample(
					n,
					new ReservoirSample.XType(n, crw, drw)
				);
			break;
			case YTYPE:
				//this.function = new ReservoirSample(
				//	n,
				//	new ReservoirSample.YType(n, crw, drw)
				//);
				throw new IllegalArgumentException("type y is not supported so far. See javadoc xxl.core.math.statistics.parametric.aggregates.ReservoirSample for details!");
				//break;
			case ZTYPE:
				this.function = new ReservoirSample(
					n,
					new ReservoirSample.ZType(n, crw, drw)
				);
			break;
			default:
				throw new IllegalArgumentException("unknown sampling strategy given!");
		}
	}

	/**
	 * Constructs a new aggregator that provides a reservoir sampling as output
	 * using a default PRNG for computing
	 * {@link xxl.core.util.random.JavaContinuousRandomWrapper continuous} random
	 * numbers and a default PRNG for computing
	 * {@link xxl.core.util.random.JavaDiscreteRandomWrapper discrete} random
	 * numbers.
	 *
	 * @param input iteration to draw the sample from.
	 * @param n size of the sample to draw.
	 * @param type strategy used to determine the position of the treated object
	 *        in the sampling reservoir.
	 * @throws IllegalArgumentException if an unknown sampling strategy has been
	 *         given.
	 */
	public ReservoirSampler(Iterator input, int n, int type) throws IllegalArgumentException {
		this(input, n, type, new JavaContinuousRandomWrapper(), new JavaDiscreteRandomWrapper());
	}

	/**
	 * Constructs a new aggregator that provides a reservoir sampling as output.
	 *
	 * @param input iteration to draw the sample from.
	 * @param reservoirSample function providing an online sampling.
	 */
	public ReservoirSampler(Iterator input, ReservoirSample reservoirSample) {
		super(input, reservoirSample);
	}

	/**
	 * Constructs a new aggregator that provides a reservoir sampling as output.
	 *
	 * @param input iteration to draw the sample from.
	 * @param n size of the sample to draw.
	 * @param strategy strategy used to determine the position of the treated
	 *        object in the sampling reservoir.
	 */
	public ReservoirSampler(Iterator input, int n, ReservoirSample.ReservoirSamplingStrategy strategy) {
		this(input, new ReservoirSample(n, strategy));
	}

	/**
	 * The main method contains some examples to demonstrate the usage and the
	 * functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to submit
	 *        parameters when the main method is called.
	 */
	public static void main(String[] args) {

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/

		ReservoirSampler sample = new ReservoirSampler(
			new xxl.core.cursors.sources.RandomIntegers(100, 50),
			new ReservoirSample(
				10,
				new ReservoirSample.XType(10)
			)
		);
		
		sample.open();
		
		int c = 0;
		while (sample.hasNext()) {
			Object[] o = (Object [])sample.next();
			System.out.print(c++);
			if (o != null) {
				for (int i = 0; i < o.length; i++)
					System.out.print(": " + o[i]);
				System.out.println();
			}
			else
				System.out.println(": reservoir not yet initialized!");
		}
		System.out.println("---------------------------------");
	}
}
