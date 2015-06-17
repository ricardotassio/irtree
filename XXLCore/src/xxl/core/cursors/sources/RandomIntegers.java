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

package xxl.core.cursors.sources;

import java.util.Random;

import xxl.core.cursors.AbstractCursor;

/**
 * The random integers-cursor returns randomly distributed integer objects. An
 * instance of this class is used to generate a stream of pseudo random integers.
 * The class uses a 48-bit seed, which is modified using a linear congruential
 * formula. (See Donald Knuth, <i>The Art of Computer Programming,
 * Volume&nbsp;2</i>, Section&nbsp;3.2.1.) If two instances of the
 * random integers-cursor are created with the same seed, and the same sequence
 * of method calls is made for each, they will generate and return identical
 * sequences of numbers, because this class uses a pseudo random number generator
 * (PRNG) of Java from {@link java.util.Random}. The range of an instance of
 * random integers is [0,<tt>maxValue</tt>[. If <tt>maxValue</tt> is not
 * specified, it is set to {@link java.lang.Integer#MAX_VALUE} by default.
 * 
 * <p><b>Example usage:</b>
 * <pre>
 *     RandomIntegers randomIntegers = new RandomIntegers(10000, 1000);
 * 
 *     randomIntegers.open();
 * 
 *     while (randomIntegers.hasNext())
 *         System.out.print(randomIntegers.next() + " ");
 *     System.out.println();
 * 
 *     randomIntegers.close();
 * </pre>
 * This example creates a new random integer-cursor so that the caller is able to
 * receive 1000 elements returned by calls to the <tt>next</tt> method. The
 * maximum value which may be returned is 9999. So 1000 randomly distributed
 * integer values with range [0,10000[ are printed to the output stream.</p>
 * 
 * <p><b>Note:</b> XXL provides further cursors delivering randomly distributed
 * integer or double values. See
 * {@link xxl.core.cursors.sources.ContinuousRandomNumber} and
 * {@link xxl.core.cursors.sources.DiscreteRandomNumber}.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 * @see java.util.Random
 * @see xxl.core.cursors.sources.ContinuousRandomNumber
 * @see xxl.core.cursors.sources.DiscreteRandomNumber
 */
public class RandomIntegers extends AbstractCursor {

	/**
	 * The pseudo random number generator (PRNG) to be used.
	 */
	protected Random random;

	/**
	 * The seed used to initialize the PRNG.
	 */
	protected long seed;

	/**
	 * The maximum value that can be returned by this random integers-cursor
	 * (given by <tt>maxValue - 1</tt>).
	 */
	protected int maxValue;

	/**
	 * The initial number of integer objects the caller wants to receive.
	 */
	protected int initialNoOfObjects;

	/**
	 * The current number of integer objects the caller still has to receive.
	 */
	protected int noOfObjects;

	/**
	 * Creates a new random integers-cursor.
	 *
	 * @param random the pseudo random number generator (PRNG).
	 * @param seed the seed used to hold the state of the pseudo random number
	 *        generator.
	 * @param maxValue the maximum value that can be returned by this random
	 *        integers-cursor (given by <tt>maxValue - 1</tt>).
	 * @param noOfObjects the number of integer objects the caller wants to
	 *        receive (-1 means an infinite number of objects).
	 */
	public RandomIntegers(Random random, long seed, int maxValue, int noOfObjects) {
		(this.random = random).setSeed(this.seed = seed);
		this.maxValue = maxValue;
		this.initialNoOfObjects = this.noOfObjects = noOfObjects;
	}

	/**
	 * Creates a new random integers-cursor using {@link java.util.Random Java}'s
	 * default random number generator.
	 *
	 * @param seed the seed used to hold the state of the pseudo random number
	 *        generator.
	 * @param maxValue the maximum value that can be returned by this random
	 *        integers-cursor (given by <tt>maxValue - 1</tt>).
	 * @param noOfObjects the number of integer objects the caller wants to
	 *        receive (-1 means an infinite number of objects).
	 */
	public RandomIntegers(long seed, int maxValue, int noOfObjects) {
		this(new Random(seed), seed, maxValue, noOfObjects);
	}

	/**
	 * Creates a new random integers-cursor using {@link java.util.Random Java}'s
	 * default random number generator. The <tt>seed</tt> used to hold the state
	 * of the pseudo random number generator is set to the current
	 * {@link java.lang.System#currentTimeMillis() system time}.
	 *
	 * @param maxValue the maximum value that can be returned by this random
	 *        integers-cursor (given by <tt>maxValue - 1</tt>).
	 * @param noOfObjects the number of integer objects the caller wants to
	 *        receive (-1 means an infinite number of objects).
	 */
	public RandomIntegers(int maxValue, int noOfObjects) {
		this(System.currentTimeMillis(), maxValue, noOfObjects);
	}

	/**
	 * Creates a new random integers-cursor using {@link java.util.Random Java}'s
	 * default random number generator. The <tt>seed</tt> used to hold the state
	 * of the pseudo random number generator is set to the current
	 * {@link java.lang.System#currentTimeMillis() system time} and the maximum
	 * value that can be returned is
	 * {@link java.lang.Integer#MAX_VALUE}<tt>-1</tt>.
	 *
	 * @param noOfObjects the number of integer objects the caller wants to
	 *        receive (-1 means an infinite number of objects).
	 */
	public RandomIntegers(int noOfObjects) {
		this(Integer.MAX_VALUE, noOfObjects);
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the random integers-cursor has more elements.
	 */
	protected boolean hasNextObject() {
		return noOfObjects == -1 || noOfObjects > 0;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the random integers-cursor's methods, e.g.,
	 * <tt>update</tt> or <tt>remove</tt>, until a call to <tt>next</tt> or
	 * <tt>peek</tt> occurs. This is calling <tt>next</tt> or <tt>peek</tt>
	 * proceeds the iteration and therefore its previous element will not be
	 * accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject(){
		if (noOfObjects > 0)
			noOfObjects--;
		return new Integer(random.nextInt(maxValue));
	}

	/**
	 * Resets the random integers-cursor to its initial state such that the
	 * caller is able to traverse the underlying data structure again without
	 * constructing a new random integers-cursor (optional operation). The
	 * modifications, removes and updates concerning the underlying data
	 * structure, are still persistent.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the random integers-cursor.
	 */
	public void reset(){
		super.reset();
		random.setSeed(seed);
		this.noOfObjects = initialNoOfObjects;
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the random integers-cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the random integers-cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return true;
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
		
		RandomIntegers randomIntegers = new RandomIntegers(10000, 1000);
		
		randomIntegers.open();
		
		while (randomIntegers.hasNext())
			System.out.print(randomIntegers.next() + "; ");
		System.out.println();
		
		randomIntegers.close();
	}
}
