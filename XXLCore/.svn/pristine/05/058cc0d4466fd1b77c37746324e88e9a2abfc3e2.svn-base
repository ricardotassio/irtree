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

import java.util.Arrays;

import xxl.core.functions.Function;
import xxl.core.predicates.Predicate;

/**
 * This class provides ready to use
 * {@link xxl.core.cursors.sources.Inductor inductors}. The static methods
 * implemented in this class return an inductor, each realizing a particular
 * kind of an inductive algorithm. The following inductors are supported:
 * <ul>
 *     <li>
 *         natural numbers
 *     </li>
 *     <li>
 *         factorial numbers
 *     </li>
 *     <li>
 *         fibonacci numbers
 *     </li>
 *     <li>
 *         n-dimensional counters
 *     </li>
 * </ul>
 * 
 * <p><b>Example usage (1): n-dimensional counter</b>
 * <pre>
 *     System.out.println("counter lexiographic order with int[] {6, 7, 2} [nDimCounter]");
 * 
 *     Cursor cursor = nDimCounter(new int[] {6, 7, 2});
 * 
 *     cursor.open();
 * 
 *     while (cursor.hasNext())
 *         System.out.println(Strings.toString((int[])cursor.next()));
 * 
 *     cursor.close();
 * </pre>
 * An extract of the output demonstrates the kind this counter works:<pre>
 *     counter lexiographic order with int[]{6,7,2} [nDimCounter]
 *     0 0 0
 *     1 0 0
 *     2 0 0
 *     3 0 0
 *     4 0 0
 *     5 0 0
 *     0 1 0
 *     1 1 0
 *     2 1 0
 *     3 1 0
 *     4 1 0
 *     5 1 0
 *     0 2 0
 *     1 2 0
 *     2 2 0
 *     3 2 0
 *     4 2 0
 *     5 2 0
 *     0 3 0
 *     1 3 0
 *     2 3 0
 *     3 3 0
 *     4 3 0
 *     5 3 0
 *     0 4 0
 *     ...
 * </pre></p>
 * 
 * <p><b>Example usage (2): natural numbers</b>
 * <pre>
 *     int f = 3;
 *     int t = 78;
 *     
 *     System.out.println("natural numbers from " + f + " to " + t);
 * 
 *     cursor = naturalNumbers(f, t);
 * 
 *     cursor.open();
 * 
 *     while (cursor.hasNext())
 *         System.out.println(cursor.next());
 * 
 *     cursor.close();
 * </pre>
 * This example prints the natural numbers of the interval [3, 78] to the
 * standard output stream.
 *
 * @see xxl.core.cursors.sources.Inductor
 */
public class Inductors {

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private Inductors() {}

	/**
	 * This methods returns an {@link xxl.core.cursors.sources.Inductor inductor}
	 * delivering objects of type <tt>int[]</tt>. This int-arrays could be seen
	 * as a n-dimensional counter consisting of n counters, each for every
	 * dimension.
	 *
	 * @param cardinal cardinality for each dimension. That means every returned
	 *        counter object <tt>c</tt> holds:
	 *        <pre>
	 *            0 &le; c[i] &lt; cardinal[i], i = 0,...,cardinal.length
	 *        </pre>
	 * @return an inductor delivering counting objects.
	 */
	public static Inductor nDimCounter(final int[] cardinal) {
		final int[] start = new int[cardinal.length];
		Arrays.fill(start, 0);
		return new Inductor(
			new Object[] {
				start
			} ,
			new Predicate() {
				public boolean invoke(Object object) {
					int[] count = (int[])object;
					boolean r = true;
					int numberOfEquals = 0;
					for (int i = 0; i < count.length; i++) {
						if (count[i] >= cardinal[i])
							r = false;
						if (count[i] == (cardinal[i]-1))
							numberOfEquals++;
					}
					if (numberOfEquals == count.length)
						r = false;
					return r;
				}
			},
			new Function() {
				public Object invoke(Object old) {
					int[] next = new int[cardinal.length];
					for (int i = 0 ; i < next.length; i++)
						next[i] = ((int[])old)[i];
					//
					int i=0;
					boolean carry = true;
					while (carry) {
						next[i]++;
						carry = false;
						if ((next[i] % cardinal[i]) == 0) { // ueberlauf
							carry = true;
							next[i] = 0;
							i++;
						}
					} // end of while
					return next;
				}
			}
		);
	}

	/**
	 * This methods returns an {@link xxl.core.cursors.sources.Inductor inductor}
	 * delivering objects of type <tt>Long</tt> representing
	 * <tt>natural numbers</tt> in ascending order.
	 *
	 * @param from starting with this natural number.
	 * @param to ending with this natural number.
	 * @return an inductor delivering natural numbers of the given range.
	 */
	public static Inductor naturalNumbers(final long from, final long to) {
		return new Inductor(
			new Object[] {
				new Long(from)
			},
			new Predicate() {
				long c = from;
				public boolean invoke(Object o) {
					return c++ < to;
				}
			},
			new Function() {
				public Object invoke(Object n) {
					return new Long (((Long)n).longValue()+1);
				}
			}
		);
	}

	/**
	 * This methods returns an {@link xxl.core.cursors.sources.Inductor inductor}
	 * delivering objects of type <tt>Long</tt> representing
	 * <tt>natural numbers</tt> in ascending order starting with 0.
	 *
	 * @param to ending with this natural number.
	 * @return an inductor delivering natural numbers of the given range.
	 */
	public static Inductor naturalNumbers(long to) {
		return naturalNumbers(0, to);
	}

	/**
	 * This methods returns an {@link xxl.core.cursors.sources.Inductor inductor}
	 * delivering objects of type <tt>Long</tt> representing
	 * <tt>natural numbers</tt> in ascending order starting with 0 and ending
	 * with {@link java.lang.Long#MAX_VALUE}.
	 *
	 * @return an inductor delivering natural numbers.
	 */
	public static Inductor naturalNumbers() {
		return naturalNumbers(0, Long.MAX_VALUE);
	}

	/**
	 * This methods returns an {@link xxl.core.cursors.sources.Inductor inductor}
	 * delivering objects of type <tt>Integer</tt> representing
	 * <tt>fibonacci numbers</tt>. The calculation rule is
	 * <pre>
	 *     fib(i) = fib(i-1) + fib(i-2)
	 * </pre>
	 *
	 * @return an inductor delivering <tt>fibonacci numbers</tt>.
	 */
	public static Inductor fibonacci() {
		return new Inductor(
			new Object[] {
				new Integer(1),
				new Integer(1)
			},
			new Function() {
				public Object invoke(Object fib_1, Object fib_2) {
					return new Integer(((Integer)fib_1).intValue() + ((Integer)fib_2).intValue());
				}
			}
		);
	}

	/**
	 * This methods returns an {@link xxl.core.cursors.sources.Inductor inductor}
	 * delivering objects of type <tt>Integer</tt> representing a sequence of
	 * factorial numbers.
	 *
	 * @return an inductor delivering factorial numbers.
	 */
	public static Inductor factorial() {
		return new Inductor(
			new Object[] {
				new Integer(1)
			},
			new Function() {
				int factor = 1;
				
				public Object invoke(Object n) {
					return new Integer(((Integer)n).intValue() * factor++);
				}
			}
		);
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
		
		System.out.println("counter lexiographic order with int[] {6, 7, 2} {nDimCounter]");
		
		xxl.core.cursors.Cursor cursor = nDimCounter(new int[] {6, 7, 2});
		
		cursor.open();
		
		while (cursor.hasNext())
			System.out.println(xxl.core.util.Strings.toString((int[])cursor.next()));
		System.out.println("- end -");
		
		cursor.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		int f = 3;
		int t = 78;
		
		System.out.println("natural numbers from " + f + " to " + t);
		
		cursor = naturalNumbers( f, t);
		
		cursor.open();
		
		while (cursor.hasNext())
			System.out.println(cursor.next());
		System.out.println("- end -");
		
		cursor.close();

		/*********************************************************************/
		/*                            Example 2b                             */
		/*********************************************************************/
		
		/*System.out.println("natural numbers (all) (0 to Long.MAX_VALUE)");
		
		cursor = naturalNumbers();
		
		cursor.open();
		
		while (cursor.hasNext())
			System.out.println(cursor.next());
		System.out.println("- end -");
		
		cursor.close();*/
	}
}
