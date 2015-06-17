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

package xxl.core.cursors.mappers;

import java.util.Iterator;

import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.functions.Function;

/**
 * A mapper invokes a given mapping function on an array of input iterations. The
 * mapping function is applied to <tt>n</tt> input iterations at the same time,
 * that means a <tt>n</tt>-dimensional function is called and its arguments are
 * the elements of each input iteration (an array storing one element per input
 * iteration). The result of this mapping is an array storing the results of the
 * different functions that is returned by the mapper. Also a partial input is
 * allowed with the intention to apply the mapping function on less than
 * <tt>n</tt> arguments.
 * 
 * <p><b>Note:</b> When the given input iteration only implements the interface
 * {@link java.util.Iterator} it is wrapped to a cursor by a call to the static
 * method {@link xxl.core.cursors.Cursors#wrap(Iterator) wrap}. Additionally some
 * mapping functions are supplied in the class
 * {@link xxl.core.functions.Functions}.</p>
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     Mapper mapper = new Mapper(
 *         new Enumerator(21),
 *         new Function() {
 *             public Object invoke(Object[] arguments) {
 *                 return new Integer(((Integer)arguments[0]).intValue() * 2);
 *             }
 *         }
 *     );
 * 
 *     mapper.open();
 * 
 *     while (mapper.hasNext())
 *         System.out.print(mapper.next() + "; ");
 *     System.out.flush();
 * 
 *     mapper.close();
 * </pre>
 * This mapper maps the given numbers of the enumerator with range 0,...,20
 * concerning the above defined function
 * <pre>
 *     f : x --> 2*x.
 * </pre>
 * The function is applied on each element of the given enumerator, therefore the
 * following output is printed to the output stream:
 * <pre>
 *     0; 2; 4; 6; 8; 10; ... ; 36; 38; 40;
 * </pre>
 * But pay attention that the function's <tt>invoke</tt> method gets an object
 * array as parameter!</p>
 *
 * <p><b>Example usage (2):</b>
 * <pre>
 *     HashGrouper hashGrouper = new HashGrouper(
 *         new Enumerator(21),
 *         new Function() {
 *             public Object invoke(Object next) {
 *                 return new Integer(((Integer)next).intValue() % 5);
 *             }
 *         }
 *     );
 * 
 *     hashGrouper.open();
 * 
 *     Cursor[] cursors = new Cursor[5];
 *     for (int i = 0; hashGrouper.hasNext(); i++)
 *         cursors[i] = (Cursor)hashGrouper.next();
 *     
 *     mapper = new Mapper(
 *         cursors,
 *         new Function() {
 *             public Object invoke(Object[] arguments) {
 *                 return Cursors.minima(new ArrayCursor(arguments)).getFirst();
 *             }
 *         }
 *     );
 * 
 *     mapper.open();
 * 
 *     while (mapper.hasNext())
 *         System.out.print(mapper.next() + "; ");
 *     System.out.flush();
 *     
 *     mapper.close();
 *     hashGrouper.close();
 * </pre>
 * This example uses the a hash-grouper to partition the delivered numbers of the
 * input enumerator. For further information see
 * {@link xxl.core.cursors.groupers.HashGrouper}. The buckets of the hash-map
 * used by the hash-grouper are stored in the cursor array <tt>cursors</tt>. Then
 * a new mapper is created that maps this cursor array to the first element of
 * its contained minima. Therefore the static method <tt>minima</tt> is used
 * returning a linked list, where the <tt>getFirst</tt> method is applied. For
 * the first call to <tt>next</tt> the cursor array contains the integer objects
 * with value 0,...,4, namely the first element of each bucket. The returned
 * minimum is 0. The next call to this method returns the minimum of 5,...,10,
 * namely the second element in each bucket. So the output of this use case is:
 * <pre>
 *     0; 5; 10; 15;
 * </pre>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 */
public class Mapper extends AbstractCursor {

	/**
	 * The array of input iterations holding the data to be mapped.
	 */
	protected Cursor[] inputs;

	/**
	 * The function used to map the input-elements to an output-element.
	 */
	protected Function function;

	/**
	 * The arguments the function is applied to.
	 */
	protected Object[] arguments = null;

	/**
	 * A flag to detect if the function can be applied to less than one element
	 * of each input iteration.
	 */
	protected boolean allowPartialInput = true;

	/**
	 * Creates a new mapper using an input iteration array and a user defined
	 * function to map the elements. The flag <tt>allowPartialInput</tt> defines
	 * whether the function can be used with a lower dimension than
	 * <tt>iterators.length</tt>. Every iterator to this constructor is wrapped
	 * to a cursor.
	 *
	 * @param iterators the input iterations.
	 * @param function the function used to map the input-elements to an
	 *        output-element.
	 * @param allowPartialInput <tt>true</tt> if the function can be applied to
	 *        less than one element of each input iterator.
	 */
	public Mapper(Iterator[] iterators, Function function, boolean allowPartialInput) {
		this.inputs = new Cursor[iterators.length];
		for (int i = 0; i < iterators.length; i++)
			inputs[i] = Cursors.wrap(iterators[i]);
		this.function = function;
		this.allowPartialInput = allowPartialInput;
	}

	/**
	 * Creates a new mapper using the given input iteration array and user
	 * defined function to map the elements. Every iterator given to this
	 * constructor is wrapped to a cursor and no partial input is allowed.
	 *
	 * @param iterators the input iterations.
	 * @param function the function used to map the input-elements to an
	 *        output-element.
	 */
	public Mapper(Iterator[] iterators, Function function) {
		this(iterators, function, false);
	}

	/**
	 * Creates a new mapper using the given input iteration array and an
	 * {@link xxl.core.functions.Function#IDENTITY identity} function to map the
	 * elements. Every iterator given to this constructor is wrapped to a cursor
	 * and no partial input is allowed.
	 *
	 * @param iterators the input iterations.
	 */
	public Mapper(Iterator[] iterators) {
		this(iterators, Function.IDENTITY);
	}

	/**
	 * Creates a new mapper with only one input iteration and a user defined
	 * function to map the elements. An iterator given to this constructor is
	 * wrapped to a cursor and no partial input is allowed.
	 *
	 * @param iterator the input iteration.
	 * @param function the function used to map an input-element to an
	 *        output-element.
	 */
	public Mapper(Iterator iterator, Function function) {
		this(new Iterator[] {iterator}, function);
	}

	/**
	 * Creates a new mapper using two input iterators and the an
	 * {@link xxl.core.functions.Function#IDENTITY identity} function to map the
	 * elements. Every iterator given to this constructor is wrapped to a cursor
	 * and no partial input is allowed.
	 *
	 * @param iterator0 the first input iteration.
	 * @param iterator1 the second input ion.
	 */
	public Mapper(Iterator iterator0, Iterator iterator1) {
		this(new Iterator[] {iterator0, iterator1}, Function.IDENTITY);
	}

	/**
	 * Creates a new mapper using two input iterations and a user defined
	 * function to map the elements. Every iterator given to this constructor is
	 * wrapped to a cursor and no partial input is allowed.
	 *
	 * @param iterator0 the first input iteration.
	 * @param iterator1 the second input iteration.
	 * @param function the function used to map the input-elements to an
	 *        output-element.
	 */
	public Mapper(Iterator iterator0, Iterator iterator1, Function function) {
		this(new Iterator[] { iterator0, iterator1 }, function);
	}

	/**
	 * Opens the mapper, i.e., signals the cursor to reserve resources, open
	 * input iterations, etc. Before a cursor has been opened calls to methods
	 * like <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield proper
	 * results. Therefore <tt>open</tt> must be called before a cursor's data can
	 * be processed. Multiple calls to <tt>open</tt> do not have any effect,
	 * i.e., if <tt>open</tt> was called the cursor remains in the state
	 * <i>opened</i> until its <tt>close</tt> method is called.
	 * 
	 * <p>Note, that a call to the <tt>open</tt> method of a closed cursor
	 * usually does not open it again because of the fact that its state
	 * generally cannot be restored when resources are released respectively
	 * files are closed.</p>
	 */
	public void open() {
		super.open();
		for (int i = 0; i < inputs.length; i++)
			inputs[i].open();
	}
	
	/**
	 * Closes the mapper. Signals the mapper to clean up resources, close input
	 * iterations, etc. After a call to <tt>close</tt> calls to methods like
	 * <tt>next</tt> or <tt>peek</tt> are not guarantied to yield proper results.
	 * Multiple calls to <tt>close</tt> do not have any effect, i.e., if
	 * <tt>close</tt> was called the mapper remains in the state "closed".
	 */
	public void close() {
		super.close();
		for (int i = 0; i < inputs.length; i++)
			inputs[i].close();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * <p>This method returns <tt>true</tt> if all input iterations have more
	 * elements or if at least one input iteration has more elements and
	 * partial input is allowed.</p>
	 * 
	 * @return <tt>true</tt> if the mapper has more elements.
	 */
	protected boolean hasNextObject() {
		int count = 0;
		for (int i = 0; i < inputs.length; i++)
			if (inputs[i].hasNext())
				count++;
		return count == 0 ?
			false :
			count == inputs.length ?
				true :
				allowPartialInput;
	}

	/**
	 * Computes the next element to be returned by a call to <tt>next</tt>
	 * or <tt>peek</tt>.
	 * 
	 * <p>A new object array <tt>arguments</tt> with length
	 * <tt>inputs.length</tt> is assigned and filled with the next elements of
	 * the input iterations or <tt>null</tt>, if the next element of such an
	 * input iteration does not exist. Then the mapping function is applied and 
	 * the next element to be returned by the mapper is returned.</p>
	 *
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		arguments = new Object[inputs.length];
		for (int i = 0; i < inputs.length; i++)
			arguments[i] = inputs[i].hasNext() ?
				inputs[i].next() :
				null;
		return function.invoke(arguments);
	}

	/**
	 * Removes from the underlying data structure the last element returned by
	 * the mapper (optional operation). This method can be called only once per
	 * call to <tt>next</tt> or <tt>peek</tt> and removes the element returned
	 * by this method. Note, that between a call to <tt>next</tt> and
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
	 *         not supported by the cursor.
	 */
	public void remove() throws IllegalStateException, UnsupportedOperationException {
		super.remove();
		for (int i = 0; i < inputs.length; i++)
			inputs[i].remove();
	}

	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the mapper. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the mapper, otherwise <tt>false</tt>.
	 */
	public boolean supportsRemove() {
		boolean supportsRemove = true;
		for (int i = 0; i < inputs.length; i++)
			supportsRemove &= inputs[i].supportsRemove();
		return supportsRemove;
	}
	
	/**
	 * Resets the mapper to its initial state such that the caller is able to
	 * traverse the underlying data structure again without constructing a new
	 * cursor (optional operation). The modifications, removes and updates
	 * concerning the underlying data structure, are still persistent.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the mapper.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		for (int i = 0; i < inputs.length; i++)
			inputs[i].reset();
	}
	
	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the mapper. Otherwise it returns <tt>false</tt>.
	 * 
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the mapper, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		boolean supportsReset = true;
		for (int i = 0; i < inputs.length; i++)
			supportsReset &= inputs[i].supportsReset();
		return supportsReset;
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
		
		Mapper mapper = new Mapper(
			new xxl.core.cursors.sources.Enumerator(21),
			new Function() {
				public Object invoke(Object[] arguments) {
					return new Integer(((Integer)arguments[0]).intValue() * 2);
				}
			}
		);
		
		mapper.open();
		
		while (mapper.hasNext())
			System.out.print(mapper.next() + "; ");
		System.out.flush();
		System.out.println();
		
		mapper.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		xxl.core.cursors.groupers.HashGrouper hashGrouper = new xxl.core.cursors.groupers.HashGrouper(
			new xxl.core.cursors.sources.Enumerator(21),
			new Function() {
				public Object invoke(Object next) {
					return new Integer(((Integer)next).intValue() % 5);
				}
			}
		);
		
		hashGrouper.open();
		
		Cursor[] cursors = new Cursor[5];
		for (int i = 0; hashGrouper.hasNext(); i++)
			cursors[i] = (Cursor)hashGrouper.next();
		
		mapper = new Mapper(
			cursors,
			new Function() {
				public Object invoke(Object[] arguments) {
					return Cursors.minima(new xxl.core.cursors.sources.ArrayCursor(arguments)).getFirst();
				}
			}
		);
		
		mapper.open();
		
		while (mapper.hasNext())
			System.out.print(mapper.next() + "; ");
		System.out.flush();
		
		mapper.close();
	}
}
