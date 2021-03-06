/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import util.Util;
import util.cache.CacheItem;
import util.cache.CacheLRU;
import util.file.ColumnFileException;
import util.file.DataNotFoundException;
import util.file.ListIterator;
import util.file.ListStorage;
import util.sse.SSEExeption;
import util.sse.Term;
import util.sse.Vector;
import util.sse.Vocabulary;

/**
 *
 * @author joao
 */
public class VectorCacheManager {
    //Stores the entry <"small.id:big.id", double> maping the consines processed among the datavectors
    private CacheLRU<String, Double> dataVectorsProcessedCosines;

    private ListStorage<Term> dataPersistentList;
    private final Vocabulary dataVocabulary;     //maps big ids to small ids

    private ListStorage<Term> nodePersistentList;
    private final Vocabulary nodeVocabulary;     //maps big ids to small ids

    private VectorCache<String, CacheItem<Vector>> cache;    
    private String prefixFile;
    private boolean hasDataToFlush=false;

    private final int blockSize;    

    public VectorCacheManager(Vocabulary dataVocabulary, Vocabulary nodeVocabulary, int blockSize,
            String prefixFile, int cacheSize){
        this.cache = new VectorCache<String, CacheItem<Vector>>(cacheSize);
                
        this.prefixFile = prefixFile;
        this.dataVocabulary = dataVocabulary;
        this.nodeVocabulary = nodeVocabulary;
        this.dataVectorsProcessedCosines = new CacheLRU<String, Double>(cacheSize);
        this.blockSize = blockSize;
    }

    public void open() throws ColumnFileException, FileNotFoundException, IOException{
        dataPersistentList = new ListStorage<Term>(null,
                "data_vectors",prefixFile+"_data", Term.SIZE, Term.FACTORY);
        dataPersistentList.open();

        nodePersistentList = new ListStorage<Term>(null, "node_vectors",
                prefixFile+"_node", Term.SIZE, Term.FACTORY);
        nodePersistentList.open();
    }

    public Vector getDataVector(int objectId) throws IOException, ColumnFileException, SSEExeption{
        return getVector(dataVocabulary.getId(IRTree.getStrId(objectId, true)), objectId, true);
    }

    public Vector getNodeVector(int nodeId) throws IOException, ColumnFileException, SSEExeption{
        return getVector(nodeVocabulary.getId(IRTree.getStrId(nodeId,false)), nodeId, false);
    }
    
    private Vector getVector(int smallId, int originalId, boolean isDataVector) throws ColumnFileException, IOException{
        String strId = IRTree.getStrId(originalId, isDataVector);
        CacheItem<Vector> item = cache.get(strId);
        if(item==null){
            Vector vector = new Vector(originalId);

            ListIterator<Term> entries = isDataVector
                    ? dataPersistentList.getEntries(smallId)
                    : nodePersistentList.getEntries(smallId);

            while(entries!=null && entries.hasNext()){
                vector.add(entries.next());
            }

            item = new CacheItem<Vector>(vector, CacheItem.Type.READ);
            
            cache.put(strId, item);
        }
        return item.getItem();
    }


    public void putDataVector(Vector vector){
        hasDataToFlush=true;
         cache.put(IRTree.getStrId(vector.getId(), true),
                 new CacheItem<Vector>(vector, CacheItem.Type.WRITE));
    }

    public void putNodeVector(Vector vector){
        hasDataToFlush=true;
        cache.put(IRTree.getStrId(vector.getId(), false),
                new CacheItem<Vector>(vector, CacheItem.Type.WRITE));
    }

    public void deleteNodeVector(int id) throws ColumnFileException, IOException, SSEExeption{
        cache.remove(IRTree.getStrId(id, false));
        nodePersistentList.remove(nodeVocabulary.getId(IRTree.getStrId(id, false)));
    }

    public void deleteDataVector(int id) throws ColumnFileException, IOException, SSEExeption{
        cache.remove(IRTree.getStrId(id, true));
        dataPersistentList.remove(dataVocabulary.getId(IRTree.getStrId(id, true)));
    }

    public void storeProcessedCosineDataVectors(Vector v1, Vector v2, Double cosine){
        Vector small = v1.size()<v2.size() ? v1 : v2;
        Vector big = v1.size()<v2.size() ? v2 : v1;
        this.dataVectorsProcessedCosines.put(new StringBuilder().append(
                small.getId()).append(':').append(big.getId()).toString(), cosine);
    }

    public Double getProcessedCosineDataVectors(Vector v1, Vector v2){
        Vector small = v1.size()<v2.size() ? v1 : v2;
        Vector big = v1.size()<v2.size() ? v2 : v1;
        return this.dataVectorsProcessedCosines.get(new StringBuilder().append(
                small.getId()).append(':').append(big.getId()).toString());
    }

    /**
     * Forces any data in cached to be persistently stored in disk.
     * @throws ColumnFileException
     */
    public void flush() throws IOException, ColumnFileException, SSEExeption{
        if(hasDataToFlush){
            hasDataToFlush=false;
             //make the data in the cache persistent
            int count=0;
            long time = System.currentTimeMillis();
            System.out.print("[Flushing...");

            ArrayList<Vector> nodeVectors = new ArrayList<Vector>();
            for(Map.Entry<String, CacheItem<Vector>> entry:cache.entrySet()){
                if(entry.getValue().isWrite()){
                    count++;
                    entry.getValue().setType(CacheItem.Type.READ);
                    Vector vector = entry.getValue().getItem();
                    if(IRTree.isDataVector(entry.getKey())){
                        this.dataPersistentList.putList(dataVocabulary.getId(IRTree.getStrId(vector.getId(),true)),
                                vector.iterator(), vector.size());
                    }else{
                        //We are not writing here to avoid writing in two different files at the same size
                        nodeVectors.add(vector);
                    }
                }
            }

            for(Vector vector: nodeVectors){
                nodePersistentList.putList(nodeVocabulary.getId(IRTree.getStrId(vector.getId(),false)),
                        vector.iterator(), vector.size());
            }

            System.out.print(""+count+" vectors in"+Util.time(System.currentTimeMillis()-time)+"]");
        }
    }

    public void close() throws IOException, ColumnFileException, SSEExeption {
        flush();
        dataPersistentList.close();
        nodePersistentList.close();
    }

    long getStoredSizeInBytes() throws ColumnFileException, DataNotFoundException, IOException {
        return dataPersistentList.getSizeInBytes() + nodePersistentList.getSizeInBytes();
    }

    private class VectorCache<K extends String, V extends CacheItem<Vector>> extends CacheLRU<K, V>{
        private int cacheSize;
        public VectorCache (int cacheSize) {
            super(cacheSize);
            this.cacheSize= cacheSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            if(size() > this.cacheSize){
                try {
                    if(eldest.getValue().isWrite()){
                        flush();
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                } 
                return true;
            }else{
                return false;
            }
        }
    }
}
