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
import java.io.DataOutput;
import java.io.IOException;

/**
 * This class provides a converter that is able to read and write
 * <tt>String</tt> objects as streams of <tt>ASCII</tt> characters (the
 * <tt>byte</tt> values representing the <tt>ASCII</tt> characters). In
 * addition to the read and write methods that read or write
 * <tt>String</tt> objects (these methods get <tt>Object</tt>s and cast
 * them to <tt>String</tt> objects) this class contains readAsciiString
 * and writeAsciiString methods that gets directly <tt>String</tt>
 * objects.<p>
 * <b>Note:</b> Streams of <tt>ASCII</tt> characters (representing
 * <tt>String</tt> objects) are separated by special delimiters. Those
 * delimiters are sequences of one or more <tt>ASCII</tt> characters, that
 * can be specified by the user. When a stream (especially the last of a
 * data input) is not delimited by one of the specified delimiters, it
 * cannot be read by this converter.<p>
 *
 * Example usage (1).
 * <pre>
 *     // catch IOExceptions
 *
 *     try {
 *
 *         // create a new AsciiStringConverter that uses "\r\n" (cariage return, line feed),
 *         // "\t" (horizontal tab) and " " (white space) as delimiters
 *
 *         AsciiStringConverter converter = new AsciiStringConverter(
 *             new String [] {
 *                 "\r\n",
 *                 "\t",
 *                 " "
 *             }
 *         );
 *
 *         // create a byte array output stream
 *
 *         ByteArrayOutputStream output = new ByteArrayOutputStream();
 *
 *         // write some strings (delimited by "\r\n" per default) to the output stream
 *
 *         converter.write(
 *             new DataOutputStream(output),
 *             "Far out in the uncharted backwaters of the unfashionable end of the western spiral
 *              arm of the Galaxy lies a small unregarded yellow sun."
 *         );
 *         converter.write(
 *             new DataOutputStream(output),
 *             "Orbiting this at a distance of roughly ninety-two million miles is an utterly
 *              insignificant little blue green planet whose ape-descended life forms are so
 *              amazingly primitive that they still think digital watches are a pretty neat idea."
 *         );
 *
 *         // create a byte array input stream on the output stream
 *
 *         ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
 *
 *         // read strings from the input stream and print them as long as the stream contains data
 *
 *         while (input.available() > 0)
 *             System.out.println(converter.read(new DataInputStream(input)));
 *
 *         // close the streams after use
 *
 *         input.close();
 *         output.close();
 *     }
 *     catch (IOException ioe) {
 *         System.out.println("An I/O error occured.");
 *     }
 * </pre>
 *
 * @see DataInput
 * @see DataOutput
 * @see IOException
 */
public class AsciiStringConverter extends Converter {

	/**
	 * The converter that is used for converting single <tt>Character</tt>
	 * objects into <tt>ASCII</tt> characters and vice versa.
	 */
	protected CharacterConverter converter;

	/**
	 * An array of delimiters that can be used to separate the converted
	 * <tt>String</tt> objects.
	 */
	protected String [] delimiters;

	/**
	 * Constructs a new AsciiStringConverter that uses the given converter
	 * for converting single <tt>Character</tt> objects into
	 * <tt>ASCII</tt> characters and vice versa. The specified array
	 * contains the delimiters that can be used to separate the converted
	 * <tt>String</tt> objects.
	 *
	 * @param converter the converter that is used for converting single
	 *        <tt>Character</tt> objects into <tt>ASCII</tt> characters
	 *        and vice versa.
	 * @param delimiters an array of delimiters that can be used to
	 *        separate the converted <tt>String</tt> objects.
	 * @throws IllegalArgumentException if no delimiters are specified.
	 */
	public AsciiStringConverter(CharacterConverter converter, String [] delimiters) {
		this.converter = converter;
		if (delimiters.length < 1)
			throw new IllegalArgumentException("at least one delimiter must be specified!");
		this.delimiters = delimiters;
	}

	/**
	 * Constructs a new AsciiStringConverter that uses a default converter
	 * for converting single <tt>Character</tt> objects into
	 * <tt>ASCII</tt> characters and vice versa. The specified array
	 * contains the delimiters that can be used to separate the converted
	 * <tt>String</tt> objects.<br>
	 * This constructor is equivalent to the call of
	 * <code>new AsciiStringConverter(AsciiConverter.DEFAULT_INSTANCE, delimiters)</code>.
	 *
	 * @param delimiters an array of delimiters that can be used to
	 *        separate the converted <tt>String</tt> objects.
	 * @throws IllegalArgumentException if no delimiters are specified.
	 */
	public AsciiStringConverter(String [] delimiters) {
		this(AsciiConverter.DEFAULT_INSTANCE, delimiters);
	}

	/**
	 * Constructs a new AsciiStringConverter that uses the given converter
	 * for converting single <tt>Character</tt> objects into
	 * <tt>ASCII</tt> characters and vice versa. <i>Cariage return</i> and
	 * <i>line feed</i> are used as delimiters.<br>
	 * This constructor is equivalent to the call of
	 * <code>new AsciiStringConverter(converter, new String [] {"\r", "\n"})</code>.
	 *
	 * @param converter the converter that is used for converting single
	 *        <tt>Character</tt> objects into <tt>ASCII</tt> characters
	 *        and vice versa.
	 */
	public AsciiStringConverter(CharacterConverter converter) {
		this(converter, new String [] {"\r", "\n"});
	}

	/**
	 * Constructs a new AsciiStringConverter that uses a default converter
	 * for converting single <tt>Character</tt> objects into
	 * <tt>ASCII</tt> characters and vice versa. <i>Cariage return</i> and
	 * <i>line feed</i> are used as delimiters.<br>
	 * This constructor is equivalent to the call of
	 * <code>new AsciiStringConverter(AsciiConverter.DEFAULT_INSTANCE, new String [] {"\r", "\n"})</code>.
	 */
	public AsciiStringConverter() {
		this(AsciiConverter.DEFAULT_INSTANCE, new String [] {"\r", "\n"});
	}

	/**
	 * Reads in a string of <tt>ASCII</tt> characters and returns the
	 * restored (<tt>String</tt>) object, when a delimiter is found. When
	 * a string (especially the last) is not delimited by one of the
	 * specified delimiters, this method will expect more data and
	 * therefore an <tt>EOFException</tt> will be thrown.<br>
	 * This implementation ignores the specified object, so it does not
	 * matter when the specified object is <tt>null</tt>.
	 *
	 * @param dataInput the stream to read the <tt>ASCII</tt> characters
	 *        representing a string from in order to return a
	 *        <tt>String</tt> object.
	 * @param object the (<tt>String</tt>) object to be restored. In this
	 *        implementation it is ignored.
	 * @return the read <tt>String</tt> object.
	 * @throws IOException if I/O errors occur.
	 */
	public Object read (DataInput dataInput, Object object) throws IOException {
		StringBuffer buffer = new StringBuffer();
		int index = -1;
		do {
			buffer.append(converter.readChar(dataInput));
		}
		while ((index = endsWithDelimiter(buffer.toString())) == -1);
		buffer.setLength(buffer.length() - delimiters[index].length());
		return buffer.toString();
	}

	/**
	 * Reads in a string of <tt>ASCII</tt> characters and returns the
	 * restored (<tt>String</tt>) object, when a delimiter is found. <br>
	 * This implementation calls the read method and casts its result to
	 * <tt>String</tt>.
	 *
	 * @param dataInput the stream to read the <tt>ASCII</tt> characters
	 *        representing a string from in order to return a
	 *        <tt>String</tt> object.
	 * @return the read <tt>String</tt> object.
	 * @throws IOException if I/O errors occur.
	 */
	public String readAsciiString (DataInput dataInput) throws IOException {
		return (String)read(dataInput);
	}

	/**
	 * Writes the specified <tt>String</tt> object to the specified data
	 * output by writing a string of <tt>ASCII</tt> characters to it. The
	 * string is delimited by the delimiter the given index points to.
	 *
	 * @param dataOutput the stream to write a string of <tt>ASCII</tt>
	 *        characters representing the specified <tt>String</tt> object
	 *        to.
	 * @param object the <tt>String</tt> object that should be written to
	 *        the data output.
	 * @param index the index of the delimiter that should be used to
	 *        delimit the converted string.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (DataOutput dataOutput, Object object, int index) throws IOException {
		for (int i = 0; i < ((String)object).length(); i++)
			converter.writeChar(dataOutput, ((String)object).charAt(i));
		for (int i = 0; i < delimiters[index].length(); i++)
			converter.writeChar(dataOutput, delimiters[index].charAt(i));
	}

	/**
	 * Writes the specified <tt>String</tt> object to the specified data
	 * output by writing a string of <tt>ASCII</tt> characters to it. <br>
	 * This implementation calls the write method with the given data
	 * output, the specified object and index <tt>0</tt>. That means, the
	 * first delimiter of the array will be used to delimit the string per
	 * default.
	 *
	 * @param dataOutput the stream to write a string of <tt>ASCII</tt>
	 *        characters representing the specified <tt>String</tt> object
	 *        to.
	 * @param object the <tt>String</tt> object that should be written to
	 *        the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (DataOutput dataOutput, Object object) throws IOException {
		write(dataOutput, object, 0);
	}

	/**
	 * Writes the specified <tt>String</tt> object to the specified data
	 * output by writing a string of <tt>ASCII</tt> characters to it. <br>
	 * This implementation calls the write method with the given data
	 * output, the specified object and the given index.
	 *
	 * @param dataOutput the stream to write a string of <tt>ASCII</tt>
	 *        characters representing the specified <tt>String</tt> object
	 *        to.
	 * @param object the <tt>String</tt> object that should be written to
	 *        the data output.
	 * @param index the index of the delimiter that should be used to
	 *        delimit the converted string.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void writeAsciiString (DataOutput dataOutput, Object object, int index) throws IOException {
		write(dataOutput, object, index);
	}

	/**
	 * Writes the specified <tt>String</tt> object to the specified data
	 * output by writing a string of <tt>ASCII</tt> characters to it. <br>
	 * This implementation calls the write method with the given data
	 * output, the specified object and index <tt>0</tt>. That means, the
	 * first delimiter of the array will be used to delimit the string per
	 * default.
	 *
	 * @param dataOutput the stream to write a string of <tt>ASCII</tt>
	 *        characters representing the specified <tt>String</tt> object
	 *        to.
	 * @param object the <tt>String</tt> object that should be written to
	 *        the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void writeAsciiString (DataOutput dataOutput, Object object) throws IOException {
		write(dataOutput, object, 0);
	}

	/**
	 * Determines whether the specified <tt>String</tt> object ends with
	 * one of the delimiters stored in the array. The index of the
	 * delimiter in the array is returned, if the string is delimited by
	 * it, else <tt>-1</tt> is returned.
	 *
	 * @param buffer the string that should be tested whether it ends with
	 *        one of the specified delimiters.
	 * @return the index of the delimiter in the array, if the string is
	 *         delimited by it, else <tt>-1</tt>.
	 */
	protected int endsWithDelimiter (String buffer) {
		for (int i = 0; i < delimiters.length; i++)
			if (buffer.endsWith(delimiters[i]))
				return i;
		return -1;
	}

	/**
	 * The main method contains some examples how to use an
	 * AsciiStringConverter. It can also be used to test the functionality
	 * of an AsciiStringConverter.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// catch IOExceptions
		try {
			// create a new AsciiStringConverter that uses "\r\n" (cariage
			// return, line feed), "\t" (horizontal tab) and " " (white
			// space) as delimiters
			AsciiStringConverter converter = new AsciiStringConverter(new String [] {"\r\n", "\t", " "});
			// create a byte array output stream
			java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
			// write some strings (delimited by "\r\n" per default) to the
			// output stream
			converter.write(new java.io.DataOutputStream(output), "Far out in the uncharted backwaters of the unfashionable end of the western spiral arm of the Galaxy lies a small unregarded yellow sun.");
			converter.write(new java.io.DataOutputStream(output), "Orbiting this at a distance of roughly ninety-two million miles is an utterly insignificant little blue green planet whose ape-descended life forms are so amazingly primitive that they still think digital watches are a pretty neat idea.");
			// create a byte array input stream on the output stream
			java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(output.toByteArray());
			// read strings from the input stream and print them as long
			// as the stream contains data
			while (input.available() > 0)
				System.out.println(converter.read(new java.io.DataInputStream(input)));
			// close the streams after use
			input.close();
			output.close();
		}
		catch (IOException ioe) {
			System.out.println("An I/O error occured.");
		}
		System.out.println();
	}
}