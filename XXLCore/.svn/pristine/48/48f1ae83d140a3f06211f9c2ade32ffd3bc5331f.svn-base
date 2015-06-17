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
package xxl.core.io.converters;

/** This is a Converter which can convert objects with a known maximal size. That is in many cases very useful 
 * (for example {@link xxl.core.collections.containers.io.BlockFileContainer}). 
 * For example the class {@link xxl.core.indexStructures.BPlusTree} uses normaly a 
 * {@link xxl.core.collections.containers.io.BlockFileContainer} to store its nodes. I/O Operations are executed by composite 
 * Converters. To determine the node size it is necessary to know the maximal size of the data objects. For this 
 * purpose a MeasuredConverter is practical.
 */
public abstract class MeasuredConverter extends Converter {
	
	/** determines the maximal size of the objects for which this Converter is used. In the case of Integer-Converter 
	 * the result will be 2 bytes. 
	 * @return the maximal object size in bytes.
	 */
	public abstract int getMaxObjectSize();
}
