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

package xxl.core.predicates;

/**
 * This class provides an unary predicate that returns a boolean <tt>value</tt>
 * for every <tt>n</tt>-th argument. Internally the predicate counts
 * how often the method <tt>invoke(Object)</tt> has been called in
 * order to return a user-defined boolean <tt>value</tt> for every <tt>n</tt>-th call.
 *
 */
public class EveryNth extends Predicate {

	/**
	 * The number of times the method <tt>invoke(Object)</tt> has been
	 * called.
	 */
	protected long count = 0;

	/**
	 * The boolean value to be returned, if the <tt>n</tt>-th call
	 * to <tt>invoke(Object)</tt> is performed.
	 */
	protected boolean value;

	/**
	 * A positive non-zero number that specifies on which argument the
	 * predicate returns <tt>value</tt>. Every <tt>n</tt>-th call of the
	 * method <tt>invoke(Object)</tt> <tt>true</tt> is returned.
	 */
	protected long n;

	/**
	 * Creates a new predicate that returns <tt>value</tt> everytime the
	 * method <tt>invoke(Object)</tt> is called the <tt>n</tt>-th time.
	 *
	 * @param n a positive non-zero number that specifies on which
	 *        argument the predicate returns <tt>value</tt>. Every
	 *        <tt>n</tt>-th call of the method <tt>invoke(Object)</tt>
	 *        <tt>value</tt> is returned.
	 * @param value boolean value to be returned.
	 * @throws IllegalArgumentException if <tt>n</tt> is less or equal
	 *         zero (<tt>n&le;0</tt>).
	 */
	public EveryNth (int n, boolean value) {
		if (n <= 0)
			throw new IllegalArgumentException("only positive values could be used");
		this.n = n;
		this.value = value;
	}

	/**
	 * Creates a new predicate that returns <tt>true</tt> everytime the
	 * method <tt>invoke(Object)</tt> is called the <tt>n</tt>-th time.
	 *
	 * @param n a positive non-zero number that specifies on which
	 *        argument the predicate returns <tt>true</tt>. Every
	 *        <tt>n</tt>-th call of the method <tt>invoke(Object)</tt>
	 *        <tt>true</tt> is returned.
	 * @throws IllegalArgumentException if <tt>n</tt> is less or equal
	 *         zero (<tt>n&le;0</tt>).
	 */
	public EveryNth (int n) {
		this(n, true);
	}

	/**
	 * Returns <tt>value</tt> if this method is called the <tt>n</tt>-th
	 * time. Internally a counter is increased every time the method is
	 * called.
	 *
	 * @param argument the argument to the predicate.
	 * @return <tt>value</tt> if this method is called the <tt>n</tt>-th
	 *         time, otherwise <tt>!value</tt>.
	 */
	public boolean invoke (Object argument) {
		return (++count % n) == 0 ? value : !value;
	}
}
