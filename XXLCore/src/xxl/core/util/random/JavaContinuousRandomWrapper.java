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

package xxl.core.util.random;

import java.util.Random;

/**
 *	Default implementation of RandomWrapper-interface. This class
 *	uses the Random-generator provided by Java.
 *	@see java.util.Random
 */

public class JavaContinuousRandomWrapper implements ContinuousRandomWrapper{

	/** internally used pseudo random number generator */
	protected Random random;

	/** Constructs a new wrapper for the given JavaRandom object.
	 * @param random pseudo random number generator
	 */
	public JavaContinuousRandomWrapper( Random random){
		this.random = random;
	}

	/** Constructs a new wrapper for the pseudo
	 * random number generator provided by java
	 */
	public JavaContinuousRandomWrapper(){
		this( new Random() );
	}

	/** Constructs a new wrapper for the pseudo
	 * random number generator provided by java using the given seed.
	 * @param seed parameter for random number generator 
	 */
	public JavaContinuousRandomWrapper( long seed){
		this( new Random( seed));
	}

	/** Returns the next pseudorandom, uniformly distributed double value
	 * between 0.0 and 1.0 from the java random number generator's sequence.
	 * <b>Taken from the original java api:</b><br>
	 * The general contract of nextDouble is that one double value,
	 * chosen (approximately) uniformly from the range 0.0d (inclusive)
	 * to 1.0d (exclusive), is pseudorandomly generated and returned.
	 * All 253 possible float values of the form m x 2-53 ,
	 * where m is a positive integer less than 253, are produced
	 * with (approximately) equal probability.
	 * The method nextDouble is implemented by class
	 * {@link java.util.Random Random} as follows: 
	 * <br><br>
	 * <code><pre>
	public double nextDouble() {
	return (((long)next(26) << 27) + next(27))
   		/ (double)(1L << 53);
		}
	 * </code></pre><br>
	 * The hedge "approximately" is used in the foregoing description
	 * only because the next method is only approximately an
	 * unbiased source of independently chosen bits.
	 * If it were a perfect source or randomly chosen bits,
	 * then the algorithm shown would choose double values
	 * from the stated range with perfect uniformity. 
	 * @return the next pseudorandom, uniformly distributed
	 * double value between 0.0 and 1.0 from the java random
	 * number generator's sequence.
	 */
	public double nextDouble() {
		return random.nextDouble();
	}
}