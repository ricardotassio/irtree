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

package xxl.core.functions;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import xxl.core.cursors.Cursors;
import xxl.core.cursors.mappers.Mapper;


/**
 * This class contains some useful static methods for manipulating objects
 * of type {@link xxl.core.functions.Function Function}.
 */

public class Functions{

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private Functions(){}

	/** Returns the first function value (function takes 1 argument).
	 * @param iterator Iterator of arguments to the function
	 * @param function function to invoke
	 * @throws NoSuchElementException if the given {@link java.util.Iterator iterator} does not deliver
	 * the necessary number of objects
	 * @return the first function value.
	 */
	public static Object returnFirst( Function function, Iterator iterator) throws NoSuchElementException {
		return returnNth( function, iterator, 0);
	}

	/** Returns the first function value (function takes m arguments).
	 * @param iterators Iterator-array of arguments to the function.
	 * The array.length should correspond to the number of arguments of the function
	 * @param function function to invoke
	 * @throws NoSuchElementException if the given {@link java.util.Iterator iterators} do not deliver
	 * the necessary number of objects
	 * @return the first function value.
	 */
	public static Object returnFirst( Function function, Iterator [] iterators) throws NoSuchElementException {
		return returnNth( function, iterators, 0);
	}

	/** Returns the first function value (function takes 2 arguments).
	 * @param iterator0 first Iterator of arguments to the function
	 * @param iterator1 second Iterator of arguments to the function
	 * @param function function to invoke
	 * @throws NoSuchElementException if the given {@link java.util.Iterator iterators} do not deliver
	 * the necessary number of objects
	 * @return the first function value.
	 */
	public static Object returnFirst( Function function, Iterator iterator0, Iterator iterator1) throws NoSuchElementException {
		return returnNth( function, iterator0, iterator1, 0);
	}

	//////////////////////////////////////////////////////////////////////////////////

	/** Returns the last function value (function takes 1 argument).
	 * @param iterator Iterator of arguments to the function
	 * @param function function to invoke
	 * @throws NoSuchElementException if the given {@link java.util.Iterator iterator} does not deliver
	 * the necessary number of objects
	 * @return the last function value.
	 */
	public static Object returnLast( Function function, Iterator iterator) throws NoSuchElementException {
		return returnLast( function, new Iterator [] {iterator});
	}

	/** Returns the last function value (function takes m arguments).
	 * @param iterators Iterator-array of arguments to the function. array.length should correspond to the number of arguments of the function
	 * @param function function to invoke
	 * @throws NoSuchElementException if the given {@link java.util.Iterator iterators} do not deliver
	 * the necessary number of objects
	 * @return the last function value.
	 */
	public static Object returnLast( Function function, Iterator [] iterators) throws NoSuchElementException {
		return Cursors.last( new Mapper(iterators, function));
	}

	/** Returns the last function value (function takes 2 arguments).
	 * @param iterator0 first Iterator of arguments to the function
	 * @param iterator1 second Iterator of arguments to the function
	 * @param function function to invoke
	 * @throws NoSuchElementException if the given {@link java.util.Iterator iterators} do not deliver
	 * the necessary number of objects
	 * @return the last function value.
	 */
	public static Object returnLast( Function function, Iterator iterator0, Iterator iterator1) throws NoSuchElementException {
		return returnLast( function, new Iterator [] {iterator0, iterator1});
	}

	//////////////////////////////////////////////////////////////////////////////////

	/** Returns the n-th function value (function takes 1 argument).
	 * @param iterator Iterator of arguments to the function
	 * @param function function to invoke
	 * @param n the number of the returned value.
	 * @throws NoSuchElementException if the given {@link java.util.Iterator iterator} does not deliver
	 * the necessary number of objects
	 * @return the n-th function value.
	 */
	public static Object returnNth( Function function, Iterator iterator, int n) throws NoSuchElementException {
		return returnNth( function, new Iterator [] {iterator}, n);
	}

	/** Returns the n-th function value (function takes m arguments).
	 * @param iterators Iterator-array of arguments to the function.
	 * The array.length should correspond to the number of arguments of the function
	 * @param function function to invoke
	 * @param n the number of the returned value.
	 * @throws NoSuchElementException if the given {@link java.util.Iterator iterators} do not deliver
	 * the necessary number of objects
	 * @return the n-th function value.
	 */
	public static Object returnNth( Function function, Iterator [] iterators, int n) throws NoSuchElementException {
		return Cursors.nth( new Mapper(iterators, function), n);
	}

	/** Returns the n-th function value (function takes 2 arguments).
	 * @param function function to invoke
	 * @param iterator0 first Iterator of arguments to the function
	 * @param iterator1 second Iterator of arguments to the function
	 * @param n the number of the returned value.
	 * @throws NoSuchElementException if the given {@link java.util.Iterator iterators} do not deliver
	 * the necessary number of objects
	 * @return the n-th function value.
	 */
	public static Object returnNth( Function function, Iterator iterator0, Iterator iterator1, int n) throws NoSuchElementException {
		return returnNth( function, new Iterator [] {iterator0, iterator1}, n);
	}

	//////////////////////////////////////////////////////////////////////////////////

	/** Wraps an aggregation function to a unary function by storing
	 * the status of the aggregation internally.
	 * @param aggregateFunction aggregation function to provide as an unary function
	 * @return an unary function wrapping an aggregation function
	 */
	public static Function aggregateUnaryFunction( final Function aggregateFunction){
		return new Function(){
			Object agg = null;
			public Object invoke ( Object o){
				agg = aggregateFunction.invoke ( agg, o);
				return agg;
			}
		};
	}

	//////////////////////////////////////////////////////////////////////////////////

	/** Returns an one-dimensional real-valued function providing a product of
	 * two <tt>Objects</tt> of type {@link java.lang.Number}.
	 * The result of the mathematical operation will be returned by an Object of type <tt>Double</tt>!
	 * @return a {@link xxl.core.functions.Function Function} performing a multiplication of numerical data
	 */
	public static Function mult() {
		return new Function() {
			public Object invoke( Object p1, Object p2){
				return new Double( ((Number) p1).doubleValue() * ((Number) p2).doubleValue());
			}
		};
	}

	/** Returns an one-dimensional real-valued function providing a sum of
	 * two <tt>Objects</tt> of type {@link java.lang.Number}.
	 * The result of the mathematical operation will be returned by an Object of type <tt>Double</tt>!
	 * @return a {@link xxl.core.functions.Function Function} performing a summation of numerical data
	 */
	public static Function add() {
		return new Function() {
			public Object invoke( Object p1, Object p2){
				return new Double( ((Number) p1).doubleValue() + ((Number) p2).doubleValue());
			}
		};
	}

	/**
	 * Returns the hash-value of the given object wrapped to an Integer instance.
	 * Note, this implementation delivers an unary function. Do not apply to none, two or
	 * more parameters.
	 * @return the hash-value of the given object.
	 */
	public static Function hash() {
		return new Function() {
			public Object invoke(Object o) {
				return new Integer(o.hashCode());
			}
		};
	}
	
	/**
	 * This method returns a function which is the identity function with the side effect of sending
	 * the object to a PrintStream.
	 * @param ps PrintStream to which the object is sent
	 * @return the desired function
	 */
	public static Function printlnMapFunction(final PrintStream ps) {
		return new Function() {
			public Object invoke(Object o) {
				ps.println(o);
				return o;
			}
		};
	}

	/**
	 * This method returns a function which is the identity function with the side effect of sending
	 * the object to a PrintStream.
	 * @param f Function to be decorated.
	 * @param ps PrintStream to which the object is sent
	 * @param showArgs showing the arguments which are sent to the Function? (yes/no).
	 * @param beforeArgs String which is printed at first (before writing the arguments).
	 * @param argDelimiter String which delimits the arguments from each other.
	 * @param beforeResultDelimiter String which is places between the last argument (if 
	 *		this is printed) and the rest.
	 * @param afterResultDelimiter String which is printed after the result (at the end).
	 * @return the desired function
	 */
	public static Function printlnDecoratorFunction(final Function f, final PrintStream ps, final boolean showArgs,
			final String beforeArgs, final String argDelimiter, final String beforeResultDelimiter, final String afterResultDelimiter) {
		return new Function() {
			public Object invoke(Object o[]) {
				ps.print(beforeArgs);
				if (showArgs) {
					for (int i=0; i<o.length-1; i++) {
						ps.print(o[i]);
						ps.print(argDelimiter);
					}
					ps.print(o[o.length-1]);
				}
				ps.print(beforeResultDelimiter);

				Object ret = f.invoke(o);
				
				ps.print(ret);
				ps.print(afterResultDelimiter);
				return ret;
			}
		};
	}

	/**
	 * This method returns a function which is the identity function with an additional test.
	 * If the objects of subsequent invoke-calls do not adhere to the given Comparator, a 
	 * runtime exception is sent.
	 * @param c Comparator
	 * @param ascending true iff the order is ascending, else false
	 * @return the desired function
	 */
	public static Function comparatorTestMapFunction(final Comparator c, final boolean ascending) {
		return new Function() {
			boolean first = true;
			Object lastObject;
			int value = ascending?+1:-1;
			
			public Object invoke(Object o) {
				if (first)
					first = false;
				else if (c.compare(lastObject,o)*value>0)
					throw new RuntimeException("Ordering is not correct");
				lastObject = o;
				return o;
			}
		};
	}
}
