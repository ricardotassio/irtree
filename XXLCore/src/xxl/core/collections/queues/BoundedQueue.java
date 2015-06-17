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

package xxl.core.collections.queues;

import java.util.Iterator;

import xxl.core.functions.Function;
import xxl.core.predicates.Predicate;

/**
 * Decorates an arbitrary queue by adding boundary conditions. <br>
 * These conditions are represented as a user-defined, unary predicate
 * which is applied to next element to be inserted into the underlying queue.
 * If an insert operation detects an overflow, i.e., the predicate returns
 * <tt>false</tt>, the method <code>handleOverflow()</code> is called.
 * The default implementation
 * throws an {@link java.lang.IndexOutOfBoundsException IndexOutOfBoundsException},
 * but overwriting this method offers the possibility to handle an overflow of
 * the underlying queue differently.
 * If the predicate returns <tt>false</tt>, the element will be inserted
 * into the underlying queue. <br>
 *
 * For the construction of a bounded buffer, this class has
 * proven to be useful.
 * <p>
 * This class is used in ThreadedIterator.
 * <p>
 * 
 * Example usage (1).
 * <pre>
 *     // create a new bounded queue using an ArrayQueue
 * 
 *     Queue queue = new BoundedQueue(new ArrayQueue(), 100);
 *
 *     // open the queue
 *
 *     queue.open();
 *
 *     try {
 *         Iterator iterator = new xxl.core.cursors.sources.Enumerator(200);
 *         for (; iterator.hasNext(); queue.enqueue(iterator.next()));
 *     }
 *     catch (IndexOutOfBoundsException e) {}
 *
 *     int count = queue.size();
 *     System.out.println("The queue contained "+count+
 *                        " elements (100 is correct!)");
 *
 *      // close the queue
 *
 *      queue.close();
 *</pre>
 *
 * @see xxl.core.collections.queues.Queue
 * @see xxl.core.collections.queues.DecoratorQueue
 */
public class BoundedQueue extends DecoratorQueue {

	/**
	 * A factory method to create a new bounded queue (see contract for
	 * {@link Queue#FACTORY_METHOD FACTORY_METHOD} in interface
	 * Queue). It may be invoked with an array (<i>parameter list</i>)
	 * (for further details see Function) of object arrays, an iterator or
	 * without any parameters. An array (<i>parameter list</i>) of object
	 * arrays will be used to initialize the internally used array with
	 * the object array at index 0 and an iterator will be used to insert
	 * the contained elements into the new ArrayQueue.
	 * 
	 * @see Function
	 */
	public static final Function FACTORY_METHOD = new Function() {
		public Object invoke() {
			return new BoundedQueue(
				(Queue)Queue.FACTORY_METHOD.invoke(),
				1024
			);
		}
		
		public Object invoke(Object iterator) {
			Queue queue = new BoundedQueue(
				(Queue)Queue.FACTORY_METHOD.invoke(),
				1024
			);
			for (Iterator i = (Iterator)iterator; i.hasNext(); queue.enqueue(i.next()));
			return queue;
		}
		
		public Object invoke(Object[] array) {
			return new BoundedQueue(
				(Queue)array[0],
				(Predicate)array[1]
			);
		}
	};
	
	/**
	 * Predicate that evaluates if a further element can be inserted.
	 * Unary predicate that gets the next element to be inserted
	 * as parameter. If <tt>false</tt> is returned, an overflow
	 * must be handled, so the method <code>handleOverflow()</code>
	 * is called. Otherwise (<tt>true</tt> is returned), the element
	 * specified as argument will be inserted into the queue.
	 */
	protected Predicate predicate;

	/**
	 * Constructs a BoundedQueue. The queue to become bounded must be passed
	 * as the first argument. The second argument is a predicate evaluating
	 * the next element and determining if an overflow must be handled.
	 *
	 * @param queue the queue that becomes bounded.
	 * @param predicate the predicate evaluating the next element and
	 * 		determining if an overflow must be handled, i.e. handleOverflow()
	 * 		will be called, if the predicate returns <tt>false</tt>.
	 */
	public BoundedQueue(Queue queue, Predicate predicate) {
		super(queue);
		this.predicate = predicate;
	}

	/**
	 * Constructs a BoundedQueue. The queue to become bounded must be passed as
	 * the first argument. The second argument defines the size of the queue. 
	 * 
	 * @param queue the queue that becomes bounded. 
	 * @param maxSize the size of the queue. If more elements become
	 *			inserted method handleOverflow() is called.
	 */
	public BoundedQueue(final Queue queue, final int maxSize) {
		this(
			queue,
			new Predicate() {
				public boolean invoke(Object object) {
					return queue.size() < maxSize;
				}
			}
		);
	}

	/**
	 * Appends the specified element to the <i>end</i> of this queue. The
	 * <i>end</i> of the queue is given by its <i>strategy</i>.
	 *
	 * @param object element to be appended to the <i>end</i> of this
	 *        queue.
	 * @throws IllegalStateException if the queue is already closed when this
	 *         method is called.
	 * @throws IndexOutOfBoundsException if the overflow cannot be handled.
	 */
	public void enqueue(Object object) throws IllegalStateException, IndexOutOfBoundsException {
		if (predicate.invoke(object))
			super.enqueue(object);
		else
			handleOverflow();
	}

	/**
	 * If an insert operation detects an overflow, i.e., the maximum number
	 * of objects has been inserted into the queue, and a further element
	 * is to be inserted this method is called. The default implementation
	 * throws an {@link java.lang.IndexOutOfBoundsException IndexOutOfBoundsException},
	 * but overwriting this method offers the possibility to handle an overflow in 
	 * the queue differently.
	 * 
	 * @throws IndexOutOfBoundsException if the overflow cannot be handled.
	 */
	public void handleOverflow() throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("The maximum number of elements has been reached. "+
											"No further elements can be inserted.");
	}

	/**
	 * The main method contains an example to demonstrate the usage
	 * of the BoundedQueue. An emumerator produces a sequence of
	 * Integers that are inserted into the bounded queue. When the queue
	 * is full, an exception is thrown, which is caught by this main
	 * method. Then, all elements are consumed and counted.
	 * 
	 * @param args the arguments for the <tt>main</tt> method.
	 */
	public static void main(String[] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////
		System.out.println("Example for the BoundedQueue");
		// create a new bounded queue using an ArrayQueue
		Queue queue = new BoundedQueue(new xxl.core.collections.queues.ArrayQueue(), 100);
		
		// open the queue
		queue.open();

		try {
			Iterator iterator = new xxl.core.cursors.sources.Enumerator(200);
			for (; iterator.hasNext(); queue.enqueue(iterator.next()));
		}
		catch (IndexOutOfBoundsException e) {}

		int count = queue.size();
		System.out.println("The queue contained "+count+" elements (100 is correct!)");

		if (count != 100) {
			// for main maker
			throw new RuntimeException("Error in BoundedQueue!!!");
		}
		
		// close the queue
		queue.close();
	}
}
