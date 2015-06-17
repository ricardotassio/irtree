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

package xxl.core.functions;

import java.util.Iterator;

import xxl.core.cursors.identities.TeeCursor;
import xxl.core.math.Maths;
import xxl.core.util.Distance;

/**
 * This class provides a projection from any object-space with a given distance function
 * to a k-dimensional real valued
 * metric space (using the euklidean distance function, also known as L2-Metric)
 * using the FastMap algorithm.
 * The implementation is based upon
 * [FL95 ]: Christos Faloutsos, King-Ip Lin.
 * FastMap: A Fast Algorithm for Indexing, Data-Mining and Visualization
 * of Traditional and Multimedia Datasets. SIGMOD Conference 1995. 163-174.
 * 
 * Due to the lack of an efficient memory management in java
 * the original algorithm is modified.
 * Instead of computing and storing a distance matrix in advance 
 * only the pivot objects of each dimension
 * are computed in advance and the modified distance function for each dimension
 * will be recursivly evaluated.
 */
public class FastMapProjection extends Function {

	/** The chosen dimension */
	protected int dim;

	/** Stores the computed pivot elements (left) for the dimension denoted by the index */
	protected Object[] a;

	/** Stores the computed pivot elements (right) for the dimension denoted by the index */
	protected Object[] b;

	/** The distance function used for the object space */
	protected Distance dist;

	/** Stores the distances between the pivot-objects.
	 * d[k] is the distance between a[k] and b[k]. Used for effectiveness.
	 */
	protected double[] d;

	/** Given objects of the object space. A {@link xxl.core.cursors.identities.TeeCursor tee cursor}
	 * is used because in order to compute the pivot elements in advance
	 * one must scan the data <tt>2*k</tt> times, i.e. the complexity of locating
	 * the pivot elements is <tt>O(2kn)</tt> whereas <tt>n</tt> denotes the number of objects
	 * in the {@link xxl.core.cursors.Cursor cursor}.
	 *
	 * @see xxl.core.cursors.Cursor
	 * @see xxl.core.cursors.identities.TeeCursor
	 */
	protected TeeCursor data;

	/** Indicates wether the tee cursor has been used at least one time */
	protected boolean teecursorInit = false;

	/**
	 * Constructs a new FastMap Projection based upon the data given.
	 * @param data init data
	 * @param dimension dimension to project data to
	 * @param dist distance function for the kind of data given in data.
	 * @throws IllegalArgumentException if the given dimension isn't at least
	 * 1 or the given tee cursor doesn't support
	 * multiple access.
	 *
	 * @see xxl.core.cursors.identities.TeeCursor
	 * @see xxl.core.util.Distance
	 */
	public FastMapProjection( TeeCursor data , int dimension, Distance dist ) throws IllegalArgumentException {
		dim = dimension ;
		if ( dim <= 0) throw new IllegalArgumentException("Dimension to project to must be one or greater! (given="+dim+")");
		a = new Object [ dim ];
		b = new Object [ dim ];
		d = new double [ dim ];
		this.dist = dist;
		this.data = data;
		try{
			init();
		}
		catch ( IllegalStateException e){
			throw new IllegalArgumentException( "Used teecursor must support multiple access!");
		}
	}

	/**
	 * Constructs a new FastMap Projection based upon the data given. The given data will
	 * be wrapped by a tee cursor in order to support
	 * multiple access to the data. For default a TeeCursor is used.
	 * @param data init data. Will be wrapped by a tee cursor to support multiple access.
	 * @param dimension dimension to project data to.
	 * @param dist distance function for the kind of data given.
	 * @throws IllegalArgumentException if the given dimension isn't at least 1.
	 *
	 * @see xxl.core.cursors.identities.TeeCursor
	 * @see xxl.core.cursors.identities.TeeCursor
	 * @see xxl.core.util.Distance
	 */
	public FastMapProjection( Iterator data , int dimension, Distance dist ) throws IllegalArgumentException {
		this( new TeeCursor(data), dimension, dist);
	}

	/**
	 * Returns a projection to a k-dimensional real valued space
	 * of the given Object as an object of type <tt>double[k]</tt>.
	 * 
	 * @param x object to project to a subspace
	 * @return projected object (represented by a <tt>double[]</tt> of dimension <tt>k</tt>.)
	 */
	public Object invoke( Object x) {
		double[] re = new double [ dim ];
		for ( int i = 0; i < dim ; i++)
			re [ i ] = p ( i, x );
		return re;
	}

	/** Determines the pivot objects for each dimension needed. The complexity
	 * of the initialization is <tt>O(2kn)</tt> where <tt>n</tt> denotes the number
	 * of objects in the given {@link xxl.core.cursors.identities.TeeCursor tee cursor}
	 * and <tt>k</tt> the given dimensions.
	 * 
	 * @throws IllegalStateException if the {@link xxl.core.cursors.identities.TeeCursor tee cursor}
	 * doesn't support adequate multiple access.
	 */
	protected void init() throws IllegalStateException{
		Object p = null;
		if ( data.hasNext() ) p = data.next();
		for ( int k = 0 ; k < dim ; k++ ) {
			a[k] = p ;
			b[k] = getFurthermost( k, a[k] );
 			a[k] = getFurthermost( k, b[k] );
 			d[k] = d ( k, a[k], b[k] );
 		}
	}

	/** Returns the furthermost object wrt to p.
	 * @param k Specifies the aggregation level of the distance function
	 * used to compute the furthermost object
	 * @param p reference object
	 * @throws IllegalStateException if the tee cursor doesn't support
	 * adequate multiple access.
	 * @return the furthermost object wrt to p.
	 */
 	protected Object getFurthermost( int k, Object p ) throws IllegalStateException {
 		Iterator it = null;
 		if ( ! teecursorInit){
 			it = data;
 			teecursorInit = true;
		}
 		else it = data.cursor();
 		Object fm  = p;
 		Object tmp = null;
		while ( it.hasNext() ){
 			tmp = it.next();
 			if ( d( k,  p , fm ) < d (k, p , tmp ) ) fm = tmp;
 		}
 		return fm;	
 	}

	/** Returns the projected distance between the objects l and r.
	 * @param k Specifies the aggregation level of the distance function
	 * @param l left object for distance computing.
	 * @param r right object for distance computing.
	 * @return the modified distance between two objects of given aggregation level
	 */
	protected double d( int k, Object l, Object r) {
		double re = 0.0;
		if ( k > 0) re = Math.sqrt ( Math.pow ( d ( k-1, l, r) , 2.0 ) 
						- Math.pow ( Maths.pDistance ( p (k-1, l) , p( k-1, r), 2), 2.0));
		else re = dist.distance ( l, r );
		return re;
	}

	/** Returns the k-th component of the FastMap-Projection of o.
	 * @param o the object to project
	 * @param k dimension of the subspace the object is projected to 
	 * @return the k-th component of the projection of object o
	 */
	protected double p ( int k, Object o){
		double dab = d[k];
		double dai = d ( k,  a[k], o);
		double dbi = d ( k,  b[k], o);
		return ( Math.pow ( dai, 2.0) + Math.pow ( dab, 2.0) - Math.pow ( dbi, 2.0)) / (2.0 * dab);
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * submit parameters when the main method is called.
	 */
	public static void main (String[] args) {
		java.util.List l = new java.util.ArrayList();
		
		l.add(new xxl.core.spatial.points.DoublePoint(new double[]{0.9,0.3}));
		l.add(new xxl.core.spatial.points.DoublePoint(new double[]{0.8,0.4}));
		l.add(new xxl.core.spatial.points.DoublePoint(new double[]{0.7,0.2}));
		l.add(new xxl.core.spatial.points.DoublePoint(new double[]{0.6,0.8}));
		l.add(new xxl.core.spatial.points.DoublePoint(new double[]{0.5,0.9}));
		
		Function fm = new FastMapProjection(
			l.iterator(),
			1,				// map everything to one double value
			xxl.core.spatial.LpMetric.EUCLIDEAN	
		);
		
		System.out.println("All points are projected to the line between point 3 {0.7,0.2} and point 5 {0.5,0.9}");
		System.out.println("Transformed Coordinates");
		Iterator it = l.iterator(); 
		while (it.hasNext()) {
			Object point = it.next();
			double res[] = (double[]) fm.invoke(point);
			for (int i=0; i<res.length-1; i++)
				System.out.print(res[i]);
			System.out.print(res[res.length-1]);
			System.out.println();
		}
	}
}
