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

package xxl.core.util;

import java.lang.reflect.Array;
import java.lang.System;

/**
 * The ArrayResizer provides methods that allow to modify the size of a
 * given array after its construction. <br>
 * An ArrayResizer is especially useful with regard to dynamic structures based
 * on arrays, because this class supports methods to expanded or shrink an array
 * depending on user specified parameters.<p>
 * Therefore the user has to consider these two kinds of sizes of an array:
 * <ul>
 * <li><b>logical size</b>: a user specified size that represents the minimum needed size
 * 			of the array after the resize-operation.</li>
 * <li><b>physical size</b>: the actual returned size of a given array after a resize-
 *			operation.</li>
 * </ul>
 * An instance of ArrayResizer can be created with the parameters <tt>fmin</tt>,
 * <tt>fover</tt>, <tt>funder</tt>, which refer to these two sizes of an array.<p>
 * <b>IMPORTANT:</b><br>
 * These parameters have to be specified with a value in a range of [0,1]! (per cent values)
 * <ul>
 * <li><tt><b>fmin</b></tt> is the minimal utilization of capacity concerning
 *		 the ratio between logical and physical size after an expansion or
 *		 contraction of an array.
 *		 The following condition is valid:
 *		 logical size * fmin <= physical size * fmin <= logical size
 * 		 <ul>
 * 		<li><b>expansion:</b><br>
 * 			<pre>	fmin = 0: logical size = physical size * fover </pre><br>
 * 			<pre>	fmin = 1: logical size = physical size <br></pre></li>
 * 		<li><b>contraction:</b><br>
 * 			<pre>	fmin = 0: logical size = physical size * funder </pre><br>
 * 			<pre>	fmin = 1: logical size = physical size </pre><br></li>
 * </ul></li>
 * <li><tt><b>fover</b></tt> is a parameter specifying the array's logical size
 * 		in proportion to the array's physical size after an expansion. <br>
 * 		<pre>	fover = 0: logical size = fmin * physical size </pre><br>
 * 		<pre>	fover = 1: logical size = physical size </pre><br></li>
 * <li><tt><b>funder</b></tt> is a parameter specifying the array's logical size
 * 		in proportion to the array's physical size after a contraction. <br>
 * 		<pre>	funder = 0: logical size = fmin * physical size </pre><br>
 * 		<pre>	funder = 1: logical size = physical size </pre><br></li>
 * </ul><p>
 * To resize a given (Object or primitive type) array, one of the resize-methods
 * of this class has to be called. Therefore the array to be resized and the minimum
 * <tt>size</tt> (logical size) the array should have afer a contraction
 * or an expanison has to be specified.
 * Depending on this parameter <tt>size</tt> (logical size) and the array's length the given
 * array is expanded, i.e. the method <code>grow(array, size)</code> is called,
 * or shrinked, i.e.<code>shrink(array, size)</code> is called.<br>
 * <b>Note:</b> The specified parameter <tt>size</tt> represents the minimum size (logical size)
 * an array can have after a resize-operation. The actual physical size after a resize-
 * operation is determined by the parameters <tt>fover</tt> (grow-operation) and <tt>funder</tt>
 * (shrink-operation) in the following way:<br>
 * <ul>
 * <li>	grow: returned size = (int)(size / (fover+(1-fover)*fmin))
 * <li>	shrink: returned size = (int) (size / (funder+(1-funder)*fmin))
 * </ul>
 * So the following <b>invariant conditions</b> are still valid:
 * <ul>
 * <li>	<b>grow</b>:
 * 		<pre>	<tt>logical size * fmin <= physical size * fmin <= logical size</tt></pre><br>
 * 		<pre>	<tt>logical size * fover <= physical size * fover <= logical size</tt></pre></li>
 * <li>	<b>shrink</b>:
 * 		<pre>	<tt>logical size * fmin <= physical size * fmin <= logical size</tt></pre><br>
 * 		<pre>	<tt>logical size * funder <= physical size * funder <= logical size</tt></pre></li>
 * </ul><p>
 *
 * Further this class provides some useful methods to copy arrays and contains
 * a static field DEFAULT_INSTANCE, which is similar to the design pattern, named Singleton except
 * that there are no mechanisms to avoid the creation of other instances. <br>
 * <b>Intent:</b><br>
 * "Ensure a class only has one instance, and provide a global point of access to it."<br>
 * For further information see: "Gamma et al.: <i>DesignPatterns</i>. Addision Wesley 1998."
 * <p>
 *
 * <b>Example usage (1):</b>
 * <br><br>
 * <code><pre>
 * 	ArrayResizer arrayResizer = ArrayResizer.DEFAULT_INSTANCE; // get a default instance
 * 	int[] ints = new int[10]; // use a primitive type array
 * 	int size = 20; // minimal needed size (logical size) --> grow
 * 	int[] resizedArray = (int[])arrayResizer.resize(ints, size); // resize-call
 * </code></pre>
 * This examples creates a new default instance of this class and calls the <tt>resize</tt>
 * method for a primitive type (int) array. By using a default instance of this class,
 * the fields <tt>fmin</tt>, <tt>fover</tt>, <tt>funder</tt> are initialized with '0.5'.<br>
 * The logical size is set to '20'.<br>
 * The initial array size is '10' so the ArrayResizer calls <tt>grow(ints, 20)</tt>.
 * So the returned size, i.e. the physical size, is '26', because <br>
 * <pre>
 * 	(grow: <code>return (int)(size / (fover+(1-fover)*fmin)</code>) <br>
 * 	==> (int)(20/(0.5+(1-0.5)*0.5) = 26 <br>
 * </pre><p>
 *
 * <b>Example usage (2):</b>
 * <br><br>
 * <code><pre>
 * 	arrayResizer = new ArrayResizer(0.2); // create new ArrayResizer with fmin = 0.2
 * 	ints = new int[100]; // use a primitive type array
 * 	size = 10; // minimal needed size (logical size) --> shrink
 * 	resizedArray = (int[])arrayResizer.resize(ints, size); // resize-call
 * </code></pre>
 * In this case a new instance of ArrayResizer is created by specifying
 * <tt>fmin</tt> with value '0.2'. The other fields <tt>fover</tt> and <tt>fmin</tt>
 * are set to '0.5'. Because the array has an initial array size of '100' and the
 * logical size is specified with '10' the array has to be shrinked. Therefore the
 * <tt>resize</tt> method calls <code>shrink(ints, 10)</code>.<br>
 * Because of <tt>fmin</tt> the physical array size has to be 20 per cent larger (or even more)
 * than the logical array size. So the actual physical size is determined by: <br>
 * 	(shrink: <code>return (int)(size / (funder+(1-funder)*fmin)</code>) <br>
 * So the returned physical size is '16'.<p>
 *
 * <b>Example usage (3):</b>
 * <br><br>
 * <code><pre>
 * 	System.out.println("------------------EXAMPLE 3-------------------");
 * 	// create new ArrayResizer with fmin = 0.2, fover = 0.6 and funder = 0.8
 * 	arrayResizer = new ArrayResizer(0.2, 0.6, 0.8);
 * 	// use an Object array
 * 	Integer[] objects = new Integer[]{new Integer(0), new Integer(1), new Integer(2)};
 * 	size = 10; // minimal needed size (logical size) --> grow
 * 	Integer[] resizedObjectArray = (Integer[])arrayResizer.resize(objects, size); // resize-call
 * 	System.out.println("initial array size:" +objects.length);
 * 	System.out.println("logical size:  " +size);
 * 	System.out.println("physical size: " +resizedObjectArray.length +" (returned size!)");
 * 	System.out.println("fover:         " +arrayResizer.fover);
 * 	System.out.println("funder:        " +arrayResizer.funder);
 * 	System.out.println("fmin:          " +arrayResizer.fmin);
 * 	System.out.println("logical size * fover:    " +size*arrayResizer.fover);
 * 	System.out.println("logical size * funder:   " +size*arrayResizer.funder);
 * 	System.out.println("logical size * fmin:     " +size*arrayResizer.fmin);
 * 	System.out.println("physical size * fover:    " +resizedArray.length*arrayResizer.fover);
 * 	System.out.println("physical size * funder:   " +resizedArray.length*arrayResizer.funder);
 * 	System.out.println("physical size * fmin:    " +(int)(resizedObjectArray.length*arrayResizer.fmin));
 * 	System.out.println("NOTE the following conditions:");
 * 	System.out.println("general:          logical size * fmin <= physical size * fmin <= logical size");
 * 	System.out.println("grow-operation:   logical size * fover <= physical size * fover <= logical size");
 * 	System.out.println("shrink-operation: logical size * funder <= physical size * funder <= logical size");
 * 	System.out.println();
 * </code></pre>
 * This last example demonstrates the most complex constructor that can be used to create an
 * an instance of ArrayResizer. Further it demonstrates the usage of Object-type arrays
 * concerning the <tt>resize</tt> method. The initial array size is '3', therefore an expansion
 * has to be made to gain a minimum logical size of '10'. <br>
 * Further the output shows the used sizes, the used parameters and their relations, i.e. the
 * invariant conditions.<p>
 *
 * Every example shown above generates an output like example (3) if you run the
 * use cases.
 *
 * @see java.lang.reflect.Array
 * @see java.lang.System#arraycopy(Object,int,Object,int,int)
 *
 * @see xxl.core.collections.queues.ArrayQueue
 * @see xxl.core.collections.queues.DynamicHeap
 */
public class ArrayResizer {

	/**
	 * <tt>fmin</tt> is the minimal utilization of capacity concerning
	 * the ratio between logical and physical size after an expansion or
	 * contraction of an array.
	 * The following condition is valid:
	 * 	logical size * fmin <= physical size * fmin <= logical size
	 * <ul>
	 * <li><b>expansion:</b><br>
	 * 	<pre>	fmin = 0: logical size = physical size * fover </pre><br>
 	 * 	<pre>	fmin = 1: logical size = physical size </pre><br></li>
 	 * <li><b>contraction:</b><br>
 	 *	<pre>	fmin = 0: logical size = physical size * funder </pre><br>
 	 * 	<pre>	fmin = 1: logical size = physical size </pre><br></li>
 	 * </ul>
	 */
	protected double fmin; // minimaler F’llgrad

	/**
	 * <tt>fover</tt> is a parameter specifying the array's logical size
	 * in proportion to the array's physical size after an expansion. <br>
	 * 	<pre>	fover = 0: logical size = fmin * physical size </pre><br>
	 * 	<pre>	fover = 1: logical size = physical size </pre><br>
	 */
	protected double fover;

	/**
	 * <tt>funder</tt> is a parameter specifying the array's logical size
	 * in proportion to the array's physical size after a contraction. <br>
	 * 	<pre>	funder = 0: logical size = fmin * physical size </pre><br>
	 * 	<pre>	funder = 1: logical size = physical size </pre><br>
	 */
	protected double funder;

	/**
	 * This instance can be used for getting a default instance of
	 * ArrayResizer. It is similar to the <i>Singleton Design Pattern</i>
	 * (for further details see Creational Patterns, Singleton in
	 * <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of ArrayResizer.
	 */
	public static final ArrayResizer DEFAULT_INSTANCE = new ArrayResizer();

	/**
	 * Creates a new ArrayResizer using a default value of <tt>fmin = 0.5</tt>.
	 */
	public ArrayResizer () {
		this(0.5);
	}

	/**
	 * Creates a new ArrayResizer using default values of <tt>fover = funder = 0.5</tt>.
	 *
	 * @param fmin is the minimal utilization of capacity concerning
	 * 		the ratio between logical and physical size after an expansion or
	 * 		contraction of an array.
	 * 		<ul>
	 * 		<li><b>expansion:</b><br>
	 * 			<pre>	fmin = 0: logical size = physical size * fover </pre><br>
 	 * 			<pre>	fmin = 1: logical size = physical size </pre><br></li>
 	 * 		<li><b>contraction:</b><br>
 	 *			<pre>	fmin = 0: logical size = physical size * funder </pre><br>
 	 * 			<pre>	fmin = 1: logical size = physical size </pre><br></li>
 	 * 		</ul>
	 */
	public ArrayResizer (double fmin) {
		this(fmin, 0.5, 0.5);
	}

	/**
	 * Creates a new ArrayResizer by initializing <tt>fover</tt> and
	 * <tt>funder</tt> with the same given value <tt>f</tt>.
	 *
	 * @param fmin is the minimal utilization of capacity concerning
	 * 		the ratio between logical and physical size after an expansion or
	 * 		contraction of an array.
	 * 		<ul>
	 * 		<li><b>expansion:</b><br>
	 * 			<pre>	fmin = 0: logical size = physical size * fover </pre><br>
 	 * 			<pre>	fmin = 1: logical size = physical size </pre><br></li>
 	 * 		<li><b>contraction:</b><br>
 	 *			<pre>	fmin = 0: logical size = physical size * funder </pre><br>
 	 * 			<pre>	fmin = 1: logical size = physical size </pre><br></li>
 	 * 		</ul>
	 * @param f parameter specifying the array's logical size in proportion to the array's
	 * 		physical size after an expansion or contraction. Thats means <tt>fover</tt> and
	 * 		<tt>funder</tt> are initialized with the same value <tt>f</tt>.
	 */
	public ArrayResizer (double fmin, double f) {
		this(fmin, f, f);
	}

	/**
	 * Creates a new ArrayResizer.
	 *
	 * @param fmin is the minimal utilization of capacity concerning
	 * 		the ratio between logical and physical size after an expansion or
	 * 		contraction of an array.
	 * 		<ul>
	 * 		<li><b>expansion:</b><br>
	 * 			<pre>	fmin = 0: logical size = physical size * fover </pre><br>
 	 * 			<pre>	fmin = 1: logical size = physical size </pre><br></li>
 	 * 		<li><b>contraction:</b><br>
 	 *			<pre>	fmin = 0: logical size = physical size * funder </pre><br>
 	 * 			<pre>	fmin = 1: logical size = physical size </pre><br></li>
 	 * 		</ul>
	 * @param fover a parameter specifying the array's logical size
	 * 		in proportion to the array's physical size after an expansion. <br>
	 * 		<pre>	fover = 0: logical size = fmin * physical size </pre><br>
	 * 		<pre>	fover = 1: logical size = physical size </pre><br>
	 * @param funder a parameter specifying the array's logical size
	 * 		in proportion to the array's physical size after a contraction. <br>
	 * 		<pre>	funder = 0: logical size = fmin * physical size <br>
	 * 		<pre>	funder = 1: logical size = physical size <br>
	 */
	public ArrayResizer (double fmin, double fover, double funder) {
		this.fmin = fmin;
		this.fover = fover;
		this.funder = funder;
	}

	/**
	 * Returns a new array with a minimum size of <tt>size</tt> after the expansion.
	 * This parameter <tt>size</tt> is the logical size computed with.
	 * Creates a new array with the specified component type and a new physical size
	 * that is computed by:
	 * <br><br>
	 * <code><pre>
	 * 	(int)(size/(fover+(1-fover)*fmin))
	 * </code></pre>
	 * <b>Note:</b> <tt>fover</tt> is a parameter specifying the array's logical size
	 * in proportion to the array's physical size after an expansion.
	 * When <tt>fover = 0 </tt> then an array is created with a new physical size of
	 * <tt>size / fmin</tt>, i.e. <tt>logical size = fmin * physical size</tt>.
	 * When <tt>fover = 1</tt> then an array is created were the logical size is equal to
	 * the physical size.
	 * To guarantee the <b>invariant conditions</b>
	 * <pre>
	 * 	<tt>logical size * fmin <= physical size * fmin <= logical size</tt>
	 * 	<tt>logical size * fover <= physical size * fover <= logical size</tt>
	 * </pre>
	 * the parameter <tt>size</tt> has not only to be divided by <tt>fover</tt> but by the
	 * complement of <tt>fover</tt> multiplied by <tt>fmin</tt>.
	 *
	 * Creates a new instance of an array by calling
	 * {@link java.lang.reflect.Array#newInstance(Class, int)}.
	 *
	 * @param array the array to be expanded.
	 * @param size the minimal needed size of the array after an expansion (logical size).
	 * @return a new array with a physical size computed by <code>(int)(size/(fover+(1-fover)*fmin))</code>.
	 * @throws NullPointerException if the specified
	 * 		<code>array</code> parameter is null.
	 * @throws NegativeArraySizeException if the specified <code>length</code>
	 * 		is negative.
	 */
	public Object grow (Object array, int size) {
		return Array.newInstance(array.getClass().getComponentType(), (int)(size/(fover+(1-fover)*fmin)));
	}

	/**
	 * Returns a new array with a minimum size of <tt>size</tt> after the contraction.
	 * This parameter <tt>size</tt> is the logical size computed with.
	 * Creates a new array with the specified component type and a new physical size that is
	 * computed by:
	 * <br><br>
	 * <code><pre>
	 * 	(int)(size/(funder+(1-funder)*fmin))
	 * </code></pre>
	 * <b>Note:</b> <tt>funder</tt> is a parameter specifying the array's logical size
	 * in proportion to the array's physical size after a contraction.
	 * When <tt>funder = 0 </tt> then an array is created with a new physical size of
	 * <tt>size / fmin</tt>, i.e. <tt>logical size = fmin * physical size</tt>.
	 * When <tt>funder = 1</tt> then an array is created were the logical size is equal to
	 * the physical size.
	 * To guarantee the <b>invariant conditions</b>
	 * <pre>
	 * 	<tt>logical size * fmin <= physical size * fmin <= logical size</tt>
	 * 	<tt>logical size * funder <= physical size * funder <= logical size</tt>
	 * </pre>
	 * the parameter size has not only to be divided by <tt>funder</tt> but by the
	 * complement of <tt>funder</tt> multiplied by <tt>fmin</tt>.
	 *
	 * Creates a new instance of an array by calling
	 * {@link java.lang.reflect.Array#newInstance(Class, int)}.
	 *
	 * @param array the array to be shrinked.
	 * @param size the minimal needed size of the array after a contraction (logical size).
	 * @return a new array with a physical size computed by <code>(int)(size/(funder+(1-funder)*fmin))</code>.
	 * @throws NullPointerException if the specified
	 * 		<code>array</code> parameter is null.
	 * @throws NegativeArraySizeException if the specified <code>length</code>
	 * 		is negative.
	 */
	public Object shrink (Object array, int size) {
		return Array.newInstance(array.getClass().getComponentType(), (int)(size/(funder+(1-funder)*fmin)));
	}

	/**
	 * Copies an array from the specified source array, beginning at the
	 * specified position, to the specified position of the destination array.
	 * A subsequence of array components are copied from the source
	 * array referenced by <code>source</code> to the destination array
	 * referenced by <code>target</code>. The number of components copied is
	 * equal to the <code>size</code> argument. The components at
	 * positions <code>sourceOffset</code> through
	 * <code>sourceOffset+size-1</code> in the source array are copied into
	 * positions <code>targetOffset</code> through
	 * <code>targetOffset+size-1</code>, respectively, of the destination
	 * array.
	 * <p>
	 * If the <code>source</code> and <code>target</code> arguments refer to the
	 * same array object, then the copying is performed as if the
	 * components at positions <code>sourceOffset</code> through
	 * <code>sourceOffset+size-1</code> were first copied to a temporary
	 * array with <code>size</code> components and then the contents of
	 * the temporary array were copied into positions
	 * <code>targetOffset</code> through <code>targetOffset+size-1</code> of the
	 * destination array.
	 * <p>
	 * If <code>target</code> is <code>null</code>, then a
	 * <code>NullPointerException</code> is thrown.
	 * <p>
	 * If <code>source</code> is <code>null</code>, then a
	 * <code>NullPointerException</code> is thrown and the destination
	 * array is not modified.
	 * <p>
	 * Otherwise, if any of the following is true, an
	 * <code>ArrayStoreException</code> is thrown and the destination is
	 * not modified:
	 * <ul>
	 * <li>The <code>source</code> argument refers to an object that is not an
	 * 	array.
	 * <li>The <code>target</code> argument refers to an object that is not an
	 * 	array.
	 * <li>The <code>source</code> argument and <code>target</code> argument refer to
	 * 	arrays whose component types are different primitive types.
	 * <li>The <code>source</code> argument refers to an array with a primitive
	 * 	component type and the <code>target</code> argument refers to an array
	 * 	with a reference component type.
	 * <li>The <code>source</code> argument refers to an array with a reference
	 * 	component type and the <code>target</code> argument refers to an array
	 * 	with a primitive component type.
	 * </ul>
	 * <p>
	 * Otherwise, if any of the following is true, an
	 * <code>IndexOutOfBoundsException</code> is
	 * thrown and the destination is not modified:
	 * <ul>
	 * <li>The <code>sourceOffset</code> argument is negative.
	 * <li>The <code>targetOffset</code> argument is negative.
	 * <li>The <code>size</code> argument is negative.
	 * <li><code>sourceOffset+size</code> is greater than
	 * 	<code>source.length</code>, the length of the source array.
	 * <li><code>targetOffset+size</code> is greater than
	 * 	<code>target.length</code>, the length of the destination array.
	 * </ul>
	 * <p>
	 * Otherwise, if any actual component of the source array from
	 * position <code>sourceOffset</code> through
	 * <code>sourceOffset+size-1</code> cannot be converted to the component
	 * type of the destination array by assignment conversion, an
	 * <code>ArrayStoreException</code> is thrown. In this case, let
	 * <b><i>k</i></b> be the smallest nonnegative integer less than
	 * length such that <code>source[sourceOffset+</code><i>k</i><code>]</code>
	 * cannot be converted to the component type of the destination
	 * array; when the exception is thrown, source array components from
	 * positions <code>sourceOffset</code> through
	 * <code>sourceOffset+</code><i>k</i><code>-1</code>
	 * will already have been copied to destination array positions
	 * <code>targetOffset</code> through
	 * <code>targetOffset+</code><i>k</I><code>-1</code> and no other
	 * positions of the destination array will have been modified.
	 * (Because of the restrictions already itemized, this
	 * paragraph effectively applies only to the situation where both
	 * arrays have component types that are reference types.)<p>
	 * This method calls {@link java.lang.System#arraycopy(Object,int,Object,int,int)}.
	 *
	 * @param source the source array.
	 * @param from start position in the source array.
	 * @param target the destination array.
	 * @param to start position in the destination data.
	 * @param size the number of array elements to be copied.
	 * @throws IndexOutOfBoundsException  if copying would cause
	 * 		access of data outside array bounds.
	 * @throws ArrayStoreException if an element in the <code>source</code>
	 * 		array could not be stored into the <code>target</code> array
	 * 		because of a type mismatch.
	 * @throws NullPointerException if either <code>source</code> or
	 * 		<code>target</code> is <code>null</code>.
	 */
	public static void copy (Object source, int from, Object target, int to, int size) {
		int sourceLength = Array.getLength(source), targetLength = Array.getLength(target), length;

		for (; size>0; size -= length) {
			length = Math.min(Math.min(sourceLength-from, targetLength-to), size);
			System.arraycopy(source, from, target, to, length);
			from = (from+length)%sourceLength;
			to = (to+length)%targetLength;
		}
	}

	/**
	 * Copies an array from the specified source array at position 0
	 * to position 0 of the destination array.
	 * The number of components copied is
	 * equal to the <code>size</code> argument. The components at
	 * positions <code>sourceOffset</code> through
	 * <code>sourceOffset+length-1</code> in the source array are copied into
	 * positions <code>targetOffset</code> through
	 * <code>targetOffset+length-1</code>, respectively, of the destination
	 * array.
	 * <p>
	 * If the <code>source</code> and <code>target</code> arguments refer to the
	 * same array object, then the copying is performed as if the
	 * components at positions <code>0</code> through
	 * <code>size-1</code> were first copied to a temporary
	 * array with <code>size</code> components and then the contents of
	 * the temporary array were copied into positions
	 * <code>0</code> through <code>size-1</code> of the
	 * destination array.
	 * <p>
	 * If <code>target</code> is <code>null</code>, then a
	 * <code>NullPointerException</code> is thrown.
	 * <p>
	 * If <code>source</code> is <code>null</code>, then a
	 * <code>NullPointerException</code> is thrown and the destination
	 * array is not modified.
	 * <p>
	 * Otherwise, if any of the following is true, an
	 * <code>ArrayStoreException</code> is thrown and the destination is
	 * not modified:
	 * <ul>
	 * <li>The <code>source</code> argument refers to an object that is not an
	 * 	array.
	 * <li>The <code>target</code> argument refers to an object that is not an
	 * 	array.
	 * <li>The <code>source</code> argument and <code>target</code> argument refer to
	 * 	arrays whose component types are different primitive types.
	 * <li>The <code>source</code> argument refers to an array with a primitive
	 * 	component type and the <code>target</code> argument refers to an array
	 * 	with a reference component type.
	 * <li>The <code>source</code> argument refers to an array with a reference
	 * 	component type and the <code>target</code> argument refers to an array
	 * 	with a primitive component type.
	 * </ul>
	 * <p>
	 * Otherwise, if any of the following is true, an
	 * <code>IndexOutOfBoundsException</code> is
	 * thrown and the destination is not modified:
	 * <ul>
	 * <li>The <code>size</code> argument is negative.
	 * </ul>
	 * <p>
	 * Otherwise, if any actual component of the source array from
	 * position <code>0</code> through
	 * <code>size-1</code> cannot be converted to the component
	 * type of the destination array by assignment conversion, an
	 * <code>ArrayStoreException</code> is thrown. In this case, let
	 * <b><i>k</i></b> be the smallest nonnegative integer less than
	 * length such that <code>source[</code><i>k</i><code>]</code>
	 * cannot be converted to the component type of the destination
	 * array; when the exception is thrown, source array components from
	 * positions <code>0</code> through
	 * <code></code><i>k</i><code>-1</code>
	 * will already have been copied to destination array positions
	 * <code>0</code> through
	 * <code></code><i>k</I><code>-1</code> and no other
	 * positions of the destination array will have been modified.
	 * (Because of the restrictions already itemized, this
	 * paragraph effectively applies only to the situation where both
	 * arrays have component types that are reference types.)
	 * This method calls:
	 * <br><br>
	 * <code><pre>
	 * 	copy(source, 0, target, 0, size);
	 * </code></pre>
	 *
	 * @param source the source array.
	 * @param target the destination array.
	 * @param size the number of array elements to be copied.
	 * @throws IndexOutOfBoundsException if copying would cause
	 * 		access of data outside array bounds.
	 * @throws ArrayStoreException  if an element in the <code>source</code>
	 * 		array could not be stored into the <code>target</code> array
	 * 		because of a type mismatch.
	 * @throws NullPointerException if either <code>source</code> or
	 * 		<code>target</code> is <code>null</code>.
	 * @see #copy(Object, int, Object, int, int)
	 */
	public void copy (Object source, Object target, int size) {
		copy(source, 0, target, 0, size);
	}

	/**
	 * Resizes a given (Object or primitive type) array.
	 * The minimum <tt>size</tt> (logical size) the array should have afer a contraction
	 * or an expanison has to be specified. <br>
	 * Depending on this logical size and the array's length the given
	 * array is expanded, i.e. <code>grow(array, size)</code> is called, or shrinked, i.e.
	 * <code>shrink(array, size)</code> is called.
	 *
	 * @param array the array to be resized.
	 * @param size the minimal needed size of the array (logical size).
	 * @return the resized array is returned.
	 * @throws ArrayStoreException  if an element in the <code>source</code>
	 * 		array could not be stored into the <code>target</code> array
	 * 		because of a type mismatch.
	 * @throws NullPointerException if the given array is <code>null</code>.
	 * @throws IllegalArgumentException if the given object argument is not an array.
	 * @see #grow(Object,int)
	 * @see #shrink(Object,int)
	 */
	public Object resize (Object array, int size) {
		int length = Array.getLength(array);

		if (size>length) {
			Object newArray = grow(array, size);

			copy(array, newArray, length);
			array = newArray;
		}
		else if (size<Math.ceil(fmin*length)) {
			Object newArray = shrink(array, size);

			copy(array, newArray, size);
			array = newArray;
		}
		return array;
	}

	/**
	 * Resizes a given Object-array. (no primitive types allowed)
	 * The minimum <tt>size</tt> (logical size) the array should have afer a contraction
	 * or an expanison has to be specified. <br>
	 * Depending on this logical size and the array's length the given
	 * array is expanded, i.e. <code>grow(array, size)</code> is called, or shrinked, i.e.
	 * <code>shrink(array, size)</code> is called.
	 *
	 * @param array the Object-array to resized.
	 * @param size the minimal needed size of the array (logical size).
	 * @return the resized array is returned.
	 * @throws ArrayStoreException  if an element in the <code>source</code>
	 * 		array could not be stored into the <code>target</code> array
	 * 		because of a type mismatch.
	 * @throws NullPointerException if the given array is <code>null</code>.
	 * @throws IllegalArgumentException if the given object argument is not an array.
	 * @see #grow(Object,int)
	 * @see #shrink(Object,int)
	 */
	public Object [] resize (Object [] array, int size) {
		if (size>array.length) {
			Object newArray = grow(array, size);

			copy(array, newArray, array.length);
			array = (Object[])newArray;
		}
		else if (size<Math.ceil(fmin*array.length)) {
			Object newArray = shrink(array, size);

			copy(array, newArray, size);
			array = (Object[])newArray;
		}
		return array;
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main (String[] args) {

		/*************************************************************************/
		/*                              Example 1                                */
		/*************************************************************************/
		System.out.println("------------------EXAMPLE 1-------------------");
		ArrayResizer arrayResizer = ArrayResizer.DEFAULT_INSTANCE; // get a default instance
		int[] ints = new int[10]; // use a primitive type array
		int size = 20; // minimal needed size (logical size) --> grow
		int[] resizedArray = (int[])arrayResizer.resize(ints, size); // resize-call
		System.out.println("initial array size:              " +ints.length);
		System.out.println("logical size:                    " +size);
		System.out.println("physical size:                   " +resizedArray.length +" (returned size!)");
		System.out.println("fover:                           " +arrayResizer.fover);
		System.out.println("funder:                          " +arrayResizer.funder);
		System.out.println("fmin:                            " +arrayResizer.fmin);
		System.out.println("logical size * fover:            " +size*arrayResizer.fover);
		System.out.println("logical size * funder:           " +size*arrayResizer.funder);
		System.out.println("logical size * fmin:             " +size*arrayResizer.fmin);
		System.out.println("physical size * fover:           " +resizedArray.length*arrayResizer.fover);
		System.out.println("physical size * funder:          " +resizedArray.length*arrayResizer.funder);
		System.out.println("physical size * fmin:            " +resizedArray.length*arrayResizer.fmin);
		System.out.println("NOTE the following conditions:");
		System.out.println("general:          logical size * fmin <= physical size * fmin <= logical size");
		System.out.println("grow-operation:   logical size * fover <= physical size * fover <= logical size");
		System.out.println("shrink-operation: logical size * funder <= physical size * funder <= logical size");
		System.out.println();

		/*************************************************************************/
		/*                              Example 2                                */
		/*************************************************************************/
		System.out.println("------------------EXAMPLE 2-------------------");
		arrayResizer = new ArrayResizer(0.2); // create new ArrayResizer with fmin = 0.2
		ints = new int[100]; // use a primitive type array
		size = 10; // minimal needed size (logical size) --> shrink
		resizedArray = (int[])arrayResizer.resize(ints, size); // resize-call
		System.out.println("initial array size:" +ints.length);
		System.out.println("logical size:              " +size);
		System.out.println("physical size:             " +resizedArray.length +" (returned size!)");
		System.out.println("fover:                     " +arrayResizer.fover);
		System.out.println("funder:                    " +arrayResizer.funder);
		System.out.println("fmin:                      " +arrayResizer.fmin);
		System.out.println("logical size * fover:      " +size*arrayResizer.fover);
		System.out.println("logical size * funder:     " +size*arrayResizer.funder);
		System.out.println("logical size * fmin:       " +size*arrayResizer.fmin);
		System.out.println("physical size * fover:     " +resizedArray.length*arrayResizer.fover);
		System.out.println("physical size * funder:    " +resizedArray.length*arrayResizer.funder);
		System.out.println("physical size * fmin:      " +resizedArray.length*arrayResizer.fmin);
		System.out.println("NOTE the following conditions:");
		System.out.println("general:          logical size * fmin <= physical size * fmin <= logical size");
		System.out.println("grow-operation:   logical size * fover <= physical size * fover <= logical size");
		System.out.println("shrink-operation: logical size * funder <= physical size * funder <= logical size");
		System.out.println();

		/*************************************************************************/
		/*                              Example 3                                */
		/*************************************************************************/
		System.out.println("------------------EXAMPLE 3-------------------");
		// create new ArrayResizer with fmin = 0.2, fover = 0.6 and funder = 0.8
		arrayResizer = new ArrayResizer(0.2, 0.6, 0.8);
		// use an Object array
		Integer[] objects = new Integer[]{new Integer(0), new Integer(1), new Integer(2)};
		size = 10; // minimal needed size (logical size) --> grow
		Integer[] resizedObjectArray = (Integer[])arrayResizer.resize(objects, size); // resize-call
		System.out.println("initial array size:        " +objects.length);
		System.out.println("logical size:              " +size);
		System.out.println("physical size:             " +resizedObjectArray.length +" (returned size!)");
		System.out.println("fover:                     " +arrayResizer.fover);
		System.out.println("funder:                    " +arrayResizer.funder);
		System.out.println("fmin:                      " +arrayResizer.fmin);
		System.out.println("logical size * fover:      " +size*arrayResizer.fover);
		System.out.println("logical size * funder:     " +size*arrayResizer.funder);
		System.out.println("logical size * fmin:       " +size*arrayResizer.fmin);
		System.out.println("physical size * fover:     " +resizedArray.length*arrayResizer.fover);
		System.out.println("physical size * funder:    " +resizedArray.length*arrayResizer.funder);
		System.out.println("physical size * fmin:      " +(int)(resizedObjectArray.length*arrayResizer.fmin));
		System.out.println("NOTE the following conditions:");
		System.out.println("general:          logical size * fmin <= physical size * fmin <= logical size");
		System.out.println("grow-operation:   logical size * fover <= physical size * fover <= logical size");
		System.out.println("shrink-operation: logical size * funder <= physical size * funder <= logical size");
		System.out.println();
	}
}
