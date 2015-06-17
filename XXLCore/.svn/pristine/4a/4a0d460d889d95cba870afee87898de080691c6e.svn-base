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

package xxl.core.comparators;

import java.util.Comparator;

/** 
 * This class provides some useful static methods for dealing with comparators.
 */

public class Comparators {

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private Comparators(){}

	/** 
	 * Returns a {@link java.util.Comparator comparator} able to handle <TT>null</TT> values.
	 * A flag controls the position of null values concerning the induced
	 * ordering of the given comparator.
	 * If the flag is true the null values will be positioned before all other values
	 * and vice versa.
	 * 
	 * @param c internally used {@link java.util.Comparator comparator} for objects that are not <TT>null</TT>
	 * @param flag determines the position of null values
	 * 
	 * @return a {@link java.util.Comparator comparator} able to handle null values
	 */
	public static Comparator newNullSensitiveComparator( final Comparator c, boolean flag){
		return flag ?
			(Comparator) new Comparator(){
				public int compare( Object o1, Object o2){
					return (( o1 == null) & (o2 == null)) ? 0 : (
								(o1 == null) ? -1 : (
									(o2 == null) ? 1 : c.compare(o1, o2)
								)
							);
				}
			}
		:
			(Comparator) new Comparator(){
				public int compare( Object o1, Object o2){
					return (( o1 == null) & (o2 == null)) ? 0 : (
								(o1 == null) ? 1 : (
									(o2 == null) ? -1 : c.compare(o1, o2)
								)
							);
				}
			}
		;
	}

	/** 
	 * Returns a {@link java.util.Comparator comparator} able to handle <TT>null</TT> values.
	 * Null values will be positioned before all other values.
	 * 
	 * @param c internally used {@link java.util.Comparator comparator} for objects 
	 * 		  that are not <TT>null</TT>
	 * 
	 * @return a {@link java.util.Comparator comparator} able to handle null values
	 */
	public static Comparator newNullSensitiveComparator( Comparator c){
		return newNullSensitiveComparator( c, true);
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		  submit parameters when the main method is called.
	 */
	public static void main(String[] args) {

		/*********************************************************************/
		/*                            Example 1                             */
		/*********************************************************************/
		// example to newNullSensitiveComparator( Comparator, boolean)
		Comparator c = newNullSensitiveComparator( new ComparableComparator(), false);
		/*
		Object [] c1 = new Object[]{ new Integer(1), new Integer(2), new Integer(4), null};
		Object [] c2 = new Object[]{ new Integer(3), new Integer(4), new Integer(5), null};
		*/
		Object [] c1 = new Object[]{ "a1", "a2", "A3", null};
		Object [] c2 = new Object[]{ "a3", "A2", "a1", null};
	
		for ( int i = 0 ; i < c1.length ; i++){
			for( int j = 0; j < c2.length ; j++){
				System.out.println("compare ( " + c1[i] +", " + c2[j] + ")=" + c.compare(c1[i],c2[j]));
			}
		}

	}
}
