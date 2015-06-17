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

import xxl.core.functions.Function;

/**
 * This class provides a decorator for a given queue that cannot be
 * modified. The methods of this class call the corresponding methods of
 * the internally stored queue except the methods that modify the queue.
 * These methods (<tt>enqueue</tt> and <tt>clear</tt>) throw
 * an <tt>UnsupportedOperationException</tt>.<p>
 *
 * Usage example (1).
 * <pre>
 *     // create a new queue
 *
 *     ListQueue inputQueue = new ListQueue();
 *
 *     // open the queue
 *
 *     inputQueue.open();
 *
 *     // create an iteration over 20 random Integers (between 0 and 100)
 * 
 *     Cursor cursor = new RandomIntegers(100, 20);
 *
 *     // insert all elements of the given iterator
 *
 *     for (; cursor.hasNext(); inputQueue.enqueue(cursor.next()));
 * 
 *     // create a new unmodifiable queue with the given queue
 *
 *     UnmodifiableQueue queue = new UnmodifiableQueue(inputQueue);
 * 
 *     // open the queue
 *
 *     queue.open();
 * 
 *     // print all elements of the queue
 *
 *     while (!queue.isEmpty())
 *          System.out.println(queue.dequeue());
 *
 *     System.out.println();
 *
 *     // close the open queue and cursor after use
 * 
 *     inputQueue.close();
 *     queue.close();
 *     cursor.close();
 * </pre>
 *
 * @see xxl.core.collections.queues.Queue
 * @see xxl.core.collections.queues.DecoratorQueue
 */
public class UnmodifiableQueue extends DecoratorQueue {

	/**
	 * A factory method to create a new unmodifiable queue (see contract
	 * for {@link Queue#FACTORY_METHOD FACTORY_METHOD} in interface
	 * Queue). In contradiction to the contract in Queue it may only be
	 * invoked with an array (<i>parameter list</i>) (for further details
	 * see Function) of queues. The array (<i>parameter list</i>) will be
	 * used to initialize the decorated queue by calling the constructor
	 * <code>UnmodifiableQueue((Queue) array[0])</code>.
	 *
	 * @see Function
	 */
	public static final Function FACTORY_METHOD = new Function() {
		public Object invoke() {
			return new UnmodifiableQueue(
				(Queue)Queue.FACTORY_METHOD.invoke()
			);
		}
		
		public Object invoke(Object[] array) {
			return new UnmodifiableQueue(
				(Queue)array[0]
			);
		}
	};

	/**
	 * Constructs a new unmodifiable queue that decorates the specified
	 * queue.
	 *
	 * @param queue the queue to be decorated.
	 */
	public UnmodifiableQueue(Queue queue) {
		super(queue);
	}

	/**
	 * Appends the specified element to the <i>end</i> of this queue. The
	 * <i>end</i> of the queue is given by its <i>strategy</i>. This
	 * implementation always throws an
	 * <tt>UnsupportedOperationException</tt>.
	 *
	 * @param object element to be appended to the <i>end</i> of this
	 *        queue.
	 * @throws IllegalStateException if the queue is already closed when this
	 *         method is called.
	 * @throws UnsupportedOperationException when the method is not
	 *         supported.
	 */
	public void enqueue(Object object) throws IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes all of the elements from this queue. The queue will be
	 * empty after this call returns so that <tt>size() == 0</tt>. This
	 * implementation always throws an
	 * <tt>UnsupportedOperationException</tt>.
	 *
	 * @throws UnsupportedOperationException if the <tt>remove</tt>
	 *         operation is not supported by this queue.
	 */
	public void clear() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The main method contains some examples how to use an
	 * UnmodifiableQueue. It can also be used to test the functionality of
	 * an UnmodifiableQueue.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main(String[] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create a new queue
		ListQueue inputQueue = new ListQueue();
		// open the queue
		inputQueue.open();
		// create an iteration over 20 random Integers (between 0 and 100)
		xxl.core.cursors.Cursor cursor = new xxl.core.cursors.sources.RandomIntegers(100, 20);
		// insert all elements of the given iterator
		for (; cursor.hasNext(); inputQueue.enqueue(cursor.next()));
		// create a new unmodifiable queue with the given queue
		UnmodifiableQueue queue = new UnmodifiableQueue(inputQueue);
		// open the queue
		queue.open();
		// print all elements of the queue
		while (!queue.isEmpty())
			System.out.println(queue.dequeue());
		System.out.println();
		// close the open queues and cursor after use
		inputQueue.close();
		queue.close();
		cursor.close();
	}
}
