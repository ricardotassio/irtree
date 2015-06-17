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

package xxl.core.cursors.wrappers;

import java.util.ConcurrentModificationException;

import xxl.core.collections.queues.Queue;
import xxl.core.cursors.AbstractCursor;

/**
 * A queue-cursor wraps a {@link xxl.core.collections.queues.Queue queue} to a
 * cursor, i.e., the wrapped queue can be accessed via the
 * {@link xxl.core.cursors.Cursor cursor} interface. All method calls are passed
 * to the underlying queue. Therefore the methods <tt>remove</tt>,
 * <tt>update</tt> and <tt>reset</tt> throw an
 * {@link java.lang.UnsupportedOperationException}.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.AbstractCursor
 * @see xxl.core.collections.queues.Queue
 */
public class QueueCursor extends AbstractCursor {
	
	/**
	 * The internally used queue that is wrapped to a cursor.
	 */
	protected Queue queue;

	/**
	 * Creates a new queue-cursor.
	 *
	 * @param queue the queue to be wrapped to a cursor.
	 */
	public QueueCursor(Queue queue) {
		this.queue = queue;
	}

	/**
	 * Opens the queue-cursor, i.e., signals it to reserve resources, open the
	 * underlying queue, etc. Before a cursor has been opened calls to methods
	 * like <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield proper
	 * results. Therefore <tt>open</tt> must be called before a cursor's data
	 * can be processed. Multiple calls to <tt>open</tt> do not have any effect,
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
		queue.open();
	}

	/**
	 * Closes the queue-cursor, i.e., signals it to clean up resources, close the
	 * underlying queue, etc. When a cursor has been closed calls to methods like
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
		queue.close();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the queue-cursor has more elements.
	 */
	protected boolean hasNextObject() {
		return !queue.isEmpty();
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the queue-cursor's methods, e.g., <tt>update</tt>
	 * or <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more. <br>
	 * 
	 * @return the next element in the iteration.
	 * @throws ConcurrentModificationException If the element returned by <tt>peek</tt>
	 * 		would differ from that returned by a call to <tt>next</tt>.
	 */
	protected Object nextObject() {
		return queue.dequeue();
	}
	
	/**
	 * Returns the number of elements in this queue-cursor. If this queue-cursor
	 * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * 
	 * @return the number of elements in this queue-cursor.
	 */
	public int size() {
		return queue.size();
	}

}
