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

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import xxl.core.io.DataInputInputStream;
import xxl.core.io.DataOutputOutputStream;

/**
 * This class provides a converter that converts a byte array into a
 * zip compressed byte array representation and vice versa. Only
 * big byte arrays (>256 Bytes) are worth compressing!
 *
 * @see DataInput
 * @see DataOutput
 * @see IOException
 */
public class ByteArrayZipConverter extends Converter {

	/**
	 * This instance can be used for getting a default instance of
	 * ByteArrayZipConverter. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns, Prototype
	 * in <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of ByteArrayZipConverter.
	 */
	public static final ByteArrayZipConverter DEFAULT_INSTANCE = new ByteArrayZipConverter();

	/**
	 * Sole constructor. (For invocation by subclass constructors,
	 * typically implicit.)
	 */
	public ByteArrayZipConverter () {
	}

	/**
	 * Reads in a zip compressed byte array for the specified from the 
	 * specified data input and returns the decompressed byte array. <br>
	 * This implementation ignores the specified object and returns a new
	 * byte array. So it does not matter when the specified
	 * object is <tt>null</tt>.
	 *
	 * @param dataInput the stream to read a string from in order to
	 *        return an decompressed byte array.
	 * @param object the byte array to be decompressed. In
	 *        this implementation it is ignored.
	 * @return the read decompressed byte array.
	 * @throws IOException if I/O errors occur.
	 */
	public Object read (final DataInput dataInput, Object object) throws IOException {
		// decompresses the data from dataInput and returns the decompressed byte array
		ZipInputStream zis = new ZipInputStream(new DataInputInputStream(dataInput));
		// ZipEntry entry
		zis.getNextEntry();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len;
		byte buffer[] = new byte[512];
		while (true) {
			len = zis.read(buffer);
			if (len==-1)
				break;
			else
				baos.write(buffer,0,len);
		}
		return baos.toByteArray();
	}

	/**
	 * Writes the specified <tt>byte array</tt> object compressed to the specified
	 * data output. <br>
	 *
	 * @param dataOutput the stream to write the string representation of
	 *        the specified object to.
	 * @param object the byte array object that string
	 *        representation should be written to the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (final DataOutput dataOutput, Object object) throws IOException {
		// compressed the byte array object to DataOutput.
		ZipOutputStream zos = new ZipOutputStream(new DataOutputOutputStream(dataOutput));
		zos.setMethod(ZipOutputStream.DEFLATED);
		zos.putNextEntry(new ZipEntry("a"));
		byte array[] = (byte[])object;
		zos.write(array,0,array.length);
		zos.finish();
		zos.close();
	}

	/**
	 * The main method contains some examples how to use a
	 * ByteArrayZipConverter. It can also be used to test the
	 * functionality of a ByteArrayZipConverter.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit a String to compress/decompress.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		try {
			// create a byte array output stream
			java.io.ByteArrayOutputStream output;
			java.io.ByteArrayInputStream input;
			byte buf[], array[];
			
			String s;
			if (args.length==0) {
				s =	"This is a really long message text for test purposes."+
					"For shorter strings than 100 characters, this compression "+
		 			"does not make much sense because the zip-header ist too long. ";
		 		// make the String longer
		 		s = s+s+s;
		 	}
		 	else
		 		s = args[0];
	 		
			output = new java.io.ByteArrayOutputStream();
			// write two string bufferss to the output stream
			StringConverter.DEFAULT_INSTANCE.write(new java.io.DataOutputStream(output),s);
			buf = output.toByteArray();
			output.close();

			System.out.println("Decompressed length: "+buf.length);
			
			output = new java.io.ByteArrayOutputStream();
			ByteArrayZipConverter.DEFAULT_INSTANCE.write(new java.io.DataOutputStream(output),buf);
			array = output.toByteArray();
			output.close();
			
			System.out.println("Compressed length: "+array.length);
			
			input = new java.io.ByteArrayInputStream(array);
			buf = (byte[]) ByteArrayZipConverter.DEFAULT_INSTANCE.read(new java.io.DataInputStream(input),null);
			input.close();

			input = new java.io.ByteArrayInputStream(buf);
			String s2 = (String)StringConverter.DEFAULT_INSTANCE.read(new java.io.DataInputStream(input));
			input.close();
			
			System.out.println("The decompressed text: "+s2);

			if (!s.equals(s2))
				throw new RuntimeException("The original and the compressed/decompressed Strings are not equal.");
		}
		catch (IOException ioe) {
			System.out.println("An I/O error occured.");
		}
	}
}
