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

package xxl.core.relational;

import java.sql.ResultSet;
import java.sql.SQLException;

import xxl.core.functions.Function;
import xxl.core.util.WrappingRuntimeException;


/**
 * This class is a wrapper for ResultSets with the intention
 * to receive an instance of the VTI (Virtual Table Interface)
 * in order to use the wrapped ResultSet in a FROM clause of
 * an SQL-J statement. <p>
 *
 * <b>IMPORTANT: </b>The function SET_RESULTSET has to be defined
 * before any call to a constructor, i.e. before using NEW VirtualTable()
 * in a FROM-clause.
 */
public class VirtualTable extends DecoratorResultSet {

	/** Function has to be defined before any call to a constructor. */
	public static Function SET_RESULTSET = null;

	/**
	 * Creates a Virtual Table when the Function
	 * SET_RESULTSET has been set before calling
	 * this constructor.
	 */
	public VirtualTable ()  {
		super(null);
		try {
			if(SET_RESULTSET == null)
				throw new SQLException("No ResultSet set to create a VirtualTable from. Define the Function SET_RESULTSET.");
			else {
				((DecoratorResultSet)this).resultSet = (ResultSet)SET_RESULTSET.invoke();
			}
		}
		catch (Exception e) {
			throw new WrappingRuntimeException(e);
		}
	}
}
