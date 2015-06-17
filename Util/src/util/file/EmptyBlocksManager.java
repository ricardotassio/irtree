/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import util.FileUtils;

/**
 *
 * @author joao
 */
class EmptyBlocksManager {
    private final String fileName;

    //Maps blocks size to the list of blocks pointers that have the given number of free blocks
    private HashMap<Long, LinkedList<Long>> emptyBlocks;
    private long maxNumBlocks=0; //Stores the maxNumBlocks that are empty
    private long lastUsedBlock; //pointer for the last used block

    public EmptyBlocksManager(String filename, long lastUsedBlock){
        this.fileName = filename;
        this.emptyBlocks = new HashMap<Long, LinkedList<Long>>();
        this.lastUsedBlock = lastUsedBlock;
    }

    /**
     * Load the data stored localy in a file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void open() throws FileNotFoundException, IOException{
         if(new File(fileName).exists()){
            DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));

            long ptr = 0;
            
            while( (ptr= input.readLong()) != -1){
                markEmpty(ptr,  input.readLong());
            }
            input.close();
        }
    }

    /**
     * Returns a pointer for the number of blocks required. The numBlocks are
     * consecutive blocks in the BlockColumnFiles. These blocks are marked as
     * used by the manager.
     * @param numBytes
     * @return
     */
    public long getPointer(long numBytes) throws ColumnFileException{
        long pointer = 0;
        for(long i=numBytes; i <=maxNumBlocks; i++){
            if(emptyBlocks.get(i)!=null && emptyBlocks.get(i).size()>0){
                pointer = emptyBlocks.get(i).removeFirst();

                //The group of blocks has more blocks than required.
                if(i>numBytes){
                    markEmpty(pointer+numBytes, i-numBytes);
                }
                return pointer;
            }
        }
        pointer = lastUsedBlock+1;

        lastUsedBlock+=numBytes;
        return pointer;
    }

    public void markEmpty(long pointer, long numBlocks){
        if(emptyBlocks.get(numBlocks)==null){
            emptyBlocks.put(numBlocks, new LinkedList<Long>());
        }
        emptyBlocks.get(numBlocks).add(pointer);
        maxNumBlocks = Math.max(maxNumBlocks, numBlocks);
    }

    /**
     * Stores the data persistently.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void close() throws FileNotFoundException, IOException{
        FileUtils.createDirectories(fileName);
        DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName,false)));

        for(Entry<Long, LinkedList<Long>> entry:emptyBlocks.entrySet()){
            for(long ptr:entry.getValue()){
               output.writeLong(ptr); //The pointer
               output.writeLong(entry.getKey());
            }
        }

        output.writeLong(-1);

        output.close();
    }
}
