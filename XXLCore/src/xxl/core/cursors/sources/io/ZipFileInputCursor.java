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

package xxl.core.cursors.sources.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import xxl.core.io.converters.Converter;
import xxl.core.util.WrappingRuntimeException;

/**
 * This class provides a cursor that reads the input from an entry of a given zip
 * file.
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.sources.io.InputStreamCursor
 * @see java.util.zip.ZipFile
 */
public class ZipFileInputCursor extends InputStreamCursor {

	/**
	 * Constructs a new zip-file-input cursor that depends on the specified zip
	 * file and uses the specified converter in order to read out the serialized
	 * objects from the given zip entry.
	 *
	 * @param converter the converter that is used for reading out the serialized
	 *        objects of this iteration.
	 * @param file the zip file that contains the serialized objects of this
	 *        iteration.
	 * @param zipEntry the name of the entry inside the zip file containing the
	 *        data of the iteration.
	 * @param bufferSize the size of the buffer that is used for the file input.
	 */
	public ZipFileInputCursor(Converter converter, File file, String zipEntry, int bufferSize) {
		super(null, converter);
		try {
			ZipFile zip = new ZipFile(file);
			input = new DataInputStream(
				new BufferedInputStream(
					zip.getInputStream(
						zip.getEntry(zipEntry)
					),
					bufferSize
				)
			);
		}
		catch (IOException ie) {
			throw new WrappingRuntimeException(ie);
		}
	}

	/**
	 * Constructs a new zip-file-input cursor that depends on the specified zip
	 * file and uses the specified converter in order to read out the serialized
	 * objects from the given zip entry. The buffer size is set to 4096 bytes.
	 *
	 * @param converter the converter that is used for reading out the serialized
	 *        objects of this iteration.
	 * @param file the zip file that contains the serialized objects of this
	 *        iteration.
	 * @param zipEntry the name of the entry inside the zip file containing the
	 *        data of the iteration.
	 */
	public ZipFileInputCursor(Converter converter, File file, String zipEntry) {
		this(converter, file, zipEntry, 4096);
	}
	
}
