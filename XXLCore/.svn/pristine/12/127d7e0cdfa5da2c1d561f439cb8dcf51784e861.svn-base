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

package xxl.core.util;

import java.util.Arrays;

/**
 * This class implements an n-AbstractTuple of objects. <br>
 * A n-tuple is compared and hashed in terms of its objects.
 * <p>
 * <b>Example usage:</b>
 * <br><br>
 * <code><pre>
 * 	AbstractTuple tuple1 = new AbstractTuple( new Integer(13), new Integer(42));
 * 	AbstractTuple tuple2 = new AbstractTuple(
 * 		new Object[] {
 * 			new Integer(7),
 * 			new Integer(13),
 * 			new Integer(21),
 * 			new Integer(42)
 * 		}
 * 	);
 *
 * 	System.out.println("AbstractTuple1: " +tuple1);
 * 	System.out.println("AbstractTuple2: " +tuple2);
 * 	System.out.println();
 * 	System.out.println("Length of tuple1: " +tuple1.length());
 * 	System.out.println("Length of tuple2: " +tuple2.length());
 * 	System.out.println("Are tuple1 and tuple2 equal? " +tuple1.equals(tuple2));
 * 	System.out.println("Setting the second component of tuple1 to a new Character object 'A'.");
 * 	tuple1.set(1, new Character('A'));
 * 	System.out.println("AbstractTuple1: " +tuple1);
 * 	System.out.println("Getting the third component of tuple2 printed to the output stream: " +tuple2.get(2));
 * </code></pre>
 * This example shows the creation of a 2-tuple and a 4-tuple and the most
 * fundamental operations applied to these tuples.
 * <p>
 * The following output was generated:<br>
 * <pre>
 * 	AbstractTuple1: {13; 42}
 * 	AbstractTuple2: {7; 13; 21; 42}
 *
 * 	Length of tuple1: 2
 * 	Length of tuple2: 4
 * 	Are tuple1 and tuple2 equal? false
 * 	Setting the second component of tuple1 to a new Character object 'A'.
 * 	AbstractTuple1: {13; A}
 * 	Getting the third component of tuple2 printed to the output stream: 21
 * </pre>
 *
 * @see java.util.Arrays
 */
public class AbstractTuple implements Tuple {

	/** The array that backs the tuple. */
	protected Object [] objects;

	/**
	 * Creates a new tuple using the given array.
	 * No primitive types allowed.
	 *
	 * @param objects The array that backs the tuple.
	*/
	public AbstractTuple (Object [] objects) {
		this.objects = objects;
	}

	/**
	 * Creates a tuple using the two given objects.
	 *
	 * @param o1 first object in tuple.
	 * @param o2 second object in tuple.
	 */
	public AbstractTuple (Object o1, Object o2) {
		this(new Object[]{o1, o2});
	}

	/** Returns a copy of this Tuple by cloning the objects[] array.
		Note that the Objects referenced by the array are NOT cloned.
	 * @return returns a copy of this Tuple by cloning the objects[] array
	*/
	public Object clone(){
		return new AbstractTuple( (Object[]) objects.clone() );
	}

	/**
	 * Returns the length of the underlying array that represents the tuple (cardinality).
	 *
	 * @return The length of the underlying array (tuple's cardinality).
	 */
	public int length () {
		return objects.length;
	}

	/**
	 * Compares two tuples in terms of their objects.
	 *
	 * @param o tuple to be compared with.
	 * @return Returns <tt>true</tt> if the given object <tt>o</tt>
	 * 		is a tuple whose array is equal to this array, tested with
	 *		{@link java.util.Arrays#equals(Object[], Object[])}.
	 */
	public boolean equals (Object o) {
		try {
			AbstractTuple tuple = (AbstractTuple)o;

			return Arrays.equals(objects, tuple.objects);
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Returns a String representation of this tuple.
	 *
	 * @return the String representation of this tuple.
	 */
	public String toString () {
		String result = "{";

		for (int i=0; i<length(); i++) {
			result += objects[i].toString();
			result = i<length()-1 ? result+"; " : result;
		}
		return result+"}";
	}

	/**
	 * Computes a hash code for this tuple based on the objects stored in it. <br>
	 * Starting with '0', the hashCodes of the objects contained are superimposed with xor (^). <br>
	 * The hashCode of null is '0' by definition.
	 *
	 * @return Returns the hashCode.
	*/
	public int hashCode () {
		int hashCode = 0;

		for (int index = 0; index<length(); index++)
			hashCode ^= (objects[index]==null? 0: objects[index].hashCode());
		return hashCode;
	}

	/**
	 * Updates this tuple at the given index with the given object. <br>
	 *
	 * @param index The object of this tuple at the given index is being updated.
	 * @param object Updates the object of this tuple at the given index with the give object.
	 * @return The given object.
	 * @throws IndexOutOfBoundsException Throws an {@link java.lang.IndexOutOfBoundsException} if
	 * 		the index is negative or greater than or equal to <code>length()</code>.
	*/
	public Object set (int index, Object object) throws IndexOutOfBoundsException {
		if (index<0 || index>=length())
			throw new IndexOutOfBoundsException();
		return objects[index] = object;
	}

	/**
	 * Gets the object stored at the given index.
	 *
	 * @param index the object is stored at the specified index.
	 * @return Returns the object stored at the given index.
	 * @throws IndexOutOfBoundsException Throws an {@link java.lang.IndexOutOfBoundsException} if
	 * 		the index is negative or greater than or equal to <code>length()</code>.
	 */
	public Object get (int index) throws IndexOutOfBoundsException {
		if (index<0 || index>=length())
			throw new IndexOutOfBoundsException();
		return objects[index];
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main (String[] args) {

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		AbstractTuple tuple1 = new AbstractTuple( new Integer(13), new Integer(42));
		AbstractTuple tuple2 = new AbstractTuple(
			new Object[] {
				new Integer(7),
				new Integer(13),
				new Integer(21),
				new Integer(42)
			}
		);

		System.out.println("AbstractTuple1: " +tuple1);
		System.out.println("AbstractTuple2: " +tuple2);
		System.out.println();
		System.out.println("Length of tuple1: " +tuple1.length());
		System.out.println("Length of tuple2: " +tuple2.length());
		System.out.println("Are tuple1 and tuple2 equal? " +tuple1.equals(tuple2));
		System.out.println("Setting the second component of tuple1 to a new Character object 'A'.");
		tuple1.set(1, new Character('A'));
		System.out.println("AbstractTuple1: " +tuple1);
		System.out.println("Getting the third component of tuple2 printed to the output stream: " +tuple2.get(2));
	}
}
