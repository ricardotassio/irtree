/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import util.cache.CacheItem;
import util.cache.CacheLRU;
import util.statistics.StatisticCenter;


public class BufferedListStorage<E extends PersistentEntry> {
    private final ListStorage<E> persistentEntries;
    private ListCache<Integer, CacheItem<List<E>>> cache;
    private boolean hasDataToFlush=false;        
    private final StatisticCenter statisticCenter;
    private final String statisticCenterId;

    public BufferedListStorage(StatisticCenter statisticCenter, 
            String statisticCenterId, String prefixFile, int cacheSize,
            int entrySize, PersistentEntryFactory<E> factory){
        this.cache = new ListCache<Integer, CacheItem<List<E>>>(cacheSize);
        this.statisticCenter = statisticCenter;
        this.statisticCenterId = statisticCenterId;        

        this.persistentEntries = new ListStorage<E>(statisticCenter, statisticCenterId,
                prefixFile, entrySize, factory);
    }

    public void open() throws ColumnFileException, FileNotFoundException, IOException{
        persistentEntries.open();
    }

    public void resetCacheSize(int newCacheSize) throws ColumnFileException, IOException{
        this.flush();
        this.cache = new ListCache<Integer, CacheItem<List<E>>>(newCacheSize);
    }

    public void putList(int id, List<E> list){
        putList(id, list, true);
    }

    public void putList(int id, List<E> list, boolean createCopy){
        hasDataToFlush=true;

        if(createCopy){
            cache.put(id, new CacheItem<List<E>>(new ArrayList<E>(list), CacheItem.Type.WRITE));
        }else{
            cache.put(id, new CacheItem<List<E>>(list, CacheItem.Type.WRITE));
        }
    }

    public int getListSize(int id) throws ColumnFileException, IOException{
        CacheItem<List<E>> item = cache.get(id);
        if(item!=null){
            return item.getItem().size();
        }else{
            return persistentEntries.getListSize(id);
        }
    }

    public List<Integer> getIds() throws ColumnFileException, IOException{
        flush();
        return persistentEntries.getIds();
    }

    /**
     * Returns a map of ids->listSize
     * @return
     */
    public Set<Entry<Integer,Integer>> entrySet() throws ColumnFileException, IOException{
        return this.persistentEntries.entrySet();
    }

    public List<E> getList(int id) throws ColumnFileException, IOException{
        return getList(id, true);
    }

    public List<E> getList(int id, boolean createCopy) throws ColumnFileException, IOException{
        if(statisticCenter != null){
            statisticCenter.getCount(statisticCenterId+"BlocksAccessed").inc();
        }
        
        CacheItem<List<E>> item = cache.get(id);
        if(item==null){
            ListIterator<E> entries = persistentEntries.getEntries(id);

            if(entries != null){
               List<E> list = new LinkedList<E>();

                while(entries.hasNext()){
                    list.add(entries.next());
                }

                item = new CacheItem<List<E>>(list, CacheItem.Type.READ);

                cache.put(id, item);
            }else{
                return null;
            }
        }
        if(createCopy){
            return new ArrayList<E>(item.getItem());
        }else{
            return item.getItem();
        }
    }

    public void flush() throws ColumnFileException, IOException{
        if(hasDataToFlush){
            hasDataToFlush=false;
             //make the data in the cache persistent
            //int count=0;
            //long time = System.currentTimeMillis();
            //System.out.print("[Flushing...");

            for(Map.Entry<Integer, CacheItem<List<E>>> entry:cache.entrySet()){
                if(entry.getValue().isWrite()){
                    //count++;
                    entry.getValue().setType(CacheItem.Type.READ);
                    List<E> list = entry.getValue().getItem();

                    this.persistentEntries.putList(entry.getKey(),list.iterator(),list.size());
                }
            }
            //System.out.print(""+count+" lists in"+Util.time(System.currentTimeMillis()-time)+"]");
        }
    }

    public void remove(int id) throws ColumnFileException, IOException{
        cache.remove(id);
        persistentEntries.remove(id);
    }

    public long getSizeInBytes() throws ColumnFileException, DataNotFoundException, IOException{
        flush();
        return persistentEntries.getSizeInBytes();
    }

    public void close() throws IOException, ColumnFileException{
        flush();
        persistentEntries.close();
        cache.clear();
    }

    public void delete() throws IOException, ColumnFileException{
        close();
        persistentEntries.delete();
    }

    public int getLargestId() throws ColumnFileException {
        return persistentEntries.getLargestId();
    }

    public String getPrefixFile() {
        return persistentEntries.getPrefixFile();
    }

    public void copy(String fileName) throws ColumnFileException, IOException {
        this.flush();
        persistentEntries.copy(fileName);
    }

    public int getCacheSize() {
        return cache.size();
    }

    public void addEntry(int listId, E value, boolean unique) throws ColumnFileException, IOException {
        List<E> list = getList(listId, false);
        if(list==null){
            list = new LinkedList<E>();
        }else if(unique && list.contains(value)){
            return;
        }
        list.add(value);
        putList(listId, list, false);
    }

    public void addList(int listId, List<E> newList, boolean unique) throws ColumnFileException, IOException {
        List<E> list = getList(listId,false);
        if(list==null){
            list = newList;
        }else if(!unique){
            list.addAll(newList);
        }else{ //unique
            HashSet<E> curList = new HashSet<E>(list);
            for(E e:newList){
                if(!curList.contains(e)){
                    list.add(e);
                }
            }
        }
        putList(listId, list, false);
    }

    public Iterator<Integer> getIdsIterator() throws ColumnFileException, IOException {
        flush();
        return persistentEntries.getIdsIterator();
    }

    public boolean containsList(int id) throws ColumnFileException, IOException{
        return cache.containsKey(id) || persistentEntries.containsList(id);
    }

    private class ListCache<K extends Integer, V extends CacheItem<List<E>>> extends CacheLRU<K, V>{
        private int cacheSize;
        public ListCache (int cacheSize) {
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
