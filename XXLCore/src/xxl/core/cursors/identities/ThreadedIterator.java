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

package xxl.core.cursors.identities;

import java.util.Iterator;
import java.util.NoSuchElementException;

import xxl.core.collections.queues.ListQueue;
import xxl.core.collections.queues.Queue;
import xxl.core.collections.queues.ThreadsafeQueue;
import xxl.core.util.concurrency.AsynchronousChannel;

/**
 * This class provides a wrapper for iterations to let them run in a new thread.
 * There are different motivations to use this class. If you have got a data
 * source that delivers data infrequently you can put the iteration into a
 * threaded iterator. The threaded iterator buffers the data such that the
 * waiting time for subsequent processing will be reduced.
 * 
 * <p><b>Note:</b> The class itself cannot be used by multiple threads
 * simultaneously!</p>
 * 
 * @see java.util.Iterator
 * @see java.lang.Thread
 */
public class ThreadedIterator implements Iterator {

	/**
	 * The iteration to be observed.
	 */
	protected Iterator iterator;

	/**
	 * A queue that is used for storing elements of this iterator.
	 */
	protected Queue tsQueue;

	/**
	 * A boolean flag determining whether the internally used thread has
	 * initilized or not.
	 */
	protected boolean initialized = false;

	/**
	 * An array of threads that is used to run the wrapped iterators.
	 */
	protected Thread[] threads;

	/**
	 * The number of threads that are sill running. This number indicates whether
	 * the threaded iterator will run out of elements soon.
	 */
	protected int numThreadsRunning;
	
	/**
	 * An asynchronous channel for communication between the threads and the
	 * threaded iterator. The name of the variable is chosen from the perspective
	 * of the threaded iterator.
	 */
	protected AsynchronousChannel waitForThreadChannel = new AsynchronousChannel();
	
	/**
	 * An asynchronous channel for communication between the threaded iterator
	 * and the threads. The name of the variable is chosen from the perspective
	 * of the threaded iterator.
	 */
	protected AsynchronousChannel signalThreadChannel = new AsynchronousChannel();

	/**
	 * Constructs a new thread iterator. The given iterators run in their own
	 * threads. The threads start working after the first demand (a call to
	 * <tt>hasNext</tt> or <tt>next</tt>).
	 *
	 * @param iterators an array of iterators that should run in single threads.
	 * @param queue a queue that stores the delivered objects of the given
	 *        iterators. In order to get a buffer of a fixed size use a
	 *        {@link xxl.core.collections.queues.BoundedQueue bounded queue}.
	 * @param threadPriority the priority of the threads.
	 */
	public ThreadedIterator(Iterator[] iterators, final Queue queue, int threadPriority) {
		// The queue always has to be threadsafe, because there are always at least two
		// threads: the producer and the consumer.
		tsQueue = new ThreadsafeQueue(queue);
		tsQueue.open();
		numThreadsRunning = 0;
		threads = new Thread[iterators.length];
		for (int i = 0; i < iterators.length; i++) {
			final Iterator iterator = iterators[i];
			threads[i] = new Thread() {
				
				Object[] object = new Object[1]; // Return value
				
				public void run() {
					Object next;
					while (iterator.hasNext()) {
						next = iterator.next();
						tsQueue.enqueue(next);
						// OLD:
						// it is important, that there must be an object
						// for the ThreadedIterator, because the insert statement
						// was inside this synchronized block. If not so,
						// the ThreadedIterator would eventually be able to 
						// consume the element and waits in the next iteration,
						// before the synchronized block is entered (this happens not very
						// often, but sometimes!).
						
						synchronized (ThreadedIterator.this) {
							if (signalThreadChannel.isFull()) {
								if (!tsQueue.isEmpty()) {
									// only awake the main thread iff there is an element inside the queue
									// (else the main thread would return hasNext()==false).
									waitForThreadChannel.put(null);
									signalThreadChannel.take();
								}
							}
						}
					}
					synchronized (ThreadedIterator.this) {
						numThreadsRunning--;
						if (numThreadsRunning == 0) {
							// Let the waiting master thread run
							if (signalThreadChannel.attemptTake(object))
								waitForThreadChannel.put(null);
						}
					}
				}
			};
			threads[i].setPriority(threadPriority);
		}
	}

	/**
	 * Constructs a new thread iterator wrapping a single iterator and using a
	 * list-queue for storing the elements of the wrapped iterator. The priority
	 * of the internally used thread is set to
	 * {@link java.lang.Thread#NORM_PRIORITY}.
	 *
	 * @param iterator the iterator that should run in a thread.
	 */
	public ThreadedIterator(Iterator iterator) {
		this(new Iterator[] {iterator}, new ListQueue(), Thread.NORM_PRIORITY);
	}

	/**
	 * Initializes the threaded iterator and starts the single threads.
	 */
	protected void init() {
		numThreadsRunning = threads.length;
		for (int i = 0; i < numThreadsRunning; i++)
			threads[i].start();
		initialized = true;
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the iteration has more elements.
	 */
	public boolean hasNext() {
		if (!initialized)
			init();
		if (!tsQueue.isEmpty())
			return true;
		else {
			// Initialize new communication with the thread.
			// Examine the status of the thread.
			boolean waitForThread=false;
			synchronized (this) {
				if (tsQueue.isEmpty() && numThreadsRunning > 0) {
					// Send threads, that one should wake me up if
					// - the one thread is the last thread or
					// - the one thread has inserted an element into the queue 
					//   (at least one element is inside the queue).
					signalThreadChannel.put(null);
					waitForThread = true;
				}
			}
			if (waitForThread)
				waitForThreadChannel.take();
			
			// Meanwhile, one thread has either inserted an element into the 
			// tsQueue or all threads have terminated.
			return !tsQueue.isEmpty();
		}
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by the iteration's <tt>remove</tt> method, until a call to
	 * <tt>next</tt> occurs. This is calling <tt>next</tt> proceeds the iteration
	 * and therefore its previous element will not be accessible any more.
	 *
	 * @return the next element in the iteration.
	 * @throws NoSuchElementException if the iteration has no more elements.
	 */
	public Object next() throws NoSuchElementException {
		if (this.hasNext())
			return tsQueue.dequeue();
		else
			throw new NoSuchElementException("no element available in the ThreadedIterator");
	}

	/**
	 * Removes from the underlying data structure the last element returned by
	 * the iteration (optional operation). This method can be called only once
	 * per call to <tt>next</tt> and removes the element returned by this method.
	 * Note, that between a call to <tt>next</tt> and <tt>remove</tt> the
	 * invocation of <tt>hasNext</tt> is forbidden. The behaviour of an iteration
	 * is unspecified if the underlying data structure is modified while the
	 * iteration is in progress in any way other than by calling this method.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is
	 *         not supported by the iteration.
	 */
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("ThreadedIterator does not support removal in general");
	}

	/**
	 * This method consumes the given cursor and checks whether it contains
	 * the specified number of elements. If not so an exception is thrown.
	 * 
	 * @param it the iterator to be consumed.
	 * @param numberOfElementsExpected the expected number of the given
	 *        iterator's elements.
	 */
	private static void consumeCursor(Iterator it, int numberOfElementsExpected) {
		int count = 0;
		while (it.hasNext()) {
			System.out.print(it.next()+" ");
			count++;
		}
		
		if (count != numberOfElementsExpected) {
			// for main maker
			System.out.println("Number of elements: " + count);
			System.out.println("Number of elements (expected): " + numberOfElementsExpected);
			throw new RuntimeException("Error in ThreadedIterator (1)!!!");
		}
			
		// Print an exception!
		try {
			it.next();
			// for main maker
			throw new RuntimeException("Error in ThreadedIterator (2)!!!");
		}
		catch (Exception e) {
			System.out.println("Everything ok!");
		}
	}

	/**
	 * This method consumes the given cursor and checks whether it contains
	 * the specified number of elements. If not so an exception is thrown.
	 * Additionally an output will be generated for the iterator's elements.
	 * 
	 * @param it the iterator to be consumed.
	 * @param numberOfElementsExpected the expected number of the given
	 *        iterator's elements.
	 */
	private static void consumeCursorWithoutOutput(Iterator it, int numberOfElementsExpected) {
		long t1 = System.currentTimeMillis();
		int count = 0;
		while (it.hasNext()) {
			it.next();
			count++;
			try { it.wait(10); } catch (Exception e) {}
		}
		long t2 = System.currentTimeMillis();
		
		System.out.println("Time for consuming cursor: " + (t2-t1));
		
		if (count != numberOfElementsExpected) {
			// for main maker
			System.out.println("Number of elements: " + count);
			System.out.println("Number of elements (expected): " + numberOfElementsExpected);
			if (it instanceof ThreadedIterator)
				System.out.println("Number of threads running: " + ((ThreadedIterator) it).numThreadsRunning);
			throw new RuntimeException("Error in ThreadedIterator (1)!!!");
		}
		
		// Print an exception!
		try {
			it.next();
			// for main maker
			throw new RuntimeException("Error in ThreadedIterator (2)!!!");
		}
		catch (Exception e) {
			System.out.println("Everything ok!");
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
		/*                            Example                                */
		/*********************************************************************/
		
		int numbers=100;
		int numbers2=1000;
		int numbers3=100000;
		int queueSize=10;
		
		ThreadedIterator it;
		
		System.out.println("Example for the ThreadedIterator");
		
		System.out.println();
		System.out.println("Usage with one Enumerator (" + numbers3 + " elements)");

		System.out.println("Performance test: without threading");
		consumeCursorWithoutOutput(
			new xxl.core.cursors.sources.Enumerator(numbers3),
			numbers3
		);
		
		System.out.println("Performance test: with ThreadedIterator");
		it = new ThreadedIterator(new xxl.core.cursors.sources.Enumerator(numbers3));
		consumeCursorWithoutOutput(
			it,
			numbers3
		);
		
		System.out.println();
		System.out.println("Usage with some Enumerators and a RandomIntegers cursor");
		
		it = new ThreadedIterator(
			new Iterator[] {
				new xxl.core.cursors.sources.Enumerator(numbers+20),
				new xxl.core.cursors.sources.Enumerator(numbers),
				new xxl.core.cursors.sources.RandomIntegers(numbers+10),
				new xxl.core.cursors.sources.Enumerator(0),
				new xxl.core.cursors.sources.Enumerator(1)
			},
			new xxl.core.collections.queues.BoundedQueue(
				new ListQueue(),
				queueSize
				),
			Thread.MIN_PRIORITY
		);
		consumeCursor(
			it,
			3*numbers+31
		);
		
		System.out.println();
		System.out.println("Usage with fast consumer without output");
		
		it = new ThreadedIterator(
			new Iterator[] {
				new DelayCursor(
					new xxl.core.cursors.sources.Enumerator(2*numbers2),
					new xxl.core.cursors.sources.RandomIntegers(2,-1),
					true
				),
				new xxl.core.cursors.sources.Enumerator(numbers2),
				new DelayCursor(
					new xxl.core.cursors.sources.RandomIntegers(numbers2),
					3,
					true
				),
			},
			new xxl.core.collections.queues.BoundedQueue(
				new ListQueue(),
				queueSize
			),
			Thread.MIN_PRIORITY
		);
		consumeCursorWithoutOutput(
			it,
			4*numbers2
		);

		System.out.println();
		System.out.println("Usage with fast consumer without output and delays, with different priorities");
		
		for (int priority=Thread.MIN_PRIORITY; priority <= Thread.MAX_PRIORITY; priority++) {
			System.out.println("Priority: " + priority);
			it = new ThreadedIterator(
				new Iterator[] {
					new xxl.core.cursors.sources.Enumerator(2*numbers2),
					new xxl.core.cursors.sources.Enumerator(numbers2),
					new xxl.core.cursors.sources.RandomIntegers(3*numbers2),
					new xxl.core.cursors.sources.Enumerator(numbers2),
					new xxl.core.cursors.sources.RandomIntegers(numbers2)
				},
				new xxl.core.collections.queues.BoundedQueue(
					new ListQueue(),
					queueSize
				),
				priority
			);
			consumeCursorWithoutOutput(
				it,
				8*numbers2
			);
		}
		
		System.out.println();
		System.out.println("Usage with delayed cursors");

		it = new ThreadedIterator(
			new Iterator[] {
				new DelayCursor(
					new xxl.core.cursors.sources.Enumerator(2*numbers),
					new xxl.core.cursors.sources.RandomIntegers(100,-1),
					true
				),
				new DelayCursor(
					new xxl.core.cursors.sources.Enumerator(numbers),
					10,
					true
				),
				new DelayCursor(
					new xxl.core.cursors.sources.RandomIntegers(numbers),
					200,
					true
				),
				new xxl.core.cursors.sources.Enumerator(0),
				new xxl.core.cursors.sources.Enumerator(1)
			},
			new xxl.core.collections.queues.BoundedQueue(
				new ListQueue(),
				queueSize
			),
			Thread.MIN_PRIORITY
		);
		consumeCursor(
			it,
			4*numbers+1
		);

		System.out.println();
		System.out.println("ThreadedIterator finished successfully.");
	}
}
