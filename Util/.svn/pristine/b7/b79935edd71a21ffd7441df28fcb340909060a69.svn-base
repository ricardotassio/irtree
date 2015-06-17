package util.rtree;

/**
 * Currently hardcoded to 2 dimensions, but could be extended.
 * 
 * @author  aled@sourceforge.net
 * @version 1.0b2p1
 */
public class Point {
  
    /**
    * The (x, y) coordinates of the point.
    */
    public double[] coordinates;
  
    /**
    * Constructor.
    *
    * @param x The x coordinate of the point
    * @param y The y coordinate of the point
    */
    public Point(int dimensions) {
        coordinates = new double[dimensions];
    }


    public Point(double[] values){
        this(values.length);
        System.arraycopy(values, 0, coordinates, 0, values.length);
    }

    public int getDimensions(){
        return coordinates.length;
    }

    public void setValue(int d, double value){
        coordinates[d] = value;
    }

    public double getValue(int d){
        return coordinates[d];
    }

    public double distance(Point p) {
        assert this.coordinates.length == p.coordinates.length;

        double distance=0;
        for(int i = 0; i < this.coordinates.length ; i++) {
            distance+=Math.pow(p.coordinates[i]-this.coordinates[i], 2);
        }
        return Math.sqrt(distance);
    }
}
