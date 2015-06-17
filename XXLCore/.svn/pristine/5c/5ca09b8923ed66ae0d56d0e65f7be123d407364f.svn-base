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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import xxl.core.collections.ReversedList;
import xxl.core.comparators.FeatureComparator;
import xxl.core.cursors.Cursors;
import xxl.core.cursors.filters.Filter;
import xxl.core.cursors.identities.TeeCursor;
import xxl.core.cursors.mappers.Mapper;
import xxl.core.cursors.sources.Enumerator;
import xxl.core.functions.Function;
import xxl.core.io.converters.ConvertableConverter;
import xxl.core.io.converters.Converter;
import xxl.core.predicates.Predicate;
import xxl.core.spatial.rectangles.DoublePointRectangle;
import xxl.core.spatial.rectangles.Rectangle;

/** An <tt>ORTree</tt> for objects with bounding rectangles as regions. 
 * This implementation of a member of the R-Tree family uses the 
 * split-strategy of the R*-Tree.  
 * 
 * For a detailed discussion see 
 * Norbert Beckmann, Hans-Peter Kriegel, Ralf Schneider, Bernhard Seeger:
 * "The R*-tree: An Efficient and Robust Access Method for Points and Rectangles",
 * ACM-SIGMOD (1990)322-331.
 * 
 * @see Tree
 * @see ORTree
 * @see LinearRTree
 * @see QuadraticRTree
 * @see GreenesRTree 
 */
public class RTree extends ORTree {

	/** Returns the bounding rectangle of <tt>entry</tt>.
	 * 
	 * @param entry an entry
	 * @return the bounding rectangle of <tt>entry</tt>
	 */
	public Rectangle rectangle (Object entry) {
		return (Rectangle)descriptor(entry);
	}

	/* (non-Javadoc)
	 * @see xxl.core.indexStructures.Tree#createNode(int)
	 */
	public Tree.Node createNode (int level) {
		return new Node().initialize(level, new LinkedList());
	}

	/** <tt>Node</tt> is the class used to represent leaf- and non-leaf nodes of <tt>RTree</tt>.
	 *	Nodes are stored in containers.
	 *	@see Tree.Node
	 *  @see ORTree.Node
	 */
	public class Node extends ORTree.Node {

		/** SplitInfo contains information about a split. The enclosing
		 * Object of this SplitInfo-Object (i.e. Node.this) is the new node
		 * that was created by the split.
		 */		
		public class SplitInfo extends ORTree.Node.SplitInfo {
						
			/** The distribution of rectangles for the split.
			 */
			protected Distribution distribution;

			/** Creates a new <tt>SplitInfo</tt> with a given path.
			 * @param path the path from the root to the splitted node
			 */
			public SplitInfo (Stack path) {
				super(path);
			}

			/** Initializes the SplitInfo by setting the distribution of 
			 * the split.
			 * 
			 * @param distribution the distribution for the split
			 * @return the initialized <tt>SplitInfo</tt>
			 */
			public ORTree.Node.SplitInfo initialize (Distribution distribution) {
				this.distribution = distribution;
				return initialize(distribution.descriptor(true));
			}

			/** Returns the distribution of the <tt>SplitInfo</tt>.
			 * 
			 * @return the distribution of the <tt>SplitInfo</tt>
			 */
			public Distribution distribution(){
				return distribution;
			}
		}

		/* (non-Javadoc)
		 * @see xxl.core.indexStructures.ORTree.Node#chooseSubtree(xxl.core.indexStructures.Descriptor, java.util.Iterator)
		 */
		protected ORTree.IndexEntry chooseSubtree (Descriptor descriptor, Iterator minima) {
			final Rectangle dataRectangle = (Rectangle)descriptor;
			TeeCursor validEntries = new TeeCursor(minima);

			minima = new Filter(validEntries,
				new Predicate () {
					public boolean invoke (Object object) {
						return rectangle(object).contains(dataRectangle);
					}
				}
			);
			if (!minima.hasNext()) {
				minima = Cursors.minima(validEntries.cursor(),
					new Function () {
						public Object invoke (Object object) {
							Rectangle oldRectangle = rectangle(object);
							Rectangle newRectangle = Descriptors.union(oldRectangle, dataRectangle);
							return new Double(newRectangle.area()-oldRectangle.area());
						}
					}
				).iterator();
			}
			minima = Cursors.minima(minima,
				new Function () {
					public Object invoke (Object object) {
						return new Double(rectangle(object).area());
					}
				}
			).iterator();
			validEntries.close();
			return (IndexEntry)minima.next();
		}

		/* (non-Javadoc)
		 * @see xxl.core.indexStructures.Tree.Node#split(java.util.Stack)
		 */
		protected Tree.Node.SplitInfo split (Stack path) {
			final Node node = (Node) node(path);
			Distribution distribution;
			final int minEntries = node.splitMinNumber();
			final int maxEntries = node.splitMaxNumber();
                        int dimensions = ((Rectangle)rootDescriptor()).dimensions();

			// For each dimension generate a list of all possible distributions
			Iterator distributionLists = new Mapper(new Enumerator(dimensions),
				new Function () {
					public Object invoke (Object object) {
						final int dim = ((Integer)object).intValue();
						// list of distributions for this dimension
						ArrayList distributionList = new ArrayList(2*(maxEntries-minEntries+1));
						Rectangle [][] rectangles = new Rectangle[2][];

						// Consider the entrys sorted by left or right borders
						for (int i=0; i<2; i++) {
							Object [] entryArray = node.entries.toArray();
							final boolean right = i==1;

							// Sort the entries by left or right border in the actual dimension
							Arrays.sort(entryArray, new FeatureComparator(
								new Function () {
									public Object invoke (Object entry) {
										return new Double(rectangle(entry).getCorner(right).getValue(dim));
									}
								}
							));

							// Calculation of descriptors for all distributions (linear!)
							for (int k = 0; k<2; k++) {
								List entryList = Arrays.asList(entryArray);
								Iterator entries = (k==0? entryList: new ReversedList(entryList)).iterator();
								//removed to become generic. Rectangle rectangle = new DoublePointRectangle(rectangle(entries.next()));
                                                                Rectangle rectangle = rectangle(entries.next());

								for (int l = (k==0? minEntries: node.number()-maxEntries); --l>0;)
									rectangle.union(rectangle(entries.next()));
								(rectangles[k] = new Rectangle[maxEntries-minEntries+1])[0] = rectangle;
								for (int j=1; j<=maxEntries-minEntries; rectangles[k][j++] = rectangle)
									rectangle = Descriptors.union(rectangle, rectangle(entries.next()));
							}
							// Creation of the distributions for this dimension
							for (int j = minEntries; j<=maxEntries; j++)
								distributionList.add(new Distribution(entryArray, j, rectangles[0][j-minEntries], rectangles[1][maxEntries-j], dim));
						}
						return distributionList;
					}
				}
			);
			
			// Return the distributionList of the dimension for which the margin-sum of all
			// of its distributions is minimal (i.e. choose the dimension)
			List distributionList = (List)Cursors.minima(distributionLists,
				new Function () {
					public Object invoke (Object object) {
						double marginValue = 0.0;

						for (Iterator distributions = ((List)object).iterator(); distributions.hasNext();)
							marginValue += ((Distribution)distributions.next()).marginValue();
						return new Double(marginValue);
					}
				}
			).getFirst();
			
			// Choose the distributions of the chosen dimension with minimal overlap 
			distributionList = Cursors.minima(distributionList.iterator(),
				new Function () {
					public Object invoke (Object object) {
						return new Double(((Distribution)object).overlapValue());
					}
				}
			);
			
			// If still more than one distribution has to be considered, choose one
			// with minimal area 
			distributionList = Cursors.minima(distributionList.iterator(),
				new Function () {
					public Object invoke (Object object) {
						return new Double(((Distribution)object).areaValue());
					}
				}
			);
			
			distribution = (Distribution)distributionList.get(0);
			// Fill the pages with the entries according to the distribution
			node.entries.clear();
			node.entries.addAll(distribution.entries(false));
			entries.addAll(distribution.entries(true));
			// update the descriptor of the old index entry
			((IndexEntry)indexEntry(path)).descriptor = distribution.descriptor(false);
			return new SplitInfo(path).initialize(distribution);
		}

		/** <tt>Distribution</tt> is the class used to represent the distribution of
		 * entries of a node of the <tt>RTree</tt> into two partitions used for a split.
		 */
		protected class Distribution {
			
			/** Entries stored in this distribution.
			 */
			protected Object [] entries;
			
			/** Start index of the second part of the distribution.
			 */
			protected int secondStart;
			
			/** Bounding Rectangle of the first part of the distribution.
			 */
			protected Rectangle firstDescriptor;
			
			/** Bounding Rectangle of the first part of the distribution.
			 */
			protected Rectangle secondDescriptor;
			
			/** Number of dimensions.
			 */
			protected int dim;

			/**
			 * @param entries an array containing all entries to be distributed
			 * @param secondStart the start index of the second partition
			 * @param firstDescriptor the descriptor for the first partition 
			 * @param secondDescriptor the descriptor for the second partition
			 * @param dim the number of dimensions
			 */
			protected Distribution (Object [] entries, int secondStart, Rectangle firstDescriptor, Rectangle secondDescriptor, int dim) {
				this.entries = entries;
				this.secondStart = secondStart;
				this.firstDescriptor = firstDescriptor;
				this.secondDescriptor = secondDescriptor;
				this.dim = dim;
			}

			/** Returns one of the partitions of this distribution.
			 * 
			 * @param second a <tt>boolean</tt> value determining if the second partition
			 * should be returned
			 * @return the entries of the first (if <tt>second == false</tt>) or of the
			 * second partition (if <tt>second == true</tt>)
			 */
			protected List entries (boolean second) {
				return Arrays.asList(entries).subList(second? secondStart: 0, second? entries.length: secondStart);
			}

			/** Returns a descriptor of one of the partitions of this distribution.
			 * 
			 * @param second a <tt>boolean</tt> value determining if the descriptor of 
			 * second partition should be returned
			 * @return the descriptor of the first (if <tt>second == false</tt>) or of the 
			 * second partition (if <tt>second == true</tt>)
			 */
			protected Descriptor descriptor (boolean second) {
				return second? secondDescriptor: firstDescriptor;
			}

			/** Returns the number of dimenssions.
			 * 
			 * @return the number of dimenssions
			 */
			protected int getDim(){
				return dim;
			}

			/** Returns the sum of the margins of the two partitions. 
			 * 
			 * @return the sum of the margins of the two partitions
			 */
			protected double marginValue () {
				return firstDescriptor.margin()+secondDescriptor.margin();
			}

			/** Returns the overlap of the two partitions.
			 * 
			 * @return the overlap of the two partitions
			 */
			protected double overlapValue () {
				return firstDescriptor.overlap(secondDescriptor);
			}

			/** Returns the sum of the areas of the two partitions. 
			 * 
			 * @return the sum of the areas of the two partitions
			 */
			protected double areaValue () {
				return firstDescriptor.area()+secondDescriptor.area();
			}
		}
	}

	/** Gets a suitable Converter to serialize the tree's nodes.
	 * 
	 * @param objectConverter a converter to convert the data objects stored in the tree
	 * @param dimensions the dimensions of the bounding rectangles 
	 * @return a NodeConverter
	 */
	public Converter nodeConverter (Converter objectConverter, final int dimensions) {
		return nodeConverter(objectConverter, indexEntryConverter(
			new ConvertableConverter(
				new Function () {
					public Object invoke () {
						return new DoublePointRectangle(dimensions);
					}
				}
			)
		));
	}
	
	/*********************************************************************/
	/*                       DEBUG FUNCTIONALITY                         */
	/*********************************************************************/
	
	/* (non-Javadoc)
	 * @see xxl.core.indexStructures.ORTree#checkDescriptors(xxl.core.indexStructures.ORTree.IndexEntry)
	 */
	public boolean checkDescriptors (IndexEntry indexEntry) {
		boolean returnValue = true;
		Node node = (Node)indexEntry.get(true);
		Descriptor descriptor = computeDescriptor(node.entries);

		if (!descriptor.equals(indexEntry.descriptor))
			System.out.println("Level "+node.level+": expected: "+descriptor+" actually:"+indexEntry.descriptor);
		if (node.level>0)
			for (Iterator entries = node.entries(); entries.hasNext();)
				if (!checkDescriptors((IndexEntry)entries.next()))
					returnValue = false;

		return returnValue;
	}
}
