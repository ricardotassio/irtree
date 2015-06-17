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

package xxl.core.relational.cursors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.MetaDataCursor;
import xxl.core.functions.Function;
import xxl.core.relational.ArrayTuple;
import xxl.core.relational.Tuple;
import xxl.core.relational.metaData.AssembledResultSetMetaData;
import xxl.core.relational.metaData.MetaData;
import xxl.core.util.WrappingRuntimeException;

/**
 *	This class constructs a MetaDataCursor for a given File or URL.<BR>
 *	<P>
 *
 *	The first line of the file must contain all the column names delimited by a character of
 *	<code>columnDelimiters</code>. The default <code>columnDelimiters</code> are "\t".<BR>
 *	<P>
 *
 *	The second line of the file must contain the column types (also delimited by <code>columnDelimiters</code>).
 *	The possible column types are: <code><B>NUMBER</B></code>(<code><B>NUMERIC</B></code>), 
 *  <code><B>INTEGER</B></code>,
 *  <code><B>BIGINT</B></code>, <code><B>DOUBLE</B></code>, <code><B>DATE</B></code>,
 *  <code><B>TIME</B></code>,  <code><B>TIMESTAMP</B></code>, <code><B>VARBINARY</B></code>,
 *  <code><B>BIT</B></code> and <code><B>VARCHAR</B></code>.
 *	The type Varchar is set to Varchar(40) by default.<BR>
 *	<P>
 *
 *	The third line of the file is an empty line.<BR>
 *	<P>
 *
 *	The first three lines of the file are case insensitive (the header). All 
 *	following lines are case sensitive.<BR>
 *	<P>
 *
 *	Each of the following lines represents a tuple of this relation.<BR>
 *	Values are delimited by the characters of <code>columnDelimiters</code>.<BR>
 *	To model a SQL-Null-Value one must use the <code>nullRepresentation</code>.
 *	The String <code>nullRepresentation</code> is by default set to <code>"#"</code>.<BR>
 *	<P>
 *
 *	Example for a file:
 *
 *	<pre><code>
 *	key     NAME        first name  year of birth
 *	Number  Varchar     varChar     number
 *
 *	1       Tiger       Scott       1957
 *	2       Smith       Scott       1961
 *	3       Smith       John        1948
 *	4       #           Test        #
 *	</code></pre>
 *
 *	By passing the file name to the constructor, the specified file can be used as a MetaDataCursor.
 */
public class FileRelationalMetaDataCursor extends AbstractCursor implements MetaDataCursor {
	
	/** Contains the delimiter bewteen columns (in the line). */
	protected String columnDelimiters;
	/** Contains the String representation of <code>NULL</code>. Default is "#". */
	protected String nullRepresentation;
	/** Stores the url of the used file */
	protected URL url;
	/** Stores the filename of the used file */
	protected String fileName;
	/** InputStream needed to free resources */
	protected InputStream fileStream;
	/** The BufferedReader to read from the file */
	protected BufferedReader file;
	/** The associated MetaData */
	protected ResultSetMetaData metaData;
	/** Number of Columns */
	protected int numberOfColumns;
	/** Determines if a column contains objects of the type Number (otherwise VARCHAR) */
	protected int[] columnType;
	/** Contains the names of the columns */
	protected String[] columnNames;
	/** Contains the current tuple */
	protected Tuple tuple;
	/** Function that constructs a tuple out of an object array */
	protected Function createTuple;
	/** Function that returns a new Inputstream of the Data*/
	protected Function newInputStream;


	/**Write the MetaDataCursor to the Outputstream in the FileMetaDataCursor-Format.
	 * The Representation of null is <code>"#"</code>.
	 * @param mdC The MetaDataCursor
	 * @param out The Outputstream to write.*/	
	public static void writeMetaDataCursor(MetaDataCursor mdC, OutputStream out){
		writeMetaDataCursor(mdC,"#",out);
	}
	
	/**Write the MetaDataCursor to the Outputstream in the FileMetaDataCursor-Format.
	 * @param mdC The MetaDataCursor
	 * @param nullRepresentation Representation that represents a null value.
	 * 	Default is <code>"#"</code>.
	 * @param out The Outputstream to write.*/	
	public static void writeMetaDataCursor(MetaDataCursor mdC, String nullRepresentation, OutputStream out){
		try{
			PrintStream ps=new PrintStream(out);
			ResultSetMetaData rsmd=(ResultSetMetaData)mdC.getMetaData();
			int columnCount=rsmd.getColumnCount();
			for (int column=1 ; column<=columnCount ; column++){
				if (column>1) ps.print("\t");
				ps.print(rsmd.getColumnName(column));
			}
			ps.print("\n");
			for (int column=1 ; column<=columnCount ; column++){
				if (column>1) ps.print("\t");
				ps.print(rsmd.getColumnTypeName(column));
			}
			ps.print("\n");

			while(mdC.hasNext()){
				ps.print("\n");
				Tuple tup=(Tuple)mdC.next();
				for (int column=1 ; column<=columnCount ; column++){
					if (column>1) ps.print("\t");
					if (tup.getObject(column)==null)
						ps.print(nullRepresentation);
					else ps.print(tup.getObject(column));
				}
			}
			ps.close();
			mdC.close();
		}catch(SQLException e){System.err.println(e.getMessage());e.printStackTrace();}
	}
	/**
	 * Constructs a FileMetaDataCursor that returns the data of the InputStream.
	 *
	 * @param newInputStream Function that returns a new Inputstream of the Data
	 * @param columnDelimiters delimiter String
	 * @param nullRepresentation Representation that represents a null value.
	 * 	Default is <code>"#"</code>.
	 * @param createTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD} is used.
	 */
	public FileRelationalMetaDataCursor(Function newInputStream, String columnDelimiters, String nullRepresentation, Function createTuple) {
		this.columnDelimiters = columnDelimiters;
		this.nullRepresentation = nullRepresentation;
		this.newInputStream = newInputStream;
		this.fileStream = (InputStream)newInputStream.invoke();
		this.file = new BufferedReader(new InputStreamReader(fileStream));
		this.createTuple = createTuple;
		
		try {
			// determine column count
			StringTokenizer st = new StringTokenizer(file.readLine(), columnDelimiters);
			this.numberOfColumns = st.countTokens();
			this.columnType = new int[numberOfColumns];
			this.columnNames = new String[numberOfColumns];
			try {
				for (int i=0; i<numberOfColumns; i++)
					columnNames[i] = st.nextToken().toUpperCase();
			}
			catch (NoSuchElementException e) {
				throw new IllegalArgumentException("More column names expected");
			}

			// read column types
			st = new StringTokenizer(file.readLine(), columnDelimiters);
			ResultSetMetaData[] columnMetaDatas = new ResultSetMetaData[numberOfColumns];
			String s;
			try {
				for (int i=0; i<numberOfColumns; i++) {
					s = st.nextToken();
					//columnTypeIsNumber[i] = s.equalsIgnoreCase("number");
					if (s.equalsIgnoreCase("number")) {
						columnType[i]=java.sql.Types.NUMERIC;
						columnMetaDatas[i] = MetaData.NUMBER_META_DATA_DEFAULT_INSTANCE;
						continue;
					}
					if (s.equalsIgnoreCase("numeric")) {
						columnType[i]=java.sql.Types.NUMERIC;
						columnMetaDatas[i] = MetaData.NUMBER_META_DATA_DEFAULT_INSTANCE;
						continue;
					}
					else if (s.equalsIgnoreCase("smallint")) {
						columnType[i]=java.sql.Types.SMALLINT;
						columnMetaDatas[i] = new MetaData(java.sql.Types.SMALLINT,"",7,0);
						continue;
					}
					else if (s.equalsIgnoreCase("integer")) {
						columnType[i]=java.sql.Types.INTEGER;
						columnMetaDatas[i] = new MetaData(java.sql.Types.INTEGER,"",9,0);
						continue;
					}
					else if (s.equalsIgnoreCase("BIGINT")) {
						columnType[i]=java.sql.Types.BIGINT;
						columnMetaDatas[i] = new MetaData(java.sql.Types.BIGINT,"",18,0);
						continue;
					}
					else if (s.equalsIgnoreCase("DOUBLE")) {
						columnType[i]=java.sql.Types.DOUBLE;
						columnMetaDatas[i] = new MetaData(java.sql.Types.DOUBLE,"",15,0);
						continue;
					}
					else if (s.equalsIgnoreCase("Date")) {
						columnType[i]=java.sql.Types.DATE;
						columnMetaDatas[i] = new MetaData(java.sql.Types.DATE,"",0,0);
						continue;
					}
					else if (s.equalsIgnoreCase("Time")) {
						columnType[i]=java.sql.Types.TIME;
						columnMetaDatas[i] = new MetaData(java.sql.Types.TIME,"",0,0);
						continue;
					}
					else if (s.equalsIgnoreCase("Timestamp")) {
						columnType[i]=java.sql.Types.TIMESTAMP;
						columnMetaDatas[i] = new MetaData(java.sql.Types.TIMESTAMP,"",6,0);
						continue;
					}
					else if (s.equalsIgnoreCase("BIT")) {
						columnType[i]=java.sql.Types.BIT;
						columnMetaDatas[i] = new MetaData(java.sql.Types.BIT,"",1,0);
						continue;
					}
					else if (s.toUpperCase ().startsWith ("VARCHAR")){
						columnType[i]=java.sql.Types.VARCHAR;
						int maxLength = 40;
						columnMetaDatas[i] = new MetaData(Types.VARCHAR,"",maxLength);
					}
					else if (s.equalsIgnoreCase("VARBINARY")) {
						columnType[i]=java.sql.Types.VARBINARY;
						columnMetaDatas[i] = new MetaData(java.sql.Types.VARBINARY,"",120,0);
						continue;
					}
					else throw new IllegalArgumentException ("Only the types NUMBER and VARCHAR are allowed");

					// ***** every VARCHAR column is here VARCHAR(40).  (hard coded)
				}
			}
			catch (NoSuchElementException e) {
				throw new IllegalArgumentException("More column types expected");
			}

			// eine Leerzeile einlesen
			file.readLine();

			this.metaData = new AssembledResultSetMetaData(columnMetaDatas, columnNames);
		}
		catch (IOException e) {
			throw new WrappingRuntimeException(e);
		}
	}
	
	/**
	 * Constructs a FileMetaDataCursor that returns the data of a URL.
	 *
	 * @param url URL of the data
	 * @param columnDelimiters delimiter String
	 * @param nullRepresentation Representation that represents a null value.
	 * 	Default is <code>"#"</code>.
	 * @param createTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD} is used.
	 */
	public FileRelationalMetaDataCursor(final URL url, String columnDelimiters, String nullRepresentation, Function createTuple) {
		this(new Function(){
				public Object invoke(){
					try{
						return url.openStream();
					}catch(IOException ioe){throw new WrappingRuntimeException(ioe);}
				}
			}
			,columnDelimiters, nullRepresentation, createTuple);
	}
	
	/**
	 * Constructs a FileMetaDataCursor that returns the data of a URL.
	 * The column delimiter is set to "\t" and the null representation is set to "#".
	 * To construct tuples, ArrayTuple.FACTORY_METHOD is used.
	 *
	 * @param url URL of the data
	 */
	public FileRelationalMetaDataCursor(URL url) {
		this(url, "\t", "#", ArrayTuple.FACTORY_METHOD);
	}
	
	/**
	 * Constructs a FileMetaDataCursor that returns the data of a file.
	 *
	 * @param fileName Name of the file
	 * @param columnDelimiters delimiter String
	 * @param nullRepresentation Representation that represents a null value.
	 * 	Default is <code>"#"</code>.
	 * @param createTuple Function that maps an Object array (column values) and a 
	 *	ResultSetMetaData object to a new result Tuple. {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD}
	 *	can be used. If null is passed, the constructor will try to determine the type of
	 *	tuple that is used in the cursor. If it is possible, the appropriate factory method is
	 *	used. If it is not possible, {@link xxl.core.relational.ArrayTuple#FACTORY_METHOD} is used.
	 */
	public FileRelationalMetaDataCursor(final String fileName, String columnDelimiters, String nullRepresentation, Function createTuple) {
		this(new Function(){
				public Object invoke(){
					try{
						return new FileInputStream(fileName);
					}catch(FileNotFoundException fnfe){throw new WrappingRuntimeException(fnfe);}
				}
			}
			,columnDelimiters, nullRepresentation, createTuple);
	}

	/**
	 * Constructs a FileMetaDataCursor that returns the data of a file.
	 * The column delimiter is set to "\t" and the null representation is set to "#".
	 * To construct tuples, ArrayTuple.FACTORY_METHOD is used.
	 *
	 * @param fileName Name of the file
	 */
	public FileRelationalMetaDataCursor(String fileName) {
		this(fileName, "\t", "#", ArrayTuple.FACTORY_METHOD);
	}

	/**
	 * Closes the cursor.
	 * Signals the cursor to clean up resources,
	 * close files, etc. <br> After a call to <code>close()</code> calls to methods
	 * like <code>next()</code> or <code>peek()</code> are not guarantied to yield proper
	 * results. <br>Multiple calls to <code>close()</code> do not have any effect, i.e., if
	 * <code>close()</code> was called the cursor remains in the state "closed".
	 */
	public void close() {
		super.close();
		try {
			file.close();
			fileStream.close();
			file = null;
		}
		catch (IOException e) {
			throw new WrappingRuntimeException(e);
		}
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements.
	 * (In other words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.) <br>
	 * This operation should be implemented idempotent, i.e., consequent calls to
	 * <code>hasNext()</code> do not have any effect.
	 *
	 * @return <tt>true</tt> if the cursor has more elements.
	 */
	public boolean hasNextObject() {
		String s;
		try {
			s = file.readLine();
		}
		catch (IOException e) {
			throw new WrappingRuntimeException(e);
		}
		if (s == null)
			return false;
		StringTokenizer st = new StringTokenizer(s, columnDelimiters);
		/** Contains the values of the current tuple */
		Object[] tupleValues = new Object[numberOfColumns];
		
		for (int i=0; i<numberOfColumns; i++) {
			if (st.hasMoreTokens()) {
				s = st.nextToken();
				if (s.equals(nullRepresentation))
					tupleValues[i]=null;
				else 
					switch(columnType[i]){
					case java.sql.Types.NUMERIC:
						tupleValues[i]=new BigDecimal(s);
						break;
					case java.sql.Types.SMALLINT:
						tupleValues[i]=new Short(s);
						break;	
					case java.sql.Types.INTEGER:
						tupleValues[i]=new Integer(s);
						break;
					case java.sql.Types.BIGINT:
						tupleValues[i]=new Long(s);
						break;
					case java.sql.Types.DOUBLE:
						tupleValues[i]=new Double(s);
						break;
					case java.sql.Types.DATE:
						tupleValues[i]=java.sql.Date.valueOf(s);
						break;
					case java.sql.Types.TIME:
						tupleValues[i]=java.sql.Time.valueOf(s);
						break;
					case java.sql.Types.TIMESTAMP:
						tupleValues[i]=java.sql.Timestamp.valueOf(s);
						break;
					case java.sql.Types.BIT:
						if (s.equalsIgnoreCase("false"))
							tupleValues[i]=new Boolean(false);
						else if (s.equalsIgnoreCase("true"))
							tupleValues[i]=new Boolean(true);
						else tupleValues[i]=null;
						break;
					case java.sql.Types.VARCHAR:
						tupleValues[i]=s;
						break;
					case java.sql.Types.VARBINARY:
						tupleValues[i]=s;
						break;
					default: tupleValues[i] = null;
					}
			}
			else
				tupleValues[i] = null;
		}
		tuple = (Tuple) createTuple.invoke(tupleValues, metaData);
		return true;
	}

	/**
	 * Returns the next element in the iteration.
	 * This element will be removed from the underlying collection, if
	 * <tt>next</tt> is called.
	 *
	 * @return the next element in the iteration.
	 * @throws java.util.NoSuchElementException if the iteration has no more elements.
	 */
	public Object nextObject() {
		return tuple;
	}	

	/**
	 * Resets the cursor to its initial state. This call is supported by this class.
	 *
	 * @throws java.lang.UnsupportedOperationException if the <tt>reset</tt> method is
	 * 		not supported by the cursor.
	 */
	public void reset() throws UnsupportedOperationException {
		super.reset();
		try {
			file.close();
			fileStream.close();
			this.fileStream = (InputStream)newInputStream.invoke();
			file = new BufferedReader(new InputStreamReader(fileStream));
			file.readLine();
			file.readLine();
			file.readLine();
			tuple = null;
		}
		catch (IOException e) {
			throw new WrappingRuntimeException(e);
		}
	}
	
	/** Returns true if reset is supported. This method always returns true 
	 *  because this cursor is considered as resetable. 
	 * 
	 * @return true if reset is supported */
	public boolean supportsReset() {
		return true;
	}
	
	/**
	 * Returns the metadata information for this MetaDataCursor.
	 * The return value of this method is of type ResultSetMetaData.
	 *
	 * @return a ResultSetMetaData representing metadata information for this MetaDataCursor.
	 */
	public Object getMetaData() {
		return metaData;
	}
	
	/**
	 * Use case that reads the specified file (first command line argument)
	 * and outputs all tuples. This method can be used to test a file
	 * if it can be used with a FileMetaDataCursor.
	 * 
	 * @param args the arguments 
	 */
	public static void main(String args[]) {
		if ((args.length==0) || (args.length>2)) {
			System.out.println("The class has to be called with one or two parameters,");
			System.out.println("the name of the used input files.");
			return;
		}
		
		MetaDataCursor c_out=null;
		
		if (args.length==1)
			c_out = new FileRelationalMetaDataCursor(args[0]);
		
		if (args.length==2) {
			MetaDataCursor c0 = new FileRelationalMetaDataCursor(args[0]);
			MetaDataCursor c1 = new FileRelationalMetaDataCursor(args[1]);
			
			c_out = new NestedLoopsJoin (c0,c1,null,ArrayTuple.FACTORY_METHOD,NestedLoopsJoin.NATURAL_JOIN);
		}
		
		while (c_out.hasNext()) {
			Tuple t = (Tuple) c_out.next();
			System.out.println(t);
		}
		
		c_out.reset();
		System.out.println("Number of tuples: "+xxl.core.cursors.Cursors.count(c_out));
		
		c_out.close();
	}
}
