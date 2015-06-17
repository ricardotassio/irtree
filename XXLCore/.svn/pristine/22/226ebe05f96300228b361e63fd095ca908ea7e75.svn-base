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
 * This class provides a converter that is able to read and write arrays
 * of <tt>boolean</tt> values. First the converter reads or writes the
 * length of the <tt>boolean</tt> array, if necessary. Thereafter the <tt>boolean</tt>
 * values are read or written (8 booleans per byte).<p>
 *
 * Example usage (1).
 * <pre>
 *     // create an boolean array
 *
 *     boolean [] array = {true, false, false, true};
 *
 *     // catch IOExceptions
 *
 *     try {
 *
 *         // create a byte array output stream
 *
 *         ByteArrayOutputStream output = new ByteArrayOutputStream();
 *
 *         // write array to the output stream
 *
 *         BooleanArrayConverter.DEFAULT_INSTANCE.write(new DataOutputStream(output), array);
 *
 *         // create a byte array input stream on the output stream
 *
 *         ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
 *
 *         // reset the array
 *
 *         array = null;
 *
 *         // read array from the input stream
 *
 *         array = (boolean[])BooleanArrayConverter.DEFAULT_INSTANCE.read(new DataInputStream(input));
 *
 *         // print the array
 *
 *         for (int i = 0; i < array.length; i++)
 *             System.out.println(array[i]);
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
public class BooleanArrayConverter extends SizeConverter {
	
	/**
	 * This instance can be used for getting a default instance of
	 * BooleanArrayConverter. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns, Prototype
	 * in <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of BooleanArrayConverter.
	 */
	public static final BooleanArrayConverter DEFAULT_INSTANCE = new BooleanArrayConverter();

	/**
	 * Determines the length of the conversion of a boolean array.
	 * @param withLengthInfo has to be true iff the length info of the array has to be stored.
	 * @param len length of the boolean array to be stored.
	 * @return length in bytes
	 */
	public static int getSizeForArray(boolean withLengthInfo, int len) {
		if (withLengthInfo)
			return 4+((len+7)>>3);  // >>3 <=> /8
		else
			return ((len+7)>>3);
	}

	/** 
	 * Determines the length of the array. If it is -1, then the 
	 * size information is also serialized.
	 */
	protected int arraySize;

	/**
	 * Constructors a BooleanArrayConverter which not necessarily serializes/deserializes the 
	 * length of the array.
	 * @param arraySize if -1 then the size is not serialized/deserialized. Else this
	 *	int value represents the number of booleans which are serialized/deserialized.
	 */
	public BooleanArrayConverter(int arraySize) {
		this.arraySize = arraySize;
	}

	/**
	 * Constructors a BooleanArrayConverter which also serializes/deserializes the 
	 * length of the array.
	 */
	public BooleanArrayConverter() {
		this(-1);
	}

	/**
	 * Reads an array of <tt>boolean</tt> values from the specified data
	 * input and returns the restored <tt>boolean</tt> array. <br>
	 *
	 * @param dataInput the stream to read the <tt>boolean</tt> array
	 *        from.
	 * @param object the <tt>boolean</tt> array to be restored. The size of 
	 *	the boolean array has to be sufficient! If this parameter is null,
	 *	then a new boolean array is constructed.
	 * @return the read array of <tt>boolean</tt> values.
	 * @throws IOException if I/O errors occur.
	 */
	public Object read (DataInput dataInput, Object object) throws IOException {
		boolean [] array = (boolean[]) object;
		int len = arraySize;
		if (arraySize==-1)
			len = dataInput.readInt();
		if (array==null)
			array = new boolean[len];
		byte b = 0;

		for (int i=0; i<len; i++) {
			if ((i&7)==0) // i mod 8 == 0
				b = dataInput.readByte();
			array[i] = b<0;
			b <<= 1;
		}
		return array;
	}

	/**
	 * Writes the specified array of <tt>boolean</tt> values to the
	 * specified data output. <br>
	 * This implementation first writes the length of the array to the
	 * data output. Thereafter the <tt>boolean</tt> values are written.
	 *
	 * @param dataOutput the stream to write the <tt>boolean</tt> array
	 *        to.
	 * @param object the <tt>boolean</tt> array that should be written to
	 *        the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (DataOutput dataOutput, Object object) throws IOException {
		boolean [] array = (boolean [])object;
		byte b = 0;
		int i = 0;
		int len = arraySize;

		if (arraySize==-1) {
			dataOutput.writeInt(array.length);
			len = array.length;
		}
		while (i<len) {
			b <<= 1;
			if (array[i])
				b++;
			if ((++i&7)==0) {
				dataOutput.writeByte(b);
				b = 0;
			}
		}
		if ((i&7)!=0) {
			b <<= 8-(i&7);
			dataOutput.write(b);
		}
	}

	/**
	 * Determines the size of the boolean array in bytes.
	 * @param o boolean array
	 * @return the size in bytes
	 * @see xxl.core.io.converters.SizeConverter#getSerializedSize(java.lang.Object)
	 */
	public int getSerializedSize(Object o) {
		boolean [] array = (boolean [])o;
		if (arraySize==-1)
			return getSizeForArray(true, array.length);
		else
			return getSizeForArray(false, arraySize);
	}

	/**
	 * Prints the array and tests it against the reconstructed array. This method
	 * is used by makeTest (for main method).
	 * 
	 * @param array the array to be tested.
	 * @param reconstructedArray the reconstructed array to be tested.
	 * @param len the number of elements from the first array that should be tested.
	 */
	private static void test(boolean[] array, boolean[] reconstructedArray, int len) {
		// print the array
		if (len==-1) {
			if (reconstructedArray.length!=array.length)
				throw new RuntimeException("Error in BooleanArrayConverter (length is not correct)");
			len = array.length;
		}
		
		for (int i = 0; i<len; i++) {
			System.out.print(reconstructedArray[i]+" ");
			if (array[i]!=reconstructedArray[i])
				throw new RuntimeException("Error in BooleanArrayConverter");
		}
		System.out.println();
	}

	/**
	 * Test which is called from the main method.
	 * @param array the array to be tested.
	 */
	private static void makeTest(boolean array[]) {
		// catch IOExceptions
		try {
			boolean reconstructedArray[];
			
			System.out.println("Converting an array with "+array.length+" components");
			// create a byte array output stream
			java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
			// write array to the output stream
			BooleanArrayConverter.DEFAULT_INSTANCE.write(new java.io.DataOutputStream(output), array);
			// create a byte array input stream on the output stream
			java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(output.toByteArray());
			// read array from the input stream
			reconstructedArray = (boolean[])BooleanArrayConverter.DEFAULT_INSTANCE.read(new java.io.DataInputStream(input));

			test(array,reconstructedArray,-1);

			// close the streams after use
			input.close();
			output.close();

			System.out.println("Take the array and convert only 3 components");
			// create a byte array output stream
			output = new java.io.ByteArrayOutputStream();
			// write array to the output stream
			Converter c = new BooleanArrayConverter(3); // only transforms 3 booleans
			c.write(new java.io.DataOutputStream(output), array);
			// create a byte array input stream on the output stream
			input = new java.io.ByteArrayInputStream(output.toByteArray());

			// read array from the input stream
			reconstructedArray = new boolean[4]; // Tricky. Only 3 fields are used.
			c.read(new java.io.DataInputStream(input),reconstructedArray);

			test(array,reconstructedArray,3);
			
			// close the streams after use
			input.close();
			output.close();
		}
		catch (IOException ioe) {
			throw new xxl.core.util.WrappingRuntimeException(ioe);
		}
	}

	/**
	 * The main method contains some examples how to use a
	 * BooleanArrayConverter. It can also be used to test the
	 * functionality of a BooleanArrayConverter.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args) {

		//////////////////////////////////////////////////////////////////
		//                      Usage example (1).                      //
		//////////////////////////////////////////////////////////////////

		// create an boolean array
		System.out.println("Test 1:");
		makeTest(new boolean[]{false, true, false, true});
		System.out.println("Test 2:");
		makeTest(new boolean[]{true, false, false, true});
		System.out.println("Test 3:");
		makeTest(new boolean[]{true, false, false, true, true, false, false});
		System.out.println("Test 4:");
		makeTest(new boolean[]{true, false, false, true, true, false, false, true});
		System.out.println("Test 5:");
		makeTest(new boolean[]{false, true, false, false, true, true, false, false, true});
		System.out.println("Test 6:");
		makeTest(new boolean[]{true, false, false, true, false, false, false, true, true, false});
		System.out.println("Test 7:");
		makeTest(new boolean[]{
			true,  false, false, true,  true,  false, false, true,
			false, false, true,  false, false, false, true,  true
		});
		System.out.println("Test 8:");
		makeTest(
			new boolean[] {
				true, false, false, true, false, false, false, true, true, false,
				false,false, true,  false,false, false, true,  true, false,true,
				true, false, false, true, false, false, false, true, true, false,
				true, false, false, true, false, false, false, true, true, false,
				false,false, true,  false,false, false, true,  true, false,true,
				true, false, false, true, false, false, false, true, true, false,
				true, false, false, true, false, false, false, true, true, false,
				true, false, false, true, false, false, false, true, true, false,
				true, false, false, true, false, false, false, true, true, false
			}
		);
	}
}
