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

package xxl.core.cursors.joins;

import java.util.Comparator;
import java.util.Iterator;

import xxl.core.collections.queues.Heap;
import xxl.core.collections.sweepAreas.SweepArea;
import xxl.core.cursors.AbstractCursor;
import xxl.core.functions.Function;
import xxl.core.predicates.Predicate;

/**
 * A sort-merge implementation of the join operator over <tt>n</tt> input
 * iterations. This class provides an implementation of the function
 * <i>earlyMerge</i> described in "<i>Frühe Ergebnisse bei
 * Verbundoperationen</i>" ([Cam03]). The join operator takes multiple inputs and
 * computes the join operation via writing merged sequences to the outputs.
 */
public class MultiWaySortMergeJoin extends AbstractCursor {

	/**
	 * A wrapper for tuples that adds the index of the input iteration the tuple
	 * comes from to it. Additionally this class implements the
	 * {@link java.lang.Comparable comparable} interface.
	 */
	class TupleWrapper implements Comparable {
	
		/**
		 * The wrapped tuple, i.e., the original element derived from the
		 * <tt>inputNumber</tt>-th input iteration.
		 */
		public Object tuple;
	
		/**
		 * The index of the input iteration the wrapped tuple belongs to.
		 */
		public int inputNumber;
	
		/**
		 * Constructs a new wrapper for the given tuple storing the index of the
		 * input iteration the tuple belongs to.
		 *
		 * @param tuple the tuple to be wrapped.
		 * @param inputNumber the number of the input iteration the wrapped tuple
		 *        belongs to.
		 */
		public TupleWrapper(Object tuple, int inputNumber) {
			this.tuple = tuple;
			this.inputNumber = inputNumber;
		}
	
		/**
		 * Compares this wrapped tuple to another one. The required comparator
		 * can be found in the array <tt>comparators</tt> stored by the
		 * surrounding class.
		 *
		 * @param wrappedTuple the wrapped tuple that should be compared to this
		 *        one.
		 * @return a negative integer, zero, or a positive integer as this object
		 *         is less than, equal to, or greater than the specified object.
		 */
		public int compareTo(Object wrappedTuple) {
			return comparators[inputNumber][((TupleWrapper)wrappedTuple).inputNumber].compare(tuple, ((TupleWrapper)wrappedTuple).tuple);
		}	
	}

	/**
	 * The input iterations holding the data.
	 */
	protected Iterator[] inputs;

	/**
	 * Parts of the global join predicate which are checked on partially created
	 * results. Each predicate can be <tt>null</tt> if no condition should be
	 * checked. These predicate fragements can be used for pruning the number of
	 * possible join results during the calculation of the join operation. 
	 */
	protected Predicate[][] joinPredicates;

	/**
	 * This array determines the order in which an input iteration is tested
	 * against the other input iterations.
	 */
	protected int[][] sweepOrders;

	/**
	 * The comparators that are used for comparing elements of the different
	 * input iterations. 
	 */
	protected Comparator[][] comparators;

	/**
	 * A heap that is used for determining the current minimum of all inputs.
	 */
	protected Heap heap;
	
	/**
	 * The sweep-areas that are used for storing the elements of the input
	 * iterations and probing these elements for matches.
	 */
	protected SweepArea[] sweepAreas;
	
	/**
	 * An array of boolean flags determining whether the corresponding sweep-area
	 * is empty.
	 */
	protected boolean[] isEmpty;
	
	/**
	 * A function that is invoked on each qualifying tuple before it is returned
	 * to the caller concerning a call to the <tt>next</tt> method. This function
	 * works like a kind of factory method modelling the resulting object
	 * (tuple).
	 */
	protected Function newResult;
									
	/**
	 * The number of input iterations to be joined.
	 */
	protected int r;
	
	/**
	 * The current recursion depth of the join algorithm.
	 */
	protected int d = 0;

	/**
	 * Partial result of the join. The array is used to collect the elements of
	 * the different input iterations during the calculation of the join
	 * operation.
	 */
	protected Object[] queryTuples;
	
	/**
	 * The query iterations on the internally used sweep-areas that are currently
	 * used for the construction of the result-tuple stored in
	 * <tt>queryTuples</tt>.
	 */
	protected Iterator[] querys;

	/**
	 * The index of the input iteration that currently provides the minimum of
	 * all inputs. This minimal element is determined by the heap stored in the
	 * field <tt>heap</tt>.
	 */
	protected int currentInput;

	/**
	 * Creates a new multi-way sort-merge join of the given input iterations
	 * using the given sweep-areas to store the input iterations' elements and
	 * probe for join results.
	 * 
	 * <p><b>Precondition:</b> The input cursors have to be sorted!</p>
	 * 
	 * <p>The result-tuples created by this operator are represented as an array
	 * of the input iterations' elements that are participated in this join
	 * result. If the user wants to specify a different result-type, a mapping
	 * function <tt>newResult</tt> can be specified. This function works like a
	 * kind of factory method modelling the resulting object (tuple). Further a
	 * set of comparators have to be specified, in order to compare the elements
	 * of different input iterations. Every iterator given to this constructor is
	 * wrapped to a cursor.<p>
	 *
	 * @param inputs the sorted input iterations to be joined.
	 * @param sweepAreas the sweep-areas used for storing elements of the input
	 *        iterations and probing for join results.
	 * @param sweepOrders the order in which an input iteration is tested against
	 *        the other input iterations.
	 * @param joinPredicates parts of the global join predicate which are checked
	 *        on partially created results. Each predicate can be <tt>null</tt>
	 *        if no condition should be checked. These predicate fragements can
	 *        be used for pruning the number of possible join results during the
	 *        calculation of the join operation.
	 * @param comparators the comparators that are used for comparing elements of
	 *        the different input iterations.
	 * @param newResult a function that is invoked on each qualifying tuple
	 *        before it is returned to the caller concerning a call to the
	 *        <tt>next</tt> method. This function works like a kind of factory
	 *        method modelling the resulting object (tuple).
	 */				
	public MultiWaySortMergeJoin(
			Iterator[] inputs, 
			SweepArea[] sweepAreas, 
			int[][] sweepOrders,
			Predicate[][] joinPredicates, 
			Comparator[][] comparators, 
			final Function newResult
	) {
		this.inputs = inputs;
		this.sweepAreas = sweepAreas;
		this.joinPredicates = joinPredicates;
		this.sweepOrders = sweepOrders;
		this.comparators = comparators;
		this.newResult = newResult;

		this.r = inputs.length;
		this.queryTuples = new Object[r];
		this.querys = new Iterator[r];
		this.isEmpty = new boolean[r];
	} 

	/**
	 * Creates a new multi-way sort-merge join of the given input iterations
	 * using the given sweep-areas to store the input iterations' elements and
	 * probe for join results.
	 * 
	 * <p><b>Precondition:</b> The input cursors have to be sorted!</p>
	 * 
	 * <p>The result-tuples created by this operator are represented as an array
	 * of the input iterations' elements that are participated in this join
	 * result. If the user wants to specify a different result-type, a mapping
	 * function <tt>newResult</tt> can be specified. This function works like a
	 * kind of factory method modelling the resulting object (tuple). Further a
	 * set of comparators have to be specified, in order to compare the elements
	 * of different input iterations. Every iterator given to this constructor is
	 * wrapped to a cursor.<p>
	 *
	 * @param inputs the sorted input iterations to be joined.
	 * @param sweepAreas the sweep-areas used for storing elements of the input
	 *        iterations and probing for join results.
	 * @param comparators the comparators that are used for comparing elements of
	 *        the different input iterations.
	 * @param newResult a function that is invoked on each qualifying tuple
	 *        before it is returned to the caller concerning a call to the
	 *        <tt>next</tt> method. This function works like a kind of factory
	 *        method modelling the resulting object (tuple).
	 */				
	public MultiWaySortMergeJoin(
		Iterator[] inputs,
		SweepArea[] sweepAreas,
		Comparator[][] comparators,
		Function newResult
	) {
		this(
			inputs,
			sweepAreas,
			defaultSweepOrders(inputs.length),
			new Predicate[inputs.length][inputs.length],
			comparators,
			newResult
		);		
	}

	/**
	 * Creates a default query order for the sweep-areas used to store the
	 * elements of the input iterations. For every input iteration the other
	 * sweep-areas are queried according to their index.
	 * 
	 * @param r the number of input iterations.
	 * @return a default query order for the sweep-areas used to store the
	 *         elements of the input iterations.
	 */
	public static int[][] defaultSweepOrders(int r) {
		int[][] sweepOrders = new int[r][r];
		for (int i = 0; i < r; i++) {
			sweepOrders[i][0] = i;
			for (int j = 0, t = 1; j < r; j++)
				if (j != i)
					sweepOrders[i][t++] = j;
		}
		return sweepOrders;
	}
	
	/**
	 * Opens the cursor, i.e., signals the cursor to reserve resources, open
	 * the internally used heap, etc. Before a cursor has been opened calls to
	 * methods like <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield
	 * proper results. Therefore <tt>open</tt> must be called before a cursor's
	 * data can be processed. Multiple calls to <tt>open</tt> do not have any
	 * effect, i.e., if <tt>open</tt> was called the cursor remains in the state
	 * <i>opened</i> until its <tt>close</tt> method is called.
	 * 
	 * <p>Note, that a call to the <tt>open</tt> method of a closed cursor
	 * usually does not open it again because of the fact that its state
	 * generally cannot be restored when resources are released respectively
	 * files are closed.</p>
	 */
	public void open() {
		super.open();
		TupleWrapper[] wrappers = new TupleWrapper[r];
		for (int i = 0; i < r; i++)
			if (inputs[i].hasNext())
				wrappers[i] = new TupleWrapper(inputs[i].next(), i);
		heap = new Heap(wrappers);
		heap.open();
	}
	
	/**
	 * Closes the cursor, i.e., signals the cursor to clean up resources, close
	 * the internally used heap, etc. When a cursor has been closed calls to
	 * methods like <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield
	 * proper results. Multiple calls to <tt>close</tt> do not have any effect,
	 * i.e., if <tt>close</tt> was called the cursor remains in the state
	 * <i>closed</i>.
	 * 
	 * <p>Note, that a closed cursor usually cannot be opened again because of
	 * the fact that its state generally cannot be restored when resources are
	 * released respectively files are closed.</p>
	 */
	public void close() {
		super.close();
		heap.close();
	}
	
	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the join operator has more elements.
	 */
	protected boolean hasNextObject() {
		compute : while (true) {
			heap : while (d==0) {
				if (heap.isEmpty())
					break compute;	//as long as there exist input elements

				final TupleWrapper wrapper = (TupleWrapper)heap.dequeue();
				final Object currentObject = wrapper.tuple;
				currentInput = wrapper.inputNumber;

				sweepAreas[currentInput].reorganize(currentObject, currentInput);
				sweepAreas[currentInput].insert(currentObject);

				if (inputs[currentInput].hasNext()) {						
					wrapper.tuple = inputs[currentInput].next();
					heap.enqueue(wrapper);
				}

				isEmpty[currentInput] = false;
				boolean existsEmpty = false;
				emptyhandle : for (int i = 0; i < r; i++) {
					if (i == currentInput)
						continue emptyhandle;
					if (isEmpty[i]) {
						existsEmpty = true;
						continue emptyhandle;
					}
					sweepAreas[i].reorganize(currentObject, i);
					if (sweepAreas[i].size() == 0) {
						isEmpty[i] = true;
						existsEmpty = true;
					}
				}

				if (existsEmpty)
					continue heap;

				if (joinPredicates[currentInput][0] != null && !joinPredicates[currentInput][0].invoke(currentObject))
					continue heap;

				queryTuples[0] = currentObject;
				querys[1] = sweepAreas[sweepOrders[currentInput][1]].query(queryTuples, sweepOrders[currentInput], 1);
				d = 1;
			} // heap : while(d==0)
			while (d > 0) {
				querywork : while (querys[d].hasNext()) {
					queryTuples[d] = querys[d].next();
					if (joinPredicates[currentInput][d] != null && !joinPredicates[currentInput][d].invoke(queryTuples))
						continue querywork;
					if (d == r-1) {
						Object[] resultTuples = new Object[queryTuples.length];
						for (int i = 0; i < r; i++)
							resultTuples[sweepOrders[currentInput][i]] = queryTuples[i];
						next=newResult.invoke(resultTuples);
						return true;
					}
					else {
						d++;
						querys[d] = sweepAreas[sweepOrders[currentInput][d]].query(queryTuples, sweepOrders[currentInput], d);
					}
				} // querywork : while (querys[d].hasNext())
				d--;
			} // while (d>0)
		} // compute : while (true)
		return false;
	}

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 * 
	 * @return the next element in the iteration.
	 */
	protected Object nextObject() {
		return next;
	}
	
	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class. The number of inputs to use can
	 * be specified by using the command line parameter. If none are used,
	 * the default of three is used.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main(String[] args) {

		/*********************************************************************/
		/*                            Example 1                              */
		/*********************************************************************/
		
		int dim = 3;
		final int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};
		
		if (args.length > 0) {
			try {
				dim = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e) {}
		}
		
		java.util.LinkedList[] l = new java.util.LinkedList[dim];
		int inputLength = 1;
		
		for (int i = 0; i < dim; i++) {
			l[i] = new java.util.LinkedList();
			inputLength *= 10;
		}	
		for (int j = 0; j <= inputLength; j++)
			for (int i = 0; i < dim; i++)
				if (j%primes[i] == 0)
					l[i].add(new Integer(j));
		
		Iterator[] input = new Iterator[dim];
		SweepArea[] sweepAreas = new SweepArea[dim];
		Comparator[][] comparators = new Comparator[dim][];				
		for (int i = 0; i < dim; i++) {
			input[i] = l[i].listIterator();
			sweepAreas[i] = new xxl.core.collections.sweepAreas.SortMergeEquiJoinSA(
				new xxl.core.collections.sweepAreas.ListSAImplementor(),
				i,
				dim
			);
			comparators[i] = new Comparator[dim];
			java.util.Arrays.fill(comparators[i], xxl.core.comparators.ComparableComparator.DEFAULT_INSTANCE);
		}	
				
		MultiWaySortMergeJoin join = new MultiWaySortMergeJoin(
			input, 
			sweepAreas, 
			comparators, 
			Function.IDENTITY
		);

		join.open();
		
		while (join.hasNext()) {
			Object[] result = (Object[])join.next();
			System.out.print("Tuple: ( ");
			for (int i = 0; i < dim; i++)
				System.out.print(result[i]+" ");
			System.out.println(")");
		}
		
		join.close();
	}
	
} 
