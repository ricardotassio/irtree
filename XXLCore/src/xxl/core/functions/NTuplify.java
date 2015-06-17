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
package xxl.core.functions;

/**
 * A binary function that creates a tuple represented as an
 * Object[]. The arguments can be a simple Object or an
 * Object[]. The results is always an Object[]. <br>
 * This class is typically used for creating the resulting 
 * tuples in join-trees. 
 *
 */
public class NTuplify extends Function {

	/**
	 * This instance can be used for getting a default instance of
	 * NTuplify. It is similar to the <i>Singleton Design
	 * Pattern</i> (for further details see Creational Patterns, Prototype
	 * in <i>Design Patterns: Elements of Reusable Object-Oriented
	 * Software</i> by Erich Gamma, Richard Helm, Ralph Johnson, and John
	 * Vlissides) except that there are no mechanisms to avoid the
	 * creation of other instances of NTuplify.
	 */
	public static final NTuplify DEFAULT_INSTANCE = new NTuplify();

	/**
	 * A binary method that creates a tuple represented as an
	 * Object[]. The arguments can be a simple Object or an
	 * Object[]. The results is always an Object[].
	 *
	 * @param object1 First Object/Object[].
	 * @param object2 Second Object/Object[].
	 * @return The tuple (Object[]) containing the elements of object1 and object2. 
	 */
	public Object invoke(Object object1, Object object2) {
		if (!(object1 instanceof Object[]))
				object1 = new Object[]{object1};
		if(!(object2 instanceof Object[])) 
			object2 = new Object[]{object2};
		Object[]oa1 = (Object[])object1;
		Object[]oa2 = (Object[])object2;
		Object[] result = new Object[oa1.length + oa2.length];
		System.arraycopy(oa1, 0, result, 0, oa1.length);
		System.arraycopy(oa2, 0, result, oa1.length, oa2.length);
		return result;
	}
	
	/**
	 * This method has been overwritten in order to avoid wrong usage.
	 * It will always throw an UnsupportedOperationException.
	 *  
	 * @param args arguments
	 * @return always throws an UnsupportedOperationException
	 */
	public Object invoke(Object[] args) {
		throw new UnsupportedOperationException("Please calls invoke(Object, Object)!");	
	}

}
