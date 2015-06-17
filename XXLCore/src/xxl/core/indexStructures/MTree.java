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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import xxl.core.collections.Collections;
import xxl.core.collections.MapEntry;
import xxl.core.cursors.AbstractCursor;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.Cursors;
import xxl.core.cursors.filters.Filter;
import xxl.core.cursors.identities.TeeCursor;
import xxl.core.cursors.mappers.Mapper;
import xxl.core.cursors.sources.EmptyCursor;
import xxl.core.cursors.sources.SingleObjectCursor;
import xxl.core.cursors.unions.Sequentializer;
import xxl.core.cursors.wrappers.IteratorCursor;
import xxl.core.functions.Function;
import xxl.core.io.Convertable;
import xxl.core.io.converters.Converter;
import xxl.core.io.converters.DoubleConverter;
import xxl.core.io.converters.IntegerConverter;
import xxl.core.io.converters.ShortConverter;
import xxl.core.predicates.Predicate;
import xxl.core.spatial.LpMetric;
import xxl.core.util.Distance;

/** An <tt>ORTree</tt> for objects with spheres as regions. 
 * 
 * For a detailed discussion see 
 * P. Ciaccia, M. Patella, P. Zezula:
 * "M-tree: An Efficient Access Method for Similarity Search in Metric Spaces",
 * VLDB (1997) 426-435.
 * 
 * @see Tree
 * @see ORTree
 * @see SlimTree 
 */
public class MTree extends ORTree {

	/** Hyperplane split strategy
	 */
	public static final int HYPERPLANE_SPLIT = 0;
	
	/** Balanced split strategy
	 */
	public static final int BALANCED_SPLIT   = 1;

	/** The metric distance function for points. 
	 */
	public static Distance pointDistance  = LpMetric.EUCLIDEAN;
	
	/** The metric distance function for spheres.
	 */
	public static Distance sphereDistance =
		new Distance () {
			public double distance (Object o1, Object o2) {
				Sphere s1 = (Sphere)o1, s2 = (Sphere)o2;
				return Math.abs(s1.centerDistance(s2) - s1.radius - s2.radius);
			}
		};

	/** A flag indicating if a split is in progress.
	 */
	protected boolean split = false;
	
	/** The split strategy used in this <tt>MTree</tt>.
	 */
	protected int splitMode = HYPERPLANE_SPLIT;

	/** This class implements a {@link xxl.core.indexStructures.Descriptor} that is a sphere. 
	 */
	public static class Sphere implements Descriptor, Convertable {

		/** The center of the sphere.
		 */
		protected Object center;
		
		/** The radius if the sphere.
		 */
		protected double radius;
		
		/** A suitable converter for the center of the sphere.
		 */
		protected Converter centerConverter;
		
		/** The distance from the center of this sphere to the center of the
		 * sphere of the parent node in the MTree.
		 */
		protected double distanceToParent = -1;

		/** Creates a new sphere.
		 * 
		 * @param center the new {@link MTree.Sphere#center}
		 * @param radius the new {@link MTree.Sphere#radius}
		 * @param centerConverter the new {@link MTree.Sphere#centerConverter}
		 * @param distanceToParent the new {@link MTree.Sphere#distanceToParent}
		 */
		public Sphere (Object center, double radius, Converter centerConverter, double distanceToParent) {
			this.center = center;
			this.radius = radius;
			this.centerConverter = centerConverter;
			this.distanceToParent = distanceToParent;
		}

		/** Creates a new sphere by calling
		 * <pre><code>
	 			this(center, radius, centerConverter, -1);
	 		 </code></pre>
		 * 
		 * @param center the new {@link MTree.Sphere#center}
		 * @param radius the new {@link MTree.Sphere#radius}
		 * @param centerConverter the new {@link MTree.Sphere#centerConverter}
		 */
		public Sphere (Object center, double radius, Converter centerConverter) {
			this(center, radius, centerConverter, -1);
		}

		/* (non-Javadoc)
		 * @see xxl.core.io.Convertable#write(java.io.DataOutput)
		 */
		public void write (DataOutput dataOutput) throws IOException {
			centerConverter.write(dataOutput, center);
			DoubleConverter.DEFAULT_INSTANCE.writeDouble(dataOutput,radius);
			DoubleConverter.DEFAULT_INSTANCE.writeDouble(dataOutput, distanceToParent);
		}

		/* (non-Javadoc)
		 * @see xxl.core.io.Convertable#read(java.io.DataInput)
		 */
		public void read (DataInput dataInput) throws IOException {
			center = centerConverter.read(dataInput);
			radius = DoubleConverter.DEFAULT_INSTANCE.readDouble(dataInput);
			distanceToParent = DoubleConverter.DEFAULT_INSTANCE.readDouble(dataInput);
		}

		/** Computes the distance between the center of this and the center of the 
		 *  specified <tt>sphere</tt> using the {@link MTree#pointDistance}.
		 * 
		 * @param sphere the sphere to whichs center the distance should be determined
		 * @return the distance between the center of this and the center of <tt>sphere</tt>
		 */
		public double centerDistance (Sphere sphere) {
			return pointDistance.distance(center, sphere.center);
		}

		/** Computes the distance between this and the 
		 *  specified <tt>sphere</tt> using the {@link MTree#sphereDistance}.
		 * 
		 * @param sphere the sphere to which the distance should be determined
		 * @return the distance between this and <tt>sphere</tt>
		 */
		public double sphereDistance (Sphere sphere) {
			return overlapsPD(sphere) ? 0 : sphereDistance.distance(this, sphere);
		}

		/** Returns <tt>true</tt> if this sphere overlaps the specified sphere.
		 * 
		 * @param descriptor the sphere to check
		 * @return <tt>true</tt> if this sphere overlaps the specified sphere
		 */
		public boolean overlaps (Descriptor descriptor) {
			Sphere sphere = (Sphere)descriptor;
			return centerDistance(sphere) <= radius + sphere.radius;
		}

		/** Returns <tt>true</tt> if this sphere overlaps the specified sphere.
		 * This implementation uses an optimization: If the difference of
		 * distances to the center of the parent node of the spheres is greater
		 * than the sum of their radiuses, the spheres cannot overlap. This
		 * case can be detected using  
		 * {@link MTree.Sphere#distanceToParent}. 
		 * 
		 * @param descriptor the sphere to check
		 * @return <tt>true</tt> if this sphere overlaps the specified sphere
		 */
		public boolean overlapsPD (Descriptor descriptor) {
			Sphere sphere = (Sphere)descriptor;
			if (sphere.distanceToParent != -1 && distanceToParent != -1)
				if (Math.abs(sphere.distanceToParent - distanceToParent) > (sphere.radius + radius))
					return false;
			return overlaps(sphere);
		}

		/** Returns <tt>true</tt> if this sphere contains the specified sphere.
		 * 
		 * @param descriptor the sphere to check
		 * @return <tt>true</tt> if this sphere contains the specified sphere
		 */
		public boolean contains (Descriptor descriptor) {
			Sphere sphere = (Sphere)descriptor;
			return centerDistance(sphere) + sphere.radius <= radius;
		}

		/** Returns <tt>true</tt> if this sphere contains the specified sphere.
		 * This implementation uses an optimization: If the difference of
		 * distances to the center of the parent node of the spheres is greater
		 * than the sum of their radiuses, the spheres cannot overlap and i.e.
		 * not contain each other. This case can be detected using  
		 * {@link MTree.Sphere#distanceToParent}. 
		 * 
		 * @param descriptor the sphere to check
		 * @return <tt>true</tt> if this sphere contains the specified sphere
		 */
		public boolean containsPD (Descriptor descriptor) {
			Sphere sphere = (Sphere)descriptor;
			if (sphere.distanceToParent != -1 && distanceToParent != -1)
				if (Math.abs(sphere.distanceToParent - distanceToParent) > (sphere.radius + radius))
					return false;
			return contains(sphere);
		}

		/* (non-Javadoc)
		 * @see xxl.core.indexStructures.Descriptor#union(xxl.core.indexStructures.Descriptor)
		 */
		public void union (Descriptor descriptor) {
			Sphere sphere = (Sphere)descriptor;
			radius = Math.max(radius, centerDistance(sphere) + sphere.radius);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals (Object object) {
			Sphere sphere = (Sphere)object;
			return center.equals(sphere.center) && radius == sphere.radius;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return center.hashCode();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#clone()
		 */
		public Object clone () {
			return new Sphere(center, radius, centerConverter, distanceToParent);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString () {
			String s = "center: "+"("+center.toString()+")";
			s += "\tradius: "+radius;
			s += "\tdistance to parent: "+distanceToParent;
			return s;
		}

		/** Returns the center of this sphere.
		 * 
		 * @return the center of this sphere
		 */
		public Object center () {
			return center;
		}


		/** Returns the radius of this sphere.
		 * 
		 * @return the radius of this sphere
		 */
		public double radius () {
			return radius;
		}


		/** Returns the distance from the center of this node to the center of 
		 * the parent node. 
		 * 
		 * @return the distance to the center of the parent node
		 */
		public double distanceToParent () {
			return distanceToParent;
		}

	} // end of class Sphere

	/** <tt>Node</tt> is the class used to represent leaf- and non-leaf nodes 
	 * of <tt>MTree</tt>. Nodes are stored in containers.
	 *	@see Tree.Node
	 *  @see ORTree.Node
	 */
	public class Node extends ORTree.Node {

		/* (non-Javadoc)
		 * @see xxl.core.indexStructures.ORTree.Node#chooseSubtree(xxl.core.indexStructures.Descriptor, java.util.Iterator)
		 */
		protected ORTree.IndexEntry chooseSubtree (Descriptor descriptor, Iterator minima) {
			final Sphere sphere = (Sphere)descriptor;
			TeeCursor entries = new TeeCursor(new IteratorCursor(minima));
			Iterator containing = new Filter(
				entries,
				new Predicate () {
					public boolean invoke (Object object) {
						return sphere(object).containsPD(sphere);
					}
				}
			);
			MapEntry entry = (MapEntry)(containing.hasNext() ?
				Cursors.minimize(containing,
					new Function () {
						public Object invoke (Object object) {
							return new Double(sphere(object).centerDistance(sphere));
						}
					}
				) :
				Cursors.minimize(entries.cursor(),
					new Function () {
						public Object invoke (Object object) {
							return new Double(sphere(object).centerDistance(sphere) - sphere(object).radius);
						}
					}
				)
			);
			IndexEntry indexEntry = (IndexEntry)((LinkedList)entry.getValue()).getFirst();
			sphere.distanceToParent = sphere.centerDistance((Sphere)indexEntry.descriptor());
			entries.close();
			return indexEntry;
		}

		/* (non-Javadoc)
		 * @see xxl.core.indexStructures.Tree.Node#split(java.util.Stack)
		 */
		protected Tree.Node.SplitInfo split (Stack path) {
			final Node node = (Node)node(path);
			// promote
			Iterator seeds = new Sequentializer(
				new Mapper(node.entries(),
					new Function () {
						int index = 0;
						public Object invoke (final Object entry1) {
							return new Mapper(((ArrayList)node.entries).listIterator(++index),
								new Function () {
									public Object invoke (Object entry2) {
										return new Object[] {entry1, entry2};
									}
								}
							);
						}
					}
				)
			);
			final Object [] seed = (Object[])Cursors.maxima(seeds,
				new Function () {
					public Object invoke (Object seed) {
						double dist0 = sphere(((Object[])seed)[0]).distanceToParent;
						double dist1 = sphere(((Object[])seed)[1]).distanceToParent;
						if (dist0 == -1 || dist1 == -1)
							throw new IllegalArgumentException("Undefined 'distanceToParent' entry in a sphere detected.");
						return new Double(Math.abs(dist0 - dist1));
					}
				}
			).getFirst();

			// reference spheres
			final Sphere sphere0 = sphere(seed[0]), sphere1 = sphere(seed[1]);

			final ArrayList entries0 = new ArrayList(node.number()/2);
			switch (splitMode) {
				case HYPERPLANE_SPLIT : { /* modified generalized hyperplane decomposition ensuring minCapacity */
					final int maxCapacity = node.splitMaxNumber();
					Cursors.consume(new Mapper(node.entries(),
						new Function () {
							Collection insertTo = null;
							Collection[] collections = new Collection []{entries0, entries};

							public Object invoke (Object o) {
								if (insertTo != null) {
									insertTo.add(o);
									return o;
								}
								Sphere sphere = sphere(o);
								if (o != seed[0] && (o == seed[1] || sphere.centerDistance(sphere0) > sphere.centerDistance(sphere1)))
									collections[1].add(o);
								else
									collections[0].add(o);
								for (int i = 0; i < collections.length; i++)
									if (collections[i].size() == maxCapacity) {
										insertTo = collections[(i+1)%2];
										return o;
									}
								return o;
							}
						})
					);
					break;
				}
				case BALANCED_SPLIT : { /* balanced distribution */
					ArrayList NNSphere0 = (ArrayList)node.entries;
					Collections.quickSort(NNSphere0, getDistanceBasedComparator(sphere0));
					ArrayList NNSphere1 = (ArrayList)((ArrayList)node.entries).clone();
					Collections.quickSort(NNSphere1, getDistanceBasedComparator(sphere1));
					while(!(NNSphere0.isEmpty() && NNSphere1.isEmpty())) {
						Object next;
						if (!NNSphere0.isEmpty()) {
							next = NNSphere0.get(0);
							entries0.add(next);
							NNSphere0.remove(0);
							NNSphere1.remove(next);
						}
						if (!NNSphere1.isEmpty()) {
							next = NNSphere1.get(0);
							entries.add(next);
							NNSphere1.remove(0);
							NNSphere0.remove(next);
						}
					}
					break;
				}
				default : throw new IllegalArgumentException ("Undefined split mode.");
			}
			Sphere newSphere = (Sphere)computeDescriptor(node.entries = entries0);
			if (!path.isEmpty()) {
				Object top = path.pop();
				newSphere.distanceToParent = newSphere.centerDistance((Sphere)((IndexEntry)indexEntry(path)).descriptor());
				path.push(top);
			}
			((IndexEntry)indexEntry(path)).descriptor = newSphere;
			split = true;
			return new SplitInfo(path).initialize(computeDescriptor(entries));
		}

		/* (non-Javadoc)
		 * @see xxl.core.indexStructures.Tree.Node#grow(java.lang.Object, java.util.Stack)
		 */
		protected void grow (Object data, Stack path) {
			Sphere child;
			if (!(data instanceof IndexEntry)) {
				LeafEntry entry = !(data instanceof LeafEntry) ? new LeafEntry(data) : (LeafEntry)data;
				child = (Sphere)entry.descriptor();
				super.grow(entry, path);
			}
			else {
				 child = (Sphere)((IndexEntry)data).descriptor();
				 super.grow(data, path);
			}
			Stack s = (Stack)path.clone();
			if (split && !s.isEmpty()) s.pop();
			split = false;
			postDistanceToParent(child, s);
		}

		/** Updates the {@link MTree.Sphere#distanceToParent} used for optimization.
		 * 
		 * @param entry the sphere to be updated 
		 * @param path the path from the root to <tt>entry</tt>
		 */
		protected void postDistanceToParent (Sphere entry, Stack path) {
			Sphere child = entry;
			double distToParent = child.distanceToParent;
			Sphere parent = path.isEmpty() ? (Sphere)rootDescriptor : (Sphere)((IndexEntry)indexEntry(path)).descriptor();
			child.distanceToParent = child.centerDistance(parent);
			if (distToParent == child.distanceToParent)
				return;
			if (!path.isEmpty()) {
				path.pop();
				postDistanceToParent(parent, path);
			}
		}

		/* (non-Javadoc)
		 * @see xxl.core.indexStructures.Tree.Node#descriptors(xxl.core.indexStructures.Descriptor)
		 */
		public Iterator descriptors (Descriptor nodeDescriptor) {
			return new Mapper(entries(),
				level == 0 ?
					(Function)new Function () {
						public Object invoke (Object entry) {
							return ((LeafEntry)entry).descriptor;
						}
					}
				:
					(Function)new Function () {
						public Object invoke (Object entry) {
							return ((IndexEntry)entry).descriptor;
						}
					}
			);
		}

		/* (non-Javadoc)
		 * @see xxl.core.indexStructures.Tree.Node#query(xxl.core.indexStructures.Descriptor)
		 */
		public Iterator query (final Descriptor queryDescriptor) {
			final Sphere querySphere = (Sphere)queryDescriptor;
			final double querySphereDist = querySphere.distanceToParent;
			return new Filter(entries(),
				new Predicate () {
					public boolean invoke (Object object) {
						querySphere.distanceToParent = querySphereDist;
						return sphere(object).overlapsPD(querySphere);
					}
				}
			);
		}
	} // end of class Node


	/** <tt>LeafEntry</tt> is the class used to represent entries of leafnodes 
	 * of <tt>MTree</tt>. 
	 */
	public class LeafEntry  {

		/** The sphere containing <tt>data</tt>.
		 */
		protected Sphere descriptor;
		
		/** The data object stored in this leafentry.
		 */
		protected Object data;

		
		/** Creates a new leafentry without data.
		 * 
		 */
		public LeafEntry () {
			data = descriptor = null;
		}

		/** Creates a new leafentry containing <tt>data</tt>. 
		 * 
		 * @param data the object to store in this leafentry.
		 */
		public LeafEntry (Object data) {
			this.data = data;
			descriptor = (Sphere)getDescriptor.invoke(data);
		}

		/** Returns the descriptor of this leafentry.
		 *  
		 * @return the descriptor of this leafentry
		 */
		public Descriptor descriptor () {
			return descriptor;
		}

		/** Compairs this leafentry to other objects. Returns <tt>true</tt> 
		 * if <tt>obj</tt> is another instance of <tt>LeafEntry</tt> containing
		 * the same data or if <tt>obj</tt> itself equals the data object stored
		 * in this leafentry.
		 * 
		 * @param obj the object to be compared for equality with this LeafEntry
		 * @return <tt>true</tt> if <tt>obj</tt> equals the data in this entry or 
		 * is a <tt>LeafNode</tt> containing the same data as this entry 
		 */
		public boolean equals (Object obj) {
			return data.equals(obj instanceof LeafEntry ? ((LeafEntry)obj).data : obj);
		}
				
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return data.hashCode();
		}

	}  // end of class LeafEntry

	/** Creates a new Mtree using the spezified distance functions and the
	 * spezified split mode.
	 * 
	 * @param pointDistance the new {@link #pointDistance}
	 * @param sphereDistance the new {@link #sphereDistance}
	 * @param splitMode the new {@link #splitMode}
	 */
	public MTree (Distance pointDistance, Distance sphereDistance, int splitMode) {
		MTree.pointDistance  = pointDistance;
		MTree.sphereDistance = sphereDistance;
		this.splitMode = splitMode;
	}

	/** Creates a new Mtree using the spezified distance functions and the
	 * default split mode.
	 * 
	 * @param pointDistance the new {@link #pointDistance}
	 * @param sphereDistance the new {@link #sphereDistance}
	 */
	public MTree (Distance pointDistance, Distance sphereDistance) {
		MTree.pointDistance  = pointDistance;
		MTree.sphereDistance = sphereDistance;
	}

	/** Creates a new Mtree using the spezified distance function for points 
	 * and the spezified split mode.
	 * 
	 * @param pointDistance the new {@link #pointDistance}
	 * @param splitMode the new {@link #splitMode}
	 */
	public MTree (Distance pointDistance, int splitMode) {
		MTree.pointDistance = pointDistance;
		this.splitMode = splitMode;
	}

	/** Creates a new Mtree using the spezified distance function for points and the
	 * default split mode.	 
	 * 
	 * @param pointDistance the new {@link #pointDistance}
	 */
	public MTree (Distance pointDistance) {
		this(pointDistance, HYPERPLANE_SPLIT);
	}

	/** Creates a new Mtree using the spezified split mode.
	 * 
	 * @param splitMode the new {@link #splitMode}
	 */
	public MTree (int splitMode) {
		this.splitMode = splitMode;
	}

	/** Creates a new Mtree using the default parameters.
	 */
	public MTree () {}

	/** Returns a comparator comparing spheres with respect to their distance
	 * to the given <tt>referenceSphere</tt>.
	 * 
	 * @param referenceSphere the rererence for the comparison of the comparator
	 * @return a comparator returning 1, 0 or -1 if its first parameter has
	 * smaller, equal or greater distance to <tt>referenceSphere</tt> than the second one 
	 */
	protected Comparator getDistanceBasedComparator (final Sphere referenceSphere) {
		return new Comparator () {
				public int compare (Object o1, Object o2) {
					double dist1 = referenceSphere.centerDistance(sphere(o1));
					double dist2 = referenceSphere.centerDistance(sphere(o2));
					return dist1 < dist2 ? 1 : dist1 > dist2 ? -1 : 0;
				}
		};
	}

	/* (non-Javadoc)
	 * @see xxl.core.indexStructures.ORTree#computeDescriptor(java.util.Collection)
	 */
	public Descriptor computeDescriptor (Collection collection) {
		ArrayList entries = (ArrayList)Cursors.toList(collection.iterator(), new ArrayList(collection.size()));
		double[][] distance = new double[entries.size()][entries.size()];
		int i_min = 0;
		double minValue = 0;
		for (int i = 0; i < entries.size(); i++) {
			double maxValue = 0, dist = 0;
			for (int j = i+1; j < entries.size(); j++) {
				Sphere sphere0 = sphere(entries.get(i));
				Sphere sphere1 = sphere(entries.get(j));
				dist = (distance[i][j] = sphere0.centerDistance(sphere1)) + sphere1.radius;
				if (dist > maxValue)
					maxValue = dist;
			}
			if (i == 0 || maxValue < minValue) {
				i_min = i;
				minValue = maxValue;
			}
		}
		Sphere descriptor = (Sphere)sphere(entries.get(i_min)).clone();
		for (int i = 0; i < entries.size(); i++) {
			Sphere next = sphere(entries.get(i));
			next.distanceToParent = distance[i][i_min];
			descriptor.union(next);
		}
		return descriptor;
	}

	/* (non-Javadoc)
	 * @see xxl.core.indexStructures.Tree#createNode(int)
	 */
	public Tree.Node createNode (int level) {
		return new Node().initialize(level, new ArrayList(20));
	}

	/* (non-Javadoc)
	 * @see xxl.core.indexStructures.Tree#descriptor(java.lang.Object)
	 */
	public Descriptor descriptor (Object entry) {
		return (entry instanceof IndexEntry) ?
				((IndexEntry)entry).descriptor() :
					(entry instanceof LeafEntry) ?
					((LeafEntry)entry).descriptor() :
						(Descriptor)getDescriptor.invoke(entry);
	}

	/** Returns the descriptor of <tt>entry</tt> as a sphere.
	 * 
	 * @param entry the entry whos descriptor sphere is demanded.
	 * @return the sphere which is the descriptor of <tt>entry</tt>
	 */
	public Sphere sphere (Object entry) {
		return (Sphere)descriptor(entry);
	}

	/* (non-Javadoc)
	 * @see xxl.core.indexStructures.Tree#query(xxl.core.indexStructures.Descriptor, int)
	 */
	public Cursor query (final Descriptor queryDescriptor, final int targetLevel) {
		final Iterator [] iterators = new Iterator[height()+1];

		final Sphere querySphere = (Sphere)queryDescriptor;
		final double[] parentDistance = new double[height()+1];
		Arrays.fill(parentDistance, -1);

		Arrays.fill(iterators, EmptyCursor.DEFAULT_INSTANCE);
		if (height()>0 && querySphere.overlapsPD(rootDescriptor())) {
			iterators[height()] = new SingleObjectCursor(rootEntry());
			parentDistance[height()] = querySphere.centerDistance((Sphere)rootDescriptor());
		}

		return new AbstractCursor() {
			int queryAllLevel = 0;
			Object toRemove = null;
			Stack path = new Stack();

			public boolean hasNextObject() {
				for (int parentLevel = targetLevel;;)
					if (iterators[parentLevel].hasNext())
						if (parentLevel==targetLevel)
							return true;
						else {
							IndexEntry indexEntry = (IndexEntry)iterators[parentLevel].next();
							if (indexEntry.level()>=targetLevel) {
								Node node = (Node)indexEntry.get(true);
								Sphere sphere = (Sphere)indexEntry.descriptor();
								if (parentDistance[node.level] == -1)
									parentDistance[node.level] = querySphere.centerDistance(sphere);
								if (parentDistance[parentLevel] != -1)
									querySphere.distanceToParent = parentDistance[parentLevel];
								Iterator queryIterator;

								if (parentLevel<=queryAllLevel || querySphere.containsPD(sphere)) {
									queryIterator = node.entries();
									if (parentLevel>queryAllLevel && !iterators[node.level].hasNext())
											queryAllLevel = node.level;
								}
								else {
									querySphere.distanceToParent = parentDistance[node.level];
									queryIterator = node.query(querySphere);
								}
								iterators[parentLevel = node.level] =
									iterators[parentLevel].hasNext()?
										new Sequentializer(queryIterator, iterators[parentLevel]):
										queryIterator;
								path.push(new MapEntry(indexEntry, node));
							}
						}
					else
						if (parentLevel==height())
							return false;
						else {
							if (parentLevel==queryAllLevel)
								queryAllLevel = 0;
							if (level(path)==parentLevel) {
								path.pop();
								parentDistance[parentLevel] = -1;
							}
							iterators[parentLevel++] = EmptyCursor.DEFAULT_INSTANCE;
						}
			}

			public Object nextObject() {
				Object next = toRemove = iterators[targetLevel].next();
				return next instanceof LeafEntry ? ((LeafEntry)next).data : next;
			}

			public void update (Object object) throws UnsupportedOperationException, IllegalStateException, IllegalArgumentException {
				super.update(object);
				if (targetLevel>0)
					throw new IllegalStateException();
				else
					if (targetLevel!=0 || !descriptor(object).equals(descriptor(toRemove)))
						throw new IllegalArgumentException();
					else {
						IndexEntry indexEntry = (IndexEntry)indexEntry(path);
						Node node = (Node)node(path);

						iterators[0].remove();
						node.grow(object, path);
						indexEntry.update(node, true);
					}
			}
			
			public boolean supportsUpdate() {
				return true;
			}

			public void remove () throws UnsupportedOperationException, IllegalStateException {
				super.remove();
				if (targetLevel<height()) {
					IndexEntry indexEntry = (IndexEntry)indexEntry(path);
					Node node = (Node)node(path);
					iterators[node.level].remove();
					for (;;) {
						if (indexEntry==rootEntry() && node.level>0 && node.number()==1) {
							rootEntry = (IndexEntry)node.entries().next();
							rootDescriptor = ((IndexEntry)rootEntry()).descriptor();
							indexEntry.remove();
							break;
						}
						if (node.number()==0) {
							up(path);
							indexEntry.remove();
							if (height()==1) {
								rootEntry = null;
								rootDescriptor = null;
								break;
							}
							else {
								indexEntry = (IndexEntry)indexEntry(path);
								node = (Node)node(path);
								iterators[node.level].remove();
							}
						}
						else if (indexEntry!=rootEntry() && node.underflows()) {
							Iterator entries = node.entries();
							indexEntry.descriptor = computeDescriptor(node.entries);
							up(path);
							indexEntry.remove();
							iterators[level(path)].remove();
							((Sphere)indexEntry.descriptor).distanceToParent = -1;
							indexEntry = (IndexEntry)node(path).chooseSubtree(indexEntry.descriptor, path);
							MTree.this.update(path);
							node = (Node)down(path, indexEntry);
							while (entries.hasNext())
								node.grow(entries.next(), path);
							if (node.overflows())
								node.redressOverflow(path);
							else {
								MTree.this.update(path);
								up(path);
							}
							indexEntry = (IndexEntry)indexEntry(path);
							node = (Node)node(path);
						}
						else {
							MTree.this.update(path);
							while (up(path)!=rootEntry()) {
								MTree.this.update(path);
								indexEntry = (IndexEntry)indexEntry(path);
								node = (Node)node(path);
							}
							((IndexEntry)rootEntry).descriptor = rootDescriptor = computeDescriptor(node.entries);
							break;
						}
					}
				}
				else {
					rootEntry = null;
					rootDescriptor = null;
				}
				if (targetLevel>0) {
					IndexEntry indexEntry = (IndexEntry)toRemove;

					indexEntry.removeAll();
				}
			}
			
			public boolean supportsRemove() {
				return true;
			}
			
		};
	}


	/*********************************************************************/
	/*                              CONVERTER                            */
	/*********************************************************************/

	/** This class acts as a converter to serialize the tree's leaf-entries.
	 */
	public class LeafEntryConverter extends Converter {
		
		/** A suitable Converter to serialize the objects stored in the leaf-entries.
		 */	
		protected Converter objectConverter;

		/** Creates a new <tt>LeafEntryConverter</tt>.
		 * 
		 * @param objectConverter a <tt>Converter</tt> to serialize objects stored in the leaf-entries
		 */
		public LeafEntryConverter (Converter objectConverter) {
			this.objectConverter = objectConverter;
		}

		/* (non-Javadoc)
		 * @see xxl.core.io.converters.Converter#read(java.io.DataInput, java.lang.Object)
		 */
		public Object read (DataInput dataInput, Object object) throws IOException {
			LeafEntry leafEntry = (LeafEntry)object;
			leafEntry.data = objectConverter.read(dataInput, null);
			leafEntry.descriptor = (Sphere)getDescriptor.invoke(leafEntry.data);
			leafEntry.descriptor.distanceToParent = DoubleConverter.DEFAULT_INSTANCE.readDouble(dataInput);
			return leafEntry;
		}

		/* (non-Javadoc)
		 * @see xxl.core.io.converters.Converter#write(java.io.DataOutput, java.lang.Object)
		 */
		public void write (DataOutput dataOutput, Object object) throws IOException {
			LeafEntry leafEntry = (LeafEntry)object;
			objectConverter.write(dataOutput, leafEntry.data);
			DoubleConverter.DEFAULT_INSTANCE.writeDouble(dataOutput, leafEntry.descriptor.distanceToParent);
		}
	}

	/** Creates a new Converter for leaf-entries using the given Converter
	 * for objects stored within the entries.
	 * 
	 * @param objectConverter a Converter to serialize objects stored in the leaf-entries
	 * @return a new Converter for leaf-entries
	 */
	public Converter leafEntryConverter (Converter objectConverter) {
		return new LeafEntryConverter(objectConverter);
	}

	/** This class acts as a converter to serialize the tree's nodes.
	 */
	public class NodeConverter extends Converter {

		/** A converter for index-entries.
		 */
		protected Converter indexEntryConverter;

		/** A converter for leaf-entries.
		 */
 		protected Converter leafEntryConverter;


		/** Creates a new <tt>NodeConverter</tt> using the given converters
		 * for leaf- and index-entries.
		 * 
		 * @param leafEntryConverter a converter for leaf-entries
		 * @param indexEntryConverter a converter for index-entries
		 */
		public NodeConverter (Converter leafEntryConverter, Converter indexEntryConverter) {
			this.indexEntryConverter = indexEntryConverter;
			this.leafEntryConverter = leafEntryConverter;
		}

		/* (non-Javadoc)
		 * @see xxl.core.io.converters.Converter#read(java.io.DataInput, java.lang.Object)
		 */
		public Object read (DataInput dataInput, Object object) throws IOException {
			Node node = (Node)createNode(dataInput.readShort());

			for (int i=dataInput.readInt(); --i>=0;)
				node.entries.add(node.level==0?
					leafEntryConverter.read(dataInput, new LeafEntry()):
					indexEntryConverter.read(dataInput, createIndexEntry(node.level))
			);
			return node;
		}

		/* (non-Javadoc)
		 * @see xxl.core.io.converters.Converter#write(java.io.DataOutput, java.lang.Object)
		 */
		public void write (DataOutput dataOutput, Object object) throws IOException {
			Node node = (Node)object;
			Converter converter = node.level == 0 ? leafEntryConverter: indexEntryConverter;
			ShortConverter.DEFAULT_INSTANCE.write(dataOutput, new Short((short)node.level));
			IntegerConverter.DEFAULT_INSTANCE.write(dataOutput, new Integer(node.number()));
			for (Iterator entries = node.entries(); entries.hasNext();)
				converter.write(dataOutput, entries.next());
		}
	}

	/* (non-Javadoc)
	 * @see xxl.core.indexStructures.ORTree#nodeConverter(xxl.core.io.converters.Converter, xxl.core.io.converters.Converter)
	 */
	public Converter nodeConverter (Converter leafEntryConverter, Converter indexEntryConverter) {
		return new NodeConverter(leafEntryConverter, indexEntryConverter);
	}

	/*********************************************************************/
	/*                       DEBUG FUNCTIONALITY                         */
	/*********************************************************************/

	/** Checks if the distances to parents are valid.
	 * 
	 * @param indexEntry IndexEntry to start at
	 * @param descriptorList list collecting descriptors
	 */
	public void checkDistanceToParent (IndexEntry indexEntry, List descriptorList) {
		Node parent = (Node)indexEntry.get(true);
		Sphere parentSphere = (Sphere)indexEntry.descriptor();
		if (parent.level > 0)
			for (Iterator entries = parent.entries(); entries.hasNext();) {
				IndexEntry next = (IndexEntry)entries.next();
				Sphere descriptor = (Sphere)next.descriptor();
				descriptorList.add(next.descriptor);
				if (descriptor.distanceToParent != parentSphere.centerDistance(descriptor))
					System.out.println("Error occured: wrong distance to parent! \n"
						+"\tchild: "+descriptor
						+"\n\tparent: "+parentSphere
						+"\n\tchild.distanceToParent: "+descriptor.distanceToParent
						+"\n\tparent.centerDistance(child): "+parentSphere.centerDistance(descriptor)+"\n"
					);
				checkDistanceToParent(next, descriptorList);
				descriptorList.remove(next.descriptor);
			}
		else {
			Iterator entries = parent.entries();
			while (entries.hasNext()) {
				Sphere descriptor = (Sphere)((LeafEntry)entries.next()).descriptor();
				if (descriptor.distanceToParent != parentSphere.centerDistance(descriptor))
					System.out.println("Error occured: wrong distance to parent! \n"
						+"\tchild: "+descriptor
						+"\n\tparent: "+parentSphere
						+"\n\tchild.distanceToParent: "+descriptor.distanceToParent
						+"\n\tparent.centerDistance(child): "+parentSphere.centerDistance(descriptor)+"\n"
					);
			}
		}
	}

	/** Checks if the distances to parents are valid in the tree.
	 */
	public void checkDistanceToParent () {
		if (height() > 0)
			checkDistanceToParent((IndexEntry)rootEntry(), new LinkedList());
	}


}
