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

package xxl.core.io.fat.util;

import java.io.FilenameFilter;
import xxl.core.io.fat.ExtendedFile;

/**
 * Instances of classes that implement this interface are used to filter filenames.
 * These instances are used to filter directory listings in
 * the list method of class ExtendedFile.
 */
public interface ExtendedFilenameFilter extends FilenameFilter
{
	/**
	 * Tests if a specified file should be included in a file list.
	 * @param dir directory in which the file was found.
	 * @param name name of the file.
	 * @return true if and only if the name should be included in the file list; false otherwise.
	 */
	public boolean accept(ExtendedFile dir, String name);
}	//end ExtendedFilenameFilter