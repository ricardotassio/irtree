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

import java.util.NoSuchElementException;

/**
 * A ThreadsafeQueue can be used by different threads that
 * use this queue for communicating with each other.
 * If the queue is empty, the queue has to wait until
 * a different thread writes something into the queue.
 * This class can also be used as a bounded buffer!
 * <p>
 * This class is used by ThreadedIterator.</p>
 * <p>
 * Usage example (1).
 * <pre>
 *     // create a threadsafe array queue
 *
 *     Queue queue = new ThreadsafeQueue(new BoundedQueue(new ArrayQueue(),100));
 *
 *     // open the queue
 * 
 *     queue.open();
 *     
 *     // create an enumeration with 100 elements
 * 
 *     Cursor cursor = new Enumerator(100);
 *
 *     // insert all elements in the queue
 * 
 *     for (; cursor.hasNext(); queue.enqueue(cursor.next()));
 * 
 *     System.out.println("There were "+queue.size()+" elements in the queue");
 *
 *     // close the queue and the cursor
 * 
 *     queue.close();
 *     cursor.close();
 * </pre>
 * 
 * @see xxl.core.collections.queues.Queue
 * @see xxl.core.collections.queues.DecoratorQueue
 */
public class ThreadsafeQueue extends DecoratorQueue {

	/** Counts the number of failed calls to the underlying queue because the queue was full. */
	public int failedCallsFull = 0;
	/** Counts the number of failed calls to the underlying queue because the queue was empty. */
	public int failedCallsEmpty = 0;
	
	/** Counts the number of threads which want to insert into the queue. */
	protected int countWaitForInsert = 0;
	/** Counts the number of threads which want to get an object out of the queue. */
	protected int countWaitForNext   = 0;
	
	/** Internal state constant. */
	protected static final int EMPTY=0;
	/** Internal state constant. */
	protected static final int NORMAL=1;
	/** Internal state constant. */
	protected static final int FULL=2;

	/** Internal state of the queue. */
	protected int state = EMPTY;

	/**
	 * Constructs a threadsafe Queue.
	 *
	 * @param queue the queue that becomes threadsafe.
	 */
	public ThreadsafeQueue(Queue queue) {
		super(queue);
	}

	/**
	 * Appends the specified element to the <i>end</i> of this queue. The
	 * <i>end</i> of the queue is given by its <i>strategy</i>.
	 *
	 * @param object element to be appended to the <i>end</i> of this
	 *        queue.
	 */
	public synchronized void enqueue(Object object) {
		while (true) {
			if (state == FULL) {
				countWaitForInsert++;
				try {
					wait();
				}
				catch (java.lang.InterruptedException e) {
					e.printStackTrace();
				}
				countWaitForInsert--;
			}
			else {
				try {
					super.enqueue(object);
					// if (state == EMPTY)
					state=NORMAL;
					break;
				}
				catch (IndexOutOfBoundsException e) {
					failedCallsFull++;
					state = FULL;
				}
			}
		}
		if ((countWaitForNext > 0) || (countWaitForInsert > 0))
			notify();
	}

	/**
	 * Returns the <i>next</i> element in the queue and <i>removes</i> it. 
	 * The <i>next</i> element of the queue is given by its <i>strategy</i>.
	 *
	 * @return the <i>next</i> element in the queue.
	 */
	public synchronized Object dequeue() {
		Object object;
		while (true) {
			if (state == EMPTY) {
				countWaitForNext++;
				try  {
					wait();
				}
				catch (java.lang.InterruptedException e) {}
				countWaitForNext--;
			}
			else {
				try {
					if (queue.size() > 0) { // equivalent to if (hasNext()) {
						object = super.dequeue();
						//if (state == FULL)
						state = NORMAL;
						break;
					}
				}
				catch (NoSuchElementException e) {}
				// no element availlable here!
				failedCallsEmpty++;
				state = EMPTY;
			}
		}
		if ((countWaitForNext > 0) || (countWaitForInsert > 0))
			notify();

		return object;
	}

	/**
	 * The main method contains an examples to demonstrate the usage
	 * of the ThreadsafeQueue.
	 * 
	 * @param args the arguments for the <tt>main</tt> method.
	 */
	public static void main(String[] args){

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////
		System.out.println("Simple example for the ThreadsafeQueue");
		// create a threadsafe array queue
		Queue queue = new ThreadsafeQueue(new BoundedQueue(new ArrayQueue(),100));
		// open the queue
		queue.open();
		// create an enumeration with 100 elements
		xxl.core.cursors.Cursor cursor = new xxl.core.cursors.sources.Enumerator(100);
		// insert all elements in the queue
		for (; cursor.hasNext(); queue.enqueue(cursor.next()));
		System.out.println("There were "+queue.size()+" elements in the queue");
		
		// close the queue and the cursor
		queue.close();
		cursor.close();
	}
}
