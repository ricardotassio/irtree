/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.rtree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import util.cache.MinHeap;
import util.rtree.DiskBuffer.Operation;
import util.statistics.Count;
import util.statistics.StatisticCenter;


/**
 *
 * @author joao
 */
public class DiskBuffer {
    protected static enum Operation {READ, WRITE};
    private final StatisticCenter statisticCenter;        
    private final int cacheSize;
    private LinkedHashMap<String, NodeWrapper> cache;

    public DiskBuffer(StatisticCenter statisticCenter, int size){
        this.statisticCenter = statisticCenter;
        this.cacheSize = size;


        this.cache = new LinkedHashMap<String,NodeWrapper>(size, 0.75f, true) {
            private static final long serialVersionUID = 1;
            @Override
            protected boolean removeEldestEntry (Map.Entry<String,NodeWrapper> eldest) {
                if(size() > DiskBuffer.this.cacheSize){
                    NodeWrapper wrapper = eldest.getValue();
                    if(wrapper.getOperation().equals(Operation.WRITE)){
                        try {
                            DiskBuffer.this.flush(0.25f);
                        } catch (RTreeException ex) {
                            ex.printStackTrace();
                            throw new RuntimeException(ex);
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }


    void store(int nodeId, Node node, DiskStorageManager storageManager) throws RTreeException {
        NodeWrapper wrapper = new NodeWrapper(node,storageManager, Operation.WRITE);

        cache.put(storageManager.getId() +"_"+nodeId, wrapper);
    }

   
    Node read(int nodeId, DiskStorageManager storageManager) throws RTreeException {
        //Increment the number of nodes accessed
        if(statisticCenter!=null) statisticCenter.getCount("nodesAccessed").inc();

        //This id identifies unically one object in the cache, even if it
        //comes from different storage managers... So, one cache can be used
        //by different DiskStorageManagers
        NodeWrapper wrapper = cache.get(storageManager.getId() +"_"+nodeId);
        if(wrapper==null){//It is not in cache
                //Increment the number of page faults
           if(statisticCenter!=null) statisticCenter.getCount("pageFaults").inc();

            wrapper = new NodeWrapper(storageManager.readNodeFromDisk(nodeId),
                    storageManager, Operation.READ);

            cache.put(storageManager.getId() +"_"+nodeId, wrapper);
        }
        return wrapper.getNode();
    }

    /**
     * percentage of the data (WRITE) in the cache that is stored in disk.
     * @param percentage
     * @throws RTreeException
     */
    public void flush(float percentage) throws RTreeException {
        int flushSize = (int) (cache.size() * percentage + 1);
        NodeWrapper wrapper=null;

        HashMap<Integer, MinHeap<NodeWrapper>> heaps = new HashMap();

        MinHeap<NodeWrapper> heap=null;
        Iterator<NodeWrapper> it=this.cache.values().iterator();
        for(int i=0;it.hasNext() && i<flushSize;i++){
            wrapper = it.next();
            if(wrapper.getOperation().equals(Operation.WRITE)){
                heap = heaps.get(wrapper.getStorageManager().getId());
                if(heap==null){
                    heap= new MinHeap<NodeWrapper>();
                    heaps.put(wrapper.getStorageManager().getId(), heap);
                }
                heap.add(wrapper);
            }
        }

        //Store for each storage manager individually
        for(MinHeap<NodeWrapper> h:heaps.values()){
            while(!h.isEmpty()){
                wrapper = h.poll();
                wrapper.getStorageManager().storeNodeInDisk(wrapper.getNode().getID(), wrapper.getNode());
                wrapper.setOperation(Operation.READ);
            }
        }
    }
    public void flush() throws RTreeException {
        flush(1.0f);
    }

    public void clear() throws RTreeException{
        close();
    }

    public void close() throws RTreeException{
        flush();
        cache.clear();        
    }

    public String toString(){
        return "DiskBuffer. [maxSize="+this.cacheSize+
                    ", curSize="+cache.size()+"]";
    }    
}
class NodeWrapper implements Comparable{
    private final Node node;
    private final DiskStorageManager storageManager;
    private Operation op;

    public NodeWrapper(Node node, DiskStorageManager storageManager, Operation op){
        this.node = node;
        this.storageManager = storageManager;
        this.op = op;
    }

    /**
     * @return the node
     */
    public Node getNode() {
        return node;
    }

    /**
     * @return the storageManager
     */
    public DiskStorageManager getStorageManager() {
        return storageManager;
    }

    /**
     * @return the op
     */
    public Operation getOperation() {
        return op;
    }

    /**
     * @return the op
     */
    public void setOperation(Operation op) {
        this.op = op;
    }

    public int compareTo(Object o) {
        return this.node.getID() - ((NodeWrapper)o).getNode().getID();
    }

    public String toString(){
        return this.node.toString();
    }
}