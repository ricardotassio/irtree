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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import xxl.core.comparators.FeatureComparator;
import xxl.core.cursors.Cursors;
import xxl.core.cursors.sources.Enumerator;
import xxl.core.functions.Function;
import xxl.core.spatial.rectangles.Rectangle;

/** An <tt>RTree</tt> implementing the split-strategy proposed by D.&nbsp;Greene.
 * 
 * For a detailed discussion see D. Greene: 
 * "An Implementation and Performance Analysis of Spatial Data Access Methods", 
 * Proc. 5th Int. Conf. on Data Engineering, 606-615, 1989.
 * 
 * @see Tree
 * @see ORTree
 * @see RTree
 */
public class GreenesRTree extends RTree {
	
	/* (non-Javadoc)
	 * @see xxl.core.indexStructures.Tree#createNode(int)
	 */
	public Tree.Node createNode (int level) {
		return new Node().initialize(level, new LinkedList());
	}

	/** A modification of {@link RTree.Node node} implementing Greenes split-algorithm.
	 *
	 * @see RTree.Node 
	 */
	public class Node extends RTree.Node {
		
		/* (non-Javadoc)
		 * @see xxl.core.indexStructures.Tree.Node#split(java.util.Stack)
		 */
		protected Tree.Node.SplitInfo split (final Stack path) {
			final Node node = (Node)node(path);
			final int splitNumber = (node.splitMinNumber()+node.splitMaxNumber())/2;
			final int dimensions = ((Rectangle)rootDescriptor()).dimensions();
			final int splitDimension = ((Integer)Cursors.maxima(new Enumerator(dimensions),
				new Function () {
					Rectangle MBR = rectangle(indexEntry(path));

					public Object invoke (Object object) {
						final int dimension = ((Integer)object).intValue();
						Object e1 = Cursors.minima(node.entries(),
							new Function () {
								public Object invoke (Object object) {
									return new Double(rectangle(object).getCorner(true).getValue(dimension));
								}
							}
						).getFirst();
						Object e2 = Cursors.maxima(node.entries(),
							new Function () {
								public Object invoke (Object object) {
									return new Double(rectangle(object).getCorner(false).getValue(dimension));
								}
							}
						).getLast();
						return new Double(
							(rectangle(e2).getCorner(false).getValue(dimension)-rectangle(e1).getCorner(true).getValue(dimension))/
							(MBR.getCorner(true).getValue(dimension)-MBR.getCorner(false).getValue(dimension))
						);
					}
				}
			).getFirst()).intValue();
			List entries = new ArrayList(node.entries);

			node.entries.clear();
			Collections.sort(entries, new FeatureComparator(
				new Function () {
					public Object invoke (Object entry) {
						return new Double(rectangle(entry).getCorner(false).getValue(splitDimension));
					}
				}
			));
			node.entries.addAll(entries.subList(0, splitNumber));
			this.entries.addAll(entries.subList(splitNumber, entries.size()));
			((IndexEntry)indexEntry(path)).descriptor = computeDescriptor(node.entries);
			return new SplitInfo(path).initialize(computeDescriptor(this.entries));
		}
	}
}