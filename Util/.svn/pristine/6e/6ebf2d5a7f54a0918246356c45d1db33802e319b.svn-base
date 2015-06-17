/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.File;
import java.util.Map;
import util.cache.CacheItem;
import util.cache.CacheLRU;
import util.statistics.Count;
import util.statistics.StatisticCenter;

/**
 *
 * @author joao
 */
public class BufferedBlockColumnFile extends BlockColumnFileImpl implements BlockColumnFile{
    private final BlocksCache<Integer, CacheItem<byte[]>> cache;
    /**
     * 
     * @param file
     * @param blockSize
     * @param blocksPerFile
     * @param bufferedBlocks Defines the size of the buffer in terms of the maximum number of blocks that can hold in memory
     * @throws ColumnFileException
     */
    public BufferedBlockColumnFile(String file, int blockSize, int blocksPerFile, int bufferedBlocks) throws ColumnFileException{
        this(new File(file), blockSize, blocksPerFile, bufferedBlocks);
    }

    public BufferedBlockColumnFile(File prefixfile, int blockSize, int blocksPerFile, int bufferedBlocks) throws ColumnFileException {
        super(prefixfile, blockSize, blocksPerFile);

        if(bufferedBlocks<1){
            throw new ColumnFileException("The number of bufferedBlocks must be greater than 0.");
        }

        cache = new BlocksCache<Integer, CacheItem<byte[]>>(bufferedBlocks);
    }

    @Override
    public void setStatisticCenter(StatisticCenter statisticCenter, String statisticsIdentification){
        super.setStatisticCenter(statisticCenter, statisticsIdentification);
    }

    @Override
    protected void internalSelect(int blockId, byte[] buffer) throws ColumnFileException{
        CacheItem<byte[]> item = cache.get(blockId);
        if(item!=null){
            System.arraycopy(item.getItem(), 0, buffer, 0, buffer.length);
            if(statisticCenter!=null){
                statisticCenter.getCount(statsId+"_cacheHit").inc();
            }
        }else{
            super.internalSelect(blockId, buffer);
            //Put the buffer in the cache
            cacheInsert(blockId, buffer, CacheItem.Type.READ);
        }
    }

    @Override
    protected void internalInsert(int blockId, byte[] buffer) throws ColumnFileException{
        cacheInsert(blockId, buffer, CacheItem.Type.WRITE);
    }

    private void cacheInsert(int blockId, byte[] buffer, CacheItem.Type type){
        byte[] bufferClone = new byte[buffer.length];
        System.arraycopy(buffer, 0, bufferClone, 0, buffer.length);
        cache.put(blockId, new CacheItem<byte[]>(bufferClone, type));
    }

    /**
     * Forces any data in cached to be persistently stored in disk.
     * @throws ColumnFileException
     */
    public void flush() throws ColumnFileException{
         //make the data in the cache persistent
        for(Map.Entry<Integer, CacheItem<byte[]>> entry:cache.entrySet()){
            if(entry.getValue().isWrite()){
                super.internalInsert(entry.getKey(), entry.getValue().getItem());
            }
        }
    }

    @Override
    public void close() throws ColumnFileException {
        flush();
        super.close();
    }

    class BlocksCache<K extends Integer, V extends CacheItem<byte[]>> extends CacheLRU<K, V>{
        private int cacheSize;
        public BlocksCache (int cacheSize) {
            super(cacheSize);
            this.cacheSize= cacheSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            if(size() > this.cacheSize){
                try {
                    if(eldest.getValue().isWrite()){
                        BufferedBlockColumnFile.super.internalInsert(eldest.getKey(), eldest.getValue().getItem());
                    }
                } catch (ColumnFileException ex) {
                    throw new RuntimeException(ex);
                }
                return true;
            }else{
                return false;
            }
        }

    }
}
