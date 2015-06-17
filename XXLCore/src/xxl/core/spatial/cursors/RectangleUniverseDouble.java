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

package xxl.core.spatial.cursors;

import java.io.File;
import java.util.Iterator;

import xxl.core.cursors.mappers.Aggregator;
import xxl.core.functions.Function;
import xxl.core.io.Convertable;
import xxl.core.io.Convertables;
import xxl.core.spatial.points.DoublePoint;
import xxl.core.spatial.points.Point;
import xxl.core.spatial.rectangles.DoublePointRectangle;
import xxl.core.spatial.rectangles.Rectangle;


/**
 *	The RectangleUniverseDouble computes the universe (Minimal Bounding Rectangle, MBR or MBB) for an Iterator of DoublePoints.
 *	Note, that this universe is an aggregate (of type Rectangle).
 *
 */
public class RectangleUniverseDouble extends Aggregator{

	/** The aggregate function that computes the actual MBR.
	*/
	public static class UniverseFunction extends Function{

		/** the Dimensionaltiy of the data
		 */
		protected int dim;

		/** Creates a new UniverseFunction.
		 *
		 * @param dim the Dimensionaltiy of the data
		 */
		public UniverseFunction(int dim){
			this.dim = dim;
		}

		/** prints debug information
		 * @param mbr a rectangle
		 * @param point a point
	 	 */
		protected void debugOut(Rectangle mbr, Point point){
			System.out.println("MBR-----------------------------------------");
			System.out.println(mbr);
			System.out.println("POINT---------------------------------------");
			System.out.println(point);
			System.out.println("--------------------------------------------\n\n");
		}

		/** invoke-method required by the aggregator
		 * @param aggregate is the current value for the aggregation
		 * @param next is the next object that is used for computing the next value of the aggregation
		 * @return the next aggregate value 
		 */
		public Object invoke(Object aggregate, Object next){

			Rectangle mbr = (Rectangle) aggregate;
			Rectangle rectangle = (Rectangle) next;

			//debugOut(mbr,point);							//show current aggregate and next Point

			double[] rectll = (double[]) rectangle.getCorner(false).getPoint();	//extract ll-point from rectangle
			double[] rectur = (double[]) rectangle.getCorner(true).getPoint();	//extract ur-point from rectangle
			DoublePoint rectllp = new DoublePoint(rectll);
			DoublePoint recturp = new DoublePoint(rectur);
			if(mbr==null)								//if aggregate == null
				mbr = new DoublePointRectangle( rectllp, recturp );	//initialize aggregate-Object
			double[] ll = (double[]) mbr.getCorner(false).getPoint();		//extract ll-point
			double[] ur = (double[]) mbr.getCorner(true).getPoint();		//extract ur-point

			for(int i=0; i<dim; i++){
				ll[i] = Math.min(ll[i], rectll[i]);				//compute min for this dimension
				ur[i] = Math.max(ur[i], rectur[i]);				//compute max for this dimension
			}
			return mbr;								//return new aggregate
		}
	}

	/** Creates a new RectangleUniverseDouble.
	 *
	 * @param iterator input iterator containing Points
	 * @param dim dimensionality of the data
	 */
	public RectangleUniverseDouble(Iterator iterator, int dim){
		super( iterator, new UniverseFunction(dim) );
	}

	/**
	 * use-case: compute universe for a file containing KPEs with
	 * DoubleRectangle data-objects (common data format for spatial data)
	 * and write aggregate rectangle to a file "file-name.universe".
	 *
	 * @param args: args[0] file-name, args[1] dimensionality of the data
	 */
	public static void main(String[] args){

		if(args.length != 2){
			System.out.println("usage: java xxl.core.spatial.cursors.UniverseFunction <file-name> <dim>");
			return;	
		}

		final int dim = Integer.parseInt(args[1]);
		
		Aggregator ag =
			new RectangleUniverseDouble(
					Mappers.getKPEDataMapper(
						new KPEInputCursor( new File(args[0]), 1024*1024, dim )
					),
					dim
			);
		
		Object last = ag.last();
		System.out.println("# The universe of "+args[0]+" dim "+args[1]+" is");
		System.out.println(last);
		ag.close();

		Convertables.write(args[0]+".universe",(Convertable)last);
	}
}
