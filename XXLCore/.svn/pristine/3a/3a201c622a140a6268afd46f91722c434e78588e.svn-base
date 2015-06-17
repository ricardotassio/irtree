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

import java.io.Serializable;

/**
 * In Java, there is no direct support for the declaration of functional types
 * and functional variables. This deficiency is eliminated by the class Function.
 * In order to declare a function as an object of class Function, the invoke
 * method has to be overridden. The most general form of invoke expects the
 * input parameter being delivered in an array of objects. The output of invoke
 * is an object that generally has to be casted by the caller.
 * Simplified versions of invoke exists suitable for functions with none,
 * one or two input parameters. It is important to mention that in general only one of
 * the invoke methods has to be overriden. The invocation of a function is
 * then triggered by simply calling invoke.
 *
 * In combination with the powerful mechanism of anonymous classes, it is also
 * possible to build higher-order functions which is known from functional programming
 * (e. g. Haskel). The method compose shows how to declare a function f which
 * consists of h o (f1,...,fn) where h is a function with n arguments and f1,...,fn
 * are functions with an equal number of arguments. Note that compose provides the
 * declaration of the function and does not execute the function.
 *
 * Consider for example (see also the main-method) that you are
 * interested in building the function tan which
 * can be build by composing division and (sin, cos).
 * Function div, sin, cos;
 * <code><pre>
 *	// Initialization of your functional objects
 *	.....
 *	// Declaration of a new function
 *	Function tan = div.compose(new Function[]{sin,cos});
 *	....
 *	// Execution of the new function
 *	tan.invoke(new Double(x));
 * </code></pre>
 */

public class Function implements Serializable {

	/** The prototype function returns the argument (identity-function)	*/
	public static final Function IDENTITY = new Function () {

		/** Returns the argument itself (identity-function).
		 * @param argument the argument of the function
		 * @return the <code>argument</code> is returned
		 */
		public final Object invoke (final Object argument) {
			return argument;
		}

		/** Returns the arguments itself (identity-function).
		 * @param arguments the argument-array of the function
		 * @return the <code>arguments</code>-array is returned
		 */
		public final Object invoke (final Object [] arguments) {
			return arguments;
		}
	};

	/** Returns the result of the function as an object. Note that the
	 * caller has to cast the result to the desired class.
	 * This method determines <code>arguments.length</code> and
	 * calls the appropriate invoke-method (see below). The other
	 * invoke-methods call this method. This means, that
	 * the user either has to override this method or one (!) of the
	 * other invoke()-methods.
	 * If <tt>code</tt> is given as argument <code>invoke ( (Object) null)</code>
	 * will be returned meaning <code>invoke ( Object )</code> is needed to
	 * be overriden.
	 * <br>
	 * <pre>
	 * implementation:
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
                        //return null;
						throw new RuntimeException("Object invoke( Object[]) has to be overridden! arguments.length was "+arguments.length);
		}
	 * </pre>
	 * @param arguments the arguments to the function
	 * @throws RuntimeException if a <tt>argument []</tt> is given with length >= 3 and the
	 * corresponding invoke <code>Object invoke (Object[])</code> has not been overridden.
	 * @return the function value is returned
	 */
	public Object invoke( Object[] arguments) throws RuntimeException{
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
				throw new RuntimeException("Object invoke( Object[]) has to be overridden! arguments.length was "+arguments.length);
		}
	}

	/** Returns the result of the function as an object. Note that the
	 * caller has to cast the result to the desired class.
	 * <br>
	 * implementation:
	 * <code><pre>
		return invoke( new Object[0]);
	 * </code></pre>
	 * @return the function value is returned
	 */
	public Object invoke() {
		return invoke( new Object[0]);
	}

	/** Returns the result of the function as an object. Note that the
	 * caller has to cast the result to the desired class.
	 * <br>
	 * implementation:
	 * <code><pre>
		return invoke(new Object [] {argument});
	 * </code></pre>
	 * @param argument the argument to the function
	 * @return the function value is returned
	 */
	public Object invoke( Object argument) {
		return invoke( new Object [] {argument});
	}

	/** Returns the result of the function as an object. Note that the
	 * caller has to cast the result to the desired class.
	 * <br>
	 * implementation:
	 * <code><pre>
		return invoke(new Object [] {argument0, argument1});
	 * </code></pre>
	 * @param argument0 the first argument to the function
	 * @param argument1 the second argument to the function
	 * @return the function value is returned
	 */
	public Object invoke( Object argument0, Object argument1) {
		return invoke( new Object [] { argument0, argument1});
	}

	/** This method declares a new function <tt>h</tt> by composing an array of functions
	 * <tt>f_1, ..., f_n</tt> with this function <tt>g</tt>.<br>
	 * <b>Note</b>:<br>
	 * Note that the method does not execute the code of the new function <tt>h</tt>.
	 * The invocation of the composed function <tt>h</tt> is just triggered by
	 * a call of its own <tt>invoke-method</tt>.
	 * Then, the input parameters are passed to each function of the array
	 * <tt>(f_1, ..., f_n)</tt>, and the returned objects are used as the input parameters
	 * of the new function <tt>h</tt>. The invoke method of <tt>h</tt> returns the final result.
	 * @param functions the functions to be concatenated with this function.
	 * @return the result of the composition.
	 */
	public Function compose( final Function [] functions) {
		final Function outer = this;
		final DecoratorArrayFunction inner = new DecoratorArrayFunction( functions);
		return new Function() {
			public Object invoke ( Object [] objects) {
				return outer.invoke( (Object []) inner.invoke( objects));
			}
		};
	}

	/** This method declares a new function <tt>h</tt> by composing two functions
	 * <tt>f_1, f2</tt> with this function <tt>g</tt>.<br>
	 * <b>Note</b>:<br>
	 * Note that the method does not execute the code of the new function <tt>h</tt>.
	 * The invocation of the composed function <tt>h</tt> is just triggered by
	 * a call of its own <tt>invoke-method</tt>.
	 * Then, the input parameters are passed to each of the two functions
	 * and the returned objects are used as the input parameters
	 * of the new function <tt>h</tt>. The invoke method of <tt>h</tt> returns the final result.
	 * @param function1 the first function to concatenate with this function.
	 * @param function2 the second function to concatenate with this function.
	 * @return the result of the composition.
	 */
	public Function compose( Function function1, Function function2){
		return compose( new Function [] { function1, function2});
	}

	/** This method declares a new function <tt>h</tt> by composing it of a given function
	 * <tt>f</tt> with this function <tt>g</tt>.<br>
	 * <b>Note</b>:<br>
	 * Note that the method does not execute the code of the new function <tt>h</tt>.
	 * The invocation of the composed function <tt>h</tt> is just triggered by
	 * a call of its own <tt>invoke-method</tt>.
	 * Then, the input parameters are passed to the given function
	 * <tt>f</tt>, and the returned object is used as the input parameter
	 * of the new function <tt>h</tt>. The invoke method of <tt>h</tt> returns the final result.
	 * @param function the function to concatenate with this function.
	 * @return the result of the composition.
	 */
	public Function compose( Function function) {
		return compose( new Function [] { function});
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * submit parameters when the main method is called.
	 */
	public static void main (String[] args) {
		Function sin = new Function () {
					public Object invoke(Object o) {
						return new Double(Math.sin(((Double) o).doubleValue()));
					}
				};
		Function cos = new Function () {
					public Object invoke(Object o) {
						return new Double(Math.cos(((Double) o).doubleValue()));
					}
				};
		Function tan = new Function () {
					public Object invoke(Object o) {
						return new Double(Math.tan(((Double) o).doubleValue()));
					}
				};
		Function div = new Function () {
					public Object invoke(Object o1,Object o2) {
						return new Double( ((Double) o1).doubleValue() / ((Double) o2).doubleValue() );
					}
				};
		Double dv = new Double(0.5);
		if (args.length==1)
			dv = new Double(args[0]);
			
		System.out.println("Parameter value: "+dv);
		
		System.out.println("Sin: " + sin.invoke(dv));
		System.out.println("Cos: " + cos.invoke(dv));

		double resTan = ((Double) tan.invoke(dv)).doubleValue();
		double resComposition = ((Double) (div.compose(new Function[] {sin,cos})).invoke(dv)).doubleValue();
		
		System.out.println("Tan: " + resTan);
		System.out.println("div.compose(sin,cos): " + resComposition);
		
		if (Math.abs(resTan-resComposition)>1E-12)
			throw new RuntimeException("Something is wrong with function composition");
	}
}