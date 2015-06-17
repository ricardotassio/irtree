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

import xxl.core.util.ArrayResizer;

/**
 * This class provides a resizable-array implementation of the Queue
 * interface with a FIFO (<i>first in, first out</i>) strategy. It
 * implements the <tt>peek</tt> method. 
 * In addition to implementing the Queue interface, this class provides methods
 * to directly access the array that is internally used to store the data.<p>
 * 
 * Each ArrayQueue instance has a capacity. The capacity is the size of
 * the array used to store the elements in the queue. It is always at
 * least as large as the queue size. When elements are inserted into an
 * ArrayQueue, its capacity may grow automatically. The details of the
 * growth policy are specified by the ArrayResizer.<p>
 * 
 * The array is used as a circular storage for the queue to avoid the
 * shift of the whole queue when the first element is removed. Therefore a
 * field is needed to store the index of the first element in the array.<p>
 * 
 * Example usage (1).
 * <pre>
 *     // create a new array
 * 
 *     Object[] array = new Object[] {
 *         new Integer(1),
 *         new Integer(2),
 *         new Integer(3),
 *         new Integer(4),
 *         new Integer(5)
 *     };
 * 
 *     // create a new array queue with the given array
 * 
 *     ArrayQueue queue = new ArrayQueue(array);
 * 		
 *     // open the queue
 *     
 *     queue.open();
 * 
 *     // print all elements of the queue
 * 
 *     while (!queue.isEmpty())
 *         System.out.println(queue.dequeue());
 *     
 *     System.out.println();
 *  
 *     // close the open queue after use
 * 
 *     queue.close();
 * </pre>
 * 
 * Example usage (2).
 * <pre>
 *     // create a new array queue with the first three elements of the given array
 * 
 *     queue = new ArrayQueue(3, array);
 * 
 *     // open the queue
 *     
 *     queue.open();
 * 
 *     // print all elements of the queue
 * 
 *     while (!queue.isEmpty())
 *         System.out.println(queue.dequeue());
 * 
 *     System.out.println();
 * 
 *     // close the open queue after use
 * 
 *      queue.close();
 * </pre>
 * 
 * Example usage (3).
 * <pre>
 *     // create a new iterator containing the elements of the given array
 * 
 *     Cursor cursor = new ArrayCursor(array);
 * 
 *     // create a new empty array queue
 * 
 *     queue = new ArrayQueue();
 *
 *     // open the queue
 *     
 *     queue.open();
 * 
 *     for (; cursor.hasNext(); queue.enqueue(cursor.next()));
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
 *     queue.close();
 *     cursor.close();
 * </pre>
 * 
 * Example usage (4).
 * <pre>
 *     // create a new empty queue with a growth policy managed by new ArrayResizer(0.5, 0.6, 0.4)
 * 
 *     queue = new ArrayQueue(0.5, 0.6, 0.4);
 * 
 *     // open the queue
 *     
 *     queue.open();
 * 
 *     // insert and remove 20 elements and print after each operation the size of the queue and
 *     // the internally used array
 * 
 *     System.out.println("queue.size()="+queue.size()+" & "
 *                        +"queue.array.length="+queue.array.length);
 *     for (int i = 1; i &lt; 21; i++) {
 *         queue.enqueue(new Integer(i));
 *         System.out.println("queue.size()="+queue.size()+" & "
 *                             +"queue.array.length="+queue.array.length);
 *     }
 *     while (!queue.isEmpty()) {
 *         queue.dequeue();
 *         System.out.println("queue.size()="+queue.size()+" & "
 *                            +"queue.array.length="+queue.array.length);
 *     }
 * 
 *     System.out.println();
 *     
 *      // close the open queue after use
 * 
 *      queue.close();
 * </pre>
 * 
 * @see xxl.core.collections.queues.Queue
 * @see xxl.core.collections.queues.AbstractQueue
 * @see xxl.core.collections.queues.FIFOQueue
 * @see xxl.core.util.ArrayResizer
 */
public class ArrayQueue extends AbstractQueue implements FIFOQueue {
		
	/**
	 * An ArrayResizer managing the growth policy for the internally used
	 * array. The ArrayResizer decides before an insertion or after an
	 * removal of an element whether the array has to be resized or not.
	 * 
	 * @see ArrayResizer
	 */
	protected ArrayResizer resizer;
	
	/**
	 * The array is internally used to store the elements of the queue.
	 * Before an insertion and after the removal of an element this array
	 * is automatically resized by an ArrayResizer.
	 */
	protected Object[] array;
	
	/**
	 * An <tt>int</tt> field storing the index of the first element of the
	 * queue in the array.
	 */
	protected int first = 0;

	/**
	 * Constructs a queue containing the elements of the specified array
	 * with a growth polity that depends on the specified <tt>double</tt>
	 * parameters. The field <tt>array</tt> is set to the specified array,
	 * the field <tt>size</tt> is set to the specified size and the field
	 * <tt>resizer</tt> (growth policy) is initialized by the constructor
	 * <code>ArrayResizer(fmin, fover, funder)</code>.
	 * 
	 * @param size the number of elements of the specified array which
	 *        should be used to initialize the internally used array.
	 * @param array the object array that is used to initialize the
	 *        internally used array.
	 * @param fmin a control parameter for the growth policy.
	 * @param fover a control parameter for the growth policy.
	 * @param funder a control parameter for the growth policy.
	 * @throws IllegalArgumentException if the specified size argument is
	 *         negative, or if it is greater than the length of the
	 *         specified array.
	 * @see ArrayResizer#ArrayResizer(double, double, double)
	 */
	public ArrayQueue(int size, Object[] array, double fmin, double fover, double funder) throws IllegalArgumentException {
		if (array.length < size || size < 0)
			throw new IllegalArgumentException();
		this.array = array;
		this.size = size;
		this.resizer = new ArrayResizer(fmin, fover, funder) {
			public void copy(Object source, Object target, int size) {
				copy(source, first, target, 0, size);
			}
		};
	}

	/**
	 * Constructs a queue containing the elements of the specified array
	 * with a growth polity that depends on the specified <tt>double</tt>
	 * parameters. This constructor is equivalent to the call of
	 * <code>ArrayQueue(array.length, array, fmin, fover, funder)</code>.
	 * 
	 * @param array the object array that is used to initialize the
	 *        internally used array.
	 * @param fmin a control parameter for the growth policy.
	 * @param fover a control parameter for the growth policy.
	 * @param funder a control parameter for the growth policy.
	 */
	public ArrayQueue(Object[] array, double fmin, double fover, double funder) {
		this(array.length, array, fmin, fover, funder);
	}

	/**
	 * Constructs an empty queue with a growth policy that depends on the
	 * specified <tt>double</tt> parameters. This constructor is equivalent
	 * to the call of
	 * <code>ArrayQueue(0, new Object[0], fmin, fover, funder)</code>.
	 * 
	 * @param fmin a control parameter for the growth policy.
	 * @param fover a control parameter for the growth policy.
	 * @param funder a control parameter for the growth policy.
	 */
	public ArrayQueue(double fmin, double fover, double funder) {
		this(new Object[0], fmin, fover, funder);
	}

	/**
	 * Constructs a queue containing the elements of the specified array
	 * with a growth policy that depends on the specified <tt>double</tt>
	 * parameters. The field <tt>array</tt> is set to the specified array,
	 * the field <tt>size</tt> is set to the specified size and the field
	 * <tt>resizer</tt> (growth policy) is initialized by the constructor
	 * <code>ArrayResizer(fmin, f)</code>.
	 * 
	 * @param size the number of elements of the specified array which
	 *        should be used to initialize the internally used array.
	 * @param array the object array that is used to initialize the
	 *        internally used array.
	 * @param fmin a control parameter for the growth policy.
	 * @param f a control parameter for the growth policy.
	 * @throws IllegalArgumentException if the specified size argument is
	 *         negative, or if it is greater than the length of the
	 *         specified array.
	 * @see ArrayResizer#ArrayResizer(double, double)
	 */
	public ArrayQueue(int size, Object[] array, double fmin, double f) throws IllegalArgumentException {
		if (array.length < size || size < 0)
			throw new IllegalArgumentException();
		this.array = array;
		this.size = size;
		this.resizer = new ArrayResizer(fmin, f) {
			public void copy(Object source, Object target, int size) {
				copy(source, first, target, 0, size);
			}
		};
	}

	/**
	 * Constructs a queue containing the elements of the specified array
	 * with a growth policy that depends on the specified <tt>double</tt>
	 * parameters. This constructor is equivalent to the call of
	 * <code>ArrayQueue(array.length, array, fmin, f)</code>.
	 * 
	 * @param array the object array that is used to initialize the
	 *        internally used array.
	 * @param fmin a control parameter for the growth policy.
	 * @param f a control parameter for the growth policy.
	 */
	public ArrayQueue(Object[] array, double fmin, double f) {
		this(array.length, array, fmin, f);
	}

	/**
	 * Constructs an empty queue with a growth policy that depends on the
	 * specified <tt>double</tt> parameters. This constructor is
	 * equivalent to the call of
	 * <code>ArrayQueue(0, new Object[0], fmin, f)</code>.
	 * 
	 * @param fmin a control parameter for the growth policy.
	 * @param f a control parameter for the growth policy.
	 */
	public ArrayQueue(double fmin, double f) {
		this(new Object[0], fmin, f);
	}

	/**
	 * Constructs a queue containing the elements of the specified array
	 * with a growth policy that depends on the specified <tt>double</tt>
	 * parameter. The field <tt>array</tt> is set to the specified array,
	 * the field <tt>size</tt> is set to the specified size and the field
	 * <tt>resizer</tt> (growth policy) is initialized by the constructor
	 * <code>ArrayResizer(fmin)</code>.
	 * 
	 * @param size the number of elements of the specified array which
	 *        should be used to initialize the internally used array.
	 * @param array the object array that is used to initialize the
	 *        internally used array.
	 * @param fmin a control parameter for the growth policy.
	 * @throws IllegalArgumentException if the specified size argument is
	 *         negative, or if it is greater than the length of the
	 *         specified array.
	 * @see ArrayResizer#ArrayResizer(double)
	 */
	public ArrayQueue(int size, Object[] array, double fmin) throws IllegalArgumentException {
		if (array.length < size || size < 0)
			throw new IllegalArgumentException();
		this.array = array;
		this.size = size;
		this.resizer = new ArrayResizer(fmin) {
			public void copy(Object source, Object target, int size) {
				copy(source, first, target, 0, size);
			}
		};
	}

	/**
	 * Constructs a queue containing the elements of the specified array
	 * with a growth policy that depends on the specified <tt>double</tt>
	 * parameter. This constructor is equivalent to the call of
	 * <code>ArrayQueue(array.length, array, fmin)</code>.
	 * 
	 * @param array the object array that is used to initialize the
	 *        internally used array.
	 * @param fmin a control parameter for the growth policy.
	 */
	public ArrayQueue(Object[] array, double fmin) {
		this(array.length, array, fmin);
	}

	/**
	 * Constructs an empty queue with a growth policy that depends on the
	 * specified <tt>double</tt> parameter. This constructor is equivalent
	 * to the call of <code>ArrayQueue(0, new Object[0], fmin)</code>.
	 * 
	 * @param fmin a control parameter for the growth policy.
	 */
	public ArrayQueue(double fmin) {
		this(new Object[0], fmin);
	}

	/**
	 * Constructs a queue containing the elements of the specified array.
	 * The field <tt>array</tt> is set to the specified array, the field
	 * <tt>size</tt> is set to the specified size and the field
	 * <tt>resizer</tt> (growth policy) is initialized by the void
	 * constructor <code>ArrayResizer()</code>.
	 * 
	 * @param size the number of elements of the specified array which
	 *        should be used to initialize the internally used array.
	 * @param array the object array that is used to initialize the
	 *        internally used array.
	 * @throws IllegalArgumentException if the specified size argument is
	 *         negative, or if it is greater than the length of the
	 *         specified array.
	 * @see ArrayResizer#ArrayResizer()
	 */
	public ArrayQueue(int size, Object[] array) throws IllegalArgumentException {
		if (array.length < size || size < 0)
			throw new IllegalArgumentException();
		this.array = array;
		this.size = size;
		this.resizer = new ArrayResizer() {
			public void copy(Object source, Object target, int size) {
				copy(source, first, target, 0, size);
			}
		};
	}

	/**
	 * Constructs a queue containing the elements of the specified array.
	 * This constructor is equivalent to the call of
	 * <code>ArrayQueue(array.length, array)</code>.
	 * 
	 * @param array the object array that is used to initialize the
	 *        internally used array.
	 */
	public ArrayQueue(Object[] array) {
		this(array.length, array);
	}

	/**
	 * Constructs an empty queue. This constructor is equivalent to the
	 * call of <code>ArrayQueue(0, new Object[0])</code>.
	 */
	public ArrayQueue() {
		this(new Object[0]);
	}

	/**
	 * Appends the specified element to the <i>end</i> of this queue.
	 * Before the element is inserted the internally used array may be
	 * resized automatically depending on its growth policy.
	 * 
	 * @param object element to be appended to the <i>end</i> of this
	 *        queue.
	 */
	public void enqueueObject(Object object) {
		if (array != (array = resizer.resize(array, size+1)))
			first = 0;
		array[(first+size)%array.length] = object;
	}

	/**
	 * Returns the <i>next</i> element in the queue <i>without</i> removing it.
	 * The <i>next</i> element is the value of the component of the
	 * internally used array which is pointed by the index <tt>first</tt>.
	 * 
	 * @return the <i>next</i> element in the queue.
	 */
	public Object peekObject() {
		return array[first];
	}

	/**
	 * Returns the <i>next</i> element in the queue (and <i>removes</i> it). 
	 * The <i>next</i> element is the value of the component of the internally 
	 * used array which is pointed by the index <tt>first</tt>.
	 * 
	 * @return the <i>next</i> element in the queue.
	 */
	public Object dequeueObject() {
		Object object = array[first];
		first = (first+1)%array.length;
		if (array != (array = resizer.resize(array, size-1)))
			first = 0;
		return object;
	}

	/**
	 * Removes all elements from this queue. The queue will be
	 * empty after this call returns so that <tt>size() == 0</tt>.<br>
	 * The internally used array will be replaced by an empty array and
	 * the fields <tt>size</tt> and <tt>first</tt> will be set to 0.
	 */
	public void clear () {
		array = new Object[0];
		size = 0;
		first = 0;
	}
	
	/**
	 * The main method contains some examples how to use an ArrayQueue.
	 * It can also be used to test the functionality of an ArrayQueue.
	 * 
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main(String[] args) {
		
		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////
		
		// create a new array
		Object[] array = new Object[] {
			new Integer(1),
			new Integer(2),
			new Integer(3),
			new Integer(4),
			new Integer(5)
		};
		// create a new array queue with the given array
		ArrayQueue queue = new ArrayQueue(array);	
		// open the queue
		queue.open();
		// print all elements of the queue
		while (!queue.isEmpty())
			System.out.println(queue.dequeue());
		System.out.println();
		// close the open queue after use
		queue.close();
		
		//////////////////////////////////////////////////////////////////
		//                      Usage example (2).                      //
		//////////////////////////////////////////////////////////////////
		
		// create a new array queue with the first three elements of the
		// given array
		queue = new ArrayQueue(3, array);
		// open the queue
		queue.open();
		// print all elements of the queue
		while (!queue.isEmpty())
			System.out.println(queue.dequeue());
		System.out.println();
		// close the open queue after use
		queue.close();
		
		//////////////////////////////////////////////////////////////////
		//                      Usage example (3).                      //
		//////////////////////////////////////////////////////////////////
		
		// create a new iterator containing the elements of the given
		// array
		xxl.core.cursors.Cursor cursor = new xxl.core.cursors.sources.ArrayCursor(array);
		// create a new empty array queue
		queue = new ArrayQueue();
		// open the queue
		queue.open();
		// insert all elements of the given iterator into the queue
		for (; cursor.hasNext(); queue.enqueue(cursor.next()));
		// print all elements of the queue
		while (!queue.isEmpty())
			System.out.println(queue.dequeue());
		System.out.println();
		// close the open queue and cursor after use
		queue.close();
		cursor.close();
		
		//////////////////////////////////////////////////////////////////
		//                      Usage example (4).                      //
		//////////////////////////////////////////////////////////////////
		
		// create a new empty queue with a growth policy managed by
		// new ArrayResizer(0.5, 0.6, 0.4)
		queue = new ArrayQueue(0.5, 0.6, 0.4);
		// open the queue
		queue.open();
		// insert and remove 20 elements and print after each operation
		// the size of the queue and the internally used array
		System.out.println("queue.size()="+queue.size()+" & "
			+"queue.array.length="+queue.array.length);
		for (int i = 1; i < 21; i++) {
			queue.enqueue(new Integer(i));
			System.out.println("queue.size()="+queue.size()+" & "
				+"queue.array.length="+queue.array.length);
		}
		while (!queue.isEmpty()) {
			queue.dequeue();
			System.out.println("queue.size()="+queue.size()+" & "
				+"queue.array.length="+queue.array.length);
		}
		System.out.println();
		// close the open queue after use
		queue.close();
	}
}