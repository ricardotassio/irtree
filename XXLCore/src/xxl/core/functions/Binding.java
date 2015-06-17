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
 * This class represent an object with free variables which can bind with 
 * values (arguments). This object can be a function, a predicate or a whole subquery.
 */
public interface Binding {
	
	/**
	 * Set the constant values to which a part of the free arguments 
	 * should be bound.
	 *
	 * @param constArguments0 the constant values to which a part of the
	 *        free arguments should be bound.
	 */
	public void setBinds (Object [] constArguments0);
	
	/**
	 * Set the constant values to which a part of the free arguments
	 * should be bound.
	 *
	 * @param constIndices0 the indices of the arguments which
	 *        should be bound to <tt>null</tt>. The important is
	 *		  that it should always be sorted.
	 * @param constArguments0 the constant values to which a part of the
	 *        free arguments should be bound.
	 */
	public void setBinds (int [] constIndices0, Object [] constArguments0);
	/**
	 * Set a constant value to which a free argument
	 * should be bound.
	 *
	 * @param constIndex the index of the arguments which should
	 *	  be bound to <tt>null</tt>.
	 * @param constArgument the constant value to which a free argument
	 *	  should be bound.
	 */
	public void setBind (int constIndex, Object constArgument);
	
	/**
	 * Set free all bound arguments.
	 */
	public void restoreBinds ();
}
