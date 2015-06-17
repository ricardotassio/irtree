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
import java.io.FileInputStream;
import java.io.IOException;

import xxl.core.functions.Function;
import xxl.core.io.converters.ConvertableConverter;
import xxl.core.io.converters.Converter;
import xxl.core.util.WrappingRuntimeException;

/**
 * This class provides a cursor that depends on a given file. It iterates over
 * the objects that are read out of the underlying file. A converter is used in
 * order to read out the serialized objects. This class implements only a few
 * constructors. These constructers create a new input stream cursor that depends
 * on a file input stream.
 * 
 * <p><b>Example usage (1):</b>
 * <pre>
 *     // create a new file
 *
 *     File file = new File("file.dat");
 *
 *     // catch IOExceptions
 *
 *     try {
 *
 *         // create a random access file on that file
 *
 *         RandomAccessFile output = new RandomAccessFile(file, "rw");
 *
 *         // write some data to that file
 *
 *         output.writeUTF("Some data.");
 *         output.writeUTF("More data.");
 *         output.writeUTF("Another bundle of data.");
 *         output.writeUTF("A last bundle of data.");
 *
 *         // close the random access file
 *
 *         output.close();
 *     }
 *     catch (Exception e) {
 *         System.out.println("An error occured.");
 *     }
 *
 *     // create a new file input cursor with ...
 *
 *     FileInputCursor cursor = new FileInputCursor(
 *
 *         // a string converter
 *
 *         StringConverter.DEFAULT_INSTANCE,
 *
 *         // the created file
 *
 *         file
 *     );
 * 
 *     // open the cursor
 * 
 *     cursor.open();
 *
 *     // print all elements of the cursor
 *
 *     while (cursor.hasNext())
 *         System.out.println(cursor.next());
 * 
 *     // close the cursor
 * 
 *     cursor.close();
 *
 *     // delete the file
 *
 *     file.delete();
 * </pre></p>
 *
 * @see java.util.Iterator
 * @see xxl.core.cursors.Cursor
 * @see xxl.core.cursors.sources.io.InputStreamCursor
 * @see java.io.File
 */
public class FileInputCursor extends InputStreamCursor {

	/**
	 * Constructs a new file-input cursor that depends on the specified file and
	 * uses the specified converter in order to read out the serialized objects.
	 * An internal buffer of size <tt>bufferSize</tt> is used for the file input.
	 *
	 * @param converter the converter that is used for reading out the serialized
	 *        objects of this iteration.
	 * @param file the file that contains the serialized objects of this
	 *        iteration.
	 * @param bufferSize the size of the buffer that is used for the file input.
	 */
	public FileInputCursor(Converter converter, File file, int bufferSize) {
		super(null, converter);
		try {
			input = new DataInputStream(
				new BufferedInputStream(
					new FileInputStream(file),
					bufferSize
				)
			);
		}
		catch (IOException ie) {
			throw new WrappingRuntimeException(ie);
		}
	}

	/**
	 * Constructs a new file-input cursor that depends on the specified file and
	 * uses the specified factory method for initializing an object of this
	 * iteration before the file is read out. When using this constructor the
	 * objects of the iteration must implement the
	 * {@link xxl.core.io.Convertable convertable} interface. An internal buffer
	 * of size <tt>bufferSize</tt> is used for the file input. This constructor
	 * is equivalent to the call of
	 * <pre>
	 *     new FileInputIterator(new ConvertableConverter(newObject), file, bufferSize)
	 * </pre>
	 *
	 * @param newObject a factory method that is used for initializing an object
	 *        of this iteration before the file is read out.
	 * @param file the file that contains the serialized objects of this
	 *        iteration.
	 * @param bufferSize the size of the buffer that is used for the file input.
	 */
	public FileInputCursor(Function newObject, File file, int bufferSize) {
		this(new ConvertableConverter(newObject), file, bufferSize);
	}

	/**
	 * Constructs a new file-input cursor that depends on the specified file and
	 * uses the specified converter in order to read out the serialized objects.
	 * This constructor is equivalent to the call of
	 * <pre>
	 *     new FileInputIterator(converter, file, 4096)
	 * </pre>.
	 *
	 * @param converter the converter that is used for reading out the serialized
	 *        objects of this iteration.
	 * @param file the file that contains the serialized objects of this
	 *        iteration.
	 */
	public FileInputCursor(Converter converter, File file) {
		this(converter, file, 4096);
	}

	/**
	 * Constructs a new file-input cursor that depends on the specified file and
	 * uses the specified factory method for initializing an object of this
	 * iteration before the file is read out. When using this constructor the
	 * objects of the iteration must implement the
	 * {@link xxl.core.io.Convertable convertable} interface. This constructor is
	 * equivalent to the call of
	 * <pre>
	 *     new FileInputIterator(new ConvertableConverter(newObject), file, 4096)
	 * </pre>.
	 *
	 * @param newObject a factory method that is used for initializing an object
	 *        of this iteration before the file is read out.
	 * @param file the file that contains the serialized objects of this
	 *        iteration.
	 */
	public FileInputCursor(Function newObject, File file) {
		this(newObject, file, 4096);
	}

	/**
	 * The main method contains some examples to demonstrate the usage and the
	 * functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to submit
	 *        parameters when the main method is called.
	 */
	public static void main(String[] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create a new file
		
		File file = new File("file.dat");
		// catch IOExceptions
		
		try {
			
			// create a random access file on that file
			
			java.io.RandomAccessFile output = new java.io.RandomAccessFile(file, "rw");
			
			// write some data to that file
			
			output.writeUTF("Some data.");
			output.writeUTF("More data.");
			output.writeUTF("Another bundle of data.");
			output.writeUTF("A last bundle of data.");
			
			// close the random access file
			
			output.close();
		}
		catch (Exception e) {
			System.out.println("An error occured.");
		}
		
		// create a new file input iterator with ...
		
		FileInputCursor cursor = new FileInputCursor(
			
			// a string converter
			
			xxl.core.io.converters.StringConverter.DEFAULT_INSTANCE,
			
			// the created file
			
			file
		);
		
		// open the cursor
		
		cursor.open();
		
		// print all elements of the iterator
		
		while (cursor.hasNext())
			System.out.println(cursor.next());
		System.out.println();
		
		// close the cursor
		
		cursor.close();
		
		// delete the file
		file.delete();
	}
}
