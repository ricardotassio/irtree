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

package xxl.core.util;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;

/**
 * This class provides some useful methods for dealing with Strings and parsing Strings.
 */

public class Strings{

	/**
	 * The default constructor has private access in order to ensure
	 * non-instantiability.
	 */
	private Strings(){}


	/** This method is parsing a String using given delimeters and is
	 * returning the parsed String as <tt>String[]</tt>
	 * separated by the delimeters. The delimeters are not returned.
	 * @param s String to parse
	 * @param delims <tt>String[]</tt> used as delimeters
	 * @return <tt>String[]</tt> containing the parsed String
	 */
	public static String[] parse( String s, String[] delims){ 
		List l = new ArrayList();
		if ( s.equals ("") ) return new String[]{""};
		if ( delims.length == 0) return new String[]{s};
		String t = s.trim();
		while ( t.length() > 0){
			int delim = 0;
			int index = t.length() + 1;
			for ( int i= 0; i < delims.length ; i++){
				if ( ( index > t.indexOf(delims[i])) && (t.indexOf(delims[i]) != -1) ){
					index = t.indexOf(delims[i]) ;
					delim = i; // welcher trenner war es?
				}
			}
			try{
				String n = t.substring (0, index);
				l.add ( n);
				t = t.substring (index - 1 + delims[delim].length() , t.length()).trim();
			}
			catch (Exception e){
				l.add ( t);
				t = "";
			}
		}
		String[] r = new String[l.size()];
		for ( int i= 0; i < r.length; i++){
			r[i] = (String) l.get(i);
		}
		return r;
	}

	/** This method is parsing a String using the given delimeter and is
	 * returning the parsed String as String[]
	 * separated by the delimeter. The delimeter is not returned.
	 * @param s String to parse
	 * @param delim String used as delimeter
	 * @return String[] containing the parsed String
	 */
	public static String [] parse( String s, String delim){
		if ( s.trim().equals ("") && !delim.equals(" ")) return new String[]{""};
		if ( s.equals ("") ) return new String[]{""};
		StringTokenizer st = new StringTokenizer( s, delim);
		String[] r = new String[ st.countTokens() ];
		int i=0;
		while( st.hasMoreElements() ) {
	        	r[i] = (String) st.nextElement();
        		i++;
	     	}
		return r;
	}

	/** This method is converting a <tt>String []</tt> to a <tt>double []</tt> of same length.
	 * @param s <tt>String []</tt> to convert
	 * @return <tt>double []</tt> containing a double representation of each String from the
	 * input array.
	 */
	public static double [] toDoubleArray ( String [] s){
		double [] r = new double[ s.length];
		for ( int i = 0; i < r.length ; i++)
			r[i] = (new Double( s[i])).doubleValue();
		return r;
	}

	/** This method is converting a <tt>String []</tt> to a <tt>float []</tt> of same length.
	 * @param s <tt>String []</tt> to convert
	 * @return <tt>float []</tt> containing a float representation of each String from the
	 * input array.
	 */
	public static float[] toFloatArray ( String[] s){
		float [] r = new float[ s.length];
		for ( int i = 0; i < r.length ; i++)
			r[i] = (new Float( s[i])).floatValue();
		return r;
	}

	/** This method is converting a <tt>String []</tt> to a <tt>int []</tt> of same length.
	 * @param s <tt>String []</tt> to convert
	 * @return <tt>int []</tt> containing a float representation of each String from the
	 * input array.
	 */
	public static int [] toIntArray ( String[] s){
		int [] r = new int[ s.length];
		for ( int i = 0; i < r.length ; i++)
			r[i] = (new Integer( s[i])).intValue();
		return r;
	}

	/** This method is converting a <tt>String []</tt> to a <tt>long []</tt> of same length.
	 * @param s <tt>String []</tt> to convert
	 * @return <tt>long []</tt> containing a float representation of each String from the
	 * input array.
	 */
	public static long [] toLongArray ( String[] s){
		long [] r = new long[ s.length];
		for ( int i = 0; i < r.length ; i++)
			r[i] = (new Long( s[i])).longValue();
		return r;
	}

/* ------------------------------------------------------------------- */
/* ------ toString methods ------------------------------------------- */
/* ------------------------------------------------------------------- */	

	/** Returns a String representation of the given float[] using the given delimeter.
	 * A given array of size n will be converted to a String using n-1 delimeters,
	 * e.g. no delimeter is used for terminating the new String.
	 * @param f float[] the convert to String
	 * @param delim used delimeter to separate the given float values.
	 * @return a String representation of the given float[] using the given delimeter
	 */
	public static String toString ( float[] f, String delim){
		String r = "";
		for (int i=0 ; i< f.length - 1; i++)
			r = r + (new Float(f[i])).toString() + delim;
		r = r + (new Float( f [f.length-1] ) ).toString();
		return r;
	}

	/** Returns a String representation of the given float[] using spaces as delimeters.
	 * A given array of size n will be converted to a String using n-1 spaces,
	 * e.g. no delimeter is used for terminating the new String.
	 * @param f float[] the convert to String
	 * @return a String representation of the given float[]
	 */
	public static String toString ( float[] f){
		return toString ( f, " ");
	}

	
	/** Returns a String representation of the given double[] using the given delimeter.
	 * A given array of size n will be converted to a String using n-1 delimeters,
	 * e.g. no delimeter is used for terminating the new String.
	 * @param d double[] the convert to String
	 * @param delim used delimeter to separate the given double values.
	 * @return a String representation of the given double[] using the given delimeter
	 */
	public static String toString ( double[] d, String delim){
		String r = "";
		for (int i=0 ; i< d.length - 1; i++)
			r = r + (new Double(d[i])).toString() + delim;
		r = r + (new Double( d [d.length-1] ) ).toString();
		return r;
	}

	/** Returns a String representation of the given double[] using spaces as delimeters.
	 * A given array of size n will be converted to a String using n-1 spaces,
	 * e.g. no delimeter is used for terminating the new String.
	 * @param d double[] the convert to String
	 * @return a String representation of the given double[]
	 */
	public static String toString ( double[] d){
		return toString ( d, " ");
	}

	/** Returns a String representation of the given int[] using the given delimeter.
	 * A given array of size n will be converted to a String using n-1 delimeters,
	 * e.g. no delimeter is used for terminating the new String.
	 * @param i int[] the convert to String
	 * @param delim used delimeter to separate the given int values.
	 * @return a String representation of the given int[] using the given delimeter
	 */
	public static String toString ( int[] i, String delim){
		String r = "";
		for (int j=0 ; j < i.length - 1; j++)
			r = r + (new Integer( i[j])).toString() + delim;
		r = r + (new Integer( i [i.length-1] ) ).toString();
		return r;
	}

	/** Returns a String representation of the given int[] using spaces as delimeters.
	 * A given array of size n will be converted to a String using n-1 spaces,
	 * e.g. no delimeter is used for terminating the new String.
	 * @param i int[] the convert to String
	 * @return a String representation of the given int[]
	 */
	public static String toString ( int[] i){
		return toString ( i, " ");
	}

	/** Returns a String representation of the given String[] using ',' as delimeter.
	 * A given array of size n will be converted to a String using n-1 spaces,
	 * e.g. no delimeter is used for terminating the new String.
	 * @param s String[] the convert to String
	 * @return a String representation of the given String[]
	 */
	public static String toString ( String[] s){
		return toString (s, ", ");
	}

	/** Returns a String representation of the given Object[] using the given delimeter.
	 * A given array of size n will be converted to a String using n-1 delimeters,
	 * e.g. no delimeter is used for terminating the new String.
	 * @param o Object[] the convert to String
	 * @param delim used delimeter to separate String representations of the givhen objects.
	 * @return a String representation of the given Object[] using the given delimeter
	 */
	public static String toString ( Object[] o, String delim){
		if ( o == null) return "<null>";
		if ( o.length == 0) return "<empty>";
		String r = "";
		for (int i=0 ; i< o.length - 1; i++)
			if (o[i] == null) r = r + "<null>" + delim;
			else r = r + o[i].toString() + delim;
		if (o[o.length - 1] == null) r = r + "<null>";
		else r = r + o[o.length - 1].toString();
		return r;
	}

	/** Returns a String representation of the given double[][] using the given delimeter
	 * and separating each 'row' with a cf/lf.
	 * A given array of size nxm will be converted to m rows containing Strings using n-1 delimeters,
	 * e.g. no delimeter is used for terminating the new String in each row.
	 * @param d double[][] the convert to String
	 * @param delim used delimeter to separate the given double values.
	 * @return a String representation of the given double[][] using the given delimeter
	 */
	public static String toString ( double[] [] d, String delim){
		if ( d == null) return "<null>";
		if ( d.length == 0) return "<empty>";
		String r = "";
		for (int i=0 ; i< d.length - 1; i++)
			r = r + toString ( d[i], delim) + "\n";
		return r;
	}

	/** Returns a String representation of the given double[][] using ',' as delimeter
	 * and separating each 'row' with a cf/lf.
	 * A given array of size nxm will be converted to m rows containg Strings using n-1 delimeters,
	 * e.g. no delimeter is used for terminating the new String in each row.
	 * @param d double[][] the convert to String
	 * @return a String representation of the given double[][] using the given delimeter
	 */
	public static String toString ( double[] [] d){
		return toString ( d, ", ");
	}

	/** Returns a character reader based reader upon the source given by the name.
	 * Different sources such as web-servers or files will be choosen automatically
	 * determind by the given name, i.e., a source starting with <tt>http</tt> will be treated as
	 * a web-based text-file. At this time only <tt>http</tt> and files are supported.
	 *
	 * @param source name of the data source provided as {@link java.io.Reader reader}
	 * @return the given source as {@link java.io.Reader reader}
	 * @throws WrappingRuntimeException if an error of any type occures
	 */
	public static Reader getReader( String source) throws WrappingRuntimeException{
		Reader r = null;
		boolean init = false;
		try{
			// -- url ---
			if ( source.trim().toLowerCase().startsWith("http")){
				URL url = new URL (source);
				r = new InputStreamReader ( url.openStream());
				init = true;
			}
			// -- default file ---
			if ( ! init){
				r = new FileReader ( new File ( source));
			}
		}
		catch ( Exception e){
			throw new WrappingRuntimeException (e);
		}
		return r;
	}

	/** Assumes that the given String indicates a file and replaces the
	 * suffix by a new one. The suffix if given by "*.suffix" meaning the last found
	 * '.' and all following character will be replaced. If there's no '.' in the given String
	 * the given suffix will be concatenated.
	 * @param filename String treated as file name
	 * @param newSuffix new suffix for the file name
	 * @return a String representing a file name 
	 */
	public static String changeSuffix( String filename, String newSuffix){
		int k = filename.lastIndexOf(".");
		return (k == -1) ?
				filename
			:
				filename.substring(0,k) + newSuffix;
	}

	/** Assumes that the given String represents a fully qualified 
	 * file name (i.e., with path information) and returns the file name without any
	 * folder information by pruning all parts before the last 
	 * separator character 
	 * return the file name itself
	 * @param filename fully qualified file name
	 * @return file name withiout path information
	 * @see File#separator
	 */
	public static String prunePath( String filename){
		String r = filename;
		int i1 = r.lastIndexOf( File.separator);
		if ( i1 != -1)
			r = r.substring( i1+1, r.length());
		return r;
	}
	
	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main(String[] args) {
		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		String p = "Dies\tist\fein Test\n!";
		String[] s = parse(p, " ");
		System.out.println("-----------------------");
		System.out.println(p);
		System.out.println("-----------------------");
		for ( int i=0; i< s.length ; i++) System.out.print(s[i]+":");
		System.out.println("-----------------------");	
	}	
}