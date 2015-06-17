/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author JOAO
 */
public class Teste {


    private static void test(Map<Integer, String> hash) {
        System.out.println("Testing " + hash.getClass().getName());
        hash.put(1, "1");
        hash.put(2, "2");
        hash.put(3, "3");
        hash.put(4, "4");
        hash.put(5, "5");
        hash.put(6, "6");
        hash.put(7, "7");
        hash.put(8, "8");
        hash.put(9, "9");
        hash.put(10, "10");

        hash.get(1);
        hash.get(2);
        hash.get(3);


        for (Iterator it = hash.values().iterator(); it.hasNext();) {
          System.out.println(it.next());
        }
        System.out.println();
    }

    public static void testHahes() {
        test(new HashMap<Integer, String>());
        test(new LinkedHashMap<Integer, String>(10, 0.75f, true));
        test(new IdentityHashMap<Integer, String>());
    }
    
    public static void main(String[] args){
        testHahes();

        //object id, distance, quality
        double[][] objects = new double[15][2];
        double maxDistance=100;
        double maxQuality = 1;
        Random random = new Random(127);


        Double v1= 0.03 - Double.MIN_VALUE;
        Double v2=0.3 - Double.MIN_VALUE;
        Double sum = v1;
        sum=v1+v2;
        System.out.println("sum:"+sum);
        if(sum == (0.33-Double.MIN_VALUE)){
            System.out.println("equals");
        }else{
            System.out.println("different!!   "+(0.33-Double.MIN_VALUE));
        }

        for(int i=0; i<objects.length;i++){
            //distance
            objects[i][0]= (int)(random.nextDouble()*maxDistance);
            //quality
            objects[i][1]= random.nextDouble();
        }

        //Arrays.sort(objects);
        for(int i=0; i<objects.length;i++){
            System.out.println((i+1)+"->"+objects[i][0]+"m, "+objects[i][1]);
        }
    }

}
