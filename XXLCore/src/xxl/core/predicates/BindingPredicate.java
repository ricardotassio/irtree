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
import xxl.core.functions.Binding;

/**
 * This class provides a predicate whose arguments are partially
 * bound to some constant objects. <p>
 *
 * Example:<br>
 * Consider the predicate {@link Less} that returns true if the first
 * argument is less than the second.<br>
 * By creating a <tt>BindingPredicate</tt> instance
 * <pre>
 *     Predicate p = new BindingPredicate(new Less(), (new int [] {0}), (new Object [] {new Integer(42)}));
 * </pre>
 * <tt>p</tt> can be evaluated by calling
 * <pre>
 *     p.invoke(new Integer(2));                   //predicate: 42 < 2
 * </pre>
 * which corresponds to the call
 * <pre>
 *     p.invoke(new Object [] {new Integer(2)});  //predicate: 42 < 2
 * </pre>
 *
 * @see LeftBind
 * @see RightBind
 *
 */
 
public class BindingPredicate extends Predicate implements Binding {

	/**
	 * These objects are used as constant objects of this
	 * predicate's <tt>invoke</tt> methods. The important
	 * is that the attribute constIndices should always
	 * be sorted(!).
	 */
	protected Predicate predicate;
	
	/** Arguments for binding.*/
	protected Object [] constArguments;
	
	/** Indices for binding.*/
	protected int [] constIndices;

	/**
	 * Creates a new predicate which binds a part of the arguments of
	 * the specified predicate to the given constant objects.
	 *
	 * @param predicate0 the predicate whose arguments should be
	 *        partially bound.
	 * @param constIndices0 the indices of the arguments which
	 *        should be bound.
	 * @param constArguments0 the constant arguments to be used
	 *        in the predicate.
	 */
	public BindingPredicate (Predicate predicate0, int [] constIndices0, Object [] constArguments0) {
		this.predicate = predicate0;
		this.constIndices = null;
		this.constArguments = null;
		setBinds(constIndices0,constArguments0);
	}

	/**
	 * Creates a new predicate which binds part of the arguments of
	 * the specified predicate to <tt>null</tt>.
	 *
	 * @param predicate the predicate whose arguments should
	 *        be bound to <tt>null</tt>.
	 */
	public BindingPredicate (Predicate predicate) {
		this(predicate, null, null);
	}

	/**
	 * Creates a new predicate which binds part of the arguments of
	 * the specified predicate to <tt>null</tt>.
	 *
	 * @param predicate0 the predicate whose arguments should
	 *        be partially bound to <tt>null</tt>.
	 * @param constIndices0 the indices of the arguments which
	 *        should be bound to <tt>null</tt>. 
	 */
	public BindingPredicate (Predicate predicate0, int [] constIndices0) {
		this.predicate = predicate0;

		int i = constIndices0.length;
		setBinds(constIndices0,new Object[i]);
	}
	
	/**
	 * Set the constant values to which a part of the arguments of
	 * the wrapped predicate should be bound.
	 *
	 * @param constArguments0 the constant values to which a part of the
	 *        arguments of the wrapped predicate should be
	 *        bound.
	 */
	public void setBinds (Object [] constArguments0) {
		if (constArguments.length==constArguments0.length)
			this.constArguments = constArguments0;
	}
	
	/**
	 * Set the constant values to which a part of the arguments of
	 * the wrapped predicate should be bound and returns the changed
	 * predicate.
	 *
	 * @param constIndices0 the indices of the arguments which
	 *        should be bound to <tt>null</tt>.
	 * @param constArguments0 the constant values to which a part of the
	 *        arguments of the wrapped predicate should be
	 *        bound.
	 */
	public void setBinds (int [] constIndices0, Object [] constArguments0) {
		if (constIndices0==null) return;
		
		else {
			int len=constIndices0.length;
			for (int i=0;i<len;i++){
				if (constIndices0[i]!=-1) setBind(constIndices0[i],constArguments0[i]);
			}
		}
	}

	/**
	 * Set free all bound arguments of the wrapped predicate.
	 */
	public void restoreBinds () {
		this.constIndices = null;
		this.constArguments = null;
	} 

	/**
	 * Set a constant value to which an arguments of the wrapped
	 * predicate should be bound and returns the changed predicate.
	 *
	 * @param constIndex the index of the arguments which should
	 *	      be bound to <tt>null</tt>.
	 * @param constArgument the constant value to which an argument
	 *	      of the wrapped predicate should be bound.
	 */
	public void setBind (int constIndex, Object constArgument) {
		if (constIndex==-1) return;
		int len;
		if (constIndices!=null){
			len=constIndices.length;
			for (int i=0;i<len;i++){
				if (constIndex==constIndices[i]){
					constArguments[i]=constArgument;
					return;
				}
			}
			int[] tempConstIndices=new int[len+1];
			Object[] tempConstArguments=new Object[len+1];
			int pos=0;
			while((pos<len)&&(constIndices[pos]<constIndex)){
				tempConstIndices[pos]=constIndices[pos];
				tempConstArguments[pos]=constArguments[pos];
				pos++;
			}
			tempConstIndices[pos]=constIndex;
			tempConstArguments[pos]=constArgument;
			pos++;
			while(pos<=len){
				tempConstIndices[pos]=constIndices[pos-1];
				tempConstArguments[pos]=constArguments[pos-1];
				pos++;
			}
			constIndices=tempConstIndices;
			constArguments=tempConstArguments;
		}
		else {
			constIndices=new int[]{constIndex};
			constArguments=new Object[]{constArgument};
		}
		
	} 

	/**
	 * Returns the result of the underlying predicate's <tt>invoke</tt>
	 * method that is called with the partially bound arguments.
	 *
	 * @param arguments the arguments to the underlying predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with the partially bound
	 *         arguments.
	 */
	public boolean invoke (Object [] arguments) {
		if (arguments==null)
			arguments=new Object[0];
		if (constArguments==null)
			constArguments=new Object[0];
			
		int totalArgumentsLength = constArguments.length + arguments.length;
		Object newArguments [] = new Object [totalArgumentsLength];
		int j = 0;
		int indConst = 0;
		int ind = 0;
		while (j<totalArgumentsLength) {
			if ((indConst<constArguments.length)&&(j==constIndices[indConst])) {
				newArguments[j] = constArguments[indConst];
				indConst++;
			}
			else {
				if(ind<arguments.length) {
					newArguments[j] = arguments[ind];
					ind ++;
				}
			}
			j++;
		}

		switch (newArguments.length) {
			case 0:
				return predicate.invoke();
			case 1:
				return predicate.invoke(newArguments[0]);
			case 2:
				return predicate.invoke(newArguments[0], newArguments[1]);
			default: {
				return predicate.invoke(newArguments);
//				return predicate.invoke(newArguments[0], newArguments[1]);
			}
		}
	}

	/**
	 * Returns the result of the underlying predicate's <tt>invoke</tt>
	 * method that is called with the partially bound arguments.
	 *
	 * @param argument the free argument to the underlying predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with the partially bound arguments.
	 */
	public boolean invoke (Object argument) {
		return invoke(new Object [] {argument});
	}

	/**
	 * Returns the result of the underlying predicate's <tt>invoke</tt>
	 * method that is called with the partially bound arguments.
	 *
	 * @param argument0 the frist free argument to the underlying
	 *	      predicate.
	 * @param argument1 the second free argument to the underlying
	 *        predicate.
	 * @return the result of the underlying predicate's <tt>invoke</tt>
	 *         method that is called with the partially bound
	 *         argument.
	 */
	public boolean invoke (Object argument0, Object argument1) {
		return invoke(new Object [] {argument0, argument1});

	}


	/**
	 * The main method contains some examples of how to use a BindingPredicate.
	 * It can also be used to test the functionality of a BindingPredicate.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 *        submit parameters when the main method is called.
	 */
	public static void main (String [] args){
		// create a BindingPredicate that implements the Less-Predicate
		BindingPredicate p = new BindingPredicate(new Less(), (new int [] {0}), (new Integer [] {(new Integer (42))}));
		System.out.println (p.invoke(new Integer [] {(new Integer (2))}));

		System.out.println(p.invoke(new Integer (44)));


		p.restoreBinds();
		p.setBind(1, new Integer (42));
		System.out.println(p.invoke(new Integer [] {(new Integer (2))}));

		p.setBind(0,new Integer (44));
		System.out.println(p.invoke());

		System.out.println();
	}
}