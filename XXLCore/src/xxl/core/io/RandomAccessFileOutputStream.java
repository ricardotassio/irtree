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

package xxl.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * This class provides a mechanism to write Data via an 
 * OutputStream into a RandomAccessFile. The stream starts 
 * writing at the current position of the underlying file. 
 * A close of the stream does not close the file, so it
 * is still open for subsequent processing.
 */
public class RandomAccessFileOutputStream extends OutputStream {
	/**
	 * The internally used RandomAccessFile
	 */
	protected RandomAccessFile raf;
	
	/**
	 * Constructs an OutputStream that writes its data into a
	 * RandomAccessFile.
	 * @param raf The RandomAccessFile used for outputing the stream.
	 */
	public RandomAccessFileOutputStream(RandomAccessFile raf) {
		this.raf = raf;
	}

	/**
	 * Writes a byte into the RandomAccessFile.
	 * @param b byte
	 * @throws IOException
	 */
	public void write(int b) throws IOException {
		write(b);
	}
	
	/**
	 * Writes a byte array into the RandomAccessFile.
	 * @param b byte array
	 * @param off position of the first byte in the array
	 *	that should be written.
	 * @param len length in bytes that should be written.
	 * @throws IOException
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		raf.write(b,off,len);
	}
	
	/**
	 * Data is automatically written to the RandomAccessFile. There is
	 * no gerneral possibility to tell a RandomAccessFile to really write
	 * the data.
	 * @throws IOException
	 */
	public void flush() throws IOException {
	}
	
	/** 
	 * Disconnects the OutputStream. Further calls to the OutputStream 
	 * are not allowed.
	 */
	public void close() {
		raf = null;
	}
	
	/**
	 * Example: copying a RandomAccessFile via Streams. You have to 
	 * specify two filenames as arguments. Example:
	 * <pre>
	 * xxl xxl.core.io.RandomAccessFileOutputStream c:\test.txt c:\copytest.txt
	 * </pre>
	 * @param args The command line options (two filenames).
	 */
	public static void main (String args[]) {
		if (args.length==2) {
			try {
				RandomAccessFile source = new RandomAccessFile(args[0],"r");
				RandomAccessFile sink = new RandomAccessFile(args[1],"rw");
				
				InputStream inputStream = new java.io.BufferedInputStream(new RandomAccessFileInputStream(source));
				OutputStream outputStream = new java.io.BufferedOutputStream(new RandomAccessFileOutputStream(sink));
				for (int b; (b = inputStream.read())!=-1;)
					outputStream.write(b);
				outputStream.close();
				inputStream.close();
			}
			catch (IOException e) {
				System.out.println("Error while copying:");
				e.printStackTrace();
			}
		}
	}
}
