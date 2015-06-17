/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

/**
 *
 * @author joao
 */
public class SpatialUtils {

    /**
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return the distance between two latitude and longitude coordinates in meters.
     */
    public static double haversineDistance(double lat1, double lon1,
            double lat2, double lon2) {

        double radius = 6371.0; // avg radius of earth in km

        double lat = Math.toRadians(lat2 - lat1);
        double lon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(lat / 2) * Math.sin(lat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lon / 2) * Math.sin(lon / 2);

        return 2 * radius * Math.asin(Math.sqrt(a)) * 1000.0;
    }

    private static double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
    }

    public static void main(String[] args){
        System.out.println("distance="+SpatialUtils.haversineDistance(0, -90,0,90) + " m");
    }
}
