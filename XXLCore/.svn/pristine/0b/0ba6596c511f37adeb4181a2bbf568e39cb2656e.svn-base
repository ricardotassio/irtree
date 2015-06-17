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

import java.util.Iterator;

import xxl.core.cursors.sources.EmptyCursor;
import xxl.core.cursors.sources.Enumerator;

/** 
 * This class provides a Factory-method for Object-typed-arrays. The ArrayFactory creates
 * an array and fills it with Objects. An Iterator of arguments
 * can be passed to the invoke-method of this class to create the objects
 * which are stored in the array.
 */

public class ArrayFactory extends Function{

	/** A factory-method that overrides <code>invoke(Object)</code> and returns an array */
	protected Function newArray;

	/** A factory-method that overrides <code>invoke(Object)</code> and returns an Object */
	protected Function newObject;

	/** Creates a new ArrayFactory.
	 * @param newArray factory-method that returns an Object-type array
	 * @param newObject factory-method that returns Objects
	 */
	public ArrayFactory( Function newArray, Function newObject){
		this.newArray = newArray;
		this.newObject = newObject;
	}

    /**Returns the result of the ArrayFactory as an object. Note that the caller has to cast 
     * the result to the desired class. This method calls the invoke method of the newArray Function 
     * which returns an array of objects. After this, the invoke method of the newObject Function is called,
     * so many times as the length of the array. As parameter to the function an element of the iterator is given. 
     * Notice the second argument must be an iterator.
     * @param object the arguments to the function
     * @param iterator an iterator
     * @return the value is returned as an Object
     */
	public Object invoke( Object object, Object iterator){
		final Iterator arguments = (Iterator) iterator;
		Object [] array = (Object[]) newArray.invoke( object);
		for( int i=0; i<array.length; i++){
			array [i] = newObject.invoke( arguments.hasNext() ? arguments.next() : null);	//call invoke on newObject with argument-Object from arguments-Iterator
		}
		return array;
	}
    
    /**
     * Calls the invoke method using the EmptyCursor.DEFAULT_INSTANCE as default iterator.
     * @param object the arguments to the function
     * @return the value is returned as an Object
     */
	public Object invoke( Object object){
		return invoke( object, EmptyCursor.DEFAULT_INSTANCE);
	}

    /**
     * Calls the invoke method using the null Object as default Object.
     * @return the value is returned as an Object
     */
	public Object invoke(){
		return invoke( (Object) null);
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * submit parameters when the main method is called.
	 */
	public static void main(String[] args){
		// class to 'produce'
		/**
		 * Inner class used for use case.
		 */
		class A {
			
			/**
			 * A field holding an integer object.
			 */
			Integer a;

			/**
			 * A default constructor for this class.
			 * 
			 * @param a the integer object that is used to initialize this object.
			 *        If it is set to <tt>null</tt>, an integer object
			 *        representing 0 is used.
			 */
			A(Integer a) {
				this.a = a != null ? a : new Integer(0);
			}

			/**
			 * Returns a string representation of the object. In general, the
			 * <tt>toString</tt> method returns a string that "textually
			 * represents" this object. The result should be a concise but
			 * informative representation that is easy for a person to read. It
			 * is recommended that all subclasses override this method.
			 * 
			 * @return a string representation of the object.
			 */
			public String toString() {
				return new String("A" + a);
			}
		}

		Function f = new ArrayFactory(
			new Function(){
				public Object invoke(Object object){
					return new A[((Integer)object).intValue()];
				}
			},
			new Function(){
				public Object invoke(Object object){
					return new A((Integer)object);
				}
			}
		);

		A[] a = (A[]) f.invoke(new Integer(10),new Enumerator());
		for(int i=0; i<a.length; i++){
			System.out.println(i+"\t"+a[i]);
		}
	}
}