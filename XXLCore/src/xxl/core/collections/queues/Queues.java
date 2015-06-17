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

/**
 * This class contains various <tt>static</tt> methods for manipulating
 * queues. <br>
 */
public abstract class Queues {

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private Queues() {}

	/**
	 * Invokes <tt>queue.enqueue(Object o)</tt> on all objects <tt>o</tt> 
	 * contained in the iterator.
	 * @param queue the queue to insert the elements into.
	 * @param iterator iterator containing the elements to insert.
     */
	public static void enqueueAll(Queue queue, Iterator iterator) {
		for (; iterator.hasNext(); queue.enqueue(iterator.next()));
	}
	
	/**
	 * Invokes <tt>queue.enqueue(Object o)</tt> on all objects <tt>o</tt> 
	 * from the array <tt>objects</tt>.
	 * @param queue the queue to insert the elements into.
	 * @param objects array containing the elements to insert.
	 */
	public static void enqueueAll(Queue queue, Object[] objects) {
		for (int i = 0; i < objects.length; queue.enqueue(objects[i++]));
	}

	/**
	 * Converts a queue to an object array whose length is equal to
	 * the number of the queue's elements.
	 *
	 * @param queue the input queue.
	 * @return an object array containing the queue's elements.
	 */
	public static Object[] toArray(Queue queue) {
		return toArray(queue, new Object[queue.size()]);
	}

	/**
	 * Converts a queue to an object array.
	 * Throws an {@link java.lang.ArrayIndexOutOfBoundsException ArrayIndexOutOfBoundsException}
	 * if the number of the queue's elements is larger than the length of the given array.
	 *
	 * @param queue the input queue.
	 * @param array the array to be filled.
	 * @return the filled object array.
	*/
	public static Object[] toArray(Queue queue, Object[] array) {
		for (int i = 0; !queue.isEmpty() && i < array.length; array[i++] = queue.dequeue());
		return array;
	}

}
