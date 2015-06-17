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

package xxl.core.io.fat.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Conversion routines byte-Array --> short, int, long.
 * Little endian byte ordering is assumed:
 * 00 01 00 00 = 1, 00 00 00 01 = 65536, 00 00 80 00 = -2147483648
 */
public class ByteArrayConversionsLittleEndian
{
	
	/** 
	 * Do not allow instances of this class 
	 */
	private ByteArrayConversionsLittleEndian()
	{}	//end constructor


	/**
	 * Convert the given byte to a short.
	 * @param b byte to convert.
	 * @return short.
	 */	
	public static short conv255(byte b)
	{
		return (b<0)?(short) (256+ b):(short)b;
	}	//end conv255(byte b)
	
	
	/**
	 * Convert the given byte to a short.
	 * @param b byte to convert.
	 * @return short.
	 */	
	public static short conv127(byte b)
	{
		return (short) (b&127);
	}	//end conv127(byte b)
	
	
	/**
	 * Convert the byte array to a short. It is assumed that the array has a length of 2.
	 * @param b the byte array to convert.
	 * @return short.
	 */
	public static short convShort(byte b[])
	{
		int zw = (conv127(b[1])<<8) | conv255(b[0]);
		if (b[1]<0) // Sign
			return (short) (zw - (1<<15));
		else
			return (short) zw;
	}	//end convShort(byte b[])
	
	
	/**
	 * Convert the two given byte values to a short.
	 * @param b0 the 'left' byte value.
	 * @param b1 the 'right' byte value.
	 * @return short.
	 */
	public static short convShort(byte b0, byte b1)
	{
		int zw = (conv127(b1)<<8) | conv255(b0);
		if (b1 < 0) // Sign
			return (short) (zw - (1<<15));
		else
			return (short) zw;
	}	//end convShort(byte b0, byte b1)
	
	
	/**
	 * Convert the given byte to a short value.
	 * @param b1 the byte to convert.
	 * @return short.
	 */
	public static short convShort(byte b1)
	{
		int zw = conv255(b1);
		return (short) zw;
	}	//end convShort(byte b1)
	

	/**
	 * Convert the byte array to a short value.
	 * @param b the byte array.
	 * @return short.
	 */	
	public static short convShortStream(byte b[])
	{
		try
		{
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			DataOutput out = new DataOutputStream(output);
			out.write(b,0,b.length);
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
			DataInput in = new DataInputStream(input);
			return in.readShort();
		}
		catch (IOException e)
		{
			return 0;
		}
	}	//end convShortStream(byte b[])


	/**
	 * Convert the given byte array to an int value. It is assumed that the byte
	 * array has a length of 4.
	 * @param b the byte array.
	 * @return int.
	 */	
	public static int convInt(byte b[])
	{
		int zw = (((((conv127(b[3])<<8) | conv255(b[2]))<<8) | conv255(b[1]))<<8) | conv255(b[0]);
		if (b[3]<0) // Sign
			return zw - (1<<31);
		else
			return zw;
	}
	
	
	/**
	 * Convert the given bytes to an int value.
	 * @param b0 the 'outer left' byte value.
	 * @param b1 the byte value at the right of b0.
	 * @param b2 the byte value at the right of b1.
	 * @param b3 the byte value at the right of b2.
	 * @return int.
	 */
	public static int convInt(byte b0, byte b1, byte b2, byte b3)
	{
		int zw = (((((conv127(b3)<<8) | conv255(b2))<<8) | conv255(b1))<<8) | conv255(b0);
		if (b3<0) // Sign
			return zw - (1<<31);
		else
			return zw;
	}	//end convInt(byte b0, byte b1, byte b2, byte b3)
	
	
	/**
	 * Convert the given bytes to an int value.
	 * @param b2 the 'left' byte value.
	 * @param b3 the 'right' byte value.
	 * @return int.
	 */
	public static int convInt(byte b2, byte b3)
	{
		int zw = ((conv255(b3))<<8) | conv255(b2);
		return zw;
	}	//end convInt(byte b2, byte b3)
	

	/**
	 * Convert the byte array to an int value.
	 * @param b the byte array.
	 * @return int.
	 */	
	public static int convIntStream(byte b[])
	{
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			DataOutput out = new DataOutputStream(output);
			out.write(b,0,b.length);
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
			DataInput in = new DataInputStream(input);
			return in.readInt();
		}
		catch (IOException e) {
			return 0;
		}
	}	//end convIntStream(byte b[])


	/**
	 * Convert the given byte array to a long value. It is assumed that the
	 * byte array has a length of 8.
	 * @param b the byte array.
	 * @return long.
	 */	
	public static long convLong(byte b[])
	{
		long zw = ((long) conv127(b[7])<<56) | ((long) conv255(b[6])<<48) | ((long) conv255(b[5])<<40) | ((long) conv255(b[4])<<32) |
			((long) conv255(b[3])<<24) | ((long) conv255(b[2])<<16) | ((long) conv255(b[1])<<8) | conv255(b[0]);
		if (b[7]<0) // Sign
			return  (zw - (((long) 1)<<63));
		else
			return  zw;
	}	//end convLong(byte b[])
	
	
	/**
	 * Convert the given byte values to a long value.
	 * @param b0 the 'outer left' byte value.
	 * @param b1 the byte value at the right of b0.
	 * @param b2 the byte value at the right of b1.
	 * @param b3 the byte value at the right of b2.
	 * @param b4 the byte value at the right of b3.
	 * @param b5 the byte value at the right of b4.
	 * @param b6 the byte value at the right of b5.
	 * @param b7 the byte value at the right of b6.
	 * @return long.
	 */
	public static long convLong(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7)
	{
		long zw = ((long) conv127(b7)<<56) | ((long) conv255(b6)<<48) | ((long) conv255(b5)<<40) | ((long) conv255(b4)<<32) |
			((long) conv255(b3)<<24) | ((long) conv255(b2)<<16) | ((long) conv255(b1)<<8) | conv255(b0);
		if (b7<0) // Vorzeichen
			return  (zw - (((long) 1)<<63));
		else
			return  zw;
	}	//end convLong(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7)
	
	
	/**
	 * Convert the given byte values to a long value.
	 * @param b4 the 'outer left' byte value.
	 * @param b5 the byte value at the right of b4.
	 * @param b6 the byte value at the right of b5.
	 * @param b7 the byte value at the right of b6.
	 * @return long.
	 */
	public static long convLong(byte b4, byte b5, byte b6, byte b7)
	{
		long zw = 	((long) conv255(b7)<<24) | ((long) conv255(b6)<<16) | ((long) conv255(b5)<<8) | conv255(b4);
		return  zw;
	}	//end convLong(byte b4, byte b5, byte b6, byte b7)
	

	/**
	 * Convert the byte array to an long value.
	 * @param b the byte array.
	 * @return long.
	 */	
	public static long convLongStream(byte b[])
	{
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			DataOutput out = new DataOutputStream(output);
			out.write(b,0,b.length);
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
			DataInput in = new DataInputStream(input);
			return in.readLong();
		}
		catch (IOException e) {
			return 0;
		}
	}	//end convLongStream(byte b[])
	
	
	/**
	 * Retun the string representation of the byte array. Starting at offset from (inclusive)
	 * until the index to (exclusive).
	 * @param b the byte array.
	 * @param from index (inclusive).
	 * @param to index (exclusive).
	 * @return a string representation of the byte array.
	 */
	public static String byteToString(byte[] b, int from, int to)
	{
		try
		{
			return new String(b, from, to-from, "ISO-8859-1");
		}
		catch(Exception e)
		{
			System.out.println(e);
			return "";
		}
	}	//end byteToString(byte[] bpb, int from, int to)
	
	
	/**
	 * Retun the unicode string representation of the byte array. Starting at
	 * offset from (inclusive) until the index to (exclusive).
	 * @param bpb the byte array.
	 * @param from index (inclusive).
	 * @param to index (exclusive).
	 * @return a unicode  string representation of the byte array.
	 */
	public static String unicodeByteToString(byte[] bpb, int from, int to)
	{
		try
		{
			return new String(bpb, from, to-from, "UTF-16LE");
		}
		catch(Exception e)
		{
			System.out.println(e);
			return "";
		}
	}	//end unicodeByteToString(byte[] bpb, int from, int to)


	/**
	 * Main method is containing usage examples.
	 * 
     * @param args the arguments
	 */	
	public static void main(String args[])
	{
		byte[] b = new byte[2];
		byte[] z = new byte[2];
		z[0] = 0;
		z[1] = 0;
		b[0] = (byte)0x0F;
		b[1] = (byte)0xFF;
		
		System.out.println("int 0xAFFF="+convInt(b[0], b[1]));
		
		System.out.println("short 0xAF="+convShort(b[0]));
		/*
		byte[] b = new byte[4];
		b[0] = (byte)0xFF;
		b[1] = (byte)0xFF;
		b[2] = (byte)0xFF;
		b[3] = (byte)0xFF;
		
		System.out.println(convInt(b[0], b[1], b[2], b[3]));
		*/
		/*byte[] b = new byte[8];
		short s;
		int i;
		long t1,t2;
		
		for (i=0; i<8 ; i++)
			b[i] = -1;

		s = (short) (((short)1)<<15);
		System.out.println("Test 1 << 15: "+s);

		System.out.println("Teste conv-Methoden");
		System.out.println(conv127((byte)-1));
		System.out.println(conv255((byte)-1));	
		System.out.println(conv255((byte)-128));	
		
		System.out.println("Test correctness of convShort-Methods");
		byte[] ba = new byte[2];
		for (int i1=-128 ; i1<=127 ; i1++) {
			for (int i2=-128 ; i2<=127 ; i2++) {
				ba[0] = (byte) i1;
				ba[1] = (byte) i2;
				if (convShort(ba) != convShortStream(ba))
					System.out.println("Fehler bei " +i1+", "+i2);
			}
		}
	
		// -1
		System.out.println("Test convShort-Method");
		s = convShort(b);
		System.out.println(s);
		
		s = convShort(new byte[]{0,-1});
		System.out.println(s);		

		System.out.print("Time is running (1E7 calls)...");
		t1 = System.currentTimeMillis();
		for (i=0 ; i<10000000 ; i++) {
			s = convShort(b);
		}
		t2 = System.currentTimeMillis();
		System.out.println("\nTime: "+(t2-t1)+" ms");
		
		System.out.println("Test conversion using streams");
		s = convShortStream(b);
		System.out.println(s);
		
		s = convShortStream(new byte[]{0,-1});
		System.out.println(s);				

		System.out.print("Time is running (1E6 calls)...");
		t1 = System.currentTimeMillis();
		for (i=0 ; i<1000000 ; i++) {
			s = convShortStream(b);
		}
		t2 = System.currentTimeMillis();
		System.out.println("\nTime: "+(t2-t1)+" ms");

		System.out.println("Testing convInt");		
		b = new byte[]{-1,-1,-1,-1};
		System.out.println(convInt(b)+" / "+convIntStream(b));
		b = new byte[]{-1,-1,-1,0};
		System.out.println(convInt(b)+" / "+convIntStream(b));
		b = new byte[]{0,-1,-1,-1};
		System.out.println(convInt(b)+" / "+convIntStream(b));

		System.out.println("Testing convLong");
		b = new byte[]{-1,-1,-1,-1,-1,-1,-1,-1};
		System.out.println(convLong(b)+" / "+convLongStream(b));
		b = new byte[]{-1,-1,-1,-1,-1,-1,-1,0};
		System.out.println(convLong(b)+" / "+convLongStream(b));
		b = new byte[]{0,-1,-1,-1,-1,-1,-1,-1};
		System.out.println(convLong(b)+" / "+convLongStream(b));

		System.out.println("Test finished");
		*/
	}
}
