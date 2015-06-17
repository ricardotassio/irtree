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

package xxl.core.cursors;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Observable;

import xxl.core.functions.Function;
import xxl.core.predicates.Predicate;

/**
 * This class provides a wrapper for {@link java.util.Iterator iterators} with
 * the intention to {@link java.util.Observer observe} them. The notification
 * can be controlled using a {@link xxl.core.predicates.Predicate predicate} that
 * decides whether the observers should be notified about the currently delivered
 * object by the wrapped iterator. Moreover a
 * {@link xxl.core.functions.Function function} could be used to map the
 * currently delivered object to informations the oberservers should get.
 * 
 * <p><b>Example usage (1):</b><br />
 * <br/>
 * <pre>
 *     Iterator iterator = new ObservableIterator(
 *         new RandomIntegers(10, 100),
 *         Functions.aggregateUnaryFunction(new CountAll())
 *     );
 *     Observer observer = new Observer() {
 *         public void update(Observable observable, Object object) {
 *             System.out.println("getting " + object + " from " + observable);
 *         }
 *     };
 *     ((Observable)iterator).addObserver(observer);
 *
 *     while (iterator.hasNext())
 *         System.out.println("next = " + iterator.next());
 * </pre>
 * This example illustrates how an iteration over random integers gets observed,
 * whereas the observer only sees the results of an unary aggregation function,
 * namely a count of all elements delivered by the wrapped cursor.
 *
 * @see java.util.Iterator
 * @see java.util.Observable
 * @see java.util.Observer
 */
public class ObservableIterator extends Observable implements Iterator {

	/**
	 * The iterator to observe.
	 */
	protected Iterator iterator;

	/**
	 * A predicate that determines whether the observers should be notified or
	 * not.
	 */
	protected Predicate predicate;

	/**
	 * A function that maps the object received by the wrapped
	 * iterator to the object handed out to the observers.
	 */
	protected Function function;

	/**
	 * The last object that has been delivered by this iterator.
	 */
	protected Object next;

	/**
	 * Constructs an observable iterator.
	 *
	 * @param iterator the iterator to observe.
	 * @param function function that maps the object received by the wrapped
	 *        iterator to the object handed out to the observers.
	 * @param predicate predicate that determines whether the observers should
	 *        be notified about the currently received object or not.
	 */
	public ObservableIterator(Iterator iterator, Function function, Predicate predicate) {
		this.iterator = iterator;
		this.predicate = predicate;
		this.function = function;
	}

	/**
	 * Constructs an observable iterator. By using this constructor every seen
	 * object of this iterator will be passed to the registerd observers meaning
	 * the constant TRUE will be used as predicate for determining if the
	 * observers should be notified.
	 *
	 * @param iterator the iterator to observe.
	 * @param function function that maps the object received by the wrapped
	 *        iterator to the object handed out to the observers.
	 */
	public ObservableIterator(Iterator iterator, Function function) {
		this(iterator, function, Predicate.TRUE);
	}

	/**
	 * Constructs an observable iterator. By using this constructor every object
	 * itself will be passed to the registered observers meaning the identity
	 * function will be used to map the seen objects.
	 *
	 * @param iterator the iterator to observe.
	 * @param predicate predicate that determines whether the observers should
	 *        be notified about the currently received object or not.
	 */
	public ObservableIterator(Iterator iterator, Predicate predicate) {
		this(iterator, Function.IDENTITY, predicate);
	}

	/**
	 * Constructs an observable iterator. Every seen object will be passed to the
	 * registered observers.
	 *
	 * @param iterator the iterator to observe.
	 */
	public ObservableIterator(Iterator iterator) {
		this(iterator, Function.IDENTITY, Predicate.TRUE);
	}

	/**
	 * Returns true if the wrapped iteration has more elements.
	 *
	 * @return true if the iterator has more elements
	 */
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/**
	 * By calling the method <tt>next</tt> the next object of the wrapped
	 * iterator will be returned. All registerd observers will be notified if
	 * and only if the given predicate returns <tt>true</tt> for this object.
	 * Furthermore all observers receive only a transformed object given by the
	 * transformation function.
	 *
	 * @throws NoSuchElementException if the wrapped iterator has no further
	 *         objects.
	 * @return the next object of the wrapped iterator.
	 */
	public Object next() throws NoSuchElementException {
		next = iterator.next();
		if (predicate.invoke(next)) {
			setChanged();
			notifyObservers(function.invoke(next));
		}
		return next;
	}

	/**
	 * By calling remove the last seen object of the wrapped iterator is removed
	 * (optional operation).<br />
	 * <b>Note:</b> The last delivered object will be removed, <b>not</b> the
	 * last object notified to the observers.
	 *
	 * @throws UnsupportedOperationException if remove is not supported by the
	 *         wrapped iterator.
	 */
	public void remove() throws UnsupportedOperationException {
		iterator.remove();
		if (predicate.invoke(next)) {
			setChanged();
			notifyObservers();
		}
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
		
		// counting the objects
		Iterator iterator = new ObservableIterator(
			new xxl.core.cursors.sources.RandomIntegers(10, 100),
			xxl.core.functions.Functions.aggregateUnaryFunction(
				new xxl.core.math.statistics.parametric.aggregates.CountAll()));
		java.util.Observer observer = new java.util.Observer() {
			public void update(Observable observable, Object object) {
				System.out.println("getting " + object + " from " + observable);
			}
		};
		((Observable)iterator).addObserver(observer);

		while (iterator.hasNext())
			System.out.println("next=" + iterator.next());
		System.out.println("---");
	}
}
