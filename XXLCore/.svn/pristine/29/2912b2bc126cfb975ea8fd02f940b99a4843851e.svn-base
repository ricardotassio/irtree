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
package xxl.core.spatial;

import xxl.core.io.converters.ConvertableConverter;
import xxl.core.io.converters.Converter;
import xxl.core.io.converters.IntegerConverter;
import xxl.core.math.Maths;
import xxl.core.spatial.rectangles.DoublePointRectangle;
import xxl.core.util.ConvertableTuple;

/**
 *	A KPE (key-pointer-element) stores a data Object and an ID. This is the key data-type for spatial data. 
 *  The data stored by objects of this class typically contains some sort of approximation like e.g. a
 *  Minimum Bounding Rectangle (MBR) or a point.
 *
 *	The ID can be an Integer or a reference pointing to the exact representation of the data Object.
 *	It is based on the {@link xxl.core.util.ConvertableTuple ConvertableTuple}.
 *
 *  The user has to provide a converter for the data object. If no Converter is provided the data object
 *  is assumed to be of type Convertable.
 *  
 *  @see xxl.core.util.ConvertableTuple
 *  @see xxl.core.spatial.KPEzCode
 *
 */
public class KPE extends ConvertableTuple {

	/** Creates a new KPE.
	 *
	 *	@param objects the objects to be stored by this KPE
	 *	@param dataConverter the converters to use for the objects
	*/
	public KPE(Object[] objects, Converter[] dataConverter){
		super(objects, dataConverter);
	}

	/** Creates a new KPE.
	 *
	 *	@param data The data to be stored by this KPE (assumed to implement the Convertable interface).
	 *	@param ID The ID-Object wrapped by this KPE.
	 *	@param IDConverter The Converter for serializing the ID-Object wrapped by this KPE.
	*/
	public KPE(Object data, Object ID, Converter IDConverter){
		super(new Object[]{ data, ID }, new Converter[]{ ConvertableConverter.DEFAULT_INSTANCE, IDConverter} );
	}

	/** Creates a new KPE. The IDConverter is set to <tt>IntegerConverter.DEFAULT_INSTANCE</tt>.
	 *
	 *	@param data The data to be stored by this KPE.
	 *	@param ID The ID-Object wrapped by this KPE.
	 */
	public KPE(Object data, Object ID){
		this(data, ID, IntegerConverter.DEFAULT_INSTANCE);
	}

	/** Creates a new KPE. The ID is set to {@link xxl.core.math.Maths#zero}. The IDConverter is set to <tt>IntegerConverter.DEFAULT_INSTANCE</tt>.
	 *
	 *	@param data The data to be stored by this KPE.
	 */
	public KPE(Object data){
		this(data, Maths.zero);	
	}

	/** Returns a physical copy of the given KPE.
	 *
	 * @param k the KPE to use as a template for the new instance
	 */
	public KPE(KPE k){
		super(k);
	}

	/** Creates a new KPE. This constructor assigns a DoublePoint based rectangle of dimension dim to the data field.
	 *  The ID is set to {@link xxl.core.math.Maths#zero}.
	 *
	 *  @param dim the dimension of the rectangle to create
	 *	@param IDConverter The Converter for serializing the ID-Object wrapped by this KPE.
	 */
	public KPE(int dim, Converter IDConverter){
		this(new DoublePointRectangle(dim), Maths.zero, IDConverter);
	}

	/** Creates a new KPE of dimension dim.
	 *
	 * @param dim the dimension of the rectangle to create
	 */
	public KPE(int dim){
		this(dim, null);
	}

	/**	Get method for backward-compatibility:
	 *
	 * @return the data stored by this KPE is returned
	 */
	public Object getData(){
		return get(0);	
	}

	/**	Set method for backward-compatibility:
	 *
	 * @param data sets the data stored by this KPE
	 * @return returns the object given
	 * 
	 */
	public Object setData(Object data){
		return set(0, data);	
	}
	
	/**	Get method for backward-compatibility:
	 *
	 * @return the ID stored by this KPE is returned
	 */
	public Object getID(){
		return get(1);	
	}	

	/**	Set method for backward-compatibility:
	 *
	 * @param ID sets the ID stored by this KPE
	 */
	public void setID(Object ID){
		set(1, ID);
	}
	/** Returns a physical copy of this KPE.
	 * 
	 * @return returns a physical copy of this KPE 
	 */
	public Object clone () {
		return new KPE(this);
	}

	/** Returns a string representation of the object.
	 * @return a string representation of the object.
	 */
	public String toString(){
		return new String(get(0).toString()+"ID:\t"+get(1)+"\n");
	}
}
