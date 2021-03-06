/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author JOAO
 */
public class MathUtils {
    private static double EPSILON=1E-5;

    public static boolean isLesserOrEqual(double a, double b){
        return  (a < b + EPSILON);
    }

    public static boolean isGreaterOrEqual(double a, double b){
        return  (a > b - EPSILON);
    }

    public static boolean isEqual(double a,double b){
        return  (isLesserOrEqual(a,b) && isGreaterOrEqual(a,b));
    }

    /**
     * Returns the logarithm in any base.
     * @param base
     * @param value
     * @return
     */
    public static double log(double base, double value) {
        return Math.log(value)/Math.log(base);
    }

    public static int factorial (int n) {
        int fact = 1;
        for (int i = n; i > 1; i--) {
            fact = fact* i;
        }
        return fact;
    }

    public static int[][] combinations(int start, int[] varDims){
        String key = "{"+start+", "+Arrays.toString(varDims)+"}";
        ArrayList<int[]> items = new ArrayList<int[]>();
        for(int dim=start;dim<varDims.length;dim++){
            //compute the possible number of combinations for dim
            int total = factorial(varDims.length)/(factorial(varDims.length-dim)*factorial(dim));
            int[] innerDims = new int[dim];
            int[] index = new int[dim];
            for(int i=0;i<dim;i++){
                index[i]=i;
            }
            for(int v=0;v<index.length;v++){
                innerDims[v]=varDims[index[v]];
            }
            items.add(innerDims.clone());

            for(int i=1; i< total;i++){
                combination(index, varDims.length, dim);

                for(int v=0;v<index.length;v++){
                    innerDims[v]=varDims[index[v]];
                }
                items.add(innerDims.clone());
            }
        }
        items.add(varDims.clone());
        return items.toArray(new int[items.size()][]);
    }

    private static void combination (int index[], int n, int r) {
        int i = r - 1;
        while (index[i] == n - r + i) {
          i--;
        }
        index[i] = index[i] + 1;
        for (int j = i + 1; j < r; j++) {
          index[j] = index[i] + j - i;
        }
    }

    /**
     * Returns a random point inside the segment (x1, y1) (x2, y2))
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double[] getRandomPoint(double x1, double y1, double x2, double y2){
        double x,y;
        if(x1==x2 && y1==y2){
            x = x1;
            y = y1;
        }else{
            // y - y1 = ((y2-y1)/(x2-x1))*(x-x1), http://en.wikipedia.org/wiki/Linear_equation
            if(x1==x2){
                x = x1;
                y = y1 + RandomUtil.nextDouble(y2-y1);
            }else{
                x = x1 + RandomUtil.nextDouble(x2-x1);
                y = ((y2-y1)/(x2-x1))*(x-x1) + y1;
            }
        }
        return new double[]{x,y};
    }

    /**
     * returns true if the point (x,y) is on the line (x1, y1)(x2, y2)
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x
     * @param y
     * @return
     */
    public static boolean isOnSegment(double x1, double y1, double x2, double y2, double x, double y, int precision){
        x=Util.cast(x, precision);
        y=Util.cast(y, precision);

        x1=Util.cast(x1, precision);
        x2=Util.cast(x2, precision);
        y1=Util.cast(y1, precision);
        y2=Util.cast(y2, precision);

        if(x1==x2 && y1==y2){
            return x==x1 && y==y1;
        }else{
            // y - y1 = ((y2-y1)/(x2-x1))*(x-x1), http://en.wikipedia.org/wiki/Linear_equation
            if(x1==x2){
                return x == x1 && (y>=y1 && y<=y2);
            }else{
            	return (((x>=x1 && x<=x2) && (y>=y1 && y<=y2)) ||
            		   ((x<=x1 && x>=x2) && (y<=y1 && y>=y2)) ||
            		   ((x>=x1 && x<=x2) && (y<=y1 && y>=y2)) ||
            		   ((x<=x1 && x>=x2) && (y>=y1 && y<=y2))) &&
            		   isEqual((y-y1), ((y2-y1)/(x2-x1))*(x-x1));
            }
        }

    }

    /**
     * Given a segment [(x1, y1), (x2, y2)] and a point (x3,y3). This method
     * returns the point (x,y) in the segment [(x1, y1), (x2, y2)] that minimizes
     * the distance between (x3,y3) and the segment [(x1, y1), (x2, y2)].
     * http://paulbourke.net/geometry/pointline/
     * @param args
     */
    public static double[] getMinDistPoint(double x1, double y1, double x2, double y2, double x3, double y3){
        double x;
        double y;
        if(x1==x2 && y1==y2){
            x = x1;
            y = y1;
        }else{

            double u = ((x3-x1)*(x2-x1) + (y3 -y1)*(y2-y1))/((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));

            if(u>1){
            	x = x2;
            	y = y2;
            }else if(u<0){
            	x = x1;
            	y = y1;
            }else{
            	x = x1 + u*(x2-x1);
            	y = y1 + u*(y2-y1);
            }

        }

        return new double[]{x,y};
    }

    public static void main(String[] args){
        int[] varDim = new int[]{0,1,2,3};
        int[][] combinations = combinations(1, varDim);

        for(int i=0;i<combinations.length;i++){
            System.out.println(Arrays.toString(combinations[i]));
        }
        double x1= 0.0;
        double y1= 7.0;
        double x2= 7.0;
        double y2= 0.0;
        double x3= 3.0;
        double y3= 3.0;

        double[] point = getRandomPoint(x1, y1, x2, y2);
        boolean foo = isOnSegment(x1, y1, x2, y2, point[0], point[1], 6);
        //System.out.println(Arrays.toString(point)+" is on "+ isOnSegment(x1, y1, x2, y2, 0, 0, 5));
//        double[] point = getMinDistPoint(x1, y1, x2, y2, x3, y3);
        System.out.println("Point:" + Arrays.toString(point) + " is "+foo);
    }
}