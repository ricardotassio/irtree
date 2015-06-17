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

package xxl.core.predicates;

import xxl.core.cursors.Cursor;
import xxl.core.cursors.Subquery;

/**
 * This class provides a prototypical implementation of the exist-predicate.
 * The <tt>invoke</tt> method of this class returns <tt>true</tt>, if the
 * subquery delivers a non-empty result with the applied arguments. <p>
 *
 * Example:<br>
 * Consider the implementation of the query:<br>
 *	SELECT (Integer2)<br>
 *	FROM Cursor 2<br>
 *	WHERE EXIST (SELECT Integer1<br>
 *				 FROM Cursor 1<br>
 *				 WHERE Integer1=Integer2)<br>
 * by using an <tt>ExistPredicate</tt> instance
 * <code><pre>
		System.out.println("Cursor 1: integers 8 to 15");
		Cursor cursor1 = xxl.core.cursors.Cursors.wrap(
			new xxl.core.cursors.sources.Enumerator(8,16)
		);
		
		System.out.println("Cursor 2: integers 9 to 19");
		Cursor cursor2 = xxl.core.cursors.Cursors.wrap(
			new xxl.core.cursors.sources.Enumerator(9,20)
		);

		//SELECT (Integer2)
		//FROM Cursor 2
		//WHERE EXIST (SELECT Integer1
		//		FROM Cursor 1
		//		WHERE Integer1=Integer2)

		Predicate pred = new xxl.core.predicates.Equal();
		BindingPredicate bindPred = new BindingPredicate(pred, new int[]{1});

		xxl.core.cursors.filters.Filter sel = new xxl.core.cursors.filters.Filter (cursor1, bindPred);

		Subquery sub = new Subquery(sel, new BindingPredicate[]{bindPred}, new int[][]{new int[]{1}});
		Predicate exist0 = new ExistPredicate (sub);

		xxl.core.cursors.filters.Filter cursor = new xxl.core.cursors.filters.Filter(cursor2, exist0);

		System.out.println("Cursor: result");

		xxl.core.cursors.Cursors.println(cursor);
 * </code></pre>
 *
 */

public class ExistPredicate extends Predicate {

	/**
	 * The subquery used in the exist-predicate.
	 */
	protected Subquery subquery;

	/**
	 * Creates a new exist-predicate. The <tt>invoke</tt> method
	 * returns <tt>true</tt>, if the subquery delivers a non-empty
	 * result with the applied arguments.
	 *
	 * @param subquery the subquery used in the exist-predicate.
	 */
	public ExistPredicate (Subquery subquery) {
		this.subquery = subquery;
	}

	/**
	 * Creates a new exist-predicate. The <tt>invoke</tt> method
	 * of this class returns <tt>true</tt>, if the subquery
	 * delivers a non-empty result with the applied arguments.
	 */
	public ExistPredicate () {
	}

	/**
	 * Set the subquery of the exist-predicate.
	 *
	 * @param subquery0 the subquery used in the exist-predicate.
	 */
	public void setSubquery (Subquery subquery0) {
		this.subquery = subquery0;
	}

	/**
	 * Test whether the subquery of the predicate delivers a non-empty
	 * result with the applied arguments.
	 *
	 * @param arguments the arguments to be applied to the underlying predicate.
	 *	  The arguments are 2D indicated: arguments[i][j] saves the value for
	 *	  the j-th parameter of the i-th predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with applied arguments.
	 */
	public boolean invoke (Object [][] arguments) {
		if (arguments==null)
			return invoke((Object) null);
		subquery.setBinds(arguments);
		subquery.reset();
		Cursor outCursor = subquery;
		if (outCursor.hasNext()) {
			outCursor.next();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Test whether the subquery of the predicate delivers a non-empty
	 * result with the applied arguments.
	 *
	 * @param arguments the arguments to be applied to the underlying predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with applied arguments.
	 */
	public boolean invoke (Object [] arguments) {
		if (arguments==null)
			return invoke((Object) null);
		subquery.bind(arguments);
		subquery.reset();
		Cursor outCursor = subquery;
		if (outCursor.hasNext()) {
			outCursor.next();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Test whether the subquery of the predicate delivers a non-empty
	 * result with the applied argument.
	 *
	 * @param argument the argument to be applied to the underlying predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with applied argument.
	 */
	public boolean invoke (Object argument) {
		return invoke(new Object [] {argument});
	}

	/**
	 * Test whether the subquery of the predicate delivers a non-empty
	 * result with the applied arguments.
	 *
	 * @param argument0 the first argument to be applied to the underlying predicate.
	 * @param argument1 the second argument to be applied to the underlying predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with applied arguments.
	 */
	public boolean invoke (Object argument0, Object argument1) {
		return invoke(new Object [] {argument0, argument1});

	}


	/**
	 * The main method contains some examples of how to use an ExistPredicate.
	 * It can also be used to test the functionality of the ExistPredicate.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main(String[] args){

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/

		System.out.println("Cursor 1: integers 8 to 15");
		Cursor cursor1 = xxl.core.cursors.Cursors.wrap(
			new xxl.core.cursors.sources.Enumerator(8,16)
		);
		
		System.out.println("Cursor 2: integers 9 to 19");
		Cursor cursor2 = xxl.core.cursors.Cursors.wrap(
			new xxl.core.cursors.sources.Enumerator(9,20)
		);

		//SELECT (Integer2)
		//FROM Cursor 2
		//WHERE EXIST (SELECT Integer1
		//		FROM Cursor 1
		//		WHERE Integer1=Integer2)

		Predicate pred = new xxl.core.predicates.Equal();
		BindingPredicate bindPred = new BindingPredicate(pred, new int[]{1});

		xxl.core.cursors.filters.Filter sel = new xxl.core.cursors.filters.Filter (cursor1, bindPred);

		Subquery sub = new Subquery(sel, new BindingPredicate[]{bindPred}, new int[][]{new int[]{1}});
		Predicate exist0 = new ExistPredicate (sub);

		xxl.core.cursors.filters.Filter cursor = new xxl.core.cursors.filters.Filter(cursor2, exist0);

		System.out.println("Cursor: result");

		xxl.core.cursors.Cursors.println(cursor);
	}	
}
