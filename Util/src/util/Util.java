/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.Arrays;

/**
 *
 * @author joao
 */
public class Util {
    public static long ID=1;
    public static int millisecond=1;
    public static int second=1000*millisecond;
    public static int minute=60*second;
    public static int hour=60*minute;
    /**
     * return the execution time in h m s
     * @param time
     */
    public static String time(long time){
        StringBuilder result = new StringBuilder();
        long h = (time / hour);
        if(h>0) result.append(" ").append(h).append("h");
        long m = (time % hour)/minute;
        if(h>0 || m>0) result.append(" ").append(m).append("m");
        long s = ((time % hour) % minute)/second;
        if(h>0 || m>0 || s>0) result.append(" ").append(s).append("s");
        long ms = (((time % hour) % minute)%second);
        result.append(" ").append(ms).append("ms");

        return result.toString();
    }

    public static String size(long size) {
        StringBuilder result = new StringBuilder();
        long g = (size / FileUtils.GB);
        if(g>0) result.append(" ").append(g).append("GB");
        long m = (size % FileUtils.GB)/FileUtils.MB;
        if(g>0 || m>0) result.append(" ").append(m).append("MB");
        long k = ((size % FileUtils.GB) % FileUtils.MB)/FileUtils.kB;
        if(g>0 || m>0 || k>0) result.append(" ").append(k).append("kB");
        long b = (((size % FileUtils.GB) % FileUtils.MB)%FileUtils.kB);
        result.append(" ").append(b).append("B");

        return result.toString();
    }

    public static long nextID(){
        return ID++;
    }

    public static double cast(double value, int precision){
        return Double.parseDouble(String.format("%."+precision+"f", value));
    }

    public static double[] cast(double[] set, int precision){
        for(int i=0;i<set.length;i++){
            set[i]= cast(set[i], precision);
        }
        return set;
    }
    
    public static void main(String[] args){
        /*
        int[] termIds = new int[]{1,2,4};
        int[] bitmaps = new int[]{3,8, 64};
        
        int[] temp = new int[]{Arrays.hashCode(termIds), 
                                Arrays.hashCode(bitmaps)};
        int hashCode =  Arrays.hashCode(temp);
        
        System.out.println("Term ids: "+Arrays.toString(termIds));
        System.out.println("bitmaps:" + Arrays.toString(bitmaps));
        System.out.println("HashCode ->" +hashCode);
        
        double base = Math.sqrt(Math.E);
        for(int i=0;i<5000;i++){
            System.out.println("round(ln(1+"+i+")) = " +Math.round(MathUtils.log(base, 1+i)));
        }
        */
        double pref = 0.5;
        double value1 = 0.44;
        double value2 = 0.11;
        
        System.out.println("pref=="+pref);
        System.out.println("normalize("+value1+")="+ (1-value1));
        System.out.println("normalize("+value2+")="+ (1-value2));

        pref=0.6;
        System.out.println("pref=="+pref);
        System.out.println("normalize("+value1+")="+ (1-value1)/(Math.pow(10,1-pref)));
        System.out.println("normalize("+value2+")="+ (1-value2)/(Math.pow(10,1-pref)));

        pref=1;
        System.out.println("pref=="+pref);
        System.out.println("normalize("+value1+")="+ (1-value1)/(Math.pow(10,pref)));
        System.out.println("normalize("+value2+")="+ (1-value2)/(Math.pow(10,pref)));
    }
}
