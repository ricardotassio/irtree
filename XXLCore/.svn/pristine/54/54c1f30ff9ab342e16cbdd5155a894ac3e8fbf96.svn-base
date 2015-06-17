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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import xxl.core.io.DataInputInputStream;
import xxl.core.io.DataOutputOutputStream;

/**
 * This class provides a converter that compresses/decompresses everything which a
 * different converter writes/reads.
 * Only big Objects (>256 Bytes) are worth being compressed!
 *
 * @see DataInput
 * @see DataOutput
 * @see IOException
 */
public class BlockZipDecoratorConverter extends Converter {

	/**
	 * Converter which is decorated.
	 */
	protected Converter converter;

	/**
	 * Constructs a new BlockZipDecoratorConverter.
	 * @param converter Converter which converts the uncompressed byte
	 * 	arrays into Objects of a certain type and vice versa.
	 */
	public BlockZipDecoratorConverter (Converter converter) {
		this.converter = converter;
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
		ZipInputStream zis = new ZipInputStream(new DataInputInputStream(dataInput));
		// ZipEntry entry
		zis.getNextEntry();
		return converter.read(new DataInputStream(zis), object);
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
		ZipOutputStream zos = new ZipOutputStream(new DataOutputOutputStream(dataOutput));
		zos.setMethod(ZipOutputStream.DEFLATED);
		zos.putNextEntry(new ZipEntry("a"));
		DataOutputStream dos = new DataOutputStream(zos);
		
		converter.write(dos, object);
		dos.flush();
		zos.finish();
		dos.close();
	}

	/**
	 * The main method contains some examples how to use a
	 * BlockZipDecoratorConverter. It can also be used to test the
	 * functionality of a BlockZipDecoratorConverter.
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
			
			Converter converter = new BlockZipDecoratorConverter(StringConverter.DEFAULT_INSTANCE);
			
			output = new java.io.ByteArrayOutputStream();
			converter.write(new java.io.DataOutputStream(output),s);
			array = output.toByteArray();
			output.close();
			
			System.out.println("Compressed length: "+array.length);
			
			input = new java.io.ByteArrayInputStream(array);
			String s2 = (String) converter.read(new java.io.DataInputStream(input),null);
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
