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
package xxl.core.spatial.rectangles;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import xxl.core.util.WrappingRuntimeException;

/**
 * This class provides functionality for saving and restoring rectangles to disk files 
 */

public abstract class Rectangles {

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private Rectangles(){}

	/** Reads rectangle object from the given file using Convertable interface read metod  
	 * @see xxl.core.io.Convertable
	 *	
	 * @param file input file 
	 * @param rectangle rectangle to save data from file
	 * @return returns rectangle which have been read
	 */
	public static Rectangle readSingletonRectangle(File file, Rectangle rectangle) {
		DataInputStream din;
		try {
			din = new DataInputStream(new FileInputStream(file));
			rectangle.read(din);
		}
		catch (Exception e) {
			throw new WrappingRuntimeException(e);
		}
		return rectangle;
	}

	/** Writes rectangle object to the given file using Convertable interface write metod  
	 * @see xxl.core.io.Convertable
	 * @param file output file
	 * @param rectangle rectangle to write
	 */
	public static void writeSingletonRectangle(
		File file,
		Rectangle rectangle) {
		DataOutputStream dos;
		try {
			dos = new DataOutputStream(new FileOutputStream(file));
			rectangle.write(dos);
			dos.close();
		}
		catch (Exception e) {
			throw new WrappingRuntimeException(e);
		}
	}
}
