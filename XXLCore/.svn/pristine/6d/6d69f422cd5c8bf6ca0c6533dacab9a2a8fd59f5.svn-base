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

package xxl.core.relational.cursors;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.functions.MetaDataFunction;

/**
 * A Mapper invokes a given metadata mapping function on an input metadata cursor array. <br>
 * The mapping function is applied to <tt>n</tt> input cursors at the same time,
 * that means a n-dimensional function is called and its arguments are one
 * element of each input cursor. The result is an object that is returned by the mapper.
 * <br>
 * Also a partial input is allowed with the intention to apply the mapping function
 * on less than <tt>n</tt> arguments (see allowPartialInput).
 * <p>
 * <b>IMPORTANT:</b> The function of the mapper is called with an object array.
 * <p>
 * @see xxl.core.functions.MetaDataFunction
 */
public class Mapper extends xxl.core.cursors.mappers.Mapper implements MetaDataCursor {

	/** Metadata that is passed by the metadata function */
	protected ResultSetMetaData metaData;

	/**
	 * Constructs a Mapper object that invokes a given metadata mapping function on an input 
	 * metadata cursor array.<br>
	 * The mapping function is applied to <tt>n</tt> input cursors at the same time,
	 * that means a n-dimensional function is called and its arguments are one
	 * element of each input cursor. The result is an object that is returned by the mapper.
	 * <br>
	 * Also a partial input is allowed with the intention to apply the mapping function
	 * on less than <tt>n</tt> arguments (see allowPartialInput).
	 * <p>
	 * <b>IMPORTANT:</b> The function of the mapper is called with an object array.
	 *
	 * @param cursors Array of MetaDataCursors that deliver the input
	 * @param mapping MetaDataFunction that maps an array of objects (one object per input cursor)
	 *	to a new object (output). The function also has to deliver the metadata for the output.
	 * @param allowPartialInput true if the function can be applied if not every cursor
	 *	delivers an input object.
	 */	
	public Mapper (MetaDataCursor [] cursors, MetaDataFunction mapping, boolean allowPartialInput) {
		super(cursors, mapping, allowPartialInput);
		metaData = (ResultSetMetaData)mapping.getMetaData();
	}

	/**
	 * Constructs a Mapper object that invokes a given metadata mapping function on an input 
	 * metadata cursor and returns an iteration containing the resulting objects.
	 * <p>
	 * <b>IMPORTANT:</b> The function of the mapper is called with an object array.
	 *
	 * @param cursor MetaDataCursor that delivers the input
	 * @param mapping MetaDataFunction that maps each object
	 *	to a new object (output). The function also has to deliver the metadata for the output.
	 */	
	public Mapper (MetaDataCursor cursor, MetaDataFunction mapping) {
		this(new MetaDataCursor[] {cursor}, mapping, false);
	}

	/**
	 * Constructs a Mapper object that invokes a given metadata mapping function on 
	 * an array of two ResultSets.<br>
	 * The mapping function is applied to both input cursors at the same time,
	 * that means a 2-dimensional function is called and its arguments are one
	 * element of each input cursor. The result is an object that is returned by the mapper.
	 * <br>
	 * Also a partial input is allowed with the intention to apply the mapping function
	 * if only one ResultSet delivers results (see allowPartialInput).
	 * <p>
	 * <b>IMPORTANT:</b> The function of the mapper is called with an object array.
	 *
	 * @param resultSet1 ResultSet that delivers the first input relation
	 * @param resultSet2 ResultSet that delivers the second input relation
	 * @param createTuple Function that maps a (row of the) ResultSet to a Tuple. 
	 *	The function gets a ResultSet and maps the current row to a Tuple. It is
	 * 	forbidden to call the next, update and similar methods of the ResultSet!
	 * @param mapping MetaDataFunction that maps an array of two objects (one object per input cursor)
	 *	to a new object (output). The function also has to deliver the metadata for the output.
	 * @param allowPartialInput true if the function can be applied if not both cursors
	 *	deliver an input object.
	 */	
	public Mapper (ResultSet resultSet1, ResultSet resultSet2, Function createTuple, MetaDataFunction mapping, boolean allowPartialInput) {
		this(new MetaDataCursor[] {
				new ResultSetMetaDataCursor(resultSet1, createTuple),
				new ResultSetMetaDataCursor(resultSet2, createTuple)
			}, mapping, allowPartialInput);
	}

	/**
	 * Constructs a Mapper object that invokes a given metadata mapping function on an input 
	 * ResultSet and returns an iteration containing the resulting objects.
	 * <p>
	 * <b>IMPORTANT:</b> The function of the mapper is called with an object array.
	 *
	 * @param resultSet ResultSet that delivers the input relation
	 * @param mapping MetaDataFunction that maps each object
	 *	to a new object (output). The function also has to deliver the metadata for the output.
	 * @param createTuple Function that maps a (row of the) ResultSet to a Tuple. 
	 *	The function gets a ResultSet and maps the current row to a Tuple. It is
	 * 	forbidden to call the next, update and similar methods of the ResultSet!
	 */	
	public Mapper (ResultSet resultSet, MetaDataFunction mapping, Function createTuple) {
		this(new ResultSetMetaDataCursor(resultSet, createTuple), mapping);
	}
	
	/**
	 * Returns the metaData that comes from the mapping function.
	 *
	 * @return the metaData that comes from the mapping function
	 */
	public Object getMetaData () {
		return metaData;
	}
}
