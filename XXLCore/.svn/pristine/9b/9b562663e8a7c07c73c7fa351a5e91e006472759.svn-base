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

import java.util.Comparator;
import java.util.Iterator;

import xxl.core.comparators.ComparableComparator;
import xxl.core.cursors.Cursors;
import xxl.core.functions.Function;
import xxl.core.util.ArrayResizer;

/**
 * This class provides a dynamical resizable-array implementation of the
 * heap datastructure. In addition to the advantages of a heap this class
 * uses an ArrayResizer to automatically adjust the size of the internally 
 * used array to the size of the heap. This means that it is not
 * necessary to specify the maximum capacity of this heap in advance.<p>
 *
 * Usage example (1).
 * <pre>
 *     // create an array of objects to store in the heap
 *
 *     Object[] array = new Object[] {
 *          new Object[] {
 *              new Integer(1),
 *              new String("first")
 *          },
 *          new Object[] {
 *              new Integer(2),
 *              new String("first")
 *          },
 *          new Object[] {
 *              new Integer(1),
 *              new String("second")
 *          },
 *          new Object[] {
 *              new Integer(3),
 *              new String("first")
 *          },
 *          new Object[] {
 *              new Integer(1),
 *              new String("third")
 *          }
 *    };
 *
 *     // create a comparator that compares the objects by comparing
 *     // their Integers
 *
 *     Comparator comparator = new Comparator() {
 *          public int compare(Object o1, Object o2) {
 *             return ((Integer)((Object[])o1)[0]).intValue() - 
 *                    ((Integer)((Object[])o2)[0]).intValue();
 *          }
 *     };
 *
 *     // create a heap that is initialized with the array and that uses
 *     // the given comparator
 *
 *     DynamicHeap heap = new DynamicHeap(array, comparator);
 *
 *     // open the heap
 *     
 *     heap.open();
 *
 *     // insert two elements
 *
 *     heap.enqueue(new Object[] {
 *          new Integer(4),
 *          new String("first")
 *     });
 *     heap.enqueue(new Object[] {
 *          new Integer(1),
 *          new String("fourth")
 *     });
 *
 *     // print the elements of the heap
 *
 *     while (!heap.isEmpty()) {
 *           Object[] o = (Object[])heap.dequeue();
 *           System.out.println("Integer = "+o[0]+" & String = "+o[1]);
 *     }
 *     System.out.println();
 *
 *     // close the open heap after use
 *
 *     heap.close();
 * </pre>
 *
 * Usage example (2).
 * <pre>
 *     // refresh the array (it was internally used into the heap and has changed)
 *
 *     array = new Object[] {
 *         new Object[] {
 *             new Integer(1),
 *             new String("first")
 *         },
 *         new Object[] {
 *             new Integer(2),
 *             new String("first")
 *         },
 *         new Object[] {
 *             new Integer(1),
 *             new String("second")
 *         },
 *         new Object[] {
 *             new Integer(3),
 *             new String("first")
 *         },
 *         new Object[] {
 *             new Integer(1),
 *             new String("third")
 *         }
 *     };
 *
 *     // create an empty heap that uses the given comparator
 *
 *     heap = new DynamicHeap(comparator);
 *
 *     // open the heap
 *
 *     heap.open();
 *
 *     // generate an iteration over the elements of the given array
 *
 *      Iterator iterator = new ArrayCursor(array);
 * 
 *      // insert all elements of the given iterator
 * 
 *      for (; iterator.hasNext(); heap.enqueue(iterator.next()));
 *
 *      // print the elements of the heap
 *    
 *      while (!heap.isEmpty()) {
 *         Object[] o = (Object[])heap.dequeue();
 *         System.out.println("Integer = "+o[0]+" & String = "+o[1]);
 *      }
 *      System.out.println();
 *
 *      // close the open heap after use
 *
 *      heap.close();
 * </pre>
 *
 * @see xxl.core.collections.queues.Queue
 * @see xxl.core.collections.queues.Heap
 * @see xxl.core.util.ArrayResizer
 */
public class DynamicHeap extends Heap {

	/**
	 * A factory method to create a new DynamicHeap (see contract for
	 * {@link Queue#FACTORY_METHOD FACTORY_METHOD} in interface
	 * Queue). It may be invoked with an array (<i>parameter list</i>)
	 * (for further details see Function) of object arrays, an iterator or
	 * without any parameters. An array (<i>parameter list</i>) of object
	 * arrays will be used to initialize the internally used array with
	 * the object array at index 0 and an iterator will be used to insert
	 * the contained elements into the new DynamicHeap.
	 *
	 * @see Function
	 */
	public static final Function FACTORY_METHOD = new Function() {
		public Object invoke() {
			return new DynamicHeap();
		}

		public Object invoke(Object iterator) {
			return new DynamicHeap(
				Cursors.toArray((Iterator)iterator)
			);
		}

		public Object invoke(Object[] array) {
			return new DynamicHeap(
				(Object[])array[0]
			);
		}
	};

	/**
	 * An ArrayResizer managing the growth policy for internally used
	 * array. The ArrayResizer decides before an insertion or after an
	 * removal of an element whether the array is to be resized or not.
	 *
	 * @see ArrayResizer
	 */
	protected ArrayResizer resizer;

	/**
	 * Constructs a dynamic heap containing the elements of the specified
	 * array that returns them according to the order induced by the
	 * specified comparator. The heap is initialized by the call of
	 * <code>new Heap(array, size, comparator)</code> and the growth
	 * policy is managed by the specified ArrayResizer.
	 *
	 * @param array the object array that is used to store the heap and
	 *        initialize the internally used array.
	 * @param size the number of elements of the specified array which
	 *        should be used to initialize the heap.
	 * @param comparator the comparator to determine the order of the
	 *        heap.
	 * @param resizer an ArrayResizer managing the growth policy for the
	 *        internally used array.
	 */
	public DynamicHeap(Object[] array, int size, Comparator comparator, ArrayResizer resizer) {
		super(array, size, comparator);
		this.resizer = resizer;
	}

	/**
	 * Constructs a dynamic heap containing the elements of the specified
	 * array that returns them according to the order induced by the
	 * specified comparator. This constructor is equivalent to the call of
	 * <code>new DynamicHeap(array, array.length, comparator, resizer)</code>.
	 *
	 * @param array the object array that is used to store the heap and
	 *        initialize the internally used array.
	 * @param comparator the comparator to determine the order of the
	 *        heap.
	 * @param resizer an ArrayResizer managing the growth policy for the
	 *        internally used array.
	 */
	public DynamicHeap(Object[] array, Comparator comparator, ArrayResizer resizer) {
		this(array, array.length, comparator, resizer);
	}

	/**
	 * Constructs an empty dynamic heap and uses the specified comparator
	 * to order elements when inserted. This constructor is equivalent to
	 * the call of
	 * <code>new DynamicHeap(new Object[0], 0, comparator, resizer)</code>.
	 *
	 * @param comparator the comparator to determine the order of the
	 *        heap.
	 * @param resizer an ArrayResizer managing the growth policy for the
	 *        internally used array.
	 */
	public DynamicHeap(Comparator comparator, ArrayResizer resizer) {
		this(new Object[0], comparator, resizer);
	}

	/**
	 * Constructs a dynamic heap containing the elements of the specified
	 * array that returns them according to their <i>natural ordering</i>.
	 * This constructor is equivalent to the call of
	 * <code>new DynamicHeap(array, size, ComparableComparator.DEFAULT_INSTANCE, resizer)</code>.
	 *
	 * @param array the object array that is used to store the heap and
	 *        initialize the internally used array.
	 * @param size the number of elements of the specified array which
	 *        should be used to initialize the heap.
	 * @param resizer an ArrayResizer managing the growth policy for the
	 *        internally used array.
	 * @see ComparableComparator#DEFAULT_INSTANCE
	 */
	public DynamicHeap(Object[] array, int size, ArrayResizer resizer) {
		this(array, size, ComparableComparator.DEFAULT_INSTANCE, resizer);
	}

	/**
	 * Constructs a dynamic heap containing the elements of the specified
	 * array that returns them according to their <i>natural ordering</i>.
	 * This constructor is equivalent to the call of
	 * <code>new DynamicHeap(array, array.length, ComparableComparator.DEFAULT_INSTANCE, resizer)</code>.
	 *
	 * @param array the object array that is used to store the heap and
	 *        initialize the internally used array.
	 * @param resizer an ArrayResizer managing the growth policy for the
	 *        internally used array.
	 * @see ComparableComparator#DEFAULT_INSTANCE
	 */
	public DynamicHeap(Object[] array, ArrayResizer resizer) {
		this(array, array.length, resizer);
	}

	/**
	 * Constructs an empty dynamic heap and uses the
	 * <i>natural ordering</i> of the elements to order them when they are
	 * inserted. This constructor is equivalent to the call of
	 * <code>new DynamicHeap(new Object[0], 0, ComparableComparator.DEFAULT_INSTANCE, resizer)</code>.
	 *
	 * @param resizer an ArrayResizer managing the growth policy for the
	 *        internally used array.
	 * @see ComparableComparator#DEFAULT_INSTANCE
	 */
	public DynamicHeap(ArrayResizer resizer) {
		this(new Object[0], resizer);
	}

	/**
	 * Constructs a dynamic heap containing the elements of the specified
	 * array that returns them according to the order induced by the
	 * specified comparator. The growth policy is managed by
	 * <tt>ArrayResizer.DEFAULT_INSTANCE</tt>. This constructor is
	 * equivalent to the call of
	 * <code>new DynamicHeap(array, size, comparator, ArrayResizer.DEFAULT_INSTANCE)</code>.
	 *
	 * @param array the object array that is used to store the heap and
	 *        initialize the internally used array.
	 * @param size the number of elements of the specified array which
	 *        should be used to initialize the heap.
	 * @param comparator the comparator to determine the order of the
	 *        heap.
	 * @see ArrayResizer#DEFAULT_INSTANCE
	 */
	public DynamicHeap(Object[] array, int size, Comparator comparator) {
		this(array, size, comparator, ArrayResizer.DEFAULT_INSTANCE);
	}

	/**
	 * Constructs a dynamic heap containing the elements of the specified
	 * array that returns them according to the order induced by the
	 * specified comparator. The growth policy is managed by
	 * <tt>ArrayResizer.DEFAULT_INSTANCE</tt>. This constructor is
	 * equivalent to the call of
	 * <code>new DynamicHeap(array, array.length, comparator, ArrayResizer.DEFAULT_INSTANCE)</code>.
	 *
	 * @param array the object array that is used to store the heap and
	 *        initialize the internally used array.
	 * @param comparator the comparator to determine the order of the
	 *        heap.
	 * @see ArrayResizer#DEFAULT_INSTANCE
	 */
	public DynamicHeap(Object[] array, Comparator comparator) {
		this(array, array.length, comparator);
	}

	/**
	 * Constructs an empty dynamic heap and uses the specified comparator
	 * to order elements when inserted. The growth policy is managed by
	 * <tt>ArrayResizer.DEFAULT_INSTANCE</tt>. This constructor is
	 * equivalent to the call of
	 * <code>new DynamicHeap(new Object[0], 0, comparator, ArrayResizer.DEFAULT_INSTANCE)</code>.
	 *
	 * @param comparator the comparator to determine the order of the
	 *        heap.
	 * @see ArrayResizer#DEFAULT_INSTANCE
	 */
	public DynamicHeap(Comparator comparator) {
		this(new Object[0], comparator);
	}

	/**
	 * Constructs a dynamic heap containing the elements of the specified
	 * array that returns them according to their <i>natural ordering</i>.
	 * The growth policy is managed by
	 * <tt>ArrayResizer.DEFAULT_INSTANCE</tt>. This constructor is
	 * equivalent to the call of
	 * <code>new DynamicHeap(array, size, ComparableComparator.DEFAULT_INSTANCE, ArrayResizer.DEFAULT_INSTANCE)</code>.
	 *
	 * @param array the object array that is used to store the heap and
	 *        initialize the internally used array.
	 * @param size the number of elements of the specified array which
	 *        should be used to initialize the heap.
	 * @see ArrayResizer#DEFAULT_INSTANCE
	 * @see ComparableComparator#DEFAULT_INSTANCE
	 */
	public DynamicHeap(Object[] array, int size) {
		this(array, size, ComparableComparator.DEFAULT_INSTANCE);
	}

	/**
	 * Constructs a dynamic heap containing the elements of the specified
	 * array that returns them according to their <i>natural ordering</i>.
	 * The growth policy is managed by
	 * <tt>ArrayResizer.DEFAULT_INSTANCE</tt>. This constructor is
	 * equivalent to the call of
	 * <code>new DynamicHeap(array, array.length, ComparableComparator.DEFAULT_INSTANCE, ArrayResizer.DEFAULT_INSTANCE)</code>.
	 *
	 * @param array the object array that is used to store the heap and
	 *        initialize the internally used array.
	 * @see ArrayResizer#DEFAULT_INSTANCE
	 * @see ComparableComparator#DEFAULT_INSTANCE
	 */
	public DynamicHeap(Object[] array) {
		this(array, array.length);
	}

	/**
	 * Constructs an empty dynamic heap and uses the
	 * <i>natural ordering</i> of the elements to order them when they are
	 * inserted. The growth policy is managed by
	 * <tt>ArrayResizer.DEFAULT_INSTANCE</tt>. This constructor is
	 * equivalent to the call of
	 * <code>new DynamicHeap(new Object[0], 0, ComparableComparator.DEFAULT_INSTANCE, ArrayResizer.DEFAULT_INSTANCE)</code>.
	 * @see ArrayResizer#DEFAULT_INSTANCE
	 * @see ComparableComparator#DEFAULT_INSTANCE
	 */
	public DynamicHeap() {
		this(new Object[0]);
	}

	/**
	 * Removes all elements from this heap. The heap will be empty
	 * after this call returns so that <tt>size() == 0</tt>.
	 */
	public void clear() {
		array = resizer.resize(array, 0);
		super.clear();
	}

	/**
	 * Inserts the specified element into this heap and restores the
	 * structure of the heap if necessary.
	 *
	 * @param object element to be inserted into this heap.
	 */
	public void enqueueObject(Object object) {
		if (resizer != null)
			array = resizer.resize(array, size()+1);
		super.enqueueObject(object);
	}

	/**
	 * Returns the <i>next</i> element in the heap and <i>removes</i> it. 
	 * The <i>next</i> element of the heap is determined by the comparator.
	 *
	 * @return the <i>next</i> element in the heap.
	 */
	public Object dequeueObject() {
		Object minimum = super.dequeueObject();
		array = resizer.resize(array, size());
		return minimum;
	}


	/**
	 * The main method contains some examples how to use a DynamicHeap.
	 * It can also be used to test the functionality of a DynamicHeap.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create an array of objects to store in the heap
		Object[] array = new Object[] {
			new Object[] {
				new Integer(1),
				new String("first")
			},
			new Object[] {
				new Integer(2),
				new String("first")
			},
			new Object[] {
				new Integer(1),
				new String("second")
			},
			new Object[] {
				new Integer(3),
				new String("first")
			},
			new Object[] {
				new Integer(1),
				new String("third")
			}
		};
		// create a comparator that compares the objects by comparing
		// their Integers
		Comparator comparator = new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Integer)((Object[])o1)[0]).intValue() - ((Integer)((Object[])o2)[0]).intValue();
			}
		};
		// create a heap that is initialized with the array and that uses
		// the given comparator
		DynamicHeap heap = new DynamicHeap(array, comparator);
		// open the heap
		heap.open();
		// insert two elements
		heap.enqueue(new Object[] {
			new Integer(4),
			new String("first")
		});
		heap.enqueue(new Object[] {
			new Integer(1),
			new String("fourth")
		});
		// print the elements of the heap
		while (!heap.isEmpty()) {
			Object[] o = (Object[])heap.dequeue();
			System.out.println("Integer = "+o[0]+" & String = "+o[1]);
		}
		System.out.println();
		// close the open heap after use
		heap.close();

		//////////////////////////////////////////////////////////////////
		//                      Usage example (2).                      //
		//////////////////////////////////////////////////////////////////

		// refresh the array (it was internally used into the heap and has
		// changed
		array = new Object[] {
			new Object[] {
				new Integer(1),
				new String("first")
			},
			new Object[] {
				new Integer(2),
				new String("first")
			},
			new Object[] {
				new Integer(1),
				new String("second")
			},
			new Object[] {
				new Integer(3),
				new String("first")
			},
			new Object[] {
				new Integer(1),
				new String("third")
			}
		};
		// create an empty heap that uses the given comparator
		heap = new DynamicHeap(comparator);
		// open the heap
		heap.open();
		// generate an iteration over the elements of the given array
		Iterator iterator = new xxl.core.cursors.sources.ArrayCursor(array);
		// insert all elements of the given iterator
		for (; iterator.hasNext(); heap.enqueue(iterator.next()));
		// print the elements of the heap
		while (!heap.isEmpty()) {
			Object[] o = (Object[])heap.dequeue();
			System.out.println("Integer = "+o[0]+" & String = "+o[1]);
		}
		System.out.println();
		// close the open heap after use
		heap.close();
	}
}
