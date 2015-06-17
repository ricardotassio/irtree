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

package xxl.core.io;

/**
 * This class provides a buffer with a LRU (<i>least recently used</i>)
 * displacement strategy. I.e. when searching an object to displace, the
 * object in the buffer that is the longest time unfixed is chosen.<p>
 *
 * This implementation uses a double linked list of the slots in addition
 * to the array of slots. The first slot in this list is the slot that is
 * unfixed at last and the last slot in the list is the slot that is
 * unfixed the longest time. When fixing a slot, it is removed out of the
 * list and it is appended in front of the list, when it is unfixed. So
 * the last slot in the list is always the least recently used slot of the
 * buffer. When the list is empty, all slots of the buffer are fixed.<p>
 *
 * Example usage (1).
 * <pre>
 *     // create a new owner
 *
 *     String owner = "owner";
 *
 *     // create a new LRU buffer with a capacity of 5 objects
 *
 *     LRUBuffer buffer = new LRUBuffer(5);
 *
 *     // create a new iterator with 100 integers between 0 and 5
 *
 *     Iterator iterator = new RandomIntegers(6, 100);
 *
 *     // insert all elements of the iterator with a flush function that prints the flushed
 *     // integer into the buffer
 *
 *     while (iterator.hasNext()) {
 *         Integer i = (Integer)iterator.next();
 *         System.out.println("insert "+i);
 *         buffer.update(owner, i, i, new Function() {
 *             public Object invoke (Object o1, Object o2) {
 *                 System.out.println("flush "+o1);
 *                 return o1;
 *             }
 *         }, true);
 *     }
 * </pre>
 */
public class LRUBuffer extends Buffer {

	/**
	 * The first slot of the double linked list of slots that represents
	 * the duration of being unfixed. The first slot of the list is the
	 * slot that is unfixed at last.
	 */
	protected Slot first = null;

	/**
	 * The last slot of the double linked list of slots that represents
	 * the duration of being unfixed. The last slot of the list is the
	 * slot that is unfixed the longest time.
	 */
	protected Slot last = null;

	/**
	 * This class provides a single slot in a LRU buffer. <p>
	 *
	 * Every slot is linked to its predecessor and successor in the double
	 * linked list representing the duration of being unfixed. When a slot
	 * is fixed, it is not contained in the list and its predecessor and
	 * successor are set to <tt>null</tt>. In addition to the usual
	 * methods of a slot, slots of LRU buffers support the removal out of
	 * the double linked list by the unlink method. When fixing a slot, it
	 * is removed out of the list and it is appended in front of the list,
	 * when it is unfixed.
	 */
	protected class Slot extends Buffer.Slot {

		/**
		 * The predecessor of this slot in the double linked list
		 * representing the duration of being unfixed. When this slot is
		 * not contained by the list or the first element of it, the
		 * predecessor is set to <tt>null</tt>.
		 */
		protected Slot prev = this;

		/**
		 * The successor of this slot in the double linked list
		 * representing the duration of being unfixed. When this slot is
		 * not contained by the list or the last element of it, the
		 * successor is set to <tt>null</tt>.
		 */
		protected Slot next = this;

		/**
		 * Constructs a new empty slot with the specified index. The new
		 * slot contains no object, is not fixed and has no predecessor
		 * and successor.
		 *
		 * @param index the index of the new slot.
		 */
		public Slot (int index) {
			super(index);
		}

		/**
		 * Removes this slot out of the double linked list representing
		 * duration of being unfixed.
		 */
		protected void unlink () {
			if (prev!=null)
				prev.next = next;
			else
				first = next;
			if (next!=null)
				next.prev = prev;
			else
				last = prev;
			prev = next = this;
		}

		/**
		 * Fixes this slot so that the object contained by it cannot be
		 * removed out of the LRU buffer. This implementation also removes
		 * the slot out of the double linked list.
		 */
		public void fix () {
			super.fix();
			unlink();
		}

		/**
		 * Unfixes this slot so that the object contained by it can be
		 * removed out of the buffer. This implementation also appends the
		 * slot in front of the double linked list.
		 */
		public void unfix () {
			super.unfix();
			unlink();
			next = first;
			first = this;
			prev = null;
			if (next!=null)
				next.prev = this;
			else
				last = this;
		}

		/**
		 * Removes the object and any information belonging to it from
		 * this slot so that it is empty thereafter. This implementation
		 * also removes the slot out of the double linked list.
		 */
		public void remove () {
			super.remove();
			unlink();
		}
	}

	/**
	 * Constructs a new empty LRU buffer with a number of slots specified
	 * by the given capacity and an empty double linked list.
	 *
	 * @param capacity the number of slots in the new LRU buffer.
	 */
	public LRUBuffer (int capacity) {
		super(capacity);
	}

	/**
	 * Creates a new empty slot with the specified index. The new slot
	 * contains no object, is not fixed and has no predecessor and
	 * successor. For further detail see contract for
	 * {@link Buffer#newSlot(int) newSlot} in Buffer.
	 *
	 * @param index the index of the new slot.
	 * @return a new empty slot with the specified index.
	 */
	protected Buffer.Slot newSlot (int index) {
		return new Slot(index);
	}

	/**
	 * Returns the <i>next</i> slot to displace in this LRU buffer. This
	 * implementation returns the last slot in the double linked list
	 * representing the durating of being unfixed.
	 *
	 * @return the least recently used slot in this buffer.
	 */
	protected Buffer.Slot victim () {
		return last;
	}

	/**
	 * The main method contains some examples how to use a LRUBuffer. It
	 * can also be used to test the functionality of a LRUBuffer.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create a new owner
		String owner = "owner";
		// create a new LRU buffer with a capacity of 5 objects
		LRUBuffer buffer = new LRUBuffer(5);
		// create a new iterator with 100 integers between 0 and 5
		java.util.Iterator iterator = new xxl.core.cursors.sources.RandomIntegers(6, 100);
		// insert all elements of the iterator with a flush function that
		// prints the flushed integer into the buffer
		while (iterator.hasNext()) {
			Integer i = (Integer)iterator.next();
			System.out.println("insert "+i);
			buffer.update(owner, i, i, new xxl.core.functions.Function() {
				public Object invoke (Object o1, Object o2) {
					System.out.println("flush "+o1);
					return o1;
				}
			}, true);
		}
		System.out.println();
	}
}