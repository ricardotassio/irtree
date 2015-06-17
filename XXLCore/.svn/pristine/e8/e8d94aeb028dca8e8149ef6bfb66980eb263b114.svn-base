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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import xxl.core.functions.Constant;
import xxl.core.functions.Function;

/**
 * This class provides a buffer for buffering I/Os in order to increase
 * performance. <p>
 *
 * It does not define any displacement strategy. When implementing a
 * buffer by extending this class, the user only has to implement the
 * victim method, that determines the <i>next</i> slot to displace in the
 * buffer. Objects in the buffer are identified by their owner and an id
 * that is unique at the owner. Additional, the buffer stored a map	that
 * contains the owners and their slots in this buffer. The owners are
 * mapped to maps that contain the owner's slots. These slot maps identify
 * the slots by their ids (for further detail see field
 * {@link Slot#members members} in <tt>Buffer.Slot</tt>).
 * 
 * Important: every class used as identifyers has to implement
 * the hashCode method of {@link java.lang.Object}.
 *  
 * The buffer supports the access, update and removal of slots (the objects contained
 * by the slots), that identified by their id and owner, and the the
 * removal of all slot owned by a specified owner. Slots of the buffer can
 * also be fixed (in order to avoid the removal of them) and unfixed. In
 * order to guarantee highest flexibility, the way of flushing an object
 * in the buffer must be implemented by a function. Therefore an owner can
 * specify an own flush function for every object in the buffer.<p>
 *
 * Objects can be inserted into the buffer by calling the fix or get
 * method with an id that is new at the owner and a function, that returns
 * the object to insert, when it is invoked with the specified id. The
 * update method can also be used for inserting an object, when it is
 * called with a new id and the object to insert and a flush function for
 * it.
 *
 * @see Constant
 * @see Function
 * @see HashMap
 * @see Iterator
 * @see Map
 * @see java.util.Map.Entry
 */
public abstract class Buffer {

	/**
	 * This class provides a single slot in a buffer. <p>
	 *
	 * The slots are indexed according to their position in the buffer
	 * beginning at index <tt>0</tt>. Every slot is able to contain a
	 * single object. In addition to the object itself, the slot stores an
	 * id for the object, the owner of the object and a map of all slots
	 * that are owned by the owner of the object. The id AND the owner of
	 * an object are stored in order to ease the identification of
	 * elements in the buffer. Therefore the id of an object must only be
	 * an unique id at the owner but not a global unique id. A slot can be
	 * fixed, so that its object can not be removed from it. In order to
	 * guarantee highest flexibility, the way of flushing a slot must be
	 * implemented by a function. Therefore an owner can specify an own
	 * flush function for every object in the buffer.
	 *
	 * @see Function
	 * @see HashMap
	 * @see Map
	 */
	protected class Slot {

		/**
		 * The index of the slot in the buffer. The slots are indexed
		 * according to their position in the buffer beginning at index
		 * <tt>0</tt>.
		 */
		protected int index;

		/**
		 * A map that contains all slots that are owned by the owner of
		 * this slot. The slots are identified by their ids. Therefore the
		 * id must only be an unique id at the owner but not a global
		 * unique id. Every class of identifyers has to implement the hashCode
		 * method.
		 */
		protected Map members;

		/**
		 * An owner of this slot (the object contained by this slot).
		 */
		protected Object owner;

		/**
		 * An id that identifies this slot (the object contained by this
		 * slot).
		 */
		protected Object id;

		/**
		 * The object that is contained by this slot.
		 */
		protected Object object = null;

		/**
		 * A flag that determines whether this slot is fixed or not. The
		 * object contained by a fixed slot and the information belonging
		 * to it cannot be removed.
		 */
		protected boolean isFixed = false;

		/**
		 * A function that implements the functionality of flushing a
		 * slot. When this slot should be flushed, the function is called
		 * with its id and object. Therefore the function must implement
		 * all the functionality that is needed to flush a slot.
		 */
		protected Function flush = null;

		/**
		 * Constructs a new empty slot with the specified index. The new
		 * slot contains no object and is not fixed.
		 *
		 * @param index the index of the new slot.
		 */
		protected Slot (int index) {
			this.index = index;
		}

		/**
		 * Returns the id of this slot.
		 *
		 * @return the id of this slot.
		 */
		protected Object id () {
			return id;
		}

		/**
		 * Returns the object that is contained by this slot.
		 *
		 * @return the object of this slot.
		 */
		protected Object get () {
			return object;
		}

		/**
		 * Returns whether this slot is dirty or not. In other words,
		 * returns <tt>true</tt> if (<tt>flush!=null</tt>) (the object is
		 * not yet flushed), else returns <tt>false</tt>.
		 *
		 * @return <tt>true</tt> if this slot is dirty, else returns
		 *         <tt>false</tt>.
		 */
		protected boolean isDirty () {
			return flush!=null;
		}

		/**
		 * Returns whether this slot is fixed or not. In other words,
		 * returns <tt>true</tt> if (<tt>isFixed==true</tt>), else returns
		 * <tt>false</tt>.
		 *
		 * @return <tt>true</tt> if this slot is fixed, else returns
		 *         <tt>false</tt>.
		 */
		protected boolean isFixed () {
			return isFixed;
		}

		/**
		 * Fixes this slot so that the object contained by it cannot be
		 * removed out of the buffer.
		 */
		protected void fix () {
			if (!isFixed())
				fixedSlots++;
			isFixed = true;
		}

		/**
		 * Unfixes this slot so that the object contained by it can be
		 * removed out of the buffer.
		 */
		protected void unfix () {
			if (isFixed())
				fixedSlots--;
			isFixed = false;
		}

		/**
		 * Flushes this slot by calling its flush function with its id and
		 * object. This implementation checks first whether the slot is
		 * dirty. When it is dirty, the flush function is called and set
		 * to <tt>null</tt>, i.e. the slot is not any longer dirty after
		 * calling this method.
		 */
		protected void flush () {
			if (isDirty()) {
				flush.invoke(id, object);
				flush = null;
			}
		}

		/**
		 * Updates the object and flush function of this slot. The object
		 * and flush function are replaced by the specified object and
		 * function, i.e. the slot is dirty after calling this method.
		 *
		 * @param object the new object of this slot.
		 * @param flush the new flush function of this slot.
		 */
		protected void update (Object object, Function flush) {
			this.object = object;
			this.flush = flush;
		}

		/**
		 * Inserts the specified object with the specified id and owner in
		 * this slot. Sets the object, id and owner of this slot to the
		 * specified objects and updates member map of this slot and the
		 * owner map of the buffer.
		 *
		 * @param owner the new owner of this slot.
		 * @param id the new id of this slot.
		 * @param object the new object of this slot.
		 */
		protected void insert (Object owner, Object id, Object object) {
			this.owner = owner;
			this.id = id;
			this.object = object;
			if ((members = (Map)owners.get(owner))==null)
				owners.put(owner, members = new HashMap());
			members.put(id, this);
			size++;
		}

		/**
		 * Removes the object and any information belonging to it from
		 * this slot so that it is empty thereafter. This implementation
		 * swaps this slot and the occupied slot with the highest index.
		 * Then their indices, this slot's member map and the owner map of
		 * the buffer are updated. At last, the attributes of this slot
		 * are reset.
		 */
		protected void remove () {
			if (index<size) {
				Slot slot = slots[--size];

				slots[slot.index = index] = slot;
				slots[index = size] = this;
				if (members.containsKey(id)) {
					members.remove(id);
					if (members.isEmpty())
						owners.remove(owner);
					members = null;
				}
				owner = null;
				if (isFixed())
					fixedSlots--;
				isFixed = false;
				flush = null;
				object = null;
			}
		}

		/**
		 * Displaces this slot by flushing it and removing the object and
		 * any information belonging to it from it.
		 */
		protected void displace () {
			flush();
			remove();
		}
	}

	/**
	 * The number of fixed slots in this buffer.
	 */
	protected int fixedSlots = 0;

	/**
	 * The number of slots in this buffer that contain an object.
	 */
	protected int size = 0;

	/**
	 * An array containing all the slots of this buffer.
	 */
	protected Slot [] slots;

	/**
	 * A map that contains the owners and their slots in this buffer. The
	 * owners are mapped to maps that contain the owner's slots. These
	 * slot maps identify the slots by their ids (for further detail see
	 * field {@link Slot#members members} in <tt>Buffer.Slot</tt>).
	 */
	protected Map owners = new HashMap();

	/**
	 * Constructs a new empty buffer with a number of slots specified by
	 * the given capacity.
	 *
	 * @param capacity the number of slots in the new buffer.
	 */
	public Buffer (int capacity) {
		this.slots = new Slot[capacity];
		for (int i = 0; i<capacity; i++)
			slots[i] = newSlot(i);
	}

	/**
	 * Returns the <i>next</i> slot to displace in this buffer. This
	 * method is called every time a slot should be displaced and must
	 * implement the displacement strategy of this buffer.
	 *
	 * @return the <i>next</i> slot to displace in this buffer.
	 */
	protected abstract Slot victim ();

	/**
	 * Creates a new empty slot with the specified index. This factory
	 * method simply calls the constructor of <tt>Slot</tt>. Every
	 * subclass of <tt>Buffer</tt> that extends the inner class
	 * <tt>Slot</tt> must overwrite this method by defining the method
	 * <pre>
	 * protected Buffer.Slot newSlot (int index) {
	 *     return new Slot(index);
	 * }
	 * </pre>
	 * This guarantees that every call of the newSlot method creates the
	 * correct corresponding <tt>Slot</tt> object of the subclass.
	 *
	 * @param index the index of the new slot.
	 * @return a new empty slot with the specified index.
	 */
	protected Slot newSlot (int index) {
		return new Slot(index);
	}

	/**
	 * Returns the number of slots in this buffer that contain an object.
	 *
	 * @return the number of occupied slots in this buffer.
	 */
	protected int size () {
		return size;
	}

	/**
	 * Returns the slot with the given id owned by the specified owner.
	 * This implementation maps owner to the map that contains the owner's
	 * slots by using the map <tt>owners</tt>. Thereafter the slot map is
	 * used for mapping the id to the slot identified by the id. When
	 * there is no such slot <tt>null</tt> is returned.
	 *
	 * @param owner the owner of the slot to return.
	 * @param id the id of the slot to return.
	 * @return the slot with the given id owned by the specified owner or
	 *         <tt>null</tt> if no such slot exists.
	 */
	protected Slot lookUp (Object owner, Object id) {
		Map members = (Map)owners.get(owner);

		return members==null ? null : (Slot)members.get(id);
	}

	/**
	 * Fixes the slot with the given id owned by the specified owner and
	 * returns it. When no such slot exists, a new object is created by
	 * calling the given function obtain with the specified id and this
	 * object is inserted into the buffer. When the buffer overflows (it
	 * is full and all slots are fixed), an <tt>IllegalStateException</tt>
	 * will be thrown. Otherwise the <i>next</i> slot to displace will be
	 * determined by calling the victim method and its object will be
	 * replaced by the new object.
	 *
	 * @param owner the owner of the slot to fix.
	 * @param id the id of the slot to fix.
	 * @param obtain a function for creating a new object, when there is
	 *        no slot the the given id owned by the specified owner.
	 * @return the fixed slot with the given id owned by the specified
	 *         owner.
	 * @throws IllegalStateException when the buffer overflows.
	 */
	protected Slot fix (Object owner, Object id, Function obtain) throws IllegalStateException {
		Slot slot = lookUp(owner, id);

		if (slot==null) {
			if (fixedSlots==slots.length)
				throw new IllegalStateException("Buffer overflows.");
			if (size()==slots.length)
				victim().displace();
			(slot = slots[size()]).insert(owner, id, obtain.invoke(id));
		}
		slot.fix();
		return slot;
	}

	/**
	 * Unfixes the slot with the given id owned by the specified owner.
	 * The desired slot is determined by calling the lookUp method. When
	 * such a slot exists, its unfix method is called.
	 *
	 * @param owner the owner of the slot to unfix.
	 * @param id the id of the slot to unfix.
	 */
	public void unfix (Object owner, Object id) {
		Slot slot = lookUp(owner, id);

		if (slot!=null)
			slot.unfix();
	}

	/**
	 * Returns whether this buffer contains a slot with the given id owned
	 * by the specified owner. In other words, returns <tt>true</tt> if
	 * (<code>lookUp(owner,id)!=null</code>), else returns <tt>false</tt>.
	 *
	 * @param owner the owner of the desired slot.
	 * @param id the id of the desired slot.
	 * @return <tt>true</tt> if this buffer contains a slot with the given
	 *         id owned by the specified owner, else returns
	 *         <tt>false</tt>.
	 */
	public boolean contains (Object owner, Object id) {
		return lookUp(owner, id)!=null;
	}

	/**
	 * Returns whether the slot with the given id owned by the specified
	 * owner is fixed or not. This implementation checks whether such a
	 * slot exists and, when it exists, whether it is fixed.
	 *
	 * @param owner the owner of the desired slot.
	 * @param id the id of the desired slot.
	 * @return <tt>true</tt> if the slot with the given id owned by the
	 *         specified owner is fixed, else returns <tt>false</tt>.
	 */
	public boolean isFixed (Object owner, Object id) {
		Slot slot = lookUp(owner, id);

		return slot!=null && slot.isFixed();
	}

	/**
	 * Flushes the slot with the given id owned by the specified owner.
	 * This implementation checks whether such a slot exists and flushes
	 * it, when it exists.
	 *
	 * @param owner the owner of the slot to flush.
	 * @param id the id of the slot to flush.
	 */
	public void flush (Object owner, Object id) {
		Slot slot = lookUp(owner, id);

		if (slot!=null)
			slot.flush();
	}

	/**
	 * Flushes all slots in this buffer that are owned by the specified
	 * owner. This implementation gets all slot owned by the specified
	 * owner by using the map <tt>owners</tt> and flushes these slot
	 * thereafter.
	 *
	 * @param owner the owner of the slots to flush.
	 */
	public void flushAll (Object owner) {
		Map members = (Map)owners.get(owner);

		if (members!=null)
			for (Iterator slots = members.values().iterator(); slots.hasNext(); ((Slot)slots.next()).flush());
	}

	/**
	 * Returns the object contained by the slot with the given id owned by
	 * the specified owner. When no such slot exists, a new object is
	 * created by calling the given function obtain with the specified id
	 * and this object is inserted into the buffer. When the buffer
	 * overflows (it is full and all slots are fixed), an
	 * <tt>IllegalStateException</tt> will be thrown. Otherwise the
	 * <i>next</i> slot to displace will be determined by calling the
	 * victim method and its object will be replaced by the new object.
	 * When (<tt>unfix==true</tt>) the slot containing the desired object
	 * is unfixed at last.<br>
	 * This implementation fixes the desired slot by calling this buffer's
	 * fix method with the specified owner, id and obtain function and
	 * calls the slot's get method thereafter. When
	 * (<tt>unfix==true</tt>), its unfix method is called at last.
	 *
	 * @param owner the owner of the slot containing the object to get.
	 * @param id the id of the slot containing the object to get.
	 * @param obtain a function for creating a new object, when there is
	 *        no slot the the given id owned by the specified owner.
	 * @param unfix a flag that determines whether the desired slot should
	 *        be unfixed after getting its object or not.
	 * @return the object contained by the slot with the given id owned by
	 *         the specified owner.
	 * @throws IllegalStateException when the buffer overflows.
	 */
	public Object get (Object owner, Object id, Function obtain, boolean unfix) throws IllegalStateException {
		Slot slot = fix(owner, id, obtain);
		Object object = slot.get();

		if (unfix)
			slot.unfix();
		return object;
	}

	/**
	 * Updates the slot with the given id owned by the specified owner
	 * with the specifed object and flush function. When no such slot
	 * exists, the given object is inserted into the buffer. When the
	 * buffer overflows (it is full and all slots are fixed), an
	 * <tt>IllegalStateException</tt> will be thrown. Otherwise the
	 * <i>next</i> slot to displace will be determined by calling the
	 * victim method and its object and flush function will be replaced by
	 * the given object and flush function. When (<tt>unfix==true</tt>)
	 * the slot containing the desired object is unfixed at last.<br>
	 * This implementation fixes the desired slot by calling this buffer's
	 * fix method with the specified owner, id and a constant function
	 * that always returns the given object, when it is invoked.
	 * Thereafter the slot's update method is called with the specified
	 * object and flush function. When (<tt>unfix==true</tt>), its
	 * unfix method is called at last.
	 *
	 * @param owner the owner of the slot to update.
	 * @param id the id of the slot to update.
	 * @param object the object that replaces the object contained by the
	 *        desired slot.
	 * @param flush the function that replaces the flush function of the
	 *        desired slot.
	 * @param unfix a flag that determines whether the desired slot should
	 *        be unfixed after updating it or not.
	 * @throws IllegalStateException when the buffer overflows.
	 */
	public void update (Object owner, Object id, Object object, Function flush, boolean unfix) throws IllegalStateException {
		Slot slot = fix(owner, id, new Constant(object));

		slot.update(object, flush);
		if (unfix)
			slot.unfix();
	}

	/**
	 * Removes the object and any information belonging to it from the
	 * slot with the given id owned by the specified owner. This
	 * implementation checks whether such a slot exists and calls its
	 * remove method, when it exists.
	 *
	 * @param owner the owner of the slot to remove.
	 * @param id the id of the slot to remove.
	 */
	public void remove (Object owner, Object id) {
		Slot slot = lookUp(owner, id);

		if (slot!=null)
			slot.remove();
	}

	/**
	 * Removes the objects and any information belonging to them from all
	 * slots in this buffer that are owned by the specified owner. This
	 * implementation gets all slot owned by the specified owner by using
	 * the map <tt>owners</tt> and calls the remove methods of these slots
	 * thereafter.
	 *
	 * @param owner the owner of the slots to remove.
	 */
	public void removeAll (Object owner) {
		Map members = (Map)owners.get(owner);

		if (members!=null)
			for (Iterator entries = members.entrySet().iterator(); entries.hasNext();) {
				Slot slot = (Slot)((Entry)entries.next()).getValue();

				entries.remove();
				slot.remove();
			}
	}
}