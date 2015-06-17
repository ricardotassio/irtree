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

package xxl.core.collections;

import java.util.Map.Entry;

import xxl.core.functions.Function;

/**
 * This class provides a straightforward implementation of the <code>Map.Entry</code>
 * interface from <code>java.util</code>. A MapEntry is a key-value pair. The
 * <code>Map.entrySet</code> method returns a collection-view of the map, whose
 * elements are of this class. One way to obtain a reference to a map
 * entry is from the iterator of this collection-view. These <code>Map.Entry</code>
 * objects are valid only for the duration of the iteration; more
 * formally, the behavior of a map entry is undefined if the backing map
 * has been modified after the entry was returned by the iterator, except
 * through the iterator's own remove operation, or through the <code>setValue</code>
 * operation on a map entry returned by the iterator.<p>
 *
 * Usage example (1).
 * <pre>
 *     // create two MapEntry
 *
 *     MapEntry entry1 = new MapEntry(new Integer(5), "five"),
 *              entry2 = new MapEntry(new Integer(2), "two");
 *
 *     // check if both entries are equal
 *
 *     if (entry1.equals(entry2))
 *         System.out.println("the entries are equal");
 *     else
 *         System.out.println("the entries are unqual");
 *
 *     // change the second entry
 *
 *     entry2.setKey(new Integer(5));
 *
 *     // check if both entries are equal
 *
 *     if (entry1.equals(entry2))
 *         System.out.println("the entries are equal");
 *     else
 *         System.out.println("the entries are unqual");
 *
 *     // change the second entry
 *
 *     entry2.setKey(new Integer(2));
 *     entry2.setValue("five");
 *
 *     // check if both entries are equal
 *
 *     if (entry1.equals(entry2))
 *         System.out.println("the entries are equal");
 *     else
 *         System.out.println("the entries are unqual");
 *
 *     // change the second entry
 *
 *     entry2.setKey(new Integer(5));
 *
 *     // check if both entries are equal
 *
 *     if (entry1.equals(entry2))
 *         System.out.println("the entries are equal");
 *     else
 *         System.out.println("the entries are unqual");
 * </pre>
 *
 * @see java.util.Map.Entry
 */
public class MapEntry implements Entry {
	/** 
	 * Factory method. To get an Object of the class MapEntry, use the invoke
	 * method with two arguments: key and value.
	 */
	public static Function FACTORY_METHOD =
		new Function () {
			public Object invoke(Object key, Object value) {
				return new MapEntry(key,value);
			}
		};

	/**
	 * Converts the MapEntry to an Object array.
	 */
	public static Function TO_OBJECT_ARRAY_FUNCTION =
		new Function () {
			public Object invoke(Object mapEntry) {
				return new Object[] { 
					((java.util.Map.Entry) mapEntry).getKey(),
					((java.util.Map.Entry) mapEntry).getValue()
				};
			}
		};

	/**
	 * The key object of this MapEntry.
	 */
	protected Object key;

	/**
	 * The value object of this MapEntry.
	 */
	protected Object value;

	/**
	 * Constructs a new MapEntry with the specified key and value object.
	 *
	 * @param key the key of the new MapEntry.
	 * @param value the value of the new MapEntry.
	 */
	public MapEntry (Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Compares the specified object with this entry for equality. Returns
	 * <tt>true</tt> if the given object is also a map entry and the two
	 * entries represent the same mapping. More formally, two entries
	 * <tt>e1</tt> and <tt>e2</tt> represent the same mapping if
	 * <pre>
	 * (e1.getKey()==null ?
	 *  e2.getKey()==null : e1.getKey().equals(e2.getKey()))  &&
	 * (e1.getValue()==null ?
	 *  e2.getValue()==null : e1.getValue().equals(e2.getValue()))
	 * </pre>
	 *
	 * @param o object to be compared for equality with this map entry.
	 * @return <tt>true</tt> if the specified object is equal to this map
	 *         entry.
	 */
	public boolean equals (Object o) {
		Entry mapEntry = (Entry)o;
		return getKey()==null?
			mapEntry.getKey()==null:
			getKey().equals(mapEntry.getKey()) && (
				getValue()==null?
					mapEntry.getValue()==null:
					getValue().equals(mapEntry.getValue())
			)
		;
	}

	/**
	 * Returns the key corresponding to this entry.
	 *
	 * @return the key corresponding to this entry.
	 */
	public Object getKey () {
		return key;
	}

	/**
	 * Returns the value corresponding to this entry.
	 *
	 * @return the value corresponding to this entry.
	 */
	public Object getValue () {
		return value;
	}

	/**
	 * Returns the hash code value for this map entry. The hash code of a
	 * map entry <tt>e</tt> is defined to be:
	 * <pre>
	 * (e.getKey()==null   ? 0 : e.getKey().hashCode()) ^
	 * (e.getValue()==null ? 0 : e.getValue().hashCode())
	 * </pre>
	 * This ensures that <tt>e1.equals(e2)</tt> implies that
	 * <tt>e1.hashCode()==e2.hashCode()</tt> for any two Entries
	 * <tt>e1</tt> and <tt>e2</tt>, as required by the general contract of
	 * Object.hashCode.
	 *
	 * @return the hash code value for this map entry.
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	public int hashCode () {
		return (getKey()==null? 0: getKey().hashCode()) ^ (getValue()==null? 0: getValue().hashCode());
	}

	/**
	 * Returns the value corresponding to this entry. If the mapping has
	 * been removed from the backing map (by the iterator's remove
	 * operation), the results of this call are undefined.
	 *
	 * @param key the new key of the map entry.
	 * @return the value corresponding to this entry.
	 */
	public Object setKey (Object key) {
		Object oldKey = this.key;
		this.key = key;
		return oldKey;
	}

	/**
	 * Replaces the value corresponding to this entry with the specified
	 * value. The behavior of this call is undefined if the mapping has
	 * already been removed from the map (by the iterator's remove
	 * operation).
	 *
	 * @param value new value to be stored in this entry.
	 * @return old value corresponding to the entry.
	 * @throws UnsupportedOperationException if the <tt>put</tt> operation
	 *         is not supported by the backing map.
	 * @throws ClassCastException if the class of the specified value
	 *         prevents it from being stored in the backing map.
	 * @throws IllegalArgumentException if some aspect of this value
	 *         prevents it from being stored in the backing map.
	 * @throws NullPointerException the backing map does not permit null
	 *         values, and the specified value is null.
	 */
	public Object setValue (Object value) throws ClassCastException, IllegalArgumentException, NullPointerException, UnsupportedOperationException {
		Object oldValue = this.value;
		this.value = value;
		return oldValue;
	}

	/**
	 * Converts the MapEntry to a String.
	 * @return a String representation of the key value pair. 
	 */
	public String toString() {
		return "key: "+key+", value: "+value;
	}

	/**
	 * The main method contains some examples how to use a MapEntry. It
	 * can also be used to test the functionality of a MapEntry.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create two MapEntry
		MapEntry entry1 = new MapEntry(new Integer(5), "five"),
		         entry2 = new MapEntry(new Integer(2), "two");
		// check if both entries are equal
		if (entry1.equals(entry2))
			System.out.println("the entries are equal");
		else
			System.out.println("the entries are unqual");
		// change the second entry
		entry2.setKey(new Integer(5));
		// check if both entries are equal
		if (entry1.equals(entry2))
			System.out.println("the entries are equal");
		else
			System.out.println("the entries are unqual");
		// change the second entry
		entry2.setKey(new Integer(2));
		entry2.setValue("five");
		// check if both entries are equal
		if (entry1.equals(entry2))
			System.out.println("the entries are equal");
		else
			System.out.println("the entries are unqual");
		// change the second entry
		entry2.setKey(new Integer(5));
		// check if both entries are equal
		if (entry1.equals(entry2))
			System.out.println("the entries are equal");
		else
			System.out.println("the entries are unqual");
		System.out.println();
	}
}