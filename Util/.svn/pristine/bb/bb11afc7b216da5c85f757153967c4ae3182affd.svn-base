/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.List;

/**
 *
 * @author JOAO
 */
public class ArraysUtils {
    /**
     * Returns the smallerst value of the double values array.
     * @param values values array
     * @return the smallerst value
     */
    public static double min(double[] values){
        double min=Double.MAX_VALUE;
        for(double val:values){
            if(val<min){
                min=val;
            }
        }
        return min;
    }

    /**
     * Returns the largest value of the double values array.
     * @param values values array
     * @return the largest value
     */
    public static double max(double[] values){
        double max=Double.MIN_VALUE;
        for(double val:values){
            if(val>max){
                max=val;
            }
        }
        return max;
    }

    public static double sum(double[] values){
        double sum=0.0;
        for(double val:values){
            sum= sum + val;
        }
        return sum;
    }

    public static int sum(int[] values){
        int sum=0;
        for(int val:values){
            sum+=val;
        }
        return sum;
    }

    public static double[] toArray(List<Double> list){
        double[] array = new double[list.size()];
        for(int i=0;i<list.size();i++){
            array[i] = list.get(i);
        }
        return array;
    }

    public static double stdDev(double[] values){
        if (values.length < 2) {
                throw new RuntimeException("Attempt to get a standard deviation, "+
                        "but there is not sufficient data yet.");
        }

        double sumSquare=0;
        double sum=0;
        for(double val:values){
            sum+=val;
            sumSquare += val * val;
        }

        return Math.sqrt(Math.abs(values.length * sumSquare - sum * sum)
                        / (values.length * (values.length - 1)));
    }

    public static double mean(double[] values){
        return sum(values)/(double)values.length;
    }

    /**
     * Returns true if all elements of the array has the same value of the value given.
     * @param values values array
     * @param val to be compared of
     * @return true if all elements of the array has the same value of the value given, false otherwise
     */
    public static boolean equals(double[] values, double val){       
        for(int i=0;i<values.length;i++){
            if(values[i]!= val){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns true if all elements of the array has the same value of the value given.
     * @param values values array
     * @param val to be compared of
     * @return true if all elements of the array has the same value of the value given, false otherwise
     */
    public static boolean equals(int[] values, double val){       
        for(int i=0;i<values.length;i++){
            if(values[i]!= val){
                return false;
            }
        }
        return true;
    }    
    
    /**
     * Returns the smaller of the int values array.
     * @param values values array
     * @return the smaller value
     */
    public static int min(int[] values){
        int min=Integer.MAX_VALUE;
        for(int i=0;i<values.length;i++){
            if(values[i]<min){
                min=values[i];
            }
        }
        return min;
    }

    public static boolean contains(int[] array, int value, int from, int to){
        for(int i=from;i<to;i++){
            if(array[i]==value){
                return true;
            }
        }
        return false;
    }

}
