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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import xxl.core.cursors.MetaDataCursor;
import xxl.core.cursors.sources.ContinuousRandomNumber;
import xxl.core.functions.Function;
import xxl.core.relational.Tuples;
import java.util.Iterator;

/**
 * The Sampler is based on {@link xxl.core.cursors.filters.Sampler}.
 * A sampler is a {@link xxl.core.cursors.filters.Filter filter} that generates a sample of the
 * elements contained in a given input iterator. <br>
 * To generate the sample, a pseudo random number generator and some parameters
 * for the generation of random numbers can be passed to a 
 * constructor call. For a detailed description see
 * {@link xxl.core.cursors.filters.Sampler}.
 * <p>
 * The example in the main method wraps an Enumerator cursor (integers 0 to 99)
 * to a MetaDataCursor using {@link xxl.core.cursors.Cursors#wrapToMetaDataCursor(Iterator, Object)}. 
 * Then, a sample of approximately 10% of size is generated. 
 * The interesting call is: 
 *
 * <code><pre>
 *		cursor = new Sampler(cursor, 0.1);
 * </pre></code>
 */
public class Sampler extends xxl.core.cursors.filters.Sampler implements MetaDataCursor {

	/** Meta data associated with the output */
	protected ResultSetMetaData metaData;

	/**
	 * Creates a new sampler based on a {@link xxl.core.cursors.sources.ContinuousRandomNumber  ContinuousRandomNumber}
	 * cursor using {@link java.util.Random} as PRNG and a given Bernoulli probability.
	 *
	 * @param cursor the MetaDataCursor containing the elements the sample is to be created from.
	 * @param p the Bernoulli probability, i.e. the probability the elements will be contained
	 * 		in the delivered sample. <tt>p</tt> must be in range (0,1].
	 * @param seed the seed to the pseudo random number generator.
	 * @throws java.lang.IllegalArgumentException if <tt>p</tt> is not in range (0, 1].
	 */
	public Sampler (MetaDataCursor cursor, double p, long seed) {
		super(cursor, p, seed);
		metaData = (ResultSetMetaData)cursor.getMetaData();
	}
	
	/**
	 * Creates a new sampler based on a {@link xxl.core.cursors.sources.ContinuousRandomNumber  ContinuousRandomNumber}
	 * cursor and a given Bernoulli probability.
	 *
	 * @param cursor the MetaDataCursor containing the elements the sample is to be created from.
	 * @param crn a ContinuousRandomNumber cursor wrapping a PRNG.
	 * @param p the Bernoulli probability, i.e. the probability the elements will be contained
	 * 		in the delivered sample. <tt>p</tt> must be in range (0,1].
	 * @throws java.lang.IllegalArgumentException if <tt>p</tt> is not in range (0, 1].
	 */
	public Sampler (MetaDataCursor cursor, ContinuousRandomNumber crn, double p) {
		super(cursor, crn, p);
		metaData = (ResultSetMetaData)cursor.getMetaData();
	}
	
	/**
	 * Creates a new sampler based on a {@link xxl.core.cursors.sources.ContinuousRandomNumber  ContinuousRandomNumber}
	 * cursor using {@link java.util.Random} as PRNG and a given Bernoulli probability.
	 *
	 * @param cursor the MetaDataCursor containing the elements the sample is to be created from.
	 * @param p the Bernoulli probability, i.e. the probability the elements will be contained
	 * 		in the delivered sample. <tt>p</tt> must be in range (0,1].
	 * @throws java.lang.IllegalArgumentException if <tt>p</tt> is not in range (0, 1].
	 */
	public Sampler (MetaDataCursor cursor, double p) {
		super(cursor, p);
		metaData = (ResultSetMetaData)cursor.getMetaData();
	}	
	
	/**
	 * Creates a new sampler based on a {@link xxl.core.cursors.sources.ContinuousRandomNumber  ContinuousRandomNumber}
	 * cursor using {@link java.util.Random} as PRNG and a given Bernoulli probability.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param createInputTuple Function that maps a (row of the) ResultSet to a Tuple. 
	 *	The function gets a ResultSet and maps the current row to a Tuple. 
	 *	If null is passed, the factory method of ArrayTuple is used.
	 *	It is forbidden to call the next, update and similar methods of the ResultSet
	 *	from inside the function!
	 * @param p the Bernoulli probability, i.e. the probability the elements will be contained
	 * 		in the delivered sample. <tt>p</tt> must be in range (0,1].
	 * @param seed the seed to the pseudo random number generator.
	 * @throws java.lang.IllegalArgumentException if <tt>p</tt> is not in range (0, 1].
	 */
	public Sampler (ResultSet resultSet, Function createInputTuple , double p, long seed) {
		this(new ResultSetMetaDataCursor(resultSet, createInputTuple), p, seed);
	}

	/**
	 * Creates a new sampler based on a {@link xxl.core.cursors.sources.ContinuousRandomNumber  ContinuousRandomNumber}
	 * cursor and a given Bernoulli probability.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param createInputTuple Function that maps a (row of the) ResultSet to a Tuple. 
	 *	The function gets a ResultSet and maps the current row to a Tuple. 
	 *	If null is passed, the factory method of ArrayTuple is used.
	 *	It is forbidden to call the next, update and similar methods of the ResultSet
	 *	from inside the function!
	 * @param crn a ContinuousRandomNumber cursor wrapping a PRNG.
	 * @param p the Bernoulli probability, i.e. the probability the elements will be contained
	 * 		in the delivered sample. <tt>p</tt> must be in range (0,1].
	 * @throws java.lang.IllegalArgumentException if <tt>p</tt> is not in range (0, 1].
	 */
	public Sampler (ResultSet resultSet, Function createInputTuple , ContinuousRandomNumber crn, double p) {
		this(new ResultSetMetaDataCursor(resultSet, createInputTuple), crn, p);
	}
	
	/**
	 * Creates a new sampler based on a {@link xxl.core.cursors.sources.ContinuousRandomNumber  ContinuousRandomNumber}
	 * cursor using {@link java.util.Random} as PRNG and a given Bernoulli probability.
	 *
	 * @param resultSet the input ResultSet delivering the elements. The ResultSet is wrapped internally to a
	 *	MetaDataCursor using {@link ResultSetMetaDataCursor}.
	 * @param createInputTuple Function that maps a (row of the) ResultSet to a Tuple. 
	 *	The function gets a ResultSet and maps the current row to a Tuple. 
	 *	If null is passed, the factory method of ArrayTuple is used.
	 *	It is forbidden to call the next, update and similar methods of the ResultSet
	 *	from inside the function!
	 * @param p the Bernoulli probability, i.e. the probability the elements will be contained
	 * 		in the delivered sample. <tt>p</tt> must be in range (0,1].
	 * @throws java.lang.IllegalArgumentException if <tt>p</tt> is not in range (0, 1].
	 */
	public Sampler (ResultSet resultSet, Function createInputTuple , double p) {
		this(new ResultSetMetaDataCursor(resultSet, createInputTuple), p);
	}

	/**
	 * Returns the resulting metadata for this operator.
	 *
	 * The metadata the same as for the input operator.
	 *
	 * @return object of class ResultSetMetaData
	 */
	public Object getMetaData () {
		return metaData;
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 * 
     * @param args the arguments
	 */
	public static void main(String[] args){

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		/* 
			Wraps an Enumerator cursor (integers 0 to 99)
			to a MetaDataCursor using {@link xxl.core.cursors.Cursors#wrapToMetaDataCursor(Iterator, Object)}. 
			Then, a sample of approximatly 10% of the original size is produced.
		*/
		System.out.println("Example 1: Generating a sample of approximately 10% of size");
		ResultSetMetaData metaData = (ResultSetMetaData) xxl.core.relational.metaData.MetaData.NUMBER_META_DATA_FACTORY_METHOD.invoke();
		
		MetaDataCursor cursor = xxl.core.cursors.Cursors.wrapToMetaDataCursor(
			Tuples.mapObjectsToTuples(
				new xxl.core.cursors.sources.Enumerator(0,100),
				metaData),
			    metaData
		);
		
		cursor = new Sampler(cursor, 0.1);
		
		xxl.core.cursors.Cursors.println(cursor);
	}
}
