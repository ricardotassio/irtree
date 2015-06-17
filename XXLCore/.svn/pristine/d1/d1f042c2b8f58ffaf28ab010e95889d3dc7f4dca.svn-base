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

package xxl.core.spatial.cursors;

import java.io.File;
import java.util.Iterator;

import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.cursors.filters.Filter;
import xxl.core.cursors.filters.Sampler;
import xxl.core.cursors.wrappers.IteratorCursor;
import xxl.core.functions.Function;
import xxl.core.functions.Tuplify;
import xxl.core.predicates.Predicate;
import xxl.core.spatial.points.Point;
import xxl.core.spatial.points.Points;
import xxl.core.spatial.predicates.DistanceWithinMaximum;
import xxl.core.spatial.predicates.OverlapsPredicate;

/**
 *	NestedLoops distance similarity-join for Points.
 *
 *	The use-case contains a detailed example for a similarity-join on one or two input Point sets.
 *
 */
public class NestedLoopsJoin extends xxl.core.cursors.joins.NestedLoopsJoin {

	/** Creates a new NestedLoopsJoin.
	 *
	 * @param input0 the input-Cursor that is traversed in the "outer"
	 * 		loop.
	 * @param input1 the input-Cursor that is traversed in the "inner"
	 * 		loop.
	 * @param newCursor a parameterless function that delivers a new Cursor, when the
	 * 		Cursor <code>input1</code> cannot be reset, i.e.
	 * 		<code>input1.reset()</code> will cause a
	 * 		{@link java.lang.UnsupportedOperationException}.
	 * @param predicate the Predicate used to determin whether a tuple is a result-tuple
	 * @param newResult a factory-method (Function) that takes two parameters as argument
	 * 		and is invoked on each tuple where the predicate's evaluation result
	 * 		is <tt>true</tt>, i.e. on each qualifying tuple before it is
	 * 		returned to the caller concerning a call to <tt>next()</tt>.
	 */
	public NestedLoopsJoin(Cursor input0, Cursor input1, Function newCursor, Predicate predicate, Function newResult) {
		super(input0, input1, newCursor, predicate, newResult);
	}

	/** Creates a new NestedLoopsJoin.
	 *
	 * @param input0 the input-Iterator that is traversed in the "outer"
	 * 		loop.
	 * @param input1 the input-Iterator that is traversed in the "inner"
	 * 		loop.
	 * @param newCursor a parameterless function that delivers a new Cursor, when the
	 * 		Cursor <code>input1</code> cannot be reset, i.e.
	 * 		<code>input1.reset()</code> will cause a
	 * 		{@link java.lang.UnsupportedOperationException}.
	 * @param predicate the Predicate used to determin whether a tuple is a result-tuple
	 * @param newResult a factory-method (Function) that takes two parameters as argument
	 * 		and is invoked on each tuple where the predicate's evaluation result
	 * 		is <tt>true</tt>, i.e. on each qualifying tuple before it is
	 * 		returned to the caller concerning a call to <tt>next()</tt>.
	 */
	public NestedLoopsJoin(Iterator input0, Iterator input1, Function newCursor, Predicate predicate, Function newResult) {
		this(new IteratorCursor(input0), new IteratorCursor(input1), newCursor, predicate, newResult);
	}

	/** Creates a new NestedLoopsJoin. Uses OverlapsPredicate.
	 *
	 * @param input0 the input-Cursor that is traversed in the "outer"
	 * 		loop.
	 * @param input1 the input-Cursor that is traversed in the "inner"
	 * 		loop.
	 * @param newCursor a parameterless function that delivers a new Cursor, when the
	 * 		Cursor <code>input1</code> cannot be reset, i.e.
	 * 		<code>input1.reset()</code> will cause a
	 * 		{@link java.lang.UnsupportedOperationException}.
	 * @param newResult a factory-method (Function) that takes two parameters as argument
	 * 		and is invoked on each tuple where the predicate's evaluation result
	 * 		is <tt>true</tt>, i.e. on each qualifying tuple before it is
	 * 		returned to the caller concerning a call to <tt>next()</tt>.
	 */
	public NestedLoopsJoin(Cursor input0, Cursor input1, Function newCursor, Function newResult) {
		this(input0, input1, newCursor, OverlapsPredicate.DEFAULT_INSTANCE, newResult);
	}

	/** Creates a new NestedLoopsJoin-Object. Uses OverlapsPredicate.
	 *
	 * @param input0 the input-Cursor that is traversed in the "outer"
	 * 		loop.
	 * @param input1 the input-Cursor that is traversed in the "inner"
	 * 		loop.
	 * @param newResult a factory-method (Function) that takes two parameters as argument
	 * 		and is invoked on each tuple where the predicate's evaluation result
	 * 		is <tt>true</tt>, i.e. on each qualifying tuple before it is
	 * 		returned to the caller concerning a call to <tt>next()</tt>.
	 */
	public NestedLoopsJoin(Cursor input0, Cursor input1, Function newResult){
		super(input0, input1, OverlapsPredicate.DEFAULT_INSTANCE, newResult, 0);
	}

	/** Creates a new NestedLoopsJoin. newResult is set to Tuplify.DEFAULT_INSTANCE. Uses OverlapsPredicate.
	 *
	 * @param input0 the input-Cursor that is traversed in the "outer"
	 * 		loop.
	 * @param input1 the input-Cursor that is traversed in the "inner"
	 * 		loop.
	*/
	public NestedLoopsJoin(Cursor input0, Cursor input1){
		this(input0, input1, Tuplify.DEFAULT_INSTANCE);
	}

	/** use-case for similarity-join
	 *	@param args The arguments of the main method.
	 *
	 */
	public static void main(String[] args) {

	    if (args.length == 0) {
	        args = new String[5];
	        String dataPath = xxl.core.util.XXLSystem.getDataPath(new String[] {"geo"}); 
		    args[0] = dataPath+File.separator+"rr_small.bin";
		    args[1] = dataPath+File.separator+"st_small.bin";
		    args[2] = "2";
		    args[3] = "0.2";
		    args[4] = "0.02";
	    }
	    
        if (args.length != 5) {
			System.out.println("Similarity-join for point data. If the given file-names are equal a self-join is performed.");
			System.out.println("usage: java xxl.core.spatial.cursors.NestedLoops <file-name0> <file-name1> <dim> <epsilon-distance> <fraction of elements to be used from the input>");
			return;
		}
		boolean selfJoin = false;
		final String input0 = args[0];
		final String input1 = args[1];
		if (input0.equals(input1)) {
			//                        System.out.print("APPLYING_SELF_JOIN\t");
			selfJoin = true;
		}
		final int dim = Integer.parseInt(args[2]);
		final float epsilon = Float.parseFloat(args[3]);
		final double p = Double.parseDouble(args[4]); //fraction of elements to be used from the input data
		final boolean strict = false;
		final int seed = 42; //note: seed must be passed to the Sampler (at least for input s) otherwise the result of the join will not be correct!
		Iterator r = new Sampler(new PointInputCursor(new File(input1),
				PointInputCursor.FLOAT_POINT, dim, 1024 * 1024), p, seed);
		Function newCursor = new Function() {
			public Object invoke() {
				return new Sampler(new PointInputCursor(new File(input0),
						PointInputCursor.FLOAT_POINT, dim, 1024 * 1024), p,
						seed);
			}
		};
		Iterator s = (Iterator) newCursor.invoke();
		Predicate predicate = new DistanceWithinMaximum(epsilon);
		if (strict) { //low-level nested-loops join based on a simple loop
			int res = 0;
			Object[] array = new Object[Cursors.count(s)];
			s = (Iterator) newCursor.invoke();
			for (int i = 0; i < array.length; i++)
				array[i] = s.next();
			while (r.hasNext()) {
				Object next = r.next();
				for (int i = 0; i < array.length; i++) {
					if (predicate.invoke(next, array[i])) {
						res++;
						System.out.println(Points.maxDistance((Point) next,
								(Point) array[i]));
					}
				}
			}
		} else { //lazy (XXL):
			long start = System.currentTimeMillis();
			Cursor nl = null;
			nl = new NestedLoopsJoin(r, s, newCursor, predicate,
					Tuplify.DEFAULT_INSTANCE);
			if (selfJoin) {
				nl = new Filter(nl, //a filter that removes trivial results form the result-cursor
						new Predicate() {
							public boolean invoke(Object object) {
								Object[] tuple = (Object[]) object;
								return !tuple[0].equals(tuple[1]);
							}
						});
			}/**/
			/*			int res = 0;
			 for(;nl.hasNext();res++){
			 Object[] tuple = (Object[]) nl.next();
			 System.out.println(Points.maxDistance( (Point) tuple[0], (Point) tuple[1] ) );
			 }/**/
			System.out.print(xxl.core.util.Strings.toString(args) + "\t");
			if (selfJoin)
				System.out.print("RES(divided by 2):\t"
						+ (Cursors.count(nl) / 2) + "\t");
			else
				System.out.print("RES:\t" + Cursors.count(nl) + "\t");
			System.out.println("runtime(sec):\t"
					+ (System.currentTimeMillis() - start) / 1000.0);/**/
		}
	}
}
