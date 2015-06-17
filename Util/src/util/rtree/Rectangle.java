package util.rtree;

import java.util.Arrays;
import util.Util;

public class Rectangle {
  
    /**
    * array containing the minimum value for each dimension; ie { min(x), min(y) }
    */
    public double[] max;

    /**
    * array containing the maximum value for each dimension; ie { max(x), max(y) }
    */
    public double[] min;


    public Rectangle(int numDimension) {
        this.min = new double[numDimension];
        this.max = new double[numDimension];
    }

    public Rectangle(double[] newMin, double[] newMax) {
        this(newMin.length);

        if (newMin.length != newMax.length) {
            throw new RuntimeException("Error in Rectangle constructor: " +
                    "min and max arrays must be of the same length.");
        }
        set(newMin, newMax);
    }

    public int getDimension(){
        return min.length;
    }
    /**
    * Sets the size of the rectangle.
    *
    * @param min array containing the minimum value for each dimension; ie { min(x), min(y) }
    * @param max array containing the maximum value for each dimension; ie { max(x), max(y) }
    */
    public void set(double[] min, double[] max) {
        System.arraycopy(min, 0, this.min, 0, getDimension());
        System.arraycopy(max, 0, this.max, 0, getDimension());
    }

    public double[] getMin(){
        return min;
    }

    public double[] getMax(){
        return max;
    }

    /**
    * Make a copy of this rectangle
    *
    * @return copy of this rectangle
    */
    public Rectangle copy() {
        return new Rectangle(min, max);
    }

    /**
    * Determine whether an edge of this rectangle overlies the equivalent
    * edge of the passed rectangle
    */
    public boolean edgeOverlaps(Rectangle r) {
        for (int i = 0; i < getDimension(); i++) {
            if (min[i] == r.min[i] || max[i] == r.max[i]) {
                return true;
            }
        }
        return false;
    }

    /**
    * Determine whether this rectangle intersects the passed rectangle
    *
    * @param r The rectangle that might intersect this rectangle
    *
    * @return true if the rectangles intersect, false if they do not intersect
    */
    public boolean intersects(Rectangle r) {
        // Every dimension must intersect. If any dimension
        // does not intersect, return false immediately.
        for (int i = 0; i < getDimension(); i++) {
            if (max[i] < r.min[i] || min[i] > r.max[i]) {
                return false;
            }
        }
        return true;
    }

    /**
    * Determine whether this rectangle contains the passed rectangle
    *
    * @param r The rectangle that might be contained by this rectangle
    *
    * @return true if this rectangle contains the passed rectangle, false if
    *         it does not
    */
    public boolean contains(Rectangle r) {
        for (int i = 0; i < getDimension(); i++) {
            if (max[i] < r.max[i] || min[i] > r.min[i]) {
                return false;
            }
        }
        return true;
    }

    /**
    * Determine whether this rectangle is contained by the passed rectangle
    *
    * @param r The rectangle that might contain this rectangle
    *
    * @return true if the passed rectangle contains this rectangle, false if
    *         it does not
    */
    public boolean containedBy(Rectangle r) {
        for (int i = 0; i < getDimension(); i++) {
            if (max[i] > r.max[i] || min[i] < r.min[i]) {
                return false;
            }
        }
        return true;
    }

    /**
    * Return the distance between this rectangle and the passed point.
    * If the rectangle contains the point, the distance is zero.
    *
    * @param p Point to find the distance to
    *
    * @return distance beween this rectangle and the passed point.
    */
    public double distance(Point p) {
        double distanceSquared = 0;
        for (int i = 0; i < getDimension(); i++) {
            double greatestMin = Math.max(min[i], p.coordinates[i]);
            double leastMax    = Math.min(max[i], p.coordinates[i]);
            if (greatestMin > leastMax) {
                distanceSquared += ((greatestMin - leastMax) * (greatestMin - leastMax));
            }
        }
        return  Math.sqrt(distanceSquared);
    }

    /**
    * Return the distance between this rectangle and the passed rectangle.
    * If the rectangles overlap, the distance is zero.
    *
    * @param r Rectangle to find the distance to
    *
    * @return distance between this rectangle and the passed rectangle
    */

    public double distance(Rectangle r) {
        double distanceSquared = 0;
        for (int i = 0; i < getDimension(); i++) {
            double greatestMin = Math.max(min[i], r.min[i]);
            double leastMax    = Math.min(max[i], r.max[i]);
            if (greatestMin > leastMax) {
                distanceSquared += ((greatestMin - leastMax) * (greatestMin - leastMax));
            }
        }
        return (double) Math.sqrt(distanceSquared);
    }

    
    /**
    * Return the furthst possible distance between this rectangle and
    * the passed rectangle.
    *
    * Find the distance between this rectangle and each corner of the
    * passed rectangle, and use the maximum.
    *
    */
    public double furthestDistance(Rectangle r) {
        int combinantions = (int)Math.pow(2, getDimension());
        Point point = new Point(getDimension());
        double maxDist = 0;
        for(int i=0;i<combinantions;i++){
            for(int d=0;d<getDimension();d++){
                if(((i>>d)&1)==0){ //check the binary number of i
                    point.coordinates[d]=r.getMin()[d];
                }else{
                    point.coordinates[d]=r.getMax()[d];
                }
            }
            maxDist =  Math.max(maxDist, furthestDistance(point));
        }
        return maxDist;
    }

    public double furthestDistance(Point p) {
        double value = 0;
        double middle;
        for(int d=0;d<getDimension();d++){
            middle = this.getMin()[d] + (this.getMax()[d]-this.getMin()[d])/2.0;
            if(p.coordinates[d]<middle){
                value+= Math.pow(this.getMax()[d]-p.coordinates[d], 2);
            }else{
                value+=Math.pow(p.coordinates[d]-this.getMin()[d], 2);
            }
        }
        return Math.sqrt(value);
    }


    /**
    * Calculate the area by which this rectangle would be enlarged if
    * added to the passed rectangle. Neither rectangle is altered.
    *
    * @param r Rectangle to union with this rectangle, in order to
    *          compute the difference in area of the union and the
    *          original rectangle
    */
    public double enlargement(Rectangle r) {
        double enlargedArea=1;
        for(int i=0;i<getDimension();i++){
            enlargedArea*=(Math.max(max[i], r.max[i]) - Math.min(min[i], r.min[i]));
        }
        return enlargedArea - area();
    }

    /**
    * Compute the area of this rectangle.
    *
    * @return The area of this rectangle
    */
    public double area() {
        double area=1;
        for(int i=0;i<getDimension();i++){
            area*=(max[i] - min[i]);
        }
        return area;
    }

    /**
    * Computes the union of this rectangle and the passed rectangle, storing
    * the result in this rectangle.
    *
    * @param r Rectangle to add to this rectangle
    */
    public void add(Rectangle r) {
        for (int i = 0; i < getDimension(); i++) {
            if (r.min[i] < min[i]) {
                min[i] = r.min[i];
            }
            if (r.max[i] > max[i]) {
                max[i] = r.max[i];
            }
        }
    }

    /**
    * Find the the union of this rectangle and the passed rectangle.
    * Neither rectangle is altered
    *
    * @param r The rectangle to union with this rectangle
    */
    public Rectangle union(Rectangle r) {
        Rectangle union = this.copy();
        union.add(r);
        return union;
    }

    /**
    * Determine whether this rectangle is equal to a given object.
    * Equality is determined by the bounds of the rectangle.
    *
    * @param o The object to compare with this rectangle
    */
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof Rectangle) {
            Rectangle r = (Rectangle) o;
            if (Arrays.equals(r.min, min) && Arrays.equals(r.max, max)) {
                equals = true;
            }
        }
        return equals;
    }

    /**
    * Return a string representation of this rectangle, in the form:
    * (1.2, 3.4), (5.6, 7.8)
    *
    * @return String String representation of this rectangle.
    */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        // min coordinates
        sb.append('(');
        for (int i = 0; i < getDimension(); i++) {
          if (i > 0) {
            sb.append(", ");
          }
          sb.append(Util.cast(min[i],2));
        }
        sb.append("), (");

        // max coordinates
        for (int i = 0; i < getDimension(); i++) {
          if (i > 0) {
            sb.append(", ");
          }
          sb.append(Util.cast(max[i],2));
        }
        sb.append(')');
        return sb.toString();
    }
}
