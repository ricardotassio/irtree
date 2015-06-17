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

import java.util.Iterator;

import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;

/**
 * An array-cursor constructs a cursor backed on an arbitrary object array. The
 * elements from a user-defined array (<tt>array[0]</tt> to
 * <tt>array[array.length-1]</tt>) will be returned by the array-cursor. Note,
 * that the order can also be user-definded when the caller specifies a further
 * iteration <tt>indices</tt> defining the order the elements will be returned.
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     Object[] numbers = Cursors.toArray(new Enumerator(5));
 *     
 *     ArrayCursor arrayCursor1 = new ArrayCursor(numbers);
 * 
 *     arrayCursor1.open();
 * 
 *     while (arrayCursor1.hasNext())
 *         System.out.print(arrayCursor1.next() + "; ");
 *     System.out.flush();
 *     System.out.println();
 * 
 *     arrayCursor1.close();
 * </pre>
 * At first this example converts an
 * {@link xxl.core.cursors.sources.Enumerator enumerator} with the elements
 * 0,...,4 to an object array using the static method
 * {@link xxl.core.cursors.Cursors#toArray(java.util.Iterator)}. This object
 * array called <tt>numbers</tt> is given to the second constructor of this class
 * and a new instance of an array-cursor is generated. This array-cursor is
 * completely consumed in the while-loop and at last it is closed. So the output
 * is:
 * <pre>
 *     0; 1; 2; 3; 4;
 * </pre></p>
 * 
 * <p><b>Example usage (2):</b>
 * <pre>
 *     // using an indices iterator
 * 
 *     ArrayCursor arrayCursor2 = new ArrayCursor(
 *         numbers,
 *         new Enumerator(4, -1)
 *     );
 * 
 *     arrayCursor2.open();
 * 
 *     while (arrayCursor2.hasNext())
 *         System.out.println(arrayCursor2.next() + "; ");
 *     System.out.flush();
 *     System.out.println();
 * 
 *     arrayCursor2.close();
 * </pre>
 * This example creates a new instance of an array-cursor using the above defined
 * enumerator and further an iteration of indices, given to the constructor,
 * implemented as an other enumerator containing the elements:
 * <pre>
 *     4; 3; 2; 1; 0.
 * </pre>
 * According to the order the iteration of indices defines, the output is
 * reversed.</p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 */
public class ArrayCursor extends AbstractCursor {

	/**
	 * The object array holding the data to be returned by the array-cursor.
	 */
	protected Object [] array;

	/**
	 * An iteration of indices defining the order the elements stored in the
	 * object array will be returned by the array-cursor. If such an iteration of
	 * indices has not been specified a simple enumerator holding the indices
	 * <tt>0</tt>,...,<tt>array.length-1</tt> is used instead, i.e., the data
	 * is returned in the same order as it is contained in the object array.
	 */
	protected Cursor indices;

	/**
	 * The current position of this iteration in the object array.
	 */
	protected Integer index = null;

	/**
	 * Creates a new array-cursor. The elements stored in the given oject array
	 * will be returned according to the order given by the iteration of indices.
	 *
	 * @param array the object array delivering the elements of the cursor.
	 * @param indices an iteration of indices; the indices of this iteration
	 *        define the order the elements will be returned by the array-cursor.
	 */
	public ArrayCursor(Object[] array, Iterator indices) {
		this.array = array;
		this.indices = Cursors.wrap(indices);
	}

	/**
	 * Creates a new array-cursor. The elements from <tt>array[0]</tt> to
	 * <tt>array[array.length-1]</tt> will be returned by this array-cursor. The
	 * order will be specified by a new enumerator, i.e., the elements are
	 * returned in the same order as they are contained in the given object
	 * array.
	 *
	 * @param array the object array delivering the elements.
	 */
	public ArrayCursor(Object[] array) {
		this(array, new Enumerator(array.length));
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the array-cursor has more elements.
	 */
	protected boolean hasNextObject() {
		return indices.hasNext();
	}

	/**
	 * Returns the next element in the iteration. This element will be accessible
	 * by some of the array-cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return array[(index = (Integer)indices.next()).intValue()];
	}

	/**
	 * Replaces the last element returned by the array-cursor in the underlying
	 * object array (optional operation). This method can be called only once per
	 * call to <tt>next</tt> or <tt>peek</tt> and updates the element returned
	 * by this method. Note, that between a call to <tt>next</tt> and
	 * <tt>update</tt> the invocation of <tt>peek</tt> or <tt>hasNext</tt> is
	 * forbidden. The behaviour of an array-cursor is unspecified if the
	 * underlying object array is modified while the iteration is in progress in
	 * any way other than by calling this method.
	 * 
	 * <p>Note, that this operation is optional and might work for all
	 * cursors</p>
	 *
	 * @param object the object that replaces the last element returned by the
	 *        array-cursor.
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>update</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>update</tt> operation is
	 *         not supported by the array-cursor.
	 */
	public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
		super.update(object);
		if (index == null)
			throw new IllegalStateException();
		array[index.intValue()] = object;
	}

	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the array-cursor. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the array-cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsUpdate() {
		return true;
	}

	/**
	 * Resets the array-cursor to its initial state such that the caller is able
	 * to traverse the underlying object array again without constructing a new
	 * array-cursor (optional operation). The modifications, removes and updates
	 * concerning the underlying object array, are still persistent.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the array-cursor.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		indices.reset();
		index = null;
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the array-cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the array-cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return indices.supportsReset();
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
		
		Object[] numbers = Cursors.toArray(new Enumerator(5));
		
		ArrayCursor arrayCursor1 = new ArrayCursor(numbers);
		
		arrayCursor1.open();
		
		while (arrayCursor1.hasNext())
			System.out.print(arrayCursor1.next() + "; ");
		System.out.flush();
		System.out.println();
		
		arrayCursor1.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		ArrayCursor arrayCursor2 = new ArrayCursor(
			numbers,
			new Enumerator(4,-1)
		); // using an indices iterator
		
		arrayCursor2.open();
		
		while (arrayCursor2.hasNext())
			System.out.print(arrayCursor2.next() + "; ");
		System.out.flush();
		System.out.println();
		
		arrayCursor2.close();
	}
}
