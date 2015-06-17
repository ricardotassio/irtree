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

package xxl.core.relational;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import xxl.core.functions.Function;
import xxl.core.io.converters.BooleanConverter;
import xxl.core.io.converters.Converter;
import xxl.core.io.converters.Converters;
import xxl.core.relational.metaData.AssembledResultSetMetaData;
import xxl.core.relational.metaData.MetaData;
import xxl.core.relational.Tuples;
import xxl.core.util.WrappingRuntimeException;

/**
 * This class is a converter for Tuple which converts a Tuple into a 
 * byte value in order to read or write a Tuple. The meta data
 * is not converted and has to be given to the constructor of a
 * TupleConverter.
 */
public class TupleConverter extends Converter {
 	/**
	 * ResulSetMetaData returns the number of columns and their types. 
	 */
 	protected ResultSetMetaData rm;
 	
 	/**
 	 * A ConverterArray can contain different Converters as 
 	 * elements for each column of a Tuple.
 	 */
 	protected Converter[] converter;
 	
 	/**
 	 * BooleanConverter is used to save, if the given column of the Tuple
 	 * is a null value or not.
 	 */
 	protected BooleanConverter nullwert;
	
	/**
	 * the number of Columns in a Tuple
	 */
	protected int size;
	
	/**
	 * Factory which is used, to construct a new Tuple object.
	 */
	protected Function createResTuple;
	
	/** 
	 * Creates a TupleConverter.
	 * @param rm metadata that is needed.
	 * @param createResTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. xxl.core.relational.ArrayTuple.FACTORY_METHOD
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, xxl.relational.ArrayTuple.FACTORY_METHOD is used.
	 */
	public TupleConverter (ResultSetMetaData rm, Function createResTuple) {
		this.rm = rm;
		this.createResTuple = createResTuple;
		
		try{
			size = rm.getColumnCount();
			converter = new Converter[size];
			// Construction of the Converter array
			for(int i=0;i<size;i++)
				setConverter(i,rm.getColumnType(i+1));
			nullwert=new BooleanConverter();
		}
		catch(SQLException e){
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Creates a TupleConverter.
	 * @param rm metadata that is needed.
	 */
	public TupleConverter (ResultSetMetaData rm) {
		this(rm,xxl.core.relational.ArrayTuple.FACTORY_METHOD);
	}

	/**
	 * This method sets a Converter for the specified column of the tuple.
	 * @param index the column number, whose Converter is searched now.
	 * @param type integer number from java.sql.Types
	 * @throws IndexOutOfBoundsException Throws an {@link java.lang.IndexOutOfBoundsException} if
	 * 	the index is negative or greater than or equal to <code>length()</code>.
	 */
	public void setConverter (int index, int type) throws IndexOutOfBoundsException{
		int position = MetaData.searchSQLtypesArray(type);
		converter[index] = Converters.getConverterForJavaType(MetaData.javaSQLtypes[position].getClassName());
	}
	
	/**
	 * This method returns the Converter used for a special column of the tuple.
	 *
	 * @param index the converter is stored at the specified index.
	 * @return Returns Converter, which is stored at the given Index.
	 * @throws IndexOutOfBoundsException Throws an 
	 * {@link java.lang.IndexOutOfBoundsException} if
	 * the index is negative or greater than or equal to <code>length()</code>.
	 */
	public Converter getConverter (int index) throws IndexOutOfBoundsException{
		return converter[index];
	}
	
	/**
	 * Writes the byte value of a tuple to the specified DataOutput.
	 * @param output the Stream, where to write the byte value of the Tuple
	 * @param o an Object (Tuple) to be written on the DataOutput Stream.
	 * @throws IOException, if an I/O error occurs. 
	 */	
	public void write (DataOutput output, Object o) throws IOException {
		Object [] arr = Tuples.getObjectArray((Tuple) o);
		for (int i=0;i<size;i++) {
		 	nullwert.writeBoolean(output,arr[i]==null?true:false);
			if(arr[i]!=null) converter[i].write(output,arr[i]);
		}
	}
	
	/**
	 * Reads the Tuple from the DataInput.
	 * Null values in the columns are possible.
	 * @param input a DataInput containing contents of a tuple in byte form.
	 * @param o a stored object. It is ignored here.
	 * @return a Tuple object.
	 * @throws IOException, if an I/O error occurs. 
	 */
	public Object read (DataInput input, Object o) throws IOException {
		Object [] arr = new Object[size];
		for (int j=0;j<size;j++){	
			if(nullwert.readBoolean(input))	
				arr[j]= null;
			else
				arr[j] = converter[j].read(input, o);
		}
		return createResTuple.invoke(arr,rm);
	}
	
	/**
	 * This main method contains an example how to use
	 * the TupelConverter. 
	 * 
	 * @param args the arguments
	 */
	public static void main (String [] args) {
		try{ //catch IOException
			// At first, create MetaData m1,...,m6 with only one column
			ResultSetMetaData m1=new MetaData(
					java.sql.Types.VARCHAR,
					"stagenm",
					10);
			ResultSetMetaData m2=new MetaData(
					java.sql.Types.VARCHAR,
					"birthnm",
					10);		
			ResultSetMetaData m3=new MetaData(
					java.sql.Types.VARCHAR,
					"firstnm",
					10);
			ResultSetMetaData m4=new MetaData(
					java.sql.Types.SMALLINT,
					"dod",
					5);
			ResultSetMetaData m5=new MetaData(
					java.sql.Types.VARCHAR,
					"origin",
					5);
			ResultSetMetaData m6=new MetaData(
					java.sql.Types.VARCHAR,
					"gender",
					5);
			ResultSetMetaData m7=new MetaData(
					java.sql.Types.NUMERIC,
					"income",
					10,5);
			
			// Assemble MetaData m1,...,m6 in a ResultSetMetaData					
			AssembledResultSetMetaData rm=new AssembledResultSetMetaData(
					new ResultSetMetaData[]{m1,m2,m3,m4,m5,m6,m7},
					new String[]{"stagenm","birthnm","firstnm","dod","origin","gender","income"});
			
			System.out.println("Number of columns: "+rm.getColumnCount());
		
			// create 3 objectarrays a,b,c, that contain data from 3 tuples.
			Object[] a=new Object[]{
					new String("Willie Aames"),new String("Aames"),	new String("William"),
					new Short((short)1960),new String("\\Am"),new String("M"),new java.math.BigDecimal("12244.2")
					};
			Object[] b=new Object[]{
					new String("Bud Abbott"),new String("Abott"),new String("William"),
					new Short((short)1895),new String("\\Am"),new String("M"),null
					};	
			Object[] c=new Object[]{
					new String("Diahnne Abott"),null,null,
					new Short((short)1960),new String("\\Am"),new String("F"),new java.math.BigDecimal("2712244.23333")
					};

		 	//create array of arraytuples
			Tuple [] tOrig=new Tuple[3];
			tOrig[0]=(ArrayTuple)xxl.core.relational.ArrayTuple.FACTORY_METHOD.invoke(a,rm);
			tOrig[1]=(ArrayTuple)xxl.core.relational.ArrayTuple.FACTORY_METHOD.invoke(b,rm);
			tOrig[2]=(ArrayTuple)xxl.core.relational.ArrayTuple.FACTORY_METHOD.invoke(c,rm);
			
			// output of original tuples 
			System.out.println("Tuples constructed");
			
			for (int i=0; i<3; i++) 
				System.out.println(tOrig[i]);
			
			// create a tupleconverter in oder to read and write
			// the tuples.
			
			TupleConverter tupleConverter= new TupleConverter(rm);

			// create a ByteArrayOutputStream			
			java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
			
			// writes a tuple on the  OutputStream
			
			System.out.println("Write tuples");
			for (int i=0; i<3; i++)
				tupleConverter.write(new java.io.DataOutputStream(output), tOrig[i]);
			
			// create a ByteArrayInputStream input  on the  OutputStream	
			
			java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(output.toByteArray());

			// reads 3 tuples from the  InputStream
			System.out.println("Read tuples");
			Tuple [] tRecon=new Tuple[3];
			for (int i=0; i<3; i++)
				tRecon[i] = (ArrayTuple)tupleConverter.read(new java.io.DataInputStream(input));

			// close the Streams after use
			input.close();
			output.close();
			
			// output of the all tuple
			System.out.println("Output the restored tuples");

			for (int i=0; i<3; i++) 
				System.out.println(tRecon[i]);
			
			System.out.println("Test the restored tuples");
			
			for (int i=0; i<3; i++)
				if (!tOrig[i].equals(tRecon[i])) {
					System.out.println("Tuples are not identical!");
					throw new RuntimeException("Tuples should have been identical");
				}

			System.out.println("Tuples successfully reconstructed");
		}
		catch (IOException ioe) {
			System.out.println("An I/O error occurred.");
		}
		catch (Exception se) {
			System.out.println(se);
		}
	}
}
