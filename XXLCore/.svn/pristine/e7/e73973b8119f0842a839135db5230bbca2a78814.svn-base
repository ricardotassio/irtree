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

/**
 * This class supports mathematical functions.
 */
public class MyMath
{
	
	/**
	 * Round the given value (format: x.y). If the value is less x.5 then x is returned
	 * otherwise x+1.
	 * @param value which should be round.
	 * @return the round value.
	 */
	public static int round(float value)
	{
		return Math.round(value);
	}	//end round(float value)
	

	/**
	 * Round the given value down.
	 * @param value which should be round down.
	 * @return the round down value.
	 */
	public static int roundDown(float value)
	{
		return (int)value;
	}	//end roundDown(float value)
	
	
	/**
	 * Round the given value up.
	 * @param value which should be round up.
	 * @return round up value.
	 */
	public static int roundUp(float value)
	{
		if ((int)value == value)
			return (int)value;
		return (int)Math.floor(value+1);
	}	//end roundUp(float value)
	

	/**
	 * Round the given value (format: x.y). If the value is less x.5 then x is returned
	 * otherwise x+1.
	 * @param value which should be round.
	 * @return the round value.
	 */
	public static long round(double value)
	{
		return Math.round(value);
	}	//end round(double value)
	

	/**
	 * Round the given value down.
	 * @param value which should be round down.
	 * @return the round down value.
	 */
	public static long roundDown(double value)
	{
		return (long)value;
	}	//end roundDown(double value)
	
	
	/**
	 * Round the given value up.
	 * @param value which should be round up.
	 * @return the round up value.
	 */
	public static long roundUp(double value)
	{
		if ((long)value == value)
			return (long)value;
		return (long)Math.floor(value+1);
	}	//end roundUp(double value)


	/**
	 * Can be used to test this class.
	 * 
     * @param args the arguments
	 */	
	public static void main(String[] args)
	{
		long a = 625;
		int b = 512;
		int c = 2;
		System.out.println(roundUp((double)a/b*c));
		for (double i=0; i < 24; i++)
			//System.out.println(i+") "+" value "+(i/7)+" roundDown "+roundDown(i/7));
			//System.out.println(i+") "+" value "+(i/7)+" roundUp "+roundUp(i/7));
			System.out.println(i+") "+" value "+(i/7)+" round "+round(i/7));
	}

}
