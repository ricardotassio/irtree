/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 *
 * @author joao
 */
public class Benchmark {
    public static long insertAndSort(Collection< ? extends Comparable> items){
        long time = System.currentTimeMillis();
        LinkedList<Comparable> temp = new LinkedList<>();
        for(Comparable item:items){
           temp.add(item);
        }
        Collections.sort(temp);
        return System.currentTimeMillis()-time;
    }
    public static long insertAndSortArray(Collection< ? extends Comparable> items){
        long time = System.currentTimeMillis();
        Comparable[] temp = new Comparable[items.size()];        
        int i=0;
        for(Comparable item:items){
           temp[i++] = item;
        }
        Arrays.sort(temp);        
        return System.currentTimeMillis()-time;
    }
    
    public static long sortedInsert(Collection<? extends Comparable > items){
        long time = System.currentTimeMillis();
        TreeSet<Comparable> temp = new TreeSet<>();
        for(Comparable item:items){
           temp.add(item);
        }        
        return System.currentTimeMillis()-time;
    }
    
    public static LinkedList<Integer> randomIntegerSet(int size){
        LinkedList<Integer> set = new LinkedList<>();
        for(int i=0;i<size;i++){
            set.add((int)Math.random());
        }
        return set;
    }
    public static LinkedList<String> randomStringSet(int size){
        LinkedList<String> set = new LinkedList<>();
        for(int i=0;i<size;i++){
            set.add(i+"->"+Math.random());
        }
        return set;
    }
    public static LinkedList<Double> randomDoubleSet(int size){
        LinkedList<Double> set = new LinkedList<>();
        for(int i=0;i<size;i++){
            set.add(Math.random());
        }
        return set;
    }
    private static void insertAndSortVersusSortedInsert() {
        LinkedList<String> set;
        set= Benchmark.randomStringSet(10000);
        Benchmark.insertAndSort(set);Benchmark.insertAndSortArray(set);Benchmark.sortedInsert(set);
        System.out.println("Size: 10mil");
        System.out.println("insertAndSort: "+ Util.time(Benchmark.insertAndSort(set)));      
        System.out.println("insertAndSortArray: "+ Util.time(Benchmark.insertAndSortArray(set)));      
        System.out.println("sortedInsert: "+ Util.time(Benchmark.sortedInsert(set)));          

        set = Benchmark.randomStringSet(100000);
        Benchmark.insertAndSort(set);Benchmark.insertAndSortArray(set);Benchmark.sortedInsert(set);
        System.out.println("Size: 100mil");
        System.out.println("insertAndSort: "+ Util.time(Benchmark.insertAndSort(set))); 
        System.out.println("insertAndSortArray: "+ Util.time(Benchmark.insertAndSortArray(set)));      
        System.out.println("sortedInsert: "+ Util.time(Benchmark.sortedInsert(set)));          

        set = Benchmark.randomStringSet(1000000);
        Benchmark.insertAndSort(set);Benchmark.insertAndSortArray(set);Benchmark.sortedInsert(set);
        System.out.println("Size: 1 milhão");
        System.out.println("insertAndSort: "+ Util.time(Benchmark.insertAndSort(set))); 
        System.out.println("insertAndSortArray: "+ Util.time(Benchmark.insertAndSortArray(set)));            
        System.out.println("sortedInsert: "+ Util.time(Benchmark.sortedInsert(set)));          
        
        set = Benchmark.randomStringSet(10000000);
        Benchmark.insertAndSort(set);Benchmark.insertAndSortArray(set);Benchmark.sortedInsert(set);
        System.out.println("Size: 10 milhões");
        System.out.println("insertAndSort: "+ Util.time(Benchmark.insertAndSort(set))); 
        System.out.println("insertAndSortArray: "+ Util.time(Benchmark.insertAndSortArray(set)));            
        System.out.println("sortedInsert: "+ Util.time(Benchmark.sortedInsert(set)));          
    }    
    public static void main(String[] args){
        Benchmark.insertAndSortVersusSortedInsert();    
    }    
}
