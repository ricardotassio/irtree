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

package xxl.core.cursors.unions;

import java.util.Iterator;

import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.cursors.mappers.Mapper;
import xxl.core.cursors.sources.ArrayCursor;
import xxl.core.cursors.sources.EmptyCursor;
import xxl.core.functions.Constant;
import xxl.core.functions.Function;

/**
 * A sequentializer concatenates <tt>n</tt> input iterations to a single one. To
 * provide this functionality, it internally accesses an iteration of input
 * iterations specified by the user and stores a reference to the currently
 * processed inout iteration. If this input iteration has been completely
 * consumed, the next input iteration delivered by the internal iteration of
 * input iterations will be processed. Moreover it is possible to define a
 * function delivering the second input input iteration to be processed.
 * 
 * <p><b>Implementation details:</b> The attribute <tt>cursor</tt> represents the
 * currently processed input iteration and is set by the method
 * <tt>hasNextObject</tt> to the next input iteration as follows:
 * <pre>
 *     while (!cursor.hasNext()) {
 *         cursor.close();
 *         if (iteratorsCursor.hasNext())
 *             cursor = Cursors.wrap((Iterator)iteratorsCursor.next());
 *         else
 *             cursor = EmptyCursor.DEFAULT_INSTANCE;
 *     }
 * </pre>
 * The method <tt>next</tt> returns the next element of the currently used input
 * iteration, i.e., the <tt>next</tt> method of <tt>cursor</tt> is called. So a
 * sequentializer sets the attribute <tt>cursor</tt> to the first input iteration
 * and returns this iteration by lazy evaluation, after that the attribute
 * <tt>cursor</tt> is set to the next input iteration and so on.</p>
 * 
 * <p><b>Note:</b> When the given input iteration only implements the interface
 * {@link java.util.Iterator} it is wrapped to a cursor by a call to the static
 * method {@link xxl.core.cursors.Cursors#wrap(Iterator) wrap}.</p>
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     Sequentializer sequentializer = new Sequentializer(
 *         new Enumerator(11),
 *         new Enumerator(11, 21)
 *     );
 * 
 *     sequentializer.open();
 * 
 *     while (sequentializer.hasNext())
 *         System.out.print(sequentializer.next() + "; ");
 *     System.out.flush();
 *     System.out.println();
 * 
 *     sequentializer.close();
 * </pre>
 * This instance of a sequentializer concatenates the two given enumerators. The
 * first enumerator contains the elements 0,...,10. The second one contains the
 * elements 11,...,20. So the result of the completely consumed sequentializer
 * is an ascending sequence with range [0, 20]. </p>
 *
 * <p><b>Example usage (2):</b>
 * <pre>
 *     sequentializer = new Sequentializer(
 *         new Enumerator(1, 4),
 *         new Function() {
 *             public Object invoke() {
 *                 return new Enumerator(4, 7);
 *             }
 *         }
 *     );
 * 
 *     sequentializer.open();
 * 
 *     while (sequentializer.hasNext())
 *         System.out.print(sequentializer.next() + "; ");
 * 
 *     System.out.flush();
 *     System.out.println();
 * 
 *     sequentializer.close();
 * </pre>
 * This instance of a sequentializer concatenates the three elements of the first
 * enumerator (1, 2, 3) with the elements of the second enumerator delivered by
 * invoking the defined function. So the output printed to the output stream is:
 * <pre>
 *     1; 2; 3; 4; 5; 6;
 * </pre></p>
 *
 * <p><b>Example usage (3):</b>
 * <pre>
 *     sequentializer = new Sequentializer(
 *         new HashGrouper(
 *             new Enumerator(21),
 *             new Function() {
 *                 public Object invoke(Object next) {
 *                     return new Integer(((Integer)next).intValue() % 5);
 *                 }
 *             }
 *         )
 *     );
 * 
 *     sequentializer.open();
 * 
 *     while (sequentializer.hasNext())
 *         System.out.print(sequentializer.next() + "; ");
 *     System.out.flush();
 *     System.out.println();
 * 
 *     sequentializer.close();
 * </pre>
 * This example demonstrates the sequentializer's concatentation using a
 * constructor that receives an iteration of input iterations as paramater. The
 * used {@link xxl.core.cursors.groupers.HashGrouper hash-grouper} is a cursor
 * and each element of this cursor points to a group of the used
 * {@link java.util.HashMap hash-map}, realized as a cursor. The elements of the
 * enumerator are inserted in the buckets of the hash-map by applying the given
 * function on them. So the buckets with keys 0,...,4 remain and are filled up.
 * For further details see {@link xxl.core.cursors.groupers.HashGrouper}. Now the
 * sequentializer takes these buckets (cursors) and concatenates them. So the
 * output is:
 * <pre>
 *     4; 9; 14; 19; 3; 8; 13; 18; 2; 7; 12; 17; 1; 6; 11; 16; 0; 5; 10; 15; 20;
 * </pre>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 */
public class Sequentializer extends AbstractCursor {

	/**
	 * The iteration containing the input iterations to be sequentialized.
	 */
	protected Iterator iteratorsCursor;

	/**
	 * The currently processed input iteration. The constructors set this cursor
	 * to a new {@link xxl.core.cursors.sources.EmptyCursor empty cursor}. This
	 * cursor is set in the method <tt>hasNextObject</tt> to the next input
	 * iteration as follows:
	 * <pre>
	 *     while (!cursor.hasNext()) {
	 *         cursor.close();
	 *         if (iteratorsCursor.hasNext())
	 *             cursor = Cursors.wrap((Iterator)iteratorsCursor.next());
	 *         else
	 *             cursor = EmptyCursor.DEFAULT_INSTANCE;
	 *     }
	 * </pre>
	 * If the input iteration is given by an iterator it is wrapped to a cursor.
	 */
	protected Cursor cursor = null;

	/**
	 * Creates a new sequentializer backed on an iteration of input iterations.
	 * Every iterator given to this constructor is wrapped to a cursor.
	 *
	 * @param iteratorsCursor iteration of input iterations to be sequentialized.
	 */
	public Sequentializer(Iterator iteratorsCursor) {
		this.iteratorsCursor = iteratorsCursor;
		this.cursor = new EmptyCursor();
	}

	/**
	 * Creates a new sequentializer backed on an array of input iterations. This
	 * array is converted to a cursor of input iterations using an
	 * {@link xxl.core.cursors.sources.ArrayCursor array-cursor}. Every iterator
	 * given to this constructor is wrapped to a cursor.
	 *
	 * @param iterators an array of input iterations to be sequentialized.
	 */
	public Sequentializer(Iterator[] iterators) {
		this(new ArrayCursor(iterators));
	}

	/**
	 * Creates a new sequentializer backed on two input iterations. Every
	 * iterator given to this constructor is wrapped to a cursor.
	 *
	 * @param iterator0 the first input iteration to be sequentialized.
	 * @param iterator1 the second input iteration to be sequentialized.
	 */
	public Sequentializer(Iterator iterator0, Iterator iterator1) {
		this(new Iterator[] {iterator0, iterator1});
	}

	/**
	 * Creates a new sequentializer backed on an input iteration and a
	 * parameterless function returning the second input iteration on demand.
	 * Every iterator given to this constructor is wrapped to a cursor.
	 *
	 * @param iterator the first input iteration to be sequentialized.
	 * @param function a parameterless function returning the second input
	 *        iteration to be sequentialized on demand. This function is invoked
	 *        after the first input iteration has been processed completely.
	 */
	public Sequentializer(Iterator iterator, Function function) {
		this(
			new Mapper(
				new ArrayCursor(
					new Function[] {
						new Constant(iterator),
						function
					}
				),
				new Function() {
					public Object invoke(Object function) {
						return ((Function)function).invoke();
					}
				}
			)
		);
	}

	/**
	 * Closes the sequentialier, i.e., signals it to clean up resources, close
	 * input iterations, etc. When a cursor has been closed calls to methods like
	 * <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield proper
	 * results. Multiple calls to <tt>close</tt> do not have any effect, i.e.,
	 * if <tt>close</tt> was called the cursor remains in the state
	 * <i>closed</i>.
	 * 
	 * <p>Note, that a closed cursor usually cannot be opened again because of
	 * the fact that its state generally cannot be restored when resources are
	 * released respectively files are closed.</p>
	 */
	public void close() {
		super.close();
		cursor.close();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other words,
	 * returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would return an
	 * element rather than throwing an exception.)
	 * 
	 * <p>The attribute <tt>cursor</tt> is set by this method to the next input
	 * iteration as follows:
	 * <pre>
	 *     while (!cursor.hasNext()) {
	 *         cursor.close();
	 *         if (iteratorsCursor.hasNext())
	 *             cursor = Cursors.wrap((Iterator)iteratorsCursor.next());
	 *         else
	 *             cursor = EmptyCursor.DEFAULT_INSTANCE;
	 *     }
	 * </pre>
	 * If the next input iteration is given by an iterator it is wrapped to a
	 * cursor. The method returns whether the currently processed input iteration
	 * contains further elements, i.e., the result of the <tt>hasNext</tt> method
	 * of <tt>cursor</tt>.
	 *
	 * @return <tt>true</tt> if the sequentializer has more elements.
	 */
	protected boolean hasNextObject() {
		while (!cursor.hasNext()) {
			cursor.close();
			if (iteratorsCursor.hasNext())
				cursor = Cursors.wrap((Iterator)iteratorsCursor.next());
			else {
				cursor = EmptyCursor.DEFAULT_INSTANCE;
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the sequentializer's methods, e.g., <tt>update</tt>
	 * or <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return cursor.next();
	}

	/**
	 * Removes from the underlying data structure the last element returned by
	 * the sequentializer (optional operation). This method can be called only
	 * once per call to <tt>next</tt> or <tt>peek</tt> and removes the element
	 * returned by this method. Note, that between a call to <tt>next</tt> and
	 * <tt>remove</tt> the invocation of <tt>peek</tt> or <tt>hasNext</tt> is
	 * forbidden. The behaviour of a cursor is unspecified if the underlying
	 * data structure is modified while the iteration is in progress in any way
	 * other than by calling this method.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>remove</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is
	 *         not supported by the sequentializer.
	 */
	public void remove() throws IllegalStateException, UnsupportedOperationException {
		super.remove();
		cursor.remove();
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the sequentializer. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the sequentializer, otherwise <tt>false</tt>.
	 */
	public boolean supportsRemove() {
		return cursor.supportsRemove();
	}

	/**
	 * Replaces the last element returned by the sequentializer in the underlying data
	 * structure (optional operation). This method can be called only once per
	 * call to <tt>next</tt> or <tt>peek</tt> and updates the element returned
	 * by this method. Note, that between a call to <tt>next</tt> and
	 * <tt>update</tt> the invocation of <tt>peek</tt> or <tt>hasNext</tt> is
	 * forbidden. The behaviour of a cursor is unspecified if the underlying
	 * data structure is modified while the iteration is in progress in any way
	 * other than by calling this method.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @param object the object that replaces the last element returned by the
	 *        sequentializer.
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>update</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>update</tt> operation is
	 *         not supported by the sequentializer.
	 */
	public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
		super.update(object);
		cursor.update(object);
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the sequentializer. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the sequentializer, otherwise <tt>false</tt>.
	 */
	public boolean supportsUpdate() {
		return cursor.supportsUpdate();
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
		
		Sequentializer sequentializer = new Sequentializer(
			new xxl.core.cursors.sources.Enumerator(11),
			new xxl.core.cursors.sources.Enumerator(11, 21)
		);
		
		sequentializer.open();
		
		while (sequentializer.hasNext())
			System.out.print(sequentializer.next() + "; ");
		System.out.flush();
		System.out.println();
		
		sequentializer.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		sequentializer = new Sequentializer(
			new xxl.core.cursors.sources.Enumerator(1, 4),
			new Function() {
				public Object invoke() {
					return new xxl.core.cursors.sources.Enumerator(4, 7);
				}
			}
		);
		
		sequentializer.open();
		
		while (sequentializer.hasNext())
			System.out.print(sequentializer.next() + "; ");
		System.out.flush();
		System.out.println();
		
		sequentializer.close();

		/*********************************************************************/
		/*                            Example 3                              */
		/*********************************************************************/
		
		sequentializer = new Sequentializer(
			new xxl.core.cursors.groupers.HashGrouper(
				new xxl.core.cursors.sources.Enumerator(21),
				new Function() {
					public Object invoke(Object next) {
						return new Integer(((Integer)next).intValue() % 5);
					}
				}
			)
		);
		
		sequentializer.open();
		
		while (sequentializer.hasNext())
			System.out.print(sequentializer.next() + "; ");
		System.out.flush();
		System.out.println();
		
		sequentializer.close();
	}
}
