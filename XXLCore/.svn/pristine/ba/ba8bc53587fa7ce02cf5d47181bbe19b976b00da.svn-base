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

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This class provides an output stream pointing to a NULL sink, i.e., all
 * output will be supressed.
 * 
 * @see xxl.core.util.XXLSystem#NULL
 */
public class NullOutputStream extends OutputStream{

	/** Privodes a ready-to-use {@link java.io.OutputStream OutputStream} representing
	 * a nul device, i.e., a dummy sink.
	 *
	 * @see java.lang.System#out
	 * @see java.lang.System#err
	 */
	public static final OutputStream NULL = new NullOutputStream();

	/**
	 * Don't let anyone instanciate this class (singleton pattern). 
	 */
	private NullOutputStream(){}

	/**
	 * Closes the stream.
	 */
	public void close() {}

	/**
	 * Flushes the stream (not necessary to call here). 
	 */
	public void flush() {}

	/**
	 * Writes the byte array to the NullOutputStream.
	 * @param b
	 */
	public void write(byte[] b) {}

	/**
	 * Writes the byte array to the NullOutputStream.
	 * @param b the array
	 * @param off the offset
	 * @param len the number of bytes written.
	 */
	public void write(byte[] b, int off, int len) {}

	/**
	 * Writes the byte to the NullOutputStream.
	 * @param b the value of the byte.
	 */
	public void write(int b) {}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of the assoiciated class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main( String [] args){
		PrintStream p = new PrintStream( NULL);
		for( int i=0; i< 10; i++)
			p.println("i=" + i);
		p.flush();
		p.close();
	}
}
