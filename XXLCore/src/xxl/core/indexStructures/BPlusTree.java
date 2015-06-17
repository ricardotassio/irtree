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
package xxl.core.indexStructures;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Collections;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import xxl.core.cursors.Cursor;
import xxl.core.cursors.mappers.Mapper;
import xxl.core.cursors.sources.EmptyCursor;
import xxl.core.functions.Function;
import xxl.core.functions.Constant;
import xxl.core.predicates.Predicate;
import xxl.core.collections.MappedList;
import xxl.core.collections.containers.Container;
import xxl.core.collections.MapEntry;
import xxl.core.io.converters.BooleanConverter;
import xxl.core.io.converters.Converter;
import xxl.core.io.converters.IntegerConverter;
import xxl.core.io.converters.MeasuredConverter;

/** This class implemets a B+Tree.
 * 
 * For a detailed discussion see Douglas Comer: 
 * "The Ubiquitous B-Tree",
 * ACM Comput. Surv. 11(2), 121-137, 1979.
 */
public class BPlusTree extends Tree {
			
	/** This declares the block size of the underlaying external storage. This field is initialized finally 
	 * in the constructor by the value given from the user as a parameter. It is used to compute the maximal 
	 * capacity of the tree's nodes. 
	 * @see BPlusTree#B
	 */
	public final int BLOCK_SIZE;
	
	/** This is the maximal capacity of the tree's nodes (except the root), i.e. the maximal number of entries which 
	 * a node can contain. Its value is the quotient of the given block size and the maximal entry size.
	 * @see BPlusTree#BLOCK_SIZE
	 */
	protected int B;
	
	/** This is the minimal capacity of the tree's nodes, i.e. the minimal number of entries which 
	 * a node can contain. Its value is: <code> {@link BPlusTree#B}*{@link BPlusTree#minCapacityRatio}</code>.
	 * @see BPlusTree#B
	 * @see BPlusTree#minCapacityRatio
	 */
	protected int D;
	
	/** This defines the minimal capacity ratio of the tree's nodes. 
	 */
	protected final float minCapacityRatio;
	
	/** This is a Converter for data objects stored in the tree. It is used to read or write these data objects 
	 * from or into the external storage.
	 */
	protected MeasuredConverter dataConverter;
	
	/** This is a <tt>Converter</tt> for keys used by the tree. It is used to read or write these keys 
	 * from or into the external storage.
	 */
	protected MeasuredConverter keyConverter;

	/** This <tt>Converter</tt> is used to read or write nodes (i.e. blocks) from or into the external storage.
	 */
	protected NodeConverter nodeConverter;
	
	/** This <tt>Function</tt> is used to get the key of a data object.
	 * Example:
	 * <code><pre>
	 * 		Comparable key = (Comparable)getKey.invoke(data);
	 * </pre></code>
	 */
	protected Function getKey;
	
	/** This <tt>Function</tt> is used by the method {BPlusTree#createSeparator(Comparable)} to create 
	 * <tt>Separators</tt>. The user has to initialize this field with a suitable <tt>Function</tt>.
	 * 
	 * @see BPlusTree#createSeparator(Comparable)
	 */
	protected Function createSeparator;

	/** This <tt>Function</tt> is used by the method {BPlusTree#createKeyRange(Comparable, Comparable)} to create a 
	 * <tt>KeyRange</tt>. The user has to initialize this field with a suitable <tt>Function</tt>.
	 * 
	 * @see BPlusTree#createKeyRange(Comparable, Comparable)
	 */	
	protected Function createKeyRange;

	/** Flag indicating if a reorganization took place. 
	 */
	protected boolean reorg=false;
		
	/** Creates a new <tt>BPlusTree</tt>.
	 * 
	 * @param blockSize the block size of the underlaying storage
	 * @param minCapacityRatio the minimal capacity ratio of the tree's nodes
	 */
	public BPlusTree(int blockSize, float minCapacityRatio) {
		super();
		if((minCapacityRatio>=1)||(minCapacityRatio<=0)) 
			throw new IllegalArgumentException("Illegal min. capacity: "+minCapacityRatio);
		this.minCapacityRatio=minCapacityRatio;
		BLOCK_SIZE= blockSize;
		nodeConverter= createNodeConverter();
	}

	/** Creates a new <tt>BPlusTree</tt>. The minimal capacity ratio is set to the default value 0.5 (i.e. 50%).
	 * 
	 * @param blockSize the block size of the underlaying storage.
	 */
	public BPlusTree(int blockSize) {
		this(blockSize, 0.5f);
	}
	
	/** Initializes the <tt>BPlusTree</tt>.
	 * 
	 * @param getKey the <tt>Function</tt> to get the key of a data object
	 * @param keyConverter the <tt>Converter</tt> for the keys used by the tree
	 * @param dataConverter the <tt>Converter</tt> for data objects stored in the tree
	 * @param createSeparator a factory <tt>Function</tt> to create <tt>Separators</tt>
	 * @param createKeyRange a factory <tt>Function</tt> to create <tt>KeyRanges</tt>
	 * @param getSplitMinRatio a <tt>Function</tt> to determine the minimal relative number of entries 
	 * which the node maycontain after a split
	 * @param getSplitMaxRatio a <tt>Function</tt> to determine the maximal relative number of entries 
	 * which the node may contain after a split
	 * @return the initialized <tt>BPlusTree</tt> itself
	 */
	protected BPlusTree initialize(Function getKey, MeasuredConverter keyConverter, MeasuredConverter dataConverter, 
									Function createSeparator, Function createKeyRange, 
									Function getSplitMinRatio, Function getSplitMaxRatio) {
		this.getKey= getKey;
		this.keyConverter=keyConverter;
		this.dataConverter=dataConverter;
		this.nodeConverter= createNodeConverter();
		this.createSeparator= createSeparator;
		this.createKeyRange= createKeyRange;
		int recordSize= nodeConverter.getMaxRecordSize();
		int space=this.BLOCK_SIZE-nodeConverter.headerSize();
		this.B = space/recordSize;
		this.D= (int)(minCapacityRatio*this.B);
		Function getDescriptor=new Function() {
			public Object invoke(Object o) {
				if(o instanceof Separator) return (Separator) o;
				if(o instanceof IndexEntry) return ((IndexEntry) o).separator;
				return createSeparator(key(o));
			}
		};
		Predicate overflows=new Predicate() {
			public boolean invoke(Object o) {
				Node node=(Node) o;
				return node.number()>B;
			}
		};
		Predicate underflows=new Predicate() {
			public boolean invoke(Object o) {
				Node node=(Node) o;
				return node.number()<D;
			}
		};		
		return initialize(getDescriptor, underflows, overflows, getSplitMinRatio, getSplitMaxRatio);										
	}
	
	/** Initializes the <tt>BPlusTree</tt>.
	 * 
	 * @param getKey the <tt>Function</tt> to get the key of a data object
	 * @param getContainer returns the Container that is used to store the nodes of the tree
	 * @param determineContainer a Function to determine the Container that is used to store a new node 
	 * created by a split operation
	 * @param keyConverter the <tt>Converter</tt> for the keys used by the tree
	 * @param dataConverter the <tt>Converter</tt> for data objects stored in the tree
	 * @param createSeparator a factory <tt>Function</tt> to create <tt>Separators</tt>
	 * @param createKeyRange a factory <tt>Function</tt> to create <tt>KeyRanges</tt>
	 * @param getSplitMinRatio a <tt>Function</tt> to determine the minimal relative number of entries 
	 * which the node maycontain after a split
	 * @param getSplitMaxRatio a <tt>Function</tt> to determine the maximal relative number of entries 
	 * which the node may contain after a split
	 * @return the initialized <tt>BPlusTree</tt> itself
	 */
	public BPlusTree initialize (Function getKey, Function getContainer, Function determineContainer,  
								MeasuredConverter keyConverter, MeasuredConverter dataConverter, 
								Function createSeparator, Function createKeyRange, 
								Function getSplitMinRatio, Function getSplitMaxRatio) {
		this.getContainer= getContainer;
		this.determineContainer= determineContainer;
		return initialize(getKey, keyConverter, dataConverter, createSeparator, createKeyRange, 
					getSplitMinRatio, getSplitMaxRatio);
	}
	
	/** Initializes the <tt>BPlusTree</tt>.
	 * 
	 * NOTE: This method is used in the case that the tree has only one container. 
	 * For multidisk storage the user has to use the method:  
	 * {@link BPlusTree#initialize (Function getKey, Container container,  
	 *						MeasuredConverter keyConverter, MeasuredConverter dataConverter, 
	 *						Function createSeparator, Function createKeyRange, 
	 *						Function getSplitMinRatio, Function getSplitMaxRatio)}.
	 *
	 * @param getKey the <tt>Function</tt> to get the key of a data object
	 * @param container the Container that is used to store the nodes of the tree
	 * @param keyConverter the <tt>Converter</tt> for the keys used by the tree
	 * @param dataConverter the <tt>Converter</tt> for data objects stored in the tree
	 * @param createSeparator a factory <tt>Function</tt> to create <tt>Separators</tt>
	 * @param createKeyRange a factory <tt>Function</tt> to create <tt>KeyRanges</tt>
	 * @param getSplitMinRatio a <tt>Function</tt> to determine the minimal relative number of entries 
	 * which the node maycontain after a split
	 * @param getSplitMaxRatio a <tt>Function</tt> to determine the maximal relative number of entries 
	 * which the node may contain after a split
	 * @return the initialized <tt>BPlusTree</tt> itself
	 * 
	 * @see BPlusTree#initialize (Function getKey, Container container,  
	 *						MeasuredConverter keyConverter, MeasuredConverter dataConverter, 
	 *						Function createSeparator, Function createKeyRange, 
	 *						Function getSplitMinRatio, Function getSplitMaxRatio)}.
	 */	
	public BPlusTree initialize (Function getKey, final Container container,  
							MeasuredConverter keyConverter, MeasuredConverter dataConverter, 
							Function createSeparator, Function createKeyRange, 
							Function getSplitMinRatio, Function getSplitMaxRatio) {
		Function getContainer=new Constant(container);
		Function determineContainer=new Constant(container);
		return initialize(getKey,  getContainer, determineContainer,  
							keyConverter, dataConverter, createSeparator, createKeyRange,  
							getSplitMinRatio, getSplitMaxRatio);
	}


	/** Initializes the <tt>BPlusTree</tt>. It initializes {@link xxl.core.indexStructures.Tree#getSplitMinRatio} and 
	 * {@link xxl.core.indexStructures.Tree#getSplitMaxRatio} on the following wise: <code><pre>
	    	Function getSplitMinRatio=new Constant(minCapacityRatio); 
	    	Function getSplitMaxRatio=new Constant(1.0);
	   		return initialize(getKey, container,  keyConverter, dataConverter, 
	  							createSeparator, createKeyRange, getSplitMinRatio, getSplitMaxRatio);							 	
	   </pre></code> 
	 * NOTE: This method is used in the case that the tree has only one container. 
	 * For multidisk storage the user has to use the method:  
	 * {@link BPlusTree#initialize (Function, Function, Function, MeasuredConverter, MeasuredConverter, Function, Function)}.
	 *
	 * @param getKey the <tt>Function</tt> to get the key of a data object
	 * @param container the Container that is used to store the nodes of the tree
	 * @param keyConverter the <tt>Converter</tt> for the keys used by the tree
	 * @param dataConverter the <tt>Converter</tt> for data objects stored in the tree
	 * @param createSeparator a factory <tt>Function</tt> to create <tt>Separators</tt>
	 * @param createKeyRange a factory <tt>Function</tt> to create <tt>KeyRanges</tt>
	 * @return the initialized <tt>BPlusTree</tt> itself
	 * 
	 * @see BPlusTree#initialize (Function, Function, Function, MeasuredConverter, MeasuredConverter, Function, Function)
	 * @see BPlusTree#initialize (Function, Container, MeasuredConverter, MeasuredConverter, Function, Function, Function, Function)
	 */	
	public BPlusTree initialize (final Function getKey, final Container container, 
							MeasuredConverter keyConverter, MeasuredConverter dataConverter, 
							 final Function createSeparator, final Function createKeyRange) {
		Function getSplitMinRatio=new Constant(this.minCapacityRatio);
		Function getSplitMaxRatio=new Constant(1.0);
		return initialize(getKey, container,  keyConverter, dataConverter, 
							createSeparator, createKeyRange, getSplitMinRatio, getSplitMaxRatio);							 	
	}
	
	/** Initializes the <tt>BPlusTree</tt>. It initializes {@link xxl.core.indexStructures.Tree#getSplitMinRatio} and 
	 * {@link xxl.core.indexStructures.Tree#getSplitMaxRatio} on the following wise: <code><pre>
	    	Function getSplitMinRatio=new Constant(minCapacityRatio); 
	    	Function getSplitMaxRatio=new Constant(1.0);
	   		return initialize(getKey, getContainer,  determineContainer, keyConverter, dataConverter, 
	  							createSeparator, createKeyRange, getSplitMinRatio, getSplitMaxRatio);							 	
	   </pre></code>
	 *   
	 * @param getKey the <tt>Function</tt> to get the key of a data object
	 * @param getContainer returns the Container that is used to store the nodes of the tree
	 * @param determineContainer a Function to determine the Container that is used to store a new node 
	 * created by a split operation
	 * @param keyConverter the <tt>Converter</tt> for the keys used by the tree
	 * @param dataConverter the <tt>Converter</tt> for data objects stored in the tree
	 * @param createSeparator a factory <tt>Function</tt> to create <tt>Separators</tt>
	 * @param createKeyRange a factory <tt>Function</tt> to create <tt>KeyRanges</tt>
	 * @return the initialized <tt>BPlusTree</tt> itself
	 * 
	 * @see BPlusTree#initialize (Function, Function, Function, MeasuredConverter, MeasuredConverter, Function, Function, Function, Function)
	 * @see BPlusTree#initialize (Function, Container, MeasuredConverter, MeasuredConverter, Function, Function)
	 */	
	public BPlusTree initialize (Function getKey, Function getContainer, Function determineContainer,
									MeasuredConverter keyConverter, MeasuredConverter dataConverter, 
									 final Function createSeparator, final Function createKeyRange) {
		Function getSplitMinRatio=new Constant(this.minCapacityRatio);
		Function getSplitMaxRatio=new Constant(1.0);
		return initialize(getKey, getContainer, determineContainer, keyConverter, 
							dataConverter, createSeparator, createKeyRange, 
							getSplitMinRatio, getSplitMaxRatio);
	}
	
	/** Initializes the <tt>BPlusTree</tt> and sets the member fields <tt>rootEntry</tt> and <tt>rootDescriptor</tt> 
	 * to null.
	 * 
	 * @param getDescriptor the new {@link xxl.core.indexStructures.Tree#getDescriptor}
	 * @param underflows a <tt>Predicate</tt> which test whether a node underflows
	 * @param overflows a <tt>Predicate</tt> which test whether a node overflows
	 * @param getSplitMinRatio a <tt>Function</tt> to determine the minimal relative number of entries 
	 * which the node maycontain after a split
	 * @param getSplitMaxRatio a <tt>Function</tt> to determine the maximal relative number of entries 
	 * which the node may contain after a split
	 * @return the initialized <tt>BPlusTree</tt> itself
	 */		
	protected BPlusTree initialize (Function getDescriptor, Predicate underflows, Predicate overflows,   
								Function getSplitMinRatio, Function getSplitMaxRatio) {
		return initialize(getDescriptor, this.getContainer, this.determineContainer, 
							underflows, overflows, getSplitMinRatio, getSplitMaxRatio);
	}

	/** Initializes the <tt>BPlusTree</tt> and sets the member fields <tt>rootEntry</tt> and <tt>rootDescriptor</tt> 
	 * to null.
	 * 
	 * @param getDescriptor the new {@link xxl.core.indexStructures.Tree#getDescriptor}
	 * @param getContainer returns the Container that is used to store the nodes of the tree
	 * @param determineContainer a Function to determine the Container that is used to store a new node 
	 * created by a split operation
	 * @param underflows a <tt>Predicate</tt> which test whether a node underflows
	 * @param overflows a <tt>Predicate</tt> which test whether a node overflows
	 * @param getSplitMinRatio a <tt>Function</tt> to determine the minimal relative number of entries 
	 * which the node maycontain after a split
	 * @param getSplitMaxRatio a <tt>Function</tt> to determine the maximal relative number of entries 
	 * which the node may contain after a split
	 * @return the initialized <tt>BPlusTree</tt> itself
	 */					
	protected BPlusTree initialize (Function getDescriptor, Function getContainer, Function determineContainer,
								Predicate underflows, Predicate overflows,   
								Function getSplitMinRatio, Function getSplitMaxRatio) {
		return(BPlusTree)super.initialize((IndexEntry)null, (Descriptor)null, getDescriptor, 
											getContainer, determineContainer, underflows, overflows,  
											getSplitMinRatio, getSplitMaxRatio);
	}
	
	/** Unsupported method! Always throws UnsupportedOperationException.
	 * This is the basic method to create a new tree. It is not sufficient to call the constructor. 
	 * Before the tree is usable, it must be initialized.
	 * 
	 * @param rootEntry the new {@link Tree#rootEntry}
	 * @param rootDescriptor the new {@link Tree#rootDescriptor}
	 * @param getDescriptor the new {@link Tree#getDescriptor}
	 * @param getContainer the new {@link Tree#getContainer}
	 * @param determineContainer the new {@link Tree#determineContainer}
	 * @param underflows the new {@link Tree#underflows}
	 * @param overflows the new {@link Tree#overflows}
	 * @param getSplitMinRatio the new {@link Tree#getSplitMinRatio}
	 * @param getSplitMaxRatio the new {@link Tree#getSplitMaxRatio}
	 * @return the initialized tree (i.e. return this)
	 * @throws UnsupportedOperationException every time
	 */
	public Tree initialize (IndexEntry rootEntry, Descriptor rootDescriptor, Function getDescriptor, 
							Function getContainer, Function determineContainer, 
							Predicate underflows, Predicate overflows, 
							Function getSplitMinRatio, Function getSplitMaxRatio) {	
		throw new UnsupportedOperationException();
	}
	
		
	/** Creates a new node on a given level.
	 * 
	 * @param level the level of the new Node
	 * 
	 * @see xxl.core.indexStructures.Tree#createNode(int)
	 */
	public Tree.Node createNode(int level) {
		Node node=new Node(level);
		return node;
	}
	
	/** Creates a new Index Entry.
	 * 
	 * @param parentLevel the level of the node in which the new indexEntry is stored
	 * @return a new indexEntry
	 * 
	 * @see xxl.core.indexStructures.Tree#createIndexEntry(int)
	 * @see xxl.core.indexStructures.BPlusTree.IndexEntry
	 */
	public Tree.IndexEntry createIndexEntry(int parentLevel) {
		return new IndexEntry(parentLevel);
	}

	/** This method invokes the Function {BPlusTree#createSeparator} with the given 
	 * argument and casts the result to <tt>Separator</tt>.
	 * 
	 * @param sepValue the separation value of the new <tt>Separator</tt>
	 * @return the new created <tt>Separator</tt>
	 */
	protected Separator createSeparator(Comparable sepValue) {
		return (Separator) this.createSeparator.invoke(sepValue);
	}

	/** This method invokes the Function {BPlusTree#createKeyRange} with the given 
	 * arguments and casts the result to <tt>KeyRange</tt>.
	 * @param min the minimal bound of the <tt>KeyRange</tt>
	 * @param max the maximal bound of the <tt>KeyRange</tt>
	 * @return the new created <tt>KeyRange</tt>.
	 */
	protected KeyRange createKeyRange(Comparable min, Comparable max) {
		return (KeyRange)createKeyRange.invoke(min, max);
	}

	/** This method invokes the Function {BPlusTree#getKey} with the given argument and casts the result to 
	 * <tt>Comparable</tt>. It is used to get the key value of a data object.
	 * 
	 * @param data the data object whose key is required
	 * @return the key of the given data object
	 */
	protected Comparable key(Object data) {
		return (Comparable) getKey.invoke(data);
	}
	
	/** It is used to determine the <tt>Separator</tt> of a given entry 
	 * (e.g. <tt>IndexEntry</tt>, data entry, etc.).
	 * It is equivalent to:<pre><code>
	  		return (Separator)descriptor(entry);
	   </code></pre>
	 * @param entry the entry whose <tt>Separator</tt> is required
	 * @return the <tt>Separator</tt> of the given entry
	 */
	protected Separator separator(Object entry) {
		return (Separator)descriptor(entry);
	}
	
	/** Returns the maximal size of a <tt>Node</tt>, i.e. {@link BPlusTree#B}.
	 * 
	 * @return {@link BPlusTree#B}
	 */
	public int getB() {
		return B;
	}

	/** Returns the minimal size of a <tt>Node</tt>, i.e. {@link BPlusTree#D}.
	 * 
	 * @return {@link BPlusTree#D}
	 */	
	public int getD(){ 
		return D;
	}

	/** Creates a new <tt>NodeConverter</tt>. 
	 * 
	 * @return a new <tt>NodeConverter</tt>
	 */
	protected NodeConverter createNodeConverter() {
		return new NodeConverter();
	}
	
	/** Gives the <tt>NodeConverter</tt> used by the <tt>BPlusTree</tt>.
	 *  
	 * @return the <tt>NodeConverter</tt> <tt>BPlusTree</tt>
	 * 
	 * @see BPlusTree#nodeConverter
	 */
	public NodeConverter nodeConverter() {
		return nodeConverter;
	}
	
	/** Gives the <tt>Container</tt> used by the <tt>BPlusTree</tt> to store the nodes. 
	 * It is equivalent to: <code><pre>
	  		return (Container)this.getContainer.invoke(this);
	   </pre></code>
	 * NOTE: This only makes sense if the whole Tree is stored in a single <tt>Container</tt>. 
	 * This method is used by the default <tt>NodeContverter</tt>. Therefor the 
	 * user has to overwrite the <tt>NodeConverter</tt> if the tree works in multdisk mode.
	 * 
	 * @return the <tt>Container</tt> of the <tt>BPlusTree</tt>
	 */
	public Container container() {
		return (Container)this.getContainer.invoke(this);
	}

	/** Checks whether the <tt>BPlusTree</tt> is empty. The <tt>BPlusTree</tt> is empty 
	 * if the <tt>rootEntry</tt> is <tt>null</tt> or the root node is empty.
	 * 
	 * @return <tt>true</tt> if the <tt>BPlusTree</tt> is empty and <tt>false</tt> otherwise
	 */ 
	public boolean isEmpty() {
		return (rootEntry==null)||(rootEntry.get().number()==0);
	}

	/** This method is used to remove a data object from the tree. It uses the method 
	 * {@link BPlusTree#separator(Object)} to get the <tt>Separator</tt> of the given object. 
	 * Thereafter it searches in the tree for an Object with the same <tt>Separator</tt>. 
	 * The method {Separator#equals{Object)} is used to checks whether two Separators are the same.
	 * 
	 * @param data the object which has to be removed
	 * @return the removed object if the search was successful, and <tt>null</tt> otherwise
	 */
	public Object remove(final Object data) {
		return remove(data, new Predicate() {
			public boolean invoke(Object o1, Object o2) {
				return key(o1).compareTo(key(o2))==0;
			}
		});
	}

	/** Inserts an object into a given level of the tree. If level>0 <tt>data</tt> has 
	 * to be an <tt>IndexEntry</tt> (this only makes sense for trees whose index nodes
	 * support Node.grow()).
	 * 
	 * @param data the data to insert
	 * @param descriptor is used to find the node into which data must be inserted
	 * @param targetLevel is the tree-level into which <tt>data</tt> has to be inserted 
	 * 			(<tt>targetLevel==0</tt> means insert into the suitable leaf node)
	 */	
	protected void insert (Object data, Descriptor descriptor, int targetLevel) {
		if (rootEntry()==null) {
			Comparable key=((Separator)descriptor).sepValue();
			rootDescriptor = createKeyRange(key, key);
			grow(data);
		}
		else super.insert(data, descriptor, targetLevel);
	}
	
	/** This method computes the path from the root to the leaf node refered by the given <tt>indexEntry</tt>. 
	 * The path nodes are fixed in the underlayig buffer. The caller has to unfix them after they are not used any 
	 * more. If the given node is not a leaf node an {@link java.lang.UnsupportedOperationException} is thrown. 
	 * The method calls {@link xxl.core.indexStructures.BPlusTree#pathToLeaf(xxl.core.indexStructures.BPlusTree.Node)} where <tt>node</tt> is the leaf refered by <tt>indexEntry</tt>.
	 * 
	 * @param indexEntry the Index Entry which refers the leaf node
	 * @return the path to the leaf node refered by the given index entry
	 * @throws UnsupportedOperationException if invoked an a none leaf node 
	 *  
	 * @see xxl.core.indexStructures.BPlusTree#pathToLeaf(xxl.core.indexStructures.BPlusTree.Node)
	 */		
	protected Stack pathToLeaf(IndexEntry indexEntry) throws UnsupportedOperationException {
		if(indexEntry.level()!=0) 
			throw new UnsupportedOperationException("The node is not a leaf node.");
		Node node=(Node)indexEntry.get(false);
		return pathToLeaf(node);
	}

	/** This method computes the path from the root to a leaf node. If the given node is not a leaf node 
	 * an {@link java.lang.UnsupportedOperationException} is thrown.
	 * The method computes the key range of the leaf and then calls the method 
	 * {@link BPlusTree#pathToNode(BPlusTree.KeyRange, int)} (level=0). 
	 * The path nodes are fixed in the underlayig buffer. The caller has to unfix them after they 
	 * are not used any more.
	 *  
	 * @param leaf node whose path is needed
	 * @return the path to the given leaf
	 * @throws UnsupportedOperationException if invoked an a none leaf node
	 * 
	 * @see #pathToNode(BPlusTree.KeyRange, int)
	 */
	protected Stack pathToLeaf(Node leaf) throws UnsupportedOperationException {
		if(leaf.level()!=0) throw new UnsupportedOperationException("The node is not a leaf node.");
		Comparable min=key(leaf.getFirst());
		Comparable max=key(leaf.getLast());
		return pathToNode(createKeyRange(min, max), 0);
	}
	
	/** This method determines the path from the root to a <tt>Node</tt>. The <tt>Node</tt> is specified by its key 
	 * range. The level is needed to know on which level the path has to end. The path nodes are fixed in 
	 * the underlayig buffer. The caller has to unfix them after they are not used any more. 
	 * 
	 * @param nodeRegion the <tt>KeyRange</tt> of the <tt>Node</tt>
	 * @param level the level on which the <tt>Node</tt> is
	 * @return the path from the root to the <tt>Node</tt> with the given <tt>KeyRange</tt> on the given level 
	 */
	protected Stack pathToNode(KeyRange nodeRegion, int level) {
		return ((QueryCursor)query(nodeRegion,level)).path();
	}
	
	/** Searches the single data object with the given key stored in the <tt>BPlusTree</tt>.
	 *   
	 * @param key the key of the data object which has to be searched
	 * @return the single data object with the given key or <tt>null</tt> if no such object could be found
	 */
	public Object exactMatchQuery(Comparable key) {
		Predicate p = new Predicate() {
				public boolean invoke() {
					return true;
				}
				public boolean invoke(Object argument) {
					return invoke();
				}
		};
		return exactMatchQuery(key, p);
	}
	
	/** Searches the single leaf entry with the given key stored in the <tt>BPlusTree</tt> and fulfills the given 
	 * Predicate. The method {@link BPlusTree#query(Descriptor queryDescriptor, int targetLevel)} takes over the 
	 * search process.
	 *  
	 * @param key the key of the data object which has to be searched
	 * @param test a Predicate which the found entry has to fulfill
	 * @return the single data object with the given key or <tt>null</tt> if no such object could be found
	 */	
	protected Object exactMatchQuery(Comparable key, Predicate test) {
		Cursor results= query(createKeyRange(key, key),0);
		Object result=null;
		while(results.hasNext()) {
			Object o=results.next();
			if(test.invoke(o)) {
				result=o;
				break;
			}
		}
		results.close();
		return result;
	}

	/** Searches all data objects stored in the tree whose key lie in the given key range [minKey, maxKey].
	 * The method {@link BPlusTree#query(Descriptor queryDescriptor, int targetLevel)} takes over the search process.
	 * 
	 * @param minKey the minimal bound of the query's key range
	 * @param maxKey the maximal bound of the query's key range
	 * @return a <tt>Cursor</tt> pointing all qualified entries
	 */	
	public Cursor rangeQuery(Comparable minKey, Comparable maxKey) {
		KeyRange range= createKeyRange(minKey, maxKey);
		return query(range, 0);
	}
	
	/** This method is an implemtation of an efficient querying algorithm. The result is a lazy <tt>Cursor</tt> 
	 * pointing to all entries whose keys are contained in <tt>queryDescriptor</tt>.
	 *  
	 * @param queryDescriptor describes the query in terms of a <tt>Descriptor</tt>. In this case it has to an 
	 * instance of <tt>Separator</tt> oder <tt>KeyRange</tt> 
	 * @param targetLevel the tree-level to provide the answer-objects
	 * @return a lazy <tt>Cursor</tt> pointing to all response objects
	*/	
	public Cursor query(Descriptor queryDescriptor, int targetLevel) {
		KeyRange queryRange;
		if(!(queryDescriptor instanceof KeyRange)) {
			Comparable key=((Separator)queryDescriptor).sepValue();
			queryRange=createKeyRange(key, key);
		}
		else queryRange=(KeyRange)queryDescriptor;
		if((targetLevel>= height())|| !(rootDescriptor).overlaps(queryRange)) 
			return new EmptyCursor();
		return query((IndexEntry)rootEntry, queryRange, targetLevel);
	}
	
	/** This method is an implemtation of an efficient querying algorithm. The result is a lazy <tt>Cursor</tt> 
	 * pointing to all entries whose keys are contained in <tt>queryDescriptor</tt>.
	 *  
	 * @param subRootEntry the root of the subtree in which the query is to execute
	 * @param queryInterval describes the query in terms of a <tt>Descriptor</tt>. In this case it has to be an 
	 * instance of <tt>KeyRange</tt> 
	 * @param targetLevel the tree-level to provide the answer-objects
	 * @return a lazy <tt>Cursor</tt> pointing to all response objects
	*/	
	protected Cursor query(IndexEntry subRootEntry, KeyRange queryInterval, int targetLevel) {			
		return new QueryCursor(subRootEntry, queryInterval, targetLevel);
	}
	
	/** This method is called after an entry was removed in order to repair the index structure.
	 * 
	 * @param path the path from the root to the <tt>Node</tt> from which the entry was removed
	 */
	protected void treatUnderflow(Stack path) {
		while(((Node)node(path)).redressUnderflow(path));		
	}
	
	/** This method is called after a new entry was inserted in order to repair the index structure.
	 * 
	 * @param path the path from the root to the <tt>Node</tt> into which the new entry was inserted
	 */	
	protected void treatOverflow(Stack path) {
		post(path);
	}	
	
	/** Return the flag {@link #reorg} and resets it to <tt>false</tt>. 
	 * 
	 * @return value of #reorg.
	 */
	public boolean wasReorg() {
		boolean ret = reorg;
		reorg = false;
		return ret;
	}
	
	/** This class describes the index entries of the <tt>BPlusTree</tt> (i.e. the entries of the non-leaf nodes). 
	 * Each <tt>IndexEntry</tt> refers to a {@link BPlusTree.Node Node} which is the root node of the 
	 * subtree. Each <tt>IndexEntry</tt> has a <tt>Separator</tt> which descripes the key range of this subtree.
	 * 
	 * @see xxl.core.indexStructures.Tree.IndexEntry
	 */
	public class IndexEntry extends Tree.IndexEntry {
		
		/** The <tt>Separator</tt> of the <tt>IndexEntry</tt>. It describes the 
	 	  * key range of the subtree refered by this <tt>IndexEntry</tt>.
	 	  */
		protected Separator separator;
		
		/** Creates a new <tt>IndexEntry</tt> with a given {@link xxl.core.indexStructures.Tree.IndexEntry#parentLevel}.
		 * 
		 * @param parentLevel the parent level of the new <tt>IndexEntry</tt>
		 */		
		public IndexEntry(int parentLevel) {
			super(parentLevel);
		}

		/** Initializes the (new created) <tt>IndexEntry</tt> using some split information. The <tt>SplitInfo</tt> is used to 
		 * determine the new <tt>Separator</tt> of the new <tt>IndexEntry</tt>.
		 * 
		 * @param splitInfo contains information about the split which led to create this <tt>IndexEntry</tt> 
		 * @return the <tt>IndexEntry</tt> itself
		 * 
		 * @see BPlusTree.Node.SplitInfo
		 * @see BPlusTree.Node.SplitInfo#separatorOfNewNode()
		 */
		public Tree.IndexEntry initialize (Tree.Node.SplitInfo splitInfo) {
			super.initialize(splitInfo);
			Separator sep=((Node.SplitInfo)splitInfo).separatorOfNewNode();
			if(sep==null) return this;
			return initialize(sep);
		}
		
		/** Initializes the <tt>IndexEntry</tt> by the given <tt>Separator</tt>.
		 * @param separator the new <tt>Separator</tt> of the current <tt>IndexEntry</tt>.
		 * @return the <tt>IndexEntry</tt> itself.
		 */
		public IndexEntry initialize (Separator separator) {
			this.separator =(Separator)separator.clone();
			return this;
		}
		
		/** Initializes the <tt>IndexEntry</tt> with the given ID and <tt>Separator</tt>.
		 * 
		 * @param id the new ID of the current <tt>IndexEntry</tt>
		 * @param separator the new <tt>Separator</tt> of the current <tt>IndexEntry</tt>
		 * @return the <tt>IndexEntry</tt> itself
		 */	
		public IndexEntry initialize(Object id, Separator separator) {
			return ((IndexEntry)this.initialize(id)).initialize(separator);
		}
		
		/** Gives the <tt>Separator</tt> of this <tt>IndexEntry</tt>.
		 * 
		 * @return the <tt>Separator</tt> of this <tt>IndexEntry</tt>
		 */
		public Separator separator() {
			return separator;
		}
		
		/** Chooses the subtree which should be followed during an insertion. 
		 * Let N be the <tt>Node</tt> refered by the current <tt>IndexEntry</tt>. The method returns the
		 * <tt>IndexEntry</tt> stored in N which refers to the subtree of N in which the search has to be continued.
		 * First {@link xxl.core.indexStructures.Tree.IndexEntry#chooseSubtree(Descriptor, Stack)} is called to choose the subtree. Thereafter 
		 * The given <tt>Descriptor</tt> is united with the <tt>Separator</tt> of the choosen <tt>IndexEntry</tt>. 
		 * Finally the choosen <tt>IndexEntry</tt> will be returned.
		 * 
		 * @param descriptor the <tt>Separator</tt> of the entry which is to be inserted 
		 * @param path a stack to store the path from the <tt>IndexEntry</tt>, with which the search began, to 
		 * 			returned one
		 * @return the next <tt>IndexEntry</tt> in whose subtree the search has to be continued
		 */				
		public Tree.IndexEntry chooseSubtree (Descriptor descriptor, Stack path) {
			IndexEntry indexEntry = (IndexEntry)super.chooseSubtree(descriptor,path);
			indexEntry.separator().union(descriptor);
			return indexEntry;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer sb= new StringBuffer("(");
			sb.append(separator);
			sb.append(", ->");
			sb.append(id);
			sb.append(")");
			return sb.toString();
		}
	}
	
	/** This class is used to represent leaf- and non-leaf nodes of a <tt>BPlusTree</tt>. A <tt>Node</tt> contains 
	 * a List of entries (data objects or <tt>IndexEntries</tt>). All <tt>Nodes</tt> are stored in 
	 * <tt>Containers</tt>.
	 */
	public class Node extends Tree.Node {

		/** A <tt>List</tt> to hold the entries of the <tt>Node</tt>.
		 */
		protected List entries;
		
		/** This is a reference on the <tt>Node</tt> containing the smallest key which is larger than all keys stored 
		 * in the subtree of this <tt>Node</tt>.
		 */ 
		protected IndexEntry nextNeighbor;
		
		/** A <tt>Function</tt> to create the entries list. That makes the <tt>Node</tt> more flexible, 
		 * because it can use any implementation of the abstract interface {@link java.util.List}.
		 */ 
		protected Function createEntryList;
		
		/** Creates a new <tt>Node</tt> on a given level.
		 * 
		 * @param level the level of the new <tt>Node</tt>
		 * @param createEntryList a factory <tt>Function</tt> to create the entries list
		 * 
		 * @see BPlusTree.Node#createEntryList
		 */
		public Node(int level, Function createEntryList) {
			super();
			initialize(level, createEntryList);
		}
		
		/** Creates a new <tt>Node</tt> on a given level. An {@link java.util.ArrayList ArrayList} is used to store 
		 * the entries.
		 * 
		 * @param level the level of the new <tt>Node</tt>
		 */		
		public Node(int level) {
			super();
			initialize(level, new Function () {
				public Object invoke() {
					return new ArrayList(BPlusTree.this.B+1);
				}
			});
		}
		
		/** Initializes the (new) <tt>Node</tt> with a level and a <tt>Function</tt> for creating the 
		 * <tt>Entries List</tt>.
		 * 
		 * @param level the level of the <tt>Node</tt>
		 * @param createEntryList a factory <tt>Function</tt> to create the entries list 
		 * @return the initialized <tt>Node</tt>
		 */
		private Node initialize(int level, Function createEntryList) {
			this.level=level;
			this.createEntryList=createEntryList;
			entries=(List) this.createEntryList.invoke();
			return this;
		}
		
		/** Initializes the <tt>Node</tt> and inserts a new entry into it.
		 *  
		 * @param entry the entry which has to be inserted
		 * @return <tt>SplitInfo</tt> which contains information about a possible split
		 */
		protected Tree.Node.SplitInfo initialize(Object entry) {
			Stack path = new Stack();
			if (level()>0) {
				IndexEntry indexEntry = (IndexEntry)entry;
				if(indexEntry==BPlusTree.this.rootEntry)
					indexEntry.separator = createSeparator(((KeyRange)rootDescriptor).minBound());
			}
			grow(entry, path);
			return new SplitInfo(path);
		}
		
		/** Gives the next neighbor of this <tt>Node</tt>.
		 * 
		 * @return the next neighbor of this <tt>Node</tt>
		 * 
		 * @see BPlusTree.Node#nextNeighbor
		 */ 
		public IndexEntry nextNeighbor() {
			return nextNeighbor;		
		}
		
		/** Gives the entry stored on the given position in the <tt>Node</tt>.
		 * 
		 * @param index the position of the requiered entry
		 * @return the entry stored on the given position in the <tt>Node</tt> or 
		 * <tt>null</tt> if the <tt>Node</tt> is empty
		 */ 
		public Object getEntry(int index) {
			if (entries.isEmpty()) return null;
			return entries.get(index);
		}
		
		
		/** Gives the last entry of the <tt>Node</tt>.
		 * 
		 * @return the last entry of the <tt>Node</tt> or <tt>null</tt> if the <tt>Node</tt> is empty
		 */ 
		public Object getLast() {
			if (entries.isEmpty()) return null;
			return entries.get(entries.size()-1);
		}

		/** Gives the first entry of the <tt>Node</tt>.
		 * 
		 * @return the first entry of the <tt>Node</tt> or <tt>null</tt> if the <tt>Node</tt> is empty
		 */ 
		public Object getFirst() {
			if(entries.isEmpty()) return null;
			return entries.get(0);
		}

		/** Gives the number of entries which are currently stored in this <tt>Node</tt>.
		 * 
		 * @return the number of entries which are currently stored in this <tt>Node</tt>
		*/
		public int number() {
			return entries.size();
		}

		/**  Gives an <tt>Iterator</tt> pointing to all entries stored in this <tt>Node</tt>
		 * 
		 * @return an <tt>Iterator</tt> pointing to all entries stored in this <tt>Node</tt>
		*/
		public Iterator entries(){
			return iterator();
		}

		/**  Gives an <tt>Iterator</tt> pointing to all entries stored in this <tt>Node</tt>
		 * 
		 * @return an <tt>Iterator</tt> pointing to all entries stored in this <tt>Node</tt>
		*/
		protected Iterator iterator() {
			return entries.iterator();
		}

		/** Gives an <tt>Iterator</tt> pointing to the <tt>Descriptors</tt> of each entry in this <tt>Node</tt>.
		 * 
		 * @param nodeDescriptor the descriptor of this <tt>Node</tt>
		 * @return an <tt>Iterator</tt> pointing to the <tt>Descriptors</tt> of each entry in this <tt>Node</tt>
		*/
		public Iterator descriptors(Descriptor nodeDescriptor){
			return new Mapper( iterator(), 
								new Function () {
									public Object invoke (Object entry) {
										return separator(entry);
									}
								});
		}

		/** Returns an <tt>Iterator</tt> of entries whose <tt>Separators</tt> overlap with the 
		 * <tt>queryDescriptor</tt>.
		 * 
		 * @param queryDescriptor the <tt>KeyRange</tt> describing the query 
		 * @return an <tt>Iterator</tt> of entries whose <tt>Separators</tt> overlap with the 
		 * <tt>queryDescriptor</tt>
		 */
		public Iterator query (Descriptor queryDescriptor) {
			KeyRange qInterval= (KeyRange) queryDescriptor;
			int offset=level()==0? 0: 1;
			int minIndex= search(qInterval.minBound());
			int maxIndex= search(qInterval.maxBound());
			minIndex=(minIndex>=0)? minIndex: -minIndex-1-offset;
			maxIndex=(maxIndex>=0)? maxIndex: -maxIndex-1-offset;
			maxIndex=Math.min(maxIndex+1, number());
			List response=entries.subList(minIndex, maxIndex);
			return response.iterator();
		}
		
		/** Searches the given key in this <tt>Node</tt> using the binary search algorithm on a sorted list.
		 * 
		 * @param key the key which is to search in this <tt>Node</tt>
		 * @return the postion of the key if it was found or its insertion position in the list otherwise 
		 */
		protected int binarySearch(Comparable key) {
			if(entries.size()==0) return -1;
			int start=level()==0? 0: 1;
			List sepValues = new MappedList( entries.subList(start, entries.size()), 
											new Function() {
												public Object invoke(Object entry) {
													return separator(entry).sepValue();
												}
										});			
			int minIndex = Collections.binarySearch(sepValues, key);
			minIndex = minIndex>=0 ? minIndex+start : minIndex-start;
			return minIndex;			
		}

		/** Searches the given key in this <tt>Node</tt>. The default implementation simply calls the method 
		 * {@link BPlusTree.Node#binarySearch(Comparable)}.
		 * 
		 * @param key the key which is to search in this <tt>Node</tt>
		 * @return the postion of the key if it was found or its insertion position in the list otherwise 
		 */		
		protected int search(Comparable key) {
			return binarySearch(key);
		}
		
		/** Searches the given <tt>Separator</tt> in this <tt>Node</tt>. The default implementation takes the 
		 * separation value of the given <tt>Separator</tt> and searches for it using the method 
		 * {@link BPlusTree.Node#search(Comparable)}.
		 * 
		 * @param separator the <tt>Separator</tt> which is to search in this <tt>Node</tt>
		 * @return the postion of the <tt>Separator</tt> if it was found or its insertion position in the list 
		 * otherwise
		 */		
		protected int search(Separator separator) {
			if(entries.size()==0) return -1;
			if(!separator.isDefinite()) return 0;
			return search(separator.sepValue());
		}
		
		/** Searches the given <tt>IndexEntry</tt> in this <tt>Node</tt>. The default implementation takes the 
		 * <tt>Separator</tt> of the given <tt>IndexEntry</tt> and searches for it using the method 
		 * {@link BPlusTree.Node#search(Separator)}.
		 * 
		 * @param indexEntry the <tt>IndexEntry</tt> which is to search in this <tt>Node</tt>
		 * @return the postion of the <tt>IndexEntry</tt> if it was found or its insertion position in the list 
		 * otherwise
		 */				
		protected int search(IndexEntry indexEntry) {
			return search(indexEntry.separator);
		}
						
		/** Chooses the subtree which is followed during an insertion.
		 * 
		 * @param descriptor the <tt>Separator</tt> of the entry which is to insert
		 * @param path the path from the root to this <tt>Node</tt>. The default implementation does not use this 
		 * path
		 * @return  the <tt>IndexEntry</tt> pointing to the subtree which is followed during an insertion
		 */
		protected Tree.IndexEntry chooseSubtree(Descriptor descriptor, Stack path) {
			Separator separator = (Separator) descriptor;
			int minIndex = search(separator);
			minIndex = (minIndex>=0) ? minIndex : -minIndex-2;
			return (IndexEntry) entries.get(minIndex);
		}
				
		/** Posts the <tt>SplitInfo</tt> from the child <tt>Nodes</tt> to the current <tt>Node</tt>. The method 
		 * inserts the new <tt>IndexEntry</tt> into the <tt>Node</tt> using the method 
		 * {@link BPlusTree.Node#grow(Object, Stack)}. It gets the path from the given <tt>SplitInfo</tt>.
		 *  
		 * @param splitInfo contains the information about the split which led to create the new <tt>IndexEntry</tt>
		 * @param newIndexEntry the new <tt>IndexEntry</tt> created by the split
		 * 
		 * @see BPlusTree.Node#grow(Object, Stack)
		 */
		protected void post(Tree.Node.SplitInfo splitInfo, Tree.IndexEntry newIndexEntry) {
			grow(newIndexEntry, splitInfo.path);
		}

		/** Inserts an entry into the current <tt>Node</tt>. If level>0 data must be an index entry. A condition for 
		 * the correctitude of this method is that an <tt>IndexEntry</tt> (level > 0) is never inserted at the first 
		 * position into a non empty node.
		 *  
		 * @param entry the entry which has to be inserted into the node
		 * @param path the path from the root to the current node. The default implementaion does not use this path
		 * 
		 * @see BPlusTree.Node#grow(Object)
		 */
		protected void grow(Object entry, Stack path) {
			grow(entry);
		}
		
		/** Inserts a new entry into this <tt>Node</tt> at the suitable position. A condition for the correctitude of
		 * this method is that an <tt>IndexEntry</tt> (level > 0) is never inserted at the first position into a non 
		 * empty node. 
		 * 
		 * @param entry the new entry which has to be inserted
		 */
		protected void grow(Object entry) {
			int index; 
			if(entries.isEmpty()) index=0;
			else {
				index=search((separator(entry)));
				if(index>=0) throw new IllegalArgumentException("Insertion failed: An entry having the same key " +					"was found");
				index=-index-1; 
			}
			entries.add(index, entry);
		}
		
		/** Treats overflows which occur during an insertion. 
		 * The method {@link xxl.core.indexStructures.Tree.Node#redressOverflow(Stack, xxl.core.indexStructures.Tree.Node, List)} of
		 * the super class is used to split the overflowing <tt>Node</tt> in two <tt>Nodes</tt>. It creates a new 
		 * <tt>IndexEntry</tt> for the new <tt>Node</tt>. This <tt>IndexEntry</tt> will be added to the given List. 
		 * The method {@link xxl.core.indexStructures.Tree.Node#post(xxl.core.indexStructures.Tree.Node.SplitInfo, xxl.core.indexStructures.Tree.IndexEntry)} 
		 * will be used to post the created <tt>IndexEntries</tt> 
		 * to the parent <tt>Node</tt>. Finally the new <tt>Node</tt> is linked from the split <tt>Node</tt> as 
		 * {@link BPlusTree.Node#nextNeighbor}.
		 * 
		 * @param path the path from the root to the overflowing <tt>Node</tt>
		 * @param parentNode the parent <tt>Node</tt> of the overflowing <tt>Node</tt>
		 * @param newIndexEntries a List to carry the new <tt>IndexEntries</tt> created by the split
		 * @return a <tt>SplitInfo</tt> containing required information about the split
		 */
		protected Tree.Node.SplitInfo redressOverflow (Stack path, Tree.Node parentNode, List newIndexEntries) {
			Node node = (Node) node(path);
			SplitInfo splitInfo =(SplitInfo) super.redressOverflow(path,parentNode,newIndexEntries);
			IndexEntry newIndexEntry=(IndexEntry)newIndexEntries.get(newIndexEntries.size()-1);
			node.nextNeighbor=newIndexEntry;
			return splitInfo;
		}

		/** This method is used to split the first <tt>Node</tt> on the path in two <tt>Nodes</tt>. The current 
		 * <tt>Node</tt> should be the empty new <tt>Node</tt>. The method distributes the entries of overflowing 
		 * <tt>Node</tt> to the both <tt>Nodes</tt>.
		 * 
		 * @param path the <tt>Node</tt> already visited during this insert
		 * @return a <tt>SplitInfo</tt> containig all needed information about the split
		*/
		protected Tree.Node.SplitInfo split(Stack path) {
			reorg=true;
			Node node=(Node) node(path);
			List newEntries=node.entries.subList((node.number()+1)/2, node.number());
			Separator sepNewNode=(Separator)separator(newEntries.get(0)).clone();
			entries.addAll(newEntries);
			newEntries.clear();
			nextNeighbor=node.nextNeighbor;
			return (new SplitInfo(path)).initialize(sepNewNode);
		}

		/** Searches the given <tt>IndexEntry</tt> and removes it from this <tt>Node</tt>.
		 * 
		 * @param indexEntry the <tt>IndexEntry</tt> which is to remove
 		 * @return the removed <tt>IndexEntry</tt> if it was found or <tt>null</tt> otherwise
		 */
		protected IndexEntry remove(IndexEntry indexEntry) {
			if(level==0) return null;
			int index=search(indexEntry);
			return (IndexEntry)remove(index);
		}

		/** Removes the entry stored in the <tt>Node</tt> on the given position.
		 * 
		 * @param index the position of the entry which is to remove
		 * @return the removed entry if the given position is valid or <tt>null</tt> otherwise
		 */
		protected Object remove(int index) {
			if((index<0)||(index>= entries.size())) return null; 
			Object entry=entries.get(index);
			if((level>0) && (index>0)) ((IndexEntry)entries.get(index-1)).separator.union(separator(entry));
			entries.remove(index);
			return entry;
		}

		/** Treats underflows which occur during a remove operation. 
		 * If the given path is empty an <tt>IllegalStateException</tt> is thrown, else the method 
		 * {@link BPlusTree.Node#redressUnderflow(Stack, boolean)} is called with the parameter path and <tt>true</tt> to 
		 * repair the underflow. 
		 * 
		 * @param path the path from the root to the underflowing <tt>Node</tt> 
		 * @return a boolean which indicates whether the parent <tt>Node</tt> was updated during the operation
		 */
		protected boolean redressUnderflow(Stack path) {
			if(path.isEmpty()) throw new IllegalStateException();
			return redressUnderflow(path, true);
		}
		
		/** Treats underflows which occur during a remove operation. 
		 * In case of underflow the method {@link BPlusTree.Node#redressUnderflow (BPlusTree.IndexEntry, BPlusTree.Node, Stack, boolean)} is 
		 * called to repair the underflow.
		 *  
		 * @param path the path from the root to the underflowing <tt>Node</tt>
		 * @param up signals whether the treated <tt>Nodes</tt> have to be updated in the underlaying 
		 * <tt>Container</tt>
		 * @return a boolean which indicates whether the parent <tt>Node</tt> was been updated during the operation
		 */
		protected boolean redressUnderflow(Stack path, boolean up) {
			MapEntry pathEntry=(MapEntry) path.pop();
			IndexEntry indexEntry=	(IndexEntry) pathEntry.getKey();
			Node node = (Node) pathEntry.getValue();
			boolean parentUpdated=false;
			//The root node...
			if(path.isEmpty()&&(node.number()==1)&&(node.level>0)) {
				IndexEntry newRootEntry=(IndexEntry)node.getFirst();
				rootEntry.remove();
				rootEntry=newRootEntry;
				((IndexEntry)rootEntry).separator=null;
			}
			else {
				if(!path.isEmpty()&& node.underflows())
					parentUpdated=redressUnderflow(indexEntry, node, path, up);
				else 
					if(up) {
						indexEntry.update(node);
						indexEntry.unfix();
					}
			}
			return parentUpdated;
		}
		
		/** Treats underflows which occur during a remove operation. It calls the method 
		 * {@link BPlusTree.Node#merge(BPlusTree.IndexEntry, Stack)} to redistribute the elements of the underflowing 
		 * <tt>Node</tt> an a suitable sibling or to merge them so that the underflow is repaired.
		 *  
		 * @param indexEntry the <tt>IndexEntry</tt> refering to the underflowing <tt>Node</tt>
		 * @param node the underflowing <tt>Node</tt>
		 * @param path the path from the root to parent of the underflowing <tt>Node</tt>
		 * @param up signals whether the treated <tt>Nodes</tt> have to be updated in the underlaying 
		 * <tt>Container</tt>
		 * @return a boolean which indicate whether the parent <tt>Node</tt> was been updated during the operation
		 */		
		protected boolean redressUnderflow(IndexEntry indexEntry, Node node, Stack path, boolean up) {
			Node parentNode = (Node)node(path);
			// The merge...
			MergeInfo mergeInfo = node.merge(indexEntry, path);
			// Post the changes to the parent Node...
			if(mergeInfo.isMerge()) {
				if(mergeInfo.isRightSide()) {
					((IndexEntry)parentNode.getEntry(mergeInfo.index())).separator=mergeInfo.newSeparator();
					mergeInfo.indexEntry().update(mergeInfo.node(), true);
					((IndexEntry)parentNode.remove(mergeInfo.index()+1)).remove();
				}
				else {
					((IndexEntry)parentNode.getEntry(mergeInfo.index()-1)).separator=mergeInfo.siblingNewSeparator();
					mergeInfo.siblingIndexEntry().update(mergeInfo.siblingNode(), true);
					((IndexEntry)parentNode.remove(mergeInfo.index())).remove();
				}
			}
			else {
				((IndexEntry)parentNode.getEntry(mergeInfo.index())).separator=mergeInfo.newSeparator();
				int sibIndex=mergeInfo.isRightSide()?mergeInfo.index()+1:mergeInfo.index()-1;
				((IndexEntry)parentNode.getEntry(sibIndex)).separator=mergeInfo.siblingNewSeparator();
				if(up){
					mergeInfo.indexEntry().update(mergeInfo.node(), true);
					mergeInfo.siblingIndexEntry().update(mergeInfo.siblingNode(), true);
				}
			}
			return true;
		}
		
		/** Used to redistribute the elements of the underflowing <tt>Node</tt> an a suitable sibling or to merge 
		 * them so that the underflow is repaired.
		 *  
		 * @param indexEntry the <tt>IndexEntry</tt> refering to the underflowing <tt>Node</tt>
		 * @param path the path from the root to parent of the underflowing <tt>Node</tt>
		 * @return a <tt>MergeInfo</tt> containing some information about the merge (or redistribution)
		 */
		protected MergeInfo merge(IndexEntry indexEntry, Stack path) {
			reorg=true;
			// Get the neighbor node with the most entries...
			Node parentNode= (Node) node(path);
			int index= parentNode.search(indexEntry);
			index=Math.max(0, index);
			IndexEntry rightSibling=null;
			Node rightNode=null;
			IndexEntry leftSibling=null;
			Node leftNode=null;
			if(index > 0) {
				leftSibling=(IndexEntry)parentNode.getEntry(index-1);
				leftNode= (Node)leftSibling.get(false); 
			}
			else {
				rightSibling=(IndexEntry)parentNode.getEntry(index+1);
				rightNode=(Node)rightSibling.get(false); 
			}
			MergeInfo mergeInfo=new MergeInfo(indexEntry, this, index, path);
			if(leftNode!=null) {
				// merge left
				if(level()!=0) separator(getFirst()).updateSepValue(indexEntry.separator.sepValue());
				if(leftNode.number() > D) {
					List newEntries= leftNode.entries.subList(D+(leftNode.number()-D)/2,leftNode.number());
					this.entries.addAll(0, newEntries);
					newEntries.clear();
					mergeInfo.initialize(leftSibling, leftNode, false);
				}
				else {
					leftNode.entries.addAll(leftNode.number(), entries);
					leftNode.nextNeighbor=this.nextNeighbor();
					mergeInfo.initialize(leftSibling, leftNode, true);
				}				
			}
			else {
				//merge right
				if(level()!=0) separator(rightNode.getFirst()).updateSepValue(rightSibling.separator.sepValue());
				if(rightNode.number() > D) {
					List newEntries= rightNode.entries.subList(0,(rightNode.number()-D+1)/2); 
					this.entries.addAll(this.number(), newEntries);
					newEntries.clear();
					mergeInfo.initialize(rightSibling, rightNode, false);
				}
				else {
					this.entries.addAll(this.number(), rightNode.entries);
					this.nextNeighbor=rightNode.nextNeighbor();
					mergeInfo.initialize(rightSibling, rightNode, true);
				}				
			}
			return mergeInfo;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "{"+number()+": "+entries+"}";
		}
		
		/** A <tt>SplitInfo</tt> contains information about a split. The enclosing
		  *	object of this SplitInfo-Object (i.e. <tt>Node.this</tt>) is the new node
		  * which was created by the split.
		  */		
		public class SplitInfo extends Tree.Node.SplitInfo {
			
			/** The <tt>Separator</tt> of the <tt>IndexEntry</tt> of the new <tt>Node</tt>.
			 */
			protected Separator separatorOfNewNode;

			/** Creates a new <tt>SplitInfo</tt>.
			 * 
			 * @param path the current path 
			 */
			public SplitInfo(Stack path) {
				super(path);
			}
			
			/** Initializes the <tt>SplitInfo</tt> with the <tt>Separator</tt> of the <tt>IndexEntry</tt> of the new 
			 * <tt>Node</tt>.
			 * 
			 * @param newSeparator the <tt>Separator</tt> of the <tt>IndexEntry</tt> of the new <tt>Node</tt>
			 * @return the initialized <tt>SplitInfo</tt> itself
			 */
			public SplitInfo initialize (Separator newSeparator) {
				this.separatorOfNewNode=newSeparator;
				return this;
			}
			
			/** Gives <tt>Separator</tt> of the <tt>IndexEntry</tt> of the new <tt>Node</tt>.
			 * 
			 * @return <tt>Separator</tt> of the <tt>IndexEntry</tt> of the new <tt>Node</tt>
			 */			
			public Separator separatorOfNewNode() {
				return separatorOfNewNode;
			}
		}
		
		/** A <tt>MergeInfo</tt> contains information about a merge (or redistribution). 
		  */		
		public class MergeInfo {	
			
			/** The path including the underflowing node.
			 */
			protected Stack path;
			
			/** The position of the <tt>IndexEntry</tt> of the underflowing <tt>Node</tt> in the parent 
			 * <tt>Node</tt>.
			 */
			protected int index;
	
			/** The <tt>IndexEntry</tt> of the underflowing <tt>Node</tt>.
			 */	
			protected IndexEntry indexEntry;
			
			/**The underflowing <tt>Node</tt>.
			 */
			protected Node node;
			
			/** The <tt>IndexEntry</tt> of the sibling <tt>Node</tt>.
			 */	
			protected IndexEntry siblingIndexEntry;
			
			/**The sibling <tt>Node</tt>.
			 */
			protected Node siblingNode;
			
			/** Indicates whether this MergeInfo describes a real merge or an element redistribution.
			 */ 
			protected boolean isMerge = false;
			
			/** Creates a new <tt>MergeInfo</tt>.
			 * 
			 * @param indexEntry the <tt>IndexEntry</tt> of the underflowing <tt>Node</tt>
			 * @param node the underflowing <tt>Node</tt>
			 * @param index the position of the <tt>IndexEntry</tt> of the underflowing <tt>Node</tt> in the parent 
			 * <tt>Node</tt>
			 * @param path the path including the underflowing node
			 */
			public MergeInfo(IndexEntry indexEntry, Node node, int index, Stack path) {
				this.indexEntry=indexEntry;
				this.node=node;
				this.index=index;
				this.path=path;
			}
			
			/** Initializes the <tt>MergeInfo</tt>.
			 * 
			 * @param siblingIndexEntry the <tt>IndexEntry</tt> of the sibling <tt>Node</tt>
			 * @param sibilingNode the sibling <tt>Node</tt>
			 * @param isMerge indicates whether a real merge or a element redistribution is described
			 * @return the initialized <tt>MergeInfo</tt> itself
			 */
			protected MergeInfo initialize(IndexEntry siblingIndexEntry, Node sibilingNode, boolean isMerge) {
				this.siblingIndexEntry=siblingIndexEntry;
				this.siblingNode=sibilingNode;
				this.isMerge=isMerge;
				return this;
			}
						
			/** Computes the new <tt>Separator</tt> of the <tt>IndexEntry</tt> after the merge. The result is 
			 * as follows: 
			 * <ul>
			 * <li> Merge with the left sibling:
			 * 		<ul>
			 * 		<li> Real merge --> <tt>IllegalStateException</tt></li>
			 * 		<li> Non real merge --> a copy of the <tt>Separator</tt> of the <tt>Node's</tt> first entry</li>
			 * 		</ul>
			 * </li>
			 * <li> Merge with the right sibling --> the <tt>Separator</tt> is not changed.
			 * </li>
			 * </ul>
			 * 
			 * @return the new <tt>Separator</tt> of the <tt>IndexEntry</tt>
			 */
			public Separator newSeparator() {
				if(isRightSide()) return indexEntry.separator;
				else {
					if(isMerge()) throw new IllegalStateException();
					return (Separator)separator(node.getFirst()).clone();
				}
			}

			/** Computes the new <tt>Separator</tt> of the sibling <tt>IndexEntry</tt> after the merge. The result is 
			 * as follows: 
			 * <ul>
			 * <li> It is the left sibling --> the <tt>Separator</tt> is not changed.
			 * </li>
			 * <li> It is the right sibling
			 * 		<ul>
			 * 		<li> Real merge --> <tt>IllegalStateException</tt></li>
			 * 		<li> Non real merge --> a copy of the <tt>Separator</tt> of the first entry of the sibling 
			 * 			<tt>Node</tt></li>
			 * 		</ul>
			 * </li>
			 * </ul>
			 * 
			 * @return the new <tt>Separator</tt> of the sibling <tt>IndexEntry</tt>
			 */			
			public Separator siblingNewSeparator() {
				if(isRightSide()) {
					if(isMerge()) throw new IllegalStateException();
					return (Separator)separator(siblingNode.getFirst()).clone();
				} 
				else return siblingIndexEntry.separator;
			}

			/** Indicates whether this MergeInfo describes a real merge or an element redistribution.
			 * 
			 * @return <tt>true</tt> if it is a real merge or <tt>false</tt> otherwise
			 */
			public boolean isMerge() {
				return isMerge;
			}
			
			/** Indicates whether the sibling <tt>Node</tt> lies on the right side of the underflowing <tt>Node</tt>.
			 * 
			 * @return <tt>true</tt> if the sibling <tt>Node</tt> lies on the right side of the underflowing <tt>Node</tt> 
			 * and <tt>false</tt> otherwise
			 */
			public boolean isRightSide() {
				return index==0;
			}
			
			/** Gives the position of the <tt>IndexEntry</tt> of the underflowing <tt>Node</tt> in the parent 
			 * <tt>Node</tt>.
			 * 
			 * @return the position of the <tt>IndexEntry</tt> of the underflowing <tt>Node</tt> in the parent 
			 * <tt>Node</tt>
			 */
			public int index() {
				return index;
			}

			/** Gives the path including the underflowing node.
			 * 
			 * @return the path including the underflowing node
			 */			
			public Stack path() {
				return path;
			}
			
			/** Gives the <tt>IndexEntry</tt> of the sibling <tt>Node</tt>.
			 * 
			 * @return the <tt>IndexEntry</tt> of the sibling <tt>Node</tt>
			 */
			public IndexEntry siblingIndexEntry() {
				return siblingIndexEntry;
			}
			
			/** Gives the parent <tt>Node</tt> of the underflowing <tt>Node</tt>.
			 * 
			 * @return the parent <tt>Node</tt> of the underflowing <tt>Node</tt>
			 */			
			public Node parentNode() {
				return (Node) BPlusTree.this.node(path);
			}

			/** Gives the underflowing <tt>Node</tt>.
			 * 
			 * @return the underflowing <tt>Node</tt>
			 */			
			public  Node node() {
				return node;
			}

			/** Gives the sibling <tt>Node</tt>.
			 * 
			 * @return the sibling <tt>Node</tt>
			 */			
			public  Node siblingNode(){
				return siblingNode;
			}
			
			/** Gives the <tt>IndexEntry</tt> of the underflowing <tt>Node</tt>.
			 * 
			 * @return the <tt>IndexEntry</tt> of the underflowing <tt>Node</tt>
			 */			
			public  IndexEntry indexEntry() {
				return indexEntry;
			}
		}
	}

	/** This class represents key ranges (i.e. intervals of keys). It is used to specify (range) queries on the 
	 * <tt>BPlusTree</tt> and to hold the key range of the data objects stored in the tree in the member field 
	 * <tt>rootDescriptor</tt>. 
	 */
	public static abstract class KeyRange extends Separator {
		
		/** The maximal bound of the interval.
		 */
		protected Comparable maxBound;
		
		/** Creates a new <tt>QueryRange</tt>.
		 * 
		 * @param min the minimal bound of the <tt>KeyRange</tt>
		 * @param max the maximal bound of the <tt>KeyRange</tt>
		 */
		public KeyRange(Comparable min, Comparable max) {
			super(min);
			this.maxBound=max;
		}

		/** Gives the minimal bound of the <tt>KeyRange</tt>.
		 * 
		 * @return the minimal bound of the <tt>KeyRange</tt>
		 */ 
		public Comparable minBound() {
			return sepValue;
		}

		/** Gest the maximal bound of the <tt>KeyRange</tt>.
		 * 
		 * @return the maximal bound of the <tt>KeyRange</tt>
		 */ 		
		public Comparable maxBound() {
			return maxBound;
		}
		
		/** Updates the minimal bound of the <tt>KeyRange</tt>.
		 * 
		 * @param newMinBound the new minimal bound
		 */
		public void updateMinBound(Comparable newMinBound) {
			updateSepValue(newMinBound);
		}

		/** Updates the maximal bound of the <tt>KeyRange</tt>.
		 * 
		 * @param newMaxBound the new maximal bound
		 */
		public void updateMaxBound(Comparable newMaxBound) {
			this.maxBound=newMaxBound;
		}
		
		/** Checks whether the given <tt>Descriptor</tt> overlaps the current <tt>KeyRange</tt>.
		 *   
		 * @param descriptor the <tt>KeyRange</tt> to check
		 * @return <tt>true</tt> if the given <tt>Descriptor</tt> an instance of of <tt>KeyRange</tt> and overlaps this 
		 * <tt>KeyRange</tt> and <tt>false</tt> otherwise.
		 *  
		 * @see xxl.core.indexStructures.Descriptor#overlaps(xxl.core.indexStructures.Descriptor)
		 */
		public boolean overlaps(Descriptor descriptor) {
			if(!(descriptor instanceof KeyRange)) return false;
			KeyRange qInterval=(KeyRange) descriptor;
			return contains(qInterval.minBound())|| qInterval.contains(this.minBound());
		}	
		
		/** Checks whether the given <tt>Descriptor</tt> is totally contained in this <tt>KeyRange</tt>.
		 *   
		 * @param descriptor the <tt>KeyRange</tt> to check
		 * @return <tt>true</tt> if the given <tt>Descriptor</tt> an instance of <tt>KeyRange</tt> and lies totally in this 
		 * <tt>KeyRange</tt> and <tt>false</tt> otherwise
		 *  
		 * @see xxl.core.indexStructures.Descriptor#contains(xxl.core.indexStructures.Descriptor)
		 */
		public boolean contains(Descriptor descriptor) {
			if(!(descriptor instanceof KeyRange)) return false;
			KeyRange qInterval=(KeyRange) descriptor;
			return contains(qInterval.minBound())&& contains(qInterval.maxBound());		
		}

		/** Checks whether the current <tt>KeyRangel</tt> contains the given key.
		 * 
		 * @param key the key to check
		 * @return <tt>true</tt> if key lies in this <tt>KeyRangel</tt> and <tt>false</tt> otherwise
		 */		
		protected boolean contains(Comparable key) {
			return minBound().compareTo(key)<=0 && maxBound().compareTo(key)>=0;
		}
		
		/** Tests whether this <tt>KeyRange</tt> equals the given <tt>KeyRange</tt>.
		 * 
		 * @param object the second <tt>KeyRange</tt> 
		 * @return <tt>true</tt> if the given object an instance of <tt>KeyRange</tt> and equals the current <tt>KeyRange</tt> 
		 * and <tt>false</tt> otherwise
		 */
		public boolean equals(Object object) {
			if(!(object instanceof KeyRange)) return false;
			KeyRange qInterval=(KeyRange) object;
			return minBound().compareTo(qInterval.minBound())==0 && maxBound().compareTo(qInterval.maxBound())==0;
		}
		
		/** Builds the union of two <tt>KeyRanges</tt>. The current <tt>KeyRange</tt> will be changed and returned.
		 * 
		 * @param descriptor the <tt>KeyRange</tt> which is to unite with the current <tt>KeyRange</tt>
		 *  
		 * @see xxl.core.indexStructures.Descriptor#union(xxl.core.indexStructures.Descriptor)
		 */
		public void union(Descriptor descriptor) {
			if(!(descriptor instanceof Separator)) return;
			if(descriptor instanceof KeyRange) {
				KeyRange qInterval=(KeyRange) descriptor;
				if(minBound().compareTo(qInterval.minBound())>0) updateMinBound(qInterval.minBound());
				if(maxBound().compareTo(qInterval.maxBound())<0) updateMaxBound(qInterval.maxBound());
			}
			else union(((Separator)descriptor).sepValue());
		}

		/** Builds the union of this <tt>KeyRanges</tt> and a key. The union is the minimal extenstion of the current 
		 * <tt>KeyRange</tt> which contains the given key. The current <tt>KeyRange</tt> will be changed and returned.
		 * 
		 * @param key the key which is to unite with the current <tt>KeyRange</tt>
		 */		
		public void union(Comparable key) {
			if(minBound().compareTo(key)>0) updateMinBound(key);
			if(maxBound().compareTo(key)<0) updateMaxBound(key);
		}
		
		/** Checks whether this <tt>KeyRangel</tt> is a point (i.e. minimal and maximal bounds are equal).
		 * 
		 * @return <tt>true</tt> if this <tt>KeyRangel</tt> is a point and <tt>false</tt> otherwise
		 */
		public boolean isPoint() {
			return minBound().compareTo(maxBound())==0;
		}
		
		/** Overwrites the method {@link xxl.core.indexStructures.Separator#isDefinite()} so that is always returns <tt>true</tt>.
		 * 
		 * @return <tt>true</tt>
		 */
		public boolean isDefinite() {
			return true;
		}
				
		/** Unsupported operation. 
		 * 
		 * @param separator unused
		 * @return never returns
		 * @throws UnsupportedOperationException
		 */
		public int compareTo(Object separator) {
			throw new UnsupportedOperationException();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer sb=new StringBuffer("["+minBound());
			if(!isPoint()) {
				sb.append(", "); 
				sb.append(maxBound());
			}
			sb.append("]");	
			return sb.toString();			
		}
	}
	
	/** This class uses an efficient algorithm to perform queries on the <tt>BPlusTree</tt>. It does not traverse the 
	 * <tt>BPlusTree</tt> in TOP-DOWN fashion to find the response set but it uses the known efficient querying 
	 * algorithm of B+-Tree. It only traverses the path to the first <tt>Node</tt> which can contain answers and 
	 * then traverses the <tt>Nodes</tt> on the same level cross the <tt>BPlusTree</tt> from left to right using the 
	 * <tt>nextNeighbor</tt> references of <tt>BPlusTree.Node</tt>. 
	 */
	protected class QueryCursor extends xxl.core.indexStructures.QueryCursor {
		
		/** The position of the current entry in the current <tt>Node</tt>.
		 */
		private int index;
		
		/** The position of the last entry in the last <tt>Node</tt>.
		 */
		private int lastIndex;
		
		/** The <tt>IndexEntry</tt> of the last <tt>Node</tt>.
		 */
		private IndexEntry lastIndexEntry;
		
		/** The last <tt>Node</tt>, i.e. the <tt>Node</tt> which contains the last entry returnd by the 
		 * <tt>QueryCursor</tt>. It is used as a history information to support remove an update.
		 */ 
		private Node lastNode;
		
		/** A counter which counts how many <tt>Nodes</tt> on the target level was already visited. The number of 
		 * the visited <tt>Nodes</tt> equals <tt>nodeChangeover+1</tt>.
		 */
		private int nodeChangeover;
		
		/** Creates a new <tt>QueryCursor</tt>.
		 * 
		 * @param subRootEntry the <tt>IndexEntry</tt> which is the root of the subtree in which the query has to be 
		 * exectuted
		 * @param qInterval a <tt>Descriptor</tt> which specifies the query
		 * @param targetLevel the target level of the query
		 */
		public QueryCursor(IndexEntry subRootEntry, KeyRange qInterval, int targetLevel) {
			super(BPlusTree.this, subRootEntry, qInterval, targetLevel);			
			path=new Stack();
			index=-1;	
			lastIndex=index;
			lastNode=null;
			lastIndexEntry=null;
			nodeChangeover= 0;
		}
		
		/** Checkes whether a next element exists.
		 * 
		 * @return <tt>true</tt> if there is a next element and <tt>false</tt> otherwise
		 */	
		public boolean hasNextObject() {
			if(index==-1) {
				down(path, indexEntry);
				while(level(path)>targetLevel) {
					Iterator entries= node(path).query(queryRegion);
					if(entries.hasNext()) down(path, (IndexEntry)entries.next());
					else return false; 
				}
				indexEntry=(IndexEntry)indexEntry(path);
				currentNode=(Node)node(path);
				lastIndexEntry=(IndexEntry)indexEntry;
				lastNode=(Node)currentNode;
				//Search the first index in currentNode.
				int offset=currentNode.level==0? 0: 1;
				for(int i=offset; i<currentNode.number(); i++) {
					lastIndex=index;
					index=i;
					if(separator(((Node)currentNode).getEntry(i)).isRightOf(((KeyRange)queryRegion).minBound())){
						index=index-offset;
						break;
					}
					if(index==currentNode.number()-1&& ((Node)currentNode).nextNeighbor!=null) {
						nodeChangeOver(); 
						i=offset;
						abolishPath();
						lastIndexEntry.unfix();
						lastIndexEntry=(IndexEntry)indexEntry;
						lastNode=(Node)currentNode;
					}
				}
			}
			if(index==-1) return false; 
			if(index== currentNode.number()) {
				if(((Node)currentNode).nextNeighbor==null) return false;
				nodeChangeOver();
			}
			return separator(((Node)currentNode).getEntry(index)).sepValue().compareTo(((KeyRange)queryRegion).maxBound())<=0;
		}
		
		/** Computes the next element of the <tt>QueryCursor</tt>.
		 * 
		 * @return the next element
		 */	
		public Object nextObject() {
			if(!indexEntry.equals(lastIndexEntry)) {
				abolishPath();
				lastIndexEntry.unfix();
			}
			lastIndex=index;
			lastIndexEntry=(IndexEntry)indexEntry;
			lastNode=(Node)currentNode;
			return ((Node)currentNode).getEntry(index++);
		}
		
		/** Removes the last element returned by the method next() and calls the method 
		 * {@link BPlusTree#treatUnderflow(Stack)} to repair underflows which can accour. It is only supported on the 
		 * level 0.
		 */
		protected void removeObject() {
			if (!hasPath()) 
				path=pathToLeaf(lastNode);
			lastNode.remove(lastIndex);
			treatUnderflow(path);
		}
		
		/** Replaces the last element returnd by the method next() by a new data object. It is only supported on the 
		 * level 0.
		 * 
		 * @param newData the new data object
		 */
		protected void updateObject(Object newData) {
			if(targetLevel != 0) 
				throw new UnsupportedOperationException("update(Object) is to use only on leaf level.");
			Object oldData=lastNode.remove(lastIndex);
			if(!key(oldData).equals(key(newData))) throw new IllegalArgumentException();
			lastNode.grow(newData);
		}
		
		/** It unfixes all <tt>Nodes</tt> loaded in the underlaying buffer then calls super.close().
		 */		
		public void close() {
			abolishPath();
			lastIndexEntry.unfix();
			indexEntry.unfix();
			super.close();
		}
		
		/** Moves the <tt>QueryCursor</tt> to next right neighbor.
		 */
		protected void nodeChangeOver() {
			indexEntry=((Node)currentNode).nextNeighbor;
			currentNode=(Node)indexEntry.get(false);
			index=0;
			nodeChangeover++;	
		}
	}
	
	/** A <tt>NodeConverter</tt> is used by the <tt>BPlusTree</tt> to convert the <tt>Nodes</tt> for I/O-purposes.
	 */
	public class NodeConverter extends Converter {

		/** Reads a <tt>Node</tt> from the given <tt>DataInput</tt>.
		 * 
		 * @param dataInput the <tt>DataInput</tt> from which the <tt>Node</tt> has to be read
		 * @param object is not used
		 * @return the read <tt>Node</tt>
		 * @throws IOException
		 */
		public Object read (DataInput dataInput, Object object) throws IOException {
			int level=dataInput.readInt();
			Node node = (Node)createNode(level);
			int number=dataInput.readInt();
			boolean readNext=dataInput.readBoolean();
			if(readNext) {
				node.nextNeighbor=(IndexEntry)createIndexEntry(level+1);
				node.nextNeighbor.initialize(readID(dataInput));
			} 
			else node.nextNeighbor=null;	
			readEntries(dataInput, node, number);
			if(node.level!=0) {
				((IndexEntry)node.getFirst()).initialize(createSeparator(null)); 
				for(int i=1; i<node.number(); i++) {
					Comparable sepValue=(Comparable)keyConverter.read(dataInput, null);
					((IndexEntry)node.getEntry(i)).initialize(createSeparator(sepValue)); 
				}
			}
			return node;
		}
		
		/** Writes a given <tt>Node</tt> into a given <tt>DataOutput</tt>.
		 *  
		 * @param dataOutput the <tt>DataOutput</tt> which the <tt>Node</tt> has to be written to
		 * @param object the <tt>Node</tt> which has to be written
		 * @throws IOException
		 */
		public void write (DataOutput dataOutput, Object object) throws IOException {
			Node node = (Node)object;
			//2x Integer
			dataOutput.writeInt(node.level);
			dataOutput.writeInt(node.number());
			//Boolean
			dataOutput.writeBoolean(node.nextNeighbor!=null);
			//ID
			if(node.nextNeighbor!=null) writeID(dataOutput,node.nextNeighbor.id()); 
			//Entries
			writeEntries(dataOutput, node);
			//Separators
			if(node.level!=0) 
				for(int i=1; i<node.number(); i++) 
					keyConverter.write(dataOutput, separator(node.getEntry(i)).sepValue());
		}
		
		/** Read the entries of the given <tt>Node</tt> from the <tt>DataInput</tt>. If the <tt>Node</tt> is a leaf 
		 * the <tt>dataConverter</tt> is used to read its data objects. Otherwise the method 
		 * {@link #readIndexEntry(DataInput, int)} is used to read its <tt>IndexEntries</tt>.
		 * 
		 * @param input the <tt>DataInput</tt>
		 * @param node the <tt>Node</tt>
		 * @param number the number of the entries which have to be read
		 * @throws IOException
		 */
		protected void readEntries(DataInput input, Node node, int number) throws IOException {
			for(int i=0; i<number; i++) {
				Object entry;
				if (node.level==0) 
					entry = dataConverter.read(input, null);
				else 
					entry = readIndexEntry(input, node.level);
				node.entries.add(i, entry);
			}
		}

		/** Writes the entries of the given <tt>Node</tt> into the <tt>DataOutput</tt>. If the <tt>Node</tt> is a 
		 * leaf the <tt>dataConverter</tt> is used to write its data objects. Otherwise the method 
		 * {@link #writeIndexEntry(DataOutput, BPlusTree.IndexEntry)} is used to write its <tt>IndexEntries</tt>.
		 * 
		 * @param output the <tt>DataOutput</tt>
		 * @param node the <tt>Node</tt>
		 * @throws IOException
		 */		
		protected void writeEntries(DataOutput output, Node node) throws IOException {
			Iterator entries = node.entries();
			while(entries.hasNext()) {
				Object entry = entries.next();
				if (node.level==0) 
					dataConverter.write(output, entry);
				else 
					writeIndexEntry(output, (IndexEntry)entry);
			}
		}
		
		/** Computes the maximal size (in bytes) of a record (IndexEntry or data object). It calls the method 
		 * getIdSize() of the tree container. If the tree container has not been initialized 
		 * a NullPointerException is thrown.
		 * 
		 * @return the maximal size of a record (IndexEntry or data object)
		 */
		public int getMaxRecordSize() {
			return Math.max(dataConverter.getMaxObjectSize(),indexEntrySize());					
		}
			
		/** Computes the maximal size (in bytes) of an <tt>IndexEntry</tt>. It calls the method 
		 * getIdSize() of the tree container. If the tree container has not been initialized 
		 * a NullPointerException is thrown.
		 * 
		 * @return the maximal size of an <tt>IndexEntry</tt>
		 */
		protected int indexEntrySize() {
			return BPlusTree.this.container().getIdSize()+ keyConverter.getMaxObjectSize();
		}

		/** Computes the size (in bytes) of the <tt>Node's</tt> header which contains some information such 
		 * as level and entries number. 
		 * 
		 * @return the maximal size of an <tt>IndexEntry</tt>
		 */		
		protected int headerSize() {
			return 2*IntegerConverter.SIZE + BooleanConverter.SIZE + BPlusTree.this.container().getIdSize();
		}

		/** Reads an <tt>IndexEntry</tt> from the given <tt>DataInput</tt>. In the default implementation only the  
		 * ID of the <tt>IndexEntry</tt> is read.
		 * 
		 * @param input the <tt>DataInput</tt>
		 * @param parentLevel the parent level of the <tt>IndexEntry</tt>
		 * @return the read <tt>IndexEntry</tt>
		 * @throws IOException
		 */
		protected IndexEntry readIndexEntry(DataInput input, int parentLevel) throws IOException {
			IndexEntry indexEntry= new IndexEntry(parentLevel);
			Object id=readID(input);
			indexEntry.initialize(id);
			return indexEntry;
		}
		
		/** Writes an <tt>IndexEntry</tt> into the given <tt>DataOutput</tt>. In the default implementation only the  
		 * ID of the <tt>IndexEntry</tt> is written.
		 * 
		 * @param output the <tt>DataOutput</tt>
		 * @param entry the <tt>IndexEntry</tt> which has to be written
		 * @throws IOException
		 */	
		protected void writeIndexEntry(DataOutput output, IndexEntry entry) throws IOException {
			writeID(output, entry.id);
		}

		/** Reads an ID from the given <tt>DataInput</tt>.
		 * 
		 * @param input the <tt>DataInput</tt>
		 * @return the read ID
		 * @throws IOException
		 */
		private Object readID(DataInput input) throws IOException{
			Converter idConverter= BPlusTree.this.container().objectIdConverter();
			return idConverter.read(input,null);
		}

		/** Writes an ID into the given <tt>DataOutput</tt>.
		 * 
		 * @param output the <tt>DataOutput</tt>
		 * @param id the ID which has to be written
		 * @throws IOException
		 */		
		private void writeID(DataOutput output, Object id) throws IOException {
			Converter idConverter= BPlusTree.this.container().objectIdConverter();
			idConverter.write(output,id);
		}
	}
}
