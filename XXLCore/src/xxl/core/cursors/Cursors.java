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

package xxl.core.cursors;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import xxl.core.comparators.InverseComparator;
import xxl.core.cursors.groupers.Minimator;
import xxl.core.cursors.mappers.Mapper;
import xxl.core.cursors.wrappers.IteratorCursor;
import xxl.core.functions.Function;
import xxl.core.functions.Print;
import xxl.core.functions.Println;
import xxl.core.predicates.FunctionPredicate;
import xxl.core.predicates.Predicate;
import xxl.core.util.ArrayResizer;

/**
 * This class contains various <tt>static</tt> methods for manipulating
 * iterators. For example :
 * <ul>
 *     <li>
 *         A user defined function can be applied to each element of an iterator,
 *         to a couple of elements of two iterators traversed synchronously or
 *         even to a set of elements of an array of iterators.
 *     </li>
 *     <li>
 *         A condition can be checked for a whole iterator by invoking a given
 *         boolean function or predicate on it.
 *     </li>
 *     <li>
 *         Useful methods are given to determine the first, the last or the
 *         <tt>n</tt>'th element of an iterator, its minima and maxima and to
 *         update or remove all elements of an iterator.
 *     </li>
 *     <li>
 *         Some converters are given, i.e., the caller is able to convert an
 *         iterator to a list, a map, a collection or an array.
 *      </li>
 * </ul>
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     Cursor cursor = new Enumerator(11);
 *     cursor.open();
 *     
 *     System.out.println("First element : " + Cursors.first(cursor));
 *     
 *     cursor.reset();
 *     System.out.println("Third element : " + Cursors.nth(cursor, 3));
 * 
 *     cursor.reset();
 *     System.out.println("Last element : " + Cursors.last(cursor));
 * 
 *     cursor.reset();
 *     System.out.println("Length : " + Cursors.count(cursor));
 * 
 *     cursor.close();
 * </pre>
 * These examples demonstrate some useful static methods for navigation in a
 * cursor.</p>
 * 
 * <p><b>Example usage (2):</b>
 * <pre>
 *     Map.Entry entry = Cursors.maximize(
 *         cursor = new RandomIntegers(100, 50),
 *         new Function() {
 *             public Object invoke(Object object) {
 *                 return new Integer(((Integer)object).intValue());
 *             }
 *         }
 *     );
 *     System.out.println("Maximum value is : " + entry.getKey());
 * 
 *     Iterator iterator = ((LinkedList)entry.getValue()).listIterator(0);
 *     System.out.print("Maxima : ");
 *     while(iterator.hasNext())
 *         System.out.print(iterator.next() + "; ");
 *     System.out.flush();
 *     
 *     cursor.close();
 * </pre>
 * This example computes the maximum value of 50 randomly distributed integers
 * of the interval [0, 100) using a mapping. Also an iterator containing all
 * maxima is printed to the output.</p>
 * 
 * <p><b>Example usage (3):</b>
 * <pre>
 *     Cursors.forEach(
 *         cursor = new Enumerator(11),
 *         new Function() {
 *             public Object invoke(Object object) {
 *                 Object result = new Integer((int)Math.pow(((Integer)object).intValue(), 2));
 *                 System.out.println("Number : " + object + " ; Number^2 : " + result);
 *                 return result;
 *             }
 *         }
 *     );
 *     cursor.close();
 * </pre>
 * This example computes the square value for each element of the given
 * enumerator with range 0,...,10.</p>
 * 
 * <p><b>Example usage (4):</b>
 * <pre>
 *     System.out.println(
 *         "Is the number '13' contained in the RandomIntegers' cursor : " +
 *         Cursors.any(
 *             cursor = new RandomIntegers(1000, 200),
 *             new Predicate() {
 *                 public boolean invoke(Object object) {
 *                     return ((Integer)object).intValue() == 13;
 *                 }
 *             }
 *         )
 *     );
 *     cursor.close();
 * </pre>
 * This example checks if a cursor of 200 randomly distributed integers with
 * maximum value 999 contains the value 13 by evaluating a predictate on its
 * elements. It returns <tt>true</tt> if the predicate is <tt>true</tt> for only
 * one element of the input cursor.</p>
 * 
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.functions.Function
 * @see xxl.core.predicates.Predicate
 * @see java.util.Collection
 * @see java.util.List
 * @see java.util.Map
 * @see java.util.Map.Entry
 * @see java.util.Comparator
 */
public abstract class Cursors {

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private Cursors() {}

	/**
	 * Invokes the given unary function on each element of the given iterator.
	 *
	 * @param iterator the iterator delivering the elements the function should
	 *        be applied to.
	 * @param function the function to be invoked on each element of the
	 *        iterator.
	 */
	public static void forEach(Iterator iterator, Function function) {
		while (iterator.hasNext())
			function.invoke(iterator.next());
	}

	/**
	 * Invokes the given binary function on each pair of elements of
	 * <tt>iterator1</tt> and <tt>iterator2</tt> while both iterators are not
	 * empty.
	 *
	 * @param iterator1 the iterator that contains the first element of each
	 *        pair.
	 * @param iterator2 the iterator that contains the second element of each
	 *        pair.
	 * @param function the function to be invoked on each pair of elements.
	 */
	public static void forEach(Iterator iterator1, Iterator iterator2, Function function) {
		while (iterator1.hasNext() && iterator2.hasNext())
			function.invoke(iterator1.next(), iterator2.next());
	}

	/**
	 * Invokes the higher-order function on each set of elements given by the
	 * input iterator array. The set contains one element per iterator and the
	 * function is only invoked while all iterators are not empty.
	 *
	 * @param iterators the iterators used to get a set of elements by taking one
	 *        element per iterator.
	 * @param function the function to be invoked on each set of elements.
	 */
	public static void forEach(Iterator[] iterators, Function function) {
		Cursors.consume(new Mapper(iterators, function));
	}

	/**
	 * Returns <tt>true</tt> if the unary, boolean function returns
	 * {@link java.lang.Boolean#TRUE true} for each element of the given
	 * iterator.
	 * 
	 * <p><b>Note :</b> The boolean function is internally wrapped to a
	 * predicate.</p>
	 *
	 * @param iterator the iterator to be checked.
	 * @param function the boolean function to be invoked on each element of the
	 *        given iterator.
	 * @return <tt>true</tt> if the function returns
	 *         {@link java.lang.Boolean#TRUE true} for each element of the
	 *         iterator, otherwise <tt>false</tt>.
	 */
	public static boolean all(Iterator iterator, Function function) {
		return all(iterator, new FunctionPredicate(function));
	}

	/**
	 * Returns <tt>true</tt> if the unary predicate is <tt>true</tt> for each
	 * element of the given iterator.
	 *
	 * @param iterator the iterator to be checked.
	 * @param predicate the predicate to be invoked on each element of the given
	 *        iterator.
	 * @return <tt>true</tt> if the predicate is <tt>true</tt> for each element
	 * 	       of the iterator, otherwise <tt>false</tt>.
	 */
	public static boolean all(Iterator iterator, Predicate predicate) {
		while (iterator.hasNext())
			if (!predicate.invoke(iterator.next()))
				return false;
		return true;
	}

	/**
	 * Returns <tt>true</tt> if the given boolean function returns
	 * {@link java.lang.Boolean#TRUE true} for each pair of elements of
	 * <tt>iterator1</tt> and <tt>iterator2</tt>. The comparison is stopped when 
	 * <ul>
	 *     <li>
	 *         one iterator is empty or
	 *     </li>
	 *     <li>
	 *         the function returns {@link java.lang.Boolean#FALSE false}.
	 *     </li>
	 * </ul>
	 * 
	 * <p><b>Note :</b> The boolean function is internally wrapped to a
	 * predicate.</p>
	 *
	 * @param iterator1 the iterator that contains the first element of each
	 *        pair.
	 * @param iterator2 the iterator that contains the second element of each
	 *        pair.
	 * @param function the function to be invoked on each pair of elements.
	 * @return <tt>true</tt> if the function returns
	 *         {@link java.lang.Boolean#TRUE true} for each pair of elements of
	 *         <tt>iterator1</tt> and <tt>iterator2</tt>, otherwise
	 *         <tt>false</tt>.
	 */
	public static boolean all(Iterator iterator1, Iterator iterator2, Function function) {
		return all(iterator1, iterator2, new FunctionPredicate(function));
	}

	/**
	 * Returns <tt>true</tt> if the given predicate is <tt>true</tt> for each
	 * pair of elements of <tt>iterator1</tt> and <tt>iterator2</tt>. The
	 * comparison is stopped when 
	 * <ul>
	 *     <li>
	 *         one iterator is empty or
	 *     </li>
	 *     <li>
	 *         the predicate returns false.
	 *     </li>
	 * </ul>
	 *
	 * @param iterator1 the iterator that contains the first element of each
	 *        pair.
	 * @param iterator2 the iterator that contains the second element of each
	 *        pair.
	 * @param predicate the predicate to be evaluated for each pair of elements.
	 * @return <tt>true</tt> if the given predicate returns <tt>true</tt> for
	 *         each pair of elements of <tt>iterator1</tt> and
	 *         <tt>iterator2</tt>, otherwise <tt>false</tt>.
	 */
	public static boolean all(Iterator iterator1, Iterator iterator2, Predicate predicate) {
		while (iterator1.hasNext() && iterator2.hasNext())
			if (!predicate.invoke(iterator1.next(), iterator2.next()))
				return false;
		return true;
	}

	/**
	 * Returns <tt>true</tt> if the boolean higher-order function returns
	 * {@link java.lang.Boolean#TRUE true} applied on each set of elements of the
	 * given input iterator array. The set contains one element per iterator and
	 * the function is only applied while all iterators are not empty.
	 *
	 * @param iterators the iterators used to get a set of elements by taking one
	 *        element of each iterator.
	 * @param function the function to be invoked on each set of elements.
	 * @return <tt>true</tt> if the function returns
	 *         {@link java.lang.Boolean#TRUE true} for each set of elements of
	 *         the given input iterator array, otherwise <tt>false</tt>.
	 */
	public static boolean all(Iterator [] iterators, Function function) {
		for (Iterator mapper = new Mapper(iterators, function); mapper.hasNext();)
			if (!Boolean.TRUE.equals(mapper.next()))
				return false;
		return true;
	}

	/**
	 * Returns <tt>true</tt> if the unary, boolean function returns
	 * {@link java.lang.Boolean#TRUE true} for any element of the given iterator.
	 * 
	 * <p><b>Note :</b> The boolean function is internally wrapped to a
	 * predicate.</p>
	 *
	 * @param iterator the iterator to be checked.
	 * @param function the boolean function to be evaluated for the iterator's
	 *        elements.
	 * @return <tt>true</tt> if the function returns
	 *         {@link java.lang.Boolean#TRUE true} for any element of the
	 *         iterator, otherwise <tt>false</tt>.
	 */
	public static boolean any(Iterator iterator, Function function) {
		return any(iterator, new FunctionPredicate(function));
	}

	/**
	 * Returns <tt>true</tt> if the unary predicate returns true for any element
	 * of the given iterator.
	 * 
	 * @param iterator the iterator to be checked.
	 * @param predicate the predicate to be evaluated for the iterator's
	 *        elements.
	 * @return <tt>true</tt> if the function returns true for any element of the
	 *         iterator, otherwise <tt>false</tt>.
	 */
	public static boolean any(Iterator iterator, Predicate predicate) {
		while (iterator.hasNext())
			if(predicate.invoke(iterator.next()))
				return true;
		return false;
	}

	/**
	 * Returns <tt>true</tt> if the given boolean function returns
	 * {@link java.lang.Boolean#TRUE true} for any pair of elements of
	 * <tt>iterator1</tt> and <tt>iterator2</tt>. The comparison is stopped when 
	 * <ul>
	 *     <li>
	 *         one iterator is empty or
	 *     </li>
	 *     <li>
	 *         the function returns {@link java.lang.Boolean#FALSE false}.
	 *     </li>
	 * </ul>
	 * 
	 * <p><b>Note :</b> The boolean function is internally wrapped to a
	 * predicate.</p>
	 *
	 * @param iterator1 the iterator that contains the first element of each
	 *        pair.
	 * @param iterator2 the iterator that contains the second element of each
	 *        pair.
	 * @param function the boolean function to be evaluated for the pairs of the
	 *        iterator's elements.
	 * @return <tt>true</tt> if the function returns
	 *         {@link java.lang.Boolean#TRUE true} for any pair of elements of
	 *         <tt>iterator1</tt> and <tt>iterator2</tt>, otherwise
	 *         <tt>false</tt>.
	 */
	public static boolean any(Iterator iterator1, Iterator iterator2, Function function) {
		return any(iterator1, iterator2, new FunctionPredicate(function));
	}

	/**
	 * Returns <tt>true</tt> if the given boolean function returns true for any
	 * pair of elements of <tt>iterator1</tt> and <tt>iterator2</tt>. The
	 * comparison is stopped when 
	 * <ul>
	 *     <li>
	 *         one iterator is empty or
	 *     </li>
	 *     <li>
	 *         the predicate returns false.
	 *     </li>
	 * </ul>
	 * 
	 * @param iterator1 the iterator that contains the first element of each
	 *        pair.
	 * @param iterator2 the iterator that contains the second element of each
	 *        pair.
	 * @param predicate the predicate to be evaluated for the iterator's
	 *        elements.
	 * @return <tt>true</tt> if the function returns true for any pair of
	 *         elements of <tt>iterator1</tt> and <tt>iterator2</tt>, otherwise
	 *         <tt>false</tt>.
	 */
	public static boolean any(Iterator iterator1, Iterator iterator2, Predicate predicate) {
		while (iterator1.hasNext() && iterator2.hasNext())
			if (predicate.invoke(iterator1.next(), iterator2.next()))
				return true;
		return false;
	}

	/**
	 * Returns <tt>true</tt> if the boolean higher-order function returns
	 * {@link java.lang.Boolean#TRUE true} applied on any set of elements of the
	 * given input iterator array. The set contains one element per iterator and
	 * the function is only applied while all iterators are not empty.
	 *
	 * @param iterators the iterators used to get a set of elements by taking one
	 *        element of each iterator.
	 * @param function the function to be invoked on each set of elements.
	 * @return <tt>true</tt> if the function returns
	 *         {@link java.lang.Boolean#TRUE true} for any set of elements of
	 *         the given input iterator array, otherwise <tt>false</tt>.
	 */
	public static boolean any(Iterator [] iterators, Function function) {
		for (Iterator mapper = new Mapper(iterators, function); mapper.hasNext();)
			if (Boolean.TRUE.equals(mapper.next()))
				return true;
		return false;
	}

	/**
	 * Returns the last element of the given iterator.
	 *
	 * @param iterator the input iterator.
	 * @return the last element of the given iterator.
	 * @throws java.util.NoSuchElementException if the iterator does not contain
	 *         any elements.
	 */
	public static Object last(Iterator iterator) throws NoSuchElementException {
		Object result;
		do
			result = iterator.next();
		while (iterator.hasNext());
		return result;
	}

	/**
	 * Returns the <tt>n</tt>-th element of the given iterator.
	 *
	 * @param iterator the input iterator.
	 * @param n the index of the element to return.
	 * @return the <tt>n</tt>-th element of the given iterator.
	 * @throws java.util.NoSuchElementException if the iterator does not contain
	 *         any elements.
	 */
	public static Object nth(Iterator iterator, int n) throws NoSuchElementException {
		Object result = null;
		for (int i=0; iterator.hasNext() && i<=n; i++) {
			Object object = iterator.next();
			if (i==n)
				result = object;
		}
		return result;
	}

	/**
	 * Returns the first element of the given iterator.
	 *
	 * @param iterator the input iterator.
	 * @return the first element of the given iterator.
	 * @throws java.util.NoSuchElementException if the iterator does not contain
	 *         any elements.
	 */
	public static Object first(Iterator iterator) throws NoSuchElementException {
		return nth(iterator, 0);
	}

	/**
	 * Counts the elements of a specified iterator.
	 *
	 * @param iterator the input iterator.
	 * @return the number of elements of the given iterator.
	 */
	public static int count(Iterator iterator) {
		int size = 0;
		while (iterator.hasNext()) {
			iterator.next();
			size++;
		}
		return size;
	}

	/**
	 * Calls <tt>next</tt> on the iterator until the method <tt>hasNext</tt>
	 * returns <tt>false</tt>.
	 *
	 * @param iterator the input iterator.
	 */
	public static void consume(Iterator iterator) {
		while (iterator.hasNext())
			iterator.next();
	}

	/**
	 * Calls <tt>next</tt> on the iterator until the method <tt>hasNext</tt>
	 * returns <tt>false</tt> and prints the elements of the iterator separated
	 * by a line feed to the standard output stream.
	 *
	 * @param iterator the input iterator.
	 */
	public static void println(Iterator iterator) {
		forEach(iterator, Println.DEFAULT_INSTANCE);
	}

	/**
	 * Calls <tt>next</tt> on the iterator until the method <tt>hasNext</tt>
	 * returns <tt>false</tt> and prints the elements of the iterator to the
	 * standard output stream.
	 *
	 * @param iterator the input iterator.
	 */
	public static void print(Iterator iterator) {
		forEach(iterator, Print.DEFAULT_INSTANCE);
	}

	/**
	 * Calls <tt>next</tt> on the iterator until the method <tt>hasNext</tt>
	 * returns <tt>false</tt> and prints the elements of the iterator separated
	 * by a line feed to the specified print stream.
	 *
	 * @param iterator the input iterator.
	 * @param printStream the print stream the output is delegated to.
	 */
	public static void println(Iterator iterator, PrintStream printStream) {
		forEach(iterator, new Println(printStream));
	}

	/**
	 * Calls <tt>next</tt> on the iterator until the method <tt>hasNext</tt>
	 * returns <tt>false</tt> and prints the elements of the iterator to the
	 * specified print stream.
	 *
	 * @param iterator the input iterator.
	 * @param printStream the print stream the output is delegated to.
	 */
	public static void print(Iterator iterator, PrintStream printStream) {
		forEach(iterator, new Print(printStream));
	}

	/**
	 * Returns a {@link java.util.Map.Entry Map.Entry} object with the minimal
	 * value as its key and the list of objects with this minimal value in the
	 * same order as provided by the iterator as its value.
	 *
	 * @param iterator the objects to be minimized.
	 * @param getFeature the function obtaining the value for each object used to
	 *        compare.
	 * @param comparator the comparator that compares the values obtained by
	 *        <tt>getFeature</tt>.
	 * @return a map entry with the minimal value as its key and the list of
	 *         objects with this minimal value in the same order as provided by
	 *         the iterator as its value.
	 */
	public static Entry minimize(Iterator iterator, Function getFeature, Comparator comparator) {
		return (Entry)last(new Minimator(iterator, getFeature, comparator));
	}

	/**
	 * Returns a {@link java.util.Map.Entry Map.Entry} object with the maximal
	 * value as its key and the list of objects with this maximal value in the
	 * same order as provided by the iterator as its value.
	 *
	 * @param iterator the objects to be maximized.
	 * @param getFeature the function obtaining the value for each object used to
	 *        compare.
	 * @param comparator the comparator that compares the values obtained by
	 *        <tt>getFeature</tt>.
	 * @return a map entry with the maximal value as its key and the list of
	 *         objects with this maximal value in the same order as provided by
	 *         the iterator as its value.
	 */
	public static Entry maximize(Iterator iterator, Function getFeature, Comparator comparator) {
		return (Entry)last(new Minimator(iterator, getFeature, new InverseComparator(comparator)));
	}

	/**
	 * Returns the list of objects having the minimal value in the same order as
	 * provided by the iterator as its value.
	 *
	 * @param iterator the objects to be minimized.
	 * @param getFeature the function obtaining the value for each object used to
	 *        compare.
	 * @param comparator the comparator that compares the values obtained by
	 *        <tt>getFeature</tt>.
	 * @return a linked list of objects having the minimal value in the same
	 *         order as provided by the iterator.
	 */
	public static LinkedList minima(Iterator iterator, Function getFeature, Comparator comparator) {
		return (LinkedList)minimize(iterator, getFeature, comparator).getValue();
	}

	/**
	 * Returns the list of objects having the maximal value in the same order as
	 * provided by the iterator as its value.
	 *
	 * @param iterator the objects to be maximized.
	 * @param getFeature the function obtaining the value for each object used to
	 *        compare.
	 * @param comparator the comparator that compares the values obtained by
	 *        <tt>getFeature</tt>.
	 * @return a linked list of objects having the maximal value in the same
	 *         order as provided by the iterator.
	 */
	public static LinkedList maxima(Iterator iterator, Function getFeature, Comparator comparator) {
		return (LinkedList)maximize(iterator, getFeature, comparator).getValue();
	}

	/**
	 * Returns a {@link java.util.Map.Entry Map.Entry} object with the minimal
	 * value as its key and the list of objects with this minimal value in the
	 * same order as provided by the iterator as its value. For being able to use
	 * a default {@link xxl.core.comparators.ComparableComparator comparator} all
	 * elements of the iteration must implement the interface
	 * {@link java.lang.Comparable}.
	 *
	 * @param iterator the objects to be minimized.
	 * @param getFeature the function obtaining the value for each object used to
	 *        compare.
	 * @return a map entry with the minimal value as its key and the list of
	 *         objects with this minimal value in the same order as provided by
	 *         the iterator as its value.
	 */
	public static Entry minimize(Iterator iterator, Function getFeature) {
		return (Entry)last(new Minimator(iterator, getFeature));
	}

	/**
	 * Returns a {@link java.util.Map.Entry Map.Entry} object with the maximal
	 * value as its key and the list of objects with this maximal value in the
	 * same order as provided by the iterator as its value. For being able to use
	 * a default {@link xxl.core.comparators.ComparableComparator comparator} all
	 * elements of the iteration must implement the interface
	 * {@link java.lang.Comparable}.
	 *
	 * @param iterator the objects to be maximized.
	 * @param getFeature the function obtaining the value for each object used to
	 *        compare.
	 * @return a map entry with the maximal value as its key and the list of
	 *         objects with this maximal value in the same order as provided by
	 *         the iterator as its value.
	 */
	public static Entry maximize(Iterator iterator, Function getFeature) {
		return (Entry)last(new Minimator(iterator, getFeature, InverseComparator.DEFAULT_INSTANCE));
	}

	/**
	 * Returns the list of objects having the minimal value in the same order as
	 * provided by the iterator as its value. For being able to use a default
	 * {@link xxl.core.comparators.ComparableComparator comparator} all elements
	 * of the iteration must implement the interface
	 * {@link java.lang.Comparable}.
	 *
	 * @param iterator the objects to be minimized.
	 * @param getFeature the function obtaining the value for each object used to
	 *        compare.
	 * @return a linked list of objects having the minimal value in the same
	 *         order as provided by the iterator.
	 */
	public static LinkedList minima(Iterator iterator, Function getFeature) {
		return (LinkedList)minimize(iterator, getFeature).getValue();
	}

	/**
	 * Returns the list of objects having the maximal value in the same order as
	 * provided by the iterator as its value. For being able to use a default
	 * {@link xxl.core.comparators.ComparableComparator comparator} all elements
	 * of the iteration must implement the interface
	 * {@link java.lang.Comparable}.
	 *
	 * @param iterator the objects to be maximized.
	 * @param getFeature the function obtaining the value for each object used to
	 *        compare.
	 * @return a linked list of objects having the maximal value in the same
	 *         order as provided by the iterator.
	 */
	public static LinkedList maxima(Iterator iterator, Function getFeature) {
		return (LinkedList)maximize(iterator, getFeature).getValue();
	}

	/**
	 * Returns a {@link java.util.Map.Entry Map.Entry} object with the minimal
	 * value as its key and the list of objects with this minimal value in the
	 * same order as provided by the iterator as its value.
	 *
	 * @param iterator the objects to be minimized.
	 * @param comparator the comparator that compares the objects.
	 * @return a map entry with the minimal value as its key and the list of
	 *         objects with this minimal value in the same order as provided by
	 *         the iterator as its value.
	 */
	public static Entry minimize(Iterator iterator, Comparator comparator) {
		return (Entry)last(new Minimator(iterator, comparator));
	}

	/**
	 * Returns a {@link java.util.Map.Entry Map.Entry} object with the maximal
	 * value as its key and the list of objects with this maximal value in the
	 * same order as provided by the iterator as its value.
	 *
	 * @param iterator the objects to be maximized.
	 * @param comparator the comparator that compares the objects.
	 * @return a map entry with the maximal value as its key and the list of
	 *         objects with this maximal value in the same order as provided by
	 *         the iterator as its value.
	 */
	public static Entry maximize(Iterator iterator, Comparator comparator) {
		return (Entry)last(new Minimator(iterator, new InverseComparator(comparator)));
	}

	/**
	 * Returns the list of objects having the minimal value in the same order as
	 * provided by the iterator as its value.
	 *
	 * @param iterator the objects to be minimized.
	 * @param comparator the comparator that compares the objects.
	 * @return a linked list of objects having the minimal value in the same
	 *         order as provided by the iterator.
	 */
	public static LinkedList minima(Iterator iterator, Comparator comparator) {
		return (LinkedList)minimize(iterator, comparator).getValue();
	}

	/**
	 * Returns the list of objects having the maximal value in the same order as
	 * provided by the iterator as its value.
	 *
	 * @param iterator the objects to be maximized.
	 * @param comparator the comparator that compares the objects.
	 * @return a linked list of objects having the maximal value in the same
	 *         order as provided by the iterator.
	 */
	public static LinkedList maxima(Iterator iterator, Comparator comparator) {
		return (LinkedList)maximize(iterator, comparator).getValue();
	}

	/**
	 * Returns a {@link java.util.Map.Entry Map.Entry} object with the minimal
	 * value as its key and the list of objects with this minimal value in the
	 * same order as provided by the iterator as its value. For being able to use
	 * a default {@link xxl.core.comparators.ComparableComparator comparator} all
	 * elements of the iteration must implement the interface
	 * {@link java.lang.Comparable}.
	 *
	 * @param iterator the objects to be minimized.
	 * @return a map entry with the minimal value as its key and the list of
	 *         objects with this minimal value in the same order as provided by
	 *         the iterator as its value.
	 */
	public static Entry minimize(Iterator iterator) {
		return (Entry)last(new Minimator(iterator));
	}

	/**
	 * Returns a {@link java.util.Map.Entry Map.Entry} object with the maximal
	 * value as its key and the list of objects with this maximal value in the
	 * same order as provided by the iterator as its value. For being able to use
	 * a default {@link xxl.core.comparators.ComparableComparator comparator} all
	 * elements of the iteration must implement the interface
	 * {@link java.lang.Comparable}.
	 *
	 * @param iterator the objects to be maximized.
	 * @return a map entry with the maximal value as its key and the list of
	 *         objects with this maximal value in the same order as provided by
	 *         the iterator as its value.
	 */
	public static Entry maximize(Iterator iterator) {
		return (Entry)last(new Minimator(iterator, InverseComparator.DEFAULT_INSTANCE));
	}

	/**
	 * Returns the list of objects having the minimal value in the same order as
	 * provided by the iterator as its value. For being able to use a default
	 * {@link xxl.core.comparators.ComparableComparator comparator} all
	 * elements of the iteration must implement the interface
	 * {@link java.lang.Comparable}.
	 *
	 * @param iterator the objects to be minimized.
	 * @return a linked list of objects having the minimal value in the same
	 *         order as provided by the iterator.
	 */
	public static LinkedList minima(Iterator iterator) {
		return (LinkedList)minimize(iterator).getValue();
	}

	/**
	 * Returns the list of objects having the maximal value in the same order as
	 * provided by the iterator as its value. For being able to use a default
	 * {@link xxl.core.comparators.ComparableComparator comparator} all elements
	 * of the iteration must implement the interface
	 * {@link java.lang.Comparable}.
	 *
	 * @param iterator the objects to be maximized.
	 * @return a linked list of objects having the maximal value in the same
	 *         order as provided by the iterator.
	 */
	public static LinkedList maxima(Iterator iterator) {
		return (LinkedList)maximize(iterator).getValue();
	}

	/**
	 * Removes all elements of the given iterator.
	 *
	 * @param iterator the input iterator.
	 */
	public static void removeAll(Iterator iterator) {
		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
	}

	/**
	 * Removes the elements of the given iterator for that the specified
	 * predicate returns <tt>true</tt>.
	 *
	 * @param iterator the input iterator.
	 * @param predicate a predicate that decides whether an element is removed
	 *        or not.
	 */
	public static void removeAll(Iterator iterator, Predicate predicate) {
		while (iterator.hasNext())
			if (predicate.invoke(iterator.next()))
				iterator.remove();
	}

	/**
	 * Removes the first element of the given iterator for that the specified
	 * predicate returns <tt>true</tt>.
	 *
	 * @param iterator the input iterator.
	 * @param predicate a predicate that decides whether an element is removed
	 *        or not.
	 * @return true if and only if an element was removed.
	 */
	public static boolean removeFirst(Iterator iterator, Predicate predicate) {
		while (iterator.hasNext())
			if (predicate.invoke(iterator.next())) {
				iterator.remove();
				return true;
			}
		return false;
	}

	/**
	 * Updates all elements of a cursor.
	 *
	 * @param cursor the input cursor.
	 * @param objects an iterator with objects to be used for the
	 *        <tt>update</tt> operation.
	 */
	public static void updateAll(Cursor cursor, Iterator objects) {
		while (cursor.hasNext() && objects.hasNext()) {
			cursor.next();
			cursor.update(objects.next());
		}
	}

	/**
	 * Updates all elements of a cursor with the result of a given, unary
	 * function.
	 *
	 * @param cursor the input cursor.
	 * @param function the unary function used to compute the object for the
	 *        <tt>update</tt> operation.
	 */
	public static void updateAll(Cursor cursor, Function function) {
		while (cursor.hasNext())
			cursor.update(function.invoke(cursor.next()));
	}

	/**
	 * Adds the elements of the given iteration to a specified
	 * {@link java.util.Collection collection}.
	 *
	 * @param iterator the input iterator.
	 * @param collection the collection into which the elements of the given
	 *        iteration are to be stored.
	 * @return the specified collection containing the elements of the iteration.
	 */
	public static Collection toCollection(Iterator iterator, Collection collection) {
		while (iterator.hasNext())
			collection.add(iterator.next());
		return collection;
	}

	/**
	 * Adds the elements of the given iteration to a specified
	 * {@link java.util.List list}.
	 *
	 * @param iterator the input iterator.
	 * @param list the list into which the elements of the given iteration are
	 *        to be stored.
	 * @return the specified list containing the elements of the iteration.
	 */
	public static List toList(Iterator iterator, List list) {
		return (List)toCollection(iterator, list);
	}

	/**
	 * Adds the elements of the given iteration to a 
	 * {@link java.util.LinkedList linked list} and returns it.
	 *
	 * @param iterator the input iterator.
	 * @return the linked list containing the elements of the iteration.
	 */
	public static List toList(Iterator iterator) {
		return toList(iterator, new LinkedList());
	}

	/**
	 * Converts an iterator to an object array whose length is equal to the
	 * number of the iterator's elements.
	 *
	 * @param iterator the input iterator.
	 * @return an object array containing the iterator's elements.
	 */
	public static Object[] toArray(Iterator iterator) {
		ArrayResizer resizer = new ArrayResizer(0.5, 0);
		Object [] array = new Object[0];
		int size = array.length;
		while (iterator.hasNext())
			(array = resizer.resize(array, ++size))[size-1] = iterator.next();
		return new ArrayResizer(1, 1).resize(array, size);
	}

	/**
	 * Converts an iterator to an object array whose length is determined by the
	 * largest index. Stops as soon as one iterator is exhausted.
	 *
	 * @param iterator the input iterator.
	 * @param indices each index determines where to store the corresponding
	 *        iterator element.
	 * @return an object array of the iterator's elements following the order
	 *         defined by the iterator <tt>indices</tt>.
	 */
	public static Object[] toArray(Iterator iterator, Iterator indices) {
		ArrayResizer resizer = new ArrayResizer(0.5, 0);
		Object [] array = new Object[0];
		int size = array.length;
		for (int index; iterator.hasNext() && indices.hasNext(); array[index] = iterator.next())
			if ((index = ((Integer)indices.next()).intValue())>=size)
				array = resizer.resize(array, size = index+1);
		return new ArrayResizer(1, 1).resize(array, size);
	}

	/**
	 * Converts an iterator to an object array using the given one. The order
	 * is defined by the given indices iterator. Stops as soon as one iterator
	 * is exhausted. Throws an {@link java.lang.ArrayIndexOutOfBoundsException ArrayIndexOutOfBoundsException}
	 * if the given array is not long enough.
	 *
	 * @param iterator the input iterator.
	 * @param array the array into which the elements of the given iteration are
	 *        to be stored.
	 * @param indices each index determines where to store the corresponding
	 *        iterator element.
	 * @return the object array containing the iterator's elements following the
	 *         order defined by the iterator <tt>indices</tt>.
	 * @throws java.lang.ArrayIndexOutOfBoundsException if the given array is not
	 *         long enough.
	 */
	public static Object[] toArray(Iterator iterator, Object[] array, Iterator indices) {
		while (iterator.hasNext() && indices.hasNext())
			array[((Integer)indices.next()).intValue()] = iterator.next();
		return array;
	}

	/**
	 * Converts an iterator to an object array using the given one. Throws an
	 * {@link java.lang.ArrayIndexOutOfBoundsException ArrayIndexOutOfBoundsException}
	 * if the number of the iterator's elements is larger than the length of the
	 * given array.
	 *
	 * @param iterator the input iterator.
	 * @param array the array into which the elements of the given iteration are
	 *        to be stored.
	 * @return the object array containing the iterator's elements.
	 * @throws java.lang.ArrayIndexOutOfBoundsException if the given array is not
	 *         long enough.
	 */
	public static Object[] toArray(Iterator iterator, Object[] array) {
		for (int index = 0; iterator.hasNext() && index < array.length; array[index++] = iterator.next());
		return array;
	}

	/**
	 * Converts an iterator to a {@link java.util.Map map}. Partitions the
	 * iterator's elements according to the result of the unary function
	 * <tt>getKey</tt>. Each partition is the value of its corresponding map
	 * entry and is implemented as a collection.
	 *
	 * @param iterator the input iterator.
	 * @param getKey unary function that determines the key for an iterator's
	 *        element.
	 * @param map the map used to store the partitions.
	 * @param newCollection creates a new collection to store the elements of a
	 *        new partition.
	 * @return the map implementing the partition of the iterator's elements.
	 */
	public static Map toMap(Iterator iterator, Function getKey, Map map, Function newCollection) {
		while (iterator.hasNext()) {
			Object next = iterator.next();
			Object key = getKey.invoke(next);
			Collection collection = (Collection)map.get(key);
			if (collection == null)
				map.put(key, collection = (Collection)newCollection.invoke());
			collection.add(next);
		}
		return map;
	}

	/**
	 * Converts an iterator to a {@link java.util.Map map}. Partitions the
	 * iterator's elements according to the result of the unary function
	 * <tt>getKey</tt>. Each partition is the value of its corresponding map
	 * entry and is implemented as a {@link java.util.LinkedList linked list}.
	 *
	 * @param iterator the input iterator.
	 * @param getKey unary function that determines the key for an iterator's
	 *        element.
	 * @param map the map used to store the partitions.
	 * @return the map implementing the partition of the iterator's elements.
	 */
	public static Map toMap(Iterator iterator, Function getKey, Map map) {
		return toMap(iterator, getKey, map, new Function () {
			public Object invoke () {
				return new LinkedList();
			}
		});
	}

	/**
	 * Converts an iterator to a {@link java.util.HashMap hash map}. Partitions
	 * the iterator's elements according to the result of the unary function
	 * <tt>getKey</tt>. Each partition is the value part of its corresponding map
	 * entry and is implemented as a collection.
	 *
	 * @param iterator the input iterator.
	 * @param getKey unary function that determines the key for an iterator's
	 *        element.
	 * @param newCollection creates a new collection to store the elements of a
	 *        new partition.
	 * @return the map implementing the partition of the iterator's elements.
	 */
	public static Map toMap(Iterator iterator, Function getKey, Function newCollection) {
		return toMap(iterator, getKey, new HashMap(), newCollection);
	}

	/**
	 * Converts an iterator to a {@link java.util.HashMap hash map}. Partitions
	 * the iterator's elements according to the result of the function
	 * <tt>getKey</tt>. Each partition is the value part of its corresponding map
	 * entry and is implemented as a {@link java.util.LinkedList linked list}.
	 *
	 * @param iterator the input iterator.
	 * @param getKey unary function that determines the key for an iterator's
	 *        element.
	 * @return the map implementing the partition of the iterator's elements.
	 */
	public static Map toMap(Iterator iterator, Function getKey) {
		return toMap(iterator, getKey, new HashMap());
	}

	/**
	 * Wraps the given {@link java.util.Iterator iterator} to a
	 * {@link xxl.core.cursors.Cursor cursor}. If the given iterator is already
	 * a cursor, it is returned unchanged. The current implementation is as
	 * follows:
	 * <pre>
	 *     return iterator instanceof Cursor ?
	 *         (Cursor)iterator :
	 *         new IteratorCursor(iterator);
	 * </pre>
	 *
	 * @param iterator the iterator to be wrapped.
	 * @return a cursor wrapping the given iterator.
	 * @see xxl.core.cursors.wrappers.IteratorCursor
	 */
	public static Cursor wrap(Iterator iterator) {
		return iterator instanceof Cursor ?
			(Cursor)iterator :
			new IteratorCursor(iterator);
	}

	/**
	 * Wraps a given {@link java.util.Iterator iterator} (keep in mind that a
	 * {@link xxl.core.cursors.Cursor cursor} is also an iterator) to a
	 * {@link xxl.core.cursors.MetaDataCursor meta data cursor}. In that way an
	 * ordinary iterator can be enriched by meta data that is provided via the
	 * {@link xxl.core.cursors.MetaDataCursor#getMetaData() getMetaData} method.
	 *
	 * @param iterator the iterator to be enriched by meta data information.
	 * @param metaData an object containing the meta data information.
	 * @return a meta data cursor enriching the given iterator by some meta data
	 *         information.
	 */
	public static MetaDataCursor wrapToMetaDataCursor(Iterator iterator, final Object metaData) {
		return new AbstractMetaDataCursor(iterator) {
			public Object getMetaData() {
				return metaData;
			}
		};
	}

	/**
	 * The main method contains some examples to demonstrate the usage and the
	 * functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main(String[] args) {

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		
		Cursor cursor = new xxl.core.cursors.sources.Enumerator(11);
		cursor.open();
		
		System.out.println("First element:"+ Cursors.first(cursor));
		
		cursor.reset();
		System.out.println("Third element: "+ Cursors.nth(cursor, 3));
		
		cursor.reset();
		System.out.println("Last element: "+ Cursors.last(cursor));
		
		cursor.reset();
		System.out.println("Length: "+ Cursors.count(cursor));
		
		cursor.close();

		/*********************************************************************/
		/*                            Example 2                              */
		/*********************************************************************/
		
		Map.Entry entry = Cursors.maximize(
			cursor = new xxl.core.cursors.sources.RandomIntegers(100, 50),
			new Function() {
				public Object invoke(Object object) {
					return new Integer(((Integer)object).intValue());
				}
			}
		);
		System.out.println("Maximal value is : " + entry.getKey());
		
		Iterator iterator = ((LinkedList)entry.getValue()).listIterator(0);
		System.out.print("Maxima : ");
		while(iterator.hasNext())
			System.out.print(iterator.next() + "; ");
		System.out.flush();
		
		cursor.close();

		/*********************************************************************/
		/*                            Example 3                              */
		/*********************************************************************/
		
		Cursors.forEach(
			cursor = new xxl.core.cursors.sources.Enumerator(11),
			new Function() {
				public Object invoke(Object object) {
					Object result = new Integer((int)Math.pow(((Integer)object).intValue(), 2));
					System.out.println("Number : " + object + " ; Number^2 : " + result);
					return result;
				}
			}
		);
		cursor.close();

		/*********************************************************************/
		/*                            Example 4                              */
		/*********************************************************************/
		
		System.out.println(
			"Is the number '13' contained in the RandomIntegers' cursor: " +
			Cursors.any(
				cursor = new xxl.core.cursors.sources.RandomIntegers(1000, 200),
				new Predicate() {
					public boolean invoke(Object object) {
						return ((Integer)object).intValue() == 13;
					}
				}
			)
		);
		cursor.close();
	}
}
