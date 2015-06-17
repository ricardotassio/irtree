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

import xxl.core.util.MetaDataProvider;

/**
 * A metadata-cursor realizes a cursor additionally providing metadata. It
 * extends the interface {@link xxl.core.cursors.Cursor} and
 * {@link xxl.core.util.MetaDataProvider}. So the main difference between a
 * regular cursor and a metadata-cursor lies in the provision of the
 * <tt>getMetaData</tt> method, which returns metadata information about the
 * elements this metadata-cursor delivers. The return value of the
 * <tt>getMetaData</tt> method can be an arbitrary kind of metadata information,
 * e.g., relational metadata information, therefore it is of type
 * {@link java.lang.Object}.
 * 
 * <p>When using a metadata-cursor, it has to be guaranteed, that all elements
 * contained in this metadata-cursor refer to the same metadata information.
 * That means, every time <tt>getMetaData</tt> is called on a metadata-cursor,
 * it should return the same metadata information. Generally this method is
 * called only once.</p>
 *
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.util.MetaDataProvider
 */
public interface MetaDataCursor extends Cursor, MetaDataProvider {

	/**
	 * Returns the metadata information for this metadata-cursor. The return
	 * value of this method can be an arbitrary kind of metadata information,
	 * e.g., relational metadata information, therefore it is of type
	 * {@link java.lang.Object}. When using a metadata-cursor, it has to be
	 * guaranteed, that all elements contained in this metadata-cursor refer to
	 * the same metadata information. That means, every time <tt>getMetaData</tt>
	 * is called on a metadata-cursor, it should return exactly the same metadata
	 * information.
	 *
	 * @return an object representing metadata information for this
	 *         metadata-cursor.
	 */
	public abstract Object getMetaData();

}
