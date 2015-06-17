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
import xxl.core.cursors.mappers.Mapper;
import xxl.core.functions.Function;
import xxl.core.io.Convertables;
import xxl.core.spatial.KPE;
import xxl.core.spatial.KPEzCode;
import xxl.core.spatial.SpaceFillingCurves;
import xxl.core.spatial.points.DoublePoint;
import xxl.core.spatial.points.FixedPoint;
import xxl.core.spatial.points.FloatPoint;
import xxl.core.spatial.points.Point;
import xxl.core.spatial.rectangles.DoublePointRectangle;
import xxl.core.spatial.rectangles.FixedPointRectangle;
import xxl.core.spatial.rectangles.FloatPointRectangle;
import xxl.core.spatial.rectangles.Rectangle;

/**
 * This class provides several static methods returning suitable Mappers
 * for the spatial context.
 * 
 * @see xxl.core.cursors.mappers.Mapper
 */
public class Mappers {
	
	/** Don't let anyone instantiate this class. 
	 */
	private Mappers() {}
	
	/** Returns a Mapper mapping an Iterator of DoublePoints to 
	 * an Iterator of FloatPoints.
	 *
	 * @param iterator an input iterator containing DoublePoints
	 * @return a mapper mapping DoublePoints to FloatPoints
	 */
	public static Mapper getDoublePointFloatPointMapper(final Iterator iterator) {
		return new Mapper(iterator,
			new Function(){
				public Object invoke(Object object){

					DoublePoint dp = (DoublePoint) object;
					float[] point = new float[dp.dimensions()];

					for(int i=0; i<point.length; i++)
						point[i] = (float) dp.getValue(i);

					return new FloatPoint(point);
				}
			}
		);		
	}

	/** Returns a Mapper mapping an Iterator of FloatPoints to 
	 * an Iterator of DoublePoints.
	 *
	 * @param iterator an input iterator containing FloatPoints
	 * @return a mapper mapping FloatPoints to DoublePoints
	 */
	public static Mapper getFloatPointDoublePointMapper(final Iterator iterator){
		return new Mapper(iterator,
			new Function(){
				public Object invoke(Object object){
					FloatPoint fp = (FloatPoint) object;
					double[] point = new double[fp.dimensions()];
					for(int i=0; i<point.length; i++)
						point[i] = fp.getValue(i);
					return new DoublePoint(point);
				}
			}
		);		
	}

	
	/**
	 * Return a mapper mapping an input iterator of KPEs containing 
	 * a Rectangle as data item to Points. This is useful if one wants to extract 
	 * Points from an existing data set of spatial data.
	 * 
	 * @param iterator an input iterator containing KPEs
	 * @param corner boolean flag determining which corner should be returned by the mapping function
	 * @return a mapper mapping KPEs to FloatPoints
	 */
	public static Mapper getKPEFloatPointMapper (Iterator iterator, final boolean corner) {
		return new Mapper(iterator,
			new Function() {
				public Object invoke(Object object){
					return new FloatPoint( (DoublePoint) ((Rectangle) ((KPE)object).getData()).getCorner(corner) );	//returns a corner of the Rectangle and maps it to a FloatPoint
				}
			}
		);		
	}
	
	/** Return a mapper extracting data objects from a given Iterator of KPEs. 
	 * This is performed by calling the <tt>getData()</tt> method on each input object.
	 * 
	 * @param iterator an input iterator containing KPEs
	 * @return a mapper mapping KPEs to the included data objects
	 */
	public static Mapper getKPEDataMapper (final Iterator iterator) {
		return new Mapper(iterator, 
			new Function() {
				public Object invoke(Object object) {
					return ((KPE)object).getData();
				}
			}
		);		
	}
	
	/**  
	 *	Returns a Mapper mapping a DoublePoint to a DoublePointRectangle 
	 *  where the point is the center of the rectangle
	 *	2*epsilon x 2*epsilon x ... x 2*epsilon.
	 * 
	 * @param iterator an Iterator containing DoublePoints
	 * @param epsilon the epsilon distance (Note: rectangles of side length 2*epsilon are created)
	 * @return a mapper mapping DoublePoints to DoublePointRectangles
	 */
	public static Mapper getDoublePointRectangleMapper(Iterator iterator, final double epsilon) {
		return new Mapper(iterator, 
			new Function() {
				/** 
				 * @param object The type of the object is assumed to be DoublePoint.
				 * @return a DoublePointRectangle with the point being the center.
				 */
				public Object invoke(Object object){
					double[] point = (double[]) ((DoublePoint) object).getPoint();
					double[] ll = new double[point.length];
					double[] ur = new double[point.length];
	
					for(int i=0; i<point.length; i++){
						ll[i] = Math.max(0.0d, point[i]-epsilon);					//check lower border
						ur[i] = Math.min(0.9999999999999999d, point[i]+epsilon);	//check upper border
					}
					DoublePoint llp = new DoublePoint(ll);
					DoublePoint urp = new DoublePoint(ur);
					return new DoublePointRectangle(llp, urp);
				}
			}
		);
	}

	/**  
	 *	Returns a function converting a FloatPoint to a FloatPointRectangle where 
	 *  the point is the center of the rectangle
	 *	2*epsilon x 2*epsilon x ... x 2*epsilon. 
	 *  The input-Points are assumed to be inside the unit-cube.
	 * 
	 * @param epsilon the epsilon distance (Note: rectangles of side length 2*epsilon are created)
	 * @return a function converting Points to FixedPointRectangles
	 */
	public static Function getFloatPointRectangleMappingFunction(final float epsilon) {
		return new Function() {
			/** 
			 * @param object is expected to be of type FloatPoint
			 * @return a DefaultRectangle with the FloatPoint as its center.
			 */
			public Object invoke(Object object) {
				float[] point = (float[]) ((FloatPoint) object).getPoint();
				float[] ll = new float[point.length];
				float[] ur = new float[point.length];

				for(int i=0; i<point.length; i++){
					ll[i] = Math.max(0.0f, point[i]-epsilon);			//check lower border
					ur[i] = Math.min(0.9999999f, point[i]+epsilon);		//check upper border
				}
				FloatPoint llp = new FloatPoint(ll);
				FloatPoint urp = new FloatPoint(ur);
				return new FloatPointRectangle(llp, urp);
			}						
		};
	}
	
	/**  
	 *	Returns a Mapper mapping a FloatPoint to a FloatPointRectangle 
	 *  where the point is the center of the rectangle
	 *	2*epsilon x 2*epsilon x ... x 2*epsilon.
	 * 
	 * @param iterator an Iterator containing FloatPoints
	 * @param epsilon the epsilon distance (Note: rectangles of side length 2*epsilon are created)
	 * @return a mapper mapping FloatPoints to FloatPointRectangles
	 */
	public static Mapper getFloatPointRectangleMapper(Iterator iterator, final float epsilon) {
		return new Mapper(iterator,getFloatPointRectangleMappingFunction(epsilon));
	}
	
	/** 
	 *	Returns a Mapper mapping incoming FloatPoints to an Iterator of
	 *	<tt>KPEzCodes</tt> containing a rectangle (of type Rectangle) as well as the corresponding z-code 
	 *	(of type <tt>BitSet</tt>).
	 *	<br>
	 *	This means, for each input FloatPoint this class computes
	 *
	 *  <ol>
	 *    <li> a Rectangle with length
	 *	       epsilon in each dimension. The incoming FloatPoint is the center of the latter rectangle.
	 *    <li> the z-code of that rectangle
	 *  </ol>
	 *
	 *  Both objects are contained in a specialized ConvertableTuple called KPEzCode which is returned by this Mapper.
	 * 
	 * 	@param iterator an Iterator containing FloatPoints
	 *  @param epsilon extension of the rectangles to be created in each dimension
	 *  @param maxLevel the maximum level to be used for the z-code computation
	 *  @return a mapper mapping FloatPoints to KPEzCodes
	 */
	public static Mapper getFloatPointKPEzCodeMapper(Iterator iterator, final float epsilon, final int maxLevel) {
		return new Mapper(iterator, 
			new Function() {
				protected int count = 0;
	
				Function epsilonMapping = getFloatPointRectangleMappingFunction(epsilon/2);
	
				public Object invoke(Object object){
					return new KPEzCode(object, new Integer(count++), SpaceFillingCurves.zCode( (Rectangle) epsilonMapping.invoke(object), maxLevel) );
				}
			}
		);
	}

	/**  
	 *	Returns a function converting a Point to a FixedPointRectangle where 
	 *  the point is the center of the rectangle
	 *	2*epsilon x 2*epsilon x ... x 2*epsilon. 
	 *  The input-Points are assumed to be inside the unit-cube.
	 * 
	 * @param epsilon the epsilon distance (Note: rectangles of side length 2*epsilon are created)
	 * @return a function converting Points to FixedPointRectangles
	 */
	public static Function getPointFixedPointRectangleMappingFunction(final double epsilon) {
		return new Function () {
			public Object invoke(Object object) {
				Point point = (Point) object;
				long[] ll = new long[ point.dimensions() ];
				long[] ur = new long[ point.dimensions() ];

				for(int i=0; i<ll.length; i++){
					double scalarValue = point.getValue(i);
					ll[i] = xxl.core.math.Maths.doubleToNormalizedLongBits( Math.max(0.0d, scalarValue-epsilon) );		//check lower border
					ur[i] = xxl.core.math.Maths.doubleToNormalizedLongBits( Math.min(0.9999999999999999d, scalarValue+epsilon) );	//check upper border
				}
				FixedPoint llp = new FixedPoint(ll);
				FixedPoint urp = new FixedPoint(ur);
				return new FixedPointRectangle(llp, urp);
			}
		};		
	}
		
	/**  
	 *	Returns a Mapper mapping a Point to a FixedPointRectangle where 
	 *  the point is the center of the rectangle
	 *	2*epsilon x 2*epsilon x ... x 2*epsilon. 
	 *  The input-Points are assumed to be inside the unit-cube.
	 * 
	 * @param iterator an Iterator containing Points
	 * @param epsilon the epsilon distance (Note: rectangles of side length 2*epsilon are created)
	 * @return a mapper mapping Points to FixedPointRectangles
	 * 
	 * @see #getPointFixedPointRectangleMappingFunction(double)
	 */
	public static Mapper getPointFixedPointRectangleMapper(Iterator iterator, final double epsilon) {
		return new Mapper(iterator, getPointFixedPointRectangleMappingFunction(epsilon));
	}

	/** Returns a Mapper transforming a given Iterator of FloatPoints 
	 *  to the unit-cube [0;1)^dim assuming that
	 *	all Points are inside the given universe-FloatPointRectangle and that
	 *	the Points are of type FloatPoint.
	 *  This is an important pre-processing step needed e.g. for hihg-dimensional data.
	 *
	 *	@param iterator an Iterator containing FloatPoints
	 *	@param universe the minimal bounding rectangle of the Points
	 *  @return a mapper mapping FloatPoints to into the unit-cube
	 */	
	public static Mapper getFloatPointUnitCubeMapper(final Iterator iterator, final Rectangle universe) {
		return new Mapper(iterator, 
			new Function() {
				protected float[] ll = (float[]) universe.getCorner(false).getPoint();		//lower-left corner
				protected float[] ur = (float[]) universe.getCorner(true).getPoint();		//upper-right corner
	
				public Object invoke(Object object){
					float[] point = (float[]) ((Point)((FloatPoint) object).clone()).getPoint();	//clone the input in order to avoid strange side-effects
	
					for(int i=0; i < point.length; i++){
						//scale to unit-cube:
						point[i] = (point[i]-ll[i]) / (ur[i]-ll[i]);	//new coordinate = point-leftBorder of MBR /(extension of MBR in actual dimension)
	
						//ensure that value is in [0;1):
						point[i] = Math.min(Math.max(0, point[i]), 0.999999f);
					}
	
					return new FloatPoint(point);	//create a new FloatPoint (to avoid side-effects);
				}		
			}
		);
	}
	
	/**
	 * Use-case:<br>
	 * <ol> 
	 *     <li>compute universe for a file containing FloatPoints 
	 *     <li>map Points of input-iterator to new value
	 *     <li>write them to disk<br>
	 * </ol>
	 * <br><br>
	 *
	 * @param args: args[0] input file-name, args[1] dimensionality of the data, args[2] output file-name
   */
	public static void main(String[] args){

		if(args.length != 3){
			System.out.println("usage: java xxl.core.spatial.cursor.Mappers <input file-name> <dim> <output file-name>");
			return;	
		}

		final String inputFile = args[0];
		final int dim = Integer.parseInt(args[1]);
		final String outputFile = args[2];
                
		//1.) compute universe for the given file:
		Aggregator ag =
			new UniverseFloat(
				new PointInputCursor(new File(inputFile), PointInputCursor.FLOAT_POINT, dim, 1024*1024),
				dim
			)
		;

		Rectangle universe = (Rectangle) ag.last();
		ag.close();

		System.out.println("# The universe of "+inputFile+" dim "+dim+" is");
		System.out.println(universe);

		//2.) map Points to unit-cube:
		Iterator it = getFloatPointUnitCubeMapper(
			new PointInputCursor(new File(args[0]), PointInputCursor.FLOAT_POINT, dim, 1024*1024),
			universe
		);				

		//3.) write the file to disk:
		Convertables.write(outputFile, it);
	}

}
