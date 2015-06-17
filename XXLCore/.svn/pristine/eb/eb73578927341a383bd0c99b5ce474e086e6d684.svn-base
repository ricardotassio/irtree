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

package xxl.core.functions;

/**
 * A Constant is a Function that returns a constant value.
 */

public class Constant extends Function {

	/** Constant function returning a Boolean object representing <code>true</code> */
	public static final Constant TRUE = new Constant(true);

	/** Constant function returning a Boolean object representing <code>false</code> */
	public static final Constant FALSE = new Constant(false);

	/** Constant returned by this function */
	protected final Object object;

	/** Constructs a new constant function returning the given Object.
	 * @param object constant object to return by calling {@link xxl.core.functions.Function#invoke() invoke}
	 */
	public Constant (Object object) {
		this.object = object;
	}

	/** Constructs a new constant function returning an Object representing
	 * the given boolean (for convenience only).
	 * @param b constant to return by calling {@link xxl.core.functions.Function#invoke() invoke}
	 */
	public Constant (boolean b) {
		this(new Boolean(b));
	}

	/** Constructs a new constant function returning an Object representing
	 * the given byte (for convenience only).
	 * @param b constant to return by calling {@link xxl.core.functions.Function#invoke() invoke}
	 * represented by a corresponding Object-Type.
	 */
	public Constant (byte b) {
		this(new Byte(b));
	}

	/** Constructs a new constant function returning an Object representing
	 * the given char (for convenience only).
	 * @param c constant to return by calling {@link xxl.core.functions.Function#invoke() invoke}
	 * represented by a corresponding Object-Type.
	 */
	public Constant (char c) {
		this(new Character(c));
	}

	/** Constructs a new constant function returning an Object representing
	 * the given double (for convenience only).
	 * @param d constant to return by calling {@link xxl.core.functions.Function#invoke() invoke}
	 * represented by a corresponding Object-Type.
	 */
	public Constant (double d) {
		this(new Double(d));
	}

	/** Constructs a new constant function returning an Object representing
	 * the given float (for convenience only).
	 * @param f constant to return by calling {@link xxl.core.functions.Function#invoke() invoke}
	 * represented by a corresponding Object-Type.
	 */
	public Constant (float f) {
		this(new Float(f));
	}

	/** Constructs a new constant function returning an Object representing
	 * the given int (for convenience only).
	 * @param i constant to return by calling {@link xxl.core.functions.Function#invoke() invoke}
	 * represented by a corresponding Object-Type.
	 */
	public Constant (int i) {
		this(new Integer(i));
	}

	/** Constructs a new constant function returning an Object representing
	 * the given long (for convenience only).
	 * @param l constant to return by calling {@link xxl.core.functions.Function#invoke() invoke}
	 * represented by a corresponding Object-Type.
	 */
	public Constant (long l) {
		this(new Long(l));
	}

	/** Constructs a new constant function returning an Object representing
	 * the given short (for convenience only).
	 * @param s constant to return by calling {@link xxl.core.functions.Function#invoke() invoke}
	 * represented by a corresponding Object-Type.
	 */
	public Constant (short s) {
		this(new Short(s));
	}

	/** Returns the stored constant value.
	 * @param objects arguments of the function
	 * @return the stored constant value
	 */
	public Object invoke (Object [] objects) {
		return object;
	}
}