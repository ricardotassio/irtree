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

package xxl.core.util.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This class wraps a given Object that implements a known interface,
 * so that all Methods become synchronized. It uses the reflection mechanism that
 * was introduced with jdk 1.3.
 */
public class SynchronizedWrapper implements InvocationHandler {
	/** Object that should be synchronized */
	private Object delegate;

	/** 
	 * Produces dynamically a SynchronizedWrapper for the specified interface and object and 
	 * returns it.
	 *
	 * @param delegate Object to which the calls are delegated.
	 * @param interfaceName name of the interface
	 * @return returns a SynchronizedWrapper produced
	 * 
	 */
	public static Object newInstance(Object delegate, String interfaceName) {
		try {
			Class c = Class.forName(interfaceName);

			return Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[] { c },
				new SynchronizedWrapper(delegate)
			);
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Creates a SynchronizedWrapper (private!).
	 * 
	 * @param delegate Object to which the calls are delegated.
	 */
	private SynchronizedWrapper(Object delegate) {
		this.delegate = delegate;
	}

	/**
	 * This method is invoked automatically by the proxy. It is 
	 * unusual to call this method directly.
	 *
	 * @param proxy the proxy instance that the method was invoked on
	 * @param meth the Method instance corresponding to the interface method 
	 * 	invoked on the proxy instance. The declaring class of the Method 
	 *	object will be the interface that the method was declared in, which 
	 *	may be a superinterface of the proxy interface that the proxy class 
	 *	inherits the method through.
	 * @param args an array of objects containing the values of the arguments passed 
	 *	in the method invocation on the proxy instance, or null if interface 
	 *	method takes no arguments. Arguments of primitive types are wrapped in 
	 *	instances of the appropriate primitive wrapper class, such as 
	 *	java.lang.Integer or java.lang.Boolean.
	 * @return the value to return from the method invocation on the proxy instance. 
	 *	If the declared return type of the interface method is a primitive type, 
	 *	then the value returned by this method must be an instance of the 
	 *	corresponding primitive wrapper class; otherwise, it must be a type 
	 *	assignable to the declared return type. If the value returned by this 
	 *	method is null and the interface method's return type is primitive, then 
	 *	a NullPointerException will be thrown by the method invocation on the 
	 *	proxy instance. If the value returned by this method is otherwise not 
	 *	compatible with the interface method's declared return type as described 
	 *	above, a ClassCastException will be thrown by the method invocation on 
	 *	the proxy instance.
	 * @throws Throwable
	 */
	public synchronized Object invoke(Object proxy, Method meth, Object[] args) throws Throwable {
		try {	
			return meth.invoke(delegate, args);
		}
		catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	/** Interface needed in the example (only). */
	private interface TestWrapper {
		/**
		 * init example TestWrapper
		 */
		public void init();
		/**
		 * call example TestWrapper
		 */
		public void call();
	}
	
	/**
	 * Usage Example for SynchronizedWrapper.
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 	  	   submit parameters when the main method is called.
	 */
	public static void main(String args[]) {
		System.out.println("Wraping a class to be completly synchronized");
		System.out.println();

		TestWrapper a = new TestWrapper() {
			int i;
			public void init() {
				i=0;
			}
			public void call() {
				if (i!=0)
					System.out.println("Fehler in SynchronizedWrapper");
				i++;
				try { Thread.sleep(10); } catch (Exception e) {}
				i--;
			}
		};
		// and now the glory...
		// System.out.println(TestWrapper.class.getName());
		final TestWrapper aWrapped = (TestWrapper) newInstance(a,"xxl.core.util.reflect.SynchronizedWrapper$TestWrapper");
		aWrapped.init();

		final xxl.core.util.concurrency.MutableBoolean cont = new xxl.core.util.concurrency.MutableBoolean(true);

		for (int i=0 ; i<10 ; i++) {
			new Thread () {
				public void run(){
					while (cont.get()) {
						aWrapped.call();
					}
				}
			}.start();
		}

		// Test it for two seconds.
		try { Thread.sleep(2000); } catch (Exception e) {}
		cont.set(false);

		System.out.println();
		System.out.println("Test successfully completed.");
	}
}
