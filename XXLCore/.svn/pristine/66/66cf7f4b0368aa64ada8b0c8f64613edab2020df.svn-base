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

package xxl.core.predicates;

import java.io.Serializable;

/**
 * This class provides a predicate, i.e. a kind of function that
 * implements a logical statement and, when it is invoked, determines
 * whether specified objects fulfill the statement. Predicates are
 * highly related to {@link xxl.core.functions.Function functions}. Like
 * functions, predicates provide a set of <tt>invoke</tt> methods that
 * can be used to evaluate the predicate. For providing predicates with
 * and without parameters, this class contains invoke methods with
 * zero, one and two arguments and with an array of arguments. The
 * <tt>invoke</tt> methods call themselves in a recursive manner. That
 * means when using the default implementation the <tt>invoke</tt>
 * method with zero, one or two arguments calls the <tt>invoke</tt>
 * method with an object array containing the arguments and the other
 * way round (see {@link #invoke(Object[])}, {@link #invoke()},
 * {@link #invoke(Object)} and {@link #invoke(Object, Object)}. For
 * this reason, an <tt>invoke</tt> method with the desired signatur has
 * to be overridden in order to declare a predicate as an object of
 * class Predicate.<p>
 *
 * But in contrast with functions, predicates do not return the result
 * of their evaluation as an object (that must be cast to a
 * <tt>Boolean</tt> object for getting the correct result), but as a
 * primitive <tt>boolean</tt> value.<p>
 *
 * A first example shows how to declare a tautology, i.e. a predicate
 * that always returns <tt>true</tt>:
 * <pre>
 *     Predicate tautology = new Predicate() {
 *         public boolean invoke (Object [] arguments) {
 *             return true;
 *         }
 *     };
 * </pre>
 * In this example, the <tt>invoke</tt> method with an object array
 * argument has been overwritten. Thanks to the recursive default
 * implementation of the <tt>invoke</tt> methods, every <tt>invoke</tt>
 * method calls the overwritten method and the overwritten method
 * itself breaks the recursion.<p>
 *
 * A second example shows how to declare a predicate implementing the
 * logical statement '<tt>n is even</tt>':
 * <pre>
 *     Predicate even = new Predicate() {
 *         public boolean invoke (Object n) {
 *             return ((Integer)n).intValue%2 == 0;
 *         }
 *     };
 * </pre>
 * In this example, the <tt>invoke</tt> method with one argument has
 * been overwritten. This method guarantees the specification of the
 * parameter <tt>n</tt> that is needed for the evaluation of the
 * predicate. Thanks to the recursive default implementation it is all
 * the same whether the user calls <tt>even.invoke(n)</tt> or
 * <tt>even.invoke(new Object[]{n})</tt>. Note that default
 * implementations of this class are still available. Therefore a call
 * to <tt>even.invoke()</tt> will cause an infinite recursive loop.
 *
 */
public abstract class Predicate implements Serializable {

	/**
	 * A prototype predicate that returns <tt>false</tt> every time it
	 * is invoked.
	 */
	public static final Predicate FALSE = new Predicate () {
		public boolean invoke (Object [] arguments) {
			return false;
		}
	};

	/**
	 * A prototype predicate that returns <tt>true</tt> every time it
	 * is invoked.
	 */
	public static final Predicate TRUE = new Predicate () {
		public boolean invoke (Object [] arguments) {
			return true;
		}
	};

	/**
	 * Sole constructor. (For invocation by subclass constructors,
	 * typically implicit.)
	 */
	public Predicate () {
	}

	/**
	 * Returns the result of the predicate as a primitive boolean value.
	 * This method determines <code>arguments.length</code> and calls
	 * the appropriate <tt>invoke</tt> method (see below). The other
	 * <tt>invoke</tt> methods call this method. This means, that the
	 * user either has to override this method or at least one (!) of
	 * the other <tt>invoke</tt> methods.<br>
	 * The following listing shows the exact implementation of this
	 * method:
	 * <pre>
	 *     if (arguments == null)
	 *         return invoke((Object)null);
	 *     switch (arguments.length) {
	 *         case 0 :
	 *             return invoke();
	 *         case 1 :
	 *             return invoke(arguments[0]);
	 *         case 2 :
	 *             return invoke(arguments[0], arguments[1]);
	 *         default :
	 *             throw new RuntimeException("Predicate.invoke(Object[] arguments) has to be overridden, arguments.length was " + arguments.length + ".");
	 *     }
	 * </pre>
	 *
	 * @param arguments the arguments to the predicate.
	 * @return the result of the predicate as a primitive boolean value.
	 * @throws RuntimeException if an object array with length&ge;3 is
	 *         given and the corresponding <tt>invoke</tt> method
	 *         (<code>Object invoke (Object[])</code>) has not been
	 *         overridden.
	 */
	public boolean invoke (Object[] arguments) {
		if(arguments==null)
			return invoke( (Object) null);

		switch (arguments.length) {
			case 0:
				return invoke();
			case 1:
				return invoke(arguments[0]);
			case 2:
				return invoke(arguments[0], arguments[1]);
			default:
				throw new RuntimeException("Predicate.invoke(Object[] arguments) has to be overridden! arguments.length was "+arguments.length);
		}
	}

	/**
	 * Returns the result of the predicate as a primitive boolean value.
	 * This method calls the <tt>invoke</tt> method working on object
	 * arrays with an empty object array (see below). This means, that
	 * the user has to override this method to create a predicate without
	 * any argument.<br>
	 * The following listing shows the exact implementation of this
	 * method:
	 * <pre>
	 *     return invoke(new Object[0]);
	 * </pre>
	 *
	 * @return the result of the predicate as a primitive boolean value.
	 */
	public boolean invoke () {
		return invoke(new Object[0]);
	}

	/**
	 * Returns the result of the predicate as a primitive boolean value.
	 * This method calls the <tt>invoke</tt> method working on object
	 * arrays with an object array containing the given argument (see
	 * below). This means, that the user has to override this method to
	 * create a predicate with one argument.<br>
	 * The following listing shows the exact implementation of this
	 * method:
	 * <pre>
	 *     return invoke(new Object [] {argument});
	 * </pre>
	 *
	 * @param argument the argument to the predicate.
	 * @return the result of the predicate as a primitive boolean value.
	 */
	public boolean invoke (Object argument) {
		return invoke(new Object [] {argument});
	}

	/**
	 * Returns the result of the predicate as a primitive boolean value.
	 * This method calls the <tt>invoke</tt> method working on object
	 * arrays with an object array containing the given arguments (see
	 * below). This means, that the user has to override this method to
	 * create a predicate with two arguments.<br>
	 * The following listing shows the exact implementation of this
	 * method:
	 * <pre>
	 *     return invoke(new Object [] {argument0, argument1});
	 * </pre>
	 *
	 * @param argument0 the first argument to the predicate.
	 * @param argument1 the second argument to the predicate.
	 * @return the result of the predicate as a primitive boolean value.
	 */
 	public boolean invoke (Object argument0, Object argument1) {
		return invoke(new Object [] {argument0, argument1});
	}

	/**
	 * The main method contains some examples how to use a Predicate.
	 * It can also be used to test the functionality of a Predicate.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args){
		// create a predicate that always returns true
		Predicate p = new Predicate() {
			public boolean invoke (Object [] arguments) {
				return true;
			}
		};
		System.out.println(p.invoke());
		System.out.println(p.invoke(new Object()));
		System.out.println(p.invoke(new Object(), new Object()));
		System.out.println(p.invoke(new Object[25]));
		System.out.println();

		// create a predicate that determines whether a given Integer
		// object is even or not
		p = new Predicate() {
			public boolean invoke (Object argument) {
				return ((Integer)argument).intValue()%2 == 0;
			}
		};
		for (int i = 0; i < 10; i++)
			System.out.println(i + " : " + p.invoke(new Integer(i)));
		System.out.println();
	}
}