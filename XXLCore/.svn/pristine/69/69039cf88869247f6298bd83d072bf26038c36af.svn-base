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
 * This class provides a prototypical implementation of the any-predicate.
 * When the <tt>invoke</tt> method of this class is called, the given arguments
 * will be applied to the subquery and it will be checked if any of the objects
 * the subquery delivers satisfy the check condition. <p>
 *
 * Example:<br>
 * Consider the implementation of the query:<br>
 *	SELECT (Integer2)
 *	FROM Cursor 2
 *	WHERE ANY Integer2 = (SELECT Integer1
 *						  FROM Cursor 1)
 * by using an <tt>AnyPredicate</tt> instance
 * <code><pre>
		System.out.println("Cursor 1: integers 11 to 19");
		Cursor cursor1 = xxl.core.cursors.Cursors.wrap(
			new xxl.core.cursors.sources.Enumerator(11,20)
		);
		
		System.out.println("Cursor 2: integers 9 to 14");
		Cursor cursor2 = xxl.core.cursors.Cursors.wrap(
			new xxl.core.cursors.sources.Enumerator(9,15)
		);

		//SELECT (Integer2)
		//FROM Cursor 2
		//WHERE ANY Integer2 = (SELECT Integer1
		//		FROM Cursor 1)

		Predicate pred = new xxl.core.predicates.Equal();

		Subquery sub = new Subquery(cursor1,null,null);
		
		Predicate any0 = new AnyPredicate (sub, pred, new int[] {1});

		xxl.core.cursors.filters.Filter cursor = new xxl.core.cursors.filters.Filter(cursor2, any0);

		System.out.println("Cursor: result");

		xxl.core.cursors.Cursors.println(cursor);
 * </code></pre>
 *
 */
 
public class AnyPredicate extends Predicate {

	/**
	 * The subquery used in the any-predicate.
	 */
	protected Subquery subquery;

	/**
	 * The check condition of the any-predicate.
	 */
	protected BindingPredicate checkCondition;

	/**
	 * The Indices to the positions where the free variable
	 * emerges in the check condition.
	 */
	protected int[] checkBindIndices;

	/**
	 * Creates a new any-predicate. When the <tt>invoke</tt> method
	 * is called, the given arguments will be applied to the subquery
	 * and it will be checked if any of the objects the subquery delivers
	 * satisfy the check condition.
	 *
	 * @param subquery the subquery used in the any-predicate.
	 * @param checkCondition the check condition of the any-predicate.
	 * @param checkBindIndices the Indices to the positions where the free variable
	 *	      emerges in the check condition.
	 */
	public AnyPredicate (Subquery subquery, Predicate checkCondition, int[] checkBindIndices) {
		this.subquery = subquery;
		this.checkCondition = new BindingPredicate (checkCondition);
		this.checkBindIndices = checkBindIndices;
	}

	/**
	 * Creates a new any-predicate. When the <tt>invoke</tt> method
	 * is called, the given arguments will be applied to the subquery
	 * and it will be checked if any of the objects the subquery delivers
	 * satisfy the check condition.
	 */
	public AnyPredicate () {
	}

	/**
	 * Set the subquery of the any-predicate.
	 *
	 * @param subquery0 the subquery used in the all-predicate.
	 */
	public void setSubquery (Subquery subquery0) {
		this.subquery = subquery0;
	}

	/**
	 * Set the check condition of the any-predicate.
	 *
	 * @param checkCondition0 the check condition of the any-predicate.
	 * @param checkBindIndices0 the Indices to the positions where the free variable
	 *	  emerges in the check condition.
	 */
	public void setCheckCondition (Predicate checkCondition0, int[] checkBindIndices0) {
		this.checkCondition = new BindingPredicate (checkCondition0);
		this.checkBindIndices = checkBindIndices0;
	}

	/**
	 * When the <tt>invoke</tt> method is called, the given arguments
	 * will be applied to the subquery and it will be checked if any of
	 * the objects the subquery delivers satisfy the check condition.
	 *
	 * @param arguments the arguments to be applied to the underlying predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with applied arguments.
	 */
	public boolean invoke (Object [] arguments) {
		boolean result = false;
		if (arguments==null)
			return invoke((Object) null);
		subquery.bind(arguments);
		subquery.reset();
		Cursor outCursor = subquery;
		checkCondition.setBinds(checkBindIndices,arguments);
		while ((outCursor.hasNext())&&(!result)) {
			if (checkCondition.invoke(outCursor.next()))
				result = true;
		}
		while (outCursor.hasNext())
			outCursor.next();
		return result;
	}

	/**
	 * When the <tt>invoke</tt> method is called, the given argument
	 * will be applied to the subquery and it will be checked if any of
	 * the objects the subquery delivers satisfy the check condition.
	 *
	 * @param argument the argument to be applied to the underlying predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with applied argument.
	 */
	public boolean invoke (Object argument) {
		return invoke(new Object [] {argument});
	}

	/**
	 * When the <tt>invoke</tt> method is called, the given arguments
	 * will be applied to the subquery and it will be checked if any of
	 * the objects the subquery delivers satisfy the check condition.
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
	 * The main method contains some examples of how to use an AnyPredicate.
	 * It can also be used to test the functionality of the AnyPredicate.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main(String[] args){

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/

		System.out.println("Cursor 1: integers 11 to 19");
		Cursor cursor1 = xxl.core.cursors.Cursors.wrap(
			new xxl.core.cursors.sources.Enumerator(11,20)
		);
		
		System.out.println("Cursor 2: integers 9 to 14");
		Cursor cursor2 = xxl.core.cursors.Cursors.wrap(
			new xxl.core.cursors.sources.Enumerator(9,15)
		);

		//SELECT (Integer2)
		//FROM Cursor 2
		//WHERE ANY Integer2 = (SELECT Integer1
		//		FROM Cursor 1)

		Predicate pred = new xxl.core.predicates.Equal();

		Subquery sub = new Subquery(cursor1,null,null);
		
		Predicate any0 = new AnyPredicate (sub, pred, new int[] {1});

		xxl.core.cursors.filters.Filter cursor = new xxl.core.cursors.filters.Filter(cursor2, any0);

		System.out.println("Cursor: result");

		xxl.core.cursors.Cursors.println(cursor);

	}
}
