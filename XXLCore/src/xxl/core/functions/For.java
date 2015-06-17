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

import xxl.core.predicates.Less;
import xxl.core.predicates.Predicate;
import xxl.core.predicates.RightBind;

/**
 * A (recursive) functional for-loop.
 */

public class For extends DecoratorFunction {

	/** 
	 * This class provides a Function that expects an Integer-Object as its input and returns
	 * the increment. This means, if <code>new Integer(n)</code> is the input-argument
	 * <code>new Integer(n+1)</code> is returned.
	 */
	public static class IntegerIncrement extends Function{
	
		/** 
		 * This instance can be used for getting a default instance of
		 * IntegerIncrement. It is similar to the <i>Singleton Design
		 * Pattern</i> (for further details see Creational Patterns, Prototype
		 * in <i>Design Patterns: Elements of Reusable Object-Oriented
		 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
		 * Vlissides) except that there are no mechanisms to avoid the
		 * creation of other instances of IntegerIncrement.
		 */
		public static final IntegerIncrement DEFAULT_INSTANCE = new IntegerIncrement();
	
		/** 
		 * Returns the increment of the given Integer-Object.
		 * @param argument Integer-Object to increment
		 * @return a new Integer-Object representing the increment of the given argument
		 */
		public Object invoke( Object argument) {
			return new Integer( ( (Integer) argument).intValue() + 1 );
		}
	}

	/** 
	 * Constructs a new Object of this class.
	 * 
	 * @param predicate the Predicate that determines whether f1 is called recursively
	 * @param f1 code to be executed if predicate returns true, general contract:
	 *        has to return arguments for newState
	 * @param f2 code to be executed if predicate returns false
	 * @param newState computes new arguments for consequent call of f1
	 */
	public For( Predicate predicate, final Function f1, final Function f2, final Function newState){
		super();							//should be "super(new Iff..." (early bind of compiler prob)
		function = new Iff( predicate,
			new Function() {
				public Object invoke( Object[] arguments){	//Function to be called if predicate returned true
					return function.invoke( 		//recurse
						newState.invoke( 		//compute newState, i.e. increment, use return value as argument
										//for iff-Function
							f1.invoke( arguments) 	//execute actual code and use return value as argument vor newState
						)
					);
				}
			},
			f2							//Function to be called if predicate returned false
		);
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main( String [] args){
		//example: counting from 1 to 42
		For f = new For(
			new RightBind( Less.DEFAULT_INSTANCE, new Integer(42)),	//predicate
			Println.DEFAULT_INSTANCE,				//f1: Output
			Function.IDENTITY,						//f2: do nothing
			IntegerIncrement.DEFAULT_INSTANCE		//newState: increment int
		);

		Object out = f.invoke(new Integer(1));
		System.out.println("Return-value:\t"+((Object[])out)[0]);
	}
}