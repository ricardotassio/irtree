/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.cache;

import java.util.PriorityQueue;

public class MaxHeap<K extends Comparable> extends PriorityQueue<K>{

    public MaxHeap() {
        super(11, new InverseComparator());
    }

    public MaxHeap(int initialCapacity) {
        super(initialCapacity, new InverseComparator());
    }

    public<T> T[] ToArrayOrder(T[] a){
        for(int i=a.length-1;i>=0;i--){
            a[i] = (T) this.poll();
        }
        for(int i=0;i<a.length;i++){
            this.add((K)a[i]);
        }
        return a;
    }

    public static void main(String[] args){
        MinHeap<Integer> heap = new MinHeap<Integer>();
        heap.add(1);
        heap.add(3);
        heap.add(2);
        while(!heap.isEmpty()){
            System.out.println(heap.poll());
        }
    }
}
