/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.cache;

import java.util.PriorityQueue;


public class MinHeap<K extends Comparable> extends PriorityQueue<K>{

    public MinHeap() {
        super();
    }

    public MinHeap(int initialCapacity) {
        super(initialCapacity);
    }
}