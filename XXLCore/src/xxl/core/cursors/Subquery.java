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

package xxl.core.cursors;

import xxl.core.functions.Binding;

/**
 * This class is a prototypical implementation for subqueries which may be used
 * in {@link xxl.core.predicates.ExistPredicate exist},
 * {@link xxl.core.predicates.AllPredicate all},
 * {@link xxl.core.predicates.AnyPredicate any} predicates, etc. It allows to
 * treat a part of the parameters of a subquery as free variables and delivers
 * the result when they are bound.
 * 
 * <p>The cursor of the subquery has to support the reset-method. After one
 * reset the result has to be recomputed and must not be buffered.</p>
 *
 * @see xxl.core.predicates.AllPredicate
 * @see xxl.core.predicates.AnyPredicate
 * @see xxl.core.predicates.ExistPredicate
 */
public class Subquery extends SecureDecoratorCursor implements Binding {

	/**
	 * The subquery, whose result will be delivered when its free variables are
	 * bound.
	 */
	protected Cursor outCursor;

	/**
	 * This two-dimensional array saves information of the free variables.
	 * Two-dimensional index: (index of the bindings, index of the parameter)
	 */
	protected int[][] mConstIndices;

	/**
	 * The bindings used in this subquery.
	 */
	protected Binding[] bindings;

	/**
	 * The arguments for prebinding.
	 */
	protected Object[] constArguments;

	/**
	 * The indices for prebinding.
	 */
	protected int[] constIndices;

	/**
	 * Creates a new instance of a subquery. It allow to treat a part of the
	 * parameters of a subquery as free variables and delivers the result when
	 * they are bound.
	 *
	 * @param outCursor the subquery, whose result will be delivered when its
	 *        free variables are bound.
	 * @param bindings the bindings used in this subquery.
	 * @param mConstIndices the two-dimensional array saving information of the
	 *        free variables.<br />
	 *		  <i>Example:</i><br />
	 *        <tt>mConstIndices[i][j]=k</tt>: the <tt>j</tt>-th
	 *        free variable is the <tt>k</tt>-th parameter of the <tt>i</tt>-th
	 *        binding.<br />
	 *        <tt>k=-1</tt>: the free variable is not used in the binding.
	 */
	public Subquery(Cursor outCursor, Binding[] bindings, int[][] mConstIndices) {
		super(outCursor);
		this.outCursor = outCursor;
		this.bindings = bindings;
		this.mConstIndices = mConstIndices;
		int i = 0;
		if (bindings != null) {
			while ((i < mConstIndices.length) && (i < bindings.length)) {
				for (int j = 0; j < mConstIndices[i].length; j++)
					if (mConstIndices[i][j] != -1)
						bindings[i].setBind(mConstIndices[i][j], null);
				i++;
			}
		}
		constArguments = new Object[0];
		constIndices = new int[0];
	}

	/**
	 * Set the values of the free variables of the subquery.
	 *
	 * @param arguments the objects to which the free variables of the wrapped
	 *        subquery should be bound.
	 */
	public void bind(Object[] arguments) {
		if (arguments == null)
			arguments = new Object[0];
		int totalArgumentsLength = constArguments.length + arguments.length;
		Object newArguments[] = new Object[totalArgumentsLength];
		int pos = 0;
		int indConst = 0;
		int ind = 0;
		while (pos < totalArgumentsLength) {
			if ((indConst < constArguments.length) && (pos == constIndices[indConst])) {
				newArguments[pos] = constArguments[indConst];
				indConst++;
			}
			else
				if (ind < arguments.length) {
					newArguments[pos] = arguments[ind];
					ind++;
				}
			pos++;
		}

		if (bindings == null)
			return;
		int i = 0;
		while ((i < mConstIndices.length) && (i < bindings.length)) {
			for (int j = 0; j < mConstIndices[i].length; j++)
				if ((mConstIndices[i][j] != -1) && (j < newArguments.length))
					bindings[i].setBind(mConstIndices[i][j], newArguments[j]);
			i++;
		}
	}

	/**
	 * Set the values of the free variables of the subquery.
	 *
	 * @param arguments the objects to which the free variables of the wrapped
	 *        subquery should be bound.
	 */
	public void setBinds(Object[] arguments) {
		if (constArguments.length == arguments.length)
			this.constArguments = arguments;
	}

	/**
	 * Set the values of given parameters of the subquery.
	 *
	 * @param indices the indices of the free variables which should be bound to
	 *        the given value.
	 * @param arguments the objects to which the given predicate-parameter of
	 *        the wrapped subquery should be bound.
	 */
	public void setBinds(int[] indices, Object[] arguments) {
		if (indices == null)
			return;
		else {
			int len = indices.length;
			for (int i = 0; i < len; i++) {
				if (indices[i] != -1)
					setBind(indices[i], arguments[i]);
			}
		}
	}

	/**
	 * Set the value of a given parameter of the subquery.
	 *
	 * @param index the index of the free variable which should be bound to the
	 *        given value.
	 * @param argument the object to which the given predicate-parameter of the
	 *        wrapped subquery should be bound.
	 */
	public void setBind(int index, Object argument) {
		if (index == -1)
			return;
		int len;
		if (constIndices != null) {
			len = constIndices.length;
			for (int i = 0; i < len; i++)
				if (index == constIndices[i]) {
					constArguments[i] = argument;
					return;
				}
			int[] tempConstIndices = new int[len + 1];
			Object[] tempConstArguments = new Object[len + 1];
			int pos = 0;
			while ((pos < len) && (constIndices[pos] < index)) {
				tempConstIndices[pos] = constIndices[pos];
				tempConstArguments[pos] = constArguments[pos];
				pos++;
			}
			tempConstIndices[pos] = index;
			tempConstArguments[pos] = argument;
			pos++;
			while (pos <= len) {
				tempConstIndices[pos] = constIndices[pos - 1];
				tempConstArguments[pos] = constArguments[pos - 1];
				pos++;
			}
			constIndices = tempConstIndices;
			constArguments = tempConstArguments;
		}
		else {
			constIndices = new int[] {
				index
			};
			constArguments = new Object[] {
				argument
			};
		}
	}

	/**
	 * Remove all bindings from the free variables of the subquery, i.e., restore
	 * the initial state of the subquery without any bindings.
	 */
	public void restoreBinds() {
		constArguments = new Object[0];
		constIndices = new int[0];
	}

	/**
	 * The main method contains some examples of how to use an
	 * {@link xxl.core.predicates.ExistPredicate exist} predicate. It can also
	 * be used to test the functionality of the exist predicate.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to submit
	 *        parameters when the main method is called.
	 */
	public static void main(String[] args) {

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/

		System.out.println("Cursor 1: integers 8 to 15");
		Cursor cursor1 = xxl.core.cursors.Cursors.wrap(new xxl.core.cursors.sources.Enumerator(8, 16));

		System.out.println("Cursor 2: integers 9 to 19");
		Cursor cursor2 = xxl.core.cursors.Cursors.wrap(new xxl.core.cursors.sources.Enumerator(9, 20));

		System.out.println("Cursor 3: integers 1 to 11");
		Cursor cursor3 = xxl.core.cursors.Cursors.wrap(new xxl.core.cursors.sources.Enumerator(1, 12));
		
		// SELECT Integer3
		// FROM Cursor3
		// WHERE EXIST(
		//     SELECT Integer2
		//     FROM Cursor 2
		//     WHERE ANY Integer3 = (
		//         SELECT Integer1
		//         FROM Cursor 1
		//         WHERE Integer2=Integer1 AND Integer1>=Integer3
		//     )
		// )

		xxl.core.predicates.Predicate equal = new xxl.core.predicates.Equal();
		xxl.core.predicates.Predicate greaterEqual = new xxl.core.predicates.GreaterEqual();
		// bind Integer2 in Equal
		xxl.core.predicates.BindingPredicate bindEqual = new xxl.core.predicates.BindingPredicate(equal, new int[] {0});
		// bind Integer3 in GreaterEqual
		xxl.core.predicates.BindingPredicate bindGrEqual = new xxl.core.predicates.BindingPredicate(greaterEqual, new int[] {1});
		// and bindings
		xxl.core.predicates.Predicate and = new xxl.core.predicates.And(bindEqual, bindGrEqual);
		xxl.core.cursors.filters.Filter sel = new xxl.core.cursors.filters.Filter(cursor1, and);
		// inner subquery with two bindings
		Subquery sub = new Subquery(
			sel,
			new Binding[] {
				bindEqual,
				bindGrEqual
			},
			new int[][] {
				new int[] {0, -1},
				new int[] {-1, 1}
			}
		);

		// any condition
		xxl.core.predicates.Predicate equal2 = new xxl.core.predicates.Equal();
		// integer3 has to bind in any condition
		xxl.core.predicates.BindingPredicate bindEqual2 = new xxl.core.predicates.BindingPredicate(equal2, new int[] {0});
		// any predicate will called as invoke(Integer2), so nothing has to bind in the any condition (value -1)
		xxl.core.predicates.Predicate anyPred = new xxl.core.predicates.AnyPredicate(sub, bindEqual2, new int[] {-1});
		// filter in subquery of cursor2
		xxl.core.cursors.filters.Filter sel2 = new xxl.core.cursors.filters.Filter(cursor2, anyPred);

		// subquery where Integer3 has to bind in inner subquery and any condition
		Subquery sub2 = new Subquery(
			sel2,
			new Binding[] {
				sub,
				bindEqual2
			},
			new int[][] {
				new int[] {1},
				new int[] {0}
			}
		);

		// exist predicate of the outer subquery
		xxl.core.predicates.Predicate exist0 = new xxl.core.predicates.ExistPredicate(sub2);
		// filter of cursor3
		xxl.core.cursors.filters.Filter cursor = new xxl.core.cursors.filters.Filter(cursor3, exist0);

		// cursor output
		System.out.println("Cursor: result");
		xxl.core.cursors.Cursors.println(cursor);
	}

}
