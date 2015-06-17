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

package xxl.core.util.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import xxl.core.cursors.Cursor;
import xxl.core.cursors.mappers.Mapper;
import xxl.core.cursors.sources.Enumerator;
import xxl.core.functions.Function;

/**
 * This class provides some static methods which make heavily use
 * of the Java reflection mechanism.
 */
public class Reflections {
	/**
	 * No instance of this class allowed, because it only contains
	 * static methods.
	 */
	private Reflections() {
	}

	/**
	 * Converts a given String into a type which is given by
	 * its class object.
	 * @param s String containing a value.
	 * @param type Class object of the desired return type.
	 * @return the value parsed from the String.
	 */
	public static Object convertStringToDifferentType(String s, Class type) {
		if (type==String.class)
			return s;
		else if (type==StringBuffer.class)
			return new StringBuffer(s);
		else if (type==int.class || type==Integer.class)
			return new Integer(s);
		else if (type==byte.class || type==Byte.class)
			return new Byte(s);
		else if (type==short.class || type==Short.class)
			return new Short(s);
		else if (type==long.class || type==Long.class)
			return new Long(s);
		else if (type==float.class || type==Float.class)
			return new Float(s);
		else if (type==double.class || type==Double.class)
			return new Double(s);
		else if (type==boolean.class || type==Boolean.class)
			return new Boolean(s);
		else if (type==char.class || type==Character.class)
			return new Character(s.charAt(0));
		else
			return null;
	}

	/**
	 * Determines if the type is supported by the convertStringToDifferentType
	 * method above.
	 * @param type Class object of the desired return type.
	 * @return true iff the type is supported.
	 */
	public static boolean convertStringToDifferentTypeSupported(Class type) {
		return 
			type==String.class ||
			type==StringBuffer.class ||
			type==int.class || type==Integer.class ||
			type==byte.class || type==Byte.class ||
			type==short.class || type==Short.class ||
			type==long.class || type==Long.class ||
			type==float.class || type==Float.class ||
			type==double.class || type==Double.class ||
			type==boolean.class || type==Boolean.class ||
			type==char.class || type==Character.class;
	}

	/**
	 * Sets the static fields of a class cl with
	 * values which are given by a map.
	 * @param map Map containing the values to be set.
	 * @param cl Given class.
	 */
	public static void setStaticFields(Map map, Class cl) {
		Iterator it = map.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry e = (Entry) it.next();
			try {
				Field field = cl.getField((String) e.getKey());
				field.set(null, e.getValue());
			}
			catch (IllegalAccessException ex) {}
			catch (NoSuchFieldException ex) {}
		}
	}

	/**
	 * Returns a function which converts Number types into the
	 * desired Wrapper class type, which is given by returnClass.
	 * @param returnClass Desired return Class. If a primitive
	 * 	class is given, then also the associated wrapper class is
	 * 	used.
	 * @return The conversion function.
	 */
	public static Function getNumberTypeConversionFunction(final Class returnClass) {
		return 
			new Function() {
				public Object invoke(Object o) {
					Number n = (Number) o;
					if (returnClass == byte.class || returnClass == Byte.class)
						return new Byte(n.byteValue());
					if (returnClass == short.class || returnClass == Short.class)
						return new Short(n.shortValue());
					if (returnClass == int.class || returnClass == Integer.class)
						return new Integer(n.intValue());
					if (returnClass == long.class || returnClass == Long.class)
						return new Long(n.longValue());
					if (returnClass == float.class || returnClass == Float.class)
						return new Float(n.floatValue());
					if (returnClass == double.class || returnClass == Double.class)
						return new Double(n.doubleValue());
					else
						return n;
				}
			};
	}

	/**
	 * Returns true iff the given class is a integral number type. 
	 * @param clv Given class
	 * @return true iff the given class is a integral number type.
	 */
	public static boolean isIntegralType(Class clv) {
		return 
			clv == int.class || clv == short.class || clv == byte.class || clv == long.class ||
			clv == Integer.class || clv == Short.class || clv == Byte.class || clv == Long.class;
	}

	/**
	 * Returns true iff the given class is a real number type. 
	 * @param clv Given class
	 * @return true iff the given class is a real number type.
	 */
	public static boolean isRealType(Class clv) {
		return 
			clv == float.class || clv == double.class ||
			clv == double.class || clv == Double.class;
	}

	/**
	 * Calls the main method of a class cl with a given paramter array.
	 * @param cl The class.
	 * @param args String array with parameters for the main method.
	 */
	public static void callMainMethod(Class cl, String[] args) {
		Method m;
		try {
			m = cl.getMethod("main", new Class[]{String[].class});
			m.invoke(null, new Object[]{args});
		}
		catch (SecurityException e) {}
		catch (NoSuchMethodException e) {}
		catch (IllegalArgumentException e) {}
		catch (IllegalAccessException e) {}
		catch (InvocationTargetException e) {}
	}

	/**
	 * Returns a Cursor iterating over the elements of the
	 * array. This method is applicable for all kinds of arrays.
	 * @param valuesArray Array of arbitrary base type.
	 * @return Cursor iterating over the elements of the array.
	 */
	public static Cursor typedArrayCursor(final Object valuesArray) {
		return
			new Mapper(
				new Enumerator(Array.getLength(valuesArray)),
				new Function () {
					public Object invoke(Object index) {
						return Array.get(valuesArray, ((Integer)index).intValue());
					}
				}
			);
	}
}
