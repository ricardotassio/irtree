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

package	xxl.core.cursors.sources;

import xxl.core.cursors.AbstractCursor;

/**
 * An empty cursor contains no elements, i.e., every call to its <tt>hasNext</tt>
 * method will return <tt>false</tt>. This class is useful when you have to
 * return an iteration of some kind, but there are no elements the iteration
 * points to. The class contains a static field, called
 * <tt>DEFAULT_INSTANCE</tt>, which is similar to the design pattern, named
 * <i>Singleton</i>, except that there are no mechanisms to avoid the creation of
 * other instances. The intent of the design pattern is to ensure this class only
 * has one instance, and provide a global point of access to it. For further
 * information see: "Gamma et al.: <i>DesignPatterns. Elements of Reusable
 * Object-Oriented Software.</i> Addision Wesley 1998."
 * 
 * <p><b>Example usage:</b>
 * <pre>
 *     EmptyCursor emptyCursor = new EmptyCursor();
 *     
 *     emptyCursor.open();
 * 
 *     System.out.println("Is a next element available? " + emptyCursor.hasNext());
 * 
 *     emptyCursor.close();
 * 
 *     System.out.println("Is a next element available? " + EmptyCursor.DEFAULT_INSTANCE.hasNext());
 * </pre>
 * This example shows in two different ways that an empty cursor contains no
 * elements:
 * <ul>
 *     <li>
 *         A new instance of this class is directly created and the
 *         <tt>hasNext</tt> method is called.
 *     </li>
 *     <li>
 *         The static field <tt>DEFAULT_INSTANCE</tt>, which returns an instance
 *         of this class, is used instead and then the <tt>hasNext</tt> method is
 *         called.
 *     </li>
 * </ul>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 */
public class EmptyCursor extends AbstractCursor {

	/**
	 * This instance can be used for getting a default instance of an empty
	 * cursor. It is similar to the <i>Singleton Design Pattern</i> (for further
	 * details see Creational Patterns, Prototype in <i>Design Patterns: Elements
	 * of Reusable Object-Oriented Software</i> by Erich Gamma, Richard Helm,
	 * Ralph Johnson, and John Vlissides) except that there are no mechanisms to
	 * avoid the creation of other instances of this empty cursor.
	 */
	public static final EmptyCursor DEFAULT_INSTANCE = new EmptyCursor();

	/**
	 * Constructs a new empty cursor that contains no elements.
	 */
	public EmptyCursor() {}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the empty cursor has more elements.
	 */
	protected boolean hasNextObject() {
		return false;
	}
	
	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the empty cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return null;
	}

	/**
	 * Closes the cursor, i.e., signals the cursor to clean up resources, close
	 * files, etc. When a cursor has been closed calls to methods like
	 * <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield proper
	 * results. Multiple calls to <tt>close</tt> do not have any effect, i.e.,
	 * if <tt>close</tt> was called the cursor remains in the state
	 * <i>closed</i>.
	 * 
	 * <p>Note, that this implementation is empty. So <tt>close</tt> does
	 * not have any effect. This is necessary to allow multiple references
	 * to <tt>EmptyCursor.DEFAULT_INSTANCE<tt>.</p>
	 */
	public void close() {
	}

	
	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the empty cursor. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the empty cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsRemove() {
		return true;
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the empty cursor. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the empty cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsUpdate() {
		return true;
	}

	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the empty cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the empty cursor, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return true;
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
		
		EmptyCursor emptyCursor = new EmptyCursor();
		
		emptyCursor.open();
		
		System.out.println("Is a next element available? " + emptyCursor.hasNext());
		
		emptyCursor.close();
		
		System.out.println("Is a next element available? " + EmptyCursor.DEFAULT_INSTANCE.hasNext());
	}
}
