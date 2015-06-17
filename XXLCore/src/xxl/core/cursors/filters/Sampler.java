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

package xxl.core.cursors.filters;

import java.util.Iterator;

import xxl.core.cursors.sources.ContinuousRandomNumber;
import xxl.core.predicates.Predicate;
import xxl.core.util.random.JavaContinuousRandomWrapper;

/**
 * A sampler is a {@link xxl.core.cursors.filters.Filter filter} that generates a
 * sample of the elements contained in a given input iteration. The sample is
 * taken by filtering the underlying iteration which delivers randomly
 * distributed double values by wrapping a pseudo random number generator (PRNG),
 * namley an instance of the class
 * {@link xxl.core.cursors.sources.ContinuousRandomNumber}. This pseudo random
 * number generator has to deliver randomly distributed double values in range
 * [0, 1]. These double values are compared with the user specified parameter
 * <tt>p</tt> (probability) and depending on this parameter a decision whether
 * an element of the underlying iteration is taken for the sample or not is
 * determined. For that case the parameter <tt>p</tt> represents the <i>Bernoulli
 * probability</i>. <tt>p</tt> can be choosen in range (0, 1]. If the randomly
 * distributed double value created by the given pseudo random number generator
 * is lower than <tt>p</tt>, the current element of the input iterator is
 * returned to the caller, because the internal used predicate returns
 * <tt>true</tt>, otherwise this predicate returns <tt>false</tt>, so the
 * <tt>next</tt> method of the underlying iteration is called without returning
 * the delivered element, i.e., this element will not be contained in the sample.
 * 
 * <p><b>Example usage :</b>
 * <pre>
 *     Sampler sampler = new Sampler(
 *         new Enumerator(21),
 *         new ContinuousRandomNumber(new JavaRandom(new Random())),
 *         0.5 // Bernoulli probability
 *     );
 * 
 *     sampler.open();
 * 
 *     while (sampler.hasNext())
 *         System.out.println(sampler.next());
 * 
 *     sampler.close();
 * </pre>
 * This example demonstrates the sampler functionality. The sampler returns a
 * sample of the underlying
 * {@link xxl.core.cursors.sources.Enumerator enumerator} delivering one of the
 * enumerator's elements with a <i>Bernoulli probability</i> of '0.5'. That
 * means, half of the enumerator's elements will be returned on the average. In
 * this example Java's random number generator is used, but also Colt's random
 * number generators are supported, therefore XXL's connectivity package and
 * {@link xxl.core.util} providing wrapper classes for Colt (see package
 * <tt>xxl.connectivity.colt</tt>).</p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.filters.Filter
 */
public class Sampler extends Filter {

	/**
	 * Creates a new sampler based on an instance of the class
	 * {@link xxl.core.cursors.sources.ContinuousRandomNumber} and a given
	 * <i>Bernoulli probability</i>.
	 *
	 * @param iterator the iterator containing the elements the sample is to be
	 *        created from.
	 * @param crn a continuous random number cursor wrapping a PRNG.
	 * @param p the <i>Bernoulli probability</i>, i.e., the probabilty the
	 *        elements will be contained in the delivered sample. <tt>p</tt> must
	 *        be in range (0,1].
	 * @throws IllegalArgumentException if <tt>p</tt> is not in range (0, 1].
	 */
	public Sampler(Iterator iterator, final ContinuousRandomNumber crn, final double p) throws IllegalArgumentException {
		super(
			iterator,
			new Predicate() {
				public boolean invoke(Object object) {
					double number = ((Double)crn.next()).doubleValue();
					if (number < 0 || number > 1)
						throw new IllegalArgumentException("delivered random number not in range [0, 1].");
					return number < p;
				}
			}
		);
		if (p == 0 || p > 1)
			throw new IllegalArgumentException("p has to be in range (0, 1].");
	}

	/**
	 * Creates a new sampler based on an instance of the class
	 * {@link xxl.core.cursors.sources.ContinuousRandomNumber} using the class
	 * {@link java.util.Random} as default PRNG and a given <i>Bernoulli
	 * probability</i>.
	 *
	 * @param iterator the iterator containing the elements the sample is to be
	 *        created from.
	 * @param p the <i>Bernoulli probability</i>, i.e., the probabilty the
	 *        elements will be contained in the delivered sample. <tt>p</tt> must
	 *        be in range (0,1].
	 * @throws IllegalArgumentException if <tt>p</tt> is not in range (0, 1].
	 */
	public Sampler (Iterator iterator, final double p) throws IllegalArgumentException {
		this(iterator, new ContinuousRandomNumber(new JavaContinuousRandomWrapper()), p);
	}

	/**
	 * Creates a new sampler based on an instance of the class
	 * {@link xxl.core.cursors.sources.ContinuousRandomNumber} using the class
	 * {@link java.util.Random} as default PRNG with a given seed and a given
	 * <i>Bernoulli probability</i>.
	 *
	 * @param iterator the iterator containing the elements the sample is to be
	 *        created from.
	 * @param p the <i>Bernoulli probability</i>, i.e., the probabilty the
	 *        elements will be contained in the delivered sample. <tt>p</tt> must
	 *        be in range (0,1].
	 * @param seed the seed to the pseudo random number generator.
	 * @throws IllegalArgumentException if <tt>p</tt> is not in range (0, 1].
	 */
	public Sampler (Iterator iterator, final double p, long seed) throws IllegalArgumentException {
		this(iterator, new ContinuousRandomNumber(new JavaContinuousRandomWrapper(seed)), p);
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
		
		Sampler sampler = new Sampler(
			new xxl.core.cursors.sources.Enumerator(21),
			new ContinuousRandomNumber(new JavaContinuousRandomWrapper()),
			0.5 // Bernoulli probability
		);
		
		sampler.open();
		
		while (sampler.hasNext()) {
			System.out.println(sampler.next());
		}
		System.out.println();
		
		sampler.close();
	}
}
