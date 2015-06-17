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

import xxl.core.functions.Function;

/**
 * Converts an object with multiple convertable fields into a byte representation
 * and vice versa.
 */
public class MultiConverter extends Converter {

	/**
	 * The converters that is wrapped. The converter must be able to read
	 * and write uniform objects.
	 */
	protected Converter converter[];

	/**
	 * A factory method that is used for creating the object to be read.
	 * This function will be invoked when the read method is called
	 * (even if there is an object specified to restore).
	 */
	protected Function createObject;
	
	/**
	 * A function which converts the components of the Object into
	 * an array of Objects.
	 */
	protected Function objectToObjectArray;

	/**
	 * Constructs a new UniformConverter that wraps the specified
	 * converter and uses the specified function as factory method.
	 *
	 * @param converter the converter to be wrapped.
	 * @param createObject a factory method that is used for initializing the
	 *       object to read when it is not specified.
	 * @param objectToObjectArray Function which converts the components of 
	 * 	the Object into an array of Objects.
	 */
	public MultiConverter (Converter converter[], Function createObject, Function objectToObjectArray) {
		this.converter = converter;
		this.createObject = createObject;
		this.objectToObjectArray = objectToObjectArray;
	}

	/**
	 * Reads the state (the attributes) for the specified object from the
	 * specified data input and returns the restored object. <br>
	 * This implementation calls the read method of the wrapped converter.
	 * When the specified object is <tt>null</tt> it is initialized by
	 * invoking the function (factory method).
	 *
	 * @param dataInput the stream to read data from in order to restore
	 *        the object.
	 * @param object the object to be restored. If the object is null it
	 *        is initialized by invoking the function (factory method).
	 * @return the restored object.
	 * @throws IOException if I/O errors occur.
	 */
	public Object read (DataInput dataInput, Object object) throws IOException {
		Object o[] = new Object[converter.length];
		for (int i=0; i<converter.length; i++)
			o[i] = converter[i].read(dataInput,null);
		return createObject.invoke(o);
	}

	/**
	 * Writes the state (the attributes) of the specified object to the
	 * specified data output. <br>
	 * This implementation calls the write method of the wrapped
	 * converter.
	 *
	 * @param dataOutput the stream to write the state (the attributes) of
	 *        the object to.
	 * @param object the object whose state (attributes) should be written
	 *        to the data output.
	 * @throws IOException includes any I/O exceptions that may occur.
	 */
	public void write (DataOutput dataOutput, Object object) throws IOException {
		Object o[] = (Object[]) objectToObjectArray.invoke(object);
		for (int i=0; i<converter.length; i++)
			converter[i].write(dataOutput, o[i]);
	}
	
	/**
	 * Simple example using the MultiConverter converting the elements of a Map.
	 * @param args Command line arguments are ignored here.
	 */
	public static void main(String args[]) {
		java.util.Map map = new java.util.HashMap();
		map.put(new String("Audi"),new Integer(27000));
		map.put(new String("Mercedes"),new Integer(30000));
		map.put(new String("BMW"),new Integer(29000));
		map.put(new String("VW"),new Integer(24000));
		
		System.out.println("Example");
		System.out.println("=======");

		java.util.Iterator it = map.entrySet().iterator();

		Converter conv = new MultiConverter(
			new Converter[] {StringConverter.DEFAULT_INSTANCE,IntegerConverter.DEFAULT_INSTANCE},
			xxl.core.collections.MapEntry.FACTORY_METHOD,
			xxl.core.collections.MapEntry.TO_OBJECT_ARRAY_FUNCTION
		);
		
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
		
		try {
			while (it.hasNext())
				conv.write(dos,it.next());
			dos.flush();
			
			java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
			java.io.DataInputStream dis = new java.io.DataInputStream(bais);
			
			for (int i=0; i<4; i++)
				System.out.println(conv.read(dis));
		}
		catch (java.io.IOException e) {
			throw new xxl.core.util.WrappingRuntimeException(e);
		}
	}
}
