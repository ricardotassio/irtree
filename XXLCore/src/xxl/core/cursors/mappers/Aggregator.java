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

If you want to be informed on new new versions of XXL you can
subscribe to our mailing-list. Send an email to

	xxl-request@lists.uni-marburg.de

without subject and the word "subscribe" in the message body.
*/

package xxl.core.cursors.mappers;

import java.util.Iterator;
import java.util.NoSuchElementException;

import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.functions.Function;
import xxl.core.math.Maths;

/**
 * The aggregator incrementally computes one or even more aggregates for an
 * input iteration. Due to the fact that an aggregator has to be initialized ,
 * the user defined aggregation function has to handle the case that the
 * aggregate is <tt>null</tt>. If the initialization of the aggregate is
 * finished, in the next step the aggregate function is applied on the aggregate
 * and the input iteration's (a given input iterator is internally wrapped to a
 * cursor} <i>peek</i>-element, i.e., the element returned by the iteration's
 * <tt>peek</tt> method. In order to indicate that the aggregation function has
 * not yet become initialized, <tt>null</tt> is returned. The following code
 * fragment shows this behaviour:
 * <pre>
 *     aggregate = function.invoke(aggregate, input.peek());
 *     if (aggregate != null)
 *         initialized = true;
 *     return aggregate;
 * </pre>
 * If the aggregate has been initialized, the further computation is
 * demand-driven, so a call to the <tt>next</tt> method will set the aggregate as
 * follows:
 * <pre>
 *     aggregate = function.invoke(aggregate, input.next());
 * </pre>
 * This incremental computation with the help of a binary aggregation function
 * implies that the absolute aggregate's value is first being determined when the
 * last element of the underlying iteration has been consumed and the aggregation
 * function has been applied. If the user is not interested in the incremental
 * computation of the aggregate during the demand-driven computation, the final
 * aggregation value can be delivered directly calling the method <tt>last</tt>.
 * 
 * <p>Futhermore the aggregator offers the possibility to define more than one
 * binary aggregation function, i.e., the user is able to compute a sum and an
 * average of the same data set in only one iteration process. This kind of usage
 * is often needed for SQL queries on relations. For further information
 * concerning the usage of multi-aggregate functions see
 * {@link xxl.core.relational.cursors.Aggregator}. Another very impressive use of
 * this class is given by the
 * {@link xxl.core.cursors.mappers.ReservoirSampler reservoir-sampler} which
 * uses a reservoir sampling function with a given strategy for <tt>on-line
 * sampling</tt>.</p>
 *
 * <p><b>Note:</b> When the given input iteration only implements the interface
 * {@link java.util.Iterator} it is wrapped to a cursor by a call to the static
 * method {@link xxl.core.cursors.Cursors#wrap(Iterator) wrap}.</p>
 * 
 * <p><b>Example usage:</b>
 * <pre>
 *     Aggregator aggregator = new Aggregator(
 *         new RandomIntegers(100, 50),
 *         new Function() {
 *             public Object invoke(Object aggregate, Object next) {
 *                 if (aggregate == null)
 *                     return next;
 *                 return aggregate = Math.max(aggregate, next);
 *             }
 *         }
 *     );
 * 
 *     aggregator.open();
 * 
 *     System.out.print("The result of the maximum aggregation is: " + aggregator.last());
 * 
 *     aggregator.close();
 * </pre>
 * This example determines the maximum of 50 random numbers with the restriction
 * that the value of a random number is not greater than 99. A new function for
 * the aggregation is defined that compares the value of the current aggregate
 * with the value of the next object and returns the object with the maximum
 * value. Furthermore the first two lines of the <tt>invoke</tt> method show the
 * initialization of this instance of an aggregator. Because the aggregator works
 * demand-driven the absolute maximum is definitively detected if all elements
 * were consumed. Therefore the method <tt>last</tt> is used generating the final
 * output. At last the aggregator is closed with the intention to release
 * resources.</p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 * @see xxl.core.relational.cursors.Aggregator
 */
public class Aggregator extends AbstractCursor {

	/**
	 * The input iteration holding the data to be aggregated.
	 */
	protected Cursor input;
	
	/**
	 * The function used for the aggregation. This binary function is invoked
	 * with the prior aggregate and the next element of the input iteration.
	 * When the aggregate is not yet initialized, a <tt>null</tt> value is given
	 * to the aggregate function.
	 */
	protected Function function;

	/**
	 * The current aggregate of the processed input iteration.
	 */
	protected Object aggregate;

	/**
	 * A boolean flag to detect if an result-object for the aggregation has been
	 * specified, i.e., the aggregate is already initialized.
	 */
	protected boolean initialized;
	
	/**
	 * Creates a new aggregator backed on an input iteration. Every iterator
	 * given to this constructor is wrapped to a cursor.
	 *
	 * @param iterator the input iteration holding the data to be aggragated.
	 * @param function the function used for the aggregation.
	 */
	public Aggregator(Iterator iterator, Function function) {
		this.input = Cursors.wrap(iterator);
		this.function = function;
		this.aggregate = null;
		this.initialized = false;
	}

	/**
	 * Creates a new aggregator backed on an input iteration. The given array of
	 * aggregation functions will be wrapped by a
	 * {@link xxl.core.math.Maths#multiDimAggregateFunction(xxl.core.functions.Function[]) function}
	 * calling successively the functions of the array. The wrapping aggregation
	 * function is initialized if and only if all functions of the array are
	 * initialized, meaning <tt>null</tt> will be returned by calling the
	 * <tt>next</tt> method as long as all functions of the array will return
	 * objects that are not <tt>null</tt>! After initialization an array
	 * containing the aggregation values given by the corresponding functions
	 * will be returned every time calling the <tt>next</tt> method! Every
	 * iterator given to this constructor is wrapped to a cursor.
	 *
	 * @param iterator the input iteration holding the data to be aggragated.
	 * @param functions an array of functions simultaneously used for the
	 *        aggregation.
	 */
	public Aggregator(Iterator iterator, final Function[] functions) {
		this(iterator, Maths.multiDimAggregateFunction(functions)) ;
	}

	/**
	 * Opens the aggregator, i.e., signals the cursor to reserve resources, open
	 * the input iteration, etc. Before a cursor has been opened calls to methods
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
		input.open();
	}
	
	/**
	 * Closes the aggregator, i.e., signals the cursor to clean up resources,
	 * close the input iterations, etc. When a cursor has been closed calls to
	 * methods like <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield
	 * proper results. Multiple calls to <tt>close</tt> do not have any effect,
	 * i.e., if <tt>close</tt> was called the cursor remains in the state
	 * <i>closed</i>.
	 * 
	 * <p>Note, that a closed cursor usually cannot be opened again because of
	 * the fact that its state generally cannot be restored when resources are
	 * released respectively files are closed.</p>
	 */
	public void close () {
		super.close();
		input.close();
	}
	
	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other words,
	 * returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would return an
	 * element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the aggregator has more elements, otherwise
	 *         <tt>false</tt>.
	 */
	protected boolean hasNextObject() {
		if (input.hasNext()) {
			aggregate = function.invoke(aggregate, input.next());
			if (!initialized && aggregate != null)
				initialized = true;
			return true;
		}
		return false;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the aggregator's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return aggregate;
	}

	/**
	 * Returns the last element of this aggregator. This element represents the
	 * final aggregation value.
	 *
	 * @return the last element of the aggregator.
	 * @throws NoSuchElementException if a last element does not exist, i.e., the
	 *         input iteration does not hold enough elements to initialize the
	 *         aggregate.
	 */
	public Object last() throws NoSuchElementException {
		try {
			return Cursors.last(this);
		}
		catch (NoSuchElementException nsee) {
			if (!initialized)
				throw nsee;
			return aggregate;
		}
	}

	/**
	 * Resets the aggregator to its initial state such that the caller is able to
	 * traverse the aggregation again without constructing a new aggregator
	 * (optional operation).
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the aggregator.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		input.reset();
		aggregate = null;
		initialized = false;
	}

	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the aggregator. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the aggregator, otherwise <tt>false</tt>.
	 */
	public boolean supportsReset() {
		return input.supportsReset();
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
		
		Aggregator aggregator = new Aggregator(
			new xxl.core.cursors.sources.RandomIntegers(100, 50), // the input cursor
			new Function() { // the aggregation function
				public Object invoke(Object aggregate, Object next) {
					if (aggregate == null)
						return next;
					return Maths.max(aggregate, next);
				}
			}
		);
		
		aggregator.open();
		
		System.out.print("The result of the maximum aggregation is: " + aggregator.last());
		
		aggregator.close();
	}
}
