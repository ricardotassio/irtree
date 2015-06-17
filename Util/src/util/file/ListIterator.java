/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author joao
 */
public class ListIterator<E extends PersistentEntry> implements Iterator<E>{
    private final RandomAccessFile listFile;

    private final int entrySizeInBytes;
    private final int numEntries;
    private long blockPtr;
    private int numEntriesRetrieved;    
    private LinkedList<E> memoryList;
    private byte[] buffer;
    private PersistentEntryFactory<E> factory;

    public ListIterator(RandomAccessFile listFile, long blockPtr,
            int numEntries, int entrySizeInBytes, PersistentEntryFactory<E> factory){
        this.listFile = listFile;
        this.blockPtr = blockPtr;
        this.numEntries = numEntries;
        this.entrySizeInBytes = entrySizeInBytes;
        this.memoryList = new LinkedList<E>();
        this.numEntriesRetrieved = 0;
        this.buffer = new byte[ListStorage.LIST_STORAGE_BLOCK_SIZE];
        this.factory = factory;
    }

    /**
     * Returns the number of PostingListEntries which is the same as the number
     * of documents related with the given term.
     * @return
     */
    public int size(){
        return numEntries;
    }

    public boolean hasNext() {
        return numEntriesRetrieved < numEntries;
    }

    public E next() {
        if(!this.hasNext()){
            throw new RuntimeException("Entry does not exists, use hasNext!!!");
        }
        try{
            if(memoryList.isEmpty()){
                listFile.seek(blockPtr);
                
                int entriesToRead = Math.min(numEntries-numEntriesRetrieved, 
                        ListStorage.LIST_STORAGE_BLOCK_SIZE/entrySizeInBytes);

                listFile.readFully(buffer, 0, entriesToRead*entrySizeInBytes);
                blockPtr+=entriesToRead*entrySizeInBytes;

                DataInputStream input = new DataInputStream(new ByteArrayInputStream(buffer));
                //Load a block into the memoryList.
                for(int i=entriesToRead;i>0;i--){
                    memoryList.add(factory.produce(input));
                }
                input.close();
            }
        } catch (Exception ex) {            
            throw new RuntimeException("Error reading blockPtr="+blockPtr+
                    ", entriesToRead="+Math.min(numEntries-numEntriesRetrieved,
                    ListStorage.LIST_STORAGE_BLOCK_SIZE/entrySizeInBytes)+"!!!", ex);
        } 
        numEntriesRetrieved++;
        return memoryList.removeFirst();
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}