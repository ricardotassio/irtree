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
 * An object of this class projects an input array-argument to
 * a "sub"-array of arguments. The fields of the array
 * to be kept have to be specified.
 * The projections done by objects of this class are
 * performed by truncating the given array of arguments to project.
 * A different approach is provided by the {@link xxl.core.functions.FastMapProjection FastMap-Projection}
 * using a distance function given upon the object space. Check this class
 * for further details.
 *
 * @see xxl.core.functions.Function
 * @see xxl.core.functions.FastMapProjection
 */

public class Projection extends Function{

	/** fields to keep in the projection */
	protected int [] indices;

	/** Constructs a new Object of this type.
	 * @param indices fields to keep in the projection
	 */
	public Projection( int [] indices) {
		this.indices = indices;
	}

	/** Constructs a new Object of this type projecting
	 * any given array to a "sub"-array of dimension 1.
	 *
	 * @param index field to keep in the projection
	 */
	public Projection( int index) {
		this( new int [] { index});
	}

	/** Constructs a new Object of this type projecting
	 * any given array to a "sub"-array of dimension 2.
	 * 
	 * @param index0 first field to keep in the projection
	 * @param index1 second field to keep in the projection
	 */
	public Projection( int index0, int index1) {
		this( new int [] { index0, index1});
	}

	/** Performs the projection keeping the given fields.
	 * 
	 * @param objects array of arguments to project
	 * @return the "sub"-array containing the objects corresponding to the given indices
	 */
	public Object invoke( Object [] objects) {
		Object [] projection = new Object [indices.length];
		for( int i = 0; i < indices.length; i++)
			projection [i] = objects [indices [i] ];
		return projection;
	}

	/** Returns the used indices for projection.
	 * @return the used indices for projection
	 */
	public int [] getIndices() {
		return indices;
	}
}