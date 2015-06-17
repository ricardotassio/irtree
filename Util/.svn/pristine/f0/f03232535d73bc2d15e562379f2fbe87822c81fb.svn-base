
package util.cache;

import java.util.Arrays;
import java.util.PriorityQueue;


public class BoundedHeap<E> {
    private PriorityQueue<E> queue;
    
    private int maxCapacity;

    /**
     * @return the heapType
     */
    public HeapType getHeapType() {
        return heapType;
    }

    public static enum HeapType {MIN, MAX};

    private HeapType heapType;

    /**
     * Creates a {@code Heap} with the maximum
     * capacity
     * {@linkplain Comparable natural ordering}.
     */
    public BoundedHeap(HeapType heapType, int maxCapacity) {
        if(heapType.equals(HeapType.MIN)){
            this.queue = new PriorityQueue<E>(Math.min(maxCapacity, 11));
        } else{
            this.queue = new PriorityQueue<E>(Math.min(maxCapacity, 11), new InverseComparator());
        }
        this.maxCapacity = maxCapacity;
        this.heapType = heapType;
    }

    public int getMaxCapacity(){
        return maxCapacity;
    }

    public boolean contains(E e){
        return queue.contains(e);
    }

    public String toString(){
        return queue.toString();
    }

    /**
     * Get the entry with minimum value
     * @return
     */
    public E peak(){
        return queue.peek();
    }

    /**
     * Get the entry with minimum value removing from the heap.
     * @return
     */
    public E poll(){
        return queue.poll();
    }

    /**
     * Inserts the specified element into this priority queue.
     *
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws ClassCastException if the specified element cannot be
     *         compared with elements currently in this priority queue
     *         according to the priority queue's ordering
     * @throws NullPointerException if the specified element is null
     */
    public void add(E e) {
        queue.add(e);

        if(queue.size()>this.maxCapacity){
            queue.poll();
        }
    }

    /**
     * Return the content of this hpea in descending order, from the major to the minor
     * @param <T>
     * @param a
     * @return
     */
    public <T> T[] toArrayDescending(T[] a) {
        if(heapType.equals(HeapType.MAX)){
            return innerToArrayAscending(a);
        }
        return innerToArrayDescending(a);
    }

    private <T> T[] innerToArrayDescending(T[] a){
        for(int i=a.length-1;i>=0;i--){
            a[i] = (T) queue.poll();
        }
        for(int i=0;i<a.length;i++){
            queue.add((E) a[i]);
        }
        return a;
    }

    public <T> T[] toArrayAscending(T[] a) {
        if(heapType.equals(HeapType.MAX)){
            return innerToArrayDescending(a);
        }
        return innerToArrayAscending(a);
    }

    private <T> T[] innerToArrayAscending(T[] a) {
        for(int i=0;i<a.length;i++){
            a[i] = (T) queue.poll();
        }
        for(int i=0;i<a.length;i++){
            queue.add((E) a[i]);
        }
        return a;
    }

    public int size() {
        return queue.size();
    }

    /**
     * Removes all of the elements from this priority queue.
     * The queue will be empty after this call returns.
     */
    public void clear() {
        queue.clear();
    }


    public static void main(String[] args){
        BoundedHeap<Integer> heap = new BoundedHeap<Integer>(HeapType.MAX, 3);

        heap.add(3);
        heap.add(2);
        heap.add(4);
        System.out.println("Heap:"+Arrays.toString(heap.toArrayDescending(new Integer[heap.size()])));

        heap.add(1);
        System.out.println("Heap:"+Arrays.toString(heap.toArrayDescending(new Integer[heap.size()])));

        heap.add(5);
        System.out.println("Heap Ascending:"+Arrays.toString(heap.toArrayAscending(new Integer[heap.size()])));
        System.out.println("Heap Descending:"+Arrays.toString(heap.toArrayDescending(new Integer[heap.size()])));
    }
}
