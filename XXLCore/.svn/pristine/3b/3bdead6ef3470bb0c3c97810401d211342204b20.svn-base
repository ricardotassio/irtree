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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import xxl.core.io.Convertable;
import xxl.core.util.WrappingRuntimeException;

/**
 * This class contains various methods connected to the serialization of
 * objects. The toByteArray methods return a serialized object as byte
 * array and the sizeOf methods determine the length of a serialized
 * object without materializing the bytes into the memory.<p>
 *
 * The documentation of the methods contained in this class includes a
 * brief description of the implementation. Such descriptions should be
 * regarded as implementation notes, rather than parts of the
 * specification. Implementors should feel free to substitute other
 * algorithms, as long as the specification itself is adhered to.
 *
 * @see ByteArrayOutputStream
 * @see DataOutputStream
 * @see IOException
 * @see OutputStream
 */
public class Converters {

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private Converters () {
	}

	/**
	 * Returns a Converter which can convert a java type. Only
	 * the name of the java type has to be given. If there is no
	 * converter available then this method returns a ConvertableConverter
	 * (and hopes that the object to be converted is convertable).
	 *
	 * @param javaTypeName String which represents a java classname.
	 * @return the desired converter
	 */
	public static Converter getConverterForJavaType(String javaTypeName) {
		if (javaTypeName.equals("java.lang.String")) 		return new StringConverter();
		else if(javaTypeName.equals("java.math.BigDecimal"))	return new BigDecimalConverter();
		else if(javaTypeName.equals("java.lang.Integer"))	return new IntegerConverter();
		else if(javaTypeName.equals("java.lang.Short"))		return new ShortConverter();
		else if(javaTypeName.equals("java.lang.Boolean"))	return new BooleanConverter();
		else if(javaTypeName.equals("java.lang.Byte"))		return new ByteConverter();
		else if(javaTypeName.equals("java.lang.Long"))		return new LongConverter();
		else if(javaTypeName.equals("java.lang.Float"))		return new FloatConverter();
		else if(javaTypeName.equals("java.lang.Double")) 	return new DoubleConverter();
		else if(javaTypeName.equals("byte[]")) 			return new ByteArrayConverter();
		else return new ConvertableConverter();
	}
	
	/**
	 * Creates a newly allocated byte array that contains the
	 * serialization of the spezified object. Its size is the size of the
	 * serialized object as returned by
	 * <tt>sizeOf(converter, object)</tt>. If the creation of the byte
	 * array fails (e.g., an <tt>IOException</tt> is thrown) this method
	 * returns <tt>null</tt>.<br>
	 * This implementation creates a new <tt>ByteArrayOutputStream</tt>
	 * that is wrapped by a new <tt>DataOutputStream</tt>. Thereafter the
	 * specified object is written to the <tt>DataOutputStream</tt> using
	 * its write method of the converter and the result of the
	 * <tt>ByteArrayOutputStream</tt>'s
	 * {@link ByteArrayOutputStream#toByteArray() toByteArray} method is
	 * returned.
	 *
	 * @param converter Converter to be used.
	 * @param object The object which is serialized.
	 * @return the serialized state (attributes) of the specified object,
	 *         as a byte array.
	 */
	public static byte [] toByteArray (Converter converter, Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream daos = new DataOutputStream(baos);
			converter.write(daos, object);
			return baos.toByteArray();
		}
		catch (IOException ie) {
			return null;
		}
	}

	/**
	 * Creates a newly allocated byte array that contains the
	 * serialization of the spezified object. Its size is the size of the
	 * serialized object as returned by
	 * <tt>sizeOf(converter, object)</tt>. If the creation of the byte
	 * array fails (e.g., an <tt>IOException</tt> is thrown) this method
	 * returns <tt>null</tt>.<br>
	 * This implementation creates a new <tt>ByteArrayOutputStream</tt>
	 * that is wrapped by a new <tt>DataOutputStream</tt>. Thereafter the
	 * specified object is written to the <tt>DataOutputStream</tt> using
	 * its write method of the converter and the result of the
	 * <tt>ByteArrayOutputStream</tt>'s
	 * {@link ByteArrayOutputStream#toByteArray() toByteArray} method is
	 * returned.
	 *
	 * @param converter The converter which is used.
	 * @param object The Object which is serialized.
	 * @return the serialized state (attributes) of the specified object,
	 *         as a byte array.
	 */
	public static byte [] toByteArray (SizeConverter converter, Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(converter.getSerializedSize(object));
			DataOutputStream daos = new DataOutputStream(baos);
			converter.write(daos, object);
			return baos.toByteArray();
		}
		catch (IOException ie) {
			return null;
		}
	}

	/**
	 * Creates a newly allocated byte array that contains the
	 * serialization of the spezified convertable object. Its size is the
	 * size of the serialized object as returned by
	 * <tt>sizeOf(convertable)</tt>. If the creation of the byte array
	 * fails (e.g., an <tt>IOException</tt> is thrown) this method returns
	 * <tt>null</tt>.<br>
	 * This implementation creates a new <tt>ByteArrayOutputStream</tt>
	 * that is wrapped by a new <tt>DataOutputStream</tt>. Thereafter the
	 * specified object is written to the <tt>DataOutputStream</tt> using
	 * its write method and the result of the
	 * <tt>ByteArrayOutputStream</tt>'s
	 * {@link ByteArrayOutputStream#toByteArray() toByteArray} method is
	 * returned.
	 *
	 * @param convertable The Object which is serialized.
	 * @return the serialized state (attributes) of the specified
	 *         convertable object, as a byte array.
	 */
	public static byte [] toByteArray (Convertable convertable) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream daos = new DataOutputStream(baos);
			convertable.write(daos);
			return baos.toByteArray();
		}
		catch (IOException ie) {
			return null;
		}
	}

	/**
	 * Returns the number of bytes needed to serialize the state (the
	 * attributes) of the specified object. If the measuring fails this
	 * method should return <tt>-1.</tt><br>
	 * This implementation creates an anonymous local class that
	 * implements the interface <tt>OutputStream</tt>. The write method of
	 * this local class only increases a counter and the hashCode method
	 * returns it. Thereafter the specified object is written to the
	 * <tt>OutputStream</tt> using the write method of the converter. If
	 * this call succeeds the hashCode of the <tt>OutputStream</tt> (the
	 * number of bytes written to the output stream) is returned otherwise
	 * <tt>-1</tt> is returned.
	 *
	 * @param converter The converter which is used.
	 * @param object The Object which is serialized.
	 * @return the number of bytes needed to serialize the state (the
	 *         attributes) of the specified object or <tt>-1</tt> if the
	 *         measuring fails.
	 */
	public static int sizeOf (Converter converter, Object object) {
		try {
			OutputStream output = new OutputStream() {
				int counter = 0;
				public void write (int b) {
					counter++;
				}
				public int hashCode () {
					return counter;
				}
			};
			converter.write(new DataOutputStream(output), object);
			return output.hashCode();
		}
		catch (IOException e) {
			return -1;
		}
	}

	/**
	 * Returns the number of bytes needed to serialize the state (the
	 * attributes) of the specified convertable object. If the measuring
	 * fails this method should return <tt>-1.</tt><br>
	 * This implementation creates an anonymous local class that
	 * implements the interface <tt>OutputStream</tt>. The write method of
	 * this local class only increases a counter and the hashCode method
	 * returns it. Thereafter the specified object is written to the
	 * <tt>OutputStream</tt> using its write method. If this call succeeds
	 * the hashCode of the <tt>OutputStream</tt> (the number of bytes
	 * written to the output stream) is returned otherwise <tt>-1</tt> is
	 * returned.
	 *
	 * @param convertable The Object which is serialized.
	 * @return the number of bytes needed to serialize the state (the
	 *         attributes) of the specified convertable object or
	 *         <tt>-1</tt> if the measuring fails.
	 */
	public static int sizeOf (Convertable convertable) {
		try {
			OutputStream output = new OutputStream() {
				int counter = 0;

				public void write (int b) {
					counter++;
				}

				public int hashCode () {
					return counter;
				}
			};
			convertable.write(new DataOutputStream(output));
			return output.hashCode();
		}
		catch (IOException e) {
			return -1;
		}
	}

	/**
	 * Reads a single object from the given file and returns it. The specified
	 * converter is used for reading the object.<br>
	 * This implementation opens a new data input stream on the given file and
	 * uses the specified converter to read the desired object. The specified
	 * object is passed to the read method of the converter.<br>
	 * <b>Note</b> that every time this method is called, a new input stream is
	 * opened and closed on the given file. Therefore this method should not be
	 * used for continuous reading of objects.
	 *
	 * @param file the file from which to read the object.
	 * @param object the object to read. If (<tt>object==null</tt>) the converter
	 *        should create a new object, else the specified object is filled with
	 *        the read data.
	 * @param converter the converter used for reading the object.
	 * @return The Object which was read.
	 * @throws WrappingRuntimeException when any exception is throws during the
	 *         object reading process.
	 */
	public static Object readSingleObject (File file, Object object, Converter converter) {
		DataInputStream din = null;
		try {
			din = new DataInputStream(new FileInputStream(file));
			object = converter.read(din, object);
			din.close();
		}
		catch (Exception e) {
			throw new WrappingRuntimeException(e);
		}
		return object;
	}

	/**
	 * Writes a single object to the given file. The specified converter is used
	 * for writing the object.<br>
	 * This implementation opens a new data output stream on the given file and
	 * uses the specified converter to write the given object. The specified
	 * object is passed to the write method of the converter.<br>
	 * <b>Note</b> that every time this method is called, a new output stream is
	 * opened and closed on the given file. Therefore this method should not be
	 * used for continuous writing of objects.
	 *
	 * @param file the file to which to write the object.
	 * @param object the object to write.
	 * @param converter the converter used for writing the object.
	 * @throws WrappingRuntimeException when any exception is throws during the
	 *         object writing process.
	 */
	public static void writeSingleObject (File file, Object object, Converter converter) {
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(new FileOutputStream(file));
			converter.write(dos, object);
			dos.close();
		}
		catch (Exception e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Reads a single object from the given file and returns it. The specified
	 * converter is used for reading the object.<br>
	 * This implementation is equivalent to the call of
	 * <tt>readSingleObject(file, object, ConvertableConverter.DEFAULT_INSTANCE)</tt>.<br>
	 * <b>Note</b> that every time this method is called, a new input stream is
	 * opened and closed on the given file. Therefore this method should not be
	 * used for continuous reading of objects.
	 *
	 * @param file the file from which to read the object.
	 * @param object the object to read.
	 * @return The Object which was read.
	 * @see ConvertableConverter
	 */
	public static Object readSingleObject (File file, Convertable object) {
		return readSingleObject(file, object, ConvertableConverter.DEFAULT_INSTANCE);
	}

	/**
	 * Writes a single object to the given file. The specified converter is used
	 * for writing the object.<br>
	 * This implementation is equivalent to the call of
	 * <tt>writeSingleObject(file, object, ConvertableConverter.DEFAULT_INSTANCE)</tt>.<br>
	 * <b>Note</b> that every time this method is called, a new output stream is
	 * opened and closed on the given file. Therefore this method should not be
	 * used for continuous writing of objects.
	 *
	 * @param file the file to which to write the object.
	 * @param object the object to write.
	 * @see ConvertableConverter
	 */
	public static void writeSingleObject (File file, Convertable object) {
		writeSingleObject(file, object, ConvertableConverter.DEFAULT_INSTANCE);
	}
}
