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
import java.util.NoSuchElementException;

import xxl.core.cursors.Cursors;
import xxl.core.functions.Function;

/**
 * The interface queue represents an user specified iteration over a
 * collection of elements (also known as a <i>sequence</i>) with a
 * <tt>peek</tt> method. This interface <b>does not</b> predefine any
 * <i>strategy</i> for addition and removal of elements so that the user
 * has full control concerning his <i>strategy</i> (e.g., FIFO (<i>first
 * in, first out</i>), LIFO (<i>last in, first out</i>) etc.).<p>
 * 
 * In contrast to sets, queues typically allow duplicate elements.
 * More formally, queues typically allow pairs of elements <tt>e1</tt> and
 * <tt>e2</tt> such that <tt>e1.equals(e2)</tt>, and they typically allow
 * multiple null elements if they allow null elements at all.<p>
 * 
 * It is important to see that the <tt>peek</tt> method only shows
 * the element which currently is the next element. The next call
 * of <tt>dequeue</tt> does not have to return the peeked element.
 * This is a difference to the semantic of <tt>peek</tt> inside the
 * Cursor interface.
 */
public interface Queue {

	/**
	 * A factory method to create a default queue.
	 * Each concrete implementation of a queue except for ArrayQueue
	 * should have a function FACTORY_METHOD
	 * that implements three variants of <tt>invoke</tt><br>
	 * <ul>
	 * <dl>
	 * <dt><li><tt>Object invoke()</tt>:</dt>
	 * <dd>returns <tt>new Queue()</tt>.</dd>
	 * <dt><li><tt>Object invoke(Object iterator)</tt>:</dt>
	 * <dd>returns <tt>new Queue(iterator)</tt>.</dd>
	 * <dt><li><tt>Object invoke(Object[] internalDataStructure)</tt>:</dt>
	 * <dd>returns <tt>new Queue((&lt;<i>InternalDataStructure&gt;</i>)internalDataStructure[0])</tt>.</dd>
	 * </dl>
	 * </ul>
	 * 
	 * This factory method creates a new ArrayQueue.
	 * It may be invoked with an array (<i>parameter list</i>)
	 * (for further details see {@link Function}) of object arrays, an iterator or
	 * without any parameters. An array (<i>parameter list</i>) of object
	 * arrays will be used to initialize the internally used array with
	 * the object array at index 0 and an iterator will be used to insert
	 * the contained elements into the new ArrayQueue.
	 * 
	 * @see Function
	 */
	public static final Function FACTORY_METHOD = new Function() {
		public Object invoke() {
			return new ArrayQueue();
		}
		
		public Object invoke(Object iterator) {
			return new ArrayQueue(
				Cursors.toArray((Iterator)iterator)
			);
		}
		
		public Object invoke(Object[] array) {
			return new ArrayQueue((Object[])array[0]);
		}
	};
	
	/**
	 * Opens the queue, i.e., signals the queue to reserve resources, open
	 * files, etc. Before a queue has been opened calls to methods like 
	 * <tt>peek</tt> are not guaranteed to yield proper results.
	 * Therefore <tt>open</tt> must be called before a queue's data
	 * can be processed. Multiple calls to <tt>open</tt> do not have any effect,
	 * i.e., if <tt>open</tt> was called the queue remains in the state
	 * <i>opened</i> until its <tt>close</tt> method is called.
	 * 
	 * <p>Note, that a call to the <tt>open</tt> method of a closed queue
	 * usually does not open it again because of the fact that its state
	 * generally cannot be restored when resources are released respectively
	 * files are closed.</p>
	 */
	public abstract void open();

	/**
	 * Closes this queue and releases any system resources associated with
	 * it. This operation is idempotent, i.e., multiple calls of this
	 * method take the same effect as a single call.<br>
	 * <b>Note:</b> This method is very important for queues using
	 * external resources like files or JDBC resources.
	 */
	public abstract void close();

	/**
	 * Appends the specified element to the <i>end</i> of this queue. The
	 * <i>end</i> of the queue is given by its <i>strategy</i>.
	 * 
	 * @param object element to be appended at the <i>end</i> of this
	 *        queue.
	 * @throws IllegalStateException if the queue is already closed when this
	 *         method is called.
	 */
	public abstract void enqueue(Object object) throws IllegalStateException;
	
	/**
	 * Returns the element which currently is the <i>next</i> element in 
	 * the queue. The element is <i>not</i> removed from the queue
	 * (in contrast to the method <tt>dequeue</tt>).
	 * The <i>next</i> element of the queue is given by its
	 * <i>strategy</i>. The next call to <tt>dequeue</tt> does not have to
	 * return the element which was returned by <tt>peek</tt>.
	 * 
	 * @return the <i>next</i> element in the queue.
	 * @throws IllegalStateException if the queue is already closed when this
	 *         method is called.
	 * @throws NoSuchElementException queue has no more elements.
	 */
	public abstract Object peek() throws IllegalStateException, NoSuchElementException;
	
	/**
	 * Returns the <i>next</i> element in the queue and <i>removes</i> it. 
	 * The <i>next</i> element of the queue is given by its <i>strategy</i>.
	 * 
	 * @return the <i>next</i> element in the queue.
	 * @throws IllegalStateException if the queue is already closed when this
	 *         method is called.
	 * @throws NoSuchElementException queue has no more elements.
	 */
	public abstract Object dequeue() throws IllegalStateException, NoSuchElementException;
	
	/**
	 * Returns <tt>false</tt> if the queue has more elements (in other
	 * words, returns <tt>false</tt> if <tt>next</tt> would return an
	 * element rather than throwing an exception).
	 * 
	 * @return <tt>false</tt> if the queue has more elements.
	 */
	public abstract boolean isEmpty();
	
	/**
	 * Returns the number of elements in this queue. If this queue
	 * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * 
	 * @return the number of elements in this queue.
	 */
	public abstract int size();
	
	/**
	 * Removes all of the elements from this queue. The queue will be
	 * empty after this call returns so that <tt>size() == 0</tt>.<br>
	 * Note that the elements will only be removed from the queue but not
	 * from the underlying collection.
	 */
	public abstract void clear();

}